# 3037. Find Pattern in Infinite Stream II

## Problem Statement

You are given:

- a binary array `pattern`
- an object `stream` of class `InfiniteStream`

The stream is a 0-indexed infinite stream of bits, and the class provides:

```java
int next()
```

which reads the next bit from the stream and returns it.

We need to return the **first starting index** where `pattern` appears in the stream.

---

## Example 1

```text
Input:
stream = [1,1,1,0,1,1,1,...]
pattern = [0,1]

Output:
3
```

Explanation:

The first occurrence of `[0,1]` starts at index `3`.

---

## Example 2

```text
Input:
stream = [0,0,0,0,...]
pattern = [0]

Output:
0
```

Explanation:

The pattern appears immediately at index `0`.

---

## Example 3

```text
Input:
stream = [1,0,1,1,0,1,1,0,1,...]
pattern = [1,1,0,1]

Output:
2
```

Explanation:

The first occurrence of `[1,1,0,1]` starts at index `2`.

---

## Constraints

- `1 <= pattern.length <= 10^4`
- `pattern` consists only of `0` and `1`
- `stream` consists only of `0` and `1`
- the answer exists within the first `10^5` bits of the stream

---

# Core Insight

This is a **streaming pattern matching** problem.

The text is not stored as an array or string.
We can only read it sequentially through:

```java
stream.next()
```

So the algorithm must work **online**.

The most important difference from Problem I is the pattern size:

- in Problem I, `pattern.length <= 100`
- here, `pattern.length <= 10^4`

That means repeated full-window comparison becomes much more expensive.

So for Problem II, the natural exact solution is **KMP on the stream**.

---

# Approach 1: Sliding Window + Full Comparison

## Intuition

The most direct approach is:

- keep the last `m = pattern.length` bits in a window
- every time a new bit arrives, once the window is full, compare the whole window with `pattern`
- if equal, return the current start index

This is conceptually simple.

However, because `m` can now be as large as `10^4`, repeated full comparisons can be costly.

Still, it is a useful baseline.

---

## Algorithm

1. Maintain a window of the most recent `m` bits
2. Read one bit at a time from the stream
3. Once window size reaches `m`, compare all `m` positions with `pattern`
4. If equal, return the starting index

---

## Java Code

```java
import java.util.*;

class Solution {
    public int findPattern(InfiniteStream stream, int[] pattern) {
        int m = pattern.length;
        Deque<Integer> window = new ArrayDeque<>();
        int index = 0;

        while (true) {
            int bit = stream.next();
            window.addLast(bit);

            if (window.size() > m) {
                window.removeFirst();
            }

            if (window.size() == m && matches(window, pattern)) {
                return index - m + 1;
            }

            index++;
        }
    }

    private boolean matches(Deque<Integer> window, int[] pattern) {
        int i = 0;
        for (int bit : window) {
            if (bit != pattern[i]) {
                return false;
            }
            i++;
        }
        return true;
    }
}
```

---

## Complexity Analysis

Let:

- `m = pattern.length`
- `N` = number of bits read until the first match is found

### Time Complexity

Each new bit may trigger a full `O(m)` comparison:

```text
O(N * m)
```

With `m` up to `10^4`, this can be too slow in the worst case.

### Space Complexity

```text
O(m)
```

---

## Verdict

Correct, but not the best choice for the larger constraints.

---

# Approach 2: Circular Buffer Window

## Intuition

We can optimize the implementation details of the window by using a fixed-size circular buffer instead of a deque.

This reduces overhead, but the core issue remains:

- every full window still needs `O(m)` comparison

So asymptotically it is the same as Approach 1.

---

## Java Code

```java
class Solution {
    public int findPattern(InfiniteStream stream, int[] pattern) {
        int m = pattern.length;
        int[] window = new int[m];
        int count = 0;
        int index = 0;

        while (true) {
            window[index % m] = stream.next();
            count++;

            if (count >= m && matches(window, pattern, index, m)) {
                return index - m + 1;
            }

            index++;
        }
    }

    private boolean matches(int[] window, int[] pattern, int endIndex, int m) {
        int start = endIndex - m + 1;

        for (int i = 0; i < m; i++) {
            if (window[(start + i) % m] != pattern[i]) {
                return false;
            }
        }

        return true;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(N * m)
```

### Space Complexity

```text
O(m)
```

---

## Verdict

