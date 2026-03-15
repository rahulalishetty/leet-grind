# 3008. Find Beautiful Indices in the Given Array II

## Problem Statement

You are given:

- a 0-indexed string `s`
- a string `a`
- a string `b`
- an integer `k`

An index `i` is **beautiful** if:

1. `s[i..i + a.length() - 1] == a`
2. there exists an index `j` such that:
   - `s[j..j + b.length() - 1] == b`
   - `|j - i| <= k`

Return all beautiful indices in sorted order.

---

## Example 1

```text
Input:
s = "isawsquirrelnearmysquirrelhouseohmy"
a = "my"
b = "squirrel"
k = 15

Output:
[16, 33]
```

Explanation:

- `"my"` occurs at index `16`, and `"squirrel"` occurs at index `4`, with `|16 - 4| = 12 <= 15`
- `"my"` occurs at index `33`, and `"squirrel"` occurs at index `18`, with `|33 - 18| = 15 <= 15`

---

## Example 2

```text
Input:
s = "abcd"
a = "a"
b = "a"
k = 4

Output:
[0]
```

Explanation:

- `"a"` occurs at index `0`
- `"a"` also occurs at index `0`
- `|0 - 0| = 0 <= 4`

---

## Constraints

- `1 <= k <= s.length <= 5 * 10^5`
- `1 <= a.length, b.length <= 5 * 10^5`
- `s`, `a`, and `b` contain only lowercase English letters

---

# Core Observation

This problem is structurally the same as Problem I, but the constraints are much larger.

That changes one important thing:

- in Problem I, direct substring checking was acceptable because `a` and `b` were tiny
- here, `a.length()` and `b.length()` can each be as large as `5 * 10^5`

So naive pattern matching is no longer viable.

The correct high-level plan is still:

1. find all occurrence indices of `a` in `s`
2. find all occurrence indices of `b` in `s`
3. for each occurrence of `a`, check whether there exists some occurrence of `b` within distance `k`

But step 1 and step 2 now require **linear-time string matching**.

That naturally suggests:

- **KMP**
- **Z-algorithm**
- optionally **rolling hash** with caution

And for step 3, since occurrence lists are sorted, use:

- **binary search**, or
- **two pointers**

---

# Approach 1: KMP + Binary Search

## Intuition

KMP lets us find all occurrences of a pattern in a text in linear time.

So we can:

- run KMP to get all indices where `a` occurs
- run KMP to get all indices where `b` occurs

Both result lists are sorted because we scan the text left to right.

Then for every `a` occurrence `i`, we need to know whether there exists some `b` occurrence in:

```text
[i - k, i + k]
```

That can be checked using binary search on the sorted `bPositions` list.

---

## Why KMP Fits Well

Naively checking all alignments of a long pattern inside a long text can take:

```text
O(n * m)
```

But KMP finds all occurrences in:

```text
O(n + m)
```

by precomputing the LPS array and avoiding redundant comparisons.

That is the right scale for this problem.

---

## Algorithm

1. Use KMP to find all start indices of `a` in `s`
2. Use KMP to find all start indices of `b` in `s`
3. For each index `i` in `aPositions`:
   - binary search `bPositions` for the first position `>= i - k`
   - if that position exists and is `<= i + k`, then `i` is beautiful
4. Return the collected indices

---

## Java Code

```java
import java.util.*;

class Solution {
    public List<Integer> beautifulIndices(String s, String a, String b, int k) {
        List<Integer> aPositions = kmpSearch(s, a);
        List<Integer> bPositions = kmpSearch(s, b);
        List<Integer> answer = new ArrayList<>();

        for (int i : aPositions) {
            int idx = lowerBound(bPositions, i - k);
            if (idx < bPositions.size() && bPositions.get(idx) <= i + k) {
                answer.add(i);
            }
        }

        return answer;
    }

    private List<Integer> kmpSearch(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        int[] lps = buildLPS(pattern);

        int i = 0; // index in text
        int j = 0; // index in pattern

        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;

                if (j == pattern.length()) {
                    result.add(i - j);
                    j = lps[j - 1];
                }
            } else {
                if (j > 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return result;
    }

    private int[] buildLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len > 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }

    private int lowerBound(List<Integer> arr, int target) {
        int left = 0, right = arr.size();

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (arr.get(mid) < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }
}
```

