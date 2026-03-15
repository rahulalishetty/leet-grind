# 2936. Number of Equal Numbers Blocks — Java Solutions and Detailed Notes

## Problem

We are given a huge array `nums`, but we cannot access it directly.

Instead, we use the interface:

```java
int at(long index)
long size()
```

The array satisfies a special property:

> All equal values appear in one contiguous block.

So if a value appears multiple times, all its occurrences are adjacent.

We need to return the number of **maximal equal-value blocks**.

```java
class Solution {
    public int countBlocks(BigArray nums) {

    }
}
```

---

## What is a block?

A block is a maximal contiguous segment containing the same value.

Examples:

```text
[3,3,3,3,3]       -> 1 block
[1,1,1,3,9,9,9,2,10,10] -> 5 blocks
[1,2,3,4,5,6,7]   -> 7 blocks
```

Because equal values are already grouped together, the array is effectively a sequence of blocks.

---

# Core challenge

`nums.length` can be as large as:

```text
10^15
```

So iterating one index at a time is impossible.

We must exploit the fact that once we know the start of a block, we can jump directly to the start of the next block.

That suggests:

- read the value at the current start,
- find the last occurrence of that same value using binary search,
- jump to the next index after that block.

This is the key optimal idea.

---

# Approach 1: Linear Scan by Adjacent Comparison (Conceptual, impractical)

## Idea

If direct array access were cheap and the array were not enormous, we could count a new block whenever:

```text
nums[i] != nums[i - 1]
```

So the number of blocks would be:

```text
1 + number of value changes
```

---

## Java code sketch

```java
class Solution {
    public int countBlocks(BigArray nums) {
        long n = nums.size();
        int blocks = 1;

        for (long i = 1; i < n; i++) {
            if (nums.at(i) != nums.at(i - 1)) {
                blocks++;
            }
        }

        return blocks;
    }
}
```

---

## Complexity

Time complexity:

```text
O(n)
```

Space complexity:

```text
O(1)
```

---

## Verdict

Correct in theory, completely infeasible because `n` may be `10^15`.

This is only a baseline to understand the problem.

---

# Approach 2: Jump Block-by-Block Using Binary Search (Optimal)

## Main idea

Suppose we are currently at the start of a block, index `start`.

Let:

```text
value = nums.at(start)
```

Because equal values are contiguous, the entire block of `value` forms a consecutive interval:

```text
[start ... end]
```

If we can find the last index `end` where:

```text
nums.at(end) == value
```

then:

- we count one block,
- jump directly to `end + 1`,
- repeat.

The only remaining question is:

> How do we find the last occurrence of `value` starting from `start`?

Answer:

```text
Binary Search
```

---

## Binary search target

For a fixed `value` and starting position `start`, search in:

```text
[start, n - 1]
```

for the largest index `mid` such that:

```text
nums.at(mid) == value
```

Because equal values are contiguous, the predicate:

```text
nums.at(mid) == value
```

is true on a prefix of that suffix interval and false afterwards.

That is exactly the shape needed for binary search.

---

## Algorithm

1. Initialize:

```text
index = 0
blocks = 0
```

2. While `index < n`:
   - read `value = nums.at(index)`
   - binary search the last position `end` with that same value
   - increment `blocks`
   - set `index = end + 1`

3. Return `blocks`

---

## Java code

```java
class Solution {
    public int countBlocks(BigArray nums) {
        long n = nums.size();
        long index = 0;
        int blocks = 0;

        while (index < n) {
            int value = nums.at(index);
            long end = findLastIndex(nums, index, n - 1, value);
            blocks++;
            index = end + 1;
        }

        return blocks;
    }

    private long findLastIndex(BigArray nums, long left, long right, int value) {
        long lo = left, hi = right;

        while (lo < hi) {
            long mid = lo + (hi - lo + 1) / 2; // upper mid
            if (nums.at(mid) == value) {
                lo = mid;
            } else {
                hi = mid - 1;
            }
        }

        return lo;
    }
}
```

---

## Why upper mid?

We use:

```java
mid = lo + (hi - lo + 1) / 2;
```

because we are searching for the **last** occurrence.

If we used lower mid, we might get stuck when `lo + 1 == hi`.

Upper mid guarantees progress.

---

## Example walkthrough

### Example

```text
nums = [1,1,1,3,9,9,9,2,10,10]
```

### Step 1

`index = 0`, value = `1`

Binary search finds last `1` at index `2`.

Count block 1.
Jump to:

```text
index = 3
```

### Step 2

`index = 3`, value = `3`

Binary search finds last `3` at index `3`.

Count block 2.
Jump to:

```text
index = 4
```

### Step 3

`index = 4`, value = `9`

Binary search finds last `9` at index `6`.

Count block 3.
Jump to:

```text
index = 7
```

### Step 4

`index = 7`, value = `2`

Binary search finds last `2` at index `7`.

Count block 4.
Jump to:

```text
index = 8
```

### Step 5

`index = 8`, value = `10`

Binary search finds last `10` at index `9`.

