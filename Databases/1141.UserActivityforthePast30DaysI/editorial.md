# User Activity for the Past 30 Days — Explanation

## Overview

The two key ideas required to solve this problem are:

1. **Select a specific date range**
2. **Count only distinct users**, because a user may perform multiple activities on the same day.

The final results must also be **grouped by day**.

---

# Selecting the Date Range

The question asks for **30 days ending on `2019-07-27` (inclusive)**.

So the correct range is:

```
2019-06-28 → 2019-07-27
```

There are several ways to express this in SQL.

---

# Method 1: Manually Calculate the Date Range

We manually compute the start date and filter accordingly.

```sql
activity_date > '2019-06-27'
AND activity_date <= '2019-07-27'
```

Another equivalent expression using `BETWEEN`:

```sql
activity_date BETWEEN '2019-06-28' AND '2019-07-27'
```

Note:

- `BETWEEN` is **inclusive**
- Both the start and end values are included.

---

# Method 2: Using `DATEDIFF()`

The function:

```
DATEDIFF(date1, date2)
```

returns the difference between the two dates in **days**.

Example condition:

```sql
DATEDIFF('2019-07-27', activity_date) < 30
AND
DATEDIFF('2019-07-27', activity_date) >= 0
```

Explanation:

- `DATEDIFF('2019-07-27', activity_date) < 30`
  ensures the date is within the past **30 days**.

- `DATEDIFF('2019-07-27', activity_date) >= 0`
  ensures the activity date is **not after 2019‑07‑27**.

Without the second condition, dates after `2019‑07‑27` could appear because negative values are also `< 30`.

---

# Alternative DATEDIFF Form

Another compact version:

```sql
DATEDIFF('2019-07-27', activity_date) BETWEEN 0 AND 29
```

This guarantees the activity date lies within the **30‑day window**.

---

# Method 3: Using `DATE_SUB()`

The function:

```
DATE_SUB(date, INTERVAL expr unit)
```

performs **date arithmetic**.

Example:

```sql
activity_date BETWEEN DATE_SUB('2019-07-27', INTERVAL 29 DAY)
AND '2019-07-27'
```

This subtracts **29 days** from `2019‑07‑27`, producing the correct start date.

---

# Approach

## Algorithm

1. Select the columns needed for the final result:
   - the activity date
   - the number of **distinct users**

2. Add a filter for the **30‑day date range**.

3. Count distinct users because a user may perform **multiple activities on the same day**.

4. Group the results by **activity date**.

---

# MySQL Solution

```sql
SELECT
    activity_date AS day,
    COUNT(DISTINCT user_id) AS active_users
FROM Activity
WHERE
    DATEDIFF('2019-07-27', activity_date) < 30
    AND DATEDIFF('2019-07-27', activity_date) >= 0
GROUP BY 1;
```
