# 607. Sales Person

## Approach: Joining Tables and Using Exclusion with `NOT IN`

## Core idea

We need to find the names of salespersons who **did not have any orders related to the company `"RED"`**.

That means the problem naturally breaks into two steps:

1. find all `sales_id` values that **did** sell to `"RED"`
2. return the names of salespersons whose `sales_id` is **not** in that set

So this is an **exclusion** problem.

A very natural SQL pattern for this is:

```sql
WHERE some_id NOT IN (subquery)
```

---

## Step 1: Find orders related to company `"RED"`

The `Orders` table contains:

- which company the order belongs to (`com_id`)
- which salesperson handled it (`sales_id`)

The `Company` table contains:

- the company name for each `com_id`

So to find sales related to `"RED"`, we join `Orders` with `Company`.

```sql
SELECT
    *
FROM
    orders o
    LEFT JOIN company c
        ON o.com_id = c.com_id
WHERE
    c.name = 'RED';
```

### Why this works

- `orders` tells us which salesperson made each sale
- `company` tells us the company name
- joining them lets us filter down to only rows where the company name is `'RED'`

---

## Result of that join on the sample

| order_id | date     | com_id | sales_id | amount | com_id | name | city   |
| -------- | -------- | ------ | -------- | ------ | ------ | ---- | ------ |
| 3        | 3/1/2014 | 1      | 1        | 50000  | 1      | RED  | Boston |
| 4        | 4/1/2014 | 1      | 4        | 25000  | 1      | RED  | Boston |

From this, we learn that the salespersons who sold to `RED` are:

- `sales_id = 1`
- `sales_id = 4`

So these are the salespeople we must exclude.

---

## Step 2: Use those `sales_id` values as a filter

Now we query the `SalesPerson` table.

This table contains:

- `sales_id`
- `name`

We want all rows whose `sales_id` is **not** among the salespeople who sold to `RED`.

That gives:

```sql
SELECT
    s.name
FROM
    salesperson s
WHERE
    s.sales_id NOT IN (
        SELECT
            o.sales_id
        FROM
            orders o
            LEFT JOIN company c
                ON o.com_id = c.com_id
        WHERE
            c.name = 'RED'
    );
```

---

## Final accepted query

```sql
SELECT
    s.name
FROM
    salesperson s
WHERE
    s.sales_id NOT IN (
        SELECT
            o.sales_id
        FROM
            orders o
            LEFT JOIN company c
                ON o.com_id = c.com_id
        WHERE
            c.name = 'RED'
    );
```

---

## Full explanation of the final query

### Outer query

```sql
SELECT s.name
FROM salesperson s
```

This starts with the full list of salespersons.

### `WHERE s.sales_id NOT IN (...)`

This keeps only salespersons whose id does **not** appear in the subquery result.

### Subquery

```sql
SELECT o.sales_id
FROM orders o
LEFT JOIN company c
    ON o.com_id = c.com_id
WHERE c.name = 'RED'
```

This returns all salesperson ids that handled orders for company `RED`.

So the outer query removes them.

---

## Walkthrough on the sample

### SalesPerson table

| sales_id | name |
| -------- | ---- |
| 1        | John |
| 2        | Amy  |
| 3        | Mark |
| 4        | Pam  |
| 5        | Alex |

### Company table

| com_id | name   |
| ------ | ------ |
| 1      | RED    |
| 2      | ORANGE |
| 3      | YELLOW |
| 4      | GREEN  |

### Orders table

| order_id | com_id | sales_id |
| -------- | ------ | -------- |
| 1        | 3      | 4        |
| 2        | 4      | 5        |
| 3        | 1      | 1        |
| 4        | 1      | 4        |

---

## First, find the salespersons who sold to RED

The company `RED` has:

```text
com_id = 1
```

Orders with `com_id = 1` are:

| order_id | com_id | sales_id |
| -------- | ------ | -------- |
| 3        | 1      | 1        |
| 4        | 1      | 4        |

