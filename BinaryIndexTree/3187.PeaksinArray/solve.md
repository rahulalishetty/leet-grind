# 3187. Peaks in Array — Exhaustive Java Notes

## Problem Statement

A **peak** in an array `arr` is an element `arr[i]` such that:

```text
arr[i] > arr[i - 1] and arr[i] > arr[i + 1]
```

You are given:

- an integer array `nums`
- a list of queries

There are two query types:

### Type 1

```text
[1, l, r]
```

Return the number of peak elements in the subarray:

```text
nums[l..r]
```

### Type 2

```text
[2, index, val]
```

Update:

```text
nums[index] = val
```

Return the answers to all type-1 queries in order.

---

## Important Note

The first and last element of an array or subarray **cannot** be a peak.

So for query `[1, l, r]`, only indices in:

```text
[l + 1, r - 1]
```

can possibly count as peaks.

---

## Example 1

```text
Input:
nums = [3,1,4,2,5]
queries = [[2,3,4],[1,0,4]]

Output:
[0]
```

After update:

```text
nums = [3,1,4,4,5]
```

No index is strictly greater than both neighbors, so answer is `0`.

---

## Example 2

```text
Input:
nums = [4,1,4,2,1,5]
queries = [[2,2,4],[1,0,2],[1,0,4]]

Output:
[0,1]
```

- Query `[1,0,2]` checks subarray `[4,1,4]`, no peak
- Query `[1,0,4]` checks subarray `[4,1,4,2,1]`, where index `2` is a peak

---

## Constraints

- `3 <= nums.length <= 10^5`
- `1 <= nums[i] <= 10^5`
- `1 <= queries.length <= 10^5`

---

# 1. Core Insight

A query asks:

> How many indices `i` in a range are currently peaks?

This suggests creating a helper array:

```text
peak[i] = 1 if nums[i] is a peak, else 0
```

Then a type-1 query becomes a **range sum query** on `peak`.

But there is an important subtlety:

For query `[1, l, r]`, the endpoints of the subarray cannot be peaks **within that subarray**, even if they are peaks in the full array.

So the answer is:

```text
sum of peak[i] for i in [l + 1, r - 1]
```

Now the problem becomes:

- point updates to `nums`
- update only a few affected peak positions
- range sum queries over `peak`

That naturally suggests:

- Fenwick Tree / BIT
- Segment Tree

---

# 2. Which indices change after an update?

Suppose we update:

```text
nums[index] = val
```

Which peak statuses can change?

Only:

```text
index - 1, index, index + 1
```

Why?

Because whether position `i` is a peak depends only on:

```text
nums[i - 1], nums[i], nums[i + 1]
```

So changing one array value affects only local neighborhoods.

This is the decisive optimization.

---

# 3. Approach 1 — Recompute Peaks for Every Query

## Idea

For each type-1 query `[l, r]`:

- scan every index from `l+1` to `r-1`
- count how many are peaks

For each type-2 query:

- just update `nums[index]`

## Java Code

```java
import java.util.*;

class SolutionBruteForce {
    public List<Integer> countOfPeaks(int[] nums, int[][] queries) {
        List<Integer> ans = new ArrayList<>();

        for (int[] q : queries) {
            if (q[0] == 1) {
                int l = q[1], r = q[2];
                int count = 0;

                for (int i = l + 1; i <= r - 1; i++) {
                    if (nums[i] > nums[i - 1] && nums[i] > nums[i + 1]) {
                        count++;
                    }
                }

                ans.add(count);
            } else {
                nums[q[1]] = q[2];
            }
        }

        return ans;
    }
}
```

## Complexity

- Type 1 query: `O(n)` in worst case
- Type 2 query: `O(1)`

Overall worst case:

```text
O(n * q)
```

Too slow for `10^5`.

---

# 4. Approach 2 — Maintain Peak Array + Recompute Range by Scanning

## Idea

Precompute a `peak[]` array once.

On update:

- recompute `peak[i]` only for `i in {index-1, index, index+1}`

On query:

- sum `peak[l+1..r-1]` by scanning that range

This improves updates dramatically, but query is still linear.

## Java Code

