# Followers Count — SQL Approach

## Intuition

We need to count how many times each `user_id` appears in the `followers` table.

Each `(user_id, follower_id)` pair is unique, so counting the number of rows for a given `user_id` gives the **number of followers** that user has.

To do this, we use the SQL **COUNT()** aggregate function.

Since COUNT is an aggregate function, we must specify how the rows should be grouped.
This is done using the **GROUP BY** clause.

We group by `user_id` so that each user gets a single row showing their follower count.

Finally, the results are ordered by `user_id`.

---

# Algorithm

### 1. Select the Required Columns

We select:

- `user_id`
- the number of occurrences of that `user_id`

```sql
SELECT user_id, COUNT(user_id) AS followers_count
```

`COUNT(user_id)` counts the number of rows belonging to that user.

---

### 2. Specify the Table

```sql
FROM followers
```

This indicates that the data comes from the **followers** table.

---

### 3. Group Rows

```sql
GROUP BY user_id
```

This groups all rows belonging to the same `user_id` together.

---

### 4. Order the Results

```sql
ORDER BY user_id ASC
```

This sorts the output by `user_id` in ascending order.

Note: `ASC` is optional because ascending order is the default.

---

# Implementation

```sql
SELECT user_id, COUNT(user_id) AS followers_count
FROM followers
GROUP BY user_id
ORDER BY user_id ASC;
```

---

# Key SQL Concepts

- **COUNT()** → Counts the number of rows
- **GROUP BY** → Groups rows by user
- **ORDER BY** → Sorts the final result
