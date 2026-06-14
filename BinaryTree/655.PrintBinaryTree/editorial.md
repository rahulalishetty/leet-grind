# Binary Tree Formatted Layout Matrix

**LeetCode 655 – Print Binary Tree**

Given the root of a binary tree, construct a 0-indexed `m × n` string matrix that represents a formatted layout of the tree.

---

## Rules Recap

| Property                   | Formula                        |
| -------------------------- | ------------------------------ |
| Rows `m`                   | `height + 1`                   |
| Columns `n`                | `2^(height+1) - 1`             |
| Root position              | `res[0][(n-1)/2]`              |
| Left child of `res[r][c]`  | `res[r+1][c - 2^(height-r-1)]` |
| Right child of `res[r][c]` | `res[r+1][c + 2^(height-r-1)]` |

---

## Approach 1 – BFS (Level-Order Traversal)

### Intuition

Process nodes level by level with a queue. Each queue entry carries the node value, its row `r`, and its column `c` in the result matrix. From any `(r, c)` we can derive child positions in O(1) using the offset `2^(height - r - 1)`.

### Steps

1. Compute tree height.
2. Initialise the `m × n` matrix filled with `""`.
3. Push `(root, row=0, col=(n-1)/2)` onto the queue.
4. For each dequeued entry, write `node.val` into the matrix, then push left/right children with updated positions.

### Complexity

|       | Value                                 |
| ----- | ------------------------------------- |
| Time  | O(m × n) — every cell is visited once |
| Space | O(m × n) — result matrix + queue      |

### Code

```java
import java.util.*;

class Solution {
    public List<List<String>> printTree(TreeNode root) {
        // Step 1: find height
        int height = getHeight(root);
        int rows = height + 1;
        int cols = (1 << (height + 1)) - 1;   // 2^(height+1) - 1

        // Step 2: initialise matrix with ""
        String[][] grid = new String[rows][cols];
        for (String[] row : grid) Arrays.fill(row, "");

        // Step 3: BFS — queue stores [node, row, col]
        Queue<Object[]> queue = new LinkedList<>();
        queue.offer(new Object[]{root, 0, (cols - 1) / 2});

        while (!queue.isEmpty()) {
            Object[] entry = queue.poll();
            TreeNode node = (TreeNode) entry[0];
            int r = (int) entry[1];
            int c = (int) entry[2];

            grid[r][c] = String.valueOf(node.val);

            int offset = 1 << (height - r - 1);   // 2^(height - r - 1)

            if (node.left  != null) queue.offer(new Object[]{node.left,  r + 1, c - offset});
            if (node.right != null) queue.offer(new Object[]{node.right, r + 1, c + offset});
        }

        // Step 4: convert to List<List<String>>
        List<List<String>> res = new ArrayList<>();
        for (String[] row : grid) res.add(Arrays.asList(row));
        return res;
    }

    private int getHeight(TreeNode node) {
        if (node == null) return -1;
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }
}
```

---

## Approach 2 – DFS (Recursive Pre-order)

### Intuition

Recurse through the tree. At each recursive call we know the current node's `(row, col)` in the matrix, so we fill it in and then recurse left/right with updated positions. No queue is needed — the call stack acts as the traversal mechanism.

### Steps

1. Compute height and build the empty matrix.
2. Call `dfs(root, row=0, col=(n-1)/2)`.
3. Inside `dfs`: fill `grid[row][col]`, then call `dfs(left, row+1, col - offset)` and `dfs(right, row+1, col + offset)`.

### Complexity

|       | Value                                    |
| ----- | ---------------------------------------- |
| Time  | O(n) for traversal + O(m × n) for matrix |
| Space | O(h) recursion stack where h = height    |

### Code

```java
import java.util.*;

class Solution {
    private String[][] grid;
    private int height;

    public List<List<String>> printTree(TreeNode root) {
        height = getHeight(root);
        int rows = height + 1;
        int cols = (1 << (height + 1)) - 1;

        grid = new String[rows][cols];
        for (String[] row : grid) Arrays.fill(row, "");

        dfs(root, 0, (cols - 1) / 2);

        List<List<String>> res = new ArrayList<>();
        for (String[] row : grid) res.add(Arrays.asList(row));
        return res;
    }

    private void dfs(TreeNode node, int r, int c) {
        if (node == null) return;

        grid[r][c] = String.valueOf(node.val);

        int offset = 1 << (height - r - 1);   // 2^(height - r - 1)
        dfs(node.left,  r + 1, c - offset);
        dfs(node.right, r + 1, c + offset);
    }

    private int getHeight(TreeNode node) {
        if (node == null) return -1;
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }
}
```

