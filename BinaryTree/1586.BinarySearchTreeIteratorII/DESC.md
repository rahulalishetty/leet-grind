# 1586. Binary Search Tree Iterator II

## Problem

Implement the `BSTIterator` class that represents an iterator over the **in-order traversal** of a Binary Search Tree (BST).

The iterator must support moving **forward and backward** in the traversal sequence.

---

## Class API

### Constructor

```
BSTIterator(TreeNode root)
```

Initializes the iterator using the root of the BST.

The pointer should initially be positioned **before the smallest element** in the tree (i.e., a conceptual position smaller than all values).

---

### Methods

```
boolean hasNext()
```

Returns `true` if there exists a node **to the right** of the current pointer in the inorder traversal.

---

```
int next()
```

Moves the pointer **to the right** and returns the value at that position.

---

```
boolean hasPrev()
```

Returns `true` if there exists a node **to the left** of the current pointer.

---

```
int prev()
```

Moves the pointer **to the left** and returns the value at that position.

---

## Important Behavior

Because the pointer initially starts before the first element:

```
first call to next() → returns the smallest element
```

---

# Example

### Input

```
["BSTIterator","next","next","prev","next","hasNext","next","next","next","hasNext","hasPrev","prev","prev"]

[[[7,3,15,null,null,9,20]],
 [null],[null],[null],[null],
 [null],[null],[null],[null],
 [null],[null],[null],[null]]
```

### Output

```
[null,3,7,3,7,true,9,15,20,false,true,15,9]
```

---

## Explanation

In-order traversal of the BST:

```
[3, 7, 9, 15, 20]
```

Operations:

```
BSTIterator(root)

next() → 3
next() → 7
prev() → 3
next() → 7
hasNext() → true
next() → 9
next() → 15
next() → 20
hasNext() → false
hasPrev() → true
prev() → 15
prev() → 9
```

---

# Constraints

```
1 <= number of nodes <= 100000
0 <= Node.val <= 1,000,000
```

Additional constraint:

```
At most 100000 calls
to next(), prev(), hasNext(), hasPrev()
```

---

# Follow-up

Can you solve the problem **without precomputing all nodes of the tree beforehand?**

This would require implementing a **lazy iterator** that generates the next element only when needed rather than storing the full inorder traversal.
