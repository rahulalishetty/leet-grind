# 2792. Count Nodes That Are Great Enough

## Problem

You are given the `root` of a binary tree and an integer `k`.

A node in the tree is called **great enough** if both of the following conditions hold:

1. Its **subtree contains at least `k` nodes**.
2. Its **value is greater than the value of at least `k` nodes in its subtree**.

### Definition

A node `u` is in the subtree of node `v` if:

```
u == v OR v is an ancestor of u
```

---

# Goal

Return the **number of nodes in the tree that are great enough**.

---

# Example 1

### Input

```
root = [7,6,5,4,3,2,1]
k = 2
```

### Output

```
3
```

### Explanation

Subtree values:

- Node 1 → `{1,2,3,4,5,6,7}`
  - `7` is greater than **6 nodes**
  - satisfies condition

- Node 2 → `{3,4,6}`
  - `6` is greater than **2 nodes**
  - satisfies condition

- Node 3 → `{1,2,5}`
  - `5` is greater than **2 nodes**
  - satisfies condition

Other nodes do not satisfy the condition.

---

# Example 2

### Input

```
root = [1,2,3]
k = 1
```

### Output

```
0
```

### Explanation

- Node 1 → `{1,2,3}` → no value smaller than `1`
- Node 2 → `{2}` → no smaller values
- Node 3 → `{3}` → no smaller values

No node satisfies the condition.

---

# Example 3

### Input

```
root = [3,2,2]
k = 2
```

### Output

```
1
```

### Explanation

Subtree values:

- Node 1 → `{2,2,3}`
  - `3` is greater than **2 nodes**
  - satisfies condition

Other nodes do not.

---

# Constraints

```
1 ≤ number of nodes ≤ 10^4
1 ≤ Node.val ≤ 10^4
1 ≤ k ≤ 10
```
