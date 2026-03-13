# 1484. Group Sold Products By The Date

## Table: Activities

| Column Name | Type    |
| ----------- | ------- |
| sell_date   | date    |
| product     | varchar |

Notes:

- This table **does not have a primary key**.
- It **may contain duplicate rows**.
- Each row represents a **product sold on a specific date**.

---

# Problem

Write a SQL query to find, for each date:

1. The **number of different products sold**
2. The **names of the products sold**

Requirements:

- Product names must be **sorted lexicographically (alphabetically)**.
- Product names should be **concatenated into a single string separated by commas**.
- The result table must be **ordered by `sell_date`**.

---

# Output Columns

| Column    | Description                                                   |
| --------- | ------------------------------------------------------------- |
| sell_date | The date when products were sold                              |
| num_sold  | Number of **distinct products sold on that date**             |
| products  | Comma-separated **sorted list of products sold on that date** |

---

# Example

## Input

### Activities table

| sell_date  | product    |
| ---------- | ---------- |
| 2020-05-30 | Headphone  |
| 2020-06-01 | Pencil     |
| 2020-06-02 | Mask       |
| 2020-05-30 | Basketball |
| 2020-06-01 | Bible      |
| 2020-06-02 | Mask       |
| 2020-05-30 | T-Shirt    |

---

# Output

| sell_date  | num_sold | products                     |
| ---------- | -------- | ---------------------------- |
| 2020-05-30 | 3        | Basketball,Headphone,T-shirt |
| 2020-06-01 | 2        | Bible,Pencil                 |
| 2020-06-02 | 1        | Mask                         |

---

# Explanation

### 2020‑05‑30

Products sold:

```
Headphone
Basketball
T‑Shirt
```

Sorted lexicographically:

```
Basketball, Headphone, T‑Shirt
```

Number of unique products:

```
3
```

---

### 2020‑06‑01

Products sold:

```
Pencil
Bible
```

Sorted lexicographically:

```
Bible, Pencil
```

Number of unique products:

```
2
```

---

### 2020‑06‑02

Products sold:

```
Mask
Mask
```

Unique product:

```
Mask
```

Number of unique products:

```
1
```
