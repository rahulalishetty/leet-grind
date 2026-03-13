# 2001. Number of Pairs of Interchangeable Rectangles — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public long interchangeableRectangles(int[][] rectangles) {

    }
}
```

---

# Problem Restatement

Each rectangle is given as:

```text
[width, height]
```

Two rectangles are interchangeable if they have the same width-to-height ratio:

```text
width1 / height1 == width2 / height2
```

We need to count how many pairs:

```text
(i, j), where i < j
```

satisfy that condition.

---

# Core Insight

Two fractions are equal if their reduced forms are equal.

So instead of comparing:

```text
width / height
```

as floating-point values, reduce the pair by their GCD:

```text
(width / g, height / g), where g = gcd(width, height)
```

All rectangles with the same reduced ratio belong to the same group.

If one group has `c` rectangles, then the number of pairs from that group is:

```text
c * (c - 1) / 2
```

So the whole problem becomes:

1. normalize each rectangle ratio
2. count how many times each ratio appears
3. sum combinations across groups

---

# Why You Should Avoid Floating Point

A tempting approach is to use:

```java
double ratio = (double) width / height;
```

and count equal ratios.

That is risky because floating-point representations can introduce precision issues.

For example, many rational numbers cannot be represented exactly in binary floating-point.

So the safe way is to use the reduced fraction form.

---

# Approach 1 — HashMap with Reduced Ratio (Recommended)

## Idea

For each rectangle:

1. compute `g = gcd(width, height)`
2. reduce it to:
   ```text
   (width / g, height / g)
   ```
3. use that reduced pair as the key in a hash map

At the end, each map entry gives the size of one ratio group.

Then compute:

```text
count * (count - 1) / 2
```

for each group and sum them.

---

## Java Code

```java
import java.util.*;

