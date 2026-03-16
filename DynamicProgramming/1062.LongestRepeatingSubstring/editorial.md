# 1062. Longest Repeating Substring — Detailed Notes

This document converts the provided explanation into a detailed Markdown note.

---

# Problem Recap

Given a string `s`, return the **length** of the longest substring that appears **at least twice** in the string.

A repeating substring:

- must be **contiguous**
- may appear more than twice
- overlapping occurrences are allowed

If no repeating substring exists, return `0`.

---

# Example

## Input

```text
s = "abbaba"
```

## Output

```text
2
```

## Explanation

The longest repeating substrings are:

- `"ab"`
- `"ba"`

Each appears twice, so the answer is:

```text
2
```

---

# What Makes This Problem Interesting

This problem is about repeated **substrings**, not subsequences.

That means adjacency matters.

For a string of length `n`, there are many possible substrings, and checking whether one repeats can be expensive if done naively.

Because the constraint is:

```text
1 <= s.length <= 2000
```

an `O(n^2)` solution is acceptable, while an `O(n^3)` solution may still be understandable but is less efficient.

---

# Overview of Approaches

The provided material discusses six approaches:

1. Brute Force with Set
2. Brute Force with Incremental Search
3. Suffix Array with Sorting
4. Binary Search with Set
5. Dynamic Programming
6. MSD Radix Sort

We will go through each in detail.

---

# Approach 1: Brute Force with Set

## Intuition

Start by assuming the longest repeating substring might be as large as possible.

If the string length is `n`, then the longest possible repeating substring length is:

```text
n - 1
```

because a substring of length `n` would just be the whole string, and it cannot repeat at a different starting position.

So the plan is:

1. Try all substrings of length `n - 1`
2. Check whether any of them repeats
3. If not, try length `n - 2`
4. Continue decreasing until a repeated substring is found

As soon as one repeated substring is found for a certain length, that length is the answer, because we are searching from longest to shortest.

---

## How the Set Helps

For a fixed substring length `L`:

- scan all substrings of length `L`
- insert each substring into a hash set
- if a substring is already present in the set, it repeats

Then we can immediately return `L`.

---

## Java Implementation

```java
class Solution {

    public int longestRepeatingSubstring(String s) {
        Set<String> seenSubstrings = new HashSet<>();
        int maxLength = s.length() - 1;

        for (int start = 0; start <= s.length(); start++) {
            int end = start;
            // If the remaining substring is shorter than maxLength,
            // reset the loop
            if (end + maxLength > s.length()) {
                if (--maxLength == 0) break;
                start = -1;
                seenSubstrings.clear();
                continue;
            }
            // Extract substring of length maxLength
            String currentSubstring = s.substring(end, end + maxLength);
            // If the substring is already in the set,
            // it means we've found a repeating substring
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

Let `n` be the length of the string.

### Time Complexity

There are up to `O(n^2)` substrings checked across all lengths.

Each substring extraction and insertion/comparison can cost up to `O(n)`.

So the overall complexity is:

```text
O(n^3)
```

### Space Complexity

The set may store many substrings.

In the worst case, the total space used is:

```text
O(n^2)
```

---

## Summary

This approach is easy to understand but slow.

It is mainly useful as a baseline.

---

# Approach 2: Brute Force with Incremental Search

## Intuition

Instead of searching from the largest possible length downward, we can search in the opposite direction.

Start with substring length:

```text
1
```

Then:

- if a repeating substring of length `1` exists, try length `2`
- if a repeating substring of length `2` exists, try length `3`
- continue until a length fails
- the previous successful length is the answer

So this is still brute force, but the search order is reversed.

---

## Why This Works

Suppose length `L` has a repeating substring.

Then the longest repeating substring is at least `L`.

So we keep extending the candidate length until it becomes impossible to find any repeated substring.

The first failure tells us the previous length was optimal.

---

## Java Implementation

```java
class Solution {

