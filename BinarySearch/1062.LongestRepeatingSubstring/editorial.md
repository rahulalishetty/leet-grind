# Longest Repeating Substring — Overview and Approaches

## Overview

The problem asks for the **length of the longest repeating substring** within a given string `s`.

A repeating substring is defined as a **sequence of consecutive characters that appears more than once** in the original string.

### Example

```
Input: s = "abbaba"
Output: 2
```

In this string, there are multiple repeating substrings. The longest repeating substrings are **"ab"** and **"ba"**, each appearing twice.

Specifically:

- `"ab"` appears at the start of the string and again starting from the fourth character.
- `"ba"` appears starting from the third character and again starting from the fifth character.

Both substrings have length **2**, which is the maximum possible repeating substring length in this example.

If no repeating substring exists (e.g., all characters are unique), the function should return **0**.

---

# Approach 1: Brute Force with Set

## Intuition

We start by assuming the longest repeating substring could be as long as **n−1**, where `n` is the length of the string.

We test substrings starting from this maximum length and gradually decrease the length until a repeating substring is found.

A **set** is used to store substrings encountered during iteration.

### Idea

1. Extract substrings of length `maxLength`
2. Insert them into a set
3. If a substring already exists in the set → repetition found
4. Return that length

Example for `"abbaba"`:

Check substring lengths:

```
5 → none
4 → none
3 → none
2 → "ab" and "ba" repeat
```

So the answer is **2**.

---

## Algorithm

1. Initialize `seenSubstrings` as a set.
2. Set `maxLength = s.length() - 1`.
3. Iterate through possible starting indices.
4. Extract substring of length `maxLength`.
5. If substring already exists in the set → return `maxLength`.
6. If not found, decrease `maxLength` and repeat.

---

## Implementation

```java
class Solution {

    public int longestRepeatingSubstring(String s) {
        Set<String> seenSubstrings = new HashSet<>();
        int maxLength = s.length() - 1;

        for (int start = 0; start <= s.length(); start++) {
            int end = start;

            if (end + maxLength > s.length()) {
                if (--maxLength == 0) break;
                start = -1;
                seenSubstrings.clear();
                continue;
            }

            String currentSubstring = s.substring(end, end + maxLength);

            if (!seenSubstrings.add(currentSubstring)) {
                return maxLength;
            }
        }
        return maxLength;
    }
}
```

---

## Complexity Analysis

Let `n` be the string length.

### Time Complexity

```
O(n³)
```

- Up to `O(n²)` substrings
- Each substring extraction takes `O(n)`

### Space Complexity

```
O(n²)
```

Because up to `O(n²)` substrings may be stored in the set.

---

# Approach 2: Brute Force with Incremental Search

## Intuition

Instead of starting with the maximum length, we begin with the **shortest substring length (1)** and gradually increase it.

We test substrings of increasing lengths until no repetition exists.

### Idea

1. Start with `maxLength = 0`
2. Check substrings of length `maxLength + 1`
3. If repetition found → increase `maxLength`
4. Restart scanning
5. Stop when no repeating substring exists

---

## Implementation

```java
class Solution {

    public int longestRepeatingSubstring(String s) {
        int length = s.length(), maxLength = 0;
        Set<String> seenSubstrings = new HashSet<>();

        for (int start = 0; start < length; start++) {
            int end = start;

            if (end + maxLength >= length) {
                return maxLength;
            }

            String currentSubstring = s.substring(end, end + maxLength + 1);

            if (!seenSubstrings.add(currentSubstring)) {
                start = -1;
                seenSubstrings.clear();
                maxLength++;
            }
        }
        return maxLength;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(n³)
```

### Space Complexity

```
O(n²)
```

---

# Approach 3: Suffix Array with Sorting

## Intuition

The key idea:

> When suffixes of a string are sorted, similar prefixes appear next to each other.

Thus, we:

1. Generate all suffixes of the string
2. Sort them lexicographically
3. Compare adjacent suffixes
4. Find the longest common prefix (LCP)

