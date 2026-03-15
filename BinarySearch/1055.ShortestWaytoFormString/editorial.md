# 1055. Shortest Way to Form String — Overview and Approaches

## Overview

In this problem, we are given two strings, `source` and `target`.

We need to find the minimum number of subsequences of `source` such that their concatenation equals `target`. If the task is impossible, return `-1`.

Since the problem statement involves subsequences, readers are advised to attempt **392. Is Subsequence** before attempting this problem, to understand the algorithm for checking subsequences.

---

## Reframing the Problem

We can rephrase the problem as follows:

> Given two strings, `source` and `target`, find the minimum number of times we need to concatenate `source` such that `target` is a subsequence of the concatenated string.

### Explanation of the Rephrasing

We can say that each of the subsequences in the optimal answer is obtained from one unit of `source`. If there are minimum `k` subsequences, then we can think of their concatenation as a subsequence of `"k units of source concatenated together"`.

Therefore, if the concatenation of `k` subsequences of `source` is equal to `target`, then we need to find the minimum `k` such that `target` is a subsequence of `k` concatenated copies of `source`.

### Example

If:

```text
source = "abc"
target = "abcbc"
```

Then we can obtain `target` by concatenating 2 subsequences of `source`, namely:

```text
"abc" and "bc"
```

This can be rephrased as:

```text
target = "abcbc"
```

is a subsequence of:

```text
"abc" + "abc"
```

from the first unit we obtain `"abc"` and from the second unit we obtain `"bc"`.

This rephrasing helps clarify the structure of the problem.

---

## When is the Task Impossible?

Let the lengths of `source` and `target` be `S` and `T`.

We cannot conclude possibility just from lengths:

- If `source` is shorter than or equal to `target`, the answer may still exist or not.
- If `source` is longer than `target`, the answer may still exist or not.

Examples:

```text
source = "abcd", target = "abc"   => possible
source = "abcd", target = "abx"   => impossible
```

Similarly, if `source` contains characters not in `target`, that tells us nothing, because subsequences can skip characters.

Examples:

```text
source = "abcd", target = "abca"  => possible
source = "abcd", target = "abx"   => impossible
```

However, if `target` contains a character not present in `source`, then the task is definitely impossible.

### Proposition

> The task is possible if and only if all characters of `target` are present in `source`.

If this condition holds, then an answer always exists.

Also, if an answer exists, it is at most `T`, because each copy of `source` can contribute at least one character toward `target`.

---

## Notation

Throughout the article:

- `S` denotes the length of `source`
- `T` denotes the length of `target`
- In code:
  - `m` often denotes length of `source`
  - `n` often denotes length of `target`

By constraints, neither is zero.

---

# Approach 1: Brute Force

## Intuition

A brute force method would generate all subsequences of `source`.

There are:

```text
2^S
```

possible subsequences, since each character is either included or not included.

Then we would try all possible concatenations of these subsequences and check if any equals `target`.

This is completely infeasible for:

```text
1 <= S <= 1000
```

---

# Approach 2: Concatenate Until Subsequence

## Intuition

Using the rephrased problem, we can keep concatenating `source` until `target` becomes a subsequence of the concatenated string.

This is greedy: we try to cover as much of `target` as possible with each copy of `source`.

If the task is possible, this returns the optimal answer.

## Algorithm

1. Build a boolean table for characters present in `source`.
2. If any character in `target` is missing from `source`, return `-1`.
3. Let `concatenatedSource = source`, `count = 1`.
4. While `target` is not a subsequence of `concatenatedSource`:
   - concatenate another copy of `source`
   - increment `count`
5. Return `count`.

## Implementation

