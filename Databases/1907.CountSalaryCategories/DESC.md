# 1907. Count Salary Categories

## Table: Accounts

| Column Name | Type |
| ----------- | ---- |
| account_id  | int  |
| income      | int  |

**Notes:**

- `account_id` is the **primary key** (unique value) for the table.
- Each row represents the **monthly income of one bank account**.

---

# Problem

Write a SQL query to calculate the **number of bank accounts in each salary category**.

The salary categories are defined as:

| Category       | Definition                                   |
| -------------- | -------------------------------------------- |
| Low Salary     | income strictly **less than 20000**          |
| Average Salary | income **between 20000 and 50000 inclusive** |
| High Salary    | income strictly **greater than 50000**       |

---

# Requirements

The result table:

- **must contain all three categories**
- if a category has **no accounts**, return **0**
- result order **does not matter**

---

# Example

## Input

### Accounts Table

| account_id | income |
| ---------- | ------ |
| 3          | 108939 |
| 2          | 12747  |
| 8          | 87709  |
| 6          | 91796  |

---

## Output

| category       | accounts_count |
| -------------- | -------------- |
| Low Salary     | 1              |
| Average Salary | 0              |
| High Salary    | 3              |

---

# Explanation

### Low Salary

Definition:

```
income < 20000
```

Matching accounts:

- Account **2** → income = **12747**

Count:

```
1
```

---

### Average Salary

Definition:

```
20000 ≤ income ≤ 50000
```

Matching accounts:

- **None**

Count:

```
0
```

---

### High Salary

Definition:

```
income > 50000
```

Matching accounts:

- Account **3** → 108939
- Account **6** → 91796
- Account **8** → 87709

Count:

```
3
```
