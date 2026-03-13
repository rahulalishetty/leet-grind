# 3605. Minimum Stability Factor of Array — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int minStable(int[] nums, int maxC) {

    }
}
```

---

# Problem Restatement

We are given:

- an integer array `nums`
- an integer `maxC`

A subarray is **stable** if its HCF / GCD is at least `2`.

The **stability factor** of the whole array is the length of its **longest stable subarray**.

We may modify at most `maxC` elements to **any integers we want**.

We need the minimum possible stability factor after those modifications.

If no stable subarray remains, return:

```text
0
```

---

# Core Insight

A subarray has GCD at least `2` iff all its elements share **some prime factor**.

So a stable subarray of length `L` exists iff there is some prime `p` dividing **every element** in that subarray.

That means if we want to make the final stability factor **strictly less than `L`**, then for **every** window of length `L`, we must ensure that window is **not stable**.

Equivalently:

> In every window of length `L`, at least one element must be changed so that the window is broken.

This turns the problem into a hitting / covering problem on bad windows.

---

# Why Arbitrary Modification Helps So Much

When we modify an element, we may change it to **any integer**.

So we can simply change it to `1`.

That guarantees it contributes no prime factor at all, and therefore any stable subarray containing it is destroyed.

So each modification acts like placing a **blocker** at some position.

Thus the question becomes:

For a candidate length `L`, how many blockers are needed so that **every stable subarray of length at least `L`** is destroyed?

A simpler equivalent form is:

- every stable subarray of length `L` must contain at least one modified position

because any longer stable subarray contains many length-`L` subarrays.

So we only need to consider stable windows of **exactly** length `L`.

---

# Binary Search on the Answer

The final answer is the minimum possible stability factor.

If we can achieve stability factor `< L`, then we can also achieve stability factor `< L+1`.

So feasibility is monotone.

That suggests binary search on the answer.

A common formulation:

- check whether we can make **all stable subarrays of length `mid + 1` disappear**
- if yes, then answer `<= mid`
- otherwise answer `> mid`

This yields a binary search over the final stability factor.

---

# Key Remaining Subproblem

For a fixed window length `len`, we need to know:

1. which windows of size `len` are stable
2. the minimum number of positions needed to hit all such windows

The second part is a classic greedy interval stabbing problem:

- each bad window `[l, r]` must contain at least one modified index
- choose the rightmost possible point greedily

So the hard part is efficiently recognizing stable windows.

---

# Stable Window Criterion

A window is stable iff its GCD is at least `2`.

So for each fixed window length `len`, we need to detect windows whose GCD is `>= 2`.

This can be done with a sparse table or segment tree for range GCD queries.

Then every window query is `O(1)` or `O(log n)`.

After listing all bad windows, greedy stabbing gives the minimum number of changes required.

---

# Approach 1 — Binary Search + Sparse Table GCD + Greedy Interval Stabbing (Recommended)

## Idea

### Step 1

Build a sparse table for range GCD queries.

### Step 2

Binary search the final stability factor `ans`.

To check if we can make the final stability factor at most `x`, we must destroy all stable subarrays of length:

```text
x + 1
```

### Step 3

Enumerate every window `[i, i + len - 1]` of that length.

If its GCD is `>= 2`, it is a bad window.

### Step 4

Use greedy stabbing on the bad windows:

- process windows left to right
- if the current chosen modification point does not lie inside this window, place a new modification at the window’s right endpoint

This yields the minimum number of changes needed to break all bad windows of this length.

If that number is `<= maxC`, then the candidate answer is feasible.

---

## Why greedy stabbing is correct

For intervals on a line, choosing the right endpoint of the earliest uncovered interval is optimal.

That is the standard minimum-points-to-hit-all-intervals greedy argument.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minStable(int[] nums, int maxC) {
        int n = nums.length;
        SparseGCD st = new SparseGCD(nums);

        int left = 0, right = n; // answer in [0, n]
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (canAchieve(nums, maxC, mid, st)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    private boolean canAchieve(int[] nums, int maxC, int targetStable, SparseGCD st) {
        int n = nums.length;

        if (targetStable == n) return true;

        int len = targetStable + 1;
        if (len > n) return true;

        int used = 0;
        int lastChosen = -1;

        for (int l = 0; l + len - 1 < n; l++) {
            int r = l + len - 1;
            int g = st.query(l, r);

            if (g >= 2) {
                if (lastChosen < l) {
                    used++;
                    lastChosen = r;
                    if (used > maxC) return false;
                }
            }
        }

        return true;
    }

    static class SparseGCD {
        int[][] st;
        int[] log2;

        SparseGCD(int[] nums) {
            int n = nums.length;
            log2 = new int[n + 1];
            for (int i = 2; i <= n; i++) {
                log2[i] = log2[i / 2] + 1;
            }

            int K = log2[n] + 1;
            st = new int[K][n];
            System.arraycopy(nums, 0, st[0], 0, n);

            for (int k = 1; k < K; k++) {
                int len = 1 << k;
                int half = len >> 1;
                for (int i = 0; i + len <= n; i++) {
                    st[k][i] = gcd(st[k - 1][i], st[k - 1][i + half]);
                }
            }
        }

        int query(int l, int r) {
            int k = log2[r - l + 1];
            return gcd(st[k][l], st[k][r - (1 << k) + 1]);
        }

        private int gcd(int a, int b) {
            while (b != 0) {
                int t = a % b;
                a = b;
                b = t;
            }
            return a;
        }
    }
}
```

