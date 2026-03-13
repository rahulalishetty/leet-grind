# 1355. Activity Participants

## Tables

### Friends

| Column Name | Type    |
| ----------- | ------- |
| id          | int     |
| name        | varchar |
| activity    | varchar |

Notes:

- `id` is the **primary key** of the table.
- `name` is the name of the friend.
- `activity` is the activity the friend participates in.

---

### Activities

| Column Name | Type    |
| ----------- | ------- |
| id          | int     |
| name        | varchar |

Notes:

- `id` is the **primary key** of the table.
- `name` is the name of the activity.

---

# Problem

Find the **names of all activities** that have **neither the maximum nor the minimum number of participants**.

Important details:

- Each activity listed in `Activities` is performed by at least one person in the `Friends` table.
- The result should contain activities whose participant count is **strictly between** the minimum and maximum counts.
- The result table can be returned **in any order**.

---

# Example

## Input

### Friends Table

| id  | name        | activity     |
| --- | ----------- | ------------ |
| 1   | Jonathan D. | Eating       |
| 2   | Jade W.     | Singing      |
| 3   | Victor J.   | Singing      |
| 4   | Elvis Q.    | Eating       |
| 5   | Daniel A.   | Eating       |
| 6   | Bob B.      | Horse Riding |

---

### Activities Table

| id  | name         |
| --- | ------------ |
| 1   | Eating       |
| 2   | Singing      |
| 3   | Horse Riding |

---

# Output

| activity |
| -------- |
| Singing  |

---

# Explanation

Participant counts per activity:

| activity     | participants |
| ------------ | ------------ |
| Eating       | 3            |
| Singing      | 2            |
| Horse Riding | 1            |

- **Eating** → 3 participants → **maximum**
- **Horse Riding** → 1 participant → **minimum**
- **Singing** → 2 participants → **between min and max**

Therefore, the result is:

| activity |
| -------- |
| Singing  |
