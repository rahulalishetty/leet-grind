# Count Integers in Intervals — Intuition and Approach

## Intuition

We need a data structure that supports two operations efficiently:

1. **Add an interval `[left, right]`**
2. **Count all unique integers covered by the added intervals**

Since intervals may overlap, simply summing the lengths of intervals will **overcount integers**.

Example:

```
[1,5] and [4,8]
```

The overlap is:

```
[4,5]
```

So integers **4 and 5** would be counted twice if we simply added lengths.

Therefore, we must **merge overlapping intervals** so that each integer is counted **exactly once**.

The key idea is:

- Keep intervals **sorted**
- **Merge overlaps**
- Maintain a **running total of covered integers**.

---

# Approach

## 1. Use a Sorted Data Structure

We use a **TreeMap** in Java.

```
start -> end
```

This allows:

- fast lookup of nearby intervals
- maintaining intervals in sorted order
- efficient merging

---

## 2. Adding a New Interval `[left, right]`

Steps:

### Step 1 — Find Possible Overlaps

We look for intervals that might overlap with the new interval.

Two intervals overlap if:

```
b >= c - 1 AND a <= d + 1
```

for intervals `[a,b]` and `[c,d]`.

This also handles **touching intervals**.

---

### Step 2 — Merge Overlapping Intervals

For every overlapping interval:

```
merged_start = min(left, start)
merged_end   = max(right, end)
```

We update `[left, right]` to become this merged interval.

---

### Step 3 — Remove Old Intervals

Since the intervals are merged into one, we remove all overlapping intervals from the map.

---

### Step 4 — Maintain the Running Total

Each interval contributes:

```
(end - start + 1)
```

When merging:

1. Subtract the length of intervals being removed
2. Add the length of the merged interval

This ensures the count always represents **unique integers**.

---

## 3. Counting Covered Integers

The `count()` operation simply returns the maintained total.

```
count() = total covered integers
```

Since we update the value incrementally, the operation is:

```
O(1)
```

---

# Why This Works

- Merging intervals removes overlaps
- Sorted structure makes detecting overlaps efficient
- Incrementally updating totals avoids recomputation

Thus the structure always represents the **union of all intervals**.

---

# Complexity

## Time Complexity

### add(left, right)

```
O(log n + k)
```

Where:

- `log n` → TreeMap operations
- `k` → number of overlapping intervals merged

### count()

```
O(1)
```

---

## Space Complexity

```
O(n)
```

Where `n` is the number of merged intervals stored.

---

# Java Implementation

```java
import java.util.*;

class CountIntervals {
    private TreeMap<Integer, Integer> intervals;
    private long total; // total number of integers covered

    public CountIntervals() {
        intervals = new TreeMap<>();
        total = 0;
    }

    public void add(int left, int right) {
        // Find intervals that might overlap with [left, right]
        Integer start = intervals.floorKey(left);
        if (start == null) start = intervals.ceilingKey(left);

        while (start != null) {
            int end = intervals.get(start);

            // No overlap
            if (end < left) {
                start = intervals.higherKey(start);
                continue;
            }

            if (start > right) break;

            // Merge intervals
            left = Math.min(left, start);
            right = Math.max(right, end);

            // Remove previous interval contribution
            total -= (end - start + 1);
            intervals.remove(start);

            start = intervals.higherKey(left);
        }

        // Add merged interval
        intervals.put(left, right);
        total += (right - left + 1);
    }

    public int count() {
        return (int) total;
    }
}
```

---

# Summary

This data structure works because:

- intervals are stored **sorted**
- overlapping intervals are **merged**
- the total number of covered integers is **maintained incrementally**

This guarantees:

```
add()   -> O(log n + k)
count() -> O(1)
space   -> O(n)
```
