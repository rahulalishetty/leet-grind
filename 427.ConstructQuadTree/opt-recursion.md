# Quad Tree Construction — Approach 2 (Optimized Recursion)

This summarizes the **optimized recursive** approach for constructing a quad tree from an `N x N` binary grid (values `0/1`).

Compared to the basic recursion approach, this version avoids repeatedly scanning sub-squares to check uniformity.

---

## 1. Core Idea / Intuition

### What was inefficient earlier?

In the straightforward recursion approach (Approach 1), for every sub-square we:

1. **Scan all cells** in the current region to check if they are uniform.
2. If not uniform, split into 4 quadrants and recurse.

Problem:

- Cells are scanned **multiple times** across recursion levels.
- Worst-case time becomes **O(N² log N)** because each level collectively scans **N²** cells, and there are **log N** levels.

### Optimization insight

Instead of scanning to decide whether to split:

- **Always split first** (except when size = 1),
- Build the 4 quadrant subtrees,
- Then decide whether we can **merge** them into a single leaf node.

We no longer re-scan large regions:

- Cell values are directly read **only when length == 1** (base case).
- Merging decision is made using only the returned nodes’ properties:
  - Are they all leaves?
  - Do they all have the same `val`?

This removes redundant work and brings runtime down to **O(N²)**.

---

## 2. Recurrence Structure

Represent a sub-square by:

- `(x1, y1)` → top-left corner
- `length` → side length

### Base case

If `length == 1`:

- Return a **leaf node** with value = `grid[x1][y1]`

### Recursive case

Split into quadrants (`half = length / 2`):

1. topLeft: `(x1, y1)`
2. topRight: `(x1, y1 + half)`
3. bottomLeft: `(x1 + half, y1)`
4. bottomRight: `(x1 + half, y1 + half)`

After recursion returns 4 nodes:

- If all 4 are **leaf** and all have **same value**, merge into one leaf.
- Else return internal node with those 4 children.

---

## 3. Algorithm (Step-by-step)

Given `solve(grid, x1, y1, length)`:

1. If `length == 1`:
   - return `Leaf(val = grid[x1][y1])`
2. Compute `half = length / 2`
3. Recursively build:
   - `topLeft`, `topRight`, `bottomLeft`, `bottomRight`
4. If all 4 nodes are leaf **and** have equal values:
   - return merged leaf node
5. Else:
   - return internal node with 4 children

---

## 4. Code Example (Java)

```java
/*
// Typical LeetCode Node definition.
class Node {
    public boolean val;
    public boolean isLeaf;
    public Node topLeft;
    public Node topRight;
    public Node bottomLeft;
    public Node bottomRight;

    public Node() {}

    public Node(boolean val, boolean isLeaf) {
        this.val = val;
        this.isLeaf = isLeaf;
    }

    public Node(boolean val, boolean isLeaf, Node topLeft, Node topRight,
                Node bottomLeft, Node bottomRight) {
        this.val = val;
        this.isLeaf = isLeaf;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }
}
*/

class Solution {

    private Node solve(int[][] grid, int x1, int y1, int length) {
        // Base case: 1x1 region is always a leaf.
        if (length == 1) {
            return new Node(grid[x1][y1] == 1, true);
        }

        int half = length / 2;

        // Build the four subtrees first.
        Node topLeft = solve(grid, x1, y1, half);
        Node topRight = solve(grid, x1, y1 + half, half);
        Node bottomLeft = solve(grid, x1 + half, y1, half);
        Node bottomRight = solve(grid, x1 + half, y1 + half, half);

        // Merge condition: all leaf + all same value
        if (topLeft.isLeaf && topRight.isLeaf && bottomLeft.isLeaf && bottomRight.isLeaf
                && topLeft.val == topRight.val
                && topRight.val == bottomLeft.val
                && bottomLeft.val == bottomRight.val) {

            return new Node(topLeft.val, true);
        }

        // Otherwise return internal node
        return new Node(false, false, topLeft, topRight, bottomLeft, bottomRight);
    }

    public Node construct(int[][] grid) {
        return solve(grid, 0, 0, grid.length);
    }
}
```

---

## 5. Complexity Analysis

Let `N` be the matrix side length.

### Time Complexity: **O(N²)**

Reasoning:

- Every cell becomes a base case exactly once (`length == 1`).
- Every internal node does only constant work:
  - 4 recursive calls + constant-time merge check

Number of leaves = `N²`
Total nodes in a full quad tree = O(N²)

So overall: **O(N²)**

### Space Complexity: **O(log N)**

Ignoring output tree size:

- Maximum recursion depth is `log N` (side halves each recursion level)

So call stack: **O(log N)**

---

## 6. Practical Notes

- This approach is typically the preferred recursive solution for interview settings.
- The optimization is achieved by moving the “uniformity decision” upward:
  - Instead of _checking before splitting_,
  - we _split, build children, then merge if possible_.
- If you need even more optimizations (e.g., speed in practice), you can explore:
  - iterative construction,
  - caching/memoization of repeated regions (rare),
  - or using bit-packing / prefix sums for alternative checks.
