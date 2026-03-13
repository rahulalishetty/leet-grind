# Square-Free Subsets — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int squareFreeSubsets(int[] nums) {

    }
}
```

---

# Problem Restatement

We are given an array `nums`, where:

- `1 <= nums[i] <= 30`
- `nums.length <= 1000`

We must count the number of **non-empty subsets** whose product is **square-free**.

A product is square-free if it is not divisible by any square number greater than `1`.

Return the answer modulo:

```text
10^9 + 7
```

---

# Core Insight

Since every number is at most `30`, the only prime numbers we care about are:

```text
2, 3, 5, 7, 11, 13, 17, 19, 23, 29
```

There are only **10 primes** up to `30`.

For a subset product to be square-free:

- no prime can appear more than once in the total prime factorization

That means:

- if a number itself already contains a squared prime factor, it can never be used
- among the remaining usable numbers, the prime-factor sets of chosen elements must be disjoint

This immediately suggests a **bitmask DP** over the 10 primes.

---

# Key Preprocessing

For each value `x` from `1` to `30`, determine:

1. whether `x` is square-free itself
2. if yes, which primes divide it

Represent the prime set as a bitmask of length 10.

Examples:

- `2` -> mask for `{2}`
- `3` -> mask for `{3}`
- `6` -> mask for `{2,3}`
- `10` -> mask for `{2,5}`
- `1` -> empty mask
- `4` -> invalid, because `4 = 2^2`
- `12` -> invalid, because divisible by `2^2`

Then the problem becomes:

> Count subsets whose selected masks do not overlap.

---

# Special Role of 1

The number `1` has no prime factors, so it is always square-free.

Also, including `1` never changes the prime mask.

So if there are `cnt1` copies of `1`, then every valid subset formed from other numbers can be combined with:

```text
2^cnt1
```

choices of including/excluding the ones.

At the end, remember to subtract the empty subset.

This is a very important simplification.

---

# Approach 1 — Frequency Compression + Bitmask DP (Recommended)

## Idea

Since values are only from `1` to `30`, first count frequencies:

```text
freq[v] = how many times value v appears
```

Then process each value `v` from `2` to `30`:

- skip it if it is not square-free
- let `mask[v]` be its prime-factor bitmask
- if `freq[v] > 0`, update the DP:
  - choose zero copies of `v`, or
  - choose exactly one copy of `v`

Why at most one copy?

Because if we pick the same square-free number twice, then every prime factor in it appears twice in the product, which would create a square factor.

So for every `v > 1`, a valid subset can contain **at most one copy** of that value.

If `freq[v] = c`, then there are exactly `c` ways to choose one occurrence of value `v`.

That is why the transition multiplies by `freq[v]`.

---

## DP State

Let:

```text
dp[mask] = number of ways to build a subset whose used prime set is exactly mask
```

Initially:

```text
dp[0] = 1
```

Then for each square-free value `v > 1` with bitmask `m`:

- from every existing state `mask`
- if `(mask & m) == 0`, then we can add `v`
- new state becomes `mask | m`

Transition:

```text
newDp[mask | m] += dp[mask] * freq[v]
```

After processing all values > 1:

- total valid subsets from values > 1 is `sum(dp[mask])`
- multiply by `2^freq[1]`
- subtract 1 for the empty subset

---

## Java Code

```java
class Solution {
    private static final int MOD = 1_000_000_007;
    private static final int[] PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};

    public int squareFreeSubsets(int[] nums) {
        int[] freq = new int[31];
        for (int x : nums) {
            freq[x]++;
        }

        int[] masks = new int[31];
        boolean[] valid = new boolean[31];

        for (int x = 1; x <= 30; x++) {
            int mask = 0;
            int value = x;
            boolean ok = true;

            for (int i = 0; i < PRIMES.length; i++) {
                int p = PRIMES[i];
                int count = 0;
                while (value % p == 0) {
                    value /= p;
                    count++;
                }
                if (count >= 2) {
                    ok = false;
                    break;
                }
                if (count == 1) {
                    mask |= (1 << i);
                }
            }

            valid[x] = ok;
            masks[x] = mask;
        }

        long[] dp = new long[1 << PRIMES.length];
        dp[0] = 1;

        for (int x = 2; x <= 30; x++) {
            if (freq[x] == 0 || !valid[x]) continue;

            long[] next = dp.clone();
            int m = masks[x];

            for (int mask = 0; mask < dp.length; mask++) {
                if ((mask & m) == 0) {
                    int newMask = mask | m;
                    next[newMask] = (next[newMask] + dp[mask] * freq[x]) % MOD;
                }
            }

            dp = next;
        }

        long total = 0;
        for (long ways : dp) {
            total = (total + ways) % MOD;
        }

        long pow2 = modPow(2, freq[1], MOD);
        total = (total * pow2) % MOD;

        total = (total - 1 + MOD) % MOD; // remove empty subset
        return (int) total;
    }

    private long modPow(long base, int exp, int mod) {
        long ans = 1;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                ans = (ans * base) % mod;
            }
            base = (base * base) % mod;
            exp >>= 1;
        }
        return ans;
    }
}
```

---

## Complexity

There are only `30` possible values and `2^10 = 1024` masks.

So:

```text
Time:  O(30 * 1024)
Space: O(1024)
```

This is extremely efficient.

---

# Approach 2 — Process Elements One by One with Bitmask DP

## Idea

Instead of compressing frequencies first, process each array element directly.

For each number:

- if it is invalid (not square-free), skip it
- if it is `1`, it doubles every existing DP count
- otherwise, treat it like a mask item and update DP if no overlap occurs

This is simpler to understand operationally, though slightly less elegant than the frequency-compressed approach.

---

## Java Code

```java
class Solution {
    private static final int MOD = 1_000_000_007;
    private static final int[] PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};

    public int squareFreeSubsets(int[] nums) {
        long[] dp = new long[1 << 10];
        dp[0] = 1;

        for (int x : nums) {
            int mask = getMask(x);
            if (mask == -1) continue;

            long[] next = dp.clone();

            if (x == 1) {
                for (int m = 0; m < dp.length; m++) {
                    next[m] = (next[m] + dp[m]) % MOD;
                }
            } else {
                for (int m = 0; m < dp.length; m++) {
                    if ((m & mask) == 0) {
                        next[m | mask] = (next[m | mask] + dp[m]) % MOD;
                    }
                }
            }

            dp = next;
        }

        long ans = 0;
        for (long ways : dp) {
            ans = (ans + ways) % MOD;
        }

        ans = (ans - 1 + MOD) % MOD;
        return (int) ans;
    }

    private int getMask(int x) {
        if (x == 1) return 0;

        int mask = 0;
        int value = x;

        for (int i = 0; i < PRIMES.length; i++) {
            int p = PRIMES[i];
            int cnt = 0;
            while (value % p == 0) {
                value /= p;
                cnt++;
            }
            if (cnt >= 2) return -1;
            if (cnt == 1) mask |= (1 << i);
        }

        return mask;
    }
}
```

---

## Complexity

For each of `n` elements, we scan `1024` masks.

So:

```text
Time:  O(n * 1024)
Space: O(1024)
```

Since `n <= 1000`, this is still excellent.

---

# Approach 3 — Backtracking Over Subsets (Too Slow)

## Idea

Try all subsets and check whether each product is square-free.

This is conceptually simple, but completely infeasible because the number of subsets is:

```text
2^n
```

With `n = 1000`, this is impossible.

---

# Why duplicates matter differently for 1 and non-1

This is one of the easiest places to make a mistake.

## For `1`

You may include any number of copies of `1`, because multiplying by `1` never introduces any square factor.

If there are `c` ones, they contribute a multiplicative factor:

```text
2^c
```

to every valid non-1 subset pattern.

## For any square-free value `v > 1`

You may include at most **one copy** of that value.

Reason:

If `v` has prime factor `p`, then including `v` twice makes `p` appear twice, so the product becomes divisible by `p^2`.

Example:

```text
v = 6 = 2 * 3
6 * 6 = 2^2 * 3^2
```

not square-free.

So for `v > 1`, frequency `freq[v]` means:

- either choose none
- or choose exactly one of its `freq[v]` copies

That is why the DP transition multiplies by `freq[v]`, not by `2^freq[v]`.

---

# Example Walkthrough

## Example 1

```text
nums = [3,4,4,5]
```

- `3` is valid, mask = `{3}`
- `4` is invalid because `4 = 2^2`
- `5` is valid, mask = `{5}`

Valid subsets:

- `[3]`
- `[5]`
- `[3,5]`

So answer is:

```text
3
```

---

## Example 2

```text
nums = [1]
```

There is exactly one non-empty subset:

```text
[1]
```

Its product is `1`, which is square-free.

So answer is:

```text
1
```

---

# Important Correctness Argument

A subset product is square-free iff no prime appears with exponent 2 or more in the product.

For values up to 30, every valid element can be represented by the set of primes appearing once in its factorization.

Then a subset is valid iff:

- every chosen number is individually square-free
- no two chosen numbers share any prime factor

That is exactly what the bitmask DP enforces with:

```text
(mask & valueMask) == 0
```

So every valid square-free subset is counted once, and every counted subset is valid.

---

# Common Pitfalls

## 1. Forgetting that numbers like 4, 8, 9, 12, 18, 20, 27, 28 are invalid

If a number itself contains a squared prime factor, it can never appear in any valid subset.

---

## 2. Treating duplicates of non-1 values like 1s

For values greater than 1, you cannot take two equal copies.

---

## 3. Forgetting to subtract the empty subset

The DP naturally counts the empty subset via `dp[0] = 1`.

The problem asks for non-empty subsets only.

---

## 4. Using the product directly

Never multiply subset products explicitly. The mask representation is the right abstraction.

---

# Best Approach

## Recommended: Frequency compression + 10-bit mask DP

This is the best solution because:

- values are tiny (`<= 30`)
- there are only 10 relevant primes
- the DP state is only `1024`
- it handles duplicates and 1s cleanly

---

# Final Recommended Java Solution

```java
class Solution {
    private static final int MOD = 1_000_000_007;
    private static final int[] PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};

    public int squareFreeSubsets(int[] nums) {
        int[] freq = new int[31];
        for (int x : nums) {
            freq[x]++;
        }

        int[] masks = new int[31];
        boolean[] valid = new boolean[31];

        for (int x = 1; x <= 30; x++) {
            int mask = 0;
            int value = x;
            boolean ok = true;

            for (int i = 0; i < PRIMES.length; i++) {
                int p = PRIMES[i];
                int count = 0;
                while (value % p == 0) {
                    value /= p;
                    count++;
                }
                if (count >= 2) {
                    ok = false;
                    break;
                }
                if (count == 1) {
                    mask |= (1 << i);
                }
            }

            valid[x] = ok;
            masks[x] = mask;
        }

        long[] dp = new long[1 << PRIMES.length];
        dp[0] = 1;

        for (int x = 2; x <= 30; x++) {
            if (freq[x] == 0 || !valid[x]) continue;

            long[] next = dp.clone();
            int m = masks[x];

            for (int mask = 0; mask < dp.length; mask++) {
                if ((mask & m) == 0) {
                    next[mask | m] = (next[mask | m] + dp[mask] * freq[x]) % MOD;
                }
            }

            dp = next;
        }

        long total = 0;
        for (long ways : dp) {
            total = (total + ways) % MOD;
        }

        total = (total * modPow(2, freq[1])) % MOD;
        total = (total - 1 + MOD) % MOD;

        return (int) total;
    }

    private long modPow(long base, int exp) {
        long ans = 1;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                ans = (ans * base) % MOD;
            }
            base = (base * base) % MOD;
            exp >>= 1;
        }
        return ans;
    }
}
```

---

# Complexity Summary

```text
Time:  O(30 * 1024)
Space: O(1024)
```

---
