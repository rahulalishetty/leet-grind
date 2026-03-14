# 2689. Extract Kth Character From The Rope Tree

## Problem summary

We are given a **rope binary tree**.

Each node has:

- `left`
- `right`
- `val` → a string
- `len` → an integer

There are two kinds of nodes:

### Leaf node

- `len == 0`
- `val` is a non-empty string
- no children

### Internal node

- `len > 0`
- `val == ""`
- it represents concatenation of its children

For any node:

- if it is a leaf: `S[node] = node.val`
- if it is internal: `S[node] = S[left] + S[right]`

We must return the **k-th character** of `S[root]`.

Important: `k` is **1-indexed**.

---

# Core insight

A rope tree exists specifically so that we **do not have to build the whole string**.

If we built the whole string at every internal node, that would waste time and memory.

Instead, at each internal node, we only need to know:

- how long the left part is
- whether the k-th character lies in the left subtree or the right subtree

Then we move into exactly one subtree.

That gives a clean logarithmic-style tree descent in balanced cases, and linear in height in general.

---

# Very important subtlety: how to know left subtree length?

The statement defines:

```text
S[node] = concat(S[node.left], S[node.right])
S[node].length = node.len
```

But to descend efficiently, we need the **length of the left subtree**.

In this problem’s rope-tree convention, internal nodes carry `len`, and the standard interpretation used by rope trees is:

> `node.len` stores the length of the **left subtree string**

That is exactly what allows us to decide:

- if `k <= node.len`, go left
- otherwise go right with adjusted index

This is also consistent with the examples.

So for internal nodes:

```text
left subtree length = node.len
```

---

# Approach 1: Direct recursive rope traversal (optimal)

## Intuition

At every node:

### If it is a leaf

Then the answer is directly inside `node.val`.

Because `k` is 1-indexed:

```java
node.val.charAt(k - 1)
```

### If it is an internal node

Its left subtree contributes `node.len` characters.

So:

- if `k <= node.len`, the answer is in the left subtree
- otherwise, the answer is in the right subtree, and its new index becomes:

```text
k - node.len
```

This avoids constructing the full string.

---

## Algorithm

1. If `root.len == 0`, it is a leaf:
   - return `root.val.charAt(k - 1)`
2. Otherwise:
   - if `k <= root.len`, recurse into `root.left`
   - else recurse into `root.right` with `k - root.len`

---

## Java code

```java
class Solution {
    public char getKthCharacter(RopeTreeNode root, int k) {
        if (root.len == 0) {
            return root.val.charAt(k - 1);
        }

        if (k <= root.len) {
            return getKthCharacter(root.left, k);
        } else {
            return getKthCharacter(root.right, k - root.len);
        }
    }
}
```

---

## Why this works

For an internal node:

```text
S[node] = S[left] + S[right]
```

and the first `node.len` characters belong to `S[left]`.

So:

- positions `1 ... node.len` come from left
- positions `node.len + 1 ... total` come from right

Thus the recursive choice is exact.

---

## Complexity

Let `h` be the height of the rope tree.

### Time complexity

```text
O(h)
```

We visit only one node per level.

### Space complexity

```text
O(h)
```

due to recursion stack.

If the rope is balanced, `h` can be much smaller than total string length.

---

# Approach 2: Iterative traversal (same optimal idea, no recursion)

## Intuition

Approach 1 is already optimal, but we can avoid recursion by using a loop.

At each step:

- if current node is leaf, return the answer from its string
- otherwise decide whether to go left or right
- update `k` when going right

This is often preferable in Java if you want to avoid recursion entirely.

---

## Algorithm

1. Start with `curr = root`
2. While `curr` is not a leaf:
   - if `k <= curr.len`, move to `curr.left`
   - else:
     - `k -= curr.len`
     - move to `curr.right`
3. Return `curr.val.charAt(k - 1)`

---

## Java code

```java
class Solution {
    public char getKthCharacter(RopeTreeNode root, int k) {
        RopeTreeNode curr = root;

        while (curr.len != 0) {
            if (k <= curr.len) {
                curr = curr.left;
            } else {
                k -= curr.len;
                curr = curr.right;
            }
        }

        return curr.val.charAt(k - 1);
    }
}
```

---

## Complexity

### Time complexity

```text
O(h)
```

### Space complexity

```text
O(1)
```

This is slightly better than the recursive version in auxiliary space.

---

# Approach 3: Build the full rope string, then index (simple but inefficient)

## Intuition

The most straightforward idea is:

1. reconstruct the entire string represented by the rope
2. return the k-th character

