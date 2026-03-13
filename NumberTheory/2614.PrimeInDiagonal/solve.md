# 2614. Prime In Diagonal

## Problem Restatement

We are given a square matrix `nums`.

We need to return the **largest prime number** that appears on **either** of the two diagonals:

- primary diagonal: `nums[i][i]`
- secondary diagonal: `nums[i][n - i - 1]`

If no prime appears on either diagonal, return `0`.

---

## Key Observation

We do **not** need to inspect the entire matrix.

Only these cells matter:

- `nums[i][i]`
- `nums[i][n - i - 1]`

for every row `i`.

That means for an `n x n` matrix, we only examine at most:

```text
2n
```

values, not `n^2`.

So the problem reduces to:

1. scan both diagonals
2. test primality of each diagonal value
3. keep track of the maximum prime

---

## Important Detail About the Center Cell

If `n` is odd, the center cell belongs to **both diagonals**.

Example for `3 x 3`:

```text
[ a . b
  . c .
  d . e ]
```

The center `c` is on both diagonals.

That is fine.

If we inspect it twice, it does not affect correctness, because we only want the **maximum prime**, not a count.

---

# Approach 1 — Direct Diagonal Scan + Trial Division Primality Check

## Intuition

This is the most natural solution.

For each row `i`:

- check `nums[i][i]`
- check `nums[i][n - i - 1]`

If either is prime, update the answer.

Because matrix size is at most `300`, there are at most `600` primality checks.

That is completely manageable.

---

## Algorithm

1. Let `n = nums.length`
2. Initialize `ans = 0`
3. For each `i` from `0` to `n - 1`:
   - check primary diagonal value `nums[i][i]`
   - check secondary diagonal value `nums[i][n - i - 1]`
   - if prime, update `ans`
4. Return `ans`

---

## Java Code

