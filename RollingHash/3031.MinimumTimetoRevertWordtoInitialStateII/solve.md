# 3031. Minimum Time to Revert Word to Initial State II

## Problem Statement

You are given a 0-indexed string `word` and an integer `k`.

At every second, you must perform these two operations:

1. Remove the first `k` characters of `word`
2. Add any `k` characters to the end of `word`

The added characters do **not** need to be the same as the removed characters.

Return the **minimum time greater than zero** required for `word` to become equal to its initial state again.

---

## Example 1

```text
Input: word = "abacaba", k = 3
Output: 2
```

Explanation:

- After 1 second: remove `"aba"`, append `"bac"` → `"cababac"`
- After 2 seconds: remove `"cab"`, append `"aba"` → `"abacaba"`

So the answer is `2`.

---

## Example 2

```text
Input: word = "abacaba", k = 4
Output: 1
```

Explanation:

- After 1 second: remove `"abac"`, append `"caba"` → `"abacaba"`

So the answer is `1`.

---

## Example 3

```text
Input: word = "abcbabcd", k = 2
Output: 4
```

Explanation:

```text
abcbabcd
→ cbabcdab
→ abcdabcb
→ cdabcbab
→ abcbabcd
```

So the answer is `4`.

---

## Constraints

- `1 <= word.length <= 10^6`
- `1 <= k <= word.length`
- `word` consists only of lowercase English letters

---

# Core Insight

This problem looks like simulation, but simulation is the wrong abstraction.

The crucial observation is:

After `t` seconds, exactly the first:

```text
t * k
```

characters of the original string have been removed.

Let:

```text
shift = t * k
n = word.length()
```

If `shift >= n`, then all original characters have already been removed at least once.
Since we may append **any** characters, we can always rebuild the original word at that moment.

So the only interesting case is:

```text
shift < n
```

Then the characters from the original word that still remain are:

```text
word[shift...n-1]
```

For the whole string to equal the original word again, this remaining suffix must already match the prefix of the original word of the same length:

```text
word[shift...n-1] == word[0...n-shift-1]
```

So the problem becomes:

> Find the smallest positive `t` such that either:
>
> - `t * k >= n`, or
> - `word[t*k...]` matches the prefix of `word`

That is the entire problem.

---

# Why Problem II Changes the Strategy

In Problem I, `n <= 50`, so directly checking every multiple of `k` is trivial.

Here:

```text
n <= 10^6
```

So we need a near-linear solution.

That means repeated substring comparison is too expensive.

We need a way to test:

```text
does suffix starting at shift match the prefix?
```

quickly for many values of `shift`.

That naturally suggests:

- **Z-algorithm**
- **prefix function / KMP border reasoning**
- possibly **rolling hash** as a probabilistic alternative

---

# Approach 1: Direct Check for Every Multiple of `k` (Too Slow for Worst Case)

## Intuition

The most obvious method is:

- try `shift = k, 2k, 3k, ...`
- if `shift >= n`, return `shift / k`
- otherwise compare `word[shift...]` with `word[0...n-shift-1]`

This is correct, but can be too slow when:

- `k = 1`
- many shifts must be tested
- each test compares a large suffix

That becomes quadratic in the worst case.

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

## Complexity Analysis

### Time Complexity

Worst case:

```text
O(n^2)
```

For example, if `k = 1`, we may check almost every shift, and each check can cost linear time.

### Space Complexity

```text
O(1)
```

---

## Verdict

Correct, but not safe for `n = 10^6`.

---

# Approach 2: Z-Algorithm

## Intuition

This is the cleanest exact solution.

Recall the Z-array definition:

```text
z[i] = length of the longest substring starting at i
       that matches the prefix of the whole string
```

That is exactly what we need.

For a shift `shift`, the suffix `word[shift...]` matches the prefix of length `n - shift` if and only if:

```text
z[shift] >= n - shift
```

