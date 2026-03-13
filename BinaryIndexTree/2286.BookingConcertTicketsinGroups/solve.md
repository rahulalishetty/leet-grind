# 2286. Booking Concert Tickets in Groups

## Problem Restatement

We need to design a class `BookMyShow` for a hall with:

- `n` rows
- `m` seats per row

Each row is numbered from `0` to `n - 1`, and seats inside a row are numbered from `0` to `m - 1`.

We must support two operations:

### `gather(k, maxRow)`

Allocate `k` **consecutive** seats in the **same row**, with row index at most `maxRow`.

Among all valid choices:

- choose the **smallest row**
- inside that row, choose the **smallest seat index**

Return:

```text
[row, startingSeat]
```

If impossible, return:

```text
[]
```

---

### `scatter(k, maxRow)`

Allocate `k` seats in rows `0..maxRow`.

The seats do **not** need to be consecutive, but allocation must still respect:

- smaller row numbers first
- smaller seat numbers first inside each row

Return:

- `true` if allocation succeeds
- `false` otherwise

---

## Key Difficulty

The constraints are large:

```text
n <= 5 * 10^4
m, k <= 10^9
at most 5 * 10^4 operations
```

A naive seat-by-seat simulation is impossible.

So the real problem is to support:

1. finding the first row up to `maxRow` with at least `k` consecutive seats left
2. checking whether rows `0..maxRow` contain at least `k` total seats left
3. updating rows after allocations

This strongly suggests a segment tree.

---

# Core Observations

## Observation 1: Consecutive seats in a row are always a suffix of free seats

Since bookings always take the **smallest seat numbers first**, each row is filled from left to right.

So if a row currently has `used[row]` seats already taken, then the free seats are exactly:

```text
[used[row], used[row] + 1, ..., m - 1]
```

That means:

- the number of consecutive seats still available in that row is simply:

```text
m - used[row]
```

We do **not** need a complicated interval structure per row.

---

## Observation 2: `gather` needs a row with enough remaining seats

For `gather(k, maxRow)`, we need the **smallest row `r <= maxRow`** such that:

```text
m - used[r] >= k
```

Once found:

- starting seat = `used[r]`
- then increase `used[r]` by `k`

So `gather` becomes:

> Find the first row in a prefix whose remaining capacity is at least `k`.

This is naturally supported by a segment tree storing the **maximum remaining seats** in each segment.

---

## Observation 3: `scatter` needs total remaining seats in a prefix

For `scatter(k, maxRow)`, we need to know whether:

```text
sum of remaining seats in rows [0..maxRow] >= k
```

If yes, allocate greedily from smallest row upward.

So `scatter` needs:

- fast prefix-sum query over remaining seats
- row updates
- and efficient advancement to the next row that still has free seats

This is naturally supported by a segment tree storing the **sum of remaining seats** in each segment.

---

## Therefore

A strong segment tree solution stores, for every segment:

- `sum` = total remaining seats
- `max` = maximum remaining seats in any row in the segment

This lets us solve both operations efficiently.

---

# Approach 1 — Naive Array Simulation

## Intuition

Store how many seats are used in each row.

For:

- `gather`: linearly scan rows `0..maxRow` to find the first row with enough seats
- `scatter`: linearly scan rows `0..maxRow` and allocate greedily

This is easy to understand, but too slow in the worst case.

---

## Java Code

```java
class BookMyShow {
    private final int n;
    private final int m;
    private final long[] used;

    public BookMyShow(int n, int m) {
        this.n = n;
        this.m = m;
        this.used = new long[n];
    }

    public int[] gather(int k, int maxRow) {
        for (int r = 0; r <= maxRow; r++) {
            long remaining = m - used[r];
            if (remaining >= k) {
                int startSeat = (int) used[r];
                used[r] += k;
                return new int[]{r, startSeat};
            }
        }
        return new int[0];
    }

    public boolean scatter(int k, int maxRow) {
        long need = k;

        for (int r = 0; r <= maxRow && need > 0; r++) {
            long remaining = m - used[r];
            long take = Math.min(remaining, need);
            used[r] += take;
            need -= take;
        }

        return need == 0;
    }
}
```

---

## Complexity Analysis

### Time Complexity

In the worst case:

- `gather`: `O(n)`
- `scatter`: `O(n)`

Across `5 * 10^4` operations, this can be too slow.

### Space Complexity

```text
O(n)
```

---

# Approach 2 — Segment Tree with Sum + Max

## Intuition

This is the standard optimal solution.

For each row, define:

```text
remain[row] = m - used[row]
```

We build a segment tree over `remain`.

