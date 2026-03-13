# 1149. Article Views II — Detailed Summary

## Problem Goal

We need to find all people who viewed **more than one distinct article on the same date**.

The key points are:

- A person is identified by `viewer_id`
- The condition must hold **within the same `view_date`**
- Duplicate rows may exist
- So we must be careful to count **distinct articles**, not raw rows

The final output should be:

- one column named `id`
- sorted in ascending order

---

# Approach 1: `GROUP BY` with `HAVING`

## Main Idea

This is the most natural aggregation-based solution.

We group rows by:

- `viewer_id`
- `view_date`

That grouping tells us:

> for this viewer, on this date, how many distinct articles were viewed?

Then we keep only those groups where the count of distinct articles is greater than 1.

Finally, since the same viewer might satisfy the condition on multiple dates, we select `DISTINCT viewer_id`.

---

## Why This Works

Suppose a viewer reads:

- article 1 on 2019-08-01
- article 3 on 2019-08-01

Then after grouping by `(viewer_id, view_date)`, that viewer-date group contains 2 distinct articles, so it qualifies.

Now suppose another viewer has duplicate rows like:

- article 3 on 2019-07-21
- article 3 on 2019-07-21

That is still only **one distinct article**, so that viewer does **not** qualify.

That is exactly why `COUNT(DISTINCT article_id)` is required.

---

## Step-by-Step Breakdown

### Original `Views` Table

| article_id | author_id | viewer_id | view_date  |
| ---------- | --------: | --------: | ---------- |
| 1          |         3 |         5 | 2019-08-01 |
| 3          |         4 |         5 | 2019-08-01 |
| 1          |         3 |         6 | 2019-08-02 |
| 2          |         7 |         7 | 2019-08-01 |
| 2          |         7 |         6 | 2019-08-02 |
| 4          |         7 |         1 | 2019-07-22 |
| 3          |         4 |         4 | 2019-07-21 |
| 3          |         4 |         4 | 2019-07-21 |

---

## Step 1: Group by Viewer and Date

A conceptual grouping query is:

```sql
SELECT
  viewer_id,
  view_date,
  ARRAY_AGG(article_id) AS articles
FROM Views
GROUP BY viewer_id, view_date;
```

### Conceptual Result

| viewer_id | view_date  | articles |
| --------: | ---------- | -------- |
|         5 | 2019-08-01 | [1, 3]   |
|         6 | 2019-08-02 | [1, 2]   |
|         7 | 2019-08-01 | [2]      |
|         1 | 2019-07-22 | [4]      |
|         4 | 2019-07-21 | [3, 3]   |

This is just for intuition. We do not need `ARRAY_AGG` in the final solution.

It helps visualize that each `(viewer_id, view_date)` combination becomes one group.

---

## Step 2: Count Distinct Articles in Each Group

```sql
SELECT
  viewer_id,
  view_date,
  COUNT(DISTINCT article_id) AS article_count
FROM Views
GROUP BY viewer_id, view_date;
```

### Result

| viewer_id | view_date  | article_count |
| --------: | ---------- | ------------: |
|         5 | 2019-08-01 |             2 |
|         6 | 2019-08-02 |             2 |
|         7 | 2019-08-01 |             1 |
|         1 | 2019-07-22 |             1 |
|         4 | 2019-07-21 |             1 |

Notice viewer `4` has duplicate rows for article `3`, but `COUNT(DISTINCT article_id)` correctly gives `1`.

---

## Step 3: Filter Only Groups with More Than One Article

```sql
SELECT
  viewer_id,
  view_date,
  COUNT(DISTINCT article_id) AS article_count
FROM Views
GROUP BY viewer_id, view_date
HAVING COUNT(DISTINCT article_id) > 1;
```

### Result

| viewer_id | view_date  | article_count |
| --------: | ---------- | ------------: |
|         5 | 2019-08-01 |             2 |
|         6 | 2019-08-02 |             2 |

These are exactly the viewer-date groups we want.

---

## Step 4: Extract Distinct Viewer IDs

Now we only need the viewer IDs.

```sql
SELECT DISTINCT viewer_id AS id
FROM Views
GROUP BY viewer_id, view_date
HAVING COUNT(DISTINCT article_id) > 1;
```

### Result

|  id |
| --: |
|   5 |
|   6 |

---

## Step 5: Sort the Output

```sql
SELECT DISTINCT viewer_id AS id
FROM Views
GROUP BY viewer_id, view_date
HAVING COUNT(DISTINCT article_id) > 1
ORDER BY viewer_id;
```

