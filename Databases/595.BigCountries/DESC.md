# 595. Big Countries

## Table: World

| Column Name | Type    |
| ----------- | ------- |
| name        | varchar |
| continent   | varchar |
| area        | int     |
| population  | int     |
| gdp         | bigint  |

- `name` is the **primary key** (unique values).
- Each row contains information about a **country**, including:
  - the continent it belongs to
  - its area
  - its population
  - its GDP.

---

## Problem

A country is considered **big** if **either** of the following conditions is true:

1. Its **area is at least 3,000,000 km²**
2. Its **population is at least 25,000,000**

Write a SQL query to find the:

- `name`
- `population`
- `area`

for all **big countries**.

Return the result table in **any order**.

---

## Example

### Input

#### World table

| name        | continent | area    | population | gdp          |
| ----------- | --------- | ------- | ---------- | ------------ |
| Afghanistan | Asia      | 652230  | 25500100   | 20343000000  |
| Albania     | Europe    | 28748   | 2831741    | 12960000000  |
| Algeria     | Africa    | 2381741 | 37100000   | 188681000000 |
| Andorra     | Europe    | 468     | 78115      | 3712000000   |
| Angola      | Africa    | 1246700 | 20609294   | 100990000000 |

---

### Output

| name        | population | area    |
| ----------- | ---------- | ------- |
| Afghanistan | 25500100   | 652230  |
| Algeria     | 37100000   | 2381741 |

---

## Explanation

A country qualifies as **big** if:

- `area >= 3000000`
- **OR**
- `population >= 25000000`

From the example:

- **Afghanistan**
  - population = 25,500,100 → satisfies population condition

- **Algeria**
  - population = 37,100,000 → satisfies population condition

These countries satisfy at least one condition, so they appear in the result.