---

## Approach 3 – Single-Pass DFS (Height Computed Inline)

### Intuition

Avoid two separate passes (one for height, one to fill). Instead, compute height lazily inside the same DFS. We first infer dimensions during a pre-pass that returns the height, then fill everything in one recursive sweep. This is essentially the same as Approach 2 but structured so the helper is self-contained and readable for interviews.

_This approach is cleaner for code-review contexts because all logic is in one method._

### Code

```java
import java.util.*;

class Solution {
    public List<List<String>> printTree(TreeNode root) {
        int h = height(root);
        int rows = h + 1;
        int cols = (1 << (h + 1)) - 1;

        // Build result as List<List<String>> directly
        List<List<String>> res = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            List<String> row = new ArrayList<>(Collections.nCopies(cols, ""));
            res.add(row);
        }

        fill(res, root, 0, (cols - 1) / 2, h);
        return res;
    }

    private void fill(List<List<String>> res, TreeNode node, int r, int c, int h) {
        if (node == null) return;

        res.get(r).set(c, String.valueOf(node.val));

        int offset = 1 << (h - r - 1);
        fill(res, node.left,  r + 1, c - offset, h);
        fill(res, node.right, r + 1, c + offset, h);
    }

    private int height(TreeNode node) {
        if (node == null) return -1;
        return 1 + Math.max(height(node.left), height(node.right));
    }
}
```

---

## Approach 4 – Iterative DFS with an Explicit Stack

### Intuition

Mirrors Approach 2 but avoids recursion entirely by using an explicit `Deque` as a stack. Useful when the tree is very deep and the JVM's call stack might overflow.

### When to prefer this

- Tree depth can be in the thousands (recursion risks `StackOverflowError`).
- The interviewer asks for an iterative solution.

### Code

```java
import java.util.*;

class Solution {
    public List<List<String>> printTree(TreeNode root) {
        int h = getHeight(root);
        int rows = h + 1;
        int cols = (1 << (h + 1)) - 1;

        String[][] grid = new String[rows][cols];
        for (String[] row : grid) Arrays.fill(row, "");

        // Stack entry: [node, row, col]
        Deque<Object[]> stack = new ArrayDeque<>();
        stack.push(new Object[]{root, 0, (cols - 1) / 2});

        while (!stack.isEmpty()) {
            Object[] entry = stack.pop();
            TreeNode node = (TreeNode) entry[0];
            int r = (int) entry[1];
            int c = (int) entry[2];

            grid[r][c] = String.valueOf(node.val);

            int offset = 1 << (h - r - 1);

            // Push right first so left is processed first (pre-order)
            if (node.right != null) stack.push(new Object[]{node.right, r + 1, c + offset});
            if (node.left  != null) stack.push(new Object[]{node.left,  r + 1, c - offset});
        }

        List<List<String>> res = new ArrayList<>();
        for (String[] row : grid) res.add(Arrays.asList(row));
        return res;
    }

    private int getHeight(TreeNode node) {
        if (node == null) return -1;
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }
}
```

---

## Visual Walkthrough

For the tree:

```
      1
     / \
    2   3
     \
      4
```

**Height = 2**, so:

- Rows `m = 3`
- Cols `n = 2^3 - 1 = 7`

| Step | Node | (r, c) | Offset        |
| ---- | ---- | ------ | ------------- |
| 1    | 1    | (0, 3) | 2^(2-0-1) = 2 |
| 2    | 2    | (1, 1) | 2^(2-1-1) = 1 |
| 3    | 3    | (1, 5) | 2^(2-1-1) = 1 |
| 4    | 4    | (2, 2) | — (leaf)      |

Result matrix:

```
["", "", "", "1", "", "", ""]
["", "2", "", "", "", "3", ""]
["", "", "4", "", "", "", ""]
```

---

## Comparison Table

| Approach            | Traversal   | Recursion | Space (extra) | Best for                      |
| ------------------- | ----------- | --------- | ------------- | ----------------------------- |
| 1 – BFS             | Level-order | No        | O(n) queue    | Conceptual clarity            |
| 2 – DFS recursive   | Pre-order   | Yes       | O(h) stack    | Concise code                  |
| 3 – Single-pass DFS | Pre-order   | Yes       | O(h) stack    | Clean interview answer        |
| 4 – Iterative DFS   | Pre-order   | No        | O(n) stack    | Deep trees, no stack overflow |

---

## TreeNode Definition (for reference)

```java
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val) { this.val = val; }
}
```
