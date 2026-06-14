# Counting Bits (LeetCode 338) — Exhaustive Java Approaches

## Problem Restatement

Given an integer `n`, return an array `ans` of length `n + 1` where:

```text
ans[i] = number of 1-bits in the binary representation of i
```

for every:

```text
0 <= i <= n
```

This count of set bits is also called:

- popcount
- Hamming weight

---

## Core Insight

The problem is not asking for the number of bits in one integer.

It asks for the bit counts for **all numbers from `0` to `n`**.

That means repeated independent computation is wasteful.

The real opportunity is:

> reuse the answer for smaller numbers to build the answer for larger numbers.

That is why the most interesting solutions are linear-time DP-style formulations.

---

# Approach 1: Naive Per-Number Bit Counting by Repeated Division / Shifting

## Idea

For every integer `i` from `0` to `n`:

- repeatedly inspect the last bit
- shift right until the number becomes `0`

Count how many `1`s appear.

---

## Java Code

```java
class Solution {
    public int[] countBits(int n) {
        int[] ans = new int[n + 1];

        for (int i = 0; i <= n; i++) {
            ans[i] = countOnes(i);
        }

        return ans;
    }

    private int countOnes(int x) {
        int count = 0;

        while (x > 0) {
            count += (x & 1);
            x >>= 1;
        }

        return count;
    }
}
```

---

## Complexity

### Time Complexity

- For each number `i`, counting bits takes `O(log i)`
- Total:
  - **O(n log n)**

More precisely:

- `O(sum(log i))` for `i = 1..n`, which is `O(n log n)`

### Space Complexity

- **O(1)** extra space, ignoring output array
- Output array itself takes `O(n)`

---

## Pros

- Very simple
- Direct
- Easy to derive

## Cons

- Not linear time
- Does not exploit overlap between consecutive numbers

---

# Approach 2: Brian Kernighan’s Bit Trick Per Number

## Idea

Use the classic bit trick:

```text
x & (x - 1)
```

This removes the lowest set bit from `x`.

So for each number:

- repeatedly apply this operation
- each iteration removes one `1`

This is often faster in practice when numbers are sparse.

---

## Why It Works

If:

```text
x = 1011000
```

then:

```text
x - 1 = 1010111
```

and:

```text
x & (x - 1) = 1010000
```

So one set bit disappears.

Each loop iteration removes exactly one `1`.

---

## Java Code

```java
class Solution {
    public int[] countBits(int n) {
        int[] ans = new int[n + 1];

        for (int i = 0; i <= n; i++) {
            ans[i] = countOnes(i);
        }

        return ans;
    }

    private int countOnes(int x) {
        int count = 0;

        while (x != 0) {
            x = x & (x - 1);
            count++;
        }

        return count;
    }
}
```

---

## Complexity

### Time Complexity

- For each number `i`, time is proportional to number of set bits in `i`
- Worst case overall still:
  - **O(n log n)**

Though practically often better than repeated shifting.

### Space Complexity

- **O(1)** extra space
- Output array: `O(n)`

---

## Pros

- Elegant bit trick
- Often faster in practice than naive shifting

## Cons

- Still not linear in `n`
- Computes each number independently

---

# Approach 3: DP Using Lowest Bit Removal

## Idea

Observe:

```text
bits(i) = bits(i & (i - 1)) + 1
```

Why?

Because:

- `i & (i - 1)` removes one set bit from `i`
- so the bit count decreases by exactly `1`

Since `i & (i - 1) < i`, the answer for that smaller number is already known if we build from `0` upward.

This gives a clean linear DP.

---

## Recurrence

```text
ans[0] = 0
ans[i] = ans[i & (i - 1)] + 1
```

---

## Java Code

```java
class Solution {
    public int[] countBits(int n) {
        int[] ans = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            ans[i] = ans[i & (i - 1)] + 1;
        }

        return ans;
    }
}
```

---

## Example Dry Run

For `n = 5`:

### `i = 1`

