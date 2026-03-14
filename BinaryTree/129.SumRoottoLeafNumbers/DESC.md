# 129. Sum Root to Leaf Numbers

## Problem

You are given the **root of a binary tree** where each node contains a **digit from 0 to 9**.

Each **root-to-leaf path** in the tree represents a number.

For example:

```
1 → 2 → 3
```

represents the number:

```
123
```

Return the **total sum of all root-to-leaf numbers**.

Test cases guarantee the answer fits within a **32-bit integer**.

A **leaf node** is a node with **no children**.

---

# Examples

## Example 1

**Input**

```
root = [1,2,3]
```

**Output**

```
25
```

**Explanation**

Paths:

```
1 → 2 = 12
1 → 3 = 13
```

Total sum:

```
12 + 13 = 25
```

---

## Example 2

**Input**

```
root = [4,9,0,5,1]
```

**Output**

```
1026
```

**Explanation**

Paths:

```
4 → 9 → 5 = 495
4 → 9 → 1 = 491
4 → 0 = 40
```

Total sum:

```
495 + 491 + 40 = 1026
```

---

# Constraints

- Number of nodes: **[1, 1000]**
- Node values: **0 ≤ Node.val ≤ 9**
- Maximum tree depth: **10**
