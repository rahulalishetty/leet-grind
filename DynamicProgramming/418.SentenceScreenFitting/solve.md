# 418. Sentence Screen Fitting

## Problem Statement

You are given:

- a screen with `rows` rows and `cols` columns
- a sentence represented as an array of words

Return the number of times the **entire sentence** can fit on the screen.

### Rules

- The order of words must remain unchanged.
- A word cannot be split across lines.
- Adjacent words in the same line must be separated by exactly one space.

---

## Example 1

```text
Input:
sentence = ["hello","world"], rows = 2, cols = 8

Output:
1
```

Explanation:

```text
hello---
world---
```

`-` denotes empty cells.

---

## Example 2

```text
Input:
sentence = ["a", "bcd", "e"], rows = 3, cols = 6

Output:
2
```

Explanation:

```text
a-bcd-
e-a---
bcd-e-
```

---

## Example 3

```text
Input:
sentence = ["i","had","apple","pie"], rows = 4, cols = 5

Output:
1
```

Explanation:

```text
i-had
apple
pie-i
had--
```

---

## Constraints

- `1 <= sentence.length <= 100`
- `1 <= sentence[i].length <= 10`
- `sentence[i]` consists of lowercase English letters
- `1 <= rows, cols <= 2 * 10^4`

---

# Core Insight

We are not being asked to print the arrangement.

We only need to count how many times the whole sentence fits.

That means the real task is to simulate how the writing cursor moves across rows while preserving:

- word order
- no splitting
- one space between consecutive words

The difficulty is to do this efficiently when `rows` and `cols` are large.

---

# Approach 1: Direct Simulation Word by Word

## Intuition

The most straightforward way is to simulate the screen row by row.

For each row:

- try to place as many words as possible
- wrap naturally to the next row
- count how many times we complete the sentence

This is easy to understand and is often the first approach to think of.

---

## Algorithm

Maintain:

- `wordIndex`: current word in the sentence
- `completed`: how many full sentences have been placed

For each row:

1. Set remaining width to `cols`
2. While the current word fits:
   - place the word
   - subtract its length
   - if there is still space, subtract one more for the mandatory space
   - move to the next word
   - if we wrapped from the last word back to the first, increment `completed`

Stop when the next word no longer fits.

---

## Java Code

```java
class Solution {
    public int wordsTyping(String[] sentence, int rows, int cols) {
        int n = sentence.length;
        int wordIndex = 0;
        int completed = 0;

        for (int r = 0; r < rows; r++) {
            int remaining = cols;

            while (remaining >= sentence[wordIndex].length()) {
                remaining -= sentence[wordIndex].length();

                wordIndex++;
                if (wordIndex == n) {
                    wordIndex = 0;
                    completed++;
                }

                if (remaining > 0) {
                    remaining--; // one required space
                } else {
                    break;
                }
            }
        }

        return completed;
    }
}
```

---

## Complexity Analysis

Let:

- `R = rows`
- `C = cols`
- `N = sentence.length`

### Time Complexity

In the worst case, each row may place many short words, so the complexity can be approximated as:

```text
O(rows * number_of_words_per_row)
```

A loose upper bound is:

```text
O(rows * cols)
```

if many words have length `1`.

### Space Complexity

```text
O(1)
```

---

## Verdict

Simple and correct, but can be too slow for large inputs.

---

# Approach 2: Concatenate Sentence Into a Circular String

## Intuition

A much smarter observation is that the sentence can be represented as one cyclic string:

```text
s = "word1 word2 word3 "
```

Notice the trailing space.

If we imagine writing this string repeatedly on the screen, then after `rows` rows the number of full sentence fits is just:

```text
totalCharactersPlaced / s.length()
```

But we must respect the rule that words cannot be split.

So for each row:

- optimistically move forward by `cols`
- if we land exactly on a space, great: move one more step
- otherwise move backward until we reach a space boundary, then move one step after it

This is the classic optimal solution.

---

## Why This Works

Suppose the sentence string is:

```text
"hello world "
```

If we conceptually write characters continuously, then each row takes `cols` characters.

But a row cannot end in the middle of a word.

So after advancing `cols`, we adjust:

- if the cursor is already on a space, consume it
- otherwise backtrack until the previous character is a space

This ensures each row ends cleanly at a word boundary.

---

## Java Code

```java
class Solution {
    public int wordsTyping(String[] sentence, int rows, int cols) {
        StringBuilder sb = new StringBuilder();
        for (String word : sentence) {
            sb.append(word).append(' ');
        }

        String s = sb.toString();
        int len = s.length();
        int pos = 0;

        for (int r = 0; r < rows; r++) {
            pos += cols;

            if (s.charAt(pos % len) == ' ') {
                pos++;
            } else {
                while (pos > 0 && s.charAt((pos - 1) % len) != ' ') {
                    pos--;
                }
            }
        }

        return pos / len;
    }
}
```

---

## Complexity Analysis

### Time Complexity

At first glance, the backtracking loop might look expensive.

However, each row adjusts only locally, and the sentence length is small enough that this is efficient in practice.

A commonly accepted bound is:

```text
O(rows * maxWordLength)
```

Since `maxWordLength <= 10`, this is effectively close to linear in `rows`.

### Space Complexity

