# 602. Friend Requests II: Who Has the Most Friends

## Detailed Summary of Two Accepted Approaches

This problem asks us to find:

- the person with the **largest number of friends**
- the number of friends they have

The output format is:

| id | num |

where:

- `id` = person id
- `num` = number of friends

The table is:

```text
RequestAccepted(requester_id, accepter_id, accept_date)
```

Each row means a friendship was formed between `requester_id` and `accepter_id`.

Since friendship is mutual, **both people gain one friend** from each accepted request.

That is the key observation behind both approaches.

---

# Core idea

A person can appear in two different roles:

1. as `requester_id`
2. as `accepter_id`

If we want to know how many friends a person has, we need to count how many times that person's id appears in **either column**.

So the natural strategy is:

1. combine `requester_id` and `accepter_id` into one single list of ids
2. count how many times each id appears
3. return the id(s) with the highest count

---

# Why `UNION ALL` is used

We must preserve every friendship contribution from every accepted request.

So we combine the two columns like this:

```sql
SELECT requester_id AS id
FROM RequestAccepted
UNION ALL
SELECT accepter_id AS id
FROM RequestAccepted
```

This creates one column called `id`, containing every participant in every accepted friendship.

It is important to use **`UNION ALL`**, not `UNION`.

### Why?

- `UNION` removes duplicates
- `UNION ALL` keeps all rows

In this problem, repeated appearances are meaningful because they represent how many total friendships a user has.

If we used `UNION`, multiple appearances of the same id could be collapsed incorrectly, and the count would become wrong.

---

# Example walkthrough

Input:

| requester_id | accepter_id | accept_date |
| ------------ | ----------- | ----------- |
| 1            | 2           | 2016/06/03  |
| 1            | 3           | 2016/06/08  |
| 2            | 3           | 2016/06/08  |
| 3            | 4           | 2016/06/09  |

Friendships formed are:

- 1 <-> 2
- 1 <-> 3
- 2 <-> 3
- 3 <-> 4

Now combine both columns using `UNION ALL`:

```text
requester_id values: 1, 1, 2, 3
accepter_id values : 2, 3, 3, 4
```

Combined list:

| id  |
| --- |
| 1   |
| 1   |
| 2   |
| 3   |
| 2   |
| 3   |
| 3   |
| 4   |

Now count each id:

- 1 -> 2 times
- 2 -> 2 times
- 3 -> 3 times
- 4 -> 1 time

So the answer is:

| id  | num |
| --- | --- |
| 3   | 3   |

---

# Approach 1: Using `UNION ALL` + `GROUP BY` + `ORDER BY` + `LIMIT`

## Core idea

This approach is very direct.

1. Combine both columns into one list of ids
2. Count how many times each id appears
3. Sort by count descending
4. Return the first row

Because the problem guarantees that **only one person has the most friends**, `LIMIT 1` is enough.

---

## Step 1: Combine both columns into one list

We place the combined ids in a CTE called `all_ids`.

```sql
WITH all_ids AS (
    SELECT requester_id AS id
    FROM RequestAccepted
    UNION ALL
    SELECT accepter_id AS id
    FROM RequestAccepted
)
```

This produces one unified id column.

---

## Step 2: Count frequency of each id

Now we count how many times each id appears.

```sql
SELECT
    id,
    COUNT(id) AS num
FROM all_ids
GROUP BY id
```

This gives the number of friends for each person.

---

## Step 3: Sort by the count descending

We want the person with the most friends, so we sort by count descending.

```sql
ORDER BY COUNT(id) DESC
```

This places the highest count first.

---

## Step 4: Return only the top row

Since the problem guarantees a unique maximum, we keep just one row.

```sql
LIMIT 1
```

---

## Final query for Approach 1

```sql
WITH all_ids AS (
    SELECT requester_id AS id
    FROM RequestAccepted
    UNION ALL
    SELECT accepter_id AS id
    FROM RequestAccepted
)
SELECT
    id,
    COUNT(id) AS num
FROM all_ids
GROUP BY id
ORDER BY COUNT(id) DESC
LIMIT 1;
```

