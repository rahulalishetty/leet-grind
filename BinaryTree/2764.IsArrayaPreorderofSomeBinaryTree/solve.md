# 2764. Is Array a Preorder of Some Binary Tree

## Problem summary

We are given a list:

```text
nodes[i] = [id, parentId]
```

Interpretation:

- `id` is the node at position `i` in the given order
- `parentId` is its parent in the tree
- exactly one node has `parentId = -1`, the root

The input is guaranteed to form **a binary tree**.

We must decide whether the given order is a valid **preorder traversal** of **that** tree.

Preorder means:

1. visit current node
2. visit left subtree
3. visit right subtree

The twist is that we are **not** told which child is left and which child is right.
We only need to know whether **some** assignment of left/right children makes the order valid preorder.

---

# Key insight

In any preorder traversal, the nodes of a subtree always appear as **one contiguous block**.

That means:

- if a node has descendants
- then all descendants of that node must appear consecutively right after it, though split between its two child subtrees

This suggests several possible strategies:

1. explicitly build the tree and verify interval/subtree contiguity
2. simulate preorder validity with a stack of active ancestors
3. compute subtree ranges and validate them

The cleanest solution is the **stack-based ancestor simulation**.

---

# Approach 1: Stack of active ancestors (optimal)

## Intuition

Suppose we scan the array from left to right.

At position `i`, we see node:

```text
[id, parentId]
```

In a preorder traversal, the current node must belong to the subtree of some currently "open" ancestor.

The most recent ancestor whose subtree has not yet finished is naturally represented by a stack.

### How the stack works

The stack stores the current path of ancestors whose subtree blocks are still active.

For a new node with `parentId`:

- if `parentId` is the top of the stack, great: this node is the next descendant of that parent
- otherwise, we must pop completed subtrees until we find `parentId`
- if we never find it, then the order is impossible

This mirrors how preorder enters and exits subtrees.

---

## Why this works

In preorder, once we leave a subtree, we can never come back to it later.

So if some node appears whose parent belongs to a subtree we already closed, the ordering is invalid.

The stack precisely tracks which ancestor subtrees are still open.

---

## Algorithm

1. The first node must be the root, so its `parentId` must be `-1`
2. Initialize a stack with the root id
3. For each next node `[id, parentId]`:
   - pop while stack top is not `parentId`
   - if stack becomes empty before finding `parentId`, return `false`
   - once found, push `id`
4. If all nodes are processed successfully, return `true`

---

## Java code

```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

class Solution {
    public boolean isPreorder(List<List<Integer>> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return false;
        }

        // First node must be the root.
        if (nodes.get(0).get(1) != -1) {
            return false;
        }

        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(nodes.get(0).get(0)); // root id

        for (int i = 1; i < nodes.size(); i++) {
            int id = nodes.get(i).get(0);
            int parentId = nodes.get(i).get(1);

            while (!stack.isEmpty() && stack.peek() != parentId) {
                stack.pop();
            }

            if (stack.isEmpty()) {
                return false;
            }

            stack.push(id);
        }

        return true;
    }
}
```

---

## Dry run

### Example 1

```text
[[0,-1],[1,0],[2,0],[3,2],[4,2]]
```

Process:

- root = 0, stack = [0]
- node 1, parent 0 → top is 0 → push 1 → stack [1,0]
- node 2, parent 0 → pop 1, top becomes 0 → push 2 → stack [2,0]
- node 3, parent 2 → top is 2 → push 3 → stack [3,2,0]
- node 4, parent 2 → pop 3, top becomes 2 → push 4 → stack [4,2,0]

Valid.

### Example 2

```text
[[0,-1],[1,0],[2,0],[3,1],[4,1]]
```

Process:

- root = 0, stack = [0]
- node 1, parent 0 → push 1 → stack [1,0]
- node 2, parent 0 → pop 1, top 0 → push 2 → stack [2,0]
- node 3, parent 1 → pop 2, pop 0, stack empty → invalid