This absolutely works, and can be useful as a first brute-force solution or for understanding.

But it defeats the purpose of a rope tree.

A rope tree is designed to support efficient substring / character access without building the full string.

---

## Algorithm

1. DFS to build the full string:
   - leaf → return `node.val`
   - internal → return `build(left) + build(right)`
2. Return character at index `k - 1`

---

## Java code

```java
class Solution {
    public char getKthCharacter(RopeTreeNode root, int k) {
        String full = build(root);
        return full.charAt(k - 1);
    }

    private String build(RopeTreeNode node) {
        if (node.len == 0) {
            return node.val;
        }

        return build(node.left) + build(node.right);
    }
}
```

---

## Complexity

Let `L = S[root].length`.

### Time complexity

Naively:

```text
O(L^2)
```

in languages where repeated string concatenation copies strings each time.

With `StringBuilder` done carefully, it can be improved closer to:

```text
O(L)
```

But either way, it is still more work than necessary if we only need one character.

### Space complexity

```text
O(L)
```

because we build the whole string.

---

## Better brute-force with StringBuilder

```java
class Solution {
    public char getKthCharacter(RopeTreeNode root, int k) {
        StringBuilder sb = new StringBuilder();
        build(root, sb);
        return sb.charAt(k - 1);
    }

    private void build(RopeTreeNode node, StringBuilder sb) {
        if (node == null) {
            return;
        }

        if (node.len == 0) {
            sb.append(node.val);
            return;
        }

        build(node.left, sb);
        build(node.right, sb);
    }
}
```

This is much better than repeated string concatenation, but it still constructs the full rope string unnecessarily.

---

# Approach 4: Explicit stack simulation of the recursive descent

## Intuition

The recursive optimal solution is already very clean.
But if you want to make the traversal mechanics explicit, you can simulate it with a stack.

In practice, for this problem, a stack is not better than the iterative while-loop solution. The loop is simpler.

Still, the stack version is a legitimate alternative if you want a more general tree traversal style.

We push one node at a time because only one branch matters.

---

## Java code

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    static class State {
        RopeTreeNode node;
        int k;

        State(RopeTreeNode node, int k) {
            this.node = node;
            this.k = k;
        }
    }

    public char getKthCharacter(RopeTreeNode root, int k) {
        Deque<State> stack = new ArrayDeque<>();
        stack.push(new State(root, k));

        while (!stack.isEmpty()) {
            State cur = stack.pop();
            RopeTreeNode node = cur.node;
            int idx = cur.k;

            if (node.len == 0) {
                return node.val.charAt(idx - 1);
            }

            if (idx <= node.len) {
                stack.push(new State(node.left, idx));
            } else {
                stack.push(new State(node.right, idx - node.len));
            }
        }

        throw new IllegalStateException("Invalid rope tree input");
    }
}
```

---

## Complexity

### Time complexity

```text
O(h)
```

### Space complexity

```text
O(h)
```

because the stack may hold up to one state per level.

---

# Which approach is best?

## Best practical solution

**Approach 2 (iterative traversal)**

Why:

- optimal
- simple
- no recursion overhead
- no extra memory except a few variables

## Best for explanation

**Approach 1 (recursive traversal)**

Why:

- directly mirrors the rope-tree definition
- easiest to explain in an interview

## Worst but simplest brute force

**Approach 3**

Works, but wastes time and memory.

---

# Common mistakes

## 1. Treating `k` as 0-indexed

The problem clearly says `k` is 1-indexed.

So at a leaf:

```java
node.val.charAt(k - 1)
```

not `charAt(k)`.

---

## 2. Building the whole string unnecessarily

A rope tree exists precisely to avoid that.

If you build the full string first, the solution is correct but not leveraging the structure.

---

## 3. Misusing `node.len`

For efficient rope descent, internal node `len` must be interpreted as:

```text
length of the left subtree string
```

Then the branch decision is:

```java
if (k <= node.len) go left
else go right with k - node.len
```

---

# Final recommended solution

```java
class Solution {
    public char getKthCharacter(RopeTreeNode root, int k) {
        RopeTreeNode curr = root;

        while (curr.len != 0) {
            if (k <= curr.len) {
                curr = curr.left;
            } else {
                k -= curr.len;
                curr = curr.right;
            }
        }

        return curr.val.charAt(k - 1);
    }
}
```

## Complexity

- **Time:** `O(h)`
- **Space:** `O(1)`

where `h` is the height of the rope tree.

This is the cleanest and most efficient solution for the problem.
