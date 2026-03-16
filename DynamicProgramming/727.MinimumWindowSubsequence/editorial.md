# Minimum Window Subsequence — Detailed Notes

This document converts the provided explanation into a detailed Markdown note.

The problem discussed here is the classic **Minimum Window Subsequence** problem:

Given two strings:

- `s1`
- `s2`

find the **shortest substring of `s1`** such that `s2` appears in that substring as a **subsequence**.

If multiple answers have the same minimum length, the earlier one is usually preferred by the common formulations. If no such substring exists, return the empty string.

---

# Problem Restatement

A **substring** is contiguous.

A **subsequence** is not necessarily contiguous, but the relative order must be preserved.

So the task is:

> Find the shortest contiguous substring of `s1` that contains `s2` in order as a subsequence.

---

## Example

Let:

```text
s1 = "abcdebdde"
s2 = "bde"
```

Possible candidate windows include:

- `"bcde"` → contains `b -> d -> e`
- `"bdde"` → also contains `b -> d -> e`

The shortest valid answer is:

```text
"bcde"
```

---

# Notation

Let:

- `n = s1.length()`
- `m = s2.length()`

Define:

- `a[i]` = prefix of `s1` of length `i`
- `b[j]` = prefix of `s2` of length `j`

For example, if:

```text
s1 = "abcdebdde"
s2 = "bde"
```

then:

- `a[0] = ""`
- `a[4] = "abcd"`
- `a[7] = "abcdebd"`

and:

- `b[0] = ""`
- `b[1] = "b"`
- `b[3] = "bde"`

This notation is useful for defining the dynamic programming state.

---

# Approach 1: Dynamic Programming

## Intuition

The key idea is to define a DP state that tells us:

> For a prefix of `s1` ending at a particular position, what is the shortest substring ending there that contains a given prefix of `s2` as a subsequence?

This may sound subtle, but it is a very powerful viewpoint.

---

## DP Definition

Let:

```text
dp[i][j]
```

denote:

> the minimum length of a substring of `s1` ending at index `i - 1` that contains `b[j]` (the prefix of `s2` of length `j`) as a subsequence.

If no such substring exists, then:

```text
dp[i][j] = infinity
```

---

## What This Means

### Example 1

If:

```text
s1 = "abcdebdde"
s2 = "bde"
```

then:

```text
dp[4][3] = infinity
```

because `a[4] = "abcd"` does not contain `"bde"` as a subsequence.

---

### Example 2

```text
dp[7][3] = 6
```

because the shortest substring ending at position `6` (0-based) that contains `"bde"` is:

```text
"bcdebd"
```

with length `6`.

---

### Example 3

```text
dp[5][2] = 4
```

because the shortest substring ending at position `4` that contains `"bd"` is:

```text
"bcde"
```

with length `4`.

---

### Example 4

```text
dp[6][1] = 1
```

because the shortest substring ending at position `5` containing `"b"` is simply:

```text
"b"
```

---

### Example 5

```text
dp[4][0] = 0
```

because an empty prefix of `s2` is always contained in the empty substring.

---

# DP Base Cases

We must initialize the DP carefully.

## Case 1: `j = 0`

If we are looking for an empty prefix of `s2`, then the shortest substring containing it is the empty substring itself.

So:

```text
dp[i][0] = 0
```

for all `i`.

---

## Case 2: `i = 0` and `j > 0`

If `s1` is empty but `s2`'s prefix is non-empty, then it is impossible.

So:

```text
dp[0][j] = infinity
```

for all `j > 0`.

---

# DP Transition

Now consider a general state where:

- `i > 0`
- `j > 0`

We want to compute `dp[i][j]`.

We are considering substrings of `s1` ending at index `i - 1`.

Let the last character of that substring be:

```text
s1[i - 1]
```

Also, the last character of `b[j]` is:

```text
s2[j - 1]
```

Now there are two cases.

---

## Case 1: Characters Match

If:

```text
s1[i - 1] == s2[j - 1]
```

then the current character of `s1` can be used to match the current character of `s2`.

So the shortest valid substring ending here is obtained by extending the best substring that matched the previous prefix `b[j - 1]`.

Thus:

```text
dp[i][j] = dp[i - 1][j - 1] + 1
```

The `+1` is because we append `s1[i - 1]`.

---

## Case 2: Characters Do Not Match

If:

```text
s1[i - 1] != s2[j - 1]
```

then the current character of `s1` is useless for matching the new character of `s2`.

But since our substring must still end at `i - 1`, we include this character anyway and rely on the earlier part to already contain `b[j]`.

Thus:

```text
dp[i][j] = dp[i - 1][j] + 1
```

