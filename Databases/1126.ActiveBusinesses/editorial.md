# 1126. Active Businesses

## Detailed Summary of Two Accepted Approaches

We need to find businesses that are considered **active**.

A business is active if:

- it has **more than one event type**
- where its `occurrences` value is **strictly greater** than the average `occurrences` for that same `event_type` across all businesses

So the problem breaks into two clear parts:

1. compute the average occurrences for each event type
2. find businesses that beat that event-type average in **more than one** event type

This summary covers two accepted approaches:

1. **Using aggregate functions**
2. **Using a window function**

Both are correct.
The first computes averages in a separate grouped subquery and joins back.
The second computes the average directly beside each row using a window function.

---

# Core observation

The comparison is **within each event type**, not across all events globally.

So for each `event_type`, we need:

```text
average occurrences across all businesses that have that event
```

Then each row asks:

> Is this business’s occurrences value greater than that event type’s average?

After that, we count how many such “above average” event types each business has.

If the count is greater than 1, the business is active.

---

# Example data

## Events

| business_id | event_type | occurrences |
| ----------: | ---------- | ----------: |
|           1 | reviews    |           7 |
|           3 | reviews    |           3 |
|           1 | ads        |          11 |
|           2 | ads        |           7 |
|           3 | ads        |           6 |
|           1 | page views |           3 |
|           2 | page views |          12 |

---

# Compute averages by event type

## `reviews`

Values:

```text
7, 3
```

Average:

```text
(7 + 3) / 2 = 5
```

## `ads`

Values:

```text
11, 7, 6
```

Average:

```text
(11 + 7 + 6) / 3 = 8
```

## `page views`

Values:

```text
3, 12
```

Average:

```text
(3 + 12) / 2 = 7.5
```

Now compare each business’s row to the average of that same event type.

---

# Approach 1: Using the Aggregate Function

## Core idea

This approach explicitly computes the average occurrences per `event_type` in a grouped subquery, then joins that result back to the original `Events` table.

After the join, each row knows the average for its own event type, so we can filter rows where:

```sql
occurrences > avg
```

Then we group by `business_id` and keep businesses that satisfy this condition for more than one event type.

---

## Step 1: Compute average occurrences for each event type

```sql
SELECT
  event_type,
  AVG(occurrences) AS avg
FROM
  Events
GROUP BY
  event_type
```

---

## Why this works

### `GROUP BY event_type`

This groups rows by event type.

### `AVG(occurrences)`

Within each event type, this computes the average occurrences across all businesses that have that event.

For the sample, the subquery produces:

| event_type | avg |
| ---------- | --- |
| reviews    | 5   |
| ads        | 8   |
| page views | 7.5 |

This is exactly the reference table we need for comparisons.

---

## Step 2: Join back to the original table and keep rows above average

```sql
SELECT
  e.business_id
FROM
  Events e
  JOIN (
    SELECT
      event_type,
      AVG(occurrences) AS avg
    FROM
      Events
    GROUP BY
      event_type
  ) t0
    ON e.event_type = t0.event_type
   AND e.occurrences > t0.avg
GROUP BY
  e.business_id
HAVING
  COUNT(*) > 1
```

---

## Why this works

### Join on `event_type`

```sql
e.event_type = t0.event_type
```

This matches each row in `Events` with the average for its event type.

### Filter in the join condition

```sql
e.occurrences > t0.avg
```

This keeps only rows where that business performs above average for the event.

So after the join, we only retain “above average event rows.”

---

## Step 3: Group by business and count qualifying event types

```sql
GROUP BY e.business_id
HAVING COUNT(*) > 1
```

This counts how many above-average event rows each business has.

If the count is greater than 1, then the business has more than one qualifying event type and is therefore active.

---

## Final query for Approach 1

```sql
SELECT
  e.business_id
FROM
  Events e
  JOIN (
    SELECT
      event_type,
      AVG(occurrences) AS avg
    FROM
      Events
    GROUP BY
      event_type
  ) t0 ON e.event_type = t0.event_type
  AND e.occurrences > t0.avg
GROUP BY
  e.business_id
HAVING
  COUNT(*) > 1;
```

---

## Walkthrough on the sample

### Business 1

Rows:

| event_type | occurrences | avg | above avg? |
| ---------- | ----------: | --: | ---------- |
| reviews    |           7 |   5 | yes        |
| ads        |          11 |   8 | yes        |
| page views |           3 | 7.5 | no         |

Business 1 has **2** above-average event types.

So it qualifies.

---

### Business 2

Rows:

| event_type | occurrences | avg | above avg? |
| ---------- | ----------: | --: | ---------- |
| ads        |           7 |   8 | no         |
| page views |          12 | 7.5 | yes        |

Business 2 has only **1** above-average event type.

So it does not qualify.

---

### Business 3

Rows:

| event_type | occurrences | avg | above avg? |
| ---------- | ----------: | --: | ---------- |
| reviews    |           3 |   5 | no         |
| ads        |           6 |   8 | no         |

Business 3 has **0** qualifying event types.

So it does not qualify.

---

## Final result

| business_id |
| ----------- |
| 1           |

---

## Strengths of Approach 1

- easy to understand
- clear separation between:
  - computing event averages
  - comparing rows
  - identifying active businesses

### Tradeoff

- requires a join-back step after aggregation