---

## Why this works

Every accepted request contributes:

- one friend count to the requester
- one friend count to the accepter

By flattening both columns into one stream of ids, counting occurrences becomes exactly the same as counting friendships.

The id with the highest frequency is the person with the most friends.

---

## Strengths of Approach 1

- short
- easy to understand
- efficient
- perfect when the problem guarantees one unique top answer

### Limitation

If multiple people tie for the maximum number of friends, `LIMIT 1` returns only one of them.

So this approach is not sufficient for the follow-up version.

---

# Approach 2: Using `UNION ALL` + `RANK()`

## Core idea

This approach begins exactly the same way:

1. combine requester and accepter ids into one list
2. count how many times each id appears

But instead of taking only the first row with `LIMIT 1`, it assigns a rank based on friend count.

Then it returns all rows with rank 1.

This makes it suitable for the follow-up case where multiple users may tie for the highest friend count.

---

## Step 1: Combine both columns

Same as before:

```sql
WITH all_ids AS (
    SELECT requester_id AS id
    FROM RequestAccepted
    UNION ALL
    SELECT accepter_id AS id
    FROM RequestAccepted
)
```

---

## Step 2: Count friends per id and rank them

Inside a subquery, we calculate:

- `COUNT(id)` as `num`
- `RANK() OVER (ORDER BY COUNT(id) DESC)` as `rnk`

```sql
SELECT
    id,
    COUNT(id) AS num,
    RANK() OVER (ORDER BY COUNT(id) DESC) AS rnk
FROM all_ids
GROUP BY id
```

### What this does

- `GROUP BY id` groups all appearances of the same user
- `COUNT(id)` gives number of friends
- `RANK()` orders users by descending friend count

The top user(s) get:

```text
rnk = 1
```

---

## Step 3: Keep only the top-ranked rows

Now we filter:

```sql
WHERE rnk = 1
```

This returns all users tied for the maximum number of friends.

---

## Final query for Approach 2

```sql
WITH all_ids AS (
    SELECT requester_id AS id
    FROM RequestAccepted
    UNION ALL
    SELECT accepter_id AS id
    FROM RequestAccepted
)
SELECT id, num
FROM (
    SELECT
        id,
        COUNT(id) AS num,
        RANK() OVER (ORDER BY COUNT(id) DESC) AS rnk
    FROM all_ids
    GROUP BY id
) t0
WHERE rnk = 1;
```

---

## Why `RANK()` is useful

Suppose the counts were:

| id  | num |
| --- | --- |
| 3   | 5   |
| 4   | 5   |
| 2   | 3   |
| 1   | 2   |

Then `RANK()` would produce:

| id  | num | rnk |
| --- | --- | --- |
| 3   | 5   | 1   |
| 4   | 5   | 1   |
| 2   | 3   | 3   |
| 1   | 2   | 4   |

Filtering on `rnk = 1` returns both `3` and `4`.

That is why this method handles the real-world tie case naturally.

---

# Comparing the two approaches

## Approach 1: `ORDER BY` + `LIMIT`

### Best when

- the problem guarantees a unique winner
- you want the shortest solution

### Pros

- simple
- compact
- easy to read

### Cons

- does not handle ties correctly

---

## Approach 2: `RANK()`

### Best when

- ties are possible
- you want a more general solution

### Pros

- naturally supports multiple top users
- reusable for follow-up versions
- demonstrates good window function knowledge

### Cons

- slightly more verbose
- may be less familiar if window functions are new to you

---

# Important note about counting friendships

Because `(requester_id, accepter_id)` is the primary key, each accepted friendship pair appears only once in the table.

Each row corresponds to one friendship relation between two users.

So counting occurrences after `UNION ALL` is valid.

If the table could contain duplicate rows, we would need to be more careful.
But under this schema, the method is sound.

