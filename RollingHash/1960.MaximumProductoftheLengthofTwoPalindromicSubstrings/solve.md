# 1960. Maximum Product of the Length of Two Palindromic Substrings — Java Solutions and Detailed Notes

## Problem

We are given a string `s`.

We need to choose **two non-overlapping odd-length palindromic substrings** such that their product of lengths is maximized.

Formally, choose:

```text
0 <= i <= j < k <= l < n
```

such that:

- `s[i..j]` is a palindrome,
- `s[k..l]` is a palindrome,
- both palindromes have **odd length**,
- and the two substrings do not intersect.

Return the maximum possible product of their lengths.

---

## Examples

### Example 1

```text
s = "ababbb"
```

Choose:

- `"aba"` of length `3`
- `"bbb"` of length `3`

Product:

```text
3 * 3 = 9
```

Answer:

```text
9
```

---

### Example 2

```text
s = "zaaaxbbby"
```

Choose:

- `"aaa"` of length `3`
- `"bbb"` of length `3`

Product:

```text
3 * 3 = 9
```

Answer:

```text
9
```

---

# Core insight

This is not asking for just one palindrome.

It asks for:

- a palindrome fully on the left,
- another fully on the right,
- with a split point between them.

So the problem naturally becomes:

1. for every index `i`, find the **best odd palindrome fully ending at or before `i`**
2. for every index `i`, find the **best odd palindrome fully starting at or after `i`**
3. try every split:
   ```text
   left part ends at i, right part starts at i+1
   ```
   and maximize:
   ```text
   bestLeft[i] * bestRight[i+1]
   ```

So the real challenge is building those left/right best arrays efficiently.

---

# Approach 1: Brute force expand around every center and try all pairs

## Idea

Every odd palindrome has a center.

So we can:

- expand around every center,
- collect all odd palindromic substrings,
- then try every pair of non-overlapping palindromes,
- compute the maximum product.

---

## Java code

```java
import java.util.*;

class Solution {
    static class Pal {
        int l, r, len;
        Pal(int l, int r) {
            this.l = l;
            this.r = r;
            this.len = r - l + 1;
        }
    }

    public long maxProduct(String s) {
        int n = s.length();
        List<Pal> pals = new ArrayList<>();

        for (int center = 0; center < n; center++) {
            int l = center, r = center;
            while (l >= 0 && r < n && s.charAt(l) == s.charAt(r)) {
                pals.add(new Pal(l, r));
                l--;
                r++;
            }
        }

        long ans = 0;
        for (int i = 0; i < pals.size(); i++) {
            for (int j = i + 1; j < pals.size(); j++) {
                Pal a = pals.get(i);
                Pal b = pals.get(j);

                if (a.r < b.l || b.r < a.l) {
                    ans = Math.max(ans, 1L * a.len * b.len);
                }
            }
        }

        return ans;
    }
}
```

---

## Complexity

If there are many palindromes, this explodes.

- expanding all centers can already generate `O(n^2)` palindromes
- pairing them is much worse

Time complexity:

```text
O(n^4) in the worst case
```

or at least unusable.

This is only a conceptual baseline.

---

# Approach 2: Expand around centers + build best ending/starting arrays (too slow for n = 1e5)

## Idea

Instead of trying all pairs explicitly:

- expand around each center,
- whenever we find palindrome `[l..r]`, update:
  - best palindrome ending at `r`
  - best palindrome starting at `l`

Then propagate:

- `leftBest[i] = max(leftBest[i], leftBest[i-1])`
- `rightBest[i] = max(rightBest[i], rightBest[i+1])`

Then try all splits.

This is much better than brute force pairing, but center expansion still costs `O(n^2)` in the worst case, which is too slow for `10^5`.

---

## Java code

```java
class Solution {
    public long maxProduct(String s) {
        int n = s.length();
        int[] endBest = new int[n];
        int[] startBest = new int[n];

        for (int c = 0; c < n; c++) {
            int l = c, r = c;
            while (l >= 0 && r < n && s.charAt(l) == s.charAt(r)) {
                int len = r - l + 1;
                endBest[r] = Math.max(endBest[r], len);
                startBest[l] = Math.max(startBest[l], len);
                l--;
                r++;
            }
        }

        int[] left = new int[n];
        int[] right = new int[n];

        left[0] = endBest[0];
        for (int i = 1; i < n; i++) {
            left[i] = Math.max(left[i - 1], endBest[i]);
        }

        right[n - 1] = startBest[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            right[i] = Math.max(right[i + 1], startBest[i]);
        }

        long ans = 0;
        for (int i = 0; i < n - 1; i++) {
            ans = Math.max(ans, 1L * left[i] * right[i + 1]);
        }

        return ans;
    }
}
```

---

## Complexity

Worst-case center expansion is:

```text
O(n^2)
```

