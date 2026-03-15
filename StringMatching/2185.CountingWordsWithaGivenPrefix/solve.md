# 2185. Counting Words With a Given Prefix

## Problem

You are given an array of strings `words` and a string `pref`.

Return the number of strings in `words` that contain `pref` as a prefix.

A **prefix** of a string `s` is any leading contiguous substring of `s`.

---

## Example 1

Input:

```
words = ["pay","attention","practice","attend"]
pref = "at"
```

Output:

```
2
```

Explanation:

The 2 strings that contain `"at"` as a prefix are:

- `"attention"`
- `"attend"`

---

## Example 2

Input:

```
words = ["leetcode","win","loops","success"]
pref = "code"
```

Output:

```
0
```

Explanation:

There are no strings that contain `"code"` as a prefix.

---

## Constraints

- `1 <= words.length <= 100`
- `1 <= words[i].length, pref.length <= 100`
- `words[i]` and `pref` consist of lowercase English letters

---

# Approach 1: Brute Force

## Intuition

Given the small constraints of the problem where:

- `words.length ≤ 100`
- `words[i].length, pref.length ≤ 100`

a brute-force approach is viable.

This approach involves checking each word in the `words` list to see if it starts with `pref`. We can do this using two pointers:

- one for the current word
- one for `pref`

Both start at index `0`.

We compare characters sequentially until:

- a mismatch occurs
- the prefix finishes

If the entire prefix matches, we count that word.

---

## Algorithm

### Main Method `prefixCount`

1. Initialize `count = 0`
2. Iterate through each string in `words`
3. For each word:
   - Call helper `hasPrefix`
   - Add result to `count`
4. Return `count`

### Helper Method `hasPrefix`

1. Initialize pointer `itr = 0`
2. Compare characters of `str` and `pref`
3. If mismatch occurs → return `0`
4. If prefix length fully matched → return `1`

---

## Java Implementation

```java
class Solution {

    public int prefixCount(String[] words, String pref) {
        int count = 0;
        for (String word : words) {
            count += hasPrefix(word, pref);
        }
        return count;
    }

    // Returns 1 if str has pref as prefix, 0 otherwise
    private int hasPrefix(String str, String pref) {
        int itr;

        for (itr = 0; itr < str.length() && itr < pref.length(); itr++) {
            if (str.charAt(itr) != pref.charAt(itr)) {
                return 0;
            }
        }

        if (itr != pref.length()) {
            return 0;
        }

        return 1;
    }
}
```

---

## Complexity Analysis

Let:

- `n = words.length`
- `m = pref.length()`

### Time Complexity

```
O(n * m)
```

- Outer loop iterates `n` words
- Prefix comparison takes `m` operations

### Space Complexity

```
O(1)
```

Only constant extra variables are used.

---

# Approach 2: Built-In Methods

## Intuition

Prefix matching is a very common operation in programming.

Most programming languages provide optimized built-in functions.

In **Java**, we can use:

```
String.startsWith()
```

This method checks whether a string begins with a specified prefix.

Using built-in methods makes the solution:

- simpler
- more readable
- well optimized

---

## Algorithm

1. Initialize `count = 0`
2. Iterate through each `word` in `words`
3. If `word.startsWith(pref)`:
   - increment `count`
4. Return `count`

---

## Java Implementation

```java
class Solution {

    public int prefixCount(String[] words, String pref) {
        int count = 0;

        for (String word : words) {
            if (word.startsWith(pref)) {
                count++;
            }
        }

        return count;
    }
}
```

---

## Complexity Analysis

Let:

- `n = words.length`
- `m = pref.length()`

### Time Complexity

```
O(n * m)
```

Each `startsWith` may compare up to `m` characters.

### Space Complexity

```
O(1)
```

No additional structures are created.

---

# Approach 3: Trie

## Intuition

A **Trie** (prefix tree) is a tree-like data structure used for efficient prefix searching.

Each node represents a character.

Paths from the root form prefixes of words.

Tries are commonly used in:

- autocomplete systems
- dictionary searches
- prefix-based queries

---

## Key Idea

Instead of only storing characters, we also maintain a **count** in each node.

`count` represents:

> how many words share this prefix

Example:

If we insert:

```
cat
car
carpet
```

Then:

- node `"ca"` → count = 3
- node `"car"` → count = 2
- node `"cat"` → count = 1

---

## Algorithm

### Step 1: Build Trie

For each word:

1. Start from root
2. For each character:
   - create node if needed
   - move to child
   - increment prefix count

### Step 2: Query Prefix

Traverse Trie following `pref`:

- if any character link is missing → return `0`
- otherwise return `count` at final node

---

## Java Implementation

```java
class Solution {

    public int prefixCount(String[] words, String pref) {
        Trie trie = new Trie();

        for (String word : words) {
            trie.addWord(word);
        }

        return trie.countPrefix(pref);
    }

    private class Trie {

        class Node {
            Node[] links = new Node[26];
            int count = 0;
        }

        Node root = new Node();

        public void addWord(String word) {
            Node curr = root;

            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);

                if (curr.links[c - 'a'] == null) {
                    curr.links[c - 'a'] = new Node();
                }

                curr = curr.links[c - 'a'];
                curr.count++;
            }
        }

        public int countPrefix(String pref) {
            Node curr = root;

            for (int i = 0; i < pref.length(); i++) {
                char c = pref.charAt(i);

                if (curr.links[c - 'a'] == null) {
                    return 0;
                }

                curr = curr.links[c - 'a'];
            }

            return curr.count;
        }
    }
}
```

---

## Complexity Analysis

Let:

- `n = number of words`
- `l = max word length`
- `m = prefix length`

### Time Complexity

Building Trie:

```
O(n * l)
```

Prefix search:

```
O(m)
```

Total:

```
O(n*l + m)
```

### Space Complexity

```
O(n * l)
```

Trie may store every character of every word.

---

# Comparison of Approaches

| Approach              | Time Complexity | Space Complexity | Notes                          |
| --------------------- | --------------- | ---------------- | ------------------------------ |
| Brute Force           | O(n\*m)         | O(1)             | Simple and direct              |
| Built-in `startsWith` | O(n\*m)         | O(1)             | Cleanest solution              |
| Trie                  | O(n\*l + m)     | O(n\*l)          | Useful for many prefix queries |

---

# Recommended Solution

For this problem:

- input size is small
- only one prefix query exists

Therefore the **best approach** is:

```
Use built-in startsWith
```

It is:

- shortest code
- easiest to read
- already optimized.
