# 3042. Count Prefix and Suffix Pairs I

## Approach 1: Brute Force

### Intuition

We need to count pairs of words where one word is both a prefix and a suffix of the other.
A **prefix** of a string is a part of the string that appears at the start, and a **suffix** is a part of the string that appears at the end.

Example:

```
word = "ababa"
prefix = "aba"
suffix = "aba"
```

Here `"aba"` is both a prefix and a suffix.

A simple solution is **brute force**, comparing every pair of words and checking if one word is both a prefix and suffix of the other.

Many languages provide built-in helpers:

- **Java / Python**
  - `startsWith()` → prefix check
  - `endsWith()` → suffix check
- **C++**
  - `find()` → prefix
  - `rfind()` → suffix

The algorithm simply tests all pairs `(i, j)` where `i < j`.

If:

```
words[j].startsWith(words[i])
AND
words[j].endsWith(words[i])
```

then we count the pair.

This approach works well because constraints are small.

---

### Algorithm

1. Initialize `n = words.length`
2. Initialize `count = 0`
3. For each index `i`
4. For each index `j > i`
5. Skip if `words[i].length() > words[j].length()`
6. Check prefix and suffix conditions
7. Increment count if both match
8. Return `count`

---

### Implementation (Java)

```java
class Solution {

    public int countPrefixSuffixPairs(String[] words) {
        int n = words.length;
        int count = 0;

        // Step 1: Iterate through each pair of words
        for (int i = 0; i < n; ++i) {
            for (int j = i + 1; j < n; ++j) {
                String str1 = words[i];
                String str2 = words[j];

                // Step 2: Skip if the first string is larger than the second
                if (str1.length() > str2.length()) continue;

                // Step 3: Check prefix and suffix
                if (str2.startsWith(str1) && str2.endsWith(str1)) {
                    ++count;
                }
            }
        }

        return count;
    }
}
```

---

### Complexity Analysis

Let:

```
n = number of words
m = average word length
```

#### Time Complexity

```
O(n² * m)
```

Explanation:

- Two nested loops → `O(n²)`
- Prefix and suffix check → `O(m)`

#### Space Complexity

```
O(1)
```

Only constant extra space is used.

---

# Approach 2: Dual Trie

## Intuition

The brute force approach repeatedly checks prefixes and suffixes.

We can optimize prefix checking using a **Trie**.

### What is a Trie?

A **Trie** is a tree-like structure used for storing strings efficiently.

Each node represents a character.

Example inserting `"bat"` and `"ball"`:

```
      (root)
       |
       b
       |
       a
      / \\
     t   l
          \\
           l
```

Common prefixes share nodes.

Tries are widely used in:

- autocomplete
- dictionaries
- word games

---

## Key Idea

We use **two tries**:

### Prefix Trie

Stores the original word.

Used to test:

```
word_j starts with word_i
```

### Suffix Trie

Instead of storing suffixes directly, we:

1. Reverse the word
2. Insert reversed word into another Trie

This converts suffix checking into prefix checking.

Example:

```
word = "abzdcabz"
reverse = "zbacdzba"
```

Now suffix checks become prefix checks.

---

## Example

Check if `"abz"` is both prefix and suffix of `"abzdcabz"`.

Prefix check:

```
"abz" is prefix of "abzdcabz"
```

Suffix check:

```
reverse("abz") = "zba"
prefix of reverse("abzdcabz") = "zbacdzba"
```

So the pair is valid.

---

## Algorithm

1. Define a Trie node structure.
2. Create two tries:
   - prefixTrie
   - suffixTrie
3. For each word:
   - Insert word into prefixTrie
   - Insert reversed word into suffixTrie
4. For each previous word `j < i`
5. Check:
   - prefixTrie.startsWith(words[j])
   - suffixTrie.startsWith(reverse(words[j]))
6. If both true → increment count

---

## Implementation

```java
class Node {

    private Node[] links = new Node[26];

    public boolean contains(char c) {
        return links[c - 'a'] != null;
    }

    public void put(char c, Node node) {
        links[c - 'a'] = node;
    }

    public Node next(char c) {
        return links[c - 'a'];
    }
}

class Trie {

    private Node root;

    public Trie() {
        root = new Node();
    }

    public void insert(String word) {
        Node node = root;
        for (char c : word.toCharArray()) {
            if (!node.contains(c)) {
                node.put(c, new Node());
            }
            node = node.next(c);
        }
    }

    public boolean startsWith(String prefix) {
        Node node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.contains(c)) {
                return false;
            }
            node = node.next(c);
        }

        return true;
    }
}

class Solution {

    public int countPrefixSuffixPairs(String[] words) {
        int n = words.length;
        int count = 0;

        for (int i = 0; i < n; i++) {

            Trie prefixTrie = new Trie();
            Trie suffixTrie = new Trie();

            prefixTrie.insert(words[i]);

            String revWord = new StringBuilder(words[i]).reverse().toString();
            suffixTrie.insert(revWord);

            for (int j = 0; j < i; j++) {

                if (words[j].length() > words[i].length())
                    continue;

                String prefixWord = words[j];
                String revPrefixWord = new StringBuilder(prefixWord).reverse().toString();

                if (prefixTrie.startsWith(prefixWord)
                        && suffixTrie.startsWith(revPrefixWord)) {
                    count++;
                }
            }
        }

        return count;
    }
}
```

---

## Complexity Analysis

Let:

```
n = number of words
m = average word length
```

### Time Complexity

```
O(n² * m)
```

Explanation:

- Outer loop → `n`
- Inner loop → `n`
- Trie operations → `O(m)`

### Space Complexity

```
O(n * m)
```

Space used by Trie nodes.

---

# Summary

| Approach    | Idea                                             | Time       | Space     |
| ----------- | ------------------------------------------------ | ---------- | --------- |
| Brute Force | Check prefix & suffix directly                   | O(n² \* m) | O(1)      |
| Dual Trie   | Convert prefix & suffix checks into Trie lookups | O(n² \* m) | O(n \* m) |

For this problem, **Brute Force is usually the best choice** because constraints are small.
