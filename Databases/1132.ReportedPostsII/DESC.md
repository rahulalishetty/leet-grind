# 1132. Reported Posts II

## Tables

### Actions

| Column Name | Type    |
| ----------- | ------- |
| user_id     | int     |
| post_id     | int     |
| action_date | date    |
| action      | enum    |
| extra       | varchar |

Notes:

- This table may have duplicate rows.
- `action` ENUM values: ('view', 'like', 'reaction', 'comment', 'report', 'share').
- `extra` may contain optional information such as the reason for a report or reaction type.

---

### Removals

| Column Name | Type |
| ----------- | ---- |
| post_id     | int  |
| remove_date | date |

Notes:

- `post_id` is the primary key.
- Each row indicates that a post was removed after reports or admin review.

---

## Problem

Write a SQL query to find the **average daily percentage of posts that were removed after being reported as spam**.

Requirements:

- Only consider posts reported with `extra = 'spam'`.
- Compute the percentage **per day**.
- Then compute the **average of those daily percentages**.
- Round the final result to **2 decimal places**.

The output should contain a single column:

| average_daily_percent |

---

## Example

### Input

#### Actions

| user_id | post_id | action_date | action | extra  |
| ------- | ------- | ----------- | ------ | ------ |
| 1       | 1       | 2019-07-01  | view   | null   |
| 1       | 1       | 2019-07-01  | like   | null   |
| 1       | 1       | 2019-07-01  | share  | null   |
| 2       | 2       | 2019-07-04  | view   | null   |
| 2       | 2       | 2019-07-04  | report | spam   |
| 3       | 4       | 2019-07-04  | view   | null   |
| 3       | 4       | 2019-07-04  | report | spam   |
| 4       | 3       | 2019-07-02  | view   | null   |
| 4       | 3       | 2019-07-02  | report | spam   |
| 5       | 2       | 2019-07-03  | view   | null   |
| 5       | 2       | 2019-07-03  | report | racism |
| 5       | 5       | 2019-07-03  | view   | null   |
| 5       | 5       | 2019-07-03  | report | racism |

#### Removals

| post_id | remove_date |
| ------- | ----------- |
| 2       | 2019-07-20  |
| 3       | 2019-07-18  |

---

## Output

| average_daily_percent |
| --------------------- |
| 75.00                 |

---

## Explanation

Daily percentages:

- **2019-07-04**
  - Spam reports: 2 posts
  - Removed posts: 1
  - Percentage = 50%

- **2019-07-02**
  - Spam reports: 1 post
  - Removed posts: 1
  - Percentage = 100%

Other days have **no spam reports**, so they are ignored.

Average:

(50 + 100) / 2 = **75.00**
