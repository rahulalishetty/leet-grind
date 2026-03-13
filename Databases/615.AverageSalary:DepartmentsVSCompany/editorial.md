# 615. Average Salary: Departments VS Company

## Approach: Using `AVG()` and `CASE ... WHEN ...`

## Core idea

We need to compare, for each **department** and each **month**:

- the **department's average salary**
- the **company's average salary**

Then we must label the result as:

- `higher` if department average > company average
- `lower` if department average < company average
- `same` if they are equal

So the solution naturally breaks into **three steps**:

1. calculate the company-wide monthly average salary
2. calculate each department's monthly average salary
3. compare those two averages and output the result

---

## Why this problem should be solved in stages

A skeptical way to view the problem is:

- the company average is not per employee only; it is per **month**
- the department average is also per **month**
- so we cannot compare raw salaries directly
- we must compare **aggregated monthly averages**

That means grouping by month is essential.

---

# Step 1: Calculate the company average salary for every month

MySQL provides the built-in aggregate function:

```sql
AVG(amount)
```

to compute averages.

We also need to normalize `pay_date` into a month format like:

```text
2017-02
2017-03
```

For that, we use:

```sql
DATE_FORMAT(pay_date, '%Y-%m')
```

So the company monthly average query is:

```sql
SELECT
  AVG(amount) AS company_avg,
  DATE_FORMAT(pay_date, '%Y-%m') AS pay_month
FROM
  salary
GROUP BY
  DATE_FORMAT(pay_date, '%Y-%m');
```

---

## Why this works

- `AVG(amount)` computes the average salary for all salary rows in the same month
- `DATE_FORMAT(pay_date, '%Y-%m')` extracts the month part
- `GROUP BY DATE_FORMAT(pay_date, '%Y-%m')` groups all salary rows by month

So the result is one row per month containing the company average salary.

---

## Output of Step 1 on the sample

| company_avg | pay_month |
| ----------- | --------- |
| 7000.0000   | 2017-02   |
| 8333.3333   | 2017-03   |

This means:

- in February 2017, company average salary is `7000`
- in March 2017, company average salary is `8333.3333`

---

# Step 2: Calculate each department's average salary for every month

To compute department averages, we need to know which department each employee belongs to.

That information is not in the `Salary` table directly.
It is in the `Employee` table.

So we join:

```sql
salary.employee_id = employee.employee_id
```

Then we group by:

- department
- month

Query:

```sql
SELECT
  department_id,
  AVG(amount) AS department_avg,
  DATE_FORMAT(pay_date, '%Y-%m') AS pay_month
FROM
  salary
  JOIN employee ON salary.employee_id = employee.employee_id
GROUP BY
  department_id,
  pay_month;
```

---

## Why this works

- the join connects each salary row to its department
- `AVG(amount)` computes the average salary for that department in that month
- grouping by both `department_id` and `pay_month` ensures we get one row per department per month

---

## Output of Step 2 on the sample

| department_id | department_avg | pay_month |
| ------------- | -------------- | --------- |
| 1             | 7000.0000      | 2017-02   |
| 1             | 9000.0000      | 2017-03   |
| 2             | 7000.0000      | 2017-02   |
| 2             | 8000.0000      | 2017-03   |

This tells us:

- Department 1:
  - February average = `7000`
  - March average = `9000`

- Department 2:
  - February average = `7000`
  - March average = `8000`

---

# Step 3: Compare department average with company average

Now we have two derived result sets:

1. `company_salary` -> company monthly average
2. `department_salary` -> department monthly average

We need to compare them month by month.

So we join the two derived tables on:

```sql
department_salary.pay_month = company_salary.pay_month
```

That ensures each department's monthly average is compared against the company's average for the same month.

Then we use `CASE ... WHEN ...` to generate:

- `higher`
- `lower`
- `same`

---

## Final accepted query

```sql
SELECT
  department_salary.pay_month,
  department_id,
  CASE
    WHEN department_avg > company_avg THEN 'higher'
    WHEN department_avg < company_avg THEN 'lower'
    ELSE 'same'
  END AS comparison
FROM
  (
    SELECT
      department_id,
      AVG(amount) AS department_avg,
      DATE_FORMAT(pay_date, '%Y-%m') AS pay_month
    FROM
      salary
      JOIN employee ON salary.employee_id = employee.employee_id
    GROUP BY
      department_id,
      pay_month
  ) AS department_salary
  JOIN (
    SELECT
      AVG(amount) AS company_avg,
      DATE_FORMAT(pay_date, '%Y-%m') AS pay_month
    FROM
      salary
    GROUP BY
      DATE_FORMAT(pay_date, '%Y-%m')
  ) AS company_salary
    ON department_salary.pay_month = company_salary.pay_month;
```

---

# Step-by-step explanation of the final query

## Inner subquery 1: department averages

```sql
SELECT
  department_id,
  AVG(amount) AS department_avg,
  DATE_FORMAT(pay_date, '%Y-%m') AS pay_month
FROM
  salary
  JOIN employee ON salary.employee_id = employee.employee_id
GROUP BY
  department_id,
  pay_month
```

This creates a monthly summary for each department.

It tells us:

- which department
- which month
- what that department's average salary was in that month

---

## Inner subquery 2: company averages

```sql
SELECT
  AVG(amount) AS company_avg,
  DATE_FORMAT(pay_date, '%Y-%m') AS pay_month
FROM
  salary
GROUP BY
  DATE_FORMAT(pay_date, '%Y-%m')
```

This creates a company-wide monthly summary.

It tells us:

- which month
- what the company average salary was in that month

---

## Join between the two summaries

