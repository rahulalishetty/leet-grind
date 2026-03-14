# 173. Binary Search Tree Iterator

## Problem

Implement the **BSTIterator** class that represents an iterator over the **in-order traversal** of a Binary Search Tree (BST).

### Class Specification

`BSTIterator(TreeNode root)`
Initializes the iterator with the root of the BST. The pointer is initialized to a **non-existent number smaller than any element** in the BST.

`boolean hasNext()`
Returns **true** if there exists a number in the traversal to the **right of the pointer**, otherwise returns **false**.

`int next()`
Moves the pointer to the **right** and returns the number at the pointer.

### Important Note

Because the pointer starts before the smallest element, the **first call to `next()` returns the smallest element in the BST**.

You may assume that `next()` will always be valid when called.

---

# Example

## Input

```
["BSTIterator", "next", "next", "hasNext", "next", "hasNext", "next", "hasNext", "next", "hasNext"]
[[[7,3,15,null,null,9,20]],[],[],[],[],[],[],[],[],[]]
```

## Output

```
[null,3,7,true,9,true,15,true,20,false]
```

## Explanation

```
BSTIterator iterator = new BSTIterator([7,3,15,null,null,9,20]);

iterator.next();    // returns 3
iterator.next();    // returns 7
iterator.hasNext(); // returns true
iterator.next();    // returns 9
iterator.hasNext(); // returns true
iterator.next();    // returns 15
iterator.hasNext(); // returns true
iterator.next();    // returns 20
iterator.hasNext(); // returns false
```

---

# Constraints

- Number of nodes in the tree: **[1, 10^5]**
- `0 <= Node.val <= 10^6`
- At most **10^5 calls** will be made to `hasNext()` and `next()`.

---

# Follow-up

Can you implement:

- `next()`
- `hasNext()`

such that:

- **Average Time Complexity:** `O(1)`
- **Space Complexity:** `O(h)`

where **h** is the height of the tree.