    public int longestRepeatingSubstring(String s) {
        int length = s.length(), maxLength = 0;
        Set<String> seenSubstrings = new HashSet<>();

        for (int start = 0; start < length; start++) {
            int end = start;
            // Stop if it's not possible to find a longer repeating substring
            if (end + maxLength >= length) {
                return maxLength;
            }
            // Generate substrings of length maxLength + 1
            String currentSubstring = s.substring(end, end + maxLength + 1);
            // If a repeating substring is found, increase maxLength and restart
            if (!seenSubstrings.add(currentSubstring)) {
                start = -1; // Restart search for new length
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

Just like the first brute force method, many substrings are generated, and each extraction may cost `O(n)`.

Overall:

```text
O(n^3)
```

### Space Complexity

The set of substrings can again use up to:

```text
O(n^2)
```

space.

---

## Summary

This is another brute force variant with the same asymptotic complexity as Approach 1.

The difference is only the search direction.

---

# Approach 3: Suffix Array with Sorting

## Intuition

This approach uses a classic string observation:

> If two substrings share a long common prefix, then the suffixes starting at their positions will also share that prefix.

So instead of checking all substrings directly, we do this:

1. Generate all suffixes of the string
2. Sort the suffixes lexicographically
3. Compare adjacent suffixes
4. The longest common prefix between any two adjacent suffixes gives a candidate repeating substring
5. Take the maximum such common prefix length

---

## Why Adjacent Sorted Suffixes Matter

When suffixes are sorted, those with similar beginnings appear next to each other.

So if some substring repeats, then there exist at least two suffixes that begin with that substring, and those suffixes will become neighbors or near-neighbors in sorted order.

Thus, it is enough to compare consecutive sorted suffixes.

---

## Example

For:

```text
s = "abbaba"
```

Suffixes are:

- `"abbaba"`
- `"bbaba"`
- `"baba"`
- `"aba"`
- `"ba"`
- `"a"`

After sorting, we compare neighboring suffixes and compute their longest common prefixes.

The maximum longest common prefix length is the answer.

---

## Java Implementation

```java
class Solution {

    public int longestRepeatingSubstring(String s) {
        int length = s.length();
        String[] suffixes = new String[length];

        // Create suffix array
        for (int i = 0; i < length; i++) {
            suffixes[i] = s.substring(i);
        }
        // Sort the suffixes
        Arrays.sort(suffixes);

        int maxLength = 0;
        // Find the longest common prefix between consecutive sorted suffixes
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

1. Building all suffixes costs:

```text
O(n^2)
```

because there are `n` suffixes and each may be up to length `n`.

2. Sorting suffixes costs:

```text
O(n^2 log n)
```

because comparing strings may take `O(n)` time.

3. Computing longest common prefixes of adjacent suffixes costs:

```text
O(n^2)
```

So the overall complexity is dominated by sorting:

```text
O(n^2 log n)
```

### Space Complexity

All suffixes are stored explicitly, so:

```text
O(n^2)
```

space is required.

---

## Summary

This is a much more structured approach than brute force and introduces suffix-based thinking, but it is still not optimal because explicit suffix storage is expensive.

---

# Approach 4: Binary Search with Set

## Intuition

This approach uses a very important observation:

> If there exists a repeating substring of length `L`, then there also exists a repeating substring of every smaller length.

That means the property:

```text
"Does there exist a repeating substring of length L?"
```

is monotonic.

So we can binary search on the length.

---

## Binary Search Structure

Search range:

```text
1 to n - 1
```

For a candidate length `mid`:

- generate all substrings of length `mid`
- store them in a set
- if any duplicate appears, then a repeating substring of length `mid` exists

If yes:

- search larger lengths

If no:

- search smaller lengths

At the end, the maximum successful length is the answer.

---

## Java Implementation

```java
class Solution {

    public int longestRepeatingSubstring(String s) {
        char[] characters = s.toCharArray();
        int start = 1, end = characters.length - 1;

        while (start <= end) {
            int mid = (start + end) / 2;
            // Check if there's a repeating substring of length mid
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
        // Check for repeating substrings of given length
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

Binary search contributes:

```text
O(log n)
```

Each check for a fixed length builds up to `O(n)` substrings, each costing `O(n)` in creation/comparison in the worst case.

So each check costs:

```text
O(n^2)
```

Total:

```text
O(n^2 log n)
```

### Space Complexity

The set may store many substrings, so:

```text
O(n^2)
```

---

## Summary

This is an elegant improvement over naive brute force because the search over length becomes logarithmic.

Still, substring creation is expensive, so it is not optimal in practice compared with rolling-hash based methods used in harder variants.

---

# Approach 5: Dynamic Programming

## Intuition

This approach is based on comparing suffixes ending at different positions.

Let:

```text
dp[i][j]
```

represent the length of the longest common suffix of:

- the prefix ending at `i - 1`
- the prefix ending at `j - 1`

If `s[i - 1] == s[j - 1]`, then the common suffix can be extended from:

```text
dp[i - 1][j - 1]
```

So:

```text
dp[i][j] = dp[i - 1][j - 1] + 1
```

The maximum value in the DP table is the answer.

---

## Important Interpretation

A repeating substring corresponds to a common suffix between two prefixes ending at different positions.

If two suffixes ending at different indices share a long common suffix, then that shared suffix is a repeated substring.

---

## Example

For:

```text
s = "aabcaabdaab"
```

the repeated substring `"aab"` appears multiple times.

The DP table will capture the length of matching suffixes between different positions, and the largest such match is `3`.

---

## Java Implementation

```java
class Solution {

    public int longestRepeatingSubstring(String s) {
        int length = s.length();
        int[][] dp = new int[length + 1][length + 1];
        int maxLength = 0;

        // Use DP to find the longest common substring
        for (int i = 1; i <= length; i++) {
            for (int j = i + 1; j <= length; j++) {
                // If characters match, extend the length of
                // the common substring
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

We fill a triangular portion of an `n x n` table:

```text
O(n^2)
```

### Space Complexity

The DP table has size:

```text
O(n^2)
```

---

## Summary

This is one of the cleanest accepted approaches for this problem under the given constraints.

It is conceptually simpler than suffix-array-based techniques and runs in `O(n^2)` time.

---

# Approach 6: MSD Radix Sort

## Intuition

MSD Radix Sort means **Most Significant Digit Radix Sort**.

It sorts strings by:

1. first character
2. then recursively by second character for equal-first-character groups
3. then by third character, and so on

The key use here is the same as with suffix sorting:

> Once suffixes are sorted, repeated prefixes become adjacent.

So the algorithm is:

1. Generate all suffixes
2. Sort them using MSD radix sort
3. Compare adjacent suffixes
4. Return the maximum common prefix length

---

## Why MSD Radix Sort Is Relevant

For alphabet-based strings like lowercase English letters, radix-based sorting can be effective because:

- the alphabet is small
- sorting by character positions can avoid some generic comparison overhead

Still, since suffixes are stored explicitly as strings, the worst-case complexity here remains quadratic.

---

## Java Implementation

```java
class Solution {

    public int longestRepeatingSubstring(String s) {
        int length = s.length();
        String[] suffixes = new String[length];

        // Create suffix array
        for (int i = 0; i < length; i++) {
            suffixes[i] = s.substring(i);
        }
        // Sort the suffix array using MSD Radix Sort
        msdRadixSort(suffixes);

        int maxLength = 0;
        // Find the longest common prefix between consecutive sorted suffixes
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

    // Main method to perform MSD Radix Sort
    private void msdRadixSort(String[] input) {
        sort(input, 0, input.length - 1, 0, new String[input.length]);
    }

    // Helper method for sorting
    private void sort(String[] input, int lo, int hi, int depth, String[] aux) {
        if (lo >= hi) return;

        int[] count = new int[28];
        for (int i = lo; i <= hi; i++) {
            count[charAt(input[i], depth) + 1]++;
        }
        for (int i = 1; i < 28; i++) {
            count[i] += count[i - 1];
        }
        for (int i = lo; i <= hi; i++) {
            aux[count[charAt(input[i], depth)]++] = input[i];
        }
        for (int i = lo; i <= hi; i++) {
            input[i] = aux[i - lo];
        }
        for (int i = 0; i < 27; i++) {
            sort(input, lo + count[i], lo + count[i + 1] - 1, depth + 1, aux);
        }
    }

    // Returns the character value or 0 if index exceeds string length
    private int charAt(String s, int index) {
        if (index >= s.length()) return 0;
        return s.charAt(index) - 'a' + 1;
    }
}
```

---

## Complexity Analysis

### Time Complexity

The major work is:

- generating all suffixes: `O(n^2)`
- sorting suffixes using MSD radix sort: worst-case `O(n^2)`
- longest common prefix comparisons: `O(n^2)`

So the total remains:

```text
O(n^2)
```

### Space Complexity

Suffix storage dominates:

```text
O(n^2)
```

---

## Summary

This is another suffix-sorting approach, but using MSD radix sort instead of generic comparison sort.

It is more specialized, but still quadratic in this implementation.

---

# Comparing the Approaches

| Approach                  | Main Idea                                | Time Complexity | Space Complexity |
| ------------------------- | ---------------------------------------- | --------------: | ---------------: |
| Brute Force with Set      | Check all lengths from large to small    |        `O(n^3)` |         `O(n^2)` |
| Brute Force Incremental   | Check all lengths from small to large    |        `O(n^3)` |         `O(n^2)` |
| Suffix Array with Sorting | Sort explicit suffixes, compare adjacent |  `O(n^2 log n)` |         `O(n^2)` |
| Binary Search with Set    | Binary search on answer length           |  `O(n^2 log n)` |         `O(n^2)` |
| Dynamic Programming       | Longest common suffix DP                 |        `O(n^2)` |         `O(n^2)` |
| MSD Radix Sort            | Radix-sort suffixes, compare adjacent    |        `O(n^2)` |         `O(n^2)` |

---

# Which Approach Is Best Here?

Given the constraint:

```text
n <= 2000
```

the **dynamic programming approach** is often the most practical:

- easy to understand
- easy to implement
- runs in `O(n^2)`
- fits the constraint comfortably

The suffix-based methods are conceptually important, especially as a bridge to harder problems, but they require more machinery.

---

# Further Thoughts

The provided editorial notes an important distinction:

This problem is the **medium** version, where `O(n^2)` is acceptable.

A related harder problem is:

```text
1044. Longest Duplicate Substring
```

That version typically requires more advanced methods such as:

- binary search + Rabin-Karp rolling hash
- advanced suffix array techniques
- suffix automaton / suffix tree style methods

Those methods are better suited for much larger input sizes.

---

# Final Takeaways

## 1. Repeating substring problems often reduce to comparing suffixes

This is why suffix arrays and sorting appear naturally.

## 2. DP works because repeated substrings correspond to shared suffix structure

If two positions end matching substrings, we can extend from earlier matches.

## 3. Binary search works when feasibility is monotonic

If length `L` repeats, then every smaller length also repeats.

## 4. For this problem size, `O(n^2)` is enough

That makes the dynamic programming method especially attractive.

## 5. Harder variants require stronger tools

Rolling hash and suffix array optimizations are natural next steps beyond this problem.

---

# Final Insight

The cleanest way to think about this problem is:

> We want the maximum overlap between substrings starting or ending at different places.

Different approaches express this in different forms:

- brute force checks substrings directly
- suffix methods sort suffixes and compare neighbors
- DP measures shared suffix growth
- binary search asks whether a repeated length is feasible

Among them, the **dynamic programming solution** is usually the best balance of simplicity and efficiency for this specific problem.
