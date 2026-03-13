# 610. Triangle Judgement

## Approach: Using `CASE ... WHEN ...`

## Core idea

For each row in the `Triangle` table, we are given three segment lengths:

- `x`
- `y`
- `z`

We need to decide whether these three lengths can form a valid triangle.

This is a direct application of the **triangle inequality rule**.

---

## Triangle rule

In mathematics, three segments can form a triangle **only if the sum of any two sides is greater than the third side**.

So the following three conditions must all be true:

```sql
x + y > z
x + z > y
y + z > x
```

If all three conditions hold, the answer is:

```text
Yes
```

Otherwise, the answer is:

```text
No
```

---

## Why this rule works

A triangle is possible only when no single side is too long compared to the other two.

For example, if one side is greater than or equal to the sum of the other two, the segments cannot “close” into a triangle.

So this would fail:

```text
13, 15, 30
```

because:

```text
13 + 15 = 28
28 is not greater than 30
```

That means the third side is too long, so these three segments cannot form a triangle.

---

## Why `CASE ... WHEN ...` is a good fit

This problem is a row-by-row decision problem:

- if the triangle conditions are true -> output `"Yes"`
- otherwise -> output `"No"`

That is exactly what `CASE ... WHEN ...` is designed for.

It lets us return different output values depending on whether a condition is true or false.

---

## Final accepted query

```sql
SELECT
    x,
    y,
    z,
    CASE
        WHEN x + y > z AND x + z > y AND y + z > x THEN 'Yes'
        ELSE 'No'
    END AS 'triangle'
FROM
    triangle;
```

---

## Step-by-step explanation

### `SELECT x, y, z`

We return the original three segment lengths.

### `CASE`

We use a conditional expression to classify each row.

### `WHEN x + y > z AND x + z > y AND y + z > x THEN 'Yes'`

This checks the triangle inequality rule.

Only if **all three** conditions are satisfied do we return `"Yes"`.

### `ELSE 'No'`

If even one condition fails, the three lengths cannot form a triangle.

So we return `"No"`.

### `AS 'triangle'`

This names the output column as `triangle`, exactly as required.

---

## Example walkthrough

### Input

| x   | y   | z   |
| --- | --- | --- |
| 13  | 15  | 30  |
| 10  | 20  | 15  |

---

## Row 1: `(13, 15, 30)`

Check the conditions:

```text
13 + 15 > 30  -> 28 > 30 -> false
13 + 30 > 15  -> true
15 + 30 > 13  -> true
```

Since the first condition is false, all three conditions are not satisfied.

So the result is:

```text
No
```

---

## Row 2: `(10, 20, 15)`

Check the conditions:

```text
10 + 20 > 15  -> 30 > 15 -> true
10 + 15 > 20  -> 25 > 20 -> true
20 + 15 > 10  -> 35 > 10 -> true
```

All three conditions are true.

So the result is:

```text
Yes
```

---

## Output

| x   | y   | z   | triangle |
| --- | --- | --- | -------- |
| 13  | 15  | 30  | No       |
| 10  | 20  | 15  | Yes      |

---

## Important observation

The solution mentions another equivalent interpretation:

> the subtraction of any two segments is smaller than the third one

That is also related to triangle validity, but the standard and most direct SQL formulation is the one using sums:

```sql
x + y > z
x + z > y
y + z > x
```

That is the clearest and most common way to write the triangle check.

---

## Why all three conditions are needed

A skeptical question is: do we really need to check all three?

Yes, because each side must be smaller than the sum of the other two.

If you skip one check, you could accidentally allow an invalid triangle.

So the safe and correct condition is:

```sql
x + y > z
AND x + z > y
AND y + z > x
```

---

## Compact version

The same query can be written more compactly:

```sql
SELECT x, y, z,
       CASE
           WHEN x + y > z AND x + z > y AND y + z > x THEN 'Yes'
           ELSE 'No'
       END AS triangle
FROM triangle;
```

This is logically identical.

---

## Why this problem is straightforward

This problem does not require:

- joins
- grouping
- aggregation
- subqueries
- sorting
- window functions

It is simply a conditional check on each row.

That is why `CASE` is enough.

---

## Complexity

Let `n` be the number of rows in the `Triangle` table.

### Time Complexity

```text
O(n)
```

because each row is checked once.

### Space Complexity

```text
O(1)
```

ignoring the output, because no extra structures are required.

---

## Key takeaways

1. Three segments form a triangle only if the sum of any two is greater than the third.
2. This gives three conditions:
   - `x + y > z`
   - `x + z > y`
   - `y + z > x`
3. `CASE ... WHEN ...` is the natural SQL tool for returning `"Yes"` or `"No"`.
4. The final accepted query is:

```sql
SELECT
    x,
    y,
    z,
    CASE
        WHEN x + y > z AND x + z > y AND y + z > x THEN 'Yes'
        ELSE 'No'
    END AS 'triangle'
FROM
    triangle;
```
