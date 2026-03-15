# 2702. Minimum Operations to Make Numbers Non-positive — Java Solutions and Detailed Notes

## Problem

We are given:

- an array `nums`
- two integers `x` and `y`, where:

```text
1 <= y < x
```

In one operation we choose an index `i` and do:

- `nums[i] -= x`
- every other index decreases by `y`

We need the **minimum number of operations** needed to make **all numbers <= 0**.

```java
class Solution {
    public int minOperations(int[] nums, int x, int y) {

    }
}
```

---

## Key reformulation

In every operation:

- every element effectively gets at least `-y`
- the chosen element gets an **extra** reduction of:

```text
x - y
```

That is the central idea.

So if we perform `k` total operations, then:

- every number automatically decreases by `k * y`
- and some indices additionally receive extra hits of size `(x - y)`

Let:

```text
d = x - y
```

After `k` total operations, number `nums[i]` becomes non-positive if we assign it enough “chosen” operations to cover what remains after the global `k * y` reduction.

If after subtracting `k * y`, an element is still positive:

```text
remaining = nums[i] - k * y
```

then it needs at least:

```text
ceil(remaining / d)
```

extra chosen hits.

So for a fixed `k`, the question becomes:

> Can we distribute at most `k` chosen hits across all indices so that every element becomes <= 0?

That makes the problem a classic **binary search on answer**.

---

# Approach 1: Brute Force / Simulation Intuition (Not Practical)

## Idea

One naive way to think about the problem is:

- repeatedly choose the largest remaining number,
- apply an operation to that index,
- update the whole array,
- continue until all values are non-positive.

This is a useful intuition, but it is not practical.

---

## Why it fails

Each operation affects all elements.

If we simulate literally:

- one operation costs `O(n)`
- number of operations could be huge

So this quickly becomes too slow.

Also, greedy simulation is not obviously optimal, because one choice affects every value globally.

---

# Approach 2: Binary Search on Number of Operations (Optimal)

## Main idea

Suppose we guess the answer is `k`.

Can we finish everything in exactly `k` operations?

If yes, then maybe fewer operations are enough.
If no, then we need more operations.

This is monotonic:

- if `k` operations are enough, then `k+1` are also enough
- if `k` operations are not enough, then smaller values also are not enough

So binary search applies.

---

## Feasibility for a fixed k

Let:

```text
d = x - y
```

After `k` operations, every element automatically loses:

```text
k * y
```

For `nums[i]`, if:

```text
nums[i] <= k * y
```

then it is already non-positive without any special selection.

Otherwise it still needs:

```text
nums[i] - k * y
```

more reduction.

Each time we choose that index, we gain an extra reduction of `d`.

So required special picks for index `i` are:

```text
ceil((nums[i] - k * y) / d)
```

Let the total required special picks be `need`.

If:

```text
need <= k
```

then `k` operations are feasible.

Why? Because across `k` operations, exactly one index is chosen per operation, so there are exactly `k` total “chosen-index slots” available.

---

## Ceiling division

For positive integers `a` and `b`, we compute:

```text
ceil(a / b) = (a + b - 1) / b
```

So:

```java
required = (remaining + d - 1) / d;
```

---

## Java solution

```java
class Solution {
    public int minOperations(int[] nums, int x, int y) {
        long left = 0;
        long right = 1;

        while (!canFinish(nums, x, y, right)) {
            right *= 2;
        }

        while (left < right) {
            long mid = left + (right - left) / 2;

            if (canFinish(nums, x, y, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return (int) left;
    }

    private boolean canFinish(int[] nums, int x, int y, long operations) {
        long extra = x - y;
        long neededSelections = 0;

        for (int num : nums) {
            long remaining = num - operations * (long) y;
            if (remaining > 0) {
                neededSelections += (remaining + extra - 1) / extra;
                if (neededSelections > operations) {
                    return false;
                }
            }
        }

        return neededSelections <= operations;
    }
}
```

---

## Why this upper bound search works

We do not know the answer beforehand, so we start with:

```text
right = 1
```

and keep doubling until it becomes feasible.

This guarantees we find some valid upper bound in:

```text
O(log answer)
```

steps.

Then standard binary search works on `[left, right]`.

---

## Complexity

Let `n = nums.length`.

Time complexity:

```text
O(n log answer)
```

More precisely:

- each feasibility check is `O(n)`
- binary search uses `O(log answer)` checks

Space complexity:

```text
O(1)
```

---

# Approach 3: Binary Search with a Direct Safe Upper Bound

Instead of doubling to find the upper bound, we can choose a safe one immediately.

## Observation

In the worst case, if we always choose the same index badly, every number still drops by `y` in almost every operation, and the chosen one drops even more.

A very safe upper bound is:

```text
max(nums)
```

when `y >= 1`, because after `max(nums)` operations, every number would have decreased by at least `max(nums) * y >= max(nums)` and thus be non-positive.

So we can directly binary search in:

```text
[0, max(nums)]
```

This is simpler and fully safe.

---

## Java code

```java
class Solution {
    public int minOperations(int[] nums, int x, int y) {
        int maxVal = 0;
        for (int num : nums) {
            maxVal = Math.max(maxVal, num);
        }

        long left = 0, right = maxVal;

        while (left < right) {
            long mid = left + (right - left) / 2;

            if (canFinish(nums, x, y, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return (int) left;
    }

    private boolean canFinish(int[] nums, int x, int y, long operations) {
        long extra = x - y;
        long neededSelections = 0;

        for (int num : nums) {
            long remaining = num - operations * (long) y;
            if (remaining > 0) {
                neededSelections += (remaining + extra - 1) / extra;
                if (neededSelections > operations) {
                    return false;
                }
            }
        }

        return true;
    }
}
```