---

## Complexity

Let `n = nums.length`.

- Sparse table build: `O(n log n)`
- Each feasibility check scans all windows: `O(n)`
- Binary search over answer: `O(log n)`

So total:

```text
Time:  O(n log n)
Space: O(n log n)
```

This fits `n <= 10^5`.

---

# Approach 2 — Binary Search + Segment Tree for GCD + Greedy Stabbing

## Idea

Instead of a sparse table, we can use a segment tree for range GCD queries.

The rest stays the same:

- binary search answer
- for each candidate length, detect bad windows
- greedily stab them

This is slightly slower than the sparse-table version because each GCD query becomes `O(log n)` instead of `O(1)`.

---

## Java Code

```java
class Solution {
    public int minStable(int[] nums, int maxC) {
        SegmentTree seg = new SegmentTree(nums);
        int n = nums.length;

        int left = 0, right = n;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (can(nums.length, maxC, mid, seg)) right = mid;
            else left = mid + 1;
        }
        return left;
    }

    private boolean can(int n, int maxC, int targetStable, SegmentTree seg) {
        int len = targetStable + 1;
        if (len > n) return true;

        int used = 0;
        int lastChosen = -1;

        for (int l = 0; l + len - 1 < n; l++) {
            int r = l + len - 1;
            if (seg.query(l, r) >= 2) {
                if (lastChosen < l) {
                    used++;
                    lastChosen = r;
                    if (used > maxC) return false;
                }
            }
        }

        return true;
    }

    static class SegmentTree {
        int n;
        int[] tree;

        SegmentTree(int[] nums) {
            n = nums.length;
            tree = new int[4 * n];
            build(1, 0, n - 1, nums);
        }

        private void build(int node, int l, int r, int[] nums) {
            if (l == r) {
                tree[node] = nums[l];
                return;
            }
            int mid = (l + r) >>> 1;
            build(node << 1, l, mid, nums);
            build(node << 1 | 1, mid + 1, r, nums);
            tree[node] = gcd(tree[node << 1], tree[node << 1 | 1]);
        }

        int query(int ql, int qr) {
            return query(1, 0, n - 1, ql, qr);
        }

        private int query(int node, int l, int r, int ql, int qr) {
            if (ql <= l && r <= qr) return tree[node];
            int mid = (l + r) >>> 1;
            if (qr <= mid) return query(node << 1, l, mid, ql, qr);
            if (ql > mid) return query(node << 1 | 1, mid + 1, r, ql, qr);
            return gcd(query(node << 1, l, mid, ql, qr),
                       query(node << 1 | 1, mid + 1, r, ql, qr));
        }

        private int gcd(int a, int b) {
            while (b != 0) {
                int t = a % b;
                a = b;
                b = t;
            }
            return a;
        }
    }
}
```

---

## Complexity

- Segment tree build: `O(n)`
- Each feasibility check: `O(n log n)`
- Binary search: `O(log n)`

Total:

```text
Time:  O(n log^2 n)
Space: O(n)
```

This is also acceptable, but the sparse-table version is cleaner and faster.

---

# Approach 3 — Brute Force Recompute All Subarray GCDs After Every Modification Pattern (Impossible)

## Idea

One could imagine trying all subsets of positions to modify, then computing the longest stable subarray after each choice.

This is completely infeasible because the number of modification choices is exponential.

So this is only a conceptual baseline.

---

# Why Hitting Length-(L) Windows Is Sufficient

Suppose after modifications there is a stable subarray of length greater than `L`.

Then it contains some stable subarray of length exactly `L + 1` as a subarray.

So if we destroy **all** stable windows of length `L + 1`, then automatically no longer stable subarray can remain either.

This is why the binary-search feasibility check only has to consider fixed-size windows.

That is a crucial simplification.

---

# Example Walkthrough

## Example 1

```text
nums = [3,5,10], maxC = 1
```

Try to achieve final stability factor `1`.

That means destroy all stable windows of length `2`.

Length-2 windows:

- `[3,5]` -> gcd = 1, not stable
- `[5,10]` -> gcd = 5, stable

So only interval `[1,2]` is bad.

One modification inside it is enough, for example change `5 -> 7`.

Then no length-2 stable window remains, so answer `1` is feasible.

