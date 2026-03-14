# 2096. Step-By-Step Directions From a Binary Tree Node to Another — Approaches

## Overview

We must determine the **shortest sequence of directions** to move from a **start node `s`** to a **destination node `t`** in a binary tree.

Allowed moves:

| Direction | Meaning             |
| --------- | ------------------- |
| `L`       | move to left child  |
| `R`       | move to right child |
| `U`       | move to parent      |

The resulting string represents the **shortest path between the nodes**.

---

# Approach 1 — BFS + DFS

## Intuition

Binary trees normally allow traversal **only downward** (to children).
But this problem also requires **moving upward to the parent**.

Therefore we:

1. Build a **parent map** (child → parent)
2. Treat the tree like an **undirected graph**
3. Use **BFS from the start node** to guarantee the **shortest path**
4. Track the path using a **pathTracker map**
5. Once the destination is found, **backtrack** to build the path

---

## Key Steps

1. Locate the **start node**
2. Build **parentMap**
3. Perform **BFS**
4. Store movement directions
5. Backtrack once destination is reached
6. Reverse the path

---

## Java Implementation

```java
class Solution {

    public String getDirections(TreeNode root, int startValue, int destValue) {

        Map<Integer, TreeNode> parentMap = new HashMap<>();

        TreeNode startNode = findStartNode(root, startValue);

        populateParentMap(root, parentMap);

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(startNode);

        Set<TreeNode> visitedNodes = new HashSet<>();

        Map<TreeNode, Pair<TreeNode,String>> pathTracker = new HashMap<>();

        visitedNodes.add(startNode);

        while(!queue.isEmpty()){

            TreeNode currentNode = queue.poll();

            if(currentNode.val == destValue){
                return backtrackPath(currentNode, pathTracker);
            }

            if(parentMap.containsKey(currentNode.val)){
                TreeNode parentNode = parentMap.get(currentNode.val);

                if(!visitedNodes.contains(parentNode)){
                    queue.add(parentNode);
                    pathTracker.put(parentNode,new Pair(currentNode,"U"));
                    visitedNodes.add(parentNode);
                }
            }

            if(currentNode.left != null && !visitedNodes.contains(currentNode.left)){
                queue.add(currentNode.left);
                pathTracker.put(currentNode.left,new Pair(currentNode,"L"));
                visitedNodes.add(currentNode.left);
            }

            if(currentNode.right != null && !visitedNodes.contains(currentNode.right)){
                queue.add(currentNode.right);
                pathTracker.put(currentNode.right,new Pair(currentNode,"R"));
                visitedNodes.add(currentNode.right);
            }
        }

        return "";
    }
}
```

---

# Complexity

### Time

```
O(n)
```

### Space

```
O(n)
```

Used for:

- parent map
- BFS queue
- visited set
- path tracking

---

# Approach 2 — LCA + DFS

## Intuition

In a tree, the shortest path between two nodes **always passes through their Lowest Common Ancestor (LCA)**.

We split the path into two parts:

```
start → LCA → destination
```

From **start → LCA** we move **upwards (U)**.
From **LCA → destination** we follow **left/right directions**.

---

## Steps

1. Find **LCA**
2. Find path from **LCA → start**
3. Find path from **LCA → destination**
4. Replace path to start with `"U"`
5. Append destination path

---

## Java Implementation

```java
class Solution {

    public String getDirections(TreeNode root, int startValue, int destValue) {

        TreeNode lca = findLowestCommonAncestor(root,startValue,destValue);

        StringBuilder pathToStart = new StringBuilder();
        StringBuilder pathToDest = new StringBuilder();

        findPath(lca,startValue,pathToStart);
        findPath(lca,destValue,pathToDest);

        StringBuilder directions = new StringBuilder();

        directions.append("U".repeat(pathToStart.length()));
        directions.append(pathToDest);

        return directions.toString();
    }
}
```

---

# Complexity

### Time

```
O(n)
```

### Space

```
O(n)
```

Used by recursion and path strings.

---

# Approach 3 — Root Paths (Optimized LCA)

## Intuition

Instead of computing LCA directly, we can:

1. Compute path **root → start**
2. Compute path **root → destination**
3. Remove **common prefix**
4. Replace remaining start path with `"U"`

This implicitly determines the **LCA**.

---

## Steps

```
root → start path
root → destination path
```

Find common prefix.

Remaining:

```
start → LCA = U...U
LCA → destination = remaining dest path
```

---

## Java Implementation

```java
class Solution {

    public String getDirections(TreeNode root, int startValue, int destValue) {

        StringBuilder startPath = new StringBuilder();
        StringBuilder destPath = new StringBuilder();

        findPath(root,startValue,startPath);
        findPath(root,destValue,destPath);

        int common = 0;

        while(common < startPath.length()
        && common < destPath.length()
        && startPath.charAt(common)==destPath.charAt(common)){
            common++;
        }

        StringBuilder directions = new StringBuilder();

        for(int i=common;i<startPath.length();i++){
            directions.append("U");
        }

        for(int i=common;i<destPath.length();i++){
            directions.append(destPath.charAt(i));
        }

        return directions.toString();
    }
}
```

---

# Complexity

### Time

```
O(n)
```

### Space

```
O(n)
```

Used by recursion stack and path storage.

---

# Key Insight

The **most elegant solution** is usually:

```
Root → start path
Root → destination path
Remove common prefix
Convert remaining start path to U
Append remaining destination path
```

This method avoids explicit **graph conversion** and **LCA calculation**, making it the **cleanest implementation**.
