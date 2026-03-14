# 666. Path Sum IV

If the **depth of a binary tree is smaller than 5**, the tree can be encoded using an array of **three‑digit integers**.

Each integer in the array describes a node in the tree.

The array is **sorted in ascending order**.

---

## Encoding Format

Each integer has **three digits**:

| Digit    | Meaning                                            |
| -------- | -------------------------------------------------- |
| Hundreds | Depth `d` of the node (1 ≤ d ≤ 4)                  |
| Tens     | Position `p` of the node in that level (1 ≤ p ≤ 8) |
| Units    | Value `v` stored in the node (0 ≤ v ≤ 9)           |

The position corresponds to the node's index in a **full binary tree representation**.

---

## Problem Goal

Construct the tree implicitly from the encoded values and return:

```
The sum of all root‑to‑leaf path sums.
```

A **path sum** is the sum of node values from the **root to a leaf**.

---

## Example 1

**Input**

```
nums = [113,215,221]
```

**Output**

```
12
```

**Explanation**

The encoded tree corresponds to:

```
    3
   / \\
  5   1
```

Paths:

```
3 → 5 = 8
3 → 1 = 4
```

Total path sum:

```
8 + 4 = 12
```

---

## Example 2

**Input**

```
nums = [113,221]
```

**Output**

```
4
```

**Explanation**

The tree corresponds to:

```
   3
    \\
     1
```

Path:

```
3 → 1 = 4
```

Total path sum:

```
4
```

---

## Constraints

```
1 ≤ nums.length ≤ 15
110 ≤ nums[i] ≤ 489
```

Additional guarantees:

- The tree depth is **less than 5**
- The array represents a **valid connected binary tree**
- The input array is **sorted in ascending order**
