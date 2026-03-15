# 2137. Pour Water Between Buckets to Make Water Levels Equal — Java Solutions and Detailed Notes

## Problem

We are given:

- an array `buckets`, where `buckets[i]` is the water in the `i`th bucket,
- an integer `loss`, meaning that whenever we pour water, `loss%` of the poured amount is spilled.

We may pour any real-valued amount of water between buckets.

We want to make **all buckets contain the same final amount of water**, and among all valid equalized levels, we want the **maximum possible level**.

We need to return that maximum equal water level.

---

## Key observation

This is not a simulation problem.

The order of pours does **not** matter for the final answer.
What matters is only whether a target equal level `x` is **feasible**.

So the problem becomes:

> For a chosen final level `x`, can we redistribute water so that every bucket ends with exactly `x`?

Once we can answer that, we can binary search on `x`.

---

# Feasibility of a target level

Suppose we want every bucket to end at level:

```text
x
```

There are two kinds of buckets:

## 1. Buckets above `x`

If a bucket has `buckets[i] > x`, it has extra water:

```text
extra = buckets[i] - x
```

This extra water can be poured out to help other buckets.

But because pouring loses water, only a fraction survives.

If loss is `loss%`, then the kept fraction is:

```text
keep = (100 - loss) / 100
```

So from `extra` gallons poured out, only:

```text
extra * keep
```

can actually be received by deficit buckets.

---

## 2. Buckets below `x`

If a bucket has `buckets[i] < x`, it needs:

```text
need = x - buckets[i]
```

This much water must actually arrive in that bucket.

---

## Feasibility condition

Let:

- `donate` = total deliverable water from buckets above `x`
- `need` = total missing water in buckets below `x`

Then `x` is feasible iff:

```text
donate >= need
```

More concretely:

```text
sum((buckets[i] - x) * keep for buckets[i] > x) >= sum((x - buckets[i]) for buckets[i] < x)
```

That is the entire problem.

---

# Why binary search works

If a target level `x` is feasible, then any smaller level is also feasible.

Why?

Because lowering the target:

- decreases the total deficit,
- increases the total surplus.

So feasibility is monotonic.

That means we can binary search the maximum feasible `x`.

---

# Approach 1: Binary Search on the Answer (Optimal)

## Idea

Binary search the final equalized water level `x`.

For each candidate `x`, compute:

- how much water can be supplied after loss,
- how much water is required by lower buckets.

If supply is enough, `x` is feasible.

---

## Search range

The final equalized level must be between:

```text
0
```

and

```text
max(buckets)
```

It can never exceed the largest current bucket.

---

## Precision

The problem accepts answers within:

```text
1e-5
```

So we run binary search on doubles until the interval is sufficiently small, for example:

```text
while (right - left > 1e-6)
```

---

## Java code

```java
class Solution {
    public double equalizeWater(int[] buckets, int loss) {
        double left = 0.0;
        double right = 0.0;

        for (int b : buckets) {
            right = Math.max(right, b);
        }

        double keep = (100.0 - loss) / 100.0;

        while (right - left > 1e-6) {
            double mid = left + (right - left) / 2.0;

            if (canEqualize(buckets, mid, keep)) {
                left = mid;   // try a higher level
            } else {
                right = mid;  // mid is too high
            }
        }

        return left;
    }

    private boolean canEqualize(int[] buckets, double target, double keep) {
        double donate = 0.0;
        double need = 0.0;

        for (int water : buckets) {
            if (water > target) {
                donate += (water - target) * keep;
            } else {
                need += (target - water);
            }
        }

        return donate >= need;
    }
}
```

---

## Dry run

### Example

```text
buckets = [1,2,7], loss = 80
```

So:

```text
keep = 0.2
```

Try:

```text
x = 2
```

Buckets below 2:

- bucket 1 needs `1`

Buckets above 2:

- bucket 7 has extra `5`
- only `5 * 0.2 = 1` survives

So:

```text
donate = 1
need = 1
```

Feasible.

Try:

```text
x = 2.1
```

Need:

- bucket 1 needs `1.1`
- bucket 2 needs `0.1`
- total need = `1.2`

Donate:

- bucket 7 extra = `4.9`
- deliverable = `4.9 * 0.2 = 0.98`

Not feasible.

So answer is exactly `2`.

---

## Complexity

Let `n = buckets.length`.

Time complexity:

```text
O(n * log(precision_range))
```

More concretely, binary search runs a constant number of iterations for fixed precision, so this is effectively:

```text
O(n)
```

per iteration times about 50–60 iterations.

Space complexity:

```text
O(1)
```

---

## Verdict

This is the standard, optimal, and intended solution.

---

# Approach 2: Brute Force Sampling of Water Levels (Educational, Not Practical)

## Idea

We could try many candidate levels `x` between `0` and `max(buckets)` and test each one using the same feasibility formula.

This is conceptually simple but not suitable for high precision.

---

## Why it is weak

If we sample in increments of:

```text
delta = 1e-5
```

and the max bucket value is up to `1e5`, then the number of sampled points is enormous.

So this is only an educational stepping stone to understand why binary search is necessary.

---

## Educational Java sketch

```java
class SolutionBruteForce {
    public double equalizeWater(int[] buckets, int loss) {
        double keep = (100.0 - loss) / 100.0;
        double maxWater = 0.0;

        for (int b : buckets) {
            maxWater = Math.max(maxWater, b);
        }

        double answer = 0.0;
        double step = 0.001; // only for illustration, not enough for final constraints

        for (double target = 0.0; target <= maxWater; target += step) {
            if (canEqualize(buckets, target, keep)) {
                answer = target;
            }
        }

        return answer;
    }

    private boolean canEqualize(int[] buckets, double target, double keep) {
        double donate = 0.0;
        double need = 0.0;

        for (int water : buckets) {
            if (water > target) {
                donate += (water - target) * keep;
            } else {
                need += (target - water);
            }
        }

        return donate >= need;
    }
}
```

---

## Complexity

If we sample `M` candidate levels:

```text
Time = O(n * M)
Space = O(1)
```

This is impractical for required precision.

---

## Verdict

Not suitable for the real problem.
Useful only as a conceptual bridge.

---

# Approach 3: Binary Search with Algebraic Feasibility Interpretation

This is really the same optimal algorithm as Approach 1, but it is worth framing it from a more mathematical point of view.

## Reformulation

For target level `x`, each bucket contributes a signed balance:

- if `buckets[i] > x`, contribution is:

```text
+(buckets[i] - x) * keep
```

- if `buckets[i] < x`, contribution is:

```text
-(x - buckets[i])
```

Then define total balance:

```text
balance(x) = sum of all contributions
```

If:

```text
balance(x) >= 0
```

then `x` is feasible.

If:

```text
balance(x) < 0
```

then `x` is infeasible.

Since `balance(x)` decreases as `x` increases, we again get a monotonic predicate and binary search works.

---

## Java code

```java
class Solution {
    public double equalizeWater(int[] buckets, int loss) {
        double left = 0.0;
        double right = 0.0;

        for (int b : buckets) {
            right = Math.max(right, b);
        }

        double keep = (100.0 - loss) / 100.0;

        for (int iter = 0; iter < 60; iter++) {
            double mid = (left + right) / 2.0;

            if (balance(buckets, mid, keep) >= 0.0) {
                left = mid;
            } else {
                right = mid;
            }
        }

        return left;
    }

    private double balance(int[] buckets, double target, double keep) {
        double bal = 0.0;

        for (int water : buckets) {
            if (water > target) {
                bal += (water - target) * keep;
            } else {
                bal -= (target - water);
            }
        }

        return bal;
    }
}
```

---

## Notes on this version

Instead of using:

```text
while (right - left > eps)
```

this version runs a fixed number of iterations such as `60`.

That is also perfectly valid for floating-point binary search because each iteration halves the interval.

---

## Complexity

Time complexity:

```text
O(n * 60) = O(n)
```

Space complexity:

```text
O(1)
```

---

# Why the feasibility formula is correct

Suppose final equalized level is `x`.

## Deficit buckets

If bucket `i` has `buckets[i] < x`, then to reach level `x`, it must receive exactly:

```text
x - buckets[i]
```

actual gallons.

