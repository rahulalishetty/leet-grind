# 1495. Friendly Movies Streamed Last Month — Approach

## Overview

The key to solving this problem is to carefully read the question, break it into individual conditions, and ensure that all conditions are included in the SQL query.

From the problem statement and tables, the required conditions are:

- **Distinct titles** → use `DISTINCT` when selecting the `title` column.
- **Kid-friendly content** → `Kids_content = 'Y'`.
- **Content must be a movie** → `content_type = 'Movies'`.
- **Program streamed in June 2020** → filter `program_date` accordingly.

---

# Filtering by Month and Year

There are several ways to filter records for **June 2020** from a date column.

### Method 1 — Using MONTH() and YEAR()

```sql
WHERE MONTH(program_date) = 6
AND YEAR(program_date) = 2020
```

---

### Method 2 — Using DATE_FORMAT()

```sql
WHERE DATE_FORMAT(program_date, '%Y-%m') = '2020-06'
```

---

### Method 3 — Using LEFT() (treating date as string)

```sql
WHERE LEFT(program_date, 7) = '2020-06'
```

All three approaches correctly filter rows belonging to **June 2020**.

---

# Approach

## Algorithm

1. Select the column required in the output: **DISTINCT title**.
2. **JOIN** the `Content` and `TVProgram` tables using `content_id`.
3. Apply the following filters:
   - `Kids_content = 'Y'`
   - `content_type = 'Movies'`
   - `program_date` is in **June 2020**.
4. Return the distinct movie titles.

---

# Implementation (MySQL)

```sql
SELECT
    DISTINCT c.title
FROM Content c
JOIN TVProgram p
ON c.content_id = p.content_id
WHERE
    c.Kids_content = 'Y'
AND c.content_type = 'Movies'
AND MONTH(p.program_date) = 6
AND YEAR(p.program_date) = 2020;
```
