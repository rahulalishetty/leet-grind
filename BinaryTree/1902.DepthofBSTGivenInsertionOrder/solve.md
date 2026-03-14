# 1902. Depth of BST Given Insertion Order

## Problem summary

We are given a permutation `order` of integers from `1` to `n`.

These values are inserted into a Binary Search Tree in exactly this order:

- `order[0]` becomes the root
- every later value is inserted using normal BST insertion rules

We must return the **depth** of the final BST.

Here, depth means:

> the number of nodes on the longest root-to-leaf path

So if the longest path has 3 nodes, the answer is `3`.

---

# Key observation

If we literally build the BST node by node and then compute its depth, that works logically, but for `n = 10^5`, naive insertion into an unbalanced BST can take:

```text
O(n^2)
```

So we need something better.

The most important idea is:

When a value `x` is inserted into the BST, its parent is one of its **nearest already-inserted neighbors** in sorted order.

More precisely:

- among the values already inserted, find:
  - predecessor of `x`
  - successor of `x`
- the depth of `x` is:

```text
1 + max(depth(predecessor), depth(successor))
```

where missing predecessor/successor contribute `0`.

This is the foundation of the optimal approach.

---

# Why predecessor/successor determine the parent

Suppose some values are already inserted.

When inserting a new value `x`:

- in BST search, the path depends on comparisons against already existing values
- eventually, `x` lands under the closest boundary value that blocks it from the left or right
- these boundaries are exactly its predecessor and successor in the set of inserted values

The actual parent is whichever of those two was inserted later on the search path, but for depth computation, the formula

```text
depth[x] = 1 + max(depth[pred], depth[succ])
```

is enough.

---

# Approach 1: Naive explicit BST construction

## Intuition

The most straightforward solution is:

1. Build the BST exactly as described
2. Compute its maximum depth

This is easy to understand and easy to implement.

However, it is not efficient for worst-case inputs like:

```text
[1,2,3,4,5,...]
```

because the BST becomes completely skewed.

---

## Algorithm

### Step 1: Insert values into BST

For each value:

- start from root
- move left or right until a null child is found
- insert there

### Step 2: Compute depth

Run DFS:

```text
depth(node) = 1 + max(depth(node.left), depth(node.right))
```

---

## Java code

```java
class Solution {
    static class TreeNode {
        int val;
        TreeNode left, right;

        TreeNode(int val) {
            this.val = val;
        }
    }

    public int maxDepthBST(int[] order) {
        TreeNode root = null;

        for (int val : order) {
            root = insert(root, val);
        }

        return getDepth(root);
    }

    private TreeNode insert(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }

        TreeNode curr = root;
        while (true) {
            if (val < curr.val) {
                if (curr.left == null) {
                    curr.left = new TreeNode(val);
                    break;
                }
                curr = curr.left;
            } else {
                if (curr.right == null) {
                    curr.right = new TreeNode(val);
                    break;
                }
                curr = curr.right;
            }
        }

        return root;
    }

    private int getDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }

        return 1 + Math.max(getDepth(root.left), getDepth(root.right));
    }
}
```

---

## Complexity

### Time complexity

Worst case:

```text
O(n^2)
```

because insertion into a skewed BST costs `O(n)` each time.

### Space complexity

```text
O(n)
```

for the tree itself and recursion stack in the worst case.

---

## Verdict

This approach is useful for understanding the problem, but it is too slow for the largest constraints.

---

# Approach 2: Ordered set + depth map (optimal)

## Intuition

We do **not** need to explicitly build the tree.

Instead, we only need to know the depth of each inserted value.

For a new value `x`, we find:

- its predecessor among already inserted values
- its successor among already inserted values

Then:

```text
depth[x] = 1 + max(depth[pred], depth[succ])
```

If predecessor or successor does not exist, treat its depth as `0`.

We maintain:

1. a sorted set of inserted values
2. a map from value -> depth

In Java, `TreeSet` gives us:

- `lower(x)` → predecessor
- `higher(x)` → successor

---

## Why this works

At insertion time, all previous elements are already placed in the BST.

The eventual insertion point for `x` lies between its closest existing smaller and larger values.

Those two values are the only candidates that can determine its attachment depth.

Thus the new node’s depth is one more than the deeper of those two boundaries.

---

## Algorithm

1. Initialize:
   - `TreeSet<Integer> set`
   - `HashMap<Integer, Integer> depth`
2. Insert the first value:
   - depth = 1
3. For each later value `x`:
   - `pred = set.lower(x)`
   - `succ = set.higher(x)`
   - `leftDepth = depth.getOrDefault(pred, 0)`
   - `rightDepth = depth.getOrDefault(succ, 0)`
   - `currDepth = 1 + max(leftDepth, rightDepth)`
   - save `depth[x] = currDepth`
   - add `x` to `set`
   - update answer
4. Return maximum depth seen

---

## Java code

```java
import java.util.HashMap;
import java.util.TreeSet;

class Solution {
    public int maxDepthBST(int[] order) {
        TreeSet<Integer> set = new TreeSet<>();
        HashMap<Integer, Integer> depth = new HashMap<>();

        int ans = 1;

        set.add(order[0]);
        depth.put(order[0], 1);

        for (int i = 1; i < order.length; i++) {
            int x = order[i];

            Integer pred = set.lower(x);
            Integer succ = set.higher(x);

            int leftDepth = (pred == null) ? 0 : depth.get(pred);
            int rightDepth = (succ == null) ? 0 : depth.get(succ);

            int currDepth = 1 + Math.max(leftDepth, rightDepth);

            depth.put(x, currDepth);
            set.add(x);

            ans = Math.max(ans, currDepth);
        }

        return ans;
    }
}
```

