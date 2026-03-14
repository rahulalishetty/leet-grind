# Minimum Number of Valid Strings to Form Target — KMP + Dynamic Programming Approach

## Intuition

The problem can be divided into two main steps.

### Step 1

Find all possible prefixes of `words` and match them to pieces of the `target`. These prefixes act as the building blocks used to construct the target string.

To do this efficiently, we use the **KMP (Knuth–Morris–Pratt) string matching algorithm**.

The KMP algorithm allows us to compute prefix matches efficiently in:

```
O(N * M)
```

Where:

```
N = length of target
M = number of words
```

Each word generates a KMP array indicating how long a prefix of that word matches a substring ending at each position in the target.

---

### Step 2

For every index `i` in `target`, we determine whether the substring from:

```
target[0 ... i]
```

can be constructed using valid prefixes discovered in Step 1.

We use **Dynamic Programming** to track the minimum number of prefixes required.

---

## Dynamic Programming Definition

```
dp[i] = minimum number of prefixes needed to build target[0 ... i-1]
```

Transition rule:

```
dp[i + 1] = min(dp[i + 1], dp[i - kmp[i] + 1] + 1)
```

Where:

```
kmp[i] = size of the prefix that matches a substring ending at index i
```

Interpretation:

The substring:

```
target[i - kmp[i] ... i]
```

is covered by **one prefix**.

Therefore we only need to check whether the earlier substring:

```
target[0 ... i - kmp[i] - 1]
```

can be formed.

That value is stored in:

```
dp[i - kmp[i] + 1]
```

If:

```
kmp[i] = 0
```

it means **no prefix of the current word can cover this position**, so we skip it.

---

## Complexity

### Time Complexity

```
O(N * M)
```

Where:

```
N = length of target
M = number of words
```

This includes computing the KMP matching arrays for every word.

---

### Space Complexity

```
O(N * M)
```

Space is used for storing KMP arrays for each word.

---

# Java Implementation

```java
class Solution {
    public int minValidStrings(String[] words, String target) {
        int ans = 0, n = target.length();

        // for every word match its prefixes in target
        List<int[]> kmps = new ArrayList<>();
        for (String word : words) {
            int[] kmp = kmp(word, target);
            kmps.add(kmp);
        }

        // for every i in target find min number of prefixes required
        int[] dp = new int[n + 1];

        for (int i = 0; i < n; i++) {
            dp[i + 1] = Integer.MAX_VALUE;

            for (int[] kmp : kmps) {

                if (kmp[i] == 0) {
                    continue;
                }

                if (dp[i - kmp[i] + 1] != Integer.MAX_VALUE) {
                    dp[i + 1] = Math.min(
                        dp[i + 1],
                        dp[i - kmp[i] + 1] + 1
                    );
                }
            }
        }

        return dp[n] == Integer.MAX_VALUE ? -1 : dp[n];
    }

    // Simple KMP string matching algorithm
    int[] kmp(String p, String s) {

        int[] lps = new int[p.length()];
        int[] kmp = new int[s.length()];

        int n = p.length();
        int m = s.length();

        int len = 0;

        for (int i = 1; i < n; i++) {

            while (len > 0 && p.charAt(i) != p.charAt(len)) {
                len = lps[len - 1];
            }

            len = lps[i] = len + (p.charAt(i) == p.charAt(len) ? 1 : 0);
        }

        int j = 0;

        for (int i = 0; i < m; i++) {

            while (j > 0 && s.charAt(i) != p.charAt(j)) {
                j = lps[j - 1];
            }

            j = kmp[i] = j + (s.charAt(i) == p.charAt(j) ? 1 : 0);

            if (j == n) {
                j = lps[j - 1];
            }
        }

        return kmp;
    }
}
```
