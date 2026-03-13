# 1994. The Number of Good Subsets — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int numberOfGoodSubsets(int[] nums) {

    }
}
```

---

# Problem Restatement

We are given an array `nums`, where each value is between `1` and `30`.

A subset is called **good** if the product of its elements can be written as a product of **one or more distinct prime numbers**.

That means:

- every prime in the product can appear **at most once**
- the product must be **square-free**
- the subset cannot be empty
- `1` can be included any number of times, because multiplying by `1` does not change the prime factorization

We must return the total number of good subsets modulo:

```text
10^9 + 7
```

---

# Core Insight

A product is good if and only if:

- no prime factor appears more than once overall
- equivalently, the chosen numbers together must not create repeated prime factors

Since:

```text
1 <= nums[i] <= 30
```

there are only a few possible prime numbers involved:

```text
2, 3, 5, 7, 11, 13, 17, 19, 23, 29
```

There are exactly **10 primes <= 30**.

This suggests representing the prime factors of a number using a **10-bit mask**.

---

# Key Preprocessing Idea

For each value from `2` to `30`, determine whether it is usable:

- if the value contains a squared prime factor, like:
  - `4 = 2^2`
  - `8 = 2^3`
  - `12 = 2^2 * 3`
  - `18 = 2 * 3^2`

  then this value can **never** appear in a good subset

- otherwise, build a bitmask representing which distinct primes divide it

Examples:

- `2` -> mask of `{2}`
- `3` -> mask of `{3}`
- `6` -> mask of `{2,3}`
- `10` -> mask of `{2,5}`
- `15` -> mask of `{3,5}`

If two chosen numbers have overlapping masks, then together they repeat a prime, so that subset is invalid.

---

# Special Role of `1`

The number `1` has no prime factors.

So:

- it never breaks a good subset
- it can be attached to any good subset
- if there are `count1` ones, then every good subset can be extended in:

```text
2^count1
```

ways by choosing any subset of the ones

Important:
We apply this multiplier only to non-empty good subsets formed from numbers greater than 1.

---

# Approach 1 — DP Over Prime Masks (Recommended)

## Idea

Let:

```text
dp[mask] = number of ways to form a subset whose used prime set is exactly mask
```

We process valid numbers from `2` to `30` using their frequencies.

If a value has prime-mask `m`, then it can be added only to states `oldMask` such that:

```text
(oldMask & m) == 0
```

because overlapping bits mean repeated primes.

Transition:

```text
dp[oldMask | m] += dp[oldMask] * freq[value]
```

We iterate masks in reverse so each number is used at most once per subset-construction step.

After processing all valid numbers > 1, sum all nonzero masks, then multiply by `2^count(1)`.

---

## Why frequency matters

Suppose the number `6` appears `3` times.

Its mask is fixed, and in any good subset we can choose **at most one** copy of `6`, because choosing two copies would repeat primes `2` and `3`.

So when transitioning with mask of `6`, the number of ways contributed is exactly:

```text
freq[6]
```

because we can choose any one of its occurrences.

---

## Java Code

```java
class Solution {
    private static final int MOD = 1_000_000_007;
    private static final int[] PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};

    public int numberOfGoodSubsets(int[] nums) {
        int[] freq = new int[31];
        for (int x : nums) {
            freq[x]++;
        }

        int[] masks = new int[31];
        for (int x = 2; x <= 30; x++) {
            int mask = 0;
            int num = x;
            boolean valid = true;

            for (int i = 0; i < PRIMES.length; i++) {
                int p = PRIMES[i];
                int count = 0;

                while (num % p == 0) {
                    num /= p;
                    count++;
                }

                if (count > 1) {
                    valid = false;
                    break;
                }

                if (count == 1) {
                    mask |= (1 << i);
                }
            }

            if (valid) {
                masks[x] = mask;
            }
        }

        long[] dp = new long[1 << PRIMES.length];
        dp[0] = 1;

        for (int x = 2; x <= 30; x++) {
            if (freq[x] == 0 || masks[x] == 0) {
                continue;
            }

            int curMask = masks[x];

            for (int oldMask = (1 << PRIMES.length) - 1; oldMask >= 0; oldMask--) {
                if ((oldMask & curMask) != 0) {
                    continue;
                }

                dp[oldMask | curMask] =
                    (dp[oldMask | curMask] + dp[oldMask] * freq[x]) % MOD;
            }
        }

        long ans = 0;
        for (int mask = 1; mask < (1 << PRIMES.length); mask++) {
            ans = (ans + dp[mask]) % MOD;
        }

        long onesWays = modPow(2, freq[1]);
        ans = (ans * onesWays) % MOD;

        return (int) ans;
    }

    private long modPow(long a, int e) {
        long res = 1;
        while (e > 0) {
            if ((e & 1) == 1) {
                res = (res * a) % MOD;
            }
            a = (a * a) % MOD;
            e >>= 1;
        }
        return res;
    }
}
```

---

## Complexity

There are only:

```text
2^10 = 1024
```

masks.

For each value `2..30`, we scan all masks.

So:

```text
Time:  O(30 * 1024)
Space: O(1024)
```

plus the input frequency count.

This is extremely efficient.

This is the standard best solution.

---

# Approach 2 — Same Bitmask DP, Written as Value-by-Value Inclusion DP

## Idea

This is the same mathematical solution, but described more explicitly as “choose or skip each valid value”.

For each valid number `x`:

- skip it
- or choose exactly one occurrence of it, in `freq[x]` ways

Transition is again based on disjoint prime masks.

This approach is not fundamentally different from Approach 1, but it is often conceptually easier to explain.

---

## Java Code

```java
class Solution {
    private static final int MOD = 1_000_000_007;
    private static final int[] PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};

    public int numberOfGoodSubsets(int[] nums) {
        int[] count = new int[31];
        for (int x : nums) count[x]++;

        int[] maskOf = new int[31];
        for (int x = 2; x <= 30; x++) {
            int mask = 0;
            boolean valid = true;
            int t = x;

            for (int i = 0; i < PRIMES.length; i++) {
                int p = PRIMES[i];
                int c = 0;
                while (t % p == 0) {
                    t /= p;
                    c++;
                }
                if (c > 1) {
                    valid = false;
                    break;
                }
                if (c == 1) {
                    mask |= (1 << i);
                }
            }

            if (valid) {
                maskOf[x] = mask;
            }
        }

        long[] dp = new long[1 << 10];
        dp[0] = 1;

        for (int x = 2; x <= 30; x++) {
            if (count[x] == 0 || maskOf[x] == 0) continue;

            long[] next = dp.clone();
            int m = maskOf[x];

            for (int old = 0; old < (1 << 10); old++) {
                if ((old & m) != 0) continue;
                next[old | m] = (next[old | m] + dp[old] * count[x]) % MOD;
            }

            dp = next;
        }

        long result = 0;
        for (int mask = 1; mask < (1 << 10); mask++) {
            result = (result + dp[mask]) % MOD;
        }

        result = result * modPow(2, count[1]) % MOD;
        return (int) result;
    }

    private long modPow(long a, int e) {
        long ans = 1;
        while (e > 0) {
            if ((e & 1) == 1) ans = ans * a % MOD;
            a = a * a % MOD;
            e >>= 1;
        }
        return ans;
    }
}
```

---

## Complexity

Same as Approach 1:

```text
Time:  O(30 * 1024)
Space: O(1024)
```

---

# Approach 3 — Recursive Memoization Over Valid Values and Masks

## Idea

We can also formulate the same problem using recursion.

State:

```text
dfs(index, usedMask)
```

where:

- `index` = which valid value we are considering
- `usedMask` = which primes are already used

At each value:

- skip it
- or take it (if masks do not overlap), multiplied by frequency

This is mathematically correct, but iterative DP is simpler and cleaner here.

Still, it is useful to see the top-down version.

---

## Java Code

```java
import java.util.*;

