# Fix User Names Capitalization — Approach

## Approach 1: Separating the First Character from the Rest

### Algorithm

SQL provides several string manipulation functions that help transform text data.

The following functions are used in this solution:

- **SUBSTRING(column, start, length)**
  Extracts a substring from a string starting at a given position.

- **UPPER(expression)**
  Converts a string to uppercase.

- **LOWER(expression)**
  Converts a string to lowercase.

- **CONCAT(str1, str2, ...)**
  Concatenates multiple strings together.

---

## Key Idea

To fix the capitalization of names:

1. Extract the **first character** of the name.
2. Convert it to **uppercase**.
3. Extract the **remaining characters** of the name.
4. Convert them to **lowercase**.
5. **Concatenate** the two parts together.

---

## Implementation

```sql
SELECT
    user_id,
    CONCAT(
        UPPER(SUBSTRING(name, 1, 1)),
        LOWER(SUBSTRING(name, 2))
    ) AS name
FROM Users
ORDER BY user_id;
```

---

## Explanation

Example transformation:

| Original Name | Result |
| ------------- | ------ |
| aLice         | Alice  |
| bOB           | Bob    |

Steps applied:

1. `SUBSTRING(name,1,1)` → first character
2. `UPPER(...)` → capitalizes it
3. `SUBSTRING(name,2)` → remaining characters
4. `LOWER(...)` → converts them to lowercase
5. `CONCAT()` → merges them into the corrected name
