# Minimum Cost to Separate Sentence Into Rows — Detailed Explanation

## Problem Statement

You are given:

- a string `sentence` containing words separated by single spaces
- an integer `k`

You must split the sentence into multiple rows by inserting line breaks **only between words**.

Rules:

1. A word cannot be split across rows.
2. Every word must be used exactly once.
3. The original word order must remain unchanged.
4. Adjacent words in the same row must be separated by exactly one space.
5. No row may begin or end with a space.
6. Each row length must be at most `k`.

If a row has length `n`, then its cost is:

```text
(k - n)^2
```

But the **last row does not contribute to the total cost**.

Return the **minimum possible total cost**.

---

# Example

## Input

```text
sentence = "i love leetcode"
k = 12
```

Possible splits:

### Split 1

```text
"i"
"love"
"leetcode"
```

Costs:

- first row: `(12 - 1)^2 = 121`
- second row: `(12 - 4)^2 = 64`
- last row: ignored

Total:

```text
121 + 64 = 185
```

### Split 2

```text
"i love"
"leetcode"
```

Costs:

- first row: `(12 - 6)^2 = 36`
- last row: ignored

Total:

```text
36
```

### Split 3

```text
"i"
"love leetcode"
```

This is invalid because:

```text
len("love leetcode") = 13 > 12
```

So the answer is:

```text
36
```

---

# Key Observation

This is a classic **word wrap dynamic programming** problem.

At any position, the only real decision is:

> How many consecutive words should I place on the current row?

Once that choice is made, the rest of the problem is exactly the same problem on the remaining suffix of words.

That structure strongly suggests **DP on suffixes**.

---

# Converting the Sentence Into Words

Suppose:

```text
words = sentence.split(" ")
```

If the words are:

```text
w0, w1, w2, ..., w(n-1)
```

then for any row that uses words from index `i` to `j`, the row length is:

```text
len(i..j) = sum(length of words i..j) + (j - i)
```

Why `+ (j - i)`?

Because if a row has:

- 1 word → 0 spaces
- 2 words → 1 space
- 3 words → 2 spaces

So among `j - i + 1` words, there are exactly `j - i` spaces.

---

# DP Definition

Let:

```text
dp[i] = minimum cost to arrange words from index i to the end
```

So:

- `dp[0]` is the answer
- `dp[n] = 0` because no words remain

---

# Transition

To compute `dp[i]`, try all possible `j >= i` such that words `i..j` fit in one row.

If `len(i..j) <= k`, then:

- if `j == n - 1`, this is the last row, so row cost is `0`
- otherwise row cost is:

```text
(k - len(i..j))^2
```

Then:

```text
dp[i] = min over all valid j of (rowCost + dp[j + 1])
```

This is the full recurrence.

---

# Why the Last Row Has Cost 0

The problem explicitly says:

> the total cost is the sum of the costs for all rows except the last one

That means the final line is free, regardless of unused spaces.

This changes the recurrence slightly and is the main thing you must not forget.

---

# Correctness Intuition

Suppose we are at word index `i`.

Any valid optimal arrangement must place some consecutive block of words:

```text
i, i+1, ..., j
```

on the first row of the remaining suffix.

After that, the rest of the arrangement is just an optimal arrangement for words:

```text
j+1, j+2, ..., n-1
```

So once the first row ending `j` is fixed, the rest is exactly `dp[j+1]`.

That gives optimal substructure, which is why DP works.

---

# Top-Down View

You can think recursively:

```text
solve(i):
    try every possible row ending j
    cost = rowCost(i, j) + solve(j+1)
    return minimum
```

Memoizing `solve(i)` gives the same complexity as bottom-up DP.

---

# Bottom-Up DP

Bottom-up is often cleaner here.

We compute:

```text
dp[n] = 0
dp[n-1], dp[n-2], ..., dp[0]
```

For each `i`, extend the row word by word until the line exceeds `k`.

The moment it exceeds `k`, no larger `j` can work, so we break early.

---

# Detailed Java Implementation

```java
class Solution {
    public int minimumCost(String sentence, int k) {
        String[] words = sentence.split(" ");
        int n = words.length;

        int[] lens = new int[n];
        for (int i = 0; i < n; i++) {
            lens[i] = words[i].length();

            // If any single word is longer than k,
            // it can never fit on a row.
            if (lens[i] > k) return -1;
        }

        long INF = Long.MAX_VALUE / 4;
        long[] dp = new long[n + 1];

        for (int i = 0; i < n; i++) {
            dp[i] = INF;
        }
        dp[n] = 0;

        for (int i = n - 1; i >= 0; i--) {
            int lineLen = 0;

            for (int j = i; j < n; j++) {
                if (j == i) {
                    lineLen = lens[j];
                } else {
                    lineLen += 1 + lens[j];
                }

                if (lineLen > k) break;

                long rowCost;
                if (j == n - 1) {
                    rowCost = 0; // last row is free
                } else {
                    long extra = k - lineLen;
                    rowCost = extra * extra;
                }

                dp[i] = Math.min(dp[i], rowCost + dp[j + 1]);
            }
        }

        return (int) dp[0];
    }
}
```

---

# Walkthrough on the Example

## Input

```text
sentence = "i love leetcode"
k = 12
```

Words:

```text
["i", "love", "leetcode"]
```

Lengths:

```text
[1, 4, 8]
```

Let:

```text
n = 3
```

We compute `dp[3] = 0`.

---

## Step 1: Compute `dp[2]`

Only word left:

```text
"leetcode"
```

