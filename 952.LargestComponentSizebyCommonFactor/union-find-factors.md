# Approach 1: Union-Find via All Factors (Enumerate divisors)

## Intuition

Instead of checking all pairs, connect numbers through shared factors.

For each number `num`, enumerate factors `f` (2..sqrt(num)):

- if `f | num`, then `union(num, f)` and `union(num, num/f)`

This creates “factor hubs” so numbers that share any factor become connected through that factor.

## Algorithm

1. Build DSU over `[1..max(A)]`
2. For each `num`:
   - for each divisor `f` in `[2..sqrt(num)]`:
     - if `num % f == 0`:
       - `union(num, f)`
       - `union(num, num/f)`
3. Count component sizes by DSU representative of each `num`.

## Java Code

```java
class Solution {
    public int largestComponentSize(int[] A) {
        int max = 0;
        for (int x : A) max = Math.max(max, x);

        DisjointSetUnion dsu = new DisjointSetUnion(max);

        for (int num : A) {
            int r = (int) Math.sqrt(num);
            for (int f = 2; f <= r; f++) {
                if (num % f == 0) {
                    dsu.union(num, f);
                    dsu.union(num, num / f);
                }
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
}
```

## Complexity (High-level)

Let:

- `N = len(A)`
- `M = max(A)`

For each `num`, divisor enumeration is `O(sqrt(num))`, worst-case `O(sqrt(M))`.

- Time: `O(N * sqrt(M) * α(M))` (α from DSU is tiny)
- Space: `O(M + N)` (DSU arrays + counts)

**Tradeoff:** Often acceptable in Java, but may still be heavy for large `M` and many large numbers.

---
