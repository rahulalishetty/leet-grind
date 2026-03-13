# 3053. Classifying Triangles by Lengths

## Table: Triangles

| Column Name | Type |
| ----------- | ---- |
| A           | int  |
| B           | int  |
| C           | int  |

### Notes

- `(A, B, C)` is the **primary key**.
- Each row represents the **lengths of the three sides of a triangle**.

---

# Problem

Write a SQL query to determine the **type of triangle** for each row.

The output must classify each row as one of the following:

| Triangle Type  | Description                            |
| -------------- | -------------------------------------- |
| Equilateral    | All three sides are equal              |
| Isosceles      | Exactly two sides are equal            |
| Scalene        | All three sides are different          |
| Not A Triangle | The three sides cannot form a triangle |

---

# Triangle Validity Rule

For three sides to form a valid triangle:

```
A + B > C
A + C > B
B + C > A
```

If any of these conditions fail → **Not A Triangle**.

---

# Example

## Input

### Triangles Table

| A   | B   | C   |
| --- | --- | --- |
| 20  | 20  | 23  |
| 20  | 20  | 20  |
| 20  | 21  | 22  |
| 13  | 14  | 30  |

---

## Output

| triangle_type  |
| -------------- |
| Isosceles      |
| Equilateral    |
| Scalene        |
| Not A Triangle |

---

# Explanation

- **Row 1:** A = B → **Isosceles**
- **Row 2:** A = B = C → **Equilateral**
- **Row 3:** A ≠ B ≠ C → **Scalene**
- **Row 4:** A + B ≤ C → **Not A Triangle**
