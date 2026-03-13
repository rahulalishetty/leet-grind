# 3044. Most Frequent Prime

## Problem Restatement

We are given a matrix `mat` of digits.

From **every cell**, we may choose one of the **8 directions**:

- east
- south-east
- south
- south-west
- west
- north-west
- north
- north-east

Then we keep moving in that fixed direction, appending digits to form numbers.

If a path produces digits:

```text
1 -> 9 -> 1
```

then the generated numbers are:

```text
1, 19, 191
```

We must return:

- the **most frequent prime number greater than 10**
- if multiple primes have the same highest frequency, return the **largest**
- if none exist, return `-1`

---

## Key Constraints

```text
1 <= m, n <= 6
1 <= mat[i][j] <= 9
```

This matters a lot.

Since the matrix is tiny, the total number of generated numbers is small enough to enumerate directly.

So the real challenge is not path generation — it is organizing the logic cleanly and handling primality efficiently.

---

# High-Level Insight

For each cell:

- try all 8 directions
- keep building the number digit by digit
- every prefix along that straight-line path is a generated number
- if the number is:
  - greater than `10`
  - prime

then count it

Finally:

- choose the prime with maximum frequency
- if frequencies tie, choose the larger prime

This naturally suggests brute-force enumeration plus a frequency map.

Because matrix size is at most `6 x 6`, this is completely practical.

---

# Approach 1 — Direct Enumeration + Trial Division Primality Test

## Intuition

This is the most natural solution.

We explicitly generate every valid number:

1. start at each cell
2. move in each of the 8 directions
3. build the number step by step
4. check whether each generated number is prime

We store prime frequencies in a hash map.

At the end, scan the map and choose the prime with:

- highest frequency
- largest value in case of tie

---

## How many numbers are generated?

At most:

- `36` starting cells
- `8` directions
- at most `6` digits per path

So only a few thousand generated numbers at most.

That is tiny.

---

## Trial Division for Primality

To test whether `x` is prime:

- reject `x <= 1`
- reject even numbers except `2`
- try odd divisors from `3` to `sqrt(x)`

Because generated numbers are short and count is small, this is good enough.

---

## Java Code

```java
import java.util.*;

class Solution {
    private static final int[][] DIRS = {
        {0, 1},   // east
        {1, 1},   // south-east
        {1, 0},   // south
        {1, -1},  // south-west
        {0, -1},  // west
        {-1, -1}, // north-west
        {-1, 0},  // north
        {-1, 1}   // north-east
    };

    public int mostFrequentPrime(int[][] mat) {
        int m = mat.length;
        int n = mat[0].length;

        Map<Integer, Integer> freq = new HashMap<>();

        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                for (int[] d : DIRS) {
                    int nr = r;
                    int nc = c;
                    int num = 0;

                    while (nr >= 0 && nr < m && nc >= 0 && nc < n) {
                        num = num * 10 + mat[nr][nc];

                        if (num > 10 && isPrime(num)) {
                            freq.put(num, freq.getOrDefault(num, 0) + 1);
                        }

                        nr += d[0];
                        nc += d[1];
                    }
                }
            }
        }

        int answer = -1;
        int bestFreq = 0;

        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            int prime = entry.getKey();
            int count = entry.getValue();

            if (count > bestFreq || (count == bestFreq && prime > answer)) {
                bestFreq = count;
                answer = prime;
            }
        }

        return answer;
    }

    private boolean isPrime(int x) {
        if (x <= 1) return false;
        if (x == 2) return true;
        if (x % 2 == 0) return false;

        for (int d = 3; d * d <= x; d += 2) {
            if (x % d == 0) return false;
        }
        return true;
    }
}
```

---

## Complexity Analysis

Let:

- `m = mat.length`
- `n = mat[0].length`
- `L = max(m, n)`

### Number generation

For each cell and direction, we traverse at most `L` cells:

```text
O(m * n * 8 * L)
```

### Primality test

For a generated number `x`, trial division takes:

```text
O(sqrt(x))
```

Since the maximum path length is at most `6`, the largest number has at most 6 digits.

So overall, this is still very manageable.

### Practical complexity

Given the tiny constraints, this easily passes.

### Space Complexity

Frequency map stores distinct prime values encountered:

```text
O(P)
```

where `P` is the number of distinct primes generated.

---

# Approach 2 — Direct Enumeration + Memoized Primality

## Intuition

Approach 1 may test the same number for primality many times.

Example:

- the prime `19` may be generated from many starts and directions

Instead of recomputing primality every time, cache the result.

So:

- if we already checked `x`, reuse the stored answer
- otherwise compute once and store it

This is a classic optimization when repeated values appear.

Because many paths can generate the same number, memoization makes the solution cleaner and sometimes faster.

---

## Java Code

