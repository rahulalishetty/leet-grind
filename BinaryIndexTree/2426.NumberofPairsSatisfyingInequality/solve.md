# 2426. Number of Pairs Satisfying Inequality

## Problem Restatement

We are given:

- `nums1`
- `nums2`
- `diff`

We need to count pairs `(i, j)` such that:

```text
0 <= i < j < n
```

and

```text
nums1[i] - nums1[j] <= nums2[i] - nums2[j] + diff
```

We must return the number of such pairs.

---

## Key Algebraic Transformation

Start from:

```text
nums1[i] - nums1[j] <= nums2[i] - nums2[j] + diff
```

Rearrange terms:

```text
nums1[i] - nums2[i] <= nums1[j] - nums2[j] + diff
```

Now define:

```text
arr[i] = nums1[i] - nums2[i]
```

Then the condition becomes:

```text
arr[i] <= arr[j] + diff
```

or equivalently:

```text
arr[i] - diff <= arr[j]
```

When processing `j` from left to right, we need to count how many earlier indices `i < j` satisfy:

```text
arr[i] <= arr[j] + diff
```

So for each `j`, the task becomes:

> Count how many previous values are `<= arr[j] + diff`.

This is a prefix-count / order-statistics problem.

---

# Approach 1 — Brute Force

## Intuition

The most direct solution is to check every pair `(i, j)`.

For each pair:

- test the inequality
- increment the answer if it holds

This is easy to derive, but far too slow for `n = 10^5`.

---

## Java Code

```java
class Solution {
    public long numberOfPairs(int[] nums1, int[] nums2, int diff) {
        int n = nums1.length;
        long ans = 0;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (nums1[i] - nums1[j] <= nums2[i] - nums2[j] + diff) {
                    ans++;
                }
            }
        }

        return ans;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n^2)
```

### Space Complexity

```text
O(1)
```

This is not feasible for the full constraints.

---

# Approach 2 — Fenwick Tree with Coordinate Compression

## Intuition

After the transformation:

```text
arr[i] = nums1[i] - nums2[i]
```

we need, for each current `arr[j]`, the number of previous values satisfying:

```text
arr[i] <= arr[j] + diff
```

This is a counting query over previously seen values.

A Fenwick Tree is ideal if we compress values into ranks.

---

## Why Compression Is Needed

`arr[i]` can be negative, and `arr[j] + diff` can also be outside a small positive range.

Fenwick Tree requires indices like:

```text
1, 2, 3, ...
```

So we collect all values we may query/update:

- every `arr[i]`
- every `arr[i] + diff`

Sort them, remove duplicates, and map them to ranks.

Then:

- query prefix count up to rank of `arr[j] + diff`
- update rank of `arr[j]`

---

## Java Code

```java
import java.util.*;

class Solution {
    public long numberOfPairs(int[] nums1, int[] nums2, int diff) {
        int n = nums1.length;
        long[] arr = new long[n];
        List<Long> values = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            arr[i] = (long) nums1[i] - nums2[i];
            values.add(arr[i]);
            values.add(arr[i] + diff);
        }

        Collections.sort(values);
        List<Long> uniq = new ArrayList<>();
        for (long v : values) {
            if (uniq.isEmpty() || uniq.get(uniq.size() - 1) != v) {
                uniq.add(v);
            }
        }

        Fenwick bit = new Fenwick(uniq.size() + 2);
        long ans = 0;

        for (int j = 0; j < n; j++) {
            int qRank = upperBound(uniq, arr[j] + diff);
            ans += bit.query(qRank);

            int uRank = lowerBound(uniq, arr[j]) + 1;
            bit.add(uRank, 1);
        }

        return ans;
    }

    private int lowerBound(List<Long> list, long target) {
        int left = 0, right = list.size();
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (list.get(mid) < target) left = mid + 1;
            else right = mid;
        }
        return left;
    }

    private int upperBound(List<Long> list, long target) {
        int left = 0, right = list.size();
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (list.get(mid) <= target) left = mid + 1;
            else right = mid;
        }
        return left; // already 1-based compatible for query count
    }

    static class Fenwick {
        int[] tree;

        Fenwick(int n) {
            tree = new int[n + 1];
        }

        void add(int index, int delta) {
            while (index < tree.length) {
                tree[index] += delta;
                index += index & -index;
            }
        }

        int query(int index) {
            int sum = 0;
            while (index > 0) {
                sum += tree[index];
                index -= index & -index;
            }
            return sum;
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

- build transformed array: `O(n)`
- sorting for compression: `O(n log n)`
- Fenwick operations: `O(n log n)`

Overall:

```text
O(n log n)
```

### Space Complexity

```text
O(n)
```

This is a standard optimal solution.

---

# Approach 3 — Segment Tree with Coordinate Compression

## Intuition

This is very similar to the Fenwick approach.

We still transform the inequality to:

```text
arr[i] <= arr[j] + diff
```

We still compress values.

The difference is that we use a segment tree instead of a Fenwick Tree to store counts of seen values.

For each `j`:

- query how many previous values lie in `(-∞, arr[j] + diff]`
- then insert `arr[j]`

This is equally valid, though usually more verbose.

---

## Java Code

```java
import java.util.*;

