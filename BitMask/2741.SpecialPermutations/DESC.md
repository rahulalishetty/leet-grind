# 2741. Special Permutations

## Problem Statement

You are given a **0-indexed integer array `nums`** containing **n distinct positive integers**.

A permutation of `nums` is called **special** if the following condition holds:

For every index:

```
0 <= i < n - 1
```

either:

```
nums[i] % nums[i+1] == 0
```

or

```
nums[i+1] % nums[i] == 0
```

In other words, **adjacent numbers in the permutation must divide each other**.

Your task is to return the **total number of special permutations**.

Since the result may be large, return it **modulo (10^9 + 7)**.

---

## Example 1

### Input

```
nums = [2,3,6]
```

### Output

```
2
```

### Explanation

The special permutations are:

```
[3,6,2]
[2,6,3]
```

Both satisfy the divisibility condition for every adjacent pair.

---

## Example 2

### Input

```
nums = [1,4,3]
```

### Output

```
2
```

### Explanation

The valid permutations are:

```
[3,1,4]
[4,1,3]
```

Each adjacent pair satisfies the divisibility condition.

---

## Constraints

```
2 <= nums.length <= 14
1 <= nums[i] <= 10^9
nums contains distinct integers
```

---

## Key Observation

A permutation is valid only if **every adjacent pair satisfies a divisibility relationship**:

```
a % b == 0 OR b % a == 0
```

This means we must **check adjacency compatibility** between numbers.

The problem effectively becomes counting permutations where **edges exist between numbers that divide each other**.

---

## Important Notes

- The array contains **distinct integers**
- Maximum `n = 14`
- Result must be returned modulo:

```
10^9 + 7
```

---

## Summary

To determine whether a permutation is **special**, every adjacent pair must satisfy:

```
nums[i] % nums[i+1] == 0 OR nums[i+1] % nums[i] == 0
```

The task is to count **all such permutations** of the array.
