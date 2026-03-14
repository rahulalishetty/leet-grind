# 943. Find the Shortest Superstring

## Approach 1: Dynamic Programming

### Intuition

We need to arrange the words in a sequence so that each word may overlap with the previous one. Since no word is a substring of another word, the optimal strategy is to **maximize the total overlap between adjacent words**.

Suppose we have already placed some words and the last placed word is `A[i]`. If we place `A[j]` next (where `j` has not been used yet), then the total overlap increases by:

```
overlap(A[i], A[j])
```

To systematically compute this, we use **Dynamic Programming with Bitmasking**.

Define:

```
dp(mask, i)
```

- `mask` represents which words have already been used.
- `i` represents the **last word placed**.
- `dp(mask, i)` stores the **maximum overlap achieved**.

Transition:

```
dp(mask ^ (1 << j), j) = max(overlap(A[i], A[j]) + dp(mask, i))
```

Where:

- `j` is not yet used in `mask`
- `i` is a word already present in `mask`

We also track the **parent transitions** to reconstruct the final superstring.

---

# Algorithm

The solution has three main phases.

## 1. Precompute Overlaps

Compute:

```
overlap(A[i], A[j])
```

for every pair of words.

This represents how many characters of `A[j]` overlap with the suffix of `A[i]`.

---

## 2. Dynamic Programming

Define:

```
dp[mask][i]
```

- Maximum overlap using words in `mask`
- Ending with word `i`

Maintain:

```
parent[mask][i]
```

to reconstruct the order later.

---

## 3. Reconstruct the Superstring

1. Identify the ending word that gives maximum overlap.
2. Trace parents backwards.
3. Reverse the sequence.
4. Construct the final string using the stored overlaps.

---

# Java Implementation

```java
class Solution {
    public String shortestSuperstring(String[] A) {
        int N = A.length;

        // Populate overlaps
        int[][] overlaps = new int[N][N];
        for (int i = 0; i < N; ++i)
            for (int j = 0; j < N; ++j) if (i != j) {
                int m = Math.min(A[i].length(), A[j].length());
                for (int k = m; k >= 0; --k)
                    if (A[i].endsWith(A[j].substring(0, k))) {
                        overlaps[i][j] = k;
                        break;
                    }
            }

        // dp[mask][i] = most overlap with mask, ending with ith element
        int[][] dp = new int[1<<N][N];
        int[][] parent = new int[1<<N][N];
        for (int mask = 0; mask < (1<<N); ++mask) {
            Arrays.fill(parent[mask], -1);

            for (int bit = 0; bit < N; ++bit) if (((mask >> bit) & 1) > 0) {
                int pmask = mask ^ (1 << bit);
                if (pmask == 0) continue;
                for (int i = 0; i < N; ++i) if (((pmask >> i) & 1) > 0) {
                    int val = dp[pmask][i] + overlaps[i][bit];
                    if (val > dp[mask][bit]) {
                        dp[mask][bit] = val;
                        parent[mask][bit] = i;
                    }
                }
            }
        }

        int[] perm = new int[N];
        boolean[] seen = new boolean[N];
        int t = 0;
        int mask = (1 << N) - 1;

        int p = 0;
        for (int j = 0; j < N; ++j)
            if (dp[(1<<N) - 1][j] > dp[(1<<N) - 1][p])
                p = j;

        while (p != -1) {
            perm[t++] = p;
            seen[p] = true;
            int p2 = parent[mask][p];
            mask ^= 1 << p;
            p = p2;
        }

        for (int i = 0; i < t/2; ++i) {
            int v = perm[i];
            perm[i] = perm[t-1-i];
            perm[t-1-i] = v;
        }

        for (int i = 0; i < N; ++i) if (!seen[i])
            perm[t++] = i;

        StringBuilder ans = new StringBuilder(A[perm[0]]);
        for (int i = 1; i < N; ++i) {
            int overlap = overlaps[perm[i-1]][perm[i]];
            ans.append(A[perm[i]].substring(overlap));
        }

        return ans.toString();
    }
}
```

---

# Complexity Analysis

### Time Complexity

```
O(N^2 (2^N + W))
```

Where:

- `N` = number of words
- `W` = maximum word length

Explanation:

- `2^N` DP states
- For each state we check `N` transitions
- Overlap computation involves up to `W` characters

---

### Space Complexity

```
O(N (2^N + W))
```

Used for:

- DP table
- Parent reconstruction
- Overlap matrix
