# Median Value at a Given Level in a Binary Search Tree

## Problem summary

We are given:

- the root of a Binary Search Tree (BST)
- an integer `level`

The root is at level `0`, its children at level `1`, and so on.

We must:

1. collect all node values at the given level
2. sort them in non-decreasing order
3. return the median

If the count of values is even, we return the **upper median**, meaning:

```text
sorted[count / 2]
```

using 0-based indexing.

If the requested level does not exist, return `-1`.

---

# Important observation

Even though the tree is a **BST**, the BST ordering does **not directly help** much for this problem.

Why?

Because nodes at a fixed level are not guaranteed to appear sorted relative to each other by simple traversal order.

So the task is essentially:

- find all nodes at depth `level`
- compute the upper median of those values

The two most natural traversal styles are:

- **BFS** (level-order traversal)
- **DFS** (depth-tracking traversal)

---

# What does “upper median” mean?

Suppose the values at the target level after sorting are:

```text
[3, 8]
```

There are two middle values in the usual sense: `3` and `8`.

The problem asks for the **larger one**, so the answer is:

```text
8
```

In general, if `m = values.size()`, then the median index is:

```text
m / 2
```

because integer division automatically picks the upper middle for even lengths.

Examples:

- size = 1 -> index 0
- size = 2 -> index 1
- size = 3 -> index 1
- size = 4 -> index 2

---

# Approach 1: BFS / level-order traversal + sorting (recommended)

## Intuition

Since the problem explicitly asks about one specific tree level, BFS is the most natural solution.

BFS visits nodes level by level.

So we can:

1. traverse the tree level by level
2. stop once we reach the requested level
3. collect all node values there
4. sort them
5. return the upper median

This is direct and easy to reason about.

---

## Algorithm

1. If `root == null`, return `-1`
2. Initialize a queue with the root
3. Track current level starting at `0`
4. While queue is not empty:
   - let `size = queue.size()`
   - if current level equals target level:
     - collect all `size` node values
     - sort them
     - return `values.get(values.size() / 2)`
   - otherwise process the entire level normally:
     - pop each node
     - push its children
   - increment current level
5. If traversal finishes before reaching the target level, return `-1`

---

## Java code

```java
import java.util.*;

class Solution {
    public int levelMedian(TreeNode root, int level) {
        if (root == null) {
            return -1;
        }

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);

        int currentLevel = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();

            if (currentLevel == level) {
                List<Integer> values = new ArrayList<>(size);

                for (int i = 0; i < size; i++) {
                    TreeNode node = queue.poll();
                    values.add(node.val);
                }

                Collections.sort(values);
                return values.get(values.size() / 2);
            }

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }

            currentLevel++;
        }

        return -1;
    }
}
```

---

## Why this works

BFS guarantees that all nodes popped in one iteration belong to exactly one level.

So when `currentLevel == level`, the queue contains precisely the nodes at the required depth.

Sorting those values and taking index `size / 2` returns the upper median by definition.

---

## Complexity

Let:

- `n` = total number of nodes
- `k` = number of nodes at the target level

### Time complexity

In the worst case, BFS may visit all nodes above the target level and the level itself:

```text
O(n)
```

Then sorting the target-level values costs:

```text
O(k log k)
```

So overall:

```text
O(n + k log k)
```

Worst case, if the target level is large and contains many nodes, this is still fine.

### Space complexity

Queue can hold the width of the tree:

```text
O(w)
```

and we store `k` values for the target level.

So total:

```text
O(w + k)
```

Worst case:

```text
O(n)
```

---

# Approach 2: DFS with depth tracking + sorting

## Intuition

Instead of BFS, we can do a DFS and carry the current depth.

Whenever we reach a node at the target level, we store its value.

If depth becomes greater than target level, we stop exploring that branch.

After traversal:

- if no values were collected, return `-1`
- otherwise sort and return upper median

This is also clean and often simpler if you like recursive solutions.

---

## Algorithm

