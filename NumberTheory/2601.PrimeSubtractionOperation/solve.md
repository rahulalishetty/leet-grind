# 2601. Prime Subtraction Operation — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public boolean primeSubOperation(int[] nums) {

    }
}
```

---

# Problem Restatement

We are given an array `nums`.

For each index, we may perform the following operation **at most once**:

- choose a prime number `p` such that:
  ```text
  p < nums[i]
  ```
- replace:
  ```text
  nums[i] = nums[i] - p
  ```

We need to determine whether it is possible to make the whole array **strictly increasing**.

That means:

```text
nums[i] > nums[i - 1] for every i > 0
```

---

# Core Insight

At index `i`, after optional subtraction, the final value must be:

```text
> previous final value
```

To maximize our future flexibility, we should make the current number as **small as possible**, while still keeping it strictly greater than the previous number.

This is a classic greedy idea:

> For each position, choose the smallest achievable value that is still valid.

Why is that good?

Because a smaller current value makes it easier for later elements to be larger than it.

---

# What Values Can One Element Become?

For a number `x`, after one operation it can become:

```text
x - p
```

for any prime `p < x`.

Or we may choose not to operate, in which case it stays `x`.

So for each index, we want the **smallest possible value greater than prev**.

That means we want the **largest prime** `p` such that:

```text
x - p > prev
```

Rearranging:

```text
p < x - prev
```

So at index `i`, if current value is `x`, and previous final value is `prev`, we want a prime:

```text
p < x - prev
```

and as large as possible.

If such a prime exists, subtract it.
Otherwise, keep `x` unchanged if:

```text
x > prev
```

If not, then the task is impossible.

---

# Greedy Strategy

Walk from left to right.

At each index:

- let `prev` be the final chosen value of the previous element
- let current original value be `x`

Try to make `x` as small as possible but still greater than `prev`.

That means:

1. find the largest prime `p` such that:
   ```text
   p < x - prev
   ```
2. if such a prime exists, use:
   ```text
   x = x - p
   ```
3. otherwise keep `x` unchanged
4. if final `x <= prev`, return `false`

If we finish the scan, return `true`.

---

# Why This Greedy Choice Is Correct

Suppose at some position we choose a larger final value than necessary.

That can only make life harder for later positions, because later values must be strictly greater than the current one.

So among all feasible choices, the smallest valid current value is always at least as good as any larger one.

That means the greedy left-to-right strategy is optimal.

---

# Approach 1 — Greedy with Precomputed Prime List + Binary Search (Recommended)

## Idea

Since:

```text
nums[i] <= 1000
```

we can precompute all prime numbers up to `1000`.

For each element:

- compute threshold:
  ```text
  limit = nums[i] - prev
  ```
- find the largest prime `< limit`
- subtract it if possible
- otherwise keep the number

A binary search on the prime list makes the lookup clean.

---

## Java Code

```java
import java.util.*;

class Solution {
    public boolean primeSubOperation(int[] nums) {
        List<Integer> primes = getPrimes(1000);
        int prev = 0;

        for (int i = 0; i < nums.length; i++) {
            int x = nums[i];
            int limit = x - prev;

            int idx = largestPrimeLessThan(primes, limit);
            if (idx != -1) {
                x -= primes.get(idx);
            }

            if (x <= prev) {
                return false;
            }

            prev = x;
        }

        return true;
    }

    private List<Integer> getPrimes(int n) {
        boolean[] isPrime = new boolean[n + 1];
        Arrays.fill(isPrime, true);
        isPrime[0] = false;
        isPrime[1] = false;

        for (int p = 2; p * p <= n; p++) {
            if (!isPrime[p]) continue;
            for (int x = p * p; x <= n; x += p) {
                isPrime[x] = false;
            }
        }

        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= n; i++) {
            if (isPrime[i]) primes.add(i);
        }
        return primes;
    }

    private int largestPrimeLessThan(List<Integer> primes, int limit) {
        int lo = 0, hi = primes.size() - 1;
        int ans = -1;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (primes.get(mid) < limit) {
                ans = mid;
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }

        return ans;
    }
}
```

---

## Complexity

There are at most `1000` primes to consider indirectly, and each index does a binary search.

So:

```text
Time:  O(1000 log log 1000 + n log P)
Space: O(P)
```

where `P` is the number of primes up to `1000`.

Since `P` is tiny, this is extremely fast.

---

# Approach 2 — Greedy with Prime Sieve Array and Linear Backward Search

## Idea

Instead of building a prime list and using binary search, we can build a boolean sieve:

```text
isPrime[x]
```

Then for each index, search downward from:

```text
x - prev - 1
```

until we find the largest prime less than `x - prev`.

Because numbers are small (`<= 1000`), this is also completely fine.

---

## Java Code

```java
import java.util.*;

class Solution {
    public boolean primeSubOperation(int[] nums) {
        boolean[] isPrime = sieve(1000);
        int prev = 0;

        for (int i = 0; i < nums.length; i++) {
            int x = nums[i];
            int bestPrime = -1;

            for (int p = x - prev - 1; p >= 2; p--) {
                if (isPrime[p]) {
                    bestPrime = p;
                    break;
                }
            }

            if (bestPrime != -1) {
                x -= bestPrime;
            }

            if (x <= prev) {
                return false;
            }

            prev = x;
        }

        return true;
    }

