# 601. Human Traffic of Stadium

## Detailed Summary of Three Accepted Approaches

This problem asks us to return all rows from `Stadium` that belong to a run of **at least three consecutive `id` values**, where **every row in that run has `people >= 100`**.

Two things matter:

1. **Consecutive `id` values**, not consecutive dates
2. **Every row in the qualifying run must have `people >= 100`**

The result must be ordered by `visit_date` ascending.

---

# Problem restated in plain language

We only care about rows where:

```sql
people >= 100
```

Among those rows, we want to find sequences such as:

```text
5, 6, 7
```

or

```text
5, 6, 7, 8
```

where the `id`s are consecutive.

If a row is part of a qualifying sequence of length **3 or more**, we return it.

---

# Example input

| id  | visit_date | people |
| --- | ---------- | ------ |
| 1   | 2017-01-01 | 10     |
| 2   | 2017-01-02 | 109    |
| 3   | 2017-01-03 | 150    |
| 4   | 2017-01-04 | 99     |
| 5   | 2017-01-05 | 145    |
| 6   | 2017-01-06 | 1455   |
| 7   | 2017-01-07 | 199    |
| 8   | 2017-01-09 | 188    |

After filtering to only rows with `people >= 100`, we get:

| id  | visit_date | people |
| --- | ---------- | ------ |
| 2   | 2017-01-02 | 109    |
| 3   | 2017-01-03 | 150    |
| 5   | 2017-01-05 | 145    |
| 6   | 2017-01-06 | 1455   |
| 7   | 2017-01-07 | 199    |
| 8   | 2017-01-09 | 188    |

Now look at the `id`s:

- `2, 3` -> only length 2, so not enough
- `5, 6, 7, 8` -> length 4, valid

So the output is:

| id  | visit_date | people |
| --- | ---------- | ------ |
| 5   | 2017-01-05 | 145    |
| 6   | 2017-01-06 | 1455   |
| 7   | 2017-01-07 | 199    |
| 8   | 2017-01-09 | 188    |

Notice that `2017-01-09` is still included even though it is not the day right after `2017-01-07`.
That is correct because the requirement is about **consecutive ids**, not consecutive dates.

---

# General note on methods

There are multiple ways to solve consecutive-value problems.

This write-up covers three:

1. **Self-join**
2. **Window functions using `LEAD()` and `LAG()`**
3. **Gap and island approach**

A good practical rule:

- if the required consecutive count is small and fixed, self-join can work
- if you want a cleaner scalable SQL approach, window functions are usually better
- if you want the most elegant general pattern for consecutive groups, learn **gap and island**

The same core idea appears in many SQL problems involving consecutive dates, ids, or streaks.

---

# Approach 1: Using Self-Join

## Core idea

If we need to detect exactly **three consecutive ids**, we can compare rows from three aliases of the same table:

- `a`
- `b`
- `c`

We keep only rows where all three have:

```sql
people >= 100
```

Then we check whether their `id` values differ by exactly 1.

Because we ultimately want to return rows from only one alias (`a`), we need to consider every possible position of `a` inside a 3-row consecutive block.

---

## Step 1: Start with three aliases

```sql
SELECT *
FROM stadium AS a, stadium AS b, stadium AS c
WHERE
    a.people >= 100
    AND b.people >= 100
    AND c.people >= 100;
```

This creates combinations of three stadium rows, while filtering each alias to rows with enough people.

---

## Step 2: Detect consecutive ids

If three ids are consecutive, then the difference between adjacent ids is 1.

For example, if the order is:

```text
a.id > b.id > c.id
```

then:

```sql
a.id - b.id = 1
AND b.id - c.id = 1
```

But the selected alias `a` may not always be the largest id in the consecutive triple. It may be:

- the smallest id
- the middle id
- the largest id

So we need to account for all three possibilities.

---

## Three possible positions for `a`

### 1. `a` is the maximum id

Order:

```text
a.id > b.id > c.id
```

Condition:

```sql
a.id - b.id = 1 AND b.id - c.id = 1
```

### 2. `a` is the minimum id

Order:

```text
c.id > b.id > a.id
```

Condition:

```sql
c.id - b.id = 1 AND b.id - a.id = 1
```

### 3. `a` is the middle id

Order:

```text
b.id > a.id > c.id
```

Condition:

```sql
b.id - a.id = 1 AND a.id - c.id = 1
```

If any one of these is true, then row `a` belongs to some consecutive triple.

---

## Final self-join query

```sql
SELECT DISTINCT a.*
FROM stadium AS a, stadium AS b, stadium AS c
WHERE
    a.people >= 100
    AND b.people >= 100
    AND c.people >= 100
    AND (
           (a.id - b.id = 1 AND b.id - c.id = 1)
        OR (c.id - b.id = 1 AND b.id - a.id = 1)
        OR (b.id - a.id = 1 AND a.id - c.id = 1)
    )
ORDER BY visit_date;
```

---

## Why `DISTINCT` is needed

The same row `a` can match multiple `(b, c)` combinations.

So without `DISTINCT`, duplicate result rows may appear.

`DISTINCT` removes those duplicates.

---

## Strengths and limitations of self-join

### Strengths

- straightforward once the consecutive count is fixed
- works without window functions

### Limitations

- becomes clumsy as the required streak length grows
- if the problem asked for 5 consecutive rows, you would need 5 aliases
- readability and maintainability worsen quickly

So this is acceptable for small fixed lengths, but not the most elegant long-term pattern.

---

# Approach 2: Using Window Functions

## Core idea

Instead of self-joining multiple copies of the table, we can attach nearby ids to each row using:

- `LEAD()` for next rows
- `LAG()` for previous rows

Then, for each current row, we check whether it can be:

- the first row in a consecutive triple
- the middle row in a consecutive triple
- the last row in a consecutive triple

This avoids manual multi-table joins and is usually cleaner.

---

## Step 1: Filter and append neighboring ids

We first keep only rows with `people >= 100`.

Then we compute:

- the next id
- the second next id
- the previous id
- the second previous id

using a CTE.

```sql
WITH base AS (
    SELECT *,
        LEAD(id, 1) OVER (ORDER BY id) AS next_id,
        LEAD(id, 2) OVER (ORDER BY id) AS second_next_id,
        LAG(id, 1)  OVER (ORDER BY id) AS last_id,
        LAG(id, 2)  OVER (ORDER BY id) AS second_last_id
    FROM stadium
    WHERE people >= 100
)
```

---

## What this CTE looks like

For the example, after filtering to `people >= 100`, the rows are:

| id  | visit_date | people | next_id | second_next_id | last_id | second_last_id |
| --- | ---------- | ------ | ------- | -------------- | ------- | -------------- |
| 2   | 2017-01-02 | 109    | 3       | 5              | NULL    | NULL           |
| 3   | 2017-01-03 | 150    | 5       | 6              | 2       | NULL           |
| 5   | 2017-01-05 | 145    | 6       | 7              | 3       | 2              |
| 6   | 2017-01-06 | 1455   | 7       | 8              | 5       | 3              |
| 7   | 2017-01-07 | 199    | 8       | NULL           | 6       | 5              |
| 8   | 2017-01-09 | 188    | NULL    | NULL           | 7       | 6              |

Notice something important:

Rows like `id = 4` are already excluded because `people < 100`.

That is exactly what we want, because consecutive sequences should be formed only among qualifying rows.

---

## Step 2: Check whether current row is part of a triple

For each row, the current `id` can be in one of three positions inside a 3-row consecutive sequence.

### Case 1: current row is in the middle

Order:

```text
next_id > id > last_id
```

Condition:

```sql
next_id - id = 1
AND id - last_id = 1
```

### Case 2: current row is the minimum id

Order:

```text
second_next_id > next_id > id
```

Condition:

```sql
second_next_id - next_id = 1
AND next_id - id = 1
```

### Case 3: current row is the maximum id

Order:

```text
id > last_id > second_last_id
```

Condition:

```sql
id - last_id = 1
AND last_id - second_last_id = 1
```

If any of these is true, the row belongs to a valid consecutive block of length at least 3.

---

## Final window-function query

