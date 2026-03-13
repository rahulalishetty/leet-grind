# Maximum Area Rectangle With Point Constraints II — Fenwick Sweep Line Approach

## Intuition

The problem is about finding the largest rectangle area that can be formed using the given `xCoord` and `yCoord` as the coordinates of the vertices of a rectangle.

The solution uses a **sweep line approach combined with Fenwick Trees** to efficiently calculate the area of potential rectangles.

Key idea:

- Process points from **left to right (sorted by x)**.
- Track **vertical segments** formed by consecutive points in the same column.
- When the **same vertical segment appears again at a different x**, it can form a rectangle.
- Use a **Fenwick Tree** to track how many points lie in a vertical range so we can verify that no extra points exist inside the rectangle.

By combining horizontal relationships (x positions) and vertical relationships (y pairs), we can efficiently compute the maximum rectangle area.

---

# Approach

## Step 1 — Coordinate Compression

The `yCoord` values may be very large (`≤ 8 * 10^7`).

We compress them so they become small indices usable in Fenwick Trees.

Steps:

1. Copy and sort all `yCoord` values.
2. Remove duplicates.
3. Replace each original y with its **index in the compressed array**.

This allows Fenwick operations in `O(log n)`.

---

## Step 2 — Coordinate Pair Construction

Create an array:

```
co[i] = [xCoord[i], compressedY]
```

Each point stores:

- original x
- compressed y index

---

## Step 3 — Sorting Points

Sort the coordinate array:

1. by `x`
2. if equal x → by `y`

This simulates sweeping a vertical line across the plane.

```
Arrays.sort(co, (a,b) -> {
    if(a[0] != b[0]) return a[0] - b[0];
    return a[1] - b[1];
});
```

---

## Step 4 — Fenwick Tree Setup

Fenwick Tree (Binary Indexed Tree) tracks **how many points appear at each y index**.

This allows efficient queries:

```
How many points exist in y-range [y1, y2]?
```

Operations:

```
update(y)
prefixSum(y)
```

Both run in:

```
O(log n)
```

---

## Step 5 — Detect Vertical Segments

If two consecutive sorted points have the **same x**, they form a **vertical segment**.

Example:

```
(x, y1)
(x, y2)
```

This is a potential rectangle side.

Encode the vertical segment using:

```
key = (y2 << 32) | y1
```

This uniquely identifies the pair.

---

## Step 6 — Tracking Rectangles

Two maps are used:

```
map  -> stores Fenwick count when segment first appeared
mapx -> stores x position where segment first appeared
```

When we see the same `(y1, y2)` pair again:

1. Check if there are **exactly two points** between them in the Fenwick tree.
2. If true → rectangle interior is empty.
3. Compute area:

```
width  = currentX - previousX
height = y2 - y1
area   = width * height
```

Update the maximum area.

---

## Step 7 — Return Result

After processing all points:

```
return maxArea
```

If no rectangle is found:

```
return -1
```

---

# Complexity

### Time Complexity

```
Sorting points     : O(n log n)
Fenwick operations : O(log n) each
Total              : O(n log n)
```

### Space Complexity

```
O(n)
```

Used by:

- Fenwick tree
- coordinate compression arrays
- hash maps

---

# Java Implementation

```java
class Solution
{
    public long maxRectangleArea(int[] xCoord, int[] yCoord)
    {
        int n = xCoord.length;
        int[][] co = new int[n][];
        int[] sy = imap(yCoord);

        // Step 1: Map coordinates to compressed y-values
        for (int i = 0; i < n; i++)
        {
            co[i] = new int[]{xCoord[i], Arrays.binarySearch(sy, yCoord[i])};
        }

        // Step 2: Sort the coordinates first by x, then by y
        Arrays.sort(co, (x, y) -> {
            if (x[0] != y[0]) return x[0] - y[0];
            return x[1] - y[1];
        });

        // Step 3: Initialize Fenwick Tree and helper maps
        Map<Long, Integer> map = new HashMap<>();
        Map<Long, Integer> mapx = new HashMap<>();
        long ans = -1;
        int[] ft = new int[sy.length + 1];

        // Step 4: Iterate through the coordinates and find rectangles
        for (int i = 0; i < co.length; i++)
        {
            addFenwick(ft, co[i][1], 1);

            if (i - 1 >= 0 && co[i][0] == co[i - 1][0])
            {
                long yc = (long) co[i][1] << 32 | co[i - 1][1];
                int aft = sumFenwick(ft, co[i][1]) - sumFenwick(ft, co[i - 1][1] - 1);

                if (map.containsKey(yc))
                {
                    int bef = map.get(yc);
                    if (aft == bef + 2)
                    {
                        int x = mapx.get(yc);
                        long S = (long) (co[i][0] - x) * (sy[co[i][1]] - sy[co[i - 1][1]]);
                        ans = Math.max(ans, S);
                    }
                }

                map.put(yc, aft);
                mapx.put(yc, co[i][0]);
            }
        }

        return ans;
    }

    public static int sumFenwick(int[] ft, int i)
    {
        int sum = 0;
        for (i++; i > 0; i -= i & -i)
        {
            sum += ft[i];
        }
        return sum;
    }

    public static void addFenwick(int[] ft, int i, int v)
    {
        if (v == 0 || i < 0)
        {
            return;
        }

        int n = ft.length;
        for (i++; i < n; i += i & -i)
        {
            ft[i] += v;
        }
    }

    public static int[] imap(int[] a)
    {
        int[] imap = Arrays.copyOf(a, a.length);
        Arrays.sort(imap);
        int p = 1;
        for (int i = 1; i < imap.length; i++)
        {
            if (imap[i] != imap[p - 1])
            {
                imap[p++] = imap[i];
            }
        }

        return Arrays.copyOf(imap, p);
    }
}
```

---

# Key Takeaways

Important ideas used in this solution:

- **Sweep line algorithm**
- **Coordinate compression**
- **Fenwick Tree (Binary Indexed Tree)**
- **Hash map for vertical segment matching**

These allow the rectangle search problem to be solved efficiently even when:

```
n ≤ 200,000
```