1. Create a list `values`
2. DFS from root with current depth `0`
3. At each node:
   - if node is null, return
   - if depth == target level, add node value and return
   - if depth > target level, return
   - recurse left and right with depth + 1
4. After DFS:
   - if `values` is empty, return `-1`
   - sort `values`
   - return `values.get(values.size() / 2)`

---

## Java code

```java
import java.util.*;

class Solution {
    public int levelMedian(TreeNode root, int level) {
        List<Integer> values = new ArrayList<>();
        dfs(root, 0, level, values);

        if (values.isEmpty()) {
            return -1;
        }

        Collections.sort(values);
        return values.get(values.size() / 2);
    }

    private void dfs(TreeNode node, int depth, int targetLevel, List<Integer> values) {
        if (node == null) {
            return;
        }

        if (depth == targetLevel) {
            values.add(node.val);
            return;
        }

        if (depth > targetLevel) {
            return;
        }

        dfs(node.left, depth + 1, targetLevel, values);
        dfs(node.right, depth + 1, targetLevel, values);
    }
}
```

---

## Why this works

DFS visits every node on all root-to-node paths up to the target level.

Every node exactly at the requested level contributes to `values`.

No deeper nodes matter, so pruning with `depth > targetLevel` is valid.

Sorting and selecting index `size / 2` gives the required upper median.

---

## Complexity

### Time complexity

DFS may visit all nodes down to the target depth, worst case:

```text
O(n)
```

Sorting collected values:

```text
O(k log k)
```

Total:

```text
O(n + k log k)
```

### Space complexity

Recursion stack can reach tree height `h`, and collected values use `O(k)`:

```text
O(h + k)
```

Worst case:

```text
O(n)
```

---

# Approach 3: BFS + two heaps to avoid sorting all values

## Intuition

If the target level has many nodes, sorting all values costs `O(k log k)`.

We can instead compute the upper median online using two heaps:

- max heap for lower half
- min heap for upper half

To get the **upper median**, we keep the min heap either equal in size to the max heap or one larger.
Then the answer is the top of the min heap.

This avoids sorting the full list explicitly, though complexity remains `O(k log k)` overall. It is mostly useful if you want a streaming-median style solution.

---

## Heap balancing rule for upper median

We want:

- `upper.size() == lower.size()`
- or `upper.size() == lower.size() + 1`

where:

- `lower` is a max heap
- `upper` is a min heap

And all values in `lower <= all values in upper`.

Then the upper median is simply:

```text
upper.peek()
```

---

## Algorithm

1. Use BFS to reach target level
2. When target level is reached:
   - process each node value into the two heaps
3. If no nodes exist at that level, return `-1`
4. Return `upper.peek()`

---

## Java code

```java
import java.util.*;

class Solution {
    public int levelMedian(TreeNode root, int level) {
        if (root == null) {
            return -1;
        }

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        int currentLevel = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();

            if (currentLevel == level) {
                PriorityQueue<Integer> lower = new PriorityQueue<>(Collections.reverseOrder());
                PriorityQueue<Integer> upper = new PriorityQueue<>();

                for (int i = 0; i < size; i++) {
                    TreeNode node = queue.poll();
                    add(node.val, lower, upper);
                }

                return upper.isEmpty() ? -1 : upper.peek();
            }

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }

            currentLevel++;
        }

        return -1;
    }

    private void add(int val, PriorityQueue<Integer> lower, PriorityQueue<Integer> upper) {
        if (upper.isEmpty() || val >= upper.peek()) {
            upper.offer(val);
        } else {
            lower.offer(val);
        }

        // Rebalance so upper has equal size or one more than lower
        if (upper.size() < lower.size()) {
            upper.offer(lower.poll());
        } else if (upper.size() > lower.size() + 1) {
            lower.offer(upper.poll());
        }
    }
}
```

---

## Complexity

### Time complexity

BFS part:

```text
O(n)
```

Median maintenance for `k` values:

```text
O(k log k)
```

Total:

```text
O(n + k log k)
```