The longest LCP is the answer.

Example suffixes for `"abbaba"`:

```
abbaba
bbaba
baba
aba
ba
a
```

After sorting:

```
a
abbaba
aba
ba
baba
bbaba
```

Longest common prefixes:

```
a
ab
ba
b
```

Answer = **2**.

---

## Implementation

```java
class Solution {

    public int longestRepeatingSubstring(String s) {
        int length = s.length();
        String[] suffixes = new String[length];

        for (int i = 0; i < length; i++) {
            suffixes[i] = s.substring(i);
        }

        Arrays.sort(suffixes);

        int maxLength = 0;

        for (int i = 1; i < length; i++) {
            int j = 0;
            while (
                j < Math.min(suffixes[i].length(), suffixes[i - 1].length()) &&
                suffixes[i].charAt(j) == suffixes[i - 1].charAt(j)
            ) {
                j++;
            }
            maxLength = Math.max(maxLength, j);
        }

        return maxLength;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(n² log n)
```

### Space Complexity

```
O(n²)
```

---

# Approach 4: Binary Search with Set

## Intuition

Binary search can determine the **maximum repeating substring length**.

Search range:

```
1 → n-1
```

For a candidate length `mid`:

- Check if any substring of length `mid` repeats.
- Use a set to detect duplicates.

If repetition exists → search higher length
Otherwise → search lower length.

---

## Implementation

```java
class Solution {

    public int longestRepeatingSubstring(String s) {
        char[] characters = s.toCharArray();
        int start = 1, end = characters.length - 1;

        while (start <= end) {
            int mid = (start + end) / 2;

            if (hasRepeatingSubstring(characters, mid)) {
                start = mid + 1;
            } else {
                end = mid - 1;
            }
        }
        return start - 1;
    }

    private boolean hasRepeatingSubstring(char[] characters, int length) {
        Set<String> seenSubstrings = new HashSet<>();

        for (int i = 0; i <= characters.length - length; i++) {
            String substring = new String(characters, i, length);

            if (!seenSubstrings.add(substring)) {
                return true;
            }
        }
        return false;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(n² log n)
```

### Space Complexity

```
O(n²)
```

---

# Approach 5: Dynamic Programming

## Intuition

Dynamic Programming computes the **longest common suffix** between all pairs of substrings.

Let:

```
dp[i][j]
```

represent the length of the **longest common suffix** of substrings ending at `i` and `j`.

If:

```
s[i-1] == s[j-1]
```

then:

```
dp[i][j] = dp[i-1][j-1] + 1
```

Track the maximum value in the DP table.

---

## Implementation

```java
class Solution {

    public int longestRepeatingSubstring(String s) {
        int length = s.length();
        int[][] dp = new int[length + 1][length + 1];
        int maxLength = 0;

        for (int i = 1; i <= length; i++) {
            for (int j = i + 1; j <= length; j++) {

                if (s.charAt(i - 1) == s.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    maxLength = Math.max(maxLength, dp[i][j]);
                }
            }
        }
        return maxLength;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(n²)
```

### Space Complexity

```
O(n²)
```

---

# Approach 6: MSD Radix Sort

## Intuition

MSD (Most Significant Digit) Radix Sort sorts suffixes based on characters starting from the **most significant character**.

Steps:

1. Generate suffix array
2. Sort suffixes using MSD radix sort
3. Compare adjacent suffixes
4. Compute longest common prefix

This groups similar prefixes together efficiently.

---

## Complexity Analysis

### Time Complexity

```
O(n²)
```

### Space Complexity

```
O(n²)
```

---

# Editorial Notes

This problem is **Medium** because `O(n²)` solutions are acceptable.

However, the related problem:

```
1044. Longest Duplicate Substring
```

is **Hard** and requires faster algorithms such as:

- Binary Search + Rabin-Karp (Rolling Hash)
- Advanced Suffix Array algorithms
- Ukkonen’s algorithm

These approaches achieve:

```
O(n log n) or O(n)
```

time complexity.
