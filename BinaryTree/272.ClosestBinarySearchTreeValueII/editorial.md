import pypandoc

text = """

# 272. Closest Binary Search Tree Value II — Solution Approaches

## Approach 1: Sort With Custom Comparator

### Intuition

Traverse the tree, collect all values, then sort them based on their distance from the target.

### Algorithm

1. Perform DFS traversal and store values in an array.
2. Sort the array using a comparator based on `abs(value - target)`.
3. Return the first `k` values.

### Java Implementation

```java
class Solution {
    public List<Integer> closestKValues(TreeNode root, double target, int k) {
        List<Integer> arr = new ArrayList<>();
        dfs(root, arr);

        Collections.sort(arr, (o1, o2) ->
            Math.abs(o1 - target) <= Math.abs(o2 - target) ? -1 : 1
        );

        return arr.subList(0, k);
    }

    public void dfs(TreeNode node, List<Integer> arr) {
        if (node == null) return;

        arr.add(node.val);
        dfs(node.left, arr);
        dfs(node.right, arr);
    }
}
```

### Complexity Analysis

**Time Complexity**

```
O(n log n)
```

**Space Complexity**

```
O(n)
```

---

# Approach 2: Traverse With Heap

### Intuition

Maintain a **max heap of size k** storing the closest values.

### Algorithm

1. Traverse the tree using DFS.
2. Push each node value into a max heap.
3. If heap size exceeds `k`, remove the farthest element.
4. Remaining heap elements are the answer.

### Java Implementation

```java
class Solution {
    public List<Integer> closestKValues(TreeNode root, double target, int k) {

        Queue<Integer> heap = new PriorityQueue<>(
            (a, b) -> Math.abs(a - target) > Math.abs(b - target) ? -1 : 1
        );

        dfs(root, heap, k);

        return new ArrayList<>(heap);
    }

    public void dfs(TreeNode node, Queue<Integer> heap, int k) {
        if (node == null) return;

        heap.add(node.val);
        if (heap.size() > k) {
            heap.remove();
        }

        dfs(node.left, heap, k);
        dfs(node.right, heap, k);
    }
}
```

### Complexity Analysis

**Time Complexity**

```
O(n log k)
```

**Space Complexity**

```
O(n + k)
```

---

# Approach 3: Inorder Traversal + Sliding Window

### Intuition

Inorder traversal of a BST produces **sorted values**.
The answer must be a **subarray of length k**.

### Algorithm

1. Perform inorder traversal → sorted array.
2. Find the element closest to the target.
3. Expand a sliding window around that element until size = k.

### Java Implementation

```java
class Solution {

    public List<Integer> closestKValues(TreeNode root, double target, int k) {

        List<Integer> arr = new ArrayList<>();
        dfs(root, arr);

        int start = 0;
        double minDiff = Double.MAX_VALUE;

        for (int i = 0; i < arr.size(); i++) {
            if (Math.abs(arr.get(i) - target) < minDiff) {
                minDiff = Math.abs(arr.get(i) - target);
                start = i;
            }
        }

        int left = start;
        int right = start + 1;

        while (right - left - 1 < k) {

            if (left < 0) {
                right++;
                continue;
            }

            if (right == arr.size() ||
                Math.abs(arr.get(left) - target) <= Math.abs(arr.get(right) - target)) {
                left--;
            } else {
                right++;
            }
        }

        return arr.subList(left + 1, right);
    }

    public void dfs(TreeNode node, List<Integer> arr) {
        if (node == null) return;

        dfs(node.left, arr);
        arr.add(node.val);
        dfs(node.right, arr);
    }
}
```

### Complexity Analysis

**Time Complexity**

```
O(n + k)
```

**Space Complexity**

```
O(n)
```

---

# Approach 4: Binary Search Left Bound

### Intuition

Instead of sliding the window, directly **binary search the left boundary** of the answer window.

### Algorithm

1. Build sorted array via inorder traversal.
2. Binary search range `[0, arr.length - k]`.
3. Compare `arr[mid]` and `arr[mid + k]` relative to target.
4. Return `arr[left : left + k]`.

### Java Implementation

```java
class Solution {

    public List<Integer> closestKValues(TreeNode root, double target, int k) {

        List<Integer> arr = new ArrayList<>();
        dfs(root, arr);

        int left = 0;
        int right = arr.size() - k;

        while (left < right) {

            int mid = (left + right) / 2;

            if (Math.abs(target - arr.get(mid + k)) <
                Math.abs(target - arr.get(mid))) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return arr.subList(left, left + k);
    }

    public void dfs(TreeNode node, List<Integer> arr) {
        if (node == null) return;

        dfs(node.left, arr);
        arr.add(node.val);
        dfs(node.right, arr);
    }
}
```

### Complexity Analysis

**Time Complexity**

```
O(n)
```

**Space Complexity**

```
O(n)
```

---

# Approach 5: Build Window With Deque

### Intuition

Perform **inorder traversal** while maintaining a sliding window using a **deque**.

### Algorithm

1. Perform inorder traversal.
2. Add values to a deque.
3. If size exceeds `k`, remove the element farther from target.
4. Stop traversal early if the window becomes optimal.

### Java Implementation

```java
class Solution {

    public List<Integer> closestKValues(TreeNode root, double target, int k) {

        Deque<Integer> queue = new LinkedList<>();
        dfs(root, queue, k, target);

        return new ArrayList<>(queue);
    }

    public void dfs(TreeNode node, Deque<Integer> queue, int k, double target) {

        if (node == null) return;

        dfs(node.left, queue, k, target);

        queue.add(node.val);

        if (queue.size() > k) {

            if (Math.abs(target - queue.peekFirst()) <=
                Math.abs(target - queue.peekLast())) {

                queue.removeLast();
                return;
            } else {
                queue.removeFirst();
            }
        }

        dfs(node.right, queue, k, target);
    }
}
```

### Complexity Analysis

**Time Complexity**

```
O(n)
```

**Space Complexity**

```
O(n + k)
```

"""

out = "/mnt/data/closest_bst_value_ii_solution.md"
pypandoc.convert_text(text, "md", format="md", outputfile=out, extra_args=["--standalone"])
out
