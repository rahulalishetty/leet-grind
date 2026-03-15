# 1923. Longest Common Subpath

## Problem

There is a country of **n cities** numbered from **0 to n - 1**.
In this country, there is a **road connecting every pair of cities**.

There are **m friends** traveling through the country. Each friend follows a **path of cities**.

Each path:

- Is represented by an integer array.
- Contains cities in the order visited.
- May revisit cities.
- But **the same city never appears consecutively**.

You are given:

- An integer `n` representing the number of cities.
- A 2D array `paths`, where `paths[i]` is the path of the `i-th` friend.

Return the **length of the longest common subpath** that appears in **every friend's path**.

If no such subpath exists, return **0**.

A **subpath** is a **contiguous sequence of cities** in the path.

---

## Example 1

### Input

```
n = 5
paths = [
  [0,1,2,3,4],
  [2,3,4],
  [4,0,1,2,3]
]
```

### Output

```
2
```

### Explanation

The longest common subpath shared by all paths is:

```
[2,3]
```

---

## Example 2

### Input

```
n = 3
paths = [[0],[1],[2]]
```

### Output

```
0
```

### Explanation

There is **no common subpath** among all paths.

---

## Example 3

### Input

```
n = 5
paths = [
  [0,1,2,3,4],
  [4,3,2,1,0]
]
```

### Output

```
1
```

### Explanation

The longest possible common subpaths are:

```
[0], [1], [2], [3], [4]
```

All have length **1**.

---

## Constraints

```
1 <= n <= 10^5
2 <= m <= 10^5
sum(paths[i].length) <= 10^5
0 <= paths[i][j] < n
```

Additional guarantees:

- Cities may repeat in a path.
- But **the same city will not appear consecutively**.
