# 2196. Create Binary Tree From Descriptions — Detailed Approaches

## Overview

We are given a **2D integer array `descriptions`**, where each element is a triplet:

```
[parent_i, child_i, isLeft_i]
```

Each triplet provides the following information:

- **parent_i** → value of the parent node
- **child_i** → value of the child node
- **isLeft_i** → position of the child relative to the parent

```
isLeft_i = 1 → left child
isLeft_i = 0 → right child
```

The goal is to **construct the binary tree described by the relationships** and **return the root node**.

Key properties:

- Every node value is **unique**
- The tree is guaranteed to be **valid**
- A node can have **at most two children**
- Exactly **one node will never appear as a child** — that node is the **root**

---

# Approach 1 — Convert to Graph + Breadth‑First Search

## Intuition

The descriptions provide **unordered relationships**.
To organize them efficiently:

1. Build a **parent → children mapping**
2. Track **all parents** and **all children**
3. The **root** is the node that **appears as parent but never as child**
4. Construct the tree using **BFS**

---

## Algorithm

### Step 1 — Initialize Data Structures

- `children` set → stores all child nodes
- `parents` set → stores all nodes
- `parentToChildren` map → parent → list of `(child, isLeft)`

### Step 2 — Build Graph

For each description:

- Add parent and child to `parents`
- Add child to `children`
- Store `(child, isLeft)` inside `parentToChildren[parent]`

### Step 3 — Identify Root

```
root = node in parents but not in children
```

### Step 4 — Construct Tree Using BFS

- Initialize queue with root
- For each parent node:
  - create child nodes
  - attach left/right based on `isLeft`
  - push children into queue

---

## Java Implementation

```java
class Solution {

    public TreeNode createBinaryTree(int[][] descriptions) {

        Set<Integer> children = new HashSet<>();
        Set<Integer> parents = new HashSet<>();
        Map<Integer, List<int[]>> parentToChildren = new HashMap<>();

        for (int[] d : descriptions) {
            int parent = d[0], child = d[1], isLeft = d[2];

            parents.add(parent);
            parents.add(child);
            children.add(child);

            parentToChildren
                .computeIfAbsent(parent, k -> new ArrayList<>())
                .add(new int[]{child, isLeft});
        }

        parents.removeAll(children);
        TreeNode root = new TreeNode(parents.iterator().next());

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {

            TreeNode parent = queue.poll();

            for (int[] childInfo : parentToChildren.getOrDefault(
                parent.val,
                Collections.emptyList()
            )) {

                int childValue = childInfo[0];
                int isLeft = childInfo[1];

                TreeNode child = new TreeNode(childValue);

                if (isLeft == 1)
                    parent.left = child;
                else
                    parent.right = child;

                queue.offer(child);
            }
        }

        return root;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(n)
```

- Build map → O(n)
- Find root → O(n)
- BFS construction → O(n)

Total → **O(n)**

### Space Complexity

```
O(n)
```

Data structures used:

- map
- sets
- BFS queue

---

# Approach 2 — Convert to Graph + Depth‑First Search

## Intuition

Same data organization as BFS approach, but the **tree is constructed recursively**.

Advantages:

- Natural representation of tree structure
- Simpler logic for deep trees

---

## Algorithm

### Step 1 — Build Mapping

Create:

```
parentToChildren map
allNodes set
children set
```

### Step 2 — Find Root

```
root = node in allNodes but not in children
```

### Step 3 — DFS Construction

Recursive function:

```
dfs(val):
    create node
    attach children recursively
    return node
```

---

## Java Implementation

```java
class Solution {

    public TreeNode createBinaryTree(int[][] descriptions) {

        Map<Integer, List<int[]>> parentToChildren = new HashMap<>();
        Set<Integer> allNodes = new HashSet<>();
        Set<Integer> children = new HashSet<>();

        for (int[] desc : descriptions) {

            int parent = desc[0];
            int child = desc[1];
            int isLeft = desc[2];

            parentToChildren
                .computeIfAbsent(parent, k -> new ArrayList<>())
                .add(new int[]{child, isLeft});

            allNodes.add(parent);
            allNodes.add(child);
            children.add(child);
        }

        int rootVal = 0;

        for (int node : allNodes) {
            if (!children.contains(node)) {
                rootVal = node;
                break;
            }
        }

        return dfs(parentToChildren, rootVal);
    }

    private TreeNode dfs(Map<Integer, List<int[]>> map, int val) {

        TreeNode node = new TreeNode(val);

        if (map.containsKey(val)) {

            for (int[] childInfo : map.get(val)) {

                int child = childInfo[0];
                int isLeft = childInfo[1];

                if (isLeft == 1)
                    node.left = dfs(map, child);
                else
                    node.right = dfs(map, child);
            }
        }

        return node;
    }
}
```

---

## Complexity

### Time Complexity

```
O(n)
```

- Build graph → O(n)
- DFS construction → O(n)

### Space Complexity

```
O(n)
```

Used by:

- map
- sets
- recursion stack

---

# Approach 3 — Direct Construction Using Node Map

## Intuition

The previous solutions used:

- graph maps
- parent/child tracking
- BFS/DFS

We can simplify by **directly mapping values → TreeNode objects**.

Benefits:

- constant-time node access
- fewer data structures
- simpler implementation

---

## Algorithm

### Step 1 — Maintain Node Map

```
Map<Integer, TreeNode> nodeMap
```

### Step 2 — Process Descriptions

For each description:

- create parent node if not exists
- create child node if not exists
- attach child left/right
- mark child in `children` set

### Step 3 — Identify Root

The root is the node **not present in children set**.

---

## Java Implementation

```java
class Solution {

    public TreeNode createBinaryTree(int[][] descriptions) {

        Map<Integer, TreeNode> nodeMap = new HashMap<>();
        Set<Integer> children = new HashSet<>();

        for (int[] description : descriptions) {

            int parentValue = description[0];
            int childValue = description[1];
            boolean isLeft = description[2] == 1;

            nodeMap.putIfAbsent(parentValue, new TreeNode(parentValue));
            nodeMap.putIfAbsent(childValue, new TreeNode(childValue));

            if (isLeft)
                nodeMap.get(parentValue).left = nodeMap.get(childValue);
            else
                nodeMap.get(parentValue).right = nodeMap.get(childValue);

            children.add(childValue);
        }

        for (TreeNode node : nodeMap.values()) {

            if (!children.contains(node.val))
                return node;
        }

        return null;
    }
}
```

---

# Complexity Analysis

## Time Complexity

```
O(n)
```

- iterate descriptions once
- constant time node lookup

## Space Complexity

```
O(n)
```

Used by:

- nodeMap
- children set
- TreeNode objects

---

# Final Insight

All three approaches run in:

```
Time  : O(n)
Space : O(n)
```

But the **direct node-map approach** is typically preferred because:

- simpler implementation
- fewer data structures
- avoids explicit graph construction
