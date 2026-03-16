# 418. Sentence Screen Fitting

## Problem

Given a screen with **rows x cols** dimensions and a **sentence** represented as a list of words, determine how many times the entire sentence can be fitted on the screen.

### Rules

- The order of words in the sentence **must remain unchanged**.
- **Words cannot be split** across lines.
- Exactly **one space must separate two consecutive words** in the same line.
- Remaining cells in a row may stay empty.

Return the **number of times the entire sentence can be placed on the screen**.

---

## Example 1

**Input**

```
sentence = ["hello", "world"]
rows = 2
cols = 8
```

**Output**

```
1
```

**Explanation**

```
hello---
world---
```

`-` represents empty spaces on the screen.

---

## Example 2

**Input**

```
sentence = ["a", "bcd", "e"]
rows = 3
cols = 6
```

**Output**

```
2
```

**Explanation**

```
a-bcd-
e-a---
bcd-e-
```

`-` represents empty spaces on the screen.

---

## Example 3

**Input**

```
sentence = ["i", "had", "apple", "pie"]
rows = 4
cols = 5
```

**Output**

```
1
```

**Explanation**

```
i-had
apple
pie-i
had--
```

`-` represents empty spaces on the screen.

---

## Constraints

- `1 <= sentence.length <= 100`
- `1 <= sentence[i].length <= 10`
- `sentence[i]` contains only lowercase English letters
- `1 <= rows, cols <= 2 * 10^4`
