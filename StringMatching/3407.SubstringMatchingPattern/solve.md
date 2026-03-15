# 3407. Substring Matching Pattern

## Problem Statement

You are given:

- a string `s`
- a pattern string `p`

The pattern contains **exactly one `'*'`** character.

The `'*'` can be replaced with **any sequence of zero or more characters**.

Return `true` if `p` can be made a substring of `s`, otherwise return `false`.

---

## Example 1

```text
Input:
s = "leetcode"
p = "ee*e"

Output:
true
```

Explanation:

If `'*'` is replaced with `"tcod"`, then the pattern becomes:

```text
"eetcode"
```

and that is a substring of `"leetcode"`.

---

## Example 2

```text
Input:
s = "car"
p = "c*v"

Output:
false
```

Explanation:

No substring of `"car"` matches the pattern.

---

## Example 3

```text
Input:
s = "luck"
p = "u*"

Output:
true
```

Explanation:

Possible matching substrings include:

- `"u"`
- `"uc"`
- `"uck"`

---

## Constraints

- `1 <= s.length <= 50`
- `1 <= p.length <= 50`
- `s` contains only lowercase English letters
- `p` contains only lowercase English letters and exactly one `'*'`

---

# Core Insight

Since the pattern contains **exactly one wildcard** `'*'`, we can split it into:

- a fixed **prefix** before `'*'`
- a fixed **suffix** after `'*'`

So if:

```text
p = prefix + '*' + suffix
```

then a substring of `s` matches `p` if we can find:

1. `prefix`
2. later in the same substring, `suffix`
3. with any number of characters in between, including zero

That is the decisive simplification.

---

# Pattern Decomposition

Suppose:

```text
p = "ee*e"
```

Then:

- `prefix = "ee"`
- `suffix = "e"`

We need some substring of `s` that:

- starts with `"ee"`
- ends with `"e"`
- has anything in the middle

Example in `"leetcode"`:

```text
"eetcode"
```

starts with `"ee"` and ends with `"e"`.

So it matches.

---

# Approach 1: Brute Force Over All Substrings

## Intuition

Because the string length is at most `50`, one direct method is:

- generate every substring of `s`
- test whether that substring matches the pattern

A substring matches if:

- it starts with `prefix`
- it ends with `suffix`
- its length is at least `prefix.length + suffix.length`

This works because `'*'` can absorb the middle part.

---

## Algorithm

1. Split pattern into `prefix` and `suffix`
2. Enumerate all substrings `s[i..j]`
3. For each substring:
   - check if it starts with `prefix`
   - check if it ends with `suffix`
   - check if length is large enough
4. If any substring works, return `true`
5. Otherwise return `false`

---

## Java Code

```java
class Solution {
    public boolean hasMatch(String s, String p) {
        int star = p.indexOf('*');
        String prefix = p.substring(0, star);
        String suffix = p.substring(star + 1);

        int n = s.length();

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                String sub = s.substring(i, j + 1);

                if (sub.length() < prefix.length() + suffix.length()) {
                    continue;
                }

                if (sub.startsWith(prefix) && sub.endsWith(suffix)) {
                    return true;
                }
            }
        }

        return false;
    }
}
```

---

## Complexity Analysis

Let:

- `n = s.length()`

### Time Complexity

There are `O(n^2)` substrings.

For each substring, `startsWith` and `endsWith` take linear time in the prefix/suffix lengths, and substring creation itself can also cost extra.

So the overall complexity is roughly:

```text
O(n^3)
```

With `n <= 50`, this still passes.

### Space Complexity

Ignoring substring allocation details, auxiliary space is small, but substring creation can use extra temporary memory.

---

## Verdict

Correct and simple, but not the cleanest.

---

# Approach 2: Try Every Start Position and Greedily Place Prefix/Suffix

## Intuition

We do not need to enumerate every substring explicitly.

If a substring matches `prefix + '*' + suffix`, then within `s`:

