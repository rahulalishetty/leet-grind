# 2041. Accepted Candidates From the Interviews

## Table: Candidates

| Column Name  | Type    |
| ------------ | ------- |
| candidate_id | int     |
| name         | varchar |
| years_of_exp | int     |
| interview_id | int     |

**Notes:**

- `candidate_id` is the **primary key** (unique value) for the table.
- Each row represents:
  - the candidate's **name**
  - their **years of experience**
  - the **interview ID** associated with them.

---

## Table: Rounds

| Column Name  | Type |
| ------------ | ---- |
| interview_id | int  |
| round_id     | int  |
| score        | int  |

**Notes:**

- `(interview_id, round_id)` forms the **primary key**.
- Each row represents the **score of a particular interview round**.

---

# Problem

We need to report the **candidate IDs** of candidates who satisfy both of the following conditions:

1. They have **at least 2 years of experience**.
2. The **sum of scores across all interview rounds** is **strictly greater than 15**.

The result table may be returned **in any order**.

---

# Example

## Input

### Candidates Table

| candidate_id | name    | years_of_exp | interview_id |
| ------------ | ------- | ------------ | ------------ |
| 11           | Atticus | 1            | 101          |
| 9            | Ruben   | 6            | 104          |
| 6            | Aliza   | 10           | 109          |
| 8            | Alfredo | 0            | 107          |

---

### Rounds Table

| interview_id | round_id | score |
| ------------ | -------- | ----- |
| 109          | 3        | 4     |
| 101          | 2        | 8     |
| 109          | 4        | 1     |
| 107          | 1        | 3     |
| 104          | 3        | 6     |
| 109          | 1        | 4     |
| 104          | 4        | 7     |
| 104          | 1        | 2     |
| 109          | 2        | 1     |
| 104          | 2        | 7     |
| 107          | 2        | 3     |
| 101          | 1        | 8     |

---

# Output

| candidate_id |
| ------------ |
| 9            |

---

# Explanation

### Candidate 11

Years of experience:

```
1
```

Total interview score:

```
8 + 8 = 16
```

Although the score is greater than 15, the candidate has **less than 2 years of experience**, so they are **excluded**.

---

### Candidate 9

Years of experience:

```
6
```

Interview scores:

```
6 + 7 + 2 + 7 = 22
```

Since:

```
22 > 15
```

and the candidate has **at least 2 years of experience**, they are **included**.

---

### Candidate 6

Years of experience:

```
10
```

Interview scores:

```
4 + 1 + 4 + 1 = 10
```

Since:

```
10 ≤ 15
```

the score requirement is not satisfied, so the candidate is **excluded**.

---

### Candidate 8

Years of experience:

```
0
```

Interview scores:

```
3 + 3 = 6
```

The candidate fails both conditions:

- experience < 2
- total score ≤ 15

Therefore they are **excluded**.
