# Find Duplicate Subtrees — Java Solutions

**LeetCode 652** · Given the `root` of a binary tree, return the root nodes of all **duplicate subtrees**. Two subtrees are duplicates if they have the same structure and the same node values.

---

## Problem Intuition

To detect duplicate subtrees, we need a way to **uniquely represent** every subtree and then **count** how many times each representation appears. The key insight: **serialize each subtree into a string** — if two subtrees produce the same string, they are duplicates.

---

## Approach 1 — Postorder Serialization + HashMap

### Idea

Do a **postorder traversal** (left → right → root). At each node, build a string that encodes the subtree rooted there. Store each serialized string in a `HashMap<String, Integer>` counting occurrences. When a subtree's serialization is seen for the **second time**, add that node to the result list.

```
Serialization of a node:
  serialize(node) = serialize(left) + "," + serialize(right) + "," + node.val
  null nodes     = "#"
```

### Example

```
Tree:
        1
       / \
      2   3
     /   / \
    4   2   4
       /
      4

Serializations:
  Node(4)  → "4,#,#"           (appears 3 times → add 2nd occurrence)
  Node(2 left)  → "2,4,#,#,#" → "2,4##,#"
  Node(2 right) → "2,4,#,#,#" → same → duplicate!
```

### Code

```java
import java.util.*;

class Solution {
    // Approach 1: Postorder Serialization + HashMap
    public List<TreeNode> findDuplicateSubtrees(TreeNode root) {
        List<TreeNode> result = new ArrayList<>();
        Map<String, Integer> count = new HashMap<>();
        serialize(root, count, result);
        return result;
    }

    private String serialize(TreeNode node,
                             Map<String, Integer> count,
                             List<TreeNode> result) {
        if (node == null) return "#";

        // Postorder: build serialization bottom-up
        String left  = serialize(node.left,  count, result);
        String right = serialize(node.right, count, result);
        String key   = left + "," + right + "," + node.val;

        // Second occurrence → it's a duplicate
        count.merge(key, 1, Integer::sum);
        if (count.get(key) == 2) {
            result.add(node);
        }

        return key;
    }
}
```

### Complexity

|       | Value                                                 |
| ----- | ----------------------------------------------------- |
| Time  | O(n²) — string concatenation costs O(n) per node      |
| Space | O(n²) — storing up to n unique strings of length O(n) |

---

## Approach 2 — Serialization + Canonical ID (Optimized HashMap)

### Idea

String keys can be expensive. Instead, **assign an integer ID** to each unique serialization using a second map (`serialize → id`). Then count by integer ID, which is O(1) per lookup/comparison instead of O(n).

This reduces average-case time and space significantly.

### Code

```java
import java.util.*;

class Solution {
    // Approach 2: Serialization with Integer ID mapping
    Map<String, Integer> serialToId = new HashMap<>();
    Map<Integer, Integer> idCount   = new HashMap<>();
    List<TreeNode> result = new ArrayList<>();

    public List<TreeNode> findDuplicateSubtrees(TreeNode root) {
        encode(root);
        return result;
    }

    private int encode(TreeNode node) {
        if (node == null) return 0; // 0 = null sentinel

        int leftId  = encode(node.left);
        int rightId = encode(node.right);

        // Build canonical string for this subtree
        String key = leftId + "," + rightId + "," + node.val;

        // Assign a unique integer ID if not seen before
        int id = serialToId.computeIfAbsent(key, k -> serialToId.size() + 1);

        // Count occurrences of this ID
        idCount.merge(id, 1, Integer::sum);
        if (idCount.get(id) == 2) {
            result.add(node);
        }

        return id;
    }
}
```

### Complexity

|       | Value                                                     |
| ----- | --------------------------------------------------------- |
| Time  | O(n) — each node visited once; map ops are O(1) amortized |
| Space | O(n) — at most n unique IDs                               |

> **This is the recommended approach** — it has optimal time complexity.

---

## Approach 3 — Merkle Hashing (Hash-based Fingerprint)

### Idea

Instead of storing the full serialization string, compute a **hash** for each subtree using a Merkle-style formula:

```
hash(node) = hash(left) * P1 + hash(right) * P2 + node.val * P3
```

Using large primes reduces collision probability. Group nodes by hash, then verify actual structure equality only within collision groups.

> ⚠️ Pure hashing alone has collision risk. For production use, add a structural equality check as a secondary confirmation.

