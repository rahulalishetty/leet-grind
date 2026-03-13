# Counting Articles Containing the Standalone Words "bull" and "bear" — Detailed Summary

## Problem Pattern

This pattern is about counting how many rows in a text table contain a target word **at least once** as a **standalone word**.

The explanation uses a table like:

```sql
Files
```

with a text column such as:

```sql
content
```

The goal is to return two rows:

- one for `bull`
- one for `bear`

and count how many articles contain each word.

A key detail is that we want the word as a **standalone word**, not just as a substring inside another word.

For example:

- `"bull"` should match `" bull "`
- but should not incorrectly match something like `"bullish"` if the intention is strict standalone-word matching

---

# Approach 1: Distinct File Name Count Using `REGEXP`

## Main Idea

This approach uses the `REGEXP` operator to search for a word pattern inside the `content` text.

For each target word:

1. create a constant output label such as `'bull'`
2. evaluate each row with a `CASE` expression
3. return `1` if the content matches the pattern
4. return `0` otherwise
5. sum those values to get the total number of matching rows

Then combine the counts for `bull` and `bear` using `UNION`.

---

## Query for the Word `"bull"`

```sql
SELECT
  'bull' AS word,
  SUM(
    CASE WHEN content REGEXP '( bull )' THEN 1 ELSE 0 END
  ) AS count
FROM Files;
```

---

## Step-by-Step Breakdown

### `'bull' AS word`

This creates a constant column called `word`.

Every row returned by this subquery will show:

```text
bull
```

That makes the result self-describing.

---

### `CASE WHEN content REGEXP '( bull )' THEN 1 ELSE 0 END`

This checks each row of the `Files` table.

If the `content` matches the regular-expression pattern for the standalone word `"bull"`, the row contributes:

```text
1
```

Otherwise it contributes:

```text
0
```

So each article becomes a yes/no indicator for the presence of the word.

---

### `SUM(...)`

The `SUM` function adds all those `1`s and `0`s together.

That gives the number of rows whose `content` contains the target word at least once.

So this is a conditional count implemented through summation.

---

### `FROM Files`

This tells SQL to apply the check to every row in the `Files` table.

---

## Query for the Word `"bear"`

The same logic is repeated for `"bear"`:

```sql
SELECT
  'bear' AS word,
  SUM(
    CASE WHEN content REGEXP '( bear )' THEN 1 ELSE 0 END
  ) AS count
FROM Files;
```

---

## Combine the Two Results

```sql
SELECT
  'bull' AS word,
  SUM(
    CASE WHEN content REGEXP '( bull )' THEN 1 ELSE 0 END
  ) AS count
FROM Files

UNION

SELECT
  'bear' AS word,
  SUM(
    CASE WHEN content REGEXP '( bear )' THEN 1 ELSE 0 END
  ) AS count
FROM Files;
```

---

## Why `UNION` Works Here

Each query returns the same two-column shape:

- `word`
- `count`

So the results can be stacked vertically into one result set.

The final output will look like:

| word | count |
| ---- | ----: |
| bull |   ... |
| bear |   ... |

Because the labels `bull` and `bear` are different, `UNION ALL` would also work here.

---

## Why `REGEXP` Is Useful

`REGEXP` is more expressive than simple text matching.

It allows pattern-based matching, which is important when the requirement is about a **standalone word**.

For text search tasks, regular expressions are usually more precise than plain `LIKE`, especially when punctuation, boundaries, or complex patterns matter.

---

## Important Caveat About the Pattern `'( bull )'`

The example pattern:

```sql
'( bull )'
```

is only a rough standalone-word approximation.

It assumes the word is surrounded by spaces.

That may fail in cases like:

- `"bull,"`
- `"bull."`
- `"(bull)"`
- `"Bull"` with different case
- word at the start or end of content

So while the idea is correct, the exact regex may be too simplistic depending on the SQL engine and input format.

A more robust regex often uses word boundaries if the SQL dialect supports them.

Still, the explanation's main teaching point is the conditional-count pattern.

---

# Approach 2: Distinct File Name Count Using `LIKE`

## Main Idea

This approach uses the `LIKE` operator instead of `REGEXP`.

The logic is similar:

1. create one query for `"bull"`
2. create one query for `"bear"`
3. check whether the content matches a space-delimited pattern
4. sum the boolean results
5. combine both outputs with `UNION ALL`

This is simpler than regex-based matching, but also less precise.

---

## Query for `"bull"`

```sql
SELECT
  'bull' AS word,
  SUM(content LIKE '% bull %') AS count
FROM Files;
```

---

## Step-by-Step Breakdown

### `'bull' AS word`

Again, this creates a constant label for the result row.

---

### `content LIKE '% bull %'`

This checks whether the string contains the pattern:

```text
space + bull + space
```

The `%` wildcards allow any characters before and after that pattern.

So it tries to identify `"bull"` as a standalone word surrounded by spaces.

If the SQL dialect treats boolean expressions as `1` and `0`, then:

- matching rows contribute `1`
- non-matching rows contribute `0`

---

### `SUM(content LIKE '% bull %')`

This adds those boolean results across all rows.

That gives the total number of rows containing the pattern.

---

## Query for `"bear"`

```sql
SELECT
  'bear' AS word,
  SUM(content LIKE '% bear %') AS count
FROM Files;
```

---

## Combine the Results

```sql
SELECT
  'bull' AS word,
  SUM(content LIKE '% bull %') AS count
FROM Files

UNION ALL

SELECT
  'bear' AS word,
  SUM(content LIKE '% bear %') AS count
FROM Files;
```

---

## Why `UNION ALL` Is Used Here

The output rows are guaranteed to be different because:

- one has `word = 'bull'`
- the other has `word = 'bear'`

So there is no need to remove duplicates.

That makes `UNION ALL` slightly more efficient than `UNION`.

---

# Comparing the Two Approaches

## Approach 1: `REGEXP`

### Strengths

- more flexible
- better suited for real standalone-word matching
- more adaptable to punctuation and richer text rules

### Weaknesses

- regex syntax is more complex
- behavior varies across SQL engines
- the exact sample regex shown is still somewhat simplistic

---

## Approach 2: `LIKE`

### Strengths

- easy to read
- easy to write
- good for simple approximate matching

### Weaknesses

- less precise
- only matches words surrounded by spaces
- can miss matches near punctuation or string boundaries

---

# Why `SUM(...)` Is Used Instead of `COUNT(...)`

This is an important SQL pattern.

Suppose the condition returns boolean-like values:

- true → `1`
- false → `0`

Then:

```sql
SUM(condition)
```

counts how many rows satisfy the condition.

That works because only matching rows contribute `1`.

Using `COUNT(condition)` would not behave the same way in many SQL engines, because `COUNT` counts non-null values, not true values.

So `SUM(...)` is the correct aggregation when the condition is converted to `1` or `0`.

---

# Example Mental Model

Suppose the `Files` table contains:

| file_name | content                     |
| --------- | --------------------------- |
| f1        | "the bull market is rising" |
| f2        | "bear trends continue"      |
| f3        | "the bull and bear debate"  |
| f4        | "nothing relevant here"     |

For `"bull"`:

- f1 → yes
- f2 → no
- f3 → yes
- f4 → no

Count = `2`

For `"bear"`:

- f1 → no
- f2 → yes
- f3 → yes
- f4 → no

Count = `2`

So the output would be:

| word | count |
| ---- | ----: |
| bull |     2 |
| bear |     2 |

---

# Recommended Practical Version

If the intention is strictly to count rows containing the words `"bull"` and `"bear"` and the SQL engine supports regex well, the regex approach is generally better.

A readable version following the explanation is:

```sql
SELECT
  'bull' AS word,
  SUM(CASE WHEN content REGEXP '( bull )' THEN 1 ELSE 0 END) AS count
FROM Files

UNION ALL

SELECT
  'bear' AS word,
  SUM(CASE WHEN content REGEXP '( bear )' THEN 1 ELSE 0 END) AS count
FROM Files;
```

This keeps the original logic while using `UNION ALL`, which is usually more appropriate here.

---

# Cleaner Boolean-Sum Version

If the SQL engine supports boolean-to-integer conversion, this shorter style may also work:

```sql
SELECT
  'bull' AS word,
  SUM(content REGEXP '( bull )') AS count
FROM Files

UNION ALL

SELECT
  'bear' AS word,
  SUM(content REGEXP '( bear )') AS count
FROM Files;
```

This is concise, but the explicit `CASE WHEN` version is easier to understand and more portable.

---

# Complexity Discussion

Let `n` be the number of rows in `Files`, and let `L` be the average content length.

Each query scans the full `Files` table and performs a text match per row.

Since there are two words:

- one pass for `"bull"`
- one pass for `"bear"`

the work is roughly proportional to two full scans of the text column.

That is usually acceptable for a small fixed number of search terms.

---

# Final Code Examples

## Approach 1 — `REGEXP`

```sql
SELECT
  'bull' AS word,
  SUM(
    CASE WHEN content REGEXP '( bull )' THEN 1 ELSE 0 END
  ) AS count
FROM Files

UNION ALL

SELECT
  'bear' AS word,
  SUM(
    CASE WHEN content REGEXP '( bear )' THEN 1 ELSE 0 END
  ) AS count
FROM Files;
```

---

## Approach 2 — `LIKE`

```sql
SELECT
  'bull' AS word,
  SUM(content LIKE '% bull %') AS count
FROM Files

UNION ALL

SELECT
  'bear' AS word,
  SUM(content LIKE '% bear %') AS count
FROM Files;
```

---

# Key Takeaways

- Build one result row per target word
- Use conditional aggregation to count how many articles contain that word
- `REGEXP` is generally more expressive than `LIKE`
- `LIKE '% word %'` is simpler but less precise
- `UNION ALL` is enough because the result labels are distinct

---
