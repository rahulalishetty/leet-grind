import pypandoc, textwrap, pathlib

md = """

# 964. Least Operators to Express Number

## Problem Description

You are given a **positive integer `x`**.

You must write an expression using the number `x` repeatedly in the following form:

```
x op1 x op2 x op3 x ...
```

where each operator `op` can be one of:

```
+  -  *  /
```

### Example Expression

For example, if:

```
x = 3
```

A valid expression could be:

```
3 * 3 / 3 + 3 - 3
```

This evaluates to:

```
3
```

---

## Expression Rules

When forming the expression, the following constraints apply:

### 1. Division Produces Rational Numbers

```
/
```

returns rational numbers (not just integers).

Example:

```
3 / 3 = 1
```

---

### 2. No Parentheses Allowed

Expressions must follow **normal operator precedence** only.

```
Multiplication (*) and division (/) happen before addition (+) and subtraction (-)
```

Example:

```
3 + 3 * 3 = 12
```

not

```
(3 + 3) * 3
```

---

### 3. Unary Negation Is Not Allowed

Expressions like:

```
-x
```

are **not allowed**.

However, subtraction between two expressions **is allowed**:

Valid:

```
x - x
```

Invalid:

```
-x + x
```

---

## Objective

Construct an expression using only:

```
x
+  -  *  /
```

that evaluates exactly to:

```
target
```

while minimizing the **number of operators used**.

Return the **minimum number of operators** required.

---

# Examples

## Example 1

### Input

```
x = 3
target = 19
```

### Output

```
5
```

### Explanation

One optimal expression:

```
3 * 3 + 3 * 3 + 3 / 3
```

This evaluates to:

```
9 + 9 + 1 = 19
```

Number of operators:

```
5
```

---

## Example 2

### Input

```
x = 5
target = 501
```

### Output

```
8
```

### Explanation

One optimal expression:

```
5 * 5 * 5 * 5 - 5 * 5 * 5 + 5 / 5
```

Evaluation:

```
625 - 125 + 1 = 501
```

Number of operators:

```
8
```

---

## Example 3

### Input

```
x = 100
target = 100000000
```

### Output

```
3
```

### Explanation

```
100 * 100 * 100 * 100
```

Evaluation:

```
100000000
```

Number of operators:

```
3
```

---

# Constraints

```
2 <= x <= 100
1 <= target <= 2 * 10^8
```

---
