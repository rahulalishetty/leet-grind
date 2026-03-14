# 2445. Number of Nodes With Value One

## Problem summary

We have a tree with nodes labeled from `1` to `n`.

The parent of node `v` is:

```text
floor(v / 2)
```

So this is the usual implicit binary tree indexing:

- left child of `x` is `2 * x`
- right child of `x` is `2 * x + 1`
- as long as the child label is `<= n`

Initially, every node has value `0`.

For each query `q`, we flip every node in the subtree rooted at `q`:

- `0 -> 1`
- `1 -> 0`

We must return the number of nodes whose final value is `1`.

---

# Key observation

A node’s final value depends only on how many query subtrees contain it.

If a node is included in an **odd** number of flips, it ends up as `1`.

If included in an **even** number of flips, it ends up as `0`.

So for every node, we only care about the **parity** of flips affecting it.

---

# Important structural observation

A node `u` belongs to the subtree of query node `q` **iff** `q` is an ancestor of `u`.

So instead of "apply every query to a whole subtree", we can think:

> For each node, count how many query nodes appear on the path from the root to that node.

If that count is odd, the node is `1`.

That viewpoint makes DFS-based solutions natural.

---

# Approach 1: Brute force subtree flipping

## Intuition

The most direct approach is:

1. Build the tree
2. Maintain a value for each node
3. For every query, DFS through that whole subtree and flip every node
4. At the end, count how many nodes are `1`

This is straightforward but too slow in the worst case.

Example bad case:

- `queries = [1, 1, 1, 1, ...]`
- every query flips almost the whole tree

That leads to quadratic behavior.

---

## Algorithm

1. Build adjacency / child representation using the implicit binary tree rule
2. Maintain `state[node]` initialized to `0`
3. For each query:
   - DFS from that query node
   - flip `state[x] ^= 1`
4. Count all nodes with value `1`

---

## Java code

```java
class Solution {
    public int numberOfNodes(int n, int[] queries) {
        int[] state = new int[n + 1];

        for (int q : queries) {
            flipSubtree(q, n, state);
        }

        int ans = 0;
        for (int i = 1; i <= n; i++) {
            if (state[i] == 1) {
                ans++;
            }
        }
        return ans;
    }

    private void flipSubtree(int node, int n, int[] state) {
        if (node > n) {
            return;
        }

        state[node] ^= 1;

        flipSubtree(node * 2, n, state);
        flipSubtree(node * 2 + 1, n, state);
    }
}
```

---

## Complexity

### Time complexity

Worst case:

```text
O(n * q)
```

where `q = queries.length`.

This can become `O(10^10)` in the worst case, so it is not acceptable.

### Space complexity

```text
O(h)
```

for recursion stack, worst-case `O(n)` if the tree were skewed, though here the tree height is about `O(log n)` because the labeling structure is like a complete binary tree.

---

# Approach 2: Count query parity first, then DFS from root

## Intuition

Instead of applying each query to all descendants explicitly, we can compress the queries first.

Suppose node `x` appears in `queries` `k` times.

Flipping the subtree rooted at `x` `k` times only matters by parity:

- even `k` -> no net effect
- odd `k` -> one effective flip

So first we compute:

```text
flip[x] = number of queries equal to x modulo 2
```

Then we do one DFS from the root.

As we move from parent to child, we maintain the cumulative parity of flips seen so far on the path.

For a node `u`:

```text
currentParity = parity of all query nodes on root-to-u path
```

If `currentParity == 1`, then node `u` ends up as `1`.

This is efficient because each node is visited once.

---

## Why this works

A query at node `x` flips exactly the nodes in `x`’s subtree.

That means node `u` is flipped by query `x` iff `x` is an ancestor of `u`.

So the total number of flips affecting `u` is exactly the sum of query counts on the ancestor chain of `u`.

Hence a root-to-leaf DFS carrying parity gives the answer directly.

---

## Algorithm

1. Create array `flip[1..n]`
2. For every query `q`, toggle:

```java
flip[q] ^= 1;
```

3. DFS from node `1` with current parity `0`
4. At each node:
   - newParity = parentParity XOR flip[node]
   - if newParity == 1, increment answer
   - recurse to children if they exist
5. Return answer

---

## Java code

```java
class Solution {
    private int answer = 0;

    public int numberOfNodes(int n, int[] queries) {
        int[] flip = new int[n + 1];

        for (int q : queries) {
            flip[q] ^= 1;
        }

        dfs(1, 0, n, flip);
        return answer;
    }

    private void dfs(int node, int parity, int n, int[] flip) {
        if (node > n) {
            return;
        }

        int currParity = parity ^ flip[node];

        if (currParity == 1) {
            answer++;
        }

        dfs(node * 2, currParity, n, flip);
        dfs(node * 2 + 1, currParity, n, flip);
    }
}
```

---

## Complexity

### Time complexity

```text
O(n + q)
```

- `O(q)` to compress queries by parity
- `O(n)` to DFS all nodes once

### Space complexity

```text
O(h)
```

where `h` is tree height.

Because this tree is implicitly balanced by index structure:

```text
h = O(log n)
```

