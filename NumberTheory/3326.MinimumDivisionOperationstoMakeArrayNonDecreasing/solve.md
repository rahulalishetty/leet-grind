# 3326. Minimum Division Operations to Make Array Non Decreasing

## Problem Restatement

We are given an integer array `nums`.

In one operation, we may choose any element `x = nums[i]` and replace it with:

```text
x / (greatest proper divisor of x)
```

We want the minimum number of operations needed to make the array **non-decreasing**.

If impossible, return `-1`.

---

## First Critical Observation

This operation is much more restrictive than it first appears.

Let `x > 1`.

Suppose `d` is the **greatest proper divisor** of `x`.

Then:

```text
x = d * p
```

where `p` is the **smallest prime factor** of `x`.

Why?

Because the greatest proper divisor is obtained by dividing `x` by its smallest prime factor.

So after one operation:

```text
x / d = p
```

That means:

> Every operation replaces a number with its **smallest prime factor**.

This is the decisive simplification.

---

## What does that imply?

- If `x` is prime, then its greatest proper divisor is `1`
- so:

```text
x / 1 = x
```

So **prime numbers do not change**.

- If `x = 1`, it also stays `1` effectively
- If `x` is composite, one operation turns it into its smallest prime factor
- After that, it becomes prime, so it cannot be reduced any further

So each element has only two possible useful states:

1. its original value
2. its smallest prime factor

That means each element can be changed **at most once in any meaningful way**.

This turns the problem into a greedy right-to-left decision process.

---

# Core Greedy Idea

To make the array non-decreasing:

```text
nums[i] <= nums[i + 1]
```

must hold for all `i`.

If we process from **right to left**, then when we are at index `i`, the value at `i + 1` is already finalized.

So for `nums[i]`, there are only two possibilities:

- keep it as it is
- replace it with its smallest prime factor

If `nums[i] <= nums[i+1]`, do nothing.

Otherwise, we must try to reduce `nums[i]` to its smallest prime factor.

- if that reduced value is still greater than `nums[i+1]`, impossible
- otherwise, do one operation

This greedy is optimal because:

- changing a number only makes it smaller
- when scanning from right to left, the only way to satisfy the current constraint is to locally fix the left element
- delaying or changing something else cannot help, since elements to the right are already fixed and left elements cannot increase

---

# Approach 1 — Naive Factor Search Per Element

## Intuition

When an element is too large compared with the next one, compute its smallest prime factor by trial division.

Then replace it if possible.

This is straightforward and already efficient enough because:

```text
nums[i] <= 10^6
```

So trial division per changed element is manageable.

---

## Algorithm

1. Traverse array from right to left
2. If `nums[i] <= nums[i+1]`, continue
3. Otherwise compute `spf = smallestPrimeFactor(nums[i])`
4. Replace `nums[i] = spf`
5. If now `nums[i] > nums[i+1]`, return `-1`
6. Count the operation
7. Return the total count

---

## Java Code

```java
class Solution {
    public int minOperations(int[] nums) {
        int n = nums.length;
        int operations = 0;

        for (int i = n - 2; i >= 0; i--) {
            if (nums[i] <= nums[i + 1]) {
                continue;
            }

            nums[i] = smallestPrimeFactor(nums[i]);
            operations++;

            if (nums[i] > nums[i + 1]) {
                return -1;
            }
        }

        return operations;
    }

    private int smallestPrimeFactor(int x) {
        if (x <= 1) return x;
        for (int d = 2; d * d <= x; d++) {
            if (x % d == 0) {
                return d;
            }
        }
        return x; // prime
    }
}
```

---

## Complexity Analysis

Let:

- `n = nums.length`
- `M = max(nums)`

### Time Complexity

We scan the array once:

```text
O(n)
```

For each problematic element, we may do trial division up to `sqrt(M)`:

```text
O(sqrt(M))
```

So worst-case total:

```text
O(n * sqrt(M))
```

With `M <= 10^6`, this is acceptable.

### Space Complexity

```text
O(1)
```

---

# Approach 2 — Precompute Smallest Prime Factors with Sieve

## Intuition

Approach 1 recomputes smallest prime factors repeatedly.

Since:

```text
nums[i] <= 10^6
```

we can precompute the smallest prime factor (SPF) for every number from `1` to `10^6` using a sieve.

Then each reduction becomes `O(1)`.

This is the cleanest efficient approach.

---

## SPF Sieve Idea

For each number `x`, store:

```text
spf[x] = smallest prime factor of x
```

Examples:

```text
spf[2] = 2
spf[3] = 3
spf[4] = 2
spf[6] = 2
spf[15] = 3
spf[25] = 5
```

Then when `nums[i]` must be reduced, we directly set:

```text
nums[i] = spf[nums[i]]
```

---

## Java Code

```java
class Solution {
    public int minOperations(int[] nums) {
        int maxVal = 0;
        for (int x : nums) {
            maxVal = Math.max(maxVal, x);
        }

        int[] spf = buildSPF(maxVal);
        int operations = 0;

        for (int i = nums.length - 2; i >= 0; i--) {
            if (nums[i] <= nums[i + 1]) {
                continue;
            }

            nums[i] = spf[nums[i]];
            operations++;

            if (nums[i] > nums[i + 1]) {
                return -1;
            }
        }

        return operations;
    }

    private int[] buildSPF(int n) {
        int[] spf = new int[n + 1];

        for (int i = 0; i <= n; i++) {
            spf[i] = i;
        }

        for (int i = 2; i * i <= n; i++) {
            if (spf[i] == i) { // i is prime
                for (int j = i * i; j <= n; j += i) {
                    if (spf[j] == j) {
                        spf[j] = i;
                    }
                }
            }
        }

        return spf;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Building SPF sieve:

```text
O(M log log M)
```

where `M = max(nums)`.

Then one right-to-left pass:

```text
O(n)
```

Overall:

```text
O(M log log M + n)
```

### Space Complexity

SPF array:

```text
O(M)
```

---

# Approach 3 — Same Greedy, but Explain It as a Two-State DP View

## Intuition

This problem can also be seen as a tiny-state DP, though greedy is sufficient.

Each element has only two meaningful candidate values:

- original value
- smallest prime factor

So conceptually, for each position, we want to choose one of these two states so that the final array is non-decreasing and the number of reductions is minimized.

A general DP would try both states per element.

But because:

- reducing only decreases
- each element has only one possible reduced state
- and right-side constraints dominate

the DP collapses into a simple greedy choice:

- if current value already fits, never reduce it
- otherwise reduce it if that makes it fit
- else impossible

So this “DP perspective” helps justify why greedy works.

---

## Java Code

```java
class Solution {
    public int minOperations(int[] nums) {
        int maxVal = 0;
        for (int x : nums) maxVal = Math.max(maxVal, x);

        int[] spf = buildSPF(maxVal);
        int ans = 0;

        for (int i = nums.length - 2; i >= 0; i--) {
            if (nums[i] <= nums[i + 1]) continue;

            int reduced = spf[nums[i]];
            if (reduced > nums[i + 1]) return -1;

            nums[i] = reduced;
            ans++;
        }

        return ans;
    }

