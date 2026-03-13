# 2470. Number of Subarrays With LCM Equal to K — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int subarrayLCM(int[] nums, int k) {

    }
}
```

---

# Problem Restatement

We are given:

- an integer array `nums`
- an integer `k`

We need to count how many contiguous non-empty subarrays have:

```text
LCM(subarray) == k
```

A subarray is contiguous, and the least common multiple of a set of numbers is the smallest positive integer divisible by all of them.

---

# Core Insight

The natural brute-force idea is:

- choose a starting index `i`
- extend the subarray to the right
- keep updating the running LCM
- count whenever the running LCM becomes exactly `k`

This works because `nums.length <= 1000`, so an `O(n^2)` approach is acceptable.

The key optimization is:

> Once the running LCM becomes greater than `k`, or is not a divisor of `k`, continuing further can never bring it back to exactly `k`.

That gives a clean early break.

---

# Important LCM Property

For any subarray, as we extend it by adding more elements, its LCM:

- never decreases
- can only stay the same or increase

So if at some point:

```text
lcm > k
```

then the subarray will never again have LCM exactly `k`.

Also, if:

```text
k % lcm != 0
```

then this running LCM contains some prime exponent that is not compatible with `k`, so no extension can fix that either.

This is why the double loop is efficient enough in practice.

---

# Approach 1 — Brute Force with Incremental LCM and Early Break (Recommended)

## Idea

For each start index `i`:

1. initialize `curLcm = 1`
2. extend the subarray to the right
3. update:
   ```text
   curLcm = lcm(curLcm, nums[j])
   ```
4. if `curLcm == k`, increment answer
5. if `curLcm > k` or `k % curLcm != 0`, stop extending from this `i`

Because once the LCM is incompatible with `k`, longer subarrays cannot recover.

---

## Why this works

Every subarray starting at `i` is considered in order.

The running LCM exactly represents the LCM of the current subarray.

The early break is valid because LCM only grows or stays the same.

So this counts every valid subarray exactly once.

---

## Java Code

```java
class Solution {
    public int subarrayLCM(int[] nums, int k) {
        int n = nums.length;
        int ans = 0;

        for (int i = 0; i < n; i++) {
            long lcm = 1;

            for (int j = i; j < n; j++) {
                lcm = lcm(lcm, nums[j]);

                if (lcm == k) {
                    ans++;
                }

                if (lcm > k || k % lcm != 0) {
                    break;
                }
            }
        }

        return ans;
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    private long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }
}
```

---

## Complexity

There are `O(n^2)` subarrays in the worst case.

Each extension performs a GCD/LCM operation.

So:

```text
Time:  O(n^2 log M)
Space: O(1)
```

where `M` is the magnitude of values.

Since:

```text
n <= 1000
```

this is efficient enough.

---

# Approach 2 — Brute Force with Pre-Filtering by Divisibility

## Idea

A subarray can only have LCM equal to `k` if every element in that subarray divides `k`.

Why?

Because if some element `x` does not divide `k`, then the LCM must contain a prime factor or exponent not present in `k`, so it can never equal `k`.

So before even updating the running LCM, we can stop immediately when we encounter:

```text
k % nums[j] != 0
```

This gives slightly cleaner pruning.

---

## Java Code

```java
class Solution {
    public int subarrayLCM(int[] nums, int k) {
        int n = nums.length;
        int ans = 0;

        for (int i = 0; i < n; i++) {
            long cur = 1;

            for (int j = i; j < n; j++) {
                if (k % nums[j] != 0) {
                    break;
                }

                cur = lcm(cur, nums[j]);

                if (cur == k) {
                    ans++;
                }
            }
        }

        return ans;
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    private long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }
}
```

---

## Why this is valid

If `nums[j]` itself is not a divisor of `k`, then no subarray containing it can have LCM exactly `k`.

So the extension can stop immediately.

---

## Complexity

Same asymptotic complexity:

```text
Time:  O(n^2 log M)
Space: O(1)
```

but often slightly better in practice because of earlier breaks.

---

# Approach 3 — Dynamic Set of LCM Values of Subarrays Ending at Each Position

## Idea

A common trick for GCD/LCM subarray problems is:

- for each ending position `j`
- keep all distinct LCM values of subarrays ending at `j`

When extending from position `j-1` to `j`, compute:

```text
newLCM = lcm(oldLCM, nums[j])
```

Also include the single-element subarray `[nums[j]]`.

Because many different subarrays can collapse to the same LCM, the number of distinct LCM states often stays small.

Then count how many of those states equal `k`.

This is more advanced and elegant, though for this problem the simpler quadratic method is usually preferable.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int subarrayLCM(int[] nums, int k) {
        int ans = 0;
        Map<Long, Integer> prev = new HashMap<>();

        for (int x : nums) {
            Map<Long, Integer> cur = new HashMap<>();

            if (k % x == 0) {
                cur.put((long) x, cur.getOrDefault((long) x, 0) + 1);
            }

            for (Map.Entry<Long, Integer> entry : prev.entrySet()) {
                long oldLcm = entry.getKey();
                int cnt = entry.getValue();

                long next = lcm(oldLcm, x);
                if (next <= k && k % next == 0) {
                    cur.put(next, cur.getOrDefault(next, 0) + cnt);
                }
            }

            ans += cur.getOrDefault((long) k, 0);
            prev = cur;
        }

        return ans;
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    private long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }
}
```

