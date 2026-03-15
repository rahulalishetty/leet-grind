# 1891. Cutting Ribbons — Overview & Binary Search Approach

## Overview

We are given an array `ribbons`, where each element represents the **length of a ribbon**, and an integer `k`.

Our task is to determine whether it is possible to cut the ribbons into **at least `k` pieces**, where:

- Every piece has the **same length**
- Pieces must have **positive integer length**
- Any leftover ribbon **can be discarded**

If it is possible, we must find the **maximum possible length** of these pieces.

---

## Example

```
ribbons = [1, 2, 3, 4]
k = 3
```

### Option 1

Cut ribbons into pieces of length **1**

```
Total pieces = 10
```

Since `10 ≥ 3`, this length works.

### Option 2

Cut ribbons into pieces of length **2**

```
1 → 0 pieces
2 → 1 piece
3 → 1 piece
4 → 2 pieces
Total = 4 pieces
```

Since `4 ≥ 3`, this also works.

Since we want the **largest possible length**, the answer is:

```
2
```

---

# Approach: Binary Search on the Answer

## Intuition

If we try cutting ribbons into pieces of a **very large length**, we will produce **very few pieces**.

If we try cutting ribbons into **smaller lengths**, we produce **more pieces**.

This creates a **monotonic relationship**:

```
piece length ↑  → number of pieces ↓
piece length ↓  → number of pieces ↑
```

Because of this monotonic behavior, we can use:

```
Binary Search on the ribbon length
```

instead of testing every possible length.

---

## Binary Search Strategy

The longest possible ribbon piece cannot exceed:

```
max(ribbons)
```

Thus our search space becomes:

```
[0 , max(ribbons)]
```

For a candidate length `x`, we check:

```
Can we produce ≥ k pieces of length x?
```

If yes → try a **larger length**
If no → try a **smaller length**

---

# Upper Middle in Binary Search

We use:

```
middle = (left + right + 1) / 2
```

This avoids **infinite loops**.

Example:

```
left = 3
right = 4
```

Upper middle:

```
mid = (3 + 4 + 1) / 2 = 4
```

If `mid` works:

```
left = mid = 4
```

Search terminates correctly.

If we used the lower middle:

```
mid = (3 + 4) / 2 = 3
```

Updating `left = mid` would cause an **infinite loop**.

---

# Algorithm

### Helper Function

Define:

```
isPossible(x)
```

Returns `true` if we can produce **≥ k ribbons** of length `x`.

Steps:

1. Initialize

```
totalRibbons = 0
```

2. For each ribbon:

```
pieces = floor(ribbon / x)
totalRibbons += pieces
```

3. If

```
totalRibbons ≥ k
```

return `true`.

Otherwise return `false`.

---

### Main Binary Search

1. Initialize

```
left = 0
right = max(ribbons)
```

2. While

```
left < right
```

3. Compute

```
middle = (left + right + 1) / 2
```

4. If

```
isPossible(middle)
```

then

```
left = middle
```

Else

```
right = middle - 1
```

5. Return

```
left
```

---

# Implementation

```java
class Solution {

    public int maxLength(int[] ribbons, int k) {

        int left = 0;
        int right = Arrays.stream(ribbons).max().getAsInt();

        while (left < right) {

            int middle = (left + right + 1) / 2;

            if (isPossible(middle, ribbons, k)) {
                left = middle;
            } else {
                right = middle - 1;
            }
        }

        return left;
    }

    private boolean isPossible(int x, int[] ribbons, int k) {

        int totalRibbons = 0;

        for (int ribbon : ribbons) {

            totalRibbons += ribbon / x;

            if (totalRibbons >= k) {
                return true;
            }
        }

        return false;
    }
}
```

---

# Complexity Analysis

Let:

```
n = number of ribbons
m = maximum ribbon length
```

### Time Complexity

```
O(n log m)
```

Binary search takes `log m` iterations and each iteration scans the array.

### Space Complexity

```
O(1)
```

Only constant extra variables are used.
