# 1147. Longest Chunked Palindrome Decomposition — Java Solutions and Detailed Notes

## Problem

We are given a string `text`.

We want to split it into:

```text
subtext1, subtext2, ..., subtextk
```

such that:

1. every chunk is non-empty,
2. concatenating all chunks gives the original string,
3. the chunks are symmetric:

```text
subtext_i == subtext_(k - i + 1)
```

We need to return the **largest possible** value of `k`.

---

## Examples

### Example 1

```text
text = "ghiabcdefhelloadamhelloabcdefghi"
```

A valid split is:

```text
(ghi)(abcdef)(hello)(adam)(hello)(abcdef)(ghi)
```

So the answer is:

```text
7
```

---

### Example 2

```text
text = "merchant"
```

No nontrivial symmetric chunking exists, so:

```text
(merchant)
```

Answer:

```text
1
```

---

### Example 3

```text
text = "antaprezatepzapreanta"
```

A valid split is:

```text
(a)(nt)(a)(pre)(za)(tep)(za)(pre)(a)(nt)(a)
```

So the answer is:

```text
11
```

---

# Key idea

This is a **maximize number of symmetric chunks** problem.

To maximize the number of chunks, we should greedily peel off the **smallest possible matching prefix and suffix** at each step.

Why?

Because if a prefix and suffix match, taking them immediately gives us:

- 2 chunks right away,
- and leaves the middle substring to be decomposed independently.

If we delayed taking a smaller valid match and instead took a larger outer chunk, we would never get **more** chunks from the outside.

That is the core greedy insight.

---

# Approach 1: Recursive Greedy with substring comparison

## Idea

At each step:

- try all prefix lengths `len = 1 .. n/2`
- if:

```text
text[0 .. len-1] == text[n-len .. n-1]
```

then we can take these two as matching outer chunks and recurse on the middle:

```text
text[len .. n-len-1]
```

If no such prefix/suffix match exists, then the whole current string must be one chunk.

---

## Why this greedy choice is correct

Suppose the smallest matching prefix/suffix has length `len`.

If we take them now, we gain:

```text
2 + answer(middle)
```

Could a larger first choice lead to more chunks? No.

A larger outer match would consume more characters on both ends, leaving a smaller middle. Since the smaller match was already valid, taking it first cannot reduce the maximum number of chunks. In fact, it is the best way to maximize chunk count.

So the first matching prefix/suffix we find is safe to take.

---

## Java code

```java
class Solution {
    public int longestDecomposition(String text) {
        int n = text.length();

        for (int len = 1; len <= n / 2; len++) {
            String left = text.substring(0, len);
            String right = text.substring(n - len);

            if (left.equals(right)) {
                return 2 + longestDecomposition(text.substring(len, n - len));
            }
        }

        return 1;
    }
}
```

---

## Complexity

Let `n = text.length()`.

Each recursive level scans possible chunk lengths and compares substrings.

In Java, `substring(...).equals(...)` costs proportional to chunk length.

Worst-case time complexity:

```text
O(n^2)
```

Space complexity:

```text
O(n)
```

due to recursion depth and substring objects.

Given `n <= 1000`, this is acceptable.

---

# Approach 2: Iterative two-ended greedy with builders

## Idea

Instead of recursion, build prefix and suffix incrementally:

- move one pointer from the left,
- one pointer from the right,
- append characters to `leftPart` and prepend to `rightPart`,
- whenever they match, count two chunks and reset both builders.

If the pointers meet with some unmatched middle part left, that contributes one final chunk.

This avoids repeatedly scanning all possible lengths from scratch and expresses the greedy strategy more directly.

---

## Example

For:

```text
"ghiabcdefhelloadamhelloabcdefghi"
```

we build:

- `"g"` vs `"i"` → no
- `"gh"` vs `"hi"` → no
- `"ghi"` vs `"ghi"` → match → count `2`

Then continue on the remaining middle section.

---

## Java code

```java
class Solution {
    public int longestDecomposition(String text) {
        int n = text.length();
        StringBuilder left = new StringBuilder();
        StringBuilder right = new StringBuilder();

        int ans = 0;
        int i = 0, j = n - 1;

        while (i < j) {
            left.append(text.charAt(i));
            right.insert(0, text.charAt(j));

            if (left.toString().equals(right.toString())) {
                ans += 2;
                left.setLength(0);
                right.setLength(0);
            }

            i++;
            j--;
        }

        if (i == j || left.length() > 0) {
            ans += 1;
        }

        return ans;
    }
}
```

