# 2523. Closest Prime Numbers in Range

## Approach 1: Sieve of Eratosthenes

### Intuition

We are given two numbers, **left** and **right**, and we need to find a pair of prime numbers within this range such that their difference is minimized.

Conditions:

- `left <= num1 < num2 <= right`
- Both numbers are **prime**
- `num2 - num1` is the **minimum difference**
- If multiple pairs exist, return the one with the **smallest num1**
- If no valid pair exists, return `[-1, -1]`

A naive approach would check each number individually for primality, which requires checking divisibility up to `sqrt(n)`. However, doing this for every number up to `10^6` would still be slow.

Instead we use the **Sieve of Eratosthenes**, which efficiently marks all prime numbers up to `right`.

### Sieve Idea

1. Create an array from `2` to `right`
2. Assume every number is prime
3. Start from `2`:
   - mark multiples of `2`
4. Move to next unmarked number (`3`):
   - mark multiples of `3`
5. Continue until `sqrt(right)`

Remaining unmarked numbers are **prime**.

Once primes are known, we scan the range `[left, right]` and compute the smallest gap between consecutive primes.

---

## Algorithm

### Main Function: `closestPrimes(left, right)`

1. Generate primes using `sieve(right)`
2. Collect primes in `[left, right]`
3. If fewer than 2 primes exist → return `[-1,-1]`
4. Compare consecutive primes and track minimum difference

### Helper Function: `sieve(upperLimit)`

1. Create array `sieve[upperLimit+1]`
2. Initialize all entries as `1`
3. Mark `0` and `1` as non-prime
4. For each number `2 → sqrt(upperLimit)`:
   - mark multiples as non-prime

---

## Implementation

```java
class Solution {

    public int[] closestPrimes(int left, int right) {

        int[] sieveArray = sieve(right);

        List<Integer> primeNumbers = new ArrayList<>();

        for (int num = left; num <= right; num++) {
            if (sieveArray[num] == 1) {
                primeNumbers.add(num);
            }
        }

        if (primeNumbers.size() < 2)
            return new int[]{-1,-1};

        int minDifference = Integer.MAX_VALUE;
        int[] closestPair = new int[]{-1,-1};

        for (int i = 1; i < primeNumbers.size(); i++) {

            int diff = primeNumbers.get(i) - primeNumbers.get(i-1);

            if (diff < minDifference) {
                minDifference = diff;
                closestPair[0] = primeNumbers.get(i-1);
                closestPair[1] = primeNumbers.get(i);
            }
        }

        return closestPair;
    }

    private int[] sieve(int upperLimit) {

        int[] sieve = new int[upperLimit + 1];

        Arrays.fill(sieve,1);

        sieve[0] = 0;
        sieve[1] = 0;

        for (int number = 2; number * number <= upperLimit; number++) {

            if (sieve[number] == 1) {

                for (int multiple = number * number;
                     multiple <= upperLimit;
                     multiple += number) {

                    sieve[multiple] = 0;
                }
            }
        }

        return sieve;
    }
}
```

---

## Complexity Analysis

Let:

```
R = right
L = left
```

### Time Complexity

```
O(R log log R + (R-L))
```

- Sieve: `O(R log log R)`
- Collect primes: `O(R-L)`
- Scan pairs: `O(R-L)`

### Space Complexity

```
O(R)
```

Used for the sieve array.

---

# Approach 2: Twin Prime Optimization

### Intuition

Instead of storing all primes, we can directly **scan the range** and check primality.

Observations:

1. The only consecutive primes with gap **1** are `(2,3)`.
2. The smallest possible prime gap afterward is **2** — called **twin primes**:

```
(3,5)
(11,13)
(17,19)
```

If we ever find a gap of **2**, we can immediately return it.

A mathematical result states:

> Any range larger than **1452** contains at least one twin prime pair.

Thus we can terminate early.

---

## Algorithm

### Main Function

1. If `(2,3)` lies in range → return `{2,3}`
2. Track previous prime
3. When a new prime is found:
   - compute gap
   - update best pair
   - if gap == 2 → return immediately

### Helper Function

Primality test:

- reject even numbers
- test odd divisors up to `sqrt(n)`

---

## Implementation

```java
class Solution {

    public int[] closestPrimes(int left, int right) {

        if (left <= 2 && right >= 3)
            return new int[]{2,3};

        int prevPrime = -1;
        int closestA = -1;
        int closestB = -1;
        int minDifference = (int)1e6;

        for (int candidate = left; candidate <= right; candidate++) {

            if (isPrime(candidate)) {

                if (prevPrime != -1) {

                    int diff = candidate - prevPrime;

                    if (diff < minDifference) {
                        minDifference = diff;
                        closestA = prevPrime;
                        closestB = candidate;
                    }

                    if (diff == 2)
                        return new int[]{prevPrime,candidate};
                }

                prevPrime = candidate;
            }
        }

        return new int[]{closestA,closestB};
    }

    private boolean isPrime(int number) {

        if (number < 2) return false;

        if (number == 2 || number == 3)
            return true;

        if (number % 2 == 0)
            return false;

        for (int divisor = 3; divisor * divisor <= number; divisor += 2) {

            if (number % divisor == 0)
                return false;
        }

        return true;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(min(1452, R-L) * sqrt(R))
```

Because we stop early once twin primes appear.

### Space Complexity

```
O(1)
```

Only constant variables are used.

---

# Summary

| Approach                | Time Complexity           | Space | Best For       |
| ----------------------- | ------------------------- | ----- | -------------- |
| Sieve of Eratosthenes   | O(R log log R)            | O(R)  | Large ranges   |
| Twin Prime Optimization | O(min(1452,R-L)\*sqrt(R)) | O(1)  | Smaller ranges |

The **Sieve approach is generally preferred** due to predictable performance.
