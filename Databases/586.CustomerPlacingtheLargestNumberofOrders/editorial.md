# 586. Customer Placing the Largest Number of Orders

## Approach: `GROUP BY`

## Core idea

We need to find the `customer_number` of the customer who placed the **largest number of orders**.

Since each row in the `Orders` table represents one order, the problem reduces to:

1. group rows by `customer_number`
2. count how many orders each customer placed
3. sort those counts in descending order
4. return the customer at the top

Because the problem guarantees that **exactly one customer** has more orders than any other customer, returning the first row is enough.

---

## Step 1: Count orders per customer

We first group the table by `customer_number` and count how many rows belong to each customer.

```sql
SELECT
    customer_number,
    COUNT(*)
FROM orders
GROUP BY customer_number;
```

### Why this works

- each row in `Orders` is one order
- `GROUP BY customer_number` puts all orders of the same customer into one group
- `COUNT(*)` gives the number of orders in that group

---

## Example

Given:

| order_number | customer_number |
| ------------ | --------------- |
| 1            | 1               |
| 2            | 2               |
| 3            | 3               |
| 4            | 3               |

Running:

```sql
SELECT
    customer_number,
    COUNT(*)
FROM orders
GROUP BY customer_number;
```

produces:

| customer_number | COUNT(\*) |
| --------------- | --------- |
| 1               | 1         |
| 2               | 1         |
| 3               | 2         |

This tells us:

- customer `1` placed `1` order
- customer `2` placed `1` order
- customer `3` placed `2` orders

---

## Step 2: Sort by order count descending

Now that we know the order count for each customer, we want the customer with the **largest** count.

So we sort by:

```sql
ORDER BY COUNT(*) DESC
```

This puts the customer with the highest number of orders first.

Using the previous grouped result, sorting descending gives:

| customer_number | COUNT(\*) |
| --------------- | --------- |
| 3               | 2         |
| 1               | 1         |
| 2               | 1         |

Now the answer is clearly the first row.

---

## Step 3: Return only the first row

Since exactly one customer has the largest number of orders, we only need the top row.

That is what `LIMIT 1` does.

```sql
LIMIT 1
```

---

## Final query

```sql
SELECT
    customer_number
FROM orders
GROUP BY customer_number
ORDER BY COUNT(*) DESC
LIMIT 1;
```

---

## Full explanation of the final query

### `SELECT customer_number`

We only need to return the customer's id, not the count.

### `FROM orders`

Use the `Orders` table since it contains all order records.

### `GROUP BY customer_number`

Collect all rows belonging to the same customer into one group.

### `ORDER BY COUNT(*) DESC`

Sort customers by number of orders, highest first.

### `LIMIT 1`

Return only the first row, which is the customer with the maximum number of orders.

---

## Why `COUNT(*)` is correct here

In this table:

- every row represents one order
- `order_number` is the primary key
- there are no special null-handling issues involved in counting rows

So:

```sql
COUNT(*)
```

is exactly what we want, because it counts the total number of orders for each customer.

---

## Walkthrough on the sample

Input:

| order_number | customer_number |
| ------------ | --------------- |
| 1            | 1               |
| 2            | 2               |
| 3            | 3               |
| 4            | 3               |

### Grouping result

```sql
SELECT
    customer_number,
    COUNT(*)
FROM orders
GROUP BY customer_number;
```

becomes:

| customer_number | COUNT(\*) |
| --------------- | --------- |
| 1               | 1         |
| 2               | 1         |
| 3               | 2         |

### After sorting

```sql
ORDER BY COUNT(*) DESC
```

becomes:

| customer_number | COUNT(\*) |
| --------------- | --------- |
| 3               | 2         |
| 1               | 1         |
| 2               | 1         |

### After limiting

```sql
LIMIT 1
```

result:

| customer_number |
| --------------- |
| 3               |

That is the final answer.

---

## Important SQL concept: `LIMIT`

In MySQL, `LIMIT` restricts how many rows are returned.

### Example

```sql
SELECT *
FROM orders
LIMIT 1;
```

This returns the first row only.

### General behavior

- `LIMIT 1` -> return 1 row
- `LIMIT 5` -> return 5 rows
- `LIMIT offset, count` -> skip `offset` rows, then return `count` rows

For this problem, we use:

```sql
LIMIT 1
```

because only the top customer is needed.

---

## Why this solution is efficient

This is a compact aggregation query:

- one grouping step
- one sorting step
- one final row returned

It directly matches the problem.

---

## Follow-up: what if more than one customer ties for the largest number of orders?

The original problem guarantees a unique answer, so `LIMIT 1` is enough.

But in the follow-up, multiple customers may have the same maximum order count.

In that case, we cannot simply use `LIMIT 1`, because that would return only one of them.

We need to:

1. count orders per customer
2. find the maximum order count
3. return all customers whose count equals that maximum

One possible solution is:

```sql
SELECT customer_number
FROM Orders
GROUP BY customer_number
HAVING COUNT(*) = (
    SELECT COUNT(*)
    FROM Orders
    GROUP BY customer_number
    ORDER BY COUNT(*) DESC
    LIMIT 1
);
```

This returns all customers whose order count matches the highest count.

---

## Alternative readable version using a subquery

```sql
SELECT customer_number
FROM (
    SELECT customer_number, COUNT(*) AS order_count
    FROM Orders
    GROUP BY customer_number
) t
WHERE order_count = (
    SELECT MAX(order_count)
    FROM (
        SELECT COUNT(*) AS order_count
        FROM Orders
        GROUP BY customer_number
    ) x
);
```

This is more verbose, but often easier to understand in steps.

---

## Complexity

Let `n` be the number of rows in `Orders`.

### Time Complexity

- grouping the rows: `O(n)`
- sorting grouped customers: depends on number of distinct customers

A practical interview-level answer is:

```text
O(n log n)
```

in the general case due to sorting.

### Space Complexity

```text
O(k)
```

where `k` is the number of distinct customers, because grouping creates one aggregated result per customer.

---

## Final accepted implementation

```sql
SELECT
    customer_number
FROM
    orders
GROUP BY customer_number
ORDER BY COUNT(*) DESC
LIMIT 1;
```

---

## Key takeaways

1. Each row is one order, so counting rows per customer gives order totals.
2. `GROUP BY customer_number` groups orders customer-wise.
3. `COUNT(*)` gives the number of orders in each group.
4. `ORDER BY COUNT(*) DESC` puts the customer with the most orders first.
5. `LIMIT 1` returns the unique answer guaranteed by the problem.