Again the `+1` accounts for including the current ending character in the window.

---

# Final Answer Extraction

The original problem wants the shortest substring of `s1` that contains the **full** `s2` as a subsequence.

That means we want:

```text
min(dp[i][m]) for all i
```

because the optimal window may end anywhere.

So while filling the DP table, we keep track of:

- `length` = smallest value of `dp[i][m]`
- `end` = the smallest `i` where that minimum occurs

Then the answer is:

```text
s1.substring(end - length, end)
```

If no valid value is found, return `""`.

---

## Example

For:

```text
s1 = "abcdebdde"
s2 = "bde"
```

the minimum value among `dp[i][3]` is:

```text
4
```

and it occurs first at:

```text
end = 5
```

So the answer is:

```text
s1.substring(5 - 4, 5) = s1.substring(1, 5) = "bcde"
```

---

# Algorithm Summary

1. Let `n = s1.length()`, `m = s2.length()`
2. Create `dp[n + 1][m + 1]`
3. Initialize all entries to a large value (infinity)
4. Set `dp[0][0] = 0`
5. For every `i`, set `dp[i][0] = 0`
6. Fill the DP table row by row
7. For each `i`, if `dp[i][m]` improves the answer, update:
   - `length`
   - `end`
8. If no valid answer exists, return `""`
9. Otherwise return the corresponding substring

---

# Java Implementation

```java
class Solution {
    public String minWindow(String s1, String s2) {
        int n = s1.length(), m = s2.length();

        int dp[][] = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) {
            Arrays.fill(dp[i], 1000000000);
        }

        dp[0][0] = 0;

        int end = 0, length = n + 1;

        for (int i = 1; i <= n; i++) {
            dp[i][0] = 0;

            for (int j = 1; j <= m; j++) {
                dp[i][j] = 1 + (
                    s1.charAt(i - 1) == s2.charAt(j - 1)
                    ? dp[i - 1][j - 1]
                    : dp[i - 1][j]
                );
            }

            if (dp[i][m] < length) {
                length = dp[i][m];
                end = i;
            }
        }

        return length > n ? "" : s1.substring(end - length, end);
    }
}
```

---

# Complexity Analysis

Let:

- `n = s1.length()`
- `m = s2.length()`

### Time Complexity

There are `O(n * m)` DP states, and each one is computed in constant time.

So:

```text
O(n * m)
```

### Space Complexity

We store the full DP table of size:

```text
O(n * m)
```

---

# Approach 2: Dynamic Programming with Optimized Space Complexity

## Intuition

In the full DP, each state `dp[i][j]` depends only on the previous row:

- `dp[i - 1][j - 1]`
- `dp[i - 1][j]`

So we do not need all `n + 1` rows in memory at once.

We only need:

- the previous row
- the current row

This allows us to reduce space from `O(n * m)` to `O(m)`.

---

## Space-Optimized DP Definition

Use two arrays:

- `f[j]` = previous row
- `g[j]` = current row

At step `i`:

- `f` stores `dp[i - 1][*]`
- `g` will become `dp[i][*]`

After computing `g`, copy or swap it into `f`.

---

## Transition

Same recurrence as before:

If:

```text
s1[i - 1] == s2[j - 1]
```

then:

```text
g[j] = f[j - 1] + 1
```

Else:

```text
g[j] = f[j] + 1
```

We still track the best `length` and `end`.

---

# Java Implementation

```java
class Solution {
    public String minWindow(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        int f[] = new int[m + 1], g[] = new int[m + 1];

        Arrays.fill(f, 1000000000);

        int end = 0, length = n + 1;
        f[0] = 0;

        for (int i = 1; i <= n; i++) {
            g[0] = 0;

            for (int j = 1; j <= m; j++) {
                g[j] = 1 + (
                    s1.charAt(i - 1) == s2.charAt(j - 1)
                    ? f[j - 1]
                    : f[j]
                );
            }

            f = g.clone();

            if (f[m] < length) {
                length = f[m];
                end = i;
            }
        }

        return length > n ? "" : s1.substring(end - length, end);
    }
}
```

---

# Complexity Analysis

### Time Complexity

The number of states and transitions is unchanged:

```text
O(n * m)
```

### Space Complexity

Now we store only two rows of size `m + 1`:

```text
O(m)
```

---

# Approach 3: Greedy

## Intuition

Instead of DP, we can try all possible starting positions `start` in `s1`.

For each `start`, we greedily find the **earliest ending index** `end` such that:

```text
s2
```

is a subsequence of:

```text
s1[start..end]
```

Once we know the earliest valid `end` for a fixed `start`, that gives the shortest valid window beginning at `start`.