---

## Complexity

Time complexity:

```text
O(n log max(nums))
```

Space complexity:

```text
O(1)
```

Because `max(nums) <= 10^9`, this is easily efficient enough.

---

# Approach 4: Priority Queue Simulation Idea (Educational, but not reliable / not optimal)

## Idea

One might try:

- always choose the currently largest number,
- use a max heap,
- lazily track the global `y` reductions,
- repeatedly reduce the chosen number by extra `(x-y)`.

This sounds tempting because we are always targeting the most “urgent” element.

---

## Why it is not a good final approach

The number of operations can still be huge.
Even with lazy propagation, simulation may take too long.

More importantly, while it often feels reasonable, proving optimality is far more complicated than the binary-search feasibility approach.

So this approach is mostly educational, not recommended.

---

# Proof of the feasibility condition

We now justify the core condition used in binary search.

Suppose we perform exactly `k` operations.

Each operation decreases all elements by at least `y`, except the chosen one, which gets `x`.
Equivalently:

- everyone loses `y`
- chosen one gets an extra `(x - y)`

After `k` operations, every element `nums[i]` has lost:

```text
k * y
```

automatically.

So its leftover is:

```text
nums[i] - k * y
```

If this is `<= 0`, no problem.

If this is positive, then we must choose index `i` some number of additional times.
Each such choice contributes an extra:

```text
x - y
```

reduction.

Thus the minimum number of times index `i` must be chosen is:

```text
ceil((nums[i] - k*y) / (x-y))
```

The total number of chosen-index assignments available across `k` operations is exactly `k`.

Therefore, `k` operations are feasible iff:

```text
sum over i of ceil(max(0, nums[i] - k*y) / (x-y)) <= k
```

That is exactly what the checker computes.

---

# Dry run

## Example 1

```text
nums = [3,4,1,7,6]
x = 4
y = 2
d = x - y = 2
```

Try `k = 2`.

After global reduction by `2 * 2 = 4`:

```text
[3,4,1,7,6] -> [-1,0,-3,3,2]
```

Still positive:

- `3` needs `ceil(3/2)=2` extra hits
- `2` needs `ceil(2/2)=1` extra hit

Total needed:

```text
3
```

But only `2` chosen slots exist, so `k=2` is impossible.

Try `k = 3`.

After global reduction by `6`:

```text
[3,4,1,7,6] -> [-3,-2,-5,1,0]
```

Still positive:

- `1` needs `ceil(1/2)=1`

Total needed = `1 <= 3`, so feasible.

Thus answer is `3`.

---

## Example 2

```text
nums = [1,2,1]
x = 2
y = 1
d = 1
```

Try `k = 1`.

Global reduction by `1`:

```text
[1,2,1] -> [0,1,0]
```

Still positive:

- `1` needs `ceil(1/1)=1`

Needed = `1 <= 1`, so feasible.

Answer = `1`.

---

# Comparison of approaches

## Approach 1: Literal simulation / greedy intuition

### Pros

- intuitive starting point

### Cons

- too slow
- hard to prove correctness

---

## Approach 2: Binary search + doubling upper bound

### Pros

- optimal
- robust
- does not need a guessed upper bound
- clean feasibility logic

### Complexity

```text
Time:  O(n log answer)
Space: O(1)
```

---

## Approach 3: Binary search + direct upper bound

### Pros

- slightly simpler code
- same optimal idea

### Complexity

```text
Time:  O(n log max(nums))
Space: O(1)
```

---

## Approach 4: Heap-based simulation idea

### Pros

- interesting thought exercise

### Cons

- not the right tool here
- worse in both proof and runtime

---

# Final recommended solution

Use **Binary Search on Number of Operations** with the feasibility check:

```text
sum ceil(max(0, nums[i] - k*y) / (x-y)) <= k
```

This is the intended clean solution.

---

# Final polished Java solution

```java
class Solution {
    public int minOperations(int[] nums, int x, int y) {
        long left = 0;
        long right = 0;

        for (int num : nums) {
            right = Math.max(right, num);
        }

        while (left < right) {
            long mid = left + (right - left) / 2;

            if (canFinish(nums, x, y, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return (int) left;
    }

    private boolean canFinish(int[] nums, int x, int y, long k) {
        long diff = x - y;
        long need = 0;

        for (int num : nums) {
            long remaining = num - k * (long) y;
            if (remaining > 0) {
                need += (remaining + diff - 1) / diff;
                if (need > k) {
                    return false;
                }
            }
        }

        return true;
    }
}
```

---

# Edge cases

## 1. Very small array

```text
nums = [5], x = 10, y = 1
```

Only one element.
One operation chooses that element and reduces it by `x`.

The checker handles this naturally.

## 2. Large values

Because `nums[i]`, `x`, and `y` can be up to `10^9`, always use `long` when computing:

- `k * y`
- cumulative `need`

Otherwise overflow can happen.

## 3. Why `y < x` matters

If `x == y`, choosing an index gives no special advantage.
But the problem guarantees:

```text
y < x
```

so `(x-y)` is positive and the extra-hit logic is valid.

---

# Pattern takeaway

This is a strong example of:

```text
Binary Search on Answer
+ transform each operation into:
  global effect + extra local effect
```

That decomposition:

```text
everyone gets -y
chosen index gets extra -(x-y)
```

is the key insight that turns a hard simulation problem into a clean counting problem.