```text
1 & 0 = 0
ans[1] = ans[0] + 1 = 1
```

### `i = 2`

```text
2 = 10
2 & 1 = 0
ans[2] = ans[0] + 1 = 1
```

### `i = 3`

```text
3 = 11
3 & 2 = 2
ans[3] = ans[2] + 1 = 2
```

### `i = 4`

```text
4 = 100
4 & 3 = 0
ans[4] = ans[0] + 1 = 1
```

### `i = 5`

```text
5 = 101
5 & 4 = 4
ans[5] = ans[4] + 1 = 2
```

Result:

```text
[0,1,1,2,1,2]
```

---

## Complexity

### Time Complexity

- **O(n)**

Each `i` is processed once with O(1) work.

### Space Complexity

- **O(1)** extra space besides output
- Output array: `O(n)`

---

## Pros

- Elegant
- Linear time
- Very common interview solution

## Cons

- Slightly less intuitive for beginners if they do not know `x & (x - 1)`

---

# Approach 4: DP Using Right Shift + Lowest Bit

## Idea

Split number `i` into:

- all bits except the last one: `i >> 1`
- the last bit: `i & 1`

Then:

```text
bits(i) = bits(i >> 1) + (i & 1)
```

Why?

- right shift removes the least significant bit
- if the removed bit was `1`, add one
- if it was `0`, add zero

This is one of the cleanest formulations.

---

## Recurrence

```text
ans[0] = 0
ans[i] = ans[i >> 1] + (i & 1)
```

---

## Java Code

```java
class Solution {
    public int[] countBits(int n) {
        int[] ans = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            ans[i] = ans[i >> 1] + (i & 1);
        }

        return ans;
    }
}
```

---

## Example Dry Run

For `n = 5`:

### `i = 1`

```text
1 >> 1 = 0
1 & 1 = 1
ans[1] = ans[0] + 1 = 1
```

### `i = 2`

```text
2 >> 1 = 1
2 & 1 = 0
ans[2] = ans[1] + 0 = 1
```

### `i = 3`

```text
3 >> 1 = 1
3 & 1 = 1
ans[3] = ans[1] + 1 = 2
```

### `i = 4`

```text
4 >> 1 = 2
4 & 1 = 0
ans[4] = ans[2] + 0 = 1
```

### `i = 5`

```text
5 >> 1 = 2
5 & 1 = 1
ans[5] = ans[2] + 1 = 2
```

Result:

```text
[0,1,1,2,1,2]
```

---

## Complexity

### Time Complexity

- **O(n)**

### Space Complexity

- **O(1)** extra besides output
- Output array: `O(n)`

---

## Pros

- Very intuitive
- Linear
- Single pass
- Easy to explain

## Cons

- None significant

---

# Approach 5: DP Using the Most Significant Power of Two (Offset Method)

## Idea

Numbers come in blocks between powers of two.

Example:

```text
0
1
10 11
100 101 110 111
1000 ...
```

If `offset` is the largest power of two less than or equal to `i`, then:

```text
bits(i) = 1 + bits(i - offset)
```

Why?

Because:

- `i` is just `offset` plus the remaining lower bits
- `offset` contributes exactly one set bit

Example:

```text
13 = 8 + 5
1101 = 1000 + 0101
bits(13) = 1 + bits(5)
```

---

## Recurrence

```text
ans[0] = 0
ans[i] = 1 + ans[i - offset]
```

where `offset` is current highest power of two not exceeding `i`.

---

## Java Code

```java
class Solution {
    public int[] countBits(int n) {
        int[] ans = new int[n + 1];
        int offset = 1;

        for (int i = 1; i <= n; i++) {
            if (offset * 2 == i) {
                offset = i;
            }

            ans[i] = 1 + ans[i - offset];
        }

        return ans;
    }
}
```

---

## Example Dry Run

For `n = 8`:

Start:

```text
offset = 1
```

### `i = 1`

```text
ans[1] = 1 + ans[0] = 1
```

### `i = 2`

Now `offset * 2 == i`, so:

```text
offset = 2
ans[2] = 1 + ans[0] = 1
```

### `i = 3`

```text
ans[3] = 1 + ans[1] = 2
```

### `i = 4`

```text
offset = 4
ans[4] = 1 + ans[0] = 1
```

### `i = 5`

```text
ans[5] = 1 + ans[1] = 2
```

and so on.

---

## Complexity

### Time Complexity

- **O(n)**

### Space Complexity

- **O(1)** extra besides output
- Output array: `O(n)`

---

## Pros

- Nice binary block insight
- Linear
- Elegant once understood

## Cons

- Slightly less obvious than shift-based recurrence

---

# Approach 6: BFS / Level-by-Level Generation by Binary Blocks (Conceptual)

## Idea

The counts follow a repeating pattern:

If you know counts for:

```text
[0 ... 2^k - 1]
```

then counts for:

```text
[2^k ... 2^(k+1) - 1]
```

are just:

```text
previous block + 1
```

because every number in the new block is the old block with a leading `1`.

Example:

```text
0,1,1,2   for 0..3
1,2,2,3   for 4..7
```

This can be implemented iteratively in blocks.

---

## Java Code

```java
class Solution {
    public int[] countBits(int n) {
        int[] ans = new int[n + 1];
        int blockStart = 1;

        while (blockStart <= n) {
            int blockSize = blockStart;

            for (int i = 0; i < blockSize && blockStart + i <= n; i++) {
                ans[blockStart + i] = ans[i] + 1;
            }

            blockStart <<= 1;
        }

        return ans;
    }
}
```

---

## Why It Works

The block:

```text
[2^k ... 2^(k+1)-1]
```

has binary representations equal to:

- a leading `1`
- followed by all patterns from `0...2^k-1`

So each bit count is:

```text
1 + ans[oldIndex]
```

---

## Complexity

### Time Complexity

- **O(n)**

### Space Complexity

- **O(1)** extra besides output
- Output array: `O(n)`

---

## Pros

- Strong binary-pattern intuition
- Single pass over the array in block form

## Cons

- Slightly less standard than shift recurrence

---

# Approach 7: Built-In Popcount Per Number (Not Allowed by Follow-Up, but Worth Mentioning)

## Idea

Java has built-in support:

```java
Integer.bitCount(i)
```

So the simplest practical code is just:

- loop through all `i`
- store `Integer.bitCount(i)`

---

## Java Code

```java
class Solution {
    public int[] countBits(int n) {
        int[] ans = new int[n + 1];

        for (int i = 0; i <= n; i++) {
            ans[i] = Integer.bitCount(i);
        }

        return ans;
    }
}
```

---

## Complexity

### Time Complexity

- Usually treated as **O(n)** in practice because bit width is fixed (32 bits)
- But conceptually if you think by operation count per number, it is still constant for Java `int`

### Space Complexity

- **O(1)** extra besides output
- Output array: `O(n)`

---

## Important Note

This violates the spirit of the follow-up asking for:

- no built-in function
- derive a linear-time recurrence manually

So it is not the intended interview solution.

---

# Full Derivation of the Two Most Important DP Recurrences

## Recurrence A: Lowest Set Bit Removal

For any `i > 0`:

```text
i & (i - 1)
```

removes exactly one `1` bit.

So if:

```text
bits(i) = number of 1s in i
```

then:

```text
bits(i) = bits(i & (i - 1)) + 1
```

This gives:

```text
ans[i] = ans[i & (i - 1)] + 1
```

---

## Recurrence B: Right Shift + Lowest Bit

Any integer `i` can be decomposed into:

- `i >> 1` : all bits except the last
- `i & 1` : the last bit

So:

```text
bits(i) = bits(i >> 1) + (i & 1)
```

This gives:

```text
ans[i] = ans[i >> 1] + (i & 1)
```

This is often the easiest recurrence to explain in interviews.

---

# Comparing All Approaches

