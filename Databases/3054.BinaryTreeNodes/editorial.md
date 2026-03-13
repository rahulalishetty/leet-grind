# 3054. Binary Tree Nodes — Detailed Summary

## Approach: Utilizing `CASE` Statements

This approach classifies every node in the binary tree by using SQL conditional logic.

For each node, we determine whether it is:

- `Root`
- `Inner`
- `Leaf`

The classification is based on two simple questions:

1. Does the node have a parent?
2. Does the node have at least one child?

The query answers these questions using:

- the `P` column directly
- a subquery over the parent column
- a `CASE` expression to assign the correct type

---

## Problem Restatement

The table `Tree` contains:

- `N` → the node value
- `P` → the parent of that node

We must classify each node as:

### Root

A node with no parent.

### Leaf

A node with no children.

### Inner

A node that is neither root nor leaf, meaning:

- it has a parent
- and it is also a parent of at least one other node

The output must be sorted by `N` in ascending order.

---

## Core Idea

A node's type can be determined by checking these conditions in order:

### 1. Root check

If `P IS NULL`, the node is the root.

### 2. Inner check

If the node's value `N` appears in the set of parent values `P`, then that node has at least one child, so it is an inner node.

### 3. Otherwise

If neither of the above is true, then the node has a parent but no children, so it is a leaf node.

This leads naturally to a `CASE` expression.

---

# Query

```sql
SELECT
  N,
  CASE
    WHEN P IS NULL THEN
      "Root"
    WHEN N IN (
      SELECT
        P
      FROM
        Tree
    ) THEN
      "Inner"
    ELSE
      "Leaf"
  END AS Type
FROM Tree
ORDER BY N;
```

---

# Step-by-Step Explanation

## 1. Select the node value

```sql
SELECT N
```

This returns the node itself.

Every row in the final result corresponds to one node from the tree.

---

## 2. Use `CASE` to determine the node type

```sql
CASE
  WHEN P IS NULL THEN "Root"
  WHEN N IN (SELECT P FROM Tree) THEN "Inner"
  ELSE "Leaf"
END AS Type
```

This is the heart of the solution.

The conditions are checked from top to bottom.

---

## Root Condition

```sql
WHEN P IS NULL THEN "Root"
```

### Why this works

The root node is the only node that has no parent.

In the `Tree` table, that means:

```sql
P IS NULL
```

So if the current row has `P = NULL`, we label it:

```text
Root
```

---

## Inner Condition

```sql
WHEN N IN (SELECT P FROM Tree) THEN "Inner"
```

### Why this works

A node is an inner node if it has at least one child.

In table terms, that means:

- the node's value `N`
- appears somewhere in the parent column `P`

Because if some row has `P = current N`, that means the current node is the parent of that child.

So the subquery:

```sql
SELECT P FROM Tree
```

collects all parent values.

If the current node value is found there, then the node has at least one child.

So it must be an inner node.

---

## Leaf Condition

```sql
ELSE "Leaf"
```

If the node is not root and does not appear as a parent, then:

- it has a parent
- it has no children

That makes it a leaf node.

---

# Example Walkthrough

## Input

|   N |    P |
| --: | ---: |
|   1 |    2 |
|   3 |    2 |
|   6 |    8 |
|   9 |    8 |
|   2 |    5 |
|   8 |    5 |
|   5 | NULL |

---

# Classify Each Node

## Node 5

Row:

|   N |    P |
| --: | ---: |
|   5 | NULL |

Since:

```sql
P IS NULL
```

Node `5` is:

```text
Root
```

---

## Node 2

Row:

|   N |   P |
| --: | --: |
|   2 |   5 |

Node `2` is not root because `P` is not null.

Now check whether `2` appears in the parent column:

Parent column values are:

```text
2, 2, 8, 8, 5, 5, NULL
```

Since `2` appears there, node `2` has children.

So node `2` is:

```text
Inner
```

---

## Node 8

Row:

|   N |   P |
| --: | --: |
|   8 |   5 |

Node `8` is not root.

