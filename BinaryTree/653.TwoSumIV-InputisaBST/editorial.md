# 653. Two Sum IV - Input is a BST

## Detailed Notes on Multiple Approaches

## Overview

We are given the root of a **Binary Search Tree (BST)** and an integer `k`.

We need to determine whether there exist **two distinct nodes** in the tree such that:

```text
node1.val + node2.val = k
```

This is a tree version of the classic **Two Sum** problem.

The main challenge is deciding how to traverse the tree and how to efficiently check whether a complement exists.

This document explains three approaches in detail:

1. **Using HashSet with DFS**
2. **Using BFS with HashSet**
3. **Using BST property with In-order Traversal + Two Pointers**

---

# Core Observation

If we are currently at a node with value `x`, then to form the required sum `k`, we need another value:

```text
y = k - x
```

So instead of checking every possible pair directly, we can ask:

> Have we already seen `k - x` somewhere in the tree?

If yes, we are done.

That single observation leads to the first two efficient solutions.

---

# Approach 1: Using HashSet

## Intuition

The most straightforward brute-force idea would be:

- look at every pair of nodes
- check whether their sum is `k`

But that would be inefficient.

Instead, while traversing the tree, we maintain a set of all values we have seen so far.

For every current node value `p`:

- compute `k - p`
- check whether `k - p` already exists in the set

If yes, then the current node and the previously seen node form the required pair.

Otherwise:

- add `p` to the set
- continue traversing

This is exactly the standard Two Sum pattern adapted to a tree.

---

## Why This Works

Suppose two nodes with values `x` and `y` satisfy:

```text
x + y = k
```

When the traversal reaches the second one among those two:

- the first one is already in the set
- the complement check succeeds
- we return `true`

If no such pair exists, the traversal finishes and returns `false`.

---

## Traversal Choice

This approach does not depend on the BST property at all.

It works for any binary tree.

We can traverse the tree recursively in depth-first order:

- current node
- left subtree
- right subtree

or any other order.

The order does not matter for correctness.

---

## Java Code

```java
public class Solution {
    public boolean findTarget(TreeNode root, int k) {
        Set<Integer> set = new HashSet();
        return find(root, k, set);
    }

    public boolean find(TreeNode root, int k, Set<Integer> set) {
        if (root == null)
            return false;
        if (set.contains(k - root.val))
            return true;
        set.add(root.val);
        return find(root.left, k, set) || find(root.right, k, set);
    }
}
```

---

## Detailed Walkthrough

### 1. Create the set

```java
Set<Integer> set = new HashSet();
```

This stores the values encountered so far during traversal.

---

### 2. Recursive helper

```java
return find(root, k, set);
```

We pass the tree root, target `k`, and the set into a recursive DFS helper.

---

### 3. Base case

```java
if (root == null)
    return false;
```

If we reach a null node, there is nothing to check.

---

### 4. Complement check

```java
if (set.contains(k - root.val))
    return true;
```

If the complement already exists, then we have found two values whose sum is `k`.

---

### 5. Add the current value

```java
set.add(root.val);
```

If no complement was found yet, store the current value for future nodes.

---

### 6. Continue DFS

```java
return find(root.left, k, set) || find(root.right, k, set);
```

Search the left subtree and right subtree.

If either one finds a valid pair, return `true`.

---

## Complexity Analysis

Let `n` be the number of nodes in the tree.

### Time Complexity

```text
O(n)
```

Why?

- each node is visited once
- each set lookup and insertion is `O(1)` on average

So total time is linear.

---

### Space Complexity

```text
O(n)
```

Why?

- the set may store up to all node values
- recursion stack may also take space up to tree height

So overall worst-case auxiliary space is linear.

---

## Pros and Cons

### Pros

- very simple
- efficient
- does not require BST property
- often the easiest accepted solution

### Cons

- does not take advantage of the BST structure
- uses extra memory for the set

---

# Approach 2: Using BFS and HashSet

## Intuition

This approach uses the exact same complement-check idea as Approach 1.

The only difference is the traversal style.

Instead of depth-first search, we traverse the tree in **breadth-first order** using a queue.

At each step:

1. remove one node from the queue
2. check whether its complement has already been seen
3. if not, add the node value to the set
4. push its children into the queue

This explores the tree level by level.

---

## Why This Works

The core logic is unchanged:

- whenever we process a node `p`
- we ask whether `k - p` has already appeared earlier in traversal

