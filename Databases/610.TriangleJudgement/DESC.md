# 610. Triangle Judgement

## Table: Triangle

| Column Name | Type |
| ----------- | ---- |
| x           | int  |
| y           | int  |
| z           | int  |

- `(x, y, z)` is the **primary key** of the table.
- Each row contains the **lengths of three line segments**.

---

## Problem

Write a SQL query to determine **whether the three line segments can form a triangle**.

For each row:

- If the three segments can form a triangle → output **"Yes"**
- Otherwise → output **"No"**

Return the result table in **any order**.

---

## Triangle Rule

Three lengths can form a triangle **if and only if**:

```
x + y > z
x + z > y
y + z > x
```

If all three conditions are satisfied, the segments can form a triangle.

---

## Example

### Input

#### Triangle table

| x   | y   | z   |
| --- | --- | --- |
| 13  | 15  | 30  |
| 10  | 20  | 15  |

---

### Output

| x   | y   | z   | triangle |
| --- | --- | --- | -------- |
| 13  | 15  | 30  | No       |
| 10  | 20  | 15  | Yes      |

---

## Explanation

### Row 1

```
13 + 15 = 28
28 < 30
```

Since one side is **greater than or equal to the sum of the other two**, it **cannot form a triangle**.

Result → **No**

---

### Row 2

```
10 + 20 > 15
10 + 15 > 20
20 + 15 > 10
```

All three triangle conditions hold.

Result → **Yes**
