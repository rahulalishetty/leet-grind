# 1994. The Number of Good Subsets

You are given an integer array `nums`. We call a subset of `nums` **good** if its product can be represented as a product of **one or more distinct prime numbers**.

For example, if:

```
nums = [1, 2, 3, 4]
```

Then:

- `[2, 3]` → product = `6 = 2 × 3`
- `[1, 2, 3]` → product = `6 = 2 × 3`
- `[1, 3]` → product = `3`

are **good subsets**.

However:

- `[1, 4]` → product = `4 = 2 × 2`
- `[4]` → product = `4 = 2 × 2`

are **not good subsets** because the product contains repeated prime factors.

Return the **number of different good subsets** in `nums` modulo:

```
10^9 + 7
```

---

# Definition

A **subset** of `nums` is any array obtained by deleting some (possibly none or all) elements of `nums`.

Two subsets are considered **different** if the **indices chosen are different**.

---

# Example 1

### Input

```
nums = [1,2,3,4]
```

### Output

```
6
```

### Explanation

The good subsets are:

- `[1,2]` → product = `2`
- `[1,2,3]` → product = `6 = 2 × 3`
- `[1,3]` → product = `3`
- `[2]` → product = `2`
- `[2,3]` → product = `6`
- `[3]` → product = `3`

Total = **6**.

---

# Example 2

### Input

```
nums = [4,2,3,15]
```

### Output

```
5
```

### Explanation

The good subsets are:

- `[2]` → product = `2`
- `[2,3]` → product = `6`
- `[2,15]` → product = `30 = 2 × 3 × 5`
- `[3]` → product = `3`
- `[15]` → product = `15 = 3 × 5`

Total = **5**.

---

# Constraints

```
1 <= nums.length <= 10^5
1 <= nums[i] <= 30
```
