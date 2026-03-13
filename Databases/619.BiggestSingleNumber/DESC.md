# 619. Biggest Single Number

## Table: MyNumbers

| Column Name | Type |
| ----------- | ---- |
| num         | int  |

### Notes

- This table **may contain duplicate values**.
- There is **no primary key**.
- Each row contains a single integer.

---

# Problem

A **single number** is defined as a number that appears **exactly once** in the `MyNumbers` table.

Your task is to:

- Find the **largest single number** in the table.
- If **no single number exists**, return **NULL**.

---

# Output Format

The result should contain one column:

| num                   |
| --------------------- |
| largest single number |

If none exists:

| num  |
| ---- |
| NULL |

---

# Example 1

## Input

### MyNumbers Table

| num |
| --- |
| 8   |
| 8   |
| 3   |
| 3   |
| 1   |
| 4   |
| 5   |
| 6   |

## Explanation

Numbers appearing exactly once:

```
1, 4, 5, 6
```

The **largest** among them is:

```
6
```

## Output

| num |
| --- |
| 6   |

---

# Example 2

## Input

### MyNumbers Table

| num |
| --- |
| 8   |
| 8   |
| 7   |
| 7   |
| 3   |
| 3   |
| 3   |

## Explanation

All numbers appear **more than once**, therefore **no single numbers exist**.

## Output

| num  |
| ---- |
| NULL |

---