Each node stores:

- `sum`: total remaining seats in that segment
- `max`: maximum remaining seats in that segment

Then:

### `gather(k, maxRow)`

- Check whether prefix `[0..maxRow]` contains a row with at least `k` seats:
  - query max on prefix
- If yes, descend the segment tree to find the **leftmost** such row
- Allocate there

### `scatter(k, maxRow)`

- Query total remaining seats in `[0..maxRow]`
- If less than `k`, return false
- Otherwise fill seats greedily from the smallest row upward

To make `scatter` efficient, keep a pointer `firstNonFullRow` indicating the smallest row that may still have free seats.

As rows become full, advance the pointer.

---

## Why this works

The greedy allocation rule says:

- use smaller row numbers first
- inside a row, smaller seat numbers first

Since rows fill left to right, once `scatter` starts from the smallest not-full row and keeps filling rows in order, it exactly follows the required policy.

---

## Java Code

```java
class BookMyShow {
    private final int n;
    private final int m;
    private final long[] sumTree;
    private final long[] maxTree;
    private final long[] used;
    private int firstNonFullRow;

    public BookMyShow(int n, int m) {
        this.n = n;
        this.m = m;
        this.sumTree = new long[4 * n];
        this.maxTree = new long[4 * n];
        this.used = new long[n];
        this.firstNonFullRow = 0;
        build(1, 0, n - 1);
    }

    private void build(int node, int left, int right) {
        if (left == right) {
            sumTree[node] = m;
            maxTree[node] = m;
            return;
        }
        int mid = left + (right - left) / 2;
        build(node * 2, left, mid);
        build(node * 2 + 1, mid + 1, right);
        pull(node);
    }

    private void pull(int node) {
        sumTree[node] = sumTree[node * 2] + sumTree[node * 2 + 1];
        maxTree[node] = Math.max(maxTree[node * 2], maxTree[node * 2 + 1]);
    }

    private void updateRow(int node, int left, int right, int index, long remain) {
        if (left == right) {
            sumTree[node] = remain;
            maxTree[node] = remain;
            return;
        }
        int mid = left + (right - left) / 2;
        if (index <= mid) {
            updateRow(node * 2, left, mid, index, remain);
        } else {
            updateRow(node * 2 + 1, mid + 1, right, index, remain);
        }
        pull(node);
    }

    private long querySum(int node, int left, int right, int ql, int qr) {
        if (ql <= left && right <= qr) return sumTree[node];
        if (right < ql || left > qr) return 0;

        int mid = left + (right - left) / 2;
        return querySum(node * 2, left, mid, ql, qr)
             + querySum(node * 2 + 1, mid + 1, right, ql, qr);
    }

    private long queryMax(int node, int left, int right, int ql, int qr) {
        if (ql <= left && right <= qr) return maxTree[node];
        if (right < ql || left > qr) return 0;

        int mid = left + (right - left) / 2;
        return Math.max(
            queryMax(node * 2, left, mid, ql, qr),
            queryMax(node * 2 + 1, mid + 1, right, ql, qr)
        );
    }

    private int findFirstRowWithAtLeastK(int node, int left, int right, int maxRow, int k) {
        if (left > maxRow || maxTree[node] < k) return -1;
        if (left == right) return left;

        int mid = left + (right - left) / 2;
        int res = findFirstRowWithAtLeastK(node * 2, left, mid, maxRow, k);
        if (res != -1) return res;
        return findFirstRowWithAtLeastK(node * 2 + 1, mid + 1, right, maxRow, k);
    }

    public int[] gather(int k, int maxRow) {
        if (queryMax(1, 0, n - 1, 0, maxRow) < k) {
            return new int[0];
        }

        int row = findFirstRowWithAtLeastK(1, 0, n - 1, maxRow, k);
        int startSeat = (int) used[row];

        used[row] += k;
        long remain = m - used[row];
        updateRow(1, 0, n - 1, row, remain);

        if (remain == 0 && row == firstNonFullRow) {
            while (firstNonFullRow < n && used[firstNonFullRow] == m) {
                firstNonFullRow++;
            }
        }

        return new int[]{row, startSeat};
    }

    public boolean scatter(int k, int maxRow) {
        long available = querySum(1, 0, n - 1, 0, maxRow);
        if (available < k) return false;

        long need = k;
        while (need > 0) {
            if (firstNonFullRow > maxRow) break;

            long remain = m - used[firstNonFullRow];
            long take = Math.min(remain, need);

            used[firstNonFullRow] += take;
            need -= take;

            long newRemain = m - used[firstNonFullRow];
            updateRow(1, 0, n - 1, firstNonFullRow, newRemain);

            if (used[firstNonFullRow] == m) {
                firstNonFullRow++;
            }
        }

        return true;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Each segment tree query/update takes:

```text
O(log n)
```

- `gather`: one max query + one descent + one update → `O(log n)`
- `scatter`: one sum query + several row fills

The crucial point: each row becomes full at most once, so the total number of pointer advances across all operations is `O(n)`.

Thus overall across all operations, the amortized complexity is very good.

### Space Complexity

```text
O(n)
```

for the segment tree and row usage.

---

# Approach 3 — Two Fenwick Trees Style Thinking (Why It Is Awkward)

## Intuition

One may try to use a Fenwick Tree for:

- total remaining seats
- maybe another structure for rows with enough seats

This works well for `scatter` because it mainly needs prefix sums of remaining seats.

However, `gather` is harder:

- it needs the **leftmost row** with at least `k` consecutive seats
- consecutive seats in a row equal remaining seats in that row
- so we need prefix-limited leftmost row by row maximum

Fenwick Trees are excellent for prefix sums, but much less natural for prefix maximum + leftmost-search.

So while a BIT-like solution can be forced, it is not the cleanest structure.

This is why segment tree is the standard answer.

---

# Approach 4 — Ordered Set of Non-Full Rows + Prefix Sum Structure

## Intuition

Another hybrid design is:

- maintain an ordered set of rows that are not full
- maintain a Fenwick/segment tree of total remaining seats

Then:

- `scatter` uses prefix sums for availability
- `gather` scans candidate rows from the ordered set until finding one with enough consecutive seats

This can work in practice, but worst-case `gather` may scan too many rows repeatedly.

So it is harder to guarantee clean worst-case performance than the pure segment tree solution.

---

# Worked Example

## Example

```text
n = 2, m = 5
```

Rows initially:

```text
row 0: 5 seats free
row 1: 5 seats free
```

### `gather(4, 0)`

Need 4 consecutive seats in row `<= 0`.

- row 0 has 5 free
- allocate seats `[0..3]`

Return:

```text
[0, 0]
```

Now:

```text
row 0 used = 4
row 1 used = 0
```

---

### `gather(2, 0)`

Need 2 consecutive seats in row `<= 0`.

- row 0 has only 1 seat free

Impossible.

Return:

```text
[]
```

---

### `scatter(5, 1)`

Need 5 total seats in rows `0..1`.

Available:

- row 0: 1
- row 1: 5

Total = 6, so possible.

Greedy allocation:

- row 0 seat 4
- row 1 seats 0..3

Return:

```text
true
```

Now only 1 seat remains.

---

### `scatter(5, 1)`

Need 5 seats but only 1 remains.

Return:

```text
false
```

---

# Why the Segment Tree Solution Is Correct

## Claim 1

A row’s remaining consecutive seats always equal its total remaining seats.

### Reason

Seats are always allocated from the smallest available seat number in each row.

So each row is filled left to right with no gaps.

Thus the free seats in a row always form one contiguous suffix.

So consecutive availability equals total remaining capacity.

---

## Claim 2

`gather` must choose the smallest row with remaining seats at least `k`.

### Reason

The rules explicitly require:

- smallest row number first
- smallest seat number in that row

Since rows fill left to right, if a row has at least `k` free seats, the valid block always starts at `used[row]`.

Therefore the problem reduces exactly to finding the smallest row whose remaining capacity is at least `k`.

The segment tree’s leftmost search returns exactly that row.

---

## Claim 3

`scatter` is correct when it fills from the smallest non-full row upward.

### Reason

The problem requires allocation in smallest row order, then smallest seat order inside each row.

Because each row fills left to right, greedily consuming rows from the smallest non-full row exactly matches the specification.

---

## Claim 4

If the prefix sum of remaining seats in `[0..maxRow]` is at least `k`, then `scatter` can always allocate successfully.

### Reason

Every remaining seat in that prefix is valid for scatter; adjacency is irrelevant.

So total remaining capacity being at least `k` is both necessary and sufficient.

---

# Comparison of Approaches

## Approach 1 — Naive row scan

Pros:

- easiest to understand

Cons:

- too slow in the worst case

---

## Approach 2 — Segment tree with sum + max

Pros:

- handles both gather and scatter cleanly
- optimal and standard
- directly matches the problem’s needs

Cons:

- more implementation detail

This is the recommended solution.

---

## Approach 3 — Fenwick-style thinking

Pros:

- useful for understanding scatter

Cons:

- awkward for gather

---

## Approach 4 — Ordered set + sum structure

Pros:

- can work in practice

Cons:

- harder to guarantee worst-case efficiency

---

# Final Recommended Java Solution

```java
class BookMyShow {
    private final int n;
    private final int m;
    private final long[] sumTree;
    private final long[] maxTree;
    private final long[] used;
    private int firstNonFullRow;