```sql
ON department_salary.pay_month = company_salary.pay_month
```

This aligns the department average with the company average for the same month.

Without this join, we would not know which company monthly average to compare against.

---

## `CASE ... WHEN ...` comparison logic

```sql
CASE
  WHEN department_avg > company_avg THEN 'higher'
  WHEN department_avg < company_avg THEN 'lower'
  ELSE 'same'
END AS comparison
```

This is the final classification step.

It directly translates the problem statement into SQL:

- if department average is larger -> `higher`
- if smaller -> `lower`
- otherwise -> `same`

---

# Full walkthrough on the sample

## Sample data

### Salary

| id  | employee_id | amount | pay_date   |
| --- | ----------- | ------ | ---------- |
| 1   | 1           | 9000   | 2017/03/31 |
| 2   | 2           | 6000   | 2017/03/31 |
| 3   | 3           | 10000  | 2017/03/31 |
| 4   | 1           | 7000   | 2017/02/28 |
| 5   | 2           | 6000   | 2017/02/28 |
| 6   | 3           | 8000   | 2017/02/28 |

### Employee

| employee_id | department_id |
| ----------- | ------------- |
| 1           | 1             |
| 2           | 2             |
| 3           | 2             |

---

## February 2017

### Company average

```text
(7000 + 6000 + 8000) / 3 = 7000
```

### Department 1 average

Only employee `1` is in department `1`:

```text
7000
```

Comparison:

```text
7000 = 7000 -> same
```

### Department 2 average

Employees `2` and `3` are in department `2`:

```text
(6000 + 8000) / 2 = 7000
```

Comparison:

```text
7000 = 7000 -> same
```

---

## March 2017

### Company average

```text
(9000 + 6000 + 10000) / 3 = 8333.33...
```

### Department 1 average

Only employee `1` is in department `1`:

```text
9000
```

Comparison:

```text
9000 > 8333.33 -> higher
```

### Department 2 average

Employees `2` and `3`:

```text
(6000 + 10000) / 2 = 8000
```

Comparison:

```text
8000 < 8333.33 -> lower
```

---

## Final output

| pay_month | department_id | comparison |
| --------- | ------------- | ---------- |
| 2017-02   | 1             | same       |
| 2017-03   | 1             | higher     |
| 2017-02   | 2             | same       |
| 2017-03   | 2             | lower      |

---

# Why `DATE_FORMAT()` is important

A careful point here: the table stores full dates like:

```text
2017/03/31
2017/02/28
```

But the comparison is required **by month**, not by full day.

So we must convert dates into a monthly key.

That is exactly why we use:

```sql
DATE_FORMAT(pay_date, '%Y-%m')
```

Without that, grouping would happen day by day, which would be wrong.

---

# Why `AVG()` is the correct aggregation

The problem is explicitly asking for average salary comparisons.

So using:

```sql
AVG(amount)
```

is essential.

Other aggregates like `SUM()` or `COUNT()` would answer different questions and would not solve the problem.

---

# Why `CASE` is the right control structure

This is a classic three-way comparison problem.

We need one of three labels depending on relative values.

That maps cleanly to:

```sql
CASE
  WHEN ...
  WHEN ...
  ELSE ...
END
```

This is much clearer than trying to force the logic through numeric flags or nested conditions.

---

# Common mistake to avoid

A common wrong idea would be to compare raw employee salaries directly against a company average.

That would be incorrect, because the problem wants:

- department **average**
- compared to company **average**

So aggregation must happen **before** the comparison.

---

# Alternative formatting note

Some SQL engines are stricter about using aliases like `pay_month` directly inside `GROUP BY`.

A more explicit version would repeat the full expression:

```sql
GROUP BY
  department_id,
  DATE_FORMAT(pay_date, '%Y-%m')
```

That is logically equivalent.

The provided solution uses the alias in the grouping step, which is accepted in MySQL-style SQL.

---

# Complexity

Let:

- `n` = number of rows in `Salary`
- `m` = number of rows in `Employee`

## Time Complexity

The query:

- scans salary rows for company aggregation
- joins salary with employee for department aggregation
- groups results by month and department
- joins the two summaries

A practical interview-style summary is:

```text
O(n + m)
```

plus grouping overhead, depending on the SQL engine.

## Space Complexity

Additional space is needed for the grouped monthly summaries:

- company monthly averages
- department monthly averages

So the extra space is proportional to the number of generated groups.

---

# Key takeaways

1. This problem must be solved month by month.
2. First compute the company monthly average salary.
3. Then compute each department's monthly average salary.
4. Join those two summaries on month.
5. Use `CASE ... WHEN ...` to label the comparison as `higher`, `lower`, or `same`.

---

## Final accepted implementation

```sql
SELECT
  department_salary.pay_month,
  department_id,
  CASE
    WHEN department_avg > company_avg THEN 'higher'
    WHEN department_avg < company_avg THEN 'lower'
    ELSE 'same'
  END AS comparison
FROM
  (
    SELECT
      department_id,
      AVG(amount) AS department_avg,
      DATE_FORMAT(pay_date, '%Y-%m') AS pay_month
    FROM
      salary
      JOIN employee ON salary.employee_id = employee.employee_id
    GROUP BY
      department_id,
      pay_month
  ) AS department_salary
  JOIN (
    SELECT
      AVG(amount) AS company_avg,
      DATE_FORMAT(pay_date, '%Y-%m') AS pay_month
    FROM
      salary
    GROUP BY
      DATE_FORMAT(pay_date, '%Y-%m')
  ) AS company_salary
    ON department_salary.pay_month = company_salary.pay_month;
```
