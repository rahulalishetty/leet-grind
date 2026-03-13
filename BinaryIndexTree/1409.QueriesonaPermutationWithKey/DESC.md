# 1409. Queries on a Permutation With Key

## Problem Statement

You are given an array **queries** consisting of positive integers between **1** and **m**.

Initially, you have a permutation:

```
P = [1,2,3,...,m]
```

You must process each query according to the following rules:

1. Find the **position (0-indexed)** of `queries[i]` in the permutation `P`.
2. Record that position as the answer for this query.
3. Move the element `queries[i]` to the **beginning** of the permutation `P`.

Return the array containing the recorded positions for each query.

---

# Example 1

## Input

```
queries = [3,1,2,1], m = 5
```

## Output

```
[2,1,2,1]
```

## Explanation

Initial permutation:

```
P = [1,2,3,4,5]
```

Step-by-step processing:

### i = 0

```
query = 3
position = 2
```

Move 3 to front:

```
P = [3,1,2,4,5]
```

### i = 1

```
query = 1
position = 1
```

Move 1 to front:

```
P = [1,3,2,4,5]
```

### i = 2

```
query = 2
position = 2
```

Move 2 to front:

```
P = [2,1,3,4,5]
```

### i = 3

```
query = 1
position = 1
```

Move 1 to front:

```
P = [1,2,3,4,5]
```

Final result:

```
[2,1,2,1]
```

---

# Example 2

## Input

```
queries = [4,1,2,2], m = 4
```

## Output

```
[3,1,2,0]
```

---

# Example 3

## Input

```
queries = [7,5,5,8,3], m = 8
```

## Output

```
[6,5,0,7,5]
```

---

# Constraints

```
1 <= m <= 10^3
1 <= queries.length <= m
1 <= queries[i] <= m
```

---
