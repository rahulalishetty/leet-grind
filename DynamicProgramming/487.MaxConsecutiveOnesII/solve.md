# 487. Max Consecutive Ones II — Detailed Notes

## Intuition

First, let us restate the problem clearly.

We are given a binary array and asked to find the maximum number of consecutive `1`s, with one extra allowance:

> we may flip **at most one `0`**.

A very useful way to think about this is:

> We are looking for the longest subarray that contains **at most one `0`**.

Why is this equivalent?

Because if a subarray contains exactly one `0`, we can flip that `0` into `1`, and the whole subarray becomes all `1`s.

So instead of literally flipping bits, we can solve the easier conceptual problem:

> Find the longest contiguous subarray containing at most one `0`.

This reformulation is the key idea behind the optimal solution.

---

# Approach 1: Brute Force

## Intuition

The brute force idea is straightforward:

- try every possible subarray
- count how many `0`s are inside it
- if the number of `0`s is at most `1`, then it is a valid candidate
- track the maximum length among all valid candidates

This is a common starting point for interview problems because it makes the problem concrete and helps reveal where the repeated work happens.

---

## Algorithm

For every possible starting index `left`:

1. initialize `numZeroes = 0`
2. extend the subarray one position at a time with `right`
3. every time `nums[right] == 0`, increment `numZeroes`
4. if `numZeroes <= 1`, the current subarray is valid
5. update the best answer using:

```text
right - left + 1
```

This checks every contiguous sequence in the array.

---

## Java Implementation

```java
class Solution {
    public int findMaxConsecutiveOnes(int[] nums) {
        int longestSequence = 0;

        for (int left = 0; left < nums.length; left++) {
            int numZeroes = 0;

            // Check every consecutive sequence
            for (int right = left; right < nums.length; right++) {
                // Count how many 0's
                if (nums[right] == 0) {
                    numZeroes += 1;
                }

                // Update answer if it's valid
                if (numZeroes <= 1) {
                    longestSequence = Math.max(longestSequence, right - left + 1);
                }
            }
        }

        return longestSequence;
    }
}
```

---

## Why It Works

This algorithm explicitly examines every possible contiguous subarray.

For each one, it counts the number of zeros and accepts the subarray only if it contains at most one zero.

Since every valid candidate is considered, the longest valid candidate will definitely be found.

---

## Complexity Analysis

Let `n` be the length of the array.

### Time Complexity

There are two nested loops:

- the outer loop chooses `left`
- the inner loop chooses `right`

So the total number of iterations is quadratic:

```text
O(n^2)
```

### Space Complexity

The algorithm uses only a constant number of variables:

- `left`
- `right`
- `numZeroes`
- `longestSequence`

So:

```text
O(1)
```

---

## Drawback

The problem with brute force is that it performs a large amount of repeated work.

For example, if we already examined a long subarray starting at index `0`, then when we start again from index `1`, we recompute information for almost the same region.

This overlap suggests that a **sliding window** solution may be possible.

---

# Approach 2: Sliding Window

## Intuition

The brute force solution checks too many overlapping subarrays.

What we really want is a way to maintain a current subarray efficiently while moving through the array only once.

That is exactly what the **sliding window** pattern is designed for.

We define a window `[left, right]` and maintain the following invariant:

> The current window contains **at most one `0`**.

That means:

- if the window is valid, we should try to **expand** it
- if the window becomes invalid, we should **shrink** it until it becomes valid again

This avoids recomputing information for overlapping subarrays from scratch.

---

## Valid and Invalid States

We define:

### Valid state

The current window contains:

```text
0 or 1 zero
```

### Invalid state

The current window contains:

```text
2 zeros
```

A window with two zeros cannot represent a valid answer, because we are allowed to flip at most one zero.

So whenever the window becomes invalid, we shrink it from the left until it becomes valid again.

---

## Algorithm

Maintain:

- `left` pointer
- `right` pointer
- `numZeroes` = number of zeros in the current window
- `longestSequence` = best answer seen so far

Process the array as follows:

1. Expand the window by moving `right`
2. If `nums[right] == 0`, increment `numZeroes`
3. While `numZeroes == 2`, shrink the window from the left:
   - if `nums[left] == 0`, decrement `numZeroes`
   - increment `left`
4. Now the window is valid again
5. Update the maximum length
6. Continue until `right` reaches the end

---

## Java Implementation

```java
class Solution {
    public int findMaxConsecutiveOnes(int[] nums) {
        int longestSequence = 0;
        int left = 0;
        int right = 0;
        int numZeroes = 0;

        // While our window is in bounds
        while (right < nums.length) {

            // Increase numZeroes if the rightmost element is 0
            if (nums[right] == 0) {
                numZeroes++;
            }

            // If our window is invalid, contract our window
            while (numZeroes == 2) {
                if (nums[left] == 0) {
                    numZeroes--;
                }
                left++;
            }

            // Update our longest sequence answer
            longestSequence = Math.max(longestSequence, right - left + 1);

            // Expand our window
            right++;
        }

        return longestSequence;
    }
}
```

---

## Step-by-Step Reasoning

Consider:

```text
nums = [1, 0, 1, 1, 0]
```

We slide the window across the array:

- Start with `[1]` → valid
- Extend to `[1,0]` → valid
- Extend to `[1,0,1]` → valid
- Extend to `[1,0,1,1]` → valid
- Extend to `[1,0,1,1,0]` → now invalid because there are 2 zeros

So we move `left` forward until one zero is removed.

That restores validity and allows us to continue.

At every point, we keep the largest valid window seen so far.

---

## Why It Works

The sliding window always maintains a valid subarray with at most one zero.

Whenever a second zero enters the window, we immediately shrink from the left until only one zero remains.

This guarantees that every time we update `longestSequence`, the current window is a valid candidate.

Since `right` explores all positions and `left` only moves forward as needed, every relevant valid window is considered in an efficient way.

---

## Complexity Analysis

Let `n` be the length of the array.

### Time Complexity

Both pointers move only forward:

- `right` goes from `0` to `n - 1`
- `left` also moves from `0` to at most `n - 1`

So the total amount of work is linear:

```text
O(n)
```

### Space Complexity

We only store a fixed number of variables:

- `left`
- `right`
- `numZeroes`
- `longestSequence`

So:

```text
O(1)
```

---

# Why Sliding Window Is Better Than Brute Force

The brute force approach recomputes the zero count for many overlapping subarrays.

The sliding window approach avoids this by maintaining the zero count incrementally.

Instead of restarting the count for every possible `left`, we carry forward the current window state and adjust it only when necessary.

That reduces the running time from:

```text
O(n^2)
```

to:

```text
O(n)
```

which is a major improvement.

---

# Key Takeaways

## Main Reformulation

The phrase:

> flip at most one `0`

is equivalent to:

> allow at most one `0` inside an otherwise consecutive run of `1`s

That is the core insight.

## Brute Force

- simple and intuitive
- checks all subarrays
- time complexity `O(n^2)`

## Sliding Window

- maintains the longest valid window with at most one zero
- expands when valid
- contracts when invalid
- time complexity `O(n)`

---

# Complexity Summary

| Approach       | Idea                                    | Time Complexity | Space Complexity |
| -------------- | --------------------------------------- | --------------: | ---------------: |
| Brute Force    | Check every subarray and count zeros    |        `O(n^2)` |           `O(1)` |
| Sliding Window | Maintain a window with at most one zero |          `O(n)` |           `O(1)` |

---

# Final Insight

This problem is a classic example of turning a vague operation:

```text
"flip at most one 0"
```

into a precise window condition:

```text
"subarray contains at most one 0"
```

Once we do that, the sliding window pattern becomes natural and leads directly to the optimal linear-time solution.
