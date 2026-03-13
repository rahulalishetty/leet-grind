# 3312. Sorted GCD Pair Queries

You are given an integer array `nums` of length `n` and an integer array `queries`.

Let `gcdPairs` denote an array obtained by:

1. Calculating the **GCD of all possible pairs** `(nums[i], nums[j])` where:

```
0 <= i < j < n
```

2. Sorting these GCD values in **ascending order**.

For each query `queries[i]`, you must return the element at index `queries[i]` in the sorted `gcdPairs` array.

Return an integer array `answer`, where:

```
answer[i] = gcdPairs[queries[i]]
```

---

# Definitions

The function:

```
gcd(a, b)
```

denotes the **greatest common divisor** of integers `a` and `b`.

---

# Example 1

## Input

```
nums = [2,3,4]
queries = [0,2,2]
```

## Process

Compute all pairs:

```
gcd(2,3) = 1
gcd(2,4) = 2
gcd(3,4) = 1
```

So:

```
gcdPairs = [1,2,1]
```

After sorting:

```
[1,1,2]
```

## Output

```
[1,2,2]
```

Explanation:

```
queries[0] -> gcdPairs[0] = 1
queries[1] -> gcdPairs[2] = 2
queries[2] -> gcdPairs[2] = 2
```

---

# Example 2

## Input

```
nums = [4,4,2,1]
queries = [5,3,1,0]
```

## Process

All pair GCD values:

```
gcd(4,4) = 4
gcd(4,2) = 2
gcd(4,1) = 1
gcd(4,2) = 2
gcd(4,1) = 1
gcd(2,1) = 1
```

Sorted:

```
[1,1,1,2,2,4]
```

## Output

```
[4,2,1,1]
```

---

# Example 3

## Input

```
nums = [2,2]
queries = [0,0]
```

## Process

```
gcdPairs = [2]
```

## Output

```
[2,2]
```

---

# Constraints

```
2 <= n == nums.length <= 10^5
1 <= nums[i] <= 5 * 10^4
1 <= queries.length <= 10^5
0 <= queries[i] < n * (n - 1) / 2
```
