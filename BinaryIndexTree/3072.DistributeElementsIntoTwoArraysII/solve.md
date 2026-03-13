# 3072. Distribute Elements Into Two Arrays II — Exhaustive Java Notes

## Problem Statement

You are given a 1-indexed array `nums` of length `n`.

We define:

```text
greaterCount(arr, val)
```

as the number of elements in `arr` that are **strictly greater than** `val`.

We must distribute the elements of `nums` into two arrays:

- `arr1`
- `arr2`

using the following rules:

- Operation 1: append `nums[1]` to `arr1`
- Operation 2: append `nums[2]` to `arr2`

For each later element `nums[i]`:

1. If `greaterCount(arr1, nums[i]) > greaterCount(arr2, nums[i])`, append to `arr1`
2. If `greaterCount(arr1, nums[i]) < greaterCount(arr2, nums[i])`, append to `arr2`
3. If equal, append to the array with fewer elements
4. If still tied, append to `arr1`

Finally return:

```text
result = arr1 followed by arr2
```

---

## Example 1

```text
Input: nums = [2,1,3,3]
Output: [2,3,1,3]
```

Process:

- `arr1 = [2]`
- `arr2 = [1]`

Now `3`:

- greater than `3` in `arr1` = 0
- greater than `3` in `arr2` = 0
- lengths equal, so append to `arr1`

Now:

- `arr1 = [2,3]`
- `arr2 = [1]`

Next `3`:

- greater than `3` in `arr1` = 0
- greater than `3` in `arr2` = 0
- `arr2` is shorter, so append to `arr2`

Final:

```text
arr1 = [2,3]
arr2 = [1,3]
result = [2,3,1,3]
```

---

## Example 2

```text
Input: nums = [5,14,3,1,2]
Output: [5,3,1,2,14]
```

---

## Example 3

```text
Input: nums = [3,3,3,3]
Output: [3,3,3,3]
```

---

## Constraints

- `3 <= n <= 10^5`
- `1 <= nums[i] <= 10^9`

---

# 1. Core Insight

The expensive part is repeatedly computing:

```text
greaterCount(arr1, x)
greaterCount(arr2, x)
```

for each new element `x`.

If we did this naively by scanning the arrays every time, it would be too slow.

So the real problem is:

> Maintain two dynamic multisets, and for each new value `x`, quickly count how many inserted values are greater than `x`.

That suggests:

- balanced BST / sorted list structures
- Fenwick Tree / BIT after coordinate compression
- Segment Tree after compression

Because values can be up to `10^9`, direct indexing is impossible, so compression is natural.

---

# 2. Approach 1 — Direct Simulation

## Idea

Maintain `arr1` and `arr2` as lists.

For each new `x`, compute `greaterCount` by scanning the full array.

## Java Code

```java
import java.util.*;

class SolutionBruteForce {
    public int[] resultArray(int[] nums) {
        List<Integer> arr1 = new ArrayList<>();
        List<Integer> arr2 = new ArrayList<>();

        arr1.add(nums[0]);
        arr2.add(nums[1]);

        for (int i = 2; i < nums.length; i++) {
            int x = nums[i];

            int g1 = greaterCount(arr1, x);
            int g2 = greaterCount(arr2, x);

            if (g1 > g2) {
                arr1.add(x);
            } else if (g1 < g2) {
                arr2.add(x);
            } else if (arr1.size() < arr2.size()) {
                arr1.add(x);
            } else if (arr1.size() > arr2.size()) {
                arr2.add(x);
            } else {
                arr1.add(x);
            }
        }

        int[] ans = new int[nums.length];
        int idx = 0;
        for (int v : arr1) ans[idx++] = v;
        for (int v : arr2) ans[idx++] = v;
        return ans;
    }

    private int greaterCount(List<Integer> arr, int x) {
        int count = 0;
        for (int v : arr) {
            if (v > x) count++;
        }
        return count;
    }
}
```

## Complexity

- Each query may scan almost the whole arrays
- Total time:

```text
O(n^2)
```

- Space:

```text
O(n)
```

## Verdict

Correct but too slow for `n = 10^5`.

---

# 3. Approach 2 — Maintain Sorted Lists and Use Binary Search

