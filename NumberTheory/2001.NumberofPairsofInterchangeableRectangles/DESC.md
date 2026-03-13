# 2001. Number of Pairs of Interchangeable Rectangles

You are given **n rectangles** represented by a **0-indexed 2D integer array**:

```
rectangles[i] = [width_i, height_i]
```

where:

- `width_i` = width of the ith rectangle
- `height_i` = height of the ith rectangle

---

# Problem Definition

Two rectangles `i` and `j` (`i < j`) are considered **interchangeable** if their **width-to-height ratios are equal**.

More formally:

```
width_i / height_i == width_j / height_j
```

Note:

- The division is **decimal division**, not integer division.

Your task is to **count the number of interchangeable pairs** of rectangles.

---

# Example 1

## Input

```
rectangles = [[4,8],[3,6],[10,20],[15,30]]
```

## Output

```
6
```

## Explanation

Each rectangle has the same ratio:

```
4/8 = 3/6 = 10/20 = 15/30 = 0.5
```

All rectangles are interchangeable with each other.

Pairs (0‑indexed):

```
(0,1)
(0,2)
(0,3)
(1,2)
(1,3)
(2,3)
```

Total:

```
6 pairs
```

---

# Example 2

## Input

```
rectangles = [[4,5],[7,8]]
```

## Output

```
0
```

## Explanation

Ratios:

```
4/5 = 0.8
7/8 = 0.875
```

They are different, so **no interchangeable pairs exist**.

---

# Constraints

```
n == rectangles.length
1 <= n <= 10^5
rectangles[i].length == 2
1 <= width_i, height_i <= 10^5
```

---

# Key Idea

Two rectangles are interchangeable if:

```
width_i / height_i == width_j / height_j
```

This ratio can be normalized using the **greatest common divisor (GCD)**:

```
width/gcd(width,height) : height/gcd(width,height)
```

Rectangles with the same **reduced ratio** belong to the same group.

If a group contains `k` rectangles, the number of interchangeable pairs is:

```
k * (k - 1) / 2
```

---
