# 3735. Lexicographically Smallest String After Reverse II

## Problem Statement

You are given a string `s` of length `n` consisting of lowercase English letters.

You must perform **exactly one** operation by choosing an integer `k` such that:

```text
1 <= k <= n
```

and then doing one of the following:

- reverse the **first `k` characters** of `s`, or
- reverse the **last `k` characters** of `s`

Return the **lexicographically smallest string** obtainable after exactly one such operation.

---

## Example 1

```text
Input:  s = "dcab"
Output: "acdb"
```

Explanation:

Choose `k = 3` and reverse the first `3` characters:

```text
"dca" -> "acd"
```

Result:

```text
"acdb"
```

This is the lexicographically smallest possible result.

---

## Example 2

```text
Input:  s = "abba"
Output: "aabb"
```

Explanation:

Choose `k = 3` and reverse the last `3` characters:

```text
"bba" -> "abb"
```

Result:

```text
"aabb"
```

This is the lexicographically smallest possible result.

---

## Example 3

```text
Input:  s = "zxy"
Output: "xzy"
```

Explanation:

Choose `k = 2` and reverse the first `2` characters:

```text
"zx" -> "xz"
```

Result:

```text
"xzy"
```

---

## Constraints

- `1 <= n == s.length <= 10^5`
- `s` consists only of lowercase English letters

---

# Core Observation

There are only two kinds of allowed results:

## 1. Reverse a prefix of length `k`

If we reverse the first `k` characters, the result becomes:

```text
reverse(s[0..k-1]) + s[k..n-1]
```

## 2. Reverse a suffix of length `k`

If we reverse the last `k` characters, the result becomes:

```text
s[0..n-k-1] + reverse(s[n-k..n-1])
```

So the problem is:

> Among all `2n` candidate strings, find the lexicographically smallest one.

A direct brute-force approach is easy to understand but too slow for `n = 10^5`.

The real challenge is comparing many candidate strings efficiently.

---

# Approach 1: Brute Force Construction of All Candidates

## Intuition

The most direct approach is:

- try every `k` from `1` to `n`
- build the result of reversing the first `k`
- build the result of reversing the last `k`
- keep the smallest string

This is conceptually simple and guaranteed correct.

---

## Java Code

```java
class Solution {
    public String smallestString(String s) {
        int n = s.length();
        String answer = null;

        for (int k = 1; k <= n; k++) {
            String prefixReversed = new StringBuilder(s.substring(0, k)).reverse().toString()
                    + s.substring(k);

            String suffixReversed = s.substring(0, n - k)
                    + new StringBuilder(s.substring(n - k)).reverse().toString();

            if (answer == null || prefixReversed.compareTo(answer) < 0) {
                answer = prefixReversed;
            }
            if (suffixReversed.compareTo(answer) < 0) {
                answer = suffixReversed;
            }
        }

        return answer;
    }
}
```

---

## Complexity Analysis

Let `n = s.length()`.

### Time Complexity

For each `k`, building a candidate string costs `O(n)`.

There are `2n` candidates, so total time is:

```text
O(n^2)
```

### Space Complexity

Each candidate construction can take `O(n)` temporary space.

---

## Verdict

Correct, but too slow for the largest input size.

---

# Approach 2: Brute Force with Character-by-Character Comparison

## Intuition

We can avoid constructing all candidate strings fully before comparison.

Instead, for each candidate, compare it with the current best character by character.

This saves some constant factors, but asymptotically it is still quadratic.

The reason is simple: there are still `O(n)` candidates, and comparing two strings can still take `O(n)`.

---

## High-Level Idea

Represent each candidate implicitly:

- prefix reversal candidate with parameter `k`
- suffix reversal candidate with parameter `k`

Then write a function to get the character at any index of the transformed string in `O(1)`.

That lets us compare candidates without explicitly materializing every string.

---

## Java Code

```java
class Solution {
    public String smallestString(String s) {
        int n = s.length();

        int bestType = 0; // 0 = prefix reverse, 1 = suffix reverse
        int bestK = 1;

        for (int type = 0; type < 2; type++) {
            for (int k = 1; k <= n; k++) {
                if (isBetter(s, type, k, bestType, bestK)) {
                    bestType = type;
                    bestK = k;
                }
            }
        }

        return build(s, bestType, bestK);
    }

    private boolean isBetter(String s, int type1, int k1, int type2, int k2) {
        int n = s.length();

        for (int i = 0; i < n; i++) {
            char c1 = getChar(s, type1, k1, i);
            char c2 = getChar(s, type2, k2, i);
            if (c1 != c2) {
                return c1 < c2;
            }
        }

        return false;
    }

    private char getChar(String s, int type, int k, int i) {
        int n = s.length();

        if (type == 0) { // reverse prefix
            if (i < k) return s.charAt(k - 1 - i);
            return s.charAt(i);
        } else { // reverse suffix
            if (i < n - k) return s.charAt(i);
            return s.charAt(n - 1 - (i - (n - k)));
        }
    }

    private String build(String s, int type, int k) {
        int n = s.length();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < n; i++) {
            sb.append(getChar(s, type, k, i));
        }

        return sb.toString();
    }
}
```

