# K-Palindrome — Detailed Notes

This document converts the provided explanation into a detailed Markdown note.

---

# Problem Idea

We want to determine whether a string `s` is a **K-Palindrome**.

A string is a K-Palindrome if we can remove at most `k` characters from it so that the remaining string becomes a palindrome.

So the core question becomes:

> What is the minimum number of deletions needed to turn `s` into a palindrome?

If that minimum is:

```text
<= k
```

then the answer is `true`, otherwise it is `false`.

---

# Core Insight

A palindrome reads the same from left to right and right to left.

That suggests using two pointers:

- one starting from the left
- one starting from the right

Let those pointers be:

- `i`
- `j`

We compare:

```text
s.charAt(i)` and `s.charAt(j)
```

There are two possibilities.

---

## Case 1: Characters Match

If:

```text
s[i] == s[j]
```

then these two characters can both remain in the final palindrome.

So we simply move inward:

```text
i++
j--
```

No deletion is needed here.

---

## Case 2: Characters Do Not Match

If:

```text
s[i] != s[j]
```

then both characters cannot simultaneously remain as matching ends of a palindrome.

So we have two choices:

1. delete `s[i]`
2. delete `s[j]`

That leads to two recursive subproblems:

- solve for `(i + 1, j)`
- solve for `(i, j - 1)`

Since deleting one character costs `1`, the recurrence becomes:

```text
1 + min( solve(i + 1, j), solve(i, j - 1) )
```

---

# Similar Problems

This idea is closely related to:

- **72. Edit Distance**
- **516. Longest Palindromic Subsequence**

In fact, this problem can also be interpreted through longest palindromic subsequence, but the provided approaches focus directly on minimum deletions.

---

# Approach 1: Top-Down DP (2D)

## Intuition

Define a recursive function:

```text
f(i, j)
```

which returns:

> the minimum number of deletions needed to make the substring `s[i...j]` a palindrome.

This is naturally recursive, because after comparing the ends, we reduce the problem to smaller substrings.

However, many `(i, j)` pairs repeat, so we use memoization.

---

## Base Cases

### Base Case 1: One Character

If:

```text
i == j
```

then the substring has only one character.

A single character is already a palindrome, so:

```text
0 deletions needed
```

---

### Base Case 2: Two Characters

If:

```text
i == j - 1
```

then the substring has exactly two characters.

- if they match, it is already a palindrome → `0`
- otherwise, remove one of them → `1`

---

## Recurrence

### If characters match

```text
s[i] == s[j]
```

then no deletion is needed at the ends, so:

```text
f(i, j) = f(i + 1, j - 1)
```

---

### If characters do not match

```text
s[i] != s[j]
```

then:

```text
f(i, j) = 1 + min(f(i + 1, j), f(i, j - 1))
```

The `+1` is the cost of deleting either the left or the right character.

---

## Java Implementation

```java
class Solution {
    Integer memo[][];

    int isValidPalindrome(String s, int i, int j) {

        // Base case, only 1 letter remaining.
        if (i == j)
            return 0;

        // Base case 2, only 2 letters remaining.
        if (i == j - 1)
            return s.charAt(i) != s.charAt(j) ? 1 : 0;

        // Return the precomputed value if exists.
        if (memo[i][j] != null)
            return memo[i][j];

        // Case 1: Character at i equals character at j
        if (s.charAt(i) == s.charAt(j))
            return memo[i][j] = isValidPalindrome(s, i + 1, j - 1);

        // Case 2: Character at i does not equal character at j
        return memo[i][j] = 1 + Math.min(
            isValidPalindrome(s, i + 1, j),
            isValidPalindrome(s, i, j - 1)
        );
    }

    public boolean isValidPalindrome(String s, int k) {
        memo = new Integer[s.length()][s.length()];

        // Return true if the minimum deletions needed
        // are <= k
        return isValidPalindrome(s, 0, s.length() - 1) <= k;
    }
}
```

---

## Complexity Analysis

Let `n` be the length of the string.

### Time Complexity

There are `O(n^2)` possible pairs `(i, j)`.

Each state is computed once, and each computation does `O(1)` work besides recursive lookups.

So:

```text
O(n^2)
```

---

### Space Complexity

The memo table stores `O(n^2)` states.

The recursion stack can go as deep as `O(n)`.

So the total is dominated by:

```text
O(n^2)
```

---

# Approach 2: Bottom-Up DP (2D)

## Intuition

The top-down solution tells us exactly what the DP state should be.

So now we can fill the same table iteratively instead of recursively.

Let:

```text
memo[i][j]
```

mean:

> minimum deletions needed to make `s[i...j]` a palindrome.

We fill the table in an order such that all required subproblems are already known before computing the current state.

---

## Why Filling Order Matters

The recurrence uses:

- `memo[i + 1][j - 1]`
- `memo[i + 1][j]`
- `memo[i][j - 1]`

So before computing `memo[i][j]`, we must already know states corresponding to smaller substrings.

That means:

- `i` should move backward
- `j` should move forward

This guarantees that smaller windows are filled before larger ones.

---

## Recurrence

Same as top-down:

### If ends match

```text
memo[i][j] = memo[i + 1][j - 1]
```

### If ends do not match

```text
memo[i][j] = 1 + min(memo[i + 1][j], memo[i][j - 1])
```

---

## Java Implementation

```java
class Solution {
    public boolean isValidPalindrome(String s, int k) {
        int memo[][] = new int[s.length()][s.length()];

        // Generate all combinations of i and j in the correct order
        for (int i = s.length() - 2; i >= 0; i--)
            for (int j = i + 1; j < s.length(); j++) {
                // Case 1: Character at i equals character at j
                if (s.charAt(i) == s.charAt(j))
                    memo[i][j] = memo[i + 1][j - 1];

                // Case 2: Character at i does not equal character at j
                else
                    memo[i][j] = 1 + Math.min(memo[i + 1][j], memo[i][j - 1]);
            }

        return memo[0][s.length() - 1] <= k;
    }
}
```

