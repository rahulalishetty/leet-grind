# 705. Design HashSet

## Detailed Notes on Hash Functions, Collision Handling, and Bucket Design

## Overview

This is a classical data structure design problem.

The goal is to implement a **HashSet** without using any built-in hash table libraries.

That means we need to build the core mechanics ourselves.

At a high level, a HashSet must support three operations:

- `add(key)`
- `remove(key)`
- `contains(key)`

To implement these operations efficiently, there are two fundamental questions we must answer:

1. **How do we map a value to a storage location?**
2. **What do we do when multiple values map to the same location?**

These two questions correspond to:

- **hash function**
- **collision handling strategy**

---

# 1. Hash Function

## Goal of a Hash Function

A hash function maps a value from a large key space into a smaller index space.

In this problem:

- input keys range from `0` to `10^6`
- we do not want to allocate direct storage for all possible keys unless we intentionally choose a direct-addressing design
- instead, we map keys into a smaller number of buckets

So the purpose of the hash function is:

> given a key, produce an index that tells us which bucket should contain that key

---

## Ideal Goal

Ideally:

- every different key would map to a different bucket
- bucket sizes would remain very small
- operations would be close to constant time

But in practice, because we map a large space into a smaller one, collisions are unavoidable.

---

# 2. Collision Handling

## What Is a Collision?

A collision happens when two different keys produce the same hash value.

For example, if we use:

```text
hash(key) = key % 769
```

then:

```text
1 % 769 = 1
770 % 769 = 1
1539 % 769 = 1
```

All of these keys map to the same bucket.

So even though the hash function gives us a location quickly, we still need a strategy to store and distinguish multiple keys inside that location.

---

## Common Collision Resolution Strategies

There are multiple standard strategies:

### 1. Separate Chaining

All values that map to the same bucket are stored together inside that bucket.

Each bucket is an independent secondary container.

Common bucket choices include:

- linked list
- dynamic array
- balanced BST

---

### 2. Open Addressing

Instead of storing multiple values inside a secondary container, we keep probing inside the main array until an empty slot is found.

Common probing strategies include:

- linear probing
- quadratic probing
- double hashing

---

### 3. 2-Choice Hashing

Use two hash functions instead of one.

For each value, compute two candidate buckets and place the value where there is less load.

This often improves distribution.

---

## Focus of This Document

This write-up focuses on **separate chaining**.

This is one of the most natural and interview-friendly ways to implement a HashSet.

---

# Separate Chaining: General Structure

## Intuition

A separate-chaining hash set has two layers:

### Layer 1: Primary Array

A continuous array of buckets.

Each position in the array corresponds to one bucket.

### Layer 2: Bucket Structure

Each bucket stores the actual values that hashed into that slot.

So the process for any key is:

1. compute its hash
2. find the bucket using the hash value
3. perform the needed operation inside that bucket

This means the bucket design matters a lot.

---

# Choosing the Hash Function

## Modulo Hashing

The most common simple hash function is:

```text
hash(key) = key % base
```

Where:

- `key` is the input value
- `base` is the number of buckets

So if the bucket array has size `769`, then:

```java
protected int _hash(int key) {
    return (key % this.keyRange);
}
```

---

## Why the Choice of `base` Matters

The base determines how many buckets the HashSet has.

A larger base means:

- more buckets
- fewer collisions on average
- more memory usage

A smaller base means:

- fewer buckets
- more collisions
- less memory usage

So the choice of base is a **space-time tradeoff**.

---

## Why Prime Numbers Are Often Used

It is common to choose a prime number such as `769` as the bucket count.

Why?

Because prime moduli often reduce undesirable clustering patterns when keys follow certain arithmetic regularities.

This is not magic, but in practice it is a good heuristic.

So using a prime like `769` can help distribute keys more evenly.

---

# Approach 1: LinkedList as Bucket

## Intuition

In this approach, each bucket is implemented with a **LinkedList**.

