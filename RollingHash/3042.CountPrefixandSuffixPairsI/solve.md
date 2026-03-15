# 3042. Count Prefix and Suffix Pairs I

## Problem Statement

You are given a 0-indexed string array `words`.

Define a boolean function:

```text
isPrefixAndSuffix(str1, str2)
```

It returns `true` if `str1` is both:

- a prefix of `str2`
- a suffix of `str2`

Otherwise it returns `false`.

We need to count the number of index pairs `(i, j)` such that:

```text
i < j
```

and `words[i]` is both a prefix and a suffix of `words[j]`.

---

## Example 1

```text
Input:  words = ["a","aba","ababa","aa"]
Output: 4
```

Valid pairs:

- `(0,1)` because `"a"` is both prefix and suffix of `"aba"`
- `(0,2)` because `"a"` is both prefix and suffix of `"ababa"`
- `(0,3)` because `"a"` is both prefix and suffix of `"aa"`
- `(1,2)` because `"aba"` is both prefix and suffix of `"ababa"`

Answer = `4`.

---

## Example 2

```text
Input:  words = ["pa","papa","ma","mama"]
Output: 2
```

Valid pairs:

- `(0,1)` because `"pa"` is both prefix and suffix of `"papa"`
- `(2,3)` because `"ma"` is both prefix and suffix of `"mama"`

Answer = `2`.

---

## Example 3

```text
Input:  words = ["abab","ab"]
Output: 0
```

The only possible pair is `(0,1)`, but `"abab"` is not both a prefix and suffix of `"ab"`.

Answer = `0`.

---

## Constraints

- `1 <= words.length <= 50`
- `1 <= words[i].length <= 10`
- `words[i]` consists only of lowercase English letters

---

# Core Insight

For a pair `(i, j)` to be valid:

```text
words[i] must match the beginning of words[j]
and
words[i] must match the end of words[j]
```

So for each pair of strings, we are checking a simple condition:

```text
words[j].startsWith(words[i]) && words[j].endsWith(words[i])
```

Because the constraints are very small, the brute-force pair checking solution is already completely sufficient.

Still, there are a few useful ways to think about it.

---

# Approach 1: Brute Force Pair Checking

## Intuition

Try every pair `(i, j)` with `i < j`.

For each pair, check whether `words[i]` is both a prefix and a suffix of `words[j]`.

Because:

- at most `50` words
- each word length at most `10`

this is very cheap.

---

## Algorithm

1. Initialize `count = 0`
2. For each `i` from `0` to `n - 1`
3. For each `j` from `i + 1` to `n - 1`
4. If `words[j]` starts with `words[i]` and ends with `words[i]`, increment `count`
5. Return `count`

---

## Java Code

```java
class Solution {
    public int countPrefixSuffixPairs(String[] words) {
        int n = words.length;
        int count = 0;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (words[j].startsWith(words[i]) && words[j].endsWith(words[i])) {
                    count++;
                }
            }
        }

        return count;
    }
}
```

---

## Complexity Analysis

Let:

- `n = words.length`
- `L = maximum word length`

### Time Complexity

There are `O(n^2)` pairs.

For each pair, `startsWith` and `endsWith` each take up to `O(L)`.

So total time is:

```text
O(n^2 * L)
```

Given `n <= 50` and `L <= 10`, this is tiny.

### Space Complexity

```text
O(1)
```

---

## Verdict

This is the best practical solution for Problem I.

---

# Approach 2: Manual Character Comparison

## Intuition

Instead of using built-in `startsWith` and `endsWith`, we can manually compare characters.

This makes the logic explicit:

- compare from the front
- compare from the back

It is useful if you want to show exactly what the built-in methods are doing.

---

## Java Code

```java
class Solution {
    public int countPrefixSuffixPairs(String[] words) {
        int n = words.length;
        int count = 0;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (isPrefixAndSuffix(words[i], words[j])) {
                    count++;
                }
            }
        }

        return count;
    }

    private boolean isPrefixAndSuffix(String small, String big) {
        int m = small.length();
        int n = big.length();

        if (m > n) {
            return false;
        }

        for (int i = 0; i < m; i++) {
            if (small.charAt(i) != big.charAt(i)) {
                return false;
            }
        }

        for (int i = 0; i < m; i++) {
            if (small.charAt(i) != big.charAt(n - m + i)) {
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

Again, for each of `O(n^2)` pairs, we compare up to `O(L)` characters for prefix and suffix checks:

```text
O(n^2 * L)
```

### Space Complexity

```text
O(1)
```

---

## Verdict

Same asymptotic complexity as Approach 1, but makes the check more explicit.

---

# Approach 3: Prefix Function / Border Perspective

## Intuition

A string `x` is both a prefix and a suffix of string `y` exactly when `x` is a **border** of `y`.

A border is a string that is both a prefix and a suffix.

So for each `words[j]`, we could compute all its border lengths and check whether any earlier word equals one of those borders.

This is a more string-algorithm-oriented view.

For this small-constraint version, it is not necessary, but it is useful conceptually.

---

## Border View

Suppose:

```text
words[j] = "ababa"
```

Its borders are:

- `"a"` (length 1)
- `"aba"` (length 3)

So any earlier word equal to `"a"` or `"aba"` contributes to the answer.

That means instead of checking every pair directly, we can enumerate border strings of each word.

---

## KMP / Prefix Function Refresher

For a string `s`, the prefix function `pi[i]` gives the length of the longest proper prefix of `s[0..i]` that is also a suffix of it.

For the full string, repeatedly following:

```text
len = pi[len - 1]
```

enumerates all border lengths.

---

## Java Code

```java
class Solution {
    public int countPrefixSuffixPairs(String[] words) {
        int n = words.length;
        int count = 0;

        for (int j = 0; j < n; j++) {
            int[] pi = buildPrefixFunction(words[j]);

            for (int i = 0; i < j; i++) {
                if (isBorder(words[i], words[j], pi)) {
                    count++;
                }
            }
        }

        return count;
    }

