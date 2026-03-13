# 1527. Patients With a Condition — Approaches

## Approach 1: Using Regular Expressions (Word Boundaries or Spaces)

### Intuition

Regular expressions provide a flexible way to search for patterns within text columns.

In this problem, we want to identify patients who have **Type I Diabetes**, which is represented by condition codes starting with:

```
DIAB1
```

The `conditions` column contains **space-separated condition codes**. Therefore, the pattern may appear:

1. At the **start of the string**
2. After a **space**

We must ensure we match **actual condition codes**, not substrings embedded inside other words.

To handle both cases, we use the SQL `REGEXP` operator with this pattern:

```
(^|[[:space:]])DIAB1
```

Explanation:

| Pattern       | Meaning                            |
| ------------- | ---------------------------------- | ------------------------------ |
| `^`           | Start of the string                |
| `[[:space:]]` | Any whitespace character           |
| `(^           | [[:space:]])`                      | Match start of string or space |
| `DIAB1`       | Prefix identifying Type I Diabetes |

This ensures we correctly match codes starting with **DIAB1** whether they appear at the beginning or after a space.

---

### Implementation

```sql
SELECT patient_id, patient_name, conditions
FROM Patients
WHERE conditions REGEXP '(^|[[:space:]])DIAB1';
```

---

# Approach 2: Without Using Regular Expressions

If you are not comfortable using regular expressions, the problem can also be solved using standard string matching.

### Intuition

There are **two valid ways** that a Type I Diabetes condition may appear in the string:

### Case 1 — Condition starts with `DIAB1`

Example:

```
DIAB100 MYOP
```

Query pattern:

```
conditions LIKE 'DIAB1%'
```

---

### Case 2 — Condition appears after a space

Example:

```
ACNE DIAB100
```

Query pattern:

```
conditions LIKE '% DIAB1%'
```

---

Since these are the only possible positions where the condition code can appear, we simply combine both checks.

---

### Implementation

```sql
SELECT patient_id, patient_name, conditions
FROM Patients
WHERE conditions LIKE 'DIAB1%'
   OR conditions LIKE '% DIAB1%';
```

---

# Result Example

| patient_id | patient_name | conditions   |
| ---------- | ------------ | ------------ |
| 3          | Bob          | DIAB100 MYOP |
| 4          | George       | ACNE DIAB100 |

Both Bob and George have condition codes that begin with **DIAB1**, indicating **Type I Diabetes**.
