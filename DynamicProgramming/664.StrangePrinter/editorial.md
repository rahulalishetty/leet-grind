# 664. Strange Printer — Exhaustive Solution Notes

## Overview

We are given a string `s` and a very unusual printer.

The printer has two important rules:

1. In one turn, it can print **only one repeated character**
2. It can print on **any interval** and overwrite characters that were printed earlier

The goal is to find the **minimum number of turns** required to print the whole string.

At first, this sounds like a simple character-counting problem, but the overwrite ability changes everything.

For example:

```text
s = "aba"
```

A naive strategy might print:

- `a`
- then `b`
- then `a`

which takes 3 turns.

But because overwriting is allowed, we can do better:

- print `aaa`
- then print `b` only at the middle position

That takes only **2 turns**.

This is the central insight of the problem:

> Sometimes it is optimal to print extra characters early and overwrite them later.

That is why this is a classic **interval dynamic programming** problem.

This write-up explains two approaches:

1. **Top-Down Dynamic Programming (Memoization)**
2. **Bottom-Up Dynamic Programming (Tabulation)**

Both run in:

```text
O(n^3)
```

time.

---

## Problem Statement

Given a string `s`, return the minimum number of turns the strange printer needs to print it.

The printer:

- prints only one repeated character per turn
- may print on any substring interval
- may overwrite characters already printed

---

## Example 1

**Input**

```text
s = "aaabbb"
```

**Output**

```text
2
```

**Explanation**

- Print `"aaa"`
- Print `"bbb"`

So only 2 turns are needed.

---

## Example 2

**Input**

```text
s = "aba"
```

**Output**

```text
2
```

**Explanation**

- Print `"aaa"`
- Print `"b"` at the second position

So the answer is 2.

---

## Constraints

- `1 <= s.length <= 100`
- `s` consists of lowercase English letters

---

# Why Dynamic Programming?

The problem asks for a **minimum number of turns**.

That often suggests dynamic programming because:

1. there is **optimal substructure**
2. there are **overlapping subproblems**

If we know the minimum number of turns to print certain substrings, we can use those results to compute larger substrings.

That is exactly the pattern of interval DP.

---

# Very Important Optimization: Remove Consecutive Duplicates

Before solving the problem, we should compress the string by removing consecutive duplicate characters.

Why is this valid?

Because a run of identical consecutive characters can always be printed in one turn.

For example:

```text
"aaabbb" -> "ab"
```

The minimum number of turns does not change.

Similarly:

```text
"aaaa" -> "a"
```

still takes 1 turn.

So compressing the string reduces the problem size without changing the answer.

This optimization helps a lot in practice.

---

## Example of Compression

```text
s = "aaabbaaac"
```

After removing consecutive duplicates:

```text
"abac"
```

The printer never needs to distinguish between multiple adjacent copies of the same character, because one print operation can cover them all together.

---

# Core Interval DP Insight

Consider a substring:

```text
s[start...end]
```

Suppose we want to compute the minimum turns needed to print it.

A simple worst-case strategy is:

- print `s[start]` separately
- then print the rest of the substring optimally

That gives:

```text
1 + dp(start + 1, end)
```

But we may do better.

If there is some later position `k` such that:

```text
s[k] == s[start]
```

then the printer may be able to print `s[start]` and `s[k]` in the same turn.

That is the key optimization.

Instead of paying for `s[start]` separately, we may merge it with a later matching character.

This is where the savings come from.

---

# Approach 1: Top-Down Dynamic Programming (Memoization)

## Intuition

Define a recursive function:

```text
minimumTurns(start, end)
```

which returns the minimum number of turns needed to print the substring:

```text
s[start...end]
```

We consider the first character `s[start]`.

### Worst case

Print `s[start]` separately, then solve the rest:

```text
1 + minimumTurns(start + 1, end)
```

### Better case

If there exists an index `k` in `(start + 1 ... end)` such that:

```text
s[k] == s[start]
```

then we may print these matching characters together.

That gives the recurrence:

```text
minimumTurns(start, k - 1) + minimumTurns(k + 1, end)
```

Why does this help?

Because the turn used to print `s[start]` can also serve `s[k]`, so we avoid paying an extra turn for it separately.

We try all such matching positions `k` and take the minimum.

---

## Base Case

If:

```text
start > end
```

the substring is empty.

So it needs:

```text
0
```

turns.

---

## Memoization

Without memoization, the recursion would recompute the same substring many times.

For example, different split choices may both need the answer for:

```text
s[3...7]
```

So we store results in:

```text
memo[start][end]
```

If already computed, return it immediately.

This reduces the complexity dramatically.

---

## Recurrence

After compression, define:

```text
dp(start, end)
```

Then:

### Initial value

```text
dp(start, end) = 1 + dp(start + 1, end)
```

### Optimization

For every `k` such that:

```text
start < k <= end
and s[k] == s[start]
```

we can try:

```text
dp(start, k - 1) + dp(k + 1, end)
```

