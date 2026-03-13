# 2513. Minimize the Maximum of Two Arrays — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int minimizeSet(int divisor1, int divisor2, int uniqueCnt1, int uniqueCnt2) {

    }
}
```

---

# Problem Restatement

We need to build two arrays:

- `arr1` with `uniqueCnt1` distinct positive integers, each **not divisible by** `divisor1`
- `arr2` with `uniqueCnt2` distinct positive integers, each **not divisible by** `divisor2`

Also:

- no integer may appear in both arrays

We want to minimize the maximum value used in either array.

---

# Core Insight

Suppose we guess the answer is some number `x`.

Then we are only allowed to use numbers from:

```text
1, 2, 3, ..., x
```

The question becomes:

> Can we pick enough valid numbers from `1..x` to fill both arrays without overlap?

This suggests **binary search** on the answer.

So the real challenge is how to test whether a candidate `x` is feasible.

---

# Classifying Numbers up to x

For a fixed `x`, every number in `1..x` falls into one of these groups:

1. **usable only for arr1**
   - divisible by `divisor2`
   - not divisible by `divisor1`

2. **usable only for arr2**
   - divisible by `divisor1`
   - not divisible by `divisor2`

3. **usable for both**
   - not divisible by either divisor

4. **usable for neither**
   - divisible by both

But instead of explicitly splitting into 4 buckets, we only need three counts:

### Numbers available for arr1

These are numbers **not divisible by divisor1**:

```text
count1 = x - floor(x / divisor1)
```

### Numbers available for arr2

These are numbers **not divisible by divisor2**:

```text
count2 = x - floor(x / divisor2)
```

### Numbers available for at least one of the arrays

A number is unusable for both arrays only if it is divisible by both divisors, i.e. divisible by:

```text
lcm(divisor1, divisor2)
```

So:

```text
countBoth = x - floor(x / lcm(divisor1, divisor2))
```

For `x` to be feasible, we need:

```text
count1 >= uniqueCnt1
count2 >= uniqueCnt2
countBoth >= uniqueCnt1 + uniqueCnt2
```

That last condition is crucial because the arrays must be disjoint.

---

# Why These Conditions Are Sufficient

The first two conditions ensure each array has enough individually valid numbers.

The third condition ensures that overall there are enough distinct usable numbers to assign to both arrays with no overlap.

Together, they are both necessary and sufficient.

That gives a monotone feasibility test, so binary search works perfectly.

---

# Approach 1 — Binary Search on Answer with Counting Formula (Recommended)

## Idea

Binary search the smallest `x` such that:

```text
x - x/divisor1 >= uniqueCnt1
x - x/divisor2 >= uniqueCnt2
x - x/lcm(divisor1, divisor2) >= uniqueCnt1 + uniqueCnt2
```

Because if some `x` works, then any larger value also works.

---

## Java Code

```java
class Solution {
    public int minimizeSet(int divisor1, int divisor2, int uniqueCnt1, int uniqueCnt2) {
        long left = 1;
        long right = (long) 2e18;

        long lcm = lcm(divisor1, divisor2);

        while (left < right) {
            long mid = left + (right - left) / 2;

            long count1 = mid - mid / divisor1;
            long count2 = mid - mid / divisor2;
            long countBoth = mid - mid / lcm;

            if (count1 >= uniqueCnt1 &&
                count2 >= uniqueCnt2 &&
                countBoth >= (long) uniqueCnt1 + uniqueCnt2) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return (int) left;
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    private long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }
}
```

---

## Complexity

Binary search runs for about `log(2e18)` iterations, which is tiny.

Each check is `O(1)`.

So:

```text
Time:  O(log answer)
Space: O(1)
```

This is the intended optimal solution.

---

# Approach 2 — Binary Search with Explicit Inclusion-Exclusion Interpretation

## Idea

This is mathematically the same as Approach 1, but it is useful to think of it more explicitly.

For a candidate `x`:

- numbers valid for `arr1` = not divisible by `divisor1`
- numbers valid for `arr2` = not divisible by `divisor2`
- numbers valid for at least one array = total numbers minus numbers divisible by both divisors

By inclusion-exclusion:

```text
divisible by both = floor(x / lcm(divisor1, divisor2))
```

So the same three inequalities appear naturally.

This is the same algorithm, just explained from the inclusion-exclusion angle.

---

## Java Code

```java
class Solution {
    public int minimizeSet(int divisor1, int divisor2, int uniqueCnt1, int uniqueCnt2) {
        long lo = 1, hi = (long) 2e18;
        long l = lcm(divisor1, divisor2);

        while (lo < hi) {
            long mid = lo + (hi - lo) / 2;

            long usableForArr1 = mid - mid / divisor1;
            long usableForArr2 = mid - mid / divisor2;
            long usableOverall = mid - mid / l;

            boolean ok = usableForArr1 >= uniqueCnt1
                      && usableForArr2 >= uniqueCnt2
                      && usableOverall >= (long) uniqueCnt1 + uniqueCnt2;

            if (ok) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }

        return (int) lo;
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    private long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }
}
```

---

## Complexity

Same as Approach 1:

```text
Time:  O(log answer)
Space: O(1)
```

---

# Approach 3 — Greedy Construction / Simulation (Too Slow)

## Idea

One might try to construct the arrays explicitly:

- walk from `1` upward
- place numbers into `arr1` or `arr2` greedily
- stop when both are filled

This works for very small inputs, but it is far too slow for:

```text
uniqueCnt1 + uniqueCnt2 up to 1e9
```

So explicit construction is infeasible.

---

## Why it fails

The answer itself may be huge, and scanning one number at a time would be much too slow.

The problem is designed for counting, not construction.

---

# Detailed Walkthrough

## Example 1

```text
divisor1 = 2
divisor2 = 7
uniqueCnt1 = 1
uniqueCnt2 = 3
```

Try `x = 4`.

### Valid for arr1

Numbers in `1..4` not divisible by `2`:

```text
1, 3
```

Count:

```text
4 - floor(4/2) = 2
```

Enough for `uniqueCnt1 = 1`.

### Valid for arr2

Numbers in `1..4` not divisible by `7`:

```text
1, 2, 3, 4
```

Count:

```text
4 - floor(4/7) = 4
```

Enough for `uniqueCnt2 = 3`.

### Valid overall

LCM of `2` and `7` is `14`.

Numbers in `1..4` not divisible by `14`:

```text
all 4 numbers
```

Count:

```text
4 - floor(4/14) = 4
```

Need total:

```text
1 + 3 = 4
```

So `x = 4` works.

Smaller values do not, so answer is:

```text
4
```

---

## Example 2

```text
divisor1 = 3
divisor2 = 5
uniqueCnt1 = 2
uniqueCnt2 = 1
```

Try `x = 3`.

- valid for arr1:
  ```text
  3 - floor(3/3) = 2
  ```
- valid for arr2:
  ```text
  3 - floor(3/5) = 3
  ```
- lcm(3,5)=15, so valid overall:
  ```text
  3 - floor(3/15) = 3
  ```

Need total `2 + 1 = 3`, so `x = 3` works.

Answer:

```text
3
```

---

## Example 3

```text
divisor1 = 2
divisor2 = 4
uniqueCnt1 = 8
uniqueCnt2 = 2
```

Try `x = 15`.

- valid for arr1:
  ```text
  15 - floor(15/2) = 8
  ```
- valid for arr2:
  ```text
  15 - floor(15/4) = 12
  ```
- lcm(2,4)=4, valid overall:
  ```text
  15 - floor(15/4) = 12
  ```

Need total `8 + 2 = 10`, so feasible.

Smaller values fail, so answer is:

```text
15
```

---

# Important Correctness Argument

Suppose `x` is feasible.

Then there exist enough distinct numbers in `1..x` to fill both arrays.

Now consider any larger `y > x`.

Since `1..x` is contained in `1..y`, `y` is also feasible.

So feasibility is monotone, which justifies binary search.

Next, the conditions:

```text
count1 >= uniqueCnt1
count2 >= uniqueCnt2
countBoth >= uniqueCnt1 + uniqueCnt2
```

are necessary because:

- `arr1` needs that many individually valid numbers
- `arr2` needs that many individually valid numbers
- together, they need enough distinct usable numbers overall

They are also sufficient because all numbers counted in `countBoth` are valid for at least one array, and the separate lower bounds guarantee that both arrays can be filled without overlap.

Thus the check is exact.

---

# Common Pitfalls

## 1. Forgetting the no-overlap condition

It is not enough to check `count1` and `count2` separately.
You must also ensure enough distinct usable numbers overall.

---

## 2. Using `int` for LCM

Use `long`, because:

```text
lcm(a, b) = a / gcd(a,b) * b
```

can overflow `int`.

---

## 3. Using too small a binary-search upper bound

The answer can be much larger than `1e9`.
Using a very safe `long` upper bound is better.

---

## 4. Confusing “not divisible by divisor1” with “not divisible by both”

The overall pool is numbers that are valid for at least one array, not necessarily both.

---

# Best Approach

## Recommended: Binary search + counting with inclusion-exclusion

This is the cleanest and fastest solution because:

- the answer is monotone
- feasibility is easy to count mathematically
- no explicit construction is needed

---

# Final Recommended Java Solution

```java
class Solution {
    public int minimizeSet(int divisor1, int divisor2, int uniqueCnt1, int uniqueCnt2) {
        long left = 1;
        long right = (long) 2e18;
        long lcm = lcm(divisor1, divisor2);

        while (left < right) {
            long mid = left + (right - left) / 2;

            long count1 = mid - mid / divisor1;
            long count2 = mid - mid / divisor2;
            long countBoth = mid - mid / lcm;

            if (count1 >= uniqueCnt1 &&
                count2 >= uniqueCnt2 &&
                countBoth >= (long) uniqueCnt1 + uniqueCnt2) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return (int) left;
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    private long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }
}
```

---

# Complexity Summary

```text
Time:  O(log answer)
Space: O(1)
```

---

# Final Takeaway

The problem looks like a construction problem, but it is really a counting problem.

Once you ask:

> For a guessed maximum `x`, are there enough valid numbers up to `x`?

the solution becomes a clean binary search with three counting formulas.
