# 2659. Make Array Empty — Exhaustive Java Notes

## Problem Statement

You are given an integer array `nums` containing **distinct** values.

You repeatedly perform one of these operations until the array becomes empty:

1. If the **first element is the smallest** among all current elements, remove it.
2. Otherwise, move the first element to the end of the array.

Return the **number of operations** needed to make the array empty.

---

## Examples

### Example 1

```text
Input: nums = [3,4,-1]
Output: 5
```

Process:

| Operation | Array      |
| --------: | ---------- |
|         1 | [4, -1, 3] |
|         2 | [-1, 3, 4] |
|         3 | [3, 4]     |
|         4 | [4]        |
|         5 | []         |

### Example 2

```text
Input: nums = [1,2,4,3]
Output: 5
```

Process:

| Operation | Array     |
| --------: | --------- |
|         1 | [2, 4, 3] |
|         2 | [4, 3]    |
|         3 | [3, 4]    |
|         4 | [4]       |
|         5 | []        |

### Example 3

```text
Input: nums = [1,2,3]
Output: 3
```

Process:

| Operation | Array  |
| --------: | ------ |
|         1 | [2, 3] |
|         2 | [3]    |
|         3 | []     |

---

## Constraints

- `1 <= nums.length <= 10^5`
- `-10^9 <= nums[i] <= 10^9`
- All values in `nums` are distinct

---

# 1. Key Insight

Because all values are distinct, the removals must happen in **increasing value order**.

So instead of thinking:

> “At each step, what is the current minimum?”

we can think:

> “Remove elements in sorted-by-value order, and count how many rotations are needed to bring each one to the front.”

That is the whole problem.

---

# 2. Important Observation About Rotations

Suppose we are about to remove the next smallest element.

- If its current position is **after** the previous removed element in circular order, we can continue moving forward.
- If its current position is **before** the previous removed element, we must wrap around the array once.

This wrap-around is exactly where extra rotations happen.

So the hard part is:

- tracking which indices are still alive
- counting how many alive indices lie between two positions in circular order

This naturally suggests a data structure such as:

- **Fenwick Tree / Binary Indexed Tree**
- **Segment Tree**

---

# 3. Approach 1 — Brute Force Simulation

## Idea

Simulate the process exactly as written:

- maintain the current array
- find the minimum
- if front is minimum, remove it
- otherwise rotate front to back

## Why it is correct

It literally follows the problem statement.

## Why it is too slow

Each step may require:

- finding the minimum: `O(n)`
- rotating/removing in a list/deque

In the worst case, the number of operations can itself be `O(n^2)`, so this approach is far too slow for `n = 10^5`.

## Java Code

```java
import java.util.*;

class SolutionBruteForce {
    public long countOperationsToEmptyArray(int[] nums) {
        LinkedList<Integer> list = new LinkedList<>();
        for (int x : nums) list.add(x);

        long operations = 0;

        while (!list.isEmpty()) {
            int minVal = Integer.MAX_VALUE;
            for (int x : list) {
                minVal = Math.min(minVal, x);
            }

            if (list.getFirst() == minVal) {
                list.removeFirst();
            } else {
                list.addLast(list.removeFirst());
            }

            operations++;
        }

        return operations;
    }
}
```

## Complexity

- **Time:** `O(n^2)` or worse in practice
- **Space:** `O(n)`

## Verdict

Useful only for understanding or tiny inputs.

---

# 4. Approach 2 — Sort Indices + Fenwick Tree (Optimal)

This is the standard optimal solution.

## Core Idea

Let:

- `pairs[i] = (nums[i], i)`

Sort these pairs by value, because removal order is increasing value order.

Now suppose we remove elements in this sorted order of original indices:

```text
idx1, idx2, idx3, ...
```

At any moment, some indices are still alive and some are already removed.

We need to know how many alive elements we pass through when moving from the previous removed index to the next one in circular order.

That count equals the number of operations needed to bring the target to the front, including the final remove operation.

---

## 4.1 Fenwick Tree Meaning

We maintain a Fenwick Tree over indices `0..n-1`.

- `1` at index `i` means element `i` is still present
- `0` means it has already been removed

Initially every position is alive, so every entry is `1`.

Fenwick operations:

- `add(i, delta)` — mark index inserted/removed
- `sum(i)` — number of alive elements in `[0..i]`
- `rangeSum(l, r)` — number of alive elements in `[l..r]`

