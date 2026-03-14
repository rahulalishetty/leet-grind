# 2005. Subtree Removal Game with Fibonacci Tree

## Problem Description

A **Fibonacci tree** is a binary tree created using the function `order(n)`:

- **order(0)** → the empty tree.
- **order(1)** → a binary tree with only **one node**.
- **order(n)** → a binary tree consisting of:
  - a **root node**
  - **left subtree = order(n - 2)**
  - **right subtree = order(n - 1)**

## Game Rules

Two players **Alice** and **Bob** play a game with this Fibonacci tree.

- **Alice starts first.**
- On each turn, a player selects **any node** in the tree.
- The chosen node **and its entire subtree** are removed.
- The player who is **forced to delete the root node loses**.

Both players play **optimally**.

Your task is to determine:

> **Given `n`, does Alice win the game?**

Return:

- **`true`** → if Alice wins
- **`false`** → if Bob wins

## Definition: Subtree

A **subtree** of a binary tree consists of:

- a chosen node
- **all of its descendants**

The tree itself is also considered a subtree.

---

# Examples

## Example 1

Input

```
n = 3
```

Output

```
true
```

Explanation

1. Alice removes the node `1` from the **right subtree**.
2. Bob can remove either:
   - the `1` in the left subtree, or
   - the `2` in the right subtree.
3. Alice removes whichever node Bob didn't remove.
4. Bob is forced to remove the **root node (3)**.

Since the player forced to delete the root loses, **Bob loses and Alice wins**.

---

## Example 2

Input

```
n = 1
```

Output

```
false
```

Explanation

Alice has only one possible move:

- remove the **root node (1)**

Since removing the root causes the player to lose, **Alice loses**.

---

## Example 3

Input

```
n = 2
```

Output

```
true
```

Explanation

1. Alice removes the node `1`.
2. Bob is forced to remove the **root node (2)**.

Bob loses, so **Alice wins**.

---

# Constraints

```
1 <= n <= 100
```
