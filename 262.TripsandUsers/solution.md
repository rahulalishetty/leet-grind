# Trip Cancellation Rate (LeetCode 262) — 3 SQL Approaches (Join vs Subquery vs CTE)

Goal: For each day in a given date range, compute the **cancellation rate** of trips, **excluding trips where either the client or driver is banned**.

Typical schema:

```sql
Trips(id, client_id, driver_id, city_id, status, request_at)
Users(users_id, banned, role)
```

Output columns:

- `Day` (date)
- `Cancellation Rate` (rounded to 2 decimals)

Cancellation Rate definition (per day):

```text
(# trips with status != 'completed') / (total # trips)
```

Date range in the problem statement:

- `request_at` between `'2013-10-01'` and `'2013-10-03'` (inclusive)

---

## Core Idea Shared by All Approaches

1. Restrict to the date range.
2. Exclude banned users:
   - client must be `banned = 'No'`
   - driver must be `banned = 'No'`
3. Group by day.
4. Compute:
   - numerator: count of non-completed trips
   - denominator: total trips

A common SQL trick:

- In MySQL, boolean expressions like `(status != 'completed')` evaluate to `0` or `1`.
- So you can do:
  - `SUM(status != 'completed')` to count non-completed rows.

---

## Approach 1: JOIN (Assemble then Filter)

### Intuition

First “assemble” trip records together with both users (client + driver) via joins, then filter out banned users and wrong dates, and finally aggregate.

### Steps

1. Start from `Trips`.
2. Join `Users` as `Clients` using `Trips.client_id = Clients.users_id`.
3. Join `Users` as `Drivers` using `Trips.driver_id = Drivers.users_id`.
4. Filter:
   - `Clients.banned = 'No'`
   - `Drivers.banned = 'No'`
   - date range
5. Group by day and compute rate.

### Implementation

```sql
SELECT
  request_at AS Day,
  ROUND(
    SUM(status != 'completed') / COUNT(*),
    2
  ) AS 'Cancellation Rate'
FROM
  Trips
  LEFT JOIN Users AS Clients ON Trips.client_id = Clients.users_id
  LEFT JOIN Users AS Drivers ON Trips.driver_id = Drivers.users_id
WHERE
  Clients.banned = 'No'
  AND Drivers.banned = 'No'
  AND request_at BETWEEN '2013-10-01' AND '2013-10-03'
GROUP BY
  Day;
```

### Notes / Practical Considerations

- Although written with `LEFT JOIN`, the `WHERE Clients.banned='No' AND Drivers.banned='No'` effectively turns it into an `INNER JOIN` for valid matches.
- Clear and widely used.
- Good with indexes on:
  - `Trips.request_at`
  - `Trips.client_id`, `Trips.driver_id`
  - `Users.users_id`

---

## Approach 2: Subqueries (Exclude banned IDs first)

### Intuition

Instead of joining user rows, explicitly list banned users and exclude any trip whose client or driver is in that banned list.

### Steps

1. Start from `Trips`.
2. Filter by date range.
3. Exclude rows where:
   - `driver_id` is in (banned user ids)
   - `client_id` is in (banned user ids)
4. Group by day and compute rate.

### Implementation

```sql
SELECT
  request_at AS Day,
  ROUND(
    SUM(status != 'completed') / COUNT(status),
    2
  ) AS 'Cancellation Rate'
FROM
  Trips
WHERE
  request_at BETWEEN '2013-10-01' AND '2013-10-03'
  AND driver_id NOT IN (
    SELECT users_id
    FROM Users
    WHERE banned = 'Yes'
  )
  AND client_id NOT IN (
    SELECT users_id
    FROM Users
    WHERE banned = 'Yes'
  )
GROUP BY
  Day;
```

### Notes / Practical Considerations

- Watch out for `NOT IN` with `NULL`s in some SQL dialects (can cause unexpected behavior). If `users_id` is never NULL, you're safe.
- Can be very readable.
- Many optimizers rewrite this into an efficient plan, but performance can vary with data size.

---

## Approach 3: CTE (Prepare a clean dataset, then aggregate)

### Intuition

Build a “clean working set” first (only eligible trips + a computed cancelled flag). Then aggregate from that clean set.

### Steps

1. CTE `TripStatus`:
   - select `request_at AS Day`
   - compute `cancelled = (status != 'completed')`
   - join to client user row, enforce `Banned='No'`
   - join to driver user row, enforce `Banned='No'`
   - filter date range
2. Main query:
   - group by `Day`
   - cancellation rate = `SUM(cancelled) / COUNT(cancelled)`
   - round to 2 decimals

### Implementation

```sql
WITH TripStatus AS (
  SELECT
    Request_at AS Day,
    T.status != 'completed' AS cancelled
  FROM
    Trips T
    JOIN Users C ON Client_Id = C.Users_Id AND C.Banned = 'No'
    JOIN Users D ON Driver_Id = D.Users_Id AND D.Banned = 'No'
  WHERE
    Request_at BETWEEN '2013-10-01' AND '2013-10-03'
)
SELECT
  Day,
  ROUND(
    SUM(cancelled) / COUNT(cancelled),
    2
  ) AS 'Cancellation Rate'
FROM
  TripStatus
GROUP BY
  Day;
```

### Notes / Practical Considerations

- Reads cleanly: “filter + compute flag” first, “aggregate” second.
- Very maintainable when the logic grows (more filters, more computed columns).
- Requires DB support for CTEs (MySQL 8+, Postgres, SQL Server, etc.).

---

## Choosing Between Them

- **JOIN approach**: Most standard and explicit. Great default.
- **Subquery approach**: Nice when you conceptually think “exclude these users”; can be compact.
- **CTE approach**: Best when you want clarity and extensibility (add more derived columns / steps).

In real systems, the best choice often depends on:

- indexing,
- data sizes,
- and the query optimizer of your database.

---

## Small Implementation Details That Matter

### 1) Boolean counting trick (MySQL)

In MySQL, `(status != 'completed')` becomes `1` for true and `0` for false, so:

- `SUM(status != 'completed')` counts non-completed trips.
- `COUNT(*)` counts total trips.

### 2) Avoid integer division surprises

Some SQL dialects may do integer division if both sides are integers.
A safe pattern is:

```sql
ROUND(1.0 * SUM(status != 'completed') / COUNT(*), 2)
```

### 3) Indexing tips

Helpful indexes:

- `Trips(request_at)`
- `Trips(client_id)`, `Trips(driver_id)`
- `Users(users_id)` (usually PK)

---
