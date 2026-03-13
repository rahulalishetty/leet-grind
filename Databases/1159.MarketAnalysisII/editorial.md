# 1159. Market Analysis II

## Approach: Using Window Function to Rank

## Core idea

For each user, we need to answer this question:

> Is the brand of the **second item they sold** equal to their **favorite brand**?

So the problem breaks into three clear parts:

1. find each seller's **second sold item**
2. determine that item's **brand**
3. compare it with the user's `favorite_brand`

If a user sold fewer than two items, the answer must be:

```text
no
```

A clean way to identify the second sold item is to use a **window function** to rank each seller's orders by date.

---

## Why a window function is a good fit

We are not just counting how many items a seller sold.

We specifically need the **2nd item by date** for each seller.

That means we need an ordering **within each seller's own rows**.

This is exactly what window functions are designed for.

In particular:

```sql
RANK() OVER (PARTITION BY seller_id ORDER BY order_date ASC)
```

lets us rank a seller's orders from earliest to latest.

Then we can simply keep the row where:

```sql
rnk = 2
```

---

# Step 1: Rank each seller's orders by date

We start by assigning a rank to every order for each seller.

```sql
SELECT
    seller_id,
    item_id,
    RANK() OVER (PARTITION BY seller_id ORDER BY order_date ASC) AS rnk
FROM Orders;
```

---

## Why this works

### `PARTITION BY seller_id`

This restarts ranking separately for each seller.

So each seller gets their own sequence:

- first sale -> rank 1
- second sale -> rank 2
- third sale -> rank 3
- and so on

### `ORDER BY order_date ASC`

This ensures the earliest sold item comes first.

So rank 2 corresponds to the **second item sold by date**.

### Why `RANK()` is safe here

The problem guarantees:

> no seller sells more than one item in a day

That means no ties exist in `order_date` within a seller's partition.

So `RANK()` behaves just like `ROW_NUMBER()` for this problem.

---

## Example ranking on the sample

### Orders table

| order_id | order_date | item_id | buyer_id | seller_id |
| -------- | ---------- | ------- | -------- | --------- |
| 1        | 2019-08-01 | 4       | 1        | 2         |
| 2        | 2019-08-02 | 2       | 1        | 3         |
| 3        | 2019-08-03 | 3       | 2        | 3         |
| 4        | 2019-08-04 | 1       | 4        | 2         |
| 5        | 2019-08-04 | 1       | 3        | 4         |
| 6        | 2019-08-05 | 2       | 2        | 4         |

Now rank orders seller by seller.

### Seller 2

- 2019-08-01 -> item 4 -> rank 1
- 2019-08-04 -> item 1 -> rank 2

### Seller 3

- 2019-08-02 -> item 2 -> rank 1
- 2019-08-03 -> item 3 -> rank 2

### Seller 4

- 2019-08-04 -> item 1 -> rank 1
- 2019-08-05 -> item 2 -> rank 2

### Seller 1

- no orders

So the ranked subquery conceptually looks like:

| seller_id | item_id | rnk |
| --------- | ------- | --- |
| 2         | 4       | 1   |
| 2         | 1       | 2   |
| 3         | 2       | 1   |
| 3         | 3       | 2   |
| 4         | 1       | 1   |
| 4         | 2       | 2   |

---

# Step 2: Keep only the second sold item and get its brand

Now that each seller's orders are ranked, we only need the row where:

```sql
rnk = 2
```

But `Orders` only gives us `item_id`.
To compare against a user's favorite brand, we must also know the `item_brand`.

So we join with the `Items` table.

```sql
SELECT
    a.seller_id,
    a.item_id,
    i.item_brand
FROM (
    SELECT
        seller_id,
        item_id,
        RANK() OVER (PARTITION BY seller_id ORDER BY order_date ASC) AS rnk
    FROM Orders
) a
JOIN Items i
    ON a.item_id = i.item_id
WHERE a.rnk = 2;
```

---

## Why this works

- the inner subquery computes each seller's order ranking
- `WHERE a.rnk = 2` keeps only the second sold item
- joining `Items` gives us the brand of that second sold item

---

## Result of Step 2 on the sample

From the ranked rows:

- seller 2 -> second item = item 1 -> brand Samsung
- seller 3 -> second item = item 3 -> brand LG
- seller 4 -> second item = item 2 -> brand Lenovo

So the intermediate result becomes:

| seller_id | item_id | item_brand |
| --------- | ------- | ---------- |
| 2         | 1       | Samsung    |
| 3         | 3       | LG         |
| 4         | 2       | Lenovo     |

Notice that seller 1 does not appear, because seller 1 sold nothing.

---

# Step 3: Join with users and compare with favorite brand

Now we want to produce one row for **every user**, even users who sold fewer than two items.

That means the base table for the final query must be:

```sql
Users
```

and we use a:

```sql
LEFT JOIN
```

to attach second-item information when it exists.

If a user does not have a second sold item, the joined columns will be `NULL`, and the result should be `no`.

That comparison is handled with:

```sql
CASE WHEN u.favorite_brand = b.item_brand THEN 'yes' ELSE 'no' END
```

---

## Final accepted query

```sql
SELECT
    u.user_id AS seller_id,
    CASE
        WHEN u.favorite_brand = b.item_brand THEN 'yes'
        ELSE 'no'
    END AS 2nd_item_fav_brand
FROM Users u
LEFT JOIN (
    SELECT
        a.seller_id,
        a.item_id,
        i.item_brand
    FROM (
        SELECT
            seller_id,
            item_id,
            RANK() OVER (PARTITION BY seller_id ORDER BY order_date ASC) AS rnk
        FROM Orders
    ) a
    JOIN Items i
        ON a.item_id = i.item_id
    WHERE a.rnk = 2
) b
    ON u.user_id = b.seller_id;
```

