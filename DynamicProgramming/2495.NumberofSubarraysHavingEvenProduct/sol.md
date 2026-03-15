# Number of Subarrays with Even Product

## Problem Restatement

You are given a `0`-indexed integer array `nums`.

You must return the number of subarrays whose product is **even**.

A subarray is a contiguous part of the array.

---

## Core Observation

A product is **even** if and only if it contains **at least one even number**.

Why?

- If every number in the subarray is odd, then the product of odd numbers stays odd.
- If even one number is even, then the whole product becomes even.

So the problem is not really about multiplication.
It is actually about detecting whether a subarray contains an even element.

That is the main simplification.

---

## Two Equivalent Ways to Think About It

There are two clean approaches:

### Approach 1: Complement counting

Count:

- total number of subarrays
- subtract the number of subarrays made entirely of odd numbers

Because:

```text
even-product subarrays = total subarrays - odd-product subarrays
```

And a subarray has odd product exactly when all its elements are odd.

---

### Approach 2: Count valid subarrays ending at each index

For each index `i`, count how many subarrays ending at `i` contain at least one even number.

This leads to a very elegant one-pass formula using the position of the most recent even element.

Both approaches are `O(n)` and `O(1)` extra space.

---

# Approach 1: Total Subarrays Minus All-Odd Subarrays

## Step 1: Count total number of subarrays

For an array of length `n`, the total number of subarrays is:

```text
n * (n + 1) / 2
```

Why?

For each starting index, you can choose every ending index to its right including itself.

Equivalently:

- start at index `0`: `n` choices
- start at index `1`: `n - 1` choices
- ...
- start at index `n - 1`: `1` choice

So:

```text
n + (n - 1) + ... + 1 = n * (n + 1) / 2
```

---

## Step 2: Count subarrays consisting only of odd numbers

A subarray has odd product if and only if **every element in it is odd**.

So we scan the array and identify consecutive runs of odd numbers.

If one such odd run has length `L`, then the number of subarrays fully contained in that run is:

```text
L * (L + 1) / 2
```

Why?

Exactly the same subarray counting formula applies inside that run.

So if we sum this quantity over all maximal odd runs, we get the number of odd-product subarrays.

Then:

```text
answer = total_subarrays - odd_only_subarrays
```

---

## Example for Approach 1

Take:

```text
nums = [1, 3, 2, 5]
```

### Total subarrays

Array length is `4`, so:

```text
total = 4 * 5 / 2 = 10
```

### Odd runs

The odd runs are:

- `[1, 3]` -> length `2`
- `[5]` -> length `1`

Number of all-odd subarrays:

- from `[1, 3]`: `2 * 3 / 2 = 3`
- from `[5]`: `1 * 2 / 2 = 1`

Total odd-only subarrays:

```text
3 + 1 = 4
```

Therefore even-product subarrays:

```text
10 - 4 = 6
```

---

## Java Code for Approach 1

```java
class Solution {
    public long evenProduct(int[] nums) {
        int n = nums.length;
        long total = 1L * n * (n + 1) / 2;

        long oddOnly = 0;
        long len = 0;

        for (int x : nums) {
            if ((x & 1) == 1) {
                len++;
            } else {
                oddOnly += len * (len + 1) / 2;
                len = 0;
            }
        }

        oddOnly += len * (len + 1) / 2;

        return total - oddOnly;
    }
}
```

---

## Detailed Logic of the Code

### Total subarrays

```java
long total = 1L * n * (n + 1) / 2;
```

We use `1L` to force `long` arithmetic and avoid integer overflow.

---

### Track current odd run length

```java
long len = 0;
```

This stores the current streak of consecutive odd numbers.

---

### Scan the array

```java
for (int x : nums) {
    if ((x & 1) == 1) {
        len++;
    } else {
        oddOnly += len * (len + 1) / 2;
        len = 0;
    }
}
```

If `x` is odd:

- extend the current odd run

If `x` is even:

- the odd run ends
- add the number of subarrays inside that run
- reset the run length

---

### Final leftover run

```java
oddOnly += len * (len + 1) / 2;
```

If the array ends with odd numbers, that run would not have been added inside the loop, so we add it afterward.

---

### Final answer

```java
return total - oddOnly;
```

That gives the number of subarrays with even product.

---

# Approach 2: Count Subarrays Ending at Each Index

This is the most elegant one-pass solution.

## Key idea

For each index `i`, let us count how many subarrays ending at `i` have even product.

A subarray ending at `i` has even product if it contains at least one even number.

So we only need to know:

> where is the most recent even element at or before index `i`?

Let that index be `lastEven`.

Then every subarray ending at `i` that starts at any index from:

```text
0 to lastEven
```

will contain that even number, so it will have even product.

How many such starting positions are there?

```text
lastEven + 1
```

So the contribution of index `i` is:

```text
lastEven + 1
```

If no even has appeared yet, then `lastEven = -1`, and contribution is `0`.

---

## Example for Approach 2

Again take:

```text
nums = [1, 3, 2, 5]
```

Track `lastEven`:

### i = 0, nums[0] = 1

No even seen yet:

```text
lastEven = -1
contribution = 0
```

### i = 1, nums[1] = 3

Still no even:

```text
lastEven = -1
contribution = 0
```

### i = 2, nums[2] = 2

Now:

```text
lastEven = 2
contribution = 2 + 1 = 3
```

These are subarrays ending at 2:

- `[2]`
- `[3, 2]`
- `[1, 3, 2]`

### i = 3, nums[3] = 5

Most recent even is still at index 2:

```text
lastEven = 2
contribution = 3
```

These are:

- `[2, 5]`
- `[3, 2, 5]`
- `[1, 3, 2, 5]`

Total:

```text
0 + 0 + 3 + 3 = 6
```

Correct.

---

## Why This Works

Suppose the latest even element at or before `i` is at index `e`.

Then:

- any subarray ending at `i` and starting at `s <= e` includes index `e`
- so it contains an even number
- therefore its product is even

But if `s > e`, then the entire subarray lies strictly after the last even number, meaning all its elements are odd.
So its product is odd.

Thus the valid starting positions are exactly:

```text
0, 1, 2, ..., e
```

Count:

```text
e + 1
```

That proves the formula.

---

## Java Code for Approach 2

```java
class Solution {
    public long evenProduct(int[] nums) {
        long ans = 0;
        int lastEven = -1;

        for (int i = 0; i < nums.length; i++) {
            if ((nums[i] & 1) == 0) {
                lastEven = i;
            }
            ans += lastEven + 1;
        }

        return ans;
    }
}
```

---

## Detailed Logic of the Code

### Answer accumulator

```java
long ans = 0;
```

Stores the total number of even-product subarrays.

---

### Most recent even index

```java
int lastEven = -1;
```

Initially, no even element has been seen.

---

### Iterate over all indices

```java
for (int i = 0; i < nums.length; i++) {
```

We count the number of valid subarrays ending at each `i`.

---

### Update most recent even position

```java
if ((nums[i] & 1) == 0) {
    lastEven = i;
}
```

If current number is even, it becomes the newest even index.

---

### Add contribution for subarrays ending at `i`

```java
ans += lastEven + 1;
```

If `lastEven = -1`, contribution is `0`.

Otherwise, every starting index from `0` through `lastEven` forms a valid subarray ending at `i`.

---

## Which Approach Is Better?

Both are correct and efficient.

### Approach 1

Pros:

- very intuitive
- uses complement counting
- good if you naturally think in terms of runs

### Approach 2

Pros:

- shorter
- more elegant
- directly counts valid subarrays in one pass

In practice, Approach 2 is usually the better final implementation.

---

# Complexity Analysis

Let `n = nums.length`.

## Time Complexity

Both approaches scan the array once.

So:

```text
O(n)
```

---

## Space Complexity

Both approaches use only a few variables.

So:

```text
O(1)
```

---

# Important Edge Cases

## All numbers are odd

Example:

```text
[1, 3, 5]
```

Every subarray has odd product.

Answer should be `0`.

- Approach 1: total subarrays equals odd-only subarrays, so answer is `0`
- Approach 2: `lastEven` remains `-1`, so every contribution is `0`

Correct.

---

## All numbers are even

Example:

```text
[2, 4, 6]
```

Every subarray has even product.

Total subarrays:

```text
3 * 4 / 2 = 6
```

- Approach 1: no odd runs, so odd-only count is `0`
- Approach 2: every index contributes all possible starting positions

Correct.

---

## Single-element array

Example:

```text
[7]
```

Only one subarray, product is odd.

Answer:

```text
0
```

Example:

```text
[8]
```

Only one subarray, product is even.

Answer:

```text
1
```

Both approaches handle this naturally.

---

# Final Takeaway

The multiplication in the problem statement is actually misleading.

The real mathematical fact is simple:

> A product is even if and only if at least one factor is even.

That transforms the problem into a pure parity-counting problem.

The two clean solutions are:

1. Count all subarrays and subtract all-odd subarrays
2. For each index, count how many subarrays ending there include the latest even element

Both give:

- **Time Complexity:** `O(n)`
- **Space Complexity:** `O(1)`

The most elegant final solution is usually the second one with `lastEven`.
