# 3023. Find Pattern in Infinite Stream I

## Problem Statement

You are given:

- a binary array `pattern`
- an object `stream` of class `InfiniteStream`

The stream is an infinite 0-indexed stream of bits, and the class provides:

```java
int next()
```

which reads the next bit from the stream and returns it.

We need to return the **first starting index** where `pattern` appears in the stream.

The problem guarantees that the first occurrence starts within the first `10^5` bits.

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

- `1 <= pattern.length <= 100`
- `pattern` consists only of `0` and `1`
- `stream` consists only of `0` and `1`
- the answer exists within the first `10^5` bits of the stream

---

# Key Observation

This is a classic **pattern matching in a stream** problem.

The challenge is that:

- the input is not given as a normal array or string
- we can only access it sequentially via `stream.next()`
- we cannot jump backward

So the solution must work in an **online** manner.

Because the pattern length is at most `100`, even straightforward window-based solutions are feasible.

But there are also cleaner streaming pattern matching approaches like **KMP**.

---

# Approach 1: Sliding Window + Full Comparison

## Intuition

The most direct idea is:

- keep the last `m = pattern.length` bits in a window
- every time we read a new bit, once the window size reaches `m`, compare the full window with `pattern`
- if they are equal, return the current start index

This is simple and perfectly acceptable because:

- pattern length is tiny (`<= 100`)
- answer appears within first `10^5` bits

So at worst we do about:

```text
10^5 * 100 = 10^7
```

comparisons, which is fine.

---

## Algorithm

1. Let `m = pattern.length`
2. Read bits from the stream one by one
3. Maintain a queue/deque storing the latest `m` bits
4. Once the queue size becomes `m`, compare it against `pattern`
5. If equal, return the current starting index
6. Otherwise continue

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

Let `m = pattern.length`, and let `N` be the first matched ending position processed from the stream.

### Time Complexity

For each bit, once the window is full, we may compare up to `m` bits:

```text
O(N * m)
```

Since `m <= 100`, this is practically fine.

### Space Complexity

```text
O(m)
```

for the sliding window.

---

## Verdict

This is the easiest correct solution.

For this problem's small pattern size, it is already strong enough.

---

# Approach 2: Sliding Window Using a Fixed Array

## Intuition

The queue solution is readable, but since the window size is fixed and tiny, we can store the last `m` bits in a circular array.

This avoids deque overhead and can be a little faster.

The logic is the same:

- always keep the last `m` bits
- when the window is full, compare against the pattern

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

Same asymptotic complexity as Approach 1, but with a lower-level fixed-size implementation.

---

# Approach 3: KMP on a Stream

## Intuition

The best algorithmic solution is **KMP**.

Normally KMP is used on a full text string, but it also works perfectly in a streaming setting because it only needs:

- the current matched prefix length
- the next incoming character/bit
- the pattern and its LPS table

That makes it ideal for sequential reading.

---

## Why KMP Fits Beautifully Here

At any point, KMP maintains:

```text
matched = number of pattern characters matched so far
```

When a new stream bit arrives:

- if it extends the current match, increment `matched`
- on mismatch, jump using the LPS array
- if `matched == pattern.length`, we found the first occurrence

This avoids rechecking old bits manually.

---

## LPS Refresher

For a pattern, `lps[i]` means:

> the length of the longest proper prefix of `pattern[0..i]`
> that is also a suffix of `pattern[0..i]`

This tells us how much of the match can be reused after a mismatch.

---

## Algorithm

1. Build the `lps` array for `pattern`
2. Read bits from `stream` one by one
3. Maintain `j`, the current matched prefix length in `pattern`
4. For each new bit:
   - while `j > 0` and current bit does not match `pattern[j]`, move `j = lps[j - 1]`
   - if it matches, increment `j`
   - if `j == pattern.length`, return the starting index

---

## Java Code

```java
class Solution {
    public int findPattern(InfiniteStream stream, int[] pattern) {
        int[] lps = buildLPS(pattern);
        int j = 0;
        int index = 0;

        while (true) {
            int bit = stream.next();

            while (j > 0 && bit != pattern[j]) {
                j = lps[j - 1];
            }

            if (bit == pattern[j]) {
                j++;
            }

            if (j == pattern.length) {
                return index - pattern.length + 1;
            }

            index++;
        }
    }

    private int[] buildLPS(int[] pattern) {
        int n = pattern.length;
        int[] lps = new int[n];
        int len = 0;
        int i = 1;

        while (i < n) {
            if (pattern[i] == pattern[len]) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len > 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }
}
```

---

## Complexity Analysis

### Time Complexity

KMP processes each incoming bit in amortized constant time:

```text
O(N + m)
```

where:

- `m = pattern.length`
- `N` = number of bits processed until first match is found

### Space Complexity

```text
O(m)
```

for the `lps` array.

---

## Verdict

This is the best algorithmic solution.

It is exact, online, and asymptotically optimal.

---

