# Handshakes That Don't Cross — Detailed Notes

This document converts the provided explanation into a detailed Markdown note.

---

# Problem Idea

We have an even number of people sitting around a circle.

Every person must shake hands with exactly one other person.

The constraint is:

> No two handshakes may cross.

We want to count how many valid non-crossing handshake pairings are possible.

Because the number can be very large, the answer is taken modulo:

```text
10^9 + 7
```

---

# Core Combinatorial Insight

Suppose there are:

```text
2 * i
```

people in the circle.

Pick person `1`.

That person must shake hands with some other person `k`.

Once that handshake is fixed, the circle is split into two independent regions:

- people on the "left" side of the handshake
- people on the "right" side of the handshake

A person from the left side cannot shake hands with a person from the right side, because that would force a crossing.

So:

- left people can only pair among themselves
- right people can only pair among themselves

This creates two smaller subproblems.

That is exactly why dynamic programming works here.

---

# Why the Two Sides Must Have Even Size

After person `1` shakes hands with person `k`, everyone else must still be fully paired.

That means both sides of the split must contain an even number of people.

If one side had an odd number of people, then at least one person in that side would remain unmatched.

So valid pairings always split the circle into two smaller even-sized groups.

---

# DP Definition

Let:

```text
dp[i]
```

represent:

> the number of valid non-crossing handshake pairings among `2 * i` people.

So:

- `dp[0]` means no people
- `dp[1]` means 2 people
- `dp[2]` means 4 people
- and so on

---

# Base Case

If there are no people, then there is exactly one way to do nothing:

```text
dp[0] = 1
```

This may feel slightly unusual, but it is standard in combinatorics and makes the recurrence work cleanly.

---

# Recurrence Relation

Suppose there are `2 * i` people.

We choose person `1` and connect them to some other person.

Assume that this split leaves:

- `2 * j` people on the left side
- `2 * (i - j - 1)` people on the right side

Then:

- the left side can be paired in `dp[j]` ways
- the right side can be paired in `dp[i - j - 1]` ways

By the multiplication principle, the total number of pairings for that split is:

```text
dp[j] * dp[i - j - 1]
```

Now we sum over all valid splits `j = 0` to `i - 1`.

So the recurrence becomes:

```text
dp[i] = sum(dp[j] * dp[i - j - 1]) for j from 0 to i - 1
```

This is exactly the classic **Catalan recurrence**.

---

# Approach 1: Bottom-Up Dynamic Programming

## Intuition

We directly compute the DP array from smaller values to larger values.

Since:

```text
dp[i]
```

depends only on earlier values:

- `dp[0]`
- `dp[1]`
- ...
- `dp[i - 1]`

we can build the table iteratively.

---

## Algorithm

1. Create an array `dp` of length:

```text
numPeople / 2 + 1
```

because we only care about half the number of people.

2. Set:

```text
dp[0] = 1
```

3. For each `i` from `1` to `numPeople / 2`:
   - compute `dp[i]` using the recurrence
   - sum over all `j` from `0` to `i - 1`

4. Return:

```text
dp[numPeople / 2]
```

---

## Java Implementation

```java
class Solution {
    private static int m = 1000000007;

    public int numberOfWays(int numPeople) {
        int[] dp = new int[numPeople / 2 + 1];
        dp[0] = 1;
        for (int i = 1; i <= numPeople / 2; i++) {
            for (int j = 0; j < i; j++) {
                dp[i] += (long) dp[j] * dp[i - j - 1] % m;
                dp[i] %= m;
            }
        }
        return dp[numPeople / 2];
    }
}
```

---

## Complexity Analysis

Let:

```text
n = numPeople / 2
```

### Time Complexity

There are two nested loops:

- outer loop runs `n` times
- inner loop runs up to `i` times

So total work is:

```text
O(n^2)
```

Since `n = numPeople / 2`, this is also:

```text
O(numPeople^2)
```

up to constant factors.

### Space Complexity

The DP array has size `n + 1`, so:

```text
O(n)
```

which is:

```text
O(numPeople)
```

---

# Approach 2: Top-Down Dynamic Programming (Memoization)

## Intuition

