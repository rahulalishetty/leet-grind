# 3569. Maximize Count of Distinct Primes After Split

You are given an integer array `nums` having length `n` and a 2D integer array `queries` where:

```
queries[i] = [idx, val]
```

For each query:

1. Update:

```
nums[idx] = val
```

2. Choose an integer `k` with:

```
1 <= k < n
```

to split the array into:

- Prefix: `nums[0..k-1]`
- Suffix: `nums[k..n-1]`

such that the **sum of the counts of distinct prime values in each part is maximum**.

Note:

Changes made in one query **persist** into the next query.

Return an array containing the result for each query in order.

---

# Example 1

## Input

```
nums = [2,1,3,1,2]
queries = [[1,2],[3,3]]
```

## Output

```
[3,4]
```

## Explanation

Initially:

```
nums = [2, 1, 3, 1, 2]
```

### After Query 1

```
nums[1] = 2
nums = [2, 2, 3, 1, 2]
```

Split:

```
[2] | [2, 3, 1, 2]
```

Distinct primes:

```
prefix = {2} → 1
suffix = {2,3} → 2
```

Result:

```
1 + 2 = 3
```

---

### After Query 2

```
nums[3] = 3
nums = [2, 2, 3, 3, 2]
```

Best split:

```
[2,2,3] | [3,2]
```

Distinct primes:

```
prefix = {2,3} → 2
suffix = {2,3} → 2
```

Result:

```
2 + 2 = 4
```

Output:

```
[3,4]
```

---

# Example 2

## Input

```
nums = [2,1,4]
queries = [[0,1]]
```

## Output

```
[0]
```

## Explanation

Initially:

```
nums = [2,1,4]
```

After update:

```
nums[0] = 1
nums = [1,1,4]
```

There are **no prime numbers** in the array.

So any split gives:

```
0 + 0 = 0
```

Output:

```
[0]
```

---

# Constraints

```
2 <= n == nums.length <= 5 * 10^4
1 <= queries.length <= 5 * 10^4
1 <= nums[i] <= 10^5
0 <= queries[i][0] < nums.length
1 <= queries[i][1] <= 10^5
```
