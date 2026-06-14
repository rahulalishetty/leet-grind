# Regular Expression Matching — Exhaustive Notes

## Problem Overview

The problem is to determine whether an input string `text` matches a pattern `pattern` where:

- `.` matches **any single character**
- `*` means **zero or more of the preceding element**

This is the classic **Regular Expression Matching** problem.

The match must cover the **entire** string, not just a prefix.

---

## Why This Problem Is Subtle

At first glance, matching seems like a left-to-right process:

- compare current character in `text`
- compare current character in `pattern`
- move forward if they match

That works when the pattern contains only normal characters and `.`.

However, `*` changes the game completely.

For example:

- pattern `"a*"` can match `""`, `"a"`, `"aa"`, `"aaa"`, ...
- pattern `".*"` can match almost any string
- pattern `"c*a*b"` can skip `c*`, use `a*` multiple times, then match `b`

So a single `*` introduces **branching decisions**:

- ignore the `x*` completely
- consume one matching character and try again

That branching is why recursion appears naturally, and why memoization or DP becomes important.

---

# Approach 1: Recursion

## Intuition

If there were **no `*` characters**, the solution would be simple:

1. Compare the current characters
2. If they match, move both pointers forward
3. If they do not match, return `false`

With `*`, the pattern may represent many possible lengths.

Suppose the pattern starts with `"a*"`. Then:

- it may represent zero `'a'` characters
- or one `'a'`
- or two `'a'`
- or more

So for a given `text` and `pattern`, we must explore multiple valid possibilities.

A recursive solution is a natural way to represent this.

---

## Basic Recursive Idea Without `*`

If we ignore `*`, then a simple recursive matcher would be:

```python
def match(text, pattern):
    if not pattern:
        return not text
    first_match = bool(text) and pattern[0] in {text[0], "."}
    return first_match and match(text[1:], pattern[1:])
```

### Meaning

- If the pattern is empty, then the text must also be empty
- Otherwise, check whether the first characters match
- If they do, recurse on the suffixes

This is straightforward.

---

## How `*` Changes the Logic

If `pattern[1] == '*'`, then we have a unit like:

```text
pattern[0]*
```

This means zero or more occurrences of `pattern[0]`.

So there are two choices:

### Choice 1: Ignore `x*`

Treat it as matching zero copies.

That means:

- skip `pattern[0]` and `*`
- move to `pattern.substring(2)`

### Choice 2: Use One Matching Character

If the first character matches, then:

- consume one character from `text`
- keep the pattern where it is, because `*` can still be used again

That means recurse on:

- `text.substring(1)`
- same `pattern`

If either choice succeeds, the match succeeds.

---

## Java Recursive Solution

```java
class Solution {
    public boolean isMatch(String text, String pattern) {
        if (pattern.isEmpty()) return text.isEmpty();

        boolean first_match =
            (!text.isEmpty() &&
                (pattern.charAt(0) == text.charAt(0) ||
                    pattern.charAt(0) == '.'));

        if (pattern.length() >= 2 && pattern.charAt(1) == '*') {
            return (
                isMatch(text, pattern.substring(2)) ||
                (first_match && isMatch(text.substring(1), pattern))
            );
        } else {
            return (
                first_match && isMatch(text.substring(1), pattern.substring(1))
            );
        }
    }
}
```

---

## Detailed Walkthrough of the Recursive Logic

### Base Case

```java
if (pattern.isEmpty()) return text.isEmpty();
```

If no pattern remains, the only valid match is when no text remains either.

---

### Compute `first_match`

```java
boolean first_match =
    (!text.isEmpty() &&
        (pattern.charAt(0) == text.charAt(0) ||
            pattern.charAt(0) == '.'));
```

This checks whether the current pattern character matches the current text character.

This is true if:

- text is not empty
- and either:
  - characters are equal, or
  - pattern has `.`

---

### Case 1: Pattern Has `*`

```java
if (pattern.length() >= 2 && pattern.charAt(1) == '*')
```

Then the pattern begins with something like:

```text
a*
.*
b*
```

Now we branch.

#### Option A: Skip This `x*`

```java
isMatch(text, pattern.substring(2))
```

This means zero copies of `x`.

#### Option B: Use One Character

```java
first_match && isMatch(text.substring(1), pattern)
```

