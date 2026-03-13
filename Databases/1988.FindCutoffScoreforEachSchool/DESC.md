# 1988. Find Cutoff Score for Each School

## Table: Schools

| Column Name | Type |
| ----------- | ---- |
| school_id   | int  |
| capacity    | int  |

**Notes:**

- `school_id` contains **unique values**.
- `capacity` represents the **maximum number of students the school can accept**.

---

## Table: Exam

| Column Name   | Type |
| ------------- | ---- |
| score         | int  |
| student_count | int  |

**Notes:**

- `score` contains **unique values**.
- `student_count` represents the number of students who scored **at least that score** in the exam.

### Logical Property of the Data

The data is **monotonic**:

If:

```
score_i > score_j
```

then:

```
student_count_i ≤ student_count_j
```

Meaning:

- Higher scores correspond to **fewer or equal students**.

---

# Problem

Every year, each school announces a **minimum score requirement** for students to apply.

The school chooses the score based on these rules:

1. Even if **every student meeting the requirement applies**, the school must be able to **accept all of them**.
2. The school wants to **maximize the number of students who can apply**.
3. The score must be a **value present in the `Exam` table**.

---

# Task

For each school:

- Determine the **minimum score requirement**.

Additional rules:

- If multiple scores satisfy the conditions, choose the **smallest score**.
- If the given data is **not enough to determine a valid score**, return **-1**.

The result may be returned **in any order**.

---

# Example

## Input

### Schools Table

| school_id | capacity |
| --------- | -------- |
| 11        | 151      |
| 5         | 48       |
| 9         | 9        |
| 10        | 99       |

---

### Exam Table

| score | student_count |
| ----- | ------------- |
| 975   | 10            |
| 966   | 60            |
| 844   | 76            |
| 749   | 76            |
| 744   | 100           |

---

# Output

| school_id | score |
| --------- | ----- |
| 5         | 975   |
| 9         | -1    |
| 10        | 749   |
| 11        | 744   |

---

# Explanation

## School 5

Capacity:

```
48
```

Check possible scores:

| score | student_count |
| ----- | ------------- |
| 975   | 10            |

Since:

```
10 ≤ 48
```

The school can accept all students.

Thus:

```
score = 975
```

---

## School 10

Capacity:

```
99
```

Possible scores:

| score | student_count |
| ----- | ------------- |
| 844   | 76            |
| 749   | 76            |

Both satisfy:

```
76 ≤ 99
```

Since multiple scores are valid, we choose the **smallest score**:

```
749
```

---

## School 11

Capacity:

```
151
```

Possible scores:

| score | student_count |
| ----- | ------------- |
| 744   | 100           |

Since:

```
100 ≤ 151
```

This score works.

Thus:

```
744
```

---

## School 9

Capacity:

```
9
```

Smallest student_count in the table:

| score | student_count |
| ----- | ------------- |
| 975   | 10            |

But:

```
10 > 9
```

This already exceeds capacity.

Because the table does **not provide information about higher scores**, we cannot determine whether a higher score would reduce the number of applicants.

Therefore:

```
score = -1
```