Then we compare all such candidate windows and keep the shortest one.

---

## Greedy Matching Process

For a fixed `start`, we want indices:

```text
i0 < i1 < ... < i(m-1)
```

such that:

```text
s1[i0] = s2[0]
s1[i1] = s2[1]
...
s1[i(m-1)] = s2[m-1]
```

and all indices are as small as possible subject to increasing order.

This greedy choice gives the earliest possible `end`.

---

## Preprocessing with Character Indices

For each character `c`, store all positions where it appears in `s1`:

```text
indices[c]
```

These lists are sorted because we build them left to right.

Then to match the next needed character of `s2`, we just need the smallest occurrence strictly greater than the previous matched position.

The provided approach maintains an array `ind[]` so that repeated searches move only forward.

---

## High-Level Process

1. Build `indices[c]` for all characters in `s1`
2. For each possible `start`:
   - set `prev = start - 1`
   - for each character of `s2`:
     - advance its pointer until you find an occurrence greater than `prev`
     - if none exists, no future start will work either, so return current answer
     - set `prev` to that occurrence
   - now `prev` is the earliest valid end for this start
   - update answer if this window is shorter
3. Return the shortest answer found

---

# Java Implementation

```java
class Solution {
    public String minWindow(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        String answer = "";

        HashMap<Character, ArrayList<Integer>> indices = new HashMap<>();
        for (int i = 0; i < n; i++) {
            Character c = s1.charAt(i);
            if (!indices.containsKey(c)) {
                indices.put(c, new ArrayList<>());
            }
            indices.get(c).add(i);
        }

        int ind[] = new int[m];

        for (int start = 0; start < n; start++) {
            int prev = start - 1;

            for (int j = 0; j < m; j++) {
                if (!indices.containsKey(s2.charAt(j))) {
                    return "";
                }

                ArrayList<Integer> curIndices = indices.get(s2.charAt(j));
                while (ind[j] < curIndices.size() && curIndices.get(ind[j]) <= prev) {
                    ind[j]++;
                }

                if (ind[j] == curIndices.size()) {
                    return answer;
                }

                prev = curIndices.get(ind[j]);
            }

            if (answer.isEmpty() || prev - start + 1 < answer.length()) {
                answer = s1.substring(start, prev + 1);
            }
        }

        return answer;
    }
}
```

---

# Why This Greedy Method Works

For a fixed `start`, choosing the earliest possible matching position for each next character of `s2` gives the earliest possible `end`.

So it does indeed produce the shortest valid window beginning at that `start`.

Then comparing over all starts yields the global best.

---

# Complexity Analysis

Let:

- `n = s1.length()`
- `m = s2.length()`

### Time Complexity

There are:

- `n` iterations over `start`
- `m` iterations over characters of `s2`

The `while` loops advance each pointer `ind[j]` only forward, and each `ind[j]` can increase at most `n` times overall.

So the total complexity is:

```text
O(n * m)
```

### Space Complexity

The `indices` structure stores every position of `s1` exactly once:

```text
O(n)
```

The array `ind` has size `m`, and `m <= n`.

So total space is:

```text
O(n)
```

---

# Comparison of Approaches

| Approach           | Main Idea                                                       | Time Complexity | Space Complexity |
| ------------------ | --------------------------------------------------------------- | --------------: | ---------------: |
| Full DP            | Store shortest valid ending substring length for every `(i, j)` |      `O(n * m)` |       `O(n * m)` |
| Space-Optimized DP | Keep only previous and current DP rows                          |      `O(n * m)` |           `O(m)` |
| Greedy             | Try each start, greedily find earliest valid end                |      `O(n * m)` |           `O(n)` |

---

# Key Takeaways

## 1. The DP state is the hardest part

The most important modeling insight is:

> `dp[i][j]` = shortest substring of `s1` ending at `i - 1` that contains the first `j` characters of `s2` as a subsequence.

That leads to very clean transitions.

## 2. Full DP is conceptually the clearest

It is often the easiest to reason about and prove.

## 3. Space can be reduced

Since each row only depends on the previous row, the DP can be compressed to `O(m)` space.

## 4. Greedy is a different angle

Instead of asking “what is the best ending at each position?”, it asks:

> for each starting point, what is the earliest valid ending point?

That also works efficiently.

---

# Final Insight

This problem looks like a window problem at first, but it is not a standard sliding window problem because the target condition is **subsequence**, not substring.

That is why dynamic programming or carefully structured greedy matching is needed.

Among the approaches, the **space-optimized DP** is often the most balanced solution:

- same `O(n * m)` time
- only `O(m)` space
- clean recurrence
- easy correctness reasoning
