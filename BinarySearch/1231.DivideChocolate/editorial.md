# 1231. Divide Chocolate — Overview and Approach

## Overview

In this problem, we are asked to cut a chocolate bar into **k + 1 pieces**, where each piece contains **one or more consecutive chunks**.

Each chunk has a sweetness value.

The sweetness of a piece equals:

```
sum of sweetness values of its chunks
```

Since you are generous:

- You **eat the piece with the minimum sweetness**
- Your **k friends take the other pieces**

Your goal is:

```
maximize the sweetness of the piece you eat
```

This means we want to **maximize the minimum sweetness among all pieces**.

---

# Approach: Binary Search + Greedy

## Intuition

We can think of the chocolate bar as an array of integers.

If we cut the bar into **k + 1 contiguous subarrays**, then:

```
sum of each subarray = sweetness of a piece
```

Our task becomes:

```
maximize the minimum sum among all k+1 subarrays
```

Instead of directly optimizing the cuts, we convert the problem into a **decision problem**.

### Decision Question

Given a value `x`:

```
Can we divide the chocolate into k+1 pieces
such that each piece has sweetness ≥ x ?
```

If yes → `x` is **workable**
If no → `x` is **unworkable**

Key observation:

- If `x` works → `x-1` also works
- If `x` doesn't work → `x+1` won't work

This means the answers follow a **monotonic pattern**, which makes **Binary Search** applicable.

---

# Binary Search Setup

## Search Space

Lower bound:

```
left = min(sweetness)
```

Upper bound:

```
right = total_sweetness / (k + 1)
```

Because if we take more than this value, the total sweetness would exceed the entire bar.

---

## Binary Search Process

Compute:

```
mid = (left + right + 1) / 2
```

Then check:

```
Can we create k+1 pieces each with sweetness ≥ mid?
```

### If possible

```
left = mid
```

Try a larger minimum sweetness.

### If impossible

```
right = mid - 1
```

Try smaller values.

Continue until:

```
left == right
```

---

# Greedy Validation

To check if `mid` works:

Traverse the chocolate chunks and accumulate sweetness.

Whenever the accumulated sweetness ≥ `mid`:

- Cut a piece
- Start building the next piece

If we can produce **k+1 pieces**, then `mid` is workable.

---

# Algorithm

1. Compute:

```
left = min(sweetness)
right = total_sweetness / (k + 1)
```

2. While `left < right`

```
mid = (left + right + 1) / 2
```

3. Greedily count pieces with sweetness ≥ `mid`

4. If pieces ≥ `k+1`

```
left = mid
```

Else

```
right = mid - 1
```

5. Return `left`.

---

# Implementation

```java
class Solution {
    public int maximizeSweetness(int[] sweetness, int k) {

        int numberOfPeople = k + 1;

        int left = Arrays.stream(sweetness).min().getAsInt();
        int right = Arrays.stream(sweetness).sum() / numberOfPeople;

        while (left < right) {

            int mid = (left + right + 1) / 2;

            int curSweetness = 0;
            int peopleWithChocolate = 0;

            for (int s : sweetness) {

                curSweetness += s;

                if (curSweetness >= mid) {
                    peopleWithChocolate++;
                    curSweetness = 0;
                }
            }

            if (peopleWithChocolate >= numberOfPeople) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }

        return right;
    }
}
```

---

# Complexity Analysis

Let:

```
n = number of chunks
S = total sweetness
```

### Time Complexity

```
O(n * log(S/(k+1)))
```

Binary search performs `log(S)` iterations.

Each iteration scans the array once.

---

### Space Complexity

```
O(1)
```

Only constant extra variables are used.
