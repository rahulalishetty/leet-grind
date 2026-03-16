# 1373. Maximum Sum BST in Binary Tree

## Problem Restatement

We are given the root of a binary tree.

We need to return the **maximum sum of node values among all subtrees that are also valid BSTs**.

A subtree is a BST if:

- every value in the left subtree is strictly smaller than the root value
- every value in the right subtree is strictly greater than the root value
- both left and right subtrees are themselves BSTs

An important detail is that the answer can be `0` if every BST subtree has a negative sum, because the problem allows choosing the **empty BST**.

---

# Core Insight

This is not a problem about checking whether the **whole tree** is a BST.

Instead, we must consider **every subtree** and ask:

1. Is this subtree a BST?
2. If yes, what is its sum?
3. Track the maximum such sum globally.

That naturally suggests a **bottom-up traversal**.

Why bottom-up?

Because to decide whether a node's subtree is a BST, we need information from:

- its left subtree
- its right subtree

So we should solve both children first, then combine their results at the parent.

This makes **postorder traversal** the key pattern.

---

# What Information Do We Need Per Subtree?

For any node, to determine whether the subtree rooted there is a BST, we need to know from each child subtree:

- whether that child subtree is a BST
- the minimum value in that subtree
- the maximum value in that subtree
- the sum of values in that subtree

Why these?

Suppose current node value is `x`.

Then the subtree rooted at this node is a BST if:

- left subtree is a BST
- right subtree is a BST
- `max(left subtree) < x < min(right subtree)`

If those conditions hold, then:

- the new subtree sum is:
  ```text
  leftSum + rightSum + x
  ```
- the new minimum becomes:
  ```text
  min(leftMin, x)
  ```
- the new maximum becomes:
  ```text
  max(rightMax, x)
  ```

That is the heart of the optimal solution.

---

# Approach 1: Brute Force by Validating Every Subtree

## Intuition

The most direct idea is:

- for every node, treat that node as a subtree root
- check whether its subtree is a BST
- if yes, compute its sum
- keep the maximum

This is conceptually simple, but inefficient.

---

## Why It Is Slow

If for each node we independently:

- validate whether the subtree is a BST
- compute the subtree sum

then we end up revisiting the same nodes many times.

In a skewed tree, this can become quadratic.

Still, it is a good starting point for understanding.

---

## Plan

For each node:

1. Check whether the subtree rooted at this node is a BST.
2. If it is, compute the sum of that subtree.
3. Recurse into left and right children to try other subtree roots.

---

## Java Code

```java
class Solution {
    public int maxSumBST(TreeNode root) {
        int[] ans = new int[1];
        ans[0] = 0;
        traverse(root, ans);
        return ans[0];
    }

    private void traverse(TreeNode node, int[] ans) {
        if (node == null) return;

        if (isBST(node, Long.MIN_VALUE, Long.MAX_VALUE)) {
            ans[0] = Math.max(ans[0], sum(node));
        }

        traverse(node.left, ans);
        traverse(node.right, ans);
    }

    private boolean isBST(TreeNode node, long low, long high) {
        if (node == null) return true;
        if (node.val <= low || node.val >= high) return false;
        return isBST(node.left, low, node.val) && isBST(node.right, node.val, high);
    }

    private int sum(TreeNode node) {
        if (node == null) return 0;
        return node.val + sum(node.left) + sum(node.right);
    }
}
```

---

## Complexity

### Time

Worst case:

```text
O(n^2)
```

Why?

For each node, `isBST` and `sum` may scan a large subtree again.

### Space

```text
O(h)
```

for recursion stack, where `h` is the tree height.

Worst case:

```text
O(n)
```

---

## Verdict

Useful for intuition, but too slow for `n = 4 * 10^4`.

We need to combine all needed subtree information in a single traversal.

---

# Approach 2: Postorder Traversal with Structured State

## Intuition

This is the standard optimal solution.

We process the tree in **postorder**:

```text
left -> right -> node
```

For each node, we return a summary of its subtree:

- whether it is a BST
- its minimum value
- its maximum value
- its sum

Then the parent can decide whether the subtree rooted at itself is a BST.

This avoids repeated work and gives a linear-time solution.

---

## State Definition

For each subtree, return an object containing:

- `isBST`
- `min`
- `max`
- `sum`

For an empty subtree, we define:

- it is a BST
- min = `+infinity`
- max = `-infinity`
- sum = `0`

Why these sentinel values?

Because they make BST checks very clean:

For a node `x`, if left is empty and right is empty, then:

```text
left.max = -infinity < x < +infinity = right.min
```

So leaf nodes automatically work.

---

## Transition

Let `left` and `right` be the results returned from children.

The current subtree is a BST if:

```text
left.isBST && right.isBST && left.max < node.val && node.val < right.min
```

If true:

- current sum:
  ```text
  left.sum + right.sum + node.val
  ```
- current min:
  ```text
  min(node.val, left.min)
  ```
