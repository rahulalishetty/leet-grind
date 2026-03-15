# 3029. Minimum Time to Revert Word to Initial State I

## Problem Statement

You are given a 0-indexed string `word` and an integer `k`.

Every second, you must:

1. remove the first `k` characters of `word`
2. append any `k` characters to the end

The appended characters do **not** have to be the same as the removed ones.

We need the minimum time **greater than 0** after which the string can become equal to its original value again.

---

## Example 1

```text
Input:  word = "abacaba", k = 3
Output: 2
```

Explanation:

- After 1 second, remove `"aba"` and append `"bac"` → `"cababac"`
- After 2 seconds, remove `"cab"` and append `"aba"` → `"abacaba"`

So the answer is `2`.

---

## Example 2

```text
Input:  word = "abacaba", k = 4
Output: 1
```

Explanation:

- After removing `"abac"`, the remaining suffix is `"aba"`
- We can append `"caba"` so the word becomes `"abacaba"` again in `1` second

---

## Example 3

```text
Input:  word = "abcbabcd", k = 2
Output: 4
```

Explanation:

If we keep rotating by 2 characters:

```text
abcbabcd
→ cbabcdab
→ abcdabcb
→ cdabcbab
→ abcbabcd
```

It returns after `4` seconds.

---

## Constraints

- `1 <= word.length <= 50`
- `1 <= k <= word.length`
- `word` contains only lowercase English letters

---

# Core Insight

After `t` seconds, we have removed the first:

```text
t * k
```

characters from the original word.

Let:

```text
shift = t * k
```

If `shift >= n`, where `n = word.length()`, then all original characters have been removed at least once.
At that point, since we are allowed to append **any** characters, we can always reconstruct the original word.

So the only interesting case is when:

```text
shift < n
```

Then the remaining part of the original word is:

```text
word[shift...n-1]
```

To get back the original string, this remaining suffix must already match the prefix of the original word:

```text
word[shift...n-1] == word[0...n-shift-1]
```

If that holds, we can choose the appended characters to complete the rest.

So the problem reduces to finding the smallest positive `t` such that:

- either `t * k >= n`
- or `word.substring(t * k).equals(word.substring(0, n - t * k))`

That is the real structure of the problem.

---

# Approach 1: Direct Simulation of Valid Shifts

## Intuition

Since `n <= 50`, we can directly test every possible number of seconds:

- compute `shift = t * k`
- if `shift >= n`, return `t`
- otherwise check whether the remaining suffix equals the original prefix

This is simple and already efficient enough.

---

## Algorithm

For `t = 1, 2, 3, ...`:

1. compute `shift = t * k`
2. if `shift >= n`, return `t`
3. compare:
   - `word[shift...]`
   - `word[0...n-shift-1]`
4. if equal, return `t`

---

## Java Code

```java
class Solution {
    public int minimumTimeToInitialState(String word, int k) {
        int n = word.length();

        for (int t = 1; ; t++) {
            int shift = t * k;

            if (shift >= n) {
                return t;
            }

            if (matches(word, shift)) {
                return t;
            }
        }
    }

    private boolean matches(String word, int shift) {
        int n = word.length();

        for (int i = shift, j = 0; i < n; i++, j++) {
            if (word.charAt(i) != word.charAt(j)) {
                return false;
            }
        }

        return true;
    }
}
```

---

## Complexity Analysis

Let `n = word.length()`.

### Time Complexity

At most we try around:

```text
ceil(n / k)
```

values of `t`.

For each one, substring matching costs up to `O(n)`.

So total is:

```text
O(n * (n / k))
```

In the worst case, since `n <= 50`, this is easily fine.

A simpler upper bound is:

```text
O(n^2)
```

### Space Complexity

```text
O(1)
```

---

## Verdict

This is the best practical solution for Problem I because the constraints are tiny.

---

# Approach 2: Using Substring Operations Directly

## Intuition

The same logic can be written in a more compact style using Java substring comparison.

This version is concise, though it creates temporary strings.

Since `n <= 50`, that cost is irrelevant here.

---

## Java Code

```java
class Solution {
    public int minimumTimeToInitialState(String word, int k) {
        int n = word.length();

        for (int t = 1; ; t++) {
            int shift = t * k;

            if (shift >= n) {
                return t;
            }

            if (word.substring(shift).equals(word.substring(0, n - shift))) {
                return t;
            }
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n^2)
```

in the worst case.

