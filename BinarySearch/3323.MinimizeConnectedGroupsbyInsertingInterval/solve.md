# 3323. Minimize Connected Groups by Inserting Interval — Java Solutions and Detailed Notes

## Problem

We are given `intervals`, where each interval is:

```text
[start_i, end_i]
```

We must insert **exactly one** new interval:

```text
[startNew, endNew]
```

such that:

```text
endNew - startNew <= k
```

After insertion, intervals that touch or overlap belong to the same connected group.

We want to minimize the number of connected groups.

---

## Example

If we have:

```text
[1,3], [5,6], [8,10]
```

then initially there are 3 groups:

- `[1,3]`
- `[5,6]`
- `[8,10]`

If we insert `[3,5]`, then:

- `[1,3]`, `[3,5]`, `[5,6]` merge into one group
- `[8,10]` stays alone

So the answer becomes `2`.

---

# First key observation: merge the original intervals first

Before thinking about the new interval, we should first merge the original intervals into their existing connected groups.

Why?

Because inside one already-connected group, inserting a new interval changes nothing internally.
The only useful effect is to connect **different groups** together.

So after sorting and merging, we get a list of disjoint groups:

```text
[g0_start, g0_end], [g1_start, g1_end], ..., [gm-1_start, gm-1_end]
```

with strict gaps between consecutive groups:

```text
g[i+1].start > g[i].end
```

Let the number of merged groups be:

```text
m
```

Without inserting anything, answer = `m`.

After inserting one interval, we want to connect as many consecutive merged groups as possible.

---

# Second key observation: what does it take to connect several consecutive groups?

Suppose we want one inserted interval to connect merged groups from index `i` through index `j`.

That means the new interval must bridge from the end of group `i` all the way to the start of group `j`, across all intermediate gaps.

To connect group `i` with group `i+1`, we must cover the gap:

```text
groups[i+1].start - groups[i].end
```

But because touching counts as connected, an inserted interval can start at `groups[i].end` and end at `groups[i+1].start`.

So to connect groups `i..j`, it is enough that the inserted interval spans from:

```text
groups[i].end
```

to:

```text
groups[j].start
```

Thus the required length is:

```text
groups[j].start - groups[i].end
```

So the problem becomes:

> Find the maximum number of consecutive merged groups that can be connected by one interval of length at most `k`.

If we can connect `t` groups together, then those `t` groups become 1 group, reducing the total by:

```text
t - 1
```

So the final answer is:

```text
m - (bestConnectedCount - 1)
= m - bestConnectedCount + 1
```

where `bestConnectedCount >= 1`.

---

# Approach 1: Brute Force after merging (too slow)

## Idea

1. Sort intervals.
2. Merge them into disjoint groups.
3. Try every pair `(i, j)` of merged groups.
4. Check whether:

```text
groups[j].start - groups[i].end <= k
```

If yes, then one inserted interval can connect all groups from `i` to `j`.

Track the maximum number of groups connected.

---

## Java code

```java
import java.util.*;

class Solution {
    public int minConnectedGroups(int[][] intervals, int k) {
        List<long[]> groups = merge(intervals);
        int m = groups.size();

        int best = 1;

        for (int i = 0; i < m; i++) {
            for (int j = i; j < m; j++) {
                if (groups.get(j)[0] - groups.get(i)[1] <= k) {
                    best = Math.max(best, j - i + 1);
                }
            }
        }

        return m - best + 1;
    }

    private List<long[]> merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        List<long[]> groups = new ArrayList<>();

        long start = intervals[0][0];
        long end = intervals[0][1];

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] <= end) {
                end = Math.max(end, intervals[i][1]);
            } else {
                groups.add(new long[]{start, end});
                start = intervals[i][0];
                end = intervals[i][1];
            }
        }

        groups.add(new long[]{start, end});
        return groups;
    }
}
```

---

## Complexity

If `m` is the number of merged groups:

- sorting: `O(n log n)`
- merging: `O(n)`
- brute force over pairs: `O(m^2)`

Worst case:

```text
O(n^2)
```

Too slow for `10^5`.

