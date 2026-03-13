# 618. Students Report By Geography

## Table: Student

| Column Name | Type    |
| ----------- | ------- |
| name        | varchar |
| continent   | varchar |

- The table may contain **duplicate rows**.
- Each row represents a **student name** and the **continent they come from**.

---

## Problem

A school has students from three continents:

- **Asia**
- **Europe**
- **America**

Write a SQL query to **pivot the continent column** so that:

- Student names appear under their corresponding continent column.
- Names inside each continent column are **sorted alphabetically**.

The output table must have the following columns:

```
America | Asia | Europe
```

---

## Important Constraints

- Names in each continent column must be **sorted alphabetically**.
- The number of students from **America is guaranteed to be greater than or equal to** the number of students from **Asia or Europe**.

---

## Example

### Input

**Student table**

| name   | continent |
| ------ | --------- |
| Jane   | America   |
| Pascal | Europe    |
| Xi     | Asia      |
| Jack   | America   |

---

### Output

| America | Asia | Europe |
| ------- | ---- | ------ |
| Jack    | Xi   | Pascal |
| Jane    | null | null   |

---

## Explanation

First, group students by continent and sort each group alphabetically.

### America

```
Jack
Jane
```

### Asia

```
Xi
```

### Europe

```
Pascal
```

Then place the names in rows aligned by their alphabetical order index.

Row 1:

```
Jack | Xi | Pascal
```

Row 2:

```
Jane | null | null
```

If one continent has fewer students, its column is filled with **NULL** values.

---

## Follow-up

If it is **unknown which continent has the most students**, the solution must dynamically handle varying numbers of students per continent and still produce the pivoted result correctly.
