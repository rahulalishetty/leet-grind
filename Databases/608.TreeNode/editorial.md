# 608. Tree Node

## Detailed Summary of Three Accepted Approaches

We are given a table `Tree(id, p_id)` representing a valid tree.

Each node must be classified as one of:

- **Root**: the node whose `p_id` is `NULL`
- **Inner**: a node that has a parent and also has at least one child
- **Leaf**: a node that has a parent but has no children

The task is to return:

| id | type |

for every node.

---

# Core observation

Every node can be classified by answering two questions:

1. Does it have a parent?
2. Does it have any children?

That leads directly to the three categories:

## Root

A node is `Root` if:

```sql
p_id IS NULL
```

## Inner

A node is `Inner` if:

- it has a parent
- and its `id` appears as someone else's `p_id`

So:

```sql
p_id IS NOT NULL
AND id IN (SELECT p_id FROM tree ...)
```

## Leaf

A node is `Leaf` if:

- it has a parent
- and its `id` does not appear as anyone's `p_id`

So:

```sql
p_id IS NOT NULL
AND id NOT IN (SELECT p_id FROM tree ...)
```

That basic logic is the foundation behind all three approaches.

---

# Example 1

| id  | p_id |
| --- | ---- |
| 1   | NULL |
| 2   | 1    |
| 3   | 1    |
| 4   | 2    |
| 5   | 2    |

Tree structure:

```text
      1
     / \\
    2   3
   / \\
  4   5
```

Now classify each node:

- `1`: no parent -> `Root`
- `2`: has parent `1`, and has children `4` and `5` -> `Inner`
- `3`: has parent `1`, no children -> `Leaf`
- `4`: has parent `2`, no children -> `Leaf`
- `5`: has parent `2`, no children -> `Leaf`

Output:

| id  | type  |
| --- | ----- |
| 1   | Root  |
| 2   | Inner |
| 3   | Leaf  |
| 4   | Leaf  |
| 5   | Leaf  |

---

# Approach 1: Using `UNION`

## Core idea

This approach writes three separate queries:

1. one for roots
2. one for leaves
3. one for inner nodes

Then it combines them using `UNION`.

This is very direct because it translates each definition literally into SQL.

---

## Part 1: Root nodes

A root node has no parent:

```sql
SELECT
    id,
    'Root' AS Type
FROM
    tree
WHERE
    p_id IS NULL
```

### Why this works

If `p_id` is `NULL`, the node is not attached below any other node, so it is the root.

---

## Part 2: Leaf nodes

A leaf node:

- has a parent
- has no children

If a node has children, then its `id` must appear in the `p_id` column of some other row.

So leaf nodes are the nodes whose `id` **does not appear** in the non-null `p_id` values, while also having a non-null parent themselves.

```sql
SELECT
    id,
    'Leaf' AS Type
FROM
    tree
WHERE
    id NOT IN (
        SELECT DISTINCT
            p_id
        FROM
            tree
        WHERE
            p_id IS NOT NULL
    )
    AND p_id IS NOT NULL
```

### Why `p_id IS NOT NULL` is needed

Without it, the root could incorrectly fall into the leaf category if it had no children.

But a root should remain classified as `Root`, not `Leaf`.

So leaf nodes must explicitly have a parent.

---

## Part 3: Inner nodes

An inner node:

- has a parent
- and has children

That means:

- `p_id IS NOT NULL`
- and its `id` appears in `p_id` somewhere else

```sql
SELECT
    id,
    'Inner' AS Type
FROM
    tree
WHERE
    id IN (
        SELECT DISTINCT
            p_id
        FROM
            tree
        WHERE
            p_id IS NOT NULL
    )
    AND p_id IS NOT NULL
```

---

## Combine all parts with `UNION`

Now merge the three groups:

```sql
SELECT
    id, 'Root' AS Type
FROM
    tree
WHERE
    p_id IS NULL

UNION

SELECT
    id, 'Leaf' AS Type
FROM
    tree
WHERE
    id NOT IN (
        SELECT DISTINCT
            p_id
        FROM
            tree
        WHERE
            p_id IS NOT NULL
    )
    AND p_id IS NOT NULL

UNION

SELECT
    id, 'Inner' AS Type
FROM
    tree
WHERE
    id IN (
        SELECT DISTINCT
            p_id
        FROM
            tree
        WHERE
            p_id IS NOT NULL
    )
    AND p_id IS NOT NULL
ORDER BY id;
```