---

## Complexity Analysis

### Time Complexity

We fill an `n x n` DP table.

So:

```text
O(n^2)
```

---

### Space Complexity

The table itself takes:

```text
O(n^2)
```

---

# Approach 3: Bottom-Up DP (1D)

## Intuition

Looking closely at the 2D bottom-up recurrence, we notice that for any `memo[i][j]`, we only need:

- the value directly below: `memo[i + 1][j]`
- the value diagonally below-left: `memo[i + 1][j - 1]`
- the value to the left in the current row: `memo[i][j - 1]`

That means we do not need the full 2D table.

We only need one row of DP at a time, plus a variable to preserve the old diagonal value.

This reduces space from:

```text
O(n^2)
```

to:

```text
O(n)
```

---

## Key Variables

We use:

- `memo[j]` → represents the current compressed DP row
- `temp` → temporarily stores old `memo[j]`
- `prev` → stores the previous diagonal value (`memo[i+1][j-1]`)

---

## How the Compression Works

When iterating:

- `memo[j]` before update is the old `memo[i+1][j]`
- `memo[j-1]` after update is the new `memo[i][j-1]`
- `prev` stores old `memo[i+1][j-1]`

This gives exactly the three values needed by the recurrence.

---

## Recurrence

### If ends match

```text
memo[j] = prev
```

because `prev` represents `memo[i+1][j-1]`.

### If ends do not match

```text
memo[j] = 1 + min(memo[j], memo[j - 1])
```

where:

- `memo[j]` is old `memo[i+1][j]`
- `memo[j - 1]` is current `memo[i][j-1]`

---

## Java Implementation

```java
class Solution {
    public boolean isValidPalindrome(String s, int k) {
        int memo[] = new int[s.length()];

        // To store the previous required values from memo
        int temp, prev;

        for (int i = s.length() - 2; i >= 0; i--) {
            // prev stores memo[i+1][j-1]
            prev = 0;
            for (int j = i + 1; j < s.length(); j++) {
                // Store memo[i+1][j] temporarily
                temp = memo[j];

                // Case 1: Characters match
                if (s.charAt(i) == s.charAt(j))
                    memo[j] = prev;

                // Case 2: Characters do not match
                else
                    memo[j] = 1 + Math.min(memo[j], memo[j - 1]);

                // Update prev for next iteration
                prev = temp;
            }
        }

        return memo[s.length() - 1] <= k;
    }
}
```

---

## Complexity Analysis

### Time Complexity

We still consider all `(i, j)` pairs with `i < j`.

So:

```text
O(n^2)
```

---

### Space Complexity

We only keep:

- one array of length `n`
- a few extra scalar variables

So:

```text
O(n)
```

---

# Comparing the Approaches

| Approach          | Main Idea                                                    | Time Complexity | Space Complexity |
| ----------------- | ------------------------------------------------------------ | --------------: | ---------------: |
| Top-Down DP (2D)  | Recursive memoized minimum deletions on substring `s[i...j]` |        `O(n^2)` |         `O(n^2)` |
| Bottom-Up DP (2D) | Iterative filling of full DP table                           |        `O(n^2)` |         `O(n^2)` |
| Bottom-Up DP (1D) | Space-optimized iterative DP                                 |        `O(n^2)` |           `O(n)` |

---

# Why Minimum Deletions Solves K-Palindrome

The entire problem reduces to this condition:

> Can the string be turned into a palindrome by deleting at most `k` characters?

If the minimum number of deletions required is:

```text
<= k
```

then the string is a valid K-Palindrome.

Otherwise it is not.

So all approaches compute:

```text
minimum deletions to make s a palindrome
```

and then compare that value against `k`.

---

# Small Example Walkthrough

Suppose:

```text
s = "abcdeca"
k = 2
```

We compare ends:

- `a` and `a` match → move inward
- now `b` and `c` do not match

At that point, we can either:

- delete `b`
- or delete `c`

The DP explores both possibilities and computes the minimum deletions needed overall.

If the result is at most `2`, then the answer is `true`.

---

# Final Takeaways

## 1. This is fundamentally a minimum-deletions DP problem

The question "Is this a K-Palindrome?" becomes:

```text
Is minDeletionsToPalindrome(s) <= k?
```

---

## 2. The DP state is very natural

For any substring `s[i...j]`, ask:

> How many deletions are needed to make this substring a palindrome?

That leads directly to the recurrence.

---

## 3. Matching ends cost nothing

If the two ends already match, they can remain in the final palindrome and we recurse inward.

---

## 4. Non-matching ends force a choice

If the ends do not match, one of them must be deleted.

So we take the cheaper of:

- deleting the left end
- deleting the right end

---

## 5. Space can be optimized

The 2D DP is conceptually clean, but only the previous row and left neighbor are needed, so it compresses nicely to 1D.

---

# Best Practical Choice

If you want:

- simplest reasoning → **Top-Down DP (2D)** or **Bottom-Up DP (2D)**
- best space usage → **Bottom-Up DP (1D)**

All three have the same time complexity:

```text
O(n^2)
```

but the 1D version reduces space to:

```text
O(n)
```

which is often the best practical implementation.

---

# Final Insight

This problem looks like a palindrome problem, but the real structure is:

> interval dynamic programming over substrings

Once you define the state as the minimum deletions for `s[i...j]`, the rest follows naturally from comparing the two ends.

That is why all three approaches are clean variations of the same central recurrence.
