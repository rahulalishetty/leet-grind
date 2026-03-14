# Binary Tree Longest Consecutive Sequence II — Approaches

## Approach 1: Brute Force (Time Limit Exceeded)

### Idea

Since a **tree has no cycles**, there exists **exactly one unique path between any two nodes**.

Therefore:

- Total possible paths in a tree with **N nodes** = number of node pairs

```
N choose 2  ≈  O(N²)
```

### Strategy

1. Generate the path between **every pair of nodes**.
2. For each path:
   - Check whether the sequence is **strictly consecutive**.
   - The difference between adjacent nodes must be **±1**.
3. Track the **maximum length path** that satisfies the condition.

### Complexity Analysis

**Time Complexity**

```
O(N³)
```

Reason:

- Total paths = `O(N²)`
- Checking each path takes `O(N)`

So:

```
O(N² × N) = O(N³)
```

**Space Complexity**

```
O(N³)
```

Reason:

- Potentially storing `N²` paths
- Each path may contain up to `N` nodes

---

# Approach 2: Single Traversal (Optimal)

Instead of examining every pair of nodes, we can compute the result **during a single tree traversal**.

---

## Key Idea

For every node we compute **two values**:

| Variable | Meaning                                                    |
| -------- | ---------------------------------------------------------- |
| `inr`    | Length of longest **increasing consecutive path** downward |
| `dcr`    | Length of longest **decreasing consecutive path** downward |

Both include the **current node itself**.

Initially:

```
inr = 1
dcr = 1
```

because a single node itself forms a path of length **1**.

---

## Recursive Function

Function:

```
longestPath(node)
```

Returns:

```
[inr, dcr]
```

for the current node.

---

## Processing Logic

### Step 1: Process Left Child

Call:

```
longestPath(root.left)
```

Then compare values.

Case 1:

```
root.val == left.val + 1
```

This forms a **decreasing sequence**.

```
dcr = left.dcr + 1
```

Case 2:

```
root.val == left.val - 1
```

This forms an **increasing sequence**.

```
inr = left.inr + 1
```

---

### Step 2: Process Right Child

Same process as left child.

However, we choose the **maximum** of the paths from left and right.

Example:

```
dcr = max(dcr, right.dcr + 1)
inr = max(inr, right.inr + 1)
```

---

## Updating Global Maximum

The longest path **may pass through the current node**.

Example:

```
increasing path from left + current node + decreasing path from right
```

So the total path length becomes:

```
inr + dcr - 1
```

We subtract **1** because the current node is counted in both.

```
maxval = max(maxval, inr + dcr - 1)
```

---

## Implementation

```java
public class Solution {

    int maxval = 0;

    public int longestConsecutive(TreeNode root) {
        longestPath(root);
        return maxval;
    }

    public int[] longestPath(TreeNode root) {

        if (root == null) {
            return new int[]{0,0};
        }

        int inr = 1, dcr = 1;

        if (root.left != null) {

            int[] left = longestPath(root.left);

            if (root.val == root.left.val + 1) {
                dcr = left[1] + 1;
            }
            else if (root.val == root.left.val - 1) {
                inr = left[0] + 1;
            }
        }

        if (root.right != null) {

            int[] right = longestPath(root.right);

            if (root.val == root.right.val + 1) {
                dcr = Math.max(dcr, right[1] + 1);
            }
            else if (root.val == root.right.val - 1) {
                inr = Math.max(inr, right[0] + 1);
            }
        }

        maxval = Math.max(maxval, dcr + inr - 1);

        return new int[]{inr, dcr};
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(N)
```

Each node in the tree is visited **once**.

---

### Space Complexity

```
O(N)
```

Worst case recursion depth = **tree height**.

For a skewed tree:

```
O(N)
```
