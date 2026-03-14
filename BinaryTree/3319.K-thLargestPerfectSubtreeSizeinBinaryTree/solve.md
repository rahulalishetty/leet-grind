# 3319. K-th Largest Perfect Subtree Size in Binary Tree

## Problem summary

We are given:

- the root of a binary tree
- an integer `k`

We need to return the size of the **k-th largest perfect binary subtree**.

If fewer than `k` perfect binary subtrees exist, return `-1`.

A binary tree is **perfect** if:

1. every internal node has exactly two children
2. all leaves are at the same depth

---

# Core observation

This is a classic **bottom-up tree property** problem.

Whether a subtree rooted at `node` is perfect depends entirely on its two children.

More specifically:

- an empty subtree is not counted as a perfect subtree candidate by itself for the answer, but it is useful as a base case
- a leaf node is always a perfect subtree of size `1`
- a non-leaf subtree is perfect **iff**:
  - both left and right subtrees are perfect
  - the heights of the left and right perfect subtrees are equal

So the natural solution is a **postorder DFS**.

At each node, we want to know:

- is this subtree perfect?
- what is its height?
- what is its size?

If it is perfect, we record its size.

After processing all nodes, we sort recorded sizes in descending order and pick the k-th one.

---

# Approach 1: Postorder DFS + collect all perfect subtree sizes (recommended)

## Intuition

A subtree rooted at `node` is perfect if:

- both children form perfect subtrees
- their heights are the same

This gives a clean recursive structure.

For each node, DFS returns enough information for its parent to decide whether the parent's subtree is perfect.

---

## What each DFS call returns

For every subtree rooted at `node`, we return:

- `isPerfect`
- `height`
- `size`

### Base cases

#### Null node

A null node is treated as a perfect subtree of height `0` and size `0` for recursion convenience.

#### Leaf node

A leaf is a perfect subtree of:

- height `1`
- size `1`

---

## Transition

Suppose we have information from left and right child.

Current subtree is perfect iff:

```text
left.isPerfect == true
right.isPerfect == true
left.height == right.height
```

Then:

```text
size = left.size + right.size + 1
height = left.height + 1
```

If the subtree is perfect, record `size`.

Otherwise, mark it as not perfect.

---

## Java code

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Solution {
    static class Info {
        boolean isPerfect;
        int height;
        int size;

        Info(boolean isPerfect, int height, int size) {
            this.isPerfect = isPerfect;
            this.height = height;
            this.size = size;
        }
    }

    public int kthLargestPerfectSubtree(TreeNode root, int k) {
        List<Integer> sizes = new ArrayList<>();
        dfs(root, sizes);

        sizes.sort(Collections.reverseOrder());

        if (sizes.size() < k) {
            return -1;
        }

        return sizes.get(k - 1);
    }

    private Info dfs(TreeNode node, List<Integer> sizes) {
        if (node == null) {
            return new Info(true, 0, 0);
        }

        Info left = dfs(node.left, sizes);
        Info right = dfs(node.right, sizes);

        if (left.isPerfect && right.isPerfect && left.height == right.height) {
            int size = left.size + right.size + 1;
            int height = left.height + 1;
            sizes.add(size);
            return new Info(true, height, size);
        }

        return new Info(false, 0, 0);
    }
}
```

---

## Why this works

This is a direct structural definition of a perfect binary tree.

A subtree can only be perfect if both children are perfect and symmetric in height.

Because postorder DFS computes child information before the parent, every node can be evaluated exactly once.

Every perfect subtree gets recorded exactly when its root is processed.

Sorting those sizes gives the required ordering.

---

## Complexity

Let `n` be the number of nodes.

### Time complexity

DFS visits each node once:

```text
O(n)
```

If `m` perfect subtree sizes are recorded, sorting costs:

```text
O(m log m)
```

Since `m <= n`, total is:

```text
O(n + m log m) = O(n log n)
```

In this problem `n <= 2000`, so this is more than fast enough.

### Space complexity

- recursion stack: `O(h)`
- list of subtree sizes: `O(m)`

Worst case:

```text
O(n)
```

---

# Approach 2: Postorder DFS + min-heap of size k

## Intuition

In Approach 1 we store **all** perfect subtree sizes, then sort them.

But the problem only asks for the **k-th largest**, not the full sorted order.

So we can maintain a **min-heap of size at most k**:

- if heap size < k, push new perfect subtree size
- otherwise, if new size is larger than heap top, replace heap top

At the end:

- if heap size < k -> return `-1`
- else heap top is the k-th largest perfect subtree size

This avoids sorting all collected sizes.

---

## Why this works

A min-heap of size `k` keeps exactly the `k` largest values seen so far.

Among those `k` largest values, the smallest is the `k`-th largest overall.

So the heap top at the end is the correct answer.

---

## Java code

```java
import java.util.PriorityQueue;