---

## 4.2 How many operations to remove the next index?

Assume the previously removed index was `prev`, and the next target index is `curr`.

### Case 1: `curr > prev`

We move forward without wrapping.

The number of alive elements from `prev+1` to `curr` is exactly the number of operations:

- each alive non-target element is rotated once
- the target is then removed once

So cost is:

```text
alive(prev+1 ... curr)
```

### Case 2: `curr < prev`

We must wrap around.

So cost is:

```text
alive(prev+1 ... n-1) + alive(0 ... curr)
```

### First removal

Before anything is removed, the front is index `0`.

So the cost to remove the first target index `curr` is simply:

```text
alive(0 ... curr)
```

Initially that is just `curr + 1`.

---

## 4.3 Walkthrough on Example 2

```text
nums = [1,2,4,3]
indices: 0 1 2 3
```

Sorted by value:

```text
1 -> index 0
2 -> index 1
3 -> index 3
4 -> index 2
```

Removal order of indices:

```text
0, 1, 3, 2
```

Initially alive set: `{0,1,2,3}`

### Remove index 0

Cost = alive(0..0) = 1
Operations so far = 1
Remove 0

Alive: `{1,2,3}`

### Remove index 1

Since `1 > 0`, cost = alive(1..1) = 1
Operations so far = 2
Remove 1

Alive: `{2,3}`

### Remove index 3

Since `3 > 1`, cost = alive(2..3) = 2
Operations so far = 4
Remove 3

Alive: `{2}`

### Remove index 2

Since `2 < 3`, wrap:

- alive(4..3) = 0
- alive(0..2) = 1

Cost = 1
Operations so far = 5

Answer = `5`

---

## 4.4 Correctness Intuition

The Fenwick Tree always knows which original indices are still present.

When moving circularly from the position right after the last removed element to the next target:

- every alive element encountered before the target contributes one rotation
- the target itself contributes one removal operation

So counting alive positions on that circular segment gives the exact operation count.

Because removals must happen in increasing value order, processing sorted indices is valid.

---

## 4.5 Java Code

```java
import java.util.*;

class Solution {
    static class Fenwick {
        int n;
        long[] bit;

        Fenwick(int n) {
            this.n = n;
            this.bit = new long[n + 1];
        }

        void add(int index, long delta) {
            index++; // convert to 1-based
            while (index <= n) {
                bit[index] += delta;
                index += index & -index;
            }
        }

        long sum(int index) {
            long res = 0;
            index++; // convert to 1-based
            while (index > 0) {
                res += bit[index];
                index -= index & -index;
            }
            return res;
        }

        long rangeSum(int left, int right) {
            if (left > right) return 0;
            return sum(right) - (left == 0 ? 0 : sum(left - 1));
        }
    }

    public long countOperationsToEmptyArray(int[] nums) {
        int n = nums.length;

        int[][] pairs = new int[n][2];
        for (int i = 0; i < n; i++) {
            pairs[i][0] = nums[i];
            pairs[i][1] = i;
        }

        Arrays.sort(pairs, Comparator.comparingInt(a -> a[0]));

        Fenwick fw = new Fenwick(n);
        for (int i = 0; i < n; i++) {
            fw.add(i, 1); // every index initially alive
        }

        long operations = 0;
        int prev = -1;

        for (int[] p : pairs) {
            int curr = p[1];

            if (prev == -1) {
                // first removal: count alive elements from 0 to curr
                operations += fw.rangeSum(0, curr);
            } else if (curr > prev) {
                operations += fw.rangeSum(prev + 1, curr);
            } else {
                operations += fw.rangeSum(prev + 1, n - 1);
                operations += fw.rangeSum(0, curr);
            }

            fw.add(curr, -1); // remove current index
            prev = curr;
        }

        return operations;
    }
}
```

---

## 4.6 Complexity

- Sorting: `O(n log n)`
- Fenwick updates: `n * O(log n)`
- Fenwick queries: `n * O(log n)`

So overall:

- **Time:** `O(n log n)`
- **Space:** `O(n)`

This easily fits the constraint `n <= 10^5`.

---

# 5. Approach 3 — Sort Indices + Segment Tree

This is conceptually the same as the Fenwick approach, but uses a Segment Tree instead.

## Idea

A segment tree also stores how many alive elements remain in every range.

So we can query:

- alive count in `[l..r]`
- mark an index as removed