```java
class Solution {
    public int shortestWay(String source, String target) {

        // Boolean array to mark all characters of source
        boolean[] sourceChars = new boolean[26];
        for (char c : source.toCharArray()) {
            sourceChars[c - 'a'] = true;
        }

        // Check if all characters of the target are present in the source
        // If any character is not present, return -1
        for (char c : target.toCharArray()) {
            if (!sourceChars[c - 'a']) {
                return -1;
            }
        }

        // Concatenate source until the target is a subsequence of the concatenated string
        String concatenatedSource = source;
        int count = 1;
        while (!isSubsequence(target, concatenatedSource)) {
            concatenatedSource += source;
            count++;
        }

        // Number of concatenations done
        return count;
    }

    // To check if toCheck is a subsequence of the inString
    public boolean isSubsequence(String toCheck, String inString) {
        int i = 0, j = 0;
        while (i < toCheck.length() && j < inString.length()) {
            if (toCheck.charAt(i) == inString.charAt(j)) {
                i++;
            }
            j++;
        }

        return i == toCheck.length();
    }
}
```

## Complexity Analysis

Time complexity:

```text
O(T^2 * S)
```

Breakdown:

- `O(S)` to mark characters of `source`
- `O(T)` to verify all target characters exist
- Up to `T` concatenations
- Each subsequence check may take `O(T*S)`
- Each concatenation may also cost up to `O(T*S)` in immutable-string languages

Overall:

```text
O(T^2 * S)
```

Space complexity:

```text
O(T * S)
```

because `concatenatedSource` may grow to length `T * S`.

---

# Approach 3: Two Pointers

## Intuition

Instead of actually concatenating `source`, we can conceptually loop around it.

We use:

- one pointer for `target`
- one pointer for `source`

Whenever the source pointer wraps back to the beginning, we count one more subsequence.

This avoids building the concatenated string and still greedily matches as much as possible in each pass.

## Algorithm

1. Check if all target characters exist in source. If not, return `-1`.
2. Initialize:
   - `sourceIterator = 0`
   - `count = 0`
   - `m = source.length()`
3. For each character `c` in `target`:
   - if `sourceIterator == 0`, increment `count`
   - move `sourceIterator` forward until `source[sourceIterator] == c`
   - wrap using modulo
   - if wrapping occurs, increment `count`
4. After matching the character, advance `sourceIterator`.
5. Return `count`.

## Implementation

```java
class Solution {
    public int shortestWay(String source, String target) {

        // Boolean array to mark all characters of source
        boolean[] sourceChars = new boolean[26];
        for (char c : source.toCharArray()) {
            sourceChars[c - 'a'] = true;
        }

        // Check if all characters of target are present in source
        // If any character is not present, return -1
        for (char c : target.toCharArray()) {
            if (!sourceChars[c - 'a']) {
                return -1;
            }
        }

        // Length of source to loop back to start of source using mod
        int m = source.length();

        // Pointer for source
        int sourceIterator = 0;

        // Number of times source is traversed
        int count = 0;

        // Find all characters of target in source
        for (char c : target.toCharArray()) {

            // If while finding, the iterator reaches the start of source again,
            // increment count
            if (sourceIterator == 0) {
                count++;
            }

            // Find the first occurrence of c in source
            while (source.charAt(sourceIterator) != c) {

                // Formula for incrementing while looping back to start.
                sourceIterator = (sourceIterator + 1) % m;

                // If while finding, iterator reaches start of source again,
                // increment count
                if (sourceIterator == 0) {
                    count++;
                }
            }

            // Character found, advance iterator
            sourceIterator = (sourceIterator + 1) % m;
        }

        return count;
    }
}
```

## Complexity Analysis

Time complexity:

```text
O(S * T)
```

- `O(S)` to build source character table
- `O(T)` to verify target feasibility
- for each target character, the internal scan may take up to `O(S)`

Space complexity:

```text
O(1)
```

---

# Approach 4: Inverted Index and Binary Search

## Intuition

The expensive part of the two-pointer approach is finding the next occurrence of a character in `source`.

We can preprocess `source`:

- For each character, store all indices where it appears.
- Then use binary search to find the smallest index `>= sourceIterator`.

If none exists, wrap around and use the first occurrence.

This reduces lookup from `O(S)` to `O(log S)`.

## Algorithm

1. Build `charToIndices[26]`, where each entry stores sorted indices of that character in `source`.
2. Initialize:
   - `sourceIterator = 0`
   - `count = 1`
