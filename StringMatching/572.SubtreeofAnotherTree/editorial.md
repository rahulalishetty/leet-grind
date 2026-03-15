# 572. Subtree of Another Tree

## Overview

We are given roots of two binary trees:

- `root`
- `subRoot`

Our goal is to determine whether the tree rooted at `subRoot` appears as a **subtree** inside the tree rooted at `root`.

A subtree must match:

- **structure**
- **node values**

exactly.

### Notation

- `N` = number of nodes in the tree rooted at `root`
- `M` = number of nodes in the tree rooted at `subRoot`

For `subRoot` to be a subtree, normally `N ≥ M`, but since we don't know the sizes beforehand, we must still perform comparisons.

---

# Approach 1: Depth First Search (Naive)

## Intuition

Traverse every node of `root`.

For each node, check if the subtree starting there is **identical** to `subRoot`.

This requires solving the classic **Same Tree** problem repeatedly.

### Identical Tree Conditions

Two trees are identical if:

1. Both nodes are `null`
2. Node values are equal
3. Left subtrees are identical
4. Right subtrees are identical

---

## Algorithm

1. Traverse the tree using DFS.
2. For each node:
   - Check if subtree equals `subRoot`
3. If yes → return `true`
4. Otherwise continue searching.

---

## Implementation

```java
class Solution {

    public boolean isSubtree(TreeNode root, TreeNode subRoot) {

        if (root == null) {
            return false;
        }

        if (isIdentical(root, subRoot)) {
            return true;
        }

        return isSubtree(root.left, subRoot) ||
               isSubtree(root.right, subRoot);
    }

    private boolean isIdentical(TreeNode a, TreeNode b) {

        if (a == null || b == null) {
            return a == null && b == null;
        }

        return a.val == b.val
            && isIdentical(a.left, b.left)
            && isIdentical(a.right, b.right);
    }
}
```

### Complexity

Time

```
O(N * M)
```

Space

```
O(N + M)
```

---

# Approach 2: String Matching (Tree Serialization)

## Intuition

This problem resembles **substring search**.

If we serialize both trees into strings, the problem becomes:

```
Is subRoot string a substring of root string?
```

### Key Requirement

The serialization must preserve:

- node values
- tree structure

We do this using **preorder traversal with null markers**.

Example marker:

```
^value for node
# for null
```

---

## Serialization Example

Tree

```
   4
  / \\
 1   2
```

Serialized

```
^4^1##^2##
```

---

## Algorithm

1. Serialize `root` using preorder traversal.
2. Serialize `subRoot`.
3. Use **KMP string matching** to check substring.

---

## Implementation

```java
class Solution {

    public boolean isSubtree(TreeNode root, TreeNode subRoot) {

        StringBuilder rootStr = new StringBuilder();
        serialize(root, rootStr);

        StringBuilder subStr = new StringBuilder();
        serialize(subRoot, subStr);

        return kmp(subStr.toString(), rootStr.toString());
    }

    private void serialize(TreeNode node, StringBuilder sb) {

        if (node == null) {
            sb.append("#");
            return;
        }

        sb.append("^").append(node.val);

        serialize(node.left, sb);
        serialize(node.right, sb);
    }

    private boolean kmp(String needle, String haystack) {

        int m = needle.length();
        int n = haystack.length();

        int[] lps = new int[m];

        int prev = 0;

        for (int i = 1; i < m; ) {

            if (needle.charAt(i) == needle.charAt(prev)) {
                lps[i++] = ++prev;
            } else if (prev != 0) {
                prev = lps[prev - 1];
            } else {
                lps[i++] = 0;
            }
        }

        int i = 0, j = 0;

        while (i < n) {

            if (haystack.charAt(i) == needle.charAt(j)) {
                i++;
                j++;

                if (j == m) return true;
            }
            else if (j != 0) {
                j = lps[j - 1];
            }
            else {
                i++;
            }
        }

        return false;
    }
}
```

### Complexity

Time

```
O(N + M)
```

Space

```
O(N + M)
```

---

# Approach 3: Tree Hashing

## Intuition

Instead of comparing trees directly, compute a **hash value for every subtree**.

If two trees are identical → their hash values are identical.

Thus comparison becomes **O(1)**.

### Hash Strategy

For each node:

```
hash(node) = combine(
    hash(left),
    hash(right),
    node.val
)
```

To reduce collisions, we compute **two hash values (double hashing)**.

---

## Algorithm

1. Traverse `root`
2. Compute hash for every subtree
3. Store hashes in a list/set
4. Compute hash of `subRoot`
5. Check if it exists in stored hashes

---

## Implementation

```java
class Solution {

    final int MOD1 = 1000000007;
    final int MOD2 = 2147483647;

    List<long[]> memo = new ArrayList<>();

    long[] hash(TreeNode node, boolean store) {

        if (node == null) {
            return new long[]{3,7};
        }

        long[] left = hash(node.left, store);
        long[] right = hash(node.right, store);

        long h1 = ((left[0] << 5) + (right[0] << 1) + node.val) % MOD1;
        long h2 = ((left[1] << 7) + (right[1] << 1) + node.val) % MOD2;

        long[] pair = new long[]{h1, h2};

        if (store) memo.add(pair);

        return pair;
    }

    public boolean isSubtree(TreeNode root, TreeNode subRoot) {

        hash(root, true);

        long[] target = hash(subRoot, false);

        for (long[] m : memo) {
            if (m[0] == target[0] && m[1] == target[1]) {
                return true;
            }
        }

        return false;
    }
}
```

### Complexity

Time

```
O(N + M)
```

Space

```
O(N + M)
```

---

# Summary

| Approach                 | Time    | Space  | Idea                  |
| ------------------------ | ------- | ------ | --------------------- |
| DFS Comparison           | O(N\*M) | O(N+M) | Compare every subtree |
| Tree Serialization + KMP | O(N+M)  | O(N+M) | Convert tree → string |
| Tree Hashing             | O(N+M)  | O(N+M) | Hash each subtree     |
