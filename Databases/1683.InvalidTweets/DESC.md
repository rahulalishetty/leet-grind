# 1683. Invalid Tweets

## Table: Tweets

| Column Name | Type    |
| ----------- | ------- |
| tweet_id    | int     |
| content     | varchar |

### Notes

- `tweet_id` is the **primary key** (unique value).
- `content` contains **alphanumeric characters**, `'!'`, or spaces `' '`.
- No other special characters are present.
- Each row represents a tweet in a social media application.

---

# Problem

Write a SQL query to find the **IDs of invalid tweets**.

A tweet is considered **invalid** if the **number of characters in the content is strictly greater than 15**.

Return the result table **in any order**.

---

# Example

## Input

### Tweets

| tweet_id | content                           |
| -------- | --------------------------------- |
| 1        | Let us Code                       |
| 2        | More than fifteen chars are here! |

---

## Output

| tweet_id |
| -------- |
| 2        |

---

# Explanation

- **Tweet 1**
  - Content: `"Let us Code"`
  - Length = **11**
  - Valid tweet

- **Tweet 2**
  - Content: `"More than fifteen chars are here!"`
  - Length = **33**
  - Invalid tweet

Therefore, tweet **2** is returned in the result.
