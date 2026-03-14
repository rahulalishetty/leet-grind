# 473. Matchsticks to Square

## Problem Description

You are given an integer array `matchsticks` where:

```
matchsticks[i]
```

represents the **length of the i-th matchstick**.

Your goal is to determine whether it is possible to **use all the matchsticks exactly once** to form **one square**.

### Rules

- You **cannot break** any matchstick.
- You **must use every matchstick exactly once**.
- You can **connect matchsticks end-to-end**.
- The resulting shape must be a **square**.

Return:

- `true` → if it is possible to form a square
- `false` → otherwise

---

# Function Signature

```
boolean makesquare(int[] matchsticks)
```

---

# Example 1

![alt text](image.png)

### Input

```
matchsticks = [1,1,2,2,2]
```

### Output

```
true
```

### Explanation

The total length is:

```
1 + 1 + 2 + 2 + 2 = 8
```

Each side of the square must therefore be:

```
8 / 4 = 2
```

One possible construction:

```
Side 1: 2
Side 2: 2
Side 3: 1 + 1
Side 4: 2
```

So a square can be formed.

---

# Example 2

### Input

```
matchsticks = [3,3,3,3,4]
```

### Output

```
false
```

### Explanation

Total length:

```
3 + 3 + 3 + 3 + 4 = 16
```

Each side must be:

```
16 / 4 = 4
```

However:

- the matchstick of length `4` forms one side
- the remaining sticks are `3,3,3,3`

There is **no way to combine these to make sides of length 4** without breaking sticks.

Thus forming a square is impossible.

---

# Constraints

```
1 <= matchsticks.length <= 15
1 <= matchsticks[i] <= 10^8
```

---
