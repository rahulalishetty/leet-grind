# 2297. Jump Game VIII

## Problem Description

You are given a **0-indexed integer array `nums`** of length `n`. You are initially standing at **index `0`**.

You can jump from index `i` to index `j` where `i < j` if **one of the following conditions holds**:

### Condition 1

```
nums[i] <= nums[j]
and
nums[k] < nums[i] for all indexes k in the range i < k < j
```

### Condition 2

```
nums[i] > nums[j]
and
nums[k] >= nums[i] for all indexes k in the range i < k < j
```

You are also given an **integer array `costs`** of length `n` where:

```
costs[i]
```

denotes the **cost of jumping to index `i`**.

---

## Goal

Return the **minimum cost required to reach index `n - 1`**.

---

# Example 1

### Input

```
nums  = [3,2,4,4,1]
costs = [3,7,6,4,2]
```

### Output

```
8
```

### Explanation

You start at **index 0**.

Possible optimal path:

```
0 -> 2 -> 4
```

Costs:

```
costs[2] = 6
costs[4] = 2
```

Total cost:

```
6 + 2 = 8
```

Other possible paths:

```
0 -> 1 -> 4  (cost = 9)
0 -> 2 -> 3 -> 4 (cost = 12)
```

Thus the **minimum cost is 8**.

---

# Example 2

### Input

```
nums  = [0,1,2]
costs = [1,1,1]
```

### Output

```
2
```

### Explanation

Start at **index 0**.

```
0 -> 1 -> 2
```

Costs:

```
costs[1] = 1
costs[2] = 1
```

Total:

```
1 + 1 = 2
```

You **cannot jump directly from 0 -> 2** because:

```
nums[0] <= nums[1]
```

which violates the jump condition.

---

# Constraints

```
n == nums.length == costs.length
1 <= n <= 10^5
0 <= nums[i], costs[i] <= 10^5
```
