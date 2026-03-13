# 3115. Maximum Prime Difference

## Problem Restatement

We are given an integer array `nums`.

We need to return the **maximum distance between the indices of two prime numbers** in the array.

The two prime numbers may be the same occurrence, so if there is only one prime in the array, the answer is `0`.

Formally, if `i` and `j` are indices such that `nums[i]` and `nums[j]` are prime, we want:

```text
max(|i - j|)
```

---

## Key Observation

To maximize the distance between two prime indices, we only need:

- the **leftmost prime index**
- the **rightmost prime index**

Then the answer is simply:

```text
rightmostPrimeIndex - leftmostPrimeIndex
```

We do **not** need to compare every pair of primes.

That is the entire heart of the problem.

---

## Why This Is True

Suppose prime indices are:

```text
p1 < p2 < p3 < ... < pk
```

The maximum possible difference among them is clearly:

```text
pk - p1
```

because:

- `p1` is the smallest prime index
- `pk` is the largest prime index

Any other pair lies between them, so its distance cannot be larger.

---

# Approach 1 — Collect All Prime Indices

## Intuition

The most straightforward way is:

1. scan the array
2. store every index whose value is prime
3. answer = last prime index - first prime index

This is very easy to understand.

---

## Algorithm

1. Create a list `primeIndices`
2. Traverse the array
3. If `nums[i]` is prime, add `i` to the list
4. Return:

```text
primeIndices.get(last) - primeIndices.get(0)
```

Because the problem guarantees at least one prime, the list is never empty.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int maximumPrimeDifference(int[] nums) {
        List<Integer> primeIndices = new ArrayList<>();

        for (int i = 0; i < nums.length; i++) {
            if (isPrime(nums[i])) {
                primeIndices.add(i);
            }
        }

        return primeIndices.get(primeIndices.size() - 1) - primeIndices.get(0);
    }

    private boolean isPrime(int x) {
        if (x < 2) return false;
        for (int d = 2; d * d <= x; d++) {
            if (x % d == 0) return false;
        }
        return true;
    }
}
```

---

## Complexity Analysis

Let `n = nums.length`.

### Time Complexity

We scan the array once:

```text
O(n)
```

For each value, primality test is:

```text
O(sqrt(nums[i]))
```

Since `nums[i] <= 100`, this is effectively constant.

So overall:

```text
O(n)
```

### Space Complexity

The list may store every index in the worst case:

```text
O(n)
```

---

# Approach 2 — Track First and Last Prime Only

## Intuition

Approach 1 stores all prime indices, but that is unnecessary.

We only need:

- the first prime index
- the last prime index

So while scanning:

- set `first` when we see the first prime
- keep updating `last` whenever we see a prime

At the end:

```text
answer = last - first
```

This is the cleanest and most efficient solution.

---

## Algorithm

1. Initialize:
   - `first = -1`
   - `last = -1`
2. Traverse the array
3. If `nums[i]` is prime:
   - if `first == -1`, set `first = i`
   - set `last = i`
4. Return:

```text
last - first
```

---

## Java Code

```java
class Solution {
    public int maximumPrimeDifference(int[] nums) {
        int first = -1;
        int last = -1;

        for (int i = 0; i < nums.length; i++) {
            if (isPrime(nums[i])) {
                if (first == -1) {
                    first = i;
                }
                last = i;
            }
        }

        return last - first;
    }