```sql
WITH base AS (
    SELECT *,
        LEAD(id, 1) OVER (ORDER BY id) AS next_id,
        LEAD(id, 2) OVER (ORDER BY id) AS second_next_id,
        LAG(id, 1)  OVER (ORDER BY id) AS last_id,
        LAG(id, 2)  OVER (ORDER BY id) AS second_last_id
    FROM stadium
    WHERE people >= 100
)
SELECT DISTINCT id, visit_date, people
FROM base
WHERE (next_id - id = 1 AND id - last_id = 1)
   OR (second_next_id - next_id = 1 AND next_id - id = 1)
   OR (id - last_id = 1 AND last_id - second_last_id = 1)
ORDER BY visit_date;
```

---

## Why `DISTINCT` is included here too

A row might satisfy more than one of the three cases.

For example, a row in a longer sequence such as `5, 6, 7, 8` can simultaneously be:

- the middle of one 3-row window
- the start of another
- the end of another

`DISTINCT` ensures each row appears once.

---

## Strengths and tradeoffs of window functions

### Strengths

- cleaner than self-join
- easier to scale conceptually
- no manual cross-table alias explosion
- window functions are powerful and reusable

### Tradeoffs

- still tied to a fixed length of 3 in this exact formulation
- for more flexible “all islands” logic, gap-and-island is often even better

---

# Approach 3: Finding the Islands

## Core idea

This is the classic **gap and island** approach.

We first keep only rows with:

```sql
people >= 100
```

Then we assign a sequential rank to those rows ordered by `id`.

If the `id`s are consecutive, then the difference:

```text
id - rank
```

stays constant across the whole consecutive block.

Rows sharing the same difference belong to the same **island**.

Then we group by that island identifier and keep only islands whose size is at least 3.

This is the most elegant general methodology among the three.

---

## Step 1: Rank qualifying rows

Start by keeping only rows with enough people and assigning a rank.

```sql
SELECT id, visit_date, people, RANK() OVER (ORDER BY id) AS rnk
FROM Stadium
WHERE people >= 100;
```

For the example, that produces:

| id  | visit_date | people | rnk |
| --- | ---------- | ------ | --- |
| 2   | 2017-01-02 | 109    | 1   |
| 3   | 2017-01-03 | 150    | 2   |
| 5   | 2017-01-05 | 145    | 3   |
| 6   | 2017-01-06 | 1455   | 4   |
| 7   | 2017-01-07 | 199    | 5   |
| 8   | 2017-01-09 | 188    | 6   |

---

## Step 2: Build the island identifier

Now calculate:

```sql
id - rnk
```

If ids are consecutive, that value stays the same.

Examples:

- for `2, 3` with ranks `1, 2`:
  - `2 - 1 = 1`
  - `3 - 2 = 1`

- for `5, 6, 7, 8` with ranks `3, 4, 5, 6`:
  - `5 - 3 = 2`
  - `6 - 4 = 2`
  - `7 - 5 = 2`
  - `8 - 6 = 2`

So the shared difference identifies the island.

We store this in a CTE.

```sql
WITH stadium_with_rnk AS (
    SELECT id, visit_date, people, rnk, (id - rnk) AS island
    FROM (
        SELECT id, visit_date, people, RANK() OVER (ORDER BY id) AS rnk
        FROM Stadium
        WHERE people >= 100
    ) AS t0
)
```

---

## What the CTE looks like

| id  | visit_date | people | rnk | island |
| --- | ---------- | ------ | --- | ------ |
| 2   | 2017-01-02 | 109    | 1   | 1      |
| 3   | 2017-01-03 | 150    | 2   | 1      |
| 5   | 2017-01-05 | 145    | 3   | 2      |
| 6   | 2017-01-06 | 1455   | 4   | 2      |
| 7   | 2017-01-07 | 199    | 5   | 2      |
| 8   | 2017-01-09 | 188    | 6   | 2      |

Now the islands are easy to see:

- island `1` -> rows `2, 3` -> size 2
- island `2` -> rows `5, 6, 7, 8` -> size 4

We only want islands with size at least 3.

---

## Step 3: Keep only large islands

Group by `island` and keep islands with at least 3 rows.

```sql
SELECT island
FROM stadium_with_rnk
GROUP BY island
HAVING COUNT(*) >= 3;
```

For the example, the result is:

| island |
| ------ |
| 2      |

Now we simply return rows whose island is one of these valid islands.

---

## Final gap-and-island query

