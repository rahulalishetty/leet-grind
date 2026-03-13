# 2601. Prime Subtraction Operation

You are given a **0-indexed integer array `nums`** of length `n`.

You can perform the following operation **as many times as you want**:

1. Pick an index `i` that **you haven't picked before**.
2. Pick a **prime number `p` strictly less than `nums[i]`**.
3. Subtract `p` from `nums[i]`.

Return **true** if you can make `nums` a **strictly increasing array** using the above operation, otherwise return **false**.

---

# Definition

### Strictly Increasing Array

An array is strictly increasing if:

```
nums[i] > nums[i-1]  for every i > 0
```

---

# Example 1

## Input

```
nums = [4,9,6,10]
```

## Explanation

First operation:

```
i = 0
p = 3
nums[0] = 4 - 3 = 1
```

Array becomes:

```
[1,9,6,10]
```

Second operation:

```
i = 1
p = 7
nums[1] = 9 - 7 = 2
```

Array becomes:

```
[1,2,6,10]
```

Now the array is strictly increasing.

## Output

```
true
```

---

# Example 2

## Input

```
nums = [6,8,11,12]
```

## Explanation

The array is already strictly increasing, so no operation is needed.

## Output

```
true
```

---

# Example 3

## Input

```
nums = [5,8,3]
```

## Explanation

No sequence of operations can make the array strictly increasing.

## Output

```
false
```

---

# Constraints

```
1 <= nums.length <= 1000
1 <= nums[i] <= 1000
nums.length == n
```

---

# Problem Goal

Determine whether it is possible to transform the array into a **strictly increasing sequence** by subtracting **one prime number (less than the element)** from each index **at most once**.