---

## Complexity Analysis

Let:

- `n = s.length()`
- `m = a.length()`
- `t = b.length()`
- `A = number of occurrences of a`
- `B = number of occurrences of b`

### Time Complexity

KMP for `a`:

```text
O(n + m)
```

KMP for `b`:

```text
O(n + t)
```

Binary search for each `a` occurrence:

```text
O(A log B)
```

Total:

```text
O(n + m + t + A log B)
```

In the worst case, `A` and `B` can be `O(n)`, so worst-case summary is:

```text
O(n log n)
```

### Space Complexity

```text
O(m + t + A + B)
```

---

## Verdict

This is already an excellent and fully acceptable solution.

---

# Approach 2: KMP + Two Pointers

## Intuition

We can improve the second phase from `O(A log B)` to `O(A + B)`.

Since both occurrence lists are sorted, for each `a` occurrence `i`, we only need to know whether the first `b` occurrence not smaller than `i - k` is also at most `i + k`.

A single pointer over `bPositions` can maintain this efficiently.

---

## Why Two Pointers Work

Suppose we process `aPositions` in sorted order.

For each `i`, any `b` position less than:

```text
i - k
```

can never help this `i` or any later `a` position, because future lower bounds only increase.

So the pointer into `bPositions` only moves forward.

That makes the whole range-check step linear.

---

## Algorithm

1. Find all occurrences of `a` using KMP
2. Find all occurrences of `b` using KMP
3. Maintain pointer `j = 0` on `bPositions`
4. For each `i` in `aPositions`:
   - advance `j` while `bPositions[j] < i - k`
   - if `j < size` and `bPositions[j] <= i + k`, then `i` is beautiful

---

## Java Code

```java
import java.util.*;

class Solution {
    public List<Integer> beautifulIndices(String s, String a, String b, int k) {
        List<Integer> aPositions = kmpSearch(s, a);
        List<Integer> bPositions = kmpSearch(s, b);
        List<Integer> answer = new ArrayList<>();

        int j = 0;

        for (int i : aPositions) {
            while (j < bPositions.size() && bPositions.get(j) < i - k) {
                j++;
            }

            if (j < bPositions.size() && bPositions.get(j) <= i + k) {
                answer.add(i);
            }
        }

        return answer;
    }

    private List<Integer> kmpSearch(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        int[] lps = buildLPS(pattern);

        int i = 0, j = 0;
        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;

                if (j == pattern.length()) {
                    result.add(i - j);
                    j = lps[j - 1];
                }
            } else {
                if (j > 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return result;
    }

    private int[] buildLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len > 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }
}
```

---

## Complexity Analysis

### Time Complexity

KMP occurrence finding:

```text
O(n + m + n + t) = O(n + m + t)
```

Two-pointer sweep:

```text
O(A + B)
```

Total:

```text
O(n + m + t + A + B)
```

Worst case, this is:

```text
O(n + m + t)
```

because `A` and `B` are each at most `n`.

### Space Complexity

```text
O(m + t + A + B)
```

---

## Verdict

This is probably the best practical solution:

- exact
- linear
- robust
- no probabilistic collisions

---

# Approach 3: Z-Algorithm + Binary Search

## Intuition

Instead of KMP, we can find all occurrences of a pattern using the Z-algorithm.

To find occurrences of pattern `p` inside text `s`, build:

```text
p + "#" + s
```

Then compute the Z-array.

Whenever the Z value at a text position is at least `p.length()`, the pattern occurs there.

We can do this separately for `a` and `b`, then use binary search or two pointers as before.

---

## Why Z-Algorithm Fits

Z-array gives:

```text
z[i] = longest substring starting at i that matches the prefix
```

So if the prefix is the pattern plus delimiter, a full pattern match appears as a Z value at least equal to the pattern length.

This is another exact linear-time string-matching technique.

---

## Java Code