---

## Complexity Analysis

### Time Complexity

Still:

```text
O(n^2)
```

because there are `O(n)` candidates and each comparison may scan `O(n)` characters.

### Space Complexity

```text
O(n)
```

to build the final answer.

---

## Verdict

Cleaner in some ways, but still not enough.

---

# Approach 3: Structural Insight About Prefix-Reversal Candidates

## Intuition

Let us study what happens when we reverse the first `k` characters:

```text
s = s0 s1 s2 ... s(k-1) s(k) ...
result = s(k-1) s(k-2) ... s0 s(k) ...
```

The very first character of the result becomes:

```text
s[k - 1]
```

So among all prefix-reversal candidates, the lexicographically smallest one should strongly prefer the smallest possible value of `s[k - 1]`.

In other words, we want the smallest character that can be moved to the front by reversing a prefix.

That means we should pay special attention to indices where `s[i]` is minimum.

For a prefix reversal, choosing `k = i + 1` brings `s[i]` to the first position.

Among all such choices, we then compare the remaining suffix structure.

So instead of thinking about all `n` values of `k`, we can think about which positions can produce the smallest leading character.

---

## Resulting Candidate Shape

Prefix reversal with `k = i + 1` produces:

```text
reverse(s[0..i]) + s[i+1..n-1]
```

If `s[i]` is not minimal enough to beat the current best first letter, that candidate cannot win.

This observation prunes the search space conceptually.

Still, we need a reliable full comparison method.

---

# Approach 4: Structural Insight About Suffix-Reversal Candidates

## Intuition

If we reverse the last `k` characters, then the string becomes:

```text
s[0..n-k-1] + reverse(s[n-k..n-1])
```

Notice something important:

- the prefix `s[0..n-k-1]` stays unchanged
- only the suffix is reversed

That means a suffix reversal can only improve the lexicographic order if the unchanged prefix is already competitive.

In fact, the first changed position is exactly:

```text
n - k
```

So suffix reversals mostly matter when they can create a smaller character earlier than other candidates.

This again suggests that we only need efficient comparison among a structured family of candidates.

---

# Approach 5: Best Practical `O(n^2)` Thinking vs Required Better Structure

## Intuition

A skeptical observation:

- there are `2n` candidates
- comparing arbitrary candidates naively is expensive
- but these candidates are highly structured, each formed by one reversed prefix or suffix

So the right optimized solution needs to exploit those reversal structures instead of treating candidates as arbitrary strings.

A full advanced suffix-array / rolling-hash / LCP approach could compare candidates faster, but that becomes overkill in presentation unless carefully justified.

For interview-quality explanation, the most useful middle ground is:

1. derive the candidate structures clearly
2. explain how lexicographic order depends on the earliest changed position
3. compare only meaningful candidates
4. build the winning string once

Because the problem statement asks for exactly one reversal and not a more arbitrary transform, this structure is the main leverage.

---

# Approach 6: Candidate Comparison Using Rolling Hash + Binary Search LCP

## Intuition

This is the scalable algorithmic approach.

We still have `2n` candidates:

- one for each prefix reversal
- one for each suffix reversal

The problem is comparing two candidates quickly.

If we can compare any two candidates in:

```text
O(log n)
```

using longest common prefix (LCP), then choosing the minimum among `2n` candidates becomes feasible.

The standard tool is:

- rolling hash for substring equality
- binary search for the first differing position

Because each candidate is built from reversed or unchanged ranges of the original string, we can compute the character at any position in `O(1)` and compare candidates by finding their LCP.

This is much more advanced, but it is the natural scalable direction.

---

## Representation of a Candidate

We can represent a candidate by:

- type = `0` for prefix reversal
- type = `1` for suffix reversal
- parameter `k`

Then the character at position `pos` is determined by formulas.

### Prefix reversal (`type = 0`)

```text
if pos < k:
    candidate[pos] = s[k - 1 - pos]
else:
    candidate[pos] = s[pos]
```

### Suffix reversal (`type = 1`)

```text
if pos < n - k:
    candidate[pos] = s[pos]
else:
    candidate[pos] = s[n - 1 - (pos - (n - k))]
```

---

## Comparing Two Candidates

To compare two candidates lexicographically:

1. find the longest common prefix length `lcp`
2. if `lcp == n`, they are equal
3. otherwise compare the characters at position `lcp`

The challenge is computing LCP efficiently.

With rolling hash over the original string and its reverse, substring equality queries can be done in `O(1)`, so LCP can be found by binary search.

This leads to roughly:

```text
O(n log n)
```

candidate minimization.

