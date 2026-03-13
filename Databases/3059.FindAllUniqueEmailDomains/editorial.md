# 3059. Find All Unique Email Domains — Approach

## Approach: Filter Utilizing LIKE

This SQL solution analyzes email domain distribution while focusing only on domains that end with **`.com`**.

The main steps are:

1. **Filter emails ending with `.com`**
2. **Extract the domain portion of each email**
3. **Count unique individuals per domain**
4. **Group and sort the results**

---

# Intuition

## 1. Extracting Email Domains

To extract the domain portion of an email, we use the SQL function:

```
SUBSTRING_INDEX(email, '@', -1)
```

This function splits the string at the `@` symbol and returns everything **after it**, which is the **email domain**.

Example:

| email              | extracted domain |
| ------------------ | ---------------- |
| adcmaf@outlook.com | outlook.com      |
| vrzmwyum@yahoo.com | yahoo.com        |

This domain becomes the column **email_domain**.

---

## 2. Counting Unique IDs

To determine how many individuals belong to each domain, we use:

```
COUNT(DISTINCT id)
```

This ensures that:

- Each individual (id) is counted **only once per domain**
- Duplicate entries do not inflate the counts

---

## 3. Filtering Only `.com` Emails

We only want domains ending in **`.com`**.

This is done using:

```
WHERE email LIKE '%.com'
```

This filters the dataset so that domains like:

- `test.edu`
- `example.org`

are excluded.

---

## 4. Grouping by Domain

Next, we group rows using:

```
GROUP BY email_domain
```

This ensures that counts are calculated **separately for each domain**.

---

## 5. Sorting the Result

The final result must be sorted alphabetically by domain:

```
ORDER BY email_domain ASC
```

This makes the output easier to read and analyze.

---

# SQL Implementation

```sql
SELECT
  SUBSTRING_INDEX(email, '@', -1) AS email_domain,
  COUNT(DISTINCT id) AS count
FROM
  Emails
WHERE
  email LIKE '%.com'
GROUP BY
  email_domain
ORDER BY
  email_domain ASC;
```

---

# Key SQL Concepts Used

- `LIKE` for pattern filtering
- `SUBSTRING_INDEX()` for extracting domain names
- `COUNT(DISTINCT ...)` for counting unique users
- `GROUP BY` for domain-level aggregation
- `ORDER BY` for sorted output