## Idea

For each array:

- keep the actual insertion-order list for final output
- also keep a sorted structure for counting greater elements

If the sorted structure is an `ArrayList`, then:

- binary search gives the insertion point / first greater position
- but inserting into the middle of an `ArrayList` costs `O(n)`

So this is better than brute force conceptually, but still worst-case quadratic.

## Java Sketch

```java
import java.util.*;

class SolutionSortedList {
    public int[] resultArray(int[] nums) {
        List<Integer> arr1 = new ArrayList<>();
        List<Integer> arr2 = new ArrayList<>();
        List<Integer> sorted1 = new ArrayList<>();
        List<Integer> sorted2 = new ArrayList<>();

        arr1.add(nums[0]);
        arr2.add(nums[1]);
        sorted1.add(nums[0]);
        sorted2.add(nums[1]);

        for (int i = 2; i < nums.length; i++) {
            int x = nums[i];

            int g1 = greaterCount(sorted1, x);
            int g2 = greaterCount(sorted2, x);

            if (g1 > g2) {
                arr1.add(x);
                insertSorted(sorted1, x);
            } else if (g1 < g2) {
                arr2.add(x);
                insertSorted(sorted2, x);
            } else if (arr1.size() <= arr2.size()) {
                arr1.add(x);
                insertSorted(sorted1, x);
            } else {
                arr2.add(x);
                insertSorted(sorted2, x);
            }
        }

        int[] ans = new int[nums.length];
        int idx = 0;
        for (int v : arr1) ans[idx++] = v;
        for (int v : arr2) ans[idx++] = v;
        return ans;
    }

    private int greaterCount(List<Integer> sorted, int x) {
        int pos = upperBound(sorted, x);
        return sorted.size() - pos;
    }

    private void insertSorted(List<Integer> sorted, int x) {
        int pos = upperBound(sorted, x);
        sorted.add(pos, x);
    }

    private int upperBound(List<Integer> arr, int target) {
        int l = 0, r = arr.size();
        while (l < r) {
            int m = l + (r - l) / 2;
            if (arr.get(m) <= target) l = m + 1;
            else r = m;
        }
        return l;
    }
}
```

## Complexity

- Binary search: `O(log n)`
- Insert into list: `O(n)`

So total remains:

```text
O(n^2)
```

## Verdict

Not sufficient for the actual constraints.

---

# 4. Optimal Pattern — Count Greater Elements with Fenwick Trees

## Key observation

If we know how many elements are `<= x`, then:

```text
greaterCount(arr, x) = size(arr) - count(<= x)
```

So for each array we need:

- total size
- prefix frequency query up to compressed value of `x`

That is exactly what a Fenwick Tree does.

Because `nums[i]` can be large (`10^9`), we first coordinate-compress all values.

Then maintain:

- Fenwick tree for `arr1`
- Fenwick tree for `arr2`
- insertion-order lists for final output

For each `x`:

1. get its compressed rank
2. compute:
   ```text
   g1 = arr1.size() - bit1.query(rank)
   g2 = arr2.size() - bit2.query(rank)
   ```
3. apply the problem rules
4. update the corresponding BIT and list

This gives the needed performance.

---

# 5. Approach 3 — Coordinate Compression + Two Fenwick Trees (Optimal)

## 5.1 Coordinate compression

Suppose:

```text
nums = [5,14,3,1,2]
```

Sorted unique values are:

```text
[1,2,3,5,14]
```

Map them to ranks:

```text
1 -> 1
2 -> 2
3 -> 3
5 -> 4
14 -> 5
```

Then prefix queries on the rank correspond to counting values `<= x`.

---

## 5.2 Fenwick Tree meaning

For each array:

- BIT stores frequency of inserted values by compressed rank
- `query(rank)` returns how many inserted values are `<= value(rank)`

Then:

```text
greaterCount = currentSize - query(rank)
```

because all remaining inserted values must be strictly greater.

---

## 5.3 Java Code — Fenwick Tree Solution

