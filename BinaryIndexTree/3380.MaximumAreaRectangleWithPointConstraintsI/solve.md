# Maximum Area Rectangle With No Other Point Inside or On Border — Exhaustive Java Notes

## Problem Statement

You are given an array:

```text
points[i] = [xi, yi]
```

representing points on a 2D plane.

You must find the maximum area of an axis-aligned rectangle such that:

- its four corners are among the given points
- its sides are parallel to the axes
- no other given point lies inside the rectangle
- no other given point lies on the rectangle border

Return the maximum area, or:

```text
-1
```

if no valid rectangle exists.

---

## Example 1

```text
Input: points = [[1,1],[1,3],[3,1],[3,3]]
Output: 4
```

The four points form a `2 x 2` rectangle, and there are no extra points inside or on the border.

So the answer is:

```text
4
```

---

## Example 2

```text
Input: points = [[1,1],[1,3],[3,1],[3,3],[2,2]]
Output: -1
```

The outer rectangle exists, but point `[2,2]` lies inside it.

So the rectangle is invalid.

---

## Example 3

```text
Input: points = [[1,1],[1,3],[3,1],[3,3],[1,2],[3,2]]
Output: 2
```

The full height rectangle is invalid because `[1,2]` and `[3,2]` lie on the border.

But smaller rectangles:

- `[1,1],[1,2],[3,1],[3,2]`
- `[1,2],[1,3],[3,2],[3,3]`

are valid, each with area `2`.

So the maximum is:

```text
2
```

---

## Constraints

```text
1 <= points.length <= 10
0 <= xi, yi <= 100
All points are unique
```

The extremely small constraint `n <= 10` is important. It changes how aggressively we need to optimize.

---

# 1. Core Observation

For an axis-aligned rectangle, if we pick two points as opposite corners:

```text
(x1, y1), (x2, y2)
```

with:

```text
x1 != x2 and y1 != y2
```

then the other two required corners must be:

```text
(x1, y2), (x2, y1)
```

So every candidate rectangle is completely determined by two diagonal corners.

After that, we only need to check:

1. do the other two corners exist?
2. is there any other point inside the rectangle?
3. is there any other point on the border?

That is the whole problem.

---

# 2. Validity Condition Carefully

Suppose the rectangle is defined by:

```text
left   = min(x1, x2)
right  = max(x1, x2)
bottom = min(y1, y2)
top    = max(y1, y2)
```

A point `(x, y)` is:

## Inside the rectangle if

```text
left < x < right and bottom < y < top
```

## On the border if

it lies on one of the four edges:

```text
x == left or x == right or y == bottom or y == top
```

while also being inside the bounding box:

```text
left <= x <= right and bottom <= y <= top
```

The four corners themselves are allowed, because they define the rectangle.

Any other point inside or on the border makes the rectangle invalid.

---

# 3. Approach 1 — Brute Force Over Every 4-Point Combination

## Idea

Since `n <= 10`, we can literally try every set of 4 points.

For each set of 4 points:

- check whether they form an axis-aligned rectangle
- check whether there are no extra points inside/border

This is the most direct approach.

## How do 4 points form an axis-aligned rectangle?

They must use exactly:

- 2 distinct x-values
- 2 distinct y-values

and the 4 points must be exactly all combinations:

```text
(xa, ya), (xa, yb), (xb, ya), (xb, yb)
```

## Java Code

