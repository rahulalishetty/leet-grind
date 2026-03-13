# 3059. Find All Unique Email Domains

## Table: Emails

| Column Name | Type    |
| ----------- | ------- |
| id          | int     |
| email       | varchar |

### Notes

- `id` is the **primary key**.
- Each row contains a **single email address**.
- Emails **do not contain uppercase letters**.

---

# Problem

Write a SQL query to:

1. Extract **email domains** from the email column.
2. Consider **only domains ending with `.com`**.
3. Count the **number of individuals associated with each domain**.
4. Return **unique domains with their counts**.

---

# Output Requirements

Return the following columns:

| email_domain | count |

The result table must be **ordered by `email_domain` in ascending order**.

---

# Example

## Input

### Emails Table

| id  | email                 |
| --- | --------------------- |
| 336 | hwkiy@test.edu        |
| 489 | adcmaf@outlook.com    |
| 449 | vrzmwyum@yahoo.com    |
| 95  | tof@test.edu          |
| 320 | jxhbagkpm@example.org |
| 411 | zxcf@outlook.com      |

---

## Output

| email_domain | count |
| ------------ | ----- |
| outlook.com  | 2     |
| yahoo.com    | 1     |

---

# Explanation

- Valid `.com` domains:
  - **outlook.com**
  - **yahoo.com**

Counts:

- `outlook.com` → 2 users
- `yahoo.com` → 1 user

Domains like `test.edu` and `example.org` are ignored because they **do not end with `.com`**.
