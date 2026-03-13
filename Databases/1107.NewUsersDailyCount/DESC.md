# 1107. New Users Daily Count

## Table: Traffic

| Column Name   | Type |
| ------------- | ---- |
| user_id       | int  |
| activity      | enum |
| activity_date | date |

- The table **may contain duplicate rows**.
- `activity` is an ENUM with the following values:

```
('login', 'logout', 'jobs', 'groups', 'homepage')
```

---

# Problem

Report, for every date within **at most 90 days from today**, the number of users that **logged in for the first time on that date**.

Assume:

```
today = 2019-06-30
```

Important rules:

- Only consider **login activities**.
- Count a user **only on the date of their first login**.
- Ignore users whose first login happened **more than 90 days before today**.
- Only include rows where the **user count is greater than 0**.

---

# Output Columns

| Column     |
| ---------- |
| login_date |
| user_count |

- `login_date` → the date when users logged in for the first time.
- `user_count` → number of users whose **first login occurred on that date**.

The result can be returned **in any order**.

---

# Example

## Input

### Traffic Table

| user_id | activity | activity_date |
| ------- | -------- | ------------- |
| 1       | login    | 2019-05-01    |
| 1       | homepage | 2019-05-01    |
| 1       | logout   | 2019-05-01    |
| 2       | login    | 2019-06-21    |
| 2       | logout   | 2019-06-21    |
| 3       | login    | 2019-01-01    |
| 3       | jobs     | 2019-01-01    |
| 3       | logout   | 2019-01-01    |
| 4       | login    | 2019-06-21    |
| 4       | groups   | 2019-06-21    |
| 4       | logout   | 2019-06-21    |
| 5       | login    | 2019-03-01    |
| 5       | logout   | 2019-03-01    |
| 5       | login    | 2019-06-21    |
| 5       | logout   | 2019-06-21    |

---

# Output

| login_date | user_count |
| ---------- | ---------- |
| 2019-05-01 | 1          |
| 2019-06-21 | 2          |

---

# Explanation

We count **only the first login of each user**.

### User 1

First login:

```
2019-05-01
```

Counted under:

```
2019-05-01
```

---

### User 2

First login:

```
2019-06-21
```

Counted under:

```
2019-06-21
```

---

### User 3

First login:

```
2019-01-01
```

This date is **more than 90 days before 2019‑06‑30**, so this user is **ignored**.

---

### User 4

First login:

```
2019-06-21
```

Counted under:

```
2019-06-21
```

---

### User 5

First login:

```
2019-03-01
```

Even though this user logged in again on **2019‑06‑21**, we only consider the **first login**, which was **2019‑03‑01**.

Since that date is **older than 90 days**, the user is **not counted**.

---

# Final Counts

| login_date | users     |
| ---------- | --------- |
| 2019-05-01 | user 1    |
| 2019-06-21 | users 2,4 |

Which gives:

| login_date | user_count |
| ---------- | ---------- |
| 2019-05-01 | 1          |
| 2019-06-21 | 2          |
