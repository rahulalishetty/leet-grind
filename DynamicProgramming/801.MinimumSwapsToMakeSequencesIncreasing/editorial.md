# 801. Minimum Swaps To Make Sequences Increasing — Exhaustive Solution Notes

## Overview

We are given two arrays of equal length:

- `nums1`
- `nums2`

At any index `i`, we are allowed to swap:

```text
nums1[i] <-> nums2[i]
```

Our goal is to make **both arrays strictly increasing** using the **minimum number of swaps**.

This problem looks tricky at first because a decision at one index affects whether future indices can remain valid. The key observation is:

> At index `i`, the only thing that matters from the previous step is whether index `i - 1` was swapped or not.

That makes this a very elegant **dynamic programming** problem with only two states per position.

This write-up explains the accepted dynamic programming approach in detail.

---

## Problem Statement

You are given two arrays `nums1` and `nums2` of the same length.

In one operation, you may swap `nums1[i]` with `nums2[i]`.

Return the minimum number of swaps required so that **both arrays become strictly increasing**.

The input is guaranteed to always have at least one valid solution.

---

## Example 1

**Input**

```text
nums1 = [1,3,5,4]
nums2 = [1,2,3,7]
```

**Output**

```text
1
```

**Explanation**

Swap index `3`.

Before:

```text
nums1 = [1,3,5,4]
nums2 = [1,2,3,7]
```

After:

```text
nums1 = [1,3,5,7]
nums2 = [1,2,3,4]
```

Now both arrays are strictly increasing.

---

## Example 2

**Input**

```text
nums1 = [0,3,5,8,9]
nums2 = [2,1,4,6,9]
```

**Output**

```text
1
```

---

## Constraints

- `2 <= nums1.length <= 10^5`
- `nums2.length == nums1.length`
- `0 <= nums1[i], nums2[i] <= 2 * 10^5`

---

# Why Dynamic Programming Works

Suppose we are processing index `i`.

To decide whether we should swap at `i` or not, we only need to know whether the previous index `i - 1` was swapped.

Why only that?

Because strict increasing order depends only on adjacent values:

```text
nums1[i - 1] < nums1[i]
nums2[i - 1] < nums2[i]
```

After any swap, only the values at the previous column matter for checking the current column.

So for each position, there are just two relevant states:

1. we do **not** swap at index `i`
2. we **do** swap at index `i`

That gives us a compact DP with constant space.

---

# DP State Definition

Let:

- `n1` = minimum swaps needed to make the sequences increasing up to index `i - 1`, **without swapping** index `i - 1`
- `s1` = minimum swaps needed to make the sequences increasing up to index `i - 1`, **with swapping** index `i - 1`

At index `i`, we compute:

- `n2` = minimum swaps needed up to index `i`, **without swapping** index `i`
- `s2` = minimum swaps needed up to index `i`, **with swapping** index `i`

Then we update:

```text
n1 = n2
s1 = s2
```

and continue.

---

# Initial Values

At index `0`:

- If we do **not** swap index `0`, cost is `0`
- If we **do** swap index `0`, cost is `1`

So:

```text
n1 = 0
s1 = 1
```

This is our starting point.

---

# Transition Logic

For convenience, let:

```text
a1 = A[i - 1]
b1 = B[i - 1]
a2 = A[i]
b2 = B[i]
```

At each step, we check which transitions are valid.

There are two independent possibilities.

---

## Case 1: Natural-to-Natural or Swapped-to-Swapped Is Valid

If:

```text
a1 < a2 and b1 < b2
```

then both arrays remain strictly increasing if:

- neither column is swapped
- both columns are swapped

### That means:

If index `i - 1` was not swapped, then index `i` can also remain not swapped:

```text
n2 = min(n2, n1)
```

If index `i - 1` was swapped, then index `i` can also be swapped:

```text
s2 = min(s2, s1 + 1)
```

Why `+1`?

Because swapping index `i` costs one additional swap.

---

## Case 2: Cross Transition Is Valid

If:

```text
a1 < b2 and b1 < a2
```

then it is valid for exactly **one** of the two adjacent columns to be swapped.

That means:

- if previous column was swapped, current one can be natural
- if previous column was natural, current one can be swapped

### So:

Natural at `i` from swapped at `i - 1`:

```text
n2 = min(n2, s1)
```

Swapped at `i` from natural at `i - 1`:

```text
s2 = min(s2, n1 + 1)
```

Again, `+1` because swapping the current column costs one operation.

---

# Why These Two `if` Statements Must Be Separate

This is extremely important.

Both of the following may be true at the same time:

```text
A[i - 1] < A[i] and B[i - 1] < B[i]
```

and

```text
A[i - 1] < B[i] and B[i - 1] < A[i]
```

If both hold, then multiple transitions are valid, and we must consider all of them.

So we must use:

- one `if` for the first condition
- another separate `if` for the second condition

Using `else if` would be incorrect because it would ignore one valid transition.

---

# Full Transition Summary

At each index `i`:

Initialize:

```text
n2 = INF
s2 = INF
```

Then:

### If natural-to-natural and swapped-to-swapped works

```text
if (A[i - 1] < A[i] && B[i - 1] < B[i]) {
    n2 = min(n2, n1)
    s2 = min(s2, s1 + 1)
}
```

### If cross transitions work

```text
if (A[i - 1] < B[i] && B[i - 1] < A[i]) {
    n2 = min(n2, s1)
    s2 = min(s2, n1 + 1)
}
```

Finally:

```text
n1 = n2
s1 = s2
```

After processing all indices, answer is:

```text
min(n1, s1)
```

because the last index may or may not be swapped.

---

# Intuition Through an Example

Consider:

```text
A = [1,3,5,4]
B = [1,2,3,7]
```

