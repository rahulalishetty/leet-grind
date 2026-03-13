# 1251. Average Selling Price

## Table: Prices

| Column Name | Type |
| ----------- | ---- |
| product_id  | int  |
| start_date  | date |
| end_date    | date |
| price       | int  |

Notes:

- `(product_id, start_date, end_date)` is the **primary key**.
- Each row represents the **price of a product during a specific date range**.
- For a given `product_id`, **date ranges do not overlap**.

---

## Table: UnitsSold

| Column Name   | Type |
| ------------- | ---- |
| product_id    | int  |
| purchase_date | date |
| units         | int  |

Notes:

- This table **may contain duplicate rows**.
- Each row represents a **sale transaction**, indicating:
  - the product sold
  - the purchase date
  - the number of units sold.

---

# Problem

Write a SQL query to find the **average selling price for each product**.

The **average selling price** is calculated as:

```
Total revenue from product sales / Total units sold
```

Where:

```
Revenue = price * units
```

Requirements:

- The result column must be named **`average_price`**.
- Round the value to **2 decimal places**.
- If a product has **no sold units**, its average price should be **0**.
- Return the result table **in any order**.

---

# Example

## Input

### Prices table

| product_id | start_date | end_date   | price |
| ---------- | ---------- | ---------- | ----- |
| 1          | 2019-02-17 | 2019-02-28 | 5     |
| 1          | 2019-03-01 | 2019-03-22 | 20    |
| 2          | 2019-02-01 | 2019-02-20 | 15    |
| 2          | 2019-02-21 | 2019-03-31 | 30    |

### UnitsSold table

| product_id | purchase_date | units |
| ---------- | ------------- | ----- |
| 1          | 2019-02-25    | 100   |
| 1          | 2019-03-01    | 15    |
| 2          | 2019-02-10    | 200   |
| 2          | 2019-03-22    | 30    |

---

# Output

| product_id | average_price |
| ---------- | ------------- |
| 1          | 6.96          |
| 2          | 16.96         |

---

# Explanation

Average selling price is calculated using:

```
(total revenue) / (total units sold)
```

### Product 1

Revenue:

```
(100 × 5) + (15 × 20)
= 500 + 300
= 800
```

Total units:

```
115
```

Average price:

```
800 / 115 = 6.96
```

---

### Product 2

Revenue:

```
(200 × 15) + (30 × 30)
= 3000 + 900
= 3900
```

Total units:

```
230
```

Average price:

```
3900 / 230 = 16.96
```
