# 2002. Maximum Product of the Length of Two Palindromic Subsequences

## Problem Restatement

Given a string `s`, choose **two disjoint subsequences** such that:

- each subsequence is a palindrome
- the two subsequences do not use the same original index
- the product of their lengths is as large as possible

Return that maximum product.

---

## Constraints

```text
2 <= s.length <= 12
s consists of lowercase English letters only
```

The most important clue is:

```text
n <= 12
```

That is extremely small.

Whenever you see such a small `n`, especially with **subsequence selection**, you should immediately think about:

- bitmask enumeration
- subset DP
- brute force with pruning

Because `2^12 = 4096`, exploring all subsets is completely feasible.

---

# Core Insight

A subsequence can be represented by a **bitmask** of chosen indices.

For example, if:

```text
s = "abcde"
mask = 10101 (binary)
```

then the subsequence is:

```text
"a", "c", "e"  -> "ace"
```

So the problem becomes:

1. enumerate subsets of indices
2. determine which subsets form palindromic subsequences
3. choose two **disjoint** palindromic subsets with maximum product of lengths

---

# Approach 1: Full Bitmask Enumeration of Palindromic Subsequences

## Idea

Enumerate every non-empty subset of indices.

For each subset:

- build the subsequence
- check whether it is a palindrome
- if yes, record its length

Then try every pair of palindromic subsets:

- they must be disjoint
- maximize `len1 * len2`

Because `n <= 12`, the number of subsets is at most `4095`, and checking all pairs is still manageable.

---

## Step 1: Represent subsequences with bitmasks

Let `mask` be a number from `1` to `(1 << n) - 1`.

If bit `i` is set, we include `s.charAt(i)` in the subsequence.

---

## Step 2: Check whether a subset is palindromic

Build the subsequence and use two pointers:

- `left = 0`
- `right = length - 1`

If all mirrored characters match, it is a palindrome.

---

## Step 3: Compare all palindromic pairs

If `mask1 & mask2 == 0`, they are disjoint.

Then update answer with:

```text
length(mask1) * length(mask2)
```

---

## Why this works

This approach literally tries every possible palindromic subsequence and every valid disjoint pair.

Since the search space is tiny, brute force is acceptable.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int maxProduct(String s) {
        int n = s.length();
        int total = 1 << n;

        int[] palLen = new int[total];

        // Record length of subsequence if it is palindrome, otherwise 0
        for (int mask = 1; mask < total; mask++) {
            if (isPalindromeSubsequence(s, mask)) {
                palLen[mask] = Integer.bitCount(mask);
            }
        }

        int ans = 0;

        for (int mask1 = 1; mask1 < total; mask1++) {
            if (palLen[mask1] == 0) continue;

            for (int mask2 = mask1 + 1; mask2 < total; mask2++) {
                if (palLen[mask2] == 0) continue;

                if ((mask1 & mask2) == 0) {
                    ans = Math.max(ans, palLen[mask1] * palLen[mask2]);
                }
            }
        }

        return ans;
    }

    private boolean isPalindromeSubsequence(String s, int mask) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            if ((mask & (1 << i)) != 0) {
                sb.append(s.charAt(i));
            }
        }

        int left = 0, right = sb.length() - 1;
        while (left < right) {
            if (sb.charAt(left) != sb.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }

        return true;
    }
}
```

---

## Complexity

Let `N = 2^n`.

### Palindrome checking for all subsets

For each subset, building/checking takes `O(n)`.

So:

```text
O(n * 2^n)
```

### Pair checking

There are about `O(2^(2n))` subset pairs in the worst view, but with `n <= 12`, this is still manageable:

```text
O(4^n)
```

Since `4^12 = 16,777,216`, this is perfectly fine in Java.

### Total

```text
O(n * 2^n + 4^n)
```

Space:

```text
O(2^n)
```

---

## Pros

- Very straightforward
- Easy to implement
- Easy to reason about

## Cons

- Pair enumeration is somewhat brute-force
- Not the cleanest optimized form

---

# Approach 2: Enumerate One Palindromic Mask, Search the Best Disjoint Partner from Remaining Bits

## Idea

This is the more standard and clever bitmask solution.

We still precompute which subsets are palindromic and their lengths.

But instead of checking all pairs blindly, for each palindromic subset `mask`, we consider:

```text
remaining = allBits ^ mask
```

Now the second subsequence must be a palindromic subset of `remaining`.

So instead of iterating over all masks again, we iterate only over submasks of `remaining`.

This improves the pairing logic and is often presented as the main accepted solution.

---

## Steps

1. Precompute `palLen[mask]`:
   - `0` if not palindromic
   - otherwise, its bit count

2. For each palindromic `mask1`:
   - let `rest = fullMask ^ mask1`
   - iterate over every submask `sub` of `rest`
   - if `sub` is palindromic, update answer with:
     ```text
     palLen[mask1] * palLen[sub]
     ```

Because `sub` is always chosen from `rest`, disjointness is guaranteed automatically.

---

## Submask Iteration Trick

To iterate over all submasks of `rest`:

```java
for (int sub = rest; sub > 0; sub = (sub - 1) & rest)
```

This is a standard bitmask pattern.

---

## Java Code

```java
class Solution {
    public int maxProduct(String s) {
        int n = s.length();
        int total = 1 << n;
        int[] palLen = new int[total];

        for (int mask = 1; mask < total; mask++) {
            if (isPalindrome(s, mask)) {
                palLen[mask] = Integer.bitCount(mask);
            }
        }

        int ans = 0;
        int fullMask = total - 1;

        for (int mask1 = 1; mask1 < total; mask1++) {
            if (palLen[mask1] == 0) continue;

            int rest = fullMask ^ mask1;

            for (int sub = rest; sub > 0; sub = (sub - 1) & rest) {
                if (palLen[sub] > 0) {
                    ans = Math.max(ans, palLen[mask1] * palLen[sub]);
                }
            }
        }

        return ans;
    }

