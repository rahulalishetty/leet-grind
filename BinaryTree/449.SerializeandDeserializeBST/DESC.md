# 449. Serialize and Deserialize BST

Serialization is converting a data structure or object into a sequence of bits so that it can be:

- Stored in a file
- Stored in a memory buffer
- Transmitted across a network connection

Later, the serialized data can be **reconstructed (deserialized)** into the original structure in the same or another computer environment.

---

## Problem

Design an algorithm to **serialize** and **deserialize** a **Binary Search Tree (BST)**.

There is **no restriction** on how the algorithm should work, but it must satisfy the following:

- A BST can be serialized into a **string**
- The string can be **deserialized back into the original BST**
- The encoded string should be **as compact as possible**

---

## Example 1

Input

```
root = [2,1,3]
```

Output

```
[2,1,3]
```

---

## Example 2

Input

```
root = []
```

Output

```
[]
```

---

## Constraints

```
0 <= number of nodes <= 10^4
0 <= Node.val <= 10^4
```
