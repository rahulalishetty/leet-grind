# 1683. Invalid Tweets — Approach

## Approach: Filtering Rows

### Algorithm

To determine whether a tweet is invalid, we need to check the **number of characters in the tweet content**.

If the content contains **more than 15 characters**, the tweet is considered **invalid**.

---

## Counting Characters

SQL provides the function:

### `CHAR_LENGTH(str)`

This function returns the **number of characters** in a string.

Example:

```sql
CHAR_LENGTH(content)
```

This gives the number of characters in the tweet text.

---

## LENGTH vs CHAR_LENGTH

Another commonly used function is:

### `LENGTH(str)`

However, there is an important difference:

| Function    | Measures                 |
| ----------- | ------------------------ |
| CHAR_LENGTH | Number of **characters** |
| LENGTH      | Number of **bytes**      |

When strings contain **multi‑byte characters**, the results may differ.

Example using the character `¥`:

| Expression       | Result |
| ---------------- | ------ |
| LENGTH('¥')      | 2      |
| CHAR_LENGTH('¥') | 1      |

For this problem, both functions work because the `content` column contains **only English characters**.

---

## Implementation

```sql
SELECT
    tweet_id
FROM
    Tweets
WHERE
    CHAR_LENGTH(content) > 15;
```

---

## Explanation

The query:

1. Selects `tweet_id`
2. Calculates the **length of the tweet content**
3. Filters tweets where the **character count is greater than 15**

Those tweets are returned as **invalid tweets**.
