# 956. Tallest Billboard

## Problem Description

You are installing a billboard and want it to have the **largest possible height**.
The billboard will have **two steel supports**, one on each side, and **both supports must have equal height**.

You are given a collection of rods that can be **welded together**. For example:

- If you have rods of lengths **1, 2, and 3**, you can weld them to form a support of length **6**.

Your task is to determine the **largest possible height** of the billboard such that:

- The rods can be divided into **two disjoint groups**
- The **sum of rods in both groups is equal**

If it is impossible to form two supports with equal height, return **0**.

---

## Example 1

### Input

```
rods = [1,2,3,6]
```

### Output

```
6
```

### Explanation

We can form two groups:

```
{1,2,3} = 6
{6}     = 6
```

Both supports have equal height **6**, so the billboard height is **6**.

---

## Example 2

### Input

```
rods = [1,2,3,4,5,6]
```

### Output

```
10
```

### Explanation

Two valid groups are:

```
{2,3,5} = 10
{4,6}   = 10
```

So the billboard height is **10**.

---

## Example 3

### Input

```
rods = [1,2]
```

### Output

```
0
```

### Explanation

It is impossible to divide the rods into two groups with equal sum.

Therefore, the billboard cannot be supported.

---

## Constraints

```
1 <= rods.length <= 20
1 <= rods[i] <= 1000
sum(rods[i]) <= 5000
```

---

## Key Idea

The problem reduces to finding **two disjoint subsets of rods whose sums are equal** while maximizing that sum.

This is similar to a **partition problem** where we want the **largest equal subset sum**.

---

## Difficulty

```
Hard
```
