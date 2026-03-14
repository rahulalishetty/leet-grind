# 2792. Count Nodes That Are Great Enough

## Problem summary

A node is **great enough** if both conditions hold:

1. its subtree has at least `k` nodes
2. its value is greater than the value of at least `k` nodes in its subtree

We need to count how many nodes satisfy this.

Since `k <= 10`, that small constraint is the most important clue.
It suggests we do **not** need to keep an entire sorted list of every subtree's values.
Usually, we only need the **smallest `k` values** in each subtree.

---

# Key observation

For a node with value `x`, the statement

```text
x is greater than at least k nodes in its subtree
```

is equivalent to:

```text
there exist at least k values in the subtree that are < x
```

If we knew the **k smallest values** in the subtree, then:

- if the subtree has fewer than `k` nodes, condition 1 fails
- otherwise, let the k-th smallest value be `t`
- then the node is great enough iff:

```text
x > t
```

So for each subtree, it is enough to maintain:

- subtree size
- the sorted list of the smallest at most `k` values

That leads to an efficient postorder DFS.

---

# Approach 1: Postorder DFS with top-k smallest values (recommended)

## Intuition

For each node, gather information from left and right children:

- number of nodes in left subtree
- number of nodes in right subtree
- smallest up to `k` values from left subtree
- smallest up to `k` values from right subtree

Then merge them together with the current node's value.

Because `k <= 10`, merging small sorted lists is cheap.

After computing the merged smallest values for the current subtree:

- if subtree size is at least `k`
- and current node value is greater than the k-th smallest value
- then this node is great enough

This is a classic postorder DP on trees.

---

## What each DFS call returns

For each node we return:

- `size` = number of nodes in its subtree
- `smallest` = sorted list of the smallest at most `k` values in its subtree

This is enough for the parent.

---

## Algorithm

For every node:

1. recursively solve left subtree
2. recursively solve right subtree
3. merge:
   - left smallest list
   - right smallest list
   - current node value
4. keep only the smallest `k` values
5. subtree size is:

```text
left.size + right.size + 1
```

6. if subtree size >= k and current value > k-th smallest, increment answer

---

## Java code

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Solution {
    private int answer = 0;
    private int k;

    static class State {
        int size;
        List<Integer> smallest;

        State(int size, List<Integer> smallest) {
            this.size = size;
            this.smallest = smallest;
        }
    }

    public int countGreatEnoughNodes(TreeNode root, int k) {
        this.k = k;
        dfs(root);
        return answer;
    }

    private State dfs(TreeNode node) {
        if (node == null) {
            return new State(0, new ArrayList<>());
        }

        State left = dfs(node.left);
        State right = dfs(node.right);

        int size = left.size + right.size + 1;

        List<Integer> merged = mergeThree(left.smallest, right.smallest, node.val);

        if (size >= k && merged.size() >= k && node.val > merged.get(k - 1)) {
            answer++;
        }

        return new State(size, merged);
    }

    private List<Integer> mergeThree(List<Integer> a, List<Integer> b, int val) {
        List<Integer> temp = new ArrayList<>(a.size() + b.size() + 1);
        temp.addAll(a);
        temp.addAll(b);
        temp.add(val);
        Collections.sort(temp);

        if (temp.size() > k) {
            return new ArrayList<>(temp.subList(0, k));
        }
        return temp;
    }
}
```

---

## Complexity

Let `n` be the number of nodes.

Since `k <= 10`, every list has size at most `10`.

### Time complexity

At each node, we sort at most:

```text
k + k + 1 <= 21
```

values, which is constant.

So overall:

```text
O(n)
```

### Space complexity

Recursion stack:

```text
O(h)
```

where `h` is tree height.

Returned lists store only up to `k` values, so auxiliary per node work is constant-sized.

Worst-case recursion depth:

```text
O(n)
```

for a skewed tree.

---

# Approach 2: Postorder DFS with min-heaps / bounded containers

## Intuition

Instead of explicitly keeping sorted arrays, we can use a bounded structure that stores the smallest `k` values seen in the subtree.

Because we only care about the first `k` smallest values, we can maintain a **max-heap of size k**:

- if heap has fewer than `k` values, push current value
- otherwise, compare with largest value in heap
  - if current value is smaller, replace the largest
  - otherwise ignore it

This way the heap always stores the smallest `k` values of the subtree.

For the final test:

- if subtree size < `k`, fail
- otherwise, the largest element in this max-heap is exactly the k-th smallest value
- node is great enough iff:

```text
node.val > heap.peek()
```

This is also efficient because `k` is tiny.

---

## Java code

```java
import java.util.Collections;
import java.util.PriorityQueue;

class Solution {
    private int answer = 0;
    private int k;

    static class State {
        int size;
        PriorityQueue<Integer> heap; // max-heap containing smallest k values

        State(int size, PriorityQueue<Integer> heap) {
            this.size = size;
            this.heap = heap;
        }
    }

    public int countGreatEnoughNodes(TreeNode root, int k) {
        this.k = k;
        dfs(root);
        return answer;
    }

    private State dfs(TreeNode node) {
        if (node == null) {
            return new State(0, new PriorityQueue<>(Collections.reverseOrder()));
        }

        State left = dfs(node.left);
        State right = dfs(node.right);

        PriorityQueue<Integer> heap = new PriorityQueue<>(Collections.reverseOrder());

        for (int x : left.heap) {
            add(heap, x);
        }
        for (int x : right.heap) {
            add(heap, x);
        }
        add(heap, node.val);

        int size = left.size + right.size + 1;

        if (size >= k && heap.size() == k && node.val > heap.peek()) {
            answer++;
        }

        return new State(size, heap);
    }

