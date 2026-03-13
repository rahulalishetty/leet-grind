# 584. Find Customer Referee

## Problem

Table: **Customer**

| Column Name | Type    |
| ----------- | ------- |
| id          | int     |
| name        | varchar |
| referee_id  | int     |

- `id` is the **primary key**.
- `referee_id` indicates which customer referred the current customer.

## Task

Find the names of customers who satisfy **either** of the following:

1. They were referred by a customer whose `id != 2`
2. They were **not referred by anyone** (`referee_id IS NULL`)

Return the result in **any order**.

---

## Example

### Input

Customer table

| id  | name | referee_id |
| --- | ---- | ---------- |
| 1   | Will | NULL       |
| 2   | Jane | NULL       |
| 3   | Alex | 2          |
| 4   | Bill | NULL       |
| 5   | Zack | 1          |
| 6   | Mark | 2          |

### Output

| name |
| ---- |
| Will |
| Jane |
| Bill |
| Zack |

---
