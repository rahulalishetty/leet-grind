# 964. Least Operators to Express Number — Exhaustive Solution Notes

## Overview

We are given:

- a positive integer `x`
- a target integer `target`

We must build an expression using **only the number `x`** and the operators:

```text
+  -  *  /
```

The expression must evaluate exactly to `target`, and we want to minimize the **number of operators used**.

There are several special rules:

- division produces rational values
- no parentheses are allowed
- usual operator precedence applies
- unary negation is not allowed

This makes the problem much more subtle than ordinary arithmetic-expression construction.

The key insight is to stop thinking in terms of arbitrary expressions and instead think in terms of **building the target from powers of `x`**.

This write-up explains the accepted **dynamic programming** approach in detail.

---

## Problem Statement

Given integers `x` and `target`, write an expression consisting of repeated `x`'s joined by `+`, `-`, `*`, `/`, such that the expression evaluates to `target`.

Return the minimum number of operators required.

---

## Example 1

**Input**

```text
x = 3
target = 19
```

**Output**

```text
5
```

**Explanation**

One optimal expression is:

```text
3 * 3 + 3 * 3 + 3 / 3
```

This equals:

```text
9 + 9 + 1 = 19
```

Number of operators:

- `*`
- `+`
- `*`
- `+`
- `/`

Total:

```text
5
```

---

## Example 2

**Input**

```text
x = 5
target = 501
```

**Output**

```text
8
```

**Explanation**

One optimal expression is:

```text
5 * 5 * 5 * 5 - 5 * 5 * 5 + 5 / 5
```

This equals:

```text
625 - 125 + 1 = 501
```

Number of operators = 8.

---

## Example 3

**Input**

```text
x = 100
target = 100000000
```

**Output**

```text
3
```

**Explanation**

```text
100 * 100 * 100 * 100
```

This uses 3 multiplication operators.

---

## Constraints

- `2 <= x <= 100`
- `1 <= target <= 2 * 10^8`

---

# Core Insight: Think in Terms of Blocks

A very important simplification is:

> Any useful multiplication/division block is really just a power of `x`.

For example:

- `x / x = 1`
- `x = x^1`
- `x * x = x^2`
- `x * x * x = x^3`

There is no reason to write something like:

```text
x * x / x
```

because it is just `x`, but uses more operators than necessary.

So the only useful atomic “blocks” are:

```text
x^0, x^1, x^2, x^3, ...
```

where:

- `x^0` is written as `x / x`
- `x^1` is just `x`
- `x^2` is `x * x`
- and so on

The problem becomes:

> Express `target` as a combination of `+x^e` and `-x^e` blocks with minimum total operator cost.

---

# Cost of Writing a Block

We define the **cost** of a block as the number of operators needed to write it, including the `+` or `-` sign in front of it.

This is a very convenient modeling trick.

## Cost of `x^e`

### If `e > 0`

To write `x^e`, we need:

```text
x * x * x * ... * x
```

with `e` copies of `x`, which requires:

```text
e - 1
```

multiplication operators.

Then, when used inside a larger sum/difference, it also has a leading `+` or `-` operator.

So the total cost becomes:

```text
e
```

### If `e = 0`

Then `x^0 = 1`, which is expressed as:

```text
x / x
```

That requires one division operator, plus a leading `+` or `-`, so total cost is:

```text
2
```

Thus:

```text
cost(e) = e      if e > 0
cost(0) = 2
```

---

## Why the Final Answer Subtracts 1

If we model every block as having a leading `+` or `-`, then the very first block in the expression does not actually need a leading operator.

For example:

```text
x * x + x + x / x
```

can be thought of as:

```text
(+ x*x) (+ x) (+ x/x)
```

The modeled cost includes the first `+`, but the real expression does not.

So after computing the total modeled cost, we subtract 1 at the end.

That is why the final answer is:

```text
dp(0, target) - 1
```

---

# Main Number-Theoretic Insight

We process the target digit-by-digit in **base `x`**.

Suppose we are considering exponent `i`, meaning the block value is:

```text
x^i
```

At this stage, the remaining target has already been divided conceptually by `x^i`.

Now let:

```text
r = target mod x
```

This means the current base-`x` digit is `r`.

To make progress, there are two choices:

## Option 1: Use `r` copies of `x^i`

