# Minimum Level Sum in a Binary Tree

## Problem summary

We are given the root of a binary tree.

For each level of the tree:

- level 1 contains the root
- level 2 contains the root's children
- level 3 contains the grandchildren
- and so on

We must compute the **sum of node values on each level** and return the **level index with the minimum sum**.

If multiple levels have the same minimum sum, we return the **smallest level number**.

---

## Important observations

This is fundamentally a **level-order aggregation** problem.

Since levels matter directly, **Breadth-First Search (BFS)** is the most natural solution.

However, we can also solve it with **Depth-First Search (DFS)** by tracking the sum for each depth.

Because:

- the tree can have up to `10^5` nodes
- node values can be as large as `10^9`

we should use `long` when computing level sums.

---

# Approach 1: BFS / Level Order Traversal (recommended)

## Intuition

BFS visits nodes level by level.

So for every BFS layer:

1. compute the sum of that level
2. compare it against the best minimum seen so far
3. update the answer if needed

This is the cleanest and most direct solution.

---

## Algorithm

1. Initialize a queue with the root.
2. Set:
   - `level = 1`
   - `bestLevel = 1`
   - `minSum = +infinity`
3. While the queue is not empty:
   - get `size = queue.size()`
   - process exactly `size` nodes, which belong to the current level
   - compute `levelSum`
   - if `levelSum < minSum`:
     - update `minSum`
     - update `bestLevel`
   - push all children into the queue
   - increment `level`
4. Return `bestLevel`

---

## Java code

```java
import java.util.ArrayDeque;
import java.util.Queue;

class Solution {
    public int minimumLevel(TreeNode root) {
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);

        int level = 1;
        int bestLevel = 1;
        long minSum = Long.MAX_VALUE;

        while (!queue.isEmpty()) {
            int size = queue.size();
            long levelSum = 0L;

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                levelSum += node.val;

                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }

            if (levelSum < minSum) {
                minSum = levelSum;
                bestLevel = level;
            }

            level++;
        }

        return bestLevel;
    }
}
```

---

## Why this works

BFS guarantees that all nodes processed in one loop iteration belong to the same level.

So the computed `levelSum` is exactly the sum of that level.

Tracking the minimum over all levels gives the required answer.

Because we only update when `levelSum < minSum` and **not** when equal, ties automatically keep the earlier level, which is exactly what the problem asks.

---

## Complexity

Let `n` be the number of nodes.

### Time complexity

```text
O(n)
```

Each node is visited exactly once.

### Space complexity

```text
O(w)
```

where `w` is the maximum width of the tree.

In the worst case:

```text
O(n)
```

---

# Approach 2: DFS with a list of level sums

## Intuition

We do not strictly need BFS.

We can run DFS and maintain an array/list where:

```text
levelSums[d] = sum of all nodes at depth d
```

Since levels are 1-indexed in the problem statement, we can store depth `0` for the root internally and return `index + 1` at the end.

During DFS:

- if we visit a depth for the first time, append a new entry
- otherwise add to the existing sum

Once DFS is complete, scan `levelSums` to find the minimum.

---

## Algorithm

1. Create `List<Long> levelSums`
2. DFS from root at depth 0
3. For each node:
   - if `depth == levelSums.size()`, add new entry
   - else update existing entry
4. After DFS, scan `levelSums`:
   - return the index of the minimum sum + 1

---