Return `false`.

---

## Complexity

Let `n = nodes.size()`.

### Time complexity

```text
O(n)
```

Each node id is pushed once and popped at most once.

### Space complexity

```text
O(n)
```

for the stack in the worst case.

---

# Approach 2: Build children lists, compute subtree intervals, and verify contiguity

## Intuition

Another way is to use the fundamental preorder property:

> every subtree must occupy one contiguous interval in the preorder array

Since the input already gives parent relationships, we can build the actual tree structure (children per parent, up to two children).

Then:

1. map each node id to its index in the given array
2. compute the size of each subtree
3. verify that all nodes in a subtree lie inside the interval:

```text
[index[node], index[node] + subtreeSize[node] - 1]
```

Also, each child must appear after its parent.

This is more elaborate than the stack solution but gives a strong structural proof.

---

## High-level idea

If a node has subtree size `sz`, then in preorder its subtree occupies exactly `sz` consecutive positions.

So for every node:

- all descendants must fall inside its interval
- both child subtrees, if they exist, must also be consecutive sub-intervals

If that fails anywhere, the array is not a preorder traversal.

Because left and right child order can be swapped, we only need the children’s subtree intervals to fit consecutively in **some** order.

---

## Java code

```java
import java.util.*;

class Solution {
    private Map<Integer, List<Integer>> children = new HashMap<>();
    private Map<Integer, Integer> index = new HashMap<>();
    private boolean ok = true;

    public boolean isPreorder(List<List<Integer>> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return false;
        }

        int root = -1;

        for (int i = 0; i < nodes.size(); i++) {
            int id = nodes.get(i).get(0);
            int parent = nodes.get(i).get(1);

            index.put(id, i);
            children.putIfAbsent(id, new ArrayList<>());

            if (parent == -1) {
                if (i != 0) return false; // root must appear first
                root = id;
            } else {
                children.putIfAbsent(parent, new ArrayList<>());
                children.get(parent).add(id);
                if (children.get(parent).size() > 2) return false;
            }
        }

        if (root == -1) return false;

        compute(root);
        return ok;
    }

    // returns subtree size
    private int compute(int node) {
        if (!ok) return 0;

        List<Integer> kids = children.getOrDefault(node, Collections.emptyList());

        if (kids.isEmpty()) {
            return 1;
        }

        if (kids.size() == 1) {
            int c = kids.get(0);
            if (index.get(c) <= index.get(node)) {
                ok = false;
                return 0;
            }
            return 1 + compute(c);
        }

        int a = kids.get(0), b = kids.get(1);

        if (index.get(a) <= index.get(node) || index.get(b) <= index.get(node)) {
            ok = false;
            return 0;
        }

        int sizeA = compute(a);
        int sizeB = compute(b);

        int start = index.get(node) + 1;

        // children subtrees must occupy consecutive preorder intervals
        boolean order1 = index.get(a) == start && index.get(b) == start + sizeA;
        boolean order2 = index.get(b) == start && index.get(a) == start + sizeB;

        if (!order1 && !order2) {
            ok = false;
        }

        return 1 + sizeA + sizeB;
    }
}
```

---

## Complexity

### Time complexity

```text
O(n)
```

We build maps once and DFS once.

### Space complexity

```text
O(n)
```

for maps, child lists, and recursion stack.

---

## Comments

This approach is more structural and can be useful if you want to reason explicitly about contiguous preorder intervals.

Still, the stack solution is shorter and more elegant.

---

# Approach 3: Recursive interval validation after building the tree

## Intuition

This is closely related to Approach 2, but phrased more directly as:

- build the child lists
- recursively ask:
  - can the subtree rooted at `node` start at array index `start`?
  - if yes, how many positions does it consume?

For preorder:

- the root of the subtree must appear at `start`
- then one child subtree
- then the other child subtree, if present

Because the children can be assigned as left/right in either order, we try both child orders when there are two children.

This is conceptually neat, though the implementation is a bit more involved.

