# 3454. Separate Squares II

## Approach: Scan Line + Segment Tree

## Intuition

Since overlapping areas of squares must be counted **only once**, we treat the problem as computing the **union area of rectangles** and then finding a horizontal line that splits that union area into two equal halves.

This approach is similar to the classic problem **Rectangle Area II (LeetCode 850)** which uses a **scan line algorithm combined with a segment tree**.

The key idea:

- Sweep a horizontal line from bottom to top.
- Track how much horizontal width is currently covered by active squares.
- Multiply the width by the vertical distance moved to accumulate area.

---

# Scan Line Idea

Consider sweeping a horizontal line upward.

For each square:

- bottom edge → add coverage `(+1)`
- top edge → remove coverage `(-1)`

Each event is represented as:

```
(y, type, x_left, x_right)
```

Where:

- `type = +1` → entering square
- `type = -1` → leaving square

While sweeping:

```
area += width * (current_y - previous_y)
```

Where:

```
width = total horizontal coverage
```

---

# Segment Tree Purpose

We need to dynamically maintain the **total horizontal coverage length**.

To do this efficiently we:

1. **Discretize all x-coordinates**
2. Build a **segment tree** over the x-intervals
3. Each node stores:

```
count   → how many rectangles currently cover the interval
covered → total covered length of that interval
```

If:

```
count > 0
```

the whole segment is covered.

Otherwise we combine children results.

---

# Computing Total Area

During the scan:

```
area += width * deltaY
```

This gives us the **total union area of all squares**.

---

# Finding the Balanced Horizontal Line

Let:

```
totalArea = union area of squares
```

We need the smallest `y` where:

```
areaBelow = areaAbove
```

Which means:

```
areaBelow = totalArea / 2
```

---

# Determining the Exact Y

Suppose the scan moves between:

```
y' → y''
```

The width stays constant in that region.

Area grows linearly:

```
area + width * (y'' - y')
```

If:

```
area < totalArea/2
area + width * (y'' - y') >= totalArea/2
```

Then the answer lies in this interval.

Distance needed:

```
Δ = (targetArea - area) / width
```

Final answer:

```
y = y' + Δ
```

---

# Implementation

```java
class SegmentTree {

    private int[] count;
    private int[] covered;
    private int[] xs;
    private int n;

    public SegmentTree(int[] xs_) {
        xs = xs_;
        n = xs.length - 1;
        count = new int[4 * n];
        covered = new int[4 * n];
    }

    private void modify(
        int qleft,
        int qright,
        int qval,
        int left,
        int right,
        int pos
    ) {
        if (xs[right + 1] <= qleft || xs[left] >= qright) {
            return;
        }
        if (qleft <= xs[left] && xs[right + 1] <= qright) {
            count[pos] += qval;
        } else {
            int mid = (left + right) / 2;
            modify(qleft, qright, qval, left, mid, pos * 2 + 1);
            modify(qleft, qright, qval, mid + 1, right, pos * 2 + 2);
        }

        if (count[pos] > 0) {
            covered[pos] = xs[right + 1] - xs[left];
        } else {
            if (left == right) {
                covered[pos] = 0;
            } else {
                covered[pos] = covered[pos * 2 + 1] + covered[pos * 2 + 2];
            }
        }
    }

    public void update(int qleft, int qright, int qval) {
        modify(qleft, qright, qval, 0, n - 1, 0);
    }

    public int query() {
        return covered[0];
    }
}

class Solution {

    public double separateSquares(int[][] squares) {
        List<int[]> events = new ArrayList<>();
        Set<Integer> xsSet = new TreeSet<>();

        for (int[] sq : squares) {
            int x = sq[0];
            int y = sq[1];
            int l = sq[2];
            int xr = x + l;

            events.add(new int[] { y, 1, x, xr });
            events.add(new int[] { y + l, -1, x, xr });

            xsSet.add(x);
            xsSet.add(xr);
        }

        events.sort((a, b) -> Integer.compare(a[0], b[0]));

        int[] xs = xsSet.stream().mapToInt(i -> i).toArray();

        SegmentTree segTree = new SegmentTree(xs);

        List<Long> psum = new ArrayList<>();
        List<Integer> widths = new ArrayList<>();

        long totalArea = 0;
        int prev = events.get(0)[0];

        for (int[] event : events) {
            int y = event[0];
            int delta = event[1];
            int xl = event[2];
            int xr = event[3];

            int len = segTree.query();
            totalArea += (long) len * (y - prev);

            segTree.update(xl, xr, delta);

            psum.add(totalArea);
            widths.add(segTree.query());

            prev = y;
        }

        long target = (totalArea + 1) / 2;

        int i = binarySearch(psum, target);

        double area = psum.get(i);
        int width = widths.get(i);
        int height = events.get(i)[0];

        return height + (totalArea - area * 2) / (width * 2.0);
    }

    private int binarySearch(List<Long> list, long target) {
        int left = 0;
        int right = list.size() - 1;
        int result = 0;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (list.get(mid) < target) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }
}
```

---

# Complexity Analysis

Let:

```
n = number of squares
```

### Time Complexity

```
O(n log n)
```

Explanation:

- Sorting events → `O(n log n)`
- Each scan step performs segment tree updates/queries → `O(log n)`
- Total updates ≈ `2n`

---

### Space Complexity

```
O(n)
```

Used for:

- Segment tree
- Discretized coordinates
- Event storage
