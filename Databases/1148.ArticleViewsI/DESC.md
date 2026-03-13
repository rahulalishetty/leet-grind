# 1148. Article Views I

## Table: Views

| Column Name | Type |
| ----------- | ---- |
| article_id  | int  |
| author_id   | int  |
| viewer_id   | int  |
| view_date   | date |

Notes:

- This table **does not have a primary key**.
- The table **may contain duplicate rows**.
- Each row indicates that a **viewer viewed an article** written by an **author** on a specific date.
- If `author_id = viewer_id`, it means **the author viewed their own article**.

---

# Problem

Write a SQL query to find all **authors who viewed at least one of their own articles**.

Return the result table:

- with a single column named **`id`**
- sorted by **id in ascending order**

---

# Example

## Input

### Views table

| article_id | author_id | viewer_id | view_date  |
| ---------- | --------- | --------- | ---------- |
| 1          | 3         | 5         | 2019-08-01 |
| 1          | 3         | 6         | 2019-08-02 |
| 2          | 7         | 7         | 2019-08-01 |
| 2          | 7         | 6         | 2019-08-02 |
| 4          | 7         | 1         | 2019-07-22 |
| 3          | 4         | 4         | 2019-07-21 |
| 3          | 4         | 4         | 2019-07-21 |

---

# Output

| id  |
| --- |
| 4   |
| 7   |

---

# Explanation

We look for rows where:

```
author_id = viewer_id
```

These rows indicate that the **author viewed their own article**.

From the table:

- `author_id = 7` viewed article **2**
- `author_id = 4` viewed article **3**

Even though there are duplicate rows for `(article_id=3, author_id=4)`, we only return **unique author IDs**.

The final result is sorted in **ascending order**:

```
4
7
```
