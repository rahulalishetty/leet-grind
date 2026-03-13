# GCD Pairs Query Problem — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int[] gcdValues(int[] nums, long[] queries) {

    }
}
```

---

# Problem Restatement

We are given:

- an integer array `nums`
- an array `queries`

Consider all unordered pairs:

```text
(nums[i], nums[j]) where 0 <= i < j < n
```

For each pair, compute its gcd.

Collect all these gcd values into an array called `gcdPairs`, then sort it in ascending order.

For every query `queries[t]`, we must return:

```text
gcdPairs[queries[t]]
```

---

# Core Difficulty

There are:

```text
n * (n - 1) / 2
```

pairs.

With:

```text
n <= 10^5
```

this can be enormous, so explicitly generating all pair gcd values is impossible.

So the real problem is:

> How many pairs have gcd exactly equal to `g`?

If we can count that for all `g`, then we can reconstruct the sorted `gcdPairs` distribution without building it explicitly.

---

# Key Insight

Let:

```text
cnt[x] = frequency of number x in nums
```

For any integer `d`, let:

```text
multiples[d] = how many numbers in nums are divisible by d
```

Then the number of pairs whose gcd is a multiple of `d` is:

```text
C(multiples[d], 2)
```

because any two numbers divisible by `d` produce a gcd divisible by `d`.

But we want pairs whose gcd is **exactly** `d`, not merely a multiple of `d`.

That suggests inclusion-exclusion / Möbius-style subtraction over multiples.

---

# Exact GCD Counting Formula

Define:

```text
atLeast[d] = number of pairs whose gcd is divisible by d
          = C(multiples[d], 2)
```

Then:

```text
exact[d] = atLeast[d] - exact[2d] - exact[3d] - exact[4d] - ...
```

So if we process `d` from large to small, we can compute the number of pairs whose gcd is exactly `d`.

This is the heart of the solution.

---

# After We Know exact[d]

If `exact[d]` tells us how many pair gcds equal `d`, then the sorted `gcdPairs` looks like:

- `exact[1]` copies of `1`
- `exact[2]` copies of `2`
- `exact[3]` copies of `3`
- ...

in ascending order.

So we build a prefix sum:

```text
prefix[d] = exact[1] + exact[2] + ... + exact[d]
```

Then for a query `q`, the answer is the smallest `d` such that:

```text
prefix[d] > q
```

Because `q` is a 0-based index.

This can be found using binary search.

---

# Approach 1 — Sieve Over Divisors + Inclusion-Exclusion + Prefix Search (Recommended)

## Idea

1. Count the frequency of each value in `nums`
2. For every divisor `d`, count how many array elements are divisible by `d`
3. Compute:

```text
atLeast[d] = C(multiples[d], 2)
```

4. From large to small, subtract contributions of multiples to get:

```text
exact[d]
```

5. Build prefix sums over `exact`
6. Answer each query with binary search

This is the standard optimal solution.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int[] gcdValues(int[] nums, long[] queries) {
        int maxVal = 0;
        for (int x : nums) {
            maxVal = Math.max(maxVal, x);
        }

        int[] freq = new int[maxVal + 1];
        for (int x : nums) {
            freq[x]++;
        }

        long[] multiples = new long[maxVal + 1];
        for (int d = 1; d <= maxVal; d++) {
            for (int m = d; m <= maxVal; m += d) {
                multiples[d] += freq[m];
            }
        }

        long[] exact = new long[maxVal + 1];

        for (int d = maxVal; d >= 1; d--) {
            long total = multiples[d] * (multiples[d] - 1) / 2;

            for (int m = 2 * d; m <= maxVal; m += d) {
                total -= exact[m];
            }

            exact[d] = total;
        }

        long[] prefix = new long[maxVal + 1];
        for (int d = 1; d <= maxVal; d++) {
            prefix[d] = prefix[d - 1] + exact[d];
        }

        int[] ans = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            long q = queries[i];
            ans[i] = lowerBound(prefix, q + 1);
        }

        return ans;
    }

    private int lowerBound(long[] prefix, long target) {
        int left = 1, right = prefix.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (prefix[mid] >= target) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }
}
```

