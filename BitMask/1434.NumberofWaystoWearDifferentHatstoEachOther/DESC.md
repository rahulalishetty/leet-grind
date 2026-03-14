# 1434. Number of Ways to Wear Different Hats to Each Other

## Problem Description

There are **n people** and **40 types of hats** labeled from **1 to 40**.

You are given a 2D integer array:

```
hats
```

Where:

```
hats[i]
```

contains a list of hats preferred by the **i-th person**.

Your task is to compute the **number of ways to assign hats** such that:

- Each person gets **exactly one hat**
- **No two people wear the same hat**
- Each person must wear a hat from their **preferred list**

Because the answer may be very large, return it modulo:

```
10^9 + 7
```

---

# Example 1

## Input

```
hats = [[3,4],[4,5],[5]]
```

## Output

```
1
```

## Explanation

Only one valid assignment exists:

```
Person 0 -> Hat 3
Person 1 -> Hat 4
Person 2 -> Hat 5
```

---

# Example 2

## Input

```
hats = [[3,5,1],[3,5]]
```

## Output

```
4
```

## Explanation

Possible assignments:

```
(3,5)
(5,3)
(1,3)
(1,5)
```

---

# Example 3

## Input

```
hats = [[1,2,3,4],
        [1,2,3,4],
        [1,2,3,4],
        [1,2,3,4]]
```

## Output

```
24
```

## Explanation

Each person can choose from hats **1–4**.

The number of permutations of:

```
(1,2,3,4)
```

is:

```
4! = 24
```

---

# Constraints

```
n == hats.length
```

```
1 <= n <= 10
```

```
1 <= hats[i].length <= 40
```

```
1 <= hats[i][j] <= 40
```

```
hats[i] contains unique integers
```

---

# Notes

- Each hat can be worn by **only one person**.
- Each person must receive **exactly one hat**.
- Hat choices are restricted by each person's **preference list**.
