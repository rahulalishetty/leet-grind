# 3006. Find Beautiful Indices in the Given Array I

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

- At index `16`, `"my"` occurs and `"squirrel"` occurs at index `4`, with `|16 - 4| = 12 <= 15`
- At index `33`, `"my"` occurs and `"squirrel"` occurs at index `18`, with `|33 - 18| = 15 <= 15`

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

- At index `0`, `"a"` occurs
- There is also a `"a"` occurrence at index `0`
- `|0 - 0| = 0 <= 4`

---

## Constraints

- `1 <= k <= s.length <= 10^5`
- `1 <= a.length, b.length <= 10`
- `s`, `a`, and `b` contain only lowercase English letters

---

# Core Idea

The problem has two parts:

1. find all positions where `a` occurs in `s`
2. find all positions where `b` occurs in `s`

Then for each position `i` where `a` occurs, check whether there exists some position `j` where `b` occurs such that:

```text
|j - i| <= k
```

Equivalently:

```text
i - k <= j <= i + k
```

So once we know all occurrence indices of `a` and `b`, the rest is a range-query problem over sorted lists of positions.

---

# Approach 1: Brute Force Matching + Brute Force Beautiful Check

## Intuition

The most direct solution is:

- scan every index of `s`
- record where `a` occurs
- record where `b` occurs
- for each `a` occurrence, scan all `b` occurrences to see whether one is within distance `k`

This is simple and correct, but it does unnecessary work.

---

## Algorithm

1. Build list `aPositions`
2. Build list `bPositions`
3. For every `i` in `aPositions`:
   - iterate every `j` in `bPositions`
   - if `abs(i - j) <= k`, add `i` to answer

---

## Java Code

```java
import java.util.*;

class Solution {
    public List<Integer> beautifulIndices(String s, String a, String b, int k) {
        List<Integer> aPositions = new ArrayList<>();
        List<Integer> bPositions = new ArrayList<>();
        List<Integer> answer = new ArrayList<>();

        for (int i = 0; i + a.length() <= s.length(); i++) {
            if (matches(s, i, a)) {
                aPositions.add(i);
            }
        }

        for (int i = 0; i + b.length() <= s.length(); i++) {
            if (matches(s, i, b)) {
                bPositions.add(i);
            }
        }

        for (int i : aPositions) {
            for (int j : bPositions) {
                if (Math.abs(i - j) <= k) {
                    answer.add(i);
                    break;
                }
            }
        }

        return answer;
    }

    private boolean matches(String s, int start, String pattern) {
        for (int i = 0; i < pattern.length(); i++) {
            if (s.charAt(start + i) != pattern.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Complexity Analysis

Let:

- `n = s.length()`
- `A = number of occurrences of a`
- `B = number of occurrences of b`

### Time Complexity

Pattern matching cost:

```text
O(n * |a| + n * |b|)
```

Beautiful-index checking cost:

```text
O(A * B)
```

Worst case, `A` and `B` can both be `O(n)`, so total worst-case complexity becomes:

```text
O(n^2)
```

### Space Complexity

```text
O(A + B)
```

which is `O(n)` in the worst case.

---

## Verdict

Good as a baseline.
But since occurrences are naturally sorted by index, we should use that structure better.

---

# Approach 2: Brute Force Matching + Binary Search on `b` Positions

## Intuition

Once we collect all `b` occurrence indices, they are automatically sorted because we found them by scanning left to right.

For a given `a` occurrence `i`, we want to know whether there exists some `b` position in:

```text
[i - k, i + k]
```

So instead of scanning all `b` positions, we can binary search for the first `b` position that is at least `i - k`.

If that position also satisfies `<= i + k`, then `i` is beautiful.

This drops the second phase from `O(A * B)` to `O(A log B)`.

---

## Algorithm

1. Find all occurrences of `a`
2. Find all occurrences of `b`
3. For each `i` in `aPositions`:
   - binary search in `bPositions` for the first value `>= i - k`
   - if found and that value `<= i + k`, then add `i`

---

## Java Code

```java
import java.util.*;

class Solution {
    public List<Integer> beautifulIndices(String s, String a, String b, int k) {
        List<Integer> aPositions = findOccurrences(s, a);
        List<Integer> bPositions = findOccurrences(s, b);
        List<Integer> answer = new ArrayList<>();

        for (int i : aPositions) {
            int idx = lowerBound(bPositions, i - k);
            if (idx < bPositions.size() && bPositions.get(idx) <= i + k) {
                answer.add(i);
            }
        }

        return answer;
    }

