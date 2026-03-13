# Prime Subtraction Operation – Greedy Approaches

## Overview

We are given an integer array `nums`. For each element in `nums`, we can subtract any **prime number strictly less than the current element at most once**.

Goal: determine whether it is possible to transform the array into a **strictly increasing sequence**.

### Key Idea

For each element, subtract the **largest possible prime** while keeping the value **greater than the previous element**.

This greedy idea minimizes each element, giving maximum flexibility for future elements.

Example:

```
nums = [5, 5, 4]
```

Option 1 – subtract small primes:

```
[5,5,4] → [3,5,4] → [3,3,4]  (not strictly increasing)
```

Option 2 – subtract largest valid primes:

```
[5,5,4] → [2,5,4] → [2,3,4]  (valid)
```

Thus we **always try subtracting the largest possible prime**.

---

# Approach 1 — Brute Force

## Intuition

For each element `nums[i]`, we find the **largest prime `p`** such that:

```
nums[i] - p > nums[i-1]
```

Meaning:

```
p < nums[i] - nums[i-1]
```

Steps:

1. Compute the allowable bound.
2. Search downward for the largest prime smaller than that bound.
3. Subtract that prime.

---

## Algorithm

### Main Function

`primeSubOperation(nums)`

1. Iterate through each element.
2. Compute `bound`
   - if `i == 0` → `bound = nums[0]`
   - else → `bound = nums[i] - nums[i-1]`
3. If `bound <= 0`, return `false`.
4. Find the largest prime `< bound`.
5. Subtract that prime.
6. Continue.

Return `true` if all elements succeed.

### Helper Function

`checkPrime(x)`

A number is prime if it has **no divisors up to √x**.

---

## Implementation (Java)

```java
public class Solution {

    public boolean checkPrime(int x) {
        for (int i = 2; i <= Math.sqrt(x); i++) {
            if (x % i == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean primeSubOperation(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            int bound;

            if (i == 0) {
                bound = nums[0];
            } else {
                bound = nums[i] - nums[i - 1];
            }

            if (bound <= 0) {
                return false;
            }

            int largestPrime = 0;

            for (int j = bound - 1; j >= 2; j--) {
                if (checkPrime(j)) {
                    largestPrime = j;
                    break;
                }
            }

            nums[i] -= largestPrime;
        }

        return true;
    }
}
```

---

## Complexity

Let:

- `n` = array length
- `m` = max value in nums

### Time

```
O(n * m * sqrt(m))
```

### Space

```
O(1)
```

---

# Approach 2 — Precomputing Previous Primes

## Intuition

Repeatedly checking primes is expensive.

Instead, build an array:

```
previousPrime[i] = largest prime ≤ i
```

Example:

| i   | previousPrime |
| --- | ------------- |
| 2   | 2             |
| 3   | 3             |
| 4   | 3             |
| 5   | 5             |
| 6   | 5             |

Then we can instantly retrieve the needed prime.

---

## Algorithm

1. Find `maxElement`.
2. Build `previousPrime` array.
3. For each element:
   - compute `bound`
   - lookup `previousPrime[bound-1]`
   - subtract it.

---

## Implementation

```java
class Solution {

    public boolean isPrime(int n) {
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean primeSubOperation(int[] nums) {

        int maxElement = Integer.MIN_VALUE;

        for (int num : nums) {
            maxElement = Math.max(maxElement, num);
        }

        int[] previousPrime = new int[maxElement + 1];

        for (int i = 2; i <= maxElement; i++) {

            if (isPrime(i)) {
                previousPrime[i] = i;
            } else {
                previousPrime[i] = previousPrime[i - 1];
            }

        }

        for (int i = 0; i < nums.length; i++) {

            int bound;

            if (i == 0) {
                bound = nums[0];
            } else {
                bound = nums[i] - nums[i - 1];
            }

            if (bound <= 0) {
                return false;
            }

            int largestPrime = previousPrime[bound - 1];

            nums[i] -= largestPrime;
        }

        return true;
    }
}
```

---

## Complexity

### Time

```
O(n + m * sqrt(m))
```

### Space

```
O(m)
```

---

# Approach 3 — Sieve of Eratosthenes + Two Pointers

## Intuition

Instead of checking primes individually, we use the **Sieve of Eratosthenes** to generate all primes efficiently.

Sieve idea:

1. Start with numbers marked as prime.
2. For each prime `p`, mark all multiples as non‑prime.
3. Remaining numbers are primes.

Complexity:

```
O(m log log m)
```

---

## Two Pointer Idea

We assign the smallest possible strictly increasing sequence:

```
currValue = 1
```

For each index `i`, check:

```
difference = nums[i] - currValue
```

If:

- difference == 0
- difference is prime

Then assignment works.

Otherwise increase `currValue` and try again.

If `difference < 0`, impossible.

---

## Implementation

```java
class Solution {

    public boolean primeSubOperation(int[] nums) {

        int maxElement = getMaxElement(nums);

        boolean[] sieve = new boolean[maxElement + 1];

        fill(sieve, true);

        sieve[1] = false;

        for (int i = 2; i <= Math.sqrt(maxElement + 1); i++) {

            if (sieve[i]) {

                for (int j = i * i; j <= maxElement; j += i) {
                    sieve[j] = false;
                }

            }
        }

        int currValue = 1;
        int i = 0;

        while (i < nums.length) {

            int difference = nums[i] - currValue;

            if (difference < 0) {
                return false;
            }

            if (sieve[difference] || difference == 0) {
                i++;
                currValue++;
            } else {
                currValue++;
            }

        }

        return true;
    }

    private int getMaxElement(int[] nums) {

        int max = -1;

        for (int num : nums) {
            if (num > max) {
                max = num;
            }
        }

        return max;
    }

    private void fill(boolean[] arr, boolean value) {

        for (int i = 0; i < arr.length; i++) {
            arr[i] = value;
        }

    }
}
```

---

## Complexity

### Time

```
O(n + m log log m)
```

### Space

```
O(m)
```

---

# Summary

| Approach       | Method                     | Time Complexity    | Space |
| -------------- | -------------------------- | ------------------ | ----- |
| Brute Force    | Check primes every time    | O(n · m · √m)      | O(1)  |
| Stored Primes  | Lookup largest prime       | O(n + m√m)         | O(m)  |
| Sieve + Greedy | Efficient prime generation | O(n + m log log m) | O(m)  |

Best practical approach: **Sieve + Greedy**.
