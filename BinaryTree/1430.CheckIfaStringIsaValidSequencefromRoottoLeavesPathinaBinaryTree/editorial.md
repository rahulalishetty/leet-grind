# 1430. Check If a String Is a Valid Sequence from Root to Leaves Path in a Binary Tree

## Problem

Given a binary tree and an integer array `arr`, determine whether `arr` matches **exactly one root-to-leaf path**.

A sequence is **valid** only if:

- it starts at the root,
- every next value matches the next node on the path,
- it ends **at a leaf** exactly when the array ends.

So even if the values match a prefix of some path, the answer is still `false` unless the last matched node is a leaf.

---

## Core observation

At any node, there are only a few valid ways to continue:

- If the node is `null`, the sequence cannot continue.
- If the current node's value does not equal `arr[index]`, this path fails immediately.
- If we consumed the last value in `arr`, then the current node **must be a leaf**.
- Otherwise, we must continue into either the left or right child.

This makes the problem a natural DFS / backtracking problem.

---

# Approach 1: Recursive DFS

## Intuition

This is the cleanest approach.

We recursively try to match `arr[index]` with the current node.

At each recursive call:

1. Reject if the node is null.
2. Reject if the value does not match.
3. If this is the last element of `arr`, return whether the current node is a leaf.
4. Otherwise recurse into left or right.

Because the path must be root-to-leaf, we cannot stop early after matching all values unless we are exactly at a leaf.

---

## Algorithm

Define a helper:

```text
dfs(node, index)
```

It returns whether there exists a valid path starting at `node` that matches `arr[index ... end]`.

Steps:

1. If `node == null`, return `false`.
2. If `index >= arr.length`, return `false`.
3. If `node.val != arr[index]`, return `false`.
4. If `index == arr.length - 1`:
   - return whether `node.left == null && node.right == null`
5. Return:
   - `dfs(node.left, index + 1) || dfs(node.right, index + 1)`

---

## Java code

```java
class Solution {
    public boolean isValidSequence(TreeNode root, int[] arr) {
        return dfs(root, arr, 0);
    }

    private boolean dfs(TreeNode node, int[] arr, int index) {
        // Null node cannot match anything.
        if (node == null) {
            return false;
        }

        // Out of bounds or value mismatch means invalid path.
        if (index >= arr.length || node.val != arr[index]) {
            return false;
        }

        // If this is the last value, current node must be a leaf.
        if (index == arr.length - 1) {
            return node.left == null && node.right == null;
        }

        // Otherwise continue searching down either subtree.
        return dfs(node.left, arr, index + 1) || dfs(node.right, arr, index + 1);
    }
}
```

---

## Dry run on Example 1

```text
root = [0,1,0,0,1,0,null,null,1,0,0]
arr  = [0,1,0,1]
```

- Start at root `0`, matches `arr[0]`
- Go left to `1`, matches `arr[1]`
- Go left/right searching for `0`
- Reach node `0`, matches `arr[2]`
- Go to child `1`, matches `arr[3]`
- This is the last array element, and node `1` is a leaf
- Return `true`

---

## Complexity

Let `n` be the number of nodes and `m` be the length of `arr`.

**Time complexity:** `O(min(n, 2^m))` in reasoning terms, but more practically `O(n)` worst case.

Why `O(n)` worst case:

- each visited node does only `O(1)` work,
- recursion only follows tree edges,
- in the worst case you may inspect many nodes before failure.

**Space complexity:** `O(h)`

- `h` is the tree height due to recursion stack.
- Worst case skewed tree: `O(n)`

---

# Approach 2: Iterative DFS with an explicit stack

## Intuition

The recursive solution is simple, but sometimes interviewers want the iterative version too.

We simulate the same DFS using our own stack.
Each stack entry stores:

- the current node,
- the current index in `arr`.

Whenever we pop an entry, we perform the same checks as in the recursive approach.

This is a direct translation of recursion into iteration.

---

## Algorithm

Use a stack of pairs `(node, index)`.

1. Push `(root, 0)`
2. While the stack is not empty:
   - pop `(node, index)`
   - if node is null, continue
   - if node value mismatches `arr[index]`, continue
   - if `index == arr.length - 1` and node is a leaf, return `true`
   - otherwise push children with `index + 1`
3. If the loop ends, return `false`

---

