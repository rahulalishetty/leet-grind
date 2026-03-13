# Approach: Selecting Rows Based on Conditions

## Idea

In SQL, we can use the keyword **DISTINCT** in the `SELECT` statement to retrieve **unique elements** from the table `Views`.

We also apply a **filter condition** using the `WHERE` clause.

The condition ensures we only select rows where:

```
author_id = viewer_id
```

This indicates that the **author viewed their own article**.

---

# Step 1: Select Unique Authors Who Viewed Their Own Articles

We retrieve the distinct `author_id` values from the table where the author and viewer are the same person.

```sql
SELECT
    DISTINCT author_id
FROM
    Views
WHERE
    author_id = viewer_id;
```

---

# Step 2: Rename the Column

The problem requires the output column to be named **`id`**.

We can rename the column using an **alias**.

```sql
SELECT
    DISTINCT author_id AS id
FROM
    Views
WHERE
    author_id = viewer_id;
```

---

# Step 3: Sort the Result

The final result must be **sorted in ascending order** based on the `id` column.

We use the `ORDER BY` clause.

```sql
ORDER BY id
```

---

# Final SQL Query

```sql
SELECT
    DISTINCT author_id AS id
FROM
    Views
WHERE
    author_id = viewer_id
ORDER BY
    id;
```

---

# Key Points

- `DISTINCT` ensures **duplicate author IDs are removed**.
- `author_id = viewer_id` identifies cases where **authors viewed their own articles**.
- `AS id` renames the column to match the **required output format**.
- `ORDER BY id` ensures the result is **sorted in ascending order**.