- `prefix` must appear first
- `suffix` must appear later, at or after the end of that prefix

So we can:

1. find an occurrence of `prefix`
2. then search for `suffix` starting from the end of that prefix
3. if both exist in order, we are done

This is much simpler.

---

## Key Observation

A matching substring does **not** need to span the whole string `s`.

It only needs to exist somewhere inside `s`.

So once we find `prefix` starting at index `i`, and then `suffix` starting at some index `j >= i + prefix.length()`, the substring:

```text
s[i .. j + suffix.length() - 1]
```

matches the pattern.

If `suffix` is empty, then any occurrence of `prefix` is enough.

If `prefix` is empty, then any occurrence of `suffix` is enough.

If both are empty, the answer is trivially `true`.

---

## Java Code

```java
class Solution {
    public boolean hasMatch(String s, String p) {
        int star = p.indexOf('*');
        String prefix = p.substring(0, star);
        String suffix = p.substring(star + 1);

        int n = s.length();

        for (int i = 0; i <= n - prefix.length(); i++) {
            if (!s.startsWith(prefix, i)) {
                continue;
            }

            int suffixStart = i + prefix.length();

            if (suffix.isEmpty()) {
                return true;
            }

            for (int j = suffixStart; j <= n - suffix.length(); j++) {
                if (s.startsWith(suffix, j)) {
                    return true;
                }
            }
        }

        return false;
    }
}
```

---

## Complexity Analysis

### Time Complexity

In the worst case, we may try many prefix positions and for each scan for suffix:

```text
O(n^2)
```

Since `n <= 50`, this is easily acceptable.

### Space Complexity

```text
O(1)
```

---

## Verdict

This is a very practical and clear solution.

---

# Approach 3: Use `indexOf` for Prefix and Suffix Search

## Intuition

Because this problem is fundamentally about locating two fixed strings in order, Java’s built-in `indexOf` is a natural fit.

Split:

```text
p = prefix + '*' + suffix
```

Then:

1. locate an occurrence of `prefix`
2. from just after that occurrence, locate an occurrence of `suffix`

Repeat for all possible prefix occurrences.

This keeps the code very compact.

---

## Algorithm

1. Split pattern into `prefix` and `suffix`
2. Find first occurrence of `prefix` in `s`
3. For each such occurrence:
   - search `suffix` starting from `prefixEnd`
   - if found, return `true`
   - otherwise move to next occurrence of `prefix`
4. Return `false` if none work

---

## Java Code

```java
class Solution {
    public boolean hasMatch(String s, String p) {
        int star = p.indexOf('*');
        String prefix = p.substring(0, star);
        String suffix = p.substring(star + 1);

        int start = s.indexOf(prefix);

        while (start != -1) {
            int afterPrefix = start + prefix.length();

            if (suffix.isEmpty() || s.indexOf(suffix, afterPrefix) != -1) {
                return true;
            }

            start = s.indexOf(prefix, start + 1);
        }

        return false;
    }
}
```

---

## Why This Works

If `prefix` occurs at position `start`, and `suffix` occurs at or after:

```text
start + prefix.length()
```

then the substring from `start` through the end of that suffix is a valid match.

The `'*'` simply expands to whatever lies between them.

---

## Complexity Analysis

### Time Complexity

Because `indexOf` is called repeatedly, the worst-case bound is still around:

```text
O(n^2)
```

for these small constraints.

### Space Complexity

```text
O(1)
```

---

## Verdict

This is the shortest and arguably best solution for this specific problem size.

---

# Approach 4: Two-Pointer Check on Every Start Position

## Intuition

Another way is to try every starting index `i` where the substring could begin.

If `prefix` matches there, then we only need to know whether `suffix` can appear later.

This leads to a direct pointer-based formulation without explicit substring creation.

This is close to Approach 2, but framed more operationally.

---

## Java Code

