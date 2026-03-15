# 3455. Shortest Matching Substring

## Problem Statement

You are given:

- a string `s`
- a pattern string `p`

The pattern contains **exactly two `'*'` characters**.

Each `'*'` can match **any sequence of zero or more characters**.

Return the **length of the shortest substring of `s`** that matches `p`.

If no such substring exists, return `-1`.

The empty substring is considered valid.

---

## Example 1

```text
Input:
s = "abaacbaecebce"
p = "ba*c*ce"

Output:
8
```

Explanation:

The shortest matching substring is:

```text
"baecebce"
```

---

## Example 2

```text
Input:
s = "baccbaadbc"
p = "cc*baa*adb"

Output:
-1
```

No substring matches the pattern.

---

## Example 3

```text
Input:
s = "a"
p = "**"

Output:
0
```

Because both stars can match empty, the empty substring is valid.

---

## Example 4

```text
Input:
s = "madlogic"
p = "*adlogi*"

Output:
6
```

The shortest matching substring is:

```text
"adlogi"
```

---

## Constraints

- `1 <= s.length <= 10^5`
- `2 <= p.length <= 10^5`
- `s` contains only lowercase English letters
- `p` contains only lowercase English letters and exactly two `'*'`

---

# Core Insight

Since `p` contains exactly two `'*'`, we can split it into three fixed parts:

```text
p = A * B * C
```

where:

- `A` = part before the first `*`
- `B` = part between the two `*`
- `C` = part after the second `*`

A substring of `s` matches `p` iff it can be written as:

```text
A + anything + B + anything + C
```

with both “anything” parts allowed to be empty.

So the problem becomes:

> Find occurrences of `A`, `B`, and `C` in order, and minimize the total span from the start of `A` to the end of `C`.

This reformulation is the whole problem.

---

# Important Edge Cases

## 1. One or more parts may be empty

Examples:

- `p = "**"` gives `A = ""`, `B = ""`, `C = ""`
- `p = "*adlogi*"` gives `A = ""`, `B = "adlogi"`, `C = ""`

Empty parts are valid and should be handled carefully.

## 2. The stars may match empty strings

So `A`, `B`, and `C` may appear consecutively with no gap between them.

## 3. We are minimizing substring length, not finding any match

So greedily finding the first full match is not enough unless the data structure supports shortest-span selection correctly.

---

# Approach 1: Brute Force Over All Substrings

## Intuition

The most direct idea is:

- enumerate every substring of `s`
- check whether it matches the wildcard pattern
- keep the minimum length

This is clearly too slow for `|s| = 10^5`, but it helps explain the matching structure.

---

## Matching Rule for a Fixed Substring

If a substring matches `A * B * C`, then inside that substring:

1. `A` must occur at the start
2. after that, `B` must occur somewhere later
3. after that, `C` must occur somewhere later
4. all gaps may be empty

---

## Why It Fails

There are `O(n^2)` substrings, and checking each one is expensive.

So this approach is not viable.

---

# Approach 2: Try Every Occurrence of `A` and Search Forward

## Intuition

A more structured brute-force idea is:

- for each occurrence of `A`
- find the first feasible occurrence of `B` after it
- then the first feasible occurrence of `C` after that
- compute the resulting substring length
- minimize over all starting occurrences

This is much better than checking all substrings.

However, if we search linearly from every occurrence, it can still degrade badly.

---

## Why It Still Needs Optimization

Suppose:

- `A` occurs many times
- `B` occurs many times
- `C` occurs many times

Then repeatedly scanning forward can become quadratic.

So we need fast “next occurrence at or after position x” queries.

That leads to preprocessing occurrence lists.

---

# Approach 3: Occurrence Lists + Binary Search

## Intuition

This is the cleanest exact solution.

### Step 1: Split the pattern

Let:

```text
p = A * B * C
```

### Step 2: Find all occurrence start positions of each part in `s`

We compute:

- all starts of `A`
- all starts of `B`
- all starts of `C`

### Step 3: For each valid starting occurrence of `A`

If `A` starts at position `i`, then:

- `B` must start at some position `j >= i + |A|`
- `C` must start at some position `k >= j + |B|`

To minimize the total substring length for this fixed `i`, we should choose:

- the earliest valid `j`
- then the earliest valid `k`

because any later choice only makes the substring longer.

So for each `i`, we do:

- binary search in `B_occurrences` for first start `>= i + |A|`
- binary search in `C_occurrences` for first start `>= j + |B|`

Then the candidate substring length is:

```text
(k + |C|) - i
```

