# 2082. The Number of Rich Customers — Approach

## Approach: Counting Unique Values

### Overview

The goal is to determine how many **distinct customers** have **at least one bill greater than 500**.

To solve this, we perform two main steps:

1. **Filter bills** where the amount is greater than 500.
2. **Count unique customers** among those filtered rows.

---

# Algorithm

### 1. Filter Rows

First, we filter the `Store` table to include only rows where:

```
amount > 500
```

This ensures we only consider **bills that qualify as expensive**.

Example filtered rows:

| bill_id | customer_id | amount |
| ------- | ----------- | ------ |
| 6       | 1           | 549    |
| 8       | 1           | 834    |
| 11      | 3           | 657    |

---

### 2. Count Distinct Customers

Since a customer might have **multiple qualifying bills**, we must count **unique customers**.

SQL provides the aggregation function:

```
COUNT(DISTINCT column_name)
```

This counts the number of **unique values** in a column.

So we apply:

```
COUNT(DISTINCT customer_id)
```

This returns the number of customers who have **at least one bill greater than 500**.

---

# SQL Implementation

```sql
SELECT
    COUNT(DISTINCT customer_id) AS rich_count
FROM
    Store
WHERE
    amount > 500;
```

---

# Key SQL Concepts Used

- `WHERE` clause for filtering rows
- `COUNT(DISTINCT ...)` for counting unique values
- Aggregation functions
