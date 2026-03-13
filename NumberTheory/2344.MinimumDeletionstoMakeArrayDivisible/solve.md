# 2344. Minimum Deletions to Make Array Divisible — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int minOperations(int[] nums, int[] numsDivide) {

    }
}
```

---

# Problem Restatement

We are given two arrays:

- `nums`
- `numsDivide`

We may delete any number of elements from `nums`.

We want the **smallest remaining element in `nums`** to divide **every element of `numsDivide`**.

We must return the **minimum number of deletions** needed to make that possible.

If it is impossible, return:

```text
-1
```

---

# Core Insight

Let the smallest remaining element be `x`.

For `x` to divide every element of `numsDivide`, it must divide their **greatest common divisor**.

Why?

Because:

```text
x divides every value in numsDivide
```

if and only if:

```text
x divides gcd(numsDivide[0], numsDivide[1], ..., numsDivide[m-1])
```

So the problem reduces to:

1. Compute:

```text
g = gcd(all elements of numsDivide)
```

2. Find the smallest value in `nums` that divides `g`
3. Delete all elements smaller than that value
4. Return how many deletions that requires

If no value in `nums` divides `g`, answer is `-1`.

---

# Why GCD of numsDivide Solves It

Suppose we need some value `x` to divide all numbers in `numsDivide`.

Then `x` must divide:

```text
g = gcd(numsDivide)
```

Conversely, if `x` divides `g`, then since `g` divides every number in `numsDivide`, `x` divides every number in `numsDivide`.

So instead of checking divisibility against every element repeatedly, we only need to check divisibility against one number:

```text
g
```

That is the key simplification.

---

# Approach 1 — Sort nums and Find First Divisor of gcd(numsDivide) (Recommended)

## Idea

1. Compute:

```text
g = gcd(numsDivide)
```

2. Sort `nums`
3. Scan from smallest to largest
4. The first value `nums[i]` such that:

```text
g % nums[i] == 0
```

is the best possible smallest remaining value

Why is it best?

Because sorting guarantees that all earlier values are smaller and must be deleted.
So the first valid divisor gives the minimum deletions.

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    public int minOperations(int[] nums, int[] numsDivide) {
        int g = numsDivide[0];
        for (int x : numsDivide) {
            g = gcd(g, x);
        }

        Arrays.sort(nums);

        for (int i = 0; i < nums.length; i++) {
            if (g % nums[i] == 0) {
                return i;
            }
        }

        return -1;
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

After sorting, suppose `nums[i]` is the first value that divides `g`.

Then:

- every value before index `i` is smaller, so if any remained, `nums[i]` would not be the smallest
- therefore all those `i` values must be deleted
- `nums[i]` can remain as the smallest value
- since it divides `g`, it divides all of `numsDivide`

Thus `i` is exactly the minimum number of deletions.

---

## Complexity

Let:

- `n = nums.length`
- `m = numsDivide.length`

Then:

- computing gcd of `numsDivide`: `O(m log V)`
- sorting `nums`: `O(n log n)`
- scanning sorted array: `O(n)`

So:

```text
Time:  O(n log n + m log V)
Space: O(1) or O(log n) depending on sort implementation
```

This is the standard best solution.

---

# Approach 2 — Frequency Map / TreeMap After gcd Compression

## Idea

Instead of sorting the entire array, we can count frequencies of values in `nums`, then iterate through distinct values in ascending order.

For each distinct value `x`:

- if `x` divides `g`, return how many elements are strictly smaller than `x`
- otherwise add its frequency into the deletion count and continue

This avoids sorting all elements directly, though internally a sorted structure is still needed.

---

## Java Code

```java
import java.util.Map;
import java.util.TreeMap;

class Solution {
    public int minOperations(int[] nums, int[] numsDivide) {
        int g = numsDivide[0];
        for (int x : numsDivide) {
            g = gcd(g, x);
        }

        TreeMap<Integer, Integer> freq = new TreeMap<>();
        for (int x : nums) {
            freq.put(x, freq.getOrDefault(x, 0) + 1);
        }

        int deleted = 0;
        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            int val = entry.getKey();
            int count = entry.getValue();

            if (g % val == 0) {
                return deleted;
            }

            deleted += count;
        }

        return -1;
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

If there are `d` distinct values in `nums`:

- building TreeMap: `O(n log d)`
- iterating distinct values: `O(d)`

Overall:

```text
Time:  O(n log d + m log V)
Space: O(d)
```

This is also good, especially when there are many duplicates.

---

# Approach 3 — Count Frequencies + Enumerate Divisors of gcd(numsDivide)

## Idea

Since the answer must be some value in `nums` that divides:

```text
g = gcd(numsDivide)
```

we could enumerate all divisors of `g`, then check which divisors appear in `nums`.

Among those present in `nums`, choose the smallest one, and count how many elements in `nums` are smaller than it.

This is mathematically valid, but not usually simpler than sorting.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minOperations(int[] nums, int[] numsDivide) {
        int g = numsDivide[0];
        for (int x : numsDivide) {
            g = gcd(g, x);
        }

        Map<Integer, Integer> freq = new HashMap<>();
        for (int x : nums) {
            freq.put(x, freq.getOrDefault(x, 0) + 1);
        }

        int best = Integer.MAX_VALUE;
        for (int d : getDivisors(g)) {
            if (freq.containsKey(d)) {
                best = Math.min(best, d);
            }
        }

        if (best == Integer.MAX_VALUE) {
            return -1;
        }

        int deleted = 0;
        for (int x : nums) {
            if (x < best) {
                deleted++;
            }
        }

        return deleted;
    }

