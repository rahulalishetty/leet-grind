# 1097. Game Play Analysis V

## Table: Activity

| Column Name  | Type |
| ------------ | ---- |
| player_id    | int  |
| device_id    | int  |
| event_date   | date |
| games_played | int  |

- `(player_id, event_date)` is the **primary key**.
- Each row records when a **player logged in and played games on a specific date**.
- A player may log in multiple days using different devices.

---

## Definitions

### Install Date

The **install date** of a player is defined as the **first day the player logged in**.

```
install_date = MIN(event_date) for each player
```

---

### Day 1 Retention

For an install date **x**, the **day 1 retention** is:

```
(number of players who installed on x AND logged in again on x + 1)
/
(total number of players who installed on x)
```

The result must be **rounded to 2 decimal places**.

---

## Problem

For each install date:

- Calculate the **number of installs** (players whose first login was that day).
- Calculate the **day one retention**.

Return the results in any order.

---

## Example

### Input

**Activity table**

| player_id | device_id | event_date | games_played |
| --------- | --------- | ---------- | ------------ |
| 1         | 2         | 2016-03-01 | 5            |
| 1         | 2         | 2016-03-02 | 6            |
| 2         | 3         | 2017-06-25 | 1            |
| 3         | 1         | 2016-03-01 | 0            |
| 3         | 4         | 2016-07-03 | 5            |

---

### Output

| install_dt | installs | Day1_retention |
| ---------- | -------- | -------------- |
| 2016-03-01 | 2        | 0.50           |
| 2017-06-25 | 1        | 0.00           |

---

## Explanation

### Install Date: 2016‑03‑01

Players who installed on this day:

```
Player 1
Player 3
```

Total installs:

```
2
```

Check if they logged in on the next day **2016‑03‑02**.

- Player 1 → logged in on 2016‑03‑02 ✔
- Player 3 → did NOT log in on 2016‑03‑02 ✘

Day 1 retention:

```
1 / 2 = 0.50
```

---

### Install Date: 2017‑06‑25

Player who installed:

```
Player 2
```

Check if the player logged in on **2017‑06‑26**.

- Player 2 → did NOT log in again

Day 1 retention:

```
0 / 1 = 0.00
```
