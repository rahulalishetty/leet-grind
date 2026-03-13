# 626. Exchange Seats

## Detailed Summary of Two Accepted Approaches

We need to swap the seat ids of every two consecutive students:

- `1 <-> 2`
- `3 <-> 4`
- `5 <-> 6`
- ...

If the total number of students is odd, the last student remains unchanged.

So the problem is not about changing the `student` values directly.
It is about producing the result as if the seat ids were swapped pairwise.

This summary covers two accepted approaches:

1. **Using `CASE`**
2. **Using bit manipulation and `COALESCE()`**

---

# Core observation

For seat ids:

- odd ids usually move to the next id
- even ids move to the previous id

So the mapping is:

```text
1 -> 2
2 -> 1
3 -> 4
4 -> 3
5 -> 6
6 -> 5
...
```

But there is one exception:

- if the total number of rows is odd, the last odd id should stay unchanged

For example, if the last seat id is `5`, then:

```text
5 -> 5
```

instead of:

```text
5 -> 6
```

That is the only special case.

---

# Approach I: Using flow control statement `CASE`

## Core idea

We directly compute the new id for each student by checking:

1. is the current id odd or even?
2. if it is odd, is it the last seat?

This is a very natural conditional-logic solution.

---

## Step 1: Count the total number of seats

To know whether an odd id is the last seat, we first need the total number of rows.

```sql
SELECT
    COUNT(*) AS counts
FROM
    seat;
```

This gives the total seat count.

For the sample:

| id  | student |
| --- | ------- |
| 1   | Abbot   |
| 2   | Doris   |
| 3   | Emerson |
| 4   | Green   |
| 5   | Jeames  |

the count is:

```text
5
```

That means seat `5` is the last seat.

---

## Step 2: Use `CASE` and `MOD()` to compute the swapped id

The logic is:

### If `id` is odd and it is not the last seat

Move it forward by 1:

```sql
id + 1
```

### If `id` is odd and it is the last seat

Keep it unchanged:

```sql
id
```

### Otherwise (`id` is even)

Move it backward by 1:

```sql
id - 1
```

That becomes:

```sql
CASE
    WHEN MOD(id, 2) != 0 AND counts != id THEN id + 1
    WHEN MOD(id, 2) != 0 AND counts = id THEN id
    ELSE id - 1
END
```

---

## Why `MOD(id, 2) != 0` means odd

The expression:

```sql
MOD(id, 2)
```

returns:

- `0` for even numbers
- `1` for odd numbers

So:

```sql
MOD(id, 2) != 0
```

detects odd ids.

---

## Final query for Approach I

```sql
SELECT
    (CASE
        WHEN MOD(id, 2) != 0 AND counts != id THEN id + 1
        WHEN MOD(id, 2) != 0 AND counts = id THEN id
        ELSE id - 1
    END) AS id,
    student
FROM
    seat,
    (SELECT
        COUNT(*) AS counts
    FROM
        seat) AS seat_counts
ORDER BY id ASC;
```

---

## How this query works

### `seat`

This is the original table.

### Subquery `seat_counts`

```sql
(SELECT COUNT(*) AS counts FROM seat) AS seat_counts
```

This produces the total number of seats.

### Cross join

Since the subquery returns one row, every seat row gets access to that total count.

### `CASE`

Computes the new seat id.

### `ORDER BY id ASC`

After assigning the swapped ids, sort by the new id so the final output is in correct seat order.

---

## Walkthrough on the sample

Original table:

| id  | student |
| --- | ------- |
| 1   | Abbot   |
| 2   | Doris   |
| 3   | Emerson |
| 4   | Green   |
| 5   | Jeames  |

Total count:

```text
5
```

Now compute new ids:

- `1` is odd and not last -> `2`
- `2` is even -> `1`
- `3` is odd and not last -> `4`
- `4` is even -> `3`
- `5` is odd and last -> `5`

Intermediate result:

| new id | student |
| ------ | ------- |
| 2      | Abbot   |
| 1      | Doris   |
| 4      | Emerson |
| 3      | Green   |
| 5      | Jeames  |

Sort by new id:

| id  | student |
| --- | ------- |
| 1   | Doris   |
| 2   | Abbot   |
| 3   | Green   |
| 4   | Emerson |
| 5   | Jeames  |

That matches the expected answer.

---

## Strengths of Approach I

- direct
- easy to understand
- closely matches the verbal logic of the problem

### Tradeoff

- explicitly needs the total seat count
- a bit more conditional branching

---

# Approach II: Using bit manipulation and `COALESCE()`

## Core idea

This approach relies on a compact mathematical trick to compute the swapped seat id:

```sql
((id + 1) ^ 1) - 1
```

This expression maps:

- `1 -> 2`
- `2 -> 1`
- `3 -> 4`
- `4 -> 3`
- `5 -> 6`
- ...

Then the query joins the table to itself using that mapping, and uses `COALESCE()` to handle the last odd seat if no matching swapped row exists.

This is more clever than the first approach, but less obvious at first glance.

---

## Step 1: Understand the bit-manipulation formula

The expression is:

```sql
(id + 1) ^ 1 - 1
```

More clearly parenthesized:

```sql
((id + 1) ^ 1) - 1
```

where `^` is bitwise XOR in MySQL.

Let us test it.

### For `id = 1`

```text
(1 + 1) ^ 1 - 1
= 2 ^ 1 - 1
= 3 - 1
= 2
```

### For `id = 2`

```text
(2 + 1) ^ 1 - 1
= 3 ^ 1 - 1
= 2 - 1
= 1
```

### For `id = 3`

