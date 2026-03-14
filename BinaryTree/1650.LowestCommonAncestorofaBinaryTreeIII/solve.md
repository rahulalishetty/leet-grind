# 1650. Lowest Common Ancestor of a Binary Tree III

## Problem summary

We are given two nodes `p` and `q` in a binary tree.

Unlike the usual LCA problem, each node has a `parent` pointer:

```java
class Node {
    public int val;
    public Node left;
    public Node right;
    public Node parent;
}
```

We need to return the **lowest common ancestor** of `p` and `q`.

Important constraints:

- both `p` and `q` exist in the tree
- values are unique
- `p != q`

Because parent pointers are available, we do **not** need the root.

---

# Key observation

With parent pointers, the problem becomes very similar to:

- finding the intersection of two linked lists
- comparing ancestor chains of two nodes

Each node has a path upward to the root.

So the problem becomes:

> find the first common node on the upward paths from `p` and `q`

---

# Approach 1: Ancestor set / hash set

## Intuition

The most direct idea is:

1. Walk upward from `p` to the root and store every ancestor in a set.
2. Walk upward from `q`.
3. The first node from `q`’s upward path that appears in the set is the LCA.

Why the **first** such node?

Because as we move upward from `q`, the first shared ancestor is the one closest to `q`, and therefore the lowest common ancestor.

---

## Algorithm

1. Create a `HashSet<Node> ancestors`
2. Move from `p` upward using `parent`
   - add each node to the set
3. Move from `q` upward using `parent`
   - if current node is in the set, return it
4. If the loop ended, return `null`
   In this problem it will never happen because `p` and `q` are guaranteed to exist in the same tree.

---

## Java code

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    public Node lowestCommonAncestor(Node p, Node q) {
        Set<Node> ancestors = new HashSet<>();

        while (p != null) {
            ancestors.add(p);
            p = p.parent;
        }

        while (q != null) {
            if (ancestors.contains(q)) {
                return q;
            }
            q = q.parent;
        }

        return null;
    }
}
```

---

## Complexity

Let:

- `hp` = height from `p` to root
- `hq` = height from `q` to root

### Time complexity

```text
O(hp + hq)
```

### Space complexity

```text
O(hp)
```

because we store `p`’s ancestor chain.

---

# Approach 2: Compute depths, then align

## Intuition

Another clean approach is to first compute how far each node is from the root.

Suppose:

- `p` is deeper than `q`

Then before comparing them, we should move `p` upward until both are on the same level.

After that, move both upward together.
The first node where they meet is the LCA.

This is exactly the same idea as finding the intersection of two depth-aligned ancestor chains.

---

## Why it works

If one node is deeper, it cannot be the LCA with the shallower node until both are at the same depth.

So:

1. compute depth of each node
2. lift the deeper node
3. walk both upward together

Once they meet, that meeting point is the lowest common ancestor.

---

## Algorithm

1. Compute `depthP` by walking from `p` to root.
2. Compute `depthQ` by walking from `q` to root.
3. If one node is deeper, move it upward until both depths match.
4. While `p != q`:
   - `p = p.parent`
   - `q = q.parent`
5. Return `p` (or `q`)

---

## Java code

```java
class Solution {
    public Node lowestCommonAncestor(Node p, Node q) {
        int depthP = getDepth(p);
        int depthQ = getDepth(q);

        while (depthP > depthQ) {
            p = p.parent;
            depthP--;
        }

        while (depthQ > depthP) {
            q = q.parent;
            depthQ--;
        }

        while (p != q) {
            p = p.parent;
            q = q.parent;
        }

        return p;
    }

