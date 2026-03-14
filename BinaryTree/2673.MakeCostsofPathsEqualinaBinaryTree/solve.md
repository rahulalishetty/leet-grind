# 2673. Make Costs of Paths Equal in a Binary Tree

## Problem summary

We are given a **perfect binary tree** with `n` nodes, numbered `1` to `n`.

For each node `i`:

- left child is `2 * i`
- right child is `2 * i + 1`

Each node has a cost:

```text
cost[i]
```

where `cost[0]` is for node `1`, `cost[1]` for node `2`, and so on.

We may increment any node’s cost by `1` any number of times.

Our goal is to make **all root-to-leaf path sums equal** using the **minimum total number of increments**.

---

# Key observation

At any internal node, its left and right subtrees must eventually contribute the **same total path sum** downward.

Suppose:

- left subtree path sum is `L`
- right subtree path sum is `R`

If `L != R`, then the smaller side must be increased by:

```text
abs(L - R)
```

That cost is unavoidable.

After balancing them, the contribution of this node upward becomes:

```text
cost[node] + max(L, R)
```

This immediately suggests a **bottom-up / postorder** solution.

---

# Why bottom-up works

The tree is perfect, so every internal node has exactly two children.

If we know the balanced path sum returned from the left child and the right child, then:

1. we add `abs(left - right)` to the answer
2. we return:

```text
nodeCost + max(left, right)
```

This greedily equalizes sibling subtrees at the lowest possible point.

That is optimal because any root-to-leaf path through this node must pass through exactly one of those two child branches, so the mismatch must be fixed somewhere below or at this node. Fixing it right here is sufficient and minimal.

---

# Approach 1: Recursive postorder DFS (recommended conceptually)

## Intuition

Define a function:

```text
dfs(node) = balanced maximum root-to-leaf path sum starting from this node
```

For a leaf:

```text
dfs(node) = cost[node]
```

For an internal node:

- compute left path sum
- compute right path sum
- add their difference to the answer
- return current cost plus the larger child sum

---

## Algorithm

For a node `u`:

1. If `u` is a leaf, return `cost[u]`
2. Let:
   - `left = dfs(left child)`
   - `right = dfs(right child)`
3. Add:

```text
abs(left - right)
```

to the answer 4. Return:

```text
cost[u] + max(left, right)
```

The final accumulated answer is the minimum increments needed.

---

## Java code

```java
class Solution {
    private int[] cost;
    private int answer = 0;
    private int n;

    public int minIncrements(int n, int[] cost) {
        this.n = n;
        this.cost = cost;
        dfs(1);
        return answer;
    }

    private int dfs(int node) {
        if (node > n) {
            return 0;
        }

        int leftChild = node * 2;
        int rightChild = node * 2 + 1;

        // leaf
        if (leftChild > n) {
            return cost[node - 1];
        }

        int left = dfs(leftChild);
        int right = dfs(rightChild);

        answer += Math.abs(left - right);

        return cost[node - 1] + Math.max(left, right);
    }
}
```

---

## Why this works

At every internal node, the two child subtrees must eventually have equal root-to-leaf totals.

If one side is smaller by `d`, then at least `d` increments are necessary somewhere in that side.

Adding exactly `d = abs(left - right)` is therefore optimal.

Once both sides are balanced, the parent only needs to know the common child contribution, which is:

```text
max(left, right)
```

So the recursion correctly solves each subtree independently and combines them optimally.

---

## Complexity

### Time complexity

```text
O(n)
```

Each node is processed once.

### Space complexity

```text
O(h)
```

where `h` is the height of the tree.

Since the tree is perfect:

```text
h = O(log n)
```

So recursion depth is logarithmic.

---

# Approach 2: Bottom-up iterative processing on the array (recommended in practice)

## Intuition

Because the tree is stored implicitly by indices, we do not actually need recursion.

We can process internal nodes from the bottom upward.

For node `i`:

- left child is `2 * i`
- right child is `2 * i + 1`

If we have already updated the child costs so that they represent balanced subtree path sums, then the same recurrence applies:

- add `abs(cost[left] - cost[right])`
- update parent by:

```text
cost[parent] += max(cost[left], cost[right])
```

This is a very elegant array-based solution.

---

## Algorithm

Traverse nodes from the last internal node down to the root.

For every internal node `i`:

1. let `left = 2 * i`
2. let `right = 2 * i + 1`
3. add:

```text
abs(cost[left - 1] - cost[right - 1])
```

to answer 4. update parent:

```text
cost[i - 1] += max(cost[left - 1], cost[right - 1])
```