class Solution {
    static class Info {
        boolean isPerfect;
        int height;
        int size;

        Info(boolean isPerfect, int height, int size) {
            this.isPerfect = isPerfect;
            this.height = height;
            this.size = size;
        }
    }

    public int kthLargestPerfectSubtree(TreeNode root, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        dfs(root, minHeap, k);

        if (minHeap.size() < k) {
            return -1;
        }

        return minHeap.peek();
    }

    private Info dfs(TreeNode node, PriorityQueue<Integer> minHeap, int k) {
        if (node == null) {
            return new Info(true, 0, 0);
        }

        Info left = dfs(node.left, minHeap, k);
        Info right = dfs(node.right, minHeap, k);

        if (left.isPerfect && right.isPerfect && left.height == right.height) {
            int size = left.size + right.size + 1;
            int height = left.height + 1;

            if (minHeap.size() < k) {
                minHeap.offer(size);
            } else if (size > minHeap.peek()) {
                minHeap.poll();
                minHeap.offer(size);
            }

            return new Info(true, height, size);
        }

        return new Info(false, 0, 0);
    }
}
```

---

## Complexity

DFS:

```text
O(n)
```

Heap maintenance for each perfect subtree:

```text
O(log k)
```

If there are `m` perfect subtrees, total is:

```text
O(n + m log k)
```

Since `m <= n`, this becomes:

```text
O(n log k)
```

### Space complexity

- recursion stack: `O(h)`
- heap: `O(k)`

Worst case:

```text
O(h + k)
```

---

## When this approach is better

If `n` were much larger and `k` much smaller than `n`, this heap approach would be preferable over sorting all sizes.

Even though constraints here are small enough for sorting, this is a strong optimization pattern.

---

# Approach 3: Brute force check every subtree separately

## Intuition

A straightforward but inefficient approach is:

1. for every node, treat it as a candidate subtree root
2. recursively check whether that subtree is perfect
3. if yes, compute its size
4. collect sizes and answer from them

This is conceptually simple but repeats work many times.

For example, the same subtree gets re-checked from multiple ancestors.

---

## Java code

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Solution {
    public int kthLargestPerfectSubtree(TreeNode root, int k) {
        List<Integer> sizes = new ArrayList<>();
        collect(root, sizes);

        sizes.sort(Collections.reverseOrder());

        if (sizes.size() < k) {
            return -1;
        }

        return sizes.get(k - 1);
    }

    private void collect(TreeNode node, List<Integer> sizes) {
        if (node == null) {
            return;
        }

        int[] res = check(node);
        if (res[0] == 1) { // perfect
            sizes.add(res[1]); // size
        }

        collect(node.left, sizes);
        collect(node.right, sizes);
    }

    // returns [isPerfect(1/0), size, height]
    private int[] check(TreeNode node) {
        if (node == null) {
            return new int[]{1, 0, 0};
        }

        int[] left = check(node.left);
        int[] right = check(node.right);

        if (left[0] == 1 && right[0] == 1 && left[2] == right[2]) {
            return new int[]{1, left[1] + right[1] + 1, left[2] + 1};
        }

        return new int[]{0, 0, 0};
    }
}
```

---

## Complexity

For each node, we may re-traverse a large part of its subtree.

Worst-case time complexity:

```text
O(n^2)
```

Space:

```text
O(h + m)
```

This is correct but inefficient.

---