class Solution {
    private static final int MOD = 1_000_000_007;
    private static final int[] PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};

    List<int[]> validList = new ArrayList<>();
    Long[][] memo;
    int ones;

    public int numberOfGoodSubsets(int[] nums) {
        int[] freq = new int[31];
        for (int x : nums) freq[x]++;

        ones = freq[1];

        for (int x = 2; x <= 30; x++) {
            if (freq[x] == 0) continue;

            int mask = getMask(x);
            if (mask != -1) {
                validList.add(new int[]{mask, freq[x]});
            }
        }

        memo = new Long[validList.size() + 1][1 << 10];
        long ans = dfs(0, 0) - 1; // subtract empty subset
        if (ans < 0) ans += MOD;

        ans = ans * modPow(2, ones) % MOD;
        return (int) ans;
    }

    private long dfs(int idx, int usedMask) {
        if (idx == validList.size()) {
            return 1;
        }

        if (memo[idx][usedMask] != null) {
            return memo[idx][usedMask];
        }

        long res = dfs(idx + 1, usedMask); // skip

        int mask = validList.get(idx)[0];
        int freq = validList.get(idx)[1];

        if ((usedMask & mask) == 0) {
            res = (res + freq * dfs(idx + 1, usedMask | mask)) % MOD;
        }

        return memo[idx][usedMask] = res;
    }

    private int getMask(int x) {
        int mask = 0;
        for (int i = 0; i < PRIMES.length; i++) {
            int p = PRIMES[i];
            int cnt = 0;
            while (x % p == 0) {
                x /= p;
                cnt++;
            }
            if (cnt > 1) return -1;
            if (cnt == 1) mask |= (1 << i);
        }
        return mask;
    }

    private long modPow(long a, int e) {
        long res = 1;
        while (e > 0) {
            if ((e & 1) == 1) res = res * a % MOD;
            a = a * a % MOD;
            e >>= 1;
        }
        return res;
    }
}
```

---

## Complexity

Number of valid values is at most 29.

States:

```text
29 * 1024
```

So complexity is still very small.

This approach is valid, but iterative bitmask DP is still more direct.

---

# Approach 4 — Brute Force Over All Subsets (Conceptual Only)

## Idea

Generate all subsets, compute the product or prime-mask, and count those that are square-free.

This is the direct interpretation of the problem.

But it is impossible for:

```text
nums.length <= 10^5
```

since the number of subsets is exponential.

This approach is useful only for tiny examples and intuition.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int numberOfGoodSubsets(int[] nums) {
        List<List<Integer>> all = new ArrayList<>();
        backtrack(nums, 0, new ArrayList<>(), all);

        int ans = 0;
        for (List<Integer> subset : all) {
            if (subset.isEmpty()) continue;
            if (isGood(subset)) ans++;
        }
        return ans;
    }

    private void backtrack(int[] nums, int idx, List<Integer> cur, List<List<Integer>> all) {
        if (idx == nums.length) {
            all.add(new ArrayList<>(cur));
            return;
        }

        backtrack(nums, idx + 1, cur, all);

        cur.add(nums[idx]);
        backtrack(nums, idx + 1, cur, all);
        cur.remove(cur.size() - 1);
    }

    private boolean isGood(List<Integer> subset) {
        int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};
        int used = 0;

        for (int num : subset) {
            if (num == 1) continue;
            for (int i = 0; i < primes.length; i++) {
                int p = primes[i];
                int cnt = 0;
                while (num % p == 0) {
                    num /= p;
                    cnt++;
                }
                if (cnt > 1) return false;
                if (cnt == 1) {
                    if ((used & (1 << i)) != 0) return false;
                    used |= (1 << i);
                }
            }
        }

        return used != 0;
    }
}
```