That means we subtract exactly the current digit and move on.

## Option 2: Use `x - r` copies of `x^i` in the opposite direction

That means we overshoot this digit, carry 1 to the next place, and continue.

This is exactly analogous to digit DP with carry/borrow choices.

---

# Example: `x = 5`, `target = 123`

Let us see the intuition.

In base 5:

```text
123 = 4*25 + 4*5 + 3
```

At the lowest place:

```text
123 mod 5 = 3
```

So we can either:

- add/subtract `3` ones
- or use `5 - 3 = 2` ones in the opposite direction and carry upward

Then the problem reduces to a smaller target at the next exponent.

At each power of `x`, we decide whether it is cheaper to:

- match the current digit directly
- or overshoot and let the excess be corrected at the next level

That is the core recursion.

---

# Dynamic Programming State

Define:

```text
dp(i, target)
```

where:

- `i` = current exponent, meaning we are considering blocks of value `x^i`
- `target` = remaining target value, already scaled relative to `x^i`

This function returns the minimum **modeled cost** to express `target * x^i`.

Equivalently, it tells us the minimum cost from exponent `i` onward.

---

# Recurrence

Let:

```text
t = target / x
r = target % x
```

Then we have two options:

## Option 1: Use `r` copies of `x^i`

Cost:

```text
r * cost(i) + dp(i + 1, t)
```

Why?

Because after removing the current digit `r`, the remaining quotient is `t`.

---

## Option 2: Use `x - r` copies and carry upward

Cost:

```text
(x - r) * cost(i) + dp(i + 1, t + 1)
```

Why `t + 1`?

Because overshooting by `x - r` means we effectively round this digit up and carry into the next higher place.

---

## Final Recurrence

So:

```text
dp(i, target) =
min(
    r * cost(i) + dp(i + 1, t),
    (x - r) * cost(i) + dp(i + 1, t + 1)
)
```

This is the heart of the solution.

---

# Base Cases

The recurrence needs stopping conditions.

## Case 1: `target == 0`

If the remaining target is zero, then no more operators are needed.

So:

```text
dp(i, 0) = 0
```

---

## Case 2: `target == 1`

If we just need one copy of the current block value, then the best we can do is write exactly `x^i`.

So:

```text
dp(i, 1) = cost(i)
```

---

## Case 3: Very Large Exponent

The code uses:

```java
if (i >= 39) ans = target + 1;
```

This is a safe cutoff because `x^39` is already enormous compared to the constraints, and at that point recursion no longer needs to go deeper meaningfully.

The value `target + 1` is just a large fallback upper bound.

This is mostly an implementation guard.

---

# Why Memoization Is Necessary

Many `(i, target)` states repeat.

Without memoization, the recursion would branch repeatedly and become exponential.

By caching results for each state, each unique state is solved once.

That reduces complexity to logarithmic in the target size.

---

# Java Implementation

```java
class Solution {
    Map<String, Integer> memo;
    int x;

    public int leastOpsExpressTarget(int x, int target) {
        memo = new HashMap<>();
        this.x = x;
        return dp(0, target) - 1;
    }

    public int dp(int i, int target) {
        String code = i + "#" + target;
        if (memo.containsKey(code))
            return memo.get(code);

        int ans;
        if (target == 0) {
            ans = 0;
        } else if (target == 1) {
            ans = cost(i);
        } else if (i >= 39) {
            ans = target + 1;
        } else {
            int t = target / x;
            int r = target % x;
            ans = Math.min(
                r * cost(i) + dp(i + 1, t),
                (x - r) * cost(i) + dp(i + 1, t + 1)
            );
        }

        memo.put(code, ans);
        return ans;
    }

    public int cost(int x) {
        return x > 0 ? x : 2;
    }
}
```

---

# Walkthrough on Example 1

## Input

```text
x = 3
target = 19
```

We want:

```text
leastOpsExpressTarget(3, 19)
= dp(0, 19) - 1
```

---

## Step 1: `dp(0, 19)`

At exponent `0`, block value is `1 = 3/3`.

```text
19 / 3 = 6
19 % 3 = 1
```

So:

- use 1 copy of `3^0`
- or use `3 - 1 = 2` copies and carry

Option 1:

```text
1 * cost(0) + dp(1, 6)
= 2 + dp(1, 6)
```

