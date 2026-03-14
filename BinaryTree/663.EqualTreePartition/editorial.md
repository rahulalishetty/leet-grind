# Equal Tree Partition — Approach 1: Depth‑First Search

## Intuition

If we remove an edge between a **parent** and a **child**, the tree splits into:

- the subtree rooted at that child
- the remaining part of the tree

For the partition to be valid:

```
subtree_sum = total_tree_sum / 2
```

Therefore:

1. Compute the **sum of every subtree**.
2. Store all subtree sums.
3. Check whether **half of the total tree sum** appears among these subtree sums.

Important detail:

- The subtree representing the **entire tree** must be excluded, because removing an edge cannot produce the original full tree.

---

## Key Idea

If the total sum is **even**, then:

```
total_sum / 2
```

must appear as a subtree sum somewhere in the tree.

If such a subtree exists, we can remove the edge above that subtree to create two equal‑sum trees.

---

## Algorithm

1. Perform a **Depth‑First Search (DFS)**.
2. During DFS compute the **sum of each subtree**.
3. Store each subtree sum in a stack (or list).
4. Remove the last element (which represents the whole tree).
5. If the total sum is even:
   - Check if `total_sum / 2` exists in the stored subtree sums.
6. Return `true` if found, otherwise `false`.

---

## Implementation

```java
class Solution {

    Stack<Integer> seen;

    public boolean checkEqualTree(TreeNode root) {

        seen = new Stack<>();

        int total = sum(root);

        // Remove the sum of the entire tree
        seen.pop();

        if (total % 2 == 0) {

            for (int s : seen) {

                if (s == total / 2) {
                    return true;
                }
            }
        }

        return false;
    }

    public int sum(TreeNode node) {

        if (node == null) {
            return 0;
        }

        seen.push(sum(node.left) + sum(node.right) + node.val);

        return seen.peek();
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(N)
```

Every node in the tree is visited exactly once during DFS.

---

### Space Complexity

```
O(N)
```

Space is used for:

- storing subtree sums
- recursion stack during DFS
