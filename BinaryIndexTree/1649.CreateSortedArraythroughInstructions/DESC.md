# 1649. Create Sorted Array through Instructions

## Problem Statement

You are given an integer array **instructions**.

You must create a **sorted array `nums`** by inserting elements from `instructions` one by one from **left to right**.

Initially:

```
nums = []
```

For each `instructions[i]`, insert it into `nums`.

The **cost of insertion** is defined as:

```
min(
    number of elements in nums strictly less than instructions[i],
    number of elements in nums strictly greater than instructions[i]
)
```

After computing the cost, insert the element into `nums` so that it remains sorted.

Return the **total insertion cost**.

Since the answer may be large, return it **modulo (10^9 + 7)**.

---

# Example 1

## Input

```
instructions = [1,5,6,2]
```

## Output

```
1
```

## Explanation

Start with:

```
nums = []
```

Insert `1`

```
less = 0
greater = 0
cost = 0
nums = [1]
```

Insert `5`

```
less = 1
greater = 0
cost = 0
nums = [1,5]
```

Insert `6`

```
less = 2
greater = 0
cost = 0
nums = [1,5,6]
```

Insert `2`

```
less = 1
greater = 2
cost = 1
nums = [1,2,5,6]
```

Total cost:

```
0 + 0 + 0 + 1 = 1
```

---

# Example 2

## Input

```
instructions = [1,2,3,6,5,4]
```

## Output

```
3
```

## Explanation

Process step-by-step:

Insert 1 → cost = 0 → nums = [1]

Insert 2 → cost = 0 → nums = [1,2]

Insert 3 → cost = 0 → nums = [1,2,3]

Insert 6 → cost = 0 → nums = [1,2,3,6]

Insert 5 → cost = min(3,1) = 1 → nums = [1,2,3,5,6]

Insert 4 → cost = min(3,2) = 2 → nums = [1,2,3,4,5,6]

Total cost:

```
3
```

---

# Example 3

## Input

```
instructions = [1,3,3,3,2,4,2,1,2]
```

## Output

```
4
```

## Explanation

Step-by-step insertions:

```
Insert 1 → cost 0 → [1]
Insert 3 → cost 0 → [1,3]
Insert 3 → cost 0 → [1,3,3]
Insert 3 → cost 0 → [1,3,3,3]
Insert 2 → cost 1 → [1,2,3,3,3]
Insert 4 → cost 0 → [1,2,3,3,3,4]
Insert 2 → cost 1 → [1,2,2,3,3,3,4]
Insert 1 → cost 0 → [1,1,2,2,3,3,3,4]
Insert 2 → cost 2 → [1,1,2,2,2,3,3,3,4]
```

Total cost:

```
4
```

---

# Constraints

```
1 <= instructions.length <= 10^5
1 <= instructions[i] <= 10^5
```

---
