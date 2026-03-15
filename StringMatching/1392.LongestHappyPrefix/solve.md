# 1392. Longest Happy Prefix

## Problem Statement

A string is called a **happy prefix** if it is:

- a **non-empty prefix**
- also a **suffix**
- but **not the whole string itself**

Given a string `s`, return the **longest happy prefix** of `s`.

If no such prefix exists, return the empty string `""`.

---

## Example 1

```text
Input:  s = "level"
Output: "l"
```

Explanation:

Prefixes excluding the full string:

```text
"l", "le", "lev", "leve"
```

Suffixes:

```text
"l", "el", "vel", "evel"
```

The longest common one is:

```text
"l"
```

---

## Example 2

```text
Input:  s = "ababab"
Output: "abab"
```

Explanation:

`"abab"` is both a prefix and a suffix.

Overlapping is allowed.

---

## Constraints

- `1 <= s.length <= 10^5`
- `s` contains only lowercase English letters

---

# Core Insight

We need the **longest proper prefix** that is also a suffix.

This is exactly the **border** of the string.

A **border** of a string is a string that is both:

- a prefix
- a suffix

So the task becomes:

> Find the longest border of `s`, excluding the whole string.

This is a classic use case for:

- **KMP prefix function / LPS array**
- alternatively **rolling hash**
- or **Z-algorithm**

The cleanest exact solution is the KMP prefix-function method.

---

# Approach 1: Brute Force Checking All Prefix Lengths

## Intuition

The simplest solution is to try all possible prefix lengths from largest to smallest.

For each length `len`:

- compare `s[0 .. len-1]`
- with `s[n-len .. n-1]`

The first matching length is the answer.

This is easy to understand, but it can be too slow.

---

## Algorithm

1. Let `n = s.length()`
2. For `len` from `n - 1` down to `1`
3. Check if:
   - prefix of length `len`
   - suffix of length `len`
     are equal
4. Return the first such prefix
5. If none found, return `""`

---

## Java Code

```java
class Solution {
    public String longestPrefix(String s) {
        int n = s.length();

        for (int len = n - 1; len >= 1; len--) {
            if (matches(s, len)) {
                return s.substring(0, len);
            }
        }

        return "";
    }

    private boolean matches(String s, int len) {
        int n = s.length();

        for (int i = 0; i < len; i++) {
            if (s.charAt(i) != s.charAt(n - len + i)) {
                return false;
            }
        }

        return true;
    }
}
```

---

## Complexity Analysis

### Time Complexity

In the worst case, we test almost every length, and each test may compare many characters:

```text
O(n^2)
```

### Space Complexity

```text
O(1)
```

excluding the returned substring.

---

## Verdict

Correct but too slow for `n = 10^5`.

---

# Approach 2: Prefix Function / KMP

## Intuition

This is the best exact solution.

The KMP prefix-function array stores, for each index `i`:

> the length of the longest proper prefix of `s[0..i]`
> that is also a suffix of `s[0..i]`

For the whole string, the final value:

```text
lps[n - 1]
```

is exactly the length of the longest happy prefix.

So once we build the prefix table, the answer is immediate.

---

## Why This Works

Suppose `lps[i] = k`.

That means:

```text
s[0 .. k-1] == suffix of s[0 .. i] of length k
```

So for the full string, `lps[n - 1]` is the longest prefix that is also a suffix of the entire string.

That is exactly the definition of the longest happy prefix.

---

## Prefix Function Construction

We maintain:

- `i` scanning the string
- `len` = current best border length for the previous position

If `s[i] == s[len]`, extend the border.

If not, fall back using previously computed border values.

This reuse of earlier information is what makes the algorithm linear.

---

## Java Code

```java
class Solution {
    public String longestPrefix(String s) {
        int n = s.length();
        int[] lps = new int[n];

        int len = 0;
        for (int i = 1; i < n; ) {
            if (s.charAt(i) == s.charAt(len)) {
                lps[i] = ++len;
                i++;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }

        int longest = lps[n - 1];
        return s.substring(0, longest);
    }
}
```

---

## Complexity Analysis

### Time Complexity

Building the prefix table takes:

```text
O(n)
```

### Space Complexity

```text
O(n)
```

for the `lps` array.

---

## Verdict

This is the standard and best exact solution.

---

# Approach 3: Rolling Hash

## Intuition

We can also compare prefix and suffix hashes.

As we scan the string:

- maintain hash of prefix from left to right
- maintain hash of suffix from right to left
- whenever the two hashes match, that length is a candidate answer

This can give a linear-time practical solution.

