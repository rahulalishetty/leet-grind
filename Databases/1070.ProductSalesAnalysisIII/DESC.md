# 1070. Product Sales Analysis III

## Table: Sales

| Column Name | Type |
| ----------- | ---- |
| sale_id     | int  |
| product_id  | int  |
| year        | int  |
| quantity    | int  |
| price       | int  |

- `(sale_id, year)` is the **primary key** (unique combination).
- Each row represents a **sale of a product in a specific year**.
- A product may have **multiple sales entries in the same year**.
- `price` represents the **per‑unit price** of the product.

---

# Problem

Find all sales that occurred in the **first year each product was sold**.

Steps required:

1. For every `product_id`, determine the **earliest year** it appears in the `Sales` table.
2. Return **all sales entries** for that product in that earliest year.

---

# Output Requirements

Return a table with the following columns:

| Column     |
| ---------- |
| product_id |
| first_year |
| quantity   |
| price      |

- `first_year` represents the **earliest year the product was sold**.
- Return the result **in any order**.

---

# Example

## Input

### Sales

| sale_id | product_id | year | quantity | price |
| ------- | ---------- | ---- | -------- | ----- |
| 1       | 100        | 2008 | 10       | 5000  |
| 2       | 100        | 2009 | 12       | 5000  |
| 7       | 200        | 2011 | 15       | 9000  |

---

# Output

| product_id | first_year | quantity | price |
| ---------- | ---------- | -------- | ----- |
| 100        | 2008       | 10       | 5000  |
| 200        | 2011       | 15       | 9000  |

---

# Explanation

### Product 100

Sales:

```
2008 → quantity 10 price 5000
2009 → quantity 12 price 5000
```

The **earliest year is 2008**, so we return the sale(s) from that year.

---

### Product 200

Sales:

```
2011 → quantity 15 price 9000
```

Since **2011 is the only year**, it is also the first year.

---

# Final Result

| product_id | first_year | quantity | price |
| ---------- | ---------- | -------- | ----- |
| 100        | 2008       | 10       | 5000  |
| 200        | 2011       | 15       | 9000  |