```java
class Solution {
    public boolean hasMatch(String s, String p) {
        int star = p.indexOf('*');
        String prefix = p.substring(0, star);
        String suffix = p.substring(star + 1);

        for (int i = 0; i < s.length(); i++) {
            if (!matchesAt(s, prefix, i)) {
                continue;
            }

            int jStart = i + prefix.length();

            if (suffix.isEmpty()) {
                return true;
            }

            for (int j = jStart; j < s.length(); j++) {
                if (matchesAt(s, suffix, j)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matchesAt(String s, String part, int start) {
        if (start + part.length() > s.length()) {
            return false;
        }

        for (int k = 0; k < part.length(); k++) {
            if (s.charAt(start + k) != part.charAt(k)) {
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

Worst case:

```text
O(n^2)
```

### Space Complexity

```text
O(1)
```

---

## Verdict

Clear and explicit, though slightly longer than using `indexOf`.

---

# Edge Cases

## 1. Pattern starts with `'*'`

Example:

```text
p = "*abc"
```

Then:

- `prefix = ""`
- `suffix = "abc"`

We only need `"abc"` to appear somewhere in `s`.

---

## 2. Pattern ends with `'*'`

Example:

```text
p = "abc*"
```

Then:

- `prefix = "abc"`
- `suffix = ""`

We only need `"abc"` to appear somewhere in `s`.

---

## 3. Pattern is just `'*'` with surrounding empties

This cannot happen here because `p.length >= 1` and exactly one `'*'` exists, but if `p = "*"`, then any substring would match. Under the current constraints, it is still safe to think of it as trivially `true`.

---

## 4. Prefix and suffix can touch

The `'*'` may represent an empty string.

So if:

```text
prefix = "ab"
suffix = "cd"
```

then `"abcd"` is a valid match.

That is why suffix search begins at:

```text
prefixEnd
```

not strictly after it.

---

# Why a Greedy Order Check Is Enough

Because the pattern has only one wildcard, matching reduces to:

```text
prefix ... suffix
```

with the only condition being that `prefix` occurs before `suffix` in the same substring.

There is no need for backtracking or DP.

Once a valid `prefix` occurrence is fixed, any later valid `suffix` occurrence completes a match.

So the structure is much simpler than general wildcard matching.

---

# Common Mistakes

## 1. Requiring the whole string `s` to match

The problem asks whether the pattern can be made a **substring** of `s`, not whether it matches all of `s`.

---

## 2. Forgetting that `'*'` may be empty

You must allow zero characters between prefix and suffix.

---

## 3. Searching for suffix before the prefix ends

The suffix must start at or after the end of the prefix within the chosen substring.

---

## 4. Treating `'*'` like regex matching with complex behavior

This is much simpler than full wildcard matching because there is exactly one `'*'`.

---

# Final Recommended Solution

Use the split-and-search approach with `indexOf`.

It is the cleanest for this problem.

---

## Clean Final Java Solution

```java
class Solution {
    public boolean hasMatch(String s, String p) {
        int star = p.indexOf('*');
        String prefix = p.substring(0, star);
        String suffix = p.substring(star + 1);

        int start = s.indexOf(prefix);

        while (start != -1) {
            int afterPrefix = start + prefix.length();

            if (suffix.isEmpty() || s.indexOf(suffix, afterPrefix) != -1) {
                return true;
            }

            start = s.indexOf(prefix, start + 1);
        }

        return false;
    }
}
```

---

# Complexity Summary

## Brute Force All Substrings

- Time: `O(n^3)` with substring construction overhead
- Space: extra temporary substring space

## Prefix-then-suffix scan

- Time: `O(n^2)`
- Space: `O(1)`

## `indexOf`-based search

- Time: `O(n^2)` in the worst case
- Space: `O(1)`

---

# Interview Summary

Because the pattern contains exactly one `'*'`, it can be split into:

- a fixed prefix
- a fixed suffix

A substring matches if the prefix appears first and the suffix appears later in the same substring, with zero or more characters in between.

That reduces the whole problem to a simple ordered substring search.
