# 1179. Reformat Department Table

## Table: Department

| Column Name | Type    |
| ----------- | ------- |
| id          | int     |
| revenue     | int     |
| month       | varchar |

Notes:

- `(id, month)` is the **primary key** of this table.
- The table contains the **revenue of each department per month**.
- The `month` column contains values from:

```
["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"]
```

---

# Problem

Reformat the table so that:

- Each **department id** appears once.
- There is a **separate revenue column for each month**.

The resulting table should contain:

- **1 column for department id**
- **12 columns for monthly revenues**

Columns should be:

```
Jan_Revenue
Feb_Revenue
Mar_Revenue
...
Dec_Revenue
```

If a department does **not have revenue for a specific month**, the value should be **NULL**.

Return the result table **in any order**.

---

# Example

## Input

### Department table

| id  | revenue | month |
| --- | ------- | ----- |
| 1   | 8000    | Jan   |
| 2   | 9000    | Jan   |
| 3   | 10000   | Feb   |
| 1   | 7000    | Feb   |
| 1   | 6000    | Mar   |

---

# Output

| id  | Jan_Revenue | Feb_Revenue | Mar_Revenue | ... | Dec_Revenue |
| --- | ----------- | ----------- | ----------- | --- | ----------- |
| 1   | 8000        | 7000        | 6000        | ... | NULL        |
| 2   | 9000        | NULL        | NULL        | ... | NULL        |
| 3   | NULL        | 10000       | NULL        | ... | NULL        |

---

# Explanation

- Department **1** has revenue in **Jan, Feb, Mar**.
- Department **2** has revenue only in **Jan**.
- Department **3** has revenue only in **Feb**.

For months **Apr to Dec**, there are no entries, so the values are **NULL**.

The final table therefore has **13 columns**:

```
1 id column
+
12 month revenue columns
```