Take the minimum of all possibilities.

---

## Why the Matching Optimization Works

Suppose:

```text
s = "aba"
```

At `start = 0`, `s[start] = 'a'`.

There is another `'a'` at index 2.

Instead of treating them separately, we can let the same turn that prints the first `'a'` also cover the last `'a'`, and then only deal with the middle substring.

That is why matching endpoints can reduce the number of turns.

---

## Java Implementation — Top-Down DP

```java
class Solution {

    public int strangePrinter(String s) {
        s = removeDuplicates(s);
        int n = s.length();
        Integer[][] memo = new Integer[n][n];
        return minimumTurns(0, n - 1, s, memo);
    }

    private int minimumTurns(int start, int end, String s, Integer[][] memo) {
        if (start > end) {
            return 0;
        }

        if (memo[start][end] != null) {
            return memo[start][end];
        }

        int minTurns = 1 + minimumTurns(start + 1, end, s, memo);

        for (int k = start + 1; k <= end; k++) {
            if (s.charAt(k) == s.charAt(start)) {
                int turnsWithMatch =
                    minimumTurns(start, k - 1, s, memo) +
                    minimumTurns(k + 1, end, s, memo);
                minTurns = Math.min(minTurns, turnsWithMatch);
            }
        }

        return memo[start][end] = minTurns;
    }

    private String removeDuplicates(String s) {
        StringBuilder uniqueChars = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char currentChar = s.charAt(i);
            uniqueChars.append(currentChar);
            while (i < s.length() && s.charAt(i) == currentChar) {
                i++;
            }
        }
        return uniqueChars.toString();
    }
}
```

---

## Complexity Analysis — Top-Down DP

Let `n` be the length of the compressed string.

### Time Complexity

There are:

```text
O(n^2)
```

possible substrings.

For each substring, we may scan all possible split positions `k`, which costs `O(n)`.

So total time complexity is:

```text
O(n^3)
```

The duplicate-removal preprocessing takes only `O(n)`.

So the overall complexity remains:

```text
O(n^3)
```

---

### Space Complexity

The memo table is:

```text
O(n^2)
```

The recursion stack can go as deep as `O(n)`.

So total space complexity is:

```text
O(n^2)
```

because the memo table dominates.

---

# Approach 2: Bottom-Up Dynamic Programming (Tabulation)

## Intuition

The top-down solution is elegant, but recursion may be undesirable.

We can convert the same logic into a bottom-up DP.

Let:

```text
minTurns[i][j]
```

be the minimum number of turns needed to print the substring:

```text
s[i...j]
```

We fill the table from shorter substrings to longer substrings.

---

## Base Case

A single character always takes exactly 1 turn.

So:

```text
minTurns[i][i] = 1
```

for every `i`.

---

## Transition

For a substring `s[start...end]`:

Initialize with the worst case:

```text
minTurns[start][end] = length
```

or more conceptually:

```text
in the worst case, print characters separately
```

Then try all split points.

If we split between:

```text
[start...mid] and [mid+1...end]
```

the naive total is:

```text
minTurns[start][mid] + minTurns[mid+1][end]
```

But if:

```text
s[mid] == s[end]
```

then the same printing turn can cover both, so we can reduce by 1:

```text
totalTurns--
```

Then take the minimum over all such splits.

---

## Why the Bottom-Up Transition Works

The bottom-up table is just another way of expressing the same interval merging logic as the recursive version.

We solve smaller substrings first, so when we need:

```text
minTurns[start][mid]
minTurns[mid+1][end]
```

they are already known.

---

## Filling Order

We must fill by increasing substring length.

That is:

1. length 1
2. length 2
3. length 3
4. ...
5. length n

This ensures all smaller intervals are already solved before larger ones are computed.

---

## Java Implementation — Bottom-Up DP

```java
class Solution {

    public int strangePrinter(String s) {
        s = removeDuplicates(s);
        int n = s.length();

        int[][] minTurns = new int[n][n];

        for (int i = 0; i < n; i++) {
            minTurns[i][i] = 1;
        }

        for (int length = 2; length <= n; length++) {
            for (int start = 0; start + length - 1 < n; start++) {
                int end = start + length - 1;

                minTurns[start][end] = length;

                for (int split = 0; split < length - 1; split++) {
                    int totalTurns =
                        minTurns[start][start + split] +
                        minTurns[start + split + 1][end];

                    if (s.charAt(start + split) == s.charAt(end)) {
                        totalTurns--;
                    }

                    minTurns[start][end] = Math.min(minTurns[start][end], totalTurns);
                }
            }
        }

        return minTurns[0][n - 1];
    }

    private String removeDuplicates(String s) {
        StringBuilder uniqueChars = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char currentChar = s.charAt(i);
            uniqueChars.append(currentChar);
            while (i < s.length() && s.charAt(i) == currentChar) {
                i++;
            }
        }
        return uniqueChars.toString();
    }
}
```

---