So the HashSet looks like:

- one array of buckets
- each bucket is a linked list of keys that hashed to that index

The overall flow is:

1. hash the key to get a bucket index
2. go to that bucket
3. search, insert, or delete inside the linked list

---

## Why Use a LinkedList?

A bucket needs to support:

- search for a key
- insert a key if it is not already present
- delete a key if it exists

A linked list is a reasonable choice because:

- insertion is easy
- deletion is easy once the node is located
- it handles variable bucket sizes naturally

---

## Why Not Use a Plain Array as Bucket?

A plain array can also store the contents of a bucket.

But there is a drawback:

- inserting into an array may require shifting elements
- deleting from an array may require shifting elements
- if dynamic resizing is involved, management becomes more cumbersome

So a linked list is a simpler bucket representation for this approach.

---

## Important Clarification

Even with a linked list, searching for a key inside a bucket is still linear in the bucket size.

So the linked list does **not** give constant lookup time inside the bucket.

The advantage is that once we reach the correct location, insertion or deletion is structurally easy.

The true performance depends on how large the buckets become.

---

## Main Idea of the Bucket

Each bucket behaves like a mini-set:

- no duplicates allowed
- add only if absent
- remove if present
- contains checks presence

---

# Java Implementation — LinkedList Bucket

```java
class MyHashSet {
  private Bucket[] bucketArray;
  private int keyRange;

  /** Initialize your data structure here. */
  public MyHashSet() {
    this.keyRange = 769;
    this.bucketArray = new Bucket[this.keyRange];
    for (int i = 0; i < this.keyRange; ++i)
      this.bucketArray[i] = new Bucket();
  }

  protected int _hash(int key) {
    return (key % this.keyRange);
  }

  public void add(int key) {
    int bucketIndex = this._hash(key);
    this.bucketArray[bucketIndex].insert(key);
  }

  public void remove(int key) {
    int bucketIndex = this._hash(key);
    this.bucketArray[bucketIndex].delete(key);
  }

  /** Returns true if this set contains the specified element */
  public boolean contains(int key) {
    int bucketIndex = this._hash(key);
    return this.bucketArray[bucketIndex].exists(key);
  }
}


class Bucket {
  private LinkedList<Integer> container;

  public Bucket() {
    container = new LinkedList<Integer>();
  }

  public void insert(Integer key) {
    int index = this.container.indexOf(key);
    if (index == -1) {
      this.container.addFirst(key);
    }
  }

  public void delete(Integer key) {
    this.container.remove(key);
  }

  public boolean exists(Integer key) {
    int index = this.container.indexOf(key);
    return (index != -1);
  }
}

/**
 * Your MyHashSet object will be instantiated and called as such:
 * MyHashSet obj = new MyHashSet();
 * obj.add(key);
 * obj.remove(key);
 * boolean param_3 = obj.contains(key);
 */
```

---

# Detailed Walkthrough — LinkedList Version

## 1. Bucket Array

```java
private Bucket[] bucketArray;
private int keyRange;
```

- `bucketArray` is the primary storage array
- `keyRange` is the number of buckets

---

## 2. Constructor

```java
public MyHashSet() {
  this.keyRange = 769;
  this.bucketArray = new Bucket[this.keyRange];
  for (int i = 0; i < this.keyRange; ++i)
    this.bucketArray[i] = new Bucket();
}
```

This creates exactly `769` buckets.

Each bucket is initialized independently.

---

## 3. Hash Function

```java
protected int _hash(int key) {
  return (key % this.keyRange);
}
```

This maps any key into the range:

```text
[0, 768]
```

---

## 4. `add(key)`

```java
public void add(int key) {
  int bucketIndex = this._hash(key);
  this.bucketArray[bucketIndex].insert(key);
}
```

Steps:

1. compute the bucket index
2. delegate insertion to that bucket

---

## 5. `remove(key)`