---

# Why not use `UNION` instead of `UNION ALL`?

Consider this example:

```text
requester_id values: 1, 1, 2, 3
accepter_id values : 2, 3, 3, 4
```

If you use `UNION`, duplicates across the combined list are removed.

That would collapse repeated ids like `1` or `3`, which destroys the frequency information we need.

We are not trying to find distinct people.
We are trying to find **how many times each person participates in accepted friendships**.

So we must preserve duplicates.

That is exactly what `UNION ALL` does.

---

# Expanded illustration of Approach 1

### CTE result

```sql
WITH all_ids AS (
    SELECT requester_id AS id
    FROM RequestAccepted
    UNION ALL
    SELECT accepter_id AS id
    FROM RequestAccepted
)
SELECT *
FROM all_ids;
```

Result:

| id  |
| --- |
| 1   |
| 1   |
| 2   |
| 3   |
| 2   |
| 3   |
| 3   |
| 4   |

### Grouped result

```sql
SELECT id, COUNT(id) AS num
FROM all_ids
GROUP BY id;
```

Result:

| id  | num |
| --- | --- |
| 1   | 2   |
| 2   | 2   |
| 3   | 3   |
| 4   | 1   |

### Sorted descending

```sql
ORDER BY COUNT(id) DESC
```

Result:

| id  | num |
| --- | --- |
| 3   | 3   |
| 1   | 2   |
| 2   | 2   |
| 4   | 1   |

### Final after `LIMIT 1`

| id  | num |
| --- | --- |
| 3   | 3   |

---

# Expanded illustration of Approach 2

### Ranked grouped result

```sql
SELECT
    id,
    COUNT(id) AS num,
    RANK() OVER (ORDER BY COUNT(id) DESC) AS rnk
FROM all_ids
GROUP BY id;
```

Result:

| id  | num | rnk |
| --- | --- | --- |
| 3   | 3   | 1   |
| 1   | 2   | 2   |
| 2   | 2   | 2   |
| 4   | 1   | 4   |

Then:

```sql
WHERE rnk = 1
```

returns:

| id  | num |
| --- | --- |
| 3   | 3   |

---

# Complexity

Let `n` be the number of rows in `RequestAccepted`.

After `UNION ALL`, there are `2n` rows in the combined stream.

## Time Complexity

The solution must:

- scan the table
- produce the combined list
- group by id
- optionally rank or sort

A practical interview-style summary is:

```text
O(n log n)
```

because grouping/sorting may dominate depending on the SQL engine.

## Space Complexity

```text
O(n)
```

for the combined stream and grouped counts.

---

# Final accepted implementations

## Approach 1

```sql
WITH all_ids AS (
   SELECT requester_id AS id
   FROM RequestAccepted
   UNION ALL
   SELECT accepter_id AS id
   FROM RequestAccepted
)
SELECT id,
   COUNT(id) AS num
FROM all_ids
GROUP BY id
ORDER BY COUNT(id) DESC
LIMIT 1;
```

## Approach 2

```sql
WITH all_ids AS (
   SELECT requester_id AS id
   FROM RequestAccepted
   UNION ALL
   SELECT accepter_id AS id
   FROM RequestAccepted
)
SELECT id, num
FROM (
   SELECT
      id,
      COUNT(id) AS num,
      RANK() OVER (ORDER BY COUNT(id) DESC) AS rnk
   FROM all_ids
   GROUP BY id
) t0
WHERE rnk = 1;
```

---

# Key takeaways

1. Each accepted friendship contributes one friend to both users.
2. So the correct strategy is to combine `requester_id` and `accepter_id` into one list.
3. Use `UNION ALL`, not `UNION`, because frequency matters.
4. `GROUP BY id` + `COUNT(id)` gives friend count per user.
5. `ORDER BY ... LIMIT 1` works when the winner is unique.
6. `RANK()` is the better choice when ties are possible.
