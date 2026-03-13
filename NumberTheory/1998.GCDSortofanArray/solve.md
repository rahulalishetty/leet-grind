# 1998. GCD Sort of an Array — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public boolean gcdSort(int[] nums) {

    }
}
```

---

# Problem Restatement

We are given an array `nums`.

We may swap two elements `nums[i]` and `nums[j]` whenever:

```text
gcd(nums[i], nums[j]) > 1
```

We may perform this operation any number of times.

We need to determine whether it is possible to sort the array into non-decreasing order.

---

# Core Insight

The key is to stop thinking in terms of individual swaps and start thinking in terms of **connectivity**.

If two numbers can be connected through a chain of swaps where every adjacent pair has gcd greater than 1, then they effectively belong to the same connected component.

Example:

```text
10 <-> 15 because gcd(10,15)=5
15 <-> 3  because gcd(15,3)=3
```

So even though:

```text
gcd(10,3)=1
```

the values `10` and `3` are still in the same swappable component through `15`.

That means:

> Any values inside the same connected component can be rearranged arbitrarily among the indices containing those values.

So the problem becomes:

1. build connected components of values based on shared prime factors
2. compare the original array with its sorted version
3. for every index, check whether `nums[i]` can move to the value required in sorted order, i.e. whether the two values lie in the same component

---

# Why Prime Factors Matter

Two numbers have:

```text
gcd(a, b) > 1
```

if and only if they share some prime factor.

So rather than connecting every pair of numbers directly, we can connect a number to its prime factors.

If two numbers share a prime factor, they become connected through that prime.

This is exactly what Union-Find / DSU is good for.

---

# Approach 1 — Union-Find by Number and Prime Factors (Recommended)

## Idea

For each number in `nums`:

1. factorize it into its prime factors
2. union the number with each of its prime factors

Then:

- create a sorted copy of `nums`
- for each index `i`, check whether:
  - `nums[i]` and `sorted[i]` belong to the same DSU component
- if not, sorting is impossible

---

## Why this works

If `nums[i]` needs to become `sorted[i]`, then the value currently at index `i` must be able to move through allowed swaps into that target value’s position.

That is possible exactly when the original value and target value belong to the same connected swappable component.

Thus the condition:

```text
find(nums[i]) == find(sorted[i])
```

for every index is both necessary and sufficient.

---

## Java Code

```java
import java.util.*;

class Solution {
    static class DSU {
        int[] parent;
        int[] rank;

        DSU(int n) {
            parent = new int[n + 1];
            rank = new int[n + 1];
            for (int i = 0; i <= n; i++) {
                parent[i] = i;
            }
        }

        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        void union(int a, int b) {
            int ra = find(a);
            int rb = find(b);
            if (ra == rb) return;

            if (rank[ra] < rank[rb]) {
                parent[ra] = rb;
            } else if (rank[ra] > rank[rb]) {
                parent[rb] = ra;
            } else {
                parent[rb] = ra;
                rank[ra]++;
            }
        }
    }

    public boolean gcdSort(int[] nums) {
        int maxVal = 0;
        for (int x : nums) {
            maxVal = Math.max(maxVal, x);
        }

        DSU dsu = new DSU(maxVal);

        for (int x : nums) {
            int value = x;
            for (int p = 2; p * p <= value; p++) {
                if (value % p == 0) {
                    dsu.union(x, p);
                    while (value % p == 0) {
                        value /= p;
                    }
                }
            }
            if (value > 1) {
                dsu.union(x, value);
            }
        }

        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        for (int i = 0; i < nums.length; i++) {
            if (dsu.find(nums[i]) != dsu.find(sorted[i])) {
                return false;
            }
        }

        return true;
    }
}
```

---

## Complexity

Let:

```text
n = nums.length
M = max(nums)
```

Each number is factorized in about:

```text
O(sqrt(M))
```

in the simple version above.

So total:

```text
Time:  O(n * sqrt(M) + n log n)
Space: O(M)
```

This is acceptable for the constraints, but we can do even better with SPF preprocessing.

---

# Approach 2 — Union-Find + Smallest Prime Factor Sieve

## Idea

Instead of trial-dividing each number up to its square root, precompute the **smallest prime factor (SPF)** for every value up to `max(nums)`.

Then prime factorization becomes much faster.

This is the same DSU logic as Approach 1, but more optimized.

---

## Why SPF helps

If we know the smallest prime factor of every number, then factorization becomes repeated division by SPF, which is much faster than testing every possible divisor.

This is especially useful when many numbers appear and `max(nums)` is moderately large.

---

## Java Code

```java
import java.util.*;