---

## Complexity

In the worst case, this can still be large, but in practice the number of distinct LCM states per position is often limited.

A rough practical complexity is:

```text
Time:  O(n * S * log M)
Space: O(S)
```

where `S` is the number of distinct LCM states per position.

For this problem, it is interesting but not necessary.

---

# Approach 4 — Naive Recompute LCM for Every Subarray from Scratch

## Idea

For every pair `(i, j)`, compute the LCM of `nums[i..j]` from scratch.

This is the most literal brute force.

---

## Java Code

```java
class Solution {
    public int subarrayLCM(int[] nums, int k) {
        int n = nums.length;
        int ans = 0;

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                long lcm = 1;
                for (int t = i; t <= j; t++) {
                    lcm = lcm(lcm, nums[t]);
                }
                if (lcm == k) {
                    ans++;
                }
            }
        }

        return ans;
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long temp = a % b;
            a = b;
            b = temp;
        }
        return a;
    }

    private long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }
}
```

---

## Why it is poor

This is roughly:

```text
O(n^3 log M)
```

which is far worse than needed.

Since the LCM can be updated incrementally, recomputing it from scratch is unnecessary.

---

# Detailed Walkthrough

## Example 1

```text
nums = [3,6,2,7,1], k = 6
```

Start at index `0`:

- `[3]` -> LCM = 3
- `[3,6]` -> LCM = 6 ✅
- `[3,6,2]` -> LCM = 6 ✅
- `[3,6,2,7]` -> LCM = 42 > 6, stop

Start at index `1`:

- `[6]` -> LCM = 6 ✅
- `[6,2]` -> LCM = 6 ✅
- `[6,2,7]` -> LCM = 42 > 6, stop

Other starts do not produce LCM 6.

So total valid subarrays:

```text
4
```

They are:

```text
[3,6]
[3,6,2]
[6]
[6,2]
```

---

## Example 2

```text
nums = [3], k = 2
```

Only subarray is `[3]`.

Its LCM is `3`, not `2`.

Answer:

```text
0
```

---

# Important Correctness Argument

For a fixed starting index `i`, as we extend the end index `j`, the running LCM is exactly the LCM of subarray `nums[i..j]`.

So checking `curLcm == k` correctly identifies valid subarrays.

The early break is valid because:

- LCM never decreases when adding more elements
- if it exceeds `k`, it can never become exactly `k` later
- if it no longer divides `k`, it can never match `k` later

Therefore the algorithm counts every valid subarray exactly once and never misses one.

---

# Common Pitfalls

## 1. Forgetting that every element in a valid subarray must divide `k`

If an element does not divide `k`, the subarray can never have LCM `k`.

---

## 2. Using int carelessly for LCM

Even though values are small here (`<= 1000`), using `long` is safer and avoids overflow in general.

---

## 3. Recomputing LCM from scratch for every subarray

That makes the solution much slower than necessary.

---

## 4. Not breaking early

Without pruning, the brute force still works but is less efficient.

---

# Best Approach

## Recommended: Incremental LCM with early break

This is the best solution here because:

- it is simple
- it is fully fast enough for the constraints
- it uses the monotonic behavior of LCM for pruning

---

# Final Recommended Java Solution

```java
class Solution {
    public int subarrayLCM(int[] nums, int k) {
        int n = nums.length;
        int ans = 0;

        for (int i = 0; i < n; i++) {
            long lcm = 1;

            for (int j = i; j < n; j++) {
                if (k % nums[j] != 0) {
                    break;
                }

                lcm = lcm(lcm, nums[j]);

                if (lcm == k) {
                    ans++;
                }

                if (lcm > k) {
                    break;
                }
            }
        }

        return ans;
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    private long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }
}
```

---

# Complexity Summary

```text
Time:  O(n^2 log M)
Space: O(1)
```

For:

```text
1 <= nums.length <= 1000
```

this is efficient enough.

---

# Final Takeaway

The key simplification is that the LCM of a growing subarray only stays the same or increases.

So for each start index, you can extend rightward, maintain the LCM incrementally, and stop as soon as it becomes impossible for the subarray to ever reach `k`.

That turns the problem into a clean quadratic scan.