---

## Why `UNION` works here

Each node belongs to exactly one category:

- Root
- Inner
- Leaf

So combining the three result sets with `UNION` produces the final answer cleanly.

---

## Strengths of Approach 1

- very explicit
- easy to map to the written definitions
- helpful when learning the classification logic

### Tradeoffs

- more verbose
- repeats similar subqueries
- not as elegant as using conditional logic in one query

---

# Approach 2: Using flow control statement `CASE`

## Core idea

This approach keeps everything inside a single `SELECT`.

Instead of splitting the logic into multiple queries, it uses a `CASE` expression to decide the node type for each row.

This makes the code shorter and usually easier to read.

---

## Query

```sql
SELECT
    id AS `Id`,
    CASE
        WHEN tree.id = (
            SELECT atree.id
            FROM tree atree
            WHERE atree.p_id IS NULL
        )
        THEN 'Root'
        WHEN tree.id IN (
            SELECT atree.p_id
            FROM tree atree
        )
        THEN 'Inner'
        ELSE 'Leaf'
    END AS Type
FROM
    tree
ORDER BY `Id`;
```

---

## Step-by-step explanation

### `WHEN tree.id = (...) THEN 'Root'`

This checks whether the current row is the root node.

The subquery:

```sql
SELECT atree.id
FROM tree atree
WHERE atree.p_id IS NULL
```

returns the root node id.

If the current node id equals that value, it is classified as `Root`.

---

### `WHEN tree.id IN (...) THEN 'Inner'`

If the current node is not the root, the next check asks whether it appears as a parent id in the tree.

```sql
tree.id IN (SELECT atree.p_id FROM tree atree)
```

If true, it means the node has at least one child, so it is `Inner`.

---

### `ELSE 'Leaf'`

If the node is neither root nor inner, the only remaining possibility is leaf.

So:

```sql
ELSE 'Leaf'
```

---

## Why the order in `CASE` matters

The checks happen top to bottom.

So the root condition must come before the inner check.

Otherwise, in some tree variations, logic order could cause confusion.

The intended classification order is:

1. Root
2. Inner
3. Leaf

---

## Strengths of Approach 2

- shorter than the `UNION` version
- expresses the classification in one place
- easy to read once `CASE` is familiar

### Tradeoffs

- slightly less explicit than splitting into three separate queries
- still relies on subqueries inside conditions

---

# Approach 3: Using `IF` function

## Core idea

This is another single-query solution, similar in spirit to `CASE`, but using nested `IF()` calls.

In MySQL, `IF(condition, true_value, false_value)` is a compact way to express conditional logic.

So we can classify nodes like this:

1. if `p_id` is null -> `Root`
2. otherwise, if `id` appears in `p_id` -> `Inner`
3. otherwise -> `Leaf`

---

## Query

```sql
SELECT
    atree.id,
    IF(
        ISNULL(atree.p_id),
        'Root',
        IF(atree.id IN (SELECT p_id FROM tree), 'Inner', 'Leaf')
    ) Type
FROM
    tree atree
ORDER BY atree.id;
```

---

## Step-by-step explanation

### Outer `IF`

```sql
IF(ISNULL(atree.p_id), 'Root', ...)
```

This checks whether the current node has no parent.

If yes, return `Root`.

---

### Inner `IF`

If the node is not root, then evaluate:

```sql
IF(atree.id IN (SELECT p_id FROM tree), 'Inner', 'Leaf')
```

This asks:

- does the node appear as a parent somewhere?
  - yes -> `Inner`
  - no -> `Leaf`

---

## Why this works

This is the same logic as Approach 2, just expressed with nested `IF()` rather than `CASE`.

So conceptually:

```text
if no parent -> Root
else if has children -> Inner
else -> Leaf
```

---

## Strengths of Approach 3

- compact
- concise in MySQL
- easy once you are comfortable with nested `IF()`

