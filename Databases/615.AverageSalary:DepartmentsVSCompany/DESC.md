# 615. Average Salary: Departments VS Company

## Tables

### Salary

| Column Name | Type |
| ----------- | ---- |
| id          | int  |
| employee_id | int  |
| amount      | int  |
| pay_date    | date |

- `id` is the primary key.
- Each row represents the **salary paid to an employee for a specific month**.
- `employee_id` is a foreign key referencing the **Employee** table.

---

### Employee

| Column Name   | Type |
| ------------- | ---- |
| employee_id   | int  |
| department_id | int  |

- `employee_id` is the primary key.
- Each row maps an employee to a **department**.

---

## Problem

Find the **comparison result** between:

- the **average salary of employees in a department**
- the **average salary of the entire company**

The comparison must be calculated **for each month**.

### Comparison values

The result must be:

- **higher** → department average > company average
- **lower** → department average < company average
- **same** → department average = company average

Return the result table in **any order**.

---

## Example

### Input

#### Salary

| id  | employee_id | amount | pay_date   |
| --- | ----------- | ------ | ---------- |
| 1   | 1           | 9000   | 2017/03/31 |
| 2   | 2           | 6000   | 2017/03/31 |
| 3   | 3           | 10000  | 2017/03/31 |
| 4   | 1           | 7000   | 2017/02/28 |
| 5   | 2           | 6000   | 2017/02/28 |
| 6   | 3           | 8000   | 2017/02/28 |

#### Employee

| employee_id | department_id |
| ----------- | ------------- |
| 1           | 1             |
| 2           | 2             |
| 3           | 2             |

---

## Output

| pay_month | department_id | comparison |
| --------- | ------------- | ---------- |
| 2017-02   | 1             | same       |
| 2017-03   | 1             | higher     |
| 2017-02   | 2             | same       |
| 2017-03   | 2             | lower      |

---

## Explanation

### March (2017‑03)

Company average salary:

```
(9000 + 6000 + 10000) / 3
= 8333.33
```

Department 1 average:

```
9000
```

Comparison:

```
9000 > 8333.33 → higher
```

Department 2 average:

```
(6000 + 10000) / 2
= 8000
```

Comparison:

```
8000 < 8333.33 → lower
```

---

### February (2017‑02)

Company average salary:

```
(7000 + 6000 + 8000) / 3
= 7000
```

Department 1 average:

```
7000
```

Department 2 average:

```
(6000 + 8000) / 2 = 7000
```

Both departments match the company average.

Comparison result:

```
same
```
