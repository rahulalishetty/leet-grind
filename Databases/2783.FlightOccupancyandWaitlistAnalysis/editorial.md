# 2783. Flight Occupancy and Waitlist Analysis — Detailed Summary

## Approach: `LEFT JOIN`

This approach solves the problem by combining:

- the flight capacity from `Flights`
- the number of booked passengers from `Passengers`

Then it computes:

- how many passengers can actually get seats
- how many must go to the waitlist

Because every flight must appear in the result, even if it has no passengers, the query uses a `LEFT JOIN`.

---

## Problem Restatement

For each flight, we need to report:

- `flight_id`
- `booked_cnt` → number of passengers who successfully got seats
- `waitlist_cnt` → number of passengers who are on the waitlist

Rules:

- a flight can confirm at most `capacity` passengers
- if bookings exceed capacity, the extra passengers are waitlisted
- output must be ordered by `flight_id`

---

## Core Idea

For each flight:

1. count how many passengers booked that flight
2. compare that count with the flight's capacity
3. compute:
   - booked passengers = smaller of `(capacity, total_bookings)`
   - waitlisted passengers = positive part of `(total_bookings - capacity)`

This leads naturally to:

- `LEAST(...)` for confirmed seats
- `GREATEST(...)` for waitlist count

---

# Query

```sql
SELECT
  f.flight_id,
  LEAST(
    f.capacity,
    COUNT(p.passenger_id)
  ) AS booked_cnt,
  GREATEST(
    0,
    COUNT(p.passenger_id) - f.capacity
  ) AS waitlist_cnt
FROM
  Flights f
  LEFT JOIN Passengers p
    ON f.flight_id = p.flight_id
GROUP BY
  f.flight_id
ORDER BY
  f.flight_id;
```

---

# Step-by-Step Explanation

## 1. Join `Flights` with `Passengers`

```sql
FROM Flights f
LEFT JOIN Passengers p
  ON f.flight_id = p.flight_id
```

### What this does

This aligns each passenger booking with the flight they booked.

So after the join, each flight row is repeated once for every passenger assigned to that flight.

For example, if flight `1` has three passengers, the joined table conceptually contains three rows for flight `1`.

---

## Why `LEFT JOIN` Is Important

A `LEFT JOIN` keeps all flights in the result, even if a flight has no passenger bookings.

That matters because the problem asks for statistics for **each flight**, not just for flights that have passengers.

If we used `INNER JOIN`, flights with zero bookings would disappear.

So `LEFT JOIN` is the correct choice.

---

## 2. Count the passengers per flight

```sql
COUNT(p.passenger_id)
```

After joining, each passenger row contributes one count to its flight.

So this gives the total number of bookings per flight.

Because `COUNT(column)` ignores `NULL`, a flight with no passengers gets:

```text
0
```

which is exactly what we want.

---

## 3. Compute confirmed bookings with `LEAST`

```sql
LEAST(
  f.capacity,
  COUNT(p.passenger_id)
) AS booked_cnt
```

### Why this works

A flight cannot confirm more seats than its capacity.

So the number of successfully booked passengers is the smaller of:

- flight capacity
- total number of booked passengers

Examples:

- capacity = 2, passengers = 3 → booked = 2
- capacity = 2, passengers = 2 → booked = 2
- capacity = 2, passengers = 1 → booked = 1

That is exactly what `LEAST(...)` returns.

---

## 4. Compute waitlisted passengers with `GREATEST`

```sql
GREATEST(
  0,
  COUNT(p.passenger_id) - f.capacity
) AS waitlist_cnt
```

### Why this works

The number of waitlisted passengers is:

```text
total bookings - capacity
```

But if bookings do not exceed capacity, this value would be negative.

A negative waitlist does not make sense, so we clamp it at `0`.

That is why we use:

```sql
GREATEST(0, ...)
```

Examples:

- passengers = 3, capacity = 2 → waitlist = 1
- passengers = 2, capacity = 2 → waitlist = 0
- passengers = 1, capacity = 2 → waitlist = 0

---

## 5. Group by flight

```sql
GROUP BY f.flight_id
```

This ensures one result row per flight.

All passenger rows belonging to the same flight are aggregated together.

---

## 6. Order by flight ID

```sql
ORDER BY f.flight_id
```

This matches the required output order.

---

# Worked Example

## Input

### Flights

| flight_id | capacity |
| --------: | -------: |
|         1 |        2 |
|         2 |        2 |
|         3 |        1 |

### Passengers

| passenger_id | flight_id |
| -----------: | --------: |
|          101 |         1 |
|          102 |         1 |
|          103 |         1 |
|          104 |         2 |
|          105 |         2 |
|          106 |         3 |
|          107 |         3 |

---

# Per-Flight Analysis

## Flight 1

Capacity:

```text
2
```

Passengers:

- 101
- 102
- 103

Total bookings:

```text
3
```

### Confirmed seats

```sql
LEAST(2, 3) = 2
```

### Waitlist

```sql
GREATEST(0, 3 - 2) = 1
```

Result:

| flight_id | booked_cnt | waitlist_cnt |
| --------: | ---------: | -----------: |
|         1 |          2 |            1 |

---

## Flight 2

Capacity:

```text
2
```

Passengers:

- 104
- 105

Total bookings:

```text
2
```

### Confirmed seats

```sql
LEAST(2, 2) = 2
```

### Waitlist

```sql
GREATEST(0, 2 - 2) = 0
```

Result:

| flight_id | booked_cnt | waitlist_cnt |
| --------: | ---------: | -----------: |
|         2 |          2 |            0 |

---

## Flight 3

Capacity:

```text
1
```

Passengers:

- 106
- 107

Total bookings:

```text
2
```

### Confirmed seats

```sql
LEAST(1, 2) = 1
```

### Waitlist

```sql
GREATEST(0, 2 - 1) = 1
```

Result:

| flight_id | booked_cnt | waitlist_cnt |
| --------: | ---------: | -----------: |
|         3 |          1 |            1 |

---

# Final Output

| flight_id | booked_cnt | waitlist_cnt |
| --------: | ---------: | -----------: |
|         1 |          2 |            1 |
|         2 |          2 |            0 |
|         3 |          1 |            1 |

---

# Why This Query Is Elegant

This solution is elegant because it does not try to simulate seat assignment passenger by passenger.

Instead, it uses a simple aggregate observation:

- if `n` passengers booked and capacity is `c`
- then:
  - confirmed passengers = `min(n, c)`
  - waitlisted passengers = `max(n - c, 0)`

That turns the whole problem into a small aggregation query.

---

# Clause-by-Clause Breakdown

## `SELECT f.flight_id`

Returns the flight identifier.

---

## `COUNT(p.passenger_id)`

Counts how many passengers booked the flight.

---

## `LEAST(f.capacity, COUNT(p.passenger_id))`

Limits confirmed seats to capacity.

---

## `GREATEST(0, COUNT(p.passenger_id) - f.capacity)`

Counts only the overflow beyond capacity.

---

## `FROM Flights f LEFT JOIN Passengers p ...`

Ensures all flights appear, including flights with zero passengers.

---

## `GROUP BY f.flight_id`

Aggregates results per flight.

---

## `ORDER BY f.flight_id`

Sorts output as required.

---

# Important Note About Flights with No Passengers

Suppose a flight has:

- capacity = 5
- no passengers at all

With `LEFT JOIN`, that flight still appears.

Then:

```sql
COUNT(p.passenger_id) = 0
```

So:

```sql
LEAST(5, 0) = 0
GREATEST(0, 0 - 5) = 0
```

Result:

|   flight_id | booked_cnt | waitlist_cnt |
| ----------: | ---------: | -----------: |
| some_flight |          0 |            0 |

That is correct.

---

# Why `COUNT(p.passenger_id)` Is Better Than `COUNT(*)`

This matters with `LEFT JOIN`.

If a flight has no passengers, the join still produces one row for the flight, but the passenger columns are `NULL`.

- `COUNT(*)` would count that row as `1`
- `COUNT(p.passenger_id)` counts only non-null passenger IDs, so it becomes `0`

That is why `COUNT(p.passenger_id)` is the correct choice.

---

# Alternative Formula View

You can summarize the logic mathematically as:

```text
total_bookings = COUNT(passengers)
booked_cnt = min(total_bookings, capacity)
waitlist_cnt = max(total_bookings - capacity, 0)
```

The SQL query directly implements those formulas.

---

# Complexity Analysis

Let:

- `F` = number of flights
- `P` = number of passengers

The query joins flights to passengers and aggregates by flight.

This is efficient and scales well because each passenger belongs to exactly one flight.

Conceptually, the work is dominated by:

- one join
- one grouping step

---

# Final Recommended Query

```sql
SELECT
  f.flight_id,
  LEAST(
    f.capacity,
    COUNT(p.passenger_id)
  ) AS booked_cnt,
  GREATEST(
    0,
    COUNT(p.passenger_id) - f.capacity
  ) AS waitlist_cnt
FROM
  Flights f
  LEFT JOIN Passengers p
    ON f.flight_id = p.flight_id
GROUP BY
  f.flight_id
ORDER BY
  f.flight_id;
```

---

# Key Takeaways

- Join flights with passengers by `flight_id`
- Count bookings per flight with `COUNT(p.passenger_id)`
- Use `LEAST(capacity, bookings)` for confirmed seats
- Use `GREATEST(0, bookings - capacity)` for waitlisted passengers
- Use `LEFT JOIN` so flights with zero passengers are still included

---