    private boolean isPrime(int x) {
        if (x < 2) return false;
        for (int d = 2; d * d <= x; d++) {
            if (x % d == 0) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Single pass through the array:

```text
O(n)
```

Primality check is constant-time in practice because `nums[i] <= 100`.

### Space Complexity

Only a few variables:

```text
O(1)
```

---

# Approach 3 — Two-Pointer Scan from Both Ends

## Intuition

Since we only care about:

- the first prime from the left
- the first prime from the right

we can find them directly using two pointers.

This approach is elegant because the answer becomes immediately available once both endpoints are found.

---

## Algorithm

1. Start `left = 0`
2. Move `left` rightward until `nums[left]` is prime
3. Start `right = nums.length - 1`
4. Move `right` leftward until `nums[right]` is prime
5. Return:

```text
right - left
```

Because the problem guarantees at least one prime, both pointers will stop successfully.

---

## Java Code

```java
class Solution {
    public int maximumPrimeDifference(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (!isPrime(nums[left])) {
            left++;
        }

        while (!isPrime(nums[right])) {
            right--;
        }

        return right - left;
    }

    private boolean isPrime(int x) {
        if (x < 2) return false;
        for (int d = 2; d * d <= x; d++) {
            if (x % d == 0) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Complexity Analysis

### Time Complexity

In the worst case, pointers each scan across much of the array:

```text
O(n)
```

### Space Complexity

```text
O(1)
```

---

# Approach 4 — Precompute Prime Table for 1 to 100

## Intuition

Since `nums[i] <= 100`, we do not even need to run a normal primality test each time.

We can precompute a small boolean table:

```text
prime[0..100]
```

Then prime checking becomes:

```text
prime[nums[i]]
```

This is slightly cleaner and avoids repeated divisibility loops.

This is a good approach when the value range is tiny.

---

## Java Code

```java
class Solution {
    public int maximumPrimeDifference(int[] nums) {
        boolean[] prime = buildPrimeTable();

        int first = -1;
        int last = -1;

        for (int i = 0; i < nums.length; i++) {
            if (prime[nums[i]]) {
                if (first == -1) {
                    first = i;
                }
                last = i;
            }
        }

        return last - first;
    }

    private boolean[] buildPrimeTable() {
        boolean[] prime = new boolean[101];
        for (int i = 2; i <= 100; i++) {
            prime[i] = true;
        }

        for (int p = 2; p * p <= 100; p++) {
            if (prime[p]) {
                for (int multiple = p * p; multiple <= 100; multiple += p) {
                    prime[multiple] = false;
                }
            }
        }

        return prime;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Building prime table:

```text
O(100 log log 100)
```

which is effectively constant.

Scanning the array:

```text
O(n)
```

Overall:

```text
O(n)
```

### Space Complexity

Prime table size is fixed:

```text
O(1)
```

---

# Worked Example

## Example 1

```text
nums = [4,2,9,5,3]
```

Prime numbers are at indices:

```text
1, 3, 4
```

Leftmost prime index:

```text
1
```

Rightmost prime index:

```text
4
```

Answer:

```text
4 - 1 = 3
```

---

## Example 2

```text
nums = [4,8,2,8]
```

Prime numbers are at index:

```text
2
```

Both leftmost and rightmost prime index are `2`.

So:

```text
2 - 2 = 0
```

---

# Correctness Argument

## Claim

The answer is the distance between the first and last prime indices.

### Proof

Let the prime indices in the array be:

```text
p1 < p2 < ... < pk
```

For any two prime indices `pi` and `pj`, the distance is:

```text
|pj - pi|
```

The maximum is achieved by choosing the smallest and largest index, namely:

```text
pk - p1
```

No other pair can have a larger difference because all other prime indices lie between `p1` and `pk`.

Therefore, the required answer is exactly:

```text
rightmostPrimeIndex - leftmostPrimeIndex
```

Proved.

---

# Comparison of Approaches

## Approach 1 — Store all prime indices

Pros:

- very intuitive
- easiest to explain first

Cons:

- unnecessary extra space

---

## Approach 2 — Track first and last prime

Pros:

- optimal
- simplest production solution
- no extra memory

Cons:

- none, really

This is the best general answer.

---

## Approach 3 — Two pointers from both ends

Pros:

- elegant
- directly targets what the answer needs

Cons:

- slightly less “general” feeling than a normal scan

---

## Approach 4 — Precomputed prime table

Pros:

- very clean because values are at most 100
- constant-time prime lookup

Cons:

- slightly more setup

---

# Final Recommended Solution

This is the most practical version: single pass, track first and last prime.

```java
class Solution {
    public int maximumPrimeDifference(int[] nums) {
        int first = -1;
        int last = -1;

        for (int i = 0; i < nums.length; i++) {
            if (isPrime(nums[i])) {
                if (first == -1) {
                    first = i;
                }
                last = i;
            }
        }

        return last - first;
    }

    private boolean isPrime(int x) {
        if (x < 2) return false;
        for (int d = 2; d * d <= x; d++) {
            if (x % d == 0) return false;
        }
        return true;
    }
}
```

---

# Even Better for This Constraint Range

Because values are only from `1` to `100`, this version is arguably the cleanest:

```java
class Solution {
    public int maximumPrimeDifference(int[] nums) {
        boolean[] prime = new boolean[101];
        for (int i = 2; i <= 100; i++) {
            prime[i] = true;
        }

        for (int p = 2; p * p <= 100; p++) {
            if (prime[p]) {
                for (int multiple = p * p; multiple <= 100; multiple += p) {
                    prime[multiple] = false;
                }
            }
        }

        int first = -1;
        int last = -1;

        for (int i = 0; i < nums.length; i++) {
            if (prime[nums[i]]) {
                if (first == -1) {
                    first = i;
                }
                last = i;
            }
        }

        return last - first;
    }
}
```

---

# Edge Cases

## 1. Only one prime in the array

Example:

```text
[4, 8, 2, 8]
```

Then `first = last = 2`, so answer is `0`.

---

## 2. Prime at both ends

Example:

```text
[2, 4, 6, 7]
```

Then answer is:

```text
3 - 0 = 3
```

---

## 3. All elements prime

Example:

```text
[2, 3, 5, 7]
```

Then answer is:

```text
3 - 0 = 3
```

---

## 4. Large array length

`nums.length` can be up to `3 * 10^5`, but all our optimal approaches are linear, so this is completely fine.

---

# Complexity Summary

## Approach 1

```text
Time:  O(n)
Space: O(n)
```

## Approach 2

```text
Time:  O(n)
Space: O(1)
```

## Approach 3

```text
Time:  O(n)
Space: O(1)
```

## Approach 4

```text
Time:  O(n)
Space: O(1)
```

---

# Final Takeaway

This problem is much simpler than it first appears.

You do **not** need to compare all prime pairs.

You only need:

- first prime index
- last prime index

Then return their difference.

That makes the problem a clean linear scan.
