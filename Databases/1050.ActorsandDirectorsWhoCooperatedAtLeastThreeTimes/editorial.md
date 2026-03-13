# 1050. Actors and Directors Who Cooperated At Least Three Times — Detailed Summary

## Approach: `GROUP BY` and `COUNT`

This approach solves the problem by grouping the table by:

- `actor_id`
- `director_id`

Then it counts how many rows belong to each pair.

If a pair appears **three or more times**, that means the actor and director cooperated at least three times, so that pair is included in the result.

---

## Problem Restatement

Each row in `ActorDirector` represents one cooperation event between:

- an actor
- a director

We need to find all pairs:

```text
(actor_id, director_id)
```

such that the number of cooperation rows for that pair is at least:

```text
3
```

---

## Core Idea

Since every row is one cooperation, the total number of cooperations for a pair is simply the number of rows for that pair.

So the solution is:

1. group rows by `(actor_id, director_id)`
2. count rows in each group
3. keep only groups whose count is at least 3

This is a classic aggregation problem.

---

# Query

```sql
SELECT actor_id, director_id
FROM ActorDirector
GROUP BY actor_id, director_id
HAVING COUNT(timestamp) >= 3;
```

---

# Step-by-Step Explanation

## 1. Read from the table

```sql
FROM ActorDirector
```

This uses the `ActorDirector` table as the source of the data.

Each row represents one cooperation event.

---

## 2. Group by actor and director pair

```sql
GROUP BY actor_id, director_id
```

This forms one group for each distinct pair of:

- `actor_id`
- `director_id`

So all cooperation rows between the same actor and director are collected into one group.

For example, with this input:

| actor_id | director_id | timestamp |
| -------: | ----------: | --------: |
|        1 |           1 |         0 |
|        1 |           1 |         1 |
|        1 |           1 |         2 |
|        1 |           2 |         3 |
|        1 |           2 |         4 |
|        2 |           1 |         5 |
|        2 |           1 |         6 |

the groups are:

- `(1, 1)` → 3 rows
- `(1, 2)` → 2 rows
- `(2, 1)` → 2 rows

---

## 3. Count the number of rows in each group

```sql
COUNT(timestamp)
```

Because `timestamp` is the primary key and is unique per row, counting `timestamp` is equivalent to counting the number of cooperation records for that group.

So for the grouped pairs:

| actor_id | director_id | count |
| -------: | ----------: | ----: |
|        1 |           1 |     3 |
|        1 |           2 |     2 |
|        2 |           1 |     2 |

---

## 4. Filter groups with at least 3 cooperations

```sql
HAVING COUNT(timestamp) >= 3
```

`HAVING` filters after grouping.

This keeps only those actor-director pairs whose cooperation count is 3 or more.

So from the above grouped results, only:

| actor_id | director_id |
| -------: | ----------: |
|        1 |           1 |

remains.

---

## 5. Return only the required columns

```sql
SELECT actor_id, director_id
```

The problem only asks for the pair itself, not the count.

So the final result includes just these two columns.

---

# Worked Example

## Input

| actor_id | director_id | timestamp |
| -------: | ----------: | --------: |
|        1 |           1 |         0 |
|        1 |           1 |         1 |
|        1 |           1 |         2 |
|        1 |           2 |         3 |
|        1 |           2 |         4 |
|        2 |           1 |         5 |
|        2 |           1 |         6 |

---

## Grouped Counts

### Pair `(1, 1)`

Rows:

- `(1, 1, 0)`
- `(1, 1, 1)`
- `(1, 1, 2)`

Count:

```text
3
```

This qualifies because:

```text
3 >= 3
```

---

### Pair `(1, 2)`

Rows:

- `(1, 2, 3)`
- `(1, 2, 4)`

Count:

```text
2
```

This does not qualify.

---

### Pair `(2, 1)`

Rows:

- `(2, 1, 5)`
- `(2, 1, 6)`

Count:

```text
2
```

This also does not qualify.

---

## Final Output

| actor_id | director_id |
| -------: | ----------: |
|        1 |           1 |

---

# Why This Approach Is Correct

The problem defines cooperation count as the number of rows for each `(actor_id, director_id)` pair.

Grouping by those two columns directly captures that definition.

Then filtering with:

```sql
HAVING COUNT(timestamp) >= 3
```

exactly matches the requirement:

> cooperated at least three times

So the logic is both direct and complete.

---

# Clause-by-Clause Breakdown

## `SELECT actor_id, director_id`

Return the pair identifiers.

---

## `FROM ActorDirector`

Use the table containing cooperation events.

---

## `GROUP BY actor_id, director_id`

Collect all rows for the same pair together.

---

## `HAVING COUNT(timestamp) >= 3`

Keep only pairs with at least three cooperation rows.

---

# Why `HAVING` Is Used Instead of `WHERE`

This is an important SQL concept.

`COUNT(timestamp)` is an aggregate value, which only exists **after grouping**.

So aggregate conditions must be placed in `HAVING`, not `WHERE`.

Correct:

```sql
GROUP BY actor_id, director_id
HAVING COUNT(timestamp) >= 3
```

Incorrect:

```sql
WHERE COUNT(timestamp) >= 3
```

because `WHERE` is evaluated before grouping.

---

# Why `COUNT(timestamp)` Works

Since `timestamp` is unique and not null, counting it counts the number of rows in the group.

Equivalent alternatives could also be:

```sql
COUNT(*)
```

or

```sql
COUNT(1)
```

All of these would work here.

Because `timestamp` is guaranteed to exist for every row, `COUNT(timestamp)` is perfectly valid.

---

# Alternative Equivalent Query

A slightly more general form is:

```sql
SELECT actor_id, director_id
FROM ActorDirector
GROUP BY actor_id, director_id
HAVING COUNT(*) >= 3;
```

This is equivalent in this problem.

Some people prefer `COUNT(*)` because it explicitly means “count rows.”

---

# Complexity Analysis

Let `n` be the number of rows in `ActorDirector`.

The query performs:

- one grouping step by pair
- one count per group
- one filter on grouped results

This is efficient for the task and is the standard SQL solution for this type of counting-by-group problem.

---

# Final Recommended Query

```sql
SELECT actor_id, director_id
FROM ActorDirector
GROUP BY actor_id, director_id
HAVING COUNT(timestamp) >= 3;
```

---

# Key Takeaways

- Each row represents one cooperation
- Group by `(actor_id, director_id)` to collect all cooperations for a pair
- Use `COUNT(...)` to measure how many times they worked together
- Use `HAVING` to keep only pairs with count at least 3

---
