# 1843. Suspicious Bank Accounts

## Table: Accounts

| Column Name | Type |
| ----------- | ---- |
| account_id  | int  |
| max_income  | int  |

**Notes:**

- `account_id` contains **unique values**.
- Each row stores the **maximum allowed monthly income** for a bank account.

---

## Table: Transactions

| Column Name    | Type     |
| -------------- | -------- |
| transaction_id | int      |
| account_id     | int      |
| type           | ENUM     |
| amount         | int      |
| day            | datetime |

**Notes:**

- `transaction_id` contains **unique values**.
- Each row represents a **bank transaction**.
- `type` can be:
  - `'Creditor'` → money **deposited** into the account.
  - `'Debtor'` → money **withdrawn** from the account.
- `amount` represents the amount deposited or withdrawn.
- `day` stores the timestamp of the transaction.

---

# Problem

A bank account is considered **suspicious** if:

- The **total income** exceeds `max_income`
- For **two or more consecutive months**

### Definition of Income

The **total income for a month** is the **sum of all deposits** (`type = 'Creditor'`) made during that month.

Withdrawals (`Debtor`) **do not count** toward income.

---

# Task

Write a SQL query to return:

```
account_id
```

for all **suspicious accounts**.

The result may be returned **in any order**.

---

# Example

## Input

### Accounts Table

| account_id | max_income |
| ---------- | ---------- |
| 3          | 21000      |
| 4          | 10400      |

---

### Transactions Table

| transaction_id | account_id | type     | amount | day                 |
| -------------- | ---------- | -------- | ------ | ------------------- |
| 2              | 3          | Creditor | 107100 | 2021-06-02 11:38:14 |
| 4              | 4          | Creditor | 10400  | 2021-06-20 12:39:18 |
| 11             | 4          | Debtor   | 58800  | 2021-07-23 12:41:55 |
| 1              | 4          | Creditor | 49300  | 2021-05-03 16:11:04 |
| 15             | 3          | Debtor   | 75500  | 2021-05-23 14:40:20 |
| 10             | 3          | Creditor | 102100 | 2021-06-15 10:37:16 |
| 14             | 4          | Creditor | 56300  | 2021-07-21 12:12:25 |
| 19             | 4          | Debtor   | 101100 | 2021-05-09 15:21:49 |
| 8              | 3          | Creditor | 64900  | 2021-07-26 15:09:56 |
| 7              | 3          | Creditor | 90900  | 2021-06-14 11:23:07 |

---

# Output

| account_id |
| ---------- |
| 3          |

---

# Explanation

## Account 3

### June 2021 Income

Deposits:

| Amount |
| ------ |
| 107100 |
| 102100 |
| 90900  |

Total:

```
300100
```

This exceeds the allowed `max_income = 21000`.

---

### July 2021 Income

Deposits:

| Amount |
| ------ |
| 64900  |

Total:

```
64900
```

This also exceeds the allowed `max_income = 21000`.

---

### Result

The account exceeded the limit for **two consecutive months**:

```
June 2021
July 2021
```

Therefore:

```
Account 3 is suspicious
```

---

## Account 4

### May 2021 Income

Deposits:

```
49300
```

This exceeds `max_income = 10400`.

---

### June 2021 Income

Deposits:

```
10400
```

This **does not exceed** the limit.

---

### July 2021 Income

Deposits:

```
56300
```

This exceeds the limit again.

---

### Result

The account exceeded the limit in:

```
May
July
```

But **not in June**, so the months are **not consecutive**.

Therefore:

```
Account 4 is NOT suspicious
```