```java
import java.util.*;

class Solution {
    private static final int[][] DIRS = {
        {0, 1}, {1, 1}, {1, 0}, {1, -1},
        {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}
    };

    public int mostFrequentPrime(int[][] mat) {
        int m = mat.length;
        int n = mat[0].length;

        Map<Integer, Integer> freq = new HashMap<>();
        Map<Integer, Boolean> primeCache = new HashMap<>();

        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                for (int[] dir : DIRS) {
                    int nr = r;
                    int nc = c;
                    int num = 0;

                    while (nr >= 0 && nr < m && nc >= 0 && nc < n) {
                        num = num * 10 + mat[nr][nc];

                        if (num > 10) {
                            boolean prime = primeCache.computeIfAbsent(num, this::isPrime);
                            if (prime) {
                                freq.put(num, freq.getOrDefault(num, 0) + 1);
                            }
                        }

                        nr += dir[0];
                        nc += dir[1];
                    }
                }
            }
        }

        int answer = -1;
        int bestFreq = 0;

        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            int prime = entry.getKey();
            int count = entry.getValue();

            if (count > bestFreq || (count == bestFreq && prime > answer)) {
                bestFreq = count;
                answer = prime;
            }
        }

        return answer;
    }

    private boolean isPrime(int x) {
        if (x <= 1) return false;
        if (x == 2) return true;
        if (x % 2 == 0) return false;

        for (int d = 3; d * d <= x; d += 2) {
            if (x % d == 0) return false;
        }
        return true;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Number generation is unchanged:

```text
O(m * n * 8 * L)
```

But each distinct number is primality-tested at most once.

If many duplicates appear, this is a useful improvement.

### Space Complexity

Two maps:

- frequency map
- prime cache

So:

```text
O(P + D)
```

where:

- `P` = distinct prime numbers counted
- `D` = distinct generated numbers checked

Given the small bounds, this is still tiny.

---

# Approach 3 — Direct Enumeration + Sieve up to Maximum Generated Number

## Intuition

Because all generated numbers come from paths of length at most `6`, the largest possible number is:

```text
999999
```

So another possible approach is:

1. enumerate all generated numbers first
2. find the maximum generated number
3. build a sieve of Eratosthenes up to that maximum
4. use `O(1)` prime lookup when counting frequencies

This approach is often appealing when:

- there are many primality queries
- the maximum value is moderate

Here `999999` is completely fine for a sieve.

---

## When is this approach attractive?

Compared with trial division:

- sieve has upfront preprocessing cost
- but after preprocessing, prime checks are constant time

This is especially elegant when you want deterministic prime lookup speed.

---

## Java Code

```java
import java.util.*;

class Solution {
    private static final int[][] DIRS = {
        {0, 1}, {1, 1}, {1, 0}, {1, -1},
        {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}
    };

