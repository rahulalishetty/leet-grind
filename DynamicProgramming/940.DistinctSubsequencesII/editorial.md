# 940. Distinct Subsequences II — Exhaustive Solution Notes

## Overview

We are given a string `s` and asked to count the number of **distinct non-empty subsequences**.

A subsequence is formed by deleting zero or more characters without changing the relative order of the remaining ones.

The difficulty is **not** generating subsequences. The real challenge is:

> how to count them **without double-counting duplicates** caused by repeated characters.

This problem has a very elegant dynamic programming solution, but the recurrence is not obvious at first sight.

This write-up explains the accepted DP approach in detail, including:

- why the naive doubling idea works for strings with all distinct characters
- why it fails with repeated characters
- how to subtract the duplicate counts correctly
- why the recurrence works

---

## Problem Statement

Given a string `s`, return the number of **distinct non-empty subsequences** of `s`.

Because the answer may be very large, return it modulo:

```text
10^9 + 7
```

---

## Example 1

**Input**

```text
s = "abc"
```

**Output**

```text
7
```

**Explanation**

The distinct non-empty subsequences are:

```text
"a", "b", "c", "ab", "ac", "bc", "abc"
```

Total:

```text
7
```

---

## Example 2

**Input**

```text
s = "aba"
```

**Output**

```text
6
```

**Explanation**

The distinct non-empty subsequences are:

```text
"a", "b", "ab", "aa", "ba", "aba"
```

Total:

```text
6
```

---

## Example 3

**Input**

```text
s = "aaa"
```

**Output**

```text
3
```

**Explanation**

The distinct non-empty subsequences are:

```text
"a", "aa", "aaa"
```

Total:

```text
3
```

---

## Constraints

- `1 <= s.length <= 2000`
- `s` consists of lowercase English letters

---

# Key Idea

It is easier to count **all distinct subsequences including the empty subsequence**, and then subtract 1 at the end to exclude the empty one.

So throughout the derivation, we define our DP in terms of:

```text
all distinct subsequences of the prefix, including ""
```

This makes the recurrence cleaner.

---

# Step 1: Define the DP State

Let:

```text
dp[i]
```

be the number of distinct subsequences of the prefix:

```text
s[0..i-1]
```

including the empty subsequence.

That means:

- `dp[0] = 1`, because the empty prefix has exactly one subsequence: `""`

If the string length is `N`, then the final answer we want is:

```text
dp[N] - 1
```

because we remove the empty subsequence.

---

# Step 2: The Naive Doubling Idea

Suppose the current character is new and has not appeared before.

For example, consider:

```text
s = "abcx"
```

When we go from `"abc"` to `"abcx"`:

- every old subsequence stays valid
- every old subsequence can also produce a new subsequence by appending `'x'`

So the number of distinct subsequences doubles.

If:

```text
dp[i] = number of distinct subsequences of prefix s[0..i-1]
```

then naively:

```text
dp[i+1] = 2 * dp[i]
```

This works when the new character has not appeared before.

---

## Example: `"abc"`

For `"abc"`:

### Prefix `""`

Subsequences:

```text
""
```

So:

```text
dp[0] = 1
```

### Prefix `"a"`

Subsequences:

```text
"", "a"
```

So:

```text
dp[1] = 2
```

### Prefix `"ab"`

Subsequences:

```text
"", "a", "b", "ab"
```

So:

```text
dp[2] = 4
```

### Prefix `"abc"`

Subsequences:

```text
"", "a", "b", "c", "ab", "ac", "bc", "abc"
```

So:

```text
dp[3] = 8
```

Each step doubled because all characters were distinct.

---

# Step 3: Why Doubling Fails with Repeated Characters

Now consider:

```text
s = "abab"
```

Let us compute carefully.

### Prefix `"a"`

Distinct subsequences:

```text
"", "a"
```

So:

```text
dp[1] = 2
```

### Prefix `"ab"`

Distinct subsequences:

```text
"", "a", "b", "ab"
```

So:

```text
dp[2] = 4
```

### Prefix `"aba"`

If we doubled, we would expect `8`, but let us list them:

```text
"", "a", "b", "aa", "ab", "ba", "aba"
```

That is only:

```text
7
```

not 8.

Why?

Because appending the last `'a'` creates duplicates.

For example:

- old subsequence `""` gives `"a"`
- but `"a"` already existed
- old subsequence `"b"` gives `"ba"`
- old subsequence `"a"` gives `"aa"`
- old subsequence `"ab"` gives `"aba"`

Some are genuinely new, some are duplicates.

So the naive doubling overcounts when the new character has appeared before.

---

# Step 4: What Exactly Gets Double Counted?

Suppose the current character is `c = s[i]`, and the last time we saw `c` was at index:

```text
last[c]
```

When we append `c` to all old subsequences, we generate many subsequences ending in `c`.

But the same kind of subsequences were already created the previous time we processed `c`.

So which subsequences are duplicated?

The duplicated ones are exactly those that were already formed by appending `c` when `c` last appeared.

And the number of distinct subsequences that existed **before that previous occurrence** is:

```text
dp[last[c]]
```

Those are exactly the ones that now get reproduced again.

That is the quantity we need to subtract.

---

# The Recurrence

So the recurrence becomes:

```text
dp[i+1] = 2 * dp[i] - dp[last[c]]
```

where:

- `c = s[i]`
- `last[c]` is the previous index of character `c`
- if `c` has never appeared before, then nothing is subtracted

More precisely:

```text
dp[i+1] = 2 * dp[i]                           if c has not appeared before
dp[i+1] = 2 * dp[i] - dp[last[c]]            otherwise
```

This is the central formula.

---

# Why `dp[last[c]]` and Not Something Else?

This is the subtle part.

Suppose `c` previously appeared at index `j`.

At that time, every subsequence of the prefix before `j` generated a new subsequence ending with `c`.

Those were counted already.

Now at index `i`, appending `c` again to all current subsequences regenerates exactly those earlier “append-c” subsequences from the prefix up to `j - 1`.

The number of those earlier base subsequences is:

```text
dp[j]
```

because `dp[j]` counts subsequences of `s[0..j-1]`.

That is why we subtract `dp[last[c]]`.

---

# Walkthrough on `"aba"`

Let us verify the recurrence.

## Initialization

```text
dp[0] = 1
last[a] = -1
last[b] = -1
```

---

## i = 0, char = 'a'

```text
dp[1] = 2 * dp[0] = 2
```

No previous `'a'`, so no subtraction.

Now:

```text
dp[1] = 2
last[a] = 0
```

---

## i = 1, char = 'b'

```text
dp[2] = 2 * dp[1] = 4
```

No previous `'b'`, so no subtraction.

Now:

```text
dp[2] = 4
last[b] = 1
```

---

## i = 2, char = 'a'

Naively:

```text
2 * dp[2] = 8
```

But `'a'` last appeared at index `0`, so subtract:

```text
dp[last[a]] = dp[0] = 1
```

Thus:

```text
dp[3] = 8 - 1 = 7
```

Now subtract the empty subsequence:

```text
7 - 1 = 6
```

which matches the example.

---

# Walkthrough on `"aaa"`

This is an excellent example because repeated characters dominate.

## Initialization

```text
dp[0] = 1
last[a] = -1
```

---

## i = 0, char = 'a'

```text
dp[1] = 2 * dp[0] = 2
```

No subtraction.

---

## i = 1, char = 'a'

```text
dp[2] = 2 * dp[1] - dp[last[a]]
      = 2 * 2 - dp[0]
      = 4 - 1
      = 3
```

---

## i = 2, char = 'a'

Now previous `'a'` is at index `1`:

```text
dp[3] = 2 * dp[2] - dp[1]
      = 2 * 3 - 2
      = 4
```

Subtract empty subsequence:

```text
4 - 1 = 3
```

So the distinct non-empty subsequences are:

```text
"a", "aa", "aaa"
```

Exactly correct.

---

# Handling Modulo Carefully

Because the recurrence includes subtraction:

```text
dp[i+1] = 2 * dp[i] - dp[last[c]]
```

the intermediate value may become negative under modulo arithmetic.

So after computing modulo, we must normalize:

```text
if (dp[i+1] < 0) dp[i+1] += MOD;
```

This is very important.

---

# Java Implementation

