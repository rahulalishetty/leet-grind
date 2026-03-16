# 1182. Shortest Distance to Target Color

## Problem Description

You are given an array `colors` where each element represents a color.

The array contains **only three possible colors**:

```
1, 2, 3
```

You are also given a list of `queries`.

Each query is defined as:

```
[i, c]
```

Where:

- `i` → index in the `colors` array
- `c` → target color

For each query, you must determine the **shortest distance** between index `i` and any index `j` such that:

```
colors[j] == c
```

If no such index exists, return:

```
-1
```

Distance is defined as:

```
|i - j|
```

---

# Example 1

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
[1,3]
```

Nearest `3` from index `1` is at index `4`.

Distance:

```
|1 - 4| = 3
```

---

Query 2:

```
[2,2]
```

Index `2` itself contains color `2`.

Distance:

```
0
```

---

Query 3:

```
[6,1]
```

Nearest `1` from index `6` is at index `3`.

Distance:

```
|6 - 3| = 3
```

---

# Example 2

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

Color `3` does not exist anywhere in the array, therefore the answer is:

```
-1
```

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

1. There are only **three possible colors**.
2. We may have up to **50,000 queries**.
3. A naive scan for each query would be too slow.

Because of this, efficient solutions typically involve:

- preprocessing the array
- storing nearest occurrences of each color
- answering each query in **O(1)** or **O(log n)** time.

---

# Problem Category

This problem falls under:

```
Array Processing
Preprocessing
Binary Search / Prefix DP
Query Optimization
```