Option 2:

```text
2 * cost(0) + dp(1, 7)
= 4 + dp(1, 7)
```

The recursion evaluates both and chooses the minimum.

Eventually the best decomposition corresponds to:

```text
3 * 3 + 3 * 3 + 3 / 3
```

Modeled cost = 6, and after subtracting 1 gives 5.

---

# Why Base-`x` Thinking Is the Right Perspective

This problem is deeply related to expressing `target` in base `x`.

Normally, in base representation, you would simply use the digits directly.

But here, because subtraction is allowed, a digit can be represented in two ways:

- directly using `r`
- or by using `x - r` and carrying up

This is exactly why the recurrence has two branches.

So the DP is essentially:

> a shortest-cost signed base-`x` representation problem

with special operator costs.

---

# Cost Function Intuition Again

This detail is easy to miss, so it is worth restating clearly.

## Cost of `x^0`

To write `1`, we must write:

```text
x / x
```

That costs:

- `/`
- and one joining sign when used in a larger expression

So `cost(0) = 2`.

## Cost of `x^1`

Just:

```text
x
```

and one joining operator when used later

So `cost(1) = 1`.

## Cost of `x^2`

Write:

```text
x * x
```

One multiplication plus one join operator = 2.

And similarly for higher powers.

So:

```text
cost(i) = i for i > 0
cost(0) = 2
```

---

# Complexity Analysis

Let `T = target`.

## Time Complexity

The recursion processes states based on base-`x` digits of `target`.

For each digit position, only a constant number of states are visited.

So total time complexity is:

```text
O(log_x T)
```

which is the same as:

```text
O(log target)
```

up to base change.

---

## Space Complexity

The recursion depth and memo size are both proportional to the number of base-`x` digits.

So space complexity is:

```text
O(log target)
```

---

# Common Mistakes

## 1. Forgetting to subtract 1 at the end

The DP cost includes a leading `+` or `-` for the first block, but real expressions do not need that.

So final answer must be:

```text
dp(0, target) - 1
```

---

## 2. Using the wrong cost for `x^0`

`1` is not free.

It must be written as:

```text
x / x
```

so it costs 2 in the modeled DP.

---

## 3. Thinking only in ordinary base representation

If you only use the exact base-`x` digits, you miss the possibility of overshooting and carrying upward, which can reduce the operator count.

---

## 4. Trying to build explicit expressions

We do not need to construct the actual expression string.

We only need the minimum number of operators.

That is why the DP works on numeric states only.

---

# Final Summary

## Key Idea

Any useful block is a power of `x`:

```text
x^0, x^1, x^2, ...
```

Each digit of the target in base `x` can be handled in two ways:

- use it directly
- overshoot and carry

That leads to a top-down memoized DP.

---

## State

```text
dp(i, target)
```

- `i` = current exponent
- `target` = remaining scaled target

---

## Recurrence

Let:

```text
t = target / x
r = target % x
```

Then:

```text
dp(i, target) =
min(
    r * cost(i) + dp(i + 1, t),
    (x - r) * cost(i) + dp(i + 1, t + 1)
)
```

---

## Cost Function

```text
cost(0) = 2
cost(i) = i for i > 0
```

---

## Final Answer

```text
dp(0, target) - 1
```

---

# Best Final Java Solution

```java
class Solution {
    Map<String, Integer> memo;
    int x;

    public int leastOpsExpressTarget(int x, int target) {
        memo = new HashMap<>();
        this.x = x;
        return dp(0, target) - 1;
    }

    public int dp(int i, int target) {
        String code = i + "#" + target;
        if (memo.containsKey(code))
            return memo.get(code);

        int ans;
        if (target == 0) {
            ans = 0;
        } else if (target == 1) {
            ans = cost(i);
        } else if (i >= 39) {
            ans = target + 1;
        } else {
            int t = target / x;
            int r = target % x;
            ans = Math.min(
                r * cost(i) + dp(i + 1, t),
                (x - r) * cost(i) + dp(i + 1, t + 1)
            );
        }

        memo.put(code, ans);
        return ans;
    }

    public int cost(int x) {
        return x > 0 ? x : 2;
    }
}
```

This is the accepted dynamic programming solution for **Least Operators to Express Number**.
