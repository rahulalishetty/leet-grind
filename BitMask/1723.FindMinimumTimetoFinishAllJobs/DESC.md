# 1723. Find Minimum Time to Finish All Jobs

## Problem Description

You are given an integer array `jobs`, where:

```
jobs[i]
```

represents the amount of time required to complete the **i-th job**.

You also have **k workers** available.

### Rules

- Each job must be assigned to **exactly one worker**.
- A worker can take **multiple jobs**.
- The **working time of a worker** is the sum of the time of all jobs assigned to them.

Your goal is to **assign jobs to workers** so that the **maximum working time among all workers is minimized**.

Return the **minimum possible maximum working time**.

---

# Example 1

## Input

```
jobs = [3,2,3]
k = 3
```

## Output

```
3
```

## Explanation

Assign each worker one job:

```
Worker 1 -> 3
Worker 2 -> 2
Worker 3 -> 3
```

Maximum working time:

```
max(3,2,3) = 3
```

---

# Example 2

## Input

```
jobs = [1,2,4,7,8]
k = 2
```

## Output

```
11
```

## Explanation

One optimal assignment:

```
Worker 1 -> [1,2,8]  = 11
Worker 2 -> [4,7]    = 11
```

Maximum working time:

```
max(11,11) = 11
```

---

# Constraints

```
1 <= k <= jobs.length <= 12
```

```
1 <= jobs[i] <= 10^7
```

---

# Key Observations

Important limits:

```
jobs.length <= 12
```

This suggests that **exponential or backtracking solutions with pruning** are feasible.

Common approaches used to solve this problem:

- Backtracking with pruning
- Binary Search + DFS feasibility check
- Bitmask Dynamic Programming
- Branch and Bound

The goal is essentially a **balanced partitioning problem** where we minimize the **maximum workload among workers**.