```java
import java.util.*;

class SolutionBruteForce4 {
    public int maxRectangleArea(int[][] points) {
        int n = points.length;
        int ans = -1;

        for (int a = 0; a < n; a++) {
            for (int b = a + 1; b < n; b++) {
                for (int c = b + 1; c < n; c++) {
                    for (int d = c + 1; d < n; d++) {
                        int[][] rect = {points[a], points[b], points[c], points[d]};

                        Set<Integer> xs = new HashSet<>();
                        Set<Integer> ys = new HashSet<>();
                        Set<String> set = new HashSet<>();

                        for (int[] p : rect) {
                            xs.add(p[0]);
                            ys.add(p[1]);
                            set.add(p[0] + "#" + p[1]);
                        }

                        if (xs.size() != 2 || ys.size() != 2) continue;

                        List<Integer> xList = new ArrayList<>(xs);
                        List<Integer> yList = new ArrayList<>(ys);

                        int x1 = xList.get(0), x2 = xList.get(1);
                        int y1 = yList.get(0), y2 = yList.get(1);

                        if (!set.contains(x1 + "#" + y1) ||
                            !set.contains(x1 + "#" + y2) ||
                            !set.contains(x2 + "#" + y1) ||
                            !set.contains(x2 + "#" + y2)) {
                            continue;
                        }

                        int left = Math.min(x1, x2);
                        int right = Math.max(x1, x2);
                        int bottom = Math.min(y1, y2);
                        int top = Math.max(y1, y2);

                        boolean ok = true;

                        for (int[] p : points) {
                            int x = p[0], y = p[1];

                            boolean isCorner =
                                (x == left && y == bottom) ||
                                (x == left && y == top) ||
                                (x == right && y == bottom) ||
                                (x == right && y == top);

                            if (isCorner) continue;

                            boolean inside = left < x && x < right && bottom < y && y < top;
                            boolean onBorder = left <= x && x <= right && bottom <= y && y <= top &&
                                               (x == left || x == right || y == bottom || y == top);

                            if (inside || onBorder) {
                                ok = false;
                                break;
                            }
                        }

                        if (ok) {
                            ans = Math.max(ans, (right - left) * (top - bottom));
                        }
                    }
                }
            }
        }

        return ans;
    }
}
```

## Complexity

Choosing 4 points:

```text
O(n^4)
```

Checking all points for validity:

```text
O(n)
```

Total:

```text
O(n^5)
```

With `n <= 10`, this is still completely fine.

## Verdict

Very straightforward, but not the cleanest.

---

# 4. Approach 2 — Enumerate Diagonals

## Idea

Instead of choosing 4 points, choose 2 points as potential diagonal corners.

If the two points are:

```text
(x1, y1), (x2, y2)
```

and:

```text
x1 != x2 and y1 != y2
```

then the other two required corners must be:

```text
(x1, y2), (x2, y1)
```

This avoids the unnecessary `O(n^4)` combination search.

## Steps

For every pair of points:

1. skip if they are on same x or same y
2. check that the other two corners exist
3. check that no extra point is inside or on border
4. compute area

## Java Code

```java
import java.util.*;

class Solution {
    public int maxRectangleArea(int[][] points) {
        int n = points.length;
        int ans = -1;

        Set<String> pointSet = new HashSet<>();
        for (int[] p : points) {
            pointSet.add(p[0] + "#" + p[1]);
        }

        for (int i = 0; i < n; i++) {
            int x1 = points[i][0];
            int y1 = points[i][1];

            for (int j = i + 1; j < n; j++) {
                int x2 = points[j][0];
                int y2 = points[j][1];

                if (x1 == x2 || y1 == y2) continue;

                if (!pointSet.contains(x1 + "#" + y2) ||
                    !pointSet.contains(x2 + "#" + y1)) {
                    continue;
                }

                int left = Math.min(x1, x2);
                int right = Math.max(x1, x2);
                int bottom = Math.min(y1, y2);
                int top = Math.max(y1, y2);

                boolean ok = true;

                for (int[] p : points) {
                    int x = p[0], y = p[1];

                    boolean isCorner =
                        (x == left && y == bottom) ||
                        (x == left && y == top) ||
                        (x == right && y == bottom) ||
                        (x == right && y == top);

                    if (isCorner) continue;

                    boolean inside = left < x && x < right && bottom < y && y < top;
                    boolean onBorder = left <= x && x <= right && bottom <= y && y <= top &&
                                       (x == left || x == right || y == bottom || y == top);

                    if (inside || onBorder) {
                        ok = false;
                        break;
                    }
                }

                if (ok) {
                    ans = Math.max(ans, (right - left) * (top - bottom));
                }
            }
        }

        return ans;
    }
}
```

