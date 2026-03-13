# 618. Students Report By Geography

## Approach: Using Session Variables and Join

## Core idea

The goal is to pivot student names into three columns:

- `America`
- `Asia`
- `Europe`

with names in each column sorted alphabetically.

The key difficulty is that SQL tables are row-oriented, but the output needs to be column-oriented.

A practical way to solve this in MySQL is:

1. create a separate ordered list for each continent
2. assign a row number to each name inside that continent
3. join the three lists together using that row number

That way:

- the 1st America student aligns with the 1st Asia student and the 1st Europe student
- the 2nd America student aligns with the 2nd Asia student and the 2nd Europe student
- and so on

This effectively simulates a pivot.

---

## Why row numbers are needed

Suppose the input is:

| name   | continent |
| ------ | --------- |
| Jane   | America   |
| Pascal | Europe    |
| Xi     | Asia      |
| Jack   | America   |

If we sort each continent alphabetically, we get:

### America

```text
Jack
Jane
```

### Asia

```text
Xi
```

### Europe

```text
Pascal
```

Now the desired output is:

| America | Asia | Europe |
| ------- | ---- | ------ |
| Jack    | Xi   | Pascal |
| Jane    | NULL | NULL   |

To make this happen, we need a positional index:

- row 1 -> first student in each continent
- row 2 -> second student in each continent

That is why we assign row ids.

---

## Step 1: Assign row ids using session variables

In MySQL, session variables can be used to simulate row numbering.

For example, for America:

```sql
SELECT
    row_id,
    America
FROM
    (SELECT @am := 0) t,
    (
        SELECT
            @am := @am + 1 AS row_id,
            name AS America
        FROM
            student
        WHERE
            continent = 'America'
        ORDER BY America
    ) AS t2;
```

---

## How this works

### `(SELECT @am := 0) t`

This initializes the session variable `@am` to 0.

### `@am := @am + 1 AS row_id`

Every time a row is processed, the variable increases by 1.

So the rows become numbered:

- first row -> 1
- second row -> 2
- third row -> 3

### `ORDER BY America`

This ensures the students are sorted alphabetically before being assigned row ids.

That is essential, because the problem requires names in each continent column to appear in alphabetical order.

---

## Example result for America

Using the sample data, the America list becomes:

| row_id | America |
| ------ | ------- |
| 1      | Jack    |
| 2      | Jane    |

Similarly, the other continents become:

### Asia

| row_id | Asia |
| ------ | ---- |
| 1      | Xi   |

### Europe

| row_id | Europe |
| ------ | ------ |
| 1      | Pascal |

Now each continent has its own ordered numbered list.

---

## Step 2: Join the three continent lists by row id

Once each continent has its own row number, we can align the rows by joining on that number.

Conceptually:

- America row 1 joins with Asia row 1 and Europe row 1
- America row 2 joins with Asia row 2 and Europe row 2

That produces the pivoted table.

---

## Why join direction matters

A subtle issue arises because the number of students is not the same for each continent.

In this problem, the test cases guarantee:

> the number of students from America is not less than either Asia or Europe

So America is the safest table to use as the “base” table in the join.

If we used a normal inner join, rows would be lost when one continent has fewer students.

For example:

- America has 2 students
- Asia has 1 student

An inner join would keep only the matched first row and lose the second America student.

So outer joins are needed.

---

## Why America is placed in the middle

The provided solution uses:

- `RIGHT JOIN` from Asia to America
- `LEFT JOIN` from America to Europe

The trick is to place the America list in the middle so that it can preserve all America rows while still attaching matching Asia and Europe rows if they exist.

This works because America is guaranteed to have at least as many rows as the other two continents.

---

## Final accepted query

```sql
SELECT
    America,
    Asia,
    Europe
FROM
    (SELECT @as := 0, @am := 0, @eu := 0) t,
    (
        SELECT
            @as := @as + 1 AS asid,
            name AS Asia
        FROM
            student
        WHERE
            continent = 'Asia'
        ORDER BY Asia
    ) AS t1
    RIGHT JOIN
    (
        SELECT
            @am := @am + 1 AS amid,
            name AS America
        FROM
            student
        WHERE
            continent = 'America'
        ORDER BY America
    ) AS t2
        ON asid = amid
    LEFT JOIN
    (
        SELECT
            @eu := @eu + 1 AS euid,
            name AS Europe
        FROM
            student
        WHERE
            continent = 'Europe'
        ORDER BY Europe
    ) AS t3
        ON amid = euid;
```

---

## Step-by-step explanation of the final query

### Variable initialization

```sql
(SELECT @as := 0, @am := 0, @eu := 0) t
```

This initializes three separate counters:

- `@as` for Asia
- `@am` for America
- `@eu` for Europe

Each continent needs its own independent row numbering.

---

### Asia subquery

```sql
(
    SELECT
        @as := @as + 1 AS asid,
        name AS Asia
    FROM
        student
    WHERE
        continent = 'Asia'
    ORDER BY Asia
) AS t1
```

This produces a table like:

| asid | Asia |
| ---- | ---- |
| 1    | Xi   |