## Java code

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    private static class State {
        TreeNode node;
        int index;

        State(TreeNode node, int index) {
            this.node = node;
            this.index = index;
        }
    }

    public boolean isValidSequence(TreeNode root, int[] arr) {
        if (root == null) {
            return false;
        }

        Deque<State> stack = new ArrayDeque<>();
        stack.push(new State(root, 0));

        while (!stack.isEmpty()) {
            State current = stack.pop();
            TreeNode node = current.node;
            int index = current.index;

            if (node == null || index >= arr.length || node.val != arr[index]) {
                continue;
            }

            if (index == arr.length - 1) {
                if (node.left == null && node.right == null) {
                    return true;
                }
                continue;
            }

            stack.push(new State(node.right, index + 1));
            stack.push(new State(node.left, index + 1));
        }

        return false;
    }
}
```

---

## Complexity

**Time complexity:** `O(n)` worst case
**Space complexity:** `O(h)` average / `O(n)` worst case

The stack may contain up to one path worth of nodes in a skewed tree, and sometimes more depending on branching, but `O(n)` is the safe worst-case bound.

---

# Approach 3: Breadth-First Search (BFS)

## Intuition

DFS is more natural here, but BFS also works.

Instead of exploring deep first, BFS explores level by level.
Each queue element stores:

- a node,
- the matched index in `arr`.

This is not better than DFS for this problem, but it is a valid alternative and sometimes useful if you want all same-depth states processed together.

---

## Algorithm

1. If root is null, return false
2. Push `(root, 0)` into a queue
3. While queue is not empty:
   - pop front
   - reject null / mismatch / out-of-range
   - if this is the last array index and node is leaf, return true
   - otherwise enqueue children with `index + 1`
4. Return false

---

## Java code

```java
import java.util.ArrayDeque;
import java.util.Queue;

class Solution {
    private static class State {
        TreeNode node;
        int index;

        State(TreeNode node, int index) {
            this.node = node;
            this.index = index;
        }
    }

    public boolean isValidSequence(TreeNode root, int[] arr) {
        if (root == null) {
            return false;
        }

        Queue<State> queue = new ArrayDeque<>();
        queue.offer(new State(root, 0));

        while (!queue.isEmpty()) {
            State current = queue.poll();
            TreeNode node = current.node;
            int index = current.index;

            if (node == null || index >= arr.length || node.val != arr[index]) {
                continue;
            }

            if (index == arr.length - 1) {
                if (node.left == null && node.right == null) {
                    return true;
                }
                continue;
            }

            queue.offer(new State(node.left, index + 1));
            queue.offer(new State(node.right, index + 1));
        }

        return false;
    }
}
```

---

## Complexity

**Time complexity:** `O(n)` worst case
**Space complexity:** `O(w)` where `w` is the maximum width of the tree, worst case `O(n)`

---

# Approach 4: Bottom-up interpretation (why it is less natural)

## Intuition

You could try to think from leaves upward:

- does some leaf correspond to the end of `arr`?
- does its parent correspond to the previous value?
- and so on.

But this is awkward because the array is naturally consumed from left to right, starting at the root.
A bottom-up solution ends up needing extra bookkeeping and gives no advantage.

So while it is possible to frame the problem this way, it is not the practical interview solution.

The correct takeaway is:

- **Top-down DFS is the most natural**
- iterative DFS is the best alternative
- BFS is valid but not especially helpful here

---

# Edge cases to watch

## 1. Matching values but not ending at a leaf

Example:

```text
arr = [0, 1, 1]
```

If the path `0 -> 1 -> 1` exists but the last node still has children, the answer is `false`.

This is the most common mistake.

---

## 2. Array longer than any root-to-leaf path

Then eventually recursion or iteration reaches `null` before consuming all of `arr`, so answer is `false`.

---

## 3. Root mismatch

If `root.val != arr[0]`, answer is immediately `false`.

---

## 4. Single-node tree

If the tree has one node:

- return `true` only if `arr.length == 1` and `arr[0] == root.val`

---

# Recommended interview answer

If asked in an interview, start with **Approach 1 (recursive DFS)**.

It is:

- the shortest,
- the clearest,
- easy to reason about,
- easy to prove correct.

Then mention:

- iterative DFS as the non-recursive version,
- BFS as another valid traversal-based alternative.

---

# Final recommendation

Use this solution in practice:

```java
class Solution {
    public boolean isValidSequence(TreeNode root, int[] arr) {
        return dfs(root, arr, 0);
    }

    private boolean dfs(TreeNode node, int[] arr, int index) {
        if (node == null || index >= arr.length || node.val != arr[index]) {
            return false;
        }

        if (index == arr.length - 1) {
            return node.left == null && node.right == null;
        }

        return dfs(node.left, arr, index + 1) || dfs(node.right, arr, index + 1);
    }
}
```

This is the most direct and robust approach.