### Space Complexity

Because of temporary substrings, auxiliary allocation may occur, but asymptotically for this problem we usually treat it as:

```text
O(n)
```

temporary space per iteration.

---

## Verdict

Very readable for small constraints.
Not the version I would choose in a performance-sensitive setting, but perfectly fine here.

---

# Approach 3: Prefix Function / KMP Border Perspective

## Intuition

The condition:

```text
word[shift...n-1] == word[0...n-shift-1]
```

means:

> the suffix of length `n - shift` equals the prefix of length `n - shift`

That is exactly a **border** condition.

A border of a string is a non-empty string that is both a prefix and a suffix.

So we can reframe the problem as:

Find the smallest `shift` such that:

- `shift` is a multiple of `k`
- the remaining length `n - shift` is a border length
- or `shift >= n`

This connects the problem to KMP / prefix function.

---

## Prefix Function Refresher

For a string `word`, the prefix function `pi[i]` gives the length of the longest proper prefix of `word[0...i]` that is also a suffix of `word[0...i]`.

For the full string, `pi[n - 1]` gives the longest border length.

By following prefix links repeatedly, we can enumerate all border lengths.

---

## Algorithm

1. Compute the prefix-function array for `word`
2. Collect all border lengths of the full word
3. For each border length `len`:
   - required shift is `n - len`
   - if `shift % k == 0`, then answer could be `shift / k`
4. Also consider the fallback case when `shift >= n`, which is:

```text
ceil(n / k)
```