```java
import java.util.*;

class Solution {
    static class Fenwick {
        int n;
        int[] bit;

        Fenwick(int n) {
            this.n = n;
            this.bit = new int[n + 1];
        }

        void add(int index, int delta) {
            while (index <= n) {
                bit[index] += delta;
                index += index & -index;
            }
        }

        int sum(int index) {
            int res = 0;
            while (index > 0) {
                res += bit[index];
                index -= index & -index;
            }
            return res;
        }
    }

    public int[] resultArray(int[] nums) {
        int n = nums.length;

        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        int m = 0;
        for (int x : sorted) {
            if (m == 0 || sorted[m - 1] != x) {
                sorted[m++] = x;
            }
        }
        int[] unique = Arrays.copyOf(sorted, m);

        Fenwick bit1 = new Fenwick(m);
        Fenwick bit2 = new Fenwick(m);

        List<Integer> arr1 = new ArrayList<>();
        List<Integer> arr2 = new ArrayList<>();

        arr1.add(nums[0]);
        arr2.add(nums[1]);

        bit1.add(rank(unique, nums[0]), 1);
        bit2.add(rank(unique, nums[1]), 1);

        for (int i = 2; i < n; i++) {
            int x = nums[i];
            int r = rank(unique, x);

            int g1 = arr1.size() - bit1.sum(r);
            int g2 = arr2.size() - bit2.sum(r);

            if (g1 > g2) {
                arr1.add(x);
                bit1.add(r, 1);
            } else if (g1 < g2) {
                arr2.add(x);
                bit2.add(r, 1);
            } else if (arr1.size() < arr2.size()) {
                arr1.add(x);
                bit1.add(r, 1);
            } else if (arr1.size() > arr2.size()) {
                arr2.add(x);
                bit2.add(r, 1);
            } else {
                arr1.add(x);
                bit1.add(r, 1);
            }
        }

        int[] ans = new int[n];
        int idx = 0;
        for (int v : arr1) ans[idx++] = v;
        for (int v : arr2) ans[idx++] = v;
        return ans;
    }

    private int rank(int[] unique, int x) {
        int l = 0, r = unique.length - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;
            if (unique[m] == x) return m + 1; // 1-based
            if (unique[m] < x) l = m + 1;
            else r = m - 1;
        }
        return -1;
    }
}
```

---

## 5.4 Complexity

- Coordinate compression: `O(n log n)`
- For each element:
  - two BIT prefix sums
  - one BIT update

Each BIT operation is `O(log n)`, so total:

```text
Time:  O(n log n)
Space: O(n)
```

This is efficient enough for `n = 10^5`.

---

# 6. Approach 4 — Coordinate Compression + Segment Tree

A segment tree can do the same job.

## Idea

Maintain counts by compressed rank in two segment trees.

For value `x` with rank `r`:

- count `<= x` by range query `[1..r]`
- derive greater count using current array size
- update point `r` by `+1`

This is slightly more verbose than Fenwick but equally valid.

## Java Code

