# Maximum Subarray Sum After Exactly One Squaring Operation — Detailed Summary

## Problem

You are given an integer array `nums`.

You must perform **exactly one operation**:

- choose one index `i`
- replace `nums[i]` with `nums[i] * nums[i]`

After doing that, return the **maximum possible subarray sum**.

Rules:

- the chosen subarray must be **non-empty**
- the squaring operation must be used **exactly once**

---

## Core idea

This is a dynamic programming problem built on top of **Kadane’s algorithm**.

Track two states for subarrays ending at the current index:

- `noOp`: best subarray sum ending here with **no square used yet**
- `used`: best subarray sum ending here with **exactly one square already used**

The answer is the maximum value of `used` over the whole array, because the operation must be used exactly once.

---

## State transitions

Let:

```text
x = nums[i]
sq = x * x
```

### 1) No operation used yet

This is ordinary Kadane:

```text
noOp = max(x, prevNoOp + x)
```

Either:

- start a new subarray at `i`
- or extend the previous one

### 2) Exactly one operation used

There are three possibilities:

- start new subarray at `i` and square `nums[i]` → `sq`
- extend a no-op subarray and use the square now → `prevNoOp + sq`
- extend a subarray that already used the square earlier → `prevUsed + x`

So:

```text
used = max(sq, prevNoOp + sq, prevUsed + x)
```

---

## Why this is correct

For every index `i`, any optimal subarray ending at `i` must belong to one of those categories.

For `noOp`, there are only two possibilities:

- start at `i`
- or extend the previous no-op subarray

For `used`, the square is either:

- used at `i` on a fresh subarray
- used at `i` while extending a no-op subarray
- or already used before, in which case we just extend that subarray

These cases are complete, so the recurrence covers all valid possibilities.

---

## Java solution

```java
class Solution {
    public int maxSumAfterOperation(int[] nums) {
        long noOp = nums[0];
        long used = 1L * nums[0] * nums[0];
        long ans = used;

        for (int i = 1; i < nums.length; i++) {
            long x = nums[i];
            long sq = x * x;

            long prevNoOp = noOp;
            long prevUsed = used;

            noOp = Math.max(x, prevNoOp + x);

            used = Math.max(
                sq,
                Math.max(prevNoOp + sq, prevUsed + x)
            );

            ans = Math.max(ans, used);
        }

        return (int) ans;
    }
}
```

---

## Why use `long` internally

Squaring can overflow `int`.

Example:

```text
100000 * 100000 = 10,000,000,000
```

So compute like this:

```java
long sq = 1L * nums[i] * nums[i];
```

If the judge expects `int`, cast only at the end:

```java
return (int) ans;
```

---

## Dry run

### Example

```text
nums = [2, -1, -4, -3]
```

A strong candidate is squaring `-4`:

```text
-4 -> 16
```

Then the best subarray becomes:

```text
[2, -1, 16] => 17
```

Let us see the DP.

### Index 0: `2`

```text
noOp = 2
used = 4
ans = 4
```

### Index 1: `-1`, square = `1`

```text
prevNoOp = 2
prevUsed = 4

noOp = max(-1, 2 + (-1)) = 1

used = max(
    1,
    2 + 1,
    4 + (-1)
) = 3

ans = 4
```

### Index 2: `-4`, square = `16`

```text
prevNoOp = 1
prevUsed = 3

noOp = max(-4, 1 + (-4)) = -3

used = max(
    16,
    1 + 16,
    3 + (-4)
) = 17

ans = 17
```

### Index 3: `-3`, square = `9`

```text
prevNoOp = -3
prevUsed = 17

noOp = max(-3, -3 + (-3)) = -3

used = max(
    9,
    -3 + 9,
    17 + (-3)
) = 14

ans = 17
```

Final answer:

```text
17
```

---

## Edge cases

### Single element

Example:

```text
nums = [-5]
```

You must square it:

```text
25
```

### All negatives

Squaring a negative may create the best answer.

### All positives

Often best to square a strong positive inside the best subarray.

### Zeros present

The DP naturally handles whether squaring zero helps or not.

---

## Common mistake

A frequent Java compile error is:

```text
possible lossy conversion from long to int
```

That happens when the method returns `long` but the driver expects `int`.

Correct fix:

- keep calculations in `long`
- return `(int) ans`

---

## Complexity

### Time

```text
O(n)
```

### Space

```text
O(1)
```

---

## Compact recurrence summary

```text
x  = nums[i]
sq = x * x

noOp = max(x, prevNoOp + x)

used = max(
    sq,
    prevNoOp + sq,
    prevUsed + x
)
```

Answer:

```text
max over all used
```

---

## Final takeaway

This is a one-operation extension of Kadane’s algorithm.

Use two DP states:

- before using the square
- after using the square

That gives a clean **linear-time** and **constant-space** solution.
