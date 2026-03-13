# 1364. Number of Trusted Contacts of a Customer — Detailed Summary

## Approach: Invoice Customer Contact Aggregation

This approach starts from the `Invoices` table and then joins outward to gather:

- the customer who owns the invoice
- that customer's contacts
- which of those contacts are also customers of the shop

Then it aggregates the results **per invoice**.

---

## Problem Restatement

For each `invoice_id`, we need to return:

- `customer_name` → the name of the customer who owns the invoice
- `price` → invoice price
- `contacts_cnt` → total number of contacts that customer has
- `trusted_contacts_cnt` → number of those contacts who are also customers in the shop

The result must be ordered by:

```sql
invoice_id
```

---

## Core Idea

Each invoice belongs to one user:

```sql
Invoices.user_id
```

That user is a customer in the `Customers` table.

That same user may have zero or more contacts in the `Contacts` table.

A contact is considered **trusted** if that contact also exists in the `Customers` table.

So the query needs to connect these pieces:

1. invoice → owning customer
2. owning customer → their contacts
3. each contact → whether they are also a customer

---

# Query

```sql
SELECT
  I.invoice_id,
  Cust.customer_name,
  I.price,
  COUNT(DISTINCT C.contact_name) AS contacts_cnt,
  COUNT(DISTINCT Nme.customer_name) AS trusted_contacts_cnt
FROM
  Invoices I
  LEFT JOIN Customers Cust
    ON I.user_id = Cust.customer_id
  LEFT JOIN Contacts C
    ON C.user_id = Cust.customer_id
  LEFT JOIN Customers Nme
    ON Nme.customer_name = C.contact_name
GROUP BY
  I.invoice_id;
```

---

# Step-by-Step Explanation

## 1. Start from `Invoices`

```sql
FROM Invoices I
```

This makes `Invoices` the base table.

That is the right starting point because the problem asks for the answer **for each invoice_id**.

So every invoice must appear in the output.

---

## 2. Join invoice to the customer who owns it

```sql
LEFT JOIN Customers Cust
  ON I.user_id = Cust.customer_id
```

### What this does

This connects each invoice to the customer who received it.

For example:

| invoice_id | user_id | price |
| ---------: | ------: | ----: |
|         77 |       1 |   100 |

joins to:

| customer_id | customer_name | email              |
| ----------: | ------------- | ------------------ |
|           1 | Alice         | alice@leetcode.com |

So now invoice `77` can show:

- customer name = `Alice`
- price = `100`

---

## 3. Join the customer to their contacts

```sql
LEFT JOIN Contacts C
  ON C.user_id = Cust.customer_id
```

### What this does

Each customer may have multiple contacts.

This join attaches all contacts belonging to the customer who owns the invoice.

For example, Alice (`customer_id = 1`) has contacts:

- Bob
- John
- Jal

So invoice `77` now expands into multiple joined rows, one per contact.

Conceptually:

| invoice_id | customer_name | contact_name |
| ---------: | ------------- | ------------ |
|         77 | Alice         | Bob          |
|         77 | Alice         | John         |
|         77 | Alice         | Jal          |

This is why aggregation is needed later.

---

## 4. Identify which contacts are also customers

```sql
LEFT JOIN Customers Nme
  ON Nme.customer_name = C.contact_name
```

### What this is trying to do

This join attempts to match a contact against the `Customers` table.

If the match succeeds, the contact is treated as a trusted contact.

So if a contact's name appears as a customer name in the shop, that contact is counted as trusted.

For example:

- Alice's contact `Bob` matches customer `Bob`
- Alice's contact `John` matches customer `John`
- Alice's contact `Jal` does not match any customer

So:

- Bob → trusted
- John → trusted
- Jal → not trusted

---

# Aggregation

After the joins, one invoice can appear in multiple rows because one customer can have many contacts.

So the query groups by invoice:

```sql
GROUP BY I.invoice_id
```

Then it computes counts over the joined rows.

---

## 5. Count total contacts

```sql
COUNT(DISTINCT C.contact_name) AS contacts_cnt
```

### Why this works

Each joined row may represent one contact.

Counting distinct contact names gives the total number of unique contacts for that invoice's customer.

For Alice:

- Bob
- John
- Jal

So:

```text
contacts_cnt = 3
```

### Why `DISTINCT` is used

