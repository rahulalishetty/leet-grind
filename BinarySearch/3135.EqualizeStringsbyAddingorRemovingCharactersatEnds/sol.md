# Minimum Operations to Transform `initial` into `target`

## Problem Restatement

We are given two strings:

- `initial`
- `target`

We want to transform `initial` into `target`.

In one operation, we may do exactly one of the following to the current string:

- add one character at the **beginning**
- add one character at the **end**
- remove one character from the **beginning**
- remove one character from the **end**

The goal is to compute the **minimum number of operations** needed.

---

## Core Intuition

The most important thing to notice is this:

Because we are only allowed to add or remove characters from the **front** or **back**, we can never preserve arbitrary scattered characters from `initial`.

The part of `initial` that survives through the transformation must remain a **single contiguous block**.

That means the only part we can keep is some **substring** of `initial`.

At the same time, that kept part must also appear contiguously inside `target`, because after preserving it, we will only add missing characters to its front and/or back to build `target`.

So the problem becomes:

> What is the longest contiguous substring that appears in both `initial` and `target`?

If we keep the **longest common substring**, then:

- everything else in `initial` must be removed
- everything missing to make `target` must be added

That gives the minimum number of operations.

---

## Why Longest Common Substring Is the Right Model

Suppose we keep a common substring of length `L`.

Then:

- `initial.length() - L` characters must be removed from `initial`
- `target.length() - L` characters must be added to obtain `target`

So total operations:

```text
(initial.length() - L) + (target.length() - L)
```

To minimize this quantity, we must maximize `L`.

So the answer is:

```text
n + m - 2 * L
```

where:

- `n = initial.length()`
- `m = target.length()`
- `L = length of the longest common substring`

---

## Why It Is Substring and Not Subsequence

This distinction is crucial.

A **subsequence** allows gaps.
A **substring** must be contiguous.

Here we can only delete from the ends. That means the characters that remain from `initial` must still form one contiguous segment of the original string.

### Example

```text
initial = "abcde"
target  = "xabc"
```

We can keep `"abc"`:

- remove `'d'` and `'e'` from the end
- add `'x'` to the front

This works in 3 operations.

But we cannot keep `"ace"` even though it is a common subsequence, because `"ace"` is not contiguous in `initial`. There is no way to remove only the middle characters using front/back deletions.

So the correct object is the **longest common substring**, not longest common subsequence.

---

## DP Idea

We now need to compute the **longest common substring** of two strings.

This is a standard dynamic programming problem.

### DP Definition

Let:

```text
dp[i][j]
```

represent:

> the length of the longest common substring ending at
> `initial[i - 1]` and `target[j - 1]`

In other words, `dp[i][j]` tells us how long the matching suffix is if we force the substring to end exactly at those two characters.

---

## DP Transition

### Case 1: Characters match

If:

```text
initial.charAt(i - 1) == target.charAt(j - 1)
```

then the common substring can be extended from the previous pair:

```text
dp[i][j] = dp[i - 1][j - 1] + 1
```

### Case 2: Characters do not match

Then a common substring cannot end at both positions simultaneously, so:

```text
dp[i][j] = 0
```

---

## Why This Works

A substring must be contiguous.

So if `initial[i - 1]` and `target[j - 1]` match, then for a longer common substring to end here, the immediately previous characters must also match contiguously. That is exactly why we extend from `dp[i - 1][j - 1]`.

If they do not match, contiguity breaks, so the length resets to `0`.

The overall answer for `L` is simply the maximum value anywhere in the DP table.

---

## Step-by-Step Algorithm

1. Let `n = initial.length()` and `m = target.length()`.
2. Create a 2D DP table `dp[n + 1][m + 1]`, initialized to `0`.
3. Maintain a variable `longest = 0`.
4. For each `i` from `1` to `n`:
   - For each `j` from `1` to `m`:
     - If `initial.charAt(i - 1) == target.charAt(j - 1)`:
       - set `dp[i][j] = dp[i - 1][j - 1] + 1`
       - update `longest`
     - Else:
       - set `dp[i][j] = 0`
5. Final answer:
   ```text
   (n - longest) + (m - longest)
   ```

---

## Full Java Code

```java
class Solution {
    public int minOperations(String initial, String target) {
        int n = initial.length();
        int m = target.length();

        int[][] dp = new int[n + 1][m + 1];
        int longest = 0;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (initial.charAt(i - 1) == target.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    longest = Math.max(longest, dp[i][j]);
                } else {
                    dp[i][j] = 0;
                }
            }
        }

        return (n - longest) + (m - longest);
    }
}
```

---

## Code Walkthrough

### 1. Read lengths

```java
int n = initial.length();
int m = target.length();
```

We store string lengths for convenience.

### 2. Create DP table

```java
int[][] dp = new int[n + 1][m + 1];
```

We use `n + 1` and `m + 1` so that row `0` and column `0` act as base cases.
Java initializes everything to `0`, which is exactly what we need.

### 3. Track best substring length

```java
int longest = 0;
```

This stores the maximum common substring length seen so far.

### 4. Fill DP

