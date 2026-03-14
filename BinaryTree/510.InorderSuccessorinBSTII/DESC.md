# 510. Inorder Successor in BST II

Given a node in a binary search tree, return the **in-order successor** of that node in the BST.

If that node has **no in-order successor**, return `null`.

The **successor** of a node is the node with the **smallest key greater than `node.val`**.

In this problem, you are given **direct access to the node**, but **not the root of the tree**.

Each node also contains a pointer to its **parent node**.

---

## Node Definition

```java
class Node {
    public int val;
    public Node left;
    public Node right;
    public Node parent;
}
```

---

## Example 1

**Input**

```
tree = [2,1,3]
node = 1
```

**Output**

```
2
```

**Explanation**

The inorder traversal of the tree is:

```
1 → 2 → 3
```

The successor of **1** is **2**.

---

## Example 2

**Input**

```
tree = [5,3,6,2,4,null,null,1]
node = 6
```

**Output**

```
null
```

**Explanation**

The node **6** is the largest node in the tree, so it has **no inorder successor**.

---

## Constraints

```
1 <= number of nodes <= 10^4
-10^5 <= Node.val <= 10^5
All node values are unique.
```

---

## Follow-up

Can you solve the problem **without comparing node values**, relying only on **tree structure and parent pointers**?