```java
import java.util.*;

class SolutionSegmentTree {
    static class SegmentTree {
        int n;
        int[] tree;

        SegmentTree(int n) {
            this.n = n;
            this.tree = new int[4 * n];
        }

        void update(int node, int left, int right, int index) {
            if (left == right) {
                tree[node]++;
                return;
            }

            int mid = left + (right - left) / 2;
            if (index <= mid) {
                update(node * 2, left, mid, index);
            } else {
                update(node * 2 + 1, mid + 1, right, index);
            }

            tree[node] = tree[node * 2] + tree[node * 2 + 1];
        }

        int query(int node, int left, int right, int ql, int qr) {
            if (ql > right || qr < left) return 0;
            if (ql <= left && right <= qr) return tree[node];

            int mid = left + (right - left) / 2;
            return query(node * 2, left, mid, ql, qr)
                 + query(node * 2 + 1, mid + 1, right, ql, qr);
        }

        void update(int index) {
            update(1, 1, n, index);
        }

        int query(int l, int r) {
            if (l > r) return 0;
            return query(1, 1, n, l, r);
        }
    }

    public int[] resultArray(int[] nums) {
        int n = nums.length;

        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        int m = 0;
        for (int x : sorted) {
            if (m == 0 || sorted[m - 1] != x) {
                sorted[m++] = x;
            }
        }
        int[] unique = Arrays.copyOf(sorted, m);

        SegmentTree st1 = new SegmentTree(m);
        SegmentTree st2 = new SegmentTree(m);

        List<Integer> arr1 = new ArrayList<>();
        List<Integer> arr2 = new ArrayList<>();

        arr1.add(nums[0]);
        arr2.add(nums[1]);

        st1.update(rank(unique, nums[0]));
        st2.update(rank(unique, nums[1]));

        for (int i = 2; i < n; i++) {
            int x = nums[i];
            int r = rank(unique, x);

            int g1 = arr1.size() - st1.query(1, r);
            int g2 = arr2.size() - st2.query(1, r);

            if (g1 > g2) {
                arr1.add(x);
                st1.update(r);
            } else if (g1 < g2) {
                arr2.add(x);
                st2.update(r);
            } else if (arr1.size() < arr2.size()) {
                arr1.add(x);
                st1.update(r);
            } else if (arr1.size() > arr2.size()) {
                arr2.add(x);
                st2.update(r);
            } else {
                arr1.add(x);
                st1.update(r);
            }
        }

        int[] ans = new int[n];
        int idx = 0;
        for (int v : arr1) ans[idx++] = v;
        for (int v : arr2) ans[idx++] = v;
        return ans;
    }

    private int rank(int[] unique, int x) {
        int l = 0, r = unique.length - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;
            if (unique[m] == x) return m + 1;
            if (unique[m] < x) l = m + 1;
            else r = m - 1;
        }
        return -1;
    }
}
```

---

## Complexity

- Time:

```text
O(n log n)
```

- Space:

```text
O(n)
```

---

# 7. Dry Run of the Fenwick Solution

Take:

```text
nums = [5,14,3,1,2]
```

Initial:

```text
arr1 = [5]
arr2 = [14]
```

Now process `3`:

- greater in `arr1` than `3` = 1 (`5`)
- greater in `arr2` than `3` = 1 (`14`)
- tie in counts, tie in lengths
- append to `arr1`

Now:

```text
arr1 = [5,3]
arr2 = [14]
```

Process `1`:

- greater in `arr1` than `1` = 2 (`5,3`)
- greater in `arr2` than `1` = 1 (`14`)
- append to `arr1`

Now:

```text
arr1 = [5,3,1]
arr2 = [14]
```

Process `2`:

- greater in `arr1` than `2` = 2 (`5,3`)
- greater in `arr2` than `2` = 1 (`14`)
- append to `arr1`

Final:

```text
arr1 = [5,3,1,2]
arr2 = [14]
result = [5,3,1,2,14]
```

Correct.

---

# 8. Why the Formula for `greaterCount` Works

Suppose an array currently has size `s`.

If `prefixLE(x)` is the number of elements `<= x`, then the number of elements `> x` must be:

```text
s - prefixLE(x)
```

There is no overlap and no missing element, because every inserted value is either:

- `<= x`, or
- `> x`

That is why counting greater elements reduces to a prefix query.

This reduction is what makes Fenwick trees work here.

---

# 9. Correctness Proof

## Lemma 1

At every step, `bit1.sum(r)` equals the number of values in `arr1` that are `<= currentValue`, where `r` is the compressed rank of `currentValue`.

### Proof

The Fenwick tree stores frequency counts by compressed value. A prefix sum up to rank `r` adds frequencies of all values whose compressed rank is at most `r`, which is exactly the count of values `<= currentValue`. ∎

---

## Lemma 2

At every step:

```text
greaterCount(arr1, x) = arr1.size() - bit1.sum(rank(x))
```

and similarly for `arr2`.

### Proof

By Lemma 1, `bit1.sum(rank(x))` counts elements `<= x`. Since all elements are either `<= x` or `> x`, subtracting from total size gives exactly the number of elements strictly greater than `x`. ∎

---

## Lemma 3

For each `nums[i]`, the algorithm appends it to the correct array according to the problem rules.

### Proof

Using Lemma 2, the algorithm computes the exact `greaterCount` values for both arrays. It then applies the four decision rules in the same order as the statement: compare greater counts, then compare lengths, then break final tie toward `arr1`. Therefore the chosen destination array is exactly correct. ∎

