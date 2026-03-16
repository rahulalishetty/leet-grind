# 1932. Merge BSTs to Create Single BST

## Problem Restatement

We are given `n` separate BST roots in an array `trees`.

Each individual tree:

- is already a valid BST
- has at most 3 nodes
- has a unique root value across the whole input

We may merge one tree into another if:

- some **leaf** in one tree has the same value as the **root** of another tree

When we do that:

- the leaf is replaced by the second tree
- the second tree disappears from the forest

We must determine whether all trees can be merged into exactly one valid BST.

If possible, return the final root.
Otherwise, return `null`.

---

# High-Level Insight

This problem has two distinct parts:

1. **Figure out which root can become the final root of the merged tree**
2. **Merge the trees while verifying that the final structure is still a valid BST**

The tricky part is that not every syntactically possible merge produces a valid BST globally.

A local leaf match is not enough.
We must also satisfy the BST range constraints all the way through the final merged tree.

---

# Key Structural Observations

## Observation 1: Exactly one final root must survive

Every merge removes one tree root from the forest.

If we start with `n` trees and want one final tree, then:

- exactly `n - 1` roots must be consumed
- exactly one root must never appear as another tree’s leaf-merge target

So the final root must be a tree root value that **never appears as any leaf value** in the whole input.

If there is not exactly one such root, the answer is impossible.

---

## Observation 2: Leaf values are the only merge points

A root can only be attached where a matching leaf exists.

Since each tree has at most 3 nodes, every merge point is extremely localized.

That suggests a map-based approach:

- map root value → tree root
- when traversing the chosen final root, if we encounter a leaf whose value matches another root value, splice that tree in

---

## Observation 3: Global BST validation is required

Even if values match for merging, the resulting full tree may violate BST rules.

Example:

```text
trees = [[5,3,8],[3,2,6]]
```

You can merge tree rooted at `3` into the left leaf `3` of tree rooted at `5`, but then node `6` ends up inside the left subtree of `5`, which is illegal because `6 > 5`.

So every merge must be checked against a valid BST range.

This means that while traversing the merged tree, each node must satisfy:

```text
lower < node.val < upper
```

---

# Approach 1: Brute Force Graph/Simulation Mindset

## Intuition

A very direct way to think about the problem is:

- try all candidate roots
- repeatedly merge matching leaf-root pairs
- after finishing, validate whether the result is one valid BST using all trees

This is a useful thought process, but it is not practical for large `n`.

Still, it helps reveal what information the optimal solution needs.

---

## Why It Is Too Slow

If we attempt to simulate many merge orders or repeatedly scan for matching leaves and roots, we may revisit trees many times.

With up to:

```text
n = 5 * 10^4
```

we need something close to linear time.

So brute force is mainly useful as intuition, not as the final solution.

---

## Brute Force Skeleton Idea

1. Choose a candidate root.
2. Search all current leaves for merge opportunities.
3. Merge repeatedly.
4. At the end:
   - check that all trees were used
   - check that the final tree is a valid BST

This can degenerate badly if implemented naively.

---

## Verdict

Not recommended for implementation under constraints.

We need a one-pass merge plus validation strategy.

---

# Approach 2: HashMap + DFS Range Validation (Optimal)

## Intuition

This is the standard optimal solution.

The idea is:

1. Count all leaf values across all trees.
2. Identify the unique root that is **not** someone else’s leaf.
3. Store every root in a map:
   ```text
   root value -> tree root
   ```
4. DFS from the chosen final root, carrying BST bounds.
5. Whenever we visit a **leaf** whose value matches another unused root, splice that tree in at that point.
6. Continue validation.
7. At the end, ensure all trees were consumed.

This combines merging and BST verification in a single traversal.

---

## Why the Candidate Root Can Be Found This Way

Suppose a root value appears as a leaf somewhere else.

Then that tree can potentially be merged into another tree, so it cannot be the final surviving root.

Therefore, the final root must be a root value that never appears in the leaf multiset.

If there is no such root, or more than one such root, we cannot get exactly one final merged BST.

---

## Core Data Structures

We maintain:

### 1. `rootMap`

Maps root value to its tree root.

```text
value -> TreeNode
```

### 2. `leafCount`

