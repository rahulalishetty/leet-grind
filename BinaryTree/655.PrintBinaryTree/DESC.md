# 655. Print Binary Tree

**Difficulty:** Medium

---

## Problem Statement

Given the `root` of a binary tree, construct a **0-indexed** `m x n` string matrix `res` that represents a formatted layout of the tree.

---

## Rules

The formatted layout matrix is constructed as follows:

1. The **height** of the tree is `height`, and the number of rows `m` equals `height + 1`.
2. The number of columns `n` equals `2^(height+1) - 1`.
3. Place the **root node** in the middle of the top row — formally at `res[0][(n-1)/2]`.
4. For each node placed at `res[r][c]`, place its:
   - **Left child** at `res[r+1][c - 2^(height-r-1)]`
   - **Right child** at `res[r+1][c + 2^(height-r-1)]`
5. Continue until all nodes are placed.
6. Any **empty cells** should contain `""`.

Return the constructed matrix `res`.

---

## Examples

### Example 1

**Input:**

```
root = [1, 2]
```

**Tree:**

```
  1
 /
2
```

**Output:**

```
[
  ["", "1", ""],
  ["2", "",  ""]
]
```

---

### Example 2

**Input:**

```
root = [1, 2, 3, null, 4]
```

**Tree:**

```
    1
   / \
  2   3
   \
    4
```

**Output:**

```
[
  ["", "", "", "1", "", "", ""],
  ["", "2", "", "", "", "3", ""],
  ["", "", "4", "", "", "", ""]
]
```

---

## Constraints

- The number of nodes in the tree is in the range `[1, 2^10]`.
- `-99 <= Node.val <= 99`
- The depth of the tree will be in the range `[1, 10]`.

---

## Approach

### Key Observations

- Tree height determines both matrix dimensions.
- Each node's column position is derived recursively from its parent's position.
- At row `r`, the offset from parent to child is `2^(height - r - 1)`.

### Algorithm

1. **Compute height** via DFS/BFS.
2. **Initialize matrix** of size `(height+1) x (2^(height+1) - 1)` filled with `""`.
3. **Fill matrix** recursively: place node value, recurse left with `c - offset`, recurse right with `c + offset`, halving offset each level.

### Implementation (Python)

```python
from typing import Optional, List

class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def printTree(self, root: Optional[TreeNode]) -> List[List[str]]:
        def get_height(node):
            if not node:
                return -1
            return 1 + max(get_height(node.left), get_height(node.right))

        height = get_height(root)
        cols = (1 << (height + 1)) - 1          # 2^(height+1) - 1
        res = [[""] * cols for _ in range(height + 1)]

        def fill(node, r, c):
            if not node:
                return
            res[r][c] = str(node.val)
            offset = 1 << (height - r - 1)      # 2^(height - r - 1)
            fill(node.left,  r + 1, c - offset)
            fill(node.right, r + 1, c + offset)

        fill(root, 0, (cols - 1) // 2)
        return res
```

### Complexity

|           | Value                                               |
| --------- | --------------------------------------------------- |
| **Time**  | `O(m × n)` — filling every cell in the matrix       |
| **Space** | `O(m × n)` — output matrix + `O(h)` recursion stack |

where `m = height + 1` and `n = 2^(height+1) - 1`.