Exactly the same logic applies.

## Why consider this approach?

Because sometimes:

- you are more comfortable with segment trees
- you want a structure that is easier to generalize to more complex range queries

For this problem, Fenwick is simpler and shorter, but segment tree is fully valid.

---

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

        void build(int node, int left, int right) {
            if (left == right) {
                tree[node] = 1;
                return;
            }
            int mid = left + (right - left) / 2;
            build(node * 2, left, mid);
            build(node * 2 + 1, mid + 1, right);
            tree[node] = tree[node * 2] + tree[node * 2 + 1];
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

        int rangeQuery(int l, int r) {
            if (l > r) return 0;
            return query(1, 0, n - 1, l, r);
        }

        void remove(int index) {
            update(1, 0, n - 1, index, 0);
        }
    }

    public long countOperationsToEmptyArray(int[] nums) {
        int n = nums.length;

        int[][] pairs = new int[n][2];
        for (int i = 0; i < n; i++) {
            pairs[i][0] = nums[i];
            pairs[i][1] = i;
        }

        Arrays.sort(pairs, Comparator.comparingInt(a -> a[0]));

        SegmentTree st = new SegmentTree(n);
        st.build(1, 0, n - 1);

        long operations = 0;
        int prev = -1;

        for (int[] p : pairs) {
            int curr = p[1];

            if (prev == -1) {
                operations += st.rangeQuery(0, curr);
            } else if (curr > prev) {
                operations += st.rangeQuery(prev + 1, curr);
            } else {
                operations += st.rangeQuery(prev + 1, n - 1);
                operations += st.rangeQuery(0, curr);
            }

            st.remove(curr);
            prev = curr;
        }

        return operations;
    }
}
```

---

## Complexity

- **Time:** `O(n log n)`
- **Space:** `O(n)`

---

# 6. Approach 4 — Ordered Set / Circular Traversal Intuition

There is another way to think about the problem:

- sort indices by value
- keep the alive indices in an ordered set
- detect whether moving from previous index to current index requires wrapping

This viewpoint is excellent for intuition, but in Java it is not enough by itself because we also need **how many alive elements** lie on a circular interval.

A plain `TreeSet<Integer>` can tell us the next larger/smaller index, but it cannot efficiently give:

- number of alive indices in a range

So in Java, a `TreeSet` alone is insufficient for an optimal implementation.

You still need an order-statistics structure such as:

- Fenwick Tree
- Segment Tree
- Balanced BST with subtree sizes (not available directly in Java standard library)

So this is more of a conceptual approach than a practical standard-library solution.

---

# 7. Why the Answer Can Be Larger Than `int`

The result can be roughly quadratic in the worst case.

For example, in a badly ordered array, we may do many rotations before many removals.

Since `n` can be `10^5`, the answer can exceed `2^31 - 1`.

So the return type must be:

```java
long
```

That is why the method signature is:

```java
public long countOperationsToEmptyArray(int[] nums)
```

---

# 8. Full Correctness Argument for Fenwick Approach

We now make the reasoning precise.

## Lemma 1

Elements are removed in increasing value order.

### Reason

At any time, the only removable element is the current minimum remaining element. Since all values are distinct, that minimum is unique. Therefore the global removal order is strictly increasing by value.

---

## Lemma 2

When removing the next target index `curr`, the number of operations needed equals the number of alive indices encountered when moving circularly from the position immediately after `prev` to `curr`, inclusive of `curr`.

### Reason

Every alive element before `curr` on that circular path must appear at the front before `curr`, so each causes one rotation. When `curr` reaches the front, one more operation removes it. Thus the total equals exactly the count of alive indices on that circular interval.

---

## Lemma 3

The Fenwick Tree correctly returns the number of alive indices on any interval.

### Reason

Each alive index stores `1`, each removed index stores `0`. Therefore the prefix/range sums equal the number of alive indices in the queried range.

---

## Lemma 4

For each removal step, the algorithm adds exactly the correct number of operations.

### Reason

From Lemma 2, the needed count is the number of alive indices on a circular segment. The algorithm computes:

- one straight interval if `curr > prev`
- two intervals (`prev+1..n-1` and `0..curr`) if wrapping is needed

By Lemma 3, these counts are exact.

---

## Theorem

The Fenwick Tree algorithm returns the exact number of operations needed to empty the array.

### Reason

By Lemma 1 it processes removals in the correct order. By Lemma 4 it adds the exact number of operations for each removal. Summing over all removals yields the exact total.

---

# 9. Dry Run in Detail

Let us dry-run the optimal method on:

```text
nums = [3, 4, -1]
```

Original indices:

```text
index: 0  1   2
value: 3  4  -1
```

Sorted by value:

```text
(-1, 2), (3, 0), (4, 1)
```

So removal order of indices is:

```text
2, 0, 1
```

Initially alive = `{0,1,2}`

---

## Step 1: remove index 2

This is the first removal.

Cost = alive in `[0..2]` = 3

Why?

- rotate index 0
- rotate index 1
- remove index 2

Operations = 3

Remove index 2.
Alive = `{0,1}`
`prev = 2`

---

## Step 2: remove index 0

Now `curr = 0 < prev = 2`, so we wrap.

Cost =

- alive in `[3..2]` = 0
- alive in `[0..0]` = 1

Total = 1

Operations = 4

Remove index 0.
Alive = `{1}`
`prev = 0`

---

## Step 3: remove index 1

Now `curr = 1 > prev = 0`

Cost = alive in `[1..1]` = 1

Operations = 5

Remove index 1.
Alive = `{}`

Final answer = `5`

Correct.

---

# 10. Common Mistakes

## Mistake 1: Simulating actual rotations for large input

That leads to `O(n^2)` behavior and will time out.

## Mistake 2: Forgetting that values are distinct

Distinctness is what makes removal order simply “sorted by value”.

## Mistake 3: Counting original positions instead of alive positions

After removals, gaps appear. You must count only indices still alive.

## Mistake 4: Using `int` for the answer

Use `long`.

## Mistake 5: Getting the wrap case wrong

When `curr < prev`, the interval is circular:

```text
(prev+1 ... n-1) + (0 ... curr)
```

not just one normal range.

---

# 11. Which Approach Should You Use?

## For interviews / contests

Use:

- **Approach 2: Fenwick Tree**

Why:

- shortest optimal code
- excellent performance
- elegant once understood

## If you prefer segment trees

Use:

- **Approach 3: Segment Tree**

Still optimal, just more verbose.

## For learning only

Start with:

- **Approach 1: Brute Force**

Then upgrade to Fenwick.

---

# 12. Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    static class Fenwick {
        int n;
        long[] bit;

        Fenwick(int n) {
            this.n = n;
            this.bit = new long[n + 1];
        }

        void add(int index, long delta) {
            index++;
            while (index <= n) {
                bit[index] += delta;
                index += index & -index;
            }
        }

        long sum(int index) {
            long res = 0;
            index++;
            while (index > 0) {
                res += bit[index];
                index -= index & -index;
            }
            return res;
        }

        long rangeSum(int left, int right) {
            if (left > right) return 0;
            return sum(right) - (left == 0 ? 0 : sum(left - 1));
        }
    }

    public long countOperationsToEmptyArray(int[] nums) {
        int n = nums.length;

        int[][] pairs = new int[n][2];
        for (int i = 0; i < n; i++) {
            pairs[i][0] = nums[i];
            pairs[i][1] = i;
        }

        Arrays.sort(pairs, Comparator.comparingInt(a -> a[0]));

        Fenwick fw = new Fenwick(n);
        for (int i = 0; i < n; i++) {
            fw.add(i, 1);
        }

        long operations = 0;
        int prev = -1;

        for (int[] pair : pairs) {
            int curr = pair[1];

            if (prev == -1) {
                operations += fw.rangeSum(0, curr);
            } else if (curr > prev) {
                operations += fw.rangeSum(prev + 1, curr);
            } else {
                operations += fw.rangeSum(prev + 1, n - 1);
                operations += fw.rangeSum(0, curr);
            }

            fw.add(curr, -1);
            prev = curr;
        }

        return operations;
    }
}
```

---

# 13. Quick Summary

- Removals happen in **increasing value order**
- So sort `(value, originalIndex)`
- Maintain which indices are still alive
- Count alive indices on the circular path from previous removed index to current one
- Use a **Fenwick Tree** or **Segment Tree**
- Final complexity: **`O(n log n)`**

---

# 14. Interview-Style One-Line Explanation

> Sort elements by value, then use a Fenwick Tree to count how many still-alive indices are crossed in circular order between consecutive removals; that total equals the number of operations.
