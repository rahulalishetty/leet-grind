# 3715. Sum of Perfect Square Ancestors

You are given an integer **n** and an **undirected tree rooted at node 0** with `n` nodes numbered from `0` to `n - 1`.

The tree is represented by a 2D array:

```
edges[i] = [ui, vi]
```

which indicates an undirected edge between nodes `ui` and `vi`.

You are also given an integer array:

```
nums
```

where:

```
nums[i]
```

is the positive integer assigned to node `i`.

---

# Problem Definition

For each node `i`, define:

```
ti = number of ancestors of node i such that
nums[i] * nums[ancestor] is a perfect square
```

Return the **sum of all `ti` values** for nodes:

```
i in [1, n - 1]
```

---

# Ancestor Definition

In a rooted tree:

- The **ancestors of node `i`** are all nodes on the path from node `i` to the root node `0`.
- The node `i` itself is **not included**.

Example:

```
0
│
1
│
2
```

For node `2`:

```
ancestors = [1,0]
```

---

# Perfect Square Condition

A number is a **perfect square** if:

```
x = k²
```

for some integer `k`.

We check:

```
nums[i] * nums[ancestor]
```

If this product is a perfect square, the ancestor contributes to `ti`.

---

# Example 1

## Input

```
n = 3
edges = [[0,1],[1,2]]
nums = [2,8,2]
```

## Output

```
3
```

## Explanation

| i   | Ancestors | Product                 | Perfect Square | ti  |
| --- | --------- | ----------------------- | -------------- | --- |
| 1   | [0]       | 8 × 2 = 16              | Yes            | 1   |
| 2   | [1,0]     | 2 × 8 = 16<br>2 × 2 = 4 | Both Yes       | 2   |

Total:

```
1 + 2 = 3
```

---

# Example 2

## Input

```
n = 3
edges = [[0,1],[0,2]]
nums = [1,2,4]
```

## Output

```
1
```

## Explanation

| i   | Ancestors | Product   | Perfect Square | ti  |
| --- | --------- | --------- | -------------- | --- |
| 1   | [0]       | 2 × 1 = 2 | No             | 0   |
| 2   | [0]       | 4 × 1 = 4 | Yes            | 1   |

Total:

```
1
```

---

# Example 3

## Input

```
n = 4
edges = [[0,1],[0,2],[1,3]]
nums = [1,2,9,4]
```

## Output

```
2
```

## Explanation

| i   | Ancestors | Product                | Perfect Square | ti  |
| --- | --------- | ---------------------- | -------------- | --- |
| 1   | [0]       | 2 × 1 = 2              | No             | 0   |
| 2   | [0]       | 9 × 1 = 9              | Yes            | 1   |
| 3   | [1,0]     | 4 × 2 = 8<br>4 × 1 = 4 | Only 4 Yes     | 1   |

Total:

```
0 + 1 + 1 = 2
```

---

# Constraints

```
1 <= n <= 10^5
edges.length == n - 1
0 <= ui, vi <= n - 1
nums.length == n
1 <= nums[i] <= 10^5
```

Additional guarantees:

- `edges` always form a **valid tree**.