If yes, we have found a valid pair.

Traversal order does not matter for correctness, so BFS works just as well as DFS.

---

## Algorithm

1. Create an empty set.
2. Create a queue and add the root to it.
3. While the queue is not empty:
   - remove the front node
   - if it is null, skip it
   - if the set contains `k - node.val`, return `true`
   - add `node.val` to the set
   - enqueue the node’s left and right children
4. If traversal completes without finding a pair, return `false`.

---

## Java Code

```java
public class Solution {
    public boolean findTarget(TreeNode root, int k) {
        Set<Integer> set = new HashSet();
        Queue<TreeNode> queue = new LinkedList();
        queue.add(root);
        while (!queue.isEmpty()) {
            if (queue.peek() != null) {
                TreeNode node = queue.remove();
                if (set.contains(k - node.val))
                    return true;
                set.add(node.val);
                queue.add(node.right);
                queue.add(node.left);
            } else
                queue.remove();
        }
        return false;
    }
}
```

---

## Detailed Walkthrough

### 1. Set and queue initialization

```java
Set<Integer> set = new HashSet();
Queue<TreeNode> queue = new LinkedList();
queue.add(root);
```

- `set` stores previously seen values
- `queue` manages BFS traversal

---

### 2. Process nodes one by one

```java
while (!queue.isEmpty()) {
```

Continue until all reachable nodes are processed.

---

### 3. Null check

```java
if (queue.peek() != null) {
    ...
} else
    queue.remove();
```

The provided code allows nulls into the queue and removes them when encountered.

A cleaner version could simply avoid enqueuing nulls in the first place, but the given code is still correct.

---

### 4. Complement check

```java
if (set.contains(k - node.val))
    return true;
```

Same complement logic as before.

---

### 5. Add current node value

```java
set.add(node.val);
```

Store it for future complement checks.

---

### 6. Push children

```java
queue.add(node.right);
queue.add(node.left);
```

Continue BFS.

Note that adding right before left does not affect correctness. It only changes traversal order.

---

## Complexity Analysis

### Time Complexity

```text
O(n)
```

Every node is processed once.

HashSet operations are `O(1)` average.

---

### Space Complexity

```text
O(n)
```

Why?

- the set may contain up to `n` values
- the queue may also contain up to `n` nodes in the worst case

So auxiliary space is linear.

---

## Pros and Cons

### Pros

- same efficiency as DFS + HashSet
- iterative, so no recursion depth issue
- easy to understand

### Cons

- still does not use BST property
- may use substantial queue space on wide trees

---

# Approach 3: Using BST Property

## Intuition

The previous two approaches worked for any binary tree.

But here the input is specifically a **BST**.

A key property of BSTs is:

> in-order traversal gives the node values in sorted ascending order

Once we have a sorted list, we can solve the problem using the classic **two-pointer** technique.

---

## Why Sorting Helps

Suppose the sorted values are:

```text
v[0] <= v[1] <= ... <= v[n - 1]
```

We place:

- `l` at the beginning
- `r` at the end

Then:

- if `v[l] + v[r] == k`, return `true`
- if the sum is too small, increase `l`
- if the sum is too large, decrease `r`

Because the list is sorted, these pointer movements are valid and efficient.

---

## Why the Two-Pointer Method Works

Let the current sum be:

```text
sum = list[l] + list[r]
```

### Case 1: `sum < k`

We need a larger sum.

Since the list is sorted, the only sensible move is to increase the smaller value.

So we move:

```text
l++
```

Moving `r` left would only reduce the sum further.

---

### Case 2: `sum > k`

We need a smaller sum.

Since the list is sorted, the only sensible move is to reduce the larger value.

So we move:

```text
r--
```

Moving `l` right would only increase the sum further.

---

## Algorithm

1. Perform in-order traversal of the BST.
2. Store node values in a list.
3. Use two pointers:
   - `l = 0`
   - `r = list.size() - 1`
4. While `l < r`:
   - compute `sum = list[l] + list[r]`
   - if `sum == k`, return `true`
   - if `sum < k`, increment `l`
   - else decrement `r`
5. If the pointers cross, return `false`

---

## Java Code

