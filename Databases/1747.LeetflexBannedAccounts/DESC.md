# 1747. Leetflex Banned Accounts

## Table: LogInfo

| Column Name | Type     |
| ----------- | -------- |
| account_id  | int      |
| ip_address  | int      |
| login       | datetime |
| logout      | datetime |

**Notes:**

- This table may contain **duplicate rows**.
- Each row represents a **login session** for an account.
- `login` indicates when the session started.
- `logout` indicates when the session ended.
- It is guaranteed that **logout occurs after login**.
- `ip_address` represents the IP address used during that session.

---

# Problem

Leetflex wants to identify **accounts that should be banned**.

An account must be banned **if it was logged in at the same time from two different IP addresses**.

In other words:

- If two sessions of the **same account overlap in time**
- And they come from **different IP addresses**
- Then that account should be banned.

Return the result table containing:

```
account_id
```

The result can be returned **in any order**.

---

# Example

## Input

### LogInfo Table

| account_id | ip_address | login               | logout              |
| ---------- | ---------- | ------------------- | ------------------- |
| 1          | 1          | 2021-02-01 09:00:00 | 2021-02-01 09:30:00 |
| 1          | 2          | 2021-02-01 08:00:00 | 2021-02-01 11:30:00 |
| 2          | 6          | 2021-02-01 20:30:00 | 2021-02-01 22:00:00 |
| 2          | 7          | 2021-02-02 20:30:00 | 2021-02-02 22:00:00 |
| 3          | 9          | 2021-02-01 16:00:00 | 2021-02-01 16:59:59 |
| 3          | 13         | 2021-02-01 17:00:00 | 2021-02-01 17:59:59 |
| 4          | 10         | 2021-02-01 16:00:00 | 2021-02-01 17:00:00 |
| 4          | 11         | 2021-02-01 17:00:00 | 2021-02-01 17:59:59 |

---

# Output

| account_id |
| ---------- |
| 1          |
| 4          |

---

# Explanation

## Account 1

Sessions:

| IP  | Login | Logout |
| --- | ----- | ------ |
| 1   | 09:00 | 09:30  |
| 2   | 08:00 | 11:30  |

The second session covers a longer window.

The interval:

```
09:00 → 09:30
```

exists simultaneously for both IPs.

Therefore the account is **logged in from two different IPs at the same time**.

Result:

```
Account 1 is banned
```

---

## Account 2

Sessions:

| IP  | Login        | Logout       |
| --- | ------------ | ------------ |
| 6   | Feb‑01 20:30 | Feb‑01 22:00 |
| 7   | Feb‑02 20:30 | Feb‑02 22:00 |

These sessions occur **on different days**.

There is **no overlapping time window**.

Result:

```
Account 2 is NOT banned
```

---

## Account 3

Sessions:

| IP  | Login | Logout   |
| --- | ----- | -------- |
| 9   | 16:00 | 16:59:59 |
| 13  | 17:00 | 17:59:59 |

The first session ends **before** the second session starts.

They do **not intersect**.

Result:

```
Account 3 is NOT banned
```

---

## Account 4

Sessions:

| IP  | Login | Logout |
| --- | ----- | ------ |
| 10  | 16:00 | 17:00  |
| 11  | 17:00 | 17:59  |

At exactly:

```
17:00:00
```

both sessions are active momentarily.

Thus the account is simultaneously logged in from two IPs.

Result:

```
Account 4 is banned
```

---

# Key Idea

An account should be banned **if two login sessions overlap** and have **different IP addresses**.

Two sessions overlap if:

```
login1 <= logout2
AND
login2 <= logout1
```

while also satisfying:

```
ip_address1 != ip_address2
```
