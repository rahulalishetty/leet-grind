# 3165. Maximum Sum of Subsequence With Non-adjacent Elements

## Problem Description

You are given:

- An integer array **nums**
- A 2D array **queries**, where:

```
queries[i] = [posi, xi]
```

For each query:

1. Update the array:

```
nums[posi] = xi
```

2. Compute the **maximum sum of a subsequence of nums such that no two selected elements are adjacent**.

Finally, return:

```
(sum of answers of all queries) mod (10^9 + 7)
```

---

# Key Definition

### Subsequence

A subsequence is formed by deleting some (or none) of the elements from the array **without changing the order of remaining elements**.

Example:

```
nums = [1,2,3,4]
subsequence = [1,3,4]
```

---

# Constraint on the Subsequence

The subsequence must satisfy:

```
No two selected elements are adjacent
```

Example:

```
nums = [3,5,9]

Valid selections:
[3]
[5]
[9]
[3,9]

Invalid selection:
[5,9]   (adjacent)
```

---

# Example 1

## Input

```
nums = [3,5,9]
queries = [[1,-2],[0,-3]]
```

## Output

```
21
```

## Explanation

### Query 1

```
nums[1] = -2
nums = [3,-2,9]
```

Maximum subsequence sum with non-adjacent elements:

```
3 + 9 = 12
```

---

### Query 2

```
nums[0] = -3
nums = [-3,-2,9]
```

Maximum subsequence sum:

```
9
```

---

Final Answer:

```
12 + 9 = 21
```

---

# Example 2

## Input

```
nums = [0,-1]
queries = [[0,-5]]
```

## Output

```
0
```

## Explanation

After the update:

```
nums = [-5,-1]
```

Best subsequence:

```
(empty subsequence)
sum = 0
```

---

# Constraints

```
1 <= nums.length <= 5 * 10^4
-10^5 <= nums[i] <= 10^5

1 <= queries.length <= 5 * 10^4
queries[i] = [posi, xi]

0 <= posi < nums.length
-10^5 <= xi <= 10^5
```