If the joins cause repeated rows, `DISTINCT` prevents overcounting.

That makes the query safer.

---

## 6. Count trusted contacts

```sql
COUNT(DISTINCT Nme.customer_name) AS trusted_contacts_cnt
```

### Why this works

If a contact matches the `Customers` table, then `Nme.customer_name` is not null.

If no match exists, `Nme.customer_name` is null.

Since `COUNT(...)` ignores nulls, only matched contacts are counted.

For Alice:

- Bob → matched
- John → matched
- Jal → null

So:

```text
trusted_contacts_cnt = 2
```

---

# Worked Example

## Input

### Customers

| customer_id | customer_name | email              |
| ----------: | ------------- | ------------------ |
|           1 | Alice         | alice@leetcode.com |
|           2 | Bob           | bob@leetcode.com   |
|          13 | John          | john@leetcode.com  |
|           6 | Alex          | alex@leetcode.com  |

### Contacts

| user_id | contact_name | contact_email      |
| ------: | ------------ | ------------------ |
|       1 | Bob          | bob@leetcode.com   |
|       1 | John         | john@leetcode.com  |
|       1 | Jal          | jal@leetcode.com   |
|       2 | Omar         | omar@leetcode.com  |
|       2 | Meir         | meir@leetcode.com  |
|       6 | Alice        | alice@leetcode.com |

### Invoices

| invoice_id | price | user_id |
| ---------: | ----: | ------: |
|         77 |   100 |       1 |
|         88 |   200 |       1 |
|         99 |   300 |       2 |
|         66 |   400 |       2 |
|         55 |   500 |      13 |
|         44 |    60 |       6 |

---

# Per-Customer Interpretation

## Alice (`customer_id = 1`)

Contacts:

- Bob
- John
- Jal

Customers in shop:

- Bob exists
- John exists
- Jal does not exist

So:

- contacts count = `3`
- trusted contacts count = `2`

Alice has invoices:

- 77
- 88

So both invoices should show:

- `contacts_cnt = 3`
- `trusted_contacts_cnt = 2`

---

## Bob (`customer_id = 2`)

Contacts:

- Omar
- Meir

Neither exists in the `Customers` table.

So:

- contacts count = `2`
- trusted contacts count = `0`

Bob has invoices:

- 66
- 99

So both invoices should show:

- `contacts_cnt = 2`
- `trusted_contacts_cnt = 0`

---

## John (`customer_id = 13`)

John has no contacts.

So:

- contacts count = `0`
- trusted contacts count = `0`

John has invoice:

- 55

So invoice 55 gets zeros.

---

## Alex (`customer_id = 6`)

Alex has one contact:

- Alice

Alice exists in `Customers`, so:

- contacts count = `1`
- trusted contacts count = `1`

Alex has invoice:

- 44

So invoice 44 gets:

- `contacts_cnt = 1`
- `trusted_contacts_cnt = 1`

---

# Expected Output

| invoice_id | customer_name | price | contacts_cnt | trusted_contacts_cnt |
| ---------: | ------------- | ----: | -----------: | -------------------: |
|         44 | Alex          |    60 |            1 |                    1 |
|         55 | John          |   500 |            0 |                    0 |
|         66 | Bob           |   400 |            2 |                    0 |
|         77 | Alice         |   100 |            3 |                    2 |
|         88 | Alice         |   200 |            3 |                    2 |
|         99 | Bob           |   300 |            2 |                    0 |

---

# Why `LEFT JOIN` Is Important

All joins are written as `LEFT JOIN`.

That is important because:

- every invoice must appear
- some customers may have no contacts
- some contacts may not be customers

If we used `INNER JOIN`, rows could disappear.

### Example: John

John has invoice `55`, but no contacts.

With `LEFT JOIN`, invoice 55 still appears.

With `INNER JOIN` on `Contacts`, John would disappear from the result, which would be wrong.

---

# Important Caution About the Trusted Contact Match

The shown query uses:

```sql
LEFT JOIN Customers Nme
  ON Nme.customer_name = C.contact_name
```

This is the logic presented in the approach, but there is a subtle modeling concern.

The problem defines trusted contact as:

> a contact whose **email exists in the Customers table**

That means the logically strongest match should be based on email, not name.

So a more precise version would be:

```sql
LEFT JOIN Customers Nme
  ON Nme.email = C.contact_email
```

