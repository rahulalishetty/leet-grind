# 2004. The Number of Seniors and Juniors to Join the Company

## Approach: Tiered Cumulative Salary Allocation Strategy

## Core idea

The company has a fixed total budget of:

```text
70000
```

and must hire under these strict priority rules:

1. hire the **largest possible number of Seniors first**
2. then, using the **remaining budget**, hire the **largest possible number of Juniors**

So this is a **two-tier hiring process**:

- Tier 1: Seniors
- Tier 2: Juniors

A clean way to solve this is:

1. sort each experience group by salary ascending
2. compute cumulative salary in that sorted order
3. count how many seniors fit into the full budget
4. compute the remaining budget after those seniors
5. count how many juniors fit into the remaining budget
6. return both counts

This approach uses:

- **CTEs**
- **window functions**
- **`COALESCE`**
- **`UNION`**

---

## Why sorting by salary ascending is the right strategy

If the goal is to hire the maximum number of candidates within a fixed budget, the right greedy order is:

```text
lowest salary first
```

Hiring more expensive candidates first can reduce the total number of hires.

So for both Seniors and Juniors, the maximum-count strategy is:

```text
sort by salary ascending
```

Then keep taking candidates while the running total stays within budget.

That is why the solution uses cumulative salary ordered by `salary`.

---

# Step 1: Build the Senior candidate list with cumulative salary

```sql
WITH SeniorCandidates AS (
  SELECT
    *,
    SUM(salary) OVER (
      ORDER BY salary
    ) AS cumulative_salary
  FROM
    Candidates
  WHERE
    experience = 'Senior'
)
```

## Why this works

This:

1. filters candidates to only Seniors
2. computes a running total of salary after sorting by salary ascending

If senior salaries are:

```text
20000, 20000, 50000
```

then cumulative salaries become:

```text
20000
40000
90000
```

This makes it easy to see how many seniors fit within the budget of `70000`.

---

# Step 2: Count how many Seniors can be hired

```sql
HiredSeniors AS (
  SELECT
    COUNT(*) AS count
  FROM
    SeniorCandidates
  WHERE
    cumulative_salary <= 70000
)
```

Once cumulative salary is computed, the seniors we can hire are exactly those rows whose cumulative salary does not exceed the budget.

Counting such rows gives the number of accepted seniors.

---

# Step 3: Compute the remaining budget after hiring Seniors

```sql
RemainingBudget AS (
  SELECT
    70000 - COALESCE(
      (
        SELECT
          cumulative_salary
        FROM
          SeniorCandidates
        WHERE
          cumulative_salary <= 70000
        ORDER BY
          cumulative_salary DESC
        LIMIT 1
      ), 0
    ) AS budget
)
```

## Why this works

We want the **largest cumulative senior salary that is still within budget**.

That corresponds to the actual total salary of the hired senior batch.

Then the remaining budget is:

```text
70000 - senior_spent
```

### Why `COALESCE(..., 0)` is necessary

There may be a case where **no seniors can be hired**.

If every senior salary is greater than `70000`, the inner subquery returns nothing.

Then the senior spending should be treated as `0`, so the remaining budget stays `70000`.

---

# Step 4: Build the Junior candidate list with cumulative salary

```sql
JuniorCandidates AS (
  SELECT
    *,
    SUM(salary) OVER (
      ORDER BY salary
    ) AS cumulative_salary
  FROM
    Candidates
  WHERE
    experience = 'Junior'
)
```

This repeats the same maximum-count logic for Juniors.

---

# Step 5: Count how many Juniors fit into the remaining budget

```sql
HiredJuniors AS (
  SELECT
    COUNT(*) AS count
  FROM
    JuniorCandidates,
    RemainingBudget
  WHERE
    JuniorCandidates.cumulative_salary <= RemainingBudget.budget
)
```

Every junior row whose cumulative salary is within the remaining budget belongs to the chosen junior batch.

Counting those rows gives the number of accepted juniors.

---

# Step 6: Return the final result

```sql
SELECT
  'Senior' AS experience,
  (
    SELECT
      count
    FROM
      HiredSeniors
  ) AS accepted_candidates
UNION
SELECT
  'Junior' AS experience,
  (
    SELECT
      count
    FROM
      HiredJuniors
  ) AS accepted_candidates;
```

This returns two rows:

- one for `Senior`
- one for `Junior`

---

## Final accepted query

```sql
WITH SeniorCandidates AS (
  SELECT
    *,
    SUM(salary) OVER (
      ORDER BY
        salary
    ) AS cumulative_salary
  FROM
    Candidates
  WHERE
    experience = 'Senior'
),
HiredSeniors AS (
  SELECT
    COUNT(*) AS count
  FROM
    SeniorCandidates
  WHERE
    cumulative_salary <= 70000
),
RemainingBudget AS (
  SELECT
    70000 - COALESCE(
      (
        SELECT
          cumulative_salary
        FROM
          SeniorCandidates
        WHERE
          cumulative_salary <= 70000
        ORDER BY
          cumulative_salary DESC
        LIMIT
          1
      ), 0
    ) AS budget
),
JuniorCandidates AS (
  SELECT
    *,
    SUM(salary) OVER (
      ORDER BY
        salary
    ) AS cumulative_salary
  FROM
    Candidates
  WHERE
    experience = 'Junior'
),
HiredJuniors AS (
  SELECT
    COUNT(*) AS count
  FROM
    JuniorCandidates,
    RemainingBudget
  WHERE
    JuniorCandidates.cumulative_salary <= RemainingBudget.budget
)
SELECT
  'Senior' AS experience,
  (
    SELECT
      count
    FROM
      HiredSeniors
  ) AS accepted_candidates
UNION
SELECT
  'Junior' AS experience,
  (
    SELECT
      count
    FROM
      HiredJuniors
  ) AS accepted_candidates;
```