    private boolean isPalindrome(String s, int mask) {
        char[] arr = new char[Integer.bitCount(mask)];
        int idx = 0;

        for (int i = 0; i < s.length(); i++) {
            if ((mask & (1 << i)) != 0) {
                arr[idx++] = s.charAt(i);
            }
        }

        int left = 0, right = arr.length - 1;
        while (left < right) {
            if (arr[left] != arr[right]) return false;
            left++;
            right--;
        }
        return true;
    }
}
```

---

## Complexity

### Precomputation

```text
O(n * 2^n)
```

### For each mask, iterate over submasks of complement

Across all masks, this pattern is bounded by:

```text
O(3^n)
```

This is a standard result for “for each mask, iterate over submasks of its complement”.

So total:

```text
O(n * 2^n + 3^n)
```

Space:

```text
O(2^n)
```

---

## Why `O(3^n)`?

For each position, there are 3 possibilities in the pair `(mask1, sub)`:

1. bit belongs to `mask1`
2. bit belongs to `sub`
3. bit belongs to neither

It cannot belong to both because they are disjoint.

That gives `3^n` combined states.

---

## Pros

- More elegant than pairwise brute force
- Stronger theoretical complexity
- Very standard for this problem

## Cons

- Slightly less intuitive than Approach 1 if you are new to submask iteration

---

# Approach 3: DFS / Backtracking with 3 Choices Per Character

## Idea

At each index, every character has 3 possibilities:

1. put it into subsequence `A`
2. put it into subsequence `B`
3. skip it

At the end, check whether both built strings are palindromes.

If yes, update answer with:

```text
len(A) * len(B)
```

Because each index is assigned to at most one subsequence, disjointness is automatic.

This gives a DFS tree of branching factor 3 and depth `n`.

---

## Why this is natural

The problem literally asks us to split indices into:

- first palindrome
- second palindrome
- unused

That is exactly a 3-way choice per character.

Since `n <= 12`, `3^12 = 531,441`, which is quite manageable.

---

## Basic DFS Structure

For each `index`:

- append `s[index]` to first builder
- append `s[index]` to second builder
- or skip it

At leaf:

- check if both built strings are palindromes
- update max product

---

## Java Code

```java
class Solution {
    private int ans = 0;

    public int maxProduct(String s) {
        dfs(s, 0, new StringBuilder(), new StringBuilder());
        return ans;
    }

    private void dfs(String s, int index, StringBuilder a, StringBuilder b) {
        if (index == s.length()) {
            if (isPalindrome(a) && isPalindrome(b)) {
                ans = Math.max(ans, a.length() * b.length());
            }
            return;
        }

        char ch = s.charAt(index);

        // Put in first subsequence
        a.append(ch);
        dfs(s, index + 1, a, b);
        a.deleteCharAt(a.length() - 1);

        // Put in second subsequence
        b.append(ch);
        dfs(s, index + 1, a, b);
        b.deleteCharAt(b.length() - 1);

        // Skip
        dfs(s, index + 1, a, b);
    }