    private List<Integer> findOccurrences(String s, String pattern) {
        List<Integer> positions = new ArrayList<>();

        for (int i = 0; i + pattern.length() <= s.length(); i++) {
            if (matches(s, i, pattern)) {
                positions.add(i);
            }
        }

        return positions;
    }

    private boolean matches(String s, int start, String pattern) {
        for (int i = 0; i < pattern.length(); i++) {
            if (s.charAt(start + i) != pattern.charAt(i)) {
                return false;
            }
        }
        return true;
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

Occurrence finding:

```text
O(n * |a| + n * |b|)
```

Range existence checks:

```text
O(A log B)
```

So total:

```text
O(n * |a| + n * |b| + A log B)
```

Since `|a|` and `|b|` are at most `10`, pattern matching is effectively linear in practice.

Worst-case summary:

```text
O(n log n)
```

### Space Complexity

```text
O(A + B)
```

---

## Verdict

This is already an excellent solution for the constraints.

Because pattern lengths are tiny, even naive substring matching is fine.

---

# Approach 3: Two Pointers on Sorted Occurrence Lists

## Intuition

We can do even better than binary search in the second phase.

Since both `aPositions` and `bPositions` are sorted, we can sweep through them with two pointers.

For each `a` occurrence `i`, we want to know if there exists a `b` occurrence in:

```text
[i - k, i + k]
```

We can move a pointer through `bPositions` so that it always points to the first candidate not less than `i - k`.

Then we only need to check whether that candidate is at most `i + k`.

This makes the second phase linear.

---

## Algorithm

1. Find all `a` occurrences
2. Find all `b` occurrences
3. Maintain pointer `j = 0` into `bPositions`
4. For each `i` in `aPositions`:
   - move `j` while `bPositions[j] < i - k`
   - now `j` is the first candidate in range or later
   - if `j < size` and `bPositions[j] <= i + k`, then `i` is beautiful

---

## Java Code

```java
import java.util.*;

class Solution {
    public List<Integer> beautifulIndices(String s, String a, String b, int k) {
        List<Integer> aPositions = findOccurrences(s, a);
        List<Integer> bPositions = findOccurrences(s, b);
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

    private List<Integer> findOccurrences(String s, String pattern) {
        List<Integer> positions = new ArrayList<>();

        for (int i = 0; i + pattern.length() <= s.length(); i++) {
            if (matches(s, i, pattern)) {
                positions.add(i);
            }
        }

        return positions;
    }

    private boolean matches(String s, int start, String pattern) {
        for (int i = 0; i < pattern.length(); i++) {
            if (s.charAt(start + i) != pattern.charAt(i)) {
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

Occurrence finding:

```text
O(n * |a| + n * |b|)
```

Two-pointer sweep:

```text
O(A + B)
```

So total:

```text
O(n * |a| + n * |b| + A + B)
```

With `|a|, |b| <= 10`, this is effectively:

```text
O(n)
```

### Space Complexity

```text
O(A + B)
```

---

## Verdict

This is probably the best practical solution for Problem I.

It is simple, exact, and extremely efficient.

---

# Approach 4: KMP for Pattern Occurrences + Binary Search

## Intuition

The previous approaches used direct matching because `|a|` and `|b|` are tiny.

But if we want a more general-purpose string matching solution, we can use **KMP** to find all occurrences of `a` and `b`.

Then we can still use binary search or two pointers for the distance condition.

This is a stronger pattern-matching technique, even though for this exact problem it is not strictly necessary.

---

## KMP Refresher

KMP finds all occurrences of a pattern in linear time:

```text
O(n + m)
```

where `m` is the pattern length.

It does this by computing the LPS array:

```text
lps[i] = longest proper prefix of pattern[0..i]
         which is also a suffix
```

Then mismatches can jump intelligently instead of restarting from scratch.

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

KMP occurrence finding:

```text
O(n + |a| + n + |b|) = O(n + |a| + |b|)
```

Range queries using binary search:

```text
O(A log B)
```

Total:

```text
O(n + A log B)
```

### Space Complexity

```text
O(A + B + |a| + |b|)
```

---

## Verdict

This is algorithmically stronger, but probably overkill for this exact problem since `a` and `b` are so short.

Still, it is a good reusable technique.

---

# Approach 5: Z-Algorithm for Occurrence Finding

## Intuition

Another exact string-matching option is the **Z-algorithm**.

To find occurrences of pattern `p` in text `s`, build:

```text
p + "#" + s
```

Then compute the Z-array.
Whenever the Z value equals `p.length()`, we found a match.

This gives linear-time matching.

Again, it is more general than needed for this problem, but useful to know.

---

## Java Code

```java
import java.util.*;

class Solution {
    public List<Integer> beautifulIndices(String s, String a, String b, int k) {
        List<Integer> aPositions = findWithZ(s, a);
        List<Integer> bPositions = findWithZ(s, b);
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

    private List<Integer> findWithZ(String text, String pattern) {
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

Occurrence finding with Z:

```text
O(n + |a| + n + |b|)
```

Two-pointer sweep:

```text
O(A + B)
```

Total:

```text
O(n + A + B)
```

### Space Complexity

```text
O(n)
```

due to the temporary combined strings and Z arrays.

---

# Which Approach Should You Prefer?

## For this exact problem

Because:

- `|a|, |b| <= 10`
- `n <= 10^5`

the simplest and strongest practical solution is:

### naive occurrence matching + binary search

or

### naive occurrence matching + two pointers

You do not need heavy string matching machinery here.

---

## Best practical choice

### Approach 3: direct matching + two pointers

Why?

- very short code
- easy to reason about
- effectively linear
- leverages the fact that pattern lengths are tiny

---

## Best reusable general-purpose choice

### KMP or Z-algorithm + two pointers

If pattern lengths were large, those would be more attractive.

---

# Final Recommended Solution

```java
import java.util.*;

class Solution {
    public List<Integer> beautifulIndices(String s, String a, String b, int k) {
        List<Integer> aPositions = findOccurrences(s, a);
        List<Integer> bPositions = findOccurrences(s, b);
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

    private List<Integer> findOccurrences(String s, String pattern) {
        List<Integer> positions = new ArrayList<>();

        for (int i = 0; i + pattern.length() <= s.length(); i++) {
            boolean match = true;

            for (int j = 0; j < pattern.length(); j++) {
                if (s.charAt(i + j) != pattern.charAt(j)) {
                    match = false;
                    break;
                }
            }

            if (match) {
                positions.add(i);
            }
        }

        return positions;
    }
}
```

---

# Why the Two-Pointer Check Works

Suppose we process `aPositions` in sorted order.

For each `i`, any `b` position less than `i - k` can never help again for this `i` or any later `a` position, because later `a` positions only increase the lower bound.

So the pointer into `bPositions` only moves forward.

That is why the second phase is linear.

---

# Common Mistakes

## 1. Scanning all `b` positions for every `a` position

Correct, but slower than necessary.

---

## 2. Forgetting sorted order

Occurrence lists are naturally sorted because we scan left to right.
That sorted structure is what enables binary search and two pointers.

---

## 3. Using substring extraction repeatedly

Writing:

```java
s.substring(i, i + a.length()).equals(a)
```

works, but can allocate many temporary strings.

Character-by-character comparison is usually cleaner and cheaper.

---

## 4. Missing overlapping matches

Occurrences are allowed to overlap.
So the scan must check every index, not jump by pattern length after a match.

---

# Complexity Summary

## Approach 1: brute force beautiful check

- Time: `O(n^2)` worst case
- Space: `O(n)`

## Approach 2: binary search over `b` positions

- Time: `O(n log n)` worst case
- Space: `O(n)`

## Approach 3: two pointers over occurrence lists

- Time: effectively `O(n)`
- Space: `O(n)`

## Approach 4: KMP + binary search

- Time: `O(n + A log B)`
- Space: `O(n)`

## Approach 5: Z-algorithm + two pointers

- Time: `O(n + A + B)`
- Space: `O(n)`

---

# Interview Summary

The problem is best seen as:

1. find all starts of `a`
2. find all starts of `b`
3. for each `a` start, check whether some `b` start lies in `[i-k, i+k]`

The distance check over sorted occurrence lists is the real simplification.

For Problem I, direct matching is sufficient because patterns are tiny.

The strongest clean answer is:

- find occurrences
- sweep with two pointers
