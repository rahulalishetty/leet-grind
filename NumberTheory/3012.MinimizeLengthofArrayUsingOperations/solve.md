# 3012. Minimize Length of Array Using Operations

## Problem Restatement

You are given an array `nums` of positive integers.

Operation:

1. pick two distinct indices `i` and `j` such that both values are positive
2. append `nums[i] % nums[j]`
3. delete `nums[i]` and `nums[j]`

So each operation reduces the array length by exactly **1**.

We want the **minimum possible final length**.

---

## First Principles

This problem looks operational, but the real structure is number-theoretic.

The key questions are:

- what values can eventually be generated?
- when can we force the array down to length `1`?
- when are we stuck with length `2` or more?

The entire solution turns on the **minimum element** of the array.

Let:

```text
mn = minimum value in nums
cnt = number of times mn appears
```

Then only two cases matter:

1. **Some element is not divisible by `mn`**
2. **Every element is divisible by `mn`**

These lead to completely different outcomes.

---

# Core Insight

## Observation 1: If some number is not divisible by the minimum, answer is `1`

Suppose `mn` is the smallest number in the array, and there exists some element `x` such that:

```text
x % mn != 0
```

Then:

```text
x % mn < mn
```

because remainder is always smaller than the divisor.

That means we can produce a value **strictly smaller than the current minimum**.

Once a smaller number appears, we can keep driving values downward, and eventually produce `1`, after which the array can be collapsed to length `1`.

So in this case:

```text
answer = 1
```

---

## Observation 2: If every number is divisible by the minimum, then no positive value smaller than `mn` can ever appear

If every element is divisible by `mn`, then:

- all elements are multiples of `mn`
- the remainder of one multiple of `mn` modulo another multiple of `mn` is also a multiple of `mn`

So every positive value that ever appears remains a multiple of `mn`.

This means:

- we can never create a positive value less than `mn`
- in particular, we can never create `1` unless `mn = 1`

So the only useful reductions come from pairing equal minimum values:

```text
mn % mn = 0
```

That allows two copies of `mn` to disappear and be replaced by `0`.

Since `0` cannot be selected later, zeros are “dead” values.

Thus the process reduces to repeatedly pairing up copies of the minimum.

If there are `cnt` copies of `mn`, then after pairing them as much as possible, the number of surviving positive minima is:

```text
ceil(cnt / 2)
```

That is the minimum possible final length.

So in this case:

```text
answer = ceil(cnt / 2) = (cnt + 1) / 2
```

using integer division.

---

# Final Formula

Let:

- `mn = min(nums)`
- `cnt = frequency of mn`

Then:

- if there exists any `x` such that `x % mn != 0`, answer is `1`
- otherwise, answer is:

```text
(cnt + 1) / 2
```

---

# Approach 1 — Simulation Mindset (Conceptual)

## Intuition

This is not a practical algorithm for the constraints, but it is the most natural way to reason at first.

We may imagine repeatedly choosing pairs and trying to reduce numbers. The important thing you quickly notice is:

- `%` creates smaller numbers
- smaller numbers are powerful because they divide other numbers
- once a very small number appears, the array can often collapse quickly

This mindset helps discover the decisive role of the minimum element.

---

## Why brute-force simulation is not feasible

The operation choices are combinatorial:

- many possible pairs
- dynamic array state
- values change each step

Trying all operation sequences is exponential and impossible for `n = 10^5`.

So this is useful only as intuition, not as a real solution.

---

## Pseudocode Idea

```text
Try all pairs
Generate new states
Search for smallest final length
```

This explodes immediately.

---

## Complexity

Exponential / infeasible.

---

# Approach 2 — Greedy / Number Theory Based on the Minimum

## Intuition

Instead of simulating the whole process, analyze what the minimum value can do.

Let:

```text
mn = minimum element
```

Now ask:

### Case A

Is there an element `x` such that:

```text
x % mn != 0
```

If yes, then:

```text
x % mn < mn
```