So total deficit is:

```text
need = sum(x - buckets[i]) over all buckets below x
```

## Surplus buckets

If bucket `i` has `buckets[i] > x`, then it may give away at most:

```text
buckets[i] - x
```

gallons, because it still needs to keep `x` for itself.

But only a fraction survives spilling, so the amount that can actually arrive elsewhere is:

```text
(buckets[i] - x) * keep
```

Thus total useful supply is:

```text
donate = sum((buckets[i] - x) * keep) over all buckets above x
```

A redistribution is possible iff useful supply covers deficit:

```text
donate >= need
```

That proves the condition.

---

# Important implementation details

## 1. Use double, not float

The accepted precision is tight enough that `double` should always be used.

## 2. Use `100.0`, not `100`

This ensures floating-point division:

```java
double keep = (100.0 - loss) / 100.0;
```

## 3. Return `left`

In maximum-feasible binary search:

- `left` tracks feasible values,
- `right` tracks the upper boundary.

At termination, `left` is the best feasible estimate.

## 4. Termination choice

Two common styles:

### Epsilon-based

```java
while (right - left > 1e-6)
```

### Fixed-iteration

```java
for (int iter = 0; iter < 60; iter++)
```

Both are fine.

---

# Comparison of approaches

## Approach 1: Binary Search on Answer

### Pros

- optimal
- short
- clean
- standard for this type of continuous feasibility problem

### Cons

- requires recognizing monotonic feasibility

### Complexity

```text
Time:  O(n * log precision)
Space: O(1)
```

---

## Approach 2: Brute Force Sampling

### Pros

- easiest to imagine conceptually

### Cons

- far too slow for real precision
- not practical

### Complexity

```text
Time:  O(n * M)
Space: O(1)
```

---

## Approach 3: Binary Search with Balance Function

### Pros

- same optimal performance
- slightly more mathematical formulation
- elegant single-balance view

### Cons

- same reasoning burden as Approach 1

### Complexity

```text
Time:  O(n * log precision)
Space: O(1)
```

---

# Final recommended Java solution

```java
class Solution {
    public double equalizeWater(int[] buckets, int loss) {
        double left = 0.0;
        double right = 0.0;

        for (int b : buckets) {
            right = Math.max(right, b);
        }

        double keep = (100.0 - loss) / 100.0;

        while (right - left > 1e-6) {
            double mid = left + (right - left) / 2.0;

            double donate = 0.0;
            double need = 0.0;

            for (int water : buckets) {
                if (water > mid) {
                    donate += (water - mid) * keep;
                } else {
                    need += (mid - water);
                }
            }

            if (donate >= need) {
                left = mid;
            } else {
                right = mid;
            }
        }

        return left;
    }
}
```

---

# Worked examples

## Example 1

```text
buckets = [1,2,7], loss = 80
keep = 0.2
```

Try `x = 2`:

- need from bucket 1 = `1`
- donate from bucket 7 = `(7 - 2) * 0.2 = 1`

Feasible.

Try anything slightly above `2` and deficit outgrows supply.

Answer:

```text
2.0
```

---

## Example 2

```text
buckets = [2,4,6], loss = 50
keep = 0.5
```

Try `x = 3.5`:

- need from bucket 2 = `1.5`
- need from bucket 4 = `0`
- donate from bucket 6 = `(6 - 3.5) * 0.5 = 1.25`
- donate from bucket 4 = `(4 - 3.5) * 0.5 = 0.25`

Total donate = `1.5`, exactly enough.

So `3.5` is feasible.

Higher values fail.

Answer:

```text
3.5
```

---

## Example 3

```text
buckets = [3,3,3,3], loss = 40
```

Already equal. The best answer is clearly:

```text
3.0
```

The binary search also converges to that value.

---

# Pattern takeaway

This problem is a classic:

```text
maximize a real-valued answer
subject to a monotonic feasibility condition
```

That is a very strong signal for:

```text
Binary Search on Answer
```

Whenever you see:

- a continuous numeric answer,
- “maximum feasible” or “minimum feasible”,
- and a feasibility test that becomes easier/harder monotonically,

you should strongly consider binary searching the answer.
