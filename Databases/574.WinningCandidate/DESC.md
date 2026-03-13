# 574. Winning Candidate

## Problem Statement

### Table: Candidate

| Column Name | Type    |
| ----------- | ------- |
| id          | int     |
| name        | varchar |

- `id` is the column with unique values for this table.
- Each row of this table contains information about the **id** and the **name of a candidate**.

---

### Table: Vote

| Column Name | Type |
| ----------- | ---- |
| id          | int  |
| candidateId | int  |

- `id` is an **auto-increment primary key**.
- `candidateId` is a **foreign key** referencing `Candidate.id`.
- Each row represents **one vote cast for a candidate**.

---

## Goal

Write a query to report the **name of the winning candidate**, i.e., the candidate who received the **largest number of votes**.

**Assumption:**
The test cases guarantee that **exactly one candidate wins**.

---

## Example

### Input

#### Candidate Table

| id  | name |
| --- | ---- |
| 1   | A    |
| 2   | B    |
| 3   | C    |
| 4   | D    |
| 5   | E    |

#### Vote Table

| id  | candidateId |
| --- | ----------- |
| 1   | 2           |
| 2   | 4           |
| 3   | 3           |
| 4   | 2           |
| 5   | 5           |

---

### Output

| name |
| ---- |
| B    |

---

## Explanation

- Candidate **B** received **2 votes**.
- Candidates **C, D, and E** each received **1 vote**.
- Therefore, the **winning candidate is B**.
