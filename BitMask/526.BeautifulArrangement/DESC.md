# 526. Beautiful Arrangement

## Problem Description

Suppose you have `n` integers labeled from:

```
1 to n
```

A **permutation** of these integers is considered a **beautiful arrangement** if for every position `i` (1-indexed):

```
1 <= i <= n
```

**One of the following conditions holds:**

1. `perm[i]` is divisible by `i`
2. `i` is divisible by `perm[i]`

Your task is to compute the **total number of beautiful arrangements** that can be formed.

---

# Function Signature

```
int countArrangement(int n)
```

---

# Example 1

### Input

```
n = 2
```

### Output

```
2
```

### Explanation

Possible permutations:

```
[1,2]
[2,1]
```

Both satisfy the beautiful arrangement condition.

#### Arrangement 1: `[1,2]`

Position `i = 1`

```
perm[1] = 1
1 % 1 = 0
```

Condition satisfied.

Position `i = 2`

```
perm[2] = 2
2 % 2 = 0
```

Condition satisfied.

---

#### Arrangement 2: `[2,1]`

Position `i = 1`

```
perm[1] = 2
2 % 1 = 0
```

Condition satisfied.

Position `i = 2`

```
perm[2] = 1
2 % 1 = 0
```

Condition satisfied.

---

# Example 2

### Input

```
n = 1
```

### Output

```
1
```

### Explanation

Only permutation:

```
[1]
```

This satisfies the condition because:

```
1 % 1 = 0
```

---

# Constraints

```
1 <= n <= 15
```

---
