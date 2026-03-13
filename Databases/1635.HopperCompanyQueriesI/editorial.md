# 1635. Hopper Company Queries I

## Approach: `LEFT JOIN` with CTEs

## Core idea

For each month of **2020**, we need to report two values:

1. **active_drivers**
   The number of drivers who had joined Hopper **by the end of that month**.

2. **accepted_rides**
   The number of rides that were **accepted during that month**.

A clear way to solve this is to build the result in stages using CTEs:

1. create a table of months from `1` to `12`
2. prepare driver join-month information
3. prepare accepted-ride month information
4. join everything together and aggregate

This approach is clean because each CTE handles one specific responsibility.

---

## Why a month table is needed

The final output must include **all 12 months of 2020**, even months where:

- no new drivers joined
- no rides were accepted

That means we need a guaranteed backbone table containing:

```text
1, 2, 3, ..., 12
```

So the solution starts by generating a month series.

---

# Step 1: Build the `Months` table using a recursive CTE

```sql
WITH RECURSIVE Months AS (
    SELECT
        1 AS month
    UNION ALL
    SELECT
        month + 1
    FROM
        Months
    WHERE
        month < 12
)
```

---

## Why this works

This is a recursive CTE:

- start with month `1`
- repeatedly add `1`
- stop when month reaches `12`

So it creates:

| month |
| ----: |
|     1 |
|     2 |
|     3 |
|     4 |
|     5 |
|     6 |
|     7 |
|     8 |
|     9 |
|    10 |
|    11 |
|    12 |

This gives us the full year structure.

---

# Step 2: Build a driver table with the month each driver becomes active

We need to know from which month each driver should start being counted as active.

A driver is active in all months from their join month onward, as long as they joined in or before 2020.

The provided solution defines a `Driver` CTE like this:

```sql
Driver AS (
    SELECT
        driver_id,
        (CASE WHEN YEAR(join_date) = 2019 THEN '1' ELSE MONTH(join_date) END) AS month
    FROM
        Drivers
    WHERE
        YEAR(join_date) <= 2020
)
```

---

## Why this works

### `WHERE YEAR(join_date) <= 2020`

This excludes drivers who joined after 2020, because they should not count toward any month in 2020.

For example, a driver joining in 2021 should not appear in the 2020 report.

---

### `CASE WHEN YEAR(join_date) = 2019 THEN '1' ELSE MONTH(join_date) END`

This maps each driver to the month from which they should begin being counted.

- If the driver joined in **2019**, then they are already active from the beginning of 2020.
  So they are assigned month `1`.

- If the driver joined in **2020**, then they become active starting from their actual join month.

So this transforms historical join dates into a 2020-compatible activation month.

---

## Example result of the `Driver` CTE

From the sample:

### Drivers table

| driver_id | join_date  |
| --------: | ---------- |
|        10 | 2019-12-10 |
|         8 | 2020-01-13 |
|         5 | 2020-02-16 |
|         7 | 2020-03-08 |
|         4 | 2020-05-17 |
|         1 | 2020-10-24 |
|         6 | 2021-01-05 |

After filtering `YEAR(join_date) <= 2020` and applying the `CASE`, we get:

| driver_id | month |
| --------: | ----: |
|        10 |     1 |
|         8 |     1 |
|         5 |     2 |
|         7 |     3 |
|         4 |     5 |
|         1 |    10 |

Driver `6` is excluded because they joined in 2021.

---

# Step 3: Build a ride table containing accepted rides in 2020

We only want rides that were:

- accepted
- requested in 2020

To identify accepted rides, we join:

- `AcceptedRides`
- `Rides`

using `ride_id`

Then we extract the request month.

```sql
Ride AS (
    SELECT
        MONTH(requested_at) AS month,
        a.ride_id
    FROM
        AcceptedRides AS a
    INNER JOIN
        Rides r
    ON
        r.ride_id = a.ride_id
    WHERE
        YEAR(requested_at) = 2020
)
```

---

## Why this works

### `INNER JOIN AcceptedRides ... Rides`

This keeps only rides that were actually accepted.

`Rides` contains all ride requests, including those that may not have been accepted.
`AcceptedRides` contains only accepted ones.