    private int getDepth(Node node) {
        int depth = 0;
        while (node != null) {
            depth++;
            node = node.parent;
        }
        return depth;
    }
}
```

---

## Complexity

### Time complexity

```text
O(hp + hq)
```

We compute both depths, then possibly walk both again.

### Space complexity

```text
O(1)
```

This is better than the hash-set approach in memory usage.

---

# Approach 3: Two pointers, linked-list intersection style

## Intuition

This is the most elegant approach.

Treat each upward path as a linked list ending at the root.

Then use the classic linked-list intersection trick:

- pointer `a` starts at `p`
- pointer `b` starts at `q`

Move both upward one step at a time.

When one pointer becomes `null`, redirect it to the other starting node.

So:

- `a`: `p -> parent -> ... -> null -> q -> parent -> ...`
- `b`: `q -> parent -> ... -> null -> p -> parent -> ...`

Eventually, they will meet at the LCA.

---

## Why this works

Let:

- `A` = unique path from `p` up to LCA
- `B` = unique path from `q` up to LCA
- `C` = shared path from LCA up to root

Then the full paths are:

```text
p path = A + C
q path = B + C
```

Pointer `a` travels:

```text
A + C + B + C
```

Pointer `b` travels:

```text
B + C + A + C
```

After the redirection, both pointers traverse exactly the same total length, so they line up and meet at the first common point: the LCA.

This is identical to the linked-list intersection proof.

---

## Algorithm

1. Set `a = p`, `b = q`
2. While `a != b`:
   - if `a == null`, set `a = q`, else `a = a.parent`
   - if `b == null`, set `b = p`, else `b = b.parent`
3. Return `a`

---

## Java code

```java
class Solution {
    public Node lowestCommonAncestor(Node p, Node q) {
        Node a = p;
        Node b = q;

        while (a != b) {
            a = (a == null) ? q : a.parent;
            b = (b == null) ? p : b.parent;
        }

        return a;
    }
}
```

---

## Complexity

### Time complexity

```text
O(hp + hq)
```

Each pointer traverses at most two ancestor chains.

### Space complexity

```text
O(1)
```

This is the best solution in both elegance and memory.

---

# Approach 4: Recursive ancestor search from one side (less practical)

## Intuition

You can also think recursively:

- check whether `p` is an ancestor of `q`
- if not, move `p` upward and try again

Or symmetrically from `q`.

This works, but it is not as efficient or elegant as the previous approaches, and usually not the best interview answer.

Still, it helps conceptually:

the LCA is the first ancestor of one node that also contains the other node in its ancestor chain.

---

## Java code

```java
class Solution {
    public Node lowestCommonAncestor(Node p, Node q) {
        Node curr = p;

        while (curr != null) {
            if (isAncestor(curr, q)) {
                return curr;
            }
            curr = curr.parent;
        }

        return null;
    }

    private boolean isAncestor(Node ancestor, Node node) {
        while (node != null) {
            if (node == ancestor) {
                return true;
            }
            node = node.parent;
        }
        return false;
    }
}
```

---

## Complexity

### Time complexity

Worst case:

```text
O(hp * hq)
```

because for each ancestor of `p`, we may scan `q`’s chain.

### Space complexity

```text
O(1)
```

This is correct but clearly worse than the previous approaches.

---

# Comparing the approaches

## Approach 1: Hash set

- very intuitive
- easy to explain
- uses extra memory

## Approach 2: Depth alignment

- clean
- no extra memory
- standard ancestor-chain technique

## Approach 3: Two-pointer switching

- most elegant
- no explicit depth computation
- constant extra space
- excellent interview solution

## Approach 4: Repeated ancestor checking

- correct
- much less efficient
- mainly educational

---

# Which solution is best?

For interviews, **Approach 3** is usually the strongest answer.

Why?

- very short
- constant extra space
- elegant proof
- directly leverages parent pointers
- strongly resembles linked-list intersection, which is a known pattern

If the interviewer prefers something more explicit, **Approach 2** is also excellent.

---

# Dry run for Approach 3

Suppose:

```text
p = 5
q = 4
```

Ancestor chains:

```text
5 -> 3 -> null
4 -> 2 -> 5 -> 3 -> null
```

Run the pointers:

- `a = 5`, `b = 4`
- `a = 3`, `b = 2`
- `a = null`, `b = 5`
- redirect `a = q = 4`, `b = 3`
- `a = 2`, `b = null`
- `a = 5`, `b = p = 5`

They meet at `5`, which is correct.

---

# Final recommended solution

```java
class Solution {
    public Node lowestCommonAncestor(Node p, Node q) {
        Node a = p;
        Node b = q;

        while (a != b) {
            a = (a == null) ? q : a.parent;
            b = (b == null) ? p : b.parent;
        }

        return a;
    }
}
```

This is the cleanest and most efficient solution for this problem.
