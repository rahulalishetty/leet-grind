# 2223. Sum of Scores of Built Strings

## Problem Statement

You are building a string `s` of length `n` one character at a time by **prepending** each new character to the front.

The strings are labeled from `1` to `n`, where the string with length `i` is called `s_i`.

For example, if:

```text
s = "abaca"
```

then:

- `s1 = "a"`
- `s2 = "ca"`
- `s3 = "aca"`
- `s4 = "baca"`
- `s5 = "abaca"`

The **score** of `s_i` is the length of the **longest common prefix** between `s_i` and `s_n` (the final string `s` itself).

We need to return the **sum of scores of all `s_i`**.

---

## Key Observation

Because we build by **prepending**, each `s_i` is actually a **suffix** of the final string `s`.

So the problem becomes:

> For every suffix of `s`, compute the length of the longest common prefix with the whole string `s`, and sum those values.

That is exactly what the **Z-array** represents.

If `z[i]` is the length of the longest common prefix between:

- `s[0...]`, and
- `s[i...]`

then the answer is:

```text
n + sum(z[i]) for i = 1 to n-1
```

Why do we add `n`?

Because the suffix starting at index `0` is the entire string itself, so its score is `n`.

---

## Example 1

```text
Input: s = "babab"
Output: 9
```

Suffixes of `"babab"`:

- `"babab"` → LCP with `"babab"` = `5`
- `"abab"` → `0`
- `"bab"` → `3`
- `"ab"` → `0`
- `"b"` → `1`

Total:

```text
5 + 0 + 3 + 0 + 1 = 9
```

---

## Example 2

```text
Input: s = "azbazbzaz"
Output: 14
```

Relevant suffixes:

- `"azbazbzaz"` → `9`
- `"az"` → `2`
- `"azbzaz"` → `3`

All others contribute `0`.

Total:

```text
9 + 2 + 3 = 14
```

---

# Approach 1: Direct Suffix Comparison

## Intuition

Since every `s_i` is a suffix of `s`, we can compare each suffix with the full string character by character.

For each starting index `start`:

- compare `s[0]` with `s[start]`
- compare `s[1]` with `s[start + 1]`
- continue until mismatch or end of string

The number of matching characters is the score of that suffix.

This is easy to understand, but too slow for `n = 10^5`.

---

## Algorithm

1. Initialize `answer = 0`.
2. For every suffix start index `start` from `0` to `n - 1`:
   - set `matchLength = 0`
   - while characters match, increment `matchLength`
   - add `matchLength` to `answer`
3. Return `answer`.

---

## Java Code

```java
class Solution {
    public long sumScores(String s) {
        int n = s.length();
        long answer = 0;

        for (int start = 0; start < n; start++) {
            int matchLength = 0;

            while (start + matchLength < n &&
                   s.charAt(matchLength) == s.charAt(start + matchLength)) {
                matchLength++;
            }

            answer += matchLength;
        }

        return answer;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n^2)
```

In the worst case, such as `"aaaaa....a"`, every suffix matches for a long distance.

### Space Complexity

```text
O(1)
```

---

## Verdict

This approach is useful for understanding the problem, but it will not pass efficiently for the full constraints.

---

# Approach 2: Rolling Hash + Binary Search

## Intuition

The bottleneck in Approach 1 is checking a suffix character by character.

We can speed this up by using **rolling hash**:

- preprocess prefix hashes of the string
- get hash of any substring in `O(1)`
- for each suffix, binary search the largest `L` such that:
  - prefix `s[0...L-1]`
  - suffix prefix `s[start...start+L-1]`
    have the same hash

That gives the LCP length between the full string and that suffix.

This reduces the work per suffix from linear to logarithmic.

---

## Important Note About Collisions

Rolling hash is usually safe in competitive programming, but it is still probabilistic.

Two different substrings can theoretically have the same hash.

To reduce that risk, we typically use:

- a large modulus
- a suitable base
- optionally double hashing

For this problem, a single large modulus is usually accepted, but the **Z-algorithm** is still the cleaner exact solution.

---

## Algorithm

1. Precompute:
   - powers of base
   - prefix hash array
2. For each suffix start index `start`:
   - binary search on possible LCP length
   - compare substring hashes
3. Add the found LCP length to the answer.

---

## Java Code

```java
class Solution {
    private static final long MOD = 1_000_000_007L;
    private static final long BASE = 911382323L;

    public long sumScores(String s) {
        int n = s.length();
        long[] prefixHash = new long[n + 1];
        long[] power = new long[n + 1];

        power[0] = 1;
        for (int i = 0; i < n; i++) {
            prefixHash[i + 1] = (prefixHash[i] * BASE + s.charAt(i)) % MOD;
            power[i + 1] = (power[i] * BASE) % MOD;
        }

        long answer = 0;

        for (int start = 0; start < n; start++) {
            int low = 0;
            int high = n - start;

            while (low < high) {
                int mid = low + (high - low + 1) / 2;

                if (getHash(prefixHash, power, 0, mid - 1) ==
                    getHash(prefixHash, power, start, start + mid - 1)) {
                    low = mid;
                } else {
                    high = mid - 1;
                }
            }

            answer += low;
        }

        return answer;
    }

    private long getHash(long[] prefixHash, long[] power, int left, int right) {
        long value = (prefixHash[right + 1]
                    - (prefixHash[left] * power[right - left + 1]) % MOD
                    + MOD) % MOD;
        return value;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n log n)
```

