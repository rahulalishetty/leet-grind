# 579. Find Cumulative Salary of an Employee

## Problem

### Table: Employee

| Column Name | Type |
| ----------- | ---- |
| id          | int  |
| month       | int  |
| salary      | int  |

- `(id, month)` is the **primary key**.
- Each row records the salary of an employee for a specific month in **2020**.

---

## Goal

Calculate the **cumulative salary summary** for every employee.

The cumulative salary for a month is defined as:

- the sum of salaries for:
  - the **current month**
  - the **previous month**
  - the **month before that**

So each row represents a **3‑month rolling salary sum**.

### Special Rules

1. If an employee **did not work in earlier months**, those months contribute **0**.
2. **Do not include the most recent month** that the employee worked.
3. **Do not include months where the employee did not work.**
4. Return the result **ordered by `id` ascending**, and if tied **by `month` descending**.

---

# Example

## Input

### Employee Table

| id  | month | salary |
| --- | ----- | ------ |
| 1   | 1     | 20     |
| 2   | 1     | 20     |
| 1   | 2     | 30     |
| 2   | 2     | 30     |
| 3   | 2     | 40     |
| 1   | 3     | 40     |
| 3   | 3     | 60     |
| 1   | 4     | 60     |
| 3   | 4     | 70     |
| 1   | 7     | 90     |
| 1   | 8     | 90     |

---

## Output

| id  | month | Salary |
| --- | ----- | ------ |
| 1   | 7     | 90     |
| 1   | 4     | 130    |
| 1   | 3     | 90     |
| 1   | 2     | 50     |
| 1   | 1     | 20     |
| 2   | 1     | 20     |
| 3   | 3     | 100    |
| 3   | 2     | 40     |

---

# Explanation

## Employee 1

Employee **1** has salary records for months:

1, 2, 3, 4, 7, 8

The **most recent month is 8**, which must be **excluded**.

Remaining months:

1, 2, 3, 4, 7

### Calculations

| id  | month | calculation  | salary |
| --- | ----- | ------------ | ------ |
| 1   | 7     | 90 + 0 + 0   | 90     |
| 1   | 4     | 60 + 40 + 30 | 130    |
| 1   | 3     | 40 + 30 + 20 | 90     |
| 1   | 2     | 30 + 20 + 0  | 50     |
| 1   | 1     | 20 + 0 + 0   | 20     |

Months **5 and 6 are missing**, so they contribute **0**.

---

## Employee 2

Employee **2** has records:

1, 2

The most recent month is **2**, which is excluded.

Remaining month:

1

| id  | month | calculation | salary |
| --- | ----- | ----------- | ------ |
| 2   | 1     | 20 + 0 + 0  | 20     |

---

## Employee 3

Employee **3** has records:

2, 3, 4

Most recent month **4** is excluded.

Remaining months:

2, 3

| id  | month | calculation | salary |
| --- | ----- | ----------- | ------ |
| 3   | 3     | 60 + 40 + 0 | 100    |
| 3   | 2     | 40 + 0 + 0  | 40     |

---

# Key Observations

- This is a **3‑month rolling window problem**.
- Missing months are treated as **salary = 0**.
- The **latest month per employee must be removed**.
- Sorting requirement:

```text
ORDER BY id ASC, month DESC
```

---

# Result Table Structure

| Column | Meaning                   |
| ------ | ------------------------- |
| id     | employee id               |
| month  | month being summarized    |
| salary | 3‑month cumulative salary |

---

# Summary

For each employee:

1. Identify their **latest working month**.
2. Ignore that month.
3. For every remaining month:
   - sum salary for:
     - current month
     - previous month
     - two months ago
4. Treat missing months as **0**.
5. Return rows sorted by:

```
id ASC, month DESC
```

---

This produces the **cumulative salary summary table**.
