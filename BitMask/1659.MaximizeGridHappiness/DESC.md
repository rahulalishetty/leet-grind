# 1659. Maximize Grid Happiness

## Problem Description

You are given four integers:

```
m, n, introvertsCount, extrovertsCount
```

- The grid size is **m × n**
- There are two types of people:
  - **Introverts**
  - **Extroverts**

You may place people in grid cells, but **you are not required to place everyone**.

Each person occupies **one cell**.

---

# Happiness Rules

Each person's happiness depends on their neighbors (up, down, left, right).

### Introverts

- Base happiness: **120**
- Lose **30 happiness** for each neighbor

Formula:

```
Introvert happiness = 120 − 30 × neighbors
```

---

### Extroverts

- Base happiness: **40**
- Gain **20 happiness** for each neighbor

Formula:

```
Extrovert happiness = 40 + 20 × neighbors
```

---

### Neighbors

Neighbors are the people living in adjacent cells:

```
North
South
East
West
```

The **total grid happiness** is the sum of happiness of all placed people.

Your goal is to **maximize total grid happiness**.

---

# Example 1

### Input

```
m = 2
n = 3
introvertsCount = 1
extrovertsCount = 2
```

### Output

```
240
```

### Explanation

Place:

```
Introvert -> (1,1)
Extrovert -> (1,3)
Extrovert -> (2,3)
```

Happiness:

```
Introvert: 120 − 30×0 = 120
Extrovert: 40 + 20×1 = 60
Extrovert: 40 + 20×1 = 60
```

Total:

```
120 + 60 + 60 = 240
```

---

# Example 2

### Input

```
m = 3
n = 1
introvertsCount = 2
extrovertsCount = 1
```

### Output

```
260
```

### Explanation

Placement:

```
Introvert -> (1,1)
Extrovert -> (2,1)
Introvert -> (3,1)
```

Happiness:

```
Introvert: 120 − 30×1 = 90
Extrovert: 40 + 20×2 = 80
Introvert: 120 − 30×1 = 90
```

Total:

```
90 + 80 + 90 = 260
```

---

# Example 3

### Input

```
m = 2
n = 2
introvertsCount = 4
extrovertsCount = 0
```

### Output

```
240
```

---

# Constraints

```
1 <= m, n <= 5
```

```
0 <= introvertsCount, extrovertsCount <= min(m * n, 6)
```

---

# Key Observations

- Grid size is **very small** (`<= 5 × 5`)
- Maximum number of people is **≤ 6**
- Placement decisions create **local neighbor interactions**
- This problem is typically solved using:

```
State Compression Dynamic Programming
+ Bitmask / Base-3 representation
```

Common techniques:

- Grid DP
- Profile DP
- Memoization