Counts how many times each value appears as a leaf in the input forest.

### 3. Global usage tracking

When a root tree is merged into another place, we remove or mark it as used so it cannot be reused.

---

## DFS Merge Rule

During DFS at a node:

- validate:
  ```text
  low < node.val < high
  ```
- if the current node is a leaf
- and its value matches an unused root in `rootMap`
- and that root is not the original same node
- then replace the leaf logically by traversing into that root tree

This effectively merges trees on demand.

---

## Important Subtlety

A leaf with value `x` might belong to a tree whose root also has value `x`.

We should not incorrectly “merge the tree into itself.”

So in implementation, the starting root remains the chosen root, and other roots are consumed from the map as they get merged.

A common clean trick is:

- keep all roots in the map initially
- remove the chosen final root before DFS
- whenever a matching leaf value is found, remove that root from the map and recurse into it

At the end:

- if the map is empty, all trees were used exactly once
- otherwise some trees were never merged

---

## Java Code

```java
import java.util.*;

class Solution {
    public TreeNode canMerge(List<TreeNode> trees) {
        Map<Integer, TreeNode> roots = new HashMap<>();
        Map<Integer, Integer> leafCount = new HashMap<>();

        for (TreeNode root : trees) {
            roots.put(root.val, root);

            if (root.left != null) {
                leafCount.put(root.left.val, leafCount.getOrDefault(root.left.val, 0) + 1);
            }
            if (root.right != null) {
                leafCount.put(root.right.val, leafCount.getOrDefault(root.right.val, 0) + 1);
            }
        }

        TreeNode start = null;
        for (TreeNode root : trees) {
            if (!leafCount.containsKey(root.val)) {
                if (start != null) return null; // more than one possible final root
                start = root;
            }
        }

        if (start == null) return null;

        roots.remove(start.val);

        if (!dfs(start, Long.MIN_VALUE, Long.MAX_VALUE, roots)) {
            return null;
        }

        return roots.isEmpty() ? start : null;
    }

    private boolean dfs(TreeNode node, long low, long high, Map<Integer, TreeNode> roots) {
        if (node == null) return true;

        if (node.val <= low || node.val >= high) return false;

        // If node is a leaf and there exists a tree with the same root value,
        // merge that tree here.
        if (node.left == null && node.right == null && roots.containsKey(node.val)) {
            TreeNode merged = roots.remove(node.val);
            node.left = merged.left;
            node.right = merged.right;
        }

        return dfs(node.left, low, node.val, roots) &&
               dfs(node.right, node.val, high, roots);
    }
}
```

---

## Detailed Walkthrough

### Step 1: Build maps

```java
Map<Integer, TreeNode> roots = new HashMap<>();
Map<Integer, Integer> leafCount = new HashMap<>();
```

- `roots` stores every tree root by value
- `leafCount` records all leaf values appearing in the forest

Since each input tree has at most 3 nodes, checking leaves is simple:

- if `left != null`, that child is necessarily a leaf or a child with no grandchildren by constraint
- similarly for `right`

---

### Step 2: Find the unique final root

```java
for (TreeNode root : trees) {
    if (!leafCount.containsKey(root.val)) {
        if (start != null) return null;
        start = root;
    }
}
```

A valid final root must not appear as any leaf value elsewhere.

If more than one such root exists, we would end with multiple disconnected components.
If none exists, every root is consumable, so no unique final root survives.

Either case means impossible.

---

### Step 3: Remove chosen root from merge pool

```java
roots.remove(start.val);
```

The final root is not something to be merged into a parent, so remove it from the map of mergeable trees.

---

### Step 4: DFS with BST bounds

```java
if (node.val <= low || node.val >= high) return false;
```

This ensures global BST validity.

---

### Step 5: Merge when encountering a matching leaf

```java
if (node.left == null && node.right == null && roots.containsKey(node.val)) {
    TreeNode merged = roots.remove(node.val);
    node.left = merged.left;
    node.right = merged.right;
}
```

This is the splice operation.

The leaf node already has the correct root value.
So to replace that leaf by the corresponding tree, we only need to graft that tree’s children onto this node.

We do **not** create a new node.
We reuse the leaf node and attach the merged tree’s left and right children.

