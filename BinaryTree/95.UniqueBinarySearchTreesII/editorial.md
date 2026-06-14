# 95. Unique Binary Search Trees II

**Difficulty:** Medium

---

## Problem Summary

Given an integer `n`, return **all structurally unique BSTs** with exactly `n` nodes having unique values from `1` to `n`.

---

## Core Insight

For any value `i` chosen as the **root**:

- All values `[1 ... i-1]` go into the **left subtree**
- All values `[i+1 ... n]` go into the **right subtree**

By recursively applying this rule across every possible root choice, we generate all valid BSTs.

---

## Approach 1: Recursive (Divide & Conquer)

### Explanation

For a range `[start, end]`:

1. Try every value `i` in `[start, end]` as the root.
2. Recursively generate all left subtrees from `[start, i-1]`.
3. Recursively generate all right subtrees from `[i+1, end]`.
4. Combine every left subtree with every right subtree under root `i`.

**Base case:** If `start > end`, return a list containing `null` (empty subtree).

**Time Complexity:** O(Catalan(n)) — the number of unique BSTs is the nth Catalan number.
**Space Complexity:** O(Catalan(n)) for storing all trees.

### Java Code

```java
import java.util.*;

class Solution {
    public List<TreeNode> generateTrees(int n) {
        if (n == 0) return new ArrayList<>();
        return generate(1, n);
    }

    private List<TreeNode> generate(int start, int end) {
        List<TreeNode> result = new ArrayList<>();

        // Base case: empty subtree
        if (start > end) {
            result.add(null);
            return result;
        }

        // Try each value as root
        for (int i = start; i <= end; i++) {
            List<TreeNode> leftTrees  = generate(start, i - 1);
            List<TreeNode> rightTrees = generate(i + 1, end);

            // Combine every left with every right
            for (TreeNode left : leftTrees) {
                for (TreeNode right : rightTrees) {
                    TreeNode root = new TreeNode(i);
                    root.left  = left;
                    root.right = right;
                    result.add(root);
                }
            }
        }
        return result;
    }
}
```

---

## Approach 2: Recursive with Memoization (Top-Down DP)

### Explanation

The recursive approach recomputes the same `(start, end)` subproblems multiple times. We use a `HashMap` keyed on `"start,end"` to cache results.

> Example: The subproblem for range `[1, 2]` appears identically whether it's a left subtree of `3` in `[1,3]` or a left subtree in a larger range — same structure, same result.

**Time Complexity:** O(Catalan(n)) unique subproblems, each computed once.
**Space Complexity:** O(n²) cache entries × O(Catalan(n)) trees per entry.

### Java Code

```java
import java.util.*;

class Solution {
    private Map<String, List<TreeNode>> memo = new HashMap<>();

    public List<TreeNode> generateTrees(int n) {
        if (n == 0) return new ArrayList<>();
        return generate(1, n);
    }

    private List<TreeNode> generate(int start, int end) {
        String key = start + "," + end;
        if (memo.containsKey(key)) return memo.get(key);

        List<TreeNode> result = new ArrayList<>();

        if (start > end) {
            result.add(null);
            memo.put(key, result);
            return result;
        }

        for (int i = start; i <= end; i++) {
            List<TreeNode> leftTrees  = generate(start, i - 1);
            List<TreeNode> rightTrees = generate(i + 1, end);

            for (TreeNode left : leftTrees) {
                for (TreeNode right : rightTrees) {
                    TreeNode root = new TreeNode(i);
                    root.left  = left;
                    root.right = right;
                    result.add(root);
                }
            }
        }

        memo.put(key, result);
        return result;
    }
}
```

---

## Approach 3: Bottom-Up Dynamic Programming (Iterative)

### Explanation

Instead of recursing top-down, we build solutions for all ranges of increasing **length**:

- Length 1: single-node trees `{1}`, `{2}`, ..., `{n}`
- Length 2: trees over `[1,2]`, `[2,3]`, ..., `[n-1,n]`
- ...and so on up to length `n`.

We store results in a 2D table `dp[start][end]` = list of all trees for that range.

**Note:** Since node values shift between ranges (e.g., `[1,2]` vs `[3,4]`), we clone and offset nodes when reusing subtrees from a cached range.

**Time Complexity:** O(n² × Catalan(n))
**Space Complexity:** O(n² × Catalan(n))

### Java Code