```java
class Solution {
    public int distinctSubseqII(String S) {
        int MOD = 1_000_000_007;
        int N = S.length();
        int[] dp = new int[N + 1];
        dp[0] = 1;

        int[] last = new int[26];
        Arrays.fill(last, -1);

        for (int i = 0; i < N; ++i) {
            int x = S.charAt(i) - 'a';
            dp[i + 1] = dp[i] * 2 % MOD;
            if (last[x] >= 0)
                dp[i + 1] -= dp[last[x]];
            dp[i + 1] %= MOD;
            last[x] = i;
        }

        dp[N]--;
        if (dp[N] < 0) dp[N] += MOD;
        return dp[N];
    }
}
```

---

# Code Explanation

## `dp[0] = 1`

The empty prefix has exactly one subsequence:

```text
""
```

---

## `last[26]`

This stores the last index where each character appeared.

If a character has not appeared yet, its value is `-1`.

---

## Main loop

For each character `s[i]`:

1. double the number of subsequences
2. subtract duplicates caused by previous occurrence of the same character
3. update the last occurrence of this character

---

## `dp[N]--`

This removes the empty subsequence so that only non-empty subsequences remain.

---

# Complexity Analysis

Let `N` be the length of the string.

## Time Complexity

We iterate through the string once.

Each step does constant work.

So:

```text
O(N)
```

---

## Space Complexity

We store:

- `dp` array of size `N + 1`
- `last` array of size `26`

So:

```text
O(N)
```

The editorial notes that this can be optimized further to constant extra space beyond the alphabet storage, but the given implementation is `O(N)`.

---

# Space Optimization Insight

Although the provided solution uses a full `dp` array, the recurrence only ever needs:

- the current total
- the previous DP value associated with the last occurrence of each character

So there is an alternative way to write the solution using only an array of size 26 to track contributions ending with each character.

That reduces space to `O(1)` relative to the alphabet size.

However, the editorial implementation is already efficient enough and easier to explain.

---

# Why This Is Dynamic Programming

This is DP because:

- `dp[i+1]` depends on previously solved states
- the recurrence reuses earlier answers
- repeated substructure appears through prefixes

The hard part is designing the correct state transition so duplicates are subtracted exactly once.

---

# Common Mistakes

## 1. Forgetting the empty subsequence

The recurrence naturally counts the empty subsequence.

So the final answer must subtract 1.

---

## 2. Subtracting the wrong DP value

The duplicate count is:

```text
dp[last[c]]
```

not `dp[last[c] - 1]`, not `dp[i - 1]`.

This is the most common conceptual bug.

---

## 3. Ignoring modulo negativity

After subtraction, the value may be negative.

Always normalize:

```text
if (value < 0) value += MOD;
```

---

## 4. Counting all subsequences instead of distinct subsequences

Without the subtraction term, you count all subsequences with multiplicity, not distinct ones.

---

# Final Summary

## Main Recurrence

Let `dp[i]` be the number of distinct subsequences of `s[0..i-1]`, including the empty subsequence.

Then for character `c = s[i]`:

```text
dp[i+1] = 2 * dp[i]                       if c has not appeared before
dp[i+1] = 2 * dp[i] - dp[last[c]]        otherwise
```

At the end:

```text
answer = dp[N] - 1
```

to remove the empty subsequence.

---

## Complexity

- Time: `O(N)`
- Space: `O(N)` in the given implementation

---

# Best Final Java Solution

```java
class Solution {
    public int distinctSubseqII(String S) {
        int MOD = 1_000_000_007;
        int N = S.length();
        int[] dp = new int[N + 1];
        dp[0] = 1;

        int[] last = new int[26];
        Arrays.fill(last, -1);

        for (int i = 0; i < N; ++i) {
            int x = S.charAt(i) - 'a';
            dp[i + 1] = dp[i] * 2 % MOD;
            if (last[x] >= 0)
                dp[i + 1] -= dp[last[x]];
            dp[i + 1] %= MOD;
            last[x] = i;
        }

        dp[N]--;
        if (dp[N] < 0) dp[N] += MOD;
        return dp[N];
    }
}
```

This is the accepted dynamic programming solution for **Distinct Subsequences II**.