    private boolean isPalindrome(StringBuilder sb) {
        int left = 0, right = sb.length() - 1;
        while (left < right) {
            if (sb.charAt(left) != sb.charAt(right)) return false;
            left++;
            right--;
        }
        return true;
    }
}
```

---

## Complexity

There are `3^n` DFS states.

At each leaf, palindrome checking costs `O(n)`.

So worst-case:

```text
O(n * 3^n)
```

Space:

```text
O(n)
```

for recursion depth and builders.

---

## Pros

- Very intuitive
- Direct modeling of the problem
- Good for understanding the disjointness constraint

## Cons

- Less optimized than bitmask solutions
- Repeated palindrome checks at leaves
- Harder to prune effectively compared to mask DP

---

# Approach 4: DFS with Bitmask Construction Instead of Strings

## Idea

This is a cleaner version of the 3-choice DFS.

Instead of building actual strings during recursion, build two bitmasks:

- `mask1` for subsequence A
- `mask2` for subsequence B

At the end:

- check whether `mask1` forms a palindrome
- check whether `mask2` forms a palindrome
- compute product

This keeps the recursion cleaner and makes it easier to combine with memoization or precomputation later.

---

## Java Code

```java
class Solution {
    private int ans = 0;
    private String s;

    public int maxProduct(String s) {
        this.s = s;
        dfs(0, 0, 0);
        return ans;
    }

    private void dfs(int index, int mask1, int mask2) {
        if (index == s.length()) {
            if (isPalindrome(mask1) && isPalindrome(mask2)) {
                ans = Math.max(ans,
                        Integer.bitCount(mask1) * Integer.bitCount(mask2));
            }
            return;
        }

        // Put current char in first subsequence
        dfs(index + 1, mask1 | (1 << index), mask2);

        // Put current char in second subsequence
        dfs(index + 1, mask1, mask2 | (1 << index));

        // Skip current char
        dfs(index + 1, mask1, mask2);
    }