---

## Theorem

The algorithm returns the exact required `result` array.

### Proof

By Lemma 3, every element is appended to exactly the correct target array. Since the algorithm also preserves insertion order inside `arr1` and `arr2`, concatenating them at the end produces exactly the required `result`. ∎

---

# 10. Common Mistakes

## Mistake 1: Scanning arrays directly for every `greaterCount`

That leads to `O(n^2)`.

## Mistake 2: Forgetting that the comparison is **strictly greater**

Do not count equal values.

## Mistake 3: Losing insertion order

The BIT or segment tree is only for counting.
You still need normal lists to preserve the actual order for the final concatenation.

## Mistake 4: Not compressing coordinates

`nums[i]` can be up to `10^9`, so direct indexing is not feasible.

## Mistake 5: Tie-breaking incorrectly

When `greaterCount` is equal:

1. smaller array size wins
2. if still tied, `arr1` wins

The order matters.

---

# 11. Comparison of Approaches

| Approach                     |         Time |  Space | Notes                   |
| ---------------------------- | -----------: | -----: | ----------------------- |
| Direct simulation            |     `O(n^2)` | `O(n)` | Too slow                |
| Sorted lists + binary search |     `O(n^2)` | `O(n)` | Insertions still costly |
| Fenwick + compression        | `O(n log n)` | `O(n)` | Best practical solution |
| Segment tree + compression   | `O(n log n)` | `O(n)` | Also valid              |

---

# 12. Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    static class Fenwick {
        int n;
        int[] bit;

        Fenwick(int n) {
            this.n = n;
            this.bit = new int[n + 1];
        }

        void add(int index, int delta) {
            while (index <= n) {
                bit[index] += delta;
                index += index & -index;
            }
        }

        int sum(int index) {
            int res = 0;
            while (index > 0) {
                res += bit[index];
                index -= index & -index;
            }
            return res;
        }
    }

    public int[] resultArray(int[] nums) {
        int n = nums.length;

        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        int m = 0;
        for (int x : sorted) {
            if (m == 0 || sorted[m - 1] != x) {
                sorted[m++] = x;
            }
        }
        int[] unique = Arrays.copyOf(sorted, m);

        Fenwick bit1 = new Fenwick(m);
        Fenwick bit2 = new Fenwick(m);

        List<Integer> arr1 = new ArrayList<>();
        List<Integer> arr2 = new ArrayList<>();

        arr1.add(nums[0]);
        arr2.add(nums[1]);

        bit1.add(rank(unique, nums[0]), 1);
        bit2.add(rank(unique, nums[1]), 1);

        for (int i = 2; i < n; i++) {
            int x = nums[i];
            int r = rank(unique, x);

            int g1 = arr1.size() - bit1.sum(r);
            int g2 = arr2.size() - bit2.sum(r);

            if (g1 > g2) {
                arr1.add(x);
                bit1.add(r, 1);
            } else if (g1 < g2) {
                arr2.add(x);
                bit2.add(r, 1);
            } else if (arr1.size() < arr2.size()) {
                arr1.add(x);
                bit1.add(r, 1);
            } else if (arr1.size() > arr2.size()) {
                arr2.add(x);
                bit2.add(r, 1);
            } else {
                arr1.add(x);
                bit1.add(r, 1);
            }
        }

        int[] ans = new int[n];
        int idx = 0;
        for (int v : arr1) ans[idx++] = v;
        for (int v : arr2) ans[idx++] = v;
        return ans;
    }

    private int rank(int[] unique, int x) {
        int l = 0, r = unique.length - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;
            if (unique[m] == x) return m + 1;
            if (unique[m] < x) l = m + 1;
            else r = m - 1;
        }
        return -1;
    }
}
```

---

# 13. Interview Summary

The hard part is repeatedly computing:

```text
greaterCount(arr, x)
```

efficiently while the arrays keep growing.

Use coordinate compression and maintain frequencies of values in `arr1` and `arr2` with two Fenwick trees.

Then:

```text
greaterCount(arr, x) = size(arr) - count(<= x)
```

which turns the problem into prefix-sum queries plus updates, giving an `O(n log n)` solution.
