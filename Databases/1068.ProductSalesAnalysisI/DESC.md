# 1068. Product Sales Analysis I

## Table: Sales

| Column Name | Type |
| ----------- | ---- |
| sale_id     | int  |
| product_id  | int  |
| year        | int  |
| quantity    | int  |
| price       | int  |

### Notes

- `(sale_id, year)` is the **primary key** (unique combination).
- `product_id` is a **foreign key** referencing the `Product` table.
- Each row represents a **sale of a product in a given year**.
- `price` represents the **price per unit**.

---

## Table: Product

| Column Name  | Type    |
| ------------ | ------- |
| product_id   | int     |
| product_name | varchar |

### Notes

- `product_id` is the **primary key**.
- Each row represents the **name of a product**.

---

# Problem

Write a SQL query to report the following information for every sale:

- `product_name`
- `year`
- `price`

The information should be derived from the **Sales** table and the **Product** table.

Return the result table **in any order**.

---

# Output Format

| product_name | year | price |

Each row corresponds to a sale record with its associated product name.

---

# Example

## Input

### Sales Table

| sale_id | product_id | year | quantity | price |
| ------: | ---------: | ---: | -------: | ----: |
|       1 |        100 | 2008 |       10 |  5000 |
|       2 |        100 | 2009 |       12 |  5000 |
|       7 |        200 | 2011 |       15 |  9000 |

### Product Table

| product_id | product_name |
| ---------: | ------------ |
|        100 | Nokia        |
|        200 | Apple        |
|        300 | Samsung      |

---

# Explanation

### Sale ID = 1

```
product_id = 100
```

From the `Product` table:

```
100 → Nokia
```

Therefore:

```
product_name = Nokia
year = 2008
price = 5000
```

---

### Sale ID = 2

```
product_id = 100
```

Product name:

```
Nokia
```

Result:

```
product_name = Nokia
year = 2009
price = 5000
```

---

### Sale ID = 7

```
product_id = 200
```

Product name:

```
Apple
```

Result:

```
product_name = Apple
year = 2011
price = 9000
```

---

# Output

| product_name | year | price |
| ------------ | ---- | ----- |
| Nokia        | 2008 | 5000  |
| Nokia        | 2009 | 5000  |
| Apple        | 2011 | 9000  |

---

# Summary

The solution requires:

- **joining** the `Sales` table with the `Product` table
- matching records using `product_id`
- returning the product name along with the year and price from the sales record