---

# 5. Why Approach 2 Works

Every axis-aligned rectangle has exactly two diagonals.

If a valid rectangle exists, choosing either diagonal will identify:

- the two opposite corners
- the required other two corners

So diagonal enumeration checks every possible rectangle.

We do get duplicate checking because the same rectangle can be found from both diagonals, but that does not affect correctness.

---

# 6. Complexity of Approach 2

There are:

```text
O(n^2)
```

pairs of points.

For each pair:

- corner existence check is `O(1)` with a hash set
- checking all points is `O(n)`

So total:

```text
O(n^3)
```

With `n <= 10`, this is tiny.

This is the recommended practical solution.

---

# 7. Approach 3 — Enumerate Two X-Coordinates and Two Y-Coordinates

## Idea

A rectangle is also fully defined by choosing:

- two distinct x-values
- two distinct y-values

Then the four corners must exist.

After that, check that no extra point lies inside or on the border.

This is another clean viewpoint.

## Java Code

```java
import java.util.*;

class SolutionByCoordinates {
    public int maxRectangleArea(int[][] points) {
        Set<Integer> xs = new HashSet<>();
        Set<Integer> ys = new HashSet<>();
        Set<String> pointSet = new HashSet<>();

        for (int[] p : points) {
            xs.add(p[0]);
            ys.add(p[1]);
            pointSet.add(p[0] + "#" + p[1]);
        }

        List<Integer> xList = new ArrayList<>(xs);
        List<Integer> yList = new ArrayList<>(ys);

        int ans = -1;

        for (int i = 0; i < xList.size(); i++) {
            for (int j = i + 1; j < xList.size(); j++) {
                int left = Math.min(xList.get(i), xList.get(j));
                int right = Math.max(xList.get(i), xList.get(j));

                for (int a = 0; a < yList.size(); a++) {
                    for (int b = a + 1; b < yList.size(); b++) {
                        int bottom = Math.min(yList.get(a), yList.get(b));
                        int top = Math.max(yList.get(a), yList.get(b));

                        if (!pointSet.contains(left + "#" + bottom) ||
                            !pointSet.contains(left + "#" + top) ||
                            !pointSet.contains(right + "#" + bottom) ||
                            !pointSet.contains(right + "#" + top)) {
                            continue;
                        }

                        boolean ok = true;

                        for (int[] p : points) {
                            int x = p[0], y = p[1];

                            boolean isCorner =
                                (x == left && y == bottom) ||
                                (x == left && y == top) ||
                                (x == right && y == bottom) ||
                                (x == right && y == top);

                            if (isCorner) continue;

                            boolean inside = left < x && x < right && bottom < y && y < top;
                            boolean onBorder = left <= x && x <= right && bottom <= y && y <= top &&
                                               (x == left || x == right || y == bottom || y == top);

                            if (inside || onBorder) {
                                ok = false;
                                break;
                            }
                        }

                        if (ok) {
                            ans = Math.max(ans, (right - left) * (top - bottom));
                        }
                    }
                }
            }
        }

        return ans;
    }
}
```

## Complexity

If there are `ux` unique x-values and `uy` unique y-values:

```text
O(ux^2 * uy^2 * n)
```

Since `ux, uy <= n <= 10`, still fine.

---

# 8. Best Approach for This Problem

Because `n <= 10`, the simplest robust solution is best.

The diagonal-enumeration approach is the cleanest balance of:

- simplicity
- correctness
- efficiency

So that is the solution I would recommend in an interview or submission.

---

# 9. Common Pitfalls

## Pitfall 1: Only checking interior points

The condition says:

> no other point inside **or on the border**

So points on edges also invalidate the rectangle.

Example 3 exists specifically to catch this mistake.

---

## Pitfall 2: Forgetting that the 4 corners themselves are allowed

