# 612. Shortest Distance in a Plane

## Table: Point2D

| Column Name | Type |
| ----------- | ---- |
| x           | int  |
| y           | int  |

- `(x, y)` is the **primary key** (a unique combination of coordinates).
- Each row represents a **point on the 2D plane**.

---

## Problem

The distance between two points:

```
p1(x1, y1) and p2(x2, y2)
```

is defined as:

```
sqrt((x2 - x1)^2 + (y2 - y1)^2)
```

Write a SQL query to compute the **shortest distance between any two points** in the table.

### Requirements

- Compare every pair of points.
- Compute the **Euclidean distance**.
- Return the **minimum distance**.
- Round the result to **two decimal places**.

---

## Example

### Input

#### Point2D table

| x   | y   |
| --- | --- |
| -1  | -1  |
| 0   | 0   |
| -1  | -2  |

---

### Output

| shortest |
| -------- |
| 1.00     |

---

## Explanation

The points are:

```
(-1, -1)
(0, 0)
(-1, -2)
```

Distances:

1. Distance between **(-1,-1)** and **(0,0)**

```
sqrt((0 - (-1))^2 + (0 - (-1))^2)
= sqrt(1^2 + 1^2)
= sqrt(2)
≈ 1.41
```

2. Distance between **(-1,-1)** and **(-1,-2)**

```
sqrt((-1 - (-1))^2 + (-2 - (-1))^2)
= sqrt(0^2 + (-1)^2)
= 1
```

3. Distance between **(0,0)** and **(-1,-2)**

```
sqrt((-1 - 0)^2 + (-2 - 0)^2)
= sqrt(1 + 4)
= sqrt(5)
≈ 2.24
```

The **minimum distance** is:

```
1.00
```

So the result is:

| shortest |
| -------- |
| 1.00     |
