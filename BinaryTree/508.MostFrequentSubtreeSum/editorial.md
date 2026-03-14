# 508. Most Frequent Subtree Sum — Detailed Approaches

## Overview

In this problem, we must return the array of subtree sums that occur with the **maximum frequency**.

A **subtree sum** is defined as the sum of all node values in a subtree, including the root of that subtree.

We will analyze the problem starting from a **naive approach** and gradually move toward an **optimized solution**.

---

# Approach 1: Pre‑Order Traversal

## Intuition

We need to compute the sum of every subtree.

One way to do this is:

1. Traverse the tree using **preorder traversal**.
2. For each node, calculate the sum of the subtree rooted at that node.

In preorder traversal we process nodes in the order:

```
Root → Left → Right
```

To compute the subtree sum for a node:

```
subtree_sum = node.val + left_subtree_sum + right_subtree_sum
```

This can be naturally implemented using recursion.

### Base Case

If the tree is empty:

```
sum = 0
```

Because there are no nodes.

---

## Recursive Function

```
int findTreeSum(TreeNode root) {
    if (root == null) {
        return 0
    }

    return root.val + findTreeSum(root.left) + findTreeSum(root.right)
}
```

---

## Algorithm

1. Initialize:
   - `sumFreq` → hashmap storing frequency of each subtree sum.
   - `maxFreq` → maximum frequency seen.
   - `maxFreqSums` → result array.

2. Traverse the tree using **preorder traversal**.

3. For each node:
   - Compute subtree sum using `findTreeSum`.
   - Update frequency in hashmap.
   - Update `maxFreq` if necessary.

4. After traversal:
   - Iterate through hashmap.
   - Collect sums whose frequency equals `maxFreq`.

5. Return the result.

---

## Implementation

```java
class Solution {
    private HashMap<Integer, Integer> sumFreq = new HashMap<>();
    private Integer maxFreq = 0;

    private int findTreeSum(TreeNode root) {
        if (root == null) {
            return 0;
        }

        return root.val + findTreeSum(root.left) + findTreeSum(root.right);
    }

    private void preOrderTraversal(TreeNode root) {
        if (root == null) {
            return;
        }

        int currSum = findTreeSum(root);

        sumFreq.put(currSum, sumFreq.getOrDefault(currSum, 0) + 1);
        maxFreq = Math.max(maxFreq, sumFreq.get(currSum));

        preOrderTraversal(root.left);
        preOrderTraversal(root.right);
    }

    public int[] findFrequentTreeSum(TreeNode root) {

        preOrderTraversal(root);

        List<Integer> ansList = new ArrayList<>();

        for (Map.Entry<Integer,Integer> entry : sumFreq.entrySet()) {
            if (entry.getValue() == maxFreq) {
                ansList.add(entry.getKey());
            }
        }

        int[] result = new int[ansList.size()];

        for (int i = 0; i < ansList.size(); i++) {
            result[i] = ansList.get(i);
        }

        return result;
    }
}
```

---

## Complexity Analysis

Let **N** be the number of nodes.

### Time Complexity

```
O(N²)
```

Reason:

- For each node we recompute the subtree sum.
- Computing subtree sum takes `O(N)` in worst case.
- Done for N nodes → `O(N²)`.

### Space Complexity

```
O(N)
```

Reasons:

- HashMap stores up to N sums.
- Recursion stack up to N for skewed tree.

---

# Approach 2: Post‑Order Traversal (Optimized)

## Intuition

The problem with preorder solution is **repeated computation**.

Example:

A subtree might be part of many larger subtrees.

That means we recompute the same subtree sum multiple times.

To avoid this, compute subtree sums **bottom‑up**.

Traversal order:

```
Left → Right → Node
```

This is **postorder traversal**.

If we already know:

```
left_subtree_sum
right_subtree_sum
```

Then:

```
current_sum = node.val + left_sum + right_sum
```

This eliminates repeated work.

---

## Algorithm

1. Initialize:
   - `sumFreq`
   - `maxFreq`

2. Perform **postorder traversal**.

3. At each node:
   - Compute left subtree sum.
   - Compute right subtree sum.
   - Compute current sum.
   - Update frequency hashmap.
   - Update `maxFreq`.

4. After traversal:
   - Iterate hashmap to find sums with max frequency.

5. Return results.

---

## Implementation

```java
class Solution {

    private HashMap<Integer,Integer> sumFreq = new HashMap<>();
    private int maxFreq = 0;

    private int subtreeSum(TreeNode root) {

        if (root == null) {
            return 0;
        }

        int left = subtreeSum(root.left);
        int right = subtreeSum(root.right);

        int currSum = root.val + left + right;

        sumFreq.put(currSum, sumFreq.getOrDefault(currSum,0)+1);

        maxFreq = Math.max(maxFreq, sumFreq.get(currSum));

        return currSum;
    }

    public int[] findFrequentTreeSum(TreeNode root) {

        subtreeSum(root);

        List<Integer> result = new ArrayList<>();

        for (Map.Entry<Integer,Integer> entry : sumFreq.entrySet()) {

            if (entry.getValue() == maxFreq) {
                result.add(entry.getKey());
            }
        }

        int[] ans = new int[result.size()];

        for (int i = 0; i < result.size(); i++) {
            ans[i] = result.get(i);
        }

        return ans;
    }
}
```

---

## Complexity Analysis

Let **N** be the number of nodes.

### Time Complexity

```
O(N)
```

Each node is visited **exactly once**.

Subtree sum calculation becomes **constant time** using results from children.

### Space Complexity

```
O(N)
```

Used for:

- HashMap storing subtree sums.
- Recursion stack (worst case skewed tree).

---

# Key Insight

| Approach  | Traversal | Time  | Idea                         |
| --------- | --------- | ----- | ---------------------------- |
| Preorder  | Top‑Down  | O(N²) | Recomputes subtree sums      |
| Postorder | Bottom‑Up | O(N)  | Reuses computed subtree sums |

**Postorder traversal eliminates repeated work, making the solution optimal.**
