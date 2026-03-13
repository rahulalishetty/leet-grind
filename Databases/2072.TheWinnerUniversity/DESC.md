# 2072. The Winner University

## Table: NewYork

| Column Name | Type |
| ----------- | ---- |
| student_id  | int  |
| score       | int  |

### Notes

- `student_id` is the **primary key**.
- Each row represents a **student from New York University** and their exam score.

---

## Table: California

| Column Name | Type |
| ----------- | ---- |
| student_id  | int  |
| score       | int  |

### Notes

- `student_id` is the **primary key**.
- Each row represents a **student from California University** and their exam score.

---

# Problem

There is a competition between **New York University** and **California University**.

Both universities have the **same number of students participating**.

The university that has **more excellent students** wins.

### Definition of an Excellent Student

A student is considered **excellent** if:

```
score >= 90
```

---

# Required Output

Return a single column:

```
winner
```

Possible values:

- `"New York University"` → if New York has more excellent students
- `"California University"` → if California has more excellent students
- `"No Winner"` → if both have the same number of excellent students

---

# Example 1

## Input

### NewYork

| student_id | score |
| ---------- | ----- |
| 1          | 90    |
| 2          | 87    |

### California

| student_id | score |
| ---------- | ----- |
| 2          | 89    |
| 3          | 88    |

## Output

| winner              |
| ------------------- |
| New York University |

### Explanation

- New York → **1 excellent student**
- California → **0 excellent students**

New York wins.

---

# Example 2

## Input

### NewYork

| student_id | score |
| ---------- | ----- |
| 1          | 89    |
| 2          | 88    |

### California

| student_id | score |
| ---------- | ----- |
| 2          | 90    |
| 3          | 87    |

## Output

| winner                |
| --------------------- |
| California University |

### Explanation

- New York → **0 excellent students**
- California → **1 excellent student**

California wins.

---

# Example 3

## Input

### NewYork

| student_id | score |
| ---------- | ----- |
| 1          | 89    |
| 2          | 90    |

### California

| student_id | score |
| ---------- | ----- |
| 2          | 87    |
| 3          | 99    |

## Output

| winner    |
| --------- |
| No Winner |

### Explanation

Both universities have **1 excellent student**, so the competition ends in a **draw**.
