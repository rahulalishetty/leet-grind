# 3474. Lexicographically Smallest Generated String

## Problem Statement

You are given two strings:

- `str1` of length `n`
- `str2` of length `m`

We want to construct a string `word` of length:

```text
n + m - 1
```

such that for every index `i` from `0` to `n - 1`:

- if `str1[i] == 'T'`, then:

```text
word[i .. i + m - 1] == str2
```

- if `str1[i] == 'F'`, then:

```text
word[i .. i + m - 1] != str2
```

Return the **lexicographically smallest** valid `word`.

If no such string exists, return the empty string `""`.

---

## Example 1

```text
Input:
str1 = "TFTF"
str2 = "ab"

Output:
"ababa"
```

Explanation:

`word = "ababa"` gives:

| index | str1[i] | substring of length 2 |
| ----- | ------- | --------------------- |
| 0     | T       | "ab"                  |
| 1     | F       | "ba"                  |
| 2     | T       | "ab"                  |
| 3     | F       | "ba"                  |

This is valid, and it is lexicographically smaller than other valid answers such as `"ababb"`.

---

## Example 2

```text
Input:
str1 = "TFTF"
str2 = "abc"

Output:
""
```

Explanation:

The `T` constraints force overlapping copies of `"abc"` in a way that conflicts with each other, so no valid string exists.

---

## Example 3

```text
Input:
str1 = "F"
str2 = "d"

Output:
"a"
```

Explanation:

The only requirement is that the length-1 substring is **not** `"d"`.
The lexicographically smallest lowercase letter satisfying that is `"a"`.

---

## Constraints

- `1 <= n == str1.length <= 10^4`
- `1 <= m == str2.length <= 500`
- `str1` consists only of `'T'` and `'F'`
- `str2` consists only of lowercase English letters

---

# Core Insight

The target string has fixed length:

```text
L = n + m - 1
```

Every `T` at position `i` forces:

```text
word[i + j] = str2[j]   for all 0 <= j < m
```

So the first task is:

## apply all `T` constraints and check whether they are mutually consistent

If two `T` windows overlap and require different letters at the same position, the answer is impossible immediately.

After that, the remaining unfixed positions should be filled as small as possible lexicographically, which suggests:

```text
put 'a' wherever possible
```

But now the `F` windows matter:

- every `F` window must differ from `str2` in at least one position

So the real challenge is:

> choose letters for the unfixed positions so that every `F` window has at least one mismatch, while keeping the final string lexicographically smallest.

That is a constrained greedy construction problem.

---

# Step 1: Understand the Effect of `T` Constraints

If `str1[i] == 'T'`, then the substring starting at `i` must equal `str2`.

So for every `j`:

```text
word[i + j] = str2[j]
```

This is a hard equality constraint.

### Example

If:

```text
str1 = "TFT"
str2 = "ab"
```

then:

- `i = 0` forces `word[0] = 'a'`, `word[1] = 'b'`
- `i = 2` forces `word[2] = 'a'`, `word[3] = 'b'`

That immediately fixes many positions.

If two such constraints conflict, there is no answer.

---

# Step 2: Understand the Effect of `F` Constraints

If `str1[i] == 'F'`, then:

```text
word[i .. i + m - 1] != str2
```

This means:

- we do **not** need every character to differ
- we only need **at least one position** inside that window where the substring differs from `str2`

So each `F` window is satisfied if it contains **one mismatch witness**.

This is the central simplification.

---

# Approach 1: Brute Force (Conceptual Only)

## Intuition

Try all strings of length `n + m - 1`, check which satisfy the rules, and return the smallest.

This is obviously impossible.

The search space is:

```text
26^(n + m - 1)
```

which is astronomical.

---

## Verdict

Only useful as a mental baseline.

---

# Approach 2: Apply `T`, Fill Rest with `'a'`, Then Validate `F`

## Intuition

A natural first attempt is:

1. apply all `T` constraints
2. fill every remaining position with `'a'`
3. check whether all `F` constraints are satisfied

This often works, but not always.

Sometimes filling all free positions with `'a'` accidentally makes an `F` window equal to `str2`, and then we must selectively change some position inside that window.

So this gives a useful base string, but not the full solution.

---

## Example Where Simple Fill May Fail

Suppose after applying `T`, an `F` window has all but one position already equal to `str2`, and the last unfixed position also becomes equal when filled with `'a'`.

Then that window becomes invalid, and we must alter one of its flexible positions.

So a repair step is needed.

---

# Approach 3: Greedy Repair of Violating `F` Windows

## Intuition

After forcing all `T` windows, we start with the smallest possible string:

- forced positions keep their forced letters
- free positions are initialized to `'a'`

Then process `F` windows.

For an `F` window:

- if it already differs from `str2`, great
- if it exactly equals `str2`, we must break it by changing one free position in that window