```java
import java.util.*;

class Solution {
    public List<TreeNode> generateTrees(int n) {
        if (n == 0) return new ArrayList<>();

        // dp[i][j] = all BSTs for values [i..j]
        @SuppressWarnings("unchecked")
        List<TreeNode>[][] dp = new List[n + 2][n + 2];

        // Base case: empty range
        for (int i = 1; i <= n + 1; i++) {
            dp[i][i - 1] = new ArrayList<>();
            dp[i][i - 1].add(null);
        }

        // Fill by increasing length
        for (int len = 1; len <= n; len++) {
            for (int start = 1; start <= n - len + 1; start++) {
                int end = start + len - 1;
                dp[start][end] = new ArrayList<>();

                for (int root = start; root <= end; root++) {
                    for (TreeNode left : dp[start][root - 1]) {
                        for (TreeNode right : dp[root + 1][end]) {
                            TreeNode node = new TreeNode(root);
                            node.left  = left;
                            node.right = right;
                            dp[start][end].add(node);
                        }
                    }
                }
            }
        }

        return dp[1][n];
    }
}
```

---

## Approach 4: Clone + Offset (Reuse Subtrees Across Ranges)

### Explanation

Notice that the **structure** of BSTs for `[1, k]` is the same as for `[m+1, m+k]` — only the values differ by offset `m`. We can generate trees for `[1, k]` once, then **clone and shift** node values by `m` to reuse them for any range starting at `m+1`.

This avoids redundant structural generation by separating structure from values.

**Steps:**

1. Build `dp[0..k]` = all BST structures for `k` nodes.
2. For range `[start, end]`, clone the trees from `dp[end - start + 1]` and offset values by `start - 1`.

### Java Code

```java
import java.util.*;

class Solution {
    public List<TreeNode> generateTrees(int n) {
        if (n == 0) return new ArrayList<>();

        // dp[k] = all unique BST structures for k nodes (values 1..k)
        @SuppressWarnings("unchecked")
        List<TreeNode>[] dp = new List[n + 1];
        dp[0] = new ArrayList<>();
        dp[0].add(null);

        for (int k = 1; k <= n; k++) {
            dp[k] = new ArrayList<>();
            for (int root = 1; root <= k; root++) {
                for (TreeNode left : dp[root - 1]) {
                    for (TreeNode right : dp[k - root]) {
                        TreeNode node = new TreeNode(root);
                        node.left  = cloneWithOffset(left,  0);
                        node.right = cloneWithOffset(right, root);
                        dp[k].add(node);
                    }
                }
            }
        }

        return dp[n];
    }

    // Deep-clone a subtree, adding `offset` to each node's value
    private TreeNode cloneWithOffset(TreeNode node, int offset) {
        if (node == null) return null;
        TreeNode clone = new TreeNode(node.val + offset);
        clone.left  = cloneWithOffset(node.left,  offset);
        clone.right = cloneWithOffset(node.right, offset);
        return clone;
    }
}
```

---

## TreeNode Definition (for reference)

```java
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val   = val;
        this.left  = left;
        this.right = right;
    }
}
```

---

## Comparison of Approaches

| Approach              | Strategy         | Time             | Space              | Notes                                    |
| --------------------- | ---------------- | ---------------- | ------------------ | ---------------------------------------- |
| **1. Recursive**      | Divide & Conquer | O(Catalan(n))    | O(Catalan(n))      | Simplest; recomputes subproblems         |
| **2. Memoization**    | Top-Down DP      | O(Catalan(n))    | O(n² + Catalan(n)) | Avoids redundant computation             |
| **3. Bottom-Up DP**   | Iterative DP     | O(n²·Catalan(n)) | O(n²·Catalan(n))   | Explicit table; no recursion             |
| **4. Clone + Offset** | Structure Reuse  | O(Catalan(n))    | O(Catalan(n))      | Elegant; separates structure from values |

> **Catalan(n)** for `n=8` is **1430** — the maximum output size given the constraint `1 ≤ n ≤ 8`.

---

## Walkthrough: n = 3

Choosing root `2`:

- Left subtree: trees over `[1, 1]` → single node `{1}`
- Right subtree: trees over `[3, 3]` → single node `{3}`
- Result: one tree `2 → (1, 3)`

All 5 trees for `n = 3`:

```
Root=1:        Root=1:        Root=2:    Root=3:        Root=3:
  1              1              2          3              3
   \              \            / \        /              /
    2              3          1   3      1              2
     \            /                      \             /
      3           2                       2           1
```

**Recommended approach:** Start with **Approach 1** for clarity, upgrade to **Approach 2** (memoization) for efficiency, or use **Approach 4** if you want the most elegant structural reuse.
