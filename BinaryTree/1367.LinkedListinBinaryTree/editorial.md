# 1367. Linked List in Binary Tree — Approaches

## Overview

We are given:

- A **binary tree**
- A **linked list**

Our goal is to determine whether the **linked list appears as a downward path in the binary tree**.

A **downward path** means:

```
start at any node → move only to children
```

---

# Approach 1: DFS (Recursive)

## Intuition

We try **every node in the tree** as a potential starting point.

For each node:

1. Check if the linked list matches starting from that node.
2. If not, recursively try its children as new starting points.

DFS is ideal because it naturally explores **complete paths before backtracking**.

---

## Algorithm

### Step 1: Start traversal

If root is null:

```
return false
```

Otherwise call:

```
checkPath(root, head)
```

---

### Step 2: checkPath(node, head)

For each node:

1. Check if the linked list matches starting from this node.
2. If yes → return true.
3. Otherwise recursively check:

```
node.left
node.right
```

---

### Step 3: dfs(node, head)

This checks whether the linked list matches starting at the current node.

Rules:

```
if head == null → return true
if node == null → return false
if node.val != head.val → return false
```

Otherwise continue checking:

```
dfs(node.left, head.next)
OR
dfs(node.right, head.next)
```

---

## Implementation

```java
class Solution {

    public boolean isSubPath(ListNode head, TreeNode root) {
        if (root == null) return false;
        return checkPath(root, head);
    }

    private boolean checkPath(TreeNode node, ListNode head) {
        if (node == null) return false;

        if (dfs(node, head)) return true;

        return checkPath(node.left, head) || checkPath(node.right, head);
    }

    private boolean dfs(TreeNode node, ListNode head) {
        if (head == null) return true;
        if (node == null) return false;
        if (node.val != head.val) return false;

        return dfs(node.left, head.next) || dfs(node.right, head.next);
    }
}
```

---

## Complexity Analysis

Let:

```
n = number of nodes in the tree
m = length of the linked list
```

### Time Complexity

```
O(n × m)
```

Worst case:

- Every tree node is tested as a starting point.
- Each test may traverse up to `m` nodes.

---

### Space Complexity

```
O(n + m)
```

Used by recursion stack.

---

# Approach 2: Iterative Stack Approach

## Intuition

Any recursive solution can be converted to an **iterative version using stacks**.

Instead of relying on the **call stack**, we explicitly manage traversal using stacks.

This helps avoid stack overflow in very deep trees.

---

## Strategy

1. Use a stack to traverse all nodes in the tree.
2. For each node, check if the linked list matches from that node.
3. Use another stack to simulate DFS matching.

---

## Algorithm

### Tree Traversal

1. Push root into stack.
2. While stack not empty:
   - Pop node
   - Check if list matches from this node
   - Push children to stack

---

### Path Matching

Maintain a stack storing pairs:

```
(treeNode, listNode)
```

Compare values and continue exploring children.

---

## Implementation

```java
class Solution {

    public boolean isSubPath(ListNode head, TreeNode root) {
        if (root == null) return false;

        Stack<TreeNode> nodes = new Stack<>();
        nodes.push(root);

        while (!nodes.isEmpty()) {
            TreeNode node = nodes.pop();

            if (isMatch(node, head)) {
                return true;
            }

            if (node.left != null) nodes.push(node.left);
            if (node.right != null) nodes.push(node.right);
        }

        return false;
    }

    private boolean isMatch(TreeNode node, ListNode lst) {
        Stack<Map.Entry<TreeNode, ListNode>> s = new Stack<>();
        s.push(new HashMap.SimpleEntry<>(node, lst));

        while (!s.isEmpty()) {
            Map.Entry<TreeNode, ListNode> entry = s.pop();
            TreeNode currentNode = entry.getKey();
            ListNode currentList = entry.getValue();

            while (currentNode != null && currentList != null) {

                if (currentNode.val != currentList.val) break;

                currentList = currentList.next;

                if (currentList != null) {

                    if (currentNode.left != null)
                        s.push(new HashMap.SimpleEntry<>(currentNode.left, currentList));

                    if (currentNode.right != null)
                        s.push(new HashMap.SimpleEntry<>(currentNode.right, currentList));

                    break;
                }
            }

            if (currentList == null) return true;
        }

        return false;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(n × m)
```

Same reasoning as recursive solution.

---

### Space Complexity

```
O(n)
```

The stack may contain many tree nodes.

---

# Approach 3: KMP Algorithm (Advanced)

## Intuition

The previous approaches repeatedly compare the linked list against many tree paths.

This leads to **redundant comparisons**.

To optimize this we adapt the **Knuth-Morris-Pratt (KMP)** string matching algorithm.

Instead of restarting comparisons, we **reuse previously matched prefix information**.

---

# KMP Concepts

KMP uses a **prefix table** (failure function).

It stores:

```
longest prefix = suffix
```

This allows skipping unnecessary comparisons.

Example pattern:

```
ABABCABAB
```

If mismatch occurs, KMP jumps to the next possible match using prefix table.

---

# Applying KMP to This Problem

Treat:

```
linked list → pattern
tree path → text
```

Steps:

1. Convert linked list to pattern array.
2. Build prefix table.
3. Perform DFS on tree.
4. Use prefix table to handle mismatches efficiently.

---

## Algorithm

### Step 1: Build Pattern

Extract linked list values into array.

### Step 2: Build Prefix Table

Compute prefix function like normal KMP.

### Step 3: DFS Search

During DFS:

1. Compare node value with pattern.
2. If mismatch → fallback using prefix table.
3. If full pattern matched → return true.

---

## Implementation

```java
class Solution {

    public boolean isSubPath(ListNode head, TreeNode root) {

        List<Integer> pattern = new ArrayList<>();
        List<Integer> prefixTable = new ArrayList<>();

        pattern.add(head.val);
        prefixTable.add(0);

        int patternIndex = 0;
        head = head.next;

        while (head != null) {

            while (patternIndex > 0 && head.val != pattern.get(patternIndex)) {
                patternIndex = prefixTable.get(patternIndex - 1);
            }

            patternIndex += head.val == pattern.get(patternIndex) ? 1 : 0;

            pattern.add(head.val);
            prefixTable.add(patternIndex);

            head = head.next;
        }

        return searchInTree(root, 0, pattern, prefixTable);
    }

    private boolean searchInTree(TreeNode node, int patternIndex,
                                 List<Integer> pattern, List<Integer> prefixTable) {

        if (node == null) return false;

        while (patternIndex > 0 && node.val != pattern.get(patternIndex)) {
            patternIndex = prefixTable.get(patternIndex - 1);
        }

        patternIndex += node.val == pattern.get(patternIndex) ? 1 : 0;

        if (patternIndex == pattern.size()) return true;

        return searchInTree(node.left, patternIndex, pattern, prefixTable) ||
               searchInTree(node.right, patternIndex, pattern, prefixTable);
    }
}
```

---

# Complexity Analysis

Let:

```
n = number of tree nodes
m = length of linked list
```

### Time Complexity

```
O(n × m)
```

Building prefix table:

```
O(m)
```

Tree traversal dominates complexity.

---

### Space Complexity

```
O(n + m)
```

- Prefix table → O(m)
- Recursion stack → O(n)
