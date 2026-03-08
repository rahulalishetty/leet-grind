# 427. Construct Quad Tree

Given an `n x n` matrix `grid` of 0's and 1's, we want to represent `grid` with a Quad-Tree.

Return the root of the Quad-Tree representing `grid`.

## Quad-Tree Definition

A Quad-Tree is a tree data structure where each internal node has exactly four children. Each node has two attributes:

- **val**: `True` if the node represents a grid of 1's, or `False` if the node represents a grid of 0's. When `isLeaf` is `False`, `val` can be assigned arbitrarily.
- **isLeaf**: `True` if the node is a leaf node, or `False` if the node has four children.

```java
class Node {
  public boolean val;
  public boolean isLeaf;
  public Node topLeft;
  public Node topRight;
  public Node bottomLeft;
  public Node bottomRight;
}
```

## Construction Steps

1. If the current grid has the same value (all 1's or all 0's):
   - Set `isLeaf` to `True`.
   - Set `val` to the value of the grid.
   - Set the four children to `null`.
   - Stop.
2. If the current grid has different values:
   - Set `isLeaf` to `False`.
   - Set `val` to any value.
   - Divide the current grid into four sub-grids.
   - Recurse for each child with the corresponding sub-grid.

For more details about Quad-Trees, refer to the [Wikipedia article](https://en.wikipedia.org/wiki/Quadtree).

## Quad-Tree Format

This section explains the output format of the Quad-Tree. The output is serialized using level-order traversal, where `null` signifies a path terminator (no node exists below).

Each node is represented as a list `[isLeaf, val]`:

- `1` represents `True`.
- `0` represents `False`.

### Example 1

**Input:**
`grid = [[0,1],[1,0]]`

**Output:**
`[[0,1],[1,0],[1,1],[1,1],[1,0]]`

**Explanation:**
The Quad-Tree representation is shown below. Here, `0` represents `False` and `1` represents `True`.

### Example 2

**Input:**

```
grid = [
  [1,1,1,1,0,0,0,0],
  [1,1,1,1,0,0,0,0],
  [1,1,1,1,1,1,1,1],
  [1,1,1,1,1,1,1,1],
  [1,1,1,1,0,0,0,0],
  [1,1,1,1,0,0,0,0],
  [1,1,1,1,0,0,0,0],
  [1,1,1,1,0,0,0,0]
]
```

**Output:**
`[[0,1],[1,1],[0,1],[1,1],[1,0],null,null,null,null,[1,0],[1,0],[1,1],[1,1]]`

![alt text](image.png)

**Explanation:**
The grid is divided into sub-grids:

- `topLeft`, `bottomLeft`, and `bottomRight` have the same value.
- `topRight` has different values, so it is further divided into sub-grids.

## Constraints

- `n == grid.length == grid[i].length`
- `n == 2^x` where `0 <= x <= 6`
- `grid[i][j]` is either `0` or `1`.
