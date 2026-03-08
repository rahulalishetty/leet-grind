# Maximum Sum of Non-Adjacent Edges in a Weighted Tree

## Problem Restatement

You are given a rooted weighted tree with nodes `0` to `n - 1`.

- `edges[i] = [par_i, weight_i]` means node `par_i` is the parent of node `i`
- the edge `(par_i, i)` has weight `weight_i`
- `edges[0] = [-1, -1]` because node `0` is the root

You want to choose a subset of edges such that:

- no two chosen edges are adjacent
- the sum of their weights is as large as possible

Two edges are adjacent if they share at least one endpoint.

The task is to return the maximum possible sum.

---

## High-Level Interpretation

This is a **maximum weight matching on a tree**.

A **matching** is a set of edges where no two edges share a node.

So the problem is asking:

> Find the maximum total weight matching in a rooted tree.

Because the graph is a tree, we can solve it with **tree DP** in linear time.

---

## Core Insight

At any node `u`, consider only the edges from `u` to its children.

Because all these edges share the node `u`, at most **one** of them can be selected.

So at node `u`, there are only two meaningful situations:

1. **No edge from `u` to any child is chosen**
2. **Exactly one edge from `u` to one child is chosen**

That is the whole DP structure.

---

## DP State Design

Let:

- `dp0[u]` = maximum sum obtainable in the subtree rooted at `u` when **no edge between `u` and any of its children is selected**
- `dp1[u]` = maximum sum obtainable in the subtree rooted at `u` when we are allowed to choose **at most one** edge between `u` and one of its children, whichever is optimal

Another way to view them:

### Meaning of `dp0[u]`

Node `u` is **not matched downward** to any child.

So every child subtree is solved independently, and each child can optimize internally.

### Meaning of `dp1[u]`

This is the true best answer for the subtree rooted at `u`.

At node `u`, either:

- choose no child edge, or
- choose exactly one child edge `(u, v)`

So `dp1[u]` is the max of those options.

---

## Transition for `dp0[u]`

If we do **not** choose any edge `(u, child)`, then every child subtree can contribute its own best possible answer.

Suppose `v` is a child of `u`. Since edge `(u, v)` is not selected, subtree `v` is free to do whatever is optimal inside it.

That best value is `dp1[v]`.

Therefore:

```text
dp0[u] = sum(dp1[v]) over all children v of u
```

This is the base total when `u` is not connected to any child.

---

## Transition for `dp1[u]`

Now we compute the actual best answer at `u`.

### Option 1: Choose no child edge

That value is simply:

```text
dp0[u]
```

### Option 2: Choose one specific child edge `(u, v)`

Suppose edge `(u, v)` has weight `w`.

Then:

- `u` cannot be connected to any other child
- `v` is already matched with `u`, so `v` cannot be matched with any of its children

That means:

- for child `v`, contribution becomes:

```text
w + dp0[v]
```

because after selecting `(u, v)`, node `v` cannot take any downward child edge

- for every other child `x != v`, contribution remains:

```text
dp1[x]
```

If we already know:

```text
base = sum(dp1[child])
```

then selecting child `v` changes only that child’s contribution:

- remove `dp1[v]`
- add `w + dp0[v]`

So:

```text
candidate = base - dp1[v] + w + dp0[v]
```

Take the best over all children:

```text
dp1[u] = max(
    dp0[u],
    max over children v of (dp0[u] - dp1[v] + weight(u, v) + dp0[v])
)
```

---

## Why This Works

This DP is correct because the only interaction between different child subtrees happens through node `u`.

And because no two chosen edges may share `u`, at most one child edge can be chosen.

Once that decision is fixed:

- each remaining child subtree becomes independent
- the subtree of the chosen child must respect the fact that the child is already matched upward

That is exactly what `dp0` and `dp1` encode.

This is the standard structure of tree matching DP:

- one state for “node unavailable for matching downward”
- one state for “node may choose one child edge”

---

## Leaf Node Behavior