```java
import java.util.*;

class Solution {
    public List<Integer> beautifulIndices(String s, String a, String b, int k) {
        List<Integer> aPositions = findOccurrencesWithZ(s, a);
        List<Integer> bPositions = findOccurrencesWithZ(s, b);
        List<Integer> answer = new ArrayList<>();

        for (int i : aPositions) {
            int idx = lowerBound(bPositions, i - k);
            if (idx < bPositions.size() && bPositions.get(idx) <= i + k) {
                answer.add(i);
            }
        }

        return answer;
    }

    private List<Integer> findOccurrencesWithZ(String text, String pattern) {
        String combined = pattern + "#" + text;
        int[] z = buildZ(combined);
        List<Integer> positions = new ArrayList<>();
        int m = pattern.length();

        for (int i = m + 1; i < combined.length(); i++) {
            if (z[i] >= m) {
                positions.add(i - m - 1);
            }
        }

        return positions;
    }

    private int[] buildZ(String s) {
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

        return z;
    }

    private int lowerBound(List<Integer> arr, int target) {
        int left = 0, right = arr.size();

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (arr.get(mid) < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Finding `a` matches:

```text
O(n + m)
```

Finding `b` matches:

```text
O(n + t)
```

Binary search checks:

```text
O(A log B)
```

Total:

```text
O(n + m + t + A log B)
```

### Space Complexity

```text
O(n + m + t + A + B)
```

because the combined strings and Z arrays are linear in size.

---

## Verdict

Very strong alternative to KMP.

---

# Approach 4: Z-Algorithm + Two Pointers

## Intuition

This is the same as Approach 3, but replaces binary search with the linear two-pointer sweep.

That gives full linear behavior end-to-end.

---

## Java Code

```java
import java.util.*;

class Solution {
    public List<Integer> beautifulIndices(String s, String a, String b, int k) {
        List<Integer> aPositions = findOccurrencesWithZ(s, a);
        List<Integer> bPositions = findOccurrencesWithZ(s, b);
        List<Integer> answer = new ArrayList<>();

        int j = 0;
        for (int i : aPositions) {
            while (j < bPositions.size() && bPositions.get(j) < i - k) {
                j++;
            }

            if (j < bPositions.size() && bPositions.get(j) <= i + k) {
                answer.add(i);
            }
        }

        return answer;
    }

    private List<Integer> findOccurrencesWithZ(String text, String pattern) {
        String combined = pattern + "#" + text;
        int[] z = buildZ(combined);
        List<Integer> positions = new ArrayList<>();
        int m = pattern.length();

        for (int i = m + 1; i < combined.length(); i++) {
            if (z[i] >= m) {
                positions.add(i - m - 1);
            }
        }

        return positions;
    }

    private int[] buildZ(String s) {
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

        return z;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n + m + t + A + B)
```

which is linear overall.

### Space Complexity

```text
O(n + m + t + A + B)
```

---

# Approach 5: Rolling Hash + Binary Search / Two Pointers

## Intuition

A tempting alternative is rolling hash:

- compute prefix hashes of `s`
- compute hash of `a` and `b`
- scan all possible start positions and compare hashes
- collect matches
- then use binary search or two pointers

This can also be efficient.

However, there is a catch:

- rolling hash is usually probabilistic unless carefully double-hashed
- the problem can be solved exactly with KMP or Z
- so rolling hash is not the most principled choice here

Still, it is useful to understand.

---

## Sketch

1. Precompute powers and prefix hashes for `s`
2. Compute hash of pattern `a`
3. For each possible start index, check whether substring hash equals `hash(a)`
4. Repeat for `b`
5. Check the distance condition over the resulting occurrence lists

---

## Why This Is Not the Best Recommendation

Even with a large modulus, collisions remain possible in principle.

For interview-quality reasoning, exact string algorithms are stronger here because:

- they are still linear
- they do not rely on probability
- they fit the constraints perfectly

So rolling hash is acceptable in some contexts, but not the first recommendation.

---

# Comparing the Best Approaches

## KMP + Two Pointers

### Strengths

- exact
- linear
- memory efficient
- standard interview tool

### Weakness

- LPS logic can feel less intuitive at first

---

## Z-Algorithm + Two Pointers

### Strengths

- also exact and linear
- often feels very elegant for occurrence finding

### Weakness

- requires building `pattern + "#" + text`, which some people find slightly less direct

---

## Binary Search vs Two Pointers

### Binary Search

- easy to reason about
- total `O(A log B)`

### Two Pointers

- exploits sorted order more fully
- total `O(A + B)`
- best asymptotically

So once you already have sorted occurrence lists, two pointers is the strongest finishing step.

---

# Final Recommended Solution

Use:

## KMP to find occurrences

and

## two pointers to validate distance

This is exact, scalable, and linear.

---

## Clean Final Java Solution

```java
import java.util.*;

class Solution {
    public List<Integer> beautifulIndices(String s, String a, String b, int k) {
        List<Integer> aPositions = kmpSearch(s, a);
        List<Integer> bPositions = kmpSearch(s, b);
        List<Integer> answer = new ArrayList<>();

        int j = 0;

        for (int i : aPositions) {
            while (j < bPositions.size() && bPositions.get(j) < i - k) {
                j++;
            }

            if (j < bPositions.size() && bPositions.get(j) <= i + k) {
                answer.add(i);
            }
        }

        return answer;
    }

