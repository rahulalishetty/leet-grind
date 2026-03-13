# 571. Find Median Given Frequency of Numbers

## Table: Numbers

| Column Name | Type |
| ----------- | ---- |
| num         | int  |
| frequency   | int  |

- `num` is the **primary key** (unique values).
- Each row represents a number and how many times it appears in the database.
- The `frequency` column indicates how many times the number occurs.

---

## Problem Description

The **median** is the value that separates the higher half of a data sample from the lower half.

You are given a compressed table `Numbers` where each row stores a number and its frequency.

Your task is to:

1. **Decompress the table** based on the `frequency` column.
2. Compute the **median** of all numbers.
3. **Round the median to one decimal place**.

---

## Example

### Input

**Numbers Table**

| num | frequency |
| --- | --------- |
| 0   | 7         |
| 1   | 1         |
| 2   | 3         |
| 3   | 1         |

---

### Decompressed Data

After expanding based on frequency:

```
[0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 3]
```

---

### Median Calculation

Total numbers = **12** (even)

Median = average of the **6th and 7th elements**:

```
(0 + 0) / 2 = 0
```

---

### Output

| median |
| ------ |
| 0.0    |
