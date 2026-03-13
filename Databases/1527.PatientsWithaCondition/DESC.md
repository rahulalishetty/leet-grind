# 1527. Patients With a Condition

## Table: Patients

| Column Name  | Type    |
| ------------ | ------- |
| patient_id   | int     |
| patient_name | varchar |
| conditions   | varchar |

Notes:

- `patient_id` is the **primary key**.
- The `conditions` column may contain **zero or more medical codes separated by spaces**.
- Each row represents a **patient and their medical conditions**.

---

# Problem

Write a SQL query to find patients who have **Type I Diabetes**.

Rules:

- Type I Diabetes conditions always start with the prefix:

```
DIAB1
```

- The condition code may appear anywhere in the `conditions` column.
- Condition codes are **space-separated**.

Return:

| Column       | Description           |
| ------------ | --------------------- |
| patient_id   | ID of the patient     |
| patient_name | Name of the patient   |
| conditions   | The conditions string |

The result can be returned **in any order**.

---

# Example

## Input

### Patients table

| patient_id | patient_name | conditions   |
| ---------- | ------------ | ------------ |
| 1          | Daniel       | YFEV COUGH   |
| 2          | Alice        |              |
| 3          | Bob          | DIAB100 MYOP |
| 4          | George       | ACNE DIAB100 |
| 5          | Alain        | DIAB201      |

---

# Output

| patient_id | patient_name | conditions   |
| ---------- | ------------ | ------------ |
| 3          | Bob          | DIAB100 MYOP |
| 4          | George       | ACNE DIAB100 |

---

# Explanation

- **Bob** has condition `DIAB100`, which starts with `DIAB1`.
- **George** also has `DIAB100`, which starts with `DIAB1`.
- **Alain** has `DIAB201`, which starts with `DIAB2`, so it is **not included**.
