# 3109. Find the Index of Permutation — Exhaustive Java Notes

## Problem Statement

You are given an array `perm` of length `n`, where `perm` is a permutation of:

```text
[1, 2, ..., n]
```

We must return the **0-based lexicographic index** of `perm` among all permutations of `[1,2,...,n]`, sorted in lexicographic order.

Since the answer may be very large, return it modulo:

```text
10^9 + 7
```

---

## Example 1

```text
Input: perm = [1,2]
Output: 0
```

All permutations in lexicographic order:

```text
[1,2]
[2,1]
```

So `[1,2]` has index `0`.

---

## Example 2

```text
Input: perm = [3,1,2]
Output: 4
```

All permutations in lexicographic order:

```text
[1,2,3]   // index 0
[1,3,2]   // index 1
[2,1,3]   // index 2
[2,3,1]   // index 3
[3,1,2]   // index 4
[3,2,1]   // index 5
```

So `[3,1,2]` has index `4`.

---

## Constraints

- `1 <= n == perm.length <= 10^5`
- `perm` is a permutation of `[1, 2, ..., n]`

---

# 1. Core Idea — Lexicographic Rank via Factorial Blocks

Suppose we are building a permutation from left to right.

At position `i`, if there are `remaining = n - i - 1` positions after it, then fixing a particular value at position `i` leaves:

```text
remaining!
```

permutations of the suffix.

So if, at position `i`, there are `k` smaller unused numbers that could have been placed before `perm[i]`, then all permutations starting with those choices come before the current permutation, contributing:

```text
k * remaining!
```

Thus the lexicographic rank is:

```text
sum over i of
(count of smaller unused numbers before perm[i]) * (n - i - 1)!
```

This is the classical factorial number system / Lehmer code idea.

---

# 2. Why We Need a Data Structure

For each position `i`, we need:

> How many unused values are smaller than `perm[i]`?

Initially all numbers `1..n` are unused.
After processing `perm[i]`, that value becomes used.

So we need a dynamic structure supporting:

- count how many unused values are in `[1 .. perm[i]-1]`
- remove `perm[i]`

That is exactly what a Fenwick Tree or Segment Tree can do.

Because the values are already `1..n`, we do **not** even need coordinate compression.

---

# 3. Approach 1 — Brute Force Enumeration

## Idea

Generate all permutations, sort them lexicographically, and find the index of `perm`.

## Why it works

It literally follows the problem statement.

## Why it is useless

There are `n!` permutations, which becomes impossible almost immediately.

Even for `n = 10`, this is already `3,628,800`.

## Java Sketch

```java
import java.util.*;

class SolutionBruteForce {
    public int getPermutationIndex(int[] perm) {
        List<int[]> all = new ArrayList<>();
        int[] nums = perm.clone();
        Arrays.sort(nums);
        generate(nums, 0, all);

        for (int i = 0; i < all.size(); i++) {
            if (Arrays.equals(all.get(i), perm)) {
                return i;
            }
        }
        return -1;
    }

    private void generate(int[] nums, int start, List<int[]> all) {
        if (start == nums.length) {
            all.add(nums.clone());
            return;
        }

        for (int i = start; i < nums.length; i++) {
            swap(nums, start, i);
            generate(nums, start + 1, all);
            swap(nums, start, i);
        }
    }

    private void swap(int[] nums, int i, int j) {
        int t = nums[i];
        nums[i] = nums[j];
        nums[j] = t;
    }
}
```

## Complexity

- Time: `O(n! * n)`
- Space: `O(n! * n)`

## Verdict

Only conceptual.

---

# 4. Approach 2 — Quadratic Simulation of Unused Numbers

## Idea

Maintain a sorted list of unused numbers.

For each `perm[i]`:

1. count how many unused numbers are smaller than it
2. add:
   ```text
   smallerCount * (n-i-1)!
   ```
3. remove `perm[i]` from unused set

If the unused numbers are kept in a list, counting/removal can be `O(n)`.

## Java Code