- current max:
  ```text
  max(node.val, right.max)
  ```

Update the global answer with this sum.

If false:

- mark current subtree as not a BST
- exact min/max/sum do not matter anymore for parent validity, but we can return placeholders

---

## Java Code

```java
class Solution {
    private int best = 0;

    private static class Info {
        boolean isBST;
        int min;
        int max;
        int sum;

        Info(boolean isBST, int min, int max, int sum) {
            this.isBST = isBST;
            this.min = min;
            this.max = max;
            this.sum = sum;
        }
    }

    public int maxSumBST(TreeNode root) {
        postorder(root);
        return best;
    }

    private Info postorder(TreeNode node) {
        if (node == null) {
            return new Info(true, Integer.MAX_VALUE, Integer.MIN_VALUE, 0);
        }

        Info left = postorder(node.left);
        Info right = postorder(node.right);

        if (left.isBST && right.isBST && left.max < node.val && node.val < right.min) {
            int sum = left.sum + right.sum + node.val;
            best = Math.max(best, sum);

            int min = Math.min(node.val, left.min);
            int max = Math.max(node.val, right.max);

            return new Info(true, min, max, sum);
        }

        return new Info(false, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
    }
}
```

---

## Detailed Walkthrough

### Base case

```java
if (node == null) {
    return new Info(true, Integer.MAX_VALUE, Integer.MIN_VALUE, 0);
}
```

An empty subtree is considered a valid BST.

Its sentinel min/max values ensure comparisons work naturally.

---

### Process children first

```java
Info left = postorder(node.left);
Info right = postorder(node.right);
```

This is why postorder is essential.

We need child results before deciding the parent result.

---

### Check BST validity at current node

```java
if (left.isBST && right.isBST && left.max < node.val && node.val < right.min)
```

This is the exact BST condition at this node.

---

### If valid, compute subtree information

```java
int sum = left.sum + right.sum + node.val;
best = Math.max(best, sum);

int min = Math.min(node.val, left.min);
int max = Math.max(node.val, right.max);

return new Info(true, min, max, sum);
```

Everything needed for the parent is now packaged.

---

### If invalid

```java
return new Info(false, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
```

Since parent will check `isBST` first, the subtree is already disqualified.

The min/max placeholders help avoid accidental reuse.

---

## Example Walkthrough

Take:

```text
root = [1,4,3,2,4,2,5,null,null,null,null,null,null,4,6]
```

The subtree rooted at `3` is:

```text
      3
     / \
    2   5
       / \
      4   6
```

This is a BST.

Its sum is:

```text
3 + 2 + 5 + 4 + 6 = 20
```

The subtree rooted at `1` is not a BST because its left subtree rooted at `4` violates BST ordering.

So the best answer is `20`.

---

## Complexity

### Time

```text
O(n)
```

Each node is visited exactly once.

### Space

```text
O(h)
```

for recursion stack.

Worst case:

```text
O(n)
```

Balanced tree:

```text
O(log n)
```

---

## Verdict

This is the best and most standard solution.

---

# Approach 3: Postorder Traversal Returning Primitive Array

## Intuition

Approach 2 is already optimal.

This variation keeps the same logic but avoids a custom class and instead returns a fixed-size integer array.

This is common in interview code where people prefer compact state passing.

We can encode:

```text
[isBSTFlag, min, max, sum]
```

For example:

- `isBSTFlag = 1` means valid BST
- `isBSTFlag = 0` means invalid

The logic remains exactly the same.

---

## Java Code

```java
class Solution {
    private int best = 0;

    public int maxSumBST(TreeNode root) {
        dfs(root);
        return best;
    }

    // returns [isBST(1/0), min, max, sum]
    private int[] dfs(TreeNode node) {
        if (node == null) {
            return new int[]{1, Integer.MAX_VALUE, Integer.MIN_VALUE, 0};
        }

        int[] left = dfs(node.left);
        int[] right = dfs(node.right);

        if (left[0] == 1 && right[0] == 1 && left[2] < node.val && node.val < right[1]) {
            int sum = left[3] + right[3] + node.val;
            best = Math.max(best, sum);

            int min = Math.min(left[1], node.val);
            int max = Math.max(right[2], node.val);

            return new int[]{1, min, max, sum};
        }

        return new int[]{0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0};
    }
}
```

---

## Complexity

Same as Approach 2.

### Time

```text
O(n)
```

### Space

```text
O(h)
```

---

## Pros and Cons

### Pros

- compact
- avoids defining a helper class

### Cons

- less readable
- magic indices like `left[2]` and `right[1]` are easier to get wrong

For readability, the helper-class version is usually better.

---

# Approach 4: Iterative Postorder with Explicit Stack

## Intuition

The optimal logic is postorder DP.

It can also be implemented iteratively if recursion depth is a concern.

This is more advanced and more verbose, but useful if you want to avoid stack overflow on deep trees.