So once we build the Z-array once, each candidate shift can be tested in `O(1)`.

---

## Why This Fits Perfectly

Our condition is:

```text
word[shift...n-1] == word[0...n-shift-1]
```

The Z-array directly tells us the longest prefix match starting from every position.

So if the suffix length is fully covered by `z[shift]`, then that shift works.

---

## Algorithm

1. Compute the Z-array of `word`
2. Check shifts in increasing multiples of `k`:
   - `shift = k, 2k, 3k, ...`
3. For each shift:
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

Building the Z-array:

```text
O(n)
```

Testing multiples of `k`:

```text
O(n / k)
```

Total:

```text
O(n)
```

### Space Complexity

```text
O(n)
```

for the Z-array.

---

## Verdict

This is one of the best solutions.

It is exact, linear, and directly matches the problem condition.

---

# Approach 3: Prefix Function / KMP Border Chain

## Intuition

The suffix-prefix matching condition can also be expressed in terms of **borders**.

A border is a string that is both a prefix and a suffix.

If `word[shift...]` matches the prefix of the same length, then the remaining length:

```text
len = n - shift
```

is a border length of `word`.

So we can think in reverse:

- enumerate all border lengths of `word`
- for each border length `len`, compute:
  ```text
  shift = n - len
  ```
- if `shift` is a positive multiple of `k`, then time is:
  ```text
  shift / k
  ```
- also handle the fallback:
  ```text
  ceil(n / k)
  ```

The prefix function makes border enumeration easy.

---

## Prefix Function Refresher

For a string `s`, `pi[i]` is the length of the longest proper prefix of `s[0...i]` that is also a suffix of `s[0...i]`.

For the full word:

```text
pi[n - 1]
```

gives the longest border length.

Then:

```text
pi[pi[n - 1] - 1]
```

gives the next smaller border, and so on.

That lets us enumerate all border lengths.

---

## Algorithm

1. Compute prefix function `pi`
2. Start with fallback answer:
   ```text
   ceil(n / k)
   ```
3. Traverse the border chain of the full word
4. For each border length `len`:
   - `shift = n - len`
   - if `shift % k == 0`, update answer with `shift / k`
5. Return the minimum answer

---

## Java Code