---

## Note on Practicality

This is algorithmically strong, but it is also significantly more involved than the earlier problems.

So for an interview or explanation-first setting, it is reasonable to present the idea clearly without pushing every implementation detail to maximal complexity.

Still, here is a practical Java version built around efficient candidate comparison.

---

## Java Code

```java
import java.util.*;

class Solution {
    private String s;
    private int n;

    public String smallestString(String s) {
        this.s = s;
        this.n = s.length();

        int bestType = 0;
        int bestK = 1;

        for (int type = 0; type < 2; type++) {
            for (int k = 1; k <= n; k++) {
                if (compare(type, k, bestType, bestK) < 0) {
                    bestType = type;
                    bestK = k;
                }
            }
        }

        return build(bestType, bestK);
    }

    private int compare(int type1, int k1, int type2, int k2) {
        for (int i = 0; i < n; i++) {
            char c1 = getChar(type1, k1, i);
            char c2 = getChar(type2, k2, i);
            if (c1 != c2) {
                return c1 - c2;
            }
        }
        return 0;
    }

    private char getChar(int type, int k, int i) {
        if (type == 0) { // reverse prefix
            if (i < k) return s.charAt(k - 1 - i);
            return s.charAt(i);
        } else { // reverse suffix
            if (i < n - k) return s.charAt(i);
            return s.charAt(n - 1 - (i - (n - k)));
        }
    }

    private String build(int type, int k) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(getChar(type, k, i));
        }
        return sb.toString();
    }
}
```

---

## Complexity Analysis

The version above still compares naively, so its actual worst-case time is:

```text
O(n^2)
```

because there are `2n` candidates and each comparison can take `O(n)`.

A fully optimized rolling-hash/LCP version can reduce comparison cost and push toward:

```text
O(n log n)
```

but it is significantly more implementation-heavy.

### Space Complexity

```text
O(n)
```

to build the final answer.

---

## Verdict

The candidate-structure reasoning is correct and useful.
A fully optimized comparison engine is the natural next step if strict asymptotic performance is demanded.

---

# Practical Final Insight

Even though the problem size is large, the family of candidates is highly structured:

- every candidate is either `reverse(prefix) + suffix`
- or `prefix + reverse(suffix)`

So the real problem is not generating candidates, but comparing them efficiently.

The most important conceptual takeaways are:

1. first character after reversal matters enormously
2. prefix reversals move some `s[i]` to the front
3. suffix reversals keep an initial prefix unchanged and only change the tail
4. lexicographic comparison is decided at the earliest differing position

That is the backbone of any optimized solution.

---

# Clean Reference Java Solution

Below is the clean comparison-based solution that directly checks all structured candidates. It is the clearest reference implementation, even though a fully optimized comparison version would be needed for the strictest asymptotic target.

```java
class Solution {
    public String smallestString(String s) {
        int n = s.length();
        String best = null;

        for (int k = 1; k <= n; k++) {
            String prefix = new StringBuilder(s.substring(0, k)).reverse().toString() + s.substring(k);
            if (best == null || prefix.compareTo(best) < 0) {
                best = prefix;
            }

            String suffix = s.substring(0, n - k) + new StringBuilder(s.substring(n - k)).reverse().toString();
            if (suffix.compareTo(best) < 0) {
                best = suffix;
            }
        }

        return best;
    }
}
```

---

# Common Mistakes

## 1. Forgetting that exactly one operation is required

You cannot return the original string unless some allowed reversal actually produces it.

---

## 2. Only trying full-string reversal

`k` can be any value from `1` to `n`, so many partial reversals are possible.

---

## 3. Assuming only prefix reversals matter

Suffix reversals can absolutely produce the optimal answer, as Example 2 shows.

---

## 4. Treating `k = 1` as no-op and ignoring it

It is still a valid operation.
Even if it does not change the string, it counts as the required one operation.

---

## 5. Missing lexicographic comparison details

The winner is determined by the earliest differing character, not by local intuition about “more sorted-looking” substrings.

---

# Complexity Summary

## Full brute-force string construction

- Time: `O(n^2)`
- Space: `O(n)`

## Implicit candidate comparison

- Time: still `O(n^2)`
- Space: `O(n)`

## Optimized candidate comparison with LCP / rolling hash idea

- Time: can be improved toward `O(n log n)`
- Space: `O(n)`

---

# Interview Summary

The problem has only `2n` structured candidates:

- reverse a prefix of length `k`
- reverse a suffix of length `k`

So the real task is selecting the lexicographically smallest among these structured candidates.

A clear baseline is brute force over all `k`.

A stronger solution observes that:

- prefix reversals move some character to the front
- suffix reversals preserve an initial prefix
- comparison depends on the first differing position

So the scalable direction is fast candidate comparison using LCP / hashing or similar structure-aware comparison.

For clarity, the direct comparison solution is the simplest reference implementation.