```java
class Solution {
    public int diagonalPrime(int[][] nums) {
        int n = nums.length;
        int ans = 0;

        for (int i = 0; i < n; i++) {
            int a = nums[i][i];
            int b = nums[i][n - i - 1];

            if (isPrime(a)) {
                ans = Math.max(ans, a);
            }
            if (isPrime(b)) {
                ans = Math.max(ans, b);
            }
        }

        return ans;
    }

    private boolean isPrime(int x) {
        if (x < 2) return false;
        if (x == 2) return true;
        if (x % 2 == 0) return false;

        for (int d = 3; d * d <= x; d += 2) {
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

Let:

- `n = nums.length`
- `V = maximum diagonal value`

There are at most `2n` diagonal values checked.

Each primality test by trial division takes:

```text
O(sqrt(V))
```

So total time is:

```text
O(n * sqrt(V))
```

Since:

```text
n <= 300
V <= 4 * 10^6
```

this is fast enough.

### Space Complexity

```text
O(1)
```

---

# Approach 2 — Collect Unique Diagonal Values, Then Test Primality Once Per Value

## Intuition

Some diagonal values may repeat.

For example, the same number may appear on both diagonals or in multiple rows.

Instead of primality-testing the same number repeatedly, we can:

1. collect all diagonal values into a set
2. test each distinct value once
3. take the maximum prime

This avoids repeated prime checks.

It is especially clean when duplicates are common.

---

## Algorithm

1. Create a `HashSet<Integer>`
2. Insert all values from both diagonals
3. Iterate through the set
4. For each value:
   - if prime, update answer
5. Return answer

---

## Java Code

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    public int diagonalPrime(int[][] nums) {
        int n = nums.length;
        Set<Integer> diagValues = new HashSet<>();

        for (int i = 0; i < n; i++) {
            diagValues.add(nums[i][i]);
            diagValues.add(nums[i][n - i - 1]);
        }

        int ans = 0;
        for (int val : diagValues) {
            if (isPrime(val)) {
                ans = Math.max(ans, val);
            }
        }

        return ans;
    }

    private boolean isPrime(int x) {
        if (x < 2) return false;
        if (x == 2) return true;
        if (x % 2 == 0) return false;

        for (int d = 3; d * d <= x; d += 2) {
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

At most `2n` values are inserted into the set.

If `u` is the number of distinct diagonal values, primality is tested `u` times.

### Time Complexity

```text
O(n + u * sqrt(V))
```

where `u <= 2n`.

### Space Complexity

```text
O(u)
```

---

# Approach 3 — Prime Sieve Up to Maximum Diagonal Value

## Intuition

If we want constant-time prime lookups, we can:

1. find the maximum value appearing on either diagonal
2. build a sieve up to that maximum
3. check each diagonal value in `O(1)`

This is attractive when the same prime checks happen many times or when we want deterministic lookup speed.

Since values can be as large as:

```text
4 * 10^6
```

a sieve is still feasible, though heavier than trial division.

---

## When is this worth it?

This approach is more useful if:

- there are many primality checks
- or we want a reusable prime table

For this problem alone, trial division is usually simpler, but the sieve version is still fully valid.

---

## Algorithm

1. Scan both diagonals and record:
   - all diagonal values
   - maximum diagonal value
2. Build sieve up to that maximum
3. Scan the diagonal values:
   - if sieve says prime, update answer
4. Return answer

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    public int diagonalPrime(int[][] nums) {
        int n = nums.length;
        int[] diag = new int[2 * n];
        int idx = 0;
        int maxVal = 0;

        for (int i = 0; i < n; i++) {
            int a = nums[i][i];
            int b = nums[i][n - i - 1];

            diag[idx++] = a;
            diag[idx++] = b;

            maxVal = Math.max(maxVal, a);
            maxVal = Math.max(maxVal, b);
        }

        boolean[] isPrime = sieve(maxVal);

        int ans = 0;
        for (int i = 0; i < idx; i++) {
            if (isPrime[diag[i]]) {
                ans = Math.max(ans, diag[i]);
            }
        }

        return ans;
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

Let `M` be the maximum diagonal value.

### Time Complexity

- scanning diagonals: `O(n)`
- sieve: `O(M log log M)`
- final scan: `O(n)`

Overall:

```text
O(n + M log log M)
```

### Space Complexity

```text
O(M)
```

This is the tradeoff for constant-time primality lookup.

---

# Approach 4 — Scan Diagonals From Largest Candidates First

## Intuition

Because we only need the **largest** prime, another viewpoint is:

1. gather diagonal values
2. sort them in descending order
3. return the first prime encountered

This is less efficient than a simple max-tracking scan, but it is useful conceptually.

It explicitly targets the “largest prime” requirement.

---

## Java Code

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Solution {
    public int diagonalPrime(int[][] nums) {
        int n = nums.length;
        List<Integer> values = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            values.add(nums[i][i]);
            values.add(nums[i][n - i - 1]);
        }

        Collections.sort(values, Collections.reverseOrder());

        for (int val : values) {
            if (isPrime(val)) {
                return val;
            }
        }

        return 0;
    }

    private boolean isPrime(int x) {
        if (x < 2) return false;
        if (x == 2) return true;
        if (x % 2 == 0) return false;

        for (int d = 3; d * d <= x; d += 2) {
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

- collect diagonal values: `O(n)`
- sort at most `2n` numbers: `O(n log n)`
- primality tests: up to `O(n * sqrt(V))`

Overall:

```text
O(n log n + n * sqrt(V))
```

This is not better than Approach 1, but it is another valid way to solve the problem.

### Space Complexity

```text
O(n)
```

---

# Correctness Reasoning

## Claim 1

Every diagonal element is examined by the algorithm.

### Proof

For each row `i`, the primary diagonal element is:

```text
nums[i][i]
```

and the secondary diagonal element is:

```text
nums[i][n - i - 1]
```

The algorithm inspects exactly these values for every `i` from `0` to `n - 1`.

Therefore every diagonal element is included.

Proved.

---

## Claim 2

Any prime candidate not on a diagonal is irrelevant.

### Proof

The problem asks only for primes that lie on at least one diagonal.

So any matrix element outside both diagonals can never affect the answer.

Therefore restricting attention to diagonal cells is sufficient.

Proved.

---

## Claim 3

Keeping the maximum prime among the inspected diagonal elements yields the correct answer.

### Proof

Among all diagonal values, the algorithm checks which are prime and keeps the largest such value.

That is exactly the definition of the required answer.

If none are prime, the maximum remains `0`, which matches the problem statement.

Proved.

---

# Worked Examples

## Example 1

```text
nums = [[1,2,3],
        [5,6,7],
        [9,10,11]]