Take the minimum over all `i`.

---

## Handling Empty Parts

If a part is empty:

- its occurrence list is conceptually every position
- but we do not need to materialize that

Instead:

- if `A` is empty, the substring may start anywhere, so candidate starts are simply positions `0..n`
- if `B` is empty, then `j = i + |A|`
- if `C` is empty, then the substring may end immediately after `B`, so candidate end is `j + |B|`

This avoids exploding the occurrence lists.

---

## How to Find All Occurrences Efficiently

We can use KMP or Z-algorithm to find all occurrences of a pattern in `s` in linear time.

Because there are only three parts, this is efficient.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int shortestMatchingSubstring(String s, String p) {
        int first = p.indexOf('*');
        int second = p.indexOf('*', first + 1);

        String A = p.substring(0, first);
        String B = p.substring(first + 1, second);
        String C = p.substring(second + 1);

        int n = s.length();

        // Special case: pattern is "**"
        if (A.isEmpty() && B.isEmpty() && C.isEmpty()) {
            return 0;
        }

        List<Integer> occA = A.isEmpty() ? null : findOccurrences(s, A);
        List<Integer> occB = B.isEmpty() ? null : findOccurrences(s, B);
        List<Integer> occC = C.isEmpty() ? null : findOccurrences(s, C);

        int ans = Integer.MAX_VALUE;

        // possible starts
        if (!A.isEmpty()) {
            for (int i : occA) {
                int startAfterA = i + A.length();

                int j;
                if (B.isEmpty()) {
                    j = startAfterA;
                } else {
                    int idxB = lowerBound(occB, startAfterA);
                    if (idxB == occB.size()) continue;
                    j = occB.get(idxB);
                }

                int k;
                if (C.isEmpty()) {
                    k = j + B.length(); // end position directly after B
                    ans = Math.min(ans, k - i);
                } else {
                    int startAfterB = j + B.length();
                    int idxC = lowerBound(occC, startAfterB);
                    if (idxC == occC.size()) continue;
                    k = occC.get(idxC);
                    ans = Math.min(ans, (k + C.length()) - i);
                }
            }
        } else {
            // A is empty: starting position can be anywhere
            for (int i = 0; i <= n; i++) {
                int j;
                if (B.isEmpty()) {
                    j = i;
                } else {
                    int idxB = lowerBound(occB, i);
                    if (idxB == occB.size()) break;
                    j = occB.get(idxB);
                }

                if (C.isEmpty()) {
                    ans = Math.min(ans, j + B.length() - i);
                } else {
                    int startAfterB = j + B.length();
                    int idxC = lowerBound(occC, startAfterB);
                    if (idxC == occC.size()) continue;
                    int k = occC.get(idxC);
                    ans = Math.min(ans, (k + C.length()) - i);
                }
            }
        }

        return ans == Integer.MAX_VALUE ? -1 : ans;
    }

    private List<Integer> findOccurrences(String s, String pat) {
        List<Integer> result = new ArrayList<>();
        int[] lps = buildLPS(pat);

        int i = 0, j = 0;
        while (i < s.length()) {
            if (s.charAt(i) == pat.charAt(j)) {
                i++;
                j++;
                if (j == pat.length()) {
                    result.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }

        return result;
    }

    private int[] buildLPS(String p) {
        int[] lps = new int[p.length()];
        int len = 0, i = 1;

        while (i < p.length()) {
            if (p.charAt(i) == p.charAt(len)) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }

        return lps;
    }

    private int lowerBound(List<Integer> list, int target) {
        int lo = 0, hi = list.size();
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (list.get(mid) < target) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }
}
```

---

## Complexity Analysis

Let:

- `n = s.length()`
- `a = |A|`, `b = |B|`, `c = |C|`

### Time Complexity

Finding occurrences of each non-empty part using KMP:

```text
O(n + a) + O(n + b) + O(n + c)
```

which is:

```text
O(n + |p|)
```

Then for each start candidate we do up to two binary searches.

In the worst case the number of candidates is `O(n)`, so:

```text
O(n log n)
```

Total:

```text
O(n log n + |p|)
```

### Space Complexity

Occurrence lists plus KMP tables:

```text
O(n + |p|)
```

---

## Verdict

This is a strong and clean exact solution.

---

# Approach 4: DP on Next Occurrences

## Intuition

Another way to avoid binary search is to preprocess “next occurrence at or after position i” arrays.

For each part, define:

```text
nextA[i] = earliest occurrence of A starting at or after i
nextB[i] = earliest occurrence of B starting at or after i
nextC[i] = earliest occurrence of C starting at or after i
```

Then for each possible start position:

- choose `i`
- get `j = nextB[i + |A|]`
- get `k = nextC[j + |B|]`

This makes the final scan `O(n)` after preprocessing.

The main challenge is building those next arrays efficiently from the occurrence lists.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int shortestMatchingSubstring(String s, String p) {
        int first = p.indexOf('*');
        int second = p.indexOf('*', first + 1);

        String A = p.substring(0, first);
        String B = p.substring(first + 1, second);
        String C = p.substring(second + 1);

        int n = s.length();

        if (A.isEmpty() && B.isEmpty() && C.isEmpty()) {
            return 0;
        }

        int[] nextB = buildNextArray(s, B);
        int[] nextC = buildNextArray(s, C);

        int ans = Integer.MAX_VALUE;

        if (!A.isEmpty()) {
            List<Integer> occA = findOccurrences(s, A);
            for (int i : occA) {
                int posAfterA = i + A.length();

                int j = B.isEmpty() ? posAfterA : (posAfterA <= n ? nextB[posAfterA] : -1);
                if (j == -1) continue;

                if (C.isEmpty()) {
                    ans = Math.min(ans, j + B.length() - i);
                } else {
                    int posAfterB = j + B.length();
                    int k = posAfterB <= n ? nextC[posAfterB] : -1;
                    if (k == -1) continue;
                    ans = Math.min(ans, k + C.length() - i);
                }
            }
        } else {
            for (int i = 0; i <= n; i++) {
                int j = B.isEmpty() ? i : nextB[i];
                if (j == -1) continue;

                if (C.isEmpty()) {
                    ans = Math.min(ans, j + B.length() - i);
                } else {
                    int posAfterB = j + B.length();
                    int k = posAfterB <= n ? nextC[posAfterB] : -1;
                    if (k == -1) continue;
                    ans = Math.min(ans, k + C.length() - i);
                }
            }
        }

        return ans == Integer.MAX_VALUE ? -1 : ans;
    }

    private int[] buildNextArray(String s, String part) {
        int n = s.length();
        int[] next = new int[n + 1];
        Arrays.fill(next, -1);

        if (part.isEmpty()) {
            for (int i = 0; i <= n; i++) {
                next[i] = i;
            }
            return next;
        }

        List<Integer> occ = findOccurrences(s, part);
        int ptr = occ.size() - 1;

        for (int i = n; i >= 0; i--) {
            if (ptr >= 0 && occ.get(ptr) == i) {
                next[i] = i;
                ptr--;
            } else if (i < n) {
                next[i] = next[i + 1];
            }
        }

        return next;
    }

    private List<Integer> findOccurrences(String s, String pat) {
        List<Integer> result = new ArrayList<>();
        int[] lps = buildLPS(pat);

        int i = 0, j = 0;
        while (i < s.length()) {
            if (s.charAt(i) == pat.charAt(j)) {
                i++;
                j++;
                if (j == pat.length()) {
                    result.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }

        return result;
    }

    private int[] buildLPS(String p) {
        int[] lps = new int[p.length()];
        int len = 0, i = 1;

        while (i < p.length()) {
            if (p.charAt(i) == p.charAt(len)) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }

        return lps;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Occurrence finding is linear in `s` plus the part lengths.

Building next arrays is linear.

Final scan is linear.

Total:

```text
O(n + |p|)
```

### Space Complexity

```text
O(n + |p|)
```

---

## Verdict

This is even faster than the binary-search version and is an excellent exact solution.

---

# Why Greedy Earliest Choices Work for a Fixed Start

Suppose the substring must start at index `i`.

Then to minimize the total ending point, once `i` is fixed:

- we should choose the earliest possible `B`
- then the earliest possible `C`

Any later valid occurrence only increases or preserves the final length.

So for a fixed start, earliest compatible choices are optimal.

This is why lower-bound search or next-occurrence arrays work.

---

# Approach 5: Full Wildcard DP (Overkill)

## Intuition

One might think of general wildcard matching DP.

But here the pattern has exactly two `'*'`, which gives much stronger structure:

```text
A * B * C
```

General wildcard DP ignores that exploitable structure and would be far heavier than necessary.

So while possible in theory, it is not the right solution for this problem.

---

# Common Mistakes

## 1. Forgetting the empty substring case

If:

```text
p = "**"
```

then the answer is `0`.

---

## 2. Mishandling empty parts

For example:

- `A = ""`
- `B = ""`
- `C = "abc"`

These cases should not be special-cased incorrectly or by building huge occurrence lists of every position unless needed.

---

## 3. Searching for overlapping order incorrectly

The occurrences must satisfy:

```text
start(B) >= end(A)
start(C) >= end(B)
```

because the stars can absorb gaps, but the fixed pieces must appear in order.

---

## 4. Minimizing by earliest start only

The goal is the shortest substring length, not the earliest starting index.

So we must optimize the span.

---

# Final Recommended Solution

Use:

- split `p` into `A`, `B`, `C`
- compute occurrence positions with KMP
- either:
  - use occurrence lists + binary search, or
  - build next-occurrence arrays

The **next-occurrence array** version is the strongest exact solution.

---

## Clean Final Java Solution

```java
import java.util.*;

class Solution {
    public int shortestMatchingSubstring(String s, String p) {
        int first = p.indexOf('*');
        int second = p.indexOf('*', first + 1);

        String A = p.substring(0, first);
        String B = p.substring(first + 1, second);
        String C = p.substring(second + 1);

        int n = s.length();

        if (A.isEmpty() && B.isEmpty() && C.isEmpty()) {
            return 0;
        }

        int[] nextB = buildNextArray(s, B);
        int[] nextC = buildNextArray(s, C);

        int ans = Integer.MAX_VALUE;

        if (!A.isEmpty()) {
            List<Integer> occA = findOccurrences(s, A);
            for (int i : occA) {
                int afterA = i + A.length();
                int j = B.isEmpty() ? afterA : (afterA <= n ? nextB[afterA] : -1);
                if (j == -1) continue;

                if (C.isEmpty()) {
                    ans = Math.min(ans, j + B.length() - i);
                } else {
                    int afterB = j + B.length();
                    int k = afterB <= n ? nextC[afterB] : -1;
                    if (k == -1) continue;
                    ans = Math.min(ans, k + C.length() - i);
                }
            }
        } else {
            for (int i = 0; i <= n; i++) {
                int j = B.isEmpty() ? i : nextB[i];
                if (j == -1) continue;

                if (C.isEmpty()) {
                    ans = Math.min(ans, j + B.length() - i);
                } else {
                    int afterB = j + B.length();
                    int k = afterB <= n ? nextC[afterB] : -1;
                    if (k == -1) continue;
                    ans = Math.min(ans, k + C.length() - i);
                }
            }
        }

        return ans == Integer.MAX_VALUE ? -1 : ans;
    }

    private int[] buildNextArray(String s, String part) {
        int n = s.length();
        int[] next = new int[n + 1];
        Arrays.fill(next, -1);

        if (part.isEmpty()) {
            for (int i = 0; i <= n; i++) {
                next[i] = i;
            }
            return next;
        }

        List<Integer> occ = findOccurrences(s, part);
        boolean[] starts = new boolean[n + 1];
        for (int pos : occ) {
            starts[pos] = true;
        }

        next[n] = -1;
        for (int i = n - 1; i >= 0; i--) {
            next[i] = starts[i] ? i : next[i + 1];
        }

        return next;
    }

    private List<Integer> findOccurrences(String s, String pat) {
        List<Integer> result = new ArrayList<>();
        if (pat.isEmpty()) return result;

        int[] lps = buildLPS(pat);
        int i = 0, j = 0;

        while (i < s.length()) {
            if (s.charAt(i) == pat.charAt(j)) {
                i++;
                j++;
                if (j == pat.length()) {
                    result.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }

        return result;
    }

    private int[] buildLPS(String p) {
        int[] lps = new int[p.length()];
        int len = 0, i = 1;

        while (i < p.length()) {
            if (p.charAt(i) == p.charAt(len)) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }

        return lps;
    }
}
```

---

# Complexity Summary

## Brute Force

- Time: too slow, roughly quadratic or worse
- Space: small

## Occurrence Lists + Binary Search

- Time: `O(n log n + |p|)`
- Space: `O(n + |p|)`

## Next-Occurrence Arrays + KMP

- Time: `O(n + |p|)`
- Space: `O(n + |p|)`

---

# Interview Summary

Because the pattern has exactly two `'*'`, it splits cleanly into:

```text
A * B * C
```

A matching substring must contain `A`, then `B`, then `C` in order, with arbitrary gaps.

So the problem reduces to finding occurrences of these fixed parts and minimizing the span from the start of `A` to the end of `C`.

The best exact solution is to:

1. find occurrences of the fixed parts with KMP
2. preprocess next-occurrence information
3. greedily choose the earliest valid `B` and `C` for each start

That yields a linear-time solution.