---

# Approach 2: Sliding Window on merged groups (optimal)

## Idea

After merging, we have disjoint groups:

```text
[g0_start, g0_end], [g1_start, g1_end], ...
```

We want the largest window `[i..j]` such that:

```text
groups[j].start - groups[i].end <= k
```

Because if that holds, then one interval of length at most `k` can connect all groups in that window.

This condition is monotonic for fixed `j` as `i` moves right, so a sliding window works.

---

## Why sliding window is valid

For a fixed left endpoint `i`, as `j` moves right:

```text
groups[j].start - groups[i].end
```

only increases.

So once the condition fails, moving `j` further right will not help.
We must move `i` forward.

That is the standard two-pointer pattern.

---

## Algorithm

### Step 1: Merge the intervals

Sort by start time and merge overlapping/touching intervals into groups.

### Step 2: Sliding window over merged groups

Maintain two pointers `left` and `right`.

For each `right`:

- while

```text
groups[right].start - groups[left].end > k
```

move `left` forward

Then window size is:

```text
right - left + 1
```

Track the maximum window size `best`.

### Step 3: Compute final answer

If one interval can connect `best` groups into one, the group count decreases by `best - 1`.

So answer is:

```text
m - best + 1
```

---

## Java code

```java
import java.util.*;

class Solution {
    public int minConnectedGroups(int[][] intervals, int k) {
        List<long[]> groups = merge(intervals);
        int m = groups.size();

        int best = 1;
        int left = 0;

        for (int right = 0; right < m; right++) {
            while (groups.get(right)[0] - groups.get(left)[1] > k) {
                left++;
            }
            best = Math.max(best, right - left + 1);
        }

        return m - best + 1;
    }

    private List<long[]> merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        List<long[]> groups = new ArrayList<>();

        long start = intervals[0][0];
        long end = intervals[0][1];

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] <= end) {
                end = Math.max(end, intervals[i][1]);
            } else {
                groups.add(new long[]{start, end});
                start = intervals[i][0];
                end = intervals[i][1];
            }
        }

        groups.add(new long[]{start, end});
        return groups;
    }
}
```

---

## Complexity

Sorting:

```text
O(n log n)
```

Merging:

```text
O(n)
```

Sliding window on merged groups:

```text
O(m)
```

Total:

```text
O(n log n)
```

Space complexity:

```text
O(m)
```

This is optimal for the problem.

---

# Approach 3: Binary search per left endpoint after merging (works, but slower than sliding window)

## Idea

After merging, for each left endpoint `i`, we could binary search the farthest `j` such that:

```text
groups[j].start - groups[i].end <= k
```

Then update the best window size.

This works because `groups[j].start` is increasing.

---

## Java code

```java
import java.util.*;

class Solution {
    public int minConnectedGroups(int[][] intervals, int k) {
        List<long[]> groups = merge(intervals);
        int m = groups.size();

        long[] starts = new long[m];
        for (int i = 0; i < m; i++) {
            starts[i] = groups.get(i)[0];
        }

        int best = 1;

        for (int i = 0; i < m; i++) {
            long limit = groups.get(i)[1] + (long) k;
            int j = upperBound(starts, limit) - 1;
            best = Math.max(best, j - i + 1);
        }

        return m - best + 1;
    }

    private int upperBound(long[] arr, long target) {
        int left = 0, right = arr.length;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] <= target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }

    private List<long[]> merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        List<long[]> groups = new ArrayList<>();

        long start = intervals[0][0];
        long end = intervals[0][1];

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] <= end) {
                end = Math.max(end, intervals[i][1]);
            } else {
                groups.add(new long[]{start, end});
                start = intervals[i][0];
                end = intervals[i][1];
            }
        }

        groups.add(new long[]{start, end});
        return groups;
    }
}
```

---

## Complexity

Sorting + merging:

```text
O(n log n)
```

Then for each merged group, one binary search:

```text
O(m log m)
```

Total:

```text
O(n log n + m log m)
```

Since `m <= n`, this is still:

```text
O(n log n)
```