    public BookMyShow(int n, int m) {
        this.n = n;
        this.m = m;
        this.sumTree = new long[4 * n];
        this.maxTree = new long[4 * n];
        this.used = new long[n];
        this.firstNonFullRow = 0;
        build(1, 0, n - 1);
    }

    private void build(int node, int left, int right) {
        if (left == right) {
            sumTree[node] = m;
            maxTree[node] = m;
            return;
        }
        int mid = left + (right - left) / 2;
        build(node * 2, left, mid);
        build(node * 2 + 1, mid + 1, right);
        pull(node);
    }

    private void pull(int node) {
        sumTree[node] = sumTree[node * 2] + sumTree[node * 2 + 1];
        maxTree[node] = Math.max(maxTree[node * 2], maxTree[node * 2 + 1]);
    }

    private void updateRow(int node, int left, int right, int index, long remain) {
        if (left == right) {
            sumTree[node] = remain;
            maxTree[node] = remain;
            return;
        }
        int mid = left + (right - left) / 2;
        if (index <= mid) {
            updateRow(node * 2, left, mid, index, remain);
        } else {
            updateRow(node * 2 + 1, mid + 1, right, index, remain);
        }
        pull(node);
    }

    private long querySum(int node, int left, int right, int ql, int qr) {
        if (ql <= left && right <= qr) return sumTree[node];
        if (right < ql || left > qr) return 0;

        int mid = left + (right - left) / 2;
        return querySum(node * 2, left, mid, ql, qr)
             + querySum(node * 2 + 1, mid + 1, right, ql, qr);
    }

