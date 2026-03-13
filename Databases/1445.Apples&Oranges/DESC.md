# 1445. Apples & Oranges

## Table: Sales

| Column Name | Type |
| ----------- | ---- |
| sale_date   | date |
| fruit       | enum |
| sold_num    | int  |

**Notes:**

- `(sale_date, fruit)` is the **primary key** (unique combination).
- The table stores the number of **apples** and **oranges** sold each day.
- `fruit` can be either:
  - `"apples"`
  - `"oranges"`

---

# Problem

Write a SQL query to report the **difference between the number of apples and oranges sold each day**.

The difference is defined as:

```
diff = apples_sold - oranges_sold
```

Return the result table ordered by:

```
sale_date
```

---

# Example

## Input

### Sales Table

| sale_date  | fruit   | sold_num |
| ---------- | ------- | -------- |
| 2020-05-01 | apples  | 10       |
| 2020-05-01 | oranges | 8        |
| 2020-05-02 | apples  | 15       |
| 2020-05-02 | oranges | 15       |
| 2020-05-03 | apples  | 20       |
| 2020-05-03 | oranges | 0        |
| 2020-05-04 | apples  | 15       |
| 2020-05-04 | oranges | 16       |

---

# Output

| sale_date  | diff |
| ---------- | ---- |
| 2020-05-01 | 2    |
| 2020-05-02 | 0    |
| 2020-05-03 | 20   |
| 2020-05-04 | -1   |

---

# Explanation

### 2020-05-01

- Apples sold = **10**
- Oranges sold = **8**

```
10 - 8 = 2
```

---

### 2020-05-02

- Apples sold = **15**
- Oranges sold = **15**

```
15 - 15 = 0
```

---

### 2020-05-03

- Apples sold = **20**
- Oranges sold = **0**

```
20 - 0 = 20
```

---

### 2020-05-04

- Apples sold = **15**
- Oranges sold = **16**

```
15 - 16 = -1
```