    private int[] buildSPF(int n) {
        int[] spf = new int[n + 1];
        for (int i = 0; i <= n; i++) spf[i] = i;

        for (int i = 2; i * i <= n; i++) {
            if (spf[i] == i) {
                for (int j = i * i; j <= n; j += i) {
                    if (spf[j] == j) {
                        spf[j] = i;
                    }
                }
            }
        }
        return spf;
    }
}
```

This is the same optimal implementation, just framed differently.

---

# Why Right-to-Left Greedy Is Correct

## Claim 1

One useful operation on `x > 1` changes `x` into its smallest prime factor.

### Proof

Let `p` be the smallest prime factor of `x`.

Then:

```text
x = p * q
```

for some integer `q`.

Since `p` is the smallest prime factor, `q = x / p` is the largest proper divisor of `x`.

So the operation gives:

```text
x / q = p
```

Thus after one operation, `x` becomes its smallest prime factor.

Proved.

---

## Claim 2

No element needs more than one meaningful operation.

### Proof

After one operation, a composite number becomes its smallest prime factor, which is prime.

A prime number divided by its greatest proper divisor (`1`) remains unchanged.

So once reduced, an element cannot become smaller through further operations.

Proved.

---

## Claim 3

If `nums[i] > nums[i+1]`, the only chance to fix position `i` is to reduce `nums[i]`.

### Proof

When scanning from right to left, `nums[i+1]` is already finalized.

Elements left of `i` do not affect whether:

```text
nums[i] <= nums[i+1]
```

holds.

We cannot increase `nums[i+1]`; operations only reduce values or keep them unchanged.

So the only possible local fix is to reduce `nums[i]`.

Proved.

---

## Claim 4

If the reduced value of `nums[i]` is still greater than `nums[i+1]`, the answer is impossible.

### Proof

By Claim 2, `nums[i]` has no smaller reachable value beyond its reduced form.

So if even that value does not satisfy:

```text
nums[i] <= nums[i+1]
```

then no sequence of operations can make the array non-decreasing.

Proved.

---

## Therefore

The greedy algorithm is optimal:

- keep `nums[i]` unchanged whenever possible
- otherwise reduce it once
- if that still fails, return `-1`

---

# Worked Examples

## Example 1

```text
nums = [25, 7]
```

Start from right:

- `7` is fixed
- `25 > 7`, so reduce `25`

Smallest prime factor of `25` is `5`

Array becomes:

```text
[5, 7]
```

Now non-decreasing.

Operations:

```text
1
```

---

## Example 2

```text
nums = [7, 7, 6]
```

Start from right:

- compare `7` and `6`
- `7 > 6`, so try reducing `7`

But `7` is prime, so its smallest prime factor is `7` itself.

It stays:

```text
7
```

Still:

```text
7 > 6
```

Impossible.

Answer:

```text
-1
```

---

## Example 3

```text
nums = [1, 1, 1, 1]
```

Already non-decreasing.

Operations:

```text
0
```

---

# Additional Dry Run

## Example

```text
nums = [10, 6, 15]
```

Right to left:

- compare `6` and `15`: okay
- compare `10` and `6`: not okay

Reduce `10`:

- smallest prime factor of `10` is `2`

Array becomes:

```text
[2, 6, 15]
```

Now non-decreasing.

Answer:

```text
1
```

---

# Edge Cases

## 1. Prime number too large for next value

Example:

```text
[11, 5]
```

Since `11` is prime, reducing it keeps it `11`.

Still:

```text
11 > 5
```

So answer is `-1`.

---

## 2. Array already non-decreasing

Example:

```text
[2, 3, 3, 10]
```

No operations needed.

---

## 3. Value `1`

`1` cannot be usefully reduced and is already the smallest possible value.

If a `1` appears before a smaller number, that cannot happen since nothing is smaller than `1`.

So `1` is never the cause of a failure, only a harmless value.

---

# Comparison of Approaches

## Approach 1 — Trial division each time

Pros:

- simple
- no preprocessing
- easy to derive

Cons:

- repeated factor computation

Good when:

- you want the most direct implementation

---

## Approach 2 — SPF sieve

Pros:

- fastest clean solution
- `O(1)` reduction lookup
- best for the given constraints

Cons:

- uses extra memory

This is the recommended approach.

---

## Approach 3 — DP interpretation

Pros:

- gives deeper intuition
- explains why greedy is enough

Cons:

- same implementation as greedy in the end

Good when:

- you want a stronger interview explanation

---

# Final Recommended Java Solution

```java
class Solution {
    public int minOperations(int[] nums) {
        int maxVal = 0;
        for (int x : nums) {
            maxVal = Math.max(maxVal, x);
        }

        int[] spf = new int[maxVal + 1];
        for (int i = 0; i <= maxVal; i++) {
            spf[i] = i;
        }

        for (int i = 2; i * i <= maxVal; i++) {
            if (spf[i] == i) {
                for (int j = i * i; j <= maxVal; j += i) {
                    if (spf[j] == j) {
                        spf[j] = i;
                    }
                }
            }
        }

        int operations = 0;

        for (int i = nums.length - 2; i >= 0; i--) {
            if (nums[i] <= nums[i + 1]) {
                continue;
            }

            nums[i] = spf[nums[i]];
            operations++;

            if (nums[i] > nums[i + 1]) {
                return -1;
            }
        }

        return operations;
    }
}
```

---

# Complexity Summary

Let:

- `n = nums.length`
- `M = max(nums)`

## Approach 1

```text
Time:  O(n * sqrt(M))
Space: O(1)
```

## Approach 2

```text
Time:  O(M log log M + n)
Space: O(M)
```

## Approach 3

```text
Time:  O(M log log M + n)
Space: O(M)
```

---

# Final Takeaway

The hidden trick is:

> Dividing a number by its greatest proper divisor turns it into its **smallest prime factor**.

That means every element can only be meaningfully changed once.

After that, the whole problem becomes a clean right-to-left greedy check:

- if current element already fits, keep it
- otherwise reduce it once
- if it still does not fit, impossible
