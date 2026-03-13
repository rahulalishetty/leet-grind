# 613. Shortest Distance in a Line

## Table: Point

| Column Name | Type |
| ----------- | ---- |
| x           | int  |

- `x` is the **primary key** for the table.
- Each row represents the **position of a point on the X‑axis**.

---

## Problem

Write a SQL query to find the **shortest distance between any two points** in the table.

Distance between two points on a line is defined as:

```
|x1 - x2|
```

Where:

- `x1` and `x2` are coordinates of two different points.

Return the result as:

| shortest |

---

## Example

### Input

#### Point table

| x   |
| --- |
| -1  |
| 0   |
| 2   |

---

### Output

| shortest |
| -------- |
| 1        |

---

## Explanation

Points on the number line:

```
-1, 0, 2
```

Possible distances:

1. Distance between **-1 and 0**

```
|-1 - 0| = 1
```

2. Distance between **-1 and 2**

```
|-1 - 2| = 3
```

3. Distance between **0 and 2**

```
|0 - 2| = 2
```

The **minimum distance** among all pairs is:

```
1
```

So the output is:

| shortest |
| -------- |
| 1        |