So the inner join gives us the accepted ride requests.

---

### `WHERE YEAR(requested_at) = 2020`

This restricts the report to rides requested during 2020.

That matches the problem statement, which asks for each month of 2020.

---

### `MONTH(requested_at) AS month`

This extracts the month number from the ride request date so we can later count accepted rides month by month.

---

## Example result of the `Ride` CTE

From the sample, accepted rides in 2020 are:

| month | ride_id |
| ----: | ------: |
|     3 |      10 |
|     6 |      13 |
|     7 |       7 |
|     8 |      17 |
|    11 |      20 |
|    11 |       5 |
|    12 |       2 |

These are exactly the accepted rides requested during 2020.

---

# Step 4: Join the CTEs and aggregate the final output

Now we combine the three CTEs:

- `Months` gives the 12-month skeleton
- `Driver` lets us count active drivers cumulatively
- `Ride` lets us count accepted rides in the exact month

```sql
SELECT
    m.month,
    COUNT(DISTINCT d.driver_id) AS active_drivers,
    COUNT(DISTINCT r.ride_id) AS accepted_rides
FROM
    Months AS m
LEFT JOIN
    Driver AS d
ON
    d.month <= m.month
LEFT JOIN
    Ride AS r
ON
    m.month = r.month
GROUP BY
    m.month
ORDER BY
    m.month
```

---

## Why the join condition for drivers is `d.month <= m.month`

This is the key idea for counting **active drivers by the end of each month**.

If a driver joined in month `k`, then they are active in:

- month `k`
- month `k + 1`
- month `k + 2`
- ...
- month `12`

So for any report month `m.month`, a driver should be counted if:

```sql
d.month <= m.month
```

This gives a rolling cumulative effect.

---

## Why the join condition for rides is `m.month = r.month`

Accepted rides are not cumulative.
We want the number of accepted rides **in that exact month**.

So we join rides only when:

```sql
m.month = r.month
```

That gives a monthly count, not a running total.

---

## Why `LEFT JOIN` is important

### `Months LEFT JOIN Driver`

This keeps all 12 months, even if no drivers are active yet.

### `Months LEFT JOIN Ride`

This keeps all 12 months, even if no accepted rides occurred in that month.

Without `LEFT JOIN`, months with zero counts could disappear from the output.

---

## Why `COUNT(DISTINCT ...)` is used

### `COUNT(DISTINCT d.driver_id)`

Because a driver may join to multiple month rows through the cumulative condition, we need to count unique drivers in each grouped month.

### `COUNT(DISTINCT r.ride_id)`

This counts the number of accepted rides in that month without duplication.

---

# Final accepted query

```sql
WITH RECURSIVE Months AS (
    SELECT
        1 AS month
    UNION ALL
    SELECT
        month + 1
    FROM
        Months
    WHERE
        month < 12
), Driver AS (
    SELECT
        driver_id,
        (CASE WHEN YEAR(join_date) = 2019 THEN '1' ELSE MONTH(join_date) END) AS month
    FROM
        Drivers
    WHERE
        YEAR(join_date) <= 2020
), Ride AS (
    SELECT
        MONTH(requested_at) AS month,
        a.ride_id
    FROM
        AcceptedRides AS a
    INNER JOIN
        Rides r
    ON
        r.ride_id = a.ride_id
    WHERE
        YEAR(requested_at) = 2020
)

SELECT
    m.month,
    COUNT(DISTINCT d.driver_id) AS active_drivers,
    COUNT(DISTINCT r.ride_id) AS accepted_rides
FROM
    Months AS m
LEFT JOIN
    Driver AS d
ON
    d.month <= m.month
LEFT JOIN
    Ride AS r
ON
    m.month = r.month
GROUP BY
    m.month
ORDER BY
    m.month;
```

---

# Step-by-step walkthrough on the sample

## Driver activation months

| driver_id | active starting month |
| --------: | --------------------: |
|        10 |                     1 |
|         8 |                     1 |
|         5 |                     2 |
|         7 |                     3 |
|         4 |                     5 |
|         1 |                    10 |

Now count cumulatively by month:

| month | active drivers |
| ----: | -------------: |
|     1 |              2 |
|     2 |              3 |
|     3 |              4 |
|     4 |              4 |
|     5 |              5 |
|     6 |              5 |
|     7 |              5 |
|     8 |              5 |
|     9 |              5 |
|    10 |              6 |
|    11 |              6 |
|    12 |              6 |

---

## Accepted rides per month

From the `Ride` CTE:

| month | accepted rides |
| ----: | -------------: |
|     3 |              1 |
|     6 |              1 |
|     7 |              1 |
|     8 |              1 |
|    11 |              2 |
|    12 |              1 |

All other months have `0`.

---

## Final result

| month | active_drivers | accepted_rides |
| ----: | -------------: | -------------: |
|     1 |              2 |              0 |
|     2 |              3 |              0 |
|     3 |              4 |              1 |
|     4 |              4 |              0 |
|     5 |              5 |              0 |
|     6 |              5 |              1 |
|     7 |              5 |              1 |
|     8 |              5 |              1 |
|     9 |              5 |              0 |
|    10 |              6 |              0 |
|    11 |              6 |              2 |
|    12 |              6 |              1 |

That matches the expected output.

---

# Why this approach is elegant

This solution is strong because it separates the problem into clear components:

- month generation
- driver activation handling
- accepted ride extraction
- final aggregation

That makes the logic easier to test and modify.

It is also a nice example of how CTEs can turn a reporting problem into a readable pipeline.

---

# Important SQL concepts used here

## 1. Recursive CTE

Used to generate all months from 1 to 12.

## 2. `CASE WHEN`

Used to map 2019 drivers to month 1 and 2020 drivers to their join month.

## 3. `LEFT JOIN`

Used to preserve all months in the final output.

## 4. `COUNT(DISTINCT ...)`

Used to avoid duplicate counting during aggregation.

## 5. `INNER JOIN`

Used to identify accepted rides by intersecting `Rides` and `AcceptedRides`.

---

# Complexity

Let:

- `D` = number of rows in `Drivers`
- `R` = number of rows in `Rides`
- `A` = number of rows in `AcceptedRides`

## Time Complexity

The query:

- builds 12 months
- scans eligible drivers
- joins accepted rides with rides
- performs grouped aggregation over 12 months

A practical summary is:

```text
O(D + R + A)
```

plus join and aggregation overhead depending on the SQL engine.

## Space Complexity

Additional space is used for the three CTEs:

- `Months`
- `Driver`
- `Ride`

This is modest and proportional to the intermediate row counts.

---

# Key takeaways

1. Build a month table first so all 12 months appear in the output.
2. Convert driver join dates into an activation month for 2020.
3. Extract accepted rides month by month from `Rides` + `AcceptedRides`.
4. Use `d.month <= m.month` to count active drivers cumulatively.
5. Use `m.month = r.month` to count accepted rides only in that month.
6. `LEFT JOIN` ensures months with zero values still appear.

---

## Final accepted implementation

```sql
WITH RECURSIVE Months AS (
    SELECT
        1 AS month
    UNION ALL
    SELECT
        month + 1
    FROM
        Months
    WHERE
        month < 12
), Driver AS (
    SELECT
        driver_id,
        (CASE WHEN YEAR(join_date) = 2019 THEN '1' ELSE MONTH(join_date) END) AS month
    FROM
        Drivers
    WHERE
        YEAR(join_date) <= 2020
), Ride AS (
    SELECT
        MONTH(requested_at) AS month,
        a.ride_id
    FROM
        AcceptedRides AS a
    INNER JOIN
        Rides r
    ON
        r.ride_id = a.ride_id
    WHERE
        YEAR(requested_at) = 2020
)

SELECT
    m.month,
    COUNT(DISTINCT d.driver_id) AS active_drivers,
    COUNT(DISTINCT r.ride_id) AS accepted_rides
FROM
    Months AS m
LEFT JOIN
    Driver AS d
ON
    d.month <= m.month
LEFT JOIN
    Ride AS r
ON
    m.month = r.month
GROUP BY
    m.month
ORDER BY
    m.month;
```
