# 1633. Percentage of Users Attended a Contest — Approach

## Approach: Percentage Calculation with Aggregation

The SQL solution follows a direct strategy to compute the **percentage of users registered for each contest**.

The approach uses:

- `GROUP BY`
- aggregate functions
- a subquery to count total users
- rounding and sorting

---

# Intuition

We compute:

```
percentage = (number of users registered in contest / total users) * 100
```

Then round the result to **two decimal places**.

---

# Step 1: Count Unique Users per Contest

First we group registrations by contest and count the distinct users.

```sql
SELECT
  contest_id,
  COUNT(DISTINCT user_id) AS unique_users
FROM Register
GROUP BY contest_id;
```

This gives the number of users registered for each contest.

---

# Step 2: Calculate Total Number of Users

We need the total number of users in the system.

```sql
SELECT COUNT(user_id)
FROM Users;
```

This value is used as the **denominator** in the percentage formula.

---

# Step 3: Calculate Percentage

We divide the number of registered users by total users and multiply by **100**.

```sql
ROUND(
  COUNT(DISTINCT user_id) * 100.0 / (SELECT COUNT(user_id) FROM Users),
  2
) AS percentage
```

`ROUND(..., 2)` ensures the result has **two decimal places**.

---

# Step 4: Order the Results

Finally, we sort the output:

1. By **percentage descending**
2. By **contest_id ascending** when percentages tie

```sql
ORDER BY percentage DESC, contest_id ASC;
```

---

# Final Implementation

```sql
SELECT
  contest_id,
  ROUND(
    COUNT(DISTINCT user_id) * 100 / (
      SELECT COUNT(user_id)
      FROM Users
    ),
    2
  ) AS percentage
FROM Register
GROUP BY contest_id
ORDER BY percentage DESC, contest_id;
```

---

# Key SQL Concepts Used

- **COUNT(DISTINCT)** → Counts unique registered users
- **Subquery** → Computes total number of users
- **ROUND()** → Formats percentage output
- **GROUP BY** → Aggregates registrations by contest
- **ORDER BY** → Controls final result ordering
