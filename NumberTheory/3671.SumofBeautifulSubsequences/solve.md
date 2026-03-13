# Beauty of GCD for Strictly Increasing Subsequences — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int totalBeauty(int[] nums) {

    }
}
```

---

# Problem Restatement

We are given an array `nums`.

For every positive integer `g`, define:

```text
beauty(g) = g × (number of strictly increasing subsequences whose GCD is exactly g)
```

We must return:

```text
Σ beauty(g)
```

over all possible `g`, modulo:

```text
10^9 + 7
```

A subsequence is strictly increasing in values, and the original order of indices must also be preserved.

---

# Core Insight

We are not asked for the beauty of one fixed GCD.

We are asked for the sum of:

```text
g × count(exact gcd = g)
```

over all `g`.

So the real task is:

1. count strictly increasing subsequences by their GCD
2. multiply each GCD by its count
3. sum everything

The challenge is the combination of:

- subsequence ordering
- strict increase condition
- GCD tracking

A naive subsequence DP is exponential, so we need compression.

---

# Key Observation About Strictly Increasing Subsequences

If a subsequence is strictly increasing, then once we know its **last value**, we can only append larger values.

That suggests a DP indexed by:

- ending value
- current GCD

Let:

```text
dp[v][g] = number of strictly increasing subsequences ending with value v and having gcd g
```

Then when processing a new value `x`, we can:

- start a new subsequence `[x]`
- extend all subsequences ending at values `< x`

The new GCD after appending `x` is:

```text
gcd(oldGcd, x)
```

This is the natural DP.

---

# Important Simplification: Values, Not Indices

Because the subsequence must be strictly increasing in values, duplicates cannot both appear in the same subsequence.

If a value `x` appears multiple times in the array, each occurrence can independently end subsequences, but transitions only come from smaller values.

That means we process the array in index order, but the DP state is small enough because GCD values are limited by:

```text
nums[i] <= 7 * 10^4
```

Also, each number has only a relatively small number of possible GCD states in practice.

---

# Approach 1 — DP Over Ending Value and GCD Using Fenwick Trees / Prefix Aggregation (Recommended)

## Idea

We want fast access to:

> all subsequences ending with values `< x`, grouped by their gcd

A straightforward scan over all smaller values for every element would be too slow.

So we can maintain, for each gcd `g`, a Fenwick tree (or some prefix-summation structure) over values that stores how many subsequences with gcd `g` end at each value.

Then for a new number `x`:

1. start subsequence `[x]`
2. for every gcd state `g` that exists among smaller ending values:
   - number of such subsequences ending with value `< x`
   - append `x`
   - new gcd becomes `gcd(g, x)`

This yields a DP with value-based prefix queries.

However, maintaining a Fenwick tree for every gcd up to `7e4` is memory-heavy.

A more practical version is to process subsequences ending at each distinct value with hash maps and use a Fenwick tree over counts of values to assist transitions only when useful.

Still, the cleanest competitive-programming implementation is usually the next approach.

---

# Approach 2 — Per-Index GCD-State DP With Merging (Practical Recommended Form)

## Idea

For each index `i`, maintain a map:

```text
cur[g] = number of strictly increasing subsequences ending at nums[i] with gcd g
```

Transitions come from earlier indices `j < i` with:

```text
nums[j] < nums[i]
```

Then:

```text
newGcd = gcd(g, nums[i])
```

This is conceptually simple, but naive `O(n^2)` index transitions are too slow for `n = 10^4`.

So we need to compress by value.

---

# Better Practical DP: Aggregate by Value

Let:

```text
best[v] = map from gcd -> number of strictly increasing subsequences ending with value exactly v
```

When processing value `x`, we need contributions from all smaller values `v < x`.

A direct loop over all smaller values is expensive, but we can observe that the number of distinct values is bounded by `7 * 10^4`, which is manageable for divisor-based optimizations.

Still, there is an even cleaner Möbius-style counting route.

---

# Crucial Number-Theoretic Reframing

Instead of counting subsequences by **exact gcd** directly, count them by **all elements divisible by d**.

For a fixed `d`, consider only numbers divisible by `d`.

Divide them all by `d`. Then a subsequence of original numbers has gcd divisible by `d` iff the scaled subsequence has gcd divisible by `1`, meaning simply all chosen elements came from numbers divisible by `d`.

So if we can count strictly increasing subsequences using only values divisible by `d`, call that:

```text
F[d] = number of strictly increasing subsequences whose gcd is divisible by d
```

then by inclusion-exclusion over multiples:

```text
exact[d] = F[d] - exact[2d] - exact[3d] - ...
```

Finally:

```text
answer = Σ d × exact[d]
```

This is the cleanest route.

---

# Counting F[d]

For a fixed `d`, we keep only array elements divisible by `d`.

Among those elements, we need the number of strictly increasing subsequences.

That is a classic counting problem:

- process numbers in array order
- subsequence values must strictly increase
- equal values cannot extend each other

We can count strictly increasing subsequences using a Fenwick tree over values:

```text
dp[i] = 1 + sum of counts of subsequences ending at smaller values
```

Then sum all `dp[i]`.

Do this for every divisor `d`.

At first glance this seems expensive, but each element contributes only to the divisors of its value, and each value has only about `O(sqrt(A))` divisors.

This makes the total feasible with careful implementation.

---

# Approach 3 — Divisor-Based Counting + Möbius-Style Inclusion-Exclusion (Best Theoretical/Practical Hybrid)

## Idea

### Step 1

For each `d`, collect the subsequence count:

```text
F[d] = number of strictly increasing subsequences using only elements divisible by d
```

### Step 2

Recover exact gcd counts:

```text
exact[d] = F[d] - Σ exact[multiple of d, > d]
```

### Step 3

Compute:

```text
answer = Σ d * exact[d]
```

The difficult part is computing `F[d]` efficiently.

For a fixed `d`, count strictly increasing subsequences among the filtered sequence of values divisible by `d`.

That can be done with a Fenwick tree over values.

---

## Java Code

```java
import java.util.*;

