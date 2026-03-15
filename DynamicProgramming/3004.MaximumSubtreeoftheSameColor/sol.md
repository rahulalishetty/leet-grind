# Maximum Monochromatic Subtree in a Rooted Tree

## Problem Restatement

You are given:

- a tree with `n` nodes labeled from `0` to `n - 1`
- the tree is rooted at node `0`
- an array `colors` where `colors[i]` is the color of node `i`

We need to find a node `v` such that **every node in the subtree rooted at `v` has the same color**.

Among all such valid subtrees, return the **maximum subtree size**.

---

## Core Observation

A subtree rooted at node `u` is monochromatic if and only if:

1. every child subtree of `u` is monochromatic, and
2. each child subtree has the same color as `u`

That immediately suggests a **bottom-up DFS**.

Why bottom-up?

Because whether node `u` forms a valid monochromatic subtree depends entirely on the status of its children.

---

## Intuition

### Think locally, combine globally

For each node `u`, we want to know:

- how many nodes are in the subtree of `u`
- whether the entire subtree of `u` is monochromatic

If we already know this information for all children of `u`, then checking `u` becomes easy.

Suppose a child is `v`.

For subtree `u` to be monochromatic:

- subtree `v` itself must already be monochromatic
- and that subtree’s color must match `colors[u]`

If subtree `v` is monochromatic, then every node in that subtree has color `colors[v]`.

So it is enough to check:

```java
colors[v] == colors[u]
```

This works because if all nodes under `v` have color `colors[v]`, and `colors[v] == colors[u]`, then all those nodes also match `u`.

That is the whole merging rule.

---

## Why this becomes a tree DP

This is really a small tree DP / DFS-state problem.

For each node, the answer depends on child answers.

A natural DFS return value is:

- `subtreeSize`
- `isMonochromatic`

Then the parent combines children as follows:

- add child subtree sizes into its own size
- if any child is not monochromatic, current node fails
- if any child root color differs from current node color, current node fails

If current node never fails, then its whole subtree is monochromatic, and we can update the global maximum.

---

## Step-by-Step Logic

### Step 1: Build the tree

The input `edges` gives undirected edges of a tree.

So first build an adjacency list.

Even though the tree is rooted at `0`, the edges themselves are undirected in the representation.
So while doing DFS, we must pass the parent to avoid going backward.

---

### Step 2: DFS from the root

For every node `u`, run DFS on all children `v != parent`.

During DFS:

- initialize `size = 1` for the current node
- initialize `ok = true`
- for each child:
  - get child result
  - add child subtree size to current size
  - if child subtree is not monochromatic, mark `ok = false`
  - if child color differs from current node color, mark `ok = false`

After processing all children:

- if `ok == true`, then subtree rooted at `u` is monochromatic
- update global answer with `size`

Return the pair:

- subtree size
- whether subtree is monochromatic

---

## Dry Reasoning on Why the Condition Is Correct

A common doubt is:

> Why do we check `colors[v] != colors[u]` instead of checking all nodes inside child subtree?

Because DFS already guarantees the child subtree is monochromatic.

That means:

- if child subtree is monochromatic, then every node inside it has exactly one color
- that color must be the child root’s color, namely `colors[v]`

So once child subtree is valid, checking the root color of that subtree is enough.

This avoids rechecking every node and keeps the solution linear.

---

## Example

### Input

```text
edges = [[0,1],[0,2],[1,3],[1,4]]
colors = [1,1,1,1,2]
```

Tree:

```text
        0(1)
       /   \
    1(1)   2(1)
   /   \
 3(1)  4(2)
```

### Evaluate bottom-up

#### Node 3

- leaf
- subtree size = 1
- monochromatic = true

#### Node 4

- leaf
- subtree size = 1
- monochromatic = true

#### Node 1

- own color = 1
- child 3 is monochromatic and color 1 -> fine
- child 4 is monochromatic but color 2 -> mismatch
- so subtree at 1 is **not** monochromatic
- subtree size = 3

#### Node 2

- leaf
- subtree size = 1
- monochromatic = true

#### Node 0

