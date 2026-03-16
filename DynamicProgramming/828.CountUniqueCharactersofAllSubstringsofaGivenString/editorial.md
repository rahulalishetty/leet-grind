# 828. Count Unique Characters of All Substrings of a Given String — Exhaustive Solution Notes

## Overview

We are asked to compute:

```text
sum(countUniqueChars(t)) for every substring t of s
```

where `countUniqueChars(t)` means:

> the number of characters that appear **exactly once** in substring `t`

This is a classic contribution-counting problem.

A brute-force solution would enumerate all substrings and count unique characters in each, but that is far too slow for:

```text
|s| <= 10^5
```

The key idea is to stop thinking substring-by-substring and instead count how much each occurrence of each character contributes to the final answer.

This write-up explains the two accepted linear-time approaches:

1. **Maintain Answer of Suffix**
2. **Split by Character**

Both run in:

```text
O(N)
```

time.

---

## Problem Statement

Define:

```text
countUniqueChars(s)
```

as the number of characters that appear exactly once in `s`.

Given a string `s`, return the sum of `countUniqueChars(t)` over **all substrings** `t` of `s`.

Repeated substrings are counted separately if they occur at different positions.

The answer fits in a 32-bit integer.

---

## Example 1

**Input**

```text
s = "ABC"
```

**Output**

```text
10
```

**Explanation**

All substrings are:

```text
"A", "B", "C", "AB", "BC", "ABC"
```

Each substring consists only of unique letters, so the counts are:

```text
1 + 1 + 1 + 2 + 2 + 3 = 10
```

---

## Example 2

**Input**

```text
s = "ABA"
```

**Output**

```text
8
```

**Explanation**

Substrings:

```text
"A", "B", "A", "AB", "BA", "ABA"
```

Unique-character counts:

- `"A"` → 1
- `"B"` → 1
- `"A"` → 1
- `"AB"` → 2
- `"BA"` → 2
- `"ABA"` → only `"B"` is unique → 1

Total:

```text
1 + 1 + 1 + 2 + 2 + 1 = 8
```

---

## Example 3

**Input**

```text
s = "LEETCODE"
```

**Output**

```text
92
```

---

## Constraints

- `1 <= s.length <= 10^5`
- `s` consists of uppercase English letters only

---

# Core Observation

Let:

```text
U(x)
```

be the number of unique characters in string `x`.

Then we can decompose it by character:

```text
U(x) = sum(U_c(x)) over all characters c
```

where:

```text
U_c(x) = 1 if c appears exactly once in x, else 0
```

This is extremely important.

It means:

> Instead of counting unique characters for each substring directly, we can count how many substrings contain each character exactly once.

Then sum over all characters.

This transforms the problem into a character-contribution problem.

---

# Approach 1: Maintain Answer of Suffix

## Intuition

Suppose we fix a starting index `i`.

Let:

```text
F(i)
```

be the sum of unique-character counts over all substrings that start at index `i`.

If we could compute every `F(i)` efficiently, then the answer would be:

```text
F(0) + F(1) + F(2) + ... + F(n-1)
```

The trick is to maintain `F(i)` incrementally as `i` moves from left to right.

---

## Contribution by One Character

Consider a specific character, say `'A'`.

Suppose `'A'` appears in the string at positions:

```text
10, 14, 20
```

Now imagine we are looking at substrings starting at some fixed `i`.

For `'A'` to be unique in a substring starting at `i`, the substring must include exactly one of these occurrences.

For example, if `i = 8`, then the first `'A'` available is at `10`, and the next `'A'` after that is at `14`.

Any substring starting at `8` and ending at:

```text
10, 11, 12, 13
```

contains exactly one `'A'`.

That gives:

```text
14 - 10 = 4
```

possible substrings where `'A'` contributes 1.

So for each character, the current contribution to `F(i)` is:

```text
nextOccurrence - currentOccurrence
```

where “currentOccurrence” is the first occurrence of that character not before `i`.

---

## Data Structures Used

For each character `c`, store a sorted list of indices where it appears.

Example:

```text
S = "ABA"
```

Then:

- `index['A'] = [0, 2]`
- `index['B'] = [1]`

To simplify boundary handling, append `N` twice to each list.

Why twice?

Because when we ask for:

```text
index[i+1] - index[i]
```