class Solution {
    static final int MOD = 1_000_000_007;

    public int totalBeauty(int[] nums) {
        int maxVal = 0;
        for (int x : nums) maxVal = Math.max(maxVal, x);

        List<Integer>[] positionsByDiv = new ArrayList[maxVal + 1];
        for (int i = 1; i <= maxVal; i++) {
            positionsByDiv[i] = new ArrayList<>();
        }

        // For each divisor d, store the values from nums that are divisible by d, in original order.
        for (int x : nums) {
            List<Integer> divs = divisors(x);
            for (int d : divs) {
                positionsByDiv[d].add(x);
            }
        }

        long[] divisibleCount = new long[maxVal + 1];

        for (int d = 1; d <= maxVal; d++) {
            if (positionsByDiv[d].isEmpty()) continue;
            divisibleCount[d] = countIncreasingSubseq(positionsByDiv[d], maxVal);
        }

        long[] exact = new long[maxVal + 1];
        for (int d = maxVal; d >= 1; d--) {
            long val = divisibleCount[d];
            for (int m = d + d; m <= maxVal; m += d) {
                val -= exact[m];
            }
            val %= MOD;
            if (val < 0) val += MOD;
            exact[d] = val;
        }

        long ans = 0;
        for (int d = 1; d <= maxVal; d++) {
            ans = (ans + d * exact[d]) % MOD;
        }

        return (int) ans;
    }

    private long countIncreasingSubseq(List<Integer> arr, int maxVal) {
        Fenwick bit = new Fenwick(maxVal + 2);
        long total = 0;

        for (int x : arr) {
            long ways = 1 + bit.query(x - 1);
            if (ways >= MOD) ways -= MOD;
            bit.add(x, ways);
            total += ways;
            if (total >= MOD) total -= MOD;
        }

        return total;
    }

