# 1149. Article Views II

## Table: Views

| Column Name | Type |
| ----------- | ---- |
| article_id  | int  |
| author_id   | int  |
| viewer_id   | int  |
| view_date   | date |

Notes:

- This table may contain **duplicate rows**.
- Each row indicates that a **viewer viewed an article** written by an author on a given date.
- If `author_id = viewer_id`, it means the **author viewed their own article**.

---

## Problem

Write a SQL query to **find all people who viewed more than one article on the same date**.

Requirements:

- A person is identified by `viewer_id`.
- The same person must have viewed **more than one article** on the **same date**.
- The result must contain only the **viewer id**.

Return the result:

- Sorted by **id in ascending order**.

---

## Example

### Input

#### Views Table

| article_id | author_id | viewer_id | view_date  |
| ---------- | --------- | --------- | ---------- |
| 1          | 3         | 5         | 2019-08-01 |
| 3          | 4         | 5         | 2019-08-01 |
| 1          | 3         | 6         | 2019-08-02 |
| 2          | 7         | 7         | 2019-08-01 |
| 2          | 7         | 6         | 2019-08-02 |
| 4          | 7         | 1         | 2019-07-22 |
| 3          | 4         | 4         | 2019-07-21 |
| 3          | 4         | 4         | 2019-07-21 |

---

### Output

| id  |
| --- |
| 5   |
| 6   |

---

## Explanation

- **Viewer 5**
  - Viewed article **1** and **3** on **2019-08-01**
  - Since they viewed more than one article on the same day, they qualify.

- **Viewer 6**
  - Viewed article **1** and **2** on **2019-08-02**
  - Also qualifies.

- **Viewer 4**
  - Has duplicate records for the same article on the same date.
  - But they viewed **only one article**, so they do **not** qualify.

- **Viewer 7** and **Viewer 1**
  - Viewed only one article per date.
  - Do not qualify.

---
