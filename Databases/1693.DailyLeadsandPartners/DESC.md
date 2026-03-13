# 1693. Daily Leads and Partners

## Table: DailySales

| Column Name | Type    |
| ----------- | ------- |
| date_id     | date    |
| make_name   | varchar |
| lead_id     | int     |
| partner_id  | int     |

### Notes

- This table **does not have a primary key** and may contain duplicate rows.
- `date_id` represents the **date of the sale**.
- `make_name` represents the **product brand name**.
- `lead_id` is the **lead identifier**.
- `partner_id` is the **partner identifier**.
- The `make_name` column contains only **lowercase English letters**.

---

# Problem

For each combination of:

- `date_id`
- `make_name`

Find:

- the number of **distinct lead_id values**
- the number of **distinct partner_id values**

Return the result table **in any order**.

---

# Example

## Input

### DailySales

| date_id   | make_name | lead_id | partner_id |
| --------- | --------- | ------- | ---------- |
| 2020-12-8 | toyota    | 0       | 1          |
| 2020-12-8 | toyota    | 1       | 0          |
| 2020-12-8 | toyota    | 1       | 2          |
| 2020-12-7 | toyota    | 0       | 2          |
| 2020-12-7 | toyota    | 0       | 1          |
| 2020-12-8 | honda     | 1       | 2          |
| 2020-12-8 | honda     | 2       | 1          |
| 2020-12-7 | honda     | 0       | 1          |
| 2020-12-7 | honda     | 1       | 2          |
| 2020-12-7 | honda     | 2       | 1          |

---

## Output

| date_id   | make_name | unique_leads | unique_partners |
| --------- | --------- | ------------ | --------------- |
| 2020-12-8 | toyota    | 2            | 3               |
| 2020-12-7 | toyota    | 1            | 2               |
| 2020-12-8 | honda     | 2            | 2               |
| 2020-12-7 | honda     | 3            | 2               |

---

# Explanation

### 2020-12-8

- **toyota**
  - leads = `[0, 1]`
  - partners = `[0, 1, 2]`
- **honda**
  - leads = `[1, 2]`
  - partners = `[1, 2]`

### 2020-12-7

- **toyota**
  - leads = `[0]`
  - partners = `[1, 2]`
- **honda**
  - leads = `[0, 1, 2]`
  - partners = `[1, 2]`