```java
import java.util.*;

class SolutionPeakArrayScan {
    public List<Integer> countOfPeaks(int[] nums, int[][] queries) {
        int n = nums.length;
        int[] peak = new int[n];

        for (int i = 1; i < n - 1; i++) {
            peak[i] = isPeak(nums, i) ? 1 : 0;
        }

        List<Integer> ans = new ArrayList<>();

        for (int[] q : queries) {
            if (q[0] == 1) {
                int l = q[1], r = q[2];
                int count = 0;
                for (int i = l + 1; i <= r - 1; i++) {
                    count += peak[i];
                }
                ans.add(count);
            } else {
                int idx = q[1], val = q[2];
                nums[idx] = val;

                for (int i = idx - 1; i <= idx + 1; i++) {
                    if (i >= 1 && i <= n - 2) {
                        peak[i] = isPeak(nums, i) ? 1 : 0;
                    }
                }
            }
        }

        return ans;
    }

    private boolean isPeak(int[] nums, int i) {
        return nums[i] > nums[i - 1] && nums[i] > nums[i + 1];
    }
}
```

## Complexity

- Update: `O(1)`
- Query: `O(n)`

Still too slow overall.

---

# 5. Approach 3 — Peak Array + Fenwick Tree (Optimal)

This is the cleanest and shortest optimal solution.

## Idea

Maintain:

- the original array `nums`
- a Fenwick Tree over `peak[]`

Where:

```text
peak[i] = 1 if nums[i] is a peak, else 0
```

Then:

- type-1 query `[l, r]` becomes:
  ```text
  sum(l+1, r-1)
  ```
- type-2 update only changes positions:
  ```text
  index-1, index, index+1
  ```
  so recompute those and update Fenwick accordingly

---

## 5.1 Fenwick Tree Meaning

Fenwick tree stores prefix sums of the `peak[]` array.

So:

```text
bit.sum(i)
```

means number of peaks in `[0..i]`.

Then range sum is:

```text
sum(r) - sum(l-1)
```

---

## 5.2 Why updates are local

For an index `i` to be a peak, it only depends on:

```text
nums[i-1], nums[i], nums[i+1]
```

Changing `nums[idx]` cannot affect whether any faraway position is a peak.

So only these can change:

```text
idx-1, idx, idx+1
```

This makes updates very cheap.

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
            index++; // convert to 1-based
            while (index <= n) {
                bit[index] += delta;
                index += index & -index;
            }
        }

        int sum(int index) {
            index++; // convert to 1-based
            int res = 0;
            while (index > 0) {
                res += bit[index];
                index -= index & -index;
            }
            return res;
        }

        int rangeSum(int left, int right) {
            if (left > right) return 0;
            return sum(right) - (left == 0 ? 0 : sum(left - 1));
        }
    }

    public List<Integer> countOfPeaks(int[] nums, int[][] queries) {
        int n = nums.length;
        int[] peak = new int[n];
        Fenwick bit = new Fenwick(n);

        for (int i = 1; i <= n - 2; i++) {
            peak[i] = isPeak(nums, i) ? 1 : 0;
            if (peak[i] == 1) {
                bit.add(i, 1);
            }
        }

        List<Integer> ans = new ArrayList<>();

        for (int[] q : queries) {
            if (q[0] == 1) {
                int l = q[1], r = q[2];
                ans.add(bit.rangeSum(l + 1, r - 1));
            } else {
                int idx = q[1];
                int val = q[2];

                // Remove old contributions for affected positions
                for (int i = idx - 1; i <= idx + 1; i++) {
                    if (i >= 1 && i <= n - 2 && peak[i] == 1) {
                        bit.add(i, -1);
                        peak[i] = 0;
                    }
                }

                nums[idx] = val;

                // Recompute and re-add new contributions
                for (int i = idx - 1; i <= idx + 1; i++) {
                    if (i >= 1 && i <= n - 2) {
                        peak[i] = isPeak(nums, i) ? 1 : 0;
                        if (peak[i] == 1) {
                            bit.add(i, 1);
                        }
                    }
                }
            }
        }

        return ans;
    }

    private boolean isPeak(int[] nums, int i) {
        return nums[i] > nums[i - 1] && nums[i] > nums[i + 1];
    }
}
```

---

## 5.4 Complexity

- Building initial peaks: `O(n log n)`
- Each type-1 query: `O(log n)`
- Each type-2 update: only 3 indices updated, each with BIT updates → `O(log n)`

Overall:

```text
Time:  O((n + q) log n)
Space: O(n)
```

This easily fits the constraints.

---

# 6. Approach 4 — Peak Array + Segment Tree

A segment tree can do the same job.

## Idea

Store `peak[i]` in a segment tree.

- Query `[l, r]` → sum over `[l+1, r-1]`
- Update type-2 → recompute `peak[idx-1]`, `peak[idx]`, `peak[idx+1]`

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

        void update(int node, int left, int right, int index, int value) {
            if (left == right) {
                tree[node] = value;
                return;
            }

            int mid = left + (right - left) / 2;
            if (index <= mid) {
                update(node * 2, left, mid, index, value);
            } else {
                update(node * 2 + 1, mid + 1, right, index, value);
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

        void update(int index, int value) {
            update(1, 0, n - 1, index, value);
        }

        int query(int left, int right) {
            if (left > right) return 0;
            return query(1, 0, n - 1, left, right);
        }
    }

    public List<Integer> countOfPeaks(int[] nums, int[][] queries) {
        int n = nums.length;
        int[] peak = new int[n];
        SegmentTree st = new SegmentTree(n);

        for (int i = 1; i <= n - 2; i++) {
            peak[i] = isPeak(nums, i) ? 1 : 0;
            st.update(i, peak[i]);
        }

        List<Integer> ans = new ArrayList<>();

        for (int[] q : queries) {
            if (q[0] == 1) {
                int l = q[1], r = q[2];
                ans.add(st.query(l + 1, r - 1));
            } else {
                int idx = q[1];
                nums[idx] = q[2];

                for (int i = idx - 1; i <= idx + 1; i++) {
                    if (i >= 1 && i <= n - 2) {
                        peak[i] = isPeak(nums, i) ? 1 : 0;
                        st.update(i, peak[i]);
                    }
                }
            }
        }

        return ans;
    }

    private boolean isPeak(int[] nums, int i) {
        return nums[i] > nums[i - 1] && nums[i] > nums[i + 1];
    }
}
```

