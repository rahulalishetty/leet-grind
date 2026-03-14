# 1815. Maximum Number of Groups Getting Fresh Donuts

## Problem Description

There is a donut shop that bakes donuts in **batches of size `batchSize`**.

The shop follows a strict serving rule:

- **All donuts from the current batch must be served before starting the next batch.**

You are given:

- An integer `batchSize`
- An integer array `groups`, where `groups[i]` represents the number of customers in the `i-th` group.

Each customer receives **exactly one donut**.

### Important Serving Rules

When a group arrives:

- All customers in that group must be served **before the next group starts receiving donuts**.
- A group is considered **happy** if the **first customer of that group receives a fresh donut**, meaning:
  - There are **no leftover donuts from the previous group**.

You are allowed to **rearrange the order of the groups**.

Return the **maximum number of happy groups** that can be achieved.

---

# Example 1

### Input

```
batchSize = 3
groups = [1,2,3,4,5,6]
```

### Output

```
4
```

### Explanation

One optimal ordering:

```
[6,2,4,5,1,3]
```

Happy groups:

```
1st group
2nd group
4th group
6th group
```

Total happy groups = **4**

---

# Example 2

### Input

```
batchSize = 4
groups = [1,3,2,5,2,2,1,6]
```

### Output

```
4
```

---

# Constraints

```
1 <= batchSize <= 9
1 <= groups.length <= 30
1 <= groups[i] <= 10^9
```

---

# Key Observations

Important properties of the problem:

1. The **exact number of donuts left after each group** determines whether the next group is happy.
2. Only the **remainder modulo `batchSize`** matters.

Instead of tracking the full group size, we track:

```
groups[i] % batchSize
```

This reduces the problem complexity significantly.

---

# Core Insight

If a group's remainder is:

```
r = groupSize % batchSize
```

Then it consumes `r` donuts from the current batch.

If the current leftover before serving the group is:

```
0
```

then the group is **happy**.

The leftover after serving becomes:

```
(leftover + r) % batchSize
```

---

# Problem Transformation

Instead of working with the raw groups, we convert them into counts of remainders:

```
count[0], count[1], ..., count[batchSize-1]
```

Where:

```
count[r] = number of groups with remainder r
```

Groups with remainder `0`:

```
groupSize % batchSize == 0
```

always start fresh → always happy.

---

# Typical Strategy (High-Level)

Common approaches used for this problem:

### 1. Greedy Pairing

Pairs such as:

```
r + (batchSize - r) = batchSize
```

cancel out perfectly.

Example:

```
r = 2
batchSize = 5
matching remainder = 3
```

These pairs can be served together to avoid leftovers.

---

### 2. State Compression + Dynamic Programming

After greedy reduction, remaining groups are solved with:

```
DFS + Memoization
```

or

```
Bitmask / state compression DP
```

State contains:

```
remaining counts of each remainder
current leftover donuts
```

---

# Why This Problem Is Hard

Although `groups.length <= 30`, the search space is huge because:

```
30! permutations
```

However:

```
batchSize <= 9
```

This allows compression by tracking **counts of remainders** instead of permutations.

---

# Key Techniques Used

Typical competitive programming techniques used in solutions:

- Greedy remainder pairing
- State compression
- DFS with memoization
- Bitmask DP
- Hashing states

---

# Summary

Goal:

```
Rearrange groups to maximize happy groups
```

Key trick:

```
Use groupSize % batchSize
```

Then solve using:

```
Greedy + DP on remainder counts
```
