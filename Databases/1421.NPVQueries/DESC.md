# 1421. NPV Queries

## Table: NPV

| Column Name | Type |
| ----------- | ---- |
| id          | int  |
| year        | int  |
| npv         | int  |

Notes:

- `(id, year)` is the **primary key**.
- Each row represents the **Net Present Value (NPV)** of an inventory item for a specific year.

---

## Table: Queries

| Column Name | Type |
| ----------- | ---- |
| id          | int  |
| year        | int  |

Notes:

- `(id, year)` is the **primary key**.
- Each row represents a **query requesting the NPV** for a given `(id, year)` pair.

---

# Problem

Write a SQL query to find the **NPV value for each query** listed in the `Queries` table.

Rules:

- If the `(id, year)` pair **exists in the `NPV` table**, return its corresponding `npv`.
- If the pair **does not exist**, return **0**.
- Return the result table **in any order**.

The result must include:

- `id`
- `year`
- `npv`

---

# Example

## Input

### NPV table

| id  | year | npv |
| --- | ---- | --- |
| 1   | 2018 | 100 |
| 7   | 2020 | 30  |
| 13  | 2019 | 40  |
| 1   | 2019 | 113 |
| 2   | 2008 | 121 |
| 3   | 2009 | 12  |
| 11  | 2020 | 99  |
| 7   | 2019 | 0   |

### Queries table

| id  | year |
| --- | ---- |
| 1   | 2019 |
| 2   | 2008 |
| 3   | 2009 |
| 7   | 2018 |
| 7   | 2019 |
| 7   | 2020 |
| 13  | 2019 |

---

# Output

| id  | year | npv |
| --- | ---- | --- |
| 1   | 2019 | 113 |
| 2   | 2008 | 121 |
| 3   | 2009 | 12  |
| 7   | 2018 | 0   |
| 7   | 2019 | 0   |
| 7   | 2020 | 30  |
| 13  | 2019 | 40  |

---

# Explanation

- `(1, 2019)` → NPV exists → **113**
- `(2, 2008)` → NPV exists → **121**
- `(3, 2009)` → NPV exists → **12**
- `(7, 2018)` → NPV **does not exist** → return **0**
- `(7, 2019)` → NPV exists → **0**
- `(7, 2020)` → NPV exists → **30**
- `(13, 2019)` → NPV exists → **40**
