# 2072. The Winner University — Approach

## Approach: Comparative Aggregation Query

### Overview

The key idea is to:

1. Count the number of **excellent students** in each university.
2. Compare these counts.
3. Return the university with the **greater number of excellent students**, or **No Winner** if the counts are equal.

An **excellent student** is defined as:

```
score >= 90
```

---

# Intuition

## 1. Counting Excellent Students

First, we identify excellent students in each university by filtering scores:

```
WHERE score >= 90
```

We then use:

```
COUNT(*)
```

to count how many students satisfy this condition.

---

## 2. Separate Subqueries for Each University

We compute the counts independently for:

- **New York University**
- **California University**

Example:

```
SELECT COUNT(*)
FROM NewYork
WHERE score >= 90
```

This returns the number of excellent students in New York.

Similarly for California.

---

## 3. Comparing the Counts

Once both counts are computed, we compare them using a **CASE statement**.

Logic:

- If New York has more excellent students → `"New York University"`
- If California has more excellent students → `"California University"`
- If both have the same number → `"No Winner"`

---

## 4. Returning a Single Row Result

The query returns a **single row** containing the competition result.

This is done by embedding the counts inside subqueries and comparing them directly.

---

# SQL Implementation

```sql
SELECT
  CASE
    WHEN NY.excellent_students > CA.excellent_students THEN 'New York University'
    WHEN NY.excellent_students < CA.excellent_students THEN 'California University'
    ELSE 'No Winner'
  END AS winner
FROM
  (
    SELECT COUNT(*) AS excellent_students
    FROM NewYork
    WHERE score >= 90
  ) NY,
  (
    SELECT COUNT(*) AS excellent_students
    FROM California
    WHERE score >= 90
  ) CA;
```

---

# Key SQL Concepts Used

- `COUNT(*)` aggregation
- Filtering with `WHERE`
- Subqueries
- `CASE` conditional logic
- Comparative evaluation between aggregated values