```java
import java.util.*;

class SolutionQuadratic {
    static final int MOD = 1_000_000_007;

    public int getPermutationIndex(int[] perm) {
        int n = perm.length;
        long[] fact = new long[n + 1];
        fact[0] = 1;

        for (int i = 1; i <= n; i++) {
            fact[i] = fact[i - 1] * i % MOD;
        }

        List<Integer> unused = new ArrayList<>();
        for (int x = 1; x <= n; x++) unused.add(x);

        long ans = 0;

        for (int i = 0; i < n; i++) {
            int x = perm[i];
            int pos = 0;

            while (unused.get(pos) != x) pos++;

            ans = (ans + pos * fact[n - i - 1]) % MOD;
            unused.remove(pos);
        }

        return (int) ans;
    }
}
```

## Complexity

- Counting smaller by scanning list: `O(n)`
- Removing from list middle: `O(n)`

Total:

- Time: `O(n^2)`
- Space: `O(n)`

## Verdict

Correct, but too slow for `n = 10^5`.

---

# 5. Approach 3 — Fenwick Tree / BIT (Optimal)

This is the standard best approach.

## 5.1 Fenwick meaning

Let the Fenwick Tree store whether a number is still unused:

- `1` means unused
- `0` means already used

Initially:

```text
all numbers 1..n are unused
```

So the BIT starts with all ones.

For each value `perm[i] = x`:

- `bit.sum(x - 1)` gives how many unused values are smaller than `x`
- add:
  ```text
  bit.sum(x - 1) * (n-i-1)!
  ```
- then remove `x`:
  ```text
  bit.add(x, -1)
  ```

That directly implements the factorial-block logic.

---

## 5.2 Why modulo arithmetic is fine

The rank can be huge, but the problem only asks for modulo `10^9+7`.

So we precompute factorials modulo `MOD`, and accumulate contributions modulo `MOD`.

Since the formula is purely additive and multiplicative, modular arithmetic works perfectly.

---

## 5.3 Java Code — Fenwick Tree Solution

```java
import java.util.*;

class Solution {
    static final int MOD = 1_000_000_007;

    static class Fenwick {
        int n;
        int[] bit;

        Fenwick(int n) {
            this.n = n;
            this.bit = new int[n + 1];
        }

        void add(int index, int delta) {
            while (index <= n) {
                bit[index] += delta;
                index += index & -index;
            }
        }

        int sum(int index) {
            int res = 0;
            while (index > 0) {
                res += bit[index];
                index -= index & -index;
            }
            return res;
        }
    }

    public int getPermutationIndex(int[] perm) {
        int n = perm.length;

        long[] fact = new long[n + 1];
        fact[0] = 1;
        for (int i = 1; i <= n; i++) {
            fact[i] = fact[i - 1] * i % MOD;
        }

        Fenwick bit = new Fenwick(n);
        for (int x = 1; x <= n; x++) {
            bit.add(x, 1); // all values initially unused
        }

        long ans = 0;

        for (int i = 0; i < n; i++) {
            int x = perm[i];
            int smallerUnused = bit.sum(x - 1);

            ans = (ans + smallerUnused * fact[n - i - 1]) % MOD;

            bit.add(x, -1); // mark x as used
        }

        return (int) ans;
    }
}
```

---

## 5.4 Complexity

- Build factorials: `O(n)`
- Initialize BIT: `O(n log n)` with straightforward adds, or conceptually `O(n)`
- For each position:
  - one prefix sum
  - one update

Total:

- Time: `O(n log n)`
- Space: `O(n)`

This fits easily for `n = 10^5`.

---

# 6. Approach 4 — Segment Tree

A segment tree can do the same job.

## Idea

Store unused counts in a segment tree over `[1..n]`.

At each step:

- query sum over `[1..x-1]`
- add contribution
- point-update `x` to remove it

This is slightly more verbose than Fenwick but equally valid.

## Java Code

