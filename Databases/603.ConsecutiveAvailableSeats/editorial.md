# 603. Consecutive Available Seats

## Approach: Using Self Join and `ABS()`

## Core idea

This problem asks us to return all seat ids that belong to a block of **consecutive available seats**.

A seat is available when:

```sql
free = 1
```

Since the problem is about checking whether one row is next to another row in the **same table**, a natural approach is to use a **self join**.

A self join means joining a table with itself so that one row can be compared with another row from the same table.

In this solution:

- one copy of the table is called `a`
- another copy is called `b`

Then we compare the seat ids from both copies.

---

## Why a self join makes sense

We need to know whether a seat has a neighboring seat that is also free.

For a seat to be part of a valid consecutive block, there must exist another seat such that:

1. the two seats are next to each other
2. both seats are free

That means we need to compare rows inside the same table.

This is exactly what a self join is good for.

---

## Step 1: Understand the Cartesian product

If we join the `Cinema` table with itself without any filtering condition, every row from `a` gets paired with every row from `b`.

```sql
SELECT a.seat_id, a.free, b.seat_id, b.free
FROM cinema a
JOIN cinema b;
```

This creates the Cartesian product.

For the sample input:

| seat_id | free |
| ------- | ---- |
| 1       | 1    |
| 2       | 0    |
| 3       | 1    |
| 4       | 1    |
| 5       | 1    |

the full join contains combinations like:

| a.seat_id | a.free | b.seat_id | b.free |
| --------- | ------ | --------- | ------ |
| 1         | 1      | 1         | 1      |
| 2         | 0      | 1         | 1      |
| 3         | 1      | 1         | 1      |
| 4         | 1      | 1         | 1      |
| 5         | 1      | 1         | 1      |
| 1         | 1      | 2         | 0      |
| 2         | 0      | 2         | 0      |
| 3         | 1      | 2         | 0      |
| 4         | 1      | 2         | 0      |
| 5         | 1      | 2         | 0      |
| 1         | 1      | 3         | 1      |
| 2         | 0      | 3         | 1      |
| 3         | 1      | 3         | 1      |
| 4         | 1      | 3         | 1      |
| 5         | 1      | 3         | 1      |
| 1         | 1      | 4         | 1      |
| 2         | 0      | 4         | 1      |
| 3         | 1      | 4         | 1      |
| 4         | 1      | 4         | 1      |
| 5         | 1      | 4         | 1      |
| 1         | 1      | 5         | 1      |
| 2         | 0      | 5         | 1      |
| 3         | 1      | 5         | 1      |
| 4         | 1      | 5         | 1      |
| 5         | 1      | 5         | 1      |

That is far more than we need.

So the next step is to keep only the pairs that represent **adjacent free seats**.

---

## Step 2: Keep only adjacent free seat pairs

Two seats are consecutive when their ids differ by exactly 1.

That condition can be written as:

```sql
ABS(a.seat_id - b.seat_id) = 1
```

### Why `ABS()`?

Because seat `4` is consecutive with seat `3`, and seat `3` is also consecutive with seat `4`.

Without `ABS()`, we would have to check both:

```sql
a.seat_id - b.seat_id = 1
```

and

```sql
b.seat_id - a.seat_id = 1
```

Using `ABS()` simplifies both cases into one condition:

```sql
ABS(a.seat_id - b.seat_id) = 1
```

We also need both seats to be free:

```sql
a.free = true
AND b.free = true
```

So the filtered join becomes:

```sql
SELECT a.seat_id, a.free, b.seat_id, b.free
FROM cinema a
JOIN cinema b
  ON ABS(a.seat_id - b.seat_id) = 1
 AND a.free = true
 AND b.free = true;
```

---

## Result after applying the filter

Using the sample input, this produces:

| a.seat_id | a.free | b.seat_id | b.free |
| --------- | ------ | --------- | ------ |
| 4         | 1      | 3         | 1      |
| 3         | 1      | 4         | 1      |
| 5         | 1      | 4         | 1      |
| 4         | 1      | 5         | 1      |

These are exactly the neighboring free seat pairs.

Notice:

- `3` pairs with `4`
- `4` pairs with `3`
- `4` pairs with `5`
- `5` pairs with `4`