- child 1 subtree is not monochromatic -> fails
- child 2 is fine
- subtree at 0 is not monochromatic
- subtree size = 5

### Valid monochromatic subtrees

- node 3 => size 1
- node 4 => size 1
- node 2 => size 1

Answer = `1`

---

## Another Example

### Input

```text
edges = [[0,1],[0,2],[1,3],[1,4]]
colors = [1,1,1,1,1]
```

Now every node has color `1`.

Then:

- node 3 => monochromatic, size 1
- node 4 => monochromatic, size 1
- node 1 => monochromatic, size 3
- node 2 => monochromatic, size 1
- node 0 => monochromatic, size 5

Answer = `5`

---

## Why Greedy or Brute Force Is Not Good

### Brute force idea

For every node `u`:

- collect all nodes in its subtree
- check whether all colors are equal

This is too slow.

In the worst case, each subtree can contain `O(n)` nodes, and doing this for every node leads to `O(n^2)` work.

### Better idea

Compute subtree information once and reuse it upward.

That is exactly what DFS does.

---

## Code

```java
import java.util.*;

class Solution {
    private List<Integer>[] tree;
    private int[] colors;
    private int ans = 1;

    public int maximumSubtreeSize(int[][] edges, int[] colors) {
        int n = colors.length;
        this.colors = colors;

        tree = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            tree[i] = new ArrayList<>();
        }

        for (int[] e : edges) {
            int u = e[0], v = e[1];
            tree[u].add(v);
            tree[v].add(u);
        }

        dfs(0, -1);
        return ans;
    }

    // returns {subtreeSize, isMonochromatic ? 1 : 0}
    private int[] dfs(int u, int parent) {
        int size = 1;
        boolean ok = true;

        for (int v : tree[u]) {
            if (v == parent) continue;

            int[] child = dfs(v, u);
            int childSize = child[0];
            boolean childMono = child[1] == 1;

            size += childSize;

            if (!childMono || colors[v] != colors[u]) {
                ok = false;
            }
        }

        if (ok) {
            ans = Math.max(ans, size);
        }

        return new int[]{size, ok ? 1 : 0};
    }
}
```

---

## Code Walkthrough

### Fields

```java
private List<Integer>[] tree;
private int[] colors;
private int ans = 1;
```

- `tree` stores adjacency list
- `colors` is stored as a field so DFS can access it easily
- `ans` keeps track of the maximum valid subtree size found so far

---

### Building adjacency list

```java
tree = new ArrayList[n];
for (int i = 0; i < n; i++) {
    tree[i] = new ArrayList<>();
}

for (int[] e : edges) {
    int u = e[0], v = e[1];
    tree[u].add(v);
    tree[v].add(u);
}
```

Because the tree edges are undirected, we add both directions.

---

### Launch DFS

```java
dfs(0, -1);
```

Start from root `0`.
Parent is `-1` because root has no parent.

---

### DFS state initialization

```java
int size = 1;
boolean ok = true;
```

- `size = 1` because subtree includes the node itself
- `ok = true` means: so far, this subtree can still be monochromatic

---

### Process each child

```java
for (int v : tree[u]) {
    if (v == parent) continue;

    int[] child = dfs(v, u);
    int childSize = child[0];
    boolean childMono = child[1] == 1;

    size += childSize;

    if (!childMono || colors[v] != colors[u]) {
        ok = false;
    }
}
```

This is the heart of the solution.

For each child:

#### `if (v == parent) continue;`

Skip the back edge to parent.

#### `int[] child = dfs(v, u);`

Recursively compute child subtree info.

#### `size += childSize;`

Accumulate subtree size.

#### `if (!childMono || colors[v] != colors[u])`

Subtree rooted at `u` fails if either:

- child subtree is not monochromatic
- child subtree has a different color from `u`

---

### Update answer

```java
if (ok) {
    ans = Math.max(ans, size);
}
```

If current subtree is monochromatic, compare its size with the best answer seen so far.

---

### Return state to parent

```java
return new int[]{size, ok ? 1 : 0};
```

This returns:

- subtree size
- monochromatic status encoded as 1 or 0