If `u` is a leaf, it has no children.

Then:

```text
dp0[u] = 0
dp1[u] = 0
```

That makes sense:

- there are no edges inside its subtree
- best sum is zero

This automatically works with the recurrence.

---

## Step-by-Step Intuition

At every node:

1. First collect the best values from all children assuming we do **not** connect to any child.
2. That gives a baseline `base = dp0[u]`.
3. Then try promoting exactly one child edge `(u, v)` into the matching.
4. If we choose `(u, v)`, we gain:
   - the edge weight `w`
   - subtree value `dp0[v]` instead of `dp1[v]`
5. Pick the best such upgrade.

This is a very common tree DP pattern:

- compute the “choose none” value
- then try each child as the unique special choice

---

## Java Code

```java
import java.util.*;

class Solution {
    List<int[]>[] tree;
    long[] dp0, dp1;

    public long maxScore(int[][] edges) {
        int n = edges.length;
        tree = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            tree[i] = new ArrayList<>();
        }

        for (int i = 1; i < n; i++) {
            int parent = edges[i][0];
            int weight = edges[i][1];
            tree[parent].add(new int[]{i, weight});
        }

        dp0 = new long[n];
        dp1 = new long[n];

        dfs(0);
        return dp1[0];
    }

    private void dfs(int u) {
        long base = 0;

        // Solve all child subtrees first
        for (int[] edge : tree[u]) {
            int v = edge[0];
            dfs(v);
            base += dp1[v];
        }

        // Case 1: choose no edge from u to any child
        dp0[u] = base;
        dp1[u] = base;

        // Case 2: choose exactly one edge (u, v)
        for (int[] edge : tree[u]) {
            int v = edge[0];
            int w = edge[1];

            long candidate = base - dp1[v] + w + dp0[v];
            dp1[u] = Math.max(dp1[u], candidate);
        }
    }
}
```

---

## Code Walkthrough

### Building the Tree

```java
tree = new ArrayList[n];
for (int i = 0; i < n; i++) {
    tree[i] = new ArrayList<>();
}
```

We create an adjacency list where each `tree[u]` stores pairs `[child, weight]`.

Then:

```java
for (int i = 1; i < n; i++) {
    int parent = edges[i][0];
    int weight = edges[i][1];
    tree[parent].add(new int[]{i, weight});
}
```

Because the input already gives parent information, building the rooted tree is direct.

---

### DP Arrays

```java
long[] dp0, dp1;
```

Use `long` because the sum of weights may exceed `int`.

---

### DFS

We process the tree in postorder:

```java
dfs(v);
```

This ensures each child’s DP values are ready before computing the parent.

---

### Computing `base`

```java
long base = 0;
for (int[] edge : tree[u]) {
    int v = edge[0];
    dfs(v);
    base += dp1[v];
}
```

This means:

- assume no edge from `u` to any child is selected
- then each child contributes its best internal value `dp1[v]`

So:

```java
dp0[u] = base;
dp1[u] = base;
```

Initially, `dp1[u]` equals the “choose none” option.

---

### Trying Each Child as the One Chosen Edge

```java
for (int[] edge : tree[u]) {
    int v = edge[0];
    int w = edge[1];

    long candidate = base - dp1[v] + w + dp0[v];
    dp1[u] = Math.max(dp1[u], candidate);
}
```

This performs the swap:

- remove the old child contribution `dp1[v]`
- add `w + dp0[v]`

This simulates selecting edge `(u, v)`.

---

## Dry Run Example

Consider:

```text
0
├── 1 (5)
└── 2 (7)
```

Leaves `1` and `2`:

```text
dp0[1] = dp1[1] = 0
dp0[2] = dp1[2] = 0
```

At node `0`:

```text
base = dp1[1] + dp1[2] = 0 + 0 = 0
dp0[0] = 0
```

Try edge `(0, 1)`:

```text
candidate = 0 - 0 + 5 + 0 = 5
```

Try edge `(0, 2)`:

```text
candidate = 0 - 0 + 7 + 0 = 7
```

