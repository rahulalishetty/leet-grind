# 1392. Longest Happy Prefix — Java Solutions and Detailed Notes

## Problem

A string is called a **happy prefix** if it is:

- a **non-empty prefix** of the string,
- also a **suffix** of the string,
- but **not the entire string itself**.

Given a string `s`, return the **longest** happy prefix.

If none exists, return:

```text
""
```

---

## Examples

### Example 1

```text
s = "level"
```

Prefixes excluding the full string:

```text
"l", "le", "lev", "leve"
```

Suffixes:

```text
"l", "el", "vel", "evel"
```

Longest common one:

```text
"l"
```

Answer:

```text
"l"
```

---

### Example 2

```text
s = "ababab"
```

A longest prefix that is also a suffix is:

```text
"abab"
```

Answer:

```text
"abab"
```

Note that prefix and suffix are allowed to **overlap**.

---

# Core observation

We need the longest length `L` such that:

```text
s[0 .. L-1] == s[n-L .. n-1]
```

with:

```text
1 <= L < n
```

This is a very standard **border** problem in string matching.

A border of a string is a substring that is both a prefix and a suffix.

So this problem is asking for:

> the longest proper border of the string.

The best-known solution uses the **KMP prefix-function / LPS array**.

---

# Approach 1: Brute force by checking all lengths

## Idea

Try all possible happy prefix lengths from largest to smallest:

```text
n-1, n-2, ..., 1
```

For each length `len`, compare:

- prefix of length `len`
- suffix of length `len`

The first valid one is the answer.

---

## Java code

```java
class Solution {
    public String longestPrefix(String s) {
        int n = s.length();

        for (int len = n - 1; len >= 1; len--) {
            if (s.substring(0, len).equals(s.substring(n - len))) {
                return s.substring(0, len);
            }
        }

        return "";
    }
}
```

---

## Complexity

There are `O(n)` candidate lengths.

Each substring comparison can cost `O(n)`.

So time complexity is:

```text
O(n^2)
```

Space complexity is:

```text
O(n)
```

if substring objects are counted.

This is too slow for:

```text
n <= 10^5
```

---

# Approach 2: Rolling Hash

## Idea

We can compare prefix and suffix hashes instead of comparing substrings directly.

As we scan from left to right, we maintain:

- rolling hash of prefix
- rolling hash of suffix

For each length `len`, if the hashes match, that length is a candidate answer.

At the end, return the longest such prefix.

This is fast, but hashing has collision risk unless we use double hashing.

---

## How it works

Let:

- `prefixHash` represent `s[0..i]`
- `suffixHash` represent `s[n-1-i .. n-1]`

If these hashes are equal, then the prefix of length `i+1` may equal the suffix of length `i+1`.

Track the maximum such length.

---

## Java code