    private List<Integer> divisors(int x) {
        List<Integer> small = new ArrayList<>();
        List<Integer> large = new ArrayList<>();

        for (int d = 1; d * d <= x; d++) {
            if (x % d == 0) {
                small.add(d);
                if (d * d != x) large.add(x / d);
            }
        }

        for (int i = large.size() - 1; i >= 0; i--) small.add(large.get(i));
        return small;
    }

    static class Fenwick {
        long[] bit;

        Fenwick(int n) {
            bit = new long[n + 1];
        }

        void add(int idx, long val) {
            for (idx++; idx < bit.length; idx += idx & -idx) {
                bit[idx] += val;
                if (bit[idx] >= MOD) bit[idx] -= MOD;
            }
        }

        long query(int idx) {
            long res = 0;
            for (idx++; idx > 0; idx -= idx & -idx) {
                res += bit[idx];
                if (res >= MOD) res -= MOD;
            }
            return res;
        }
    }
}
```

---

## Important Note About This Approach

This is the right high-level mathematics, but the raw implementation above can still be heavy because each divisor builds its own subsequence count from its filtered list.

It is a strong conceptual solution and can be optimized further, but it is not always the simplest implementation path.

For a contest/editorial-quality final implementation, one would usually refine the divisor processing or exploit tighter amortization.

Still, the method is valid and important.

---

# Approach 4 — Index DP With HashMaps (Conceptual, But Too Slow)

## Idea

For each position `i`, keep a map from gcd to count of increasing subsequences ending at `i`.

Transition from every earlier `j < i` with `nums[j] < nums[i]`.

That gives:

```text
dp[i][nums[i]] += 1
dp[i][gcd(g, nums[i])] += dp[j][g]
```

Then sum contributions over all `i`.

---

## Java Code

```java
import java.util.*;

class Solution {
    static final int MOD = 1_000_000_007;