At the end, answer is the minimum number of increments.

---

## Java code

```java
class Solution {
    public int minIncrements(int n, int[] cost) {
        int answer = 0;

        for (int i = n / 2; i >= 1; i--) {
            int left = 2 * i;
            int right = 2 * i + 1;

            int leftSum = cost[left - 1];
            int rightSum = cost[right - 1];

            answer += Math.abs(leftSum - rightSum);
            cost[i - 1] += Math.max(leftSum, rightSum);
        }

        return answer;
    }
}
```

---

## Why this works

For leaves, `cost[leaf - 1]` is already the path sum from that leaf downward.

When we move upward, after processing a node’s children:

- each child’s `cost[...]` now represents the balanced maximum path sum for that child subtree

So parent processing is identical to the recursive formula.

This is just the bottom-up DFS written iteratively using the implicit array representation.

---

## Complexity

### Time complexity

```text
O(n)
```

Each internal node is processed once.

### Space complexity

```text
O(1)
```

ignoring the input array, since we update it in place.

This is the most space-efficient approach.

---

# Approach 3: Explicit tree construction + postorder DFS

## Intuition

If someone prefers working with real tree nodes rather than array indices, we can build the perfect binary tree structure explicitly and then run the same postorder logic.

This is not necessary, but it can make the reasoning more intuitive.

---

## Java code

```java
class Solution {
    static class Node {
        int val;
        Node left, right;

        Node(int val) {
            this.val = val;
        }
    }

    private int answer = 0;

    public int minIncrements(int n, int[] cost) {
        Node[] nodes = new Node[n + 1];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new Node(cost[i - 1]);
        }

        for (int i = 1; i <= n / 2; i++) {
            nodes[i].left = nodes[2 * i];
            nodes[i].right = nodes[2 * i + 1];
        }

        dfs(nodes[1]);
        return answer;
    }

    private int dfs(Node node) {
        if (node.left == null && node.right == null) {
            return node.val;
        }

        int left = dfs(node.left);
        int right = dfs(node.right);

        answer += Math.abs(left - right);

        return node.val + Math.max(left, right);
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

because we explicitly create tree nodes.

This is less efficient than using the array directly, but still correct.

---

# Approach 4: Brute-force path equalization idea (not practical)

## Intuition

A naive idea is:

1. compute all root-to-leaf path sums
2. find the maximum path sum
3. try to increment nodes along smaller paths until they match

This quickly becomes messy, because increments on shared ancestors affect multiple paths simultaneously.

That means path-based greedy adjustments are hard to do correctly if you look only from the root downward.

The bottom-up sibling-balancing approach avoids that problem completely.

So this approach is mainly useful to understand **why** the bottom-up solution is the right one.

---

# Why the bottom-up greedy is optimal

Consider any internal node with child path sums `L` and `R`.

All root-to-leaf paths through the left child are short by `R - L` if `L < R`.

No matter how we distribute increments inside the left subtree, we must add at least:

```text
R - L
```

total increments to the left side to make it match the right.

So the minimum necessary contribution at this node is exactly:

```text
abs(L - R)
```

That local balancing decision is independent of the rest of the tree, which is why the greedy bottom-up solution is globally optimal.

---

# Comparison of approaches

## Approach 1: Recursive DFS

- very clear conceptually
- directly mirrors tree reasoning
- `O(n)` time, `O(log n)` stack

## Approach 2: Iterative bottom-up on array

- most elegant for this problem
- no recursion
- `O(n)` time, `O(1)` extra space
- best practical solution

## Approach 3: Explicit tree construction

- fine for understanding
- extra memory not needed

## Approach 4: Path-based brute force

- not practical
- useful only as intuition

---

# Best approach

The best solution is:

## **Approach 2: Bottom-up iterative processing on the array**

Why:

- the tree is already implicitly represented by indices
- no need to build nodes
- no recursion needed
- minimal extra space
- cleanest implementation

---

# Final recommended solution

```java
class Solution {
    public int minIncrements(int n, int[] cost) {
        int answer = 0;

        for (int i = n / 2; i >= 1; i--) {
            int left = 2 * i;
            int right = 2 * i + 1;

            int leftSum = cost[left - 1];
            int rightSum = cost[right - 1];

            answer += Math.abs(leftSum - rightSum);
            cost[i - 1] += Math.max(leftSum, rightSum);
        }

        return answer;
    }
}
```

## Complexity

- **Time:** `O(n)`
- **Space:** `O(1)` extra

This is the cleanest and most efficient solution for the problem.
