# 1367. Linked List in Binary Tree

## Problem

You are given:

- The **root of a binary tree**
- The **head of a linked list**

Return:

```
true
```

if **all elements of the linked list appear in a downward path in the binary tree**.

Otherwise return:

```
false
```

---

## Definition

A **downward path** means:

- The path starts at **any node in the tree**
- Moves **only from parent → child**
- Cannot move upward

---

## Example 1

### Input

```
head = [4,2,8]

root = [1,4,4,null,2,2,null,1,null,6,8,null,null,null,null,1,3]
```

### Output

```
true
```

### Explanation

The highlighted nodes in the tree form the path:

```
4 → 2 → 8
```

which matches the linked list.

---

## Example 2

### Input

```
head = [1,4,2,6]

root = [1,4,4,null,2,2,null,1,null,6,8,null,null,null,null,1,3]
```

### Output

```
true
```

---

## Example 3

### Input

```
head = [1,4,2,6,8]

root = [1,4,4,null,2,2,null,1,null,6,8,null,null,null,null,1,3]
```

### Output

```
false
```

### Explanation

There is **no downward path** in the tree that matches the entire linked list.

---

## Constraints

```
1 ≤ number of nodes in the tree ≤ 2500
1 ≤ number of nodes in the linked list ≤ 100
1 ≤ Node.val ≤ 100
```