class Solution {
    public long numberOfPairs(int[] nums1, int[] nums2, int diff) {
        int n = nums1.length;
        long[] arr = new long[n];
        List<Long> values = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            arr[i] = (long) nums1[i] - nums2[i];
            values.add(arr[i]);
            values.add(arr[i] + diff);
        }

        Collections.sort(values);
        List<Long> uniq = new ArrayList<>();
        for (long v : values) {
            if (uniq.isEmpty() || uniq.get(uniq.size() - 1) != v) {
                uniq.add(v);
            }
        }

        SegmentTree seg = new SegmentTree(uniq.size());
        long ans = 0;

        for (int j = 0; j < n; j++) {
            int q = upperBound(uniq, arr[j] + diff);
            if (q > 0) {
                ans += seg.query(1, q);
            }

            int idx = lowerBound(uniq, arr[j]) + 1;
            seg.update(idx, 1);
        }

        return ans;
    }

    private int lowerBound(List<Long> list, long target) {
        int left = 0, right = list.size();
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (list.get(mid) < target) left = mid + 1;
            else right = mid;
        }
        return left;
    }

    private int upperBound(List<Long> list, long target) {
        int left = 0, right = list.size();
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (list.get(mid) <= target) left = mid + 1;
            else right = mid;
        }
        return left;
    }

    static class SegmentTree {
        int[] tree;
        int n;

        SegmentTree(int n) {
            this.n = n;
            tree = new int[4 * n];
        }

        void update(int index, int delta) {
            update(1, 1, n, index, delta);
        }

        private void update(int node, int left, int right, int index, int delta) {
            if (left == right) {
                tree[node] += delta;
                return;
            }
            int mid = left + (right - left) / 2;
            if (index <= mid) update(node * 2, left, mid, index, delta);
            else update(node * 2 + 1, mid + 1, right, index, delta);
            tree[node] = tree[node * 2] + tree[node * 2 + 1];
        }

        int query(int ql, int qr) {
            return query(1, 1, n, ql, qr);
        }

        private int query(int node, int left, int right, int ql, int qr) {
            if (ql <= left && right <= qr) return tree[node];
            if (right < ql || left > qr) return 0;
            int mid = left + (right - left) / 2;
            return query(node * 2, left, mid, ql, qr)
                 + query(node * 2 + 1, mid + 1, right, ql, qr);
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n log n)
```

### Space Complexity

```text
O(n)
```

---

# Approach 4 — Merge Sort Counting

## Intuition

This problem can also be solved with a divide-and-conquer counting method, very similar to reverse pairs / count range problems.

After transforming:

```text
arr[i] <= arr[j] + diff
```

for pairs `i < j`.

During merge sort, before merging two sorted halves:

- left half indices are earlier
- right half indices are later

For each element in the right half, we can count how many elements in the left half satisfy:

```text
leftValue <= rightValue + diff
```

Because both halves are sorted, this can be done with a moving pointer.

Then perform the normal merge.

This yields an elegant `O(n log n)` solution without a tree structure.

---

## Java Code

```java
class Solution {
    public long numberOfPairs(int[] nums1, int[] nums2, int diff) {
        int n = nums1.length;
        long[] arr = new long[n];
        for (int i = 0; i < n; i++) {
            arr[i] = (long) nums1[i] - nums2[i];
        }

        long[] temp = new long[n];
        return mergeSort(arr, temp, 0, n - 1, diff);
    }

    private long mergeSort(long[] arr, long[] temp, int left, int right, int diff) {
        if (left >= right) return 0;

        int mid = left + (right - left) / 2;
        long count = 0;

        count += mergeSort(arr, temp, left, mid, diff);
        count += mergeSort(arr, temp, mid + 1, right, diff);

        // count valid pairs: i in left half, j in right half
        int i = left;
        for (int j = mid + 1; j <= right; j++) {
            while (i <= mid && arr[i] <= arr[j] + diff) {
                i++;
            }
            count += (i - left);
        }

        // merge sorted halves
        int p1 = left, p2 = mid + 1, k = left;
        while (p1 <= mid && p2 <= right) {
            if (arr[p1] <= arr[p2]) temp[k++] = arr[p1++];
            else temp[k++] = arr[p2++];
        }
        while (p1 <= mid) temp[k++] = arr[p1++];
        while (p2 <= right) temp[k++] = arr[p2++];

        for (int t = left; t <= right; t++) {
            arr[t] = temp[t];
        }

        return count;
    }
}
```

---

## Why the merge-sort counting works

In a merge step:

- left part indices are all `<` right part indices in the original array
- both halves are sorted by transformed value

For each `arr[j]` in the right half, we need the count of left-half values `<= arr[j] + diff`.

Since the left half is sorted, a pointer can advance monotonically.

That gives linear work per merge level, hence `O(n log n)` overall.

---

## Complexity Analysis

### Time Complexity

```text
O(n log n)
```

### Space Complexity

```text
O(n)
```

---

# Worked Example

## Example 1

```text
nums1 = [3,2,5]
nums2 = [2,2,1]
diff = 1
```

Transform:

```text
arr[i] = nums1[i] - nums2[i]
```

So:

```text
arr = [1, 0, 4]
```

We need pairs `i < j` such that:

```text
arr[i] <= arr[j] + 1
```

Check pairs:

- `(0,1)` -> `1 <= 0 + 1` -> true
- `(0,2)` -> `1 <= 4 + 1` -> true
- `(1,2)` -> `0 <= 4 + 1` -> true

Total:

```text
3
```

---

# Why the algebraic transformation is correct

Start from:

```text
nums1[i] - nums1[j] <= nums2[i] - nums2[j] + diff
```

Move terms:

```text
nums1[i] - nums2[i] <= nums1[j] - nums2[j] + diff
```

Define:

```text
arr[t] = nums1[t] - nums2[t]
```

Then:

```text
arr[i] <= arr[j] + diff
```

So the original condition and transformed condition are equivalent.

This is the critical step that makes efficient counting possible.

---

# Comparison of Approaches

## Approach 1 — Brute force

Pros:

- simplest
- easiest to verify

Cons:

- too slow

---

## Approach 2 — Fenwick Tree + compression

Pros:

- standard ordered-counting solution
- efficient
- clean once compression is understood

Cons:

- requires coordinate compression

This is the recommended tree-based solution.

---

## Approach 3 — Segment Tree + compression

Pros:

- equally valid
- useful if you prefer segment trees

Cons:

- more verbose than Fenwick Tree

---

## Approach 4 — Merge sort counting

Pros:

- elegant
- no explicit tree structure
- classic divide-and-conquer counting pattern

Cons:

- harder to derive at first glance

This is arguably the most elegant non-tree solution.

---

# Final Recommended Java Solution

The Fenwick Tree solution is usually the most interview-friendly balance of clarity and performance.

```java
import java.util.*;

class Solution {
    public long numberOfPairs(int[] nums1, int[] nums2, int diff) {
        int n = nums1.length;
        long[] arr = new long[n];
        List<Long> values = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            arr[i] = (long) nums1[i] - nums2[i];
            values.add(arr[i]);
            values.add(arr[i] + diff);
        }

        Collections.sort(values);
        List<Long> uniq = new ArrayList<>();
        for (long v : values) {
            if (uniq.isEmpty() || uniq.get(uniq.size() - 1) != v) {
                uniq.add(v);
            }
        }

        Fenwick bit = new Fenwick(uniq.size() + 2);
        long ans = 0;

        for (int j = 0; j < n; j++) {
            int qRank = upperBound(uniq, arr[j] + diff);
            ans += bit.query(qRank);

            int uRank = lowerBound(uniq, arr[j]) + 1;
            bit.add(uRank, 1);
        }

        return ans;
    }

    private int lowerBound(List<Long> list, long target) {
        int left = 0, right = list.size();
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (list.get(mid) < target) left = mid + 1;
            else right = mid;
        }
        return left;
    }

    private int upperBound(List<Long> list, long target) {
        int left = 0, right = list.size();
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (list.get(mid) <= target) left = mid + 1;
            else right = mid;
        }
        return left;
    }

    static class Fenwick {
        int[] tree;

        Fenwick(int n) {
            tree = new int[n + 1];
        }

        void add(int index, int delta) {
            while (index < tree.length) {
                tree[index] += delta;
                index += index & -index;
            }
        }

        int query(int index) {
            int sum = 0;
            while (index > 0) {
                sum += tree[index];
                index -= index & -index;
            }
            return sum;
        }
    }
}
```

---

# Complexity Summary

## Approach 1

```text
Time:  O(n^2)
Space: O(1)
```

## Approach 2

```text
Time:  O(n log n)
Space: O(n)
```

## Approach 3

```text
Time:  O(n log n)
Space: O(n)
```

## Approach 4

```text
Time:  O(n log n)
Space: O(n)
```

---

# Final Takeaway

The problem becomes much easier after the transformation:

```text
arr[i] = nums1[i] - nums2[i]
```

Then we only need to count pairs:

```text
i < j and arr[i] <= arr[j] + diff
```

That is a classic “count previous values satisfying a threshold” problem, which can be solved efficiently using:

- Fenwick Tree + coordinate compression
- Segment Tree + compression
- Merge Sort counting
