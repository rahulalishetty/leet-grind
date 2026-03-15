# 1533. Find the Index of the Large Integer — Approach

## Approach 1: Binary Search

### Intuition

The array contains mostly identical values except **one element that is larger**.

However, we **cannot directly access the array**.
Instead, we use the API:

```
compareSub(l, r, x, y)
```

which compares the **sum of two subarrays**.

---

### Key Idea

If the array is split into **two equal halves**:

- If all elements were equal → sums would be equal.
- If one half contains the **larger element** → that half will have a **larger sum**.

Therefore:

```
compare the two halves
→ discard the smaller half
→ continue searching in the larger half
```

This resembles **binary search**.

---

## Handling Odd-Length Arrays

If the current search space has an **odd number of elements**, we cannot split it evenly.

Solution:

```
split array into:
left half
right half
extra element
```

Now compare **left** and **right**.

Three cases arise:

### Case 1

```
sum(left) > sum(right)
```

The larger element is in **left**.

Discard:

```
right + extra element
```

---

### Case 2

```
sum(left) < sum(right)
```

The larger element is in **right**.

Discard:

```
left + extra element
```

---

### Case 3

```
sum(left) == sum(right)
```

The larger element is **not in either half**.

Therefore:

```
the extra element is the answer
```

---

## Key Insight

Every comparison removes **at least half of the search space**.

Thus we perform a **modified binary search**.

---

# Algorithm

1. Initialize

```
left = 0
length = reader.length()
```

The search space is:

```
[left, left + length)
```

2. While `length > 1`

```
length = length / 2
```

3. Compare two equal subarrays

```
compareSub(
    left,
    left + length - 1,
    left + length,
    left + length + length - 1
)
```

4. If comparison result:

```
0 → extra element is the answer
-1 → answer lies in right half → left += length
1 → answer lies in left half → keep left unchanged
```

5. When loop ends:

```
return left
```

---

# Implementation

```java
class Solution {
    public int getIndex(ArrayReader reader) {

        int left = 0;
        int length = reader.length();

        while (length > 1) {

            length /= 2;

            int cmp = reader.compareSub(
                left,
                left + length - 1,
                left + length,
                left + length + length - 1
            );

            if (cmp == 0) {
                return left + length + length;
            }

            if (cmp < 0) {
                left += length;
            }
        }

        return left;
    }
}
```

---

# Complexity Analysis

Let:

```
N = length of the array
```

### Time Complexity

```
O(log N)
```

Each comparison **halves the search space**.

At most:

```
log N API calls
```

---

### Space Complexity

```
O(1)
```

Only a few integer variables are used.
