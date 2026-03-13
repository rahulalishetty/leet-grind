# 626. Exchange Seats

## Table: Seat

| Column Name | Type    |
| ----------- | ------- |
| id          | int     |
| student     | varchar |

- `id` is the **primary key** of the table.
- The ID sequence **starts from 1** and increments continuously.
- Each row represents a **student and their seat number**.

---

# Problem

Swap the **seat id of every two consecutive students**.

Rules:

- Seats are swapped **pairwise**:

```
1 <-> 2
3 <-> 4
5 <-> 6
...
```

- If the total number of students is **odd**, the **last seat remains unchanged**.

---

# Output Requirements

Return the result table:

- ordered by `id` in **ascending order**.

---

# Example

## Input

### Seat

| id  | student |
| --- | ------- |
| 1   | Abbot   |
| 2   | Doris   |
| 3   | Emerson |
| 4   | Green   |
| 5   | Jeames  |

---

# Output

| id  | student |
| --- | ------- |
| 1   | Doris   |
| 2   | Abbot   |
| 3   | Green   |
| 4   | Emerson |
| 5   | Jeames  |

---

# Explanation

Seats are swapped in pairs.

### Pair 1

```
Seat 1 <-> Seat 2
```

Before:

| id  | student |
| --- | ------- |
| 1   | Abbot   |
| 2   | Doris   |

After:

| id  | student |
| --- | ------- |
| 1   | Doris   |
| 2   | Abbot   |

---

### Pair 2

```
Seat 3 <-> Seat 4
```

Before:

| id  | student |
| --- | ------- |
| 3   | Emerson |
| 4   | Green   |

After:

| id  | student |
| --- | ------- |
| 3   | Green   |
| 4   | Emerson |

---

### Last Student

Seat:

| id  | student |
| --- | ------- |
| 5   | Jeames  |

Since the total number of students is **odd**, the last student **remains unchanged**.

---

# Final Result

| id  | student |
| --- | ------- |
| 1   | Doris   |
| 2   | Abbot   |
| 3   | Green   |
| 4   | Emerson |
| 5   | Jeames  |