So salespersons who sold to RED are:

- `1` → John
- `4` → Pam

---

## Then exclude them

From all salespersons:

- John -> exclude
- Amy -> keep
- Mark -> keep
- Pam -> exclude
- Alex -> keep

Final result:

| name |
| ---- |
| Amy  |
| Mark |
| Alex |

---

## Why `NOT IN` is the right idea here

This is a classic exclusion query:

- build the set of disallowed ids
- return everyone not in that set

That makes `NOT IN` very readable.

It expresses the logic almost exactly in plain English:

> return salespersons whose `sales_id` is not in the set of salespersons who sold to `RED`

---

## Why the join is necessary

The `Orders` table does not directly store the company name.
It stores only:

```sql
com_id
```

To know which company is `"RED"`, we must look up the `Company` table.

So the join:

```sql
o.com_id = c.com_id
```

is necessary to connect each order to its company name.

---

## About `LEFT JOIN`

The provided solution uses:

```sql
LEFT JOIN
```

That is valid and accepted.

Since the `WHERE` clause filters to:

```sql
c.name = 'RED'
```

the result behaves like an inner join for practical purposes, because only rows with a matching company named `RED` survive.

So this could also be written as:

```sql
SELECT
    s.name
FROM
    salesperson s
WHERE
    s.sales_id NOT IN (
        SELECT
            o.sales_id
        FROM
            orders o
            JOIN company c
                ON o.com_id = c.com_id
        WHERE
            c.name = 'RED'
    );
```

Both versions work here.

---

## Alternative logical phrasing

Another way to describe the same solution is:

1. find all salespersons connected to RED through orders
2. subtract them from the full salesperson list

That is exactly what the SQL is doing.

---

## Caution about `NOT IN` and `NULL`

In general SQL, `NOT IN` can behave unexpectedly if the subquery returns `NULL`.

For example, if the subquery result contains even one `NULL`, the logic can become tricky.

However, in this problem:

- `sales_id` in `Orders` is a foreign key
- the subquery is selecting actual salesperson ids from matching orders

So this approach is safe under the problem’s schema.

Still, in broader real-world SQL, it is wise to stay alert when using `NOT IN`.

---

## Alternative using `NOT EXISTS`

A more defensive alternative is:

```sql
SELECT s.name
FROM SalesPerson s
WHERE NOT EXISTS (
    SELECT 1
    FROM Orders o
    JOIN Company c
      ON o.com_id = c.com_id
    WHERE c.name = 'RED'
      AND o.sales_id = s.sales_id
);
```

This expresses the same logic and often avoids `NULL` issues in more general settings.

But your provided approach specifically uses `NOT IN`, so the main explanation keeps that exact method.

---

## Complexity

Let:

- `S` = number of rows in `SalesPerson`
- `O` = number of rows in `Orders`
- `C` = number of rows in `Company`

### Time Complexity

Conceptually, the query:

- scans/joins `Orders` and `Company` to identify RED sales
- checks each salesperson against that result

A practical interview-style summary is:

```text
O(O + C + S)
```

ignoring engine-specific optimizations.

### Space Complexity

Additional space is needed for the subquery result set of excluded `sales_id` values:

```text
O(k)
```

where `k` is the number of salespeople who sold to RED.

---

## Key takeaways

1. This is an exclusion problem.
2. First identify the salespeople who sold to company `'RED'`.
3. Then return all salespersons whose `sales_id` is not in that set.
4. `Orders` must be joined with `Company` because the company name is stored only in `Company`.
5. `NOT IN` is a natural and readable way to express this logic.

---

## Final accepted implementation

```sql
SELECT
    s.name
FROM
    salesperson s
WHERE
    s.sales_id NOT IN (
        SELECT
            o.sales_id
        FROM
            orders o
            LEFT JOIN company c
                ON o.com_id = c.com_id
        WHERE
            c.name = 'RED'
    );
```
