# MySQL Window Functions — Concepts, Syntax, and Examples (Simple but Detailed)

This note explains **window functions** in MySQL in clear language, with practical examples.

---

## 1) What is a Window Function?

A **window function** computes a value using a set of rows **related to the current row**, **without collapsing rows**.

- **Aggregate query (GROUP BY)**: many input rows → **fewer output rows**
- **Window function**: many input rows → **same number of output rows**, plus extra computed columns

### Key terms

- **Current row**: the row being output right now.
- **Window**: the set of rows used to compute the window function result for the current row.

---

## 2) Why window functions matter

Window functions let you compute things like:

- Totals alongside each row (global total, country total, etc.)
- Rank/row numbers within groups
- Running totals
- “Previous row” / “next row” values (LAG/LEAD)
- Percentiles and distribution metrics

All of this is possible **without losing row-level detail**.

---

## 3) Dataset used in examples

Assume a table:

```sql
SELECT * FROM sales ORDER BY country, year, product;
```

Columns: `year, country, product, profit`

Example rows (abbreviated):

- Finland 2000 Computer 1500
- Finland 2000 Phone 100
- Finland 2001 Phone 10
- India 2000 Calculator 75 (twice)
- India 2000 Computer 1200
- USA 2000 Computer 1500
- USA 2001 TV 150 (and 100)
- …

---

## 4) Aggregate vs Window: the difference (with SUM)

### A) Aggregation collapses rows

**Global sum (1 row output):**

```sql
SELECT SUM(profit) AS total_profit
FROM sales;
```

**Sum per country (1 row per country):**

```sql
SELECT country, SUM(profit) AS country_profit
FROM sales
GROUP BY country
ORDER BY country;
```

### B) Window SUM does not collapse rows

Same SUM(), but as a **window function** using `OVER(...)`:

```sql
SELECT
  year, country, product, profit,
  SUM(profit) OVER() AS total_profit,
  SUM(profit) OVER(PARTITION BY country) AS country_profit
FROM sales
ORDER BY country, year, product, profit;
```

What happens:

- `SUM(profit) OVER()` → treats **all rows** as one partition → global total repeated on every row
- `SUM(profit) OVER(PARTITION BY country)` → partitions rows by country → country total repeated for each row in that country

---

## 5) The OVER clause: the core syntax

A function becomes a window function when you add `OVER`.

### Forms of OVER

```text
OVER (window_spec)
OVER window_name
```

- `OVER (window_spec)` defines the window directly.
- `OVER window_name` references a **named window** defined by a `WINDOW` clause.

---

## 6) Window specification (window_spec) parts

```text
window_spec:
  [window_name] [PARTITION BY ...] [ORDER BY ...] [frame_clause]
```

All parts are optional, but they change meaning a lot.

### 6.1 PARTITION BY (grouping for windows)

`PARTITION BY` divides rows into groups (partitions).

```sql
SUM(profit) OVER(PARTITION BY country)
```

- Each row’s result is computed using rows from **its country partition**.
- If `PARTITION BY` is omitted → there is **one partition** containing all rows.

> MySQL allows expressions in PARTITION BY (extension). Example: `PARTITION BY HOUR(ts)`.

### 6.2 ORDER BY (ordering inside each partition)

`ORDER BY` inside `OVER(...)` sorts rows **within each partition**.

```sql
ROW_NUMBER() OVER(PARTITION BY country ORDER BY year, product)
```

Important:

- If you omit `ORDER BY`, partition rows are unordered.
- For ranking/row-numbering, omitting `ORDER BY` makes results **nondeterministic**.

**Peers**:

- Rows that are equal by the window `ORDER BY` are called **peers**.
  (This matters for RANK/DENSE_RANK and frame behavior.)

### 6.3 frame_clause (subset of partition)

A **frame** is a subset of the current partition used for the computation.

With a frame clause, you can express **running totals**, sliding windows, etc.

Example (running total per country):

```sql
SELECT
  year, country, product, profit,
  SUM(profit) OVER (
    PARTITION BY country
    ORDER BY year, product
    ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
  ) AS running_country_total
FROM sales;
```

Meaning:

- Within each country, sort rows by `(year, product)`,
- For each row, sum from the first row of the partition up to the current row.

(Frames have many additional options; this is the most common mental model.)

---

## 7) Where window functions are allowed

MySQL permits window functions only in:

- **SELECT list**
- **ORDER BY clause**

MySQL processing order (practical view):

