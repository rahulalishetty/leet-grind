# Longest Palindromic Substring — Exhaustive Notes

## Problem Context

Given a string `s`, the goal is to find the **longest palindromic substring**.

A **palindrome** is a string that reads the same forward and backward.

Examples:

- `"racecar"` is a palindrome
- `"abba"` is a palindrome
- `"abc"` is not a palindrome

---

## Approach 1: Check All Substrings

### Intuition

The most direct way to solve the problem is brute force:

1. Generate every possible substring.
2. Check whether each substring is a palindrome.
3. Keep the longest one.

A useful optimization is to check **longer substrings first**.
That way, as soon as we find a palindrome, we can return it immediately because no longer valid answer can exist.

---

### How to Check Whether a String Is a Palindrome

Use the classic **two-pointer technique**:

- Put one pointer at the start
- Put another pointer at the end
- Compare characters
- If they differ, it is not a palindrome
- If they match, move inward and continue

This is efficient in practice because many non-palindromes fail early.

For example, in a string of length 1000, if the third and third-last characters differ, we stop after only a few checks.

---

### Algorithm

#### Helper Function: `check(i, j)`

This helper determines whether the substring `s[i...j-1]` is a palindrome.

- Initialize:
  - `left = i`
  - `right = j - 1`
- While `left < right`:
  - If `s[left] != s[right]`, return `false`
  - Otherwise:
    - `left++`
    - `right--`
- If the loop completes, return `true`

#### Main Procedure

- Loop over substring lengths from `s.length()` down to `1`
- For each length:
  - Try every starting position
  - Check if that substring is a palindrome
  - If yes, immediately return it

---

### Java Implementation

```java
class Solution {
    public String longestPalindrome(String s) {
        for (int length = s.length(); length > 0; length--) {
            for (int start = 0; start <= s.length() - length; start++) {
                if (check(start, start + length, s)) {
                    return s.substring(start, start + length);
                }
            }
        }

        return "";
    }

    private boolean check(int i, int j, String s) {
        int left = i;
        int right = j - 1;

        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }

            left++;
            right--;
        }

        return true;
    }
}
```

---

### Complexity Analysis

Let `n = s.length()`.

#### Time Complexity: `O(n^3)`

Why?

- There are `O(n^2)` substrings in total
- Each palindrome check may take `O(n)` time

So the total becomes:

```text
O(n^2) * O(n) = O(n^3)
```

A more careful view:

- 1 substring of length `n`
- 2 substrings of length `n - 1`
- 3 substrings of length `n - 2`
- ...
- `n` substrings of length `1`

This is quadratic many substrings, and each may cost linear time to validate.

#### Space Complexity: `O(1)`

We only use a few variables.
The returned substring is not counted as extra space.

---

### Strengths and Weaknesses

#### Strengths

- Very easy to understand
- Good starting point for reasoning
- Nice baseline solution

#### Weaknesses

- Too slow for large inputs
- Repeats a lot of work

---

## Approach 2: Dynamic Programming

### Core Idea

Suppose we already know that substring `s[i+1...j-1]` is a palindrome.

Then, if:

```text
s[i] == s[j]
```

the larger substring `s[i...j]` must also be a palindrome.

So we get the recurrence:

```text
dp[i][j] = true if s[i] == s[j] && dp[i+1][j-1]
```

This lets us avoid rechecking the whole substring from scratch.

---

### DP Definition

Let:

```text
dp[i][j] = whether substring s[i...j] (inclusive) is a palindrome
```

This is a boolean table of size `n x n`.

---

### Base Cases

#### Length 1

Every single character is a palindrome:

```text
dp[i][i] = true
```

#### Length 2

A substring of length 2 is a palindrome if both characters match:

```text
dp[i][i+1] = (s[i] == s[i+1])
```

---

### Transition

For substrings of length 3 or more:

```text
dp[i][j] = (s[i] == s[j]) && dp[i+1][j-1]
```

This works because:

- the outer characters must match
- the inner substring must already be a palindrome

