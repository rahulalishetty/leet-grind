# 3454. Separate Squares II

## Problem Description

You are given a **2D integer array `squares`**.

Each entry:

```
squares[i] = [xi, yi, li]
```

represents a square where:

- `(xi, yi)` is the **bottom-left coordinate**
- `li` is the **side length**
- The square is **axis-aligned** (parallel to the x-axis and y-axis).

---

## Goal

Find the **minimum y-coordinate** of a **horizontal line** such that:

```
Area above the line = Area below the line
```

Important rule:

> Squares may overlap.
> Overlapping areas must be counted **only once**.

---

## Output Requirement

Return the **minimum y-coordinate** satisfying the condition.

Accepted answers must be within:

```
10^-5
```

of the true answer.

---

# Example 1

## Input

```
squares = [[0,0,1],[2,2,1]]
```

## Output

```
1.00000
```

## Explanation

A horizontal line between:

```
y = 1 and y = 2
```

splits the total area evenly.

- Area above = 1
- Area below = 1

The **minimum such y-value** is:

```
y = 1
```

---

# Example 2

## Input

```
squares = [[0,0,2],[1,1,1]]
```

## Output

```
1.00000
```

## Explanation

The smaller square overlaps with the larger one.

Overlapping area is counted **only once**, so the union of squares forms a combined region.

The horizontal line:

```
y = 1
```

splits the union area equally.

---

# Constraints

```
1 <= squares.length <= 5 * 10^4
squares[i] = [xi, yi, li]

0 <= xi, yi <= 10^9
1 <= li <= 10^9

Total area of all squares <= 10^15
```

---

# Key Notes

- Squares may **overlap**
- Only **union area** counts
- Need to split **union area** into two equal halves
- Result must be **floating point precision within 1e-5**