---

### America subquery

```sql
(
    SELECT
        @am := @am + 1 AS amid,
        name AS America
    FROM
        student
    WHERE
        continent = 'America'
    ORDER BY America
) AS t2
```

This produces:

| amid | America |
| ---- | ------- |
| 1    | Jack    |
| 2    | Jane    |

---

### Europe subquery

```sql
(
    SELECT
        @eu := @eu + 1 AS euid,
        name AS Europe
    FROM
        student
    WHERE
        continent = 'Europe'
    ORDER BY Europe
) AS t3
```

This produces:

| euid | Europe |
| ---- | ------ |
| 1    | Pascal |

---

### First join: Asia to America

```sql
t1 RIGHT JOIN t2 ON asid = amid
```

This aligns Asia rows with America rows by row number.

Because this is a `RIGHT JOIN`, all America rows are preserved.

Result conceptually:

| amid | America | Asia |
| ---- | ------- | ---- |
| 1    | Jack    | Xi   |
| 2    | Jane    | NULL |

---

### Second join: attach Europe

```sql
LEFT JOIN t3 ON amid = euid
```

This adds the Europe names to the existing America-based rows.

Final result:

| America | Asia | Europe |
| ------- | ---- | ------ |
| Jack    | Xi   | Pascal |
| Jane    | NULL | NULL   |

That matches the required output.

---

## Walkthrough on the sample

### Input

| name   | continent |
| ------ | --------- |
| Jane   | America   |
| Pascal | Europe    |
| Xi     | Asia      |
| Jack   | America   |

### Sorted continent lists

#### America

```text
Jack
Jane
```

#### Asia

```text
Xi
```

#### Europe

```text
Pascal
```

### Add row ids

#### America

| amid | America |
| ---- | ------- |
| 1    | Jack    |
| 2    | Jane    |

#### Asia

| asid | Asia |
| ---- | ---- |
| 1    | Xi   |

#### Europe

| euid | Europe |
| ---- | ------ |
| 1    | Pascal |

### Join by row id

Row 1:

```text
Jack | Xi   | Pascal
```

Row 2:

```text
Jane | NULL | NULL
```

Final output:

| America | Asia | Europe |
| ------- | ---- | ------ |
| Jack    | Xi   | Pascal |
| Jane    | NULL | NULL   |

---

## Why this solution is specific to the problem constraint

This solution relies on the guarantee that:

```text
America count >= Asia count
America count >= Europe count
```

That is why America can safely act as the central preserved list.

If that guarantee were removed, this exact join arrangement would no longer be generally safe.

That is exactly why the follow-up asks about the case where the largest continent is unknown.

In a more general setting, you would need a different approach, often involving row numbers plus a more flexible join pattern or dynamic SQL.

---

## Important SQL idea: pseudo-row-numbering with session variables

Before modern window functions became common in MySQL, session variables were often used to simulate row numbers.

Pattern:

```sql
@var := @var + 1
```

This works, but it is more procedural and less robust than standard window functions like `ROW_NUMBER()`.

Still, for this problem and MySQL-style accepted solutions, it is a valid technique.

---

## Strengths of this approach

- clever and practical
- simulates pivot behavior without built-in pivot support
- works within MySQL using session variables
- aligns names positionally after sorting

### Tradeoffs

- depends on MySQL-specific session variable behavior
- relies on the America-count constraint
- more fragile and less portable than window-function-based approaches in modern SQL

---

## Complexity

Let:

- `A` = number of America students
- `S` = number of Asia students
- `E` = number of Europe students

### Time Complexity

The query sorts each continent list separately and then joins them by row id.

Conceptually, this is driven mainly by:

- sorting within each continent
- joining the three numbered lists

### Space Complexity

Additional intermediate space is needed for the three derived continent tables.

For practical SQL interview discussion, the key point is that this is efficient enough and much better than trying to build the pivot manually row by row.

---

## Key takeaways

1. The output is a pivot, so we first turn each continent into its own ordered list.
2. Session variables are used to assign row numbers inside each continent.
3. The row numbers act as alignment keys across continents.
4. The lists are then joined by those row numbers.
5. America is used as the central table because the problem guarantees it has at least as many students as Asia and Europe.
6. This is a MySQL-specific practical solution using session variables and outer joins.

---

## Final accepted implementation

```sql
SELECT
    America, Asia, Europe
FROM
    (SELECT @as:=0, @am:=0, @eu:=0) t,
    (SELECT
        @as:=@as + 1 AS asid, name AS Asia
    FROM
        student
    WHERE
        continent = 'Asia'
    ORDER BY Asia) AS t1
        RIGHT JOIN
    (SELECT
        @am:=@am + 1 AS amid, name AS America
    FROM
        student
    WHERE
        continent = 'America'
    ORDER BY America) AS t2 ON asid = amid
        LEFT JOIN
    (SELECT
        @eu:=@eu + 1 AS euid, name AS Europe
    FROM
        student
    WHERE
        continent = 'Europe'
    ORDER BY Europe) AS t3 ON amid = euid;
```