---

### Step 6: Recurse into children with updated bounds

```java
return dfs(node.left, low, node.val, roots) &&
       dfs(node.right, node.val, high, roots);
```

This maintains BST validity through the entire merged structure.

---

### Step 7: Ensure all trees were consumed

```java
return roots.isEmpty() ? start : null;
```

If some roots remain unused, then not all trees were merged into the final BST.

So the answer must be `null`.

---

## Example 1 Walkthrough

Input:

```text
trees = [[2,1],[3,2,5],[5,4]]
```

Roots:

- 2
- 3
- 5

Leaf values:

- from tree 2: 1
- from tree 3: 2, 5
- from tree 5: 4

So root `3` is the only root not appearing as a leaf.
Thus `3` must be the final root.

Start DFS from 3:

- visit left leaf 2
- 2 matches another root → merge tree rooted at 2 there
- visit right leaf 5
- 5 matches another root → merge tree rooted at 5 there

Final tree becomes:

```text
      3
     / \
    2   5
   /   /
  1   4
```

This is a valid BST and all roots were consumed.

---

## Example 2 Walkthrough

Input:

```text
trees = [[5,3,8],[3,2,6]]
```

Root `5` is the final root candidate.

Merge root `3` into leaf `3` of tree `5`.

Now subtree under 5.left becomes:

```text
    3
   / \
  2   6
```

But `6` is in the left subtree of `5`, violating BST ordering.

During DFS, when reaching node `6`, bounds are:

```text
low = 3, high = 5
```

Since `6 >= 5`, validation fails and the answer is `null`.

---

## Complexity Analysis

Let `n` be the number of trees.
Each tree has at most 3 nodes, so the total number of nodes is `O(n)`.

### Time Complexity

```text
O(n)
```

Why?

- building maps is linear
- DFS visits each node at most once
- every root is removed from the map at most once

So overall time is linear in the total node count.

### Space Complexity

```text
O(n)
```

Why?

- root map stores up to `n` roots
- leaf count map stores up to `O(n)` values
- recursion stack in worst case can also be linear

---

# Approach 3: Same Idea with Explicit Validation Helper Structure

## Intuition

This is not a fundamentally different algorithm, but a different organization style that some people prefer.

Instead of mutating leaves during a plain DFS, we can think of the process as:

- first identify the starting root
- then perform a validation DFS that “expands” mergeable leaves into stored trees
- while tracking used root values separately

This makes usage accounting more explicit.

---

## Java Code

```java
import java.util.*;

class Solution {
    private Map<Integer, TreeNode> rootMap;
    private Set<Integer> usedRoots;

    public TreeNode canMerge(List<TreeNode> trees) {
        rootMap = new HashMap<>();
        Map<Integer, Integer> leafFreq = new HashMap<>();
        usedRoots = new HashSet<>();

        for (TreeNode root : trees) {
            rootMap.put(root.val, root);
            if (root.left != null) {
                leafFreq.put(root.left.val, leafFreq.getOrDefault(root.left.val, 0) + 1);
            }
            if (root.right != null) {
                leafFreq.put(root.right.val, leafFreq.getOrDefault(root.right.val, 0) + 1);
            }
        }

        TreeNode candidate = null;
        for (TreeNode root : trees) {
            if (!leafFreq.containsKey(root.val)) {
                if (candidate != null) return null;
                candidate = root;
            }
        }

        if (candidate == null) return null;

        usedRoots.add(candidate.val);

        if (!buildAndValidate(candidate, Long.MIN_VALUE, Long.MAX_VALUE)) {
            return null;
        }

        return usedRoots.size() == trees.size() ? candidate : null;
    }

    private boolean buildAndValidate(TreeNode node, long low, long high) {
        if (node == null) return true;
        if (node.val <= low || node.val >= high) return false;

        if (node.left == null && node.right == null) {
            TreeNode attached = rootMap.get(node.val);
            if (attached != null && !usedRoots.contains(attached.val)) {
                usedRoots.add(attached.val);
                node.left = attached.left;
                node.right = attached.right;
            }
        }

        return buildAndValidate(node.left, low, node.val) &&
               buildAndValidate(node.right, node.val, high);
    }
}
```

