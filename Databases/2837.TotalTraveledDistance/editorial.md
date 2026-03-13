# Calculate Distance Traveled by Each User — Approach

## Approach: Using LEFT JOIN and IFNULL()

### Intuition

The required output contains:

- `user_id`
- `name`
- `total traveled distance`

However, these values exist in **two different tables**:

- `Users` → contains user details (`user_id`, `name`)
- `Rides` → contains ride information (`distance`)

Therefore, we must **join the two tables using `user_id`**.

A **LEFT JOIN** is required because some users may **not have any rides**, but they still must appear in the result.

---

# Algorithm

## 1. Join the Tables

We join the tables using:

```
Users LEFT JOIN Rides
ON Users.user_id = Rides.user_id
```

This ensures:

- Every user appears in the result
- Ride data appears only when it exists

---

## 2. Calculate Total Distance

To compute total distance traveled per user, we use:

```
SUM(distance)
```

This aggregates all ride distances belonging to the same user.

---

## 3. Group the Result

Since aggregation is used, we group by:

```
GROUP BY user_id, name
```

This ensures that **each user gets a single row with their total distance**.

---

## 4. Handle Users With No Rides

Users without rides will have **NULL values** for distance after the join.

To convert NULL to **0**, we use:

```
IFNULL(SUM(distance), 0)
```

---

## 5. Order the Output

The problem requires results ordered by:

```
ORDER BY user_id
```

Ascending order is the default.

---

# SQL Implementation

```sql
SELECT
    u.user_id,
    u.name,
    IFNULL(SUM(distance), 0) AS 'traveled distance'
FROM
    Users AS u
LEFT JOIN
    Rides AS r
ON
    u.user_id = r.user_id
GROUP BY
    user_id, name
ORDER BY
    user_id;
```

---

# Key SQL Concepts Used

- `LEFT JOIN`
- `SUM()` aggregation
- `GROUP BY`
- `IFNULL()` for handling NULL values
- `ORDER BY`
