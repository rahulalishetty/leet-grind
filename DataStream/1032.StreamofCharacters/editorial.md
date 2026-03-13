# Stream of Characters — Trie Approach

## Prerequisites

This design uses the **Trie** data structure.

If you are not familiar with tries, it is recommended to first study:

- prefix trees
- trie insertion/search
- autocomplete systems

Tries are commonly used in:

- Autocomplete search
- Spell checkers
- T9 predictive text
- IP routing (longest prefix matching)
- Some GCC containers

Whenever a problem involves **dynamic insertion and searching of strings**, a trie is often a strong candidate.

---

# Approach 1: Trie

## Intuition

A natural idea is to insert all words into a trie and perform searches against the stream.

However, the challenge is determining **how many characters to match**.

Example:

If the stream ends with:

```
... j k l
```

Possible suffix matches could be:

```
jkl
kl
l
```

Trying all suffixes would be inefficient.

---

# Key Insight

We always know the **last character** of the stream.

Therefore we can:

1. **Reverse all words**
2. Store them in a trie
3. Traverse the **stream backwards**

This converts the suffix search into a **prefix search**.

Instead of multiple possible suffix checks, we follow a **single path from the end of the stream**.

If we reach a node marked as a word → **success**.

If traversal fails → **no match**.

---

# Reversed Trie Concept

Example:

Original words:

```
["cd", "f", "kl"]
```

Stored in trie as:

```
"dc"
"f"
"lk"
```

When stream receives letters, we traverse the stream **in reverse order**.

---

# Data Structures

### Trie Node

Each node stores:

- children (character → node)
- word flag (end of word)

```java
class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean word = false;
}
```

---

# Stream Storage

We must efficiently add new characters to the **front** of the stream.

Required operation:

```
addFirst()
```

Suitable structure:

```
Deque
```

Implementations:

- Python → `collections.deque`
- Java → `ArrayDeque`

---

# Constructor Implementation

Words are inserted into the trie **in reversed order**.

```java
class TrieNode {
    Map<Character, TrieNode> children = new HashMap();
    boolean word = false;
}

class StreamChecker {

    TrieNode trie = new TrieNode();
    Deque<Character> stream = new ArrayDeque();

    public StreamChecker(String[] words) {
        for (String word : words) {

            TrieNode node = trie;

            String reversedWord =
                new StringBuilder(word).reverse().toString();

            for (char ch : reversedWord.toCharArray()) {

                if (!node.children.containsKey(ch)) {
                    node.children.put(ch, new TrieNode());
                }

                node = node.children.get(ch);
            }

            node.word = true;
        }
    }
}
```

---

# Query Implementation

When a new character arrives:

1. Add the letter to the **front of the stream**
2. Traverse the trie following stream characters
3. Stop when:
   - a word is found → return true
   - traversal fails → return false

```java
public boolean query(char letter) {

    stream.addFirst(letter);

    TrieNode node = trie;

    for (char ch : stream) {

        if (node.word) {
            return true;
        }

        if (!node.children.containsKey(ch)) {
            return false;
        }

        node = node.children.get(ch);
    }

    return node.word;
}
```

---

# Full Implementation

```java
class TrieNode {
    Map<Character, TrieNode> children = new HashMap();
    boolean word = false;
}

class StreamChecker {

    TrieNode trie = new TrieNode();
    Deque<Character> stream = new ArrayDeque();

    public StreamChecker(String[] words) {

        for (String word : words) {

            TrieNode node = trie;

            String reversedWord =
                new StringBuilder(word).reverse().toString();

            for (char ch : reversedWord.toCharArray()) {

                if (!node.children.containsKey(ch)) {
                    node.children.put(ch, new TrieNode());
                }

                node = node.children.get(ch);
            }

            node.word = true;
        }
    }

    public boolean query(char letter) {

        stream.addFirst(letter);

        TrieNode node = trie;

        for (char ch : stream) {

            if (node.word) {
                return true;
            }

            if (!node.children.containsKey(ch)) {
                return false;
            }

            node = node.children.get(ch);
        }

        return node.word;
    }
}
```

---

# Complexity Analysis

Let:

```
N = number of words
M = maximum word length
```

---

## Constructor

We insert every word character-by-character.

### Time Complexity

```
O(N * M)
```

### Space Complexity

Worst case: words share no prefixes.

```
O(N * M)
```

---

# Query Operation

Let:

```
M = max word length
```

We traverse the stream up to depth `M`.

### Time Complexity

```
O(M)
```

### Space Complexity

The stream stores up to the longest word length.

```
O(M)
```

Optimization:

We can **limit deque size to the maximum word length**.

---

# Key Takeaways

This problem demonstrates an important technique:

### Reverse Trie for Suffix Matching

Instead of checking every suffix:

```
suffix search → prefix search
```

by reversing the words.

This allows efficient streaming queries with:

```
O(M) per query
```