So:

```text
dp1[0] = max(0, 5, 7) = 7
```

Answer = `7`

Correct.

---

## More Interesting Example

Consider:

```text
        0
      /   \\
   (4)     (3)
    1       2
   /
 (10)
 3
```

Edges:

- `(0,1)=4`
- `(0,2)=3`
- `(1,3)=10`

### Leaves

Node `2`, `3`:

```text
dp0[2]=dp1[2]=0
dp0[3]=dp1[3]=0
```

### Node 1

Base:

```text
dp0[1] = dp1[3] = 0
```

Try choosing `(1,3)`:

```text
candidate = 0 - 0 + 10 + 0 = 10
```

So:

```text
dp1[1] = 10
```

### Node 0

Base:

```text
dp0[0] = dp1[1] + dp1[2] = 10 + 0 = 10
```

Try choosing `(0,1)`:

```text
candidate = 10 - 10 + 4 + dp0[1]
          = 10 - 10 + 4 + 0
          = 4
```

Try choosing `(0,2)`:

```text
candidate = 10 - 0 + 3 + 0 = 13
```

So:

```text
dp1[0] = max(10, 4, 13) = 13
```

Optimal matching:

- choose `(1,3)=10`
- choose `(0,2)=3`

Total = `13`

Notice why `(0,1)=4` is bad:
choosing it blocks the better edge `(1,3)=10`.

This is exactly why greedy fails and DP is needed.

---

## Why Greedy Does Not Work

A tempting approach is:

> Always take the largest available edge first.

That fails because choosing an edge can block another edge deeper in the tree.

Example:

```text
0 -1(4)- 1 -3(10)
0 -2(3)- 2
```

If you greedily choose `(0,1)=4`, then you lose `(1,3)=10`.

But the optimal choice is:

- `(1,3)=10`
- `(0,2)=3`

Total `13`, much larger than `4`.

So a local heavy edge can be globally bad.

---

## Correctness Intuition

The recurrence is exhaustive and non-overlapping:

- At node `u`, any valid matching either:
  - uses no child edge of `u`, or
  - uses exactly one child edge of `u`

There is no third possibility because any two child edges would share `u`.

For each such choice:

- the chosen child is forced into state `dp0`
- all other children stay in state `dp1`

Thus every valid matching is represented by one transition, and every transition corresponds to a valid matching.

So the DP explores exactly all possibilities.

---

## Time Complexity

### Building the Tree

We process each node once:

```text
O(n)
```

### DFS + DP

Each node is visited once, and each edge is processed a constant number of times:

```text
O(n)
```

### Total Time

```text
O(n)
```

---

## Space Complexity

### Adjacency List

Stores all `n - 1` edges:

```text
O(n)
```

### DP Arrays

Two arrays of length `n`:

```text
O(n)
```

### Recursion Stack

In the worst case, the tree may be a chain:

```text
O(n)
```

### Total Space

```text
O(n)
```

---

## Final Complexity Summary

- **Time:** `O(n)`
- **Space:** `O(n)`

---

## Key Takeaways

1. This problem is maximum weight matching on a tree.
2. The crucial structural fact is:
   - at a node, at most one child edge can be chosen
3. That leads naturally to two DP states:
   - `dp0[u]`: choose no child edge
   - `dp1[u]`: choose the best among none or one child edge
4. The transition uses a baseline sum and then tries each child as the unique selected edge.
5. The solution is linear in the size of the tree.

---

## Minimal Formula Summary

If `children(u)` are the children of `u`, and edge `(u, v)` has weight `w(u, v)`:

```text
dp0[u] = Σ dp1[v]
```

```text
dp1[u] = max(
    dp0[u],
    max over v in children(u) of (
        dp0[u] - dp1[v] + w(u, v) + dp0[v]
    )
)
```

Answer:

```text
dp1[0]
```

---

## Final Answer

Use **tree DP** with two states per node.

This gives an **O(n)** time and **O(n)** space solution.