So we create a new smaller positive value. This breaks the old minimum barrier. Repeating this idea eventually lets us reach `1`, and from there the array can be reduced to length `1`.

So this case gives answer `1`.

### Case B

For every element `x`:

```text
x % mn == 0
```

Then every number is a multiple of `mn`. No positive remainder smaller than `mn` can ever be produced.

So the only productive operation is:

```text
mn % mn = 0
```

Each such operation consumes **two** copies of `mn` and produces one zero.

Zeros do not help in further operations.

Therefore only the count of minimum elements matters, and the best we can do is pair them off.

Final length:

```text
ceil(cnt / 2)
```

---

## Java Code

```java
class Solution {
    public int minimumArrayLength(int[] nums) {
        int mn = Integer.MAX_VALUE;
        for (int x : nums) {
            mn = Math.min(mn, x);
        }

        int countMin = 0;
        for (int x : nums) {
            if (x == mn) {
                countMin++;
            } else if (x % mn != 0) {
                return 1;
            }
        }

        return (countMin + 1) / 2;
    }
}
```

---

## Complexity Analysis

Let `n = nums.length`.

### Time Complexity

Two passes through the array:

```text
O(n)
```

### Space Complexity

Only a few variables:

```text
O(1)
```

---

# Approach 3 — Frequency View

## Intuition

This is the same optimal solution, but viewed from counting rather than direct case analysis.

Build the reasoning around the smallest number `mn` and its frequency.

Let:

```text
freq[mn] = cnt
```

Now think in terms of resources:

- Any number not divisible by `mn` lets us create a smaller positive remainder, which eventually collapses the whole array to length `1`.
- If all numbers are divisible by `mn`, then all positive future numbers stay in the lattice:

```text
mn, 2mn, 3mn, ...
```

No smaller positive value can ever appear.
So only copies of `mn` can neutralize one another via:

```text
mn % mn = 0
```

This makes the problem equivalent to:

> Given `cnt` copies of the minimum, how many survive after repeatedly removing them in pairs?

That is exactly:

```text
ceil(cnt / 2)
```

---

## Java Code

```java
class Solution {
    public int minimumArrayLength(int[] nums) {
        int mn = nums[0];
        for (int x : nums) {
            if (x < mn) mn = x;
        }

        int cnt = 0;
        boolean hasNonMultiple = false;

        for (int x : nums) {
            if (x == mn) cnt++;
            if (x % mn != 0) hasNonMultiple = true;
        }

        if (hasNonMultiple) return 1;
        return (cnt + 1) / 2;
    }
}
```

This is logically identical to Approach 2, just framed differently.

---

## Complexity

```text
Time:  O(n)
Space: O(1)
```

---

# Why the Answer Is Correct

## Lemma 1

If there exists `x` such that `x % mn != 0`, then the answer is `1`.

### Proof

Because `mn` is the minimum positive element:

```text
0 < x % mn < mn
```

So one operation can create a smaller positive number.

Now the minimum strictly decreases.

Repeatedly applying the same principle, we can keep generating smaller positive values until reaching `1` (the Euclidean algorithm viewpoint).

Once `1` exists, any positive `y` can interact with `1`:

```text
y % 1 = 0
1 % y = 1
```

So we can use `1` to eliminate the rest and reduce the array to a single element.

Hence the minimum final length is `1`.

---

## Lemma 2

If every element is divisible by `mn`, then no positive number smaller than `mn` can ever appear.

### Proof

Initially all values are multiples of `mn`.

Take any two multiples:

```text
a = p * mn
b = q * mn
```

Then:

```text
a % b
```

is also a multiple of `mn`.

So all future positive values remain multiples of `mn`.

Since the smallest positive multiple of `mn` is `mn` itself, no smaller positive value can ever be created.

Proved.

---

## Lemma 3

In the divisible case, the minimum final length is `ceil(cnt / 2)`.

### Proof

Only copies of `mn` can create zero cheaply:

```text
mn % mn = 0
```

Each such operation removes two minimum elements and creates one zero.

Zeros cannot be used in future operations.

So every operation can eliminate at most two copies of `mn`.