---

## Complexity

The use of:

```java
right.insert(0, ...)
```

is expensive because it shifts characters.

So worst-case time complexity is still:

```text
O(n^2)
```

Space complexity:

```text
O(n)
```

---

## Comment

This is intuitive, but not the cleanest implementation in Java because prepending to a `StringBuilder` is costly.

---

# Approach 3: Recursive Greedy with rolling hash optimization

## Idea

The bottleneck in the previous approaches is substring comparison.

We can speed up prefix/suffix equality checks using rolling hash.

At each step, instead of extracting substrings, we compare hashes of:

- prefix of length `len`
- suffix of length `len`

The greedy strategy remains the same:

- find the smallest `len` where prefix and suffix match,
- recurse on the middle.

Because hash comparisons are `O(1)` after preprocessing, this improves the inner checks.

---

## Hash setup

Use polynomial rolling hash:

```text
H[i] = hash of prefix ending before i
```

Then substring hash can be computed in O(1).

We still recurse on subranges `[l, r]` instead of creating new strings.

---

## Java code

```java
class Solution {
    private static final long MOD = 1_000_000_007L;
    private static final long BASE = 911382323L;

    private long[] hash;
    private long[] pow;
    private String text;

    public int longestDecomposition(String text) {
        this.text = text;
        int n = text.length();

        hash = new long[n + 1];
        pow = new long[n + 1];
        pow[0] = 1;

        for (int i = 0; i < n; i++) {
            hash[i + 1] = (hash[i] * BASE + text.charAt(i)) % MOD;
            pow[i + 1] = (pow[i] * BASE) % MOD;
        }

        return solve(0, n - 1);
    }

    private int solve(int l, int r) {
        if (l > r) return 0;
        if (l == r) return 1;

        int len = r - l + 1;
        for (int sz = 1; sz <= len / 2; sz++) {
            if (getHash(l, l + sz - 1) == getHash(r - sz + 1, r)) {
                // To avoid relying only on hashes, verify actual substring equality.
                if (equalsRange(l, r - sz + 1, sz)) {
                    return 2 + solve(l + sz, r - sz);
                }
            }
        }

        return 1;
    }

    private long getHash(int l, int r) {
        long res = (hash[r + 1] - hash[l] * pow[r - l + 1]) % MOD;
        if (res < 0) res += MOD;
        return res;
    }

    private boolean equalsRange(int a, int b, int len) {
        for (int i = 0; i < len; i++) {
            if (text.charAt(a + i) != text.charAt(b + i)) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Complexity

Hash comparison is O(1), but we still may do a verification on matches.

Worst-case remains around:

```text
O(n^2)
```

But the constant factors can be smaller than repeated substring creation.

Space complexity:

```text
O(n)
```

---

# Approach 4: Dynamic Programming on intervals (educational, not necessary)

## Idea

Define:

```text
dp[l][r] = maximum chunked palindrome decomposition count for text[l..r]
```

Transition:

- try every chunk size `len`
- if prefix and suffix of that size match, then:

```text
dp[l][r] = max(dp[l][r], 2 + dp[l+len][r-len])
```

If nothing matches, then:

```text
dp[l][r] = 1
```

This is conceptually valid but too heavy and unnecessary for this problem.

---

## Java code

```java
class Solution {
    public int longestDecomposition(String text) {
        int n = text.length();
        int[][] dp = new int[n][n];

        for (int len = 1; len <= n; len++) {
            for (int l = 0; l + len - 1 < n; l++) {
                int r = l + len - 1;
                dp[l][r] = 1;

                for (int sz = 1; sz <= len / 2; sz++) {
                    if (text.substring(l, l + sz).equals(text.substring(r - sz + 1, r + 1))) {
                        int middle = (l + sz <= r - sz) ? dp[l + sz][r - sz] : 0;
                        dp[l][r] = Math.max(dp[l][r], 2 + middle);
                        break; // smallest valid match is enough
                    }
                }
            }
        }

        return dp[0][n - 1];
    }
}
```

---

## Complexity

Time complexity:

```text
O(n^3)
```

Space complexity:

```text
O(n^2)
```

This is only useful as an educational derivation.

---

# Why greedy works

This is the most important part of the problem.

Suppose at some substring `text[l..r]`, the smallest matching outer chunk has size `x`.

So:

```text
text[l .. l+x-1] == text[r-x+1 .. r]
```

If we take those two chunks now, we get:

```text
2 + answer(text[l+x .. r-x])
```

Now suppose there were an optimal solution that used a larger outer chunk size `y > x` first.

That larger solution cannot produce more chunks from the outside than taking the smaller valid chunk first, because:

- using a larger chunk consumes strictly more characters,
- and the smaller chunk was already a valid symmetric pair,
- so delaying it only makes the decomposition coarser, not finer.

Thus taking the **smallest valid outer match** is always safe and optimal.

This is why the first match we find can be chosen greedily.

---

# Comparison of approaches

## Approach 1: Recursive greedy with substring comparisons

### Pros

- shortest and cleanest
- easy to reason about
- fully sufficient for constraints

### Cons

- repeated substring work

### Complexity

```text
Time:  O(n^2)
Space: O(n)
```

---

## Approach 2: Iterative two-ended builder approach

### Pros

- intuitive simulation of chunk formation
- avoids explicit recursion

### Cons

- `insert(0, ...)` is costly in Java
- still quadratic

### Complexity

```text
Time:  O(n^2)
Space: O(n)
```

---

## Approach 3: Greedy with rolling hash

### Pros

- speeds up equality checks conceptually
- good if constraints were larger

### Cons

- more code
- still not dramatically better here

### Complexity

Roughly:

```text
Time:  O(n^2)
Space: O(n)
```

---

## Approach 4: Interval DP

### Pros

- shows the full optimization formulation

### Cons

- overkill
- too slow

### Complexity

```text
Time:  O(n^3)
Space: O(n^2)
```

---

# Final recommended solution

Because `text.length <= 1000`, the best practical solution is the clean recursive greedy approach.

It is simple, correct, and efficient enough.

## Recommended Java code

```java
class Solution {
    public int longestDecomposition(String text) {
        int n = text.length();

        for (int len = 1; len <= n / 2; len++) {
            if (text.substring(0, len).equals(text.substring(n - len))) {
                return 2 + longestDecomposition(text.substring(len, n - len));
            }
        }

        return 1;
    }
}
```

---

# Minor optimization: work with indices instead of creating substrings

If you want to avoid creating many substring objects, you can pass boundaries.

## Java code

```java
class Solution {
    public int longestDecomposition(String text) {
        return solve(text, 0, text.length() - 1);
    }