Count block 5.
Jump to:

```text
index = 10
```

Stop. Answer = `5`.

---

## Complexity

Let `B` be the number of blocks and `N = nums.size()`.

Each block triggers one binary search over an interval inside `[0, N-1]`.

So time complexity is:

```text
O(B log N)
```

Space complexity:

```text
O(1)
```

This is excellent, because if the array has few large blocks, we skip huge portions immediately.

---

# Approach 3: Exponential Search + Binary Search per Block (Useful variant)

## Idea

Instead of binary searching all the way to `n - 1` each time, we can first use **exponential search** to find a smaller search window containing the end of the current block.

Suppose the current block starts at `start` with value `v`.

We test indices:

```text
start + 1, start + 2, start + 4, start + 8, ...
```

until we either:

- go out of bounds, or
- find an index whose value is different from `v`

This gives a bounded range where the block ends.

Then perform binary search only inside that smaller range.

---

## Why this can help

If blocks are typically short, exponential search finds a tight window quickly.

This can reduce constant factors.

Asymptotically it is still:

```text
O(B log N)
```

in the worst case, but it is a nice alternative.

---

## Java code

```java
class Solution {
    public int countBlocks(BigArray nums) {
        long n = nums.size();
        long index = 0;
        int blocks = 0;

        while (index < n) {
            int value = nums.at(index);
            long end = findLastIndexByExponentialSearch(nums, index, n, value);
            blocks++;
            index = end + 1;
        }

        return blocks;
    }

    private long findLastIndexByExponentialSearch(BigArray nums, long start, long n, int value) {
        long step = 1;
        long bound = start;

        while (bound + step < n && nums.at(bound + step) == value) {
            bound += step;
            step <<= 1;
        }

        long left = bound;
        long right = Math.min(n - 1, bound + step);

        while (left < right) {
            long mid = left + (right - left + 1) / 2;
            if (nums.at(mid) == value) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }

        return left;
    }
}
```

---

## Complexity

Worst-case time complexity:

```text
O(B log N)
```

Space complexity:

```text
O(1)
```

---

# Why the binary-search approach is correct

We prove it in two parts.

## Lemma 1

For a block starting at index `s` with value `v = nums.at(s)`, all indices containing `v` form one contiguous interval.

This is exactly the problem guarantee:

> all equal values are adjacent.

So there exists a unique maximal interval:

```text
[s ... e]
```

such that every element in it equals `v`.

---

## Lemma 2

Binary search correctly finds the last index `e` of that interval.

Inside the search range `[s, n-1]`, the predicate:

```text
nums.at(i) == v
```

is true for all `i <= e` and false for all `i > e`.

This is a monotone predicate, so binary search for the last true index is valid.

---

## Lemma 3

Jumping from `e + 1` does not miss any block.

Because `[s ... e]` is maximal, index `e + 1` (if it exists) is the first index of the next block.

So counting this block and jumping to `e + 1` is correct.

---

## Therefore

Repeating this process counts every maximal equal-value block exactly once.

So the algorithm is correct.

---

# Comparison of approaches

## Approach 1: Linear adjacent scan

### Pros

- easiest to understand

### Cons

- impossible for huge arrays

### Complexity

```text
Time:  O(N)
Space: O(1)
```

---

## Approach 2: Binary search from each block start (Recommended)

### Pros

- simple
- optimal in spirit
- leverages the adjacency guarantee directly

### Complexity

```text
Time:  O(B log N)
Space: O(1)
```

---

## Approach 3: Exponential search + binary search

### Pros

- elegant variant
- potentially tighter windows when blocks are short

### Cons

- slightly more code
- no asymptotic improvement over Approach 2

### Complexity

```text
Time:  O(B log N)
Space: O(1)
```

---

# Final recommended Java solution

```java
class Solution {
    public int countBlocks(BigArray nums) {
        long n = nums.size();
        long start = 0;
        int blocks = 0;

        while (start < n) {
            int value = nums.at(start);

            long lo = start, hi = n - 1;
            while (lo < hi) {
                long mid = lo + (hi - lo + 1) / 2;
                if (nums.at(mid) == value) {
                    lo = mid;
                } else {
                    hi = mid - 1;
                }
            }

            blocks++;
            start = lo + 1;
        }

        return blocks;
    }
}
```

---

# Edge cases

## 1. Entire array is one value

```text
[3,3,3,3,3]
```

Binary search finds the last index immediately, answer = `1`.

## 2. Every value is distinct

```text
[1,2,3,4,5,6,7]
```

Each binary search finds the same starting index as ending index, so answer = array length.

## 3. Very large array with huge blocks

This is where the jump approach shines.

Instead of visiting every element, we skip entire blocks in one step.

---

# Pattern takeaway

This problem is a great example of:

```text
Monotone block structure
+ jump over segments using binary search
```

Whenever you know that equal values or some property forms contiguous blocks, you should ask:

> Can I identify the end of the current block quickly and jump?

That often turns an impossible linear scan into a logarithmic-per-block solution.
