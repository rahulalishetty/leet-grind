# 2921. Maximum Profitable Triplets With Increasing Prices II

## Problem Statement

You are given two **0-indexed arrays**:

- `prices`
- `profits`

Both arrays have length `n`.

There are `n` items in a store where:

```
price of item i = prices[i]
profit of item i = profits[i]
```

We must pick **three items** with indices `i < j < k` such that:

```
prices[i] < prices[j] < prices[k]
```

If we pick such items, the total profit becomes:

```
profits[i] + profits[j] + profits[k]
```

Return the **maximum profit** obtainable.

If it is **not possible** to pick three items satisfying the conditions, return:

```
-1
```

---

# Example 1

## Input

```
prices = [10,2,3,4]
profits = [100,2,7,10]
```

## Output

```
19
```

## Explanation

We cannot choose item `0` because there are no indices `j` and `k` satisfying:

```
prices[0] < prices[j] < prices[k]
```

The valid triplet is:

```
i = 1
j = 2
k = 3
```

Since:

```
prices[1] = 2
prices[2] = 3
prices[3] = 4
```

Which satisfies:

```
2 < 3 < 4
```

Total profit:

```
profits[1] + profits[2] + profits[3]
= 2 + 7 + 10
= 19
```

---

# Example 2

## Input

```
prices = [1,2,3,4,5]
profits = [1,5,3,4,6]
```

## Output

```
15
```

## Explanation

All triplets `(i, j, k)` with `i < j < k` satisfy the increasing price condition.

The most profitable three items are:

```
index 1 -> profit 5
index 3 -> profit 4
index 4 -> profit 6
```

Total profit:

```
5 + 4 + 6 = 15
```

---

# Example 3

## Input

```
prices = [4,3,2,1]
profits = [33,20,19,87]
```

## Output

```
-1
```

## Explanation

The prices are strictly decreasing.

So there are **no indices** `i < j < k` such that:

```
prices[i] < prices[j] < prices[k]
```

Therefore, it is **impossible** to pick a valid triplet.

---

# Constraints

```
3 <= prices.length == profits.length <= 50000

1 <= prices[i] <= 5000
1 <= profits[i] <= 10^6
```
