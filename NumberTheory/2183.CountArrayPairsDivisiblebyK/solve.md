# 2183. Count Array Pairs Divisible by K — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public long countPairs(int[] nums, int k) {

    }
}
```

---

# Problem Restatement

We are given:

- an array `nums`
- an integer `k`

We must count pairs `(i, j)` such that:

```text
0 <= i < j < nums.length
```

and:

```text
nums[i] * nums[j] % k == 0
```

We return the total number of such pairs.

---

# Core Difficulty

A direct check of all pairs takes:

```text
O(n^2)
```

which is far too slow for:

```text
nums.length <= 10^5
```

So we need to exploit the divisibility condition more cleverly.

---

# Key Insight

For any number `x`, what matters with respect to divisibility by `k` is not `x` itself, but:

```text
gcd(x, k)
```

Why?

Because only the prime factors shared with `k` can help make:

```text
x * y
```

divisible by `k`.

So instead of looking at full values, we reduce every number to:

```text
g = gcd(nums[i], k)
```

Then for two numbers with gcd values `g1` and `g2`, the pair is valid iff:

```text
(g1 * g2) % k == 0
```

That transforms the problem into counting gcd-pairs.

---

# Why GCD Compression Works

Let:

```text
a = nums[i]
b = nums[j]
g1 = gcd(a, k)
g2 = gcd(b, k)
```

The part of `a` or `b` outside of the prime factors of `k` is irrelevant to divisibility by `k`.

So the condition:

```text
a * b is divisible by k
```

is equivalent to:

```text
g1 * g2 is divisible by k
```

This is the central observation.

---

# Approach 1 — HashMap Over GCD Values (Recommended)

## Idea

Process numbers from left to right.

For current number `x`:

1. compute:

```text
g = gcd(x, k)
```

2. count how many previous gcd-values `prev` satisfy:

```text
(prev * g) % k == 0
```

3. add that count to the answer
4. store `g` in a frequency map

Since gcd values must divide `k`, the number of distinct possible gcd values is not very large in practice.

This makes the approach efficient.

---

## Java Code

```java
import java.util.*;