If there are `cnt` copies, after optimal pairing the number of remaining “irreducible slots” is:

```text
ceil(cnt / 2)
```

This bound is achievable by repeatedly pairing minima.

Hence it is optimal.

---

# Worked Examples

## Example 1

```text
nums = [1,4,3,1]
```

Minimum:

```text
mn = 1
cnt = 2
```

Every number is divisible by `1`, so we are in Case B.

Answer:

```text
(cnt + 1) / 2 = (2 + 1) / 2 = 1
```

Matches the example.

---

## Example 2

```text
nums = [5,5,5,10,5]
```

Minimum:

```text
mn = 5
cnt = 4
```

Every number is divisible by `5`.

So answer:

```text
(cnt + 1) / 2 = (4 + 1) / 2 = 2
```

Matches the example.

---

## Example 3

```text
nums = [2,3,4]
```

Minimum:

```text
mn = 2
```

Check other elements:

```text
3 % 2 = 1 != 0
```

So we can create a smaller positive value than `2`, therefore answer is:

```text
1
```

Matches the example.

---

# Edge Cases

## 1. Single element array

```text
nums.length = 1
```

No operation is possible, so answer is already `1`.

Our formula still works.

If `cnt = 1` and everything is divisible by `mn`:

```text
(cnt + 1) / 2 = 1
```

---

## 2. All elements equal

Example:

```text
[7,7,7,7,7]
```

Then `mn = 7`, `cnt = 5`, and all are divisible by `mn`.

Answer:

```text
(5 + 1) / 2 = 3
```

Indeed:

- pair two 7s -> 0
- pair two 7s -> 0
- one 7 remains

Final array length = 3 (`0, 0, 7`)

---

## 3. Minimum appears once but some other value is not divisible by it

Example:

```text
[6,10]
```

Minimum is `6`.

```text
10 % 6 = 4 != 0
```

So answer is `1`.

---

## 4. Minimum is 1

If `mn = 1`, then every value is divisible by `1`.

So answer depends only on how many ones exist:

```text
(cnt + 1) / 2
```

Example:

```text
[1,1,100]
cnt = 2
answer = 1
```

Example:

```text
[1,2,3]
cnt = 1
answer = 1
```

Both are consistent.

---

# Comparison of Approaches

## Approach 1 — Simulation

Good for:

- building intuition

Bad for:

- actual implementation
- huge branching space

---

## Approach 2 — Minimum + divisibility

Good for:

- interview solution
- production solution
- shortest correct implementation

This is the recommended one.

---

## Approach 3 — Frequency viewpoint

Good for:

- deeper reasoning
- proving why only the minimum matters

It leads to the same final code.

---

# Final Recommended Java Solution

```java
class Solution {
    public int minimumArrayLength(int[] nums) {
        int mn = Integer.MAX_VALUE;
        for (int x : nums) {
            mn = Math.min(mn, x);
        }

        int countMin = 0;
        for (int x : nums) {
            if (x == mn) {
                countMin++;
            } else if (x % mn != 0) {
                return 1;
            }
        }

        return (countMin + 1) / 2;
    }
}
```

---

# Dry Run

Take:

```text
nums = [5,5,5,10,5]
```

### Step 1

Find minimum:

```text
mn = 5
```

### Step 2

Count minima and test divisibility:

- `5 == 5` -> count = 1
- `5 == 5` -> count = 2
- `5 == 5` -> count = 3
- `10 % 5 == 0`
- `5 == 5` -> count = 4

No non-multiple exists.

### Step 3

Return:

```text
(4 + 1) / 2 = 2
```

---

# Complexity Summary

## Optimal solution

```text
Time:  O(n)
Space: O(1)
```

This is optimal because we must inspect the array at least once.

---

# Final Takeaway

This problem looks like dynamic simulation, but the real invariant is simple:

- If any element is **not divisible by the minimum**, the answer is `1`
- Otherwise, only the **count of the minimum** matters, and the answer is:

```text
(countMin + 1) / 2
```

That is the whole problem.