because emails are better identifiers than names.

Names can be duplicated or inconsistent, while the problem statement explicitly refers to matching emails.

So although the provided approach matches by customer name, the safer and more faithful interpretation of the problem is to match:

```sql
Customers.email = Contacts.contact_email
```

---

# Recommended Correct Version

```sql
SELECT
  I.invoice_id,
  Cust.customer_name,
  I.price,
  COUNT(DISTINCT C.contact_email) AS contacts_cnt,
  COUNT(DISTINCT Nme.email) AS trusted_contacts_cnt
FROM Invoices I
LEFT JOIN Customers Cust
  ON I.user_id = Cust.customer_id
LEFT JOIN Contacts C
  ON C.user_id = Cust.customer_id
LEFT JOIN Customers Nme
  ON Nme.email = C.contact_email
GROUP BY
  I.invoice_id,
  Cust.customer_name,
  I.price
ORDER BY
  I.invoice_id;
```

---

# Why This Recommended Version Is Better

## 1. Trusted match is based on email

The problem explicitly defines trusted contact via email existence.

## 2. Contact count is based on email

Since `(user_id, contact_email)` is the primary key in `Contacts`, counting distinct emails is a natural and stable choice.

## 3. Grouping is more portable

Some SQL engines require all non-aggregated selected columns to be listed in `GROUP BY`.

So grouping by:

- `I.invoice_id`
- `Cust.customer_name`
- `I.price`

is safer across SQL engines than grouping only by `I.invoice_id`.

## 4. Output is ordered correctly

The problem explicitly asks for ordering by `invoice_id`.

---

# Full Explanation of the Recommended Query

```sql
SELECT
  I.invoice_id,
  Cust.customer_name,
  I.price,
  COUNT(DISTINCT C.contact_email) AS contacts_cnt,
  COUNT(DISTINCT Nme.email) AS trusted_contacts_cnt
FROM Invoices I
LEFT JOIN Customers Cust
  ON I.user_id = Cust.customer_id
LEFT JOIN Contacts C
  ON C.user_id = Cust.customer_id
LEFT JOIN Customers Nme
  ON Nme.email = C.contact_email
GROUP BY
  I.invoice_id,
  Cust.customer_name,
  I.price
ORDER BY
  I.invoice_id;
```

### `Invoices I`

Base table. One output row is needed per invoice.

### `LEFT JOIN Customers Cust`

Gets the customer name for the invoice owner.

### `LEFT JOIN Contacts C`

Gets all contacts belonging to that customer.

### `LEFT JOIN Customers Nme`

Determines which contacts are also customers by matching email.

### `COUNT(DISTINCT C.contact_email)`

Counts total contacts.

### `COUNT(DISTINCT Nme.email)`

Counts trusted contacts.

### `GROUP BY ...`

Aggregates repeated contact rows back into one row per invoice.

### `ORDER BY I.invoice_id`

Produces the required order.

---

# Complexity Discussion

Let:

- `I` = number of invoices
- `K` = number of contacts
- `C` = number of customers

The query joins:

- invoices to customers
- customers to contacts
- contacts back to customers

So the runtime depends on the database optimizer and indexing, but conceptually it is driven by the join sizes and aggregation.

Indexes that help:

- `Customers.customer_id`
- `Customers.email`
- `Contacts.user_id`
- `Contacts.contact_email`
- `Invoices.user_id`

---

# Comparing the Approach Version vs the Email-Based Version

## Approach version

```sql
LEFT JOIN Customers Nme ON Nme.customer_name = C.contact_name
```

### Advantage

- matches the narrative given in the approach

### Risk

- uses name equality instead of the problem's email condition
- names are weaker identifiers

---

## Email-based version

```sql
LEFT JOIN Customers Nme ON Nme.email = C.contact_email
```

### Advantage

- directly matches the problem statement
- more robust and correct

### Recommendation

Prefer this version.

---

# Final Takeaways

- Start from `Invoices` because every invoice must appear
- Join to the invoice owner in `Customers`
- Join to the owner's contacts in `Contacts`
- Determine trusted contacts by checking whether the contact also exists in `Customers`
- Use `LEFT JOIN` so invoices with no contacts still appear
- Aggregate by invoice
- Match trusted contacts by **email**, not by name, to align with the problem statement

---
