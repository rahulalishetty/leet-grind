# 265. Paint House II

## Problem

There are `n` houses in a row, and each house can be painted with one of `k` colors.

- The cost of painting each house with a color is given in a matrix `costs`
- `costs[i][j]` = cost of painting house `i` with color `j`

## Constraints

- No two adjacent houses can have the same color
- You must paint all houses

## Goal

Return the **minimum cost** to paint all houses.

---

## Example 1

```
Input: costs = [[1,5,3],[2,9,4]]
Output: 5

Explanation:
- Paint house 0 with color 0 → cost = 1
- Paint house 1 with color 2 → cost = 4

Total = 1 + 4 = 5
```

---

## Example 2

```
Input: costs = [[1,3],[2,4]]
Output: 5
```

---

## Constraints

```
costs.length == n
costs[i].length == k
1 <= n <= 100
2 <= k <= 20
1 <= costs[i][j] <= 20
```

---

## Follow Up

Can you solve it in **O(n \* k)** time?
