# 595. Big Countries

## Approach: Filtering Rows Using `WHERE`

## Core idea

This problem is one of the most direct SQL filtering problems.

We are given a table `World`, and we need to return:

- `name`
- `population`
- `area`

for every country that is considered **big**.

A country is **big** if **at least one** of the following is true:

1. `area >= 3000000`
2. `population >= 25000000`

Since this is simply a row-level condition, the right tool is the `WHERE` clause.

---

## Step 1: Translate the conditions directly

The problem gives us two conditions:

### Condition 1: large area

```sql
area >= 3000000
```

### Condition 2: large population

```sql
population >= 25000000
```

The statement says a country is big if **either** condition is true.

So we combine them with:

```sql
OR
```

That gives:

```sql
WHERE area >= 3000000 OR population >= 25000000
```

---

## Step 2: Start with a simple filtering query

If we first want to see all columns for countries that satisfy the condition, we could write:

```sql
SELECT *
FROM world
WHERE area >= 3000000
   OR population >= 25000000;
```

### What this does

- scans the `World` table
- checks each row
- keeps a row if:
  - area is at least 3,000,000
  - or population is at least 25,000,000

This is already enough to identify the correct rows.

---

## Step 3: Select only the required columns

But the problem does **not** ask us to return every column.

It specifically asks for these three columns:

1. `name`
2. `population`
3. `area`

So instead of `SELECT *`, we should explicitly select only those columns in the required order.

That gives the final query:

```sql
SELECT
    name,
    population,
    area
FROM world
WHERE area >= 3000000
   OR population >= 25000000;
```

---

## Final accepted solution

```sql
SELECT
    name,
    population,
    area
FROM
    world
WHERE
    area >= 3000000 OR population >= 25000000;
```

---

## Why `WHERE` is enough

This problem does **not** require:

- joins
- grouping
- aggregation
- sorting
- subqueries
- window functions

We are simply checking whether each row satisfies a condition.

That makes `WHERE` the cleanest and most natural solution.

---

## Example walkthrough

### Input

| name        | continent | area    | population | gdp          |
| ----------- | --------- | ------- | ---------- | ------------ |
| Afghanistan | Asia      | 652230  | 25500100   | 20343000000  |
| Albania     | Europe    | 28748   | 2831741    | 12960000000  |
| Algeria     | Africa    | 2381741 | 37100000   | 188681000000 |
| Andorra     | Europe    | 468     | 78115      | 3712000000   |
| Angola      | Africa    | 1246700 | 20609294   | 100990000000 |

---

## Row-by-row evaluation

### Afghanistan

- `area = 652230`
- `population = 25500100`

Check conditions:

- `652230 >= 3000000` -> false
- `25500100 >= 25000000` -> true

Since one condition is true, Afghanistan is included.

---

### Albania

- `area = 28748`
- `population = 2831741`

Check:

- `28748 >= 3000000` -> false
- `2831741 >= 25000000` -> false

Neither condition is true, so Albania is excluded.

---

### Algeria

- `area = 2381741`
- `population = 37100000`

Check:

- `2381741 >= 3000000` -> false
- `37100000 >= 25000000` -> true

One condition is true, so Algeria is included.

---

### Andorra

- `area = 468`
- `population = 78115`

Check:

- `468 >= 3000000` -> false
- `78115 >= 25000000` -> false

Excluded.

---

### Angola

- `area = 1246700`
- `population = 20609294`

Check:

- `1246700 >= 3000000` -> false
- `20609294 >= 25000000` -> false

Excluded.

---

## Final output

| name        | population | area    |
| ----------- | ---------- | ------- |
| Afghanistan | 25500100   | 652230  |
| Algeria     | 37100000   | 2381741 |

---

## Why `OR` is important

The condition is:

> area is large **or** population is large

That means satisfying **either one** is enough.

So this is correct:

```sql
WHERE area >= 3000000 OR population >= 25000000
```

If we mistakenly used `AND`:

```sql
WHERE area >= 3000000 AND population >= 25000000
```

then we would only keep countries satisfying **both** conditions.

That would be stricter than the problem statement and would produce the wrong result.

---

## Wrong version example

```sql
SELECT
    name,
    population,
    area
FROM world
WHERE area >= 3000000
  AND population >= 25000000;
```

This query would exclude countries that are big in only one way.

For example:

- a country with huge population but smaller area should still be included
- a country with huge area but smaller population should still be included

So `AND` would be logically incorrect.

---

## Minimal version

You can also write the final query in one compact line:

```sql
SELECT name, population, area
FROM world
WHERE area >= 3000000 OR population >= 25000000;
```

This is exactly equivalent.

---

## Readable formatted version

A more readable style is:

```sql
SELECT
    name,
    population,
    area
FROM
    world
WHERE
    area >= 3000000
    OR population >= 25000000;
```

Same logic, just better formatting.

---

## Complexity

Let `n` be the number of rows in `World`.

### Time Complexity

```text
O(n)
```

because the query checks each row once.

### Space Complexity

```text
O(1)
```

ignoring the output, since no extra data structures, joins, or aggregations are needed.

---

## Key takeaways

1. This is a pure filtering problem.
2. Use `WHERE` because the decision is made row by row.
3. A country is big if either condition is true, so use `OR`.
4. Return only the required columns: `name`, `population`, and `area`.
5. The correct final query is:

```sql
SELECT
    name,
    population,
    area
FROM
    world
WHERE
    area >= 3000000 OR population >= 25000000;
```
