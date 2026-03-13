# 2519. Count the Number of K-Big Indices

## Problem Restatement

We are given:

- a 0-indexed integer array `nums`
- a positive integer `k`

An index `i` is called **k-big** if both of the following are true:

1. there are at least `k` indices to the **left** of `i` whose values are **strictly smaller** than `nums[i]`
2. there are at least `k` indices to the **right** of `i` whose values are **strictly smaller** than `nums[i]`

We need to return the number of such indices.

---

## Key Observation

For every index `i`, we really want two quantities:

- `leftSmaller[i]` = how many values before `i` are `< nums[i]`
- `rightSmaller[i]` = how many values after `i` are `< nums[i]`

Then:

```text
i is k-big  iff  leftSmaller[i] >= k and rightSmaller[i] >= k
```

So the whole problem reduces to efficiently computing “how many earlier/later values are smaller than the current value.”

This is a classic order-statistics / prefix-frequency problem.

Because:

```text
nums.length <= 10^5
```

a quadratic scan is too slow, so we need a better structure such as:

- Fenwick Tree / Binary Indexed Tree
- Segment Tree
- balanced ordered structure with coordinate compression

---

# Approach 1 — Brute Force Counting on Both Sides

## Intuition

The most direct idea is:

For every index `i`:

- scan left and count values smaller than `nums[i]`
- scan right and count values smaller than `nums[i]`

If both counts are at least `k`, increment the answer.

This is easy to understand, but too slow for the full constraints.

---

## Java Code

```java
class Solution {
    public int kBigIndices(int[] nums, int k) {
        int n = nums.length;
        int ans = 0;

        for (int i = 0; i < n; i++) {
            int left = 0;
            int right = 0;

            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    left++;
                }
            }

            for (int j = i + 1; j < n; j++) {
                if (nums[j] < nums[i]) {
                    right++;
                }
            }

            if (left >= k && right >= k) {
                ans++;
            }
        }

        return ans;
    }
}
```

---

## Complexity Analysis

### Time Complexity

For each index, we scan the whole array around it:

```text
O(n^2)
```

### Space Complexity

```text
O(1)
```

This is not feasible for `n = 10^5`.

---

# Approach 2 — Fenwick Tree with Coordinate Compression

## Intuition

We want counts of smaller values on the left and on the right.

A Fenwick Tree can maintain frequencies of values we have seen so far.

For an index `i`:

- when scanning left to right, querying the Fenwick Tree for values `< nums[i]` gives `leftSmaller[i]`
- when scanning right to left, doing the same gives `rightSmaller[i]`

Because values in `nums` can be as large as `n`, but not necessarily bounded tightly enough for direct safe indexing in all generalized settings, coordinate compression is a clean way to map values to ranks.

---

## Steps

1. Coordinate-compress all values in `nums`
2. Left pass:
   - query how many seen values have rank smaller than current rank
   - store in `leftSmaller[i]`
   - update current rank in Fenwick Tree
3. Right pass:
   - reset Fenwick Tree
   - query how many seen values to the right have rank smaller than current rank
   - store in `rightSmaller[i]`
4. Count indices where both counts are at least `k`

---

## Java Code