Instead of computing all DP states in forward order, we can compute them recursively on demand.

Define a recursive function:

```text
calculateDP(i)
```

which returns:

```text
dp[i]
```

If we have already computed `dp[i]`, we simply return it from memory.

Otherwise, we compute it using the same Catalan-style recurrence.

This is the same DP as Approach 1, but organized top-down instead of bottom-up.

---

## Why Memoization Is Needed

Without memoization, the recursion would repeatedly recompute the same subproblems many times.

For example:

- `calculateDP(i)` calls many smaller `calculateDP(j)`
- those calls overlap heavily across branches

Memoization ensures each state is computed once.

---

## Initialization Trick

We use an array `dp` and initialize it with:

```text
-1
```

to indicate:

> this state has not been computed yet

Then:

- `dp[0] = 1`
- every other value starts as `-1`

Whenever `calculateDP(i)` is called:

- if `dp[i] != -1`, return it immediately
- otherwise compute and store it

---

## Algorithm

1. Create array `dp`
2. Fill it with `-1`
3. Set `dp[0] = 1`
4. Return `calculateDP(numPeople / 2)`

Inside `calculateDP(i)`:

- if already known, return it
- otherwise compute:

```text
dp[i] = sum(calculateDP(j) * calculateDP(i - j - 1))
```

for all `j` from `0` to `i - 1`

---

## Java Implementation

```java
class Solution {
    private static int m = 1000000007;
    int[] dp;

    public int numberOfWays(int numPeople) {
        dp = new int[numPeople / 2 + 1];
        Arrays.fill(dp, -1);
        dp[0] = 1;
        return calculateDP(numPeople / 2);
    }

    private int calculateDP(int i) {
        if (dp[i] != -1) {
            return dp[i];
        }
        dp[i] = 0;
        for (int j = 0; j < i; j++) {
            dp[i] += (long) calculateDP(j) * calculateDP(i - j - 1) % m;
            dp[i] %= m;
        }
        return dp[i];
    }
}
```

---

## Complexity Analysis

Let:

```text
n = numPeople / 2
```

### Time Complexity

There are `O(n)` states.

Each state performs a loop of up to `O(n)`.

So total:

```text
O(n^2)
```

which is also:

```text
O(numPeople^2)
```

up to constants.

### Space Complexity

We store the memoization array of size `O(n)`.

The recursion stack can also go as deep as `O(n)`.

So total space is:

```text
O(n)
```

which is:

```text
O(numPeople)
```

---

# Approach 3: Catalan Numbers

## Intuition

This is the mathematical approach.

The number of non-crossing handshake pairings among `2n` people is equal to the `n`th **Catalan number**.

This is a famous combinatorial result.

So instead of deriving the answer through DP from scratch, we can recognize the structure and compute the Catalan number directly.

---

# Connection to Balanced Parentheses

Catalan numbers count many different objects.

One of them is:

> the number of balanced bracket sequences with `n` pairs of parentheses

There is a bijection between:

- balanced bracket sequences
- non-crossing pairings on a circle

---

## Why the Bijection Works

Imagine flattening the circle into a line.

Each handshake corresponds to pairing two positions.

Now think of:

- opening a handshake as `"("`
- closing it as `")"`

If handshakes do not cross, the resulting structure behaves exactly like a balanced parenthesis sequence.

Crossing handshakes would correspond to an impossible parenthesis nesting pattern.

So counting non-crossing handshakes is exactly the same as counting balanced bracket sequences.

That count is the Catalan number.

---

# Catalan Recurrence / Formula

Let:

```text
C_n
```

be the `n`th Catalan number.

Then:

```text
C_0 = 1
```

and one recurrence is:

```text
C_(i+1) = (2 * (2i + 1) / (i + 2)) * C_i
```

This allows us to compute Catalan numbers iteratively in linear time.

---

# The Modulo Division Problem

We need all results modulo:

```text
10^9 + 7
```

But the recurrence contains division by:

```text
i + 2
```

Division is not directly allowed in modular arithmetic.

Instead, we multiply by the **modular inverse**.

So we rewrite:

```text
C_(i+1) = 2 * (2i + 1) * inverse(i + 2) * C_i mod m
```