---

## Complexity

Also:

```text
Time:  O((n + q) log n)
Space: O(n)
```

---

# 7. Dry Run on Example 2

```text
nums = [4,1,4,2,1,5]
queries = [[2,2,4],[1,0,2],[1,0,4]]
```

Initial peaks:

- index `1`: `1 > 4 and 1 > 4`? no
- index `2`: `4 > 1 and 4 > 2`? yes
- index `3`: `2 > 4 and 2 > 1`? no
- index `4`: `1 > 2 and 1 > 5`? no

So:

```text
peak = [0,0,1,0,0,0]
```

---

## Query 1: [2,2,4]

Set:

```text
nums[2] = 4
```

It was already `4`, so nothing effectively changes.

Affected indices are `1,2,3`, but peak statuses remain same.

---

## Query 2: [1,0,2]

Need peaks in subarray:

```text
nums[0..2] = [4,1,4]
```

Only internal indices of this subarray can count, so only index `1`.

Check range:

```text
[0+1, 2-1] = [1,1]
```

`peak[1] = 0`, so answer is:

```text
0
```

---

## Query 3: [1,0,4]

Subarray:

```text
[4,1,4,2,1]
```

Internal indices are `1,2,3`.

Query range:

```text
[1,3]
```

Peak array on that range:

```text
[0,1,0]
```

Total = `1`.

Correct.

---

# 8. Why `l+1` to `r-1` Is the Right Query Range

This is one of the easiest places to make a mistake.

A peak inside subarray `nums[l..r]` must have **both neighbors also inside that subarray**.

So valid peak positions are:

```text
l < i < r
```

which means:

```text
i in [l+1, r-1]
```

Even if index `l` or `r` is a peak in the full array, it cannot count for that subarray because it lacks one neighbor _inside the subarray_.

---

# 9. Correctness Proof

## Lemma 1

For every index `i` with `1 <= i <= n-2`, `peak[i] = 1` iff `nums[i]` is a peak in the full array.

### Proof

By definition, `peak[i]` is set to 1 exactly when `nums[i] > nums[i-1]` and `nums[i] > nums[i+1]`, which is the definition of a peak. ∎

---

## Lemma 2

For a type-1 query `[1, l, r]`, the number of peaks in subarray `nums[l..r]` equals the sum of `peak[i]` over `i in [l+1, r-1]`.

### Proof

An index can be a peak in the subarray only if both its neighbors lie inside the subarray, so only indices `l+1` through `r-1` are eligible. For each such index, being a peak in the subarray is the same as being greater than its immediate left and right neighbors in the original array, which is exactly captured by `peak[i]`. ∎