---

## Why this works

For each `d`, we first count all pairs divisible by `d`.

That includes pairs whose gcd is:

- exactly `d`
- `2d`
- `3d`
- and so on

So subtracting all exact counts of larger multiples leaves only the pairs with gcd exactly `d`.

Once we know the exact frequency of every gcd value, the sorted multiset is determined.

---

## Complexity

Let:

```text
M = max(nums)
```

Then:

- counting divisible numbers for all `d`: harmonic series style `O(M log M)`
- subtracting over multiples for all `d`: also `O(M log M)`
- each query answered with binary search: `O(log M)`

So:

```text
Time:  O(M log M + Q log M)
Space: O(M)
```

This is efficient for:

```text
M <= 5 * 10^4
```

---

# Approach 2 — Möbius / Exact GCD Count Interpretation (Same Math, Different Framing)

## Idea

The same large-to-small subtraction can be viewed as a Möbius-style inclusion-exclusion.

Instead of thinking operationally, think algebraically:

- `atLeast[d]` counts pairs with gcd divisible by `d`
- `exact[d]` is recovered by subtracting all exact larger multiples

This is not really a different algorithm, but it is a useful conceptual lens.

You can implement it exactly the same way as Approach 1.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int[] gcdValues(int[] nums, long[] queries) {
        int maxVal = 0;
        for (int x : nums) maxVal = Math.max(maxVal, x);

        int[] freq = new int[maxVal + 1];
        for (int x : nums) freq[x]++;

        long[] divCount = new long[maxVal + 1];
        for (int d = 1; d <= maxVal; d++) {
            for (int m = d; m <= maxVal; m += d) {
                divCount[d] += freq[m];
            }
        }

        long[] gcdCount = new long[maxVal + 1];
        for (int d = maxVal; d >= 1; d--) {
            gcdCount[d] = divCount[d] * (divCount[d] - 1) / 2;
            for (int m = d + d; m <= maxVal; m += d) {
                gcdCount[d] -= gcdCount[m];
            }
        }

        long[] pref = new long[maxVal + 1];
        for (int i = 1; i <= maxVal; i++) {
            pref[i] = pref[i - 1] + gcdCount[i];
        }

        int[] res = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            res[i] = firstAtLeast(pref, queries[i] + 1);
        }

        return res;
    }

    private int firstAtLeast(long[] pref, long target) {
        int lo = 1, hi = pref.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (pref[mid] >= target) hi = mid;
            else lo = mid + 1;
        }
        return lo;
    }
}
```

---

## Complexity

Same as Approach 1:

```text
Time:  O(M log M + Q log M)
Space: O(M)
```

---

# Approach 3 — Brute Force Pair Generation + Sort (Too Slow)

## Idea

A straightforward solution is:

1. generate every pair
2. compute gcd
3. store all gcd values
4. sort them
5. answer queries directly

This is correct for small arrays, but impossible under the constraints.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int[] gcdValues(int[] nums, long[] queries) {
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                list.add(gcd(nums[i], nums[j]));
            }
        }

        Collections.sort(list);

        int[] ans = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            ans[i] = list.get((int) queries[i]);
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

## Why it fails

There are:

```text
O(n^2)
```

pairs.

With:

```text
n = 10^5
```

this is infeasible in both time and memory.

So this approach is only a conceptual baseline.

---

# Detailed Walkthrough

## Example 1

```text
nums = [2, 3, 4]
```

Pairs:

- gcd(2,3) = 1
- gcd(2,4) = 2
- gcd(3,4) = 1

Sorted gcdPairs:

```text
[1, 1, 2]
```

Queries:

```text
[0, 2, 2]
```

Answers:

- index 0 -> 1
- index 2 -> 2
- index 2 -> 2

So:

```text
[1, 2, 2]
```

---

# How the Counting Method Sees Example 1

Frequencies:

```text
freq[2] = 1
freq[3] = 1
freq[4] = 1
```

Divisible counts:

- divisible by 1: 3 numbers
- divisible by 2: 2 numbers (2,4)
- divisible by 3: 1 number
- divisible by 4: 1 number

Then:

```text
atLeast[1] = C(3,2) = 3
atLeast[2] = C(2,2) = 1
```

Now exact counts:

```text
exact[2] = 1
exact[1] = 3 - exact[2] = 2
```

So gcd multiset is:

- two 1s
- one 2

which matches:

```text
[1,1,2]
```

---

# Important Correctness Argument

Every pair whose gcd is exactly `g` is counted in `atLeast[d]` for every divisor `d` of `g`.

So `atLeast[d]` mixes together pairs with gcd equal to all multiples of `d`.

Processing from large to small ensures that when computing `exact[d]`, all `exact[m]` for multiples `m > d` are already known.

Thus subtracting them leaves precisely the count of pairs with gcd exactly `d`.

This is the same divisor-sieve idea that appears in many gcd/lcm counting problems.

---

# Common Pitfalls

## 1. Using int for pair counts

The number of pairs can be as large as:

```text
n * (n - 1) / 2
```

which requires `long`.

So arrays storing pair counts and prefix counts must use `long`.

---

## 2. Forgetting that queries are 0-based

If `queries[i] = q`, we need the `(q+1)`-th element in 1-based prefix-count language.

That is why binary search uses:

```text
target = q + 1
```

---

## 3. Trying to build gcdPairs explicitly

This is impossible for large `n`.

We only need the distribution of gcd values.

---

## 4. Miscounting exact gcd frequencies

Remember:

```text
exact[d] = atLeast[d] - sum(exact[multiples of d greater than d])
```

not the other way around.

---

# Best Approach

## Recommended: Divisor sieve + exact gcd pair counting + prefix binary search

This is the best solution because:

- it avoids quadratic pair generation
- it exploits the small value bound `nums[i] <= 5 * 10^4`
- it answers many queries efficiently after one preprocessing pass

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    public int[] gcdValues(int[] nums, long[] queries) {
        int maxVal = 0;
        for (int x : nums) {
            maxVal = Math.max(maxVal, x);
        }

        int[] freq = new int[maxVal + 1];
        for (int x : nums) {
            freq[x]++;
        }

        long[] multiples = new long[maxVal + 1];
        for (int d = 1; d <= maxVal; d++) {
            for (int m = d; m <= maxVal; m += d) {
                multiples[d] += freq[m];
            }
        }

        long[] exact = new long[maxVal + 1];

        for (int d = maxVal; d >= 1; d--) {
            long total = multiples[d] * (multiples[d] - 1) / 2;
            for (int m = 2 * d; m <= maxVal; m += d) {
                total -= exact[m];
            }
            exact[d] = total;
        }

        long[] prefix = new long[maxVal + 1];
        for (int d = 1; d <= maxVal; d++) {
            prefix[d] = prefix[d - 1] + exact[d];
        }

        int[] ans = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            ans[i] = lowerBound(prefix, queries[i] + 1);
        }

        return ans;
    }

    private int lowerBound(long[] prefix, long target) {
        int left = 1, right = prefix.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (prefix[mid] >= target) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }
}
```

---

# Complexity Summary

Let:

```text
M = max(nums)
Q = queries.length
```

Then:

```text
Time:  O(M log M + Q log M)
Space: O(M)
```

This is efficient for the given constraints.

---

# Final Takeaway

The problem looks like a pairwise gcd enumeration problem, but the right perspective is:

1. count how many numbers are divisible by each divisor
2. use inclusion-exclusion over multiples to compute how many pairs have gcd exactly equal to each value
3. build the sorted gcd distribution implicitly via prefix sums
4. answer each query with binary search