A lower-level implementation of the sliding-window idea, but not asymptotically strong enough.

---

# Approach 3: KMP on the Stream

## Intuition

This is the best exact solution.

KMP is perfect for streaming input because it does **not** need random access to the text.
It only needs:

- the pattern
- the LPS array of the pattern
- the current match length
- the next incoming bit

That matches this problem exactly.

---

## Why KMP Fits Naturally

Suppose we have matched the first `j` bits of the pattern so far.

When a new stream bit arrives:

- if it extends the current match, increase `j`
- if it mismatches, use the LPS array to jump to the longest reusable prefix
- when `j == pattern.length`, we found the first occurrence

This avoids rechecking old bits manually.

Since the stream is infinite and sequential, KMP is almost tailor-made for this setting.

---

## LPS Refresher

For `pattern`, `lps[i]` is:

> the length of the longest proper prefix of `pattern[0..i]`
> that is also a suffix of `pattern[0..i]`

This tells us how much of the current match can be reused after a mismatch.

---

## Algorithm

1. Build the `lps` array for `pattern`
2. Read bits from `stream` one by one
3. Maintain `matched`, the current matched prefix length
4. For each incoming bit:
   - while `matched > 0` and the bit does not equal `pattern[matched]`, fallback:
     ```java
     matched = lps[matched - 1];
     ```
   - if the bit matches, increment `matched`
   - if `matched == pattern.length`, return the starting index

---

## Java Code

```java
class Solution {
    public int findPattern(InfiniteStream stream, int[] pattern) {
        int[] lps = buildLPS(pattern);
        int matched = 0;

        for (int index = 0; ; index++) {
            int bit = stream.next();

            while (matched > 0 && bit != pattern[matched]) {
                matched = lps[matched - 1];
            }

            if (bit == pattern[matched]) {
                matched++;
            }

            if (matched == pattern.length) {
                return index - pattern.length + 1;
            }
        }
    }

    private int[] buildLPS(int[] pattern) {
        int[] lps = new int[pattern.length];
        int len = 0;

        for (int i = 1; i < pattern.length; ) {
            if (pattern[i] == pattern[len]) {
                lps[i] = ++len;
                i++;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }

        return lps;
    }
}
```

---

## Complexity Analysis

Let:

- `m = pattern.length`
- `N` = number of stream bits processed until the first match is found

### Time Complexity

- build LPS: `O(m)`
- KMP stream scan: `O(N)`

Total:

```text
O(N + m)
```

This is optimal for exact matching.

### Space Complexity

```text
O(m)
```

for the LPS array.

---

## Verdict

This is the strongest exact answer.

---

# Approach 4: Rolling Hash Over the Stream

## Intuition

Another possibility is rolling hash.

We can keep a rolling fingerprint of the last `m` bits and compare it to the hash of the pattern.

This can make each step fast.

However, there are two issues:

1. rolling hash is probabilistic due to collisions
2. binary patterns of length up to `10^4` do not fit into a primitive integer bitmask, so we need modular hashing

Because KMP already gives an exact linear solution, rolling hash is not the best final recommendation.

---

## Sketch

1. Choose a base and modulus
2. Precompute:
   - `patternHash`
   - `base^(m-1)` or equivalent factor
3. Maintain a rolling hash of the latest `m` stream bits
4. Once window size is `m`, compare hashes
5. Optionally verify the window to reduce collision issues

---

## Why This Is Weaker Than KMP

Even though it is often fast in practice, it has collision risk unless you use multiple hashes and still accept probabilistic reasoning.

KMP is:

- exact
- also linear
- simpler to justify formally

So rolling hash is an alternative, not the best answer.

---

# Approach 5: Finite Automaton View of KMP

## Intuition

KMP can also be viewed as building a finite-state machine:

- state `j` means we have matched `j` bits of the pattern
- each incoming bit causes a transition to a new state
- reaching state `m` means a match

This viewpoint is conceptually elegant, especially for streams.

In practice, though, implementing KMP directly is simpler than explicitly building the full transition table.

---

## Transition Idea

For each state `j` and bit `0/1`, define:

```text
nextState(j, bit)
```

That can be derived from the pattern and LPS information.

Then reading the stream is just automaton simulation.

This is essentially KMP in another form.

---

## Why It Is Interesting

Because this problem is online and the alphabet is tiny (`0` and `1`), an automaton interpretation feels natural.

But unless you explicitly need a DFA, plain KMP is simpler and cleaner.