```java
for (int i = 1; i <= n; i++) {
    for (int j = 1; j <= m; j++) {
```

We start from `1` so that `i - 1` and `j - 1` correctly map to string indices.

### 5. Matching characters

```java
if (initial.charAt(i - 1) == target.charAt(j - 1)) {
    dp[i][j] = dp[i - 1][j - 1] + 1;
    longest = Math.max(longest, dp[i][j]);
}
```

If the current characters match, we extend the previous matching suffix by 1.

### 6. Non-matching characters

```java
else {
    dp[i][j] = 0;
}
```

A substring ending at these two positions is impossible if the characters differ.

### 7. Compute answer

```java
return (n - longest) + (m - longest);
```

- remove everything from `initial` except the kept substring
- add everything needed to grow that substring into `target`

---

## Worked Example

### Example

```text
initial = "abcd"
target  = "bc"
```

The longest common substring is:

```text
"bc"
```

Its length is `2`.

So:

- remove `'a'` from the front
- remove `'d'` from the back

Total operations:

```text
(4 - 2) + (2 - 2) = 2
```

Correct answer: `2`

---

## Another Example

```text
initial = "abcde"
target  = "xabc"
```

Longest common substring:

```text
"abc"
```

Length = `3`

Operations:

- remove `'d'` and `'e'` from the end of `initial` → 2 operations
- add `'x'` to the front → 1 operation

Total:

```text
(5 - 3) + (4 - 3) = 3
```

---

## Edge Cases

### 1. Strings are already equal

```text
initial = "hello"
target  = "hello"
```

Longest common substring length is `5`.

Answer:

```text
(5 - 5) + (5 - 5) = 0
```

No operations needed.

---

### 2. No common characters

```text
initial = "abc"
target  = "xyz"
```

Longest common substring length is `0`.

Answer:

```text
3 + 3 = 6
```

Remove all 3 characters, then add all 3 characters.

---

### 3. One string is empty

```text
initial = ""
target  = "abc"
```

Longest common substring length is `0`.

Answer:

```text
0 + 3 = 3
```

Just add all characters.

Similarly:

```text
initial = "abc"
target  = ""
```

Answer is `3`.

---

## Correctness Argument

We can justify correctness in two parts.

### Claim 1: Any transformation preserves a substring of `initial`

Since every deletion is only from the front or back, the characters that remain from `initial` must form one contiguous block. So any preserved part is necessarily a substring of `initial`.

Also, since additions are only to the front or back, this preserved block remains contiguous in the final string `target`. Therefore it must also be a substring of `target`.

So every valid transformation preserves some **common substring**.

---

### Claim 2: Keeping the longest common substring is optimal

If we preserve a common substring of length `L`, then the number of operations is:

```text
(n - L) + (m - L)
```

This decreases as `L` increases.

Therefore, among all common substrings, keeping the longest one gives the minimum number of operations.

From Claim 1 and Claim 2, the optimal answer is exactly:

```text
n + m - 2 * (longest common substring length)
```

So the algorithm is correct.

---

## Time Complexity

Let:

- `n = initial.length()`
- `m = target.length()`

We fill an `n x m` DP table once.

### Time

```text
O(n * m)
```

### Space

```text
O(n * m)
```

because of the 2D DP table.

---

## Space Optimization

Observe that `dp[i][j]` depends only on `dp[i - 1][j - 1]`.

So we do not need the full table; only the previous row is needed.

This reduces space from `O(n * m)` to `O(m)`.

---

## Space-Optimized Java Code

```java
class Solution {
    public int minOperations(String initial, String target) {
        int n = initial.length();
        int m = target.length();

        int[] prev = new int[m + 1];
        int longest = 0;

        for (int i = 1; i <= n; i++) {
            int[] curr = new int[m + 1];
            for (int j = 1; j <= m; j++) {
                if (initial.charAt(i - 1) == target.charAt(j - 1)) {
                    curr[j] = prev[j - 1] + 1;
                    longest = Math.max(longest, curr[j]);
                }
            }
            prev = curr;
        }

        return (n - longest) + (m - longest);
    }
}
```

---

## Why the Space Optimization Works

At row `i`, each state:

```text
dp[i][j]
```

uses only:

```text
dp[i - 1][j - 1]
```

So the entire older history is unnecessary. We only need:

- `prev[j - 1]` from the previous row
- `curr[j]` for the current row

Thus one previous row plus one current row is enough.

---

## Complexity of Space-Optimized Version

### Time

```text
O(n * m)
```

### Space

```text
O(m)
```

If desired, we could swap strings first so that `m` is the smaller length, which would reduce memory further to:

```text
O(min(n, m))
```

---

## Final Takeaway

The whole problem reduces to one clean insight:

> Since we may only delete or add at the ends, the part we keep must be a contiguous block.

That means we should preserve the **longest common substring** between `initial` and `target`.

Once that substring is found, the minimum operations are simply:

```text
(initial.length() - longest) + (target.length() - longest)
```

This yields a straightforward dynamic programming solution with:

- **Time:** `O(n * m)`
- **Space:** `O(n * m)` or optimized to `O(m)`
