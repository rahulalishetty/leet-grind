# 3053. Classifying Triangles by Lengths — Approach

## Approach: Utilizing CASE

This solution uses a `CASE` expression to classify each row based on the three side lengths.

---

## Key Rules

### 1. Equilateral Triangle

All three sides are equal.

```text
A = B = C
```

### 2. Isosceles Triangle

Exactly two sides are equal.

```text
A = B OR A = C OR B = C
```

### 3. Scalene Triangle

All three sides are different.

```text
A != B, A != C, B != C
```

### 4. Not A Triangle

The triangle inequality theorem must hold for a valid triangle:

```text
A + B > C
A + C > B
B + C > A
```

If any of these conditions fail, the three sides cannot form a triangle.

---

## Intuition

The `CASE` statement checks the triangle type in a logical order:

1. **Check whether it is a valid triangle first**
   - If not, classify it as **Not A Triangle**
2. **Check if all sides are equal**
   - Then it is **Equilateral**
3. **Check if any two sides are equal**
   - Then it is **Isosceles**
4. **Otherwise**
   - It must be **Scalene**

The order matters because:

- An equilateral triangle also satisfies the condition “any two sides are equal,” so we must test **Equilateral before Isosceles**.
- An invalid triangle should be identified before any other classification.

---

## SQL Implementation

```sql
SELECT
  CASE
    WHEN A + B <= C OR A + C <= B OR B + C <= A THEN 'Not A Triangle'
    WHEN A = B AND B = C THEN 'Equilateral'
    WHEN A = B OR A = C OR B = C THEN 'Isosceles'
    ELSE 'Scalene'
  END AS triangle_type
FROM Triangles;
```

---

## Explanation of the CASE Logic

### First condition

```sql
WHEN A + B <= C OR A + C <= B OR B + C <= A
```

This checks whether the triangle inequality theorem fails.

If it fails, the result is:

```text
Not A Triangle
```

### Second condition

```sql
WHEN A = B AND B = C
```

If all three sides are equal, the result is:

```text
Equilateral
```

### Third condition

```sql
WHEN A = B OR A = C OR B = C
```

If exactly two sides are equal, the result is:

```text
Isosceles
```

### Final condition

```sql
ELSE 'Scalene'
```

If none of the above conditions apply, then all sides are different and form a valid triangle:

```text
Scalene
```

---

## Key SQL Concept Used

- `CASE` expression for conditional classification
