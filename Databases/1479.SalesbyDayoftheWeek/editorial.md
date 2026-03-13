# 1479. Sales by Day of the Week

## Approach: Reshaping Table Using `SUM(CASE WHEN ...)`

## Core idea

We need to produce a **pivot-style sales report** where:

- each row represents an **item category**
- each weekday becomes its own **column**
- each cell contains the **total quantity sold** for that category on that weekday

Since MySQL does not provide a simple built-in `PIVOT` operator in this context, a common way to build this kind of report is:

1. join the necessary tables so category and quantity are available together
2. detect the day of week for each order
3. use `SUM(CASE WHEN ...)` to pivot weekday rows into weekday columns
4. use `IFNULL()` so missing values become `0`
5. group by category

This is a classic SQL reshaping pattern.

---

## Why a join is needed

The information we need is split across two tables:

### `Items`

Contains:

- `item_id`
- `item_category`

### `Orders`

Contains:

- `item_id`
- `order_date`
- `quantity`

To report sales **by category and weekday**, we need both:

- the category from `Items`
- the quantity and order date from `Orders`

So we must join the tables.

---

## Why `LEFT JOIN` is used

The solution uses:

```sql
FROM Items i
LEFT JOIN Orders o
ON o.item_id = i.item_id
```

This is important because the result must include **all categories**, even categories with **no sales at all**.

For example, in the sample:

- `T-Shirt` has no orders
- but it still must appear in the final output with zeros for every day

If we used an inner join, categories with no matching orders would disappear.

So `LEFT JOIN` is the correct choice.

---

## Step 1: Identify the weekday of each order

The function used is:

```sql
DAYOFWEEK(order_date)
```

In MySQL, this returns:

- `1` = Sunday
- `2` = Monday
- `3` = Tuesday
- `4` = Wednesday
- `5` = Thursday
- `6` = Friday
- `7` = Saturday

That mapping is crucial, because we need to route each order's quantity into the correct weekday column.

---

## Step 2: Pivot weekdays into columns using `SUM(CASE WHEN ...)`

Since there is no direct pivot operator used here, we simulate a pivot manually.

For example, Monday is computed as:

```sql
SUM(CASE WHEN DAYOFWEEK(order_date) = 2 THEN quantity END)
```

### Why this works

For each row in the joined table:

- if the order happened on Monday, return its `quantity`
- otherwise return `NULL`

Then `SUM(...)` adds up only the Monday quantities for that category.

The same pattern is repeated for every weekday.

---

## Step 3: Use `IFNULL()` to convert missing sums to zero

If a category has no orders on a given weekday, then:

```sql
SUM(CASE WHEN ... THEN quantity END)
```

returns `NULL`.

But the output should display `0`, not `NULL`.

So we wrap each expression with:

```sql
IFNULL(..., 0)
```

Example:

```sql
IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 2 THEN quantity END), 0)
```

This ensures empty weekday totals show up as zero.

---

## Final accepted query

```sql
SELECT item_category AS CATEGORY,
       IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 2 THEN quantity END), 0) AS 'MONDAY',
       IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 3 THEN quantity END), 0) AS 'TUESDAY',
       IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 4 THEN quantity END), 0) AS 'WEDNESDAY',
       IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 5 THEN quantity END), 0) AS 'THURSDAY',
       IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 6 THEN quantity END), 0) AS 'FRIDAY',
       IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 7 THEN quantity END), 0) AS 'SATURDAY',
       IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 1 THEN quantity END), 0) AS 'SUNDAY'
FROM Items i
LEFT JOIN Orders o
ON o.item_id = i.item_id
GROUP BY item_category
ORDER BY item_category;
```

---

# Step-by-step explanation of the query

## `SELECT item_category AS CATEGORY`

We want one row per category, and the required output column name is `Category` / `CATEGORY`.

So we select:

```sql
item_category AS CATEGORY
```

---

## Monday column

```sql
IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 2 THEN quantity END), 0) AS 'MONDAY'
```

This sums quantities only for orders placed on Monday.

---

## Tuesday column

```sql
IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 3 THEN quantity END), 0) AS 'TUESDAY'
```

This sums quantities only for Tuesday.

---

## Wednesday column

```sql
IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 4 THEN quantity END), 0) AS 'WEDNESDAY'
```

---

## Thursday column

```sql
IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 5 THEN quantity END), 0) AS 'THURSDAY'
```

---

## Friday column

```sql
IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 6 THEN quantity END), 0) AS 'FRIDAY'
```

---

## Saturday column

```sql
IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 7 THEN quantity END), 0) AS 'SATURDAY'
```

---

## Sunday column

```sql
IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 1 THEN quantity END), 0) AS 'SUNDAY'
```

---

## `FROM Items i LEFT JOIN Orders o`

This makes sure all categories are included, even if no orders exist for them.

---

## `GROUP BY item_category`

After the join, there may be many order rows for the same category.

We group by category so that each result row corresponds to one category.

---

## `ORDER BY item_category`

The problem asks for the result ordered by category.

So we sort alphabetically by `item_category`.

---

# Walkthrough on the sample

## Sample Orders

