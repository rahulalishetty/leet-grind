# 3155. Maximum Number of Upgradable Servers — Java Solutions and Detailed Notes

## Problem

For each data center `i`, we are given:

- `count[i]` = number of servers
- `upgrade[i]` = cost to upgrade one server
- `sell[i]` = money gained by selling one server
- `money[i]` = initial money available

For that data center, we may:

- sell some servers,
- use the total available money to upgrade some of the remaining servers.

If we upgrade `x` servers, then we must keep exactly `x` servers and may sell the other:

```text
count[i] - x
```

servers.

We need to compute, independently for each data center, the **maximum number of servers that can be upgraded**.

Return an array `answer` where:

```text
answer[i] = maximum number of upgradable servers in data center i
```

---

# Core observation

Suppose for one data center we want to upgrade exactly `x` servers.

Then:

- we must keep `x` servers,
- we may sell the remaining `count - x` servers,
- total money available becomes:

```text
money + (count - x) * sell
```

- total cost to upgrade `x` servers is:

```text
x * upgrade
```

So `x` is feasible iff:

```text
x * upgrade <= money + (count - x) * sell
```

Rearrange:

```text
x * upgrade + x * sell <= money + count * sell
x * (upgrade + sell) <= money + count * sell
```

So the condition becomes:

```text
x <= (money + count * sell) / (upgrade + sell)
```

This gives both:

- a clean mathematical formula,
- and a monotonic feasibility condition suitable for binary search.

---

# Approach 1: Brute Force per data center

## Idea

For each data center, try every possible number of upgraded servers:

```text
x = 0, 1, 2, ..., count[i]
```

and check whether it is feasible.

This is the most direct approach.

---

## Feasibility check

For a candidate `x`:

```text
cost = x * upgrade[i]
available = money[i] + (count[i] - x) * sell[i]
```

If:

```text
cost <= available
```

then `x` is feasible.

Take the maximum feasible `x`.

---

## Java code

```java
class Solution {
    public int[] maxUpgrades(int[] count, int[] upgrade, int[] sell, int[] money) {
        int n = count.length;
        int[] answer = new int[n];

        for (int i = 0; i < n; i++) {
            int best = 0;

            for (int x = 0; x <= count[i]; x++) {
                long cost = 1L * x * upgrade[i];
                long available = money[i] + 1L * (count[i] - x) * sell[i];

                if (cost <= available) {
                    best = x;
                }
            }

            answer[i] = best;
        }

        return answer;
    }
}
```

---

## Complexity

Let `n` be the number of data centers.

Time complexity:

```text
O(sum(count[i]))
```

In the worst case, since `count[i] <= 10^5` and `n <= 10^5`, this is too slow.

Space complexity:

```text
O(1)
```

extra space apart from output.

---

## Verdict

Correct, but not suitable for large inputs.

---

# Approach 2: Binary Search per data center

## Idea

For one data center, define a predicate:

> Can we upgrade `x` servers?

We already know the condition:

```text
x * upgrade <= money + (count - x) * sell
```

If `x` is feasible, then every smaller value is also feasible.

So feasibility is monotonic, and binary search works.

---

## Why monotonicity holds

Suppose `x` servers can be upgraded.

Then for any smaller `y < x`:

- upgrade cost decreases,
- number of sold servers increases,
- so available money does not decrease.

Thus smaller values remain feasible.

That makes binary search valid.

---

## Java code

```java
class Solution {
    public int[] maxUpgrades(int[] count, int[] upgrade, int[] sell, int[] money) {
        int n = count.length;
        int[] answer = new int[n];

        for (int i = 0; i < n; i++) {
            int left = 0, right = count[i];

            while (left < right) {
                int mid = left + (right - left + 1) / 2;

                if (canUpgrade(mid, count[i], upgrade[i], sell[i], money[i])) {
                    left = mid;
                } else {
                    right = mid - 1;
                }
            }

            answer[i] = left;
        }

        return answer;
    }

    private boolean canUpgrade(int x, int count, int upgrade, int sell, int money) {
        long cost = 1L * x * upgrade;
        long available = money + 1L * (count - x) * sell;
        return cost <= available;
    }
}
```

---

## Complexity

Each data center takes:

```text
O(log count[i]))
```

So total time is:

```text
O(n log C)
```

where `C = max(count[i])`.

Space complexity:

```text
O(1)
```

extra space apart from output.

