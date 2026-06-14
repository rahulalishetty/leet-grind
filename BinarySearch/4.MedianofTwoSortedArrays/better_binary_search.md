# Median of Two Sorted Arrays — Intuition & Approach

## The Core Idea

Instead of merging both arrays (which would be O(m + n)), this solution uses **binary search** to find the correct _partition point_ directly — achieving **O(log(min(m, n)))** time.

The key insight: **the median divides a combined sorted sequence into two equal halves**. We don't need to actually merge; we just need to find where to "cut" each array so that everything on the left half is ≤ everything on the right half.

---

## Setting Up the Partition

Imagine splitting both arrays into a left half and a right half:

```
nums1: [ ... maxLeftA | minRightA ... ]
nums2: [ ... maxLeftB | minRightB ... ]
```

We pick a `partitionA` index in `nums1`, and derive `partitionB` in `nums2` so that the **total number of elements on the left equals half the combined length**:

```
partitionB = (m + n + 1) / 2 - partitionA
```

The `+1` ensures the left side gets the extra element when the total length is odd.

---

## The Valid Partition Condition

A partition is **correct** when:

```
maxLeftA <= minRightB   AND   maxLeftB <= minRightA
```

This means: the largest element on the left side of `nums1` is no greater than the smallest on the right side of `nums2`, and vice versa. When this holds, the left halves of both arrays together form exactly the lower half of the merged sorted array.

---

## Binary Search Logic

Binary search is performed on the **smaller array** (we swap if needed so `nums1` is always the shorter one — this keeps the search space minimal).

| Condition              | Meaning                                                          | Action                        |
| ---------------------- | ---------------------------------------------------------------- | ----------------------------- |
| `maxLeftA > minRightB` | `partitionA` is too far right; left side of `nums1` is too large | Move `right = partitionA - 1` |
| `maxLeftB > minRightA` | `partitionA` is too far left; need more elements from `nums1`    | Move `left = partitionA + 1`  |
| Both conditions hold   | Correct partition found                                          | Compute median                |

---

## Edge Cases: Boundary Sentinels

When a partition is at the very start or end of an array, there's no element to compare. Sentinel values handle this cleanly:

```javascript
let maxLeftA = partitionA == 0 ? -Infinity : nums1[partitionA - 1];
let minRightA = partitionA == m ? +Infinity : nums1[partitionA];
// same for B
```

- `partitionA == 0` → nothing on the left of `nums1`, so treat it as `-∞` (never blocks the condition)
- `partitionA == m` → nothing on the right of `nums1`, so treat it as `+∞` (never blocks the condition)

---

## Computing the Median

Once the correct partition is found:

**Odd total length** — the median is simply the largest element on the left side:

```javascript
return Math.max(maxLeftA, maxLeftB);
```

**Even total length** — the median is the average of the largest-left and smallest-right:

```javascript
return (Math.max(maxLeftA, maxLeftB) + Math.min(minRightA, minRightB)) / 2.0;
```

---

## Walkthrough Example

```
nums1 = [1, 3]
nums2 = [2, 4]
m = 2, n = 2  →  total = 4 (even), left half size = 2
```

Binary search on `nums1` (length 2):

- `partitionA = 1`, `partitionB = 1`
- `maxLeftA = 1`, `minRightA = 3`
- `maxLeftB = 2`, `minRightB = 4`
- Check: `1 <= 4` ✓ and `2 <= 3` ✓ → valid partition
- Median = `(max(1, 2) + min(3, 4)) / 2` = `(2 + 3) / 2` = **2.5** ✓

---

## Complexity

|           | Value             |
| --------- | ----------------- |
| **Time**  | O(log(min(m, n))) |
| **Space** | O(1)              |

The binary search only runs on the smaller array, making this optimal for the problem.

```js
var findMedianSortedArrays = function (nums1, nums2) {
  if (nums1.length > nums2.length) {
    let temp = nums1;
    nums1 = nums2;
    nums2 = temp;
  }

  let m = nums1.length,
    n = nums2.length;
  let left = 0,
    right = m;

  while (left <= right) {
    let partitionA = Math.floor((left + right) / 2);
    let partitionB = Math.floor((m + n + 1) / 2 - partitionA);

    let maxLeftA = partitionA == 0 ? Number.MIN_SAFE_INTEGER : nums1[partitionA - 1];
    let minRightA = partitionA == m ? Number.MAX_SAFE_INTEGER : nums1[partitionA];
    let maxLeftB = partitionB == 0 ? Number.MIN_SAFE_INTEGER : nums2[partitionB - 1];
    let minRightB = partitionB == n ? Number.MAX_SAFE_INTEGER : nums2[partitionB];

    if (maxLeftA <= minRightB && maxLeftB <= minRightA) {
      if ((m + n) % 2 == 0) {
        return (Math.max(maxLeftA, maxLeftB) + Math.min(minRightA, minRightB)) / 2.0;
      } else {
        return Math.max(maxLeftA, maxLeftB);
      }
    } else if (maxLeftA > minRightB) {
      right = partitionA - 1;
    } else {
      left = partitionA + 1;
    }
  }
  return 0.0;
};
```
