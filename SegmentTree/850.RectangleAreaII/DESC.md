# 850. Rectangle Area II

You are given a 2D array of axis-aligned rectangles. Each `rectangle[i] = [xi1, yi1, xi2, yi2]` denotes the i-th rectangle where `(xi1, yi1)` are the coordinates of the bottom-left corner and `(xi2, yi2)` are the coordinates of the top-right corner.

Calculate the total area covered by all rectangles in the plane. Any area covered by two or more rectangles should be counted only once.

Return the total area modulo 10^9 + 7.

**Example 1**

```note
Input: `rectangles = [[0,0,2,2],[1,0,2,3],[1,0,3,1]]`
Output: `6`
Explanation: Total covered area is 6. The rectangles overlap in the regions described in the problem.
```

**Example 2**

```note
Input: `rectangles = [[0,0,1000000000,1000000000]]`
Output: `49`
Explanation: The area is 10^18; 10^18 mod (10^9 + 7) = 49.
```

Constraints

- `1 <= rectangles.length <= 200`
- `rectangles[i].length == 4`
- `0 <= xi1, yi1, xi2, yi2 <= 10^9`
- `xi1 < xi2` and `yi1 < yi2` (all rectangles have non-zero area)
- Coordinates are integers
- Return the answer modulo `10^9 + 7`