So recursion depth is safe for this problem size in principle, though iterative solutions can avoid recursion entirely if desired.

---

# Approach 3: Iterative DFS / BFS with parity propagation

## Intuition

Approach 2 is already optimal in asymptotic complexity.

This version uses the same idea, but avoids recursion.

That can be useful in Java if you want to avoid any concern about recursion depth or simply prefer iterative traversal.

We still:

1. compress queries into parity
2. traverse from root
3. propagate current parity to children

---

## Iterative DFS version

We can use a stack storing pairs:

```text
(node, currentParityBeforeNode)
```

At a node:

```text
currParity = parityBeforeNode XOR flip[node]
```

If `currParity == 1`, count it.

Then push children with that updated parity.

---

## Java code

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    static class Pair {
        int node;
        int parity;

        Pair(int node, int parity) {
            this.node = node;
            this.parity = parity;
        }
    }

    public int numberOfNodes(int n, int[] queries) {
        int[] flip = new int[n + 1];

        for (int q : queries) {
            flip[q] ^= 1;
        }

        int answer = 0;
        Deque<Pair> stack = new ArrayDeque<>();
        stack.push(new Pair(1, 0));

        while (!stack.isEmpty()) {
            Pair cur = stack.pop();

            if (cur.node > n) {
                continue;
            }

            int currParity = cur.parity ^ flip[cur.node];

            if (currParity == 1) {
                answer++;
            }

            stack.push(new Pair(cur.node * 2 + 1, currParity));
            stack.push(new Pair(cur.node * 2, currParity));
        }

        return answer;
    }
}
```

---

## BFS version

This is the same logic, just with a queue instead of a stack.

```java
import java.util.ArrayDeque;
import java.util.Queue;

class Solution {
    static class Pair {
        int node;
        int parity;

        Pair(int node, int parity) {
            this.node = node;
            this.parity = parity;
        }
    }

    public int numberOfNodes(int n, int[] queries) {
        int[] flip = new int[n + 1];

        for (int q : queries) {
            flip[q] ^= 1;
        }

        int answer = 0;
        Queue<Pair> queue = new ArrayDeque<>();
        queue.offer(new Pair(1, 0));

        while (!queue.isEmpty()) {
            Pair cur = queue.poll();

            if (cur.node > n) {
                continue;
            }

            int currParity = cur.parity ^ flip[cur.node];

            if (currParity == 1) {
                answer++;
            }

            queue.offer(new Pair(cur.node * 2, currParity));
            queue.offer(new Pair(cur.node * 2 + 1, currParity));
        }

        return answer;
    }
}
```

---

## Complexity

### Time complexity

```text
O(n + q)
```

### Space complexity

For DFS stack or BFS queue:

```text
O(n)
```

in the worst case.

Though practically the tree is nearly complete, so the active frontier can still be large.

---

# Approach 4: Bottom-up counting idea (less natural here)

## Intuition

You might wonder whether we can do something bottom-up, like subtree aggregation.

But this problem is fundamentally about **ancestor influence**, not descendant aggregation.

Each node’s final value depends on queries placed on its ancestor chain.

That is naturally a top-down propagation problem.

So while one could still reconstruct parent-child relationships and do more elaborate techniques, the path-parity DFS from Approach 2 is already both simpler and optimal.

This makes bottom-up approaches less attractive here.

---

# Best approach

The best solution is **Approach 2**.

Why?

- optimal `O(n + q)` time
- simple
- elegant
- directly uses the ancestor-path interpretation
- no explicit subtree flipping

---

# Worked example

## Example

```text
n = 5
queries = [1,2,5]
```

Tree:

```text
        1
      /   \
     2     3
    /
   4
  /
 5   <-- actually incorrect for implicit binary tree? let's write correctly
```

Actually for labels `1..5`, the tree is:

```text
        1
      /   \
     2     3
    / \
   4   5
```

Compress queries by parity:

```text
flip[1] = 1
flip[2] = 1
flip[5] = 1
others  = 0
```

Now DFS from root with parity:

- node 1: parity = 1 -> node 1 becomes 1
- node 2: parity = 1 ^ 1 = 0 -> node 2 becomes 0
- node 4: parity = 0 -> node 4 becomes 0
- node 5: parity = 0 ^ 1 = 1 -> node 5 becomes 1
- node 3: parity = 1 -> node 3 becomes 1

Count of ones = `3`.

Correct.

---

# Final recommended solution

```java
class Solution {
    private int answer = 0;

    public int numberOfNodes(int n, int[] queries) {
        int[] flip = new int[n + 1];

        for (int q : queries) {
            flip[q] ^= 1;
        }

        dfs(1, 0, n, flip);
        return answer;
    }

    private void dfs(int node, int parity, int n, int[] flip) {
        if (node > n) {
            return;
        }

        int currParity = parity ^ flip[node];

        if (currParity == 1) {
            answer++;
        }

        dfs(node * 2, currParity, n, flip);
        dfs(node * 2 + 1, currParity, n, flip);
    }
}
```

## Complexity

- **Time:** `O(n + queries.length)`
- **Space:** `O(log n)` recursion depth for this implicit tree

This is the cleanest and most efficient solution for the problem.
