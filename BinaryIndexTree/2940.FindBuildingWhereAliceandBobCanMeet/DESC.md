# 2940. Find Building Where Alice and Bob Can Meet

## Problem Statement

You are given a **0-indexed array** `heights` of positive integers, where:

```
heights[i] = height of the i-th building
```

A person located at building `i` can move to building `j` **if and only if**:

```
i < j AND heights[i] < heights[j]
```

You are also given a 2D array:

```
queries[i] = [ai, bi]
```

For the `i`‑th query:

- Alice starts at building `ai`
- Bob starts at building `bi`

Both of them may move according to the movement rule above.

Your task is to determine the **leftmost building index** where Alice and Bob can meet.

Return an array:

```
ans[i]
```

Where:

- `ans[i]` = index of the leftmost building where both can meet
- `ans[i] = -1` if no such building exists

---

# Example 1

## Input

```
heights = [6,4,8,5,2,7]
queries = [[0,1],[0,3],[2,4],[3,4],[2,2]]
```

## Output

```
[2,5,-1,5,2]
```

## Explanation

### Query 1 → [0,1]

Alice at building `0`, Bob at building `1`.

Both can move to building `2` because:

```
heights[0] < heights[2]
heights[1] < heights[2]
```

So answer is:

```
2
```

---

### Query 2 → [0,3]

Both can reach building `5`.

```
heights[0] < heights[5]
heights[3] < heights[5]
```

Answer:

```
5
```

---

### Query 3 → [2,4]

Alice cannot move anywhere that Bob can also reach.

Answer:

```
-1
```

---

### Query 4 → [3,4]

Both can move to building:

```
5
```

Answer:

```
5
```

---

### Query 5 → [2,2]

Alice and Bob already start at the same building.

Answer:

```
2
```

---

# Example 2

## Input

```
heights = [5,3,8,2,6,1,4,6]
queries = [[0,7],[3,5],[5,2],[3,0],[1,6]]
```

## Output

```
[7,6,-1,4,6]
```

## Explanation

### Query 1 → [0,7]

Alice can directly move to Bob’s building since:

```
heights[0] < heights[7]
```

Answer:

```
7
```

---

### Query 2 → [3,5]

Both can move to building:

```
6
```

because:

```
heights[3] < heights[6]
heights[5] < heights[6]
```

---

### Query 3 → [5,2]

Bob cannot move to any building reachable by Alice.

Answer:

```
-1
```

---

### Query 4 → [3,0]

Both can meet at building:

```
4
```

---

### Query 5 → [1,6]

Alice can move directly to Bob's building.

Answer:

```
6
```

---

# Constraints

```
1 <= heights.length <= 5 * 10^4
1 <= heights[i] <= 10^9

1 <= queries.length <= 5 * 10^4
queries[i] = [ai, bi]

0 <= ai, bi <= heights.length - 1
```
