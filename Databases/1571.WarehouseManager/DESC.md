# 1571. Warehouse Manager

## Table: Warehouse

| Column Name | Type    |
| ----------- | ------- |
| name        | varchar |
| product_id  | int     |
| units       | int     |

Notes:

- `(name, product_id)` is the **primary key**.
- Each row indicates how many **units of a product** are stored in a warehouse.

---

## Table: Products

| Column Name  | Type    |
| ------------ | ------- |
| product_id   | int     |
| product_name | varchar |
| Width        | int     |
| Length       | int     |
| Height       | int     |

Notes:

- `product_id` is the **primary key**.
- Dimensions are given in **feet**.

Volume of a product:

```
Volume = Width × Length × Height
```

---

# Problem

Write a SQL query to report the **total cubic feet of inventory volume in each warehouse**.

Rules:

- Each product contributes:

```
units × (Width × Length × Height)
```

- Sum the volumes for all products in each warehouse.

Return the result with:

| Column         | Description                            |
| -------------- | -------------------------------------- |
| warehouse_name | Name of the warehouse                  |
| volume         | Total cubic feet occupied by inventory |

The result can be returned **in any order**.

---

# Example

## Input

### Warehouse table

| name     | product_id | units |
| -------- | ---------- | ----- |
| LCHouse1 | 1          | 1     |
| LCHouse1 | 2          | 10    |
| LCHouse1 | 3          | 5     |
| LCHouse2 | 1          | 2     |
| LCHouse2 | 2          | 2     |
| LCHouse3 | 4          | 1     |

### Products table

| product_id | product_name | Width | Length | Height |
| ---------- | ------------ | ----- | ------ | ------ |
| 1          | LC-TV        | 5     | 50     | 40     |
| 2          | LC-KeyChain  | 5     | 5      | 5      |
| 3          | LC-Phone     | 2     | 10     | 10     |
| 4          | LC-T-Shirt   | 4     | 10     | 20     |

---

# Output

| warehouse_name | volume |
| -------------- | ------ |
| LCHouse1       | 12250  |
| LCHouse2       | 20250  |
| LCHouse3       | 800    |

---

# Explanation

### Product Volumes

```
LC-TV        = 5 × 50 × 40 = 10000
LC-KeyChain  = 5 × 5 × 5   = 125
LC-Phone     = 2 × 10 × 10 = 200
LC-T-Shirt   = 4 × 10 × 20 = 800
```

### LCHouse1

```
1 × 10000 + 10 × 125 + 5 × 200
= 10000 + 1250 + 1000
= 12250
```

### LCHouse2

```
2 × 10000 + 2 × 125
= 20000 + 250
= 20250
```

### LCHouse3

```
1 × 800 = 800
```