    private List<Integer> getDivisors(int x) {
        List<Integer> res = new ArrayList<>();
        for (int i = 1; i * (long) i <= x; i++) {
            if (x % i == 0) {
                res.add(i);
                if (i != x / i) {
                    res.add(x / i);
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

## Why this is less preferred

This works, but:

- divisor enumeration is not as direct as just sorting `nums`
- you still need a second pass to count how many values are smaller than the chosen divisor
- the sorted solution is cleaner

Still, it is a valid alternate viewpoint.

---

# Approach 4 — Brute Force Candidate Check (Too Slow)

## Idea

For every possible choice of smallest remaining value in `nums`:

1. check whether it divides every element in `numsDivide`
2. count how many deletions are needed

This is correct but too slow in its naive form.

---

## Java Code

```java
class Solution {
    public int minOperations(int[] nums, int[] numsDivide) {
        int ans = Integer.MAX_VALUE;

        for (int i = 0; i < nums.length; i++) {
            int candidate = nums[i];

            boolean ok = true;
            for (int x : numsDivide) {
                if (x % candidate != 0) {
                    ok = false;
                    break;
                }
            }

            if (!ok) continue;

            int deletions = 0;
            for (int x : nums) {
                if (x < candidate) {
                    deletions++;
                }
            }

            ans = Math.min(ans, deletions);
        }

        return ans == Integer.MAX_VALUE ? -1 : ans;
    }
}
```

---

## Why it fails

This can take:

```text
O(n * m + n^2)
```

which is too large for:

```text
n, m <= 10^5
```

So this is only useful conceptually.

---

# Detailed Walkthrough

## Example 1

```text
nums = [2,3,2,4,3]
numsDivide = [9,6,9,3,15]
```

First compute:

```text
gcd(9,6,9,3,15) = 3
```

Now sort `nums`:

```text
[2,2,3,3,4]
```

Scan from left to right:

- `2` does not divide `3`
- next `2` does not divide `3`
- `3` divides `3`

So the first valid value is at index `2`.

That means we must delete the first two elements.

Answer:

```text
2
```

---

## Example 2

```text
nums = [4,3,6]
numsDivide = [8,2,6,10]
```

Compute:

```text
gcd(8,2,6,10) = 2
```

Sort `nums`:

```text
[3,4,6]
```

Check:

- `3` does not divide `2`
- `4` does not divide `2`
- `6` does not divide `2`

No valid value exists.

Answer:

```text
-1
```

---

# Important Correctness Argument

Why does the answer depend on the first divisor of `g` in sorted `nums`?

Because the smallest remaining value must divide every number in `numsDivide`, so it must divide `g`.

If we sort `nums`, then any valid smallest remaining value must appear somewhere in sorted order.

To make it the smallest remaining value, all earlier elements must be deleted.

Therefore the first valid divisor gives the minimum deletions.

This makes the sorted scan both necessary and sufficient.

---

# Common Pitfalls

## 1. Checking divisibility against every number in numsDivide repeatedly

This is too slow and unnecessary.

Reduce the whole condition to:

```text
divides gcd(numsDivide)
```

---

## 2. Forgetting that the chosen value must be the smallest remaining value

It is not enough for some number in `nums` to divide all of `numsDivide`.
All smaller remaining values must be removed.

---

## 3. Missing duplicates

Duplicates are harmless. Sorting handles them naturally.

---

## 4. Using lcm or factorization unnecessarily

The gcd of `numsDivide` is the cleanest and most direct reduction.

---

# Best Approach

## Recommended: gcd(numsDivide) + sort nums

This is the best solution because:

- it reduces the divisibility target to a single number
- sorting lets us directly find the minimum deletions
- it is simple, efficient, and easy to prove correct

---

# Final Recommended Java Solution

```java
import java.util.Arrays;

class Solution {
    public int minOperations(int[] nums, int[] numsDivide) {
        int g = numsDivide[0];
        for (int x : numsDivide) {
            g = gcd(g, x);
        }

        Arrays.sort(nums);

        for (int i = 0; i < nums.length; i++) {
            if (g % nums[i] == 0) {
                return i;
            }
        }

        return -1;
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

Let:

- `n = nums.length`
- `m = numsDivide.length`

Then:

```text
Time:  O(n log n + m log V)
Space: O(1) or O(log n)
```

depending on sort implementation.

This is efficient for the constraints.

---

# Final Takeaway

The key observation is:

```text
smallest remaining value must divide every element of numsDivide
```

which is equivalent to:

```text
smallest remaining value must divide gcd(numsDivide)
```

Once that is seen, the problem becomes very simple:

1. compute gcd of `numsDivide`
2. sort `nums`
3. find the first value that divides that gcd
4. its index is the minimum deletions