```java
import java.util.*;

class SolutionSegmentTree {
    static final int MOD = 1_000_000_007;

    static class SegmentTree {
        int n;
        int[] tree;

        SegmentTree(int n) {
            this.n = n;
            this.tree = new int[4 * n];
        }

        void build(int node, int left, int right) {
            if (left == right) {
                tree[node] = 1;
                return;
            }

            int mid = left + (right - left) / 2;
            build(node * 2, left, mid);
            build(node * 2 + 1, mid + 1, right);
            tree[node] = tree[node * 2] + tree[node * 2 + 1];
        }

        int query(int node, int left, int right, int ql, int qr) {
            if (ql > right || qr < left) return 0;
            if (ql <= left && right <= qr) return tree[node];

            int mid = left + (right - left) / 2;
            return query(node * 2, left, mid, ql, qr)
                 + query(node * 2 + 1, mid + 1, right, ql, qr);
        }

        void update(int node, int left, int right, int index) {
            if (left == right) {
                tree[node] = 0;
                return;
            }

            int mid = left + (right - left) / 2;
            if (index <= mid) {
                update(node * 2, left, mid, index);
            } else {
                update(node * 2 + 1, mid + 1, right, index);
            }

            tree[node] = tree[node * 2] + tree[node * 2 + 1];
        }

        int query(int l, int r) {
            if (l > r) return 0;
            return query(1, 1, n, l, r);
        }

        void remove(int index) {
            update(1, 1, n, index);
        }
    }

    public int getPermutationIndex(int[] perm) {
        int n = perm.length;

        long[] fact = new long[n + 1];
        fact[0] = 1;
        for (int i = 1; i <= n; i++) {
            fact[i] = fact[i - 1] * i % MOD;
        }

        SegmentTree st = new SegmentTree(n);
        st.build(1, 1, n);

        long ans = 0;

        for (int i = 0; i < n; i++) {
            int x = perm[i];
            int smallerUnused = st.query(1, x - 1);

            ans = (ans + smallerUnused * fact[n - i - 1]) % MOD;

            st.remove(x);
        }

        return (int) ans;
    }
}
```

---

## Complexity

- Time: `O(n log n)`
- Space: `O(n)`

---

# 7. Dry Run on Example 2

```text
perm = [3,1,2]
n = 3
```

Factorials:

```text
0! = 1
1! = 1
2! = 2
```

Initially unused numbers:

```text
{1,2,3}
```

---

## Position 0, value = 3

Unused smaller numbers than `3` are:

```text
{1,2}
```

So `smallerUnused = 2`.

Contribution:

```text
2 * 2! = 2 * 2 = 4
```

Rank so far = `4`.

Remove `3`.

Unused now:

```text
{1,2}
```

---

## Position 1, value = 1

Unused smaller numbers than `1`:

```text
none
```

Contribution:

```text
0 * 1! = 0
```

Rank still `4`.

Remove `1`.

Unused now:

```text
{2}
```

---

## Position 2, value = 2

Unused smaller numbers than `2`:

```text
none
```

Contribution:

```text
0 * 0! = 0
```

Final rank:

```text
4
```

Correct.

---

# 8. Why the Formula Works

At position `i`, suppose there are `k` smaller unused values than `perm[i]`.

If any one of those smaller values were chosen at position `i`, then the suffix can be arranged arbitrarily in:

```text
(n - i - 1)!
```

ways.

Since there are `k` such smaller choices, the number of permutations that come before the current one because of position `i` is:

```text
k * (n - i - 1)!
```

Summing this over all positions gives the total lexicographic rank.

This is exactly the factorial number system representation of a permutation.

---

# 9. Correctness Proof

## Lemma 1

At step `i`, `bit.sum(perm[i] - 1)` equals the number of unused values smaller than `perm[i]`.

### Proof

The BIT stores `1` for each unused value and `0` for each used value. A prefix sum up to `perm[i] - 1` therefore counts exactly the unused values in `[1 .. perm[i]-1]`, i.e. the unused smaller values. ∎

---

## Lemma 2

