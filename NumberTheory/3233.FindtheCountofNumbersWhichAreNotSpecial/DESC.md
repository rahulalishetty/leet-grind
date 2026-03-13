# 3233. Find the Count of Numbers Which Are Not Special

You are given two positive integers **l** and **r**.

For any number **x**, all positive divisors of **x** except **x** itself are called the **proper divisors** of **x**.

A number is called **special** if it has **exactly 2 proper divisors**.

---

## Examples

### Example 1

**Input**

```
l = 5, r = 7
```

**Output**

```
3
```

**Explanation**

There are **no special numbers** in the range:

```
[5, 7]
```

So all numbers are **not special**, giving a count of:

```
3
```

---

### Example 2

**Input**

```
l = 4, r = 16
```

**Output**

```
11
```

**Explanation**

The special numbers in the range:

```
[4, 16]
```

are:

```
4, 9
```

So the remaining numbers are **not special**.

---

## Constraints

```
1 <= l <= r <= 10^9
```

---
