# 2476. Closest Nodes Queries in a Binary Search Tree

## Problem summary

We are given:

- the root of a **Binary Search Tree**
- a list of query values

For each query `q`, we must return:

- `mini`: the **largest** value in the BST such that `value <= q`
- `maxi`: the **smallest** value in the BST such that `value >= q`

If either value does not exist, return `-1` for that side.

So each answer is:

```text
[ floor(q), ceil(q) ]
```

with respect to the BST values.

---

# Key observation

A BST’s **inorder traversal** gives all values in **sorted order**.

Once we have the sorted values, each query becomes a standard predecessor/successor search problem.

That naturally leads to:

1. extract sorted BST values
2. answer each query with binary search

This is the cleanest and most scalable solution.

---

# Approach 1: Inorder traversal + binary search (recommended)

## Intuition

Because the BST inorder traversal is sorted, we can flatten the BST into a sorted list.

Then for each query `q`:

- find the first element `>= q` using binary search
- that gives the ceiling
- the floor is either:
  - that same value if it equals `q`
  - or the previous value in the sorted list

This turns the problem into repeated binary search on a sorted array.

---

## Algorithm

### Step 1: Inorder traversal

Perform inorder traversal of the BST and store all node values in a list `values`.

Since the tree is a BST, `values` will be sorted.

### Step 2: Answer each query

For a query `q`:

- use binary search to find the first index `idx` such that:

```text
values[idx] >= q
```

Then:

- if `idx < values.size()`, ceiling is `values[idx]`
- otherwise ceiling is `-1`

For floor:

- if `idx < values.size()` and `values[idx] == q`, floor is `q`
- else if `idx > 0`, floor is `values[idx - 1]`
- else floor is `-1`

---

## Java code

```java
import java.util.*;

class Solution {
    public List<List<Integer>> closestNodes(TreeNode root, List<Integer> queries) {
        List<Integer> values = new ArrayList<>();
        inorder(root, values);

        List<List<Integer>> answer = new ArrayList<>();

        for (int q : queries) {
            int idx = lowerBound(values, q);

            int floor = -1;
            int ceil = -1;

            if (idx < values.size()) {
                ceil = values.get(idx);
            }

            if (idx < values.size() && values.get(idx) == q) {
                floor = q;
            } else if (idx > 0) {
                floor = values.get(idx - 1);
            }

            answer.add(Arrays.asList(floor, ceil));
        }

        return answer;
    }

    private void inorder(TreeNode node, List<Integer> values) {
        if (node == null) {
            return;
        }
        inorder(node.left, values);
        values.add(node.val);
        inorder(node.right, values);
    }

    // first index with values[idx] >= target
    private int lowerBound(List<Integer> values, int target) {
        int left = 0, right = values.size();

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (values.get(mid) >= target) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }
}
```

---

## Why this works

Inorder traversal of a BST produces sorted values.

For each query:

- the first value `>= q` is exactly the ceiling candidate
- the element just before that position is exactly the floor candidate

So binary search correctly identifies the two closest boundary values.

---

## Complexity

Let:

- `n` = number of nodes in the BST
- `m` = number of queries

### Time complexity

- inorder traversal: `O(n)`
- each query binary search: `O(log n)`

Total:

```text
O(n + m log n)
```

### Space complexity

- sorted values list: `O(n)`
- recursion stack: `O(h)`

Total worst case:

```text
O(n)
```

---

# Approach 2: Direct BST search per query

## Intuition

Since the input is already a BST, we can answer each query directly by walking down the tree.

For a query `q`:

- if current node value equals `q`, then both floor and ceiling are `q`
- if current node value < `q`, this node is a floor candidate, move right
- if current node value > `q`, this node is a ceiling candidate, move left

This is similar to standard BST predecessor/successor search.

---

## Algorithm

For each query `q`:

1. initialize:
   - `floor = -1`
   - `ceil = -1`
2. start from root
3. while current node is not null:
   - if `node.val == q`:
     - `floor = q`
     - `ceil = q`
     - stop
   - else if `node.val < q`:
     - update `floor = node.val`
     - move to `node.right`
   - else:
     - update `ceil = node.val`
     - move to `node.left`
4. add `[floor, ceil]` to answer

---

## Java code

```java
import java.util.*;

class Solution {
    public List<List<Integer>> closestNodes(TreeNode root, List<Integer> queries) {
        List<List<Integer>> answer = new ArrayList<>();

        for (int q : queries) {
            int floor = -1;
            int ceil = -1;

            TreeNode curr = root;

            while (curr != null) {
                if (curr.val == q) {
                    floor = q;
                    ceil = q;
                    break;
                } else if (curr.val < q) {
                    floor = curr.val;
                    curr = curr.right;
                } else {
                    ceil = curr.val;
                    curr = curr.left;
                }
            }

            answer.add(Arrays.asList(floor, ceil));
        }

        return answer;
    }
}
```

---

## Why this works

The BST property ensures:

- all values in the left subtree are smaller
- all values in the right subtree are larger

So as we descend:

- every time we go right from a smaller node, that node becomes the best floor candidate seen so far
- every time we go left from a larger node, that node becomes the best ceiling candidate seen so far

