# 2338. Count the Number of Ideal Arrays

You are given two integers **n** and **maxValue**, which are used to describe an **ideal array**.

A **0-indexed integer array** `arr` of length `n` is considered **ideal** if the following conditions hold:

1. Every `arr[i]` is a value from **1 to maxValue**, for `0 <= i < n`.
2. Every `arr[i]` is **divisible by** `arr[i - 1]`, for `0 < i < n`.

Return the number of **distinct ideal arrays** of length `n`. Since the answer may be very large, return it **modulo 10^9 + 7**.

---

# Example 1

## Input

```
n = 2
maxValue = 5
```

## Output

```
10
```

## Explanation

The possible ideal arrays are:

Arrays starting with **1** (5 arrays):

```
[1,1]
[1,2]
[1,3]
[1,4]
[1,5]
```

Arrays starting with **2** (2 arrays):

```
[2,2]
[2,4]
```

Arrays starting with **3** (1 array):

```
[3,3]
```

Arrays starting with **4** (1 array):

```
[4,4]
```

Arrays starting with **5** (1 array):

```
[5,5]
```

Total:

```
5 + 2 + 1 + 1 + 1 = 10
```

---

# Example 2

## Input

```
n = 5
maxValue = 3
```

## Output

```
11
```

## Explanation

Arrays starting with **1** (9 arrays):

No other distinct values:

```
[1,1,1,1,1]
```

With second value **2**:

```
[1,1,1,1,2]
[1,1,1,2,2]
[1,1,2,2,2]
[1,2,2,2,2]
```

With second value **3**:

```
[1,1,1,1,3]
[1,1,1,3,3]
[1,1,3,3,3]
[1,3,3,3,3]
```

Arrays starting with **2**:

```
[2,2,2,2,2]
```

Arrays starting with **3**:

```
[3,3,3,3,3]
```

Total:

```
9 + 1 + 1 = 11
```

---

# Constraints

```
2 <= n <= 10^4
1 <= maxValue <= 10^4
```
