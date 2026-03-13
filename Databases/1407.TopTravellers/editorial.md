# 1407. Top Travellers — Explanation

## Overview

This problem requires careful attention to a few important details before writing the SQL query.

### Key Observations

1. We must calculate the **total distance travelled by each user**.
2. Some users may **not have any rides**, so they must still appear in the result.
3. Therefore, we must use a **LEFT JOIN** to ensure every user from the `Users` table appears in the result.

---

## Handling Users With No Rides

If a user has no rides, the aggregated distance will become `NULL`.

To convert `NULL` to `0`, we can use either:

### IFNULL()

```
IFNULL(value, replacement)
```

- Returns `value` if it is **not NULL**
- Otherwise returns `replacement`

Example:

```sql
IFNULL(SUM(distance), 0)
```

---

### COALESCE()

```
COALESCE(value1, value2, ...)
```

- Returns the **first non-NULL value**
- If all values are NULL, it returns NULL

Example:

```sql
COALESCE(SUM(distance), 0)
```

Both `IFNULL()` and `COALESCE()` work the same for this problem.

---

## Grouping the Data

Users might have the **same name**, but the column `id` is the **primary key** and therefore unique.

Because of this, we must group by:

```
GROUP BY id
```

If we grouped by `name`, users with the same name would be incorrectly merged.

---

## Ordering the Result

The output requires **two ordering rules**:

1. **travelled_distance DESC** (largest distance first)
2. **name ASC** (alphabetical order when distances tie)

---

# Approach: LEFT JOIN

## Algorithm

1. Select the required columns:
   - `name`
   - total travelled distance

2. Use **LEFT JOIN** so that users without rides still appear.

3. Aggregate the total distance using:

```
SUM(distance)
```

4. Replace NULL values with `0` using `IFNULL()` or `COALESCE()`.

5. Group the result by **user id**.

6. Sort the result by:
   - travelled distance descending
   - name ascending

---

# Implementation (MySQL)

```sql
SELECT
    u.name,
    IFNULL(SUM(distance), 0) AS travelled_distance
FROM Users u
LEFT JOIN Rides r
ON u.id = r.user_id
GROUP BY u.id
ORDER BY 2 DESC, 1 ASC;
```