---

## Why This Is Impossible

The number of subsets is:

```text
2^n
```

For `n = 10^5`, this is hopelessly large.

So this is only for conceptual understanding.

---

# Detailed Walkthrough of the Recommended Approach

## Example 1

```text
nums = [1,2,3,4]
```

Frequencies:

```text
1 -> 1
2 -> 1
3 -> 1
4 -> 1
```

Now check valid numbers:

- `2` -> prime mask `{2}` -> valid
- `3` -> prime mask `{3}` -> valid
- `4 = 2^2` -> invalid because repeated prime factor

So only usable numbers > 1 are:

```text
2, 3
```

DP starts:

```text
dp[0] = 1
```

Process `2`:

- choose it in 1 way
- update mask `{2}`

Process `3`:

- choose it alone
- or combine with mask `{2}` because no overlap

So subsets from numbers > 1 are:

```text
[2], [3], [2,3]
```

Now there is one `1`, and each of those can independently include or exclude it:

```text
2^1 = 2
```

So total:

```text
3 * 2 = 6
```

which matches the example.

---

# Why Invalid Numbers Must Be Discarded

Numbers like:

- `4 = 2^2`
- `8 = 2^3`
- `12 = 2^2 * 3`
- `18 = 2 * 3^2`

already contain a repeated prime factor inside themselves.

