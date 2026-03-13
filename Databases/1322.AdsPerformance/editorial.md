# Ads Performance — CTR Calculation (Approach 1)

## Approach: Conditional Aggregation for CTR Calculation

### Intuition

To calculate **Click‑Through Rate (CTR)** for each advertisement, we break the task into several logical steps.

CTR formula:

```
CTR = (Number of Clicks / (Number of Clicks + Number of Views)) × 100
```

Ignored actions are **not included** in the calculation.

---

# Step 1 — Calculate Clicks and Views

For each `ad_id`, we count:

- number of **Clicked** actions
- number of **Viewed** actions

We use **conditional aggregation**.

In MySQL, boolean expressions evaluate to:

```
TRUE  -> 1
FALSE -> 0
```

So:

```
SUM(action = 'Clicked')
```

counts the number of **Clicked** rows.

Similarly:

```
SUM(action = 'Viewed')
```

counts the number of **Viewed** rows.

---

# Step 2 — Apply the CTR Formula

Using the counts from Step 1:

```
CTR = clicks / (clicks + views) × 100
```

This calculation is performed **per ad**.

---

# Step 3 — Handle Division by Zero

If an ad has:

```
clicks = 0
views = 0
```

then the denominator becomes zero.

To avoid a **division‑by‑zero error**, we use:

```
IFNULL(expression, 0)
```

This ensures the CTR becomes:

```
0.00
```

for such ads.

---

# Step 4 — Round the Result

The problem requires the CTR to be **rounded to 2 decimal places**.

We use:

```
ROUND(value, 2)
```

---

# Step 5 — Sort the Result

The output must be ordered by:

1. **CTR in descending order**
2. **ad_id in ascending order** (to break ties)

Using:

```
ORDER BY ctr DESC, ad_id ASC
```

---

# Final SQL Query

```sql
SELECT
    ad_id,
    ROUND(
        IFNULL(
            (SUM(action = 'Clicked') / (SUM(action = 'Clicked') + SUM(action = 'Viewed'))) * 100,
            0
        ),
        2
    ) AS ctr
FROM Ads
GROUP BY ad_id
ORDER BY ctr DESC, ad_id ASC;
```

---

# Key Concepts

- **Conditional aggregation** using boolean expressions.
- **SUM(condition)** to count rows matching a condition.
- **IFNULL()** to avoid division-by-zero errors.
- **ROUND(..., 2)** for formatting numeric output.