When traversal ends, the best candidates are the correct floor and ceiling.

---

## Complexity

For each query, the cost is proportional to tree height `h`.

### Time complexity

```text
O(mh)
```

- balanced BST: `O(m log n)`
- skewed BST: `O(mn)`

### Space complexity

```text
O(1)
```

excluding the output list.

---

## When is this approach good?

This is attractive when:

- you want to avoid storing all BST values
- the BST is expected to be balanced

But in the worst case, it is much less reliable than Approach 1.

---

# Approach 3: Inorder traversal + TreeSet

## Intuition

Instead of storing values in a list and using manual binary search, we can insert all BST values into a `TreeSet`.

Java’s `TreeSet` gives us:

- `floor(x)` → largest element `<= x`
- `ceiling(x)` → smallest element `>= x`

This makes the query logic very compact.

---

## Algorithm

1. Traverse the BST and insert all values into a `TreeSet<Integer>`
2. For each query `q`:
   - `Integer f = set.floor(q)`
   - `Integer c = set.ceiling(q)`
3. Convert null results to `-1`

---

## Java code

```java
import java.util.*;

class Solution {
    public List<List<Integer>> closestNodes(TreeNode root, List<Integer> queries) {
        TreeSet<Integer> set = new TreeSet<>();
        fill(root, set);

        List<List<Integer>> answer = new ArrayList<>();

        for (int q : queries) {
            Integer floor = set.floor(q);
            Integer ceil = set.ceiling(q);

            answer.add(Arrays.asList(
                floor == null ? -1 : floor,
                ceil == null ? -1 : ceil
            ));
        }

        return answer;
    }

    private void fill(TreeNode node, TreeSet<Integer> set) {
        if (node == null) {
            return;
        }
        set.add(node.val);
        fill(node.left, set);
        fill(node.right, set);
    }
}
```

---

## Complexity

### Time complexity

Building the `TreeSet`:

```text
O(n log n)
```

Each query:

```text
O(log n)
```

Total:

```text
O((n + m) log n)
```

### Space complexity

```text
O(n)
```

---

## Comparison with Approach 1

This is clean and idiomatic Java, but it is slightly less efficient than using inorder + sorted list:

- list approach builds in `O(n)`
- TreeSet build is `O(n log n)`

So Approach 1 is still better asymptotically.

---

# Approach 4: Offline sorting of queries + inorder sweep

## Intuition

We can solve the problem offline by:

1. sorting the BST values
2. sorting the queries with their original indices
3. sweeping through both to compute floors and ceilings

This can reduce repeated binary searches, though implementation becomes more involved.

A natural version is:

- flatten BST into sorted array
- sort query-value/index pairs
- maintain a pointer in the sorted values list
- compute floor while moving left to right
- compute ceiling similarly or by another sweep

This is a valid alternative, but more complex than the direct binary-search solution.

---

## High-level idea

If both the BST values and queries are sorted:

- floor information can be computed in one linear sweep
- ceiling information can also be computed in one linear sweep from the other side

After that, answers are restored in original query order.

---

## Complexity

If BST values are extracted in sorted order by inorder, and queries are sorted:

```text
O(n + m log m)
```

plus answer reconstruction.

This can be competitive, but the implementation is more complicated than Approach 1 and offers no real advantage unless query count is huge and you specifically want an offline method.

---

# Comparison of approaches

## Approach 1: Inorder + binary search

- best overall
- deterministic
- clean
- optimal asymptotically

## Approach 2: Direct BST walk per query

- no extra array
- simple
- can degrade badly on skewed BSTs

## Approach 3: TreeSet

- very concise Java solution
- slightly worse preprocessing complexity

## Approach 4: Offline sorted queries

- interesting alternative
- more complicated than necessary

---

# Best approach

The best solution is:

## **Approach 1: Inorder traversal + binary search**

Why:

- inorder traversal gives a sorted list for free
- binary search is a perfect fit for floor / ceiling queries
- excellent worst-case guarantee
- simple and interview-friendly

---

# Final recommended solution

```java
import java.util.*;

class Solution {
    public List<List<Integer>> closestNodes(TreeNode root, List<Integer> queries) {
        List<Integer> values = new ArrayList<>();
        inorder(root, values);

        List<List<Integer>> answer = new ArrayList<>();

        for (int q : queries) {
            int idx = lowerBound(values, q);

            int floor = -1;
            int ceil = -1;

            if (idx < values.size()) {
                ceil = values.get(idx);
            }

            if (idx < values.size() && values.get(idx) == q) {
                floor = q;
            } else if (idx > 0) {
                floor = values.get(idx - 1);
            }

            answer.add(Arrays.asList(floor, ceil));
        }

        return answer;
    }

    private void inorder(TreeNode node, List<Integer> values) {
        if (node == null) {
            return;
        }
        inorder(node.left, values);
        values.add(node.val);
        inorder(node.right, values);
    }

    private int lowerBound(List<Integer> values, int target) {
        int left = 0, right = values.size();

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (values.get(mid) >= target) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }
}
```

## Complexity

- **Time:** `O(n + m log n)`
- **Space:** `O(n)`

where:

- `n` = number of tree nodes
- `m` = number of queries

This is the cleanest and most efficient solution for the problem.