So still too slow for large `n`.

---

# Approach 3: Manacher’s algorithm + interval propagation (recommended)

This is the standard optimal solution.

## Main idea

Use **Manacher’s algorithm** to compute, for every center `i`, the radius of the longest odd palindrome centered at `i`.

From this, we know every odd palindrome interval.

Then we want:

- `left[i]`: maximum odd palindrome length fully contained in prefix `s[0..i]`
- `right[i]`: maximum odd palindrome length fully contained in suffix `s[i..n-1]`

Finally, compute:

```text
max(left[i] * right[i+1]) for all i
```

---

## Step 1: Manacher for odd palindromes

For each index `i`, compute:

```text
d1[i] = radius count for odd palindrome centered at i
```

Meaning the longest odd palindrome centered at `i` has length:

```text
2 * d1[i] - 1
```

For example:

- if `d1[i] = 1`, palindrome is just the single character
- if `d1[i] = 3`, palindrome length is `5`

---

## Step 2: Convert centers into endpoint information

For each center `i` with radius `r = d1[i]`, the longest odd palindrome has:

```text
L = i - r + 1
R = i + r - 1
len = 2r - 1
```

This gives one maximal palindrome per center.

Now, an important observation:

For the purpose of the final maximum product, it is enough to work with the maximal odd palindrome around each center and propagate the best reachable values.

We want to know:

- the best palindrome ending at each position,
- the best palindrome starting at each position.

A clean way is to use event arrays and propagate lengths inward/outward.

---

## Step 3: Build best palindrome ending at each position

If center `i` has radius `r`, then for each valid radius `t` from `1` to `r`, there is a palindrome ending at:

```text
i + t - 1
```

with length:

```text
2t - 1
```

We need the maximum such value for every end position.

Doing this naïvely over all radii would be quadratic.

Instead, use a sweep / propagation trick:

- record that at end `R = i + r - 1`, palindrome length `2r - 1` is available
- then as we move leftward from that endpoint, palindrome lengths decrease by `2`

A symmetric trick works for starts.

This can be implemented using max propagation arrays.

---

# Simpler practical formulation

A widely used clean implementation is:

1. compute `d1`
2. build an array `maxAtEnd[end]` using the longest palindrome for each center
3. propagate left-to-right:
   ```text
   maxAtEnd[i] = max(maxAtEnd[i], maxAtEnd[i-1] - 2)
   ```
   because if there is a palindrome ending earlier, trimming one char from each side gives another odd palindrome ending later by 1 with length reduced by 2
4. then prefix-max this to get best-in-prefix

Similarly for `maxAtStart[start]`, then propagate right-to-left and suffix-max.

This is the elegant linear solution.

---

## Why the `-2` propagation works

If there is an odd palindrome of length `L` ending at position `e`, then by removing its outer two characters we get another odd palindrome of length `L - 2` ending at position `e - 1`.

Conversely, when sweeping forward, a palindrome ending at `e - 1` of length `L` implies there exists an odd palindrome ending at `e` of length at least `L - 2` as long as `L - 2 > 0`.

This lets us propagate maximal endpoint lengths efficiently.

---

## Java code

```java
class Solution {
    public long maxProduct(String s) {
        int n = s.length();
        int[] d1 = manacherOdd(s);

        int[] end = new int[n];
        int[] start = new int[n];

        // Record longest palindrome ending at each R and starting at each L
        for (int i = 0; i < n; i++) {
            int r = d1[i];
            int L = i - r + 1;
            int R = i + r - 1;
            int len = 2 * r - 1;
            end[R] = Math.max(end[R], len);
            start[L] = Math.max(start[L], len);
        }

        // Propagate to fill all possible odd palindromes ending at each position
        for (int i = 1; i < n; i++) {
            end[i] = Math.max(end[i], end[i - 1] - 2);
        }

        // Prefix max: best palindrome anywhere in prefix [0..i]
        int[] left = new int[n];
        left[0] = end[0];
        for (int i = 1; i < n; i++) {
            left[i] = Math.max(left[i - 1], end[i]);
        }

        // Propagate from right for starts
        for (int i = n - 2; i >= 0; i--) {
            start[i] = Math.max(start[i], start[i + 1] - 2);
        }

        // Suffix max: best palindrome anywhere in suffix [i..n-1]
        int[] right = new int[n];
        right[n - 1] = start[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            right[i] = Math.max(right[i + 1], start[i]);
        }

        long ans = 0;
        for (int i = 0; i < n - 1; i++) {
            ans = Math.max(ans, 1L * left[i] * right[i + 1]);
        }

        return ans;
    }

    private int[] manacherOdd(String s) {
        int n = s.length();
        int[] d1 = new int[n];
        int l = 0, r = -1;

        for (int i = 0; i < n; i++) {
            int k = (i > r) ? 1 : Math.min(d1[l + r - i], r - i + 1);
            while (i - k >= 0 && i + k < n && s.charAt(i - k) == s.charAt(i + k)) {
                k++;
            }
            d1[i] = k;
            if (i + k - 1 > r) {
                l = i - k + 1;
                r = i + k - 1;
            }
        }

        return d1;
    }
}
```

