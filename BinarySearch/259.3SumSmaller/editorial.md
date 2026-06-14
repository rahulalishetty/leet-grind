# 259. 3Sum Smaller

**Problem:** Given an array of `n` integers `nums` and an integer `target`, find the number of index triplets `i, j, k` with `0 <= i < j < k < n` such that `nums[i] + nums[j] + nums[k] < target`.

---

## Approach 1: Brute Force — O(n³) Time, O(1) Space

### Idea

Enumerate every possible triplet using three nested loops and count those whose sum is less than `target`.

```python
def threeSumSmaller_brute(nums, target):
    count = 0
    n = len(nums)
    for i in range(n - 2):
        for j in range(i + 1, n - 1):
            for k in range(j + 1, n):
                if nums[i] + nums[j] + nums[k] < target:
                    count += 1
    return count
```

### Complexity

|           |                                              |
| --------- | -------------------------------------------- |
| **Time**  | O(n³) — three nested loops over all triplets |
| **Space** | O(1) — only a counter variable               |

### Pros / Cons

- ✅ Simple to implement and verify
- ❌ Too slow for n = 3500 (≈ 42 billion ops)

---

## Approach 2: Sort + Binary Search — O(n² log n) Time, O(1) Space

### Idea

Sort the array. Fix the first two indices `i` and `j`, then use binary search to find the largest index `k` such that `nums[i] + nums[j] + nums[k] < target`. All indices from `j+1` to `k` form valid triplets, so add `k - j` to the count.

```python
import bisect

def threeSumSmaller_binsearch(nums, target):
    nums.sort()
    count = 0
    n = len(nums)
    for i in range(n - 2):
        for j in range(i + 1, n - 1):
            remaining = target - nums[i] - nums[j]
            # Find rightmost index where nums[k] < remaining
            k = bisect.bisect_left(nums, remaining, j + 1, n) - 1
            if k > j:
                count += k - j
    return count
```

### Walkthrough (Example 1)

```
nums = [-2, 0, 1, 3], target = 2

Sorted: [-2, 0, 1, 3]

i=0 (nums[i]=-2):
  j=1 (nums[j]=0): remaining = 2-(-2)-0 = 4  → bisect_left finds idx 4 → k=3 → count += 3-1 = 2
  j=2 (nums[j]=1): remaining = 2-(-2)-1 = 3  → bisect_left finds idx 3 → k=2 → count += 2-2 = 0

Total count = 2 ✓
```

### Complexity

|           |                                                         |
| --------- | ------------------------------------------------------- |
| **Time**  | O(n² log n) — O(n²) pairs × O(log n) binary search each |
| **Space** | O(1) extra (sorting in place)                           |

### Pros / Cons

- ✅ Much better than brute force
- ✅ Easy to implement with `bisect`
- ❌ Still two nested loops; two-pointer approach is cleaner

---

## Approach 3: Sort + Two Pointers — O(n² ) Time, O(1) Space ✅ Optimal

### Idea

Sort the array. Fix the outermost index `i`, then use two pointers `lo = i+1` and `hi = n-1` to scan inward:

- If `nums[i] + nums[lo] + nums[hi] < target`:
  Every index from `lo+1` to `hi` paired with `lo` also satisfies the condition (since the array is sorted and `nums[hi]` is the largest right bound).
  Add `hi - lo` to count, then move `lo` right.
- Otherwise, move `hi` left to reduce the sum.

```python
def threeSumSmaller(nums, target):
    nums.sort()
    count = 0
    n = len(nums)
    for i in range(n - 2):
        lo, hi = i + 1, n - 1
        while lo < hi:
            s = nums[i] + nums[lo] + nums[hi]
            if s < target:
                count += hi - lo   # all (lo, lo+1..hi) pairs work with i
                lo += 1
            else:
                hi -= 1
    return count
```

### Walkthrough (Example 1)

```
nums = [-2, 0, 1, 3], target = 2

Sorted: [-2, 0, 1, 3]

i=0, nums[i]=-2:
  lo=1, hi=3 → sum = -2+0+3 = 1 < 2  → count += (3-1)=2, lo=2
  lo=2, hi=3 → sum = -2+1+3 = 2, NOT < 2 → hi=2
  lo=2 == hi=2 → exit inner loop

i=1, nums[i]=0:
  lo=2, hi=3 → sum = 0+1+3 = 4, NOT < 2 → hi=2
  lo=2 == hi=2 → exit

Total count = 2 ✓
```

### Visual: Two-Pointer Movement

```
Sorted array: [-2,  0,  1,  3]
               ^i   ^lo     ^hi

Step 1: i=0, lo=1, hi=3
        sum = -2+0+3 = 1 < 2
        → All pairs (lo, lo+1..hi) valid: (1,2), (1,3) → +2
        → lo moves right

Step 2: lo=2, hi=3
        sum = -2+1+3 = 2 ≥ 2
        → hi moves left → lo meets hi → stop
```

### Complexity

|           |                                                                  |
| --------- | ---------------------------------------------------------------- |
| **Time**  | O(n log n) for sort + O(n²) for the two-pointer scan = **O(n²)** |
| **Space** | O(1) extra                                                       |

### Why `count += hi - lo`?

When `nums[i] + nums[lo] + nums[hi] < target`, because the array is sorted:

- `nums[i] + nums[lo] + nums[lo+1] < target`
- `nums[i] + nums[lo] + nums[lo+2] < target`
- ...
- `nums[i] + nums[lo] + nums[hi] < target`

That's exactly `hi - lo` valid triplets for this fixed `(i, lo)` pair.

---

## Comparison Summary

| Approach             | Time        | Space    | Notes                   |
| -------------------- | ----------- | -------- | ----------------------- |
| Brute Force          | O(n³)       | O(1)     | Simple; TLE for n=3500  |
| Sort + Binary Search | O(n² log n) | O(1)     | Good; uses `bisect`     |
| Sort + Two Pointers  | **O(n²)**   | **O(1)** | ✅ Optimal; clean logic |

---

## Edge Cases

```python
# Empty array
threeSumSmaller([], 0)       # → 0

# Fewer than 3 elements
threeSumSmaller([0], 0)      # → 0
threeSumSmaller([0, 1], 5)   # → 0

# All negatives
threeSumSmaller([-5, -4, -3, -2], 0)  # All triplets sum < 0 → 4

# All same values
threeSumSmaller([1, 1, 1], 4)  # 1+1+1=3 < 4 → 1
threeSumSmaller([1, 1, 1], 3)  # 1+1+1=3, NOT < 3 → 0
```

---

## Final Solution (Two Pointers)

```python
class Solution:
    def threeSumSmaller(self, nums: list[int], target: int) -> int:
        nums.sort()
        count = 0
        n = len(nums)
        for i in range(n - 2):
            lo, hi = i + 1, n - 1
            while lo < hi:
                if nums[i] + nums[lo] + nums[hi] < target:
                    count += hi - lo
                    lo += 1
                else:
                    hi -= 1
        return count
```

**Key insight:** Sorting enables the two-pointer technique. When the sum is small enough, we can count an entire range of valid third elements in O(1) instead of iterating through them — collapsing the innermost loop from O(n) to amortized O(1).