---

### Why the Order of Filling Matters

`dp[i][j]` depends on `dp[i+1][j-1]`, so we must compute smaller substrings first.

That means:

- first fill length 1
- then length 2
- then length 3
- then length 4
- and so on

Equivalent looping strategy:

- fix `diff = j - i`
- process from smaller `diff` to larger `diff`

---

### Algorithm

1. Let `n = s.length()`
2. Create `boolean[][] dp = new boolean[n][n]`
3. Initialize all `dp[i][i] = true`
4. Check all adjacent pairs for length 2 palindromes
5. For increasing substring lengths:
   - compute `j = i + diff`
   - if `s[i] == s[j] && dp[i+1][j-1]`, mark `dp[i][j] = true`
6. Track the longest palindrome seen so far
7. Return the final substring

---

### Java Implementation

```java
class Solution {
    public String longestPalindrome(String s) {
        int n = s.length();
        boolean[][] dp = new boolean[n][n];
        int[] ans = new int[] { 0, 0 };

        for (int i = 0; i < n; i++) {
            dp[i][i] = true;
        }

        for (int i = 0; i < n - 1; i++) {
            if (s.charAt(i) == s.charAt(i + 1)) {
                dp[i][i + 1] = true;
                ans[0] = i;
                ans[1] = i + 1;
            }
        }

        for (int diff = 2; diff < n; diff++) {
            for (int i = 0; i < n - diff; i++) {
                int j = i + diff;
                if (s.charAt(i) == s.charAt(j) && dp[i + 1][j - 1]) {
                    dp[i][j] = true;
                    ans[0] = i;
                    ans[1] = j;
                }
            }
        }

        int i = ans[0];
        int j = ans[1];
        return s.substring(i, j + 1);
    }
}
```

---

### Walkthrough Example

Take:

```text
s = "babad"
```

#### Length 1

Every single character is a palindrome.

#### Length 2

- `"ba"` → no
- `"ab"` → no
- `"ba"` → no
- `"ad"` → no

#### Length 3

- `"bab"` → yes, because:
  - first and last characters are both `'b'`
  - middle `"a"` is a palindrome
- `"aba"` → yes
- `"bad"` → no

Now the answer becomes either `"bab"` or `"aba"` depending on update order.

---

### Complexity Analysis

#### Time Complexity: `O(n^2)`

- There are `n x n` DP states
- Each state takes `O(1)` time to compute

#### Space Complexity: `O(n^2)`

The DP table stores `n^2` booleans.

---

### Strengths and Weaknesses

#### Strengths

- Much better than brute force
- Systematic and easy to reason about
- Standard interval DP pattern

#### Weaknesses

- Uses `O(n^2)` memory
- Still iterates over all substring boundaries

---

## Approach 3: Expand From Centers

### Key Insight

A palindrome mirrors around its center.

Instead of asking:

> “Is this substring a palindrome?”

we ask:

> “How far can I expand outward from this center while keeping a palindrome?”

This changes the perspective completely.

---

### Why Centers Help

There are `O(n^2)` substrings, but only `O(n)` possible centers.

For every index:

- odd-length palindrome center: `(i, i)`
- even-length palindrome center: `(i, i + 1)`

So total centers:

```text
n + (n - 1) = 2n - 1 = O(n)
```

For each center, expand outward until mismatch.

This avoids filling an entire DP table and is typically much faster in practice.

---

### Odd-Length and Even-Length Palindromes

#### Odd-Length Example

For `"racecar"` the center is the middle `'e'`.

Start at `(i, i)` and expand:

- `e`
- `cec`
- `aceca`
- `racecar`

#### Even-Length Example

For `"abba"` the center lies between the two `'b'` characters.

Start at `(i, i + 1)` and expand:

- `bb`
- `abba`

So we must handle both cases.

---

### Helper Function: `expand(i, j)`

This helper expands while:

- `left >= 0`
- `right < s.length()`
- `s[left] == s[right]`

When expansion stops, the valid palindrome is one step inside the failed boundary.

