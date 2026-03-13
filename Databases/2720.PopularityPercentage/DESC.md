# 2720. Popularity Percentage

## Table: Friends

| Column Name | Type |
| ----------- | ---- |
| user1       | int  |
| user2       | int  |

- `(user1, user2)` is the **primary key**.
- Each row represents a **friendship** between two users.
- Friendship is **mutual**, meaning if `(1,2)` exists then both users are friends with each other.

---

## Problem

We need to compute the **popularity percentage** for each user.

The popularity percentage is defined as:

```
(number of friends a user has / total number of users on the platform) * 100
```

The result must be:

- **rounded to 2 decimal places**
- returned **for every user**
- ordered by **user1 in ascending order**

---

## Output Columns

| Column                | Description                        |
| --------------------- | ---------------------------------- |
| user1                 | user id                            |
| percentage_popularity | popularity percentage of that user |

---

# Example

## Input

### Friends

| user1 | user2 |
| ----- | ----- |
| 2     | 1     |
| 1     | 3     |
| 4     | 1     |
| 1     | 5     |
| 1     | 6     |
| 2     | 6     |
| 7     | 2     |
| 8     | 3     |
| 3     | 9     |

---

## Output

| user1 | percentage_popularity |
| ----- | --------------------- |
| 1     | 55.56                 |
| 2     | 33.33                 |
| 3     | 33.33                 |
| 4     | 11.11                 |
| 5     | 11.11                 |
| 6     | 22.22                 |
| 7     | 11.11                 |
| 8     | 11.11                 |
| 9     | 11.11                 |

---

# Explanation

There are **9 total users** in the system.

The popularity percentage is calculated using:

```
(number_of_friends / total_users) * 100
```

---

## User 1

Friends:

```
2, 3, 4, 5, 6
```

Number of friends:

```
5
```

Popularity:

```
(5 / 9) * 100 = 55.56
```

---

## User 2

Friends:

```
1, 6, 7
```

Number of friends:

```
3
```

Popularity:

```
(3 / 9) * 100 = 33.33
```

---

## User 3

Friends:

```
1, 8, 9
```

Number of friends:

```
3
```

Popularity:

```
(3 / 9) * 100 = 33.33
```

---

## User 4

Friends:

```
1
```

Popularity:

```
(1 / 9) * 100 = 11.11
```

---

## User 5

Friends:

```
1
```

Popularity:

```
(1 / 9) * 100 = 11.11
```

---

## User 6

Friends:

```
1, 2
```

Popularity:

```
(2 / 9) * 100 = 22.22
```

---

## User 7

Friends:

```
2
```

Popularity:

```
(1 / 9) * 100 = 11.11
```

---

## User 8

Friends:

```
3
```

Popularity:

```
(1 / 9) * 100 = 11.11
```

---

## User 9

Friends:

```
3
```

Popularity:

```
(1 / 9) * 100 = 11.11
```

---

## Final Result

| user1 | percentage_popularity |
| ----- | --------------------- |
| 1     | 55.56                 |
| 2     | 33.33                 |
| 3     | 33.33                 |
| 4     | 11.11                 |
| 5     | 11.11                 |
| 6     | 22.22                 |
| 7     | 11.11                 |
| 8     | 11.11                 |
| 9     | 11.11                 |