class Solution {
    static class DSU {
        int[] parent;
        int[] size;

        DSU(int n) {
            parent = new int[n + 1];
            size = new int[n + 1];
            for (int i = 0; i <= n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            while (parent[x] != x) {
                parent[x] = parent[parent[x]];
                x = parent[x];
            }
            return x;
        }

        void union(int a, int b) {
            int ra = find(a);
            int rb = find(b);
            if (ra == rb) return;

            if (size[ra] < size[rb]) {
                int temp = ra;
                ra = rb;
                rb = temp;
            }

            parent[rb] = ra;
            size[ra] += size[rb];
        }
    }

    public boolean gcdSort(int[] nums) {
        int maxVal = 0;
        for (int x : nums) {
            maxVal = Math.max(maxVal, x);
        }

        int[] spf = buildSPF(maxVal);
        DSU dsu = new DSU(maxVal);

        for (int x : nums) {
            int val = x;
            Set<Integer> primes = new HashSet<>();

            while (val > 1) {
                int p = spf[val];
                primes.add(p);
                while (val % p == 0) {
                    val /= p;
                }
            }

            for (int p : primes) {
                dsu.union(x, p);
            }
        }

        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        for (int i = 0; i < nums.length; i++) {
            if (dsu.find(nums[i]) != dsu.find(sorted[i])) {
                return false;
            }
        }

        return true;
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

---

## Complexity

- SPF preprocessing:

```text
O(M log log M)
```

- factorization of all numbers: near linear in total number of prime factors
- sorting:

```text
O(n log n)
```

So:

```text
Time:  O(M log log M + n log n + factorization work)
Space: O(M)
```

This is usually the best practical implementation.

---

# Approach 3 — Graph of Values + BFS/DFS Components

## Idea

We can think of numbers as graph nodes.

Two numbers are connected if they share a prime factor.
Or equivalently, numbers can be connected through prime factor nodes.

After building the graph, we find connected components using BFS or DFS.

Then compare original array and sorted array using component IDs.

This is logically valid, but DSU is simpler and usually cleaner.

---

## Java Code

```java
import java.util.*;

class Solution {
    public boolean gcdSort(int[] nums) {
        int maxVal = 0;
        for (int x : nums) maxVal = Math.max(maxVal, x);

        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i <= maxVal; i++) {
            graph.add(new ArrayList<>());
        }

        for (int x : nums) {
            int val = x;
            Set<Integer> factors = new HashSet<>();

            for (int p = 2; p * p <= val; p++) {
                if (val % p == 0) {
                    factors.add(p);
                    while (val % p == 0) val /= p;
                }
            }
            if (val > 1) factors.add(val);

            for (int p : factors) {
                graph.get(x).add(p);
                graph.get(p).add(x);
            }
        }

        int[] comp = new int[maxVal + 1];
        Arrays.fill(comp, -1);
        int compId = 0;

        for (int x : nums) {
            if (comp[x] != -1) continue;

            Queue<Integer> q = new ArrayDeque<>();
            q.offer(x);
            comp[x] = compId;

            while (!q.isEmpty()) {
                int cur = q.poll();
                for (int nei : graph.get(cur)) {
                    if (comp[nei] == -1) {
                        comp[nei] = compId;
                        q.offer(nei);
                    }
                }
            }

            compId++;
        }

        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        for (int i = 0; i < nums.length; i++) {
            if (comp[nums[i]] != comp[sorted[i]]) {
                return false;
            }
        }

        return true;
    }
}
```

---

## Why it is less preferred

This works, but:

- explicit graph building is heavier
- BFS/DFS over a value-factor graph is more cumbersome
- DSU does the same job more naturally

So this is more of a conceptual alternate view.

---

# Approach 4 — Direct Swap Simulation / Greedy (Incorrect or Too Slow)

## Idea

One may try to repeatedly perform swaps that improve the order:

- compare with sorted array
- try to swap numbers if gcd > 1
- continue until sorted or stuck

This is not a reliable algorithm.

Why?

Because local swap choices do not capture the full connectivity structure.
A number may need to move through a long chain of allowed swaps.

So direct simulation either becomes:

- incorrect, if greedy
- too slow, if exhaustive

---

## Why greedy fails

Suppose a number cannot swap directly with its target, but can reach it through intermediate numbers.
A local greedy approach may miss this global connectivity.

The DSU/component viewpoint is the right abstraction.

---

# Detailed Walkthrough of the Recommended Approach

## Example 1

```text
nums = [7,21,3]
sorted = [3,7,21]
```

Prime factorizations:

- `7 = 7`
- `21 = 3 * 7`
- `3 = 3`

Union steps:

- union `7` with `7`
- union `21` with `3`
- union `21` with `7`
- union `3` with `3`

Now all three values:

```text
3, 7, 21
```

become connected.

So at each index:

- `7` can move to `3`
- `21` can move to `7`
- `3` can move to `21`

Thus sorting is possible.

---

## Example 2

```text
nums = [5,2,6,2]
sorted = [2,2,5,6]
```

Prime factorizations:

- `5 = 5`
- `2 = 2`
- `6 = 2 * 3`

So:

- `2` and `6` are connected
- `5` is isolated

But in the sorted array, `5` needs to move from index `0` to index `2`, and that requires swapping with values it cannot connect to.

So sorting is impossible.

---

# Important Correctness Argument

Why is checking:

```text
find(nums[i]) == find(sorted[i])
```

enough?

Because within one connected component, the allowed swaps generate enough flexibility to permute values among the indices occupied by that component.

So if for every position the current value and desired value lie in the same component, then the sorted arrangement is achievable.

If not, then some value would need to cross component boundaries, which is impossible.

---

# Common Pitfalls

## 1. Checking only direct gcd swaps

Direct gcd is not enough.

A value may move indirectly through a chain of values.

---

## 2. Building edges between all pairs of numbers

That would be:

```text
O(n^2)
```

too slow for `n = 3 * 10^4`.

Use prime factor connectivity instead.

---

## 3. Forgetting duplicate values

Duplicates are naturally handled because the final check compares values position-by-position against the sorted copy.

---

## 4. Confusing index components with value components

The swappability is determined by the values and their prime-factor connectivity, not by index graph structure.

---

# Best Approach

## Recommended: Union-Find + Prime Factors

This is the best solution because:

- captures transitive swappability correctly
- avoids pairwise gcd checks
- works efficiently within constraints
- leads to a clean correctness condition

Among implementations, the SPF-optimized DSU version is usually the strongest.

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    static class DSU {
        int[] parent;
        int[] size;

        DSU(int n) {
            parent = new int[n + 1];
            size = new int[n + 1];
            for (int i = 0; i <= n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            while (parent[x] != x) {
                parent[x] = parent[parent[x]];
                x = parent[x];
            }
            return x;
        }

        void union(int a, int b) {
            int ra = find(a);
            int rb = find(b);
            if (ra == rb) return;

            if (size[ra] < size[rb]) {
                int temp = ra;
                ra = rb;
                rb = temp;
            }

            parent[rb] = ra;
            size[ra] += size[rb];
        }
    }

    public boolean gcdSort(int[] nums) {
        int maxVal = 0;
        for (int x : nums) {
            maxVal = Math.max(maxVal, x);
        }

        int[] spf = buildSPF(maxVal);
        DSU dsu = new DSU(maxVal);

        for (int x : nums) {
            int val = x;
            Set<Integer> primes = new HashSet<>();

            while (val > 1) {
                int p = spf[val];
                primes.add(p);
                while (val % p == 0) {
                    val /= p;
                }
            }

            for (int p : primes) {
                dsu.union(x, p);
            }
        }

        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        for (int i = 0; i < nums.length; i++) {
            if (dsu.find(nums[i]) != dsu.find(sorted[i])) {
                return false;
            }
        }

        return true;
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

---

# Complexity Summary

Let:

```text
n = nums.length
M = max(nums)
```

Then for the recommended SPF + DSU solution:

- SPF sieve: `O(M log log M)`
- factorizing numbers: near linear in total prime factors
- sorting: `O(n log n)`

Overall:

```text
Time:  O(M log log M + n log n)
Space: O(M)
```

This is efficient for the constraints.

---

# Final Takeaway

The problem is really about **connectivity under shared prime factors**.

The important mental shift is:

- do not simulate swaps
- do not check all gcd pairs
- instead build connected components using prime factors

Then sorting is possible exactly when every value can move to the value required at its sorted position.