    private int[] buildPrefixFunction(String s) {
        int n = s.length();
        int[] pi = new int[n];

        for (int i = 1; i < n; i++) {
            int j = pi[i - 1];
            while (j > 0 && s.charAt(i) != s.charAt(j)) {
                j = pi[j - 1];
            }
            if (s.charAt(i) == s.charAt(j)) {
                j++;
            }
            pi[i] = j;
        }

        return pi;
    }

    private boolean isBorder(String small, String big, int[] pi) {
        int len = small.length();
        if (len > big.length()) {
            return false;
        }

        if (!big.startsWith(small)) {
            return false;
        }

        int border = pi[big.length() - 1];
        while (border > 0) {
            if (border == len) {
                return true;
            }
            border = pi[border - 1];
        }

        return small.equals(big); // full string is also prefix and suffix of itself
    }
}
```

---

## Complexity Analysis

Because constraints are tiny, this is still fine, but it is not simpler than brute force.

A loose bound is:

```text
O(n^2 * L)
```

### Space Complexity

```text
O(L)
```

for the prefix array of a single word.

---

## Verdict

Interesting conceptually, but unnecessary for Problem I.

---

# Approach 4: Enumerate All Valid Prefix-Suffix Candidates of `words[j]`

## Intuition

For each `words[j]`, instead of checking every earlier `words[i]` character by character, we can enumerate every length `len` such that:

```text
prefix of length len == suffix of length len
```

Then compare those candidate strings with previous words.

Since word length is at most `10`, enumerating all possible lengths is trivial.

This is another way to think about the problem.

---

## Algorithm

For each `j`:

1. Enumerate every possible length `len` from `1` to `words[j].length()`
2. If prefix of length `len` equals suffix of length `len`, then that prefix is a valid candidate
3. Compare it against all earlier words to count matches

---

## Java Code

```java
class Solution {
    public int countPrefixSuffixPairs(String[] words) {
        int n = words.length;
        int count = 0;

        for (int j = 0; j < n; j++) {
            String big = words[j];

            for (int len = 1; len <= big.length(); len++) {
                String prefix = big.substring(0, len);
                String suffix = big.substring(big.length() - len);

                if (!prefix.equals(suffix)) {
                    continue;
                }

                for (int i = 0; i < j; i++) {
                    if (words[i].equals(prefix)) {
                        count++;
                    }
                }
            }
        }

        return count;
    }
}
```

---

## Complexity Analysis

This is still very small-scale under the constraints.

A rough upper bound is:

```text
O(n^2 * L^2)
```

because substring creation and comparison add overhead.

But with `L <= 10`, that is still tiny.

### Space Complexity

Temporary substring overhead aside, extra space is small.

---

## Verdict

More expensive and less elegant than direct pair checking, but still fine for such tiny constraints.

---

# Which Approach Should You Prefer?

## Best practical solution

Use:

### Brute force pair checking with `startsWith` and `endsWith`

Why?

- shortest code
- easiest to read
- completely sufficient for constraints

---

## Best conceptual string-algorithm angle

Use the **border** interpretation.

It is a nice way to connect the problem to prefix-function / KMP ideas.

But for this version, it is more educational than necessary.

---

# Final Recommended Solution

```java
class Solution {
    public int countPrefixSuffixPairs(String[] words) {
        int count = 0;

        for (int i = 0; i < words.length; i++) {
            for (int j = i + 1; j < words.length; j++) {
                if (words[j].startsWith(words[i]) && words[j].endsWith(words[i])) {
                    count++;
                }
            }
        }

        return count;
    }
}
```

---

# Why This Is Enough

Let us be skeptical about overengineering here.

Maximum work is roughly:

- at most `50 * 49 / 2 = 1225` pairs
- each string length at most `10`

That is microscopic.

So any advanced structure would add complexity without giving meaningful practical benefit.

The clean brute-force answer is the right answer here.

---

# Common Mistakes

## 1. Forgetting the `i < j` condition

The order of indices matters.
You cannot count both `(i, j)` and `(j, i)`.

---

## 2. Checking only prefix or only suffix

The string must satisfy **both** conditions.

---

## 3. Forgetting that a string can be both prefix and suffix of itself

If `words[i]` and `words[j]` are equal and `i < j`, then that pair is valid, because a string is trivially both prefix and suffix of itself.

---

## 4. Assuming longer string can be prefix/suffix of shorter string

Impossible.
If `words[i].length() > words[j].length()`, the pair cannot work.

Built-in methods handle this naturally.

---

# Complexity Summary

## Brute force with built-in prefix/suffix checks

- Time: `O(n^2 * L)`
- Space: `O(1)`

## Manual character comparison

- Time: `O(n^2 * L)`
- Space: `O(1)`

## Border / prefix-function perspective

- Time: about `O(n^2 * L)`
- Space: `O(L)`

## Enumerating all candidate border lengths

- Time: roughly `O(n^2 * L^2)`
- Space: small temporary overhead

---

# Interview Summary

The real test for a pair `(i, j)` is simply:

```java
words[j].startsWith(words[i]) && words[j].endsWith(words[i])
```

Since constraints are tiny, brute force over all pairs is exactly what you want.

The more advanced insight is that `words[i]` must be a **border** of `words[j]`, but that is not necessary to solve Problem I efficiently.

So the strongest practical answer is the straightforward pair enumeration solution.