---

## Lemma 3

After updating `nums[idx]`, only `peak[idx-1]`, `peak[idx]`, and `peak[idx+1]` can change.

### Proof

Whether index `i` is a peak depends only on `nums[i-1]`, `nums[i]`, and `nums[i+1]`. If `nums[idx]` changes, only those indices whose local neighborhood includes `idx` can be affected, namely `idx-1`, `idx`, and `idx+1`. ∎

---

## Theorem

The Fenwick-tree algorithm returns the correct answer for every query.

### Proof

By Lemma 3, updates correctly maintain the peak array by recomputing all and only affected positions. By Lemma 2, each type-1 query is exactly the range sum of the maintained peak array over `[l+1, r-1]`. Since the Fenwick tree maintains these sums correctly, every returned answer is correct. ∎

---

# 10. Common Mistakes

## Mistake 1: Counting endpoints of the query range

For query `[l, r]`, indices `l` and `r` cannot be peaks in that subarray.

## Mistake 2: Recomputing the whole peak array after every update

Only 3 positions can change.

## Mistake 3: Forgetting boundary checks

Indices `0` and `n-1` are never peaks in the full array.

## Mistake 4: Querying `[l, r]` instead of `[l+1, r-1]`

This is the most common logical bug.

## Mistake 5: Mixing up “peak in full array” and “peak in subarray”

They are equivalent only for interior indices of the subarray.

---

# 11. Comparison of Approaches

| Approach                        |               Time |  Space | Notes                      |
| ------------------------------- | -----------------: | -----: | -------------------------- |
| Recompute on every type-1 query |            `O(nq)` | `O(1)` | Too slow                   |
| Maintain peak[] but scan ranges | `O(nq)` worst-case | `O(n)` | Updates fast, queries slow |
| Fenwick tree over peak[]        |   `O((n+q)\log n)` | `O(n)` | Best practical solution    |
| Segment tree over peak[]        |   `O((n+q)\log n)` | `O(n)` | Also valid                 |

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
            index++; // 0-based to 1-based
            while (index <= n) {
                bit[index] += delta;
                index += index & -index;
            }
        }

        int sum(int index) {
            index++;
            int res = 0;
            while (index > 0) {
                res += bit[index];
                index -= index & -index;
            }
            return res;
        }

        int rangeSum(int left, int right) {
            if (left > right) return 0;
            return sum(right) - (left == 0 ? 0 : sum(left - 1));
        }
    }

    public List<Integer> countOfPeaks(int[] nums, int[][] queries) {
        int n = nums.length;
        int[] peak = new int[n];
        Fenwick bit = new Fenwick(n);

        for (int i = 1; i <= n - 2; i++) {
            peak[i] = isPeak(nums, i) ? 1 : 0;
            if (peak[i] == 1) {
                bit.add(i, 1);
            }
        }

        List<Integer> ans = new ArrayList<>();

        for (int[] q : queries) {
            if (q[0] == 1) {
                int l = q[1];
                int r = q[2];
                ans.add(bit.rangeSum(l + 1, r - 1));
            } else {
                int idx = q[1];
                int val = q[2];

                for (int i = idx - 1; i <= idx + 1; i++) {
                    if (i >= 1 && i <= n - 2 && peak[i] == 1) {
                        bit.add(i, -1);
                        peak[i] = 0;
                    }
                }

                nums[idx] = val;

                for (int i = idx - 1; i <= idx + 1; i++) {
                    if (i >= 1 && i <= n - 2) {
                        peak[i] = isPeak(nums, i) ? 1 : 0;
                        if (peak[i] == 1) {
                            bit.add(i, 1);
                        }
                    }
                }
            }
        }

        return ans;
    }

    private boolean isPeak(int[] nums, int i) {
        return nums[i] > nums[i - 1] && nums[i] > nums[i + 1];
    }
}
```

---

# 13. Interview Summary

Create a binary array `peak[]` where `peak[i] = 1` if index `i` is a peak.

Then:

- query `[l, r]` becomes sum of `peak[l+1..r-1]`
- update at index `idx` only affects peak status at:
  ```text
  idx-1, idx, idx+1
  ```

So the problem becomes a dynamic range-sum problem over a mostly static binary array, which is solved cleanly with a Fenwick Tree in `O((n+q) log n)`.
