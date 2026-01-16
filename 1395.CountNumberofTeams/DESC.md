# 1395. Count Number of Teams

Given n soldiers standing in a line, each with a unique rating value, count the number of valid teams of 3 soldiers.

Rules:

- Choose indices (i, j, k) with 0 ≤ i < j < k < n and ratings (rating[i], rating[j], rating[k]).
- A team is valid if either rating[i] < rating[j] < rating[k] (increasing) or rating[i] > rating[j] > rating[k] (decreasing).
- Soldiers can belong to multiple teams.

Return the number of valid teams.

Examples:

Example 1
Input:

```
rating = [2, 5, 3, 4, 1]
```

Output:

```
3
```

Explanation: (2,3,4), (5,4,1), (5,3,1)

Example 2
Input:

```
rating = [2, 1, 3]
```

Output:

```
0
```

Example 3
Input:

```
rating = [1, 2, 3, 4]
```

Output:

```
4
```

Constraints:

- n == rating.length
- 3 ≤ n ≤ 1000
- 1 ≤ rating[i] ≤ 10^5
- All rating values are unique.
