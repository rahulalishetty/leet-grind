# 3382. Maximum Area Rectangle With Point Constraints II

## Problem Statement

There are **n points on an infinite plane**.
You are given two integer arrays:

```
xCoord
yCoord
```

where:

```
(xCoord[i], yCoord[i])
```

represents the coordinates of the **i-th point**.

Your task is to find the **maximum area of a rectangle** that satisfies the following conditions:

- The rectangle is formed using **four of the given points as its corners**.
- The rectangle's **edges are parallel to the coordinate axes**.
- **No other point lies inside the rectangle or on its border**.

Return the **maximum possible area** of such a rectangle.

If **no valid rectangle exists**, return:

```
-1
```

---

# Example 1

![alt text](image.png)

## Input

```
xCoord = [1,1,3,3]
yCoord = [1,3,1,3]
```

## Output

```
4
```

## Explanation

The four points form a rectangle:

```
(1,1) ---- (3,1)
  |          |
  |          |
(1,3) ---- (3,3)
```

Width:

```
3 - 1 = 2
```

Height:

```
3 - 1 = 2
```

Area:

```
2 * 2 = 4
```

There are no other points inside or on the border.

---

# Example 2

![alt text](image-1.png)

## Input

```
xCoord = [1,1,3,3,2]
yCoord = [1,3,1,3,2]
```

## Output

```
-1
```

## Explanation

The rectangle formed by:

```
[1,1], [1,3], [3,1], [3,3]
```

is invalid because the point:

```
[2,2]
```

lies **inside the rectangle**.

Therefore **no valid rectangle exists**.

---

# Example 3

![alt text](image-2.png)

## Input

```
xCoord = [1,1,3,3,1,3]
yCoord = [1,3,1,3,2,2]
```

## Output

```
2
```

## Explanation

The largest valid rectangles are:

### Rectangle 1

Corners:

```
[1,3], [1,2], [3,2], [3,3]
```

Width:

```
3 - 1 = 2
```

Height:

```
3 - 2 = 1
```

Area:

```
2
```

### Rectangle 2

Corners:

```
[1,1], [1,2], [3,1], [3,2]
```

Area:

```
2
```

Maximum area:

```
2
```

---

# Constraints

```
1 <= xCoord.length == yCoord.length <= 2 * 10^5
0 <= xCoord[i], yCoord[i] <= 8 * 10^7
All points are unique
```
