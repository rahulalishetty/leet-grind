# 1179. Reformat Department Table — Explanation

## Overview

**Problem reference:** Reformat the table by creating month columns that represent the revenue of each month for every department `id`.

If revenue for a specific month does not exist, the value should be **NULL**.

The final result should contain:

- One row per **department id**
- One column for **each month’s revenue**

This transformation is commonly called a **pivot table**.

A **pivot** rotates rows into columns so that values belonging to categories (months here) become separate columns.

---

# Approach 1: GROUP BY with Conditional Aggregation

## Intuition

We need to group the table by `id` because we want to show **each month's revenue for each department**.

However, if we simply group by `id`, SQL will not know which revenue to display when there are multiple rows for the same id.

Example:

| id  | revenue |
| --- | ------- |
| 1   | 8000    |
| 1   | 6000    |

If we run:

```sql
SELECT id, revenue
FROM Department
GROUP BY id;
```

The DBMS would not know which revenue to choose.

---

## Solution Idea

We separate each month into its own column using **conditional expressions inside aggregate functions**.

For example:

```
IF(month = 'Jan', revenue, NULL)
```

This returns:

- the revenue if the month is **Jan**
- otherwise **NULL**

After separating months, the intermediate result would look like this:

| id  | Jan_Revenue | Feb_Revenue | Mar_Revenue | ... | Dec_Revenue |
| --- | ----------- | ----------- | ----------- | --- | ----------- |
| 1   | 8000        | NULL        | NULL        | ... | NULL        |
| 1   | NULL        | 7000        | NULL        | ... | NULL        |
| 1   | NULL        | NULL        | 6000        | ... | NULL        |
| 2   | 9000        | NULL        | NULL        | ... | NULL        |
| 3   | NULL        | 10000       | NULL        | ... | NULL        |

We still have multiple rows per `id`.

To reduce them to a **single row**, we apply an **aggregate function**.

Functions such as:

```
SUM
MAX
MIN
```

ignore NULL values and therefore extract the correct revenue for each month.

Because `(id, month)` is the **primary key**, there will never be more than one valid revenue value for each `(id, month)` combination.

---

## Algorithm

1. Group the table by `id`.
2. Create a column for each month.
3. Use an aggregate function with a conditional expression to select the revenue for that month.

---

## Implementation (MySQL)

```sql
SELECT
  id,
  SUM(IF(month = "Jan", revenue, NULL)) AS Jan_Revenue,
  SUM(IF(month = "Feb", revenue, NULL)) AS Feb_Revenue,
  SUM(IF(month = "Mar", revenue, NULL)) AS Mar_Revenue,
  SUM(IF(month = "Apr", revenue, NULL)) AS Apr_Revenue,
  SUM(IF(month = "May", revenue, NULL)) AS May_Revenue,
  SUM(IF(month = "Jun", revenue, NULL)) AS Jun_Revenue,
  SUM(IF(month = "Jul", revenue, NULL)) AS Jul_Revenue,
  SUM(IF(month = "Aug", revenue, NULL)) AS Aug_Revenue,
  SUM(IF(month = "Sep", revenue, NULL)) AS Sep_Revenue,
  SUM(IF(month = "Oct", revenue, NULL)) AS Oct_Revenue,
  SUM(IF(month = "Nov", revenue, NULL)) AS Nov_Revenue,
  SUM(IF(month = "Dec", revenue, NULL)) AS Dec_Revenue
FROM
  Department
GROUP BY
  id;
```

---

## Alternative Conditional Expressions

Instead of `IF`, we can use `CASE`:

```sql
SELECT
  id,
  MIN(
    CASE
      WHEN month = "Jan" THEN revenue
    END
  ) AS Jan_Revenue
FROM Department
GROUP BY id;
```

Other functions like `IFNULL` may also be used depending on SQL dialect.

---

# Approach 2: LEFT JOIN

## Intuition

Another solution is to join the `Department` table **multiple times**, once for each month.

First we create a list of unique department ids.

Then we **LEFT JOIN** each month separately.

We use **LEFT JOIN instead of INNER JOIN** so that months without revenue appear as **NULL**.

---

## Algorithm

1. Create a temporary table containing distinct department ids.
2. Join each month separately using `LEFT JOIN`.
3. Rename each joined table to represent its month.

---

## Implementation (MySQL)

```sql
SELECT
  Ids.id,
  January.revenue AS Jan_Revenue,
  Feburary.revenue AS Feb_Revenue,
  March.revenue AS Mar_Revenue,
  April.revenue AS Apr_Revenue,
  May.revenue AS May_Revenue,
  June.revenue AS Jun_Revenue,
  July.revenue AS Jul_Revenue,
  August.revenue AS Aug_Revenue,
  September.revenue AS Sep_Revenue,
  October.revenue AS Oct_Revenue,
  November.revenue AS Nov_Revenue,
  December.revenue AS Dec_Revenue
FROM
  (
    SELECT DISTINCT id
    FROM Department
  ) AS Ids
  LEFT JOIN Department AS January ON (
    Ids.id = January.id AND January.month = "Jan"
  )
  LEFT JOIN Department AS Feburary ON (
    Ids.id = Feburary.id AND Feburary.month = "Feb"
  )
  LEFT JOIN Department AS March ON (
    Ids.id = March.id AND March.month = "Mar"
  )
  LEFT JOIN Department AS April ON (
    Ids.id = April.id AND April.month = "Apr"
  )
  LEFT JOIN Department AS May ON (
    Ids.id = May.id AND May.month = "May"
  )
  LEFT JOIN Department AS June ON (
    Ids.id = June.id AND June.month = "Jun"
  )
  LEFT JOIN Department AS July ON (
    Ids.id = July.id AND July.month = "Jul"
  )
  LEFT JOIN Department AS August ON (
    Ids.id = August.id AND August.month = "Aug"
  )
  LEFT JOIN Department AS September ON (
    Ids.id = September.id AND September.month = "Sep"
  )
  LEFT JOIN Department AS October ON (
    Ids.id = October.id AND October.month = "Oct"
  )
  LEFT JOIN Department AS November ON (
    Ids.id = November.id AND November.month = "Nov"
  )
  LEFT JOIN Department AS December ON (
    Ids.id = December.id AND December.month = "Dec"
  );
```

---

# Conclusion

**Approach 1 (GROUP BY with conditional aggregation)** is recommended.

### Reasons

1. **Simpler query**
2. **Better performance**
3. **Single table scan**

In Approach 2:

- The DBMS must evaluate the table **once for every JOIN**
- Since we join **12 times**, it results in significantly more work.

Using `EXPLAIN` to analyze both queries:

- **Approach 1:** checks only the base rows once.
- **Approach 2:** checks rows repeatedly for each JOIN.

Example comparison with sample data:

| Approach | Rows Checked |
| -------- | ------------ |
| GROUP BY | ~5           |
| JOIN     | >60          |

Therefore **conditional aggregation with GROUP BY is both cleaner and more efficient**.
