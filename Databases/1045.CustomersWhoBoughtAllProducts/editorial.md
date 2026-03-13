# Customers Who Bought All Products

## Detailed Summary of Two Approaches

We need to find the `customer_id` values of customers who bought **every product** listed in the `Product` table.

This is a classic “for all” query:

> return customers such that, for every product in `Product`, that customer has a corresponding row in `Customer`.

There are two main approaches covered here:

1. **Count how many distinct products each customer bought**
2. **Use a division-style relational algebra idea with Cartesian product and set difference**

The first approach is the recommended one because it is much simpler and more efficient.

---

# Problem restatement

A customer qualifies if:

```text
number of distinct products purchased by the customer
=
number of products that exist in Product
```

This works because:

- `Product` contains the full catalog of products we care about
- a customer must have purchased **all** of them
- so their set of purchased product keys must cover the full set of product keys in `Product`

---

# Approach 1: Count how many products each customer bought

## Core idea

If a customer bought all products, then the number of **distinct** products they bought must equal the number of rows in `Product`.

So the strategy is:

1. group `Customer` by `customer_id`
2. count how many distinct `product_key` values each customer has
3. compare that count with the total number of products

This is the cleanest solution.

---

## Why `DISTINCT` is needed in `Customer`

The `Customer` table may contain duplicate rows.

That means if a customer bought the same product multiple times, or duplicate records exist, a plain `COUNT(product_key)` could overcount.

So we must use:

```sql
COUNT(DISTINCT product_key)
```

to count unique purchased products per customer.

---

## Why `DISTINCT` is not needed in `Product`

The `Product` table has:

- `product_key` as the primary key

So each product appears exactly once.

That means:

```sql
COUNT(product_key)
```

already equals the number of distinct products.

No `DISTINCT` is needed there.

---

## Final query for Approach 1

```sql
SELECT
  customer_id
FROM
  Customer
GROUP BY
  customer_id
HAVING
  COUNT(DISTINCT product_key) = (
    SELECT
      COUNT(product_key)
    FROM
      Product
  );
```

---

## Step-by-step explanation

### `GROUP BY customer_id`

This groups all purchase rows by customer.

So we can analyze each customer separately.

---

### `COUNT(DISTINCT product_key)`

This counts how many **different** products each customer bought.

If a customer bought product `5` twice, it still counts as only one distinct product.

---

### Subquery

```sql
(
  SELECT COUNT(product_key)
  FROM Product
)
```

This returns the total number of products in the product catalog.

---

### `HAVING ... = ...`

The `HAVING` clause keeps only those customers whose number of distinct purchased products matches the total number of products.

Those are exactly the customers who bought all products.

---

## Walkthrough on the sample

### Customer table

| customer_id | product_key |
| ----------- | ----------- |
| 1           | 5           |
| 2           | 6           |
| 3           | 5           |
| 3           | 6           |
| 1           | 6           |

### Product table

| product_key |
| ----------- |
| 5           |
| 6           |

Total number of products:

```text
2
```

Now count distinct products per customer:

### Customer 1

Products bought:

```text
5, 6
```

Distinct count:

```text
2
```

Matches total product count -> qualifies.

### Customer 2

Products bought:

```text
6
```

Distinct count:

```text
1
```

Does not match -> does not qualify.

### Customer 3

Products bought:

```text
5, 6
```

Distinct count:

```text
2
```

Matches total product count -> qualifies.

Final result:

| customer_id |
| ----------- |
| 1           |
| 3           |

---

## Why this approach is recommended

This approach is recommended because it is:

- simple
- direct
- efficient
- easy to explain
- easy to maintain

It captures the whole requirement in one grouped query and one small subquery.

---

# Approach 2: Use nested subquery with Cartesian Product (division-style alternative)

## Core idea

This approach is based on the relational algebra idea of **division**.

In relational algebra, division is used when you want to find values in one relation that are associated with **all** values in another relation.

That is exactly our problem:

- find customers who are associated with **all** products

SQL does not have a built-in division operator, so this must be simulated.

The simulation idea is:

1. generate all possible `(customer_id, product_key)` combinations
2. find which of those combinations are missing from the real `Customer` table
3. customers with missing combinations do **not** qualify
4. return customers not in that missing set

This is logically valid, but much more expensive.

---

## Relational algebra intuition

If we think of:

- `Customer(customer_id, product_key)`
- `Product(product_key)`

then we want the set of `customer_id` values such that every `product_key` is present alongside that `customer_id`.

That is the classical meaning of division.

Because SQL lacks direct division, we simulate it with:

- Cartesian product
- set difference
- exclusion

---

## Step 1: Build all possible `(customer_id, product_key)` combinations

```sql
SELECT DISTINCT
  Customer.customer_id,
  Product.product_key
FROM
  Customer,
  Product
```

This is a Cartesian product between:

- all customers that appear in `Customer`
- all products in `Product`

So for every customer, it creates the full list of products they **should** have bought if they were to qualify.

---

## Why `DISTINCT` is used here

Because the `Customer` table may contain duplicate rows, the Cartesian product could also generate repeated `(customer_id, product_key)` pairs.

Using `DISTINCT` reduces unnecessary duplicates before comparison.

That improves readability and can reduce the amount of work.

---

## Step 2: Find missing combinations

