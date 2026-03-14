# Maximum Average Subtree – Postorder Traversal Approach

## Intuition

To compute the **average value of a subtree**, we need two quantities:

- **ValueSum(node)** → Sum of all node values in the subtree
- **NodeCount(node)** → Number of nodes in the subtree

The average is therefore:

```
Average(node) = ValueSum(node) / NodeCount(node)
```

To compute these efficiently, we observe that both values can be derived from the child nodes.

### Recurrence Relations

```
ValueSum(node) = ValueSum(node.left) + ValueSum(node.right) + node.val
NodeCount(node) = NodeCount(node.left) + NodeCount(node.right) + 1
```

For a **leaf node**:

```
ValueSum(leaf) = leaf.val
NodeCount(leaf) = 1
```

Because the parent depends on the values of its children, we must process child nodes **before** the parent.

This traversal order is called **Postorder Traversal**:

```
Left → Right → Node
```

---

# Algorithm

1. Traverse the tree using **postorder traversal**.
2. For each node compute:
   - number of nodes in the subtree
   - sum of values in the subtree
3. Compute the **average** for the subtree.
4. Track the **maximum average found so far**.
5. Return the maximum average.

---

# Implementation

```java
class Solution {

    // State object stores information about a subtree
    class State {

        // number of nodes in subtree
        int nodeCount;

        // sum of node values
        int valueSum;

        // maximum average in subtree
        double maxAverage;

        State(int nodes, int sum, double maxAverage) {
            this.nodeCount = nodes;
            this.valueSum = sum;
            this.maxAverage = maxAverage;
        }
    }

    public double maximumAverageSubtree(TreeNode root) {
        return maxAverage(root).maxAverage;
    }

    State maxAverage(TreeNode root) {

        if (root == null) {
            return new State(0, 0, 0);
        }

        // compute results for left and right subtree
        State left = maxAverage(root.left);
        State right = maxAverage(root.right);

        int nodeCount = left.nodeCount + right.nodeCount + 1;
        int sum = left.valueSum + right.valueSum + root.val;

        double maxAverage = Math.max(
                (1.0 * sum) / nodeCount,
                Math.max(left.maxAverage, right.maxAverage)
        );

        return new State(nodeCount, sum, maxAverage);
    }
}
```

---

# Complexity Analysis

## Time Complexity

```
O(N)
```

Each node is visited exactly once during the postorder traversal.

---

## Space Complexity

```
O(N)
```

This comes from:

- recursion stack (in worst case skewed tree)
- state objects created for each node
