# 3539. Find Sum of Array Product of Magical Sequences

## Problem Statement

You are given:

- Two integers `m` and `k`
- An integer array `nums`

A sequence `seq` is called **magical** if:

1. `seq` has size `m`
2. `0 <= seq[i] < nums.length`
3. The binary representation of:

```
2^seq[0] + 2^seq[1] + ... + 2^seq[m-1]
```

contains exactly **k set bits**.

---

## Array Product

For a valid sequence:

```
seq = [a, b, c, ...]
```

The **array product** is defined as:

```
prod(seq) = nums[a] * nums[b] * nums[c] * ...
```

---

## Objective

Return the **sum of the array products** for **all magical sequences**.

Since the result can be large, return it modulo:

```
10^9 + 7
```

---

# Definitions

### Set Bit

A **set bit** is a bit equal to **1** in the binary representation of a number.

Example:

```
13 = 1101 (3 set bits)
```

---

# Example 1

## Input

```
m = 5
k = 5
nums = [1,10,100,10000,1000000]
```

## Output

```
991600007
```

## Explanation

All permutations of:

```
[0,1,2,3,4]
```

are magical sequences.

Each sequence has the same array product:

```
10^13
```

The sum of all such products modulo `10^9 + 7` equals:

```
991600007
```

---

# Example 2

## Input

```
m = 2
k = 2
nums = [5,4,3,2,1]
```

## Output

```
170
```

## Explanation

The magical sequences include:

```
[0,1], [0,2], [0,3], [0,4],
[1,0], [1,2], [1,3], [1,4],
[2,0], [2,1], [2,3], [2,4],
[3,0], [3,1], [3,2], [3,4],
[4,0], [4,1], [4,2], [4,3]
```

Each sequence contributes its array product to the total sum.

---

# Example 3

## Input

```
m = 1
k = 1
nums = [28]
```

## Output

```
28
```

## Explanation

The only magical sequence is:

```
[0]
```

Array product:

```
28
```

---

# Constraints

```
1 <= k <= m <= 30
1 <= nums.length <= 50
1 <= nums[i] <= 10^8
```
