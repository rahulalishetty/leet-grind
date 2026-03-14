# 1315. Sum of Nodes with Even-Valued Grandparent — Approaches

## Approach 1: Depth-First Search (DFS)

### Intuition

We are given a binary tree and must return the **sum of nodes whose grandparent has an even value**.

The **grandparent** of a node is the **parent of its parent**.

To determine whether a node should be added to the sum, we must know:

- the **value of its parent**
- the **value of its grandparent**

A natural approach is **Depth‑First Search (DFS)**, where we recursively traverse the tree and pass along:

- parent value
- grandparent value

This allows each recursive call to determine whether the **current node should contribute to the sum**.

For the root node, there is **no parent or grandparent**, so we initialize them with **odd dummy values** (e.g., `-1`) to avoid incorrectly adding the root.

When we recurse:

- The **parent becomes the current node**
- The **grandparent becomes the previous parent**

---

### Algorithm

1. Define a recursive function:

```
solve(root, parentValue, grandparentValue)
```

2. Base case:

```
If root is null → return 0
```

3. Recursively traverse children:

```
leftSum = solve(root.left, root.val, parent)
rightSum = solve(root.right, root.val, parent)
```

4. If the grandparent value is even:

```
add root.val to the sum
```

5. Return:

```
leftSum + rightSum + currentContribution
```

---

### Implementation

```java
class Solution {

    int solve(TreeNode root, int parent, int gParent) {
        if (root == null) {
            return 0;
        }

        return solve(root.left, root.val, parent)
             + solve(root.right, root.val, parent)
             + (gParent % 2 != 0 ? 0 : root.val);
    }

    public int sumEvenGrandparent(TreeNode root) {
        return solve(root, -1, -1);
    }
}
```

---

### Complexity Analysis

Let **N = number of nodes**.

#### Time Complexity

```
O(N)
```

Every node is visited **exactly once**.

---

#### Space Complexity

```
O(N)
```

Worst case recursion depth occurs in a **skewed tree**, producing a recursion stack of size **N**.

---

# Approach 2: Breadth‑First Search (BFS)

### Intuition

Another way to traverse a tree is **Breadth‑First Search (BFS)**.

Instead of checking the **ancestors of each node**, we reverse the perspective:

> If a node is **even**, then **its grandchildren should be added to the sum**.

This avoids explicitly tracking parent and grandparent values.

Each time we encounter an **even node**, we check its **four possible grandchildren**.

---

### Algorithm

1. Initialize a **queue**.
2. Add the root node.
3. Initialize `sum = 0`.

While the queue is not empty:

1. Pop the current node.
2. If its value is even:
   - Check its **four grandchildren**
   - Add their values to the sum.
3. Push its children into the queue.

Return the final sum.

---

### Implementation

```java
class Solution {

    int findVal(TreeNode root) {
        return root == null ? 0 : root.val;
    }

    public int sumEvenGrandparent(TreeNode root) {

        if (root == null) {
            return 0;
        }

        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);

        int sum = 0;

        while (!q.isEmpty()) {

            TreeNode curr = q.remove();

            if (curr.val % 2 == 0) {

                if (curr.left != null) {
                    sum += findVal(curr.left.left) + findVal(curr.left.right);
                }

                if (curr.right != null) {
                    sum += findVal(curr.right.left) + findVal(curr.right.right);
                }
            }

            if (curr.left != null) q.add(curr.left);
            if (curr.right != null) q.add(curr.right);
        }

        return sum;
    }
}
```

---

### Complexity Analysis

Let **N = number of nodes**.

#### Time Complexity

```
O(N)
```

Each node is added to and removed from the queue **once**.

---

#### Space Complexity

```
O(N)
```

In the worst case (complete binary tree), the queue may contain **up to N/2 nodes**.

---

# Summary

| Approach | Traversal   | Key Idea                            | Time | Space |
| -------- | ----------- | ----------------------------------- | ---- | ----- |
| DFS      | Recursive   | Track parent & grandparent values   | O(N) | O(N)  |
| BFS      | Level Order | If node is even → add grandchildren | O(N) | O(N)  |

---

## Key Insight

Instead of tracking **ancestors**, we can also detect valid nodes by looking **two levels down** (grandchildren).

Both approaches achieve **linear time traversal of the tree**.
