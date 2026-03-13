# 1113. Reported Posts

## Table: Actions

| Column Name | Type    |
| ----------- | ------- |
| user_id     | int     |
| post_id     | int     |
| action_date | date    |
| action      | enum    |
| extra       | varchar |

Notes:

- This table **may contain duplicate rows**.
- The `action` column is an **ENUM** with possible values:

```
('view', 'like', 'reaction', 'comment', 'report', 'share')
```

- The `extra` column contains **optional information** about the action.
  - For example:
    - the **reason for reporting**
    - the **type of reaction**

---

# Problem

Write a SQL query to report the **number of posts reported yesterday for each report reason**.

Assume that **today is `2019-07-05`**.

Therefore:

```
Yesterday = 2019-07-04
```

Only rows that satisfy both conditions should be considered:

- `action = 'report'`
- `action_date = '2019-07-04'`

The result should return:

- `report_reason`
- `report_count`

Return the result table **in any order**.

---

# Example

## Input

### Actions table

| user_id | post_id | action_date | action | extra  |
| ------- | ------- | ----------- | ------ | ------ |
| 1       | 1       | 2019-07-01  | view   | null   |
| 1       | 1       | 2019-07-01  | like   | null   |
| 1       | 1       | 2019-07-01  | share  | null   |
| 2       | 4       | 2019-07-04  | view   | null   |
| 2       | 4       | 2019-07-04  | report | spam   |
| 3       | 4       | 2019-07-04  | view   | null   |
| 3       | 4       | 2019-07-04  | report | spam   |
| 4       | 3       | 2019-07-02  | view   | null   |
| 4       | 3       | 2019-07-02  | report | spam   |
| 5       | 2       | 2019-07-04  | view   | null   |
| 5       | 2       | 2019-07-04  | report | racism |
| 5       | 5       | 2019-07-04  | view   | null   |
| 5       | 5       | 2019-07-04  | report | racism |

---

# Output

| report_reason | report_count |
| ------------- | ------------ |
| spam          | 1            |
| racism        | 2            |

---

# Explanation

We only consider rows where:

```
action = 'report'
AND action_date = '2019-07-04'
```

### Step 1 — Filter reports from yesterday

Relevant rows:

| user_id | post_id | action_date | action | extra  |
| ------- | ------- | ----------- | ------ | ------ |
| 2       | 4       | 2019-07-04  | report | spam   |
| 3       | 4       | 2019-07-04  | report | spam   |
| 5       | 2       | 2019-07-04  | report | racism |
| 5       | 5       | 2019-07-04  | report | racism |

---

### Step 2 — Count **distinct posts** per report reason

Important: multiple users may report the **same post** for the same reason.

For example:

```
post_id = 4
reason = spam
```

was reported twice by different users.

But the question asks:

> number of **posts** reported

Therefore we count **distinct post_id** per reason.

---

### Step 3 — Final counts

**spam**

Posts reported:

```
post_id = 4
```

Distinct posts = **1**

---

**racism**

Posts reported:

```
post_id = 2
post_id = 5
```

Distinct posts = **2**

---

Final result:

| report_reason | report_count |
| ------------- | ------------ |
| spam          | 1            |
| racism        | 2            |
