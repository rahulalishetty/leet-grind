# 620. Not Boring Movies — Detailed Summary

## Approach 1: Using `MOD()` Function

This approach solves the problem by applying two filters directly in the `WHERE` clause:

1. keep only rows whose `id` is odd
2. exclude rows whose `description` is `'boring'`

Then it sorts the remaining movies by `rating` in descending order.

This is a straightforward filtering query.

---

## Problem Restatement

We need to return movies that satisfy both conditions:

- `id` is odd-numbered
- `description` is not `'boring'`

After filtering, the result must be ordered by:

```sql
rating DESC
```

---

## Query

```sql
SELECT *
FROM cinema
WHERE MOD(id, 2) = 1
  AND description != 'boring'
ORDER BY rating DESC;
```

---

# Step-by-Step Explanation

## 1. Select all columns

```sql
SELECT *
FROM cinema
```

This retrieves all columns from the `Cinema` table:

- `id`
- `movie`
- `description`
- `rating`

Because the output format requires all of these columns, `SELECT *` works fine here.

---

## 2. Filter for odd-numbered IDs

```sql
MOD(id, 2) = 1
```

### Why this works

The `MOD(a, b)` function returns the remainder when `a` is divided by `b`.

So:

- `MOD(1, 2) = 1`
- `MOD(3, 2) = 1`
- `MOD(5, 2) = 1`

but:

- `MOD(2, 2) = 0`
- `MOD(4, 2) = 0`

That means:

```sql
MOD(id, 2) = 1
```

keeps only odd numbers.

---

## 3. Exclude boring movies

```sql
description != 'boring'
```

This removes rows where the description is exactly `'boring'`.

So any movie with:

```text
description = 'boring'
```

is excluded from the result.

---

## 4. Combine both conditions

```sql
WHERE MOD(id, 2) = 1
  AND description != 'boring'
```

This means a movie is kept only if:

- it has an odd ID
- and it is not boring

Both must be true.

---

## 5. Sort by rating descending

```sql
ORDER BY rating DESC
```

After filtering, the remaining movies are sorted from highest rating to lowest rating.

That matches the problem requirement.

---

# Worked Example

## Input

|  id | movie      | description | rating |
| --: | ---------- | ----------- | -----: |
|   1 | War        | great 3D    |    8.9 |
|   2 | Science    | fiction     |    8.5 |
|   3 | irish      | boring      |    6.2 |
|   4 | Ice song   | Fantacy     |    8.6 |
|   5 | House card | Interesting |    9.1 |

---

# Step 1: Keep Odd IDs

Apply:

```sql
MOD(id, 2) = 1
```

Remaining rows:

|  id | movie      | description | rating |
| --: | ---------- | ----------- | -----: |
|   1 | War        | great 3D    |    8.9 |
|   3 | irish      | boring      |    6.2 |
|   5 | House card | Interesting |    9.1 |

---

# Step 2: Remove `"boring"` Descriptions

Apply:

```sql
description != 'boring'
```

Remaining rows:

|  id | movie      | description | rating |
| --: | ---------- | ----------- | -----: |
|   1 | War        | great 3D    |    8.9 |
|   5 | House card | Interesting |    9.1 |

---

# Step 3: Sort by Rating Descending

Apply:

```sql
ORDER BY rating DESC
```

Final result:

|  id | movie      | description | rating |
| --: | ---------- | ----------- | -----: |
|   5 | House card | Interesting |    9.1 |
|   1 | War        | great 3D    |    8.9 |

---

# Why This Approach Is Correct

The problem only asks for a simple row-level filter.

There is no grouping, joining, or aggregation involved.

So a direct `WHERE` clause is enough.

The logic matches the requirements exactly:

- odd IDs are identified using `MOD(id, 2) = 1`
- boring movies are excluded using `description != 'boring'`
- final ordering is handled with `ORDER BY rating DESC`

---

# Clause-by-Clause Breakdown

## `SELECT *`

Returns all columns from the table.

---

## `FROM cinema`

Reads data from the `Cinema` table.

---

## `WHERE MOD(id, 2) = 1`

Keeps only rows with odd `id`.

---

## `AND description != 'boring'`

Removes rows whose description is `'boring'`.

---

## `ORDER BY rating DESC`

Sorts the result from highest rating to lowest.

---

# Alternative Ways to Check Odd IDs

`MOD(id, 2) = 1` is the most common and clear way.

Depending on SQL dialect, you may also see:

```sql
id % 2 = 1
```

That is equivalent in many databases.

But `MOD(id, 2)` is often more portable and explicit.

---

# Case Sensitivity Note

The query uses:

```sql
description != 'boring'
```

This checks exact string equality according to the SQL engine's collation and comparison rules.

In this problem, the example and requirement explicitly use the lowercase value `'boring'`, so this is appropriate.

---

# Complexity Analysis

Let `n` be the number of rows in `Cinema`.

The query scans the table once and applies simple row-level filters, then sorts the filtered rows by rating.

So conceptually:

- filtering is linear in `n`
- sorting depends on the number of filtered rows

This is efficient for the task.

---

# Final Recommended Query

```sql
SELECT *
FROM cinema
WHERE MOD(id, 2) = 1
  AND description != 'boring'
ORDER BY rating DESC;
```

---

# Key Takeaways

- Use `MOD(id, 2) = 1` to keep odd IDs
- Use `description != 'boring'` to exclude boring movies
- Use `ORDER BY rating DESC` to sort by rating from highest to lowest
- This problem is a simple filtering query, so no joins or aggregation are needed

---
