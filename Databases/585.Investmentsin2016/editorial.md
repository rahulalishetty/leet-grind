# 585. Investments in 2016

## Detailed Summary of Two Accepted Approaches

This problem asks us to compute the total `tiv_2016` for policyholders who satisfy **both** of these conditions:

1. Their `tiv_2015` value appears in **more than one record**
2. Their location `(lat, lon)` is **unique** across the table

The final answer must be:

- the **sum** of `tiv_2016` for all qualifying rows
- **rounded to 2 decimal places**

---

# Restating the filtering conditions

For a policyholder row to be included:

- `tiv_2015` must be shared with at least one other policyholder
- `(lat, lon)` must belong only to that row

So conceptually:

```text
keep row if:
count(rows with same tiv_2015) > 1
and
count(rows with same (lat, lon)) = 1
```

That is the entire logic behind both approaches.

---

# Example Input

| pid | tiv_2015 | tiv_2016 | lat | lon |
| --- | -------- | -------- | --- | --- |
| 1   | 10       | 5        | 10  | 10  |
| 2   | 20       | 20       | 20  | 20  |
| 3   | 10       | 30       | 20  | 20  |
| 4   | 10       | 40       | 40  | 40  |

We evaluate each row:

- `pid = 1`
  - `tiv_2015 = 10` appears multiple times -> good
  - location `(10,10)` is unique -> good
  - keep

- `pid = 2`
  - `tiv_2015 = 20` appears once -> fail
  - location `(20,20)` is shared -> fail
  - do not keep

- `pid = 3`
  - `tiv_2015 = 10` appears multiple times -> good
  - location `(20,20)` is shared -> fail
  - do not keep

- `pid = 4`
  - `tiv_2015 = 10` appears multiple times -> good
  - location `(40,40)` is unique -> good
  - keep

Final sum:

```text
5 + 40 = 45
```

Rounded:

```text
45.00
```

---

# Approach 1: Creating Filters in Subqueries

## Core idea

This approach builds the two required filters separately:

1. all `tiv_2015` values that occur more than once
2. all locations `(lat, lon)` that occur exactly once

Then it joins those filter results back to the original `Insurance` table to keep only the qualifying rows.

This is a very direct translation of the problem statement.

---

## Step 1: Find duplicated `tiv_2015` values

We need all `tiv_2015` values shared by more than one policyholder.

```sql
SELECT tiv_2015
FROM Insurance
GROUP BY tiv_2015
HAVING COUNT(DISTINCT pid) > 1;
```

### Why this works

- `GROUP BY tiv_2015` groups rows by the 2015 investment value
- `COUNT(DISTINCT pid) > 1` keeps only groups having at least two different policyholders

### Output on the example

| tiv_2015 |
| -------- |
| 10       |

So `10` is a valid shared `tiv_2015` value.

---

## Step 2: Find unique locations

We now need locations that belong to exactly one policyholder.

Since location is represented by two columns, `lat` and `lon`, the approach combines them using `CONCAT()`.

```sql
SELECT CONCAT(lat, lon) AS lat_lon
FROM Insurance
GROUP BY CONCAT(lat, lon)
HAVING COUNT(DISTINCT pid) = 1;
```

### Why this works

- `CONCAT(lat, lon)` turns the two location columns into one grouping key
- `GROUP BY CONCAT(lat, lon)` groups rows by location
- `HAVING COUNT(DISTINCT pid) = 1` keeps only locations that appear once

### Output on the example

| lat_lon |
| ------- |
| 1010    |
| 4040    |

These represent the unique locations:

- `(10,10)`
- `(40,40)`

---

## Step 3: Join both filters back to the base table

Now that we know:

- which `tiv_2015` values are duplicated
- which locations are unique

we join those back with the original rows.

```sql
SELECT ROUND(SUM(tiv_2016), 2) AS tiv_2016
FROM Insurance i
JOIN
(
    SELECT tiv_2015
    FROM Insurance
    GROUP BY tiv_2015
    HAVING COUNT(DISTINCT pid) > 1
) t0
ON i.tiv_2015 = t0.tiv_2015
JOIN
(
    SELECT CONCAT(lat, lon) AS lat_lon
    FROM Insurance
    GROUP BY CONCAT(lat, lon)
    HAVING COUNT(DISTINCT pid) = 1
) t1
ON CONCAT(i.lat, i.lon) = t1.lat_lon;
```

