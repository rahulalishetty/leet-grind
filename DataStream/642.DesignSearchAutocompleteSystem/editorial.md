# Design Search Autocomplete System — Trie Approaches

## Overview

A **Trie** is a tree-based data structure used for efficient prefix searching in strings.

Each node in a trie represents a character.
The path from the root to a node forms a **prefix string**.

Example structure:

```
root
 ├── i
 │    ├── ' '
 │    │     ├── l
 │    │     │    └── ...
 │    └── s
 │         └── ...
```

The root represents the **empty string**.

Tries are especially useful when:

- Searching strings **character by character**
- Handling **prefix queries**
- Supporting **autocomplete systems**

---

# Approach 1: Trie

## Intuition

A trie is ideal for autocomplete because:

- We process input **one character at a time**
- Each prefix corresponds to **one path in the trie**

If we store every sentence in the trie:

- Each node represents a prefix
- Each node keeps track of **sentences that share that prefix**

---

## TrieNode Structure

```java
class TrieNode {
    Map<Character, TrieNode> children;
    Map<String, Integer> sentences;

    public TrieNode() {
        children = new HashMap<>();
        sentences = new HashMap<>();
    }
}
```

Attributes:

- `children` → next characters in the trie
- `sentences` → sentences sharing the prefix and their frequencies

---

## Adding Sentences to Trie

```java
private void addToTrie(String sentence, int count) {
    TrieNode node = root;

    for (char c : sentence.toCharArray()) {
        if (!node.children.containsKey(c)) {
            node.children.put(c, new TrieNode());
        }

        node = node.children.get(c);

        node.sentences.put(
            sentence,
            node.sentences.getOrDefault(sentence, 0) + count
        );
    }
}
```

Each node tracks **all sentences with that prefix**.

---

## Handling Input

We maintain:

```
currSentence → current typed prefix
currNode → current position in trie
```

### Cases

1️⃣ `c == '#'`

- Sentence completed
- Insert into trie
- Reset state

2️⃣ `c` exists in children

- Move to child node
- Retrieve matching sentences
- Sort by frequency and lexicographic order

3️⃣ `c` does not exist

- No sentences match
- Move to dead node

---

## Implementation

```java
class TrieNode {
    Map<Character, TrieNode> children;
    Map<String, Integer> sentences;

    public TrieNode() {
        children = new HashMap<>();
        sentences = new HashMap<>();
    }
}

class AutocompleteSystem {

    TrieNode root;
    TrieNode currNode;
    TrieNode dead;

    StringBuilder currSentence;

    public AutocompleteSystem(String[] sentences, int[] times) {

        root = new TrieNode();

        for (int i = 0; i < sentences.length; i++) {
            addToTrie(sentences[i], times[i]);
        }

        currSentence = new StringBuilder();
        currNode = root;
        dead = new TrieNode();
    }

    public List<String> input(char c) {

        if (c == '#') {

            addToTrie(currSentence.toString(), 1);

            currSentence.setLength(0);
            currNode = root;

            return new ArrayList<>();
        }

        currSentence.append(c);

        if (!currNode.children.containsKey(c)) {
            currNode = dead;
            return new ArrayList<>();
        }

        currNode = currNode.children.get(c);

        List<String> sentences =
            new ArrayList<>(currNode.sentences.keySet());

        Collections.sort(sentences, (a, b) -> {

            int hotA = currNode.sentences.get(a);
            int hotB = currNode.sentences.get(b);

            if (hotA == hotB) {
                return a.compareTo(b);
            }

            return hotB - hotA;
        });

        List<String> ans = new ArrayList<>();

        for (int i = 0; i < Math.min(3, sentences.size()); i++) {
            ans.add(sentences.get(i));
        }

        return ans;
    }

    private void addToTrie(String sentence, int count) {

        TrieNode node = root;

        for (char c : sentence.toCharArray()) {

            if (!node.children.containsKey(c)) {
                node.children.put(c, new TrieNode());
            }

            node = node.children.get(c);

            node.sentences.put(
                sentence,
                node.sentences.getOrDefault(sentence, 0) + count
            );
        }
    }
}
```

---

# Complexity Analysis

Let:

```
n = number of sentences
k = average sentence length
m = number of input calls
```

### Time Complexity

Constructor:

```
O(n * k)
```

Input:

```
O(m * (n + k*m) * log(n + k*m))
```

Reason:

- Fetch sentences at node
- Sort them by frequency

---

### Space Complexity

```
O(k * (n*k + m))
```

Worst case:

- No sentences share prefixes
- Trie contains `n*k` nodes

---

# Approach 2: Optimization with Heap

## Intuition

The expensive step in Approach 1 is:

```
Sorting sentences every time
```

Instead we can use a **heap** to find the **top 3 sentences**.

---

## Key Idea

Instead of sorting all sentences:

```
Use a priority queue of size 3
```

Keep only the **best 3 sentences** while iterating.

---

## Java Heap Strategy

Comparator rules:

1️⃣ Lower frequency removed first
2️⃣ If tie → lexicographically larger removed first

This ensures the heap always keeps **top 3 best results**.

---

## Implementation

```java
class TrieNode {
    Map<Character, TrieNode> children;
    Map<String, Integer> sentences;

    public TrieNode() {
        children = new HashMap<>();
        sentences = new HashMap<>();
    }
}

class AutocompleteSystem {

    TrieNode root;
    TrieNode currNode;
    TrieNode dead;

    StringBuilder currSentence;

    public AutocompleteSystem(String[] sentences, int[] times) {

        root = new TrieNode();

        for (int i = 0; i < sentences.length; i++) {
            addToTrie(sentences[i], times[i]);
        }

        currSentence = new StringBuilder();
        currNode = root;
        dead = new TrieNode();
    }

    public List<String> input(char c) {

        if (c == '#') {

            addToTrie(currSentence.toString(), 1);

            currSentence.setLength(0);
            currNode = root;

            return new ArrayList<>();
        }

        currSentence.append(c);

        if (!currNode.children.containsKey(c)) {
            currNode = dead;
            return new ArrayList<>();
        }

        currNode = currNode.children.get(c);

        PriorityQueue<String> heap =
            new PriorityQueue<>((a, b) -> {

                int hotA = currNode.sentences.get(a);
                int hotB = currNode.sentences.get(b);

                if (hotA == hotB) {
                    return b.compareTo(a);
                }

                return hotA - hotB;
            });

        for (String sentence : currNode.sentences.keySet()) {

            heap.add(sentence);

            if (heap.size() > 3) {
                heap.remove();
            }
        }

        List<String> ans = new ArrayList<>();

        while (!heap.isEmpty()) {
            ans.add(heap.remove());
        }

        Collections.reverse(ans);

        return ans;
    }

    private void addToTrie(String sentence, int count) {

        TrieNode node = root;

        for (char c : sentence.toCharArray()) {

            if (!node.children.containsKey(c)) {
                node.children.put(c, new TrieNode());
            }

            node = node.children.get(c);

            node.sentences.put(
                sentence,
                node.sentences.getOrDefault(sentence, 0) + count
            );
        }
    }
}
```

---

# Complexity Analysis

Assuming heapify is linear (like Python).

### Time Complexity

Constructor:

```
O(n * k)
```

Input:

```
O(m * (n + k*m))
```

Heap keeps only **3 elements**, making extraction efficient.

---

### Space Complexity

```
O(k * (n*k + m))
```

Trie size depends on prefix sharing among sentences.