3. For each character `c` in `target`:
   - if no indices list exists for `c`, return `-1`
   - binary search for first index `>= sourceIterator`
   - if found:
     - set `sourceIterator = foundIndex + 1`
   - else:
     - wrap around
     - increment `count`
     - set `sourceIterator = firstIndex + 1`
4. Return `count`

## Implementation

```java
class Solution {
    public int shortestWay(String source, String target) {

        // List of indices for all characters in source
        ArrayList<Integer>[] charToIndices = new ArrayList[26];
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (charToIndices[c - 'a'] == null) {
                charToIndices[c - 'a'] = new ArrayList<>();
            }
            charToIndices[c - 'a'].add(i);
        }

        // Pointer for source
        int sourceIterator = 0;

        // Number of times we need to iterate through source
        int count = 1;

        // Find all characters of target in source
        for (char c : target.toCharArray()) {

            // If the character is not in the source, return -1
            if (charToIndices[c - 'a'] == null) {
                return -1;
            }

            // Binary search to find the index of the character in source
            // next to the source iterator
            ArrayList<Integer> indices = charToIndices[c - 'a'];
            int index = Collections.binarySearch(indices, sourceIterator);

            // If the index is negative, we need to find the next index
            // that is greater than the source iterator
            if (index < 0) {
                index = -index - 1;
            }

            // If we have reached the end of the list, loop around
            if (index == indices.size()) {
                count++;
                sourceIterator = indices.get(0) + 1;
            } else {
                sourceIterator = indices.get(index) + 1;
            }
        }

        return count;
    }
}
```

## Complexity Analysis

Time complexity:

```text
O(S + T log S)
```

- `O(S)` preprocessing
- `O(T)` iterations over target
- each performs binary search in up to `O(log S)`

Space complexity:

```text
O(S)
```

---

# Approach 5: 2D Array

## Intuition

Can we do even better than binary search?

Yes. We can preprocess, for every source index and every character, the next occurrence of that character at or after that index.

Let:

```text
nextOccurrence[idx][c]
```

store the earliest index `>= idx` where character `c` appears, or `-1` if it does not appear.

Then each lookup becomes `O(1)`.

## Recurrence

For index `idx` going from right to left:

- if `source[idx] == c`, then:

```text
nextOccurrence[idx][c] = idx
```

- otherwise:

```text
nextOccurrence[idx][c] = nextOccurrence[idx + 1][c]
```

## Algorithm

1. Create `nextOccurrence[S][26]`, initialize with `-1`.
2. Fill base case for the last character.
3. Fill the table backward using the recurrence.
4. Initialize:
   - `sourceIterator = 0`
   - `count = 1`
5. For each character `c` in `target`:
   - if `nextOccurrence[0][c] == -1`, return `-1`
   - if `sourceIterator == source.length()` or `nextOccurrence[sourceIterator][c] == -1`:
     - increment `count`
     - reset `sourceIterator = 0`
   - set `sourceIterator = nextOccurrence[sourceIterator][c] + 1`
6. Return `count`

## Implementation

```java
class Solution {
    public int shortestWay(String source, String target) {

        // Next occurrence of a character after a given index
        int[][] nextOccurrence = new int[source.length()][26];

        // Base Case
        for (int c = 0; c < 26; c++) {
            nextOccurrence[source.length() - 1][c] = -1;
        }
        nextOccurrence[source.length() - 1][source.charAt(source.length() - 1) - 'a'] = source.length() - 1;

        // Fill using recurrence relation
        for (int idx = source.length() - 2; idx >= 0; idx--) {
            for (int c = 0; c < 26; c++) {
                nextOccurrence[idx][c] = nextOccurrence[idx + 1][c];
            }
            nextOccurrence[idx][source.charAt(idx) - 'a'] = idx;
        }

        // Pointer to the current index in source
        int sourceIterator = 0;

        // Number of times we need to iterate through source
        int count = 1;

        // Find all characters of target in source
        for (char c : target.toCharArray()) {

            // If the character is not present in source
            if (nextOccurrence[0][c - 'a'] == -1) {
                return -1;
            }

            // If we have reached the end of source, or the character is not in
            // source after source_iterator, loop back to beginning
            if (sourceIterator == source.length() || nextOccurrence[sourceIterator][c - 'a'] == -1) {
                count++;
                sourceIterator = 0;
            }

            // Next occurrence of character in source after source_iterator
            sourceIterator = nextOccurrence[sourceIterator][c - 'a'] + 1;
        }

        return count;
    }
}
```