### Code

```java
import java.util.*;

class Solution {
    // Approach 3: Merkle Hash fingerprinting
    private static final long P1 = 1_000_000_007L;
    private static final long P2 = 998_244_353L;
    private static final long MOD = (1L << 61) - 1; // Mersenne prime

    Map<Long, List<TreeNode>> hashToNodes = new HashMap<>();
    Set<Long> seen = new HashSet<>();
    List<TreeNode> result = new ArrayList<>();

    public List<TreeNode> findDuplicateSubtrees(TreeNode root) {
        merkleHash(root);
        return result;
    }

    private long merkleHash(TreeNode node) {
        if (node == null) return 0L;

        long leftHash  = merkleHash(node.left);
        long rightHash = merkleHash(node.right);

        // Combine hashes with the node's value
        long h = (leftHash * P1 % MOD
                + rightHash * P2 % MOD
                + node.val * 1_000_003L % MOD) % MOD;

        hashToNodes.computeIfAbsent(h, k -> new ArrayList<>()).add(node);

        // When second node shares the hash, record as duplicate
        if (hashToNodes.get(h).size() == 2 && !seen.contains(h)) {
            result.add(node);
            seen.add(h);
        }

        return h;
    }
}
```

### Complexity

|       | Value                                                 |
| ----- | ----------------------------------------------------- |
| Time  | O(n) — single pass with constant-time hash operations |
| Space | O(n) — hash map storing all nodes                     |

---

## Approach 4 — Brute Force (Naive — for understanding only)

### Idea

For every pair of nodes `(u, v)` where `u ≠ v`, check if the subtrees rooted at `u` and `v` are identical using a recursive `isSameTree` helper. Collect one representative per duplicate group.

This is **not efficient** but builds intuition.

### Code

```java
import java.util.*;

class Solution {
    // Approach 4: Brute Force O(n² × n)
    public List<TreeNode> findDuplicateSubtrees(TreeNode root) {
        List<TreeNode> all    = new ArrayList<>();
        List<TreeNode> result = new ArrayList<>();
        collectAll(root, all);

        boolean[] added = new boolean[all.size()];

        for (int i = 0; i < all.size(); i++) {
            if (added[i]) continue;
            boolean foundDup = false;
            for (int j = i + 1; j < all.size(); j++) {
                if (!added[j] && isSameTree(all.get(i), all.get(j))) {
                    added[j] = true;
                    foundDup  = true;
                }
            }
            if (foundDup) {
                result.add(all.get(i));
            }
        }
        return result;
    }

    private void collectAll(TreeNode node, List<TreeNode> all) {
        if (node == null) return;
        all.add(node);
        collectAll(node.left,  all);
        collectAll(node.right, all);
    }

    private boolean isSameTree(TreeNode a, TreeNode b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.val == b.val
            && isSameTree(a.left,  b.left)
            && isSameTree(a.right, b.right);
    }
}
```

### Complexity

|       | Value                                                          |
| ----- | -------------------------------------------------------------- |
| Time  | O(n² × n) = O(n³) — comparing every pair, each comparison O(n) |
| Space | O(n) — storing all nodes                                       |

---

## TreeNode Definition (for reference)

```java
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) { this.val = val; }
}
```

---

## Approach Comparison

| Approach                           | Time  | Space | Notes                                               |
| ---------------------------------- | ----- | ----- | --------------------------------------------------- |
| 1 — Postorder String Serialization | O(n²) | O(n²) | Simple and correct; slow due to string ops          |
| 2 — Serialization + Integer ID ✅  | O(n)  | O(n)  | **Best overall** — fast and collision-free          |
| 3 — Merkle Hash Fingerprint        | O(n)  | O(n)  | Very fast; tiny collision risk without verification |
| 4 — Brute Force                    | O(n³) | O(n)  | Educational only; too slow for large inputs         |

---

## Key Takeaways

- **Postorder traversal** is essential — you must know a node's subtree structure before processing the node itself.
- **Serialization** gives a unique, collision-free identity to every subtree.
- **Integer ID mapping** (Approach 2) eliminates costly string comparisons while keeping correctness.
- **Hashing** (Approach 3) is fast but requires care around collisions in adversarial inputs.
- The pattern of `count == 2` ensures each duplicate group contributes **exactly one** node to the result — never zero, never more.
