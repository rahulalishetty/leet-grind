# 1492. The kth Factor of n

You are given two positive integers **n** and **k**.

A **factor** of an integer `n` is defined as an integer `i` such that:

```
n % i == 0
```

This means `i` divides `n` exactly.

Your task is to:

1. Find **all factors of `n`**
2. Sort them in **ascending order**
3. Return the **kth factor** in this sorted list

If `n` has **fewer than `k` factors**, return:

```
-1
```

---

# Example 1

## Input

```
n = 12
k = 3
```

## Output

```
3
```

## Explanation

Factors of 12:

```
[1, 2, 3, 4, 6, 12]
```

The **3rd factor** is:

```
3
```

---

# Example 2

## Input

```
n = 7
k = 2
```

## Output

```
7
```

## Explanation

Factors of 7:

```
[1, 7]
```

The **2nd factor** is:

```
7
```

---

# Example 3

## Input

```
n = 4
k = 4
```

## Output

```
-1
```

## Explanation

Factors of 4:

```
[1, 2, 4]
```

There are only **3 factors**, but `k = 4`, so the result is:

```
-1
```

---

# Constraints

```
1 <= k <= n <= 1000
```

---

# Follow-Up

Can you solve the problem in **less than O(n)** time complexity?
