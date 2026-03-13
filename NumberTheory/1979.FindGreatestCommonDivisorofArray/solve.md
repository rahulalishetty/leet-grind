# 1979. Find Greatest Common Divisor of Array

## Problem Restatement

We are given an integer array `nums`.

We need to return the **greatest common divisor (GCD)** of:

- the **smallest element** in the array
- the **largest element** in the array

So the problem is really:

1. find `min(nums)`
2. find `max(nums)`
3. compute `gcd(min, max)`

---

## Key Observation

The rest of the elements in the array do **not** matter for the final answer.

Only these two values matter:

```text
smallest number
largest number
```

Once we have them, the answer is simply:

```text
gcd(minValue, maxValue)
```

This makes the problem very direct.

---

# Approach 1 — Sort the Array, Then Compute GCD

## Intuition

If we sort the array:

- the first element becomes the minimum
- the last element becomes the maximum

Then we compute the GCD of those two values.

This is straightforward and easy to understand, though sorting is more work than necessary.

---

## Algorithm

1. Sort the array
2. Let:
   - `minValue = nums[0]`
   - `maxValue = nums[nums.length - 1]`
3. Return `gcd(minValue, maxValue)`

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    public int findGCD(int[] nums) {
        Arrays.sort(nums);
        return gcd(nums[0], nums[nums.length - 1]);
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Sorting takes:

```text
O(n log n)
```

GCD computation takes:

```text
O(log(min(a, b)))
```

So overall:

```text
O(n log n)
```

### Space Complexity

Depends on sorting implementation, but conceptually:

```text
O(1) or O(log n)
```

depending on the language/runtime.

---

# Approach 2 — Single Scan for Min and Max + Euclidean Algorithm

## Intuition

Sorting is unnecessary because we only care about the smallest and largest values.

So we can scan the array once and track:

- current minimum
- current maximum

Then compute their GCD.

This is the most efficient and clean solution.

---

## Algorithm

1. Initialize:
   - `minValue = nums[0]`
   - `maxValue = nums[0]`
2. Traverse the array
3. Update min and max
4. Return `gcd(minValue, maxValue)`

---

## Java Code

```java
class Solution {
    public int findGCD(int[] nums) {
        int minValue = nums[0];
        int maxValue = nums[0];

        for (int x : nums) {
            minValue = Math.min(minValue, x);
            maxValue = Math.max(maxValue, x);
        }

        return gcd(minValue, maxValue);
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Scanning array:

```text
O(n)
```

GCD via Euclidean algorithm:

```text
O(log(min(a, b)))
```

Overall:

```text
O(n)
```

### Space Complexity

```text
O(1)
```

---

# Approach 3 — Single Scan + Recursive GCD

## Intuition

This is the same optimal idea as Approach 2, but using the recursive Euclidean algorithm for GCD.

The Euclidean recurrence is:

```text
gcd(a, b) = gcd(b, a % b)
```

until `b == 0`.

This is mathematically elegant and often used in interviews.

---

## Java Code

```java
class Solution {
    public int findGCD(int[] nums) {
        int minValue = nums[0];
        int maxValue = nums[0];

        for (int x : nums) {
            if (x < minValue) minValue = x;
            if (x > maxValue) maxValue = x;
        }

        return gcd(minValue, maxValue);
    }

    private int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }
}
```

---

## Complexity Analysis

### Time Complexity

Array scan:

```text
O(n)
```

Recursive GCD:

```text
O(log(min(a, b)))
```

Overall:

```text
O(n)
```

### Space Complexity

Recursive call stack for GCD:

```text
O(log(min(a, b)))
```

---

# Approach 4 — Brute Force Common Divisor Search

## Intuition

Another way to think about GCD is:

> The GCD is the largest number that divides both values.

So after finding min and max, we could simply try all possible divisors from `min(minValue, maxValue)` down to `1`, and return the first divisor that divides both.

This is conceptually simple, but less efficient than Euclid’s algorithm.

Still, for the given small constraints (`nums[i] <= 1000`), it works fine.

---

## Algorithm

1. Find min and max
2. Let `limit = min(minValue, maxValue)`
3. Loop `d` from `limit` down to `1`
4. Return the first `d` such that:
   - `minValue % d == 0`
   - `maxValue % d == 0`

---

## Java Code

```java
class Solution {
    public int findGCD(int[] nums) {
        int minValue = nums[0];
        int maxValue = nums[0];

        for (int x : nums) {
            minValue = Math.min(minValue, x);
            maxValue = Math.max(maxValue, x);
        }

        for (int d = Math.min(minValue, maxValue); d >= 1; d--) {
            if (minValue % d == 0 && maxValue % d == 0) {
                return d;
            }
        }

        return 1;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Finding min and max:

```text
O(n)
```

Brute-force divisor search:

```text
O(min(minValue, maxValue))
```

So total:

```text
O(n + minValue)
```

Since values are at most `1000`, this is acceptable, but still not ideal.

### Space Complexity

```text
O(1)
```

---

# Why the Euclidean Algorithm Works

The Euclidean algorithm is based on the fact that:

```text
gcd(a, b) = gcd(b, a % b)
```

Why is this true?

If a number divides both `a` and `b`, then it also divides:

```text
a - k*b
```

for any integer `k`.

In particular, it divides the remainder:

```text
a % b
```

So the set of common divisors of `(a, b)` is the same as the set of common divisors of `(b, a % b)`.

Eventually the remainder becomes `0`, and the last non-zero value is the GCD.

---

# Worked Examples

## Example 1

```text
nums = [2,5,6,9,10]
```

Minimum:

```text
2
```

Maximum:

```text
10
```

Now compute:

```text
gcd(2, 10) = 2
```

Answer:

```text
2
```

---

## Example 2

```text
nums = [7,5,6,8,3]
```

Minimum:

```text
3
```

Maximum:

```text
8
```

Compute:

```text
gcd(3, 8) = 1
```

Answer:

```text
1
```

---

## Example 3

```text
nums = [3,3]
```

Minimum:

```text
3
```

Maximum:

```text
3
```

Compute:

```text
gcd(3, 3) = 3
```

Answer:

```text
3
```

---

# Edge Cases

## 1. Array of length 2

Example:

```text
[8, 12]
```

Then the min and max are just those two values, so answer is:

```text
gcd(8, 12) = 4
```

---

## 2. All elements equal

Example:

```text
[6, 6, 6]
```

Then both min and max are `6`.

So answer is:

```text
gcd(6, 6) = 6
```

---

## 3. GCD is 1

Example:

```text
[3, 10]
```

Then:

```text
gcd(3, 10) = 1
```

This is valid and common.

---

# Correctness Argument

## Claim

The answer is the GCD of the smallest and largest elements of the array.

### Proof

This is exactly what the problem statement asks us to compute.

So any correct solution must:

1. identify the smallest element
2. identify the largest element
3. compute their greatest common divisor

No other array elements influence the final answer.

Thus the problem is fully solved by computing:

```text
gcd(min(nums), max(nums))
```

Proved.

---

# Comparison of Approaches

## Approach 1 — Sorting

Pros:

- very easy to understand

Cons:

- extra work because sorting is unnecessary

---

## Approach 2 — One scan + iterative GCD

Pros:

- optimal
- clean
- best practical solution

Cons:

- none

This is the recommended approach.

---

## Approach 3 — One scan + recursive GCD

Pros:

- elegant mathematical form
- also optimal in time

Cons:

- recursive stack overhead

---

## Approach 4 — Brute-force divisor search

Pros:

- simple idea
- works because values are small

Cons:

- less efficient than Euclid’s algorithm

---

# Final Recommended Java Solution

```java
class Solution {
    public int findGCD(int[] nums) {
        int minValue = nums[0];
        int maxValue = nums[0];

        for (int x : nums) {
            if (x < minValue) minValue = x;
            if (x > maxValue) maxValue = x;
        }

        while (maxValue != 0) {
            int temp = maxValue;
            maxValue = minValue % maxValue;
            minValue = temp;
        }

        return minValue;
    }
}
```

---

# Complexity Summary

## Approach 1

```text
Time:  O(n log n)
Space: depends on sorting
```

## Approach 2

```text
Time:  O(n)
Space: O(1)
```

## Approach 3

```text
Time:  O(n)
Space: O(log(min(a,b)))
```

## Approach 4

```text
Time:  O(n + minValue)
Space: O(1)
```

---

# Final Takeaway

This problem is simpler than it first looks.

You do **not** need the GCD of the whole array.

You only need:

- the smallest number
- the largest number

Then compute their GCD.

That makes the best solution:

1. one pass to find min and max
2. Euclidean algorithm for GCD
