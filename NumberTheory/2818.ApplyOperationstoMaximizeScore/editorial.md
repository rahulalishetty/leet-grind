# 2818. Apply Operations to Maximize Score — Detailed Approaches

## Overview

We are given an array of positive integers `nums`, a number `k`, and the ability to perform the following operation at most `k` times:

1. Select any **non-empty subarray** that has not been chosen before.
2. Identify the number in this subarray with the **highest prime score**.
3. If multiple numbers have the same prime score, select the **leftmost element**.
4. Multiply the current score by the chosen number.

The **prime score** of a number is defined as the number of **distinct prime factors** of that number.

Example:

- `60 = 2 × 2 × 3 × 5` → distinct primes `{2,3,5}` → prime score = **3**
- `24 = 2 × 2 × 2 × 3` → distinct primes `{2,3}` → prime score = **2**

We start with score:

```
score = 1
```

Our goal is to **maximize the final score after at most `k` operations**.

Return the result modulo:

```
10^9 + 7
```

---

# Key Observation

Since all numbers are **positive**, multiplying always increases or maintains the score.

Therefore:

> It is always optimal to perform exactly **k operations**.

Also, the constraint

```
k ≤ n(n+1)/2
```

guarantees enough unique subarrays.

---

# Core Idea

Instead of enumerating all subarrays (`O(n²)`), we determine:

> **How many subarrays choose each element as the dominant element.**

An element is **dominant** in a subarray if:

- it has the **highest prime score** in that subarray
- ties are broken by **smaller index**

Once we know how many subarrays each element dominates, we can:

1. process elements in **descending order of value**
2. multiply the score using that element as many times as possible

---

# Approach 1 — Monotonic Stack + Priority Queue

## Intuition

For each index `i`, compute the number of subarrays where `nums[i]` is the dominant element.

An element stops being dominant when a **higher prime score** appears.

We find:

```
prevDominant[i] = nearest index to left with higher prime score
nextDominant[i] = nearest index to right with higher prime score
```

Number of valid subarrays:

```
(i - prevDominant[i]) × (nextDominant[i] - i)
```

A **monotonic decreasing stack** efficiently finds these boundaries.

Then we process numbers using a **max heap (priority queue)** ordered by value.

---

## Java Implementation

```java
class Solution {

    final int MOD = 1000000007;

    public int maximumScore(List<Integer> nums, int k) {
        int n = nums.size();
        List<Integer> primeScores = new ArrayList<>(Collections.nCopies(n, 0));

        // compute prime scores
        for (int i = 0; i < n; i++) {
            int num = nums.get(i);

            for (int f = 2; f <= Math.sqrt(num); f++) {
                if (num % f == 0) {
                    primeScores.set(i, primeScores.get(i) + 1);
                    while (num % f == 0) num /= f;
                }
            }

            if (num >= 2) primeScores.set(i, primeScores.get(i) + 1);
        }

        int[] nextDominant = new int[n];
        int[] prevDominant = new int[n];
        Arrays.fill(nextDominant, n);
        Arrays.fill(prevDominant, -1);

        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < n; i++) {

            while (!stack.isEmpty() &&
                   primeScores.get(stack.peek()) < primeScores.get(i)) {

                int top = stack.pop();
                nextDominant[top] = i;
            }

            if (!stack.isEmpty())
                prevDominant[i] = stack.peek();

            stack.push(i);
        }

        long[] subarrays = new long[n];

        for (int i = 0; i < n; i++) {
            subarrays[i] =
                    (long)(nextDominant[i] - i) *
                    (i - prevDominant[i]);
        }

        PriorityQueue<int[]> pq =
            new PriorityQueue<>((a,b) -> {
                if(b[0] == a[0])
                    return Integer.compare(a[1], b[1]);
                return Integer.compare(b[0], a[0]);
            });

        for (int i = 0; i < n; i++)
            pq.offer(new int[]{nums.get(i), i});

        long score = 1;

        while (k > 0) {

            int[] top = pq.poll();
            int num = top[0];
            int index = top[1];

            long ops = Math.min((long)k, subarrays[index]);

            score = (score * power(num, ops)) % MOD;

            k -= ops;
        }

        return (int)score;
    }

    private long power(long base, long exp) {
        long res = 1;

        while (exp > 0) {
            if (exp % 2 == 1)
                res = (res * base) % MOD;

            base = (base * base) % MOD;
            exp /= 2;
        }

        return res;
    }
}
```