This confirms that seats `3`, `4`, and `5` form a consecutive free block.

---

## Step 3: Return only the required column

The problem only asks for:

```sql
seat_id
```

So we select just `a.seat_id`.

```sql
SELECT a.seat_id
FROM cinema a
JOIN cinema b
  ON ABS(a.seat_id - b.seat_id) = 1
 AND a.free = true
 AND b.free = true;
```

But this still has duplicates.

---

## Why duplicates appear

Seat `4` appears twice:

- once because it is next to seat `3`
- once because it is next to seat `5`

So if we return `a.seat_id` directly, we may get repeated seat ids.

To remove duplicates, we use:

```sql
DISTINCT
```

---

## Final accepted query

```sql
SELECT DISTINCT a.seat_id
FROM cinema a
JOIN cinema b
  ON ABS(a.seat_id - b.seat_id) = 1
 AND a.free = true
 AND b.free = true
ORDER BY a.seat_id;
```

---

## Full explanation of the final query

### `FROM cinema a JOIN cinema b`

Create two aliases of the same table so we can compare one seat against another.

### `ABS(a.seat_id - b.seat_id) = 1`

Keep only seat pairs that are adjacent.

### `a.free = true AND b.free = true`

Keep only pairs where both seats are free.

### `SELECT DISTINCT a.seat_id`

Return the seat ids from the first alias, removing duplicates.

### `ORDER BY a.seat_id`

Return the result in ascending order, as required.

---

## Walkthrough on the sample

### Input

| seat_id | free |
| ------- | ---- |
| 1       | 1    |
| 2       | 0    |
| 3       | 1    |
| 4       | 1    |
| 5       | 1    |

### Consecutive free checks

- Seat `1`:
  - neighbor `2` is occupied
  - so `1` is not part of a consecutive free pair

- Seat `2`:
  - occupied, so excluded

- Seat `3`:
  - neighbor `4` is free
  - include `3`

- Seat `4`:
  - neighbors `3` and `5` are free
  - include `4`

- Seat `5`:
  - neighbor `4` is free
  - include `5`

### Final output

| seat_id |
| ------- |
| 3       |
| 4       |
| 5       |

---

## Why this matches the problem statement

The problem says the test cases guarantee that more than two seats are consecutively available.

So if a seat belongs to a consecutive block of free seats, it will always have at least one adjacent free neighbor.

That means checking for an adjacent free seat is enough to identify seats in the block.

This is why the self-join approach works here.

---

## Important observation

This solution returns all seats that belong to at least one adjacent free pair.

Because the problem guarantees a block longer than two seats exists, this is sufficient.

In broader variants of the problem, it is worth being careful about whether the requirement is:

- at least one adjacent free neighbor
- or belonging to a block of length at least 2
- or belonging to a block of length at least 3

This solution fits the exact accepted interpretation for this problem.

---

## Alternative without `ABS()`

Instead of using `ABS()`, we could write:

```sql
SELECT DISTINCT a.seat_id
FROM cinema a
JOIN cinema b
  ON (a.seat_id = b.seat_id + 1 OR a.seat_id = b.seat_id - 1)
 AND a.free = true
 AND b.free = true
ORDER BY a.seat_id;
```

This is logically equivalent, but `ABS()` is shorter and cleaner.

---

## Complexity

Let `n` be the number of rows in `Cinema`.

### Time Complexity

A naive self-join can compare many row pairs, so conceptually this is often described as:

```text
O(n^2)
```

though the database engine may optimize it.

### Space Complexity

Extra space depends on the execution plan, but conceptually:

```text
O(1)
```

ignoring the output.

---

## Key takeaways

1. This is a same-table comparison problem, so a self join is a natural fit.
2. Two seats are consecutive if their ids differ by exactly 1.
3. `ABS(a.seat_id - b.seat_id) = 1` captures adjacency in both directions.
4. Both seats in the pair must be free.
5. `DISTINCT` is required because a seat can match more than one neighbor.
6. The final accepted query is:

```sql
SELECT DISTINCT a.seat_id
FROM cinema a
JOIN cinema b
  ON ABS(a.seat_id - b.seat_id) = 1
 AND a.free = true
 AND b.free = true
ORDER BY a.seat_id;
```