```java
class Solution {
    public String longestPrefix(String s) {
        long mod = 1_000_000_007L;
        long base = 31L;

        long prefixHash = 0;
        long suffixHash = 0;
        long power = 1;

        int best = 0;
        int n = s.length();

        for (int i = 0; i < n - 1; i++) {
            int leftVal = s.charAt(i) - 'a' + 1;
            int rightVal = s.charAt(n - 1 - i) - 'a' + 1;

            prefixHash = (prefixHash * base + leftVal) % mod;
            suffixHash = (suffixHash + rightVal * power) % mod;

            if (prefixHash == suffixHash) {
                best = i + 1;
            }

            power = (power * base) % mod;
        }

        return s.substring(0, best);
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

---

## Caution

This is fast, but hash collisions are theoretically possible.

In interviews and production-quality reasoning, KMP is the stronger exact solution.

---

# Approach 3: KMP Prefix Function / LPS Array (Recommended)

This is the standard exact solution.

## Idea

Build the KMP longest-prefix-suffix array, often called:

- `lps`
- `prefix function`
- `pi`

For each index `i`, `lps[i]` stores the length of the longest proper prefix of:

```text
s[0..i]
```

that is also a suffix of:

```text
s[0..i]
```

Then the answer for the whole string is simply:

```text
lps[n - 1]
```

because that is the length of the longest proper prefix of the whole string that is also a suffix.

---

## Why KMP works here

The last entry of the LPS array directly tells us the size of the longest border of the full string.

So if:

```text
len = lps[n - 1]
```

then the answer is:

```text
s.substring(0, len)
```

If `len == 0`, no happy prefix exists.

---

## KMP construction intuition

Let `len` be the current length of the best border.

We scan the string from left to right.

At position `i`:

- if `s[i] == s[len]`, we can extend the current border:

  ```text
  len++
  lps[i] = len
  ```

- otherwise, we fallback to the shorter border:
  ```text
  len = lps[len - 1]
  ```

This avoids rechecking characters from scratch.

---

## Java code

```java
class Solution {
    public String longestPrefix(String s) {
        int n = s.length();
        int[] lps = new int[n];

        int len = 0; // length of previous longest prefix suffix
        int i = 1;

        while (i < n) {
            if (s.charAt(i) == s.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return s.substring(0, lps[n - 1]);
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
O(n)
```

This is optimal and exact.

---

# Approach 4: Z-Algorithm

## Idea

The Z-array tells us, for each position `i`, the length of the longest prefix of the string that matches the substring starting at `i`.

If at position `i`, we have:

```text
i + z[i] == n
```

then the substring starting at `i` reaches the end of the string, so:

```text
s[i .. n-1]
```

is both:

- a suffix of the string,
- and equal to a prefix of length `z[i]`.

That gives a valid happy prefix of length `z[i]`.

Take the maximum such value.

---

## Java code

```java
class Solution {
    public String longestPrefix(String s) {
        int n = s.length();
        int[] z = new int[n];

        int left = 0, right = 0;
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
        }

        int best = 0;
        for (int i = 1; i < n; i++) {
            if (i + z[i] == n) {
                best = Math.max(best, z[i]);
            }
        }

        return s.substring(0, best);
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
O(n)
```

This is also exact and optimal.

---

# Comparison of approaches

## Approach 1: Brute force

### Pros

- simplest to understand

### Cons

- quadratic
- too slow for large input

### Complexity

```text
O(n^2)
```

---

## Approach 2: Rolling hash

### Pros

- linear time
- simple idea

### Cons

- collision risk
- not as canonical as KMP

### Complexity

```text
O(n)
```

---

## Approach 3: KMP / LPS array

### Pros

- exact
- standard
- optimal
- directly tailored for prefix/suffix structure

### Cons

- requires understanding KMP preprocessing

### Complexity

```text
O(n)
```

---

## Approach 4: Z-algorithm

### Pros

- exact
- also linear
- elegant alternative

### Cons

- slightly less direct than KMP for this particular problem

### Complexity

```text
O(n)
```

---

# Why KMP is the best answer here

This problem is almost a direct application of the KMP prefix table.

The last LPS value already means:

> longest proper prefix of the whole string that is also a suffix.

That is exactly the problem statement.

So among all solutions, KMP is the most natural and interview-friendly.

---

# Worked examples

## Example 1: `"level"`

Build `lps`:

- `lps[0] = 0`
- compare `'e'` with `'l'` → mismatch
- compare `'v'` with `'l'` → mismatch
- compare `'e'` with `'l'` → mismatch
- compare `'l'` with `'l'` → match → `lps[4] = 1`

Final:

```text
lps = [0, 0, 0, 0, 1]
```

So answer length is:

```text
1
```

Answer:

```text
"l"
```

---

## Example 2: `"ababab"`

Build `lps`:

- `a b a b a b`
- final LPS array becomes:

```text
[0, 0, 1, 2, 3, 4]
```

So the longest happy prefix has length:

```text
4
```

Answer:

```text
"abab"
```

---

# Final recommended Java solution

```java
class Solution {
    public String longestPrefix(String s) {
        int n = s.length();
        int[] lps = new int[n];

        int len = 0;
        int i = 1;

        while (i < n) {
            if (s.charAt(i) == s.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return s.substring(0, lps[n - 1]);
    }
}
```

---

# Takeaway pattern

If a problem asks for:

- longest prefix also appearing as suffix,
- borders of a string,
- repeated prefix structure,

then you should immediately think of:

- **KMP LPS array**
- or sometimes **Z-array**

This problem is one of the cleanest examples of that pattern.