---

## Correctness Argument

We can justify correctness with induction on subtree height.

### Base case: leaf node

A leaf node has no children.

- its subtree contains only itself
- therefore it is monochromatic
- subtree size is 1

So DFS returns the correct result.

---

### Inductive hypothesis

Assume DFS correctly computes:

- subtree size
- monochromatic status

for all children of node `u`.

---

### Inductive step

For node `u`, DFS:

1. starts with `size = 1`
2. adds all child subtree sizes
   So computed size is exactly the number of nodes in subtree `u`
3. marks subtree invalid if any child subtree is not monochromatic
4. marks subtree invalid if any child subtree color differs from `u`

By the inductive hypothesis:

- if a child subtree is marked monochromatic, then all nodes inside that child subtree have color `colors[v]`
- therefore matching `colors[v]` with `colors[u]` is sufficient to ensure compatibility

So:

- `ok = true` exactly when every node in subtree `u` has color `colors[u]`

Hence DFS correctly determines whether subtree `u` is monochromatic.

Whenever it is, updating `ans` is valid.

Therefore, by induction, the algorithm is correct for the entire tree.

---

## Time Complexity

### Building adjacency list

There are `n - 1` edges in a tree.

Building the graph costs:

```text
O(n)
```

### DFS

Each node is visited once.
Each edge is examined twice in adjacency list traversal.

So DFS also costs:

```text
O(n)
```

### Total

```text
O(n)
```

---

## Space Complexity

### Adjacency list

Stores `O(n)` total neighbors.

### Recursion stack

In the worst case, the tree can be skewed, so recursion depth can be `O(n)`.

### Total

```text
O(n)
```

---

## Why This Is Optimal

You must at least inspect every node once, so any correct solution needs at least `O(n)` time.

This solution achieves exactly that.

So the time complexity is asymptotically optimal.

---

## Common Pitfalls

### 1. Forgetting that input edges are undirected

Even though the tree is rooted at `0`, the edges still need to be stored as undirected adjacency.

Then DFS uses the parent parameter to avoid revisiting.

---

### 2. Checking only immediate children without validating child subtrees

Suppose a child has the same color as `u`, but deeper nodes in that child subtree differ.

Then checking only `colors[v] == colors[u]` is not enough by itself.

That is why we also require `childMono == true`.

Both conditions are necessary.

---

### 3. Recomputing subtree nodes repeatedly

A brute-force subtree scan for each node becomes quadratic.

The DFS state-sharing avoids that.

---

### 4. Returning only boolean and not size

Even if subtree is monochromatic, you still need subtree size to update the answer.

So DFS must track both.

---

## Alternative Return Style

Instead of returning `int[]`, you could define a small helper class:

```java
class State {
    int size;
    boolean mono;

    State(int size, boolean mono) {
        this.size = size;
        this.mono = mono;
    }
}
```

Then DFS becomes a bit more readable.

But `int[]` is perfectly acceptable in interviews and coding platforms.

---

## Cleaner Mental Model

A useful mental model is:

- every leaf starts as a valid monochromatic block
- parent tries to merge all child blocks with itself
- merge succeeds only if every child block:
  - is internally valid
  - has the same color as parent

If merge succeeds, parent becomes one larger monochromatic block.

This is exactly how the DFS builds the answer.

---

## Final Takeaway

The problem looks like a subtree-color checking problem, but the efficient view is:

- compute subtree properties bottom-up
- each node asks: can all child monochromatic blocks merge with me?
- if yes, my subtree is also monochromatic
- track the largest such subtree

That gives a simple and optimal `O(n)` tree DFS solution.

---

## Final Complexity Summary

- **Time Complexity:** `O(n)`
- **Space Complexity:** `O(n)`

---

## Short Summary

Use DFS from the root. For each node, compute:

- the size of its subtree
- whether that subtree is monochromatic

A subtree rooted at `u` is monochromatic only if every child subtree is monochromatic and every child subtree’s color matches `colors[u]`. If valid, update the answer using that subtree size.

This gives a linear-time tree DP / DFS solution.