```java
public void remove(int key) {
  int bucketIndex = this._hash(key);
  this.bucketArray[bucketIndex].delete(key);
}
```

Again:

1. compute the bucket index
2. delete from that bucket

---

## 6. `contains(key)`

```java
public boolean contains(int key) {
  int bucketIndex = this._hash(key);
  return this.bucketArray[bucketIndex].exists(key);
}
```

Look inside the hashed bucket and check whether the key exists.

---

## 7. LinkedList Bucket

```java
class Bucket {
  private LinkedList<Integer> container;
```

Each bucket stores all colliding keys in a linked list.

---

## 8. Bucket Insert

```java
public void insert(Integer key) {
  int index = this.container.indexOf(key);
  if (index == -1) {
    this.container.addFirst(key);
  }
}
```

Before insertion, we search the list.

If the key is absent, insert it at the head.

This avoids duplicates.

---

## 9. Why Insert at Head?

The implementation chooses:

```java
this.container.addFirst(key);
```

rather than appending at the tail.

That is a design choice.

One possible benefit is temporal locality:

- if recently inserted keys are likely to be used soon again
- placing them near the head may reduce average search time in nearby operations

This is a heuristic, not a correctness requirement.

---

## 10. Bucket Delete

```java
public void delete(Integer key) {
  this.container.remove(key);
}
```

Remove the key if present.

If it does not exist, Java’s linked list remove operation effectively does nothing.

---

## 11. Bucket Contains

```java
public boolean exists(Integer key) {
  int index = this.container.indexOf(key);
  return (index != -1);
}
```

Check whether the key exists in the linked list.

---

# Notes on Python LinkedList Implementation

The original explanation mentions using a **pseudo head** in Python.

A pseudo head, also called a dummy head, is a node placed before the actual first element of the list.

Its purpose is to simplify linked-list logic by reducing edge cases:

- inserting at the head becomes uniform
- deleting the first real node becomes easier
- fewer conditional branches are needed

This is a standard linked-list engineering trick.

---

# Complexity Analysis — LinkedList Bucket

Let:

- `N` = number of all possible values or total inserted scale under analysis
- `K` = number of buckets, here `769`

Assuming values are distributed evenly, the average bucket size is:

```text
N / K
```

---

## Time Complexity

Each operation must first hash into the correct bucket.

That part is `O(1)`.

Then inside the bucket:

- `contains` scans the linked list
- `add` scans first to avoid duplicates
- `remove` scans to locate the key

So the cost is proportional to the bucket size.

Thus average time complexity is:

```text
O(N / K)
```

under uniform distribution assumptions.

---

## Space Complexity

We store:

- `K` buckets in the bucket array
- `M` unique inserted values in total

So space complexity is:

```text
O(K + M)
```

Where:

- `K` = fixed number of buckets
- `M` = number of unique elements actually stored

---

# Approach 2: Binary Search Tree (BST) as Bucket

## Motivation

The main weakness of the linked-list bucket is lookup time.

To determine whether a key exists in a bucket, we may need to scan the entire linked list.

That gives linear time in bucket size.

So the question becomes:

> can we choose a bucket structure that supports search, insert, and delete faster?

---

## First Thought: Sorted Array

If the bucket contents were sorted, then searching could be done with binary search in:

```text
O(log n)
```

But insertion and deletion in an array are expensive because elements must be shifted.

So a sorted array improves search but hurts updates.

---

## Better Choice: Binary Search Tree

A Binary Search Tree can support:

- search
- insert
- delete

all in:

```text
O(log n)
```

on average when reasonably balanced.

So replacing the linked list bucket with a BST can improve average bucket operations.

---

## Architectural Benefit

This approach reuses the same outer `MyHashSet` structure.

Only the bucket implementation changes.

This is a good example of the **Façade pattern**:

- `MyHashSet` interacts with buckets only through:
  - `insert`
  - `delete`
  - `exists`