We simulate postorder traversal using an explicit stack and a map that stores the computed `Info` for each node.

---

## Java Code

```java
import java.util.*;

class Solution {
    private static class Info {
        boolean isBST;
        int min;
        int max;
        int sum;

        Info(boolean isBST, int min, int max, int sum) {
            this.isBST = isBST;
            this.min = min;
            this.max = max;
            this.sum = sum;
        }
    }

    public int maxSumBST(TreeNode root) {
        if (root == null) return 0;

        int best = 0;
        Map<TreeNode, Info> dp = new HashMap<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode prev = null;
        TreeNode curr = root;

        while (curr != null || !stack.isEmpty()) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }

            TreeNode node = stack.peek();

            if (node.right != null && node.right != prev) {
                curr = node.right;
            } else {
                stack.pop();

                Info left = dp.getOrDefault(node.left,
                        new Info(true, Integer.MAX_VALUE, Integer.MIN_VALUE, 0));
                Info right = dp.getOrDefault(node.right,
                        new Info(true, Integer.MAX_VALUE, Integer.MIN_VALUE, 0));

                Info res;
                if (left.isBST && right.isBST && left.max < node.val && node.val < right.min) {
                    int sum = left.sum + right.sum + node.val;
                    best = Math.max(best, sum);
                    int min = Math.min(node.val, left.min);
                    int max = Math.max(node.val, right.max);
                    res = new Info(true, min, max, sum);
                } else {
                    res = new Info(false, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
                }

                dp.put(node, res);
                prev = node;
            }
        }

        return best;
    }
}
```

---

## Complexity

### Time

```text
O(n)
```

### Space

```text
O(n)
```

Because the map stores state for nodes, unlike recursion-only postorder.

---

## Verdict

This is mainly useful when recursion depth is a practical concern.

For interviews, the recursive postorder solution is almost always preferred unless iterative traversal is specifically requested.

---

# Why the Answer Can Be 0

This often confuses people.

Suppose all node values are negative:

```text
[-4, -2, -5]
```

Any non-empty BST subtree would have a negative sum.

But the problem allows taking the **empty BST**, whose sum is `0`.

Since `0` is larger than every negative sum, the answer is `0`.

That is why we initialize the global best as:

```java
best = 0;
```

and never force inclusion of a negative subtree.

---

# Comparing the Approaches

## Approach 1: Brute Force validate every subtree

- easy to understand
- too slow
- Time: `O(n^2)` worst case

## Approach 2: Postorder with structured state

- optimal and standard
- clear and readable
- Time: `O(n)`
- Space: `O(h)`

## Approach 3: Postorder with primitive array

- same optimal logic
- more compact but less readable
- Time: `O(n)`
- Space: `O(h)`

## Approach 4: Iterative postorder with explicit stack

- avoids recursion
- more verbose
- Time: `O(n)`
- Space: `O(n)`

---

# Which Approach Should You Prefer?

## For interview clarity

Use **Approach 2**.

It is the cleanest explanation:

- solve children first
- gather `isBST`, `min`, `max`, `sum`
- combine them at parent

That is exactly the kind of bottom-up tree DP interviewers expect.

## For compact code

Use **Approach 3**.

## For avoiding recursion depth issues

Use **Approach 4**.

---

# Final Takeaway

The key insight is:

> To know whether a subtree is a BST, and what its sum is, you must first know those answers for its left and right subtrees.

That makes this a classic **postorder tree DP** problem.

For each subtree, return enough information to let the parent decide:

- is it a BST?
- what are its min and max?
- what is its sum?

Once you do that, the whole problem becomes a clean linear-time solution.

---

# Final Complexity Summary

## Brute Force

- Time: `O(n^2)`
- Space: `O(h)`

## Optimal Postorder DP

- Time: `O(n)`
- Space: `O(h)`

## Iterative Postorder

- Time: `O(n)`
- Space: `O(n)`

---

# Recommended Java Solution

```java
class Solution {
    private int best = 0;

    private static class Info {
        boolean isBST;
        int min;
        int max;
        int sum;

        Info(boolean isBST, int min, int max, int sum) {
            this.isBST = isBST;
            this.min = min;
            this.max = max;
            this.sum = sum;
        }
    }

    public int maxSumBST(TreeNode root) {
        postorder(root);
        return best;
    }

    private Info postorder(TreeNode node) {
        if (node == null) {
            return new Info(true, Integer.MAX_VALUE, Integer.MIN_VALUE, 0);
        }

        Info left = postorder(node.left);
        Info right = postorder(node.right);

        if (left.isBST && right.isBST && left.max < node.val && node.val < right.min) {
            int sum = left.sum + right.sum + node.val;
            best = Math.max(best, sum);

            int min = Math.min(node.val, left.min);
            int max = Math.max(node.val, right.max);

            return new Info(true, min, max, sum);
        }

        return new Info(false, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
    }
}
```