---

# Why KMP Is Especially Good Here

A skeptical view helps here:

Why not just do window comparison if the answer is within `10^5` bits?

Because in the worst case:

```text
10^5 * 10^4 = 10^9
```

bit comparisons may happen.

That is much heavier than it first appears.

KMP avoids that completely by ensuring the stream is processed in amortized constant time per bit.

So the larger pattern size is exactly what makes KMP the right tool.

---

# Dry Run of KMP on Example 3

## Input

```text
stream = [1,0,1,1,0,1,1,0,1,...]
pattern = [1,1,0,1]
```

### Build LPS for pattern `[1,1,0,1]`

LPS becomes:

```text
[0,1,0,1]
```

Now scan the stream.

### index 0, bit = 1

- matches pattern[0]
- matched = 1

### index 1, bit = 0

- mismatch with pattern[1] = 1
- fallback to lps[0] = 0
- compare with pattern[0] = 1, still mismatch
- matched = 0

### index 2, bit = 1

- matched = 1

### index 3, bit = 1

- matched = 2

### index 4, bit = 0

- matched = 3

### index 5, bit = 1

- matched = 4 = pattern length

Match ends at index `5`, so starting index is:

```text
5 - 4 + 1 = 2
```

Correct answer.

---

# Comparing the Approaches

## Sliding window + compare

### Strengths

- easiest to understand
- straightforward implementation

### Weakness

- too slow when pattern length is large

---

## Circular buffer window

### Strengths

- slightly more efficient implementation

### Weakness

- same asymptotic bottleneck

---

## KMP on stream

### Strengths

- exact
- linear
- online
- ideal for sequential reading

### Weakness

- requires understanding the LPS array

---

## Rolling hash

### Strengths

- fast in practice
- natural streaming window idea

### Weakness

- probabilistic
- weaker than KMP here

---

# Final Recommended Solution

Use **KMP on the stream**.

It is the best fit because:

- the text is sequential
- the pattern can be large
- we need the first occurrence
- KMP is exact and linear

---

## Clean Final Java Solution

```java
class Solution {
    public int findPattern(InfiniteStream stream, int[] pattern) {
        int[] lps = buildLPS(pattern);
        int matched = 0;

        for (int index = 0; ; index++) {
            int bit = stream.next();

            while (matched > 0 && bit != pattern[matched]) {
                matched = lps[matched - 1];
            }

            if (bit == pattern[matched]) {
                matched++;
            }

            if (matched == pattern.length) {
                return index - pattern.length + 1;
            }
        }
    }

    private int[] buildLPS(int[] pattern) {
        int[] lps = new int[pattern.length];
        int len = 0;

        for (int i = 1; i < pattern.length; ) {
            if (pattern[i] == pattern[len]) {
                lps[i] = ++len;
                i++;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }

        return lps;
    }
}
```

---

# Common Mistakes

## 1. Treating the stream like a stored array

It is infinite and sequential.
The algorithm must work online.

---

## 2. Re-comparing the whole window every time

This may work for very small patterns, but here pattern length can be `10^4`, so that becomes expensive.

---

## 3. Returning the ending index instead of the start

If a match completes at index `idx`, the answer is:

```text
idx - pattern.length + 1
```

---

## 4. Misusing KMP fallback

After mismatch, the fallback is:

```java
matched = lps[matched - 1];
```

not `matched--`.

---

## 5. Assuming rolling bitmask fits in `long`

Pattern length may be `10^4`, so primitive bitmasking is not enough.

---

# Complexity Summary

## Sliding window + full comparison

- Time: `O(N * m)`
- Space: `O(m)`

## Circular buffer window

- Time: `O(N * m)`
- Space: `O(m)`

## KMP on stream

- Time: `O(N + m)`
- Space: `O(m)`

## Rolling hash

- Time: near `O(N + m)`
- Space: `O(m)`
- Caveat: probabilistic

---

# Interview Summary

This is a streaming pattern matching problem.

Because:

- the stream is sequential
- the pattern can be large
- the first match is guaranteed early enough but not trivially tiny

the best exact solution is **KMP**.

KMP is especially strong here because it does not need random access to the stream.
It only needs the next bit and the current matched prefix length.

So the final strategy is:

1. preprocess `pattern` with LPS
2. consume the stream bit by bit
3. run KMP transitions online
4. return the first starting index when the full pattern is matched
