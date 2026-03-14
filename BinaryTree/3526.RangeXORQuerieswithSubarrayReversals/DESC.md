# 3526. Range XOR Queries with Subarray Reversals

## Problem

You are given an integer array `nums` of length `n` and a 2D integer array `queries` of length `q`. Each query can be one of the following three types:

### 1. Update

```
queries[i] = [1, index, value]
```

Set:

```
nums[index] = value
```

---

### 2. Range XOR Query

```
queries[i] = [2, left, right]
```

Compute the **bitwise XOR** of all elements in the subarray:

```
nums[left ... right]
```

Record this result.

---

### 3. Reverse Subarray

```
queries[i] = [3, left, right]
```

Reverse the subarray:

```
nums[left ... right]
```

in place.

---

## Goal

Return an array containing the results of all **Range XOR queries** in the order they appear.

---

# Example 1

### Input

```
nums = [1,2,3,4,5]
queries = [[2,1,3],[1,2,10],[3,0,4],[2,0,4]]
```

### Output

```
[5,8]
```

### Explanation

Query 1:

```
[2,1,3]
```

XOR of `[2,3,4]`

```
2 ^ 3 ^ 4 = 5
```

Query 2:

```
[1,2,10]
```

Update:

```
nums = [1,2,10,4,5]
```

Query 3:

```
[3,0,4]
```

Reverse entire array:

```
nums = [5,4,10,2,1]
```

Query 4:

```
[2,0,4]
```

```
5 ^ 4 ^ 10 ^ 2 ^ 1 = 8
```

Result:

```
[5,8]
```

---

# Example 2

### Input

```
nums = [7,8,9]
queries = [[1,0,3],[2,0,2],[3,1,2]]
```

### Output

```
[2]
```

### Explanation

Query 1:

```
[1,0,3]
```

Update:

```
nums = [3,8,9]
```

Query 2:

```
[2,0,2]
```

```
3 ^ 8 ^ 9 = 2
```

Query 3:

```
[3,1,2]
```

Reverse `[8,9]`

```
nums = [3,9,8]
```

Result:

```
[2]
```

---

# Constraints

```
1 ≤ nums.length ≤ 10^5
0 ≤ nums[i] ≤ 10^9

1 ≤ queries.length ≤ 10^5
queries[i].length == 3

queries[i][0] ∈ {1,2,3}

If type == 1:
    0 ≤ index < nums.length
    0 ≤ value ≤ 10^9

If type == 2 or 3:
    0 ≤ left ≤ right < nums.length
```