---

## Java code

```java
import java.util.*;

class Solution {
    private Map<Integer, List<Integer>> children = new HashMap<>();
    private Map<Integer, Integer> index = new HashMap<>();

    public boolean isPreorder(List<List<Integer>> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return false;
        }

        int root = -1;

        for (int i = 0; i < nodes.size(); i++) {
            int id = nodes.get(i).get(0);
            int parent = nodes.get(i).get(1);

            index.put(id, i);
            children.putIfAbsent(id, new ArrayList<>());

            if (parent == -1) {
                if (i != 0) return false;
                root = id;
            } else {
                children.putIfAbsent(parent, new ArrayList<>());
                children.get(parent).add(id);
                if (children.get(parent).size() > 2) return false;
            }
        }

        if (root == -1) return false;

        int used = validate(root, 0);
        return used == nodes.size();
    }

    // Returns subtree size if valid starting at index `start`, else -1
    private int validate(int node, int start) {
        if (index.get(node) != start) {
            return -1;
        }

        List<Integer> kids = children.getOrDefault(node, Collections.emptyList());

        if (kids.isEmpty()) {
            return 1;
        }

        if (kids.size() == 1) {
            int size = validate(kids.get(0), start + 1);
            return size == -1 ? -1 : 1 + size;
        }

        int a = kids.get(0), b = kids.get(1);

        // try order: a then b
        int sizeA = validate(a, start + 1);
        if (sizeA != -1) {
            int sizeB = validate(b, start + 1 + sizeA);
            if (sizeB != -1) {
                return 1 + sizeA + sizeB;
            }
        }

        // try order: b then a
        int sizeB = validate(b, start + 1);
        if (sizeB != -1) {
            int sizeA2 = validate(a, start + 1 + sizeB);
            if (sizeA2 != -1) {
                return 1 + sizeB + sizeA2;
            }
        }

        return -1;
    }
}
```

---

## Complexity

### Time complexity

For binary tree and at most two children, this is still effectively:

```text
O(n)
```

because each node is processed a constant number of times.

### Space complexity

```text
O(n)
```

for maps and recursion stack.

---

# Approach 4: Explicit preorder generation after assigning child order (not ideal)

## Intuition

You might think:

1. build the tree
2. assign one child as left and the other as right
3. generate preorder
4. compare with input

But this is not enough, because when a node has two children, we do **not** know which one should be left and which one should be right.

Trying all assignments would be exponential in the worst case.

So this approach is not practical unless enhanced with interval reasoning, which then essentially turns it into Approach 2 or 3.

Thus, this is mostly useful as a conceptual starting point, not as the final solution.

---

# Best approach

The best solution is **Approach 1: stack of active ancestors**.

Why?

- it is the shortest
- it is linear
- it captures exactly the “once a subtree is closed, preorder can’t return to it” property
- no explicit tree reconstruction is needed

---

# Subtle note about left vs right child

The examples may label a child as “left” or “right” in a way that seems unusual.

That is okay.

The problem only asks whether the order is a preorder traversal of **some** binary tree with those parent links.
Since parent-child relationships are fixed but left/right assignment is not, either child can be treated as left or right if it makes preorder valid.

That is why the stack approach works without ever assigning left/right explicitly.

---

# Final recommended solution

```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

class Solution {
    public boolean isPreorder(List<List<Integer>> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return false;
        }

        if (nodes.get(0).get(1) != -1) {
            return false;
        }

        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(nodes.get(0).get(0));

        for (int i = 1; i < nodes.size(); i++) {
            int id = nodes.get(i).get(0);
            int parentId = nodes.get(i).get(1);

            while (!stack.isEmpty() && stack.peek() != parentId) {
                stack.pop();
            }

            if (stack.isEmpty()) {
                return false;
            }

            stack.push(id);
        }

        return true;
    }
}
```

## Complexity

- **Time:** `O(n)`
- **Space:** `O(n)`

This is the cleanest and most efficient solution for the problem.
