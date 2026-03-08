# Rising Temperature

Goal: Return the **ids** of days where `temperature` is **higher than the previous day** (i.e., the day with `recordDate = yesterday + 1 day`).

Typical schema:

```sql
Weather(id INT, recordDate DATE, temperature INT)
```

---

## Key Idea (applies to every approach)

For each day **D**, you want to compare:

- `temp(D)` vs `temp(D - 1 day)`
- and only when **D - 1 day actually exists** as a row in the table.

That “consecutive day exists” condition is **critical**; otherwise you may compare non-consecutive rows.

---

## Approach 1: Self-Join + `DATEDIFF()` (Recommended + Portable)

### Intuition

Join the table to itself so that each row in `w1` (today) matches the row in `w2` (yesterday).
Then filter to rising temperatures.

### Query

```sql
SELECT
    w1.id
FROM
    Weather w1
JOIN
    Weather w2
ON
    DATEDIFF(w1.recordDate, w2.recordDate) = 1
WHERE
    w1.temperature > w2.temperature;
```

### How it works

- `JOIN Weather w2` creates candidate pairs `(w1, w2)`.
- `DATEDIFF(w1.recordDate, w2.recordDate) = 1` keeps only `(today, yesterday)` pairs.
- `w1.temperature > w2.temperature` selects rising temperature days.
- Output is `w1.id` because `w1` is **today**.

### Notes

- Very clear logic.
- Good performance with an index on `recordDate`.

---

## Approach 2: Window Function `LAG()` (Most “SQL-Modern”)

### Intuition

Instead of joining, compute the previous row’s values **in the same result set** using window functions.
Then filter rows where:

1. temperature increased, and
2. the previous row is exactly one day before (to enforce consecutiveness).

### Query (MySQL 8+, PostgreSQL, SQL Server, etc.)

```sql
WITH PreviousWeatherData AS
(
    SELECT
        id,
        recordDate,
        temperature,
        LAG(temperature, 1) OVER (ORDER BY recordDate) AS PreviousTemperature,
        LAG(recordDate, 1) OVER (ORDER BY recordDate) AS PreviousRecordDate
    FROM
        Weather
)
SELECT
    id
FROM
    PreviousWeatherData
WHERE
    temperature > PreviousTemperature
AND
    recordDate = DATE_ADD(PreviousRecordDate, INTERVAL 1 DAY);
```

---

### How `LAG()` works (Detailed, Practical Explanation)

**Syntax**

```sql
LAG(expression, offset, default) OVER (PARTITION BY ... ORDER BY ...)
```

- **`expression`**: the column/value you want from a prior row (e.g., `temperature`)
- **`offset`**: how many rows back (1 = previous row)
- **`default`** (optional): used when there is no prior row (otherwise NULL)
- **`OVER (ORDER BY ...)`**: defines the sequence of rows for “previous”.

#### What it returns

For each row **R**, `LAG(temperature, 1) OVER (ORDER BY recordDate)` returns the `temperature` from the row that appears **immediately before R** in `recordDate` ordering.

Importantly:

- “Previous” here means **previous row by ordering**, not necessarily “yesterday”.
- If there are missing dates, the previous row might be 2 days or 10 days earlier.

That’s why the query includes:

```sql
recordDate = DATE_ADD(PreviousRecordDate, INTERVAL 1 DAY)
```

This explicitly enforces **consecutive calendar days**, not just consecutive rows.

#### Example to make it concrete

Suppose the table has:

| recordDate | temperature |
| ---------- | ----------- |
| 2020-01-01 | 10          |
| 2020-01-03 | 20          |

For row `2020-01-03`:

- `LAG(temperature)` = 10
- But `2020-01-03` is **not** `2020-01-01 + 1 day`, so we must reject it as not “previous day”.

Hence the date check is required.

#### Optional improvement: provide defaults

You can avoid NULL comparisons by using a default:

```sql
LAG(temperature, 1, -999999) OVER (ORDER BY recordDate) AS PreviousTemperature
```

But you still must enforce the consecutive-day condition.

---

## Approach 3: Correlated Subquery + `DATE_SUB()` (Simple, Often Slower)

### Intuition

For each row `w1`, look up yesterday’s temperature via a subquery.
If today is greater, return today’s `id`.

### Query

```sql
SELECT
    w1.id
FROM
    Weather w1
WHERE
    w1.temperature > (
        SELECT
            w2.temperature
        FROM
            Weather w2
        WHERE
            w2.recordDate = DATE_SUB(w1.recordDate, INTERVAL 1 DAY)
    );
```

### How it works

- Outer query iterates row-by-row over `w1`.
- For each `w1.recordDate`, inner query fetches the temperature at `(w1.recordDate - 1 day)`.
- If the subquery returns NULL (because yesterday row doesn’t exist), the comparison becomes UNKNOWN, and the row is not selected (as desired).

### Notes

- Easy to read.
- Can be slower because the subquery may run per row (though optimizers sometimes rewrite it).
- Index on `recordDate` is very important.

---

## Approach 4: Implicit Join (Cartesian Product) + `WHERE` (Avoid in Practice)

### Intuition

Using:

```sql
FROM Weather w1, Weather w2
```

creates a **cross join** (Cartesian product), then filters down to consecutive-day pairs with rising temperatures.

### Query

```sql
SELECT
    w2.id
FROM
    Weather w1, Weather w2
WHERE
    DATEDIFF(w2.recordDate, w1.recordDate) = 1
AND
    w2.temperature > w1.temperature;
```

### How it works

- Start with all pairs `(w1, w2)` → potentially **n²** rows.
- Filter pairs to only those 1 day apart.
- Filter further to rising temperatures.
- Select `w2.id` because `w2` is the “later” day.

### Notes

- This is logically correct but can be unnecessarily expensive on large tables.
- Prefer explicit `JOIN` (Approach 1) so intent is clear and optimizers behave predictably.

---

## Which Approach Should You Use?

- **If window functions are supported (MySQL 8+)**: Approach 2 (`LAG`) is elegant and expressive.
- **If you want maximum portability / simplicity**: Approach 1 (self-join) is the standard go-to.
- **If you are stuck without window functions**: Approach 1 or 3 are typical alternatives.
- **Avoid** Approach 4 in production unless you have a strong reason.

---

## Performance and Indexing (Practical Notes)

Add an index on `recordDate`:

```sql
CREATE INDEX idx_weather_recordDate ON Weather(recordDate);
```

Why:

- Self-join / subquery are both driven by date equality / date arithmetic patterns.
- Index helps locate “yesterday” rows quickly.

Big-picture complexity:

- **Approach 1 (Join)**: typically `O(n log n)`-ish behavior with indexes (database-specific), very practical.
- **Approach 2 (LAG)**: requires ordering by `recordDate` (sorting cost if not already indexed/clustered).
- **Approach 3 (Subquery)**: can degrade if executed per-row without good indexing/optimizer rewrite.
- **Approach 4 (Cross Join)**: worst-case `O(n²)` intermediate work before filtering.

---