We start with:

```text
n1 = 0
s1 = 1
```

Now process each index.

---

## At i = 1

```text
a1 = 1, b1 = 1
a2 = 3, b2 = 2
```

Check:

```text
1 < 3 and 1 < 2
```

true, so:

```text
n2 = min(INF, 0) = 0
s2 = min(INF, 1 + 1) = 2
```

Check cross:

```text
1 < 2 and 1 < 3
```

also true, so:

```text
n2 = min(0, 1) = 0
s2 = min(2, 0 + 1) = 1
```

Now:

```text
n1 = 0
s1 = 1
```

---

## At i = 2

```text
a1 = 3, b1 = 2
a2 = 5, b2 = 3
```

Natural case:

```text
3 < 5 and 2 < 3
```

true:

```text
n2 = min(INF, 0) = 0
s2 = min(INF, 1 + 1) = 2
```

Cross case:

```text
3 < 3 and 2 < 5
```

false.

So:

```text
n1 = 0
s1 = 2
```

---

## At i = 3

```text
a1 = 5, b1 = 3
a2 = 4, b2 = 7
```

Natural case:

```text
5 < 4 and 3 < 7
```

false.

Cross case:

```text
5 < 7 and 3 < 4
```

true:

```text
n2 = min(INF, s1) = 2
s2 = min(INF, n1 + 1) = 1
```

So final:

```text
n1 = 2
s1 = 1
```

Answer:

```text
min(2, 1) = 1
```

which matches the expected result.

---

# Java Implementation

```java
class Solution {
    public int minSwap(int[] A, int[] B) {
        // n: natural, s: swapped
        int n1 = 0, s1 = 1;

        for (int i = 1; i < A.length; ++i) {
            int n2 = Integer.MAX_VALUE, s2 = Integer.MAX_VALUE;

            if (A[i - 1] < A[i] && B[i - 1] < B[i]) {
                n2 = Math.min(n2, n1);
                s2 = Math.min(s2, s1 + 1);
            }

            if (A[i - 1] < B[i] && B[i - 1] < A[i]) {
                n2 = Math.min(n2, s1);
                s2 = Math.min(s2, n1 + 1);
            }

            n1 = n2;
            s1 = s2;
        }

        return Math.min(n1, s1);
    }
}
```

---

# Why Constant Space Is Enough

At index `i`, the DP values depend only on the previous index `i - 1`.

So we do not need an entire DP array.

We only store:

- previous natural state
- previous swapped state

That is why the algorithm uses:

```text
n1, s1
```

and updates them to:

```text
n2, s2
```

at each step.

This reduces space from `O(n)` to `O(1)`.

---

# Correctness Intuition

The algorithm is correct because for every index `i`, it considers **all valid ways** to maintain strictly increasing order while remembering only the essential information:

- whether the previous column was swapped or not

That is sufficient because only the previous values matter when checking the strict increasing condition at the current column.

For each of the two possible current states:

- do not swap current index
- swap current index

we compute the minimum possible swaps from valid previous states.

Thus by induction, after processing all indices, `min(n1, s1)` is the minimum total number of swaps needed.

---

# Complexity Analysis

Let `N` be the length of the arrays.

## Time Complexity

We process each index once, and each step does only constant work.

So:

```text
O(N)
```

---

## Space Complexity

We only store a few integer variables:

```text
n1, s1, n2, s2
```

So:

```text
O(1)
```

---

# Common Mistakes

## 1. Using `else if` instead of two separate `if` statements

Both transition conditions can be true at the same time.

If you use `else if`, you may skip a valid transition and get the wrong answer.

---

## 2. Forgetting that swapping current index costs `+1`

Whenever the current index is swapped, the swap count must increase by one.

That is why:

```text
s2 = min(s2, s1 + 1)
s2 = min(s2, n1 + 1)
```

both include `+1`.

---

## 3. Trying to greedily decide per index

A local choice may not be globally optimal.

This is why dynamic programming is necessary.

---

## 4. Not initializing the first index correctly

At index `0`:

- no swap cost = `0`
- swap cost = `1`

So:

```text
n1 = 0
s1 = 1
```

This initialization is essential.

---

# Alternative DP Interpretation

You can also think of this as:

- `keep[i]` = min swaps if index `i` is not swapped
- `swap[i]` = min swaps if index `i` is swapped

Then the same recurrence applies.

The accepted solution simply compresses this DP to constant space.

---

# Final Summary

## State Meaning

At each index, we track two quantities:

- minimum swaps if current index is **not swapped**
- minimum swaps if current index **is swapped**

---

## Two Valid Transition Types

### Type 1: Same orientation continues

```text
A[i - 1] < A[i] and B[i - 1] < B[i]
```

This allows:

- natural → natural
- swapped → swapped

---

### Type 2: Cross orientation works

```text
A[i - 1] < B[i] and B[i - 1] < A[i]
```

This allows:

- swapped → natural
- natural → swapped

---

## Complexity

- Time: `O(N)`
- Space: `O(1)`

---

# Best Final Java Solution

```java
class Solution {
    public int minSwap(int[] A, int[] B) {
        int n1 = 0, s1 = 1;

        for (int i = 1; i < A.length; ++i) {
            int n2 = Integer.MAX_VALUE, s2 = Integer.MAX_VALUE;

            if (A[i - 1] < A[i] && B[i - 1] < B[i]) {
                n2 = Math.min(n2, n1);
                s2 = Math.min(s2, s1 + 1);
            }

            if (A[i - 1] < B[i] && B[i - 1] < A[i]) {
                n2 = Math.min(n2, s1);
                s2 = Math.min(s2, n1 + 1);
            }

            n1 = n2;
            s1 = s2;
        }

        return Math.min(n1, s1);
    }
}
```
