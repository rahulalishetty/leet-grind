# Approach 2: Union-Find on Prime Factors (Reduced unions)

## Intuition

Enumerating all divisors includes many redundant factors.

Example:

- 12 has factors {2,3,4,6}
- Prime factors {2,3} already “generate” the rest.

By the **Fundamental Theorem of Arithmetic**, every integer has a unique prime factorization.
So it is enough to union using **prime factors**.

There are two variants:

1. Union number ↔ its prime factors
2. Union prime factors among themselves, and map each number to one prime factor (smaller DSU activity)

The write-up you pasted uses variant (2).

---

## Step A: Prime factorization (trial division)

A practical factorization for constraints uses trial division up to `sqrt(num)`:

```java
private List<Integer> primeFactorsUnique(int num) {
    List<Integer> pf = new ArrayList<>();
    for (int f = 2; f * f <= num; f++) {
        if (num % f == 0) {
            pf.add(f);
            while (num % f == 0) num /= f;
        }
    }
    if (num > 1) pf.add(num);
    return pf;
}
```

This returns **unique** prime factors.

---

## Algorithm (DSU over primes, numbers mapped to a prime)

1. DSU still sized to `max(A)` (simpler indexing), but we mostly union primes.
2. For each `num`:
   - compute unique prime factors `p1, p2, ...`
   - store mapping `num -> p1` (any one prime factor)
   - union adjacent prime factors `union(p1, p2), union(p2, p3), ...`
3. Counting:
   - representative for `num` is `find( numToPrime[num] )`
   - count sizes

## Java Code

```java
class Solution {
    public int largestComponentSize(int[] A) {
        int max = 0;
        for (int x : A) max = Math.max(max, x);

        DisjointSetUnion dsu = new DisjointSetUnion(max);
        HashMap<Integer, Integer> numToPrime = new HashMap<>();

        for (int num : A) {
            List<Integer> primes = primeFactorsUnique(num);
            // Map num to one of its prime factors (for membership lookup later)
            numToPrime.put(num, primes.get(0));

            // Union all prime factors of this number together
            for (int i = 0; i + 1 < primes.size(); i++) {
                dsu.union(primes.get(i), primes.get(i + 1));
            }
        }

        HashMap<Integer, Integer> cnt = new HashMap<>();
        int ans = 0;
        for (int num : A) {
            int gid = dsu.find(numToPrime.get(num));
            int v = cnt.getOrDefault(gid, 0) + 1;
            cnt.put(gid, v);
            ans = Math.max(ans, v);
        }
        return ans;
    }

    private List<Integer> primeFactorsUnique(int num) {
        List<Integer> pf = new ArrayList<>();
        for (int f = 2; f * f <= num; f++) {
            if (num % f == 0) {
                pf.add(f);
                while (num % f == 0) num /= f;
            }
        }
        if (num > 1) pf.add(num);
        return pf;
    }
}
```

## Complexity (High-level)

- Prime factorization per number by trial division: `O(sqrt(num))` worst-case
- But number of **prime factors** is small (`<= log2(M)` unique factors)
- DSU unions are fewer than Approach 1

Rough bound:

- Time: `O(N * sqrt(M))` for naive factorization, plus DSU overhead
- Space: `O(M + N)`

**Practical note:** Despite similar asymptotic bounds, this is often faster than Approach 1 because it performs far fewer union operations.

---

# Summary Comparison

| Approach         | Main idea                       | Pros                             | Cons                     |
| ---------------- | ------------------------------- | -------------------------------- | ------------------------ |
| 0) Pairwise GCD  | union pairs if gcd>1            | simplest conceptually            | `O(N^2)` TLE             |
| 1) All factors   | union num with every divisor    | avoids pairwise; straightforward | many redundant unions    |
| 2) Prime factors | union using prime factorization | fewer unions; typically fastest  | needs factorization step |

---