---

## Complexity

### Time complexity

Each insertion does:

- one `lower`
- one `higher`
- one `add`

All in `TreeSet`, each costing:

```text
O(log n)
```

So total:

```text
O(n log n)
```

### Space complexity

```text
O(n)
```

for the set and depth map.

---

## This is the recommended solution

It is efficient enough for `n = 10^5` and is the standard optimal approach.

---

# Approach 3: Ordered map / interval-thinking variant

## Intuition

Another way to think about the same optimal idea is via intervals.

Before inserting a value `x`, it belongs inside some sorted interval between already-inserted values.

The two interval boundaries determine how deep `x` must be placed.

This leads to essentially the same implementation as Approach 2, but conceptually through interval boundaries rather than “parent in BST”.

In Java, the most convenient structure is still `TreeSet`, so implementation remains similar.

Still, this viewpoint is helpful:

- inserted values partition the number line
- a new number lands into one gap
- the two neighboring inserted values define its depth

So this is more of a conceptual reframing than a fundamentally different code path.

---

## Java code

```java
import java.util.HashMap;
import java.util.TreeSet;

class Solution {
    public int maxDepthBST(int[] order) {
        TreeSet<Integer> inserted = new TreeSet<>();
        HashMap<Integer, Integer> level = new HashMap<>();

        inserted.add(order[0]);
        level.put(order[0], 1);

        int best = 1;

        for (int i = 1; i < order.length; i++) {
            int val = order[i];

            Integer leftBoundary = inserted.lower(val);
            Integer rightBoundary = inserted.higher(val);

            int depthFromLeft = leftBoundary == null ? 0 : level.get(leftBoundary);
            int depthFromRight = rightBoundary == null ? 0 : level.get(rightBoundary);

            int curr = Math.max(depthFromLeft, depthFromRight) + 1;

            level.put(val, curr);
            inserted.add(val);
            best = Math.max(best, curr);
        }

        return best;
    }
}
```

This is algorithmically the same as Approach 2.

---

# Approach 4: Recursive divide-and-conquer on insertion segments (educational, not best)

## Intuition

A BST insertion order also defines recursive subproblems:

- first element is root
- values smaller than root belong to left subtree
- values larger than root belong to right subtree

So we could recursively split the insertion order into left and right subsequences and compute subtree depths.

For example:

```text
order = [2,1,4,3]
root = 2
left part = [1]
right part = [4,3]
```

Then:

```text
depth = 1 + max(depth(left), depth(right))
```

This is conceptually elegant because it mirrors the BST structure directly.

However, naive partitioning at each recursive step can lead to:

```text
O(n^2)
```

time.

---

## Algorithm

1. The first element of the current list is subtree root
2. Partition remaining values into:
   - values less than root
   - values greater than root
3. Recursively compute left subtree depth
4. Recursively compute right subtree depth
5. Return:

```text
1 + max(leftDepth, rightDepth)
```

---

## Java code

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public int maxDepthBST(int[] order) {
        List<Integer> nums = new ArrayList<>();
        for (int x : order) {
            nums.add(x);
        }
        return solve(nums);
    }

    private int solve(List<Integer> nums) {
        if (nums.isEmpty()) {
            return 0;
        }

        int root = nums.get(0);
        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();

        for (int i = 1; i < nums.size(); i++) {
            if (nums.get(i) < root) {
                left.add(nums.get(i));
            } else {
                right.add(nums.get(i));
            }
        }

        return 1 + Math.max(solve(left), solve(right));
    }
}
```

---

## Complexity

### Time complexity

Worst case:

```text
O(n^2)
```

because each recursive step may scan almost the whole remaining list.

### Space complexity

```text
O(n^2)
```

in the worst case if lots of new lists are created across recursion.

---

## Verdict

This approach is useful for understanding the recursive structure of BST insertion order, but not good for large inputs.

---

# Comparison of approaches

## Approach 1: Explicit BST build

- simple
- intuitive
- too slow in worst case

## Approach 2: TreeSet + depth map

- optimal
- clean
- best practical solution

## Approach 3: Same optimal logic, interval viewpoint

- same complexity as Approach 2
- useful alternative explanation

## Approach 4: Recursive partitioning

- elegant conceptually
- inefficient

---

# Why Approach 2 is best

The problem only asks for the **depth**, not the actual tree.

So explicitly building the BST wastes work.

Approach 2 tracks exactly the information we need:

- which values are already inserted
- what their depths are

Using predecessor and successor in sorted order gives the new node’s depth directly.

That yields:

```text
O(n log n)
```

which is efficient for `n = 10^5`.

---

# Final recommended solution

```java
import java.util.HashMap;
import java.util.TreeSet;

class Solution {
    public int maxDepthBST(int[] order) {
        TreeSet<Integer> set = new TreeSet<>();
        HashMap<Integer, Integer> depth = new HashMap<>();

        set.add(order[0]);
        depth.put(order[0], 1);

        int ans = 1;

        for (int i = 1; i < order.length; i++) {
            int x = order[i];

            Integer pred = set.lower(x);
            Integer succ = set.higher(x);

            int predDepth = (pred == null) ? 0 : depth.get(pred);
            int succDepth = (succ == null) ? 0 : depth.get(succ);

            int currDepth = 1 + Math.max(predDepth, succDepth);

            depth.put(x, currDepth);
            set.add(x);

            ans = Math.max(ans, currDepth);
        }

        return ans;
    }
}
```

### Complexity

- **Time:** `O(n log n)`
- **Space:** `O(n)`

This is the best solution for the given constraints.