Length:

```text
8
```

It fits in one row and it is the last row.

So:

```text
rowCost = 0
dp[2] = 0
```

---

## Step 2: Compute `dp[1]`

Try putting words starting from `"love"`.

### Option A: row = `"love"`

Length:

```text
4
```

Not the last row, so cost:

```text
(12 - 4)^2 = 64
```

Then plus:

```text
dp[2] = 0
```

Total:

```text
64
```

### Option B: row = `"love leetcode"`

Length:

```text
4 + 1 + 8 = 13
```

Too large, invalid.

So:

```text
dp[1] = 64
```

---

## Step 3: Compute `dp[0]`

Try starting at `"i"`.

### Option A: row = `"i"`

Length:

```text
1
```

Cost:

```text
(12 - 1)^2 = 121
```

Remaining:

```text
dp[1] = 64
```

Total:

```text
185
```

### Option B: row = `"i love"`

Length:

```text
1 + 1 + 4 = 6
```

Cost:

```text
(12 - 6)^2 = 36
```

Remaining:

```text
dp[2] = 0
```

Total:

```text
36
```

### Option C: row = `"i love leetcode"`

Length:

```text
1 + 1 + 4 + 1 + 8 = 15
```

Too large.

Therefore:

```text
dp[0] = 36
```

Answer:

```text
36
```

---

# Time Complexity

Let `n` be the number of words.

For each starting index `i`, we may try all `j >= i`.

So the worst-case time complexity is:

```text
O(n^2)
```

This is usually acceptable for standard word-wrap constraints.

---

# Space Complexity

We store:

- word lengths array
- DP array

So space complexity is:

```text
O(n)
```

---

# Why Greedy Fails

A tempting but incorrect approach is:

> Put as many words as possible on the current row.

That does not always minimize total cost.

Why?

Because using more words now may force a terrible arrangement later, and the cost is quadratic:

```text
(k - n)^2
```

Quadratic penalties make future layout effects important.

So local best does not imply global best.

DP is needed.

---

# Alternative Top-Down Memoized Version

Here is the same logic using recursion + memoization.

```java
import java.util.Arrays;

class Solution {
    private String[] words;
    private int[] lens;
    private int n;
    private int k;
    private long[] memo;
    private static final long INF = Long.MAX_VALUE / 4;

    public int minimumCost(String sentence, int k) {
        this.words = sentence.split(" ");
        this.n = words.length;
        this.k = k;
        this.lens = new int[n];

        for (int i = 0; i < n; i++) {
            lens[i] = words[i].length();
            if (lens[i] > k) return -1;
        }

        memo = new long[n + 1];
        Arrays.fill(memo, -1);

        return (int) dfs(0);
    }

    private long dfs(int i) {
        if (i == n) return 0;
        if (memo[i] != -1) return memo[i];

        long ans = INF;
        int lineLen = 0;

        for (int j = i; j < n; j++) {
            if (j == i) {
                lineLen = lens[j];
            } else {
                lineLen += 1 + lens[j];
            }

            if (lineLen > k) break;

            long rowCost;
            if (j == n - 1) {
                rowCost = 0;
            } else {
                long extra = k - lineLen;
                rowCost = extra * extra;
            }

            ans = Math.min(ans, rowCost + dfs(j + 1));
        }

        memo[i] = ans;
        return ans;
    }
}
```

This has the same asymptotic complexity:

- Time: `O(n^2)`
- Space: `O(n)` for memo, plus recursion stack

---

# Common Mistakes

## 1. Charging the last line

This is the most common bug.

Do **not** add:

```text
(k - lineLen)^2
```

for the final row.

---

## 2. Forgetting spaces between words

The row length is not just the sum of word lengths.

You must include:

```text
(number of words - 1)
```

spaces.

---

## 3. Not handling impossible words

If any word has length:

```text
> k
```

then the sentence cannot be formatted at all.

A common convention is to return `-1`.

---

## 4. Using `int` carelessly for cost

Even if constraints seem small, squaring can grow quickly.

Using `long` for DP and cost computation is safer.

---

# Summary

This problem is a classic suffix DP.

## Main recurrence

If words `i..j` fit in one row:

```text
dp[i] = min(dp[i], cost(i, j) + dp[j+1])
```

where:

- `cost(i, j) = 0` if `j` is the last word
- otherwise:

```text
(k - len(i..j))^2
```

## Final answer

```text
dp[0]
```

---

# Final Recommended Solution

```java
class Solution {
    public int minimumCost(String sentence, int k) {
        String[] words = sentence.split(" ");
        int n = words.length;

        int[] lens = new int[n];
        for (int i = 0; i < n; i++) {
            lens[i] = words[i].length();
            if (lens[i] > k) return -1;
        }

        long INF = Long.MAX_VALUE / 4;
        long[] dp = new long[n + 1];

        for (int i = 0; i < n; i++) {
            dp[i] = INF;
        }
        dp[n] = 0;

        for (int i = n - 1; i >= 0; i--) {
            int lineLen = 0;

            for (int j = i; j < n; j++) {
                if (j == i) {
                    lineLen = lens[j];
                } else {
                    lineLen += 1 + lens[j];
                }

                if (lineLen > k) break;

                long rowCost;
                if (j == n - 1) {
                    rowCost = 0;
                } else {
                    long extra = k - lineLen;
                    rowCost = extra * extra;
                }

                dp[i] = Math.min(dp[i], rowCost + dp[j + 1]);
            }
        }

        return (int) dp[0];
    }
}
```