For position `i`, the number of permutations lexicographically smaller than `perm` that first differ at position `i` is:

```text
smallerUnused * (n - i - 1)!
```

### Proof

Each unused smaller value at position `i` gives one lexicographically smaller choice than `perm[i]`. Once that position is fixed, the remaining `n - i - 1` positions can be filled in arbitrary order, giving exactly `(n - i - 1)!` permutations for each such choice. Multiplying yields the count. ∎

---

## Lemma 3

Summing these contributions over all positions gives the lexicographic index of `perm`.

### Proof

Every permutation smaller than `perm` has a unique first position where it differs from `perm`. By Lemma 2, the permutations differing first at position `i` are counted exactly by the contribution for `i`. These sets are disjoint and cover all lexicographically smaller permutations, so summing gives the exact index. ∎

---

## Theorem

The Fenwick-tree algorithm returns the correct lexicographic index of `perm` modulo `10^9+7`.

### Proof

By Lemma 1 it computes the correct `smallerUnused` at each step. By Lemma 3 the summed contributions equal the lexicographic index. All arithmetic is done modulo `10^9+7`, so the returned value is the required answer modulo `10^9+7`. ∎

---

# 10. Common Mistakes

## Mistake 1: Confusing 0-based and 1-based rank

The problem asks for the **0-based** index.

So the smallest permutation has rank `0`, not `1`.

## Mistake 2: Forgetting to remove used values

If `perm[i]` is not marked as used after processing, later counts of unused smaller values become wrong.

## Mistake 3: Enumerating permutations

Impossible for even modest `n`.

## Mistake 4: Forgetting modulo on factorials

`n!` becomes huge, so precompute factorials modulo `MOD`.

## Mistake 5: Using `int` for intermediate multiplication

Use `long` for:

```text
smallerUnused * factorial
```

before taking modulo.

---

# 11. Comparison of Approaches

| Approach                         |         Time |    Space | Notes                   |
| -------------------------------- | -----------: | -------: | ----------------------- |
| Brute force enumeration          |  `O(n! * n)` | enormous | Only conceptual         |
| Quadratic unused-list simulation |     `O(n^2)` |   `O(n)` | Too slow                |
| Fenwick tree                     | `O(n log n)` |   `O(n)` | Best practical solution |
| Segment tree                     | `O(n log n)` |   `O(n)` | Also valid              |

---

# 12. Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    static final int MOD = 1_000_000_007;

    static class Fenwick {
        int n;
        int[] bit;

        Fenwick(int n) {
            this.n = n;
            this.bit = new int[n + 1];
        }

        void add(int index, int delta) {
            while (index <= n) {
                bit[index] += delta;
                index += index & -index;
            }
        }

        int sum(int index) {
            int res = 0;
            while (index > 0) {
                res += bit[index];
                index -= index & -index;
            }
            return res;
        }
    }

    public int getPermutationIndex(int[] perm) {
        int n = perm.length;

        long[] fact = new long[n + 1];
        fact[0] = 1;
        for (int i = 1; i <= n; i++) {
            fact[i] = fact[i - 1] * i % MOD;
        }

        Fenwick bit = new Fenwick(n);
        for (int x = 1; x <= n; x++) {
            bit.add(x, 1);
        }

        long ans = 0;

        for (int i = 0; i < n; i++) {
            int x = perm[i];
            int smallerUnused = bit.sum(x - 1);

            ans = (ans + smallerUnused * fact[n - i - 1]) % MOD;

            bit.add(x, -1);
        }

        return (int) ans;
    }
}
```

---

# 13. Interview Summary

The lexicographic index of a permutation is obtained by counting, at each position, how many smaller unused values could have appeared there.

Each such smaller choice contributes a whole block of:

```text
(n - i - 1)!
```

permutations.

So:

```text
rank = Σ (smaller unused count at position i) * (n-i-1)!
```

Use a Fenwick Tree to maintain which values are still unused and query how many unused values are smaller than the current one in `O(log n)` time.

That gives an `O(n log n)` solution.