## Java code

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public int minimumLevel(TreeNode root) {
        List<Long> levelSums = new ArrayList<>();
        dfs(root, 0, levelSums);

        long minSum = Long.MAX_VALUE;
        int bestLevel = 1;

        for (int i = 0; i < levelSums.size(); i++) {
            if (levelSums.get(i) < minSum) {
                minSum = levelSums.get(i);
                bestLevel = i + 1; // convert 0-indexed depth to 1-indexed level
            }
        }

        return bestLevel;
    }

    private void dfs(TreeNode node, int depth, List<Long> levelSums) {
        if (node == null) {
            return;
        }

        if (depth == levelSums.size()) {
            levelSums.add((long) node.val);
        } else {
            levelSums.set(depth, levelSums.get(depth) + node.val);
        }

        dfs(node.left, depth + 1, levelSums);
        dfs(node.right, depth + 1, levelSums);
    }
}
```

---

## Why this works

DFS visits every node and knows its depth.

So every node contributes to exactly one `levelSums[depth]`.

Once all nodes are processed, the level sums are exact, and scanning them yields the level with the minimum sum.

---

## Complexity

### Time complexity

```text
O(n)
```

Each node is visited once, and the final scan over levels is at most `O(n)`.

### Space complexity

```text
O(h + L)
```

where:

- `h` = recursion depth
- `L` = number of levels

Worst case:

```text
O(n)
```

---

# Approach 3: Iterative DFS using an explicit stack

## Intuition

If we want DFS without recursion, we can use an explicit stack.

Each stack entry stores:

- node
- depth

Then we update `levelSums` exactly like in recursive DFS.

This avoids recursion stack overflow concerns on deep trees.

---

## Algorithm

1. Create `List<Long> levelSums`
2. Push `(root, 0)` onto a stack
3. While stack is not empty:
   - pop one entry
   - update `levelSums[depth]`
   - push children with `depth + 1`
4. Scan `levelSums` for the minimum and return the corresponding level

---

## Java code

```java
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class Solution {
    static class Pair {
        TreeNode node;
        int depth;

        Pair(TreeNode node, int depth) {
            this.node = node;
            this.depth = depth;
        }
    }

    public int minimumLevel(TreeNode root) {
        List<Long> levelSums = new ArrayList<>();
        Deque<Pair> stack = new ArrayDeque<>();
        stack.push(new Pair(root, 0));

        while (!stack.isEmpty()) {
            Pair cur = stack.pop();
            TreeNode node = cur.node;
            int depth = cur.depth;

            if (depth == levelSums.size()) {
                levelSums.add((long) node.val);
            } else {
                levelSums.set(depth, levelSums.get(depth) + node.val);
            }

            if (node.right != null) {
                stack.push(new Pair(node.right, depth + 1));
            }
            if (node.left != null) {
                stack.push(new Pair(node.left, depth + 1));
            }
        }

        long minSum = Long.MAX_VALUE;
        int bestLevel = 1;

        for (int i = 0; i < levelSums.size(); i++) {
            if (levelSums.get(i) < minSum) {
                minSum = levelSums.get(i);
                bestLevel = i + 1;
            }
        }

        return bestLevel;
    }
}
```

---

## Complexity

### Time complexity

```text
O(n)
```

### Space complexity

```text
O(n)
```

due to explicit stack and stored level sums.

---

# Approach 4: Recursive DFS with immediate min tracking

## Intuition

Another variation is:

- first compute all level sums with DFS
- or while updating them, defer min decision until the end

Trying to update the answer immediately during DFS is possible, but not as clean, because later nodes at the same level may still change the sum.

So the more robust way is still:

1. accumulate sums
2. scan afterward

Thus this “approach” is really just a variant of Approach 2, not a fundamentally different method.

---

# Best approach

The best solution for this problem is:

## **Approach 1: BFS / Level Order Traversal**

Why:

- the problem is explicitly level-based
- BFS naturally groups nodes by level
- no extra post-processing structure besides the queue
- easy to reason about
- straightforward tie handling

---

# Common pitfalls

## 1. Using `int` for the level sum

Node values can be as large as `10^9`, and a level may contain many nodes.

So level sums can exceed `int`.

Use:

```java
long levelSum
```

and

```java
long minSum
```

---

## 2. Handling ties incorrectly

The problem says:

> in case of a tie, return the lowest level

So when comparing:

```java
if (levelSum < minSum)
```

not:

```java
if (levelSum <= minSum)
```

If you use `<=`, you would incorrectly replace an earlier level with a later one.

---

## 3. Confusing level number with depth

Internally, DFS often uses depth starting at `0`.

But the problem wants levels starting at `1`.

So remember:

```java
answer = depth + 1
```

---

# Final recommended solution

```java
import java.util.ArrayDeque;
import java.util.Queue;

class Solution {
    public int minimumLevel(TreeNode root) {
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);

        int level = 1;
        int bestLevel = 1;
        long minSum = Long.MAX_VALUE;

        while (!queue.isEmpty()) {
            int size = queue.size();
            long levelSum = 0L;

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                levelSum += node.val;

                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }

            if (levelSum < minSum) {
                minSum = levelSum;
                bestLevel = level;
            }

            level++;
        }

        return bestLevel;
    }
}
```

## Complexity

- **Time:** `O(n)`
- **Space:** `O(n)` in the worst case

This is the cleanest and most natural solution for the problem.