- the internal bucket representation can change freely

So the outer logic stays almost identical.

---

# Java Implementation — BST Bucket

```java
class MyHashSet {
  private Bucket[] bucketArray;
  private int keyRange;

  /** Initialize your data structure here. */
  public MyHashSet() {
    this.keyRange = 769;
    this.bucketArray = new Bucket[this.keyRange];
    for (int i = 0; i < this.keyRange; ++i)
      this.bucketArray[i] = new Bucket();
  }

  protected int _hash(int key) {
    return (key % this.keyRange);
  }

  public void add(int key) {
    int bucketIndex = this._hash(key);
    this.bucketArray[bucketIndex].insert(key);
  }

  public void remove(int key) {
    int bucketIndex = this._hash(key);
    this.bucketArray[bucketIndex].delete(key);
  }

  /** Returns true if this set contains the specified element */
  public boolean contains(int key) {
    int bucketIndex = this._hash(key);
    return this.bucketArray[bucketIndex].exists(key);
  }
}


class Bucket {
  private BSTree tree;

  public Bucket() {
    tree = new BSTree();
  }

  public void insert(Integer key) {
    this.tree.root = this.tree.insertIntoBST(this.tree.root, key);
  }

  public void delete(Integer key) {
    this.tree.root = this.tree.deleteNode(this.tree.root, key);
  }

  public boolean exists(Integer key) {
    TreeNode node = this.tree.searchBST(this.tree.root, key);
    return (node != null);
  }
}

public class TreeNode {
  int val;
  TreeNode left;
  TreeNode right;

  TreeNode(int x) {
    val = x;
  }
}

class BSTree {
  TreeNode root = null;

  public TreeNode searchBST(TreeNode root, int val) {
    if (root == null || val == root.val)
      return root;

    return val < root.val ? searchBST(root.left, val) : searchBST(root.right, val);
  }

  public TreeNode insertIntoBST(TreeNode root, int val) {
    if (root == null)
      return new TreeNode(val);

    if (val > root.val)
      // insert into the right subtree
      root.right = insertIntoBST(root.right, val);
    else if (val == root.val)
      // skip the insertion
      return root;
    else
      // insert into the left subtree
      root.left = insertIntoBST(root.left, val);
    return root;
  }

  /*
   * One step right and then always left
   */
  public int successor(TreeNode root) {
    root = root.right;
    while (root.left != null)
      root = root.left;
    return root.val;
  }

  /*
   * One step left and then always right
   */
  public int predecessor(TreeNode root) {
    root = root.left;
    while (root.right != null)
      root = root.right;
    return root.val;
  }

  public TreeNode deleteNode(TreeNode root, int key) {
    if (root == null)
      return null;

    // delete from the right subtree
    if (key > root.val)
      root.right = deleteNode(root.right, key);
    // delete from the left subtree
    else if (key < root.val)
      root.left = deleteNode(root.left, key);
    // delete the current node
    else {
      // the node is a leaf
      if (root.left == null && root.right == null)
        root = null;
      // the node is not a leaf and has a right child
      else if (root.right != null) {
        root.val = successor(root);
        root.right = deleteNode(root.right, root.val);
      }
      // the node is not a leaf, has no right child, and has a left child
      else {
        root.val = predecessor(root);
        root.left = deleteNode(root.left, root.val);
      }
    }
    return root;
  }
}

/**
 * Your MyHashSet object will be instantiated and called as such:
 * MyHashSet obj = new MyHashSet();
 * obj.add(key);
 * obj.remove(key);
 * boolean param_3 = obj.contains(key);
 */
```

---

# Detailed Walkthrough — BST Version

## 1. Outer HashSet Stays the Same

The `MyHashSet` class is identical in spirit to the linked-list version.

That is deliberate.

The outer HashSet only needs to:

- hash the key
- locate the bucket
- delegate the operation

The bucket internals are hidden.

---

## 2. Bucket Uses a BST

