# 602. Friend Requests II: Who Has the Most Friends

## Table: RequestAccepted

| Column Name  | Type |
| ------------ | ---- |
| requester_id | int  |
| accepter_id  | int  |
| accept_date  | date |

- `(requester_id, accepter_id)` is the **primary key** (combination of columns with unique values).
- Each row represents a **friend request that was accepted**.
- `requester_id` is the user who sent the request.
- `accepter_id` is the user who accepted the request.
- `accept_date` is the date when the request was accepted.

---

## Problem

Write a SQL query to find:

- The **person who has the most friends**
- The **number of friends** they have

The result should include:

| id | num |

Where:

- `id` → the user id
- `num` → the number of friends that user has

---

## Constraints

- Test cases guarantee that **only one person has the maximum number of friends**.

---

## Example

### Input

#### RequestAccepted table

| requester_id | accepter_id | accept_date |
| ------------ | ----------- | ----------- |
| 1            | 2           | 2016/06/03  |
| 1            | 3           | 2016/06/08  |
| 2            | 3           | 2016/06/08  |
| 3            | 4           | 2016/06/09  |

---

### Output

| id  | num |
| --- | --- |
| 3   | 3   |

---

## Explanation

Friend relationships formed:

- `(1,2)`
- `(1,3)`
- `(2,3)`
- `(3,4)`

Now count friends per person:

- **1** → friends with `{2,3}` → total **2**
- **2** → friends with `{1,3}` → total **2**
- **3** → friends with `{1,2,4}` → total **3**
- **4** → friends with `{3}` → total **1**

User **3** has the **largest number of friends (3)**.

---

## Follow‑up

In real-world scenarios, **multiple people could have the same highest number of friends**.

Can you modify the query to **return all users tied for the maximum number of friends**?
