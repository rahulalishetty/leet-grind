# 597. Friend Requests I: Overall Acceptance Rate

## Table: FriendRequest

| Column Name  | Type |
| ------------ | ---- |
| sender_id    | int  |
| send_to_id   | int  |
| request_date | date |

- This table **may contain duplicates** (there is **no primary key**).
- Each row records:
  - the user who **sent** the request
  - the user who **received** the request
  - the **date** the request was sent.

---

## Table: RequestAccepted

| Column Name  | Type |
| ------------ | ---- |
| requester_id | int  |
| accepter_id  | int  |
| accept_date  | date |

- This table **may contain duplicates** (there is **no primary key**).
- Each row records:
  - the user who **sent the friend request**
  - the user who **accepted the request**
  - the **date** when the request was accepted.

---

# Problem

Find the **overall acceptance rate of friend requests**.

The acceptance rate is defined as:

```
acceptance_rate = number_of_accepted_requests / number_of_requests
```

The result must be **rounded to 2 decimal places**.

---

# Important Rules

1. **Accepted requests may not appear in the FriendRequest table.**
   - Even if a request was never recorded in `FriendRequest`, it still counts as an acceptance.

2. **Duplicates must only be counted once.**
   - A sender may send multiple requests to the same receiver.
   - A request may also appear multiple times in `RequestAccepted`.
   - These duplicates should only be counted **once**.

3. **If there are no requests**, return:

```
0.00
```

---

# Example

## Input

### FriendRequest

| sender_id | send_to_id | request_date |
| --------- | ---------- | ------------ |
| 1         | 2          | 2016/06/01   |
| 1         | 3          | 2016/06/01   |
| 1         | 4          | 2016/06/01   |
| 2         | 3          | 2016/06/02   |
| 3         | 4          | 2016/06/09   |

### RequestAccepted

| requester_id | accepter_id | accept_date |
| ------------ | ----------- | ----------- |
| 1            | 2           | 2016/06/03  |
| 1            | 3           | 2016/06/08  |
| 2            | 3           | 2016/06/08  |
| 3            | 4           | 2016/06/09  |
| 3            | 4           | 2016/06/10  |

---

# Output

| accept_rate |
| ----------- |
| 0.8         |

---

# Explanation

## Total requests

From the `FriendRequest` table:

```
(1,2)
(1,3)
(1,4)
(2,3)
(3,4)
```

Total unique requests:

```
5
```

---

## Accepted requests

From the `RequestAccepted` table:

```
(1,2)
(1,3)
(2,3)
(3,4)
(3,4)
```

Removing duplicates:

```
(1,2)
(1,3)
(2,3)
(3,4)
```

Total unique accepted requests:

```
4
```

---

## Acceptance rate

```
acceptance_rate = accepted_requests / total_requests
                 = 4 / 5
                 = 0.80
```

Final result:

```
0.8
```

---

# Follow-up Questions

1. **Find the acceptance rate for every month.**

2. **Find the cumulative acceptance rate for every day.**