```java
class Bucket {
  private BSTree tree;
```

Now each bucket stores colliding keys inside a BST instead of a linked list.

---

## 3. Insert in Bucket

```java
public void insert(Integer key) {
  this.tree.root = this.tree.insertIntoBST(this.tree.root, key);
}
```

Insert the key into the BST.

If the key already exists, insertion is skipped.

---

## 4. Delete in Bucket

```java
public void delete(Integer key) {
  this.tree.root = this.tree.deleteNode(this.tree.root, key);
}
```

Delete the key from the BST if present.

---

## 5. Exists in Bucket

```java
public boolean exists(Integer key) {
  TreeNode node = this.tree.searchBST(this.tree.root, key);
  return (node != null);
}
```

Check presence using BST search.

---

## 6. Tree Node Definition

```java
public class TreeNode {
  int val;
  TreeNode left;
  TreeNode right;

  TreeNode(int x) {
    val = x;
  }
}
```

Standard BST node:

- one integer value
- left child
- right child

---

## 7. Search Operation

```java
public TreeNode searchBST(TreeNode root, int val) {
  if (root == null || val == root.val)
    return root;

  return val < root.val ? searchBST(root.left, val) : searchBST(root.right, val);
}
```

Standard BST search:

- if current node matches, return it
- if target is smaller, go left
- if target is larger, go right

---

## 8. Insert Operation

```java
public TreeNode insertIntoBST(TreeNode root, int val) {
  if (root == null)
    return new TreeNode(val);

  if (val > root.val)
    root.right = insertIntoBST(root.right, val);
  else if (val == root.val)
    return root;
  else
    root.left = insertIntoBST(root.left, val);
  return root;
}
```

Standard BST insertion:

- insert at the correct leaf location
- if duplicate value found, skip insertion

This preserves set semantics.

---

## 9. Delete Operation — Main Cases

Deletion in BST is more involved.

There are three cases when deleting a node:

### Case 1: Leaf Node

If the node has no children, just remove it.

### Case 2: Node Has Right Child

Replace the node’s value with its **successor**.

The successor is:

- one step right
- then keep going left

### Case 3: Node Has No Right Child But Has Left Child

Replace the node’s value with its **predecessor**.

The predecessor is:

- one step left
- then keep going right

After replacing the node’s value, recursively delete the moved successor or predecessor node from the subtree.

---

## 10. Successor Helper

```java
public int successor(TreeNode root) {
  root = root.right;
  while (root.left != null)
    root = root.left;
  return root.val;
}
```

Returns the smallest value in the right subtree.

---

## 11. Predecessor Helper

```java
public int predecessor(TreeNode root) {
  root = root.left;
  while (root.right != null)
    root = root.right;
  return root.val;
}
```

Returns the largest value in the left subtree.

---

## 12. Delete Node Function

```java
public TreeNode deleteNode(TreeNode root, int key) {
  if (root == null)
    return null;

  if (key > root.val)
    root.right = deleteNode(root.right, key);
  else if (key < root.val)
    root.left = deleteNode(root.left, key);
  else {
    if (root.left == null && root.right == null)
      root = null;
    else if (root.right != null) {
      root.val = successor(root);
      root.right = deleteNode(root.right, root.val);
    }
    else {
      root.val = predecessor(root);
      root.left = deleteNode(root.left, root.val);
    }
  }
  return root;
}
```

This is the standard recursive BST deletion pattern.

---

# Complexity Analysis — BST Bucket

Assume values distribute evenly across `K` buckets.

Then average bucket size is:

```text
N / K
```

where:

- `N` is the total scale of stored elements under analysis
- `K = 769`

If the BST inside a bucket behaves roughly balanced, then:

- search = `O(log(N / K))`
- insert = `O(log(N / K))`
- delete = `O(log(N / K))`

---

## Time Complexity

So each HashSet operation becomes:

```text
O(log(N / K))
```