But the sliding-window version is cleaner and faster in practice.

---

# Why the formula `groups[j].start - groups[i].end <= k` is sufficient

Suppose we want to connect groups `i..j`.

If we insert interval:

```text
[groups[i].end, groups[j].start]
```

then its length is:

```text
groups[j].start - groups[i].end
```

If this is at most `k`, the inserted interval touches:

- group `i` at its left endpoint
- group `j` at its right endpoint
- and therefore also bridges all intermediate groups, because those groups are already individually merged and ordered without overlap gaps inside each group list.

So all groups from `i` through `j` become one connected group.

Conversely, any inserted interval that connects groups `i` through `j` must span at least from the end of `i` to the start of `j`, so its length must be at least that amount.

Thus the condition is both necessary and sufficient.

---

# Worked examples

## Example 1

```text
intervals = [[1,3],[5,6],[8,10]], k = 3
```

These are already merged groups:

```text
[1,3], [5,6], [8,10]
```

Check windows:

- groups 0..1:

  ```text
  5 - 3 = 2 <= 3
  ```

  valid

- groups 1..2:

  ```text
  8 - 6 = 2 <= 3
  ```

  valid

- groups 0..2:
  ```text
  8 - 3 = 5 > 3
  ```
  invalid

So the maximum connectable window size is `2`.

Final answer:

```text
3 - 2 + 1 = 2
```

Correct.

---

## Example 2

```text
intervals = [[5,10],[1,1],[3,3]], k = 1
```

After sorting and merging:

```text
[1,1], [3,3], [5,10]
```

Check possible windows:

- groups 0..1:

  ```text
  3 - 1 = 2 > 1
  ```

  invalid

- groups 1..2:
  ```text
  5 - 3 = 2 > 1
  ```
  invalid

So best window size is `1`.

Final answer:

```text
3 - 1 + 1 = 3
```

Correct.

---

# Comparison of approaches

## Approach 1: Brute force on merged groups

### Pros

- easiest to reason about

### Cons

- quadratic after merging
- too slow

### Complexity

```text
O(n log n + m^2)
```

---

## Approach 2: Sliding window on merged groups (Recommended)

### Pros

- optimal
- simple after the key observation
- linear after merge

### Cons

- requires recognizing the merged-group window condition

### Complexity

```text
O(n log n)
```

---

## Approach 3: Binary search per group

### Pros

- also efficient
- useful alternative if you spot the monotone endpoint condition

### Cons

- slightly more overhead than sliding window

### Complexity

```text
O(n log n)
```

---

# Final recommended Java solution

```java
import java.util.*;

class Solution {
    public int minConnectedGroups(int[][] intervals, int k) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        List<long[]> groups = new ArrayList<>();
        long start = intervals[0][0];
        long end = intervals[0][1];

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] <= end) {
                end = Math.max(end, intervals[i][1]);
            } else {
                groups.add(new long[]{start, end});
                start = intervals[i][0];
                end = intervals[i][1];
            }
        }
        groups.add(new long[]{start, end});

        int m = groups.size();
        int best = 1;
        int left = 0;

        for (int right = 0; right < m; right++) {
            while (groups.get(right)[0] - groups.get(left)[1] > k) {
                left++;
            }
            best = Math.max(best, right - left + 1);
        }

        return m - best + 1;
    }
}
```

---

# Edge cases

## 1. Already one connected group

If the original intervals merge into one group, answer is already `1`, and adding one interval cannot do better.

The formula still works:

```text
m = 1, best = 1 => 1 - 1 + 1 = 1
```

## 2. `k` very large

If `k` is large enough to bridge from the end of the first merged group to the start of the last one, then all groups can be connected.

Answer becomes `1`.

## 3. No gap can be bridged

If every consecutive group gap is larger than `k`, then the best window size is `1`, so the answer remains `m`.

---

# Pattern takeaway

This problem is a strong example of:

1. **Sort + merge intervals first**
2. Reduce the problem to a simpler structure on merged groups
3. Use **sliding window** on a monotone condition

A lot of interval problems become much simpler once you first compress them into already-merged connected components.
