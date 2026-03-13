# 3336. Find the Number of Subsequences With Equal GCD

You are given an integer array `nums`.

Your task is to find the number of pairs of **non-empty subsequences** `(seq1, seq2)` of `nums` that satisfy the following conditions:

1. The subsequences `seq1` and `seq2` are **disjoint**, meaning no index of `nums` is shared between them.
2. The **GCD** of the elements of `seq1` is equal to the **GCD** of the elements of `seq2`.

Return the **total number of such pairs**.

Since the answer may be very large, return it modulo:

```
10^9 + 7
```

---

# Definitions

- A **subsequence** is obtained by deleting zero or more elements from the array without changing the order of the remaining elements.
- The subsequence must be **non-empty**.
- Two subsequences are **disjoint** if they do not share any index from the original array.

The function:

```
gcd(a, b)
```

denotes the **greatest common divisor** of integers `a` and `b`.

---

# Example 1

## Input

```
nums = [1,2,3,4]
```

## Output

```
10
```

## Explanation

The subsequence pairs which have the GCD of their elements equal to `1` include combinations such as:

```
([1], [1])
([1,2], [1,3])
([1,3], [1,4])
...
```

There are **10 valid pairs** where the GCD of both subsequences is equal.

---

# Example 2

## Input

```
nums = [10,20,30]
```

## Output

```
2
```

## Explanation

The subsequence pairs whose GCD equals **10** are:

```
([10], [20,30])
([20,30], [10])
```

Total valid pairs = **2**.

---

# Example 3

## Input

```
nums = [1,1,1,1]
```

## Output

```
50
```

## Explanation

Every non-empty subsequence has GCD = **1**.

So any two disjoint subsequences form a valid pair.

Total valid combinations = **50**.

---

# Constraints

```
1 <= nums.length <= 200
1 <= nums[i] <= 200
```

---

# Key Observations

1. The **GCD of subsequences** can only take values from `1` to `200`.
2. Since `nums.length <= 200`, dynamic programming over GCD states is feasible.
3. The disjoint condition means each element can be:
   - chosen for subsequence 1
   - chosen for subsequence 2
   - not chosen

Thus each element has **3 choices**, leading to a DP formulation.

---

# Problem Goal

Count the number of **pairs of disjoint subsequences** `(seq1, seq2)` such that:

```
gcd(seq1) == gcd(seq2)
```

Return the result modulo:

```
1_000_000_007
```