| Approach                          |                  Time |      Space | Notes                             |
| --------------------------------- | --------------------: | ---------: | --------------------------------- |
| Naive shifting per number         |            O(n log n) | O(1) extra | Simple but not optimal            |
| Kernighan per number              | O(n log n) worst case | O(1) extra | Good bit trick, still not optimal |
| DP using `i & (i-1)`              |                  O(n) | O(1) extra | Very common                       |
| DP using `i >> 1`                 |                  O(n) | O(1) extra | Cleanest explanation              |
| DP using highest power of two     |                  O(n) | O(1) extra | Nice binary-block view            |
| Block generation / offset copying |                  O(n) | O(1) extra | Pattern-based                     |
| Built-in popcount                 |        O(n) practical | O(1) extra | Not follow-up compliant           |

---

# Recommended Final Java Solution

## Best Practical / Interview Solution

```java
class Solution {
    public int[] countBits(int n) {
        int[] ans = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            ans[i] = ans[i >> 1] + (i & 1);
        }

        return ans;
    }
}
```

---

## Why This Version Is Excellent

It is:

- linear time
- single pass
- no built-in popcount
- easy to derive and explain
- easy to remember

---

## Complexity of Recommended Solution

- **Time Complexity:** `O(n)`
- **Space Complexity:** `O(1)` extra beyond output array
- Output array itself is `O(n)`

---

# Dry Run of Recommended Solution

Take:

```text
n = 5
```

Initialize:

```text
ans[0] = 0
```

### `i = 1`

```text
1 >> 1 = 0
1 & 1 = 1
ans[1] = ans[0] + 1 = 1
```

### `i = 2`

```text
2 >> 1 = 1
2 & 1 = 0
ans[2] = ans[1] + 0 = 1
```

### `i = 3`

```text
3 >> 1 = 1
3 & 1 = 1
ans[3] = ans[1] + 1 = 2
```

### `i = 4`

```text
4 >> 1 = 2
4 & 1 = 0
ans[4] = ans[2] + 0 = 1
```

### `i = 5`

```text
5 >> 1 = 2
5 & 1 = 1
ans[5] = ans[2] + 1 = 2
```

Final:

```text
[0,1,1,2,1,2]
```

---

# Edge Cases

## 1. `n = 0`

Output:

```text
[0]
```

## 2. `n = 1`

Output:

```text
[0,1]
```

## 3. Powers of two

For:

```text
1,2,4,8,...
```

the answer is always:

```text
1
```

because only one bit is set.

---

# Common Mistakes

## Mistake 1: Recomputing each number independently

That gives `O(n log n)` instead of `O(n)`.

## Mistake 2: Forgetting `ans[0] = 0`

Everything builds from this base case.

## Mistake 3: Confusing this with decimal digit counting

The problem is about binary representation only.

## Mistake 4: Using built-in popcount in an interview after follow-up asks you not to

It may pass code execution, but it misses the point of the question.

---

# Pattern Recognition

This problem is a classic example of:

- DP over integers
- bit manipulation + recurrence
- reuse of answers for smaller numbers

Signals:

- compute answers for all values from `0` to `n`
- recurrence based on binary structure
- follow-up asking for linear time and no built-in function

Related problems:

- Number of 1 Bits
- Power of Two
- Single Number
- Counting bits in ranges
- DP on binary representations

---

# Interview Explanation Template

A strong explanation:

1. Let `ans[i]` be the number of set bits in `i`.
2. The last bit of `i` is `i & 1`, and the remaining higher bits are `i >> 1`.
3. Therefore:
   `ans[i] = ans[i >> 1] + (i & 1)`
4. Compute this for every `i` from `1` to `n`.
5. This gives a linear-time single-pass solution.

Time:

- `O(n)`

Space:

- `O(n)` for output only

---

# Final Takeaway

The problem becomes easy once you stop treating each number independently.

The key is:

> The bit count of a number can be built from the bit count of a smaller related number.

The cleanest recurrence is:

```text
ans[i] = ans[i >> 1] + (i & 1)
```

That yields the intended **O(n)** dynamic programming solution.