However, hashing can have collisions, so it is probabilistic unless extra care is used.

---

## Idea

For each length `len` from `1` to `n - 1`:

- compute hash of prefix `s[0 .. len-1]`
- compute hash of suffix `s[n-len .. n-1]`

If hashes match, update answer.

To do this efficiently, maintain rolling values rather than recomputing from scratch.

---

## Java Code

```java
class Solution {
    public String longestPrefix(String s) {
        long mod = 1_000_000_007L;
        long base = 29L;

        long prefixHash = 0;
        long suffixHash = 0;
        long power = 1;
        int answerLen = 0;

        int n = s.length();

        for (int i = 0; i < n - 1; i++) {
            int leftVal = s.charAt(i) - 'a' + 1;
            int rightVal = s.charAt(n - 1 - i) - 'a' + 1;

            prefixHash = (prefixHash * base + leftVal) % mod;
            suffixHash = (suffixHash + rightVal * power) % mod;

            if (prefixHash == suffixHash) {
                answerLen = i + 1;
            }

            power = (power * base) % mod;
        }

        return s.substring(0, answerLen);
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n)
```

### Space Complexity

```text
O(1)
```

excluding the returned substring.

---

## Caveat

This is hash-based, so collisions are possible.

That makes it weaker than the KMP solution in terms of strict correctness guarantees.

---

# Approach 4: Z-Algorithm

## Intuition

The Z-array stores:

```text
z[i] = length of the longest substring starting at i
       that matches the prefix of the whole string
```

So if for some index `i`:

```text
i + z[i] == n
```

then the suffix starting at `i` matches the prefix all the way to the end.

That means the prefix of length `z[i]` is also a suffix.

Among all such positions, we want the maximum `z[i]`.

---

## Example

If:

```text
s = "ababab"
```

then the suffix starting at index `2` is `"abab"`.

If `z[2] = 4`, and `2 + 4 == 6`, then `"abab"` is a prefix and suffix.

So the answer is `"abab"`.

---

## Java Code

```java
class Solution {
    public String longestPrefix(String s) {
        int n = s.length();
        int[] z = new int[n];
        int left = 0, right = 0;
        int best = 0;

        for (int i = 1; i < n; i++) {
            if (i <= right) {
                z[i] = Math.min(right - i + 1, z[i - left]);
            }

            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }

            if (i + z[i] - 1 > right) {
                left = i;
                right = i + z[i] - 1;
            }

            if (i + z[i] == n) {
                best = Math.max(best, z[i]);
            }
        }

        return s.substring(0, best);
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n)
```

### Space Complexity

```text
O(n)
```

---

## Verdict

Also exact and linear.

Very good alternative to KMP.

---

# Why KMP Is the Most Natural Answer

A skeptical view helps here.

This problem is asking for the longest proper prefix that is also a suffix.

That is literally what the prefix-function / LPS array is built for.

So while rolling hash and Z-algorithm work too, KMP is the most direct tool.

---

# Common Mistakes

## 1. Including the whole string as a valid prefix

A happy prefix must be a **proper** prefix.

So the whole string is not allowed.

---

## 2. Forgetting that overlap is allowed

In:

```text
s = "ababab"
```

the answer is `"abab"`.

The prefix and suffix overlap in the string, and that is completely valid.

---

## 3. Using substring creation in brute force too often

Repeated substring extraction can make the brute-force solution even slower in practice.

Character-by-character comparison is better than creating many temporary strings.

---

## 4. Assuming rolling hash is exact

It is practical, but collisions remain theoretically possible.

---

# Final Recommended Solution

Use the KMP prefix-function approach.

---

## Clean Final Java Solution

```java
class Solution {
    public String longestPrefix(String s) {
        int n = s.length();
        int[] lps = new int[n];

        int len = 0;
        for (int i = 1; i < n; ) {
            if (s.charAt(i) == s.charAt(len)) {
                lps[i] = ++len;
                i++;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }

        return s.substring(0, lps[n - 1]);
    }
}
```

---

# Complexity Summary

## Brute Force

- Time: `O(n^2)`
- Space: `O(1)`

## KMP / Prefix Function

- Time: `O(n)`
- Space: `O(n)`

## Rolling Hash

- Time: `O(n)`
- Space: `O(1)`
- Caveat: probabilistic

## Z-Algorithm

- Time: `O(n)`
- Space: `O(n)`

---

# Interview Summary

The longest happy prefix is exactly the longest **border** of the string.

That makes this a textbook prefix-function problem.

The final value of the KMP `lps` array gives the answer directly, so the cleanest exact solution is linear-time KMP preprocessing.