```sql
WITH stadium_with_rnk AS (
    SELECT id, visit_date, people, rnk, (id - rnk) AS island
    FROM (
        SELECT id, visit_date, people, RANK() OVER (ORDER BY id) AS rnk
        FROM Stadium
        WHERE people >= 100
    ) AS t0
)
SELECT id, visit_date, people
FROM stadium_with_rnk
WHERE island IN (
    SELECT island
    FROM stadium_with_rnk
    GROUP BY island
    HAVING COUNT(*) >= 3
)
ORDER BY visit_date;
```

---

## Why gap-and-island is powerful

This method is especially useful because it generalizes well.

Instead of checking a fixed triple manually, it identifies whole blocks of consecutive values naturally.

That makes it easier to solve many other SQL streak problems, such as:

- consecutive login days
- consecutive order ids
- consecutive dates with activity
- longest streak problems

Once you understand this pattern, many “consecutive values” questions become much easier.

---

# Comparing the three approaches

## 1. Self-join

### Best when

- the required streak length is very small
- you want a solution without window functions

### Downsides

- verbose
- hard to scale
- awkward for longer sequences

---

## 2. Window functions with `LEAD()` / `LAG()`

### Best when

- you want a cleaner fixed-window solution
- your SQL dialect supports window functions

### Downsides

- still somewhat tied to a fixed pattern size in the conditions

---

## 3. Gap and island

### Best when

- you want the most elegant and general method
- the problem is fundamentally about consecutive groups

### Downsides

- requires understanding the island idea
- slightly more abstract at first

---

# Final accepted implementations

## Approach 1: Self-join

```sql
SELECT DISTINCT a.*
FROM stadium AS a, stadium AS b, stadium AS c
WHERE
     a.people >= 100 AND b.people >= 100 AND c.people >= 100
AND (
       (a.id - b.id = 1 AND b.id - c.id = 1)
    OR (c.id - b.id = 1 AND b.id - a.id = 1)
    OR (b.id - a.id = 1 AND a.id - c.id = 1)
)
ORDER BY visit_date;
```

## Approach 2: Window functions

```sql
WITH base AS (
    SELECT *,
        LEAD(id, 1) OVER (ORDER BY id) AS next_id,
        LEAD(id, 2) OVER (ORDER BY id) AS second_next_id,
        LAG(id, 1)  OVER (ORDER BY id) AS last_id,
        LAG(id, 2)  OVER (ORDER BY id) AS second_last_id
    FROM stadium
    WHERE people >= 100
)
SELECT DISTINCT id, visit_date, people
FROM base
WHERE (next_id - id = 1 AND id - last_id = 1)
   OR (second_next_id - next_id = 1 AND next_id - id = 1)
   OR (id - last_id = 1 AND last_id - second_last_id = 1)
ORDER BY visit_date;
```

## Approach 3: Finding the islands

```sql
WITH stadium_with_rnk AS (
    SELECT id, visit_date, people, rnk, (id - rnk) AS island
    FROM (
        SELECT id, visit_date, people, RANK() OVER (ORDER BY id) AS rnk
        FROM Stadium
        WHERE people >= 100
    ) AS t0
)
SELECT id, visit_date, people
FROM stadium_with_rnk
WHERE island IN (
    SELECT island
    FROM stadium_with_rnk
    GROUP BY island
    HAVING COUNT(*) >= 3
)
ORDER BY visit_date;
```

---

# Complexity discussion

The exact runtime depends on the SQL engine, indexing, and execution plan, but conceptually:

## Self-join

This can be significantly heavier because it compares combinations across multiple aliases.

## Window function approach

Usually cleaner and more efficient than the self-join for this kind of task.

## Gap-and-island

Also efficient and elegant for sequence grouping, especially when window functions are available.

For interview-style reasoning, the important part is usually not the exact asymptotic formula, but understanding the tradeoff:

- self-join is brute-force-ish
- window and island methods are more scalable and expressive

---

# Key takeaways

1. The condition is about **consecutive ids**, not consecutive dates.
2. Always filter to `people >= 100` before analyzing the sequences.
3. A row should be returned if it belongs to any run of at least 3 consecutive qualifying ids.
4. Self-join works for small fixed streak sizes.
5. `LEAD()` and `LAG()` make fixed-window checks cleaner.
6. Gap-and-island is the most general and elegant methodology for consecutive-value problems.