```java
import java.util.*;

class Solution {
    public int kBigIndices(int[] nums, int k) {
        int n = nums.length;
        int[] ranks = compress(nums);

        int[] leftSmaller = new int[n];
        int[] rightSmaller = new int[n];

        int maxRank = 0;
        for (int r : ranks) {
            maxRank = Math.max(maxRank, r);
        }

        Fenwick bit = new Fenwick(maxRank);

        // left to right
        for (int i = 0; i < n; i++) {
            leftSmaller[i] = bit.query(ranks[i] - 1);
            bit.add(ranks[i], 1);
        }

        bit = new Fenwick(maxRank);

        // right to left
        for (int i = n - 1; i >= 0; i--) {
            rightSmaller[i] = bit.query(ranks[i] - 1);
            bit.add(ranks[i], 1);
        }

        int ans = 0;
        for (int i = 0; i < n; i++) {
            if (leftSmaller[i] >= k && rightSmaller[i] >= k) {
                ans++;
            }
        }

        return ans;
    }

    private int[] compress(int[] nums) {
        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        Map<Integer, Integer> rank = new HashMap<>();
        int r = 1;
        for (int x : sorted) {
            if (!rank.containsKey(x)) {
                rank.put(x, r++);
            }
        }

        int[] res = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            res[i] = rank.get(nums[i]);
        }
        return res;
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

- compression: `O(n log n)`
- left Fenwick pass: `O(n log n)`
- right Fenwick pass: `O(n log n)`

Overall:

```text
O(n log n)
```

### Space Complexity

```text
O(n)
```

This is a standard strong solution.

---

# Approach 3 — Fenwick Tree Without Explicit Left/Right Arrays

## Intuition

We can slightly optimize memory.

Instead of storing both `leftSmaller` and `rightSmaller` fully, we can:

1. compute which indices satisfy the left condition and store a boolean flag
2. scan from the right and immediately combine with the left flag

This reduces memory usage a little while keeping the same complexity.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int kBigIndices(int[] nums, int k) {
        int n = nums.length;
        int[] ranks = compress(nums);

        int maxRank = 0;
        for (int r : ranks) {
            maxRank = Math.max(maxRank, r);
        }

        boolean[] goodLeft = new boolean[n];
        Fenwick bit = new Fenwick(maxRank);

        for (int i = 0; i < n; i++) {
            int left = bit.query(ranks[i] - 1);
            goodLeft[i] = left >= k;
            bit.add(ranks[i], 1);
        }

        bit = new Fenwick(maxRank);
        int ans = 0;

        for (int i = n - 1; i >= 0; i--) {
            int right = bit.query(ranks[i] - 1);
            if (goodLeft[i] && right >= k) {
                ans++;
            }
            bit.add(ranks[i], 1);
        }

        return ans;
    }

    private int[] compress(int[] nums) {
        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        Map<Integer, Integer> rank = new HashMap<>();
        int r = 1;
        for (int x : sorted) {
            if (!rank.containsKey(x)) {
                rank.put(x, r++);
            }
        }

        int[] res = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            res[i] = rank.get(nums[i]);
        }
        return res;
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

```text
O(n log n)
```

### Space Complexity

```text
O(n)
```

This is usually the most practical Fenwick implementation.

---

# Approach 4 — Segment Tree

## Intuition

A Segment Tree can replace the Fenwick Tree.

We still compress values into ranks.

For each index:

- query the count in rank range `[1, rank(nums[i]) - 1]`
- update the frequency at `rank(nums[i])`

Do this from left to right and from right to left.

This is asymptotically the same as Fenwick Tree, but more verbose.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int kBigIndices(int[] nums, int k) {
        int n = nums.length;
        int[] ranks = compress(nums);

        int maxRank = 0;
        for (int r : ranks) {
            maxRank = Math.max(maxRank, r);
        }

        int[] left = new int[n];
        int[] right = new int[n];

        SegmentTree seg = new SegmentTree(maxRank);

        for (int i = 0; i < n; i++) {
            left[i] = seg.query(1, ranks[i] - 1);
            seg.update(ranks[i], 1);
        }

        seg = new SegmentTree(maxRank);

        for (int i = n - 1; i >= 0; i--) {
            right[i] = seg.query(1, ranks[i] - 1);
            seg.update(ranks[i], 1);
        }

        int ans = 0;
        for (int i = 0; i < n; i++) {
            if (left[i] >= k && right[i] >= k) {
                ans++;
            }
        }

        return ans;
    }

    private int[] compress(int[] nums) {
        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        Map<Integer, Integer> rank = new HashMap<>();
        int r = 1;
        for (int x : sorted) {
            if (!rank.containsKey(x)) {
                rank.put(x, r++);
            }
        }

        int[] res = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            res[i] = rank.get(nums[i]);
        }
        return res;
    }

    static class SegmentTree {
        int[] tree;
        int n;

        SegmentTree(int n) {
            this.n = n;
            this.tree = new int[4 * n];
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
            if (index <= mid) {
                update(node * 2, left, mid, index, delta);
            } else {
                update(node * 2 + 1, mid + 1, right, index, delta);
            }
            tree[node] = tree[node * 2] + tree[node * 2 + 1];
        }

        int query(int ql, int qr) {
            if (ql > qr) return 0;
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

# Worked Example

## Example 1

```text
nums = [2,3,6,5,2,3]
k = 2
```

We count smaller values on each side.

### Index 2, value 6

Left side:

```text
[2,3]
```

Both are smaller than 6, so left count = 2.

Right side:

```text
[5,2,3]
```

All three are smaller than 6, so right count = 3.

So index 2 is 2-big.

### Index 3, value 5

Left side:

```text
[2,3,6]
```

Smaller than 5 are `2,3`, so left count = 2.

Right side:

```text
[2,3]
```

Both are smaller, so right count = 2.

So index 3 is also 2-big.

No other index qualifies.

Answer:

```text
2
```

---

# Why the Fenwick solution is correct

## Claim 1

During the left-to-right pass, `query(rank(nums[i]) - 1)` equals the number of earlier values strictly smaller than `nums[i]`.

### Reason

The Fenwick Tree stores frequencies of values already seen.

By querying ranks strictly below the current rank, we count exactly how many earlier values are smaller.

---

## Claim 2

During the right-to-left pass, `query(rank(nums[i]) - 1)` equals the number of later values strictly smaller than `nums[i]`.

### Reason

Now the Fenwick Tree stores frequencies of values to the right of `i`.

Again, querying ranks strictly below the current rank gives exactly the number of smaller later values.

---

## Claim 3

An index is k-big iff both of those counts are at least `k`.

### Reason

That is exactly the problem definition.

So once both counts are computed correctly, the final answer is correct.

---

# Comparison of Approaches

## Approach 1 — Brute force

Pros:

- easiest to understand
- direct implementation of definition

Cons:

- too slow

---

## Approach 2 — Fenwick + left/right arrays

Pros:

- standard
- efficient
- easy to reason about

Cons:

- uses extra arrays

---

## Approach 3 — Fenwick + one boolean side

Pros:

- same efficiency
- slightly cleaner memory usage

Cons:

- only a mild optimization

This is the recommended implementation.

---

## Approach 4 — Segment Tree

Pros:

- equally valid
- useful if you prefer segment trees

Cons:

- more verbose than Fenwick Tree

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    public int kBigIndices(int[] nums, int k) {
        int n = nums.length;
        int[] ranks = compress(nums);

        int maxRank = 0;
        for (int r : ranks) {
            maxRank = Math.max(maxRank, r);
        }

        boolean[] goodLeft = new boolean[n];
        Fenwick bit = new Fenwick(maxRank);

        for (int i = 0; i < n; i++) {
            int left = bit.query(ranks[i] - 1);
            goodLeft[i] = left >= k;
            bit.add(ranks[i], 1);
        }

        bit = new Fenwick(maxRank);
        int ans = 0;

        for (int i = n - 1; i >= 0; i--) {
            int right = bit.query(ranks[i] - 1);
            if (goodLeft[i] && right >= k) {
                ans++;
            }
            bit.add(ranks[i], 1);
        }

        return ans;
    }

    private int[] compress(int[] nums) {
        int[] sorted = nums.clone();
        Arrays.sort(sorted);

        Map<Integer, Integer> rank = new HashMap<>();
        int r = 1;
        for (int x : sorted) {
            if (!rank.containsKey(x)) {
                rank.put(x, r++);
            }
        }

        int[] res = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            res[i] = rank.get(nums[i]);
        }
        return res;
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

Let `n = nums.length`.

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

The problem reduces to counting, for each index, how many smaller elements exist:

- on the left
- on the right

That is exactly what a Fenwick Tree is good at after coordinate compression.

So the clean efficient strategy is:

1. compress values
2. scan left to right for left-smaller counts
3. scan right to left for right-smaller counts
4. count indices where both sides have at least `k`
