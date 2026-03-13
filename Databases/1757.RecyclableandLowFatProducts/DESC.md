# 1757. Recyclable and Low Fat Products

## Table: Products

| Column Name | Type |
| ----------- | ---- |
| product_id  | int  |
| low_fats    | enum |
| recyclable  | enum |

### Notes

- `product_id` is the **primary key**.
- `low_fats` is an ENUM with values:
  - `'Y'` → product is **low fat**
  - `'N'` → product is **not low fat**
- `recyclable` is an ENUM with values:
  - `'Y'` → product is **recyclable**
  - `'N'` → product is **not recyclable**

---

# Problem

Write a SQL query to **find the IDs of products that are both**:

- **Low fat**
- **Recyclable**

Return the result table **in any order**.

---

# Example

## Input

### Products Table

| product_id | low_fats | recyclable |
| ---------- | -------- | ---------- |
| 0          | Y        | N          |
| 1          | Y        | Y          |
| 2          | N        | Y          |
| 3          | Y        | Y          |
| 4          | N        | N          |

---

## Output

| product_id |
| ---------- |
| 1          |
| 3          |

---

## Explanation

Only **products 1 and 3** satisfy both conditions:

- `low_fats = 'Y'`
- `recyclable = 'Y'`
