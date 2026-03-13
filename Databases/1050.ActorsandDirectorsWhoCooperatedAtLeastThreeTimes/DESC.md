# 1050. Actors and Directors Who Cooperated At Least Three Times

## Table: ActorDirector

| Column Name | Type |
| ----------- | ---- |
| actor_id    | int  |
| director_id | int  |
| timestamp   | int  |

### Notes

- `timestamp` is the **primary key** (unique for each row).
- Each row represents one collaboration between an **actor** and a **director**.

---

# Problem

Write a SQL query to find all **pairs of (actor_id, director_id)** where the actor has cooperated with the director **at least three times**.

Return the result table **in any order**.

---

# Output Format

The result should contain the following columns:

| actor_id | director_id |

Each row represents a pair that has collaborated **three or more times**.

---

# Example

## Input

### ActorDirector Table

| actor_id | director_id | timestamp |
| -------: | ----------: | --------: |
|        1 |           1 |         0 |
|        1 |           1 |         1 |
|        1 |           1 |         2 |
|        1 |           2 |         3 |
|        1 |           2 |         4 |
|        2 |           1 |         5 |
|        2 |           1 |         6 |

---

# Explanation

Count the number of collaborations for each `(actor_id, director_id)` pair.

### Pair (1, 1)

Occurrences:

```
timestamp: 0, 1, 2
```

Total collaborations:

```
3
```

This satisfies the requirement **(>= 3)**.

---

### Pair (1, 2)

Occurrences:

```
timestamp: 3, 4
```

Total collaborations:

```
2
```

This does **not** satisfy the requirement.

---

### Pair (2, 1)

Occurrences:

```
timestamp: 5, 6
```

Total collaborations:

```
2
```

This also does **not** satisfy the requirement.

---

# Output

| actor_id | director_id |
| -------: | ----------: |
|        1 |           1 |

---

# Summary

The pair `(1, 1)` appears **three times**, so it is included in the result.

All other pairs appear **fewer than three times**, so they are excluded.