5. Return the minimum valid time

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minimumTimeToInitialState(String word, int k) {
        int n = word.length();
        int[] pi = buildPrefixFunction(word);

        int answer = (n + k - 1) / k; // fallback when all original chars are removed

        int len = pi[n - 1];
        while (len > 0) {
            int shift = n - len;
            if (shift % k == 0) {
                answer = Math.min(answer, shift / k);
            }
            len = pi[len - 1];
        }

        return answer;
    }

    private int[] buildPrefixFunction(String s) {
        int n = s.length();
        int[] pi = new int[n];

        for (int i = 1; i < n; i++) {
            int j = pi[i - 1];
            while (j > 0 && s.charAt(i) != s.charAt(j)) {
                j = pi[j - 1];
            }
            if (s.charAt(i) == s.charAt(j)) {
                j++;
            }
            pi[i] = j;
        }

        return pi;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Building prefix function:

```text
O(n)
```

Following border links:

```text
O(n)
```

Total:

```text
O(n)
```

### Space Complexity

```text
O(n)
```

---

## Verdict

This is algorithmically elegant and more scalable than necessary for Problem I.

It becomes more interesting when constraints are larger.

---

# Approach 4: Z-Algorithm Perspective

## Intuition

The same suffix-prefix condition can also be tested with the Z-array.

Recall:

```text
z[i] = length of the longest prefix match starting at index i
```

If after `shift` removed characters we want the suffix `word[shift...]` to match the prefix, we need:

```text
z[shift] >= n - shift
```

So if we precompute the Z-array once, then for every multiple of `k` we can test the condition in `O(1)`.

---

## Algorithm

1. Build the Z-array of `word`
2. For each positive multiple `shift = k, 2k, 3k, ...`:
   - if `shift >= n`, return `shift / k`
   - if `z[shift] >= n - shift`, return `shift / k`

---

## Java Code

```java
class Solution {
    public int minimumTimeToInitialState(String word, int k) {
        int n = word.length();
        int[] z = buildZ(word);

        for (int shift = k, t = 1; ; shift += k, t++) {
            if (shift >= n) {
                return t;
            }

            if (z[shift] >= n - shift) {
                return t;
            }
        }
    }

    private int[] buildZ(String s) {
        int n = s.length();
        int[] z = new int[n];
        int left = 0, right = 0;

        for (int i = 1; i < n; i++) {
            if (i <= right) {
                z[i] = Math.min(right - i + 1, z[i - left]);
            }

            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }

            if (i + z[i] - 1 > right) {
                left = i;
                right = i + z[i] - 1;
            }
        }

        return z;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Building Z-array:

```text
O(n)
```

Testing shifts:

```text
O(n / k)
```

So total is:

```text
O(n)
```

### Space Complexity

```text
O(n)
```

---

## Verdict

This is arguably the cleanest algorithmic solution if you think in prefix-matching terms.

---

# Approach 5: Naive Literal Simulation of the String

## Intuition

One might try to simulate the actual process:

- remove first `k`
- append some chosen `k`
- try to recover the original string

But this is misleading, because the appended characters are under our control.

So explicitly simulating arbitrary appended strings is unnecessary.

The only thing that matters is whether the remaining suffix already matches the original prefix.

This approach is more of a cautionary note than a good solution.

---

## Why Full Simulation Is the Wrong Mental Model

At second `t`, we do **not** need to know exactly what characters were appended earlier.

We only need to know whether the untouched suffix of the original word can be aligned with the original prefix, because then we can always choose the appended characters to fill the rest.

So the correct abstraction is prefix-suffix matching, not step-by-step mutation tracking.

---

# Why the Condition Is Correct

Suppose after `t` seconds, `shift = t * k < n`.

Then the first `shift` characters of the original word are gone.
The characters that remain from the original word are:

```text
word[shift...n-1]
```

If the final string is to equal the original word again, these remaining characters must occupy the first `n - shift` positions of the final word.

Therefore they must match:

```text
word[0...n-shift-1]
```

If they do match, then we can freely choose the last `shift` appended characters so the entire word becomes the original string.

If they do not match, recovery is impossible at that time.

That fully justifies the criterion.

---

# Dry Run

## Example: `word = "abacaba", k = 3`

### `t = 1`

```text
shift = 3
remaining suffix = "caba"
prefix of same length = "abac"
```

Not equal.

### `t = 2`

```text
shift = 6
remaining suffix = "a"
prefix of same length = "a"
```

Equal.

So answer = `2`.

---

## Example: `word = "abacaba", k = 4`

### `t = 1`

```text
shift = 4
remaining suffix = "aba"
prefix of same length = "aba"
```

Equal immediately.

So answer = `1`.

---

# Comparing the Best Approaches

## Direct shift checking

### Strengths

- simplest
- perfect for `n <= 50`

### Weakness

- not the most elegant asymptotically

---

## Prefix function / KMP border approach

### Strengths

- mathematically neat
- linear time
- good if you think in borders

### Weakness

- slightly more abstract

---

## Z-algorithm approach

### Strengths

- very direct suffix-prefix test
- linear time
- simple once Z is familiar

### Weakness

- requires familiarity with Z-array

---

# Final Recommended Solution

For **Problem I** with `n <= 50`, the simplest direct check is the best practical answer.

---

## Clean Final Java Solution

```java
class Solution {
    public int minimumTimeToInitialState(String word, int k) {
        int n = word.length();

        for (int t = 1; ; t++) {
            int shift = t * k;

            if (shift >= n) {
                return t;
            }

            boolean ok = true;
            for (int i = shift, j = 0; i < n; i++, j++) {
                if (word.charAt(i) != word.charAt(j)) {
                    ok = false;
                    break;
                }
            }

            if (ok) {
                return t;
            }
        }
    }
}
```

---

# Common Mistakes

## 1. Thinking the appended characters are forced

They are not.
We may append **any** `k` characters each second.

That changes the problem fundamentally.

---

## 2. Simulating rotations only

In some examples, appending the removed prefix works, but that is not required.

We only care whether recovery is possible, not whether a fixed deterministic rotation happens.

---

## 3. Forgetting the fallback case

If after enough time `shift >= n`, then all original characters are gone, and we can simply append characters to recreate the original word.

So an answer always exists by:

```text
ceil(n / k)
```

---

## 4. Confusing suffix-prefix equality direction

The condition is:

```text
word[shift...n-1] == word[0...n-shift-1]
```

not the other way around with arbitrary lengths.

---

# Complexity Summary

## Direct shift checking

- Time: `O(n^2)` worst case
- Space: `O(1)`

## Substring-based direct checking

- Time: `O(n^2)`
- Space: temporary substring overhead

## Prefix function / KMP border approach

- Time: `O(n)`
- Space: `O(n)`

## Z-algorithm approach

- Time: `O(n)`
- Space: `O(n)`

---

# Interview Summary

The essential realization is:

After `t` seconds, the only original characters still present are the suffix:

```text
word[t*k...]
```

To recover the original word, that suffix must already match the prefix of the original word of the same length.

So the answer is the smallest positive `t` such that:

- either `t * k >= n`
- or `word[t*k...]` is a prefix of `word`

For Problem I, direct checking is enough.
For a more scalable approach, use Z-algorithm or prefix-function reasoning.