```java
class Solution {
    public int minimumTimeToInitialState(String word, int k) {
        int n = word.length();
        int[] pi = buildPrefixFunction(word);

        int answer = (n + k - 1) / k;

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

Also an excellent exact linear solution.

Compared with Z:

- Z checks candidate shifts directly
- prefix function reasons in terms of border lengths

Both are strong. Z feels slightly more direct for this problem.

---

# Approach 4: Rolling Hash

## Intuition

Another possible method is rolling hash:

- precompute prefix hashes of `word`
- for each shift that is a multiple of `k`, compare:
  - prefix hash of length `n - shift`
  - suffix hash starting at `shift`
- return the first match, else fallback

This gives near-linear performance.

But there is an important caveat:

- rolling hash can collide
- unless double hashing is used, it is probabilistic

Since exact linear-time algorithms already exist here, rolling hash is not the strongest recommendation.

---

## Sketch

1. Precompute powers and prefix hashes
2. For each multiple `shift` of `k`:
   - if `shift >= n`, return `shift / k`
   - compare hash of `word[0...n-shift-1]` with hash of `word[shift...n-1]`
3. Return the first valid time

---

## Why This Is Not the Best Final Choice

It works in practice, but if asked for the most principled answer:

- Z-algorithm is exact
- prefix function is exact
- both are linear
- both avoid collision issues

So rolling hash is acceptable as an alternative, but not the preferred answer.

---

# Why the Fallback `ceil(n / k)` Always Works

A common point of confusion is why the answer always exists.

After each second, we remove `k` characters from the front.

After:

```text
ceil(n / k)
```

seconds, every original character has been removed at least once.

At that point, the entire string consists only of characters we appended ourselves.

Since we may append **any** characters, we can simply choose them so the resulting word becomes exactly the original word.

So:

```text
answer <= ceil(n / k)
```

always.

---

# Dry Run

## Example: `word = "abacaba", k = 3`

### Candidates

#### `t = 1`

```text
shift = 3
suffix = "caba"
prefix length 4 = "abac"
```

Not equal.

#### `t = 2`

```text
shift = 6
suffix = "a"
prefix length 1 = "a"
```

Equal.

Answer = `2`.

---

## Example: `word = "abacaba", k = 4`

#### `t = 1`

```text
shift = 4
suffix = "aba"
prefix length 3 = "aba"
```

Equal immediately.

Answer = `1`.

---

## Example: `word = "abcbabcd", k = 2`

Check shifts:

- `2`
- `4`
- `6`
- `8`

None of shifts `2`, `4`, `6` give suffix-prefix equality.

At `shift = 8 >= n`, fallback applies.

So answer:

```text
8 / 2 = 4
```

---

# Comparing the Best Two Approaches

## Z-Algorithm

### Strengths

- very direct
- tests exactly the condition we need
- elegant shift-based view

### Weakness

- requires knowing Z-array

---

## Prefix Function

### Strengths

- great if you think in borders
- also linear
- compact solution

### Weakness

- slightly less direct because you reason through border lengths instead of shifts

---

# Final Recommended Solution

Use the **Z-algorithm**.

It maps almost one-to-one to the problem statement:

- build prefix-match lengths once
- test each candidate shift in constant time
- return the first valid multiple of `k`

---

## Clean Final Java Solution

```java
class Solution {
    public int minimumTimeToInitialState(String word, int k) {
        int n = word.length();
        int[] z = new int[n];

        int left = 0, right = 0;
        for (int i = 1; i < n; i++) {
            if (i <= right) {
                z[i] = Math.min(right - i + 1, z[i - left]);
            }

            while (i + z[i] < n && word.charAt(z[i]) == word.charAt(i + z[i])) {
                z[i]++;
            }

            if (i + z[i] - 1 > right) {
                left = i;
                right = i + z[i] - 1;
            }
        }

        for (int shift = k, t = 1; ; shift += k, t++) {
            if (shift >= n) {
                return t;
            }

            if (z[shift] >= n - shift) {
                return t;
            }
        }
    }
}
```

---

# Common Mistakes

## 1. Simulating actual string changes

That is unnecessary and too slow.

The only thing that matters is suffix-prefix compatibility after removing `t*k` original characters.

---

## 2. Assuming appended characters are fixed

They are completely under our control.

That is why this is not a pure rotation problem.

---

## 3. Forgetting the fallback case

Even if no proper suffix matches a prefix at a valid shift, the answer still exists at:

```text
ceil(n / k)
```

---

## 4. Using quadratic substring comparison

With `n` up to `10^6`, that will time out.

---

## 5. Treating rolling hash as exact

Unless carefully designed with multiple hashes, collisions remain possible.

---

# Complexity Summary

## Direct repeated comparison

- Time: `O(n^2)` worst case
- Space: `O(1)`

## Z-algorithm

- Time: `O(n)`
- Space: `O(n)`

## Prefix function / KMP border chain

- Time: `O(n)`
- Space: `O(n)`

## Rolling hash

- Time: near `O(n)`
- Space: `O(n)`
- Caveat: probabilistic

---

# Interview Summary

The real transformation is:

After `t` operations, the surviving original suffix is:

```text
word[t*k...]
```

To return to the original word, that suffix must already equal the prefix of the original word of the same length.

So we need the smallest positive multiple `shift = t*k` such that:

- either `shift >= n`, or
- `word[shift...]` matches the prefix

That is a classic prefix-match query over all positions, which is exactly what the **Z-array** provides.

So the strongest exact solution is:

- build Z-array in `O(n)`
- scan multiples of `k`
- return the first valid one