---

## Notes on This Version

This is essentially the same strategy as Approach 2, but:

- instead of deleting merged roots from the map, it uses `usedRoots`
- final check becomes:
  ```java
  usedRoots.size() == trees.size()
  ```

This style is sometimes easier to reason about if you want to preserve the root map untouched.

---

## Complexity

Same as Approach 2.

### Time

```text
O(n)
```

### Space

```text
O(n)
```

---

# Why Simpler “Local Merge Only” Logic Fails

A tempting but incorrect idea is:

- greedily merge whenever a leaf matches a root
- assume that if all trees can be consumed, the result is valid

That is wrong because local value equality does not ensure global BST validity.

The true condition is not:

```text
leaf value == root value
```

alone.

The true condition is:

- mergeable by value
- and after merging, every descendant of that subtree must remain within the BST bounds inherited from ancestors

So global range validation is mandatory.

---

# Corner Cases

## 1. Multiple possible final roots

If more than one root never appears as a leaf, then the forest cannot become a single connected tree.

Return `null`.

## 2. No possible final root

If every root appears as some leaf, then there is no single surviving root.

Return `null`.

## 3. Merge creates BST violation deep inside

Must be detected by DFS range validation.

## 4. Some trees never get merged

Even if the main DFS forms a valid BST, if unused roots remain, the answer is still `null`.

## 5. Duplicate use of one root

Must be prevented by removing roots from the map or tracking them in a used set.

---

# Comparing the Approaches

## Approach 1: Brute force simulation

- good for intuition
- too slow in practice
- not suitable for constraints

## Approach 2: HashMap + DFS range validation

- optimal
- elegant
- standard interview-quality solution
- recommended

## Approach 3: Same logic with explicit used set

- also optimal
- slightly different state organization
- useful if you prefer explicit root-usage tracking

---

# Which Approach Should You Prefer?

Use **Approach 2**.

It is the cleanest and most standard:

- identify the unique surviving root
- map roots by value
- DFS with BST bounds
- splice matching leaves on demand
- ensure all roots are consumed

That solves both connectivity and BST correctness at once.

---

# Final Takeaway

The heart of the problem is this:

> Merging is easy locally, but correctness is global.

A merge is valid only if the final combined tree remains a BST everywhere.

So the correct solution must combine:

1. **graph-style forest merging**
2. **BST range validation**

The elegant way to do that is one DFS from the unique final root candidate, expanding mergeable leaves as you go and validating bounds at every step.

---

# Final Complexity Summary

Let `n` be the number of trees.
Since each tree has at most 3 nodes, total node count is `O(n)`.

## Optimal solution

- **Time:** `O(n)`
- **Space:** `O(n)`

---

# Recommended Java Solution

```java
import java.util.*;

class Solution {
    public TreeNode canMerge(List<TreeNode> trees) {
        Map<Integer, TreeNode> roots = new HashMap<>();
        Map<Integer, Integer> leafCount = new HashMap<>();

        for (TreeNode root : trees) {
            roots.put(root.val, root);

            if (root.left != null) {
                leafCount.put(root.left.val, leafCount.getOrDefault(root.left.val, 0) + 1);
            }
            if (root.right != null) {
                leafCount.put(root.right.val, leafCount.getOrDefault(root.right.val, 0) + 1);
            }
        }

        TreeNode start = null;
        for (TreeNode root : trees) {
            if (!leafCount.containsKey(root.val)) {
                if (start != null) return null;
                start = root;
            }
        }

        if (start == null) return null;

        roots.remove(start.val);

        if (!dfs(start, Long.MIN_VALUE, Long.MAX_VALUE, roots)) {
            return null;
        }

        return roots.isEmpty() ? start : null;
    }

    private boolean dfs(TreeNode node, long low, long high, Map<Integer, TreeNode> roots) {
        if (node == null) return true;

        if (node.val <= low || node.val >= high) return false;

        if (node.left == null && node.right == null && roots.containsKey(node.val)) {
            TreeNode merged = roots.remove(node.val);
            node.left = merged.left;
            node.right = merged.right;
        }

        return dfs(node.left, low, node.val, roots) &&
               dfs(node.right, node.val, high, roots);
    }
}
```
