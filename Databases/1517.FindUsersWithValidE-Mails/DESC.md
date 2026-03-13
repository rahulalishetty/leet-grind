# 1517. Find Users With Valid E-Mails

## Table: Users

| Column Name | Type    |
| ----------- | ------- |
| user_id     | int     |
| name        | varchar |
| mail        | varchar |

Notes:

- `user_id` is the **primary key**.
- The table stores information about users registered on a website.
- Some emails in the table are **invalid**.

---

# Problem

Write a SQL query to find users who have **valid emails**.

A valid email consists of:

## Prefix Name

The prefix (before `@`) must:

- Start with a **letter**
- May contain:
  - Letters (A–Z, a–z)
  - Digits (0–9)
  - Underscore `_`
  - Period `.`
  - Dash `-`

## Domain

The domain must be exactly:

```
@leetcode.com
```

Return the result table **in any order**.

---

# Example

## Input

### Users table

| user_id | name      | mail                    |
| ------- | --------- | ----------------------- |
| 1       | Winston   | winston@leetcode.com    |
| 2       | Jonathan  | jonathanisgreat         |
| 3       | Annabelle | bella-@leetcode.com     |
| 4       | Sally     | sally.come@leetcode.com |
| 5       | Marwan    | quarz#2020@leetcode.com |
| 6       | David     | david69@gmail.com       |
| 7       | Shapiro   | .shapo@leetcode.com     |

---

# Output

| user_id | name      | mail                    |
| ------- | --------- | ----------------------- |
| 1       | Winston   | winston@leetcode.com    |
| 3       | Annabelle | bella-@leetcode.com     |
| 4       | Sally     | sally.come@leetcode.com |

---

# Explanation

- **User 1** → valid email.
- **User 2** → missing domain → invalid.
- **User 3** → valid email.
- **User 4** → valid email.
- **User 5** → contains `#` which is not allowed.
- **User 6** → domain is not `leetcode.com`.
- **User 7** → prefix starts with `.` which is invalid.