on average under the even-distribution and reasonably balanced-BST assumptions.

---

## Space Complexity

Same as before:

```text
O(K + M)
```

Where:

- `K` = number of buckets
- `M` = number of unique stored elements

The only difference is the internal bucket structure.

---

# Important Caution About BST Buckets

A plain BST is not automatically balanced.

In the worst case, if keys are inserted in unlucky order, a BST can degenerate into a linked list.

Then operations become:

```text
O(N / K)
```

inside the bucket again.

So the stated logarithmic performance assumes reasonably balanced behavior.

If you wanted guaranteed logarithmic performance, you would need a self-balancing BST such as:

- AVL tree
- Red-Black tree

But that would add significantly more implementation complexity.

---

# Notes on Hash Function Design

## Fixed Address Range

In both approaches above, the bucket count is fixed:

```text
keyRange = 769
```

That means the address space never changes.

This is simple and acceptable for the problem constraints.

But real-world hash tables often resize dynamically.

---

## Dynamic Resizing

As more values are inserted, collisions increase.

A common strategy is to monitor the **load factor**:

```text
load factor = number of stored elements / number of buckets
```

If this exceeds some threshold, then:

- allocate a larger bucket array
- often double the size
- rehash all existing elements into the new array

This reduces collisions and improves performance.

---

## Cost of Rehashing

Dynamic resizing is not free.

When the table grows, every stored value must be reinserted into its new bucket based on the new hash range.

This is called **rehashing**.

So resizing improves long-term performance but introduces occasional expensive rebuild steps.

---

## 2-Choice Hashing

Another strategy mentioned earlier is **2-choice hashing**.

Instead of computing one bucket index, compute two.

Then place the value into the less crowded bucket.

This often produces more even load distribution and can reduce collision-heavy buckets.

It is a more advanced design compared with basic modulo hashing.

---

# Comparing the Two Bucket Approaches

## LinkedList Bucket

### Strengths

- simple to implement
- clean and intuitive
- good for interview explanation
- low engineering overhead

### Weaknesses

- search is linear in bucket size
- collision-heavy buckets degrade performance

---

## BST Bucket

### Strengths

- potentially faster search, insert, and delete
- more structured bucket behavior
- better average performance than linked list when buckets get larger

### Weaknesses

- much more complex to implement
- plain BST is not guaranteed balanced
- worst-case behavior can still degrade

---

# Big Picture Takeaway

This problem is less about the final few lines of code and more about understanding the anatomy of a hash-based set.

A custom HashSet needs:

1. a hash function
2. a collision strategy
3. a bucket representation
4. correct set semantics with no duplicates

These design choices directly determine the performance of:

- add
- remove
- contains

---

# Final Summary

## Core Concepts

A HashSet implementation requires solving two major problems:

- mapping keys to buckets with a **hash function**
- storing colliding keys with a **collision handling strategy**

---

## Separate Chaining

In separate chaining:

- the main storage is an array
- each array slot is a bucket
- the bucket stores all keys hashing to that slot

---

## Approach 1: LinkedList Bucket

- bucket = linked list
- simple to implement
- average operation cost proportional to average bucket size

### Complexity

- **Time:** `O(N / K)` on average
- **Space:** `O(K + M)`

---

## Approach 2: BST Bucket

- bucket = binary search tree
- faster average search/update inside buckets
- more complex implementation

### Complexity

- **Time:** `O(log(N / K))` on average, assuming balanced behavior
- **Space:** `O(K + M)`

---

## Hash Function Notes

- using `key % base` is a common simple hash function
- prime bucket counts often improve distribution
- dynamic resizing and rehashing are common in practical hash tables
- 2-choice hashing can improve load balancing further

---

## Most Important Lesson

A hash table’s performance is not only about the hash function.

It is equally about:

- how collisions are handled
- how bucket contents are represented
- how well values stay distributed

That is the real engineering insight behind this problem.