    private boolean isPalindrome(int mask) {
        char[] arr = new char[Integer.bitCount(mask)];
        int idx = 0;

        for (int i = 0; i < s.length(); i++) {
            if ((mask & (1 << i)) != 0) {
                arr[idx++] = s.charAt(i);
            }
        }

        int left = 0, right = arr.length - 1;
        while (left < right) {
            if (arr[left] != arr[right]) return false;
            left++;
            right--;
        }
        return true;
    }
}
```

---

## Complexity

Same structural count as Approach 3:

```text
O(n * 3^n)
```

Space:

```text
O(n)
```

---

## Pros

- Cleaner than maintaining two growing strings
- Uses masks directly
- Bridges naturally to the optimized bitmask approaches

## Cons

- Still repeats palindrome checks at leaves
- Not as efficient as full precompute-based solutions

---

# Best Approach: Precompute Palindromic Masks + Enumerate Disjoint Submasks

For this problem, the strongest practical answer is **Approach 2**.

Why?

- `n <= 12`, so subset-based processing is cheap
- bitmask representation is natural
- disjointness is handled elegantly using complement and submasks
- excellent balance of clarity and performance

---

# Deep Intuition

A common mistake is to think:

> “This feels like longest palindromic subsequence, maybe I need DP on substrings.”

That is not the right direction.

Why?

Because the two subsequences are:

- not required to be contiguous
- selected from arbitrary positions
- required to be disjoint by index, not by substring interval

That makes **index subset selection** the more natural model.

So the correct mental shift is:

> This is not primarily a string-DP-on-intervals problem.
> It is a small-state combinatorial search problem over subsets of indices.

That is why bitmask methods dominate.

---

# Correctness Sketch for Approach 2

We want to show that Approach 2 examines every valid answer.

Suppose the optimal answer uses two disjoint palindromic subsequences corresponding to masks:

```text
A and B
```

with:

```text
A & B == 0
```

During the iteration:

- we will eventually consider `mask1 = A`
- then `rest = fullMask ^ A`
- since `B` is disjoint from `A`, `B` is a submask of `rest`

When we iterate over all submasks of `rest`, we will consider `sub = B`.

If both are palindromic, the product:

```text
palLen[A] * palLen[B]
```

is evaluated.

Therefore every valid pair is considered, including the optimal one.

So the maximum computed answer is correct.

---

# Example Walkthrough

## Example 1

```text
s = "leetcodecom"
```

Suppose one palindromic subset gives:

```text
"ete"
```

and another gives:

```text
"cdc"
```

Their masks are disjoint.

Approach 2 will:

1. record both masks as palindromic during precomputation
2. when iterating with first mask = `"ete"`, the complement still contains the positions for `"cdc"`
3. while scanning submasks of the complement, it will encounter `"cdc"`
4. product becomes:

```text
3 * 3 = 9
```

No better answer exists, so result is `9`.

---

# Edge Cases

## 1. Smallest input

```text
s = "bb"
```

Possible disjoint palindromic subsequences:

- first `b`
- second `b`

Answer:

```text
1
```

---

## 2. Entire string is a palindrome

That does **not** mean the answer uses the entire string.

The subsequences must be **two disjoint subsequences**, so sometimes splitting into two smaller palindromes gives a better product.

Example idea:

```text
s = "aaaa"
```

You can take:

- `"aa"`
- `"aa"`

Product = `4`

---

## 3. Uneven split may still be optimal

Maximum product does not always come from equal lengths.
It comes from the best pair of disjoint palindromic subsequences allowed by the character positions.

---

# Final Recommended Java Solution

This is the version I would submit.

```java
class Solution {
    public int maxProduct(String s) {
        int n = s.length();
        int total = 1 << n;
        int[] palLen = new int[total];

        // Precompute which masks form palindromic subsequences
        for (int mask = 1; mask < total; mask++) {
            if (isPalindrome(s, mask)) {
                palLen[mask] = Integer.bitCount(mask);
            }
        }

        int ans = 0;
        int fullMask = total - 1;

        // Pick first palindromic subsequence
        for (int mask1 = 1; mask1 < total; mask1++) {
            if (palLen[mask1] == 0) continue;

            // Second subsequence must come from remaining unused bits
            int rest = fullMask ^ mask1;

            // Enumerate all submasks of rest
            for (int sub = rest; sub > 0; sub = (sub - 1) & rest) {
                if (palLen[sub] > 0) {
                    ans = Math.max(ans, palLen[mask1] * palLen[sub]);
                }
            }
        }

        return ans;
    }

    private boolean isPalindrome(String s, int mask) {
        char[] arr = new char[Integer.bitCount(mask)];
        int idx = 0;

        for (int i = 0; i < s.length(); i++) {
            if ((mask & (1 << i)) != 0) {
                arr[idx++] = s.charAt(i);
            }
        }

        int left = 0, right = arr.length - 1;
        while (left < right) {
            if (arr[left] != arr[right]) {
                return false;
            }
            left++;
            right--;
        }

        return true;
    }
}
```

---

# Comparison Table

| Approach   | Main Idea                                                 |    Time Complexity | Space Complexity | Verdict     |
| ---------- | --------------------------------------------------------- | -----------------: | ---------------: | ----------- |
| Approach 1 | Enumerate all palindromic masks, then all pairs           | `O(n * 2^n + 4^n)` |         `O(2^n)` | Simple      |
| Approach 2 | Enumerate one palindromic mask and submasks of complement | `O(n * 2^n + 3^n)` |         `O(2^n)` | Best        |
| Approach 3 | DFS with 3 choices per character using strings            |       `O(n * 3^n)` |           `O(n)` | Intuitive   |
| Approach 4 | DFS with masks instead of strings                         |       `O(n * 3^n)` |           `O(n)` | Cleaner DFS |

---

# Pattern Recognition Takeaway

When you see:

- subsequences
- disjoint index choices
- `n <= 12`

you should strongly suspect:

- bitmask enumeration
- subset precomputation
- disjoint-mask pairing

That is the real pattern behind this problem.

It is much closer to **subset search over indices** than to classic palindrome DP on substrings.

---

# Final Takeaway

The cleanest solution is:

1. represent every subsequence by a bitmask
2. precompute which masks are palindromes
3. for each palindromic mask, search for the best palindromic disjoint submask from the remaining bits

That gives a compact and efficient solution that fits the constraint perfectly.