we want it to remain valid even near the end.

---

## Pointer `peek[c]`

For each character `c`, maintain:

```text
peek[c]
```

which tells us which occurrence of `c` is currently the first occurrence not before the current starting index.

Then the contribution of character `c` to the current suffix-answer `F(i)` is:

```text
index[c][peek[c] + 1] - index[c][peek[c]]
```

As the start index moves from `i` to `i + 1`, only one character changes its contribution:

- the character `s[i]`

That is why the overall answer can be updated in constant time per index.

---

## Step-by-Step Logic

### Step 1: Build occurrence lists

For each character, store all positions where it occurs.

### Step 2: Initialize current suffix answer

For each character, add:

```text
index[c][1] - index[c][0]
```

to the current value `cur`.

This gives `F(0)`.

### Step 3: Sweep the string

For each character `c = s[i]`:

1. add current `cur` to the final answer
2. remove old contribution of `c`
3. advance `peek[c]`
4. add new contribution of `c`

This updates `cur` from `F(i)` to `F(i+1)`.

---

## Why This Works

When the substring starting point advances by one position, almost all character contributions remain unchanged.

Only the character that just got skipped from the left boundary may have a different “first active occurrence.”

That makes the update very efficient.

---

## Java Implementation — Maintain Answer of Suffix

```java
class Solution {
    Map<Character, List<Integer>> index;
    int[] peek;
    int N;

    public int uniqueLetterString(String S) {
        index = new HashMap<>();
        peek = new int[26];
        N = S.length();

        for (int i = 0; i < S.length(); ++i) {
            char c = S.charAt(i);
            index.computeIfAbsent(c, x -> new ArrayList<Integer>()).add(i);
        }

        long cur = 0, ans = 0;
        for (char c : index.keySet()) {
            index.get(c).add(N);
            index.get(c).add(N);
            cur += get(c);
        }

        for (char c : S.toCharArray()) {
            ans += cur;
            long oldv = get(c);
            peek[c - 'A']++;
            cur += get(c) - oldv;
        }

        return (int) ans % 1_000_000_007;
    }

    public long get(char c) {
        List<Integer> indexes = index.get(c);
        int i = peek[c - 'A'];
        return indexes.get(i + 1) - indexes.get(i);
    }
}
```

---

## Complexity Analysis — Maintain Answer of Suffix

### Time Complexity

Each index is processed once.

Each pointer `peek[c]` only moves forward through that character’s occurrence list.

So the total time complexity is:

```text
O(N)
```

---

### Space Complexity

We store all occurrence indices, so space usage is:

```text
O(N)
```

---

# Approach 2: Split by Character

## Intuition

This is the cleaner and more standard contribution-counting solution.

Instead of maintaining suffix answers, directly compute how much each occurrence of each character contributes to the final total.

Suppose character `c` appears at positions:

```text
... prev, curr, next ...
```

We ask:

> In how many substrings is the occurrence at `curr` the **unique** occurrence of character `c`?

To make `curr` the unique occurrence of `c`, the substring must:

- start after `prev`
- end before `next`

So the number of valid substrings is:

```text
(curr - prev) * (next - curr)
```

This gives the contribution of that occurrence.

Then sum this over all occurrences of all characters.

---

## Why the Formula Works

Take one occurrence of a character at position `curr`.

Let:

- `prev` = previous occurrence of the same character
- `next` = next occurrence of the same character

For `curr` to be the only occurrence of that character inside a substring:

### Left boundary

The substring can start anywhere from:

```text
prev + 1 to curr
```

That gives:

```text
curr - prev
```

choices.

### Right boundary

The substring can end anywhere from:

```text
curr to next - 1
```

That gives:

```text
next - curr
```

choices.

Multiply them:

```text
(curr - prev) * (next - curr)
```

That is exactly the number of substrings where this occurrence contributes 1.

---

## Example

Suppose:

```text
S = "ABA"
```

Occurrences of `'A'` are at:

```text
0, 2
```

### Contribution of `A` at index 0

- `prev = -1`
- `curr = 0`
- `next = 2`

Contribution:

```text
(0 - (-1)) * (2 - 0) = 1 * 2 = 2
```

Those substrings are:

```text
"A", "AB"
```

### Contribution of `A` at index 2

