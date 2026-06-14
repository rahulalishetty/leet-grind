# LeetCode 654 — Maximum Binary Tree

## Problem

Given an integer array `nums` with no duplicates:

1. The root is the maximum element in the array.
2. The left subtree is built recursively from elements to the left of the maximum.
3. The right subtree is built recursively from elements to the right of the maximum.

Return the root of the constructed Maximum Binary Tree.

---

# Solution 1: Brute Force Recursive Divide-and-Conquer

## Idea

Follow the problem statement literally:

1. Find the maximum element in the current range.
2. Create a node.
3. Recursively build the left subtree.
4. Recursively build the right subtree.

## Example

```text
nums = [3,2,1,6,0,5]

Maximum = 6

         6
       /   \
      ?     ?

Left  = [3,2,1]
Right = [0,5]
```

Continue recursively.

---

## Java Code

```java
class Solution {

    public TreeNode constructMaximumBinaryTree(int[] nums) {
        return build(nums, 0, nums.length - 1);
    }

    private TreeNode build(int[] nums, int left, int right) {
        if (left > right) {
            return null;
        }

        int maxIndex = left;

        for (int i = left + 1; i <= right; i++) {
            if (nums[i] > nums[maxIndex]) {
                maxIndex = i;
            }
        }

        TreeNode root = new TreeNode(nums[maxIndex]);

        root.left = build(nums, left, maxIndex - 1);
        root.right = build(nums, maxIndex + 1, right);

        return root;
    }
}
```

## Complexity

### Time

Finding maximum in every recursive call:

```text
Worst Case:
n + (n-1) + (n-2) + ... + 1
= O(n²)
```

### Space

```text
Average: O(log n)
Worst:   O(n)
```

(recursion stack)

---

# Solution 2: Monotonic Stack (Optimal)

## Key Observation

Consider:

```text
[3,2,1,6,0,5]
```

The parent of each element is the nearest greater element either:

- on the left
- on the right

Specifically:

```text
Parent = smaller of the two greater elements
```

A decreasing monotonic stack helps us discover these relationships in one pass.

---

## How It Works

Maintain a stack where values are decreasing.

For every number:

```text
while stack top < current:
    pop elements
```

The popped elements become the left child of the current node.

If stack is not empty after popping:

```text
stack.peek().right = current
```

Then push current.

---

## Walkthrough

```text
nums = [3,2,1,6]
```

### Insert 3

```text
3
```

Stack:

```text
[3]
```

---

### Insert 2

```text
3
 \
  2
```

Stack:

```text
[3,2]
```

---

### Insert 1

```text
3
 \
  2
   \
    1
```

Stack:

```text
[3,2,1]
```

---

### Insert 6

Pop:

```text
1
2
3
```

Attach:

```text
      6
     /
    3
     \
      2
       \
        1
```

Now push 6.

Final tree is built incrementally.

---

## Java Code

```java
import java.util.*;

class Solution {

    public TreeNode constructMaximumBinaryTree(int[] nums) {

        Deque<TreeNode> stack = new ArrayDeque<>();

        for (int num : nums) {

            TreeNode current = new TreeNode(num);

            while (!stack.isEmpty() &&
                   stack.peek().val < num) {

                current.left = stack.pop();
            }

            if (!stack.isEmpty()) {
                stack.peek().right = current;
            }

            stack.push(current);
        }

        TreeNode root = null;

        while (!stack.isEmpty()) {
            root = stack.pop();
        }

        return root;
    }
}
```

## Complexity

### Time

Each node:

- pushed once
- popped once

```text
O(n)
```

### Space

```text
O(n)
```

for stack.

---

# Why the Monotonic Stack Works

Suppose we process:

```text
[3,2,1,6,0,5]
```

The stack always maintains:

```text
top -> smallest
bottom -> largest
```

When a larger element arrives:

```text
6
```

all smaller elements to its left are popped.

Those popped elements must belong in the left subtree of `6` because:

```text
6 is the first greater element to their right.
```

If another greater element exists on the left, it remains in the stack and becomes the ancestor.

Thus the stack naturally reconstructs the same tree that the recursive definition produces.

---

# Comparison

| Approach                   | Time             | Space | Notes                            |
| -------------------------- | ---------------- | ----- | -------------------------------- |
| Recursive Divide & Conquer | O(n²) worst case | O(n)  | Direct translation of definition |
| Monotonic Stack            | O(n)             | O(n)  | Optimal solution                 |

---

# Interview Discussion

A strong interview progression is:

1. Explain the recursive definition.
2. Implement the O(n²) divide-and-conquer solution.
3. Observe repeated maximum searches.
4. Introduce the nearest-greater-element insight.
5. Derive the monotonic stack solution.
6. Arrive at the optimal O(n) implementation.

The monotonic stack solution is generally considered the expected optimal answer.