That is why the returned length is:

```text
right - left - 1
```

not `right - left + 1`.

---

### Recovering Bounds from Length

Suppose we expand from center `(i, i)` and get an odd palindrome length.

Then:

```text
dist = length / 2
left = i - dist
right = i + dist
```

For even length from center `(i, i + 1)`:

```text
dist = (length / 2) - 1
left = i - dist
right = i + 1 + dist
```

These formulas reconstruct the exact substring boundaries.

---

### Algorithm

1. Initialize answer bounds
2. For every index `i`:
   - expand around odd center `(i, i)`
   - update answer if needed
   - expand around even center `(i, i + 1)`
   - update answer if needed
3. Return the substring using final bounds

---

### Java Implementation

```java
class Solution {
    public String longestPalindrome(String s) {
        int[] ans = new int[] { 0, 0 };

        for (int i = 0; i < s.length(); i++) {
            int oddLength = expand(i, i, s);
            if (oddLength > ans[1] - ans[0] + 1) {
                int dist = oddLength / 2;
                ans[0] = i - dist;
                ans[1] = i + dist;
            }

            int evenLength = expand(i, i + 1, s);
            if (evenLength > ans[1] - ans[0] + 1) {
                int dist = (evenLength / 2) - 1;
                ans[0] = i - dist;
                ans[1] = i + 1 + dist;
            }
        }

        int i = ans[0];
        int j = ans[1];
        return s.substring(i, j + 1);
    }

    private int expand(int i, int j, String s) {
        int left = i;
        int right = j;

        while (
            left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)
        ) {
            left--;
            right++;
        }

        return right - left - 1;
    }
}
```

---

### Walkthrough Example

Take:

```text
s = "cbbd"
```

#### Center at index 1 and 2

This is the even center between the two `'b'` characters.

- Start with `"bb"`
- Expand outward:
  - left = `'c'`
  - right = `'d'`
  - mismatch

So the longest palindrome from this center is `"bb"`.

That becomes the final answer.

---

### Complexity Analysis

#### Time Complexity: `O(n^2)`

- There are `O(n)` centers
- Each expansion may cost up to `O(n)`

Worst case:

```text
"aaaaaa..."
```

Every center expands almost fully.

#### Space Complexity: `O(1)`

Only a few variables are used.

---

### Why This Is Often Preferred

Even though it has the same worst-case time complexity as the DP solution, it is usually preferred in interviews because:

- it is simpler
- it uses constant extra space
- it performs very well in practice

For most real strings, expansions terminate quickly.

---

## Approach 4: Manacher’s Algorithm

### High-Level Idea

Manacher’s Algorithm solves the longest palindromic substring problem in **linear time**.

This is a major improvement over the `O(n^2)` solutions.

However, the algorithm is much more specialized and significantly harder to derive in an interview.

---

### Why It Is Hard

The main challenge with palindromes is that there are two types of centers:

- odd-length centers
- even-length centers

Manacher’s algorithm eliminates this distinction by transforming the string.

Example:

```text
"abba" -> "#a#b#b#a#"
```

Now every palindrome has a unified center form.

The algorithm then cleverly reuses previously computed palindrome information using mirror symmetry.

---

### Key Structures

#### Transformed String

A delimiter such as `#` is inserted between all characters and at both ends.

This makes odd and even cases uniform.

#### `palindromeRadii`

For each position in the transformed string, store how far the palindrome expands around that center.

#### Active Center and Right Boundary

The algorithm keeps track of:

- current center
- rightmost known palindrome boundary

This allows mirrored reuse and prevents redundant comparisons.

---

### Java Implementation