## Complexity Analysis

Time complexity:

```text
O(S + T)
```

More precisely:

```text
O(26 * S + T)
```

which simplifies to:

```text
O(S + T)
```

Space complexity:

```text
O(S)
```

More precisely:

```text
O(26 * S)
```

---

## Alternate 2D Representation

Another valid implementation stores:

```text
nextOccurrence[char][index]
```

instead of:

```text
nextOccurrence[index][char]
```

This also gives `O(1)` next-occurrence lookup and the same asymptotic complexity.

### Implementation

```java
class Solution {
    public int shortestWay(String source, String target) {

        // Length of source
        int sourceLength = source.length();

        // Next Occurrence of Character after Index
        int[][] nextOccurrence = new int[26][sourceLength];
        for (int[] row : nextOccurrence) {
            Arrays.fill(row, -1);
        }

        // Base Case
        nextOccurrence[source.charAt(sourceLength - 1) - 'a'][sourceLength - 1] = sourceLength - 1;

        // Using Recurrence Relation to fill nextOccurrence
        for (int charIndex = 0; charIndex < 26; charIndex++) {
            char englishChar = (char) (charIndex + 'a');
            for (int index = sourceLength - 2; index >= 0; index--) {
                if (source.charAt(index) == englishChar) {
                    nextOccurrence[charIndex][index] = index;
                } else {
                    nextOccurrence[charIndex][index] = nextOccurrence[charIndex][index + 1];
                }
            }
        }

        // Pointer for source
        int sourceIterator = 0;

        // Number of times we need to iterate through source
        int count = 1;

        // Try to find all characters in target in source
        for (char charToFind : target.toCharArray()) {

            // Scaling character to 0-25
            int charIndex = charToFind - 'a';

            // If character is not in source, return -1
            if (nextOccurrence[charIndex][0] == -1) {
                return -1;
            }

            // If we have reached the end of source, or character is not in
            // source after sourceIterator, loop back to the beginning
            if (sourceIterator == sourceLength ||
                    nextOccurrence[charIndex][sourceIterator] == -1) {
                count++;
                sourceIterator = 0;
            }

            // Next occurrence of character in source after sourceIterator
            sourceIterator = nextOccurrence[charIndex][sourceIterator] + 1;
        }

        return count;
    }
}
```

---

# Approach 6: Top-Down Dynamic Programming (TLE)

## Intuition

This is an optimization problem, so dynamic programming is a natural thought.

Let:

```text
opt(i)
```

represent the minimum number of subsequences needed to form:

```text
target[0:i]
```

If `target[j:i]` is a subsequence of `source`, then one candidate solution is:

```text
1 + opt(j - 1)
```

This gives the recurrence.

## Recurrence

```text
opt(target[0:i]) = 1 + min(opt(target[0:j]))
such that target[j:i] is a subsequence of source
```

Base case:

```text
opt(0) = 1
```

if the character exists in source.

## Implementation

