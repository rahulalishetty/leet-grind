# 3323. Minimize Connected Groups by Inserting Interval

## Problem Description

You are given a 2D array `intervals`, where:

```
intervals[i] = [start_i, end_i]
```

represents an interval starting at `start_i` and ending at `end_i`.

You are also given an integer `k`.

You must add **exactly one new interval**:

```
[start_new, end_new]
```

such that:

```
end_new - start_new <= k
```

Your goal is to **minimize the number of connected groups** of intervals after inserting the new interval.

---

# Definition: Connected Group

A **connected group of intervals** is a maximal set of intervals that together cover a continuous range **without gaps**.

### Example of a connected group

```
[[1,2], [2,5], [3,3]]
```

These intervals together cover:

```
[1,5]
```

with **no gaps**, so they form **one connected group**.

---

### Example of a non‑connected set

```
[[1,2], [3,4]]
```

There is a gap:

```
(2,3)
```

so they form **two groups**.

---

# Objective

Add exactly one interval of length **≤ k** so that the number of connected groups becomes **as small as possible**.

Return the **minimum number of connected groups** after inserting the interval.

---

# Example 1

### Input

```
intervals = [[1,3],[5,6],[8,10]]
k = 3
```

### Output

```
2
```

### Explanation

Insert:

```
[3,5]
```

Resulting intervals:

```
[[1,3],[3,5],[5,6],[8,10]]
```

Connected groups:

```
Group 1 → [[1,3],[3,5],[5,6]]
Group 2 → [[8,10]]
```

Total groups = **2**.

---

# Example 2

### Input

```
intervals = [[5,10],[1,1],[3,3]]
k = 1
```

### Output

```
3
```

### Explanation

Insert:

```
[1,1]
```

Groups become:

```
[[1,1],[1,1]]
[[3,3]]
[[5,10]]
```

Total groups = **3**.

---

# Constraints

```
1 <= intervals.length <= 10^5
```

```
intervals[i] = [start_i, end_i]
```

```
1 <= start_i <= end_i <= 10^9
```

```
1 <= k <= 10^9
```
