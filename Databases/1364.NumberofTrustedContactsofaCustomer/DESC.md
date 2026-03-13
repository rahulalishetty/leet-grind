# 1364. Number of Trusted Contacts of a Customer

## Tables

### Customers

| Column Name   | Type    |
| ------------- | ------- |
| customer_id   | int     |
| customer_name | varchar |
| email         | varchar |

Notes:

- `customer_id` is the **primary key**.
- Each row contains the **name and email of a customer** of the online shop.

---

### Contacts

| Column Name   | Type    |
| ------------- | ------- |
| user_id       | int     |
| contact_name  | varchar |
| contact_email | varchar |

Notes:

- `(user_id, contact_email)` is the **primary key**.
- Each row represents **one trusted contact of a customer**.
- `user_id` identifies the customer who owns the contact.
- A contact **may or may not exist in the Customers table**.

---

### Invoices

| Column Name | Type |
| ----------- | ---- |
| invoice_id  | int  |
| price       | int  |
| user_id     | int  |

Notes:

- `invoice_id` is the **primary key**.
- Each row indicates that `user_id` has an invoice with a given `price`.

---

# Problem

For **each invoice**, return:

- **customer_name** → name of the customer the invoice belongs to
- **price** → price of the invoice
- **contacts_cnt** → total number of contacts related to the customer
- **trusted_contacts_cnt** → number of contacts whose email also exists in the `Customers` table

A **trusted contact** is defined as:

> a contact whose email appears in the `Customers.email` column.

Return the result table **ordered by `invoice_id`**.

---

# Example

## Input

### Customers Table

| customer_id | customer_name | email              |
| ----------- | ------------- | ------------------ |
| 1           | Alice         | alice@leetcode.com |
| 2           | Bob           | bob@leetcode.com   |
| 13          | John          | john@leetcode.com  |
| 6           | Alex          | alex@leetcode.com  |

---

### Contacts Table

| user_id | contact_name | contact_email      |
| ------- | ------------ | ------------------ |
| 1       | Bob          | bob@leetcode.com   |
| 1       | John         | john@leetcode.com  |
| 1       | Jal          | jal@leetcode.com   |
| 2       | Omar         | omar@leetcode.com  |
| 2       | Meir         | meir@leetcode.com  |
| 6       | Alice        | alice@leetcode.com |

---

### Invoices Table

| invoice_id | price | user_id |
| ---------- | ----- | ------- |
| 77         | 100   | 1       |
| 88         | 200   | 1       |
| 99         | 300   | 2       |
| 66         | 400   | 2       |
| 55         | 500   | 13      |
| 44         | 60    | 6       |

---

# Output

| invoice_id | customer_name | price | contacts_cnt | trusted_contacts_cnt |
| ---------- | ------------- | ----- | ------------ | -------------------- |
| 44         | Alex          | 60    | 1            | 1                    |
| 55         | John          | 500   | 0            | 0                    |
| 66         | Bob           | 400   | 2            | 0                    |
| 77         | Alice         | 100   | 3            | 2                    |
| 88         | Alice         | 200   | 3            | 2                    |
| 99         | Bob           | 300   | 2            | 0                    |

---

# Explanation

### Alice

Contacts:

| contact_name | contact_email     |
| ------------ | ----------------- |
| Bob          | bob@leetcode.com  |
| John         | john@leetcode.com |
| Jal          | jal@leetcode.com  |

- Total contacts = **3**
- Trusted contacts = **2** (Bob and John exist in `Customers`)

---

### Bob

Contacts:

| contact_name | contact_email     |
| ------------ | ----------------- |
| Omar         | omar@leetcode.com |
| Meir         | meir@leetcode.com |

- Total contacts = **2**
- Trusted contacts = **0** (neither exists in `Customers`)

---

### Alex

Contacts:

| contact_name | contact_email      |
| ------------ | ------------------ |
| Alice        | alice@leetcode.com |

- Total contacts = **1**
- Trusted contacts = **1** (Alice exists in `Customers`)

---

### John

- No contacts

So:

- `contacts_cnt = 0`
- `trusted_contacts_cnt = 0`
