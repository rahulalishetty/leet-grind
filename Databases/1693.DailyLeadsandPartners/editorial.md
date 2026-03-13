# 1693. Daily Leads and Partners — Approach

## Approach: Group By and Aggregation

### Algorithm

To solve this problem in SQL, we group the data by the columns:

- `date_id`
- `make_name`

This allows us to aggregate rows belonging to the same date and product brand.

---

### Counting Unique Leads and Partners

To calculate the number of **distinct leads** and **distinct partners**, we use the SQL function:

```
COUNT(DISTINCT column_name)
```

This function counts the number of **unique values** in a column.

For this problem:

- `COUNT(DISTINCT lead_id)` → counts unique leads
- `COUNT(DISTINCT partner_id)` → counts unique partners

---

### SQL Implementation

```sql
SELECT
    date_id,
    make_name,
    COUNT(DISTINCT lead_id) AS unique_leads,
    COUNT(DISTINCT partner_id) AS unique_partners
FROM DailySales
GROUP BY date_id, make_name;
```

---

### Explanation

The query performs the following steps:

1. Groups rows using `GROUP BY date_id, make_name`.
2. Counts distinct `lead_id` values for each group.
3. Counts distinct `partner_id` values for each group.
4. Returns the aggregated result containing:

| Column          | Description                 |
| --------------- | --------------------------- |
| date_id         | Sales date                  |
| make_name       | Product brand               |
| unique_leads    | Number of distinct leads    |
| unique_partners | Number of distinct partners |
