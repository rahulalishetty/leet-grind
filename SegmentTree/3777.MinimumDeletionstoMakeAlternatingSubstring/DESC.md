# 3777. Minimum Deletions to Make Alternating Substring

## Problem Description

You are given a string `s` of length `n` consisting only of the characters:

```
'A' and 'B'
```

You are also given a list of queries. Each query is one of the following types:

### Query Type 1 — Flip Character

```
[1, j]
```

Flip the character at index `j`:

```
'A' -> 'B'
'B' -> 'A'
```

This **modifies the string** and affects subsequent queries.

---

### Query Type 2 — Alternating Substring Check

```
[2, l, r]
```

For substring:

```
s[l..r]
```

Compute the **minimum number of character deletions** required to make the substring **alternating**.

A string is **alternating** if:

```
No two adjacent characters are equal
```

Examples:

```
ABAB  -> alternating
BA    -> alternating
AAA   -> not alternating
ABB   -> not alternating
```

Note:

- This query **does not modify** the string.
- The length of `s` remains `n`.

---

# Output

Return an array `answer` containing results for **queries of type `[2, l, r]` only**.

```
answer[i] = minimum deletions needed to make s[l..r] alternating
```

---

# Example 1

## Input

```
s = "ABA"
queries = [[2,1,2],[1,1],[2,0,2]]
```

## Output

```
[0,2]
```

## Explanation

| i   | query   | string before | substring | result                  |
| --- | ------- | ------------- | --------- | ----------------------- |
| 0   | [2,1,2] | ABA           | BA        | already alternating → 0 |
| 1   | [1,1]   | ABA           | -         | flip B → A              |
| 2   | [2,0,2] | AAA           | AAA       | delete two A → 2        |

Answer:

```
[0,2]
```

---

# Example 2

## Input

```
s = "ABB"
queries = [[2,0,2],[1,2],[2,0,2]]
```

## Output

```
[1,0]
```

## Explanation

| i   | query   | string before | substring | result                  |
| --- | ------- | ------------- | --------- | ----------------------- |
| 0   | [2,0,2] | ABB           | ABB       | delete one B → AB       |
| 1   | [1,2]   | ABB           | -         | flip B → A              |
| 2   | [2,0,2] | ABA           | ABA       | already alternating → 0 |

Answer:

```
[1,0]
```

---

# Example 3

## Input

```
s = "BABA"
queries = [[2,0,3],[1,1],[2,1,3]]
```

## Output

```
[0,1]
```

## Explanation

| i   | query   | string before | substring | result                  |
| --- | ------- | ------------- | --------- | ----------------------- |
| 0   | [2,0,3] | BABA          | BABA      | already alternating → 0 |
| 1   | [1,1]   | BABA          | -         | flip A → B              |
| 2   | [2,1,3] | BBBA          | BBA       | delete one B → BA       |

Answer:

```
[0,1]
```

---

# Constraints

```
1 <= n == s.length <= 10^5
s[i] ∈ {'A','B'}

1 <= q == queries.length <= 10^5

queries[i] = [1, j] or [2, l, r]

0 <= j <= n-1
0 <= l <= r <= n-1
```

---

# Key Observations

An alternating string must follow one of two patterns:

```
ABABAB...
or
BABABA...
```

For any substring we only need to compare against these two patterns and count mismatches.

Minimum deletions correspond to removing characters that break the alternating property.

Efficient solutions typically use:

- Prefix sums
- Segment trees
- Binary indexed trees
- Dynamic updates after flips
