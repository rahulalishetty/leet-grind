# 2818. Apply Operations to Maximize Score

You are given an array `nums` of `n` positive integers and an integer `k`.

Initially, you start with a **score of 1**. You have to maximize your score by applying the following operation **at most `k` times**:

1. Choose any **non-empty subarray** `nums[l, ..., r]` that you **haven't chosen previously**.
2. Choose an element `x` of `nums[l, ..., r]` with the **highest prime score**.
3. If multiple such elements exist, choose the one with the **smallest index**.
4. Multiply your score by `x`.

Here, `nums[l, ..., r]` denotes the subarray of `nums` starting at index `l` and ending at index `r`, both inclusive.

---

## Prime Score

The **prime score** of an integer `x` is equal to the **number of distinct prime factors of `x`**.

Example:

```
300 = 2 × 2 × 3 × 5 × 5
```

Distinct primes are:

```
{2,3,5}
```

So the **prime score = 3**.

---

# Goal

Return the **maximum possible score** after applying **at most `k` operations**.

Since the answer may be large, return it **modulo**:

```
10^9 + 7
```

---

# Example 1

## Input

```
nums = [8,3,9,3,8]
k = 2
```

## Output

```
81
```

## Explanation

To obtain the maximum score:

Operation 1:

Choose subarray:

```
[2,2]
```

Only element:

```
9
```

Score becomes:

```
1 × 9 = 9
```

Operation 2:

Choose subarray:

```
[2,3]
```

Prime scores:

```
9 -> 1
3 -> 1
```

Both equal, choose **smaller index (2)**.

Multiply:

```
9 × 9 = 81
```

Final score:

```
81
```

---

# Example 2

## Input

```
nums = [19,12,14,6,10,18]
k = 3
```

## Output

```
4788
```

## Explanation

Operations:

### Operation 1

Choose:

```
[0,0]
```

Multiply:

```
1 × 19 = 19
```

### Operation 2

Choose:

```
[5,5]
```

Multiply:

```
19 × 18 = 342
```

### Operation 3

Choose:

```
[2,3]
```

Prime scores:

```
14 -> 2
6  -> 2
```

Tie → choose smaller index **2**.

Multiply:

```
342 × 14 = 4788
```

Final score:

```
4788
```

---

# Constraints

```
1 ≤ nums.length = n ≤ 10^5
1 ≤ nums[i] ≤ 10^5
1 ≤ k ≤ min(n * (n + 1) / 2, 10^9)
```