---

## Time Complexity

```
O(n √m + n log n)
```

Where:

- `n` = size of array
- `m` = largest element

---

## Space Complexity

```
O(n)
```

---

# Approach 2 — Sieve of Eratosthenes + Sorting

## Intuition

This approach improves **prime factor computation** using the classic **Sieve of Eratosthenes**.

Steps:

1. Generate all primes up to `max(nums)`
2. Use them to factorize numbers efficiently
3. Use the same **monotonic stack dominance computation**
4. Instead of a heap, **sort the numbers in descending order**

Sorting avoids priority queue overhead.

---

## Java Implementation

```java
class Solution {

    private static final int MOD = 1_000_000_007;

    public int maximumScore(List<Integer> nums, int k) {

        int n = nums.size();
        int[] primeScores = new int[n];

        int maxElement = Collections.max(nums);

        List<Integer> primes = getPrimes(maxElement);

        for (int i = 0; i < n; i++) {

            int num = nums.get(i);

            for (int prime : primes) {

                if (prime * prime > num)
                    break;

                if (num % prime != 0)
                    continue;

                primeScores[i]++;

                while (num % prime == 0)
                    num /= prime;
            }

            if (num > 1)
                primeScores[i]++;
        }

        int[] nextDominant = new int[n];
        int[] prevDominant = new int[n];

        Arrays.fill(nextDominant, n);
        Arrays.fill(prevDominant, -1);

        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < n; i++) {

            while (!stack.isEmpty() &&
                   primeScores[stack.peek()] < primeScores[i]) {

                int top = stack.pop();
                nextDominant[top] = i;
            }

            if (!stack.isEmpty())
                prevDominant[i] = stack.peek();

            stack.push(i);
        }

        long[] subarrays = new long[n];

        for (int i = 0; i < n; i++) {
            subarrays[i] =
                    (long)(nextDominant[i] - i) *
                    (i - prevDominant[i]);
        }

        List<int[]> sorted = new ArrayList<>();

        for (int i = 0; i < n; i++)
            sorted.add(new int[]{nums.get(i), i});

        sorted.sort((a,b)->Integer.compare(b[0],a[0]));

        long score = 1;
        int ptr = 0;

        while (k > 0) {

            int[] e = sorted.get(ptr++);

            int num = e[0];
            int idx = e[1];

            long ops = Math.min(k, subarrays[idx]);

            score = (score * power(num, ops)) % MOD;

            k -= ops;
        }

        return (int)score;
    }

    private long power(long base,long exp){

        long res = 1;

        while(exp > 0){

            if((exp & 1)==1)
                res = (res*base)%MOD;

            base = (base*base)%MOD;
            exp >>= 1;
        }

        return res;
    }

    private List<Integer> getPrimes(int limit){

        boolean[] isPrime = new boolean[limit+1];
        Arrays.fill(isPrime,true);

        List<Integer> primes = new ArrayList<>();

        for(int i=2;i<=limit;i++){

            if(!isPrime[i])
                continue;

            primes.add(i);

            for(long j=(long)i*i;j<=limit;j+=i)
                isPrime[(int)j]=false;
        }

        return primes;
    }
}
```

---

## Time Complexity

```
O(m log log m + n log n)
```

Where:

- `m` = maximum element

---

## Space Complexity

```
O(m + n)
```

---

# Summary

| Approach   | Prime Score Method    | Element Processing | Complexity             |
| ---------- | --------------------- | ------------------ | ---------------------- |
| Approach 1 | Trial Division        | Priority Queue     | O(n√m + nlogn)         |
| Approach 2 | Sieve of Eratosthenes | Sorting            | O(m log log m + nlogn) |

Both rely on the same **monotonic stack dominance counting** technique.

---

# Key Techniques Used

- Prime factorization
- Monotonic stack
- Greedy multiplication
- Modular exponentiation
- Heap / sorting