where:

```text
m = 10^9 + 7
```

---

# Modular Multiplicative Inverse

For a number `a`, its modular inverse `a^(-1)` satisfies:

```text
a * a^(-1) ≡ 1 (mod m)
```

So dividing by `a` modulo `m` is equivalent to multiplying by `a^(-1)`.

---

# Efficient Inverse Precomputation

We can precompute inverses for all numbers from `1` to `n + 1`.

Using the Euclidean-division-based recurrence:

```text
inv[i] = m - (m / i) * inv[m % i] mod m
```

This lets us compute all required inverses in linear time.

---

## Algorithm

1. Let:

```text
n = numPeople / 2
```

2. Precompute inverses:

```text
inv[1], inv[2], ..., inv[n+1]
```

3. Start with:

```text
C = 1
```

4. For `i = 0` to `n - 1`, update:

```text
C = 2 * (2i + 1) * inv[i + 2] * C mod m
```

5. Return `C`

---

## Java Implementation

```java
class Solution {
    private static int m = 1000000007;

    private int mul(int a, int b) {
        return (int) ((long) a * b % m);
    }

    public int numberOfWays(int numPeople) {
        int n = numPeople / 2;
        int[] inv = new int[numPeople / 2 + 2];
        inv[1] = 1;
        for (int i = 2; i < n + 2; i++) {
            int k = m / i, r = m % i;
            inv[i] = m - mul(k, inv[r]);
        }
        int C = 1;
        for (int i = 0; i < n; i++) {
            C = mul(mul(2 * (2 * i + 1), inv[i + 2]), C);
        }
        return C;
    }
}
```

---

## Complexity Analysis

Let:

```text
n = numPeople / 2
```

### Time Complexity

- inverse precomputation: `O(n)`
- Catalan computation loop: `O(n)`

So total:

```text
O(n)
```

which is:

```text
O(numPeople)
```

---

### Space Complexity

We store the inverse array of size `O(n)`.

So:

```text
O(n)
```

which is:

```text
O(numPeople)
```

---

# Comparing the Three Approaches

| Approach        | Main Idea                                   |  Time Complexity | Space Complexity |
| --------------- | ------------------------------------------- | ---------------: | ---------------: |
| Bottom-Up DP    | Direct Catalan-style recurrence with loops  | `O(numPeople^2)` |   `O(numPeople)` |
| Top-Down DP     | Same recurrence with memoized recursion     | `O(numPeople^2)` |   `O(numPeople)` |
| Catalan Numbers | Use closed recurrence with modular inverses |   `O(numPeople)` |   `O(numPeople)` |

---

# Which Approach Should You Prefer?

## In an Interview

The safest and most natural approach is usually:

- **Bottom-Up DP**

Why?

- it comes directly from the non-crossing split insight
- easy to derive
- easy to implement
- no advanced number theory required

The **Top-Down DP** is also perfectly valid, especially if you like recursive formulations.

The **Catalan-number** approach is elegant and faster, but it is more mathematical and less likely to be expected unless the interviewer is specifically probing deeper combinatorics knowledge.

---

# Key Takeaways

## 1. Fixing one handshake splits the problem

Once person `1` shakes hands with person `k`, the remaining people split into two independent groups.

This gives the recurrence.

---

## 2. Non-crossing pairings are Catalan objects

This problem is another classic Catalan-number setting.

It belongs to the same family as:

- balanced parentheses
- full binary trees
- triangulations of polygons
- non-crossing chord pairings

---

## 3. DP and Catalan numbers are two views of the same structure

The DP recurrence:

```text
dp[i] = sum(dp[j] * dp[i - j - 1])
```

is exactly the Catalan recurrence.

---

## 4. Modular inverses replace division under modulo

When the Catalan recurrence is written in multiplicative form, division becomes multiplication by inverse.

---

# Final Insight

This problem looks geometric because of the circle and crossing handshakes, but the real structure is combinatorial.

The moment you realize that one handshake splits the circle into two smaller non-crossing subproblems, the DP recurrence becomes inevitable.

And once you recognize that recurrence, you are looking directly at the Catalan numbers.
