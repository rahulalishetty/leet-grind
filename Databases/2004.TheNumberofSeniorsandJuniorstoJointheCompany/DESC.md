# 2004. The Number of Seniors and Juniors to Join the Company

## Table: Candidates

| Column Name | Type |
| ----------- | ---- |
| employee_id | int  |
| experience  | enum |
| salary      | int  |

- `employee_id` is unique for each row.
- `experience` is an ENUM with values **'Senior'** or **'Junior'**.
- Each row represents a candidate with their **monthly salary** and **experience level**.

---

## Problem

A company wants to hire new employees with a **total salary budget of $70,000**.

The hiring rules are:

1. **Hire the largest possible number of Seniors first.**
2. After hiring the maximum number of Seniors, use the **remaining budget** to hire the **largest possible number of Juniors**.

Write a SQL query to determine how many **Senior** and **Junior** candidates can be hired under these rules.

Return the result table in **any order**.

---

## Example 1

### Input

#### Candidates

| employee_id | experience | salary |
| ----------- | ---------- | ------ |
| 1           | Junior     | 10000  |
| 9           | Junior     | 10000  |
| 2           | Senior     | 20000  |
| 11          | Senior     | 20000  |
| 13          | Senior     | 50000  |
| 4           | Junior     | 40000  |

### Output

| experience | accepted_candidates |
| ---------- | ------------------- |
| Senior     | 2                   |
| Junior     | 2                   |

### Explanation

First hire **Seniors**:

- Senior (2) → salary **20000**
- Senior (11) → salary **20000**

Total spent = **40000**

Remaining budget = **30000**

Cannot hire Senior (13) because salary **50000** exceeds the remaining budget.

Now hire **Juniors** using the remaining budget:

- Junior (1) → **10000**
- Junior (9) → **10000**

Total junior cost = **20000**

Remaining budget = **10000**, which is not enough for Junior (4) with salary **40000**.

Final result:

- Seniors hired = **2**
- Juniors hired = **2**

---

## Example 2

### Input

| employee_id | experience | salary |
| ----------- | ---------- | ------ |
| 1           | Junior     | 10000  |
| 9           | Junior     | 10000  |
| 2           | Senior     | 80000  |
| 11          | Senior     | 80000  |
| 13          | Senior     | 80000  |
| 4           | Junior     | 40000  |

### Output

| experience | accepted_candidates |
| ---------- | ------------------- |
| Senior     | 0                   |
| Junior     | 3                   |

### Explanation

No **Senior** candidate can be hired because each requires **80000**, which exceeds the budget.

So the full budget **70000** is used for **Juniors**.

Possible hires:

- Junior (1) → **10000**
- Junior (9) → **10000**
- Junior (4) → **40000**

Total = **60000**

Remaining budget = **10000**

Final result:

- Seniors hired = **0**
- Juniors hired = **3**