- `prev = 0`
- `curr = 2`
- `next = 3` (string length)

Contribution:

```text
(2 - 0) * (3 - 2) = 2 * 1 = 2
```

Those substrings are:

```text
"BA", "A"
```

### Contribution of `B` at index 1

- `prev = -1`
- `curr = 1`
- `next = 3`

Contribution:

```text
(1 - (-1)) * (3 - 1) = 2 * 2 = 4
```

Those substrings are:

```text
"B", "AB", "BA", "ABA"
```

Total:

```text
2 + 2 + 4 = 8
```

which matches the answer.

---

## Algorithm

1. Build occurrence lists for each character.
2. For each occurrence list:
   - for each position `A[i]`
   - let:
     - `prev = A[i-1]` or `-1` if none
     - `next = A[i+1]` or `N` if none
   - add:
     ```text
     (A[i] - prev) * (next - A[i])
     ```
     to the answer
3. Return the final sum

---

## Java Implementation — Split by Character

```java
class Solution {
    public int uniqueLetterString(String S) {
        Map<Character, List<Integer>> index = new HashMap<>();
        for (int i = 0; i < S.length(); ++i) {
            char c = S.charAt(i);
            index.computeIfAbsent(c, x -> new ArrayList<Integer>()).add(i);
        }

        long ans = 0;
        for (List<Integer> A : index.values()) {
            for (int i = 0; i < A.size(); ++i) {
                long prev = i > 0 ? A.get(i - 1) : -1;
                long next = i < A.size() - 1 ? A.get(i + 1) : S.length();
                ans += (A.get(i) - prev) * (next - A.get(i));
            }
        }

        return (int) ans % 1_000_000_007;
    }
}
```

---

## Complexity Analysis — Split by Character

### Time Complexity

Each character occurrence is processed exactly once.

So total time complexity is:

```text
O(N)
```

---

### Space Complexity

We store all occurrence positions:

```text
O(N)
```

The editorial notes that this could be reduced to `O(A)` where `A` is alphabet size if contributions were computed online, but with the stored lists the bound is:

```text
O(N)
```

---

# Why Approach 2 Is Often Preferred

Approach 1 is clever and efficient, but it is less intuitive.

Approach 2 is usually preferred because:

- the contribution formula is clean
- correctness is easier to explain
- implementation is shorter
- it generalizes naturally to similar problems

So in interviews and practice, Approach 2 is often the best one to remember.

---

# Common Mistakes

## 1. Counting substrings directly

There are `O(N^2)` substrings, so direct enumeration is too slow.

---

## 2. Forgetting repeated substrings count separately

Even if two substrings have the same content, if they occur at different positions they must both be counted.

---

## 3. Using only frequency of characters in the whole string

The problem is about every substring, not the full string.

---

## 4. Forgetting edge boundaries

For the first occurrence of a character, `prev` should be `-1`.

For the last occurrence, `next` should be `N`.

These sentinel values are essential for the contribution formula.

---

# Final Summary

## Key Insight

A character occurrence contributes to the answer for exactly those substrings where it is the **only** occurrence of that character.

If an occurrence is at position `curr`, with previous same-character occurrence at `prev` and next one at `next`, then its contribution is:

```text
(curr - prev) * (next - curr)
```

---

## Accepted Approaches

### Maintain Answer of Suffix

- Time: `O(N)`
- Space: `O(N)`

### Split by Character

- Time: `O(N)`
- Space: `O(N)`

---

# Best Final Java Solution

The split-by-character contribution method is usually the clearest.

```java
class Solution {
    public int uniqueLetterString(String S) {
        Map<Character, List<Integer>> index = new HashMap<>();
        for (int i = 0; i < S.length(); ++i) {
            char c = S.charAt(i);
            index.computeIfAbsent(c, x -> new ArrayList<Integer>()).add(i);
        }

        long ans = 0;
        for (List<Integer> A : index.values()) {
            for (int i = 0; i < A.size(); ++i) {
                long prev = i > 0 ? A.get(i - 1) : -1;
                long next = i < A.size() - 1 ? A.get(i + 1) : S.length();
                ans += (A.get(i) - prev) * (next - A.get(i));
            }
        }

        return (int) ans;
    }
}
```

This is the standard accepted linear-time solution for **Count Unique Characters of All Substrings of a Given String**.