This produces the final required output.

---

## Final Implementation — Approach 1

```sql
SELECT DISTINCT viewer_id AS id
FROM Views
GROUP BY viewer_id, view_date
HAVING COUNT(DISTINCT article_id) > 1
ORDER BY viewer_id;
```

---

## Detailed Reasoning About Duplicates

This problem explicitly says the table may contain duplicate rows.

That creates an important trap.

### Wrong idea

If we write:

```sql
COUNT(article_id)
```

instead of:

```sql
COUNT(DISTINCT article_id)
```

then duplicate rows would inflate the count.

For example, viewer `4` has:

- article 3 on 2019-07-21
- article 3 on 2019-07-21

Using `COUNT(article_id)` gives `2`, which would incorrectly make viewer `4` qualify.

But the problem asks for viewers who saw **more than one article**, not viewers who generated more than one row.

So `DISTINCT` is necessary.

---

## Why `DISTINCT viewer_id` Is Needed in the Outer `SELECT`

Imagine a viewer viewed multiple articles on:

- 2019-08-01
- 2019-08-02

Then grouping by `(viewer_id, view_date)` would produce multiple qualifying groups for the same viewer.

But the output should list each person only once.

So we use:

```sql
SELECT DISTINCT viewer_id AS id
```

---

## Approach 1 Complexity

Let `n` be the number of rows in `Views`.

### Time Complexity

Roughly:

```text
O(n log n)
```

or engine-dependent hashing/grouping complexity.

The expensive part is grouping and counting distinct values.

### Space Complexity

Roughly proportional to the number of groups and distinct-tracking structures used by the DB engine.

---

# Approach 2: Self-Join

## Main Idea

This approach tries to detect whether a viewer has at least **two different articles** on the same date.

Instead of aggregating first, it pairs rows with other rows from the same viewer on the same date.

If we can find two rows such that:

- same `viewer_id`
- same `view_date`
- different `article_id`

then that viewer qualifies.

---

## Why This Works

Suppose viewer `5` viewed article `1` and article `3` on `2019-08-01`.

If we join the table to itself on:

- same viewer
- same date

then rows for article `1` and article `3` can match each other.

That proves the viewer saw more than one distinct article that day.

---

## Step-by-Step Breakdown

### Original Table

| article_id | author_id | viewer_id | view_date  |
| ---------- | --------: | --------: | ---------- |
| 1          |         3 |         5 | 2019-08-01 |
| 3          |         4 |         5 | 2019-08-01 |
| 1          |         3 |         6 | 2019-08-02 |
| 2          |         7 |         7 | 2019-08-01 |
| 2          |         7 |         6 | 2019-08-02 |
| 4          |         7 |         1 | 2019-07-22 |
| 3          |         4 |         4 | 2019-07-21 |
| 3          |         4 |         4 | 2019-07-21 |

---

## Step 1: Self-Join on Viewer and Date

```sql
SELECT
  v1.article_id AS article_id_1,
  v2.article_id AS article_id_2,
  v1.viewer_id,
  v1.view_date
FROM Views v1
JOIN Views v2
  ON v1.viewer_id = v2.viewer_id
 AND v1.view_date = v2.view_date;
```

### Intuition

For each row, this join finds all other rows with the same viewer and same date.

If a viewer saw articles A and B on that date, the join can produce:

- A with A
- A with B
- B with A
- B with B

So at this stage we get all possible combinations, including redundant ones.

---

## Step 2: Remove Self-Pairs and Duplicated Mirror Pairs

We only want evidence of **two distinct articles**.

So we can enforce:

```sql
v1.article_id < v2.article_id
```

This does two things:

1. removes self-pairs like `(A, A)`
2. keeps only one ordering of article pairs, so `(A, B)` is kept but `(B, A)` is discarded

### Query

```sql
SELECT
  v1.article_id AS article_id_1,
  v2.article_id AS article_id_2,
  v1.viewer_id,
  v1.view_date
FROM Views v1
JOIN Views v2
  ON v1.viewer_id = v2.viewer_id
 AND v1.view_date = v2.view_date
 AND v1.article_id < v2.article_id;
```

### Result

| article_id_1 | article_id_2 | viewer_id | view_date  |
| -----------: | -----------: | --------: | ---------- |
|            1 |            3 |         5 | 2019-08-01 |
|            1 |            2 |         6 | 2019-08-02 |

This tells us:

- viewer `5` saw at least two different articles on 2019-08-01
- viewer `6` saw at least two different articles on 2019-08-02