This means:

- current characters must match
- consume one character from `text`
- keep pattern unchanged so `*` can consume more

---

### Case 2: No `*`

Then we must match one character and move both forward:

```java
first_match && isMatch(text.substring(1), pattern.substring(1))
```

---

## Example 1

### Input

```text
text = "aa"
pattern = "a"
```

### Flow

- first characters match
- no `*`
- recurse on `"a"` and `""`
- pattern empty but text not empty
- return `false`

So result is `false`.

---

## Example 2

### Input

```text
text = "aa"
pattern = "a*"
```

### Flow

Pattern has `*`.

Two possibilities:

#### Skip `a*`

- match `"aa"` with `""` → false

#### Use one `a`

- first characters match
- recurse on `"a"` with `"a*"`

Again:

- skip `a*` → `"a"` vs `""` → false
- use one `a` → `""` vs `"a*"`

Now:

- skip `a*` → `""` vs `""` → true

So final answer is `true`.

---

## Example 3

### Input

```text
text = "ab"
pattern = ".*"
```

`.` matches any character, and `*` means repeat it any number of times.

This can consume:

- `a`
- then `b`
- then stop

So the answer is `true`.

---

## Why Pure Recursion Is Expensive

The recursion recomputes the same states many times.

For example, the state:

```text
match(text[i:], pattern[j:])
```

can be reached from multiple paths.

That causes exponential-style branching.

Also, `substring()` creates new string objects repeatedly, which adds more cost.

So while recursion is elegant, it is not efficient enough for larger inputs.

---

## Complexity Analysis of Recursive Approach

Let:

- `T = text.length()`
- `P = pattern.length()`

### Time Complexity

The worst-case branching is very large because `*` causes many recursive possibilities.

The official analysis is much more complex than a simple big-O expression, but the key point is:

- the recursion can explore many repeated suffix combinations
- it becomes far more expensive than polynomial-time DP

So in practice, this naive recursive solution is too slow on hard cases.

---

### Space Complexity

The recursion stack can become deep.

Also, the repeated `substring()` calls create many intermediate string objects.

So the memory cost can also become very large.

---

# Approach 2: Dynamic Programming

## Intuition

The recursive approach keeps asking the same question many times:

> Does `text[i:]` match `pattern[j:]`?

This suggests a natural DP state.

Define:

```text
dp(i, j) = whether text[i:] matches pattern[j:]
```

This is the essential insight.

Instead of rebuilding suffix strings, we use indices into the original strings.

That gives us:

- no repeated substring construction
- reusable memoized states
- polynomial-time solution

---

## State Definition

For indices:

- `i` in `[0, text.length()]`
- `j` in `[0, pattern.length()]`

Define:

```text
dp(i, j) = true if text substring starting at i matches pattern substring starting at j
```

This exact state works for both:

- top-down memoization
- bottom-up tabulation

---

# Top-Down Dynamic Programming

## Idea

Use the same recursive structure as before, but store computed answers in a memo table.

So:

- first time we compute `dp(i, j)`, we solve it
- next time, we reuse the stored answer

This removes duplicate work.

---

## Java Top-Down Solution

```java
enum Result {
    TRUE,
    FALSE,
}

class Solution {
    Result[][] memo;

    public boolean isMatch(String text, String pattern) {
        memo = new Result[text.length() + 1][pattern.length() + 1];
        return dp(0, 0, text, pattern);
    }

    public boolean dp(int i, int j, String text, String pattern) {
        if (memo[i][j] != null) {
            return memo[i][j] == Result.TRUE;
        }

        boolean ans;
        if (j == pattern.length()) {
            ans = i == text.length();
        } else {
            boolean first_match =
                (i < text.length() &&
                    (pattern.charAt(j) == text.charAt(i) ||
                        pattern.charAt(j) == '.'));

            if (j + 1 < pattern.length() && pattern.charAt(j + 1) == '*') {
                ans = (dp(i, j + 2, text, pattern) ||
                    (first_match && dp(i + 1, j, text, pattern)));
            } else {
                ans = first_match && dp(i + 1, j + 1, text, pattern);
            }
        }

        memo[i][j] = ans ? Result.TRUE : Result.FALSE;
        return ans;
    }
}
```

---

## Explanation of the Top-Down DP

### Memo Table

