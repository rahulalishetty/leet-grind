# Find Products That Are Low Fat and Recyclable — Approach

## Approach: Selecting Rows Based on Conditions

### Algorithm

The SQL query retrieves products that satisfy two conditions:

- The product is **low fat**
- The product is **recyclable**

This is achieved using the `WHERE` clause with the logical operator `AND`.

---

## Step 1: Select the Required Column

The `SELECT` keyword specifies the column to retrieve from the table.

In this case, we only need:

- `product_id`

```
SELECT product_id
```

---

## Step 2: Specify the Table

The `FROM` clause indicates the source table.

```
FROM Products
```

---

## Step 3: Apply Filtering Conditions

The `WHERE` clause filters rows based on specific conditions:

- `low_fats = 'Y'`
- `recyclable = 'Y'`

Both conditions must be true, so they are combined with the logical **AND** operator.

```
WHERE low_fats = 'Y' AND recyclable = 'Y'
```

---

# SQL Implementation

```sql
SELECT
    product_id
FROM
    Products
WHERE
    low_fats = 'Y' AND recyclable = 'Y';
```

---

# Key SQL Concepts Used

- **SELECT** → choose the columns to retrieve
- **FROM** → specify the table
- **WHERE** → filter rows
- **AND** → combine multiple conditions