---

## Step 3: Extract Distinct Viewer IDs

```sql
SELECT DISTINCT v1.viewer_id
FROM Views v1
JOIN Views v2
  ON v1.viewer_id = v2.viewer_id
 AND v1.view_date = v2.view_date
 AND v1.article_id < v2.article_id;
```

### Result

| viewer_id |
| --------: |
|         5 |
|         6 |

---

## Step 4: Rename and Sort

```sql
SELECT DISTINCT v1.viewer_id AS id
FROM Views v1
JOIN Views v2
  ON v1.viewer_id = v2.viewer_id
 AND v1.view_date = v2.view_date
 AND v1.article_id < v2.article_id
ORDER BY v1.viewer_id;
```

This gives the final required output.

---

## Final Implementation — Approach 2

```sql
SELECT DISTINCT v1.viewer_id AS id
FROM Views v1
JOIN Views v2
  ON v1.viewer_id = v2.viewer_id
 AND v1.view_date = v2.view_date
 AND v1.article_id < v2.article_id
ORDER BY v1.viewer_id;
```

---

## Why `v1.article_id < v2.article_id` Is Better Than `!=`

You might think this would work:

```sql
v1.article_id != v2.article_id
```

It would remove self-pairs, but it would still produce both:

- `(1, 3)`
- `(3, 1)`

That creates duplicate logical evidence.

Using:

```sql
v1.article_id < v2.article_id
```

keeps only one direction, which is cleaner and more efficient.

---

## Important Detail About Duplicate Rows

Consider viewer `4`:

- article `3`
- article `3`

Even with duplicates, the condition:

```sql
v1.article_id < v2.article_id
```

fails because `3 < 3` is false.

So viewer `4` correctly does not qualify.

This is why the self-join approach also handles duplicate identical article rows correctly.

---

## Approach 2 Complexity

Let `n` be the number of rows.

### Time Complexity

A self-join can be much more expensive than aggregation.

In the worst case, it can behave closer to:

```text
O(n^2)
```

depending on data distribution and indexing.

### Space Complexity

The intermediate join result can become large because each viewer-date group may generate many row pairs.

---

# Comparing the Two Approaches

## Approach 1: `GROUP BY` + `HAVING`

### Strengths

- Directly matches the problem statement
- Easier to read
- Naturally handles duplicates via `COUNT(DISTINCT article_id)`
- Usually preferred in interviews and production SQL

### Weaknesses

- Requires understanding grouping and `HAVING`

---

## Approach 2: Self-Join

### Strengths

- Useful when you want to explicitly prove the existence of two distinct rows
- Good for understanding relational pairing logic

### Weaknesses

- Usually less efficient than aggregation
- More complex to reason about
- Can generate many intermediate pairs

---

## Recommended Solution

For this problem, the recommended solution is:

```sql
SELECT DISTINCT viewer_id AS id
FROM Views
GROUP BY viewer_id, view_date
HAVING COUNT(DISTINCT article_id) > 1
ORDER BY viewer_id;
```

This is the cleanest and most natural answer.

---

# Example Walkthrough

Using the sample input:

## Viewer 5

On `2019-08-01`, viewed:

- article 1
- article 3

Distinct count = 2, so qualifies.

## Viewer 6

On `2019-08-02`, viewed:

- article 1
- article 2

Distinct count = 2, so qualifies.

## Viewer 4

On `2019-07-21`, has:

- article 3
- article 3

Distinct count = 1, so does not qualify.

## Viewer 1

Only viewed article 4 on one date, so does not qualify.

## Viewer 7

Only viewed article 2 on one date, so does not qualify.

Final output:

|  id |
| --: |
|   5 |
|   6 |

---

# Final Answers

## Approach 1

```sql
SELECT DISTINCT viewer_id AS id
FROM Views
GROUP BY viewer_id, view_date
HAVING COUNT(DISTINCT article_id) > 1
ORDER BY viewer_id;
```

## Approach 2

```sql
SELECT DISTINCT v1.viewer_id AS id
FROM Views v1
JOIN Views v2
  ON v1.viewer_id = v2.viewer_id
 AND v1.view_date = v2.view_date
 AND v1.article_id < v2.article_id
ORDER BY v1.viewer_id;
```

---

# Key Takeaways

- Group by `viewer_id` and `view_date`
- Count **distinct** `article_id`
- Use `HAVING` to keep only groups with more than one article
- Duplicates in the table make `DISTINCT` essential
- Self-join also works, but aggregation is cleaner and usually better

---
