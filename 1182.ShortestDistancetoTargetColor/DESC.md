# 1182. Shortest Distance to Target Color

## Problem Description

You are given an array `colors`, in which there are three possible colors:

```
1, 2, and 3
```

You are also given an array of queries. Each query contains two integers:

```
[i, c]
```

For each query, return the **shortest distance** between index `i` and the **nearest occurrence of color `c`** in the array.

If the target color does **not exist** in the array, return:

```
-1
```

The distance between two indices is defined as:

```
|i - j|
```

where `j` is an index such that `colors[j] == c`.

---

# Examples

## Example 1

### Input

```
colors = [1,1,2,1,3,2,2,3,3]
queries = [[1,3],[2,2],[6,1]]
```

### Output

```
[3,0,3]
```

### Explanation

Query 1:

```
index = 1, color = 3
```

Nearest `3` occurs at index `4`.

```
distance = |1 - 4| = 3
```

---

Query 2:

```
index = 2, color = 2
```

The element at index `2` is already `2`.

```
distance = 0
```

---

Query 3:

```
index = 6, color = 1
```

Indices containing `1` are:

```
[0, 1, 3]
```

Nearest is index `3`.

```
distance = |6 - 3| = 3
```

---

## Example 2

### Input

```
colors = [1,2]
queries = [[0,3]]
```

### Output

```
[-1]
```

### Explanation

Color `3` does not exist in the array, so the answer is `-1`.

---

# Constraints

```
1 <= colors.length <= 5 * 10^4
1 <= colors[i] <= 3
1 <= queries.length <= 5 * 10^4
queries[i].length == 2
0 <= queries[i][0] < colors.length
1 <= queries[i][1] <= 3
```

---

# Key Observations

1. The color values are limited to:

```
1, 2, 3
```

2. The number of queries can be large:

```
up to 50,000
```

3. A naive approach scanning the entire array for each query would take:

```
O(N * Q)
```

which would be too slow.

Therefore, we need **preprocessing** or **binary search optimization**.

---

# Core Idea

For each query `(i, c)` we want:

```
min(|i - j|) for all j such that colors[j] == c
```

Two common approaches:

1. **Binary Search with Index Lists**
2. **Precomputed Distance Table**

Both reduce the query time significantly.

---

# Approach Overview

## Binary Search Approach

Steps:

1. Store indices of each color in a list.

Example:

```
1 -> [0,1,3]
2 -> [2,5,6]
3 -> [4,7,8]
```

2. For each query:

```
(i, c)
```

3. Binary search the position of `i` inside the color list.

4. Compare nearest indices.

### Time Complexity

```
O(N + Q log N)
```

### Space Complexity

```
O(N)
```

---

## Precomputed Distance Approach

Precompute the nearest distance from every index to each color.

Create a table:

```
distance[color][index]
```

Then each query becomes:

```
distance[c][i]
```

### Time Complexity

```
O(N + Q)
```

### Space Complexity

```
O(N)
```

---

# Final Takeaway

This problem is a classic example of **preprocessing to speed up queries**.

Two strong strategies:

```
Binary Search on sorted index lists
```

or

```
Precompute nearest color distances
```

For this problem, because colors are limited to **3**, the **precomputation approach is usually the fastest**.