    private boolean[] sieve(int n) {
        boolean[] isPrime = new boolean[n + 1];
        Arrays.fill(isPrime, true);
        isPrime[0] = false;
        isPrime[1] = false;

        for (int p = 2; p * p <= n; p++) {
            if (!isPrime[p]) continue;
            for (int x = p * p; x <= n; x += p) {
                isPrime[x] = false;
            }
        }

        return isPrime;
    }
}
```

---

## Complexity

In the worst case, for each element we may scan downward up to `1000`.

So:

```text
Time:  O(n * 1000)
Space: O(1000)
```

Given the constraints, this is still fine.

---

# Approach 3 — Right-to-Left Greedy

## Idea

Another valid way is to process from right to left.

At position `i`, ensure:

```text
nums[i] < nums[i + 1]
```

If not, subtract the smallest prime necessary to make it smaller.

This also works, but it is usually a bit less intuitive than the left-to-right “minimize current value” approach.

The left-to-right version is generally cleaner.

---

## High-Level Logic

For each `i` from right to left:

- we need final `nums[i] < nums[i+1]`
- if already true, maybe we can still reduce it, but that is unnecessary
- otherwise, find a prime `p < nums[i]` such that:
  ```text
  nums[i] - p < nums[i+1]
  ```
- if impossible, return false

This works, but the left-to-right approach is more natural because it greedily builds the valid sequence.

---

# Example Walkthrough

## Example 1

```text
nums = [4,9,6,10]
```

Start with:

```text
prev = 0
```

### Index 0

`x = 4`

Need final value `> 0`.

We want the largest prime `< 4 - 0 = 4`.

Largest such prime is `3`.

So:

```text
4 - 3 = 1
```

Set `prev = 1`.

### Index 1

`x = 9`

Need final value `> 1`.

We want the largest prime `< 9 - 1 = 8`.

Largest such prime is `7`.

So:

```text
9 - 7 = 2
```

Set `prev = 2`.

### Index 2

`x = 6`

Need final value `> 2`.

We want the largest prime `< 6 - 2 = 4`.

Largest such prime is `3`.

So:

```text
6 - 3 = 3
```

Set `prev = 3`.

### Index 3

`x = 10`

Need final value `> 3`.

We want the largest prime `< 10 - 3 = 7`.

Largest such prime is `5`.

So:

```text
10 - 5 = 5
```

Now final array can be:

```text
[1,2,3,5]
```

which is strictly increasing.

Answer:

```text
true
```

---

## Example 2

```text
nums = [6,8,11,12]
```

This array is already strictly increasing.

The greedy process may still reduce some numbers if helpful, but it is not required.

A valid strictly increasing outcome exists, so answer is:

```text
true
```

---

## Example 3

```text
nums = [5,8,3]
```

Try left-to-right.

### Index 0

`x = 5`, `prev = 0`

Largest prime `< 5` is `3`.

So final can become:

```text
5 - 3 = 2
```

Set `prev = 2`.

### Index 1

`x = 8`

Need final `> 2`.

Largest prime `< 8 - 2 = 6` is `5`.

So:

```text
8 - 5 = 3
```

Set `prev = 3`.

### Index 2

`x = 3`

Need final `> 3`.

But even without any subtraction, `3` is not greater than `3`.

Subtracting only makes it smaller.

So impossible.

Answer:

```text
false
```

---

# Important Correctness Argument

At each step, the only thing that matters from previous choices is the previous final value.

For the current number `x`, any feasible final value must satisfy:

```text
finalX > prev
```

Among all such feasible values, choosing the **smallest** one is always optimal, because it leaves the most room for later elements.

So the greedy strategy of subtracting the largest possible prime that still keeps the number above `prev` is correct.

If even the unchanged `x` is not greater than `prev`, then no valid choice exists and the answer must be false.

---

# Common Pitfalls

## 1. Forgetting that each index can be picked at most once

You cannot repeatedly subtract multiple primes from the same element.

---

## 2. Trying to make each value as large as possible

That is the opposite of what we want.
We want each value as small as possible while still valid.

---

## 3. Using a prime `p` with `p >= nums[i]`

The problem requires:

```text
p < nums[i]
```

strictly.

---

## 4. Thinking you must operate on every element

You may also choose not to change an element.

---

# Best Approach

## Recommended: left-to-right greedy with precomputed primes

This is the cleanest solution because:

- it follows the natural greedy principle
- constraints are small
- prime lookup is easy with a sieve
- correctness is straightforward to justify

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    public boolean primeSubOperation(int[] nums) {
        List<Integer> primes = getPrimes(1000);
        int prev = 0;

        for (int x : nums) {
            int limit = x - prev;
            int idx = largestPrimeLessThan(primes, limit);

            if (idx != -1) {
                x -= primes.get(idx);
            }

            if (x <= prev) {
                return false;
            }

            prev = x;
        }

        return true;
    }

    private List<Integer> getPrimes(int n) {
        boolean[] isPrime = new boolean[n + 1];
        Arrays.fill(isPrime, true);
        isPrime[0] = false;
        isPrime[1] = false;

        for (int p = 2; p * p <= n; p++) {
            if (!isPrime[p]) continue;
            for (int x = p * p; x <= n; x += p) {
                isPrime[x] = false;
            }
        }

        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= n; i++) {
            if (isPrime[i]) primes.add(i);
        }
        return primes;
    }

    private int largestPrimeLessThan(List<Integer> primes, int limit) {
        int lo = 0, hi = primes.size() - 1;
        int ans = -1;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (primes.get(mid) < limit) {
                ans = mid;
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }

        return ans;
    }
}
```

---

# Complexity Summary

```text
Time:  O(n log P)
Space: O(P)
```

where `P` is the number of primes up to `1000`.

Since `P` is tiny, this is essentially linear in `n`.

---

# Final Takeaway

The whole problem becomes simple once you realize the greedy target:

> Make each number as small as possible, but still strictly greater than the previous final value.

That directly leads to subtracting the largest prime allowed at each step.