```java
Result[][] memo;
```

This stores whether a state has already been computed.

Possible values:

- `null` → not computed yet
- `TRUE`
- `FALSE`

---

### Start State

```java
return dp(0, 0, text, pattern);
```

This asks:

> Does the full text match the full pattern?

---

### Memo Check

```java
if (memo[i][j] != null) {
    return memo[i][j] == Result.TRUE;
}
```

If this state was computed before, just return it.

This is what cuts off repeated recursion.

---

### Base Case

```java
if (j == pattern.length()) {
    ans = i == text.length();
}
```

If pattern is exhausted, text must also be exhausted.

---

### Current Character Match

```java
boolean first_match =
    (i < text.length() &&
        (pattern.charAt(j) == text.charAt(i) ||
            pattern.charAt(j) == '.'));
```

Same logic as recursive approach, but now based on indices.

---

### Handling `*`

```java
if (j + 1 < pattern.length() && pattern.charAt(j + 1) == '*')
```

Then:

```java
ans = dp(i, j + 2, text, pattern) ||
      (first_match && dp(i + 1, j, text, pattern));
```

#### Meaning

- `dp(i, j + 2)` → skip this `x*`
- `first_match && dp(i + 1, j)` → consume one character and stay at same pattern position

---

### No `*`

```java
ans = first_match && dp(i + 1, j + 1, text, pattern);
```

Then both text and pattern advance by one.

---

## Example Walkthrough

### Input

```text
text = "aab"
pattern = "c*a*b"
```

We begin with:

```text
dp(0, 0)
```

Pattern at index 0 is `c`, and next is `*`, so:

- skip `c*` → `dp(0, 2)`
- try consuming if first matches → it does not, so skip only

Now at `dp(0, 2)`:

- pattern at index 2 is `a`, next is `*`

Two choices:

- skip `a*` → `dp(0, 4)`
- consume one `a` → `dp(1, 2)`

`dp(0, 4)` fails because pattern is now `b` and text begins with `a`.

`dp(1, 2)` again handles `a*`:

- consume another `a` → `dp(2, 2)`
- or skip later

Eventually:

- skip `a*` from `dp(2, 2)` → `dp(2, 4)`
- `b` matches `b`
- then both end together

So the answer is `true`.

---

## Complexity of Top-Down DP

### Time Complexity: `O(TP)`

There are only:

```text
(T + 1) * (P + 1)
```

distinct states `(i, j)`.

Each state does `O(1)` work besides recursive calls.

So total complexity is:

```text
O(TP)
```

---

### Space Complexity: `O(TP)`

The memo table stores one result per state.

Additionally, recursion stack depth exists, but the dominant storage is still the memo table.

So total space is:

```text
O(TP)
```

---

# Bottom-Up Dynamic Programming

## Intuition

Top-down DP works well, but some people prefer iterative tabulation.

If:

```text
dp(i, j) = whether text[i:] matches pattern[j:]
```

then we can fill a table from the end toward the beginning.

Why from the end?

Because the recurrence depends on future states like:

- `dp(i, j + 2)`
- `dp(i + 1, j)`
- `dp(i + 1, j + 1)`

So we should compute smaller suffixes first.

---

## Java Bottom-Up Solution

```java
class Solution {
    public boolean isMatch(String text, String pattern) {
        boolean[][] dp = new boolean[text.length() + 1][pattern.length() + 1];
        dp[text.length()][pattern.length()] = true;

        for (int i = text.length(); i >= 0; i--) {
            for (int j = pattern.length() - 1; j >= 0; j--) {
                boolean first_match =
                    (i < text.length() &&
                        (pattern.charAt(j) == text.charAt(i) ||
                            pattern.charAt(j) == '.'));

                if (j + 1 < pattern.length() && pattern.charAt(j + 1) == '*') {
                    dp[i][j] = dp[i][j + 2] || (first_match && dp[i + 1][j]);
                } else {
                    dp[i][j] = first_match && dp[i + 1][j + 1];
                }
            }
        }

        return dp[0][0];
    }
}
```

---

## Table Meaning

```java
boolean[][] dp
```

Where:

```text
dp[i][j] = whether text[i:] matches pattern[j:]
```

---

## Base Initialization

```java
dp[text.length()][pattern.length()] = true;
```