So any subset containing them will have a product with repeated primes.

Therefore they can never appear in a good subset.

This is why we discard them immediately.

---

# Why Ones Are Multiplied at the End

The number `1` contributes no prime factors.

So it does not affect whether a subset is good.

If there are `k` ones, each good subset can be combined with any subset of these ones:

```text
2^k
```

choices.

That is why after counting all good subsets formed from numbers > 1, we multiply by:

```text
2^count(1)
```

---

# Common Pitfalls

## 1. Treating `1` like an ordinary valid number

It is special.
It does not consume any prime mask, and it should be handled as a final multiplier.

---

## 2. Forgetting to reject numbers with repeated prime factors

Such numbers are automatically invalid.

---

## 3. Allowing overlapping prime masks

If two numbers share a prime factor, their product repeats that prime and is invalid.

---

## 4. Counting the empty subset

The empty subset is not good.
We need at least one prime factor in the product.

---

# Best Approach

## Recommended: Bitmask DP over Prime Sets

This is the best solution because:

- values are only from `1` to `30`
- there are only 10 relevant primes
- square-free property maps perfectly to prime masks
- the DP state space is only `1024`

This makes the problem much easier than it first appears.

---

# Final Recommended Java Solution

```java
class Solution {
    private static final int MOD = 1_000_000_007;
    private static final int[] PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};

    public int numberOfGoodSubsets(int[] nums) {
        int[] freq = new int[31];
        for (int x : nums) {
            freq[x]++;
        }

        int[] masks = new int[31];
        for (int x = 2; x <= 30; x++) {
            int mask = 0;
            int num = x;
            boolean valid = true;

            for (int i = 0; i < PRIMES.length; i++) {
                int p = PRIMES[i];
                int count = 0;

                while (num % p == 0) {
                    num /= p;
                    count++;
                }

                if (count > 1) {
                    valid = false;
                    break;
                }

                if (count == 1) {
                    mask |= (1 << i);
                }
            }

            if (valid) {
                masks[x] = mask;
            }
        }

        long[] dp = new long[1 << PRIMES.length];
        dp[0] = 1;

        for (int x = 2; x <= 30; x++) {
            if (freq[x] == 0 || masks[x] == 0) {
                continue;
            }

            int curMask = masks[x];

            for (int oldMask = (1 << PRIMES.length) - 1; oldMask >= 0; oldMask--) {
                if ((oldMask & curMask) != 0) {
                    continue;
                }

                dp[oldMask | curMask] =
                    (dp[oldMask | curMask] + dp[oldMask] * freq[x]) % MOD;
            }
        }

        long ans = 0;
        for (int mask = 1; mask < (1 << PRIMES.length); mask++) {
            ans = (ans + dp[mask]) % MOD;
        }

        ans = (ans * modPow(2, freq[1])) % MOD;
        return (int) ans;
    }

    private long modPow(long a, int e) {
        long res = 1;
        while (e > 0) {
            if ((e & 1) == 1) {
                res = (res * a) % MOD;
            }
            a = (a * a) % MOD;
            e >>= 1;
        }
        return res;
    }
}
```

---

# Complexity Summary

The recommended solution uses:

- 31-value frequency array
- 1024 mask states

So the final complexity is:

```text
Time:  O(30 * 1024)
Space: O(1024)
```

This is extremely fast.

---

# Final Takeaway

The main trick is to stop thinking about subsets directly.

Instead:

1. compress each valid number into a prime mask
2. reject numbers with repeated prime factors
3. do subset DP over used prime masks
4. multiply by `2^(count of ones)`

This transforms what looks like a hard combinatorics problem into a small and elegant bitmask DP.