```

Primary diagonal:

```text
1, 6, 11
```

Secondary diagonal:

```text
3, 6, 9
```

All diagonal values considered:

```text
1, 6, 11, 3, 6, 9
```

Prime values:

```text
3, 11
```

Largest prime:

```text
11
```

Answer:

```text
11
```

---

## Example 2

```text
nums = [[1,2,3],
        [5,17,7],
        [9,11,10]]
```

Primary diagonal:

```text
1, 17, 10
```

Secondary diagonal:

```text
3, 17, 9
```

Prime values:

```text
3, 17
```

Largest prime:

```text
17
```

Answer:

```text
17
```

---

# Edge Cases

## 1. No prime on either diagonal

Example:

```text
[[1, 4],
 [6, 8]]
```

Diagonal values:

```text
1, 4, 6, 8
```

No primes.

Answer:

```text
0
```

---

## 2. Single cell matrix

If `n = 1`, there is only one cell, and it belongs to both diagonals.

Example:

```text
[[2]]
```

Since `2` is prime, answer is:

```text
2
```

If the value were `1`, answer would be `0`.

---

## 3. Center counted twice in odd-sized matrix

This does not matter because we are taking a maximum, not a frequency.

---

# Comparison of Approaches

## Approach 1 — Direct diagonal scan + trial division

Pros:

- simplest
- efficient enough
- best balance of clarity and performance

Cons:

- repeated prime checks if duplicate values appear

This is the recommended approach.

---

## Approach 2 — Unique diagonal values with a set

Pros:

- avoids repeated primality checks
- clean when duplicates are common

Cons:

- extra set storage

---

## Approach 3 — Sieve up to max diagonal value

Pros:

- constant-time prime lookup after preprocessing
- useful when many checks are needed

Cons:

- heavier memory usage
- more setup than necessary here

---

## Approach 4 — Sort candidates descending

Pros:

- directly matches “largest prime” thinking

Cons:

- sorting is unnecessary overhead

---

# Final Recommended Java Solution

```java
class Solution {
    public int diagonalPrime(int[][] nums) {
        int n = nums.length;
        int ans = 0;

        for (int i = 0; i < n; i++) {
            int a = nums[i][i];
            int b = nums[i][n - i - 1];

            if (isPrime(a)) {
                ans = Math.max(ans, a);
            }

            if (isPrime(b)) {
                ans = Math.max(ans, b);
            }
        }

        return ans;
    }

    private boolean isPrime(int x) {
        if (x < 2) return false;
        if (x == 2) return true;
        if (x % 2 == 0) return false;

        for (int d = 3; d * d <= x; d += 2) {
            if (x % d == 0) {
                return false;
            }
        }

        return true;
    }
}
```

---

# Complexity Summary

Let:

- `n = nums.length`
- `V = max diagonal value`

## Approach 1

```text
Time:  O(n * sqrt(V))
Space: O(1)
```

## Approach 2

```text
Time:  O(n + u * sqrt(V))
Space: O(u)
```

where `u` is the number of distinct diagonal values.

## Approach 3

```text
Time:  O(n + V log log V)
Space: O(V)
```

## Approach 4

```text
Time:  O(n log n + n * sqrt(V))
Space: O(n)
```

---

# Final Takeaway

The problem is much smaller than it first looks because only the two diagonals matter.

So the clean solution is:

1. scan both diagonals
2. test each value for primality
3. keep the largest prime found
4. return `0` if none exist
