# 620. Not Boring Movies

## Table: Cinema

| Column Name | Type    |
| ----------- | ------- |
| id          | int     |
| movie       | varchar |
| description | varchar |
| rating      | float   |

### Notes

- `id` is the **primary key** (unique for each movie).
- Each row stores information about a movie.
- `rating` is a **float with 2 decimal places** in the range **[0, 10]**.

---

# Problem

Write a SQL query to **report movies** that satisfy the following conditions:

1. The movie **ID is odd-numbered**.
2. The movie **description is not `"boring"`**.

The result must be **sorted by `rating` in descending order**.

---

# Output Format

The output table should include the following columns:

| id | movie | description | rating |

---

# Example

## Input

### Cinema Table

| id  | movie      | description | rating |
| --- | ---------- | ----------- | ------ |
| 1   | War        | great 3D    | 8.9    |
| 2   | Science    | fiction     | 8.5    |
| 3   | irish      | boring      | 6.2    |
| 4   | Ice song   | Fantacy     | 8.6    |
| 5   | House card | Interesting | 9.1    |

---

## Explanation

Movies with **odd IDs**:

```
1, 3, 5
```

Check description:

- Movie **1** → "great 3D" → valid
- Movie **3** → "boring" → excluded
- Movie **5** → "Interesting" → valid

Remaining movies:

```
1, 5
```

Sort by rating descending:

```
5 (9.1)
1 (8.9)
```

---

# Output

| id  | movie      | description | rating |
| --- | ---------- | ----------- | ------ |
| 5   | House card | Interesting | 9.1    |
| 1   | War        | great 3D    | 8.9    |

---
