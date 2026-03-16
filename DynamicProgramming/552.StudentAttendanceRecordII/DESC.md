# 552. Student Attendance Record II

## Problem Description

An attendance record for a student is represented as a string where each character describes the student's status on a given day.

The record can contain the following characters:

- **'A'** — Absent
- **'L'** — Late
- **'P'** — Present

---

## Award Eligibility Rules

A student is eligible for an attendance award if **both conditions** are satisfied:

1. The student was **absent ('A') strictly fewer than 2 days** in total.
2. The student was **never late ('L') for 3 or more consecutive days**.

---

## Task

Given an integer `n`, return the **number of possible attendance records of length `n`** that satisfy the eligibility conditions.

Because the result may be very large, return the answer **modulo 10⁹ + 7**.

---

## Example 1

### Input

```
n = 2
```

### Output

```
8
```

### Explanation

All possible valid records of length 2:

```
PP
AP
PA
LP
PL
AL
LA
LL
```

The only invalid record is:

```
AA
```

because it contains **2 absences**, while the rule allows **fewer than 2**.

---

## Example 2

### Input

```
n = 1
```

### Output

```
3
```

Possible records:

```
P
A
L
```

All are valid.

---

## Example 3

### Input

```
n = 10101
```

### Output

```
183236316
```

---

## Constraints

```
1 <= n <= 100000
```

---