An empty text matches an empty pattern.

This is the terminal true state.

---

## Iteration Order

```java
for (int i = text.length(); i >= 0; i--) {
    for (int j = pattern.length() - 1; j >= 0; j--) {
```

We go backward because each state depends on states to the right or below.

This is essential.

---

## Current Character Match

```java
boolean first_match =
    (i < text.length() &&
        (pattern.charAt(j) == text.charAt(i) ||
            pattern.charAt(j) == '.'));
```

Same meaning as before.

---

## If `*` Follows

```java
if (j + 1 < pattern.length() && pattern.charAt(j + 1) == '*')
```

Then:

```java
dp[i][j] = dp[i][j + 2] || (first_match && dp[i + 1][j]);
```

### Meaning

- `dp[i][j + 2]` → skip this `x*`
- `first_match && dp[i + 1][j]` → consume one matching text char, keep same pattern

---

## If No `*`

```java
dp[i][j] = first_match && dp[i + 1][j + 1];
```

We match one character and move both forward.

---

## Final Answer

```java
return dp[0][0];
```

This asks whether the full text matches the full pattern.

---

## Example Table Intuition

Take:

```text
text = "ab"
pattern = ".*"
```

We want `dp[0][0]`.

Because `.` matches any character and `*` repeats it, we can consume:

- `'a'`
- `'b'`
- then stop

So the bottom-up table will eventually make `dp[0][0] = true`.

---

## Why Bottom-Up Works

The recurrence is identical to top-down.

The only difference is:

- top-down computes states on demand
- bottom-up fills all states systematically

Both represent the same DP relation.

---

# Comparing the Approaches

## 1. Plain Recursion

### Strengths

- simple to derive
- elegant for understanding the branching structure

### Weaknesses

- repeats many states
- expensive substring creation
- not efficient enough for large inputs

---

## 2. Top-Down DP

### Strengths

- preserves recursive intuition
- avoids repeated work
- very natural transition from recursion

### Weaknesses

- recursion stack overhead
- slightly more bookkeeping with memoization

---

## 3. Bottom-Up DP

### Strengths

- iterative
- no recursion stack
- efficient and systematic

### Weaknesses

- less intuitive at first
- table fill order must be carefully chosen

---

# Critical Insight Behind the Entire Problem

The hardest part is not syntax.
It is recognizing the two behaviors of `*`:

For `x*`, there are only two meaningful choices:

1. **skip it entirely**
2. **use one matching character and stay on it**

That exact recurrence drives every correct solution here.

---

# Common Mistakes

## Mistake 1: Treating `*` as matching “anything”

It does not.

`*` only applies to the **preceding element**.

So:

- `"a*"` means zero or more `'a'`
- `".*"` means zero or more of any character

---

## Mistake 2: Forgetting zero occurrences

For a pattern like `"a*"`, matching zero `'a'` is always valid.

That is why the transition `dp(i, j + 2)` is essential.

---

## Mistake 3: Advancing pattern incorrectly

When consuming one character via `*`, the pattern does **not** move forward.

We remain at the same `j`:

```java
dp(i + 1, j)
```

because `*` can still match more occurrences.

---

## Mistake 4: Matching only a prefix

The problem requires matching the **entire text**.

So partial matching is not enough.

---

# Time and Space Summary

Let:

- `T = text.length()`
- `P = pattern.length()`

## Recursive Approach

- Time: very large / exponential-style branching in worst cases
- Space: large due to recursion and substring creation

## Top-Down DP

- Time: `O(TP)`
- Space: `O(TP)`

## Bottom-Up DP

- Time: `O(TP)`
- Space: `O(TP)`

---

# Final Recommendation

For learning progression:

1. understand the plain recursion first
2. then convert it to top-down DP
3. finally study bottom-up DP for iterative mastery

For interviews and clean correctness:

- **Top-down DP** is often the easiest to explain
- **Bottom-up DP** is often the cleanest iterative solution

---

# Final Summary

This problem becomes manageable once you frame it as:

```text
Does text[i:] match pattern[j:]?
```

Then:

- ordinary characters and `.` behave like simple forward matching
- `*` introduces exactly two choices:
  - skip
  - consume one and stay

The recursive solution reveals the structure.
The dynamic programming solutions make it efficient.

That is the real arc of the problem.