Check whether `8` appears in the parent column.

It does, because:

- node `6` has parent `8`
- node `9` has parent `8`

So node `8` is:

```text
Inner
```

---

## Node 1

Row:

|   N |   P |
| --: | --: |
|   1 |   2 |

Node `1` is not root.

Check whether `1` appears in the parent column.

It does not.

So node `1` has no children.

Therefore node `1` is:

```text
Leaf
```

---

## Node 3

Row:

|   N |   P |
| --: | --: |
|   3 |   2 |

Node `3` is not root and does not appear as a parent.

So node `3` is:

```text
Leaf
```

---

## Node 6

Row:

|   N |   P |
| --: | --: |
|   6 |   8 |

Node `6` does not appear in the parent column.

So node `6` is:

```text
Leaf
```

---

## Node 9

Row:

|   N |   P |
| --: | --: |
|   9 |   8 |

Node `9` also does not appear in the parent column.

So node `9` is:

```text
Leaf
```

---

# Final Output

|   N | Type  |
| --: | ----- |
|   1 | Leaf  |
|   2 | Inner |
|   3 | Leaf  |
|   5 | Root  |
|   6 | Leaf  |
|   8 | Inner |
|   9 | Leaf  |

---

# Why the Order of Conditions Matters

The `CASE` conditions are checked in order.

This is important.

Suppose the root node also appears in the parent column, which is very common in trees because the root often has children.

For example, node `5` is:

- root because `P IS NULL`
- also a parent, because `2` and `8` both have parent `5`

If we checked the parent-column condition first, node `5` would incorrectly be labeled `Inner`.

That is why the root check must come before the inner-node check.

Correct order:

1. check `P IS NULL`
2. then check whether `N` appears in `P`
3. otherwise label as leaf

---

# Why `N IN (SELECT P FROM Tree)` Is Enough

To know whether a node has children, we only need to know whether its value appears in the parent column at least once.

We do not need to count how many children it has.

Even one appearance is enough to prove it is an inner node.

That is why `IN (...)` works cleanly here.

---

# Potential Note About `NULL` in the Parent Column

The parent column contains `NULL` for the root.

So the subquery:

```sql
SELECT P FROM Tree
```

may include `NULL`.

That does not break this logic, because the check is:

```sql
N IN (SELECT P FROM Tree)
```

and `N` values are actual integers, not nulls.

So the condition still works for determining whether a node appears as a parent.

---

# Cleaner Version Using Single Quotes

In SQL, string literals are more commonly written with single quotes.

So a cleaner version is:

```sql
SELECT
  N,
  CASE
    WHEN P IS NULL THEN 'Root'
    WHEN N IN (
      SELECT P
      FROM Tree
    ) THEN 'Inner'
    ELSE 'Leaf'
  END AS Type
FROM Tree
ORDER BY N;
```

This is functionally the same and is often more portable across SQL engines.

---

# Alternative Approach Note

Another possible approach is to use a self-join or `EXISTS` check instead of `IN`.

For example:

```sql
WHEN EXISTS (
  SELECT 1
  FROM Tree t2
  WHERE t2.P = Tree.N
) THEN 'Inner'
```

That is also valid.

But the `IN (SELECT P FROM Tree)` version is shorter and easy to understand.

---

# Complexity Discussion

Let `n` be the number of nodes.

The query scans the `Tree` table and, for each row, checks membership against the parent set.

Actual performance depends on the SQL engine and optimization of the subquery.

For this problem, the logic is simple and efficient enough.

---

# Final Recommended Query

```sql
SELECT
  N,
  CASE
    WHEN P IS NULL THEN 'Root'
    WHEN N IN (
      SELECT P
      FROM Tree
    ) THEN 'Inner'
    ELSE 'Leaf'
  END AS Type
FROM Tree
ORDER BY N;
```

---

# Key Takeaways

- A node is `Root` if `P IS NULL`
- A node is `Inner` if its value appears in the parent column
- Otherwise it is a `Leaf`
- The root condition must be checked first
- `CASE` is a natural fit for this classification problem

---