# Approach 4: Rolling Binary Window as an Integer

## Intuition

Because the stream is binary and `pattern.length <= 100`, we might think of encoding the current window as bits and comparing integer values.

This works only when the pattern length is small enough to fit in a primitive type such as `long`.

But here the pattern length can be up to `100`, which does **not** fit in `long`.

So this is not a universally valid final solution.

Still, it is worth understanding as a specialized technique.

---

## Small-Pattern Variant

If `pattern.length <= 63`, we can:

- encode the pattern bits into a `long`
- keep a rolling `long` window
- after each new bit:
  - shift left
  - add the new bit
  - mask to keep only last `m` bits
- compare the rolling value with the target pattern value

This gives constant-time matching per bit.

---

## Java Code (Only Safe for Small Patterns)

```java
class Solution {
    public int findPattern(InfiniteStream stream, int[] pattern) {
        int m = pattern.length;

        if (m > 63) {
            throw new IllegalArgumentException("This approach only works for pattern length <= 63");
        }

        long target = 0;
        for (int bit : pattern) {
            target = (target << 1) | bit;
        }

        long mask = (1L << m) - 1;
        long window = 0;

        for (int index = 0; ; index++) {
            int bit = stream.next();
            window = ((window << 1) | bit) & mask;

            if (index + 1 >= m && window == target) {
                return index - m + 1;
            }
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(N)
```

### Space Complexity

```text
O(1)
```

---

## Verdict

Elegant, but not valid for the full constraints because pattern length may be up to `100`.

So it is more of a specialized optimization than a full solution.

---

# Which Approach Should You Prefer?

## If you want the easiest correct answer

Use:

### Sliding window + full comparison

Because:

- pattern size is tiny
- implementation is straightforward
- it comfortably fits the constraints

---

## If you want the strongest algorithmic answer

Use:

### KMP on the stream

Because:

- it is online
- it is exact
- it avoids repeated window comparisons
- it scales best conceptually

---

# Why KMP Works Naturally on a Stream

Some people associate KMP with arrays or strings already stored in memory.

But in reality, KMP does not need random access to the text.

It only needs the text characters one by one in order.

That makes it a perfect streaming matcher.

The pattern is stored locally, and the stream is consumed online.

So this problem is almost tailor-made for KMP.

---

# Dry Run of KMP on Example 3

## Input

```text
stream = [1,0,1,1,0,1,1,0,1,...]
pattern = [1,1,0,1]
```

Pattern indices:

```text
0 1 2 3
1 1 0 1
```

### Read stream bit by bit

- index `0`: bit = `1`
  - matches pattern[0]
  - `j = 1`

- index `1`: bit = `0`
  - mismatch with pattern[1] = `1`
  - fallback using LPS
  - no full reuse
  - `j = 0`
  - bit `0` does not match pattern[0] = `1`

- index `2`: bit = `1`
  - `j = 1`

- index `3`: bit = `1`
  - `j = 2`

- index `4`: bit = `0`
  - `j = 3`

- index `5`: bit = `1`
  - `j = 4` = pattern length

Match found ending at index `5`, so starting index is:

```text
5 - 4 + 1 = 2
```

That is the correct answer.

---

# Common Mistakes

## 1. Trying to store the whole stream

The stream is infinite, so storing everything is conceptually wrong and unnecessary.

Only keep the information needed for pattern matching.

---

## 2. Using substring-style logic

There is no random access into the stream, only repeated calls to `next()`.

So the algorithm must be online.

---

## 3. Forgetting the answer is the starting index

When a match is detected at current end index `idx`, the answer is:

```text
idx - pattern.length + 1
```

not `idx`.

---

## 4. Misusing KMP fallback

After mismatch, the fallback must use:

```java
j = lps[j - 1];
```

not `j--`.

That is the whole point of KMP.

---

## 5. Using bitmask encoding for all pattern lengths

That only works if the pattern fits in the chosen integer type.

With length up to `100`, `long` alone is not enough.

---

# Final Recommended Solution

## Best practical and algorithmic answer

Use **KMP**.

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

# Complexity Summary

## Sliding window + compare

- Time: `O(N * m)`
- Space: `O(m)`

## Circular array window

- Time: `O(N * m)`
- Space: `O(m)`

## KMP on stream

- Time: `O(N + m)`
- Space: `O(m)`

## Rolling integer window (small patterns only)

- Time: `O(N)`
- Space: `O(1)`
- Not valid for full constraints

---

# Interview Summary

This problem is about matching a finite binary pattern in an **infinite sequential stream**.

The text cannot be revisited, so the matcher must be online.

There are two good viewpoints:

1. because pattern length is at most `100`, a sliding window with direct comparison is already sufficient
2. the clean algorithmic solution is **KMP**, because it naturally supports streaming input and finds the first occurrence in linear time

So the strongest final answer is:

- preprocess the pattern with KMP
- consume stream bits one by one
- return the first start index where the full pattern is matched
