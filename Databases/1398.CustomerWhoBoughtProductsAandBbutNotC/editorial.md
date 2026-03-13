# 1398. Customers Who Bought Products A and B but Not C — Detailed Summary

## Approach 1: `GROUP BY` then Use `HAVING`

This approach works by grouping all orders by customer and then filtering those customer groups based on product-purchase conditions.

We want customers who satisfy all three rules:

- bought product **A**
- bought product **B**
- did **not** buy product **C**

So after grouping by customer, we use `HAVING` to keep only those customer groups that meet those conditions.

---

## Core Idea

Each customer can have many orders.

If we group rows by `customer_id`, then each group contains all products purchased by that customer.

Once that grouping is formed, we can ask questions like:

- did this customer buy A at least once?
- did this customer buy B at least once?
- did this customer buy C zero times?

That is exactly what the `HAVING` clause checks.

---

## Query

```sql
SELECT c.customer_id, customer_name
FROM customers c
LEFT JOIN orders o ON c.customer_id = o.customer_id
GROUP BY c.customer_id
HAVING SUM(product_name = 'A') > 0
   AND SUM(product_name = 'B') > 0
   AND SUM(product_name = 'C') = 0
ORDER BY c.customer_id;
```

---

# Step-by-Step Explanation

## 1. Join `Customers` and `Orders`

```sql
FROM customers c
LEFT JOIN orders o ON c.customer_id = o.customer_id
```

### What this does

This connects each customer with their orders.

So instead of having customer data and order data separately, we get a combined table where each customer is matched with the products they bought.

For example, customer `3` (Elizabeth) has these orders:

| customer_id | customer_name | order_id | product_name |
| ----------: | ------------- | -------: | ------------ |
|           3 | Elizabeth     |       60 | A            |
|           3 | Elizabeth     |       70 | B            |
|           3 | Elizabeth     |       80 | D            |

This is the raw material we need before grouping.

---

## Why `LEFT JOIN` Is Used

A `LEFT JOIN` ensures that all customers remain present even if they have no orders.

In this particular problem, such customers will not pass the `HAVING` conditions anyway, because they did not buy A or B.

So `INNER JOIN` would also work logically for the final result.

Still, `LEFT JOIN` is safe and keeps the structure broad.

---

## 2. Group by customer

```sql
GROUP BY c.customer_id
```

This creates one group per customer.

Each group contains all rows corresponding to that customer's purchases.

Conceptually:

### Customer 1 group

| product_name |
| ------------ |
| A            |
| B            |
| D            |
| C            |

### Customer 2 group

| product_name |
| ------------ |
| A            |

### Customer 3 group

| product_name |
| ------------ |
| A            |
| B            |
| D            |

### Customer 4 group

| product_name |
| ------------ |
| C            |

Now we can compute aggregate conditions on each group.

---

## 3. Use `HAVING` to filter customer groups

```sql
HAVING SUM(product_name = 'A') > 0
   AND SUM(product_name = 'B') > 0
   AND SUM(product_name = 'C') = 0
```

This is the key part of the solution.

---

# Why `SUM(product_name = 'A')` Works

In MySQL and some SQL dialects, a boolean expression like:

```sql
product_name = 'A'
```

evaluates to:

- `1` when true
- `0` when false

So:

```sql
SUM(product_name = 'A')
```

counts how many rows in the group have product `A`.

Similarly:

```sql
SUM(product_name = 'B')
```

counts the number of B purchases.

And:

```sql
SUM(product_name = 'C')
```

counts the number of C purchases.

---

## Condition 1: Bought A

```sql
SUM(product_name = 'A') > 0
```

This means the customer bought A at least once.

---

## Condition 2: Bought B

```sql
SUM(product_name = 'B') > 0
```

This means the customer bought B at least once.

---

## Condition 3: Did not buy C

```sql
SUM(product_name = 'C') = 0
```

This means the customer never bought C.

---

# Why `SUM` Instead of `COUNT`

This is an important point.

If we wrote something like:

```sql
COUNT(product_name = 'A')
```

that would not do what we want.

Why?

Because `COUNT(...)` counts non-null values, not true values.

The expression:

```sql
product_name = 'A'
```

returns either `1` or `0`, and both are non-null.

So `COUNT(product_name = 'A')` would count **all rows**, not just rows where product is A.

That would be wrong.

But `SUM(product_name = 'A')` works because only true rows contribute `1`, while false rows contribute `0`.

So `SUM` is the correct aggregation here.

---

# Example Walkthrough

## Input

### Customers

| customer_id | customer_name |
| ----------: | ------------- |
|           1 | Daniel        |
|           2 | Diana         |
|           3 | Elizabeth     |
|           4 | Jhon          |

### Orders

| order_id | customer_id | product_name |
| -------: | ----------: | ------------ |
|       10 |           1 | A            |
|       20 |           1 | B            |
|       30 |           1 | D            |
|       40 |           1 | C            |
|       50 |           2 | A            |
|       60 |           3 | A            |
|       70 |           3 | B            |
|       80 |           3 | D            |
|       90 |           4 | C            |

---

## Customer 1: Daniel

Products bought:

- A
- B
- D
- C

Check conditions:

- bought A? yes
- bought B? yes
- bought C? yes

Since product C was bought, Daniel must be excluded.

Aggregate view:

```text
SUM(product_name = 'A') = 1
SUM(product_name = 'B') = 1
SUM(product_name = 'C') = 1
```

Fails because C is not zero.

---

## Customer 2: Diana

Products bought:

- A

Check conditions:

- bought A? yes
- bought B? no
- bought C? no

Aggregate view:

```text
SUM(product_name = 'A') = 1
SUM(product_name = 'B') = 0
SUM(product_name = 'C') = 0
```

Fails because B is not greater than zero.

---

## Customer 3: Elizabeth

Products bought:

- A
- B
- D

Check conditions:

- bought A? yes
- bought B? yes
- bought C? no

Aggregate view:

```text
SUM(product_name = 'A') = 1
SUM(product_name = 'B') = 1
SUM(product_name = 'C') = 0
```

Passes all conditions.

So Elizabeth qualifies.

---

## Customer 4: Jhon

Products bought:

- C

Check conditions:

- bought A? no
- bought B? no
- bought C? yes

Aggregate view:

```text
SUM(product_name = 'A') = 0
SUM(product_name = 'B') = 0
SUM(product_name = 'C') = 1
```

Fails all relevant conditions.

---

# Final Output

| customer_id | customer_name |
| ----------: | ------------- |
|           3 | Elizabeth     |

---

# Clause-by-Clause Breakdown

## `SELECT`

```sql
SELECT c.customer_id, customer_name
```

Returns the customer's ID and name.

---

## `FROM ... LEFT JOIN`

```sql
FROM customers c
LEFT JOIN orders o ON c.customer_id = o.customer_id
```

Combines customers with their purchase rows.

---

## `GROUP BY`

```sql
GROUP BY c.customer_id
```

Creates one group per customer.

---

## `HAVING`

```sql
HAVING SUM(product_name = 'A') > 0
   AND SUM(product_name = 'B') > 0
   AND SUM(product_name = 'C') = 0
```

Filters grouped customers based on the required purchase logic.

---

## `ORDER BY`

```sql
ORDER BY c.customer_id
```

Sorts the final result by customer ID.

---

# Important Portability Note

The expression:

```sql
SUM(product_name = 'A')
```

works in MySQL because boolean expressions behave like `1` and `0`.

But not all SQL engines support this style directly.

A more portable version is:

```sql
SUM(CASE WHEN product_name = 'A' THEN 1 ELSE 0 END)
```

Similarly for B and C.

So a portable version would be:

```sql
SELECT c.customer_id, c.customer_name
FROM Customers c
LEFT JOIN Orders o
  ON c.customer_id = o.customer_id
GROUP BY c.customer_id, c.customer_name
HAVING SUM(CASE WHEN product_name = 'A' THEN 1 ELSE 0 END) > 0
   AND SUM(CASE WHEN product_name = 'B' THEN 1 ELSE 0 END) > 0
   AND SUM(CASE WHEN product_name = 'C' THEN 1 ELSE 0 END) = 0
ORDER BY c.customer_id;
```

This version is safer across different database systems.

---

# Recommended Final Version

```sql
SELECT c.customer_id, c.customer_name
FROM Customers c
LEFT JOIN Orders o
  ON c.customer_id = o.customer_id
GROUP BY c.customer_id, c.customer_name
HAVING SUM(CASE WHEN product_name = 'A' THEN 1 ELSE 0 END) > 0
   AND SUM(CASE WHEN product_name = 'B' THEN 1 ELSE 0 END) > 0
   AND SUM(CASE WHEN product_name = 'C' THEN 1 ELSE 0 END) = 0
ORDER BY c.customer_id;
```

---

# Why Grouping by `customer_name` Too Is Better

The original query groups only by:

```sql
GROUP BY c.customer_id
```

In some SQL dialects that is acceptable if `customer_id` functionally determines `customer_name`.

But many SQL engines prefer or require that all non-aggregated selected columns appear in `GROUP BY`.

So:

```sql
GROUP BY c.customer_id, c.customer_name
```

is a safer and more portable form.

---

# Alternative Mental Model

You can think of this query as turning each customer's purchases into three counters:

- count of A
- count of B
- count of C

Then checking:

- A counter > 0
- B counter > 0
- C counter = 0

That is exactly the business rule.

---

# Complexity Analysis

Let:

- `C` = number of customers
- `O` = number of orders

## Time Complexity

The database joins customers and orders, then groups by customer.

Conceptually this is around:

```text
O(C + O)
```

plus grouping overhead depending on the engine.

## Space Complexity

The engine stores aggregate state per customer group.

Conceptually:

```text
O(number of customer groups)
```

---

# Final Code Examples

## MySQL-style concise version

```sql
SELECT c.customer_id, customer_name
FROM Customers c
LEFT JOIN Orders o
  ON c.customer_id = o.customer_id
GROUP BY c.customer_id
HAVING SUM(product_name = 'A') > 0
   AND SUM(product_name = 'B') > 0
   AND SUM(product_name = 'C') = 0
ORDER BY c.customer_id;
```

---

## Portable version using `CASE`

```sql
SELECT c.customer_id, c.customer_name
FROM Customers c
LEFT JOIN Orders o
  ON c.customer_id = o.customer_id
GROUP BY c.customer_id, c.customer_name
HAVING SUM(CASE WHEN product_name = 'A' THEN 1 ELSE 0 END) > 0
   AND SUM(CASE WHEN product_name = 'B' THEN 1 ELSE 0 END) > 0
   AND SUM(CASE WHEN product_name = 'C' THEN 1 ELSE 0 END) = 0
ORDER BY c.customer_id;
```

---

# Key Takeaways

- Join customers with orders
- Group by customer
- Use `HAVING` to express the product-purchase conditions
- `SUM(boolean_expression)` works in MySQL because true becomes 1 and false becomes 0
- For portability, use `SUM(CASE WHEN ... THEN 1 ELSE 0 END)`
- Keep customers who bought A and B but never bought C

---
