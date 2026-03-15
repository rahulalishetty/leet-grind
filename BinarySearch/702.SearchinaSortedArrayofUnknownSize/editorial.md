# 702. Search in a Sorted Array of Unknown Size — Approach

# Approach 1: Binary Search

## Splitting the Problem

Since the array is **sorted**, we can achieve **O(log n)** complexity using **binary search**.

However, the array size is unknown, so we first need to determine the **search boundaries**.

Thus the solution has two stages:

1. Determine search boundaries (`left` and `right`).
2. Perform binary search within those boundaries.

---

# Step 1: Define Search Boundaries

Start with the smallest possible boundaries.

```
left = 0
right = 1
```

If the target is greater than the element at `right`, then the target must lie further to the right.

We repeatedly expand the search window.

```
left = right
right = right * 2
```

This doubling strategy ensures we find a valid boundary in **logarithmic time**.

Eventually we reach a range:

```
2^k < target_index ≤ 2^(k+1)
```

At that point the target must lie within `[left, right]`.

---

# Step 2: Binary Search

Once boundaries are known, we perform standard binary search.

1. Compute the middle index.
2. Compare the element with the target.
3. Move left or right accordingly.

Binary search continues until the element is found or the range becomes invalid.

---

# Bitwise Optimization

Instead of multiplication and division we can use bit shifts.

```
x << 1  → multiply by 2
x >> 1  → divide by 2
```

These operations are slightly faster.

---

# Algorithm

### Boundary Expansion

```
left = 0
right = 1

while reader.get(right) < target:
    left = right
    right = right * 2
```

### Binary Search

```
while left <= right:

    pivot = left + ((right - left) >> 1)
    num = reader.get(pivot)

    if num == target:
        return pivot

    if num > target:
        right = pivot - 1
    else:
        left = pivot + 1
```

If the target is never found, return `-1`.

---

# Implementation

```java
class Solution {

  public int search(ArrayReader reader, int target) {

    if (reader.get(0) == target)
        return 0;

    int left = 0;
    int right = 1;

    while (reader.get(right) < target) {
      left = right;
      right <<= 1;
    }

    int pivot;
    int num;

    while (left <= right) {

      pivot = left + ((right - left) >> 1);

      num = reader.get(pivot);

      if (num == target)
          return pivot;

      if (num > target)
          right = pivot - 1;
      else
          left = pivot + 1;
    }

    return -1;
  }
}
```

---

# Complexity Analysis

## Time Complexity

```
O(log T)
```

Where `T` is the **index of the target**.

Two phases contribute to complexity:

### Boundary Expansion

Boundaries grow exponentially:

```
1 → 2 → 4 → 8 → 16 → ...
```

After `k` expansions:

```
2^k < T ≤ 2^(k+1)
```

Thus:

```
k = log T
```

So boundary expansion takes:

```
O(log T)
```

### Binary Search

Binary search operates on a range size:

```
2^(k+1) - 2^k = 2^k
```

Since:

```
2^k = T
```

Binary search also takes:

```
O(log T)
```

---

## Space Complexity

```
O(1)
```

The algorithm uses constant extra memory.
