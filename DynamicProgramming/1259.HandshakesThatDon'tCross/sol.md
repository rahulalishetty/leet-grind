# Handshakes That Don't Cross — Detailed Summary

## Problem

You are given an even number of people, `numPeople`, standing around a circle.

Each person shakes hands with exactly one other person, so there are:

```text
numPeople / 2
```

total handshakes.

We need to count the number of ways to form these handshakes such that:

- every person is matched exactly once
- no two handshakes cross
- answer is returned modulo:

```text
1_000_000_007
```

---

## Core insight

This is a classic **non-crossing perfect matching on a circle** problem.

The answer is a **Catalan number**.

If:

```text
numPeople = 2n
```

then the answer is:

```text
Catalan(n)
```

---

# Why Catalan numbers appear

Imagine the people are numbered clockwise:

```text
0, 1, 2, ..., 2n-1
```

Now fix person `0`.

Suppose person `0` shakes hands with person `j`.

For the matching to be non-crossing, `j` must split the circle into two independent groups:

- the people between `0` and `j`
- the people outside that interval

Each side must also form a valid non-crossing matching internally.

This creates the standard Catalan recurrence.

---

# Why the partner of person 0 must create even-sized subproblems

If person `0` is matched with person `j`, then the number of people strictly between them must be even.

Why?

Because those people must be perfectly matched among themselves.

So `j` must be an odd offset away from `0`:

```text
j = 2k + 1
```

for some `k`.

This ensures the inside region contains `2k` people, and the outside region contains:

```text
2(n - 1 - k)
```

people.

Both are even, so both can be perfectly matched.

---

# DP definition

Let:

```text
dp[i] = number of valid non-crossing handshake matchings among 2i people
```

So:

- `dp[0]` means 0 people
- `dp[1]` means 2 people
- `dp[2]` means 4 people
- and so on

The final answer is:

```text
dp[numPeople / 2]
```

---

# Base case

```text
dp[0] = 1
```

Why?

There is exactly one way to match zero people: do nothing.

This empty configuration is important because it makes the recurrence work cleanly.

---

# Recurrence

Suppose we want `dp[i]`, meaning `2i` people.

Fix person `0`.

Let person `0` match with the person that leaves:

- `2k` people on one side
- `2(i - 1 - k)` people on the other side

Then:

- left side contributes `dp[k]`
- right side contributes `dp[i - 1 - k]`

Since the two sides are independent, the total for that split is:

```text
dp[k] * dp[i - 1 - k]
```

Now sum over all valid `k`:

```text
dp[i] = sum(dp[k] * dp[i - 1 - k]) for k = 0 to i - 1
```

This is exactly the Catalan recurrence.

---

# Recurrence written formally

```text
dp[0] = 1

dp[i] = Σ dp[k] * dp[i - 1 - k]   for k from 0 to i-1
```

---

# Small examples

## 2 people

Only one handshake is possible:

```text
(0, 1)
```

So:

```text
dp[1] = 1
```

---

## 4 people

People:

```text
0, 1, 2, 3
```

Valid non-crossing matchings:

1. `(0,1)` and `(2,3)`
2. `(0,3)` and `(1,2)`

So:

```text
dp[2] = 2
```

Note that `(0,2)` and `(1,3)` crosses, so it is invalid.

---

## 6 people

There are 5 valid non-crossing matchings.

So:

```text
dp[3] = 5
```

This matches the Catalan sequence:

```text
1, 1, 2, 5, 14, 42, ...
```

---

# Visual intuition for the recurrence

Suppose there are `2i` people.

Fix person `0`.

If person `0` shakes hands with:

- the nearest valid person, one side has 0 people, the other has `2(i-1)`
- a farther valid person, the inside and outside are split differently
- the farthest valid person, the split reverses

Each choice of partner gives one decomposition into:

```text
left subproblem × right subproblem
```

The total is the sum of all such products.

That “split around one fixed pair” structure is the signature of Catalan problems.

---

# Java solution

```java
class Solution {
    public int numberOfWays(int numPeople) {
        int MOD = 1_000_000_007;
        int n = numPeople / 2;

        long[] dp = new long[n + 1];
        dp[0] = 1;

        for (int i = 1; i <= n; i++) {
            for (int k = 0; k < i; k++) {
                dp[i] = (dp[i] + dp[k] * dp[i - 1 - k]) % MOD;
            }
        }

        return (int) dp[n];
    }
}
```