When scanning points, do not reject the four rectangle corners.

Only reject extra points.

---

## Pitfall 3: Using same x or same y for a rectangle

If:

```text
x1 == x2
```

or

```text
y1 == y2
```

then area is zero, so this is not a valid rectangle.

---

## Pitfall 4: Double counting rectangles

Diagonal enumeration can discover the same rectangle twice.
That is fine because we only need the maximum area.

---

# 10. Dry Run on Example 3

```text
points = [[1,1],[1,3],[3,1],[3,3],[1,2],[3,2]]
```

Try diagonal `(1,1)` and `(3,3)`:

Required corners:

```text
(1,3), (3,1)
```

They exist.

Rectangle bounds:

```text
left = 1, right = 3, bottom = 1, top = 3
```

Now check all other points:

- `(1,2)` lies on left border
- `(3,2)` lies on right border

So rectangle is invalid.

Now try diagonal `(1,1)` and `(3,2)`:

Required corners:

```text
(1,2), (3,1)
```

They exist.

Bounds:

```text
left = 1, right = 3, bottom = 1, top = 2
```

Other points like `(1,3)`, `(3,3)` lie outside this rectangle.

No extra point is inside or on border.

Area:

```text
(3 - 1) * (2 - 1) = 2
```

Valid.

Maximum becomes `2`.

---

# 11. Correctness Proof Sketch

## Claim

The diagonal-enumeration algorithm returns the maximum valid rectangle area.

## Reasoning

Every axis-aligned rectangle is uniquely determined by either diagonal pair of opposite corners.

For each pair of points with different x and y:

- if the other two corners do not exist, no rectangle exists for that diagonal
- if they do exist, the algorithm checks every point to ensure no extra point lies inside or on the border

So every valid rectangle is considered, and every invalid rectangle is rejected.

Therefore the algorithm computes the maximum area among all valid rectangles.

---

# 12. Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    public int maxRectangleArea(int[][] points) {
        int n = points.length;
        int ans = -1;

        Set<String> pointSet = new HashSet<>();
        for (int[] p : points) {
            pointSet.add(p[0] + "#" + p[1]);
        }

        for (int i = 0; i < n; i++) {
            int x1 = points[i][0];
            int y1 = points[i][1];

            for (int j = i + 1; j < n; j++) {
                int x2 = points[j][0];
                int y2 = points[j][1];

                // Must be a true diagonal of a positive-area axis-aligned rectangle
                if (x1 == x2 || y1 == y2) {
                    continue;
                }

                // The other two corners must exist
                if (!pointSet.contains(x1 + "#" + y2) ||
                    !pointSet.contains(x2 + "#" + y1)) {
                    continue;
                }

                int left = Math.min(x1, x2);
                int right = Math.max(x1, x2);
                int bottom = Math.min(y1, y2);
                int top = Math.max(y1, y2);

                boolean valid = true;

                for (int[] p : points) {
                    int x = p[0];
                    int y = p[1];

                    boolean isCorner =
                        (x == left && y == bottom) ||
                        (x == left && y == top) ||
                        (x == right && y == bottom) ||
                        (x == right && y == top);

                    if (isCorner) {
                        continue;
                    }

                    boolean inside = (left < x && x < right && bottom < y && y < top);

                    boolean onBorder =
                        (left <= x && x <= right && bottom <= y && y <= top) &&
                        (x == left || x == right || y == bottom || y == top);

                    if (inside || onBorder) {
                        valid = false;
                        break;
                    }
                }

                if (valid) {
                    ans = Math.max(ans, (right - left) * (top - bottom));
                }
            }
        }

        return ans;
    }
}
```

---

# 13. Interview Summary

Use two points as potential diagonal corners.

If they form a positive-area axis-aligned rectangle, then:

- check whether the other two corners exist
- scan all points and reject the rectangle if any extra point lies inside or on the border

Because `n <= 10`, this simple `O(n^3)` solution is more than enough and is the best practical choice.