---

## Full explanation of the final query

### First join

```sql
ON i.tiv_2015 = t0.tiv_2015
```

This keeps only rows whose `tiv_2015` appears more than once.

### Second join

```sql
ON CONCAT(i.lat, i.lon) = t1.lat_lon
```

This keeps only rows whose location is unique.

### Final aggregation

```sql
ROUND(SUM(tiv_2016), 2) AS tiv_2016
```

This adds the qualifying `tiv_2016` values and rounds the result to two decimals.

---

## Important note about `CONCAT(lat, lon)`

This approach follows the solution exactly as written, but from a careful engineering perspective, combining columns with `CONCAT(lat, lon)` can be risky in real systems.

For example, values like:

- `(1, 23)` -> `"123"`
- `(12, 3)` -> `"123"`

would collide.

A safer version would use a separator:

```sql
CONCAT(lat, ',', lon)
```

or avoid concatenation entirely by grouping on both columns directly:

```sql
GROUP BY lat, lon
```

But since you asked to preserve the approach as given, the explanation above reflects it faithfully.

---

## Approach 1: Final Query

```sql
SELECT ROUND(SUM(tiv_2016), 2) AS tiv_2016
FROM Insurance i
JOIN
(
    SELECT tiv_2015
    FROM Insurance
    GROUP BY tiv_2015
    HAVING COUNT(DISTINCT pid) > 1
) t0
ON i.tiv_2015 = t0.tiv_2015
JOIN
(
    SELECT CONCAT(lat, lon) AS lat_lon
    FROM Insurance
    GROUP BY CONCAT(lat, lon)
    HAVING COUNT(DISTINCT pid) = 1
) t1
ON CONCAT(i.lat, i.lon) = t1.lat_lon;
```

---

# Approach 2: Creating Filters Using Window Functions

## Core idea

Instead of first computing valid values in separate subqueries, this approach annotates each row with:

- how many rows share its `tiv_2015`
- how many rows share its location

Then it simply filters rows based on those counts.

This is usually more elegant and often easier to reason about once window functions are available.

---

## Step 1: Compute per-row counts with window functions

```sql
SELECT *,
    COUNT(*) OVER (PARTITION BY tiv_2015) AS tiv_2015_cnt,
    COUNT(*) OVER (PARTITION BY lat, lon) AS loc_cnt
FROM Insurance;
```

---

## Why this works

### `COUNT(*) OVER (PARTITION BY tiv_2015)`

For each row, count how many rows have the same `tiv_2015`.

### `COUNT(*) OVER (PARTITION BY lat, lon)`

For each row, count how many rows have the same location.

This gives us row-level metadata without collapsing the table.

Unlike `GROUP BY`, window functions keep all original rows visible.

---

## Output after the window calculation

| pid | tiv_2015 | tiv_2016 | lat | lon | tiv_2015_cnt | loc_cnt |
| --- | -------- | -------- | --- | --- | ------------ | ------- |
| 1   | 10       | 5        | 10  | 10  | 3            | 1       |
| 3   | 10       | 30       | 20  | 20  | 3            | 2       |
| 2   | 20       | 20       | 20  | 20  | 1            | 2       |
| 4   | 10       | 40       | 40  | 40  | 3            | 1       |

Now the filtering becomes straightforward.

---

## Step 2: Keep only rows satisfying both requirements

We want:

- `tiv_2015_cnt > 1`
- `loc_cnt = 1`

So:

```sql
SELECT ROUND(SUM(tiv_2016), 2) AS tiv_2016
FROM
(
    SELECT *,
        COUNT(*) OVER (PARTITION BY tiv_2015) AS tiv_2015_cnt,
        COUNT(*) OVER (PARTITION BY lat, lon) AS loc_cnt
    FROM Insurance
) t0
WHERE tiv_2015_cnt > 1
  AND loc_cnt = 1;
```

---

## Full explanation of the filtering logic

### Condition 1

```sql
tiv_2015_cnt > 1
```

This means the row's `tiv_2015` value appears in at least two records.

### Condition 2

```sql
loc_cnt = 1
```

This means the row's location is unique.

### Final step

```sql
SUM(tiv_2016)
```

adds the 2016 investment values from rows that survived both filters.

### Rounding

```sql
ROUND(..., 2)
```

formats the result to two decimal places, as required.

---

## Approach 2: Final Query

