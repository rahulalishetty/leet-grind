# 651. 4 Keys Keyboard — Dynamic Programming Approach (Detailed Notes)

This document converts the provided explanation into a detailed Markdown note.

---

# Problem Recap

You have a keyboard with four operations:

- **A** → print one `'A'`
- **Ctrl-A** → select everything on the screen
- **Ctrl-C** → copy the selected text to the buffer
- **Ctrl-V** → paste the buffer, appending it to the screen

Given an integer `n`, return the **maximum number of `'A'` characters** you can print using **at most `n` key presses**.

---

# Core Intuition

A crucial observation is:

> Once you have started using copy-paste operations effectively, there is usually no reason to go back to pressing single `A` again.

Why?

Because if you already have some text on the screen and a useful buffer, pressing `Ctrl-V` grows the screen faster than pressing a single `A`.

So the structure of an optimal solution typically looks like:

1. Press `A` some number of times in the beginning
2. Then do:
   - `Ctrl-A`
   - `Ctrl-C`
   - `Ctrl-V`
   - `Ctrl-V`
   - ...

That means the problem becomes:

> At what point should we stop typing `A` and switch to copy-paste?

This naturally suggests **dynamic programming**, because the best result after `i` key presses can help us build the best result after larger numbers of key presses.

---

# Understanding the Copy-Paste Multiplication

Suppose after `m` key presses, we have a screen length of:

```text
l
```

Now consider performing:

- `Ctrl-A`
- `Ctrl-C`
- then one or more `Ctrl-V`

### If we do this:

```text
Ctrl-A, Ctrl-C, Ctrl-V
```

that costs **3 presses total**, and the screen becomes:

```text
2 * l
```

because we paste one additional copy of the current text.

### If we do:

```text
Ctrl-A, Ctrl-C, Ctrl-V, Ctrl-V
```

that costs **4 presses after the original `m`**, and the screen becomes:

```text
3 * l
```

### In general

If after the copy sequence we paste enough times to end with multiplier `k`, then:

- final screen length = `k * l`
- total presses used = `m + k + 1`
- where `k >= 2`

So dynamic programming can transition from a smaller state `m` to a larger state by multiplying the earlier answer.

---

# Important Observation: No Need to Paste More Than Four Times in a Row

Another useful optimization is:

> There is no need to press `Ctrl-V` more than four times consecutively in an optimal solution.

Let the current screen length be:

```text
l
```

Now compare two strategies.

---

## Strategy 1: Paste 5 times in a row

Operations:

```text
Ctrl-A, Ctrl-C, Ctrl-V, Ctrl-V, Ctrl-V, Ctrl-V, Ctrl-V
```

This uses `7` key presses and gives:

```text
6 * l
```

The buffer still contains:

```text
l
```

---

## Strategy 2: Re-copy after some pasting

Operations:

```text
Ctrl-A, Ctrl-C, Ctrl-V, Ctrl-A, Ctrl-C, Ctrl-V, Ctrl-V
```

This also uses `7` key presses and still gives:

```text
6 * l
```

But now the buffer contains:

```text
2 * l
```

which is strictly better or at least no worse for future operations.

So pasting too many times in a row is suboptimal, because it is better to occasionally refresh the buffer using the larger current screen.

This observation lets us limit transitions and improve the runtime.

---

# DP Definition

Let:

```text
dp[i]
```

represent:

> the maximum number of `'A'` characters that can appear on the screen using exactly `i` key presses

---

# Base Initialization

At minimum, we can always just press `A` repeatedly.

So:

```text
dp[i] = i
```

because with `i` presses, pressing `A` each time gives exactly `i` characters.

This forms the initial baseline answer for each state.

---

# DP Transition

Suppose we already know `dp[i]`.

Now from this state, we can do:

- `Ctrl-A`
- `Ctrl-C`
- then paste up to four times

That creates the following possibilities.

### 1 paste

```text
dp[i + 3] = max(dp[i + 3], 2 * dp[i])
```

because:

- `Ctrl-A`
- `Ctrl-C`
- `Ctrl-V`

adds one extra copy, so total is `2 * dp[i]`

---

### 2 pastes

```text
dp[i + 4] = max(dp[i + 4], 3 * dp[i])
```

---

### 3 pastes

```text
dp[i + 5] = max(dp[i + 5], 4 * dp[i])
```

---

### 4 pastes

```text
dp[i + 6] = max(dp[i + 6], 5 * dp[i])
```

---

## General Form

For every valid `j` with:

```text
i + 3 <= j <= i + 6
```