# Approach 4: Height-based perfect subtree formula viewpoint

## Intuition

A perfect binary tree of height `h` has size:

```text
2^h - 1
```

So another way to think about the problem is:

- determine whether a subtree is perfect
- if yes, compute height
- then derive size using the formula

This is mathematically elegant, but in practice it is not better than directly carrying `size` in DFS.

Because if we already know left and right subtree sizes, computing:

```text
size = left.size + right.size + 1
```

is simpler and avoids exponentiation concerns.

Still, the height formula is useful conceptually:

- once a subtree is confirmed perfect, its size is fully determined by height

---

## Example of formula-based code

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Solution {
    static class Info {
        boolean isPerfect;
        int height;

        Info(boolean isPerfect, int height) {
            this.isPerfect = isPerfect;
            this.height = height;
        }
    }

    public int kthLargestPerfectSubtree(TreeNode root, int k) {
        List<Integer> sizes = new ArrayList<>();
        dfs(root, sizes);
        sizes.sort(Collections.reverseOrder());
        return sizes.size() < k ? -1 : sizes.get(k - 1);
    }

    private Info dfs(TreeNode node, List<Integer> sizes) {
        if (node == null) {
            return new Info(true, 0);
        }

        Info left = dfs(node.left, sizes);
        Info right = dfs(node.right, sizes);

        if (left.isPerfect && right.isPerfect && left.height == right.height) {
            int height = left.height + 1;
            int size = (1 << height) - 1;  // safe here because n <= 2000
            sizes.add(size);
            return new Info(true, height);
        }

        return new Info(false, 0);
    }
}
```

---

## Complexity

Same as Approach 1:

```text
O(n log n)
```

after sorting all sizes.

---

# Comparison of approaches

## Approach 1: Postorder DFS + collect all sizes

- simplest
- very easy to explain
- ideal for these constraints

## Approach 2: Postorder DFS + size-k heap

- more optimized
- avoids sorting everything
- elegant when only k-th largest is needed

## Approach 3: Brute force per subtree

- easy to think of first
- too slow

## Approach 4: Height formula variant

- elegant mathematically
- not really simpler in implementation

---

# Best approach

The best practical solution here is:

## **Approach 1** for simplicity

or

## **Approach 2** if you want the most targeted “k-th largest” optimization

Because `n <= 2000`, Approach 1 is already excellent and probably the best interview answer.

If you want to demonstrate extra optimization awareness, Approach 2 is a nice follow-up.

---

# Correctness intuition for Approach 1

A subtree is perfect exactly when its children are perfect and have equal height.

This recursive property is both necessary and sufficient.

Postorder DFS ensures that child results are available before evaluating the current node.

Thus every node can be classified correctly in one pass.

Every perfect subtree size is recorded exactly once, and sorting these gives the correct descending order.

So the k-th element in that sorted order is the answer, or `-1` if fewer than `k` exist.

---

# Final recommended solution

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Solution {
    static class Info {
        boolean isPerfect;
        int height;
        int size;

        Info(boolean isPerfect, int height, int size) {
            this.isPerfect = isPerfect;
            this.height = height;
            this.size = size;
        }
    }

    public int kthLargestPerfectSubtree(TreeNode root, int k) {
        List<Integer> sizes = new ArrayList<>();
        dfs(root, sizes);

        sizes.sort(Collections.reverseOrder());

        if (sizes.size() < k) {
            return -1;
        }

        return sizes.get(k - 1);
    }

    private Info dfs(TreeNode node, List<Integer> sizes) {
        if (node == null) {
            return new Info(true, 0, 0);
        }

        Info left = dfs(node.left, sizes);
        Info right = dfs(node.right, sizes);

        if (left.isPerfect && right.isPerfect && left.height == right.height) {
            int size = left.size + right.size + 1;
            int height = left.height + 1;
            sizes.add(size);
            return new Info(true, height, size);
        }

        return new Info(false, 0, 0);
    }
}
```

## Complexity

- **Time:** `O(n log n)` in the worst case due to sorting
- **Space:** `O(n)` for recursion stack + collected sizes

This is the cleanest and most reliable solution for the problem.