    private long queryMax(int node, int left, int right, int ql, int qr) {
        if (ql <= left && right <= qr) return maxTree[node];
        if (right < ql || left > qr) return 0;

        int mid = left + (right - left) / 2;
        return Math.max(
            queryMax(node * 2, left, mid, ql, qr),
            queryMax(node * 2 + 1, mid + 1, right, ql, qr)
        );
    }

    private int findFirstRowWithAtLeastK(int node, int left, int right, int maxRow, int k) {
        if (left > maxRow || maxTree[node] < k) return -1;
        if (left == right) return left;

        int mid = left + (right - left) / 2;
        int res = findFirstRowWithAtLeastK(node * 2, left, mid, maxRow, k);
        if (res != -1) return res;
        return findFirstRowWithAtLeastK(node * 2 + 1, mid + 1, right, maxRow, k);
    }

    public int[] gather(int k, int maxRow) {
        if (queryMax(1, 0, n - 1, 0, maxRow) < k) {
            return new int[0];
        }

        int row = findFirstRowWithAtLeastK(1, 0, n - 1, maxRow, k);
        int startSeat = (int) used[row];

        used[row] += k;
        long remain = m - used[row];
        updateRow(1, 0, n - 1, row, remain);

        if (remain == 0 && row == firstNonFullRow) {
            while (firstNonFullRow < n && used[firstNonFullRow] == m) {
                firstNonFullRow++;
            }
        }

        return new int[]{row, startSeat};
    }

    public boolean scatter(int k, int maxRow) {
        long available = querySum(1, 0, n - 1, 0, maxRow);
        if (available < k) return false;

        long need = k;
        while (need > 0) {
            if (firstNonFullRow > maxRow) break;

            long remain = m - used[firstNonFullRow];
            long take = Math.min(remain, need);

            used[firstNonFullRow] += take;
            need -= take;

            long newRemain = m - used[firstNonFullRow];
            updateRow(1, 0, n - 1, firstNonFullRow, newRemain);

            if (used[firstNonFullRow] == m) {
                firstNonFullRow++;
            }
        }

        return true;
    }
}
```

---

# Complexity Summary

Let `Q` be the total number of operations.

## Approach 1

```text
Time:  O(Q * n)
Space: O(n)
```

## Approach 2

```text
Time:  O(Q log n) amortized, plus O(n) total row-pointer advances
Space: O(n)
```

---

# Final Takeaway

This problem looks like seat allocation, but the key simplification is:

> because seats are always taken from left to right in each row, a row’s available consecutive block is exactly its remaining capacity.

That reduces the problem to maintaining:

- prefix sums of remaining seats
- prefix maximum of remaining seats

So the natural and strongest solution is a **segment tree with sum + max**.