```java
class Solution {
    public String longestPalindrome(String s) {
        StringBuilder sPrime = new StringBuilder("#");
        for (char c : s.toCharArray()) {
            sPrime.append(c).append("#");
        }

        int n = sPrime.length();
        int[] palindromeRadii = new int[n];
        int center = 0;
        int radius = 0;

        for (int i = 0; i < n; i++) {
            int mirror = 2 * center - i;

            if (i < radius) {
                palindromeRadii[i] = Math.min(
                    radius - i,
                    palindromeRadii[mirror]
                );
            }

            while (
                i + 1 + palindromeRadii[i] < n &&
                i - 1 - palindromeRadii[i] >= 0 &&
                sPrime.charAt(i + 1 + palindromeRadii[i]) ==
                    sPrime.charAt(i - 1 - palindromeRadii[i])
            ) {
                palindromeRadii[i]++;
            }

            if (i + palindromeRadii[i] > radius) {
                center = i;
                radius = i + palindromeRadii[i];
            }
        }

        int maxLength = 0;
        int centerIndex = 0;
        for (int i = 0; i < n; i++) {
            if (palindromeRadii[i] > maxLength) {
                maxLength = palindromeRadii[i];
                centerIndex = i;
            }
        }

        int startIndex = (centerIndex - maxLength) / 2;
        String longestPalindrome = s.substring(
            startIndex,
            startIndex + maxLength
        );

        return longestPalindrome;
    }
}
```

---

### Complexity Analysis

#### Time Complexity: `O(n)`

Manacher’s algorithm achieves true linear time.

The reason is that the algorithm avoids repeating expansion work by reusing mirrored palindrome information and by ensuring the right boundary only moves forward.

#### Space Complexity: `O(n)`

We store:

- the transformed string
- the palindrome radii array

both of which are linear in size.

---

### Practical Note

This algorithm is usually **not expected in interviews** unless the interviewer explicitly wants advanced string algorithms.

Knowing that it exists is useful.
Implementing it under pressure usually is not.

---

## Comparison of All Approaches

| Approach             | Time     | Space    | Main Idea                                    | Practicality                |
| -------------------- | -------- | -------- | -------------------------------------------- | --------------------------- |
| Check All Substrings | `O(n^3)` | `O(1)`   | Generate every substring and test palindrome | Good for understanding only |
| Dynamic Programming  | `O(n^2)` | `O(n^2)` | Build palindrome truth table                 | Good but memory-heavy       |
| Expand From Centers  | `O(n^2)` | `O(1)`   | Expand around each center                    | Best interview choice       |
| Manacher’s Algorithm | `O(n)`   | `O(n)`   | Reuse mirrored palindrome radii              | Advanced / specialized      |

---

## Which Approach Should You Use?

### For Learning

Start with:

1. brute force
2. dynamic programming
3. expand from centers

This progression teaches the problem deeply.

### For Interviews

Use **Expand From Centers** unless the interviewer specifically asks for something more advanced.

Why?

- simple
- elegant
- no extra memory
- easy to explain
- fast enough for normal constraints

### For Algorithm Enthusiasm

Study **Manacher’s Algorithm** after you fully understand center expansion.

It is best viewed as an optimization built on the same center-based insight.

---

## Important Conceptual Progression

This problem is a good example of algorithmic refinement:

### Stage 1: Brute Force

Check every substring and validate each one independently.

### Stage 2: Dynamic Programming

Reuse already known results about smaller substrings.

### Stage 3: Center Expansion

Stop thinking in terms of all substring boundaries and instead focus on the much smaller set of possible centers.

### Stage 4: Manacher

Exploit symmetry and reuse at a deeper level to reduce the total work to linear time.

That progression is the real lesson behind this problem.

---

## Final Recommendation

If the goal is to write a clean, interview-ready solution:

- choose **Expand From Centers**

If the goal is to fully understand the problem space:

- study all four approaches in sequence

If the goal is cutting-edge optimal performance:

- learn **Manacher’s Algorithm**, but only after the center-expansion method feels natural.

---

## Summary

- A palindrome mirrors around its center.
- Brute force checks every substring: simple but slow.
- DP reduces repeated work by storing palindrome states.
- Expand-from-centers is usually the best balance of simplicity and performance.
- Manacher’s algorithm gives linear time, but it is advanced and rarely required in normal interviews.
