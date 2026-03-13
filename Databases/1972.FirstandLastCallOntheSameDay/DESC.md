# 1972. First and Last Call On the Same Day

## Table: Calls

| Column Name  | Type     |
| ------------ | -------- |
| caller_id    | int      |
| recipient_id | int      |
| call_time    | datetime |

- `(caller_id, recipient_id, call_time)` is the **primary key**.
- Each row records a **phone call** between two users at a specific time.

---

## Problem

Write a SQL query to **report the IDs of the users whose first and last calls on any day were with the same person**.

### Important Rules

- Calls count **regardless of whether the user was the caller or the recipient**.
- The **first call** and **last call** must occur **on the same day**.
- If both the first and last call of that day were with the **same person**, the user should appear in the result.

Return the result table **in any order**.

---

## Example

### Input

#### Calls Table

| caller_id | recipient_id | call_time           |
| --------- | ------------ | ------------------- |
| 8         | 4            | 2021-08-24 17:46:07 |
| 4         | 8            | 2021-08-24 19:57:13 |
| 5         | 1            | 2021-08-11 05:28:44 |
| 8         | 3            | 2021-08-17 04:04:15 |
| 11        | 3            | 2021-08-17 13:07:00 |
| 8         | 11           | 2021-08-17 22:22:22 |

---

### Output

| user_id |
| ------- |
| 1       |
| 4       |
| 5       |
| 8       |

---

## Explanation

### Case 1 — Date: **2021‑08‑24**

Calls:

```
8 -> 4 at 17:46
4 -> 8 at 19:57
```

- For **user 8**:
  - First call → with **4**
  - Last call → with **4**
  - Same person → **include user 8**

- For **user 4**:
  - First call → with **8**
  - Last call → with **8**
  - Same person → **include user 4**

---

### Case 2 — Date: **2021‑08‑11**

Calls:

```
5 -> 1 at 05:28
```

- This is the **only call of the day** for both users.

Therefore:

- For **user 5**:
  - First call = last call → with **1**
  - Include **5**

- For **user 1**:
  - First call = last call → with **5**
  - Include **1**

---

### Case 3 — Date: **2021‑08‑17**

Calls:

```
8 -> 3  at 04:04
11 -> 3 at 13:07
8 -> 11 at 22:22
```

- For **user 8**:
  - First call → with **3**
  - Last call → with **11**
  - Different people → **not included for this day**

- For **user 3**:
  - First call → with **8**
  - Last call → with **11**
  - Different people → **not included**

- For **user 11**:
  - First call → with **3**
  - Last call → with **8**
  - Different people → **not included**

---

## Final Result

Users whose **first and last calls on at least one day were with the same person**:

```
1
4
5
8
```