```text
O(total length of sentence string)
```

which is at most about `1100`, so effectively small.

---

## Verdict

This is the standard best solution for interviews and LeetCode.

---

# Approach 3: Precompute Row Transitions

## Intuition

Instead of recomputing row fitting logic every time, we can precompute what happens when a row starts from a given word.

Since there are only `N` possible starting words, we can build transition tables:

- `nextIndex[i]`: which word index the next row starts with if current row starts at word `i`
- `times[i]`: how many complete sentences are finished in that row

Then simulate rows using these transitions.

This is a dynamic-programming / preprocessing style optimization.

---

## Algorithm

For each starting word index `i`:

1. Simulate filling one row of width `cols`
2. Count how many complete sentence cycles are finished
3. Record:
   - next starting word for the following row
   - completed sentence count for this row

Then process all rows:

- add `times[curr]`
- jump to `nextIndex[curr]`

---

## Java Code

```java
class Solution {
    public int wordsTyping(String[] sentence, int rows, int cols) {
        int n = sentence.length;
        int[] nextIndex = new int[n];
        int[] times = new int[n];

        for (int i = 0; i < n; i++) {
            int curr = i;
            int completed = 0;
            int remaining = cols;

            while (remaining >= sentence[curr].length()) {
                remaining -= sentence[curr].length();

                curr++;
                if (curr == n) {
                    curr = 0;
                    completed++;
                }

                if (remaining > 0) {
                    remaining--;
                } else {
                    break;
                }
            }

            nextIndex[i] = curr;
            times[i] = completed;
        }

        int ans = 0;
        int curr = 0;

        for (int r = 0; r < rows; r++) {
            ans += times[curr];
            curr = nextIndex[curr];
        }

        return ans;
    }
}
```

---

## Complexity Analysis

### Precomputation

There are `N` starting states, and each row simulation is bounded by the number of words that fit.

So preprocessing is roughly:

```text
O(N * words_per_row)
```

### Row Simulation

Each row is then `O(1)`.

So total is roughly:

```text
O(N * words_per_row + rows)
```

### Space Complexity

```text
O(N)
```

---

## Verdict

Very good solution. Easier to reason about than the circular-string trick for some people.

---

# Approach 4: DP With Cycle Detection

## Intuition

Because the row transition depends only on the current starting word index, the sequence of starting indices may eventually cycle.

So we can detect repetition:

- if the same starting word index appears again at a later row
- then the process from that point onward will repeat in a loop

This lets us skip many rows at once.

This is an optimization over Approach 3.

---

## High-Level Idea

Maintain a map:

```text
startWordIndex -> (rowNumber, totalSentencesCompleted)
```

While simulating rows:

- if a start index repeats, a cycle is found
- compute:
  - cycle length in rows
  - cycle gain in completed sentences
- fast-forward through as many cycles as possible

This is not necessary for the given constraints, but it is a useful optimization idea.

---

# Why the Circular String Method Is So Elegant

The sentence string with trailing space turns the problem into walking on a cycle.

Example:

```text
sentence = ["a", "bcd", "e"]
s = "a bcd e "
```

Each row tries to move `cols` characters forward.

The only obstacle is landing inside a word.

So every row becomes:

1. move forward by `cols`
2. repair boundary if necessary

That collapses the whole word-by-word logic into simple pointer arithmetic.

---

# Common Mistakes

## 1. Forgetting the mandatory space between words

Words placed in the same row must have exactly one separating space.

## 2. Forgetting the trailing space in the concatenated sentence string

The trailing space is crucial because it makes every sentence boundary behave naturally.

## 3. Ending a row in the middle of a word

This is not allowed. If the row ends inside a word, you must backtrack to the previous space.

## 4. Thinking leftover row space must be filled exactly

It does not. Empty cells at the end of a row are allowed.

---

# Final Recommended Solution

Use the concatenated circular string method.

It is concise, fast, and elegant.

---

## Clean Final Java Solution

```java
class Solution {
    public int wordsTyping(String[] sentence, int rows, int cols) {
        StringBuilder sb = new StringBuilder();
        for (String word : sentence) {
            sb.append(word).append(' ');
        }

        String s = sb.toString();
        int len = s.length();
        int pos = 0;

        for (int r = 0; r < rows; r++) {
            pos += cols;

            if (s.charAt(pos % len) == ' ') {
                pos++;
            } else {
                while (pos > 0 && s.charAt((pos - 1) % len) != ' ') {
                    pos--;
                }
            }
        }

        return pos / len;
    }
}
```

---

# Complexity Summary

## Direct Simulation

- Time: can be as bad as `O(rows * cols)`
- Space: `O(1)`

## Precomputed Transitions

- Time: `O(sentence.length * words_per_row + rows)`
- Space: `O(sentence.length)`

## Circular Sentence String

- Time: effectively `O(rows * maxWordLength)`
- Space: `O(total sentence length)`

---

# Interview Summary

This problem looks like simulation, but the real key is recognizing that the sentence repeats in a cycle.

By concatenating the sentence into one string with trailing space, each row becomes a pointer movement problem:

- advance by `cols`
- if you land inside a word, backtrack to the previous space
- if you land on a space, move past it

The total number of full sentence fits is then the total pointer movement divided by the sentence string length.
