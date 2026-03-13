# 1766. Tree of Coprimes — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int[] getCoprimes(int[] nums, int[][] edges) {

    }
}
```

---

# Problem Restatement

We are given:

- a tree with `n` nodes labeled from `0` to `n - 1`
- `nums[i]` = value of node `i`
- `edges` = undirected edges of the tree
- root is node `0`

For each node `i`, we must find the **closest ancestor** whose value is **coprime** with `nums[i]`.

If no such ancestor exists, answer is `-1`.

Two numbers are coprime if:

```text
gcd(x, y) == 1
```

---

# Key Observations

## 1. This is a tree

So every node has exactly one simple path to the root.

That means the ancestors of a node are exactly the nodes currently on the DFS path from the root to that node.

---

## 2. `nums[i] <= 50`

This is the most important constraint.

There are only **50 possible values**, so:

- coprime relationships between values can be precomputed
- when processing a node, instead of scanning all ancestors blindly, we can focus only on values that are coprime with `nums[i]`

This enables an efficient DFS-based solution.

---

# Approach 1 — DFS + Track Latest Ancestor for Each Value (Recommended)

## Core Idea

During DFS from the root, maintain for each value `1..50`:

- the **deepest ancestor** currently on the path having that value
- the corresponding node index and depth

When we are at node `u`:

1. Look at all values `v` such that `gcd(nums[u], v) == 1`
2. Among those, choose the tracked ancestor with maximum depth
3. That ancestor is the closest valid ancestor

Then:

- add current node to the tracking structure for `nums[u]`
- recurse into children
- backtrack after finishing children

---

## Why this works

At any point in DFS, the tracked nodes represent exactly the ancestors on the current root-to-node path.

Because for each value we keep the deepest ancestor currently available, when multiple coprime values exist we can compare depths and select the closest one.

Since values are only from `1..50`, checking all coprime possibilities is very cheap.

---

## Precomputation

We precompute coprime lists:

```text
coprimes[x] = all values y in [1..50] such that gcd(x, y) == 1
```

This avoids repeated gcd work during DFS.

---

## Java Code

```java
import java.util.*;

class Solution {
    List<Integer>[] graph;
    List<Integer>[] coprimes;
    int[] nums;
    int[] ans;

    // latestNode[val] = deepest node on current path with value = val
    int[] latestNode = new int[51];
    int[] latestDepth = new int[51];

    public int[] getCoprimes(int[] nums, int[][] edges) {
        int n = nums.length;
        this.nums = nums;
        this.ans = new int[n];

        Arrays.fill(ans, -1);
        Arrays.fill(latestNode, -1);
        Arrays.fill(latestDepth, -1);

        buildGraph(n, edges);
        buildCoprimes();

        dfs(0, -1, 0);

        return ans;
    }

    private void dfs(int u, int parent, int depth) {
        int bestNode = -1;
        int bestDepth = -1;

        for (int val : coprimes[nums[u]]) {
            if (latestNode[val] != -1 && latestDepth[val] > bestDepth) {
                bestDepth = latestDepth[val];
                bestNode = latestNode[val];
            }
        }

        ans[u] = bestNode;

        int oldNode = latestNode[nums[u]];
        int oldDepth = latestDepth[nums[u]];

        latestNode[nums[u]] = u;
        latestDepth[nums[u]] = depth;

        for (int v : graph[u]) {
            if (v == parent) continue;
            dfs(v, u, depth + 1);
        }

        latestNode[nums[u]] = oldNode;
        latestDepth[nums[u]] = oldDepth;
    }