---

# Step-by-step explanation of the final query

## Inner ranked subquery

```sql
SELECT
    seller_id,
    item_id,
    RANK() OVER (PARTITION BY seller_id ORDER BY order_date ASC) AS rnk
FROM Orders
```

This assigns each seller's orders a chronological rank.

---

## Join to `Items` and filter to second orders

```sql
SELECT
    a.seller_id,
    a.item_id,
    i.item_brand
FROM (...) a
JOIN Items i
    ON a.item_id = i.item_id
WHERE a.rnk = 2
```

This extracts the brand of the second sold item for each seller who has one.

---

## Outer query over `Users`

```sql
SELECT
    u.user_id AS seller_id,
    CASE
        WHEN u.favorite_brand = b.item_brand THEN 'yes'
        ELSE 'no'
    END AS 2nd_item_fav_brand
FROM Users u
LEFT JOIN (...) b
    ON u.user_id = b.seller_id
```

This ensures:

- every user appears exactly once
- users without a second sold item still appear
- they get `no` because `b.item_brand` is null and the comparison fails

---

# Walkthrough on the sample

## Users

| user_id | favorite_brand |
| ------- | -------------- |
| 1       | Lenovo         |
| 2       | Samsung        |
| 3       | LG             |
| 4       | HP             |

## Second sold item brand per seller

| seller_id | item_brand |
| --------- | ---------- |
| 2         | Samsung    |
| 3         | LG         |
| 4         | Lenovo     |

Now compare with favorite brand:

### User 1

- no second sold item
- result -> `no`

### User 2

- favorite brand = Samsung
- second sold item brand = Samsung
- result -> `yes`

### User 3

- favorite brand = LG
- second sold item brand = LG
- result -> `yes`

### User 4

- favorite brand = HP
- second sold item brand = Lenovo
- result -> `no`

Final output:

| seller_id | 2nd_item_fav_brand |
| --------- | ------------------ |
| 1         | no                 |
| 2         | yes                |
| 3         | yes                |
| 4         | no                 |

---

# Why `LEFT JOIN` is necessary

A common mistake would be to use an inner join from `Users` to the second-item subquery.

That would drop users who sold fewer than two items.

But the problem explicitly says those users must still appear, with answer:

```text
no
```

So `LEFT JOIN` is essential.

---

# Why `CASE` is the correct final logic

The comparison rule is very simple:

- if favorite brand equals second-item brand -> `yes`
- otherwise -> `no`

This includes two cases under `no`:

1. the second sold item exists, but its brand differs
2. the second sold item does not exist at all

Because `NULL = value` is not true, the `ELSE 'no'` naturally covers both situations.

---

# Why ranking is safer than trying to count manually

You might be tempted to solve this with grouped counts and complicated joins, but that would miss the key requirement:

> not just whether they sold at least two items, but specifically the **brand of the second sold item by date**

That is why a window function is the right tool.
It preserves row-level detail while still giving positional information.

---

# Important SQL concepts used here

## 1. Window function

```sql
RANK() OVER (PARTITION BY seller_id ORDER BY order_date ASC)
```

Used to find the second sold item for each seller.

## 2. Join to lookup brand

```sql
JOIN Items ON item_id
```

Used to translate item id into item brand.

## 3. `LEFT JOIN`

Ensures every user appears in the result, even those without two sales.

## 4. `CASE`

Converts the brand comparison into `yes` / `no`.

---

# Subtle point: `RANK()` vs `ROW_NUMBER()`

Since the problem guarantees:

> no seller sells more than one item in a day

there are no date ties per seller.

So `RANK()` and `ROW_NUMBER()` would behave the same here.

That makes `RANK()` perfectly valid for this solution.

---

# Complexity

Let:

- `U` = number of rows in `Users`
- `O` = number of rows in `Orders`
- `I` = number of rows in `Items`

## Time Complexity

The query:

- ranks orders per seller
- joins ranked second-sales to items
- left joins the result to users

A practical interview-style summary is:

```text
O(O log O)
```

for the ranking/sorting component, depending on the SQL engine.

## Space Complexity

Additional space is needed for:

- ranked order rows
- the subquery containing second sold items

So auxiliary space depends mainly on the number of orders.

---

# Key takeaways

1. The key task is identifying each seller's **second order by date**.
2. A window function is the cleanest way to do that.
3. After finding the second sold item, join with `Items` to get its brand.
4. Use `LEFT JOIN` with `Users` so that all users appear.
5. Use `CASE` to convert the comparison into `yes` or `no`.

---

## Final accepted implementation

```sql
SELECT u.user_id AS seller_id,
       CASE WHEN u.favorite_brand = b.item_brand THEN 'yes' ELSE 'no' END AS 2nd_item_fav_brand
FROM Users u
LEFT JOIN(
    SELECT a.seller_id, a.item_id, i.item_brand
    FROM (
        SELECT seller_id,
               item_id,
               RANK() OVER (PARTITION BY seller_id ORDER BY order_date ASC) AS rnk
        FROM Orders) a
    JOIN Items i
    ON a.item_id = i.item_id
    WHERE a.rnk = 2) b
ON u.user_id = b.seller_id;
```