    private List<Integer> kmpSearch(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        int[] lps = buildLPS(pattern);

        int i = 0, j = 0;
        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;

                if (j == pattern.length()) {
                    result.add(i - j);
                    j = lps[j - 1];
                }
            } else {
                if (j > 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return result;
    }

    private int[] buildLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len > 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }
}
```

---

# Why the Two-Pointer Finishing Step Is Safe

Suppose we are processing `aPositions` in increasing order.

For current `i`, if some `bPositions[j]` is less than:

```text
i - k
```

then it is too far left to help `i`.

For any later `a` position `i' > i`, the new lower bound is:

```text
i' - k >= i - k
```

So that same `bPositions[j]` will still be too far left.

Therefore, once a `b` position becomes unusable, it stays unusable forever, so the pointer only needs to move forward.

That is the full reason the sweep is linear.

---

# Common Mistakes

## 1. Using naive substring matching

With these constraints, naive matching can become far too slow because `a` and `b` may each be huge.

---

## 2. Rebuilding substrings with `substring()`

Even if logically correct, repeatedly creating substrings can add overhead and may worsen performance substantially.

---

## 3. Scanning all `b` occurrences for every `a` occurrence

This can degrade to quadratic behavior.

Since occurrence lists are sorted, use binary search or two pointers.

---

## 4. Forgetting overlaps are allowed

Pattern occurrences can overlap, so the matching algorithm must detect all occurrences, not skip ahead too aggressively.

---

## 5. Using rolling hash without acknowledging collisions

That can be acceptable in practice, but if you present it as exact, that is not fully correct.

---

# Complexity Summary

## KMP + Binary Search

- Time: `O(n + m + t + A log B)`
- Space: `O(m + t + A + B)`

## KMP + Two Pointers

- Time: `O(n + m + t + A + B)`
- Space: `O(m + t + A + B)`

## Z-Algorithm + Binary Search

- Time: `O(n + m + t + A log B)`
- Space: `O(n + m + t + A + B)`

## Z-Algorithm + Two Pointers

- Time: `O(n + m + t + A + B)`
- Space: `O(n + m + t + A + B)`

## Rolling Hash + Two Pointers

- Time: near linear
- Space: linear
- Caveat: probabilistic due to collisions

---

# Interview Summary

This problem is fundamentally:

1. find all starts of `a`
2. find all starts of `b`
3. for each `a` start, check if some `b` start lies in `[i-k, i+k]`

Because both pattern lengths and string length can be very large, occurrence finding must be linear-time.

That naturally leads to:

- **KMP** or
- **Z-algorithm**

And because occurrence positions are sorted, the distance check is best done with **two pointers**.

So the strongest final answer is:

- **KMP + two pointers**
- or **Z + two pointers**

Both are exact and linear.