    private void buildGraph(int n, int[][] edges) {
        graph = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int[] e : edges) {
            int u = e[0], v = e[1];
            graph[u].add(v);
            graph[v].add(u);
        }
    }

    private void buildCoprimes() {
        coprimes = new ArrayList[51];
        for (int i = 1; i <= 50; i++) {
            coprimes[i] = new ArrayList<>();
            for (int j = 1; j <= 50; j++) {
                if (gcd(i, j) == 1) {
                    coprimes[i].add(j);
                }
            }
        }
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

---

## Complexity

### Graph construction

```text
O(n)
```

### Coprime precompute

```text
O(50 * 50)
```

### DFS

For each node, we scan all coprime values of `nums[u]`.
That is at most 50.

So total DFS cost is:

```text
O(50 * n) = O(n)
```

### Total

```text
O(n)
```

with a small constant factor.

This is the best practical solution.

---

# Approach 2 — DFS + Stack of Ancestors for Each Value

## Idea

Instead of keeping only the latest node for each value, maintain a **stack** for each value.

Why?

Because while DFS goes deeper and then backtracks, multiple ancestors with the same value may appear on the path. A stack naturally handles this.

For each value `v`, `stacks[v]` contains all ancestors currently on the path with that value.

When processing a node:

1. Check all values coprime with `nums[u]`
2. Look at the top of each corresponding stack
3. Take the one with greatest depth
4. Push current node onto stack of `nums[u]`
5. DFS children
6. Pop current node during backtracking

This is more explicit than Approach 1 and sometimes easier to reason about.

---

## Java Code

```java
import java.util.*;

class Solution {
    List<Integer>[] graph;
    List<Integer>[] coprimes;
    Deque<int[]>[] stacks; // each entry = {node, depth}
    int[] nums;
    int[] ans;

    public int[] getCoprimes(int[] nums, int[][] edges) {
        int n = nums.length;
        this.nums = nums;
        this.ans = new int[n];

        Arrays.fill(ans, -1);

        buildGraph(n, edges);
        buildCoprimes();

        stacks = new ArrayDeque[51];
        for (int i = 1; i <= 50; i++) {
            stacks[i] = new ArrayDeque<>();
        }

        dfs(0, -1, 0);

        return ans;
    }

    private void dfs(int u, int parent, int depth) {
        int bestNode = -1;
        int bestDepth = -1;

        for (int val : coprimes[nums[u]]) {
            if (!stacks[val].isEmpty()) {
                int[] top = stacks[val].peekLast();
                if (top[1] > bestDepth) {
                    bestDepth = top[1];
                    bestNode = top[0];
                }
            }
        }

        ans[u] = bestNode;

        stacks[nums[u]].addLast(new int[]{u, depth});

        for (int v : graph[u]) {
            if (v == parent) continue;
            dfs(v, u, depth + 1);
        }

        stacks[nums[u]].removeLast();
    }

    private void buildGraph(int n, int[][] edges) {
        graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();

        for (int[] e : edges) {
            int u = e[0], v = e[1];
            graph[u].add(v);
            graph[v].add(u);
        }
    }

    private void buildCoprimes() {
        coprimes = new ArrayList[51];
        for (int i = 1; i <= 50; i++) {
            coprimes[i] = new ArrayList<>();
            for (int j = 1; j <= 50; j++) {
                if (gcd(i, j) == 1) coprimes[i].add(j);
            }
        }
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

---

## Complexity

Still effectively:

```text
O(n)
```

because each node is pushed and popped once, and each node checks only up to 50 values.

---

# Approach 3 — Naive Ancestor Scan for Each Node (Too Slow)

## Idea

A straightforward approach is:

1. Root the tree
2. Store parent and depth
3. For each node, walk upward through its ancestors until root
4. Return the first ancestor whose value is coprime

This is logically correct, but in the worst case the tree is a chain, so one query can scan `O(n)` ancestors.

Doing that for all nodes leads to:

```text
O(n^2)
```

which is too slow for `n = 10^5`.

---

## Java Code

```java
import java.util.*;

class Solution {
    List<Integer>[] graph;
    int[] nums;
    int[] parent;
    int[] ans;

    public int[] getCoprimes(int[] nums, int[][] edges) {
        int n = nums.length;
        this.nums = nums;
        this.ans = new int[n];
        this.parent = new int[n];
        Arrays.fill(parent, -1);

        buildGraph(n, edges);
        buildParent(0, -1);

        for (int u = 0; u < n; u++) {
            int p = parent[u];
            ans[u] = -1;

            while (p != -1) {
                if (gcd(nums[u], nums[p]) == 1) {
                    ans[u] = p;
                    break;
                }
                p = parent[p];
            }
        }

        return ans;
    }

    private void buildParent(int u, int par) {
        parent[u] = par;
        for (int v : graph[u]) {
            if (v == par) continue;
            buildParent(v, u);
        }
    }

    private void buildGraph(int n, int[][] edges) {
        graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();

        for (int[] e : edges) {
            int u = e[0], v = e[1];
            graph[u].add(v);
            graph[v].add(u);
        }
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

---

## Why this is not good enough

If the tree is a chain:

```text
0 - 1 - 2 - 3 - 4 - ... - (n-1)
```

then for node `n-1`, you may scan almost all previous nodes.
Doing this for every node becomes quadratic.

So this approach is useful for intuition, but not for the constraints.

---

# Approach 4 — Binary Lifting / Jump Pointers (Not Natural Here)

## Idea

One might think of binary lifting because the problem asks about ancestors.

But binary lifting usually works well when the ancestor property is **monotonic** or when we want the `k-th` ancestor, LCA, etc.

Here the condition depends on:

```text
gcd(nums[u], nums[ancestor]) == 1
```

This depends on the values and is not monotonic over depth.

So binary lifting does not directly help unless heavily augmented, and such augmentation becomes more complicated than the DFS + value tracking approach.

Thus it is generally not the right tool here.

---

# Detailed Walkthrough of Recommended Approach

## Example 1

```text
nums = [2,3,3,2]
edges = [[0,1],[1,2],[1,3]]
```

Tree:

```text
0(2)
 \
  1(3)
 / \
2(3) 3(2)
```

We DFS from node `0`.

### Node 0

- no ancestors
- answer = `-1`
- track value `2 -> node 0`

### Node 1 (value = 3)

- values coprime with 3 include 1,2,4,5,...
- current tracked ancestor with value 2 is node 0
- answer = `0`
- track value `3 -> node 1`

### Node 2 (value = 3)

- coprime values with 3 include 2,4,5,...
- current tracked value 3 is node 1, but 3 is not coprime with 3
- tracked value 2 is node 0
- answer = `0`

### Node 3 (value = 2)

- coprime values with 2 include 1,3,5,...
- tracked value 3 is node 1
- answer = `1`

Result:

```text
[-1,0,0,1]
```

---

# Why the Value Constraint Changes Everything

If `nums[i]` could be huge, checking all possible coprime ancestors by value would be difficult.

But since:

```text
1 <= nums[i] <= 50
```

we only have 50 categories of values.

That means during DFS we can store path information indexed by value instead of scanning arbitrary ancestors.

This transforms the problem from something that looks hard into a very manageable DFS state-tracking problem.

---

# Common Pitfalls

## 1. Forgetting backtracking

When DFS leaves a node, its value must be removed from the current path state.

Otherwise nodes in one subtree may incorrectly affect nodes in another subtree.

---

## 2. Returning any coprime ancestor instead of the closest one

We need the **closest** ancestor, so depth comparison matters.

---

## 3. Scanning the whole ancestor chain every time

That leads to `O(n^2)` in the worst case.

---

## 4. Recomputing gcd for many repeated value pairs

Since values are only `1..50`, precompute coprime relationships once.

---

# Best Approach

## Recommended: DFS + latest ancestor per value

This is best because:

- it uses the small value range perfectly
- it is linear in the number of nodes
- it is simple and elegant
- it naturally supports “closest ancestor” via depth comparison

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    List<Integer>[] graph;
    List<Integer>[] coprimes;
    int[] nums;
    int[] ans;
    int[] latestNode = new int[51];
    int[] latestDepth = new int[51];

    public int[] getCoprimes(int[] nums, int[][] edges) {
        int n = nums.length;
        this.nums = nums;
        this.ans = new int[n];

        Arrays.fill(ans, -1);
        Arrays.fill(latestNode, -1);
        Arrays.fill(latestDepth, -1);

        buildGraph(n, edges);
        buildCoprimes();

        dfs(0, -1, 0);

        return ans;
    }

    private void dfs(int u, int parent, int depth) {
        int bestNode = -1;
        int bestDepth = -1;

        for (int val : coprimes[nums[u]]) {
            if (latestNode[val] != -1 && latestDepth[val] > bestDepth) {
                bestDepth = latestDepth[val];
                bestNode = latestNode[val];
            }
        }

        ans[u] = bestNode;

        int oldNode = latestNode[nums[u]];
        int oldDepth = latestDepth[nums[u]];

        latestNode[nums[u]] = u;
        latestDepth[nums[u]] = depth;

        for (int v : graph[u]) {
            if (v == parent) continue;
            dfs(v, u, depth + 1);
        }

        latestNode[nums[u]] = oldNode;
        latestDepth[nums[u]] = oldDepth;
    }

    private void buildGraph(int n, int[][] edges) {
        graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();

        for (int[] e : edges) {
            int u = e[0], v = e[1];
            graph[u].add(v);
            graph[v].add(u);
        }
    }

    private void buildCoprimes() {
        coprimes = new ArrayList[51];
        for (int i = 1; i <= 50; i++) {
            coprimes[i] = new ArrayList<>();
            for (int j = 1; j <= 50; j++) {
                if (gcd(i, j) == 1) {
                    coprimes[i].add(j);
                }
            }
        }
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

---

# Final Complexity Summary

Using the recommended approach:

- **Graph build:** `O(n)`
- **Coprime precompute:** `O(50 * 50)`
- **DFS:** `O(50 * n)` which is effectively `O(n)`

## Final Big-O

```text
Time:  O(n)
Space: O(n)
```

This is the optimal and standard solution for the given constraints.
