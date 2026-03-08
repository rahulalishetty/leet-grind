# Approach 1: Window Function — `DENSE_RANK()`

## Intuition

A **window function** computes a result for **each row**, using a “window” of related rows, without collapsing rows into groups (unlike `GROUP BY`).

For ranking with ties and **no gaps**, `DENSE_RANK()` is ideal.

### What `DENSE_RANK()` does

- Orders rows (globally or within partitions)
- Equal scores are **peers** and get the **same rank**
- Next distinct score gets the next consecutive rank (dense ranking)

## Implementation (MySQL 8.0+)

```sql
SELECT
  S.score,
  DENSE_RANK() OVER (ORDER BY S.score DESC) AS `rank`
FROM Scores S;
```

## Why this is preferred

- **Simplest** and most readable
- Typically **fast** (optimizer can compute ranks efficiently)
- Directly expresses the ranking requirement

## Compatibility note

- MySQL supports window functions starting from **MySQL 8.0** (released April 2018).
- If you are on MySQL 5.7 or older, use Approach 2 or 3.

---

# Approach 2: Correlated Subquery with `COUNT(DISTINCT ...)`

## Intuition

For each score `S1.score`, if you count how many **distinct scores** are **>=** it, you get its dense rank.

Example:

- If scores ≥ 3.65 are `{4.00, 3.85, 3.65}` → count = 3 → rank is 3.

## Algorithm

For each row `S1`:

1. Look at all rows `S2` where `S2.score >= S1.score`
2. Count distinct `S2.score`
3. That count is the rank
4. Order final output by score descending

## Implementation

```sql
SELECT
  S1.score,
  (
    SELECT COUNT(DISTINCT S2.score)
    FROM Scores S2
    WHERE S2.score >= S1.score
  ) AS `rank`
FROM Scores S1
ORDER BY S1.score DESC;
```

## Practical notes

- This is conceptually elegant (shows you understand ranking logic).
- But it can be **slow** on large tables because the subquery can be executed per row (unless the optimizer rewrites/optimizes it).

---

# Approach 3: Self `INNER JOIN` + `COUNT(DISTINCT ...)`

## Intuition

Same math as Approach 2, but instead of running a correlated subquery per row, we produce the “>= score” matches via a self-join, then aggregate.

## Algorithm

1. Join `Scores S` with `Scores T` such that `T.score >= S.score` (equivalently `S.score <= T.score`)
2. For each original row `S`, count **distinct** `T.score` values in its joined matches
3. That count is the rank
4. Order by `S.score` descending

## Implementation

```sql
SELECT
  S.score,
  COUNT(DISTINCT T.score) AS `rank`
FROM Scores S
INNER JOIN Scores T
  ON S.score <= T.score
GROUP BY S.id, S.score
ORDER BY S.score DESC;
```

### Why `GROUP BY S.id, S.score`?

- Each original row `S` forms a group.
- The `COUNT(DISTINCT T.score)` inside that group yields the rank for that row.

---

## How Approach 3 works (expanded view)

This query shows the join pairs before aggregation:

```sql
SELECT
  S.id    AS S_ID,
  S.score AS S_score,
  T.id    AS T_ID,
  T.score AS T_score
FROM Scores S
INNER JOIN Scores T
  ON S.score <= T.score
ORDER BY S.id, T.score;
```

```note
+------+---------+------+---------+
| S_ID | S_score | T_ID | T_score |
+------+---------+------+---------+
|    1 |    3.50 |    1 |    3.50 |
|    1 |    3.50 |    2 |    3.65 |
|    1 |    3.50 |    6 |    3.65 |
|    1 |    3.50 |    4 |    3.85 |
|    1 |    3.50 |    3 |    4.00 |
|    1 |    3.50 |    5 |    4.00 |
|    2 |    3.65 |    2 |    3.65 |
|    2 |    3.65 |    6 |    3.65 |
|    2 |    3.65 |    4 |    3.85 |
|    2 |    3.65 |    3 |    4.00 |
|    2 |    3.65 |    5 |    4.00 |
|    3 |    4.00 |    3 |    4.00 |
|    3 |    4.00 |    5 |    4.00 |
|    4 |    3.85 |    4 |    3.85 |
|    4 |    3.85 |    3 |    4.00 |
|    4 |    3.85 |    5 |    4.00 |
|    5 |    4.00 |    3 |    4.00 |
|    5 |    4.00 |    5 |    4.00 |
|    6 |    3.65 |    2 |    3.65 |
|    6 |    3.65 |    6 |    3.65 |
|    6 |    3.65 |    4 |    3.85 |
|    6 |    3.65 |    3 |    4.00 |
|    6 |    3.65 |    5 |    4.00 |
+------+---------+------+---------+
```

Interpretation for one row:

- For `S_ID = 1, S_score = 3.50`, the joined `T_score` values include all scores ≥ 3.50.
- Distinct scores might be `{3.50, 3.65, 3.85, 4.00}` → count = 4 → rank = 4.

This produces the desired output pattern:

```text
score  rank
4.00   1
4.00   1
3.85   2
3.65   3
3.65   3
3.50   4
```

---

# Summary: Which approach to choose?

## Best overall (modern SQL)

**Approach 1 (DENSE_RANK)**:

- Cleanest
- Most direct
- Usually best performance
- Requires MySQL 8.0+

## If window functions aren’t allowed/available

**Approach 2 (correlated subquery)**:

- Very clear logic
- Can be slower at scale

**Approach 3 (self-join + group)**:

- Often faster than Approach 2 in older SQL engines
- Shows creativity and set-based reasoning
- Can still be heavy because the join expands row counts

---
