# Big Picture: Graph Partition → Disjoint Set (Union-Find)

We want to partition nodes into connected components and return the maximum size.

Union-Find (Disjoint Set Union, DSU) supports:

- `find(x)`: returns representative (component id) of x
- `union(x, y)`: merges the components containing x and y

After performing unions, we can count how many input numbers map to each final representative.

Pseudo idea:

```text
for num in A:
  component = find(num)
  count[component]++
answer = max(count.values())
```

---

# Union-Find (DSU) Reference Implementation (Java)

```java
class DisjointSetUnion {
    private int[] parent;
    private int[] size;

    public DisjointSetUnion(int n) {
        parent = new int[n + 1];
        size = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]); // path compression
        return parent[x];
    }

    public void union(int x, int y) {
        int px = find(x), py = find(y);
        if (px == py) return;

        // union by size
        if (size[px] > size[py]) {
            int tmp = px; px = py; py = tmp;
        }
        parent[px] = py;
        size[py] += size[px];
    }
}
```

**Amortized time:** with path compression + union by size/rank, total runtime for `M` operations on `N` elements is nearly linear: `O(M * α(N))` where `α` is inverse Ackermann (very small).

---

# Approach 0 (Baseline): Pairwise GCD Graph + Union-Find (Quadratic)

## Intuition

Connect every pair `(i, j)` if `gcd(A[i], A[j]) > 1`, then union them.

## Algorithm

1. For all pairs `i < j`:
   - if `gcd(A[i], A[j]) > 1`: `union(A[i], A[j])`
2. Count component sizes via `find`.

## Java Sketch

```java
class Solution {
    public int largestComponentSize(int[] A) {
        int max = 0;
        for (int x : A) max = Math.max(max, x);

        DisjointSetUnion dsu = new DisjointSetUnion(max);

        for (int i = 0; i < A.length; i++) {
            for (int j = i + 1; j < A.length; j++) {
                if (gcd(A[i], A[j]) > 1) dsu.union(A[i], A[j]);
            }
        }

        HashMap<Integer, Integer> cnt = new HashMap<>();
        int ans = 0;
        for (int x : A) {
            int p = dsu.find(x);
            int v = cnt.getOrDefault(p, 0) + 1;
            cnt.put(p, v);
            ans = Math.max(ans, v);
        }
        return ans;
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

## Complexity

- Time: `O(N^2 * log M)` where `M = max(A)`
- Space: `O(M)` for DSU + `O(N)` counting

**Why it TLEs:** `N` can be up to 20,000, so `N^2` is too large.

---