    private int solve(String text, int l, int r) {
        if (l > r) return 0;
        if (l == r) return 1;

        int len = r - l + 1;
        for (int sz = 1; sz <= len / 2; sz++) {
            if (equal(text, l, r - sz + 1, sz)) {
                return 2 + solve(text, l + sz, r - sz);
            }
        }

        return 1;
    }

    private boolean equal(String text, int i, int j, int len) {
        for (int k = 0; k < len; k++) {
            if (text.charAt(i + k) != text.charAt(j + k)) {
                return false;
            }
        }
        return true;
    }
}
```

This avoids allocating substrings and is often the nicest practical version.

---

# Walkthrough of Example 1

```text
text = "ghiabcdefhelloadamhelloabcdefghi"
```

- smallest matching prefix/suffix:

  ```text
  "ghi"
  ```

  so answer becomes:

  ```text
  2 + solve("abcdefhelloadamhelloabcdef")
  ```

- inside that, smallest matching prefix/suffix:

  ```text
  "abcdef"
  ```

  so:

  ```text
  2 + solve("helloadamhello")
  ```

- inside that:

  ```text
  "hello"
  ```

  so:

  ```text
  2 + solve("adam")
  ```

- `"adam"` has no matching smaller prefix/suffix, so contributes `1`

Total:

```text
2 + 2 + 2 + 1 = 7
```

---

# Walkthrough of Example 3

```text
text = "antaprezatepzapreanta"
```

We greedily peel off:

- `"a"`
- `"nt"`
- `"a"`
- `"pre"`
- `"za"`

middle is `"tep"` which contributes `1`.

So total:

```text
2 + 2 + 2 + 2 + 2 + 1 = 11
```

---

# Takeaway pattern

This problem is a strong example of:

```text
Greedy outer matching on a symmetric decomposition problem
```

When the goal is to maximize the number of symmetric pieces, the correct instinct is often:

- find the **smallest valid matching outer pair**,
- peel it off,
- recurse on the middle.

That is exactly what makes this problem elegant.
