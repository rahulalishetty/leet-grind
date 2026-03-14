# 996. Number of Squareful Arrays

## Approach 1: Backtracking

### Intuition

Construct a graph where an edge from value **i** to **j** exists if:

```
A[i] + A[j]
```

is a **perfect square**.

Our goal is to count **Hamiltonian paths** in this graph — paths that visit every node exactly once.

To avoid redundant permutations caused by duplicate numbers, we track the **frequency of each value**.

---

## Algorithm

1. Count occurrences of each number.
2. Build a graph where an edge exists if the sum of two numbers is a perfect square.
3. Use **DFS backtracking**:
   - At each step choose a neighbor whose remaining count is > 0.
   - Reduce the count and continue recursion.
4. When all numbers are used (`todo == 0`), we found a valid permutation.

---

## Java Implementation

```java
class Solution {
    Map<Integer, Integer> count;
    Map<Integer, List<Integer>> graph;

    public int numSquarefulPerms(int[] A) {
        int N = A.length;
        count = new HashMap();
        graph = new HashMap();

        for (int x: A)
            count.put(x, count.getOrDefault(x, 0) + 1);

        for (int x: count.keySet())
            graph.put(x, new ArrayList());

        for (int x: count.keySet())
            for (int y: count.keySet()) {
                int r = (int) (Math.sqrt(x + y) + 0.5);
                if (r * r == x + y)
                    graph.get(x).add(y);
            }

        int ans = 0;
        for (int x: count.keySet())
            ans += dfs(x, N - 1);

        return ans;
    }

    public int dfs(int x, int todo) {
        count.put(x, count.get(x) - 1);

        int ans = 1;
        if (todo != 0) {
            ans = 0;
            for (int y: graph.get(x))
                if (count.get(y) != 0)
                    ans += dfs(y, todo - 1);
        }

        count.put(x, count.get(x) + 1);
        return ans;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(N^N)
```

Where **N** is the length of the array.

The graph structure (square condition) significantly reduces branching in practice.

### Space Complexity

```
O(N)
```

Used for recursion stack and graph storage.

---

# Approach 2: Dynamic Programming

## Intuition

We again build a graph connecting numbers whose sum is a **perfect square**.

Since **N ≤ 12**, we can represent visited nodes using a **bitmask** and apply **DP + DFS**.

Define:

```
dfs(node, visited)
```

- `node` = current index
- `visited` = bitmask representing visited nodes

This returns the number of valid permutations continuing from `node`.

To avoid recomputation we store results in a memo table.

If there are duplicate values in the array, we divide the final answer by the factorial of their counts.

---

## Java Implementation

```java
class Solution {
    int N;
    Map<Integer, List<Integer>> graph;
    Integer[][] memo;

    public int numSquarefulPerms(int[] A) {
        N = A.length;
        graph = new HashMap();
        memo = new Integer[N][1 << N];

        for (int i = 0; i < N; ++i)
            graph.put(i, new ArrayList());

        for (int i = 0; i < N; ++i)
            for (int j = i + 1; j < N; ++j) {
                int r = (int) (Math.sqrt(A[i] + A[j]) + 0.5);
                if (r * r == A[i] + A[j]) {
                    graph.get(i).add(j);
                    graph.get(j).add(i);
                }
            }

        int[] factorial = new int[20];
        factorial[0] = 1;
        for (int i = 1; i < 20; ++i)
            factorial[i] = i * factorial[i - 1];

        int ans = 0;
        for (int i = 0; i < N; ++i)
            ans += dfs(i, 1 << i);

        Map<Integer, Integer> count = new HashMap();
        for (int x : A)
            count.put(x, count.getOrDefault(x, 0) + 1);

        for (int v : count.values())
            ans /= factorial[v];

        return ans;
    }

    public int dfs(int node, int visited) {
        if (visited == (1 << N) - 1)
            return 1;

        if (memo[node][visited] != null)
            return memo[node][visited];

        int ans = 0;

        for (int nei : graph.get(node))
            if (((visited >> nei) & 1) == 0)
                ans += dfs(nei, visited | (1 << nei));

        memo[node][visited] = ans;
        return ans;
    }
}
```

---

# Complexity Analysis

### Time Complexity

```
O(N^2 * 2^N)
```

Explanation:

- `N * 2^N` DP states
- Each state may explore up to `N` neighbors

### Space Complexity

```
O(N * 2^N)
```

Used for memoization table and recursion stack.