```text
(3 + 1) ^ 1 - 1
= 4 ^ 1 - 1
= 5 - 1
= 4
```

### For `id = 4`

```text
(4 + 1) ^ 1 - 1
= 5 ^ 1 - 1
= 4 - 1
= 3
```

### For `id = 5`

```text
(5 + 1) ^ 1 - 1
= 6 ^ 1 - 1
= 7 - 1
= 6
```

So it correctly produces the paired swap id.

---

## Why the last odd seat is a problem here

For an odd total count, the last seat id has no partner.

For example, with seat `5`, the formula gives:

```text
5 -> 6
```

But seat `6` does not exist.

So if we join against seat `6`, we will get no match.

That is why this approach needs a `LEFT JOIN` plus `COALESCE()`.

---

## Step 2: Join the table to itself

The join is:

```sql
SELECT
    *
FROM
    seat s1
    LEFT JOIN seat s2
      ON ((s1.id + 1) ^ 1) - 1 = s2.id
ORDER BY s1.id;
```

This means:

- for each seat `s1`
- find the seat `s2` whose id is the swapped id of `s1`

For the sample:

| s1.id | s1.student | s2.id | s2.student |
| ----: | ---------- | ----: | ---------- |
|     1 | Abbot      |     2 | Doris      |
|     2 | Doris      |     1 | Abbot      |
|     3 | Emerson    |     4 | Green      |
|     4 | Green      |     3 | Emerson    |
|     5 | Jeames     |  NULL | NULL       |

Seat `5` has no partner, so `s2` is null.

---

## Step 3: Use `COALESCE()` for the last unmatched row

The output should use:

- `s2.student` when a partner exists
- otherwise `s1.student`

That is exactly what `COALESCE()` does.

```sql
COALESCE(s2.student, s1.student)
```

This returns:

- `s2.student` if it is not null
- otherwise `s1.student`

So for seat `5`, it correctly falls back to `Jeames`.

---

## Final query for Approach II

```sql
SELECT
    s1.id,
    COALESCE(s2.student, s1.student) AS student
FROM
    seat s1
    LEFT JOIN
    seat s2 ON ((s1.id + 1) ^ 1) - 1 = s2.id
ORDER BY s1.id;
```

---

## Why `s1.id` is returned directly

Unlike Approach I, this query does not rewrite the ids.

Instead, it keeps the original ids in sorted order and fetches the student who should now occupy that seat.

So:

- seat `1` returns student from seat `2`
- seat `2` returns student from seat `1`
- seat `3` returns student from seat `4`
- seat `4` returns student from seat `3`
- seat `5` returns itself if no partner exists

This produces exactly the same final arrangement.

---

## Walkthrough on the sample

Original:

| id  | student |
| --- | ------- |
| 1   | Abbot   |
| 2   | Doris   |
| 3   | Emerson |
| 4   | Green   |
| 5   | Jeames  |

Join mapping:

- `1 -> 2` -> Doris
- `2 -> 1` -> Abbot
- `3 -> 4` -> Green
- `4 -> 3` -> Emerson
- `5 -> 6` -> no match -> fallback to Jeames

Final output:

| id  | student |
| --- | ------- |
| 1   | Doris   |
| 2   | Abbot   |
| 3   | Green   |
| 4   | Emerson |
| 5   | Jeames  |

---

## Strengths of Approach II

- elegant
- avoids explicit total-seat counting
- compact once the bit trick is understood

### Tradeoff

- much less intuitive
- relies on MySQL bitwise behavior
- harder to derive from scratch in an interview setting

---

# Comparing the two approaches

## Approach I: `CASE`

### Best when

- you want readable and straightforward logic
- you prefer explicit handling of the odd-last-seat case

### Pros

- easy to understand
- clearly maps to the problem statement

### Cons

- requires total row count
- more branching logic

---

## Approach II: bit manipulation + `COALESCE()`

### Best when

- you want a compact trick-based solution
- you are comfortable with bitwise expressions

### Pros

- elegant
- avoids explicit last-seat counting in the transformation logic

### Cons

- less intuitive
- more difficult to explain quickly

---

# Important SQL concepts used here

## 1. `CASE`

Used in Approach I for conditional seat remapping.

## 2. `MOD()`

Used to test whether an id is odd or even.

## 3. Bitwise XOR `^`

Used in Approach II for the seat-pair mapping trick.

## 4. `LEFT JOIN`

Used in Approach II to preserve the last odd seat even if no partner exists.

## 5. `COALESCE()`

Used in Approach II to fall back to the original student when no swapped partner is found.

---

# Key takeaways

1. Seats are swapped pairwise:
   - odd ids usually move forward
   - even ids move backward
2. The only exception is the last odd seat when the total count is odd.
3. Approach I solves this explicitly with `CASE`.
4. Approach II uses a clever partner-id formula and a self join.
5. Both produce the same final output.

---

## Final accepted implementations

### Approach I: Using `CASE`

```sql
SELECT
    (CASE
        WHEN MOD(id, 2) != 0 AND counts != id THEN id + 1
        WHEN MOD(id, 2) != 0 AND counts = id THEN id
        ELSE id - 1
    END) AS id,
    student
FROM
    seat,
    (SELECT
        COUNT(*) AS counts
    FROM
        seat) AS seat_counts
ORDER BY id ASC;
```

### Approach II: Using bit manipulation and `COALESCE()`

```sql
SELECT
    s1.id, COALESCE(s2.student, s1.student) AS student
FROM
    seat s1
        LEFT JOIN
    seat s2 ON ((s1.id + 1) ^ 1) - 1 = s2.id
ORDER BY s1.id;
```