| order_id | order_date | item_id | quantity |
| -------- | ---------- | ------- | -------- |
| 1        | 2020-06-01 | 1       | 10       |
| 2        | 2020-06-08 | 2       | 10       |
| 3        | 2020-06-02 | 1       | 5        |
| 4        | 2020-06-03 | 3       | 5        |
| 5        | 2020-06-04 | 4       | 1        |
| 6        | 2020-06-05 | 5       | 5        |
| 7        | 2020-06-05 | 1       | 10       |
| 8        | 2020-06-14 | 4       | 5        |
| 9        | 2020-06-21 | 3       | 5        |

## Sample Items

| item_id | item_category |
| ------- | ------------- |
| 1       | Book          |
| 2       | Book          |
| 3       | Phone         |
| 4       | Phone         |
| 5       | Glasses       |
| 6       | T-Shirt       |

---

## Category: Book

Book items are:

- item `1`
- item `2`

Relevant orders:

- `2020-06-01` (Monday) -> item 1 -> quantity 10
- `2020-06-08` (Monday) -> item 2 -> quantity 10
- `2020-06-02` (Tuesday) -> item 1 -> quantity 5
- `2020-06-05` (Friday) -> item 1 -> quantity 10

Totals:

- Monday = `10 + 10 = 20`
- Tuesday = `5`
- Friday = `10`
- all others = `0`

---

## Category: Glasses

Glasses item:

- item `5`

Relevant order:

- `2020-06-05` (Friday) -> quantity 5

Totals:

- Friday = `5`
- all others = `0`

---

## Category: Phone

Phone items are:

- item `3`
- item `4`

Relevant orders:

- `2020-06-03` (Wednesday) -> item 3 -> quantity 5
- `2020-06-04` (Thursday) -> item 4 -> quantity 1
- `2020-06-14` (Sunday) -> item 4 -> quantity 5
- `2020-06-21` (Sunday) -> item 3 -> quantity 5

Totals:

- Wednesday = `5`
- Thursday = `1`
- Sunday = `5 + 5 = 10`
- all others = `0`

---

## Category: T-Shirt

T-Shirt item:

- item `6`

No matching orders.

Because of the `LEFT JOIN`, the category still appears.

All weekday totals become `0` after `IFNULL()`.

---

## Final output

| Category | Monday | Tuesday | Wednesday | Thursday | Friday | Saturday | Sunday |
| -------- | ------ | ------- | --------- | -------- | ------ | -------- | ------ |
| Book     | 20     | 5       | 0         | 0        | 10     | 0        | 0      |
| Glasses  | 0      | 0       | 0         | 0        | 5      | 0        | 0      |
| Phone    | 0      | 0       | 5         | 1        | 0      | 0        | 10     |
| T-Shirt  | 0      | 0       | 0         | 0        | 0      | 0        | 0      |

---

# Why `SUM(CASE WHEN ...)` is the standard pivot pattern

A useful skeptical question is: how do we “move” row values into columns in SQL when there is no pivot operator?

The answer is usually:

```sql
SUM(CASE WHEN condition_for_column THEN value END)
```

This pattern is extremely common for pivot-style reports.

It works because:

- `CASE WHEN` selects the values for one target column
- `SUM` aggregates them within each group
- repeating the pattern creates one column per category/day/status/etc.

This is one of the most important SQL reshaping techniques to know.

---

# Why `IFNULL()` matters here

Without `IFNULL()`, categories with no sales on a given day would show:

```text
NULL
```

But business reports usually want:

```text
0
```

That makes the output more readable and matches the problem requirement.

---

# Important mapping of `DAYOFWEEK()`

MySQL's weekday numbering is not Monday-first.

It is:

- `1` -> Sunday
- `2` -> Monday
- `3` -> Tuesday
- `4` -> Wednesday
- `5` -> Thursday
- `6` -> Friday
- `7` -> Saturday

So the query must map weekday numbers carefully.

That is why Monday uses `= 2`, not `= 1`.

---

# Complexity

Let:

- `I` = number of rows in `Items`
- `O` = number of rows in `Orders`

## Time Complexity

The query joins orders to items and aggregates by category.

A practical summary is:

```text
O(I + O)
```

plus grouping overhead depending on the SQL engine.

## Space Complexity

Extra space is mainly for the grouped output and aggregation state, proportional to the number of categories.

---

# Key takeaways

1. Join `Items` and `Orders` because category and quantity are stored in different tables.
2. Use `LEFT JOIN` so categories with no sales still appear.
3. Use `DAYOFWEEK()` to identify the weekday for each order.
4. Use `SUM(CASE WHEN ...)` to pivot weekdays into columns.
5. Use `IFNULL()` so missing totals become `0`.
6. Group by `item_category` and order by category.

---

## Final accepted implementation

```sql
SELECT item_category AS CATEGORY,
       IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 2 THEN quantity END), 0) AS 'MONDAY',
       IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 3 THEN quantity END), 0) AS 'TUESDAY',
       IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 4 THEN quantity END), 0) AS 'WEDNESDAY',
       IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 5 THEN quantity END), 0) AS 'THURSDAY',
       IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 6 THEN quantity END), 0) AS 'FRIDAY',
       IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 7 THEN quantity END), 0) AS 'SATURDAY',
       IFNULL(SUM(CASE WHEN DAYOFWEEK(order_date) = 1 THEN quantity END), 0) AS 'SUNDAY'
FROM Items i
LEFT JOIN Orders o
ON o.item_id = i.item_id
GROUP BY item_category
ORDER BY item_category;
```