we update:

```text
dp[j] = max(dp[j], (j - i - 1) * dp[i])
```

Why `(j - i - 1)`?

Because:

- 2 presses are used for `Ctrl-A` and `Ctrl-C`
- the remaining presses are pastes
- if there are `p` pastes, total multiplier becomes `p + 1`
- and `p = j - i - 2`

So multiplier is:

```text
(j - i - 2) + 1 = j - i - 1
```

---

# Algorithm

1. Create an array `dp` of size `n + 1`
2. Initialize:

```text
dp[i] = i
```

for all `i`, because pressing `A` repeatedly is always possible 3. For each `i` from `0` to `n - 3`:

- try transitions to `i + 3`, `i + 4`, `i + 5`, `i + 6`
- update those states using copy-paste multiplication

4. Return `dp[n]`

---

# Java Implementation

```java
class Solution {
    public int maxA(int n) {
        int[] dp = new int[n + 1];

        for (int i = 0; i <= n; i++) {
            dp[i] = i;
        }

        for (int i = 0; i <= n - 3; i++) {
            for (int j = i + 3; j <= Math.min(n, i + 6); j++) {
                dp[j] = Math.max(dp[j], (j - i - 1) * dp[i]);
            }
        }

        return dp[n];
    }
}
```

---

# Step-by-Step Example

Let us understand how this works with:

```text
n = 7
```

Initially:

```text
dp[0] = 0
dp[1] = 1
dp[2] = 2
dp[3] = 3
dp[4] = 4
dp[5] = 5
dp[6] = 6
dp[7] = 7
```

These values correspond to just pressing `A` repeatedly.

Now consider `i = 3`.

At `dp[3] = 3`, the screen can contain:

```text
AAA
```

From here:

### Go to `j = 6`

```text
dp[6] = max(dp[6], (6 - 3 - 1) * dp[3])
      = max(6, 2 * 3)
      = 6
```

No improvement yet.

### Go to `j = 7`

```text
dp[7] = max(dp[7], (7 - 3 - 1) * dp[3])
      = max(7, 3 * 3)
      = 9
```

Now we improve `dp[7]` to `9`.

That corresponds to:

```text
A, A, A, Ctrl-A, Ctrl-C, Ctrl-V, Ctrl-V
```

which is indeed the optimal sequence for `n = 7`.

---

# Why This DP Is Correct

The DP works because every optimal sequence ending at some press count `j` can be thought of as either:

1. Just pressing `A` repeatedly
2. Reaching some earlier optimal state `i`, and then performing one block of:
   - `Ctrl-A`
   - `Ctrl-C`
   - some number of `Ctrl-V`s

Since we already initialize `dp[i] = i`, we cover case 1.

Since we explicitly try all valid copy-paste transitions from earlier states, we cover case 2.

And because we take the maximum across all such possibilities, `dp[j]` becomes optimal.

---

# Complexity Analysis

## Time Complexity

There are `O(n)` DP states.

For each state `i`, we try at most `4` transitions:

- `i + 3`
- `i + 4`
- `i + 5`
- `i + 6`

So each state costs `O(1)` work.

Therefore total time is:

```text
O(n)
```

---

## Space Complexity

We store all states in an array of size `n + 1`:

```text
O(n)
```

---

# Why This Is Better Than Naive O(n^2) DP

A more general DP might consider all possible breakpoints, which leads to `O(n^2)` time.

But the key optimization here is the observation that:

> It is never necessary to do more than four `Ctrl-V` operations in a row.

That bounds the number of transitions from each state by a constant, reducing the time complexity to:

```text
O(n)
```

---

# Key Takeaways

## 1. Pressing `A` is only useful in the beginning

Once copy-paste becomes worthwhile, it dominates individual typing.

## 2. Copy-paste acts like multiplication

If you have built up `l` characters, then:

- copy once
- paste multiple times

lets you multiply that base amount.

## 3. Dynamic programming fits naturally

Each `dp[i]` can generate larger answers through copy-paste transitions.

## 4. No need for long paste chains

More than four consecutive pastes can always be replaced by a better or equivalent plan involving a refreshed copy.

## 5. This observation makes the DP linear

Instead of checking all earlier breakpoints, we only check up to four future states per position.

---

# Final Insight

The main trick in this problem is not just noticing that dynamic programming applies.

It is noticing the stronger structural fact:

> After an optimal copy operation, pasting more than four times in a row is never necessary.

That turns a potentially quadratic DP into a linear one and gives a very elegant final solution.