---

# Walkthrough on Example 1

## Input

| employee_id | experience | salary |
| ----------: | ---------- | -----: |
|           1 | Junior     |  10000 |
|           9 | Junior     |  10000 |
|           2 | Senior     |  20000 |
|          11 | Senior     |  20000 |
|          13 | Senior     |  50000 |
|           4 | Junior     |  40000 |

## Seniors first

Sorted senior salaries:

```text
20000, 20000, 50000
```

Cumulative:

```text
20000, 40000, 90000
```

Affordable within `70000`:

- first two only

So:

```text
Senior accepted = 2
```

Remaining budget:

```text
70000 - 40000 = 30000
```

## Juniors next

Sorted junior salaries:

```text
10000, 10000, 40000
```

Cumulative:

```text
10000, 20000, 60000
```

Affordable within `30000`:

- first two only

So:

```text
Junior accepted = 2
```

Final result:

| experience | accepted_candidates |
| ---------- | ------------------- |
| Senior     | 2                   |
| Junior     | 2                   |

---

# Walkthrough on Example 2

## Input

| employee_id | experience | salary |
| ----------: | ---------- | -----: |
|           1 | Junior     |  10000 |
|           9 | Junior     |  10000 |
|           2 | Senior     |  80000 |
|          11 | Senior     |  80000 |
|          13 | Senior     |  80000 |
|           4 | Junior     |  40000 |

## Seniors first

Sorted senior salaries:

```text
80000, 80000, 80000
```

Cumulative:

```text
80000, 160000, 240000
```

None are within `70000`.

So:

```text
Senior accepted = 0
```

Remaining budget:

```text
70000
```

## Juniors next

Sorted junior salaries:

```text
10000, 10000, 40000
```

Cumulative:

```text
10000, 20000, 60000
```

All three fit within `70000`.

So:

```text
Junior accepted = 3
```

Final result:

| experience | accepted_candidates |
| ---------- | ------------------- |
| Senior     | 0                   |
| Junior     | 3                   |

---

# Why cumulative salary is the key pattern

When candidates are sorted by salary ascending, cumulative salary tells us:

> if we hire the cheapest `k` candidates in this group, how much would we spend?

Then the maximum affordable `k` is exactly the count of rows where cumulative salary stays within budget.

This is a strong SQL pattern for greedy budget-allocation problems.

---

# Important SQL concepts used here

## 1. Window function with running sum

```sql
SUM(salary) OVER (ORDER BY salary)
```

## 2. CTEs

Used to break the problem into stages:

- senior candidate list
- hired seniors
- remaining budget
- junior candidate list
- hired juniors

## 3. `COALESCE`

Used to handle the “no seniors hired” case safely.

## 4. `UNION`

Used to return the final two result rows.

---

# Subtle correction to the source explanation

The source text says seniors and juniors are “ranked in descending order of salary,” but the actual SQL is:

```sql
ORDER BY salary
```

which is ascending order, not descending.

The SQL implementation is correct.
Ascending order is the right greedy choice for maximizing hires.

---

# Complexity

Let:

- `S` = number of senior candidates
- `J` = number of junior candidates

## Time Complexity

The main work is sorting seniors and juniors for cumulative sums:

```text
O(S log S + J log J)
```

## Space Complexity

Additional space is used for the intermediate CTEs, proportional to the number of candidates.

---

# Key takeaways

1. To maximize number of hires under a budget, sort salaries ascending.
2. Use cumulative salary to determine how many candidates fit.
3. Apply the hiring rule in two stages:
   - Seniors first
   - Juniors with the leftover budget
4. Use `COALESCE` to handle the case where no seniors can be hired.
5. This is a clean example of greedy selection implemented through SQL window functions.

---

## Final accepted implementation

```sql
WITH SeniorCandidates AS (
  SELECT
    *,
    SUM(salary) OVER (
      ORDER BY
        salary
    ) AS cumulative_salary
  FROM
    Candidates
  WHERE
    experience = 'Senior'
),
HiredSeniors AS (
  SELECT
    COUNT(*) AS count
  FROM
    SeniorCandidates
  WHERE
    cumulative_salary <= 70000
),
RemainingBudget AS (
  SELECT
    70000 - COALESCE(
      (
        SELECT
          cumulative_salary
        FROM
          SeniorCandidates
        WHERE
          cumulative_salary <= 70000
        ORDER BY
          cumulative_salary DESC
        LIMIT
          1
      ), 0
    ) AS budget
),
JuniorCandidates AS (
  SELECT
    *,
    SUM(salary) OVER (
      ORDER BY
        salary
    ) AS cumulative_salary
  FROM
    Candidates
  WHERE
    experience = 'Junior'
),
HiredJuniors AS (
  SELECT
    COUNT(*) AS count
  FROM
    JuniorCandidates,
    RemainingBudget
  WHERE
    JuniorCandidates.cumulative_salary <= RemainingBudget.budget
)
SELECT
  'Senior' AS experience,
  (
    SELECT
      count
    FROM
      HiredSeniors
  ) AS accepted_candidates
UNION
SELECT
  'Junior' AS experience,
  (
    SELECT
      count
    FROM
      HiredJuniors
  ) AS accepted_candidates;
```