---

# Approach 2: Using the Window Function

## Core idea

Instead of computing averages in a separate grouped subquery and joining back, this approach uses a window function to place the average value directly onto each row.

That means each row in the result contains:

- `business_id`
- `event_type`
- `occurrences`
- average occurrences for that event type

Then the rest is the same:

1. keep rows where `occurrences > avg`
2. group by `business_id`
3. keep businesses with more than one such row

---

## Step 1: Add average occurrences to every row

```sql
SELECT
  business_id,
  event_type,
  occurrences,
  AVG(occurrences) OVER (PARTITION BY event_type) AS avg
FROM
  Events
```

---

## Why this works

### `PARTITION BY event_type`

The window function computes an average separately inside each event type group.

So each row gets the average occurrences of its own event type.

For the sample, the annotated result looks like this:

| business_id | event_type | occurrences | avg |
| ----------: | ---------- | ----------: | --: |
|           1 | reviews    |           7 |   5 |
|           3 | reviews    |           3 |   5 |
|           1 | ads        |          11 |   8 |
|           2 | ads        |           7 |   8 |
|           3 | ads        |           6 |   8 |
|           1 | page views |           3 | 7.5 |
|           2 | page views |          12 | 7.5 |

Now each row already contains its comparison target.

---

## Step 2: Filter rows above average and count by business

```sql
SELECT
  business_id
FROM
  (
    SELECT
      business_id,
      event_type,
      occurrences,
      AVG(occurrences) OVER (PARTITION BY event_type) AS avg
    FROM
      Events
  ) t0
WHERE
  occurrences > avg
GROUP BY
  business_id
HAVING
  COUNT(*) > 1
```

---

## Why this works

### `WHERE occurrences > avg`

This keeps only rows where the business exceeds the average for that event type.

### `GROUP BY business_id`

Now we count how many such qualifying rows each business has.

### `HAVING COUNT(*) > 1`

Businesses with more than one qualifying row are active.

---

## Final query for Approach 2

```sql
SELECT
  business_id
FROM
  (
    SELECT
      business_id,
      event_type,
      occurrences,
      AVG(occurrences) OVER (PARTITION BY event_type) AS avg
    FROM
      Events
  ) t0
WHERE
  occurrences > avg
GROUP BY
  business_id
HAVING
  COUNT(*) > 1;
```

---

## Strengths of Approach 2

- avoids an explicit join
- keeps the comparison logic very local to each row
- elegant use of analytic functions

### Tradeoff

- requires familiarity with window functions
- may feel less direct than grouped-aggregate + join for some readers

---

# Comparing the two approaches

## Approach 1: Aggregate function + join

### Best when

- you want the average-by-event-type logic to be explicit
- you prefer traditional aggregation patterns

### Pros

- very readable
- easy to explain in stages

### Cons

- needs a join-back step

---

## Approach 2: Window function

### Best when

- you want to compute the average directly beside each row
- you prefer analytic-function style solutions

### Pros

- no extra join
- compact and elegant

### Cons

- window functions can feel more advanced if you are not used to them

---

# Important SQL concepts used here

## 1. `AVG(occurrences)`

Used to compute event-type averages.

## 2. `GROUP BY event_type`

Used in Approach 1 to calculate average occurrences per event type.

## 3. Window function

```sql
AVG(occurrences) OVER (PARTITION BY event_type)
```

Used in Approach 2 to place event-type averages directly on each row.

## 4. `HAVING COUNT(*) > 1`

Used in both approaches to identify businesses with more than one above-average event type.

---

# Why `COUNT(*) > 1` is the right condition

The definition says:

> An active business is a business that has more than one event_type such that their occurrences is strictly greater than the average activity for that event.

That means a business must qualify in **at least two event types**.

So after filtering to above-average rows, the correct condition is:

```sql
HAVING COUNT(*) > 1
```

If the count were only 1, the business would not be active.

---

# Complexity discussion

Let `n` be the number of rows in `Events`.

## Approach 1

- compute grouped averages by event type
- join back to the base table
- group by business

## Approach 2

- compute per-row window averages
- filter
- group by business

In practice, both are standard SQL solutions and efficient for typical problem sizes.

---

# Key takeaways

1. The comparison is event-type-specific, not global.
2. First compute the average occurrences for each event type.
3. Then keep only rows where a business exceeds that event-type average.
4. A business is active only if it does this for more than one event type.
5. Approach 1 uses grouped aggregation + join; Approach 2 uses a window function.

---

## Final accepted implementations

### Approach 1: Using the Aggregate Function

```sql
SELECT
  e.business_id
FROM
  Events e
  JOIN (
    SELECT
      event_type,
      AVG(occurrences) AS avg
    FROM
      Events
    GROUP BY
      event_type
  ) t0 ON e.event_type = t0.event_type
  AND e.occurrences > t0.avg
GROUP BY
  e.business_id
HAVING
  COUNT(*) > 1;
```

### Approach 2: Using the Window Function

```sql
SELECT
  business_id
FROM
  (
    SELECT
      business_id,
      event_type,
      occurrences,
      AVG(occurrences) OVER (PARTITION BY event_type) AS avg
    FROM
      Events
  ) t0
WHERE
  occurrences > avg
GROUP BY
  business_id
HAVING
  COUNT(*) > 1;
```