---

## Complexity

Manacher runs in:

```text
O(n)
```

All propagation and split scans also run in:

```text
O(n)
```

So total time complexity:

```text
O(n)
```

Space complexity:

```text
O(n)
```

This is optimal.

---

# Approach 4: Prefix/suffix palindrome DP with center expansion (educational, too slow)

## Idea

One could try:

- precompute palindrome radii or palindrome table,
- then dynamic programming for best left/right arrays.

But any DP table over substring intervals is `O(n^2)` memory/time and too large for `10^5`.

So this is only educational and not practical here.

---

# Why the split formulation is correct

Suppose the optimal pair is:

- first palindrome in interval `[i..j]`
- second palindrome in interval `[k..l]`
- with `j < k`

Then there exists a split point `p = j` such that:

- the first palindrome lies fully in prefix `[0..p]`
- the second palindrome lies fully in suffix `[p+1..n-1]`

So if we know:

- best odd palindrome length in every prefix,
- best odd palindrome length in every suffix,

then trying every split finds the optimum.

That reduces the problem to a one-dimensional optimization after preprocessing.

---

# Comparison of approaches

## Approach 1: Brute force all palindromes and pairs
### Pros
- direct interpretation of the problem

### Cons
- hopelessly slow

### Complexity
```text
Too large
```

---

## Approach 2: Center expansion + best endpoint arrays
### Pros
- much simpler than Manacher
- works for smaller constraints

### Cons
- worst-case O(n^2)

### Complexity
```text
O(n^2)
```

---

## Approach 3: Manacher + propagation (Recommended)
### Pros
- optimal
- elegant
- standard solution for large palindrome-range problems

### Cons
- requires understanding Manacher and the propagation trick

### Complexity
```text
O(n)
```

---

# Final recommended solution

Use:

## Manacher’s algorithm + left/right best palindrome arrays

This gives an exact linear-time solution.

---

# Final polished Java solution

```java
class Solution {
    public long maxProduct(String s) {
        int n = s.length();
        int[] d1 = manacherOdd(s);

        int[] end = new int[n];
        int[] start = new int[n];

        for (int i = 0; i < n; i++) {
            int radius = d1[i];
            int L = i - radius + 1;
            int R = i + radius - 1;
            int len = 2 * radius - 1;
            end[R] = Math.max(end[R], len);
            start[L] = Math.max(start[L], len);
        }

        for (int i = 1; i < n; i++) {
            end[i] = Math.max(end[i], end[i - 1] - 2);
        }

        int[] left = new int[n];
        left[0] = end[0];
        for (int i = 1; i < n; i++) {
            left[i] = Math.max(left[i - 1], end[i]);
        }

        for (int i = n - 2; i >= 0; i--) {
            start[i] = Math.max(start[i], start[i + 1] - 2);
        }

        int[] right = new int[n];
        right[n - 1] = start[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            right[i] = Math.max(right[i + 1], start[i]);
        }

        long ans = 0;
        for (int i = 0; i < n - 1; i++) {
            ans = Math.max(ans, 1L * left[i] * right[i + 1]);
        }

        return ans;
    }

    private int[] manacherOdd(String s) {
        int n = s.length();
        int[] d1 = new int[n];
        int l = 0, r = -1;

        for (int i = 0; i < n; i++) {
            int k = (i > r) ? 1 : Math.min(d1[l + r - i], r - i + 1);

            while (i - k >= 0 && i + k < n && s.charAt(i - k) == s.charAt(i + k)) {
                k++;
            }

            d1[i] = k;

            if (i + k - 1 > r) {
                l = i - k + 1;
                r = i + k - 1;
            }
        }

        return d1;
    }
}
```

---

# Walkthrough on `"ababbb"`

Odd palindromes include:

- `"a"`, `"b"`, `"a"`, `"b"`, `"b"`, `"b"`
- `"aba"` centered at index 1
- `"bab"` centered at index 2
- `"bbb"` centered at index 4

Best left-prefix palindromes by split eventually give:

- left side best = `3` (`"aba"`)
- right side best = `3` (`"bbb"`)

Product:

```text
3 * 3 = 9
```

---

# Takeaway pattern

This problem is a strong example of the pattern:

```text
Palindrome preprocessing + prefix/suffix optimization
```

When you need:

- palindromes in many locations,
- split-based maximization,
- large `n`,

think of:

- **Manacher** for palindrome radii,
- then build prefix/suffix best arrays.

That is the right mental model here.