- `n` suffixes
- each suffix uses binary search
- each hash comparison is `O(1)`

### Space Complexity

```text
O(n)
```

---

## Verdict

This is much better than brute force and often accepted, but it is not the most elegant exact solution.

---

# Approach 3: Z-Algorithm

## Intuition

This problem is almost a direct application of the **Z-array**.

The Z-array for a string `s` is defined as:

```text
z[i] = length of the longest substring starting at i
       that matches the prefix of s
```

That is exactly the score of the suffix starting at index `i`.

So:

- the full string contributes `n`
- every other suffix contributes `z[i]`

Therefore:

```text
answer = n + z[1] + z[2] + ... + z[n-1]
```

This gives an `O(n)` solution.

---

## Why Z-Algorithm Fits Perfectly

Let us rewrite the problem carefully.

Each built string `s_i` is a suffix of the final string.

The score of that suffix is the **LCP of the suffix with the full string**.

The Z-array already stores the LCP of every suffix-like starting position with the full prefix.

So this is not just a possible solution. It is the natural one.

---

## Z-Algorithm Refresher

We maintain a window `[left, right]` such that:

```text
s[left...right]
```

matches the prefix:

```text
s[0...right-left]
```

For each `i`:

- if `i > right`, start matching from scratch
- otherwise reuse previously computed Z values
- then extend further if possible
- update `[left, right]` if we found a larger matching segment

This avoids repeated comparisons and ensures linear time.

---

## Step-by-Step on `"babab"`

```text
s = "babab"
index: 0 1 2 3 4
chars: b a b a b
```

We compute:

- `z[1] = 0` because `"abab"` does not start with `"b"`
- `z[2] = 3` because `"bab"` matches prefix `"bab"`
- `z[3] = 0`
- `z[4] = 1`

Now sum:

```text
n + z[1] + z[2] + z[3] + z[4]
= 5 + 0 + 3 + 0 + 1
= 9
```

---

## Java Code

```java
class Solution {
    public long sumScores(String s) {
        int n = s.length();
        int[] z = new int[n];
        long answer = n; // score of the full string itself

        int left = 0;
        int right = 0;

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

            answer += z[i];
        }

        return answer;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n)
```

Each character is extended at most a constant number of times across the whole algorithm.

### Space Complexity

```text
O(n)
```

We store the Z-array.

---

## Verdict

This is the best solution for this problem:

- exact
- linear time
- clean
- directly matches the structure of the question

---

# Approach 4: Prefix Function / KMP Perspective

## Intuition

You may wonder whether the prefix function from KMP can help.

The prefix function tells us, for each prefix ending position, the longest proper prefix that is also a suffix.

That is useful for border and pattern problems, but here we need:

> for every suffix, how much of the whole prefix matches from that suffix start

That is exactly what Z provides, not the prefix function directly.

So KMP thinking is related conceptually, but it is not the best direct tool here.

---

## Takeaway

- **Prefix function** answers border-related questions.
- **Z-array** answers prefix-vs-substring-start questions.

This problem is firmly in the second category.

---

# Final Recommended Solution

Use the **Z-algorithm**.

It is the most natural and efficient solution for this problem.

---

## Clean Final Java Solution

```java
class Solution {
    public long sumScores(String s) {
        int n = s.length();
        int[] z = new int[n];
        long totalScore = n;

        int l = 0;
        int r = 0;

        for (int i = 1; i < n; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }

            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }

            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }

            totalScore += z[i];
        }

        return totalScore;
    }
}
```

---

# Edge Cases

## 1. Single character

```text
s = "a"
```

Only one suffix exists, and it matches fully.

Answer:

```text
1
```

---

## 2. All characters same

```text
s = "aaaa"
```

Suffix scores:

- `"aaaa"` → 4
- `"aaa"` → 3
- `"aa"` → 2
- `"a"` → 1

Total:

```text
10
```

This is the worst case for brute force, but Z still handles it in linear time.

---

## 3. No repeated prefix matches

```text
s = "abcd"
```

Scores:

- `"abcd"` → 4
- `"bcd"` → 0
- `"cd"` → 0
- `"d"` → 0

Total:

```text
4
```

---

# Common Mistakes

## 1. Forgetting that `s_i` are suffixes

A very common confusion is to think the built strings are prefixes. They are not.

Because characters are **prepended**, the intermediate strings are suffixes of the final string.

---

## 2. Forgetting to include the full string

The full string always contributes:

```text
n
```

because it matches itself completely.

---

## 3. Using `int` for the final answer

The answer can be as large as:

```text
n + (n-1) + (n-2) + ... + 1 = O(n^2)
```

For `n = 10^5`, that exceeds `int`.

So the result must be stored in `long`.

---

# Interview Summary

## Core transformation

The problem looks unusual because of the "built strings" wording, but the real problem is:

> Sum the LCP of every suffix with the full string.

---

## Best insight

That is exactly what the **Z-array** gives.

---

## Best solution

- Build Z-array in `O(n)`
- Sum all values plus `n`

---

## Final Complexity

### Time

```text
O(n)
```

### Space

```text
O(n)
```

---

# Final Answer

```java
class Solution {
    public long sumScores(String s) {
        int n = s.length();
        int[] z = new int[n];
        long answer = n;

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

            answer += z[i];
        }

        return answer;
    }
}
```
