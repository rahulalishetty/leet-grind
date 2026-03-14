# 3149. Find the Minimum Cost Array Permutation

## Problem Statement

You are given an array **nums** which is a permutation of:

```
[0, 1, 2, ..., n - 1]
```

The **score** of any permutation of `[0, 1, 2, ..., n - 1]` named **perm** is defined as:

```
score(perm) =
|perm[0] - nums[perm[1]]| +
|perm[1] - nums[perm[2]]| +
...
+ |perm[n - 1] - nums[perm[0]]|
```

Your task is to:

- Find the permutation **perm** that produces the **minimum possible score**
- If multiple permutations have the same minimum score, return the **lexicographically smallest permutation**

---

# Example 1

## Input

```
nums = [1,0,2]
```

## Output

```
[0,1,2]
```

## Explanation

The lexicographically smallest permutation with minimum cost is:

```
[0,1,2]
```

The score is:

```
|0 - nums[1]| + |1 - nums[2]| + |2 - nums[0]|
= |0 - 0| + |1 - 2| + |2 - 1|
= 0 + 1 + 1
= 2
```

---

# Example 2

## Input

```
nums = [0,2,1]
```

## Output

```
[0,2,1]
```

## Explanation

The lexicographically smallest permutation with minimum cost is:

```
[0,2,1]
```

The score is:

```
|0 - nums[2]| + |2 - nums[1]| + |1 - nums[0]|
= |0 - 1| + |2 - 2| + |1 - 0|
= 1 + 0 + 1
= 2
```

---

# Constraints

```
2 <= n == nums.length <= 14
nums is a permutation of [0, 1, 2, ..., n - 1]
```

---

# Summary

We must find a permutation **perm** of `[0..n-1]` that minimizes the cyclic cost:

```
|perm[i] - nums[perm[i+1]]|
```

with the final term wrapping around:

```
|perm[n-1] - nums[perm[0]]|
```

If multiple permutations yield the same minimum score, we return the **lexicographically smallest permutation**.