### Space complexity

Queue plus heaps:

```text
O(w + k)
```

---

## When is this useful?

This is more complicated than sorting, but it is a nice alternative if:

- values arrive as a stream
- you want the median without fully sorting

For this problem, simple sorting is usually better.

---

# Approach 4: BFS + quickselect on target level values

## Intuition

If we only need one median value and do not need the whole sorted order, we can use **quickselect** instead of sorting.

Process:

1. use BFS to collect all values at target level
2. use quickselect to find the element at index `k / 2`

This reduces expected selection cost from `O(k log k)` to `O(k)` after the BFS.

This can be more efficient when the target level is very large.

---

## Algorithm

1. BFS to collect values at target level
2. If empty, return `-1`
3. Let `targetIndex = values.size() / 2`
4. Return quickselect result at that index

---

## Java code

```java
import java.util.*;

class Solution {
    public int levelMedian(TreeNode root, int level) {
        if (root == null) {
            return -1;
        }

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        int currentLevel = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();

            if (currentLevel == level) {
                int[] arr = new int[size];
                for (int i = 0; i < size; i++) {
                    TreeNode node = queue.poll();
                    arr[i] = node.val;
                }

                return quickSelect(arr, 0, arr.length - 1, arr.length / 2);
            }

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }

            currentLevel++;
        }

        return -1;
    }

    private int quickSelect(int[] arr, int left, int right, int k) {
        while (left <= right) {
            int pivotIndex = partition(arr, left, right);

            if (pivotIndex == k) {
                return arr[pivotIndex];
            } else if (pivotIndex < k) {
                left = pivotIndex + 1;
            } else {
                right = pivotIndex - 1;
            }
        }
        return -1;
    }

    private int partition(int[] arr, int left, int right) {
        int pivot = arr[right];
        int i = left;

        for (int j = left; j < right; j++) {
            if (arr[j] <= pivot) {
                swap(arr, i, j);
                i++;
            }
        }

        swap(arr, i, right);
        return i;
    }

    private void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
```

---

## Complexity

### Time complexity

BFS traversal:

```text
O(n)
```

Quickselect expected:

```text
O(k)
```

So expected total:

```text
O(n + k)
```

Worst-case quickselect can degrade to `O(k^2)` if badly pivoted, though randomized or median-of-three pivoting can reduce that risk.

### Space complexity

```text
O(w + k)
```

---

## Comments

This is a solid alternative if you care about optimizing selection over full sorting.

But for interview clarity, BFS + sort is usually the best answer.

---

# Comparison of approaches

## Approach 1: BFS + sorting

- simplest
- most natural
- robust
- recommended

## Approach 2: DFS + sorting

- also simple
- good if recursion is preferred

## Approach 3: BFS + two heaps

- avoids explicit full sorting
- more complex
- not especially better here

## Approach 4: BFS + quickselect

- asymptotically attractive
- slightly more implementation complexity

---

# Best approach

The best practical solution is:

## **Approach 1: BFS + sort target level values**

Why:

- the problem is explicitly level-based
- BFS reaches the desired level directly
- code is short and easy to verify
- tie / upper median handling is straightforward

---

# Final recommended solution

```java
import java.util.*;

class Solution {
    public int levelMedian(TreeNode root, int level) {
        if (root == null) {
            return -1;
        }

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);

        int currentLevel = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();

            if (currentLevel == level) {
                List<Integer> values = new ArrayList<>(size);

                for (int i = 0; i < size; i++) {
                    TreeNode node = queue.poll();
                    values.add(node.val);
                }

                Collections.sort(values);
                return values.get(values.size() / 2);
            }

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();

                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }

            currentLevel++;
        }

        return -1;
    }
}
```

## Complexity

- **Time:** `O(n + k log k)`
- **Space:** `O(w + k)`

where:

- `n` = number of nodes
- `k` = number of nodes at target level
- `w` = maximum width of the tree

This is the cleanest and most interview-friendly solution for the problem.