```java
class Solution {
    public int shortestWay(String source, String target) {

        // Boolean array to mark all characters of source
        boolean[] sourceChars = new boolean[26];
        for (char c : source.toCharArray()) {
            sourceChars[c - 'a'] = true;
        }

        // Check if all characters of target are present in source
        // If any character is not present, return -1
        for (char c : target.toCharArray()) {
            if (!sourceChars[c - 'a']) {
                return -1;
            }
        }

        // Optimal Answer for a given ending index. Memoizing using an Array
        int[] memo = new int[target.length()];
        Arrays.fill(memo, Integer.MAX_VALUE / 2);

        // Want to find optimal answer for the last index.
        return optimalAnswer(target.length() - 1, memo, source, target);
    }

    public int optimalAnswer(int endingIndex, int[] memo, String source, String target) {

        // Base Case
        if (endingIndex == 0) {
            return 1;
        }

        // If already calculated, return
        if (memo[endingIndex] != Integer.MAX_VALUE / 2) {
            return memo[endingIndex];
        }

        // If subsequence, return 1
        if (isSubsequence(0, endingIndex, source, target)) {
            memo[endingIndex] = 1;
            return 1;
        }

        // If not subsequence, partition into two parts and find minimum
        int answer = Integer.MAX_VALUE / 2;

        for (int partitionIndex = 0; partitionIndex < endingIndex; partitionIndex++) {

            if (optimalAnswer(partitionIndex, memo, source, target) + 1 < answer
                    && isSubsequence(partitionIndex + 1, endingIndex, source, target)) {
                answer = Math.min(answer, optimalAnswer(partitionIndex, memo, source, target) + 1);
            }
        }

        // Memoize and return
        memo[endingIndex] = answer;
        return answer;
    }

    // For to_check, passing indices of target, both included.
    public boolean isSubsequence(int start, int end, String toCheck, String inString) {
        int i = start;
        int j = 0;

        while (i <= end && j < inString.length()) {
            if (toCheck.charAt(i) == inString.charAt(j)) {
                i++;
            }
            j++;
        }

        return i == end + 1;
    }
}
```

## Complexity Analysis

Time complexity:

```text
O(T^2 * S)
```

Space complexity:

```text
O(T)
```

including memo array and recursion stack.

---

# Approach 7: Bottom-Up Dynamic Programming (TLE)

## Intuition

Instead of recursion, we can tabulate from smaller prefixes to larger prefixes.

Let:

```text
dp[i]
```

store the minimum subsequences required to form:

```text
target[0:i]
```

Base case:

```text
dp[0] = 1
```

Transition:

- If `target[0:i]` is a subsequence of `source`, then `dp[i] = 1`
- Else try all partition points `j < i` such that `target[j+1:i]` is a subsequence of `source`

## Implementation

```java
class Solution {
    public int shortestWay(String source, String target) {

        // Boolean array to mark all characters of source
        boolean[] sourceChars = new boolean[26];
        for (char c : source.toCharArray()) {
            sourceChars[c - 'a'] = true;
        }

        // Check if all characters of target are present in source
        // If any character is not present, return -1
        for (char c : target.toCharArray()) {
            if (!sourceChars[c - 'a']) {
                return -1;
            }
        }

        // Optimal Answer for a given ending index. Memoizing using an Array
        int[] memo = new int[target.length()];
        Arrays.fill(memo, Integer.MAX_VALUE);
        memo[0] = 1;

        for (int endingIndex = 1; endingIndex < target.length(); endingIndex++) {
            if (isSubsequence(0, endingIndex, source, target)) {
                memo[endingIndex] = 1;
            } else {
                for (int partitionIndex = endingIndex - 1; partitionIndex >= 0; partitionIndex--) {
                    if (memo[partitionIndex] != Integer.MAX_VALUE &&
                            isSubsequence(partitionIndex + 1, endingIndex, source, target)) {
                        memo[endingIndex] = Math.min(memo[endingIndex], memo[partitionIndex] + 1);
                    }
                }
            }
        }

        return memo[target.length() - 1];
    }

    // For to_check, passing indices of target, both included.
    public boolean isSubsequence(int start, int end, String source, String target) {
        int i = start;
        int j = 0;

        while (i <= end && j < source.length()) {
            if (target.charAt(i) == source.charAt(j)) {
                i++;
            }
            j++;
        }

        return i == end + 1;
    }
}
```

## Complexity Analysis

Time complexity:

```text
O(T^2 * S)
```

Space complexity:

```text
O(T)
```

---

## Closing Note

Although dynamic programming formulations are possible, they are redundant here. The key structural insight is that the optimal answers are monotonically non-decreasing, which is why the greedy family of approaches works well.

Among the practical solutions:

- **Two Pointers** gives `O(S * T)` with constant extra space
- **Inverted Index + Binary Search** gives `O(S + T log S)`
- **2D Next Occurrence Array** gives `O(S + T)` time with `O(S)` extra space