To preserve lexicographic minimality, we should change the **rightmost possible free position** inside that window.

Why rightmost?

Because changing a later character hurts lexicographic order less than changing an earlier one.

Among replacement letters, choose the smallest letter different from the required `str2` character at that position.

Since the base fill is `'a'`, the minimal change is usually:

- keep `'a'` if `'a'` already differs from required char
- otherwise use `'b'`

However, there is a subtle problem:

changing a position to fix one `F` window can accidentally create issues for later constraints or interact with other windows.

So we need a disciplined strategy that remains globally correct.

---

# Key Observation for Correct Greedy Construction

If an `F` window currently equals `str2`, then to make it invalid we only need **one** mismatching position in that window.

Among all candidate free positions in the window, choosing the **rightmost** one is lexicographically optimal.

Also, once we introduce a mismatch witness into a window, that window is permanently safe.

This allows a left-to-right greedy strategy.

---

# Approach 4: Greedy Construction with Forced Positions and Window Repair

## Intuition

This is the best practical exact solution.

### Plan

1. Build an array `word` of length `L = n + m - 1`
2. Mark positions forced by `T`
3. Check conflicts among forced positions
4. Initialize all unforced positions to `'a'`
5. Process each `F` window:
   - if the window already differs from `str2`, continue
   - otherwise, find the rightmost unforced position in that window and change it minimally
   - if no such position exists, return `""`
6. After all repairs, return the resulting string

The crucial part is checking whether an `F` window is currently equal, and if so, breaking it with a lexicographically minimal change.

---

## Why Rightmost Free Position Is Optimal

Suppose an `F` window must be broken, and you can choose among several flexible positions.

Changing an earlier position affects lexicographic order more strongly than changing a later position.

So to keep the whole word as small as possible:

- delay the change as far right as possible

Then at that chosen position, use the smallest character that differs from the required `str2` character.

Since allowed characters are lowercase letters:

- if required char is not `'a'`, choose `'a'`
- otherwise choose `'b'`

---

## Java Code

```java
import java.util.*;

class Solution {
    public String generateString(String str1, String str2) {
        int n = str1.length();
        int m = str2.length();
        int L = n + m - 1;

        char[] word = new char[L];
        Arrays.fill(word, '?');
        boolean[] forced = new boolean[L];

        // Step 1: apply all T constraints
        for (int i = 0; i < n; i++) {
            if (str1.charAt(i) == 'T') {
                for (int j = 0; j < m; j++) {
                    int pos = i + j;
                    char need = str2.charAt(j);

                    if (word[pos] != '?' && word[pos] != need) {
                        return "";
                    }
                    word[pos] = need;
                    forced[pos] = true;
                }
            }
        }

        // Step 2: fill all remaining positions with 'a'
        for (int i = 0; i < L; i++) {
            if (word[i] == '?') {
                word[i] = 'a';
            }
        }

        // Step 3: repair violating F windows
        for (int i = 0; i < n; i++) {
            if (str1.charAt(i) == 'F') {
                if (windowEquals(word, i, str2)) {
                    int changePos = -1;

                    // choose rightmost unforced position in this window
                    for (int j = m - 1; j >= 0; j--) {
                        int pos = i + j;
                        if (!forced[pos]) {
                            changePos = pos;
                            break;
                        }
                    }

                    if (changePos == -1) {
                        return "";
                    }

                    int j = changePos - i;
                    char forbidden = str2.charAt(j);

                    // smallest lowercase char different from forbidden
                    word[changePos] = (forbidden == 'a') ? 'b' : 'a';
                }
            }
        }

        // Final validation
        for (int i = 0; i < n; i++) {
            boolean eq = windowEquals(word, i, str2);
            if (str1.charAt(i) == 'T' && !eq) return "";
            if (str1.charAt(i) == 'F' && eq) return "";
        }

        return new String(word);
    }

    private boolean windowEquals(char[] word, int start, String str2) {
        for (int j = 0; j < str2.length(); j++) {
            if (word[start + j] != str2.charAt(j)) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Complexity Analysis

Let:

- `n = str1.length()`
- `m = str2.length()`
- `L = n + m - 1`

### Time Complexity

Applying all `T` constraints:

```text
O(n * m)
```

Processing all `F` windows, each with equality check and possible repair scan:

```text
O(n * m)
```

Final validation:

```text
O(n * m)
```

Total:

```text
O(n * m)
```

This is acceptable because:

- `n <= 10^4`
- `m <= 500`

so worst case is about `5 * 10^6` operations scale.

### Space Complexity

```text
O(n + m)
```

more precisely `O(L)` for the target array and force markers.

---

## Verdict

This is the most practical exact solution.

---

# Approach 5: Constraint Graph / SAT-Style Thinking

## Intuition

You can interpret each `T` window as equality constraints and each `F` window as one “not-all-equal” condition.

That makes the problem feel like a constraint satisfaction problem.

However, because the alphabet is ordered and we want the lexicographically smallest solution, a SAT-like formalization is far heavier than needed.

The greedy constructive method already captures the structure cleanly.

---

## Verdict

Interesting perspective, but not the right implementation path.

---

# Why the Greedy Repair Is Correct

We need to justify two choices:

## 1. Why fill free positions with `'a'` first?

Because we want the lexicographically smallest string, and `'a'` is the smallest lowercase letter.

So unless a constraint forces otherwise, `'a'` is always the best initial choice.

## 2. Why repair a bad `F` window at the rightmost free position?

If an `F` window currently equals `str2`, then we must introduce one mismatch.

Changing any earlier position would make the final string lexicographically larger than necessary compared with changing a later position.

So the rightmost possible change is optimal.

Among letters for that position, pick the smallest valid one, which is the smallest lowercase letter different from the forbidden matching character.

That is exactly the lexicographically minimal repair.

---

# Common Mistakes

## 1. Not checking conflicts among overlapping `T` windows

Two `T` windows may overlap and require different letters at the same position.
That means the answer is immediately impossible.

---

## 2. Treating `F` as “all characters must differ”

That is wrong.

`F` only means the whole length-`m` substring must **not** equal `str2`.

So one mismatch is enough.

---

## 3. Repairing an `F` window at the leftmost free position

That makes the string larger lexicographically than necessary.

Use the rightmost free position.

---

## 4. Forgetting that a repaired position may affect multiple windows

That is allowed and often helpful.
A single mismatch can satisfy several `F` windows at once.

---

# Alternative Thought: Z / KMP for Faster Equality Checks

If one wanted to optimize the equality testing of windows against `str2`, one could use pattern matching tools like:

- Z-algorithm
- KMP

to precompute exact-match windows quickly.

However, since repairs modify the target string dynamically, those precomputations become less straightforward to maintain.

Because `m <= 500`, direct window checks are already fast enough.

So advanced string matching is unnecessary here.

---

# Final Recommended Solution

Use:

1. apply all `T` constraints
2. fill remaining positions with `'a'`
3. greedily repair any `F` window that still equals `str2` using the rightmost free position
4. validate everything

This yields the lexicographically smallest valid answer.

---

## Clean Final Java Solution

```java
import java.util.*;