```java
public class Solution {
    public boolean findTarget(TreeNode root, int k) {
        List<Integer> list = new ArrayList();
        inorder(root, list);
        int l = 0, r = list.size() - 1;
        while (l < r) {
            int sum = list.get(l) + list.get(r);
            if (sum == k)
                return true;
            if (sum < k)
                l++;
            else
                r--;
        }
        return false;
    }

    public void inorder(TreeNode root, List<Integer> list) {
        if (root == null)
            return;
        inorder(root.left, list);
        list.add(root.val);
        inorder(root.right, list);
    }
}
```

---

## Detailed Walkthrough

### 1. In-order traversal

```java
public void inorder(TreeNode root, List<Integer> list) {
    if (root == null)
        return;
    inorder(root.left, list);
    list.add(root.val);
    inorder(root.right, list);
}
```

This stores BST node values in sorted order.

---

### 2. Initialize two pointers

```java
int l = 0, r = list.size() - 1;
```

- `l` starts at the smallest value
- `r` starts at the largest value

---

### 3. Check current sum

```java
int sum = list.get(l) + list.get(r);
```

This is the candidate pair.

---

### 4. Move pointers intelligently

```java
if (sum == k)
    return true;
if (sum < k)
    l++;
else
    r--;
```

Use sorted order to eliminate impossible pairs efficiently.

---

### 5. End condition

```java
while (l < r)
```

The two nodes must be distinct, so pointers must not meet.

If they cross, no valid pair exists.

---

## Complexity Analysis

### Time Complexity

```text
O(n)
```

Why?

- in-order traversal visits each node once → `O(n)`
- two-pointer scan over the sorted list → `O(n)`

Total is linear.

---

### Space Complexity

```text
O(n)
```

Why?

- the list stores all `n` node values

Additional recursion stack space may also be used for in-order traversal.

---

## Pros and Cons

### Pros

- uses the BST property
- elegant combination of tree traversal and array technique
- deterministic and easy to explain

### Cons

- stores all values in a list
- still uses `O(n)` extra space
- not as direct as the HashSet approach

---

# Comparing the Approaches

## Approach 1: DFS + HashSet

### Idea

Traverse the tree and check complements using a set.

### Time

```text
O(n)
```

### Space

```text
O(n)
```

### Uses BST property?

No.

### Best For

Simplicity and directness.

---

## Approach 2: BFS + HashSet

### Idea

Same complement-set idea, but level-order traversal.

### Time

```text
O(n)
```

### Space

```text
O(n)
```

### Uses BST property?

No.

### Best For

Iterative traversal without recursion.

---

## Approach 3: In-order + Two Pointers

### Idea

Exploit BST ordering to create a sorted list, then solve as classic Two Sum on sorted array.

### Time

```text
O(n)
```

### Space

```text
O(n)
```

### Uses BST property?

Yes.

### Best For

Taking advantage of BST structure.

---

# Which Approach Should You Prefer?

## For shortest and most natural implementation

Use **Approach 1: DFS + HashSet**

It is usually the most straightforward accepted solution.

---

## For an iterative traversal style

Use **Approach 2: BFS + HashSet**

It avoids recursion and is still simple.

---

## For explicitly using the BST property

Use **Approach 3: In-order + Two Pointers**

This is often the most conceptually satisfying BST-specific solution.

---

# Final Takeaway

The problem looks like a BST problem, but the first major insight is that it can be treated as a standard **Two Sum** problem if we remember complements in a set.

That already gives an optimal `O(n)` solution.

The second insight is that because the input is a BST, we also have the option to:

- extract values in sorted order using in-order traversal
- solve using the two-pointer technique

So the problem has two equally efficient viewpoints:

1. **Tree traversal + HashSet**
2. **BST ordering + sorted two-pointer scan**

Both are valid and efficient.

---

# Summary

- We need to determine whether there exist two distinct node values in the BST whose sum is `k`.

## Approach 1: DFS + HashSet

- Traverse the tree recursively
- Store seen values in a set
- For each node, check if `k - node.val` has already been seen
- Time: `O(n)`
- Space: `O(n)`

## Approach 2: BFS + HashSet

- Same set-based idea
- Use a queue for level-order traversal
- Time: `O(n)`
- Space: `O(n)`

## Approach 3: In-order + Two Pointers

- Use BST property to obtain a sorted list with in-order traversal
- Solve using two pointers
- Time: `O(n)`
- Space: `O(n)`

## Recommended

- Use **DFS + HashSet** for the simplest implementation
- Use **In-order + Two Pointers** if you want to explicitly leverage the BST property