class Solution {
    public long countPairs(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        long ans = 0;

        for (int x : nums) {
            int g = gcd(x, k);

            for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
                int prev = entry.getKey();
                int count = entry.getValue();

                if ((long) prev * g % k == 0) {
                    ans += count;
                }
            }

            freq.put(g, freq.getOrDefault(g, 0) + 1);
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

## Why this works

At step `j`, the map contains gcd-values of all earlier elements `i < j`.

For each such earlier gcd-value `prev`, we test whether:

```text
prev * g
```

contains enough prime power to cover `k`.

If yes, every earlier element with that gcd contributes a valid pair.

---

## Complexity

Let `D` be the number of distinct gcd values encountered.

Then:

```text
Time:  O(n * D)
Space: O(D)
```

Since every gcd value must divide `k`, `D` is bounded by the number of divisors of `k`, which is usually small.

This is the standard clean solution.

---

# Approach 2 — Enumerate Divisors of K Explicitly

## Idea

Instead of storing arbitrary gcd values in a `HashMap`, first compute all divisors of `k`.

Then maintain a frequency array/map only over divisors of `k`.

For each current `g = gcd(nums[i], k)`, count how many divisor states `d` satisfy:

```text
(d * g) % k == 0
```

This is mathematically the same as Approach 1, but it makes the divisor structure explicit.

---

## Why it can be nice

Every gcd with `k` must be a divisor of `k`, so enumerating divisors can make the logic more structured and sometimes easier to analyze.

---

## Java Code

```java
import java.util.*;

class Solution {
    public long countPairs(int[] nums, int k) {
        List<Integer> divisors = getDivisors(k);
        Map<Integer, Integer> freq = new HashMap<>();
        long ans = 0;

        for (int x : nums) {
            int g = gcd(x, k);

            for (int d : divisors) {
                if ((long) d * g % k == 0) {
                    ans += freq.getOrDefault(d, 0);
                }
            }

            freq.put(g, freq.getOrDefault(g, 0) + 1);
        }

        return ans;
    }

    private List<Integer> getDivisors(int k) {
        List<Integer> res = new ArrayList<>();
        for (int i = 1; i * i <= k; i++) {
            if (k % i == 0) {
                res.add(i);
                if (i != k / i) {
                    res.add(k / i);
                }
            }
        }
        return res;
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

If `tau(k)` is the number of divisors of `k`, then:

```text
Time:  O(n * tau(k))
Space: O(tau(k))
```

Since `k <= 10^5`, `tau(k)` is small enough.

This is also a very good solution.

---

# Approach 3 — Prime Factor Count View (Conceptual)

## Idea

We can factorize `k`:

```text
k = p1^a1 * p2^a2 * ... * pm^am
```

For a pair `(x, y)` to satisfy:

```text
x * y divisible by k
```

the combined prime exponents from `x` and `y` must cover every required exponent in `k`.

So one conceptual route is:

1. factorize `k`
2. for each number, record how much of each required prime it contributes
3. count complementary patterns

This is correct in principle, but implementing it directly is more cumbersome than the gcd compression trick.

Since:

```text
gcd(x, k)
```

already captures exactly the usable contribution of `x` toward `k`, the gcd method is simpler.

---

## Why this is mostly conceptual

Prime-exponent state encoding can get more complicated than needed.

The gcd representation automatically compresses the useful divisibility information into a single integer.

So this approach is good for understanding, but not usually the final implementation choice.

---

# Approach 4 — Brute Force Over All Pairs (Too Slow)

## Idea

Try every pair `(i, j)` and check:

```text
(nums[i] * nums[j]) % k == 0
```

---

## Java Code

```java
class Solution {
    public long countPairs(int[] nums, int k) {
        long ans = 0;

        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if ((long) nums[i] * nums[j] % k == 0) {
                    ans++;
                }
            }
        }

        return ans;
    }
}
```

---

## Why it fails

This takes:

```text
O(n^2)
```

With:

```text
n = 10^5
```

that is completely infeasible.

So this is only useful as a baseline or for very small examples.

---

# Detailed Walkthrough

## Example 1

```text
nums = [1,2,3,4,5]
k = 2
```

For each number compute gcd with `k`:

- `gcd(1,2) = 1`
- `gcd(2,2) = 2`
- `gcd(3,2) = 1`
- `gcd(4,2) = 2`
- `gcd(5,2) = 1`

So gcd sequence is:

```text
[1,2,1,2,1]
```

A pair is valid if:

```text
g1 * g2 % 2 == 0
```

That means at least one of them must be `2`.

Count such pairs:

- pairs involving index 1 (`g=2`) with earlier/later suitable numbers
- pairs involving index 3 (`g=2`)

Total becomes 7, matching the example.

---

## Example 2

```text
nums = [1,2,3,4]
k = 5
```

GCDs with 5:

- `gcd(1,5)=1`
- `gcd(2,5)=1`
- `gcd(3,5)=1`
- `gcd(4,5)=1`

Every gcd value is 1.

So every pair has:

```text
1 * 1 % 5 = 1
```

No pair works.

Answer = 0.

---

# Important Correctness Argument

Why is it enough to use `gcd(nums[i], k)`?

Because divisibility by `k` only depends on prime factors that also appear in `k`.

Any prime factor in `nums[i]` that is not part of `k` is irrelevant for making the product divisible by `k`.

So replacing each number by its gcd with `k` keeps exactly the useful divisibility information and throws away the irrelevant part.

Thus:

```text
nums[i] * nums[j] divisible by k
```

if and only if:

```text
gcd(nums[i], k) * gcd(nums[j], k) divisible by k
```

That is why the gcd compression is sound.

---

# Common Pitfalls

## 1. Multiplying ints directly without casting

Since `nums[i]` and `nums[j]` can be up to `10^5`, their product can exceed `int`.

Always use:

```java
(long) a * b
```

when testing divisibility.

---

## 2. Checking all pairs directly

That is too slow.

We must exploit gcd compression.

---

## 3. Thinking the actual value matters more than gcd with `k`

It does not. Only the portion relevant to `k` matters.

---

## 4. Forgetting that gcd values must divide `k`

This is what keeps the number of states small.

---

# Best Approach

## Recommended: HashMap / divisor-frequency over gcd values

This is the best solution because:

- it directly exploits the structure of divisibility by `k`
- it reduces each value to `gcd(value, k)`
- the number of distinct gcd states is small
- it avoids quadratic enumeration

Among implementations, both the `HashMap` version and divisor-enumeration version are good.
The HashMap version is shorter and usually preferred.

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    public long countPairs(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        long ans = 0;

        for (int x : nums) {
            int g = gcd(x, k);

            for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
                int prev = entry.getKey();
                int count = entry.getValue();

                if ((long) prev * g % k == 0) {
                    ans += count;
                }
            }

            freq.put(g, freq.getOrDefault(g, 0) + 1);
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

Let `D` be the number of distinct gcd values with `k`.

Then:

```text
Time:  O(n * D)
Space: O(D)
```

Since `D` is bounded by the number of divisors of `k`, this is efficient for the constraints.

---

# Final Takeaway

The crucial shift is:

Instead of checking whether:

```text
nums[i] * nums[j] % k == 0
```

directly for all pairs, reduce each number to:

```text
gcd(nums[i], k)
```

Then the problem becomes counting pairs of compressed gcd states whose product covers `k`.

That turns an apparently quadratic divisibility problem into a compact divisor-based counting problem.