### Tradeoffs

- less portable across SQL dialects than `CASE`
- nested `IF()` can become harder to read in more complex logic
- `CASE` is usually considered more standard SQL

---

# Comparing the three approaches

## Approach 1: `UNION`

### Best when

- you want to translate each definition literally
- you want the logic separated clearly by category

### Pros

- very explicit
- easy to reason about category by category

### Cons

- repetitive
- longer

---

## Approach 2: `CASE`

### Best when

- you want a standard SQL single-query solution
- you prefer cleaner conditional logic

### Pros

- concise
- readable
- more idiomatic than repeated unions

### Cons

- still uses subqueries in conditions

---

## Approach 3: `IF`

### Best when

- you are in MySQL
- you want the shortest compact expression

### Pros

- concise
- direct

### Cons

- more MySQL-specific
- nested `IF()` can become harder to read than `CASE`

---

# Walkthrough on Example 2

Input:

| id  | p_id |
| --- | ---- |
| 1   | NULL |

Now evaluate:

- `p_id IS NULL` -> yes
- so the node is `Root`

Output:

| id  | type |
| --- | ---- |
| 1   | Root |

This also shows why the root rule must come first.

A one-node tree is still a root, even though it has no children.

---

# Important SQL concept in this problem

The key trick is:

> A node has children if its `id` appears in the `p_id` column of some other row.

That is the central relationship behind both inner and leaf classification.

So these two checks are fundamental:

## Has children

```sql
id IN (SELECT p_id FROM tree WHERE p_id IS NOT NULL)
```

## Has no children

```sql
id NOT IN (SELECT p_id FROM tree WHERE p_id IS NOT NULL)
```

Combined with whether `p_id` is null or not, that fully determines the type.

---

# Final accepted implementations

## Approach 1: Using `UNION`

```sql
SELECT
    id, 'Root' AS Type
FROM
    tree
WHERE
    p_id IS NULL

UNION

SELECT
    id, 'Leaf' AS Type
FROM
    tree
WHERE
    id NOT IN (
        SELECT DISTINCT
            p_id
        FROM
            tree
        WHERE
            p_id IS NOT NULL
    )
    AND p_id IS NOT NULL

UNION

SELECT
    id, 'Inner' AS Type
FROM
    tree
WHERE
    id IN (
        SELECT DISTINCT
            p_id
        FROM
            tree
        WHERE
            p_id IS NOT NULL
    )
    AND p_id IS NOT NULL
ORDER BY id;
```

## Approach 2: Using `CASE`

```sql
SELECT
    id AS `Id`,
    CASE
        WHEN tree.id = (
            SELECT atree.id
            FROM tree atree
            WHERE atree.p_id IS NULL
        )
        THEN 'Root'
        WHEN tree.id IN (
            SELECT atree.p_id
            FROM tree atree
        )
        THEN 'Inner'
        ELSE 'Leaf'
    END AS Type
FROM
    tree
ORDER BY `Id`;
```

## Approach 3: Using `IF`

```sql
SELECT
    atree.id,
    IF(
        ISNULL(atree.p_id),
        'Root',
        IF(atree.id IN (SELECT p_id FROM tree), 'Inner', 'Leaf')
    ) Type
FROM
    tree atree
ORDER BY atree.id;
```

---

# Complexity

Let `n` be the number of nodes in `Tree`.

### Time Complexity

These solutions repeatedly check membership of ids in parent-id sets, so practical cost depends on indexing and the SQL engine.

At interview level, the important point is that all solutions are efficient enough for the problem and revolve around membership checks on the same table.

### Space Complexity

Additional space depends on subquery execution and intermediate results, but is generally modest.

---

# Key takeaways

1. A node is `Root` if `p_id IS NULL`.
2. A node is `Inner` if it has a parent and also appears as a parent of another node.
3. A node is `Leaf` if it has a parent but never appears as a parent.
4. The same classification logic can be expressed in multiple styles:
   - separate queries + `UNION`
   - single-query `CASE`
   - nested `IF()`
5. `CASE` is often the cleanest standard-SQL version, while `IF()` is a compact MySQL-specific alternative.