---

## Verdict

This is efficient enough and easy to justify.

---

# Approach 3: Direct Math Formula (Best)

## Idea

From the feasibility inequality:

```text
x * upgrade <= money + (count - x) * sell
```

we derived:

```text
x * (upgrade + sell) <= money + count * sell
```

Therefore the largest feasible integer `x` is simply:

```text
floor((money + count * sell) / (upgrade + sell))
```

But we also cannot upgrade more than `count`, so:

```text
answer = min(count, floor((money + count * sell) / (upgrade + sell)))
```

That gives an O(1) solution per data center.

---

## Why this formula is correct

We want the maximum integer `x` satisfying:

```text
x * (upgrade + sell) <= money + count * sell
```

Dividing both sides by `(upgrade + sell)`:

```text
x <= (money + count * sell) / (upgrade + sell)
```

So the maximum integer solution is:

```text
floor((money + count * sell) / (upgrade + sell))
```

Since `x` cannot exceed `count`, we clamp it:

```text
min(count, ...)
```

---

## Java code

```java
class Solution {
    public int[] maxUpgrades(int[] count, int[] upgrade, int[] sell, int[] money) {
        int n = count.length;
        int[] answer = new int[n];

        for (int i = 0; i < n; i++) {
            long numerator = money[i] + 1L * count[i] * sell[i];
            long denominator = upgrade[i] + 1L * sell[i];

            long maxUpgradeable = numerator / denominator;
            answer[i] = (int) Math.min(count[i], maxUpgradeable);
        }

        return answer;
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

extra space apart from output.

This is optimal.

---

# Worked examples

## Example 1

```text
count   = [4,3]
upgrade = [3,5]
sell    = [4,2]
money   = [8,9]
```

### Data center 1

```text
count = 4, upgrade = 3, sell = 4, money = 8
```

Formula:

```text
x <= (8 + 4*4) / (3 + 4)
x <= 24 / 7
x <= 3
```

So answer is `3`.

Check manually:

- sell 1 server → gain 4
- total money = 12
- upgrade 3 servers costs 9

Feasible.

---

### Data center 2

```text
count = 3, upgrade = 5, sell = 2, money = 9
```

Formula:

```text
x <= (9 + 3*2) / (5 + 2)
x <= 15 / 7
x <= 2
```

So answer is `2`.

Correct.

---

## Example 2

```text
count = [1]
upgrade = [2]
sell = [1]
money = [1]
```

Formula:

```text
x <= (1 + 1*1) / (2 + 1)
x <= 2 / 3
x <= 0
```

So answer is `0`.

Correct.

---

# Comparison of approaches

## Approach 1: Brute Force

### Pros

- most direct
- easiest to derive

### Cons

- too slow

### Complexity

```text
Time:  O(sum(count[i]))
Space: O(1)
```

---

## Approach 2: Binary Search

### Pros

- efficient
- uses monotonicity cleanly
- easier to reason about than the formula at first glance

### Cons

- still more work than necessary

### Complexity

```text
Time:  O(n log C)
Space: O(1)
```

---

## Approach 3: Direct Formula

### Pros

- fastest
- simplest final implementation
- mathematically exact

### Cons

- requires algebraic simplification insight

### Complexity

```text
Time:  O(n)
Space: O(1)
```

---

# Final recommended Java solution

```java
class Solution {
    public int[] maxUpgrades(int[] count, int[] upgrade, int[] sell, int[] money) {
        int n = count.length;
        int[] answer = new int[n];

        for (int i = 0; i < n; i++) {
            long numerator = money[i] + 1L * count[i] * sell[i];
            long denominator = upgrade[i] + 1L * sell[i];
            answer[i] = (int) Math.min(count[i], numerator / denominator);
        }

        return answer;
    }
}
```

---

# Overflow note

Even though each input value is at most `10^5`, products like:

```text
count[i] * sell[i]
```

can reach:

```text
10^10
```

which does not fit in `int`.

So you must use `long` for intermediate arithmetic:

```java
1L * count[i] * sell[i]
```

---

# Pattern takeaway

This problem looks like it might need greedy or binary search, but after writing the feasibility condition carefully, it collapses into a simple inequality.

That is a common pattern:

1. define the candidate answer `x`
2. write the exact feasibility condition
3. simplify algebraically
4. sometimes the binary search disappears completely and a direct formula remains

This problem is a nice example of that simplification.
