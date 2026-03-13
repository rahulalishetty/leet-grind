# 3009. Maximum Number of Intersections on the Chart — Corrected Summary

## Core Insight

A horizontal line `y = h` intersects a segment between `(i, y[i])` and `(i+1, y[i+1])` **iff**:

```
min(y[i], y[i+1]) ≤ h ≤ max(y[i], y[i+1])
```

However, if `h` equals a **vertex height**, two adjacent segments meet at the same point.
Geometrically this is **one intersection**, not two.

So the key difficulty is:

> Avoid double‑counting shared vertices.

---

# Correct Modeling

We assign each segment a **half‑open interval** of heights.

This guarantees that exactly **one** of the two segments touching a vertex contributes at that vertex.

### If the segment goes upward (`a < b`)

```
[a, b)
```

### If the segment goes downward (`a > b`)

```
(b, a]
```

This ensures:

- a vertex intersection is counted once
- segment interiors are counted correctly

---

# Coordinate Doubling Trick

We map real heights to integer coordinates:

```
h → 2h
```

Then:

| Type of line        | Coordinate  |
| ------------------- | ----------- |
| exactly at height h | 2h          |
| between heights     | odd numbers |

Intervals become:

### Upward segment

```
[a, b) → [2a, 2b−1]
```

### Downward segment

```
(b, a] → [2b+1, 2a]
```

Now every valid horizontal line corresponds to an integer coordinate.

The problem reduces to:

> Find the maximum overlap among these intervals.

---

# Sweep Line Solution

Use a difference map:

For interval `[L, R]`:

```
+1 at L
−1 at R+1
```

Scan the prefix sums to obtain the maximum overlap.

---

# Java Implementation

```java
import java.util.*;

class Solution {
    public int maxIntersectionCount(int[] y) {
        TreeMap<Long, Integer> diff = new TreeMap<>();

        for (int i = 0; i + 1 < y.length; i++) {
            long a = y[i];
            long b = y[i + 1];
            long L, R;

            if (a < b) {
                L = 2L * a;
                R = 2L * b - 1;
            } else {
                L = 2L * b + 1;
                R = 2L * a;
            }

            diff.put(L, diff.getOrDefault(L, 0) + 1);
            diff.put(R + 1, diff.getOrDefault(R + 1, 0) - 1);
        }

        int ans = 0;
        int cur = 0;

        for (int delta : diff.values()) {
            cur += delta;
            ans = Math.max(ans, cur);
        }

        return ans;
    }
}
```

---

# Complexity

| Metric | Value        |
| ------ | ------------ |
| Time   | `O(n log n)` |
| Space  | `O(n)`       |

Where `n` is the number of points.

---

# Key Interview Takeaway

The difficult part is **not intersection detection** — it is **correctly handling shared vertices**.

The safe approach:

1. Represent segments using **half‑open intervals**
2. Convert to integer axis using **coordinate doubling**
3. Run a **sweep‑line overlap count**

This guarantees that each geometric intersection is counted exactly once.
