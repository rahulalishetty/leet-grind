# 549. Binary Tree Longest Consecutive Sequence II

Given the root of a binary tree, return the **length of the longest consecutive path in the tree**.

A **consecutive path** is defined as a path where the values of consecutive nodes differ by **exactly one**.

The sequence can be either:

- **Increasing** (e.g., `1 → 2 → 3 → 4`)
- **Decreasing** (e.g., `4 → 3 → 2 → 1`)

However, sequences like:

```
[1,2,4,3]
```

are **not valid**, because the difference between adjacent nodes must always be **1**.

The path **does not have to follow strictly parent → child direction**.
It may also follow:

```
child → parent → child
```

This means the path may **pass through a node as a turning point**.

---

## Example 1

**Input**

```
root = [1,2,3]
```

**Output**

```
2
```

**Explanation**

The longest consecutive path can be:

```
[1,2]
```

or

```
[2,1]
```

---

## Example 2

**Input**

```
root = [2,1,3]
```

**Output**

```
3
```

**Explanation**

The longest consecutive path is:

```
[1,2,3]
```

or

```
[3,2,1]
```

---

## Constraints

```
1 <= number of nodes <= 3 * 10^4
-3 * 10^4 <= Node.val <= 3 * 10^4
```
