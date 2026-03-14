# 508. Most Frequent Subtree Sum

Given the root of a binary tree, return the **most frequent subtree sum**.

If there is a tie, return **all the values with the highest frequency** in **any order**.

---

## Definition

The **subtree sum** of a node is defined as the sum of all the node values formed by the subtree rooted at that node **(including the node itself)**.

---

## Example 1

**Input**

```
root = [5,2,-3]
```

**Output**

```
[2,-3,4]
```

**Explanation**

Subtree sums:

- Node 2 → sum = 2
- Node -3 → sum = -3
- Node 5 → sum = 5 + 2 + (-3) = 4

Each sum appears once, so all are returned.

---

## Example 2

**Input**

```
root = [5,2,-5]
```

**Output**

```
[2]
```

**Explanation**

Subtree sums:

- Node 2 → sum = 2
- Node -5 → sum = -5
- Node 5 → sum = 5 + 2 + (-5) = 2

The sum **2** appears twice, which is the highest frequency.

---

## Constraints

```
1 <= number of nodes <= 10^4
-10^5 <= Node.val <= 10^5
```