Now compare the full expected set against the actual `Customer` table.

The combinations that are in “all possible cases” but not in the real `Customer` table are the missing purchases.

```sql
WHERE
  (customer_id, product_key) NOT IN (
    SELECT
      customer_id,
      product_key
    FROM
      Customer
  )
```

If a `(customer_id, product_key)` pair is missing, then that customer did not buy that product.

Such a customer cannot qualify.

---

## Step 3: Find customers with at least one missing product

The inner query returns customers for whom at least one required `(customer_id, product_key)` pair is missing.

So these are the customers who **did not** buy all products.

---

## Step 4: Exclude those customers

Finally, return customers not in that missing set:

```sql
SELECT DISTINCT
  customer_id
FROM
  Customer
WHERE
  customer_id NOT IN (...)
```

These are the customers who do not miss any product.

So they are the customers who bought all products.

---

## Final query for Approach 2

```sql
SELECT DISTINCT
  customer_id
FROM
  Customer
WHERE
  customer_id NOT IN (
    SELECT
      customer_id
    FROM
      (
        SELECT DISTINCT
          Customer.customer_id,
          Product.product_key
        FROM
          Customer,
          Product
      ) AS AllPossibleCases
    WHERE
      (customer_id, product_key) NOT IN (
        SELECT
          customer_id,
          product_key
        FROM
          Customer
      )
  );
```

---

## Step-by-step explanation

### Outer query

```sql
SELECT DISTINCT customer_id
FROM Customer
WHERE customer_id NOT IN (...)
```

Start with all customers, then exclude those who are missing at least one product.

---

### `AllPossibleCases`

```sql
SELECT DISTINCT
  Customer.customer_id,
  Product.product_key
FROM
  Customer,
  Product
```

This creates every customer-product pair that should exist if a customer bought all products.

---

### Missing pair detection

```sql
WHERE (customer_id, product_key) NOT IN (
  SELECT customer_id, product_key
  FROM Customer
)
```

This keeps only the expected pairs that are absent from actual purchases.

---

### Customer exclusion

The intermediate result identifies customers who are missing one or more products.

The outer query removes them.

---

## Walkthrough on the sample

### Customers appearing in `Customer`

```text
1, 2, 3
```

### Products in `Product`

```text
5, 6
```

### All possible customer-product pairs

```text
(1,5), (1,6),
(2,5), (2,6),
(3,5), (3,6)
```

### Actual pairs in `Customer`

```text
(1,5), (1,6),
(2,6),
(3,5), (3,6)
```

### Missing pairs

Only:

```text
(2,5)
```

So customer `2` is missing product `5`.

That means customer `2` does not qualify.

Remaining customers:

```text
1, 3
```

Final result:

| customer_id |
| ----------- |
| 1           |
| 3           |

---

# Why Approach 2 is correct but not recommended

This approach is logically sound, but it is much heavier.

It explicitly constructs and compares many combinations.

That means:

- more rows are generated
- more comparisons are needed
- the query is harder to read
- the query is harder to maintain

It is useful mainly as a conceptual demonstration of relational division.

---

# Performance discussion

## Approach 1

Approach 1 essentially:

- scans `Customer`
- groups by `customer_id`
- scans `Product` once for the total count

This is efficient and straightforward.

---

## Approach 2

Approach 2 builds a Cartesian product.

If:

- `N` = number of rows/customers considered
- `M` = number of products

then the Cartesian product can create on the order of:

```text
O(N * M)
```

rows before filtering.

That is much more work than Approach 1.

So even though Approach 2 is interesting conceptually, it is usually not the better production choice.

---

# Important SQL concepts used here

## 1. `GROUP BY`

Used in Approach 1 to compute product counts per customer.

## 2. `COUNT(DISTINCT ...)`

Used in Approach 1 to handle duplicate customer-product rows safely.

## 3. Cartesian product

Used in Approach 2 to generate all customer-product combinations.

## 4. Set difference via `NOT IN`

Used in Approach 2 to find missing purchases.

## 5. Relational division concept

Approach 2 is a simulation of division using primitive operations.

---

# Key takeaways

1. The simplest correct solution is:
   - count distinct products per customer
   - compare with the total product count
2. `DISTINCT` is necessary on `Customer.product_key` because the table may contain duplicate rows.
3. The product count subquery does not need `DISTINCT` because `product_key` is a primary key in `Product`.
4. The Cartesian-product approach is a valid division-style alternative, but it is more expensive and less readable.
5. Approach 1 is the recommended solution in practice.

---

## Final recommended implementation

```sql
SELECT
  customer_id
FROM
  Customer
GROUP BY
  customer_id
HAVING
  COUNT(DISTINCT product_key) = (
    SELECT
      COUNT(product_key)
    FROM
      Product
  );
```

---

## Alternative implementation using division-style logic

```sql
SELECT DISTINCT
  customer_id
FROM
  Customer
WHERE
  customer_id NOT IN (
    SELECT
      customer_id
    FROM
      (
        SELECT DISTINCT
          Customer.customer_id,
          Product.product_key
        FROM
          Customer,
          Product
      ) AS AllPossibleCases
    WHERE
      (customer_id, product_key) NOT IN (
        SELECT
          customer_id,
          product_key
        FROM
          Customer
      )
  );
```
