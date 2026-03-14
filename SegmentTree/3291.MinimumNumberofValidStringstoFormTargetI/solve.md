# Minimum Number of Valid Strings to Form Target — Trie + Dynamic Programming Approach

## Intuition

To solve the problem of finding the minimum number of valid strings to form a target string, a **Trie (prefix tree)** data structure is used to efficiently handle string matching and prefix querying.

The main idea is to combine:

- **Trie traversal** for prefix matching
- **Dynamic Programming (DP)** to compute the minimum number of prefixes required to build the target string.

The Trie allows efficient prefix checks, while DP ensures we compute the minimum number of segments needed.

---

# Approach

## 1. Trie Construction

Construct a Trie from the given `words`.

Each word is inserted into the Trie where:

- each node represents a character
- edges represent continuation of prefixes
- paths from root represent prefixes of words

This structure allows efficient prefix matching against the target.

---

## 2. Dynamic Programming Setup

Define a DP array:

```
dp[i] = minimum number of valid strings required to construct target[0...i-1]
```

The goal is to compute:

```
dp[n]
```

where `n = target.length()`.

---

## 3. DP Initialization

```
dp[0] = 0
```

Because forming an empty string requires **zero words**.

All other values are initialized to:

```
INF (large number)
```

which means the position is currently unreachable.

---

## 4. DP Transition

For every index `i` in the target:

1. If `dp[i] == INF`, skip because the prefix cannot be formed.
2. Start traversing the Trie from the root.
3. Continue matching characters in the target starting from `i`.
4. For each successful match ending at `j`, update:

```
dp[j + 1] = min(dp[j + 1], dp[i] + 1)
```

This means:

- the substring `target[i...j]` can be formed using **one valid prefix**
- so we extend the construction using `dp[i] + 1` words.

Stop traversal when:

- no child exists for the next character
- or the Trie path ends.

---

## 5. Result Extraction

The answer is:

```
dp[n]
```

If:

```
dp[n] == INF
```

then the target string cannot be formed, so return:

```
-1
```

---

# Complexity

## Time Complexity

```
O(n^2)
```

Where:

```
n = length of target
```

In the worst case, from each position `i` we attempt to extend matches forward.

---

## Space Complexity

```
O(n)
```

For the DP array, plus space for the Trie.

---

# Java Implementation

```java
class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
}

class Trie {
    TrieNode root = new TrieNode();

    void insert(String word) {
        TrieNode node = root;
        for (char ch : word.toCharArray()) {
            node = node.children.computeIfAbsent(ch, k -> new TrieNode());
        }
    }

    int maxLengthFromNode(TrieNode node) {
        if (node.children.isEmpty()) return 0;
        int maxLength = 0;
        for (TrieNode child : node.children.values()) {
            maxLength = Math.max(maxLength, 1 + maxLengthFromNode(child));
        }
        return maxLength;
    }
}

class Solution {
    public int minValidStrings(String[] words, String target) {
        Trie trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }

        int n = target.length();
        int[] dp = new int[n + 1];
        final int INF = Integer.MAX_VALUE;

        Arrays.fill(dp, INF);
        dp[0] = 0;

        for (int i = 0; i < n; i++) {
            if (dp[i] == INF) continue;

            TrieNode node = trie.root;

            for (int j = i; j < n; j++) {
                char ch = target.charAt(j);

                node = node.children.get(ch);

                if (node == null) break;

                dp[j + 1] = Math.min(dp[j + 1], dp[i] + 1);

                if (node.children.isEmpty()) break;
            }
        }

        return dp[n] == INF ? -1 : dp[n];
    }
}
```