---

# Step-by-step dry run

Let:

```text
numPeople = 6
```

Then:

```text
n = 3
```

We compute `dp[0..3]`.

## Initialization

```text
dp[0] = 1
```

---

## Compute `dp[1]`

```text
dp[1] = dp[0] * dp[0] = 1
```

So:

```text
dp[1] = 1
```

---

## Compute `dp[2]`

```text
dp[2] = dp[0] * dp[1] + dp[1] * dp[0]
      = 1 * 1 + 1 * 1
      = 2
```

So:

```text
dp[2] = 2
```

---

## Compute `dp[3]`

```text
dp[3] = dp[0] * dp[2] + dp[1] * dp[1] + dp[2] * dp[0]
      = 1 * 2 + 1 * 1 + 2 * 1
      = 5
```

So:

```text
dp[3] = 5
```

Final answer:

```text
5
```

---

# Why multiplication appears

When person `0` pairs with some partner, the circle splits into two non-overlapping regions.

A valid matching on the left can be combined with any valid matching on the right.

So the number of combined matchings is:

```text
leftWays * rightWays
```

This is why multiplication appears in the recurrence.

Then we add across all partner choices, which is why the outer operation is summation.

---

# Why crossing never happens across the split

Once we fix the handshake involving person `0`, any handshake connecting one side of that chord to the other side would necessarily cross it.

Since crossings are forbidden, all remaining handshakes must stay entirely within one of the two subregions.

That is exactly why the problem decomposes into two independent subproblems.

This is the most important structural observation.

---

# Complexity analysis

Let:

```text
n = numPeople / 2
```

## Time complexity

We compute:

- `dp[1]`
- `dp[2]`
- ...
- `dp[n]`

For each `dp[i]`, we sum over `k = 0..i-1`.

So total work is:

```text
1 + 2 + 3 + ... + n = O(n^2)
```

Thus:

```text
Time = O(n^2)
```

---

## Space complexity

We only store the DP array:

```text
dp[0..n]
```

So:

```text
Space = O(n)
```

---

# Relation to Catalan numbers

The Catalan numbers count many structures, including:

- non-crossing handshakes
- balanced parentheses
- binary search trees
- triangulations of a polygon
- non-crossing chord matchings

This problem is one of the standard Catalan interpretations.

The sequence begins:

```text
C0 = 1
C1 = 1
C2 = 2
C3 = 5
C4 = 14
C5 = 42
```

So if `numPeople = 2n`, the answer is `Cn`.

---

# Alternative mathematical formula

The nth Catalan number is also:

```text
C_n = (1 / (n + 1)) * binomial(2n, n)
```

But under modulo arithmetic, especially with large constraints, directly using the recurrence is often simpler and safer unless modular inverses are intentionally used.

So the DP recurrence is usually the cleanest interview and implementation approach.

---

# Common mistakes

## 1. Treating this like arbitrary perfect matching

Without the non-crossing condition, the count would be completely different.

The non-crossing restriction is what makes this Catalan.

---

## 2. Forgetting the empty subproblem

`dp[0] = 1` is essential.

Without it, the recurrence breaks on small cases.

---

## 3. Using `numPeople` directly as DP size

The DP is based on **pairs**, not people.

So use:

```java
int n = numPeople / 2;
```

---

## 4. Integer overflow

The multiplication:

```java
dp[k] * dp[i - 1 - k]
```

should use `long` before taking modulo.

That is why the Java code uses:

```java
long[] dp
```

---

# Compact recurrence summary

If `numPeople = 2n`, then:

```text
dp[0] = 1
dp[i] = Σ dp[k] * dp[i - 1 - k]    for k = 0..i-1
answer = dp[n]
```

---

# Final takeaway

The key idea is:

- fix one person
- choose their handshake partner
- that split creates two independent smaller non-crossing handshake problems

This gives the Catalan recurrence immediately.

So the problem reduces to computing:

```text
Catalan(numPeople / 2)
```

with dynamic programming.

## Final complexities

```text
Time:  O(n^2)
Space: O(n)
```

where:

```text
n = numPeople / 2
```