- `FROM` → `WHERE` → `GROUP BY` → `HAVING`
- **Window functions**
- final `ORDER BY` → `LIMIT` → `SELECT DISTINCT`

**Implication**

- You cannot use a window function in `WHERE` directly.
- If you need to filter by a window result, use a derived table:

```sql
SELECT *
FROM (
  SELECT
    *,
    ROW_NUMBER() OVER(PARTITION BY country ORDER BY profit DESC) AS rn
  FROM sales
) t
WHERE rn = 1;
```

---

## 8) Aggregate functions that can be used as window functions

These can act as:

- normal aggregates (no OVER), or
- window functions (with OVER)

Common examples:

- `SUM()`, `AVG()`, `COUNT()`, `MIN()`, `MAX()`
- `STDDEV_POP()`, `STDDEV_SAMP()`, `VAR_POP()`, `VAR_SAMP()`
- `JSON_ARRAYAGG()`, `JSON_OBJECTAGG()`

Rule:

- If you add `OVER(...)`, the function becomes a **windowed aggregate** and does not collapse rows.

---

## 9) Window-only (non-aggregate) functions

These require `OVER(...)`:

- `ROW_NUMBER()`
- `RANK()`, `DENSE_RANK()`
- `LAG()`, `LEAD()`
- `FIRST_VALUE()`, `LAST_VALUE()`, `NTH_VALUE()`
- `NTILE()`
- `CUME_DIST()`, `PERCENT_RANK()`

---

## 10) Example: ROW_NUMBER() unordered vs ordered

```sql
SELECT
  year, country, product, profit,
  ROW_NUMBER() OVER(PARTITION BY country) AS row_num1,
  ROW_NUMBER() OVER(PARTITION BY country ORDER BY year, product) AS row_num2
FROM sales;
```

Interpretation:

- `row_num1`: partitioned by country but **no window ORDER BY** → numbering is **nondeterministic**
- `row_num2`: partitioned by country and **ordered by (year, product)** → deterministic numbering

Guidance:

- For ranking/row numbering, include `ORDER BY` inside the window definition unless order truly does not matter.

---

## 11) Example: Top-1 per country (common interview pattern)

### Pick exactly one row per country (ties broken by window order)

```sql
SELECT *
FROM (
  SELECT
    year, country, product, profit,
    ROW_NUMBER() OVER(PARTITION BY country ORDER BY profit DESC) AS rn
  FROM sales
) x
WHERE rn = 1;
```

### Return all ties for max profit per country

```sql
SELECT *
FROM (
  SELECT
    year, country, product, profit,
    RANK() OVER(PARTITION BY country ORDER BY profit DESC) AS rnk
  FROM sales
) x
WHERE rnk = 1;
```

- `ROW_NUMBER()` returns one winner per partition.
- `RANK()` can return multiple winners if profits tie.

---

## 12) Named windows (WINDOW clause concept)

Instead of repeating the same `PARTITION BY` / `ORDER BY`, define named windows:

```sql
SELECT
  year, country, product, profit,
  SUM(profit) OVER w_country AS country_profit,
  ROW_NUMBER() OVER w_country_ordered AS rownum_in_country
FROM sales
WINDOW
  w_country AS (PARTITION BY country),
  w_country_ordered AS (PARTITION BY country ORDER BY year, product);
```

Benefits:

- Less repetition
- More readable when many window functions share the same definition

---

## 13) Quick checklist (practical)

1. Need totals/ranks **without collapsing rows**? → window functions.
2. Decide the semantics explicitly:
   - Partition: `PARTITION BY ...` (grouping)
   - Order: `ORDER BY ...` (determinism, ranks, running totals)
   - Frame: `ROWS/RANGE BETWEEN ...` (running/sliding window meaning)
3. Need to filter on window results? → derived table and filter outside.
4. Remember execution timing: window functions happen after `WHERE/GROUP BY/HAVING` but before final `ORDER BY/LIMIT/DISTINCT`.

---

## 14) Minimal syntax templates

### Windowed aggregate per partition

```sql
SELECT
  ...,
  SUM(x) OVER(PARTITION BY k) AS sum_per_k
FROM t;
```

### Row number per partition with deterministic ordering

```sql
SELECT
  ...,
  ROW_NUMBER() OVER(PARTITION BY k ORDER BY ts) AS rn
FROM t;
```

### Running total per partition

```sql
SELECT
  ...,
  SUM(x) OVER(
    PARTITION BY k
    ORDER BY ts
    ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
  ) AS running_sum
FROM t;
```

---