    public int mostFrequentPrime(int[][] mat) {
        int m = mat.length;
        int n = mat[0].length;

        List<Integer> numbers = new ArrayList<>();
        int maxNum = 0;

        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                for (int[] dir : DIRS) {
                    int nr = r;
                    int nc = c;
                    int num = 0;

                    while (nr >= 0 && nr < m && nc >= 0 && nc < n) {
                        num = num * 10 + mat[nr][nc];

                        if (num > 10) {
                            numbers.add(num);
                            if (num > maxNum) {
                                maxNum = num;
                            }
                        }

                        nr += dir[0];
                        nc += dir[1];
                    }
                }
            }
        }

        if (numbers.isEmpty()) return -1;

        boolean[] isPrime = sieve(maxNum);
        Map<Integer, Integer> freq = new HashMap<>();

        for (int num : numbers) {
            if (isPrime[num]) {
                freq.put(num, freq.getOrDefault(num, 0) + 1);
            }
        }

        int answer = -1;
        int bestFreq = 0;

        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            int prime = entry.getKey();
            int count = entry.getValue();

            if (count > bestFreq || (count == bestFreq && prime > answer)) {
                bestFreq = count;
                answer = prime;
            }
        }

        return answer;
    }

    private boolean[] sieve(int limit) {
        boolean[] isPrime = new boolean[limit + 1];
        if (limit >= 2) {
            Arrays.fill(isPrime, true);
            isPrime[0] = false;
            isPrime[1] = false;

            for (int p = 2; p * p <= limit; p++) {
                if (isPrime[p]) {
                    for (int multiple = p * p; multiple <= limit; multiple += p) {
                        isPrime[multiple] = false;
                    }
                }
            }
        }
        return isPrime;
    }
}
```

---

## Complexity Analysis

### Number generation

```text
O(m * n * 8 * L)
```

### Sieve

If `X` is the maximum generated number:

```text
O(X log log X)
```

with `X <= 999999`

### Frequency counting

```text
O(T)
```

where `T` is the number of generated numbers greater than `10`.

### Space Complexity

```text
O(X + T)
```

This is acceptable here because `X` is under one million.

---

# Comparing the Approaches

## Approach 1 — Trial Division

Pros:

- simplest
- very easy to code
- enough for constraints

Cons:

- repeats primality checks for duplicates

Best when:

- you want the cleanest solution quickly

---

## Approach 2 — Memoized Trial Division

Pros:

- still simple
- avoids repeated prime testing
- likely the best practical balance

Cons:

- slightly more bookkeeping

Best when:

- you want a robust, clean optimization

---

## Approach 3 — Sieve

Pros:

- constant-time prime lookup after preprocessing
- elegant if you like precomputation

Cons:

- more memory
- more setup than necessary for such small enumeration volume

Best when:

- you want a prime-heavy precompute approach

---

# Recommended Solution

For this problem, **Approach 2** is the strongest practical choice.

Why:

- matrix is tiny, so full enumeration is natural
- memoized primality avoids repeated work
- code stays straightforward

---

# Correctness Reasoning

## Claim 1

The algorithm enumerates every valid number that can be formed.

### Why?

For each start cell, it tries all 8 legal directions.

For a fixed direction, it continues moving until leaving the matrix.

At every step, it appends the next digit and records the current formed number.

That exactly matches the problem statement: every straight-line path prefix is generated.

So no valid number is missed, and no invalid number is included.

---

## Claim 2

Every prime greater than `10` is counted exactly as many times as it is generated.

### Why?

Whenever a generated number is:

- greater than `10`
- prime

we increment its frequency once.

Since each occurrence corresponds to one unique start/direction/path-prefix event, the frequency map records the correct total count.

---

## Claim 3

The final selection rule is correct.

### Why?

The problem asks for:

1. highest frequency
2. if tied, largest prime

The final scan over the map explicitly applies this comparison:

- choose larger frequency first
- if equal, choose larger value

So the returned answer matches the specification.

---

# Worked Example

## Example 1

```text
mat = [[1,1],[9,9],[1,1]]
```

From `(0,0)`:

- east -> `11`
- south-east -> `19`
- south -> `19`, `191`

From other cells, similar values appear.

Prime frequencies end up with:

```text
11  -> several times
19  -> more times
191 -> fewer times
```

The most frequent prime is:

```text
19
```

So answer is `19`.

---

# Edge Cases

## 1. Single cell matrix

Example:

```text
[[7]]
```

Generated number is only `7`.

But the problem requires prime `> 10`.

So answer is:

```text
-1
```

---

## 2. No generated prime greater than 10

Possible if all numbers formed are composite or too small.

Then the frequency map stays empty, and we return:

```text
-1
```

---

## 3. Tie in frequency

Suppose:

- `19` occurs 4 times
- `97` occurs 4 times

Then answer must be `97`, because it is larger.

Our final comparison handles this correctly.

---

## 4. Repeated numbers from different paths

The same number may be formed multiple times from:

- different start cells
- different directions
- different path lengths

All occurrences count separately, as required.

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    private static final int[][] DIRS = {
        {0, 1}, {1, 1}, {1, 0}, {1, -1},
        {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}
    };

    public int mostFrequentPrime(int[][] mat) {
        int m = mat.length;
        int n = mat[0].length;

        Map<Integer, Integer> freq = new HashMap<>();
        Map<Integer, Boolean> primeCache = new HashMap<>();

        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                for (int[] dir : DIRS) {
                    int nr = r;
                    int nc = c;
                    int num = 0;

                    while (nr >= 0 && nr < m && nc >= 0 && nc < n) {
                        num = num * 10 + mat[nr][nc];

                        if (num > 10) {
                            boolean prime = primeCache.computeIfAbsent(num, this::isPrime);
                            if (prime) {
                                freq.put(num, freq.getOrDefault(num, 0) + 1);
                            }
                        }

                        nr += dir[0];
                        nc += dir[1];
                    }
                }
            }
        }

        int answer = -1;
        int bestFreq = 0;

        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            int prime = entry.getKey();
            int count = entry.getValue();

            if (count > bestFreq || (count == bestFreq && prime > answer)) {
                bestFreq = count;
                answer = prime;
            }
        }

        return answer;
    }

    private boolean isPrime(int x) {
        if (x <= 1) return false;
        if (x == 2) return true;
        if (x % 2 == 0) return false;

        for (int d = 3; d * d <= x; d += 2) {
            if (x % d == 0) return false;
        }

        return true;
    }
}
```

---

# Complexity Summary

Let:

- `m` = number of rows
- `n` = number of columns
- `L = max(m, n)`

## Approach 1

```text
Time:  O(m * n * 8 * L * primality_cost)
Space: O(P)
```

## Approach 2

```text
Time:  O(m * n * 8 * L + distinct_prime_checks)
Space: O(P + D)
```

## Approach 3

```text
Time:  O(m * n * 8 * L + X log log X)
Space: O(X + T)
```

where `X <= 999999`.

---

# Final Takeaway

This problem is mainly about **careful enumeration**.

Because the matrix is so small:

- generate everything
- count primes
- apply the tie-breaking rule

The cleanest implementation is:

- enumerate all straight-line numbers
- cache primality results
- pick the most frequent prime, breaking ties by larger value
