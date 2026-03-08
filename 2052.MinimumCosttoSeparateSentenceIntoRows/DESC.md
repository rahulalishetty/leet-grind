# 2052. Minimum Cost to Separate Sentence Into Rows

## Problem Description

You are given a string **sentence** containing words separated by spaces, and an integer **k**. Your task is to separate the sentence into rows such that the number of characters in each row is **at most k**.

### Rules

- The sentence **does not begin or end with a space**.
- Words are separated by **exactly one space**.
- You may insert **line breaks only between words**.
- A **word cannot be split** across rows.
- Each word must be used **exactly once**.
- The **word order cannot be changed**.
- Adjacent words in a row must be separated by **a single space**.
- Rows **cannot start or end with spaces**.

### Cost Definition

If a row has length **n**, its cost is:

```
(k - n)^2
```

However, the **last row does not contribute to the total cost**.

Return the **minimum possible total cost** of separating the sentence into rows.

---

# Examples

## Example 1

**Input**

```
sentence = "i love leetcode"
k = 12
```

**Output**

```
36
```

**Explanation**

Possible splits:

1.

```
"i"
"love"
"leetcode"
```

Cost:

```
(12 - 1)^2 + (12 - 4)^2 = 121 + 64 = 185
```

2.

```
"i love"
"leetcode"
```

Cost:

```
(12 - 6)^2 = 36
```

3.

```
"i"
"love leetcode"
```

Not allowed because:

```
length("love leetcode") = 13 > 12
```

Minimum cost is:

```
36
```

---

## Example 2

**Input**

```
sentence = "apples and bananas taste great"
k = 7
```

**Output**

```
21
```

**Explanation**

Split:

```
"apples"
"and"
"bananas"
"taste"
"great"
```

Costs:

```
(7 - 6)^2 + (7 - 3)^2 + (7 - 7)^2 + (7 - 5)^2
= 1 + 16 + 0 + 4
= 21
```

---

## Example 3

**Input**

```
sentence = "a"
k = 5
```

**Output**

```
0
```

**Explanation**

There is only one row, and the **last row cost is not counted**, so the total cost is **0**.

---

# Constraints

```
1 <= sentence.length <= 5000
1 <= k <= 5000
```

Additional guarantees:

- Each word length is **at most k**
- `sentence` contains only **lowercase English letters and spaces**
- The sentence **does not start or end with a space**
- Words are separated by **a single space**