```sql
SELECT ROUND(SUM(tiv_2016), 2) AS tiv_2016
FROM
(
    SELECT *,
        COUNT(*) OVER (PARTITION BY tiv_2015) AS tiv_2015_cnt,
        COUNT(*) OVER (PARTITION BY lat, lon) AS loc_cnt
    FROM Insurance
) t0
WHERE tiv_2015_cnt > 1
  AND loc_cnt = 1;
```

---

# Comparing the two approaches

## Approach 1: Subquery filters

### Strengths

- Very direct translation of the problem statement
- Easy to understand as “find valid values first, then keep matching rows”
- Works even in SQL environments where window functions may not be available

### Tradeoffs

- More verbose
- Repeats scans of the same table
- `CONCAT(lat, lon)` is not ideal unless handled carefully

---

## Approach 2: Window functions

### Strengths

- More elegant
- Annotates each row with exactly the counts we care about
- Keeps the logic compact and expressive
- Usually easier to debug once you understand window functions

### Tradeoffs

- Requires support for window functions
- May be less familiar if you are early in SQL learning

---

# A more robust version of Approach 1

The original version uses `CONCAT(lat, lon)`. A safer variant is to group by both columns directly.

```sql
SELECT ROUND(SUM(i.tiv_2016), 2) AS tiv_2016
FROM Insurance i
JOIN
(
    SELECT tiv_2015
    FROM Insurance
    GROUP BY tiv_2015
    HAVING COUNT(*) > 1
) t0
ON i.tiv_2015 = t0.tiv_2015
JOIN
(
    SELECT lat, lon
    FROM Insurance
    GROUP BY lat, lon
    HAVING COUNT(*) = 1
) t1
ON i.lat = t1.lat
AND i.lon = t1.lon;
```

This keeps the same idea but avoids possible concatenation ambiguity.

---

# Mental model for this problem

A good way to think about the problem is:

- one condition is about **duplicate 2015 values**
- the other condition is about **unique locations**
- a row must satisfy **both**

So every valid solution is doing two counts:

1. count by `tiv_2015`
2. count by `(lat, lon)`

Then it keeps rows where:

```text
count_by_tiv_2015 > 1
and
count_by_location = 1
```

Everything else is just SQL style.

---

# Final accepted implementations

## Approach 1

```sql
SELECT ROUND(SUM(tiv_2016), 2) AS tiv_2016
FROM Insurance i
JOIN
(
    SELECT tiv_2015
    FROM Insurance
    GROUP BY tiv_2015
    HAVING COUNT(DISTINCT pid) > 1
) t0
ON i.tiv_2015 = t0.tiv_2015
JOIN
(
    SELECT CONCAT(lat, lon) AS lat_lon
    FROM Insurance
    GROUP BY CONCAT(lat, lon)
    HAVING COUNT(DISTINCT pid) = 1
) t1
ON CONCAT(i.lat, i.lon) = t1.lat_lon;
```

## Approach 2

```sql
SELECT ROUND(SUM(tiv_2016), 2) AS tiv_2016
FROM
(
    SELECT *,
        COUNT(*) OVER (PARTITION BY tiv_2015) AS tiv_2015_cnt,
        COUNT(*) OVER (PARTITION BY lat, lon) AS loc_cnt
    FROM Insurance
) t0
WHERE tiv_2015_cnt > 1
  AND loc_cnt = 1;
```

---

# Complexity discussion

## Approach 1

You can think of this as multiple grouped scans over the table, plus joins back to the base table.

Roughly:

- grouping by `tiv_2015`
- grouping by location
- joining results back

In interview-style terms, this is commonly treated as around `O(n log n)` depending on grouping implementation, or `O(n)` with hashing assumptions.

## Approach 2

This computes two partition counts and then filters.

Again, practical performance depends on the SQL engine, but conceptually it is often cleaner because it avoids separate join filters.

For LeetCode-style discussion, both are efficient enough and accepted.

---

# Key takeaways

1. The row must satisfy **two independent counting conditions**.
2. Approach 1 solves it by building valid filters in subqueries.
3. Approach 2 solves it by attaching counts directly to each row using window functions.
4. `ROUND(SUM(tiv_2016), 2)` is the final aggregation in both cases.
5. Be careful with `CONCAT(lat, lon)` in real-world SQL; grouping by `(lat, lon)` directly is safer.
