# Customers Who Bought All Products

## Table: Customer

| Column Name | Type |
| ----------- | ---- |
| customer_id | int  |
| product_key | int  |

- The table may contain **duplicate rows**.
- `customer_id` is **NOT NULL**.
- `product_key` is a **foreign key** referencing the `Product` table.

---

## Table: Product

| Column Name | Type |
| ----------- | ---- |
| product_key | int  |

- `product_key` is the **primary key** of this table.
- Each row represents a **distinct product**.

---

# Problem

Find all **customer IDs** from the `Customer` table who have purchased **every product listed in the `Product` table**.

In other words:

```
A customer qualifies if they bought ALL products that exist in the Product table.
```

Return the result table **in any order**.

---

# Example

## Input

### Customer

| customer_id | product_key |
| ----------- | ----------- |
| 1           | 5           |
| 2           | 6           |
| 3           | 5           |
| 3           | 6           |
| 1           | 6           |

### Product

| product_key |
| ----------- |
| 5           |
| 6           |

---

# Output

| customer_id |
| ----------- |
| 1           |
| 3           |

---

# Explanation

The list of available products is:

```
5, 6
```

A customer must have purchased **both products** to qualify.

### Customer 1

Purchases:

```
5
6
```

Customer 1 bought **all products**, so they qualify.

---

### Customer 2

Purchases:

```
6
```

Customer 2 did **not purchase product 5**, so they do **not qualify**.

---

### Customer 3

Purchases:

```
5
6
```

Customer 3 bought **all products**, so they qualify.

---

# Final Result

| customer_id |
| ----------- |
| 1           |
| 3           |
