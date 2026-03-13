# 1495. Friendly Movies Streamed Last Month

## Table: TVProgram

| Column Name  | Type    |
| ------------ | ------- |
| program_date | date    |
| content_id   | int     |
| channel      | varchar |

Notes:

- `(program_date, content_id)` is the **primary key**.
- This table stores information about **TV programs broadcast on different channels**.
- `content_id` identifies the program.

---

## Table: Content

| Column Name  | Type    |
| ------------ | ------- |
| content_id   | varchar |
| title        | varchar |
| Kids_content | enum    |
| content_type | varchar |

Notes:

- `content_id` is the **primary key**.
- `Kids_content` is an ENUM with values:

```
'Y' → Kid-friendly content
'N' → Not kid-friendly
```

- `content_type` specifies the category such as **Movies**, **Series**, etc.

---

# Problem

Write a SQL query to report the **distinct titles of kid-friendly movies streamed in June 2020**.

Conditions:

1. The content must be **kid-friendly** → `Kids_content = 'Y'`
2. The content must be a **movie** → `content_type = 'Movies'`
3. The program must be **streamed during June 2020**
4. Return **distinct movie titles**.

The result table should contain:

| Column | Description        |
| ------ | ------------------ |
| title  | Title of the movie |

The result may be returned **in any order**.

---

# Example

## Input

### TVProgram table

| program_date     | content_id | channel    |
| ---------------- | ---------- | ---------- |
| 2020-06-10 08:00 | 1          | LC-Channel |
| 2020-05-11 12:00 | 2          | LC-Channel |
| 2020-05-12 12:00 | 3          | LC-Channel |
| 2020-05-13 14:00 | 4          | Disney Ch  |
| 2020-06-18 14:00 | 4          | Disney Ch  |
| 2020-07-15 16:00 | 5          | Disney Ch  |

### Content table

| content_id | title          | Kids_content | content_type |
| ---------- | -------------- | ------------ | ------------ |
| 1          | Leetcode Movie | N            | Movies       |
| 2          | Alg. for Kids  | Y            | Series       |
| 3          | Database Sols  | N            | Series       |
| 4          | Aladdin        | Y            | Movies       |
| 5          | Cinderella     | Y            | Movies       |

---

# Output

| title   |
| ------- |
| Aladdin |

---

# Explanation

- **Leetcode Movie**
  - Not kid-friendly → excluded.

- **Alg. for Kids**
  - Kid-friendly but **not a movie** → excluded.

- **Database Sols**
  - Not kid-friendly → excluded.

- **Aladdin**
  - Kid-friendly ✔
  - Movie ✔
  - Streamed in **June 2020** ✔
  - Included in result.

- **Cinderella**
  - Kid-friendly movie but **not streamed in June 2020** → excluded.
