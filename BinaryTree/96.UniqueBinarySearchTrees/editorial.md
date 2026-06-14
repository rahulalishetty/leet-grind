# 96. Unique Binary Search Trees

## Problem

Given an integer `n`, return the number of structurally unique BSTs which have exactly `n` nodes of unique values from `1` to `n`.

---

## Key Insight: Catalan Numbers

For a BST with nodes `1..n`, if we pick node `i` as root:

- **Left subtree** has nodes `1..i-1` → `i-1` nodes
- **Right subtree** has nodes `i+1..n` → `n-i` nodes

The total count is the **n-th Catalan number**:

```
G(n) = Σ G(i-1) * G(n-i)   for i = 1 to n
G(0) = 1,  G(1) = 1
```

---

## Approach 1: Recursion (Brute Force)

### Explanation

Directly implement the recurrence. For each possible root `i`, recursively count left and right subtrees and multiply them (product rule). Base case: 0 or 1 nodes → exactly 1 tree.

**Time Complexity:** O(3^n) — exponential due to recomputation
**Space Complexity:** O(n) — recursion stack depth

```java
class Solution {
    public int numTrees(int n) {
        return count(n);
    }

    private int count(int n) {
        // Base case: empty tree or single node has exactly 1 structure
        if (n <= 1) return 1;

        int total = 0;
        for (int i = 1; i <= n; i++) {
            // i is the root; left has (i-1) nodes, right has (n-i) nodes
            int left  = count(i - 1);
            int right = count(n - i);
            total += left * right;
        }
        return total;
    }
}
```

---

## Approach 2: Recursion + Memoization (Top-Down DP)

### Explanation

The recursion recomputes the same sub-problems. For example, `count(3)` is needed multiple times. Cache each result in a `memo[]` array so every sub-problem is solved only once.

**Time Complexity:** O(n²)
**Space Complexity:** O(n) — memo array + stack

```java
class Solution {
    private int[] memo;

    public int numTrees(int n) {
        memo = new int[n + 1];
        return count(n);
    }

    private int count(int n) {
        if (n <= 1) return 1;
        if (memo[n] != 0) return memo[n]; // Return cached result

        int total = 0;
        for (int i = 1; i <= n; i++) {
            total += count(i - 1) * count(n - i);
        }

        memo[n] = total; // Cache before returning
        return memo[n];
    }
}
```

---

## Approach 3: Dynamic Programming (Bottom-Up)

### Explanation

Build the solution iteratively from the ground up, avoiding recursion entirely.

**DP definition:**
`dp[i]` = number of unique BSTs with `i` nodes

**Transition:**

```
dp[0] = 1  (empty tree)
dp[1] = 1  (single node)

dp[i] = Σ dp[j-1] * dp[i-j]   for j = 1 to i
```

We fill `dp[2], dp[3], ..., dp[n]` in order, each building on previously computed values.

**Trace for n = 4:**

| dp[0] | dp[1] | dp[2] | dp[3] | dp[4] |
| ----- | ----- | ----- | ----- | ----- |
| 1     | 1     | 2     | 5     | 14    |

**Time Complexity:** O(n²)
**Space Complexity:** O(n) — dp array

```java
class Solution {
    public int numTrees(int n) {
        int[] dp = new int[n + 1];
        dp[0] = 1; // Empty tree: 1 way
        dp[1] = 1; // Single node: 1 way

        for (int nodes = 2; nodes <= n; nodes++) {
            for (int root = 1; root <= nodes; root++) {
                // root splits into left (root-1 nodes) and right (nodes-root nodes)
                dp[nodes] += dp[root - 1] * dp[nodes - root];
            }
        }

        return dp[n];
    }
}
```

---

## Approach 4: Catalan Number Formula (Math)

### Explanation

The answer is exactly the **n-th Catalan number**, with a closed-form formula:

```
C(n) = C(2n, n) / (n + 1)
     = (2n)! / ((n+1)! * n!)
```

We can compute this iteratively to avoid overflow:

```
C(n) = C(n-1) * 2*(2n-1) / (n+1)
```

**Time Complexity:** O(n)
**Space Complexity:** O(1) — no extra space needed

```java
class Solution {
    public int numTrees(int n) {
        // Compute C(n) = C(n-1) * 2*(2n-1) / (n+1)
        long catalan = 1;
        for (int i = 0; i < n; i++) {
            catalan = catalan * 2 * (2 * i + 1) / (i + 2);
        }
        return (int) catalan;
    }
}
```

> **Why `long`?** Intermediate products like `catalan * 2 * (2*i+1)` can temporarily exceed `int` range before the division, so we use `long` to avoid overflow.

---

## Comparison of All Approaches

| Approach                | Time   | Space | Notes                          |
| ----------------------- | ------ | ----- | ------------------------------ |
| Recursion (Brute Force) | O(3^n) | O(n)  | Simple but very slow           |
| Recursion + Memoization | O(n²)  | O(n)  | Top-down; avoids recomputation |
| Dynamic Programming     | O(n²)  | O(n)  | Bottom-up; clean iteration     |
| Catalan Number (Math)   | O(n)   | O(1)  | Fastest; mathematical elegance |

---

## Walkthrough: n = 3

Possible BSTs with nodes {1, 2, 3}:

```
Root = 1        Root = 2        Root = 3
  1               2               3
   \             / \             /
    2           1   3           2
     \                         /
      3                       1
  (2 trees)   (1 tree)    (2 trees)
```

`G(3) = G(0)*G(2) + G(1)*G(1) + G(2)*G(0) = 1*2 + 1*1 + 2*1 = 5` ✓

---

## Recommended Approach

**For interviews:** Use **Bottom-Up DP** — it's clean, efficient, and easy to explain.
**For production/competitive programming:** Use the **Catalan Number formula** for O(n) time and O(1) space.