## Complexity Analysis — Bottom-Up DP

Let `n` be the length of the compressed string.

### Time Complexity

There are three nested loops:

1. substring length → `O(n)`
2. starting index → `O(n)`
3. split point → `O(n)`

So total time complexity is:

```text
O(n^3)
```

The duplicate removal takes `O(n)` time.

Thus the final complexity is still:

```text
O(n^3)
```

---

### Space Complexity

The DP table `minTurns` has size:

```text
n × n
```

So the space complexity is:

```text
O(n^2)
```

The compressed string itself takes `O(n)` space, which does not change the final bound.

---

# Example Walkthrough

Consider:

```text
s = "aba"
```

After removing consecutive duplicates, it remains:

```text
"aba"
```

---

## Top-Down View

For substring `"aba"`:

### Worst case

Print `'a'` separately, then solve `"ba"`:

```text
1 + minimumTurns(1, 2)
```

### Better case

There is another `'a'` at index 2, matching the start.

So try:

```text
minimumTurns(0, 1) + minimumTurns(3, 2)
```

The second part is empty, so it contributes `0`.

This matching strategy saves a turn and yields the optimal result `2`.

---

## Bottom-Up View

### Length 1 substrings

Each single character costs `1`.

### Length 2 substrings

- `"ab"` → 2
- `"ba"` → 2

### Length 3 substring `"aba"`

Try splits:

- `"a" + "ba"` → 1 + 2 = 3
- `"ab" + "a"` → 2 + 1 = 3

But since the split character `'a'` matches the end `'a'`, reduce by 1:

```text
3 - 1 = 2
```

So answer is `2`.

---

# Common Mistakes

## 1. Not removing consecutive duplicates

This does not change the answer and can significantly reduce work.

For example:

```text
"aaaaabbbbbccccc"
```

compresses to:

```text
"abc"
```

which is much smaller.

---

## 2. Assuming each distinct character needs one turn

This is not always correct because the printer can overwrite characters.

Example:

```text
"aba"
```

has 2 distinct characters but still requires 2 turns, not 3.

---

## 3. Thinking left-to-right greedily

A greedy approach fails because printing a character now may help later matching characters.

This is why interval DP is needed.

---

## 4. Using substring creation heavily inside recursion

Creating many substring objects can add overhead.
Using indices `(start, end)` is much cleaner and more efficient.

---

# Why This Is an Interval DP Problem

The answer for a substring depends on how we split that substring into smaller substrings.

That is the hallmark of interval DP.

The printer’s overwrite ability creates dependencies between far-apart matching characters, which is why ordinary left-to-right DP is not enough.

Instead, we reason over intervals.

---

# Comparing the Two Approaches

## Top-Down DP

### Strengths

- often easier to derive
- natural recursive structure
- computes only needed subproblems

### Weaknesses

- recursion overhead
- possible stack depth concerns

---

## Bottom-Up DP

### Strengths

- iterative
- avoids recursion
- often preferred in production-style code

### Weaknesses

- slightly more mechanical to derive
- computes all intervals explicitly

---

# Final Summary

## Key Insight

If two positions contain the same character, they may be printed in the same turn, even if other characters between them must be overwritten later.

That is the source of the optimization.

---

## Preprocessing

Remove consecutive duplicate characters first.

This keeps the answer unchanged and reduces the state space.

---

## DP State

### Top-Down

```text
minimumTurns(start, end)
```

### Bottom-Up

```text
minTurns[start][end]
```

Both represent:

> minimum turns needed to print substring `s[start...end]`

---

## Complexity

### Time

```text
O(n^3)
```

### Space

```text
O(n^2)
```

---

# Best Final Java Solution

The top-down solution is usually easier to understand and explain.

```java
class Solution {

    public int strangePrinter(String s) {
        s = removeDuplicates(s);
        int n = s.length();
        Integer[][] memo = new Integer[n][n];
        return minimumTurns(0, n - 1, s, memo);
    }

    private int minimumTurns(int start, int end, String s, Integer[][] memo) {
        if (start > end) {
            return 0;
        }

        if (memo[start][end] != null) {
            return memo[start][end];
        }

        int minTurns = 1 + minimumTurns(start + 1, end, s, memo);

        for (int k = start + 1; k <= end; k++) {
            if (s.charAt(k) == s.charAt(start)) {
                int turnsWithMatch =
                    minimumTurns(start, k - 1, s, memo) +
                    minimumTurns(k + 1, end, s, memo);
                minTurns = Math.min(minTurns, turnsWithMatch);
            }
        }

        return memo[start][end] = minTurns;
    }

    private String removeDuplicates(String s) {
        StringBuilder uniqueChars = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char currentChar = s.charAt(i);
            uniqueChars.append(currentChar);
            while (i < s.length() && s.charAt(i) == currentChar) {
                i++;
            }
        }
        return uniqueChars.toString();
    }
}
```

This is the standard dynamic programming solution for the Strange Printer problem.