    private void add(PriorityQueue<Integer> heap, int val) {
        if (heap.size() < k) {
            heap.offer(val);
        } else if (val < heap.peek()) {
            heap.poll();
            heap.offer(val);
        }
    }
}
```

---

## Complexity

Because heap size is always at most `k <= 10`, all heap operations are constant-time for practical purposes.

### Time complexity

```text
O(n)
```

### Space complexity

```text
O(h)
```

for recursion, worst-case `O(n)`.

---

## Comparison with Approach 1

This approach is fine, but the sorted-list merge in Approach 1 is simpler and usually easier to explain.

---

# Approach 3: Brute force — collect and sort every subtree

## Intuition

The brute-force idea is:

For each node:

- collect all values in its subtree
- sort them
- check whether at least `k` of them are smaller than the current node value

This is correct, but very expensive because the same subtree values are recomputed many times.

---

## Java code

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Solution {
    public int countGreatEnoughNodes(TreeNode root, int k) {
        return solve(root, k);
    }

    private int solve(TreeNode node, int k) {
        if (node == null) {
            return 0;
        }

        int ans = solve(node.left, k) + solve(node.right, k);

        List<Integer> vals = new ArrayList<>();
        collect(node, vals);
        Collections.sort(vals);

        if (vals.size() >= k) {
            int smaller = 0;
            for (int x : vals) {
                if (x < node.val) {
                    smaller++;
                }
            }
            if (smaller >= k) {
                ans++;
            }
        }

        return ans;
    }

    private void collect(TreeNode node, List<Integer> vals) {
        if (node == null) {
            return;
        }
        vals.add(node.val);
        collect(node.left, vals);
        collect(node.right, vals);
    }
}
```

---

## Complexity

Let `n` be number of nodes.

For each node, subtree collection may cost `O(n)` in the worst case, and sorting also costs extra.

### Time complexity

Worst case:

```text
O(n^2 log n)
```

### Space complexity

```text
O(n)
```

for subtree collection lists plus recursion.

---

## Verdict

Correct, but far too slow for `n = 10^4`.

---

# Approach 4: Full multiset merging (works, but overkill)

## Intuition

A more advanced but less efficient approach is to let each DFS return the **entire sorted multiset** of its subtree values.

Then parent merges left and right sorted lists and inserts its own value.

This avoids recomputing subtree values from scratch, but if we keep full subtree lists, the merge cost becomes large overall.

Because the problem only needs to know the smallest `k` values, returning the full multiset is unnecessary.

Still, it is a valid intermediate idea.

---

## High-level idea

For each node:

- get sorted list from left subtree
- get sorted list from right subtree
- merge both
- insert current node value in sorted order
- count how many are smaller than node value

This is much better than brute force recomputation, but still slower than keeping only top-k smallest.

---

## Complexity

Depending on implementation, worst-case can still be around:

```text
O(n^2)
```

or `O(n log^2 n)` only with much heavier data structures.

So it is not the preferred solution here.

---

# Why the small constraint k <= 10 matters so much

If `k` were large, we might need more sophisticated order-statistics data structures.

But here:

```text
1 <= k <= 10
```

That means at every subtree we only care about a tiny constant-sized summary.

This turns the problem into a very efficient tree DP problem.

That is the main reason Approach 1 is so strong.

---

# Correctness idea for Approach 1

Suppose for a node's subtree we know its `k` smallest values.

- If subtree size < `k`, then it cannot be great enough
- Otherwise, the k-th smallest value `t` is the threshold:
  - there are at least `k` values smaller than the current node iff `node.val > t`

By merging the `k` smallest values from children plus the current node, we correctly obtain the `k` smallest values for the full subtree.

Thus the test at every node is correct.

---

# Best approach

The best solution is:

## **Approach 1: Postorder DFS with smallest-k values**

Why:

- uses the small `k <= 10` constraint perfectly
- linear time
- simple to implement
- easy to explain in an interview

---

# Final recommended solution

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Solution {
    private int answer = 0;
    private int k;

    static class State {
        int size;
        List<Integer> smallest;

        State(int size, List<Integer> smallest) {
            this.size = size;
            this.smallest = smallest;
        }
    }

    public int countGreatEnoughNodes(TreeNode root, int k) {
        this.k = k;
        dfs(root);
        return answer;
    }

    private State dfs(TreeNode node) {
        if (node == null) {
            return new State(0, new ArrayList<>());
        }

        State left = dfs(node.left);
        State right = dfs(node.right);

        int size = left.size + right.size + 1;

        List<Integer> merged = new ArrayList<>();
        merged.addAll(left.smallest);
        merged.addAll(right.smallest);
        merged.add(node.val);
        Collections.sort(merged);

        if (merged.size() > k) {
            merged = new ArrayList<>(merged.subList(0, k));
        }

        if (size >= k && merged.size() >= k && node.val > merged.get(k - 1)) {
            answer++;
        }

        return new State(size, merged);
    }
}
```

## Complexity

- **Time:** `O(n)` because `k <= 10`
- **Space:** `O(h)` recursion stack, worst-case `O(n)`

This is the cleanest and most efficient solution for the problem.