    public int totalBeauty(int[] nums) {
        int n = nums.length;
        Map<Integer, Long>[] dp = new HashMap[n];
        long ans = 0;

        for (int i = 0; i < n; i++) {
            dp[i] = new HashMap<>();
            dp[i].put(nums[i], 1L);

            for (int j = 0; j < i; j++) {
                if (nums[j] >= nums[i]) continue;

                for (Map.Entry<Integer, Long> e : dp[j].entrySet()) {
                    int g = gcd(e.getKey(), nums[i]);
                    long val = (dp[i].getOrDefault(g, 0L) + e.getValue()) % MOD;
                    dp[i].put(g, val);
                }
            }

            for (Map.Entry<Integer, Long> e : dp[i].entrySet()) {
                ans = (ans + 1L * e.getKey() * e.getValue()) % MOD;
            }
        }

        return (int) ans;
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

In the worst case this is close to:

```text
O(n^2 * number_of_gcd_states)
```

which is too slow for:

```text
n = 10^4
```

So this is only useful for intuition.

---

# Example Walkthrough

## Example 1

```text
nums = [1,2,3]
```

Strictly increasing subsequences:

- `[1]` -> gcd 1
- `[2]` -> gcd 2
- `[3]` -> gcd 3
- `[1,2]` -> gcd 1
- `[1,3]` -> gcd 1
- `[2,3]` -> gcd 1
- `[1,2,3]` -> gcd 1

Exact gcd counts:

- gcd 1: 5 subsequences
- gcd 2: 1 subsequence
- gcd 3: 1 subsequence

Beauty sum:

```text
1*5 + 2*1 + 3*1 = 10
```

---

## Example 2

```text
nums = [4,6]
```

Strictly increasing subsequences:

- `[4]` -> gcd 4
- `[6]` -> gcd 6
- `[4,6]` -> gcd 2

Beauty sum:

```text
4 + 6 + 2 = 12
```

---

# Why Inclusion-Exclusion Works Here

If `F[d]` counts increasing subsequences whose gcd is divisible by `d`, then every subsequence with exact gcd `g` contributes to all `F[d]` where `d | g`.

So:

```text
F[d] = Σ exact[m]   over multiples m of d
```

Thus we can recover `exact[d]` from larger multiples downward:

```text
exact[d] = F[d] - Σ exact[m], m = 2d, 3d, ...
```

This is the standard divisor-zeta / Möbius inversion pattern.

---

# Common Pitfalls

## 1. Forgetting that subsequences must be strictly increasing in value

Not every subsequence counts. Equal or decreasing extensions are invalid.

---

## 2. Counting gcd exactly without using divisibility counting

Direct exact-gcd DP is much harder.

---

## 3. Ignoring modulo arithmetic during inclusion-exclusion

Subtractions must be normalized back into `[0, MOD)`.

---

## 4. Using a naive `O(n^2)` transition DP

That is too slow for `n = 10^4`.

---

# Best Approach

## Recommended: Count subsequences by gcd divisibility, then invert over multiples

This is the most powerful perspective:

1. count strictly increasing subsequences whose gcd is divisible by `d`
2. recover exact gcd counts by inclusion-exclusion
3. sum `g * count`

It uses both:

- increasing subsequence counting
- divisor inversion

That is the right structure for the problem.

---

# Final Takeaway

The key shift is:

Do not count exact gcds directly.

Instead:

- count increasing subsequences where all values are divisible by `d`
- that gives the number of subsequences whose gcd is a multiple of `d`
- invert over multiples to get exact gcd counts
- weight by `g` and sum

That is the clean mathematical route to the solution.

```java
	class Solution {
		static long[][] fts = new long[70001][];
		static {
			for (int i = 1; i <= 70000; i++) {
				fts[i] = new long[70000 / i + 4];
			}
		}

		long solve(List<Integer> a, int lim)
		{
			if(a.size() <= 0)return 0;
			long[] ft = new long[lim+3];
			for(int v : a){
				addFenwick(ft, v, sumFenwick(ft, v-1) + 1);
			}
			return sumFenwick(ft, lim);
		}

		public static final int mod = 1000000007;

		public static long sumFenwick(long[] ft, int i)
		{
			long sum = 0;
			for(i++;i > 0;i -= i&-i){
				sum += ft[i];
				if(sum >= mod)sum -= mod;
			}
			return sum;
		}

		public static void addFenwick(long[] ft, int i, long v)
		{
			v %= mod;
			if(v < 0)v += mod;
			if(v == 0)return;
			int n = ft.length;
			for(i++;i < n;i += i&-i){
				ft[i] += v;
				if(ft[i] >= mod)ft[i] -= mod;
			}
		}

		public int totalBeauty(int[] nums) {
			for(int i = 1;i <= 70000;i++){
				Arrays.fill(fts[i], 0);
			}
			for(int v : nums){
				for(int d = 1;d*d <= v;d++){
					if(v % d == 0){
						addFenwick(fts[d], v/d, sumFenwick(fts[d], v/d-1) + 1);
						if(d * d != v){
							addFenwick(fts[v/d], v/(v/d), sumFenwick(fts[v/d], v/(v/d)-1) + 1);
						}
					}
				}
			}
			long[] res = new long[70001];
			for(int i = 1;i <= 70000;i++){
				res[i] = sumFenwick(fts[i], 70000/i+1);
			}
			for(int i = 70000;i >= 1;i--){
				for(int j = i*2;j <= 70000;j += i){
					res[i] -= res[j];
					if(res[i] < 0)res[i] += mod;
				}
			}
			long ans = 0;
			for(int i = 1;i <= 70000;i++){
				ans += i * res[i];
				ans %= mod;
			}
			return (int)ans;
		}
	}
```
