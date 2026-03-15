# 2168. Unique Substrings With Equal Digit Frequency

## Overview

We are given a string `s` consisting of digits (`'0'` to `'9'`). Our task is to calculate the number of **unique substrings** of `s` where every digit present in the substring occurs the **same number of times**.

For example, in the string `s = "1212"`, the substrings `"1212"` and `"12"` satisfy the condition because the digits in each of these substrings occur with equal frequency.

Notice that the substring `"12"` appears twice in the string, but it should be counted **once** in the result.

On the other hand, `"121"` does not satisfy the condition because `'1'` occurs twice while `'2'` occurs once.

---

# Approach 1: Optimized Brute Force

## Intuition

In the brute-force approach, we iterate over **all substrings** of `s`, calculate the **frequency of digits** for each substring, and increment a counter if the substring satisfies the equal-frequency condition.

To avoid counting duplicates, we store substrings in a **set**.

However, recomputing frequencies repeatedly is inefficient. Instead, we maintain a **frequency array** for substrings starting at the same index and update it incrementally as the substring expands.

---

## Algorithm

1. Initialize `n` to the length of the string `s`.
2. Create an empty set `validSubstrings`.
3. Iterate over each starting index `start` from `0` to `n-1`:
   - Initialize a frequency array `digitFrequency` of size `10`.
4. For each ending index `end` from `start` to `n-1`:
   - Increment frequency of the current digit.
   - Check if all appearing digits have the same frequency.
   - If valid, add the substring to the set.
5. Return the size of `validSubstrings`.

---

## Implementation

```java
class Solution {

    public int equalDigitFrequency(String s) {
        int n = s.length();
        Set<String> validSubstrings = new HashSet<>();

        for (int start = 0; start < n; start++) {
            int[] digitFrequency = new int[10];

            for (int end = start; end < n; end++) {
                digitFrequency[s.charAt(end) - '0']++;

                int commonFrequency = 0;
                boolean isValid = true;

                for (int i = 0; i < digitFrequency.length; i++) {
                    if (digitFrequency[i] == 0) continue;

                    if (commonFrequency == 0) {
                        commonFrequency = digitFrequency[i];
                    }

                    if (commonFrequency != digitFrequency[i]) {
                        isValid = false;
                        break;
                    }
                }

                if (isValid) {
                    String substring = s.substring(start, end + 1);
                    validSubstrings.add(substring);
                }
            }
        }

        return validSubstrings.size();
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(n^3)
```

- Two nested loops generate all substrings.
- Substring extraction and hashing each cost `O(k)` where `k` can be `n`.

### Space Complexity

```
O(n^3)
```

- Up to `O(n^2)` substrings stored.
- Each substring may take up to `O(n)` space.

---

# Approach 2: Rolling Hash

## Intuition

Extracting substrings and hashing them repeatedly is expensive. Instead, we use a **rolling hash** to represent substrings efficiently.

A rolling hash updates the hash value incrementally as characters are added.

Instead of recalculating the hash for every substring, we compute:

```
newHash = (base * previousHash + value) % mod
```

Using a **prime base** helps reduce hash collisions.

---

## Algorithm

1. Initialize:
   - `prime = 31`
   - `mod = 10^9`
2. Maintain:
   - `digitFrequency[10]`
   - `uniqueDigitsCount`
   - `maxDigitFrequency`
3. Extend substring while updating:
   - rolling hash
   - digit frequencies
4. If

```
maxDigitFrequency * uniqueDigitsCount == substring length
```

the substring is valid.

5. Insert the hash into a set.

---

## Implementation

```java
class Solution {

    public int equalDigitFrequency(String s) {
        int n = s.length();
        int prime = 31;
        long mod = 1000000000L;

        Set<Long> validSubstringHashes = new HashSet<>();

        for (int start = 0; start < n; start++) {
            int[] digitFrequency = new int[10];
            int uniqueDigitsCount = 0;
            long substringHash = 0;
            int maxDigitFrequency = 0;

            for (int end = start; end < n; end++) {
                int currentDigit = s.charAt(end) - '0';

                if (digitFrequency[currentDigit] == 0) {
                    uniqueDigitsCount++;
                }

                digitFrequency[currentDigit]++;
                maxDigitFrequency = Math.max(maxDigitFrequency, digitFrequency[currentDigit]);

                substringHash = (prime * substringHash + currentDigit + 1) % mod;

                if (maxDigitFrequency * uniqueDigitsCount == end - start + 1) {
                    validSubstringHashes.add(substringHash);
                }
            }
        }

        return validSubstringHashes.size();
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(n^2)
```

All substrings are explored but each update is constant time.

### Space Complexity

```
O(n^2)
```

The hash set may store up to `O(n^2)` unique substrings.

---

# Approach 3: Prefix Tree (Trie)

## Intuition

Rolling hash may suffer from **hash collisions**.

To avoid this randomness, we can store substrings in a **Trie (Prefix Tree)**.

Each node represents a substring prefix.

The Trie allows efficient substring storage without relying on hashing.

Each node contains:

- `children[10]` for digits
- `isVisited` flag to ensure uniqueness

---

## Algorithm

1. Create a Trie root.
2. Iterate over each substring start index.
3. Extend the substring character by character.
4. Update:
   - digit frequencies
   - unique digit count
   - maximum frequency
5. Move through Trie nodes representing digits.
6. If the substring is valid and not yet visited:
   - increment result
   - mark node as visited.

---

## Implementation

```java
class Solution {

    public int equalDigitFrequency(String s) {
        TrieNode root = new TrieNode();
        int totalValidSubstrings = 0;

        for (int start = 0; start < s.length(); start++) {
            TrieNode currentNode = root;
            int[] digitFrequency = new int[10];
            int uniqueDigitsCount = 0;
            int maxDigitFrequency = 0;

            for (int end = start; end < s.length(); end++) {
                int currentDigit = s.charAt(end) - '0';

                if (digitFrequency[currentDigit]++ == 0) {
                    uniqueDigitsCount++;
                }

                maxDigitFrequency = Math.max(maxDigitFrequency, digitFrequency[currentDigit]);

                if (currentNode.children[currentDigit] == null) {
                    currentNode.children[currentDigit] = new TrieNode();
                }

                currentNode = currentNode.children[currentDigit];

                if (uniqueDigitsCount * maxDigitFrequency == end - start + 1 && !currentNode.isVisited) {
                    totalValidSubstrings++;
                    currentNode.isVisited = true;
                }
            }
        }

        return totalValidSubstrings;
    }

    class TrieNode {
        TrieNode[] children = new TrieNode[10];
        boolean isVisited;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(n^2)
```

Every substring is processed with constant time updates.

### Space Complexity

```
O(n^2)
```

In the worst case, the Trie stores all substrings.