Hence minimum is `1`.

---

## Example 2

```text
nums = [2,6,8], maxC = 2
```

Try answer `1`.

Need to destroy all length-2 stable windows:

- `[2,6]` -> gcd = 2
- `[6,8]` -> gcd = 2

Greedy stabbing:

- stab `[0,1]` at position `1`
- that also hits `[1,2]`

But there is also the length-3 stable window. Destroying all length-2 windows already prevents any longer stable subarray from surviving.

Actually with two changes, certainly feasible.

Thus minimum is `1`.

---

## Example 3

```text
nums = [2,4,9,6], maxC = 1
```

Try answer `1`.

Need to destroy all length-2 stable windows:

- `[2,4]` -> gcd = 2
- `[4,9]` -> gcd = 1
- `[9,6]` -> gcd = 3

Bad intervals are:

```text
[0,1] and [2,3]
```

These are disjoint, so one stabbed position cannot hit both.

Need at least 2 modifications, but `maxC = 1`.

So answer `1` is not feasible.

Try answer `2`.

Need to destroy all length-3 stable windows:

- `[2,4,9]` -> gcd = 1
- `[4,9,6]` -> gcd = 1

No bad windows, so feasible.

Therefore minimum stability factor is `2`.

---

# Important Correctness Argument

The reduction has two parts:

### Part 1

A stable subarray means gcd `>= 2`.

That is exactly what range-GCD queries detect.

### Part 2

To ensure final stability factor is at most `x`, it is necessary and sufficient to break all stable subarrays of length `x+1`.

- Necessary: any surviving stable subarray of length `x+1` already violates the target.
- Sufficient: any stable subarray longer than `x` contains a stable subarray of length `x+1`.

Once reduced to intervals (bad windows), minimum modifications equal the minimum hitting set for intervals on a line, which greedy solves optimally.

So the full algorithm is correct.

---

# Common Pitfalls

## 1. Forgetting that subarrays of length 1 can still be stable

If an element is `>= 2`, then `[nums[i]]` is stable.

That is why answer can be `0` only if all remaining elements can be changed to `1`.

---

## 2. Trying to optimize actual modified values

You never need anything fancy. Changing a chosen element to `1` is always enough to destroy stability.

---

## 3. Thinking longer windows must be checked separately

They do not. Checking windows of one fixed length during feasibility is sufficient.

---

## 4. Using naive gcd recomputation per window

That will be too slow without a sparse table / segment tree.

---

# Best Approach

## Recommended: Binary search answer + sparse table GCD + greedy interval stabbing

This is the cleanest efficient solution because:

- feasibility is monotone
- range GCD can be answered quickly
- interval stabbing has a standard greedy optimum

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    public int minStable(int[] nums, int maxC) {
        int n = nums.length;
        SparseGCD st = new SparseGCD(nums);

        int left = 0, right = n;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (canAchieve(nums, maxC, mid, st)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    private boolean canAchieve(int[] nums, int maxC, int targetStable, SparseGCD st) {
        int n = nums.length;
        if (targetStable == n) return true;

        int len = targetStable + 1;
        if (len > n) return true;

        int used = 0;
        int lastChosen = -1;

        for (int l = 0; l + len - 1 < n; l++) {
            int r = l + len - 1;
            if (st.query(l, r) >= 2) {
                if (lastChosen < l) {
                    used++;
                    lastChosen = r;
                    if (used > maxC) return false;
                }
            }
        }

        return true;
    }

    static class SparseGCD {
        int[][] st;
        int[] log2;

        SparseGCD(int[] nums) {
            int n = nums.length;
            log2 = new int[n + 1];
            for (int i = 2; i <= n; i++) {
                log2[i] = log2[i / 2] + 1;
            }

            int K = log2[n] + 1;
            st = new int[K][n];
            System.arraycopy(nums, 0, st[0], 0, n);

            for (int k = 1; k < K; k++) {
                int half = 1 << (k - 1);
                int len = 1 << k;
                for (int i = 0; i + len <= n; i++) {
                    st[k][i] = gcd(st[k - 1][i], st[k - 1][i + half]);
                }
            }
        }

        int query(int l, int r) {
            int k = log2[r - l + 1];
            return gcd(st[k][l], st[k][r - (1 << k) + 1]);
        }

        static int gcd(int a, int b) {
            while (b != 0) {
                int t = a % b;
                a = b;
                b = t;
            }
            return a;
        }
    }
}
```

---

# Complexity Summary

```text
Time:  O(n log n)
Space: O(n log n)
```

This fits the constraints:

```text
n <= 10^5
```

---

# Final Takeaway

The critical simplification is:

- a modification can simply turn an element into `1`
- therefore each modification acts as a blocker
- to force the longest stable subarray below a threshold, we only need to hit all stable windows of one fixed length
- that becomes a binary-search + range-GCD + interval-stabbing problem