class Solution {
    public String generateString(String str1, String str2) {
        int n = str1.length();
        int m = str2.length();
        int L = n + m - 1;

        char[] word = new char[L];
        Arrays.fill(word, '?');
        boolean[] forced = new boolean[L];

        for (int i = 0; i < n; i++) {
            if (str1.charAt(i) == 'T') {
                for (int j = 0; j < m; j++) {
                    int pos = i + j;
                    char ch = str2.charAt(j);

                    if (word[pos] != '?' && word[pos] != ch) {
                        return "";
                    }

                    word[pos] = ch;
                    forced[pos] = true;
                }
            }
        }

        for (int i = 0; i < L; i++) {
            if (word[i] == '?') {
                word[i] = 'a';
            }
        }

        for (int i = 0; i < n; i++) {
            if (str1.charAt(i) == 'F' && equalsAt(word, i, str2)) {
                int posToChange = -1;

                for (int j = m - 1; j >= 0; j--) {
                    int pos = i + j;
                    if (!forced[pos]) {
                        posToChange = pos;
                        break;
                    }
                }

                if (posToChange == -1) {
                    return "";
                }

                char forbidden = str2.charAt(posToChange - i);
                word[posToChange] = (forbidden == 'a') ? 'b' : 'a';
            }
        }

        for (int i = 0; i < n; i++) {
            boolean eq = equalsAt(word, i, str2);
            if (str1.charAt(i) == 'T' && !eq) return "";
            if (str1.charAt(i) == 'F' && eq) return "";
        }

        return new String(word);
    }

    private boolean equalsAt(char[] word, int start, String str2) {
        for (int j = 0; j < str2.length(); j++) {
            if (word[start + j] != str2.charAt(j)) {
                return false;
            }
        }
        return true;
    }
}
```

---

# Complexity Summary

## Brute Force Search Over All Possible Words

- Exponential, impossible

## Constraint-based greedy construction

- Time: `O(n * m)`
- Space: `O(n + m)`

## More advanced string-matching optimization

- Possible in theory for exact window checks
- Not necessary under given constraints

---

# Interview Summary

This is not a general string-generation search problem.

It becomes much simpler once you separate the two kinds of constraints:

- `T` windows are hard equality constraints
- `F` windows only require one mismatch somewhere

So the optimal strategy is:

- force all `T` characters
- put `'a'` everywhere else
- whenever an `F` window accidentally becomes equal to `str2`, break it at the rightmost flexible position using the smallest possible different character

That directly produces the lexicographically smallest valid string, if one exists.