class Solution {
    public long interchangeableRectangles(int[][] rectangles) {
        Map<String, Long> freq = new HashMap<>();

        for (int[] rect : rectangles) {
            int w = rect[0];
            int h = rect[1];
            int g = gcd(w, h);

            int rw = w / g;
            int rh = h / g;

            String key = rw + "#" + rh;
            freq.put(key, freq.getOrDefault(key, 0L) + 1);
        }

        long ans = 0;
        for (long count : freq.values()) {
            ans += count * (count - 1) / 2;
        }

        return ans;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Complexity

Let `n = rectangles.length`.

For each rectangle, we compute a GCD in logarithmic time.

So:

```text
Time:  O(n log M)
Space: O(n)
```

where `M` is the maximum width/height.

This is efficient for the constraints.

---

# Approach 2 — One-Pass Counting While Building the Map

## Idea

Instead of building frequencies first and then summing combinations at the end, we can count pairs online.

When we see a new rectangle with some reduced ratio:

- if that ratio has already appeared `c` times,
- then the new rectangle forms exactly `c` new interchangeable pairs.

So:

1. look up current count for the ratio
2. add that count to the answer
3. increment the count

This avoids a second pass over the map values.

---

## Why this works

Suppose the current rectangle is the `(c + 1)`th one in its group.

It pairs with each of the previous `c` rectangles in that same group.

So the number of newly formed pairs is exactly `c`.

---

## Java Code

```java
import java.util.*;

class Solution {
    public long interchangeableRectangles(int[][] rectangles) {
        Map<Long, Long> freq = new HashMap<>();
        long ans = 0;

        for (int[] rect : rectangles) {
            int w = rect[0];
            int h = rect[1];
            int g = gcd(w, h);

            int rw = w / g;
            int rh = h / g;

            long key = (((long) rw) << 32) | (rh & 0xffffffffL);

            long seen = freq.getOrDefault(key, 0L);
            ans += seen;
            freq.put(key, seen + 1);
        }

        return ans;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Complexity

Same asymptotic complexity:

```text
Time:  O(n log M)
Space: O(n)
```

This is often the cleanest implementation in practice.

---

# Approach 3 — Sorting Reduced Ratios

## Idea

Another valid approach is:

1. reduce every rectangle ratio
2. store the normalized pairs
3. sort them
4. count consecutive equal groups
5. add combinations for each group

This avoids hash maps, but sorting adds a `log n` factor.

---

## Java Code

```java
import java.util.*;

class Solution {
    public long interchangeableRectangles(int[][] rectangles) {
        long[] arr = new long[rectangles.length];

        for (int i = 0; i < rectangles.length; i++) {
            int w = rectangles[i][0];
            int h = rectangles[i][1];
            int g = gcd(w, h);

            int rw = w / g;
            int rh = h / g;

            arr[i] = (((long) rw) << 32) | (rh & 0xffffffffL);
        }

        Arrays.sort(arr);

        long ans = 0;
        long count = 1;

        for (int i = 1; i < arr.length; i++) {
            if (arr[i] == arr[i - 1]) {
                count++;
            } else {
                ans += count * (count - 1) / 2;
                count = 1;
            }
        }

        ans += count * (count - 1) / 2;
        return ans;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Complexity

Sorting dominates:

```text
Time:  O(n log n + n log M)
Space: O(n)
```

Still efficient, but usually slightly worse than the hash map method.

---

# Approach 4 — Floating Point Ratio Map (Not Recommended)

## Idea

Use:

```java
double ratio = (double) width / height;
```

and store counts in a map keyed by `Double`.

This may appear to work for many cases, but it is not robust.

---

## Why it is risky

Floating-point equality is not reliable for all rational values.

Even if this passes some tests, it is not the safest or most principled solution.

So prefer reduced fractions instead.

---

## Java Code

```java
import java.util.*;

class Solution {
    public long interchangeableRectangles(int[][] rectangles) {
        Map<Double, Long> freq = new HashMap<>();
        long ans = 0;

        for (int[] rect : rectangles) {
            double ratio = (double) rect[0] / rect[1];
            long seen = freq.getOrDefault(ratio, 0L);
            ans += seen;
            freq.put(ratio, seen + 1);
        }

        return ans;
    }
}
```

---

## Complexity

```text
Time:  O(n)
Space: O(n)
```

But correctness is less trustworthy than the reduced-fraction solution.

---

# Detailed Walkthrough

## Example 1

```text
rectangles = [[4,8],[3,6],[10,20],[15,30]]
```

Reduce each ratio:

- `[4,8]` → `(1,2)`
- `[3,6]` → `(1,2)`
- `[10,20]` → `(1,2)`
- `[15,30]` → `(1,2)`

All 4 rectangles are in the same group.

Number of pairs:

```text
4 * 3 / 2 = 6
```

So answer is:

```text
6
```

---

## Example 2

```text
rectangles = [[4,5],[7,8]]
```

Reduced ratios:

- `[4,5]` → `(4,5)`
- `[7,8]` → `(7,8)`

Different groups, so no pairs.

Answer:

```text
0
```

---

# Important Correctness Argument

For any rectangle `[w, h]`, let:

```text
g = gcd(w, h)
```

Then the normalized form is:

```text
(w / g, h / g)
```

Two rectangles have the same ratio iff their normalized forms are identical.

That is because reducing a fraction to lowest terms gives a unique representation.

So grouping by normalized ratio is exactly equivalent to grouping by width-to-height ratio.

Once grouped, counting interchangeable pairs is just counting pairs within each group.

---

# Common Pitfalls

## 1. Using floating-point ratios

This may introduce precision bugs.

Prefer GCD normalization.

---

## 2. Forgetting to use long for the answer

The number of pairs can be large:

```text
n = 10^5
```

So the total pair count can exceed `int`.

Use `long`.

---

## 3. Using unreduced `(width, height)` directly

For example:

```text
(4,8) and (3,6)
```

represent the same ratio, but the raw pairs are different.

You must reduce them first.

---

## 4. Using a poor map key

If you store the reduced pair as a string, it works, but packed integer keys are usually faster.

---

# Best Approach

## Recommended: One-pass HashMap with reduced ratio

This is the cleanest and most efficient solution because:

- it avoids floating-point issues
- it counts pairs online
- it uses only `O(n)` memory
- it is easy to implement

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    public long interchangeableRectangles(int[][] rectangles) {
        Map<Long, Long> freq = new HashMap<>();
        long ans = 0;

        for (int[] rect : rectangles) {
            int w = rect[0];
            int h = rect[1];
            int g = gcd(w, h);

            int rw = w / g;
            int rh = h / g;

            long key = (((long) rw) << 32) | (rh & 0xffffffffL);

            long seen = freq.getOrDefault(key, 0L);
            ans += seen;
            freq.put(key, seen + 1);
        }

        return ans;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

# Complexity Summary

```text
Time:  O(n log M)
Space: O(n)
```

where `M` is the maximum width or height.

---

# Final Takeaway

The decisive trick is to avoid comparing decimal ratios directly.

Instead:

- reduce each rectangle’s ratio using GCD
- group equal reduced ratios
- count how many pairs each group contributes
