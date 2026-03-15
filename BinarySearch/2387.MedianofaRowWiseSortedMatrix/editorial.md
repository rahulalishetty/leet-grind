# 2387. Median of a Row Wise Sorted Matrix — Java Solutions and Detailed Notes

## Problem

Given an `m x n` matrix `grid`:

- each row is sorted in **non-decreasing order**,
- `m` and `n` are both odd,
- therefore `m * n` is odd,

return the **median** of all matrix elements.

You must solve it in **less than `O(m * n)`** time.

```java
class Solution {
    public int matrixMedian(int[][] grid) {

    }
}
```

---

## What is the median here?

If the matrix has:

```text
total = m * n
```

elements, then because `total` is odd, the median is the element at index:

```text
total / 2
```

in **0-based sorted order**, or equivalently the:

```text
(total / 2 + 1)-th smallest
```

element in **1-based order**.

So this is really a **kth smallest** problem.

---

# Core observation

We are **not** given a globally sorted matrix.

Only each **row** is sorted.

That means:

- we cannot directly index the median,
- but for any candidate value `x`, we **can count how many elements are <= x** efficiently,
- because each row is sorted, and we can use binary search inside each row.

That leads naturally to:

```text
Binary Search on the value space
```

This is the intended optimal solution.

---

# Approach 1: Flatten and Sort (Baseline, not valid for follow-up)

## Idea

Collect all elements into a single array, sort it, and return the middle element.

This is the most direct solution.

---

## Why it works

If we flatten all elements and sort them, the median is exactly the middle index.

---

## Java code

```java
import java.util.*;

class Solution {
    public int matrixMedian(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;

        int[] all = new int[m * n];
        int idx = 0;

        for (int[] row : grid) {
            for (int val : row) {
                all[idx++] = val;
            }
        }

        Arrays.sort(all);
        return all[all.length / 2];
    }
}
```

---

## Complexity

Time complexity:

```text
O(m * n * log(m * n))
```

Space complexity:

```text
O(m * n)
```

---

## Verdict

Simple and correct, but does **not** satisfy the required sub-`O(m*n)` spirit.

---

# Approach 2: Min-Heap K-Way Merge

## Idea

Each row is individually sorted, so the matrix can be viewed as `m` sorted lists.

We want the median, i.e. the:

```text
k = m * n / 2 + 1
```

-th smallest element.

That suggests a **k-way merge** using a min-heap.

---

## How it works

1. Push the first element of every row into a min-heap.
2. Repeatedly pop the smallest element.
3. When popping an element from row `r` and column `c`, push `grid[r][c+1]` if it exists.
4. After popping exactly `k` elements, the last popped value is the answer.

This is exactly like merging `m` sorted arrays.

---

## Java code

```java
import java.util.*;

class Solution {
    private static class Node {
        int value;
        int row;
        int col;

        Node(int value, int row, int col) {
            this.value = value;
            this.row = row;
            this.col = col;
        }
    }

    public int matrixMedian(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int k = m * n / 2 + 1;

        PriorityQueue<Node> minHeap = new PriorityQueue<>(
            Comparator.comparingInt(a -> a.value)
        );

        for (int r = 0; r < m; r++) {
            minHeap.offer(new Node(grid[r][0], r, 0));
        }

        int answer = -1;

        for (int count = 0; count < k; count++) {
            Node cur = minHeap.poll();
            answer = cur.value;

            if (cur.col + 1 < n) {
                minHeap.offer(new Node(
                    grid[cur.row][cur.col + 1],
                    cur.row,
                    cur.col + 1
                ));
            }
        }

        return answer;
    }
}
```

---

## Complexity

Let:

```text
k = m * n / 2 + 1
```

Time complexity:

```text
O(k log m)
```

Since the heap has at most `m` elements.

Space complexity:

```text
O(m)
```

---

## Verdict

This is often fast in practice and elegant, but in the worst case `k` is about `m*n/2`, so it is still not really better than linear in the number of elements.

The best asymptotic solution is still the next one.

---

# Approach 3: Binary Search on Value Space + Binary Search in Each Row (Optimal)

## Idea

Instead of searching among indices, search among **possible values**.

For a candidate value `mid`, count how many elements in the matrix are:

```text
<= mid
```

Because rows are sorted, this count can be computed with binary search in each row.

Then:

- if count is too small, median must be larger,
- otherwise median is `<= mid`.

This is a classic **binary search on answer** pattern.

---

## Why monotonicity exists

Define:

```text
count(x) = number of elements in the matrix <= x
```

As `x` increases, `count(x)` never decreases.

So `count(x)` is monotonic.

We need the smallest value `x` such that:

```text
count(x) >= k
```

where:

```text
k = m * n / 2 + 1
```

That value is exactly the median.

---

## Counting elements <= x in one row

Suppose a row is:

```text
[1, 1, 3, 3, 4]
```

If `x = 3`, then the number of elements `<= 3` is `4`.

Because the row is sorted, we can binary search for the **first index strictly greater than `x`**.

That index is also the count of elements `<= x`.

This is the classic `upper_bound` idea.

---

## Overall algorithm

1. Let:

```text
left = minimum value in the matrix
right = maximum value in the matrix
```

Since rows are sorted:

- row minimum is at column `0`
- row maximum is at column `n - 1`

So we can scan only the row endpoints to initialize bounds.

2. Set:

```text
target = m * n / 2 + 1
```

3. While `left < right`:
   - `mid = left + (right - left) / 2`
   - compute `count(mid)` = total number of matrix elements `<= mid`
   - if `count(mid) < target`, move right:
     ```text
     left = mid + 1
     ```
   - else move left:
     ```text
     right = mid
     ```

4. Return `left`.

---

## Java code

```java
class Solution {
    public int matrixMedian(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;

        int left = Integer.MAX_VALUE;
        int right = Integer.MIN_VALUE;

        for (int[] row : grid) {
            left = Math.min(left, row[0]);
            right = Math.max(right, row[n - 1]);
        }

        int target = m * n / 2 + 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            int count = 0;
            for (int[] row : grid) {
                count += countLessThanOrEqual(row, mid);
            }

            if (count < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    private int countLessThanOrEqual(int[] row, int target) {
        int lo = 0, hi = row.length;

        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (row[mid] <= target) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }

        return lo;
    }
}
```

---

## Complexity

Let:

- `m` = number of rows
- `n` = number of columns
- `V` = value range = `maxValue - minValue`

Time complexity:

```text
O(m * log n * log V)
```

Why?

- binary search on value space: `O(log V)`
- for each candidate value, binary search each row: `m * O(log n)`

Space complexity:

```text
O(1)
```

excluding recursion / library overhead.

---

## This is the intended optimal solution

Because:

- it never scans all `m*n` elements directly,
- it exploits row sorting efficiently,
- it fits the required constraint well.

---

# Approach 4: Binary Search on Value Space + Linear Count per Row (Simpler, but not optimal enough)

## Idea

Same value-space binary search as Approach 3, but instead of binary searching inside each row, linearly count how many elements in a row are `<= mid`.

This is simpler conceptually, but too slow compared to the optimized version.

---

## Java code

```java
class Solution {
    public int matrixMedian(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;

        int left = Integer.MAX_VALUE;
        int right = Integer.MIN_VALUE;

        for (int[] row : grid) {
            left = Math.min(left, row[0]);
            right = Math.max(right, row[n - 1]);
        }

        int target = m * n / 2 + 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            int count = 0;
            for (int[] row : grid) {
                for (int val : row) {
                    if (val <= mid) {
                        count++;
                    }
                }
            }

            if (count < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }
}
```

---

## Complexity

Time complexity:

```text
O(m * n * log V)
```

Space complexity:

```text
O(1)
```

---

## Verdict

Conceptually useful, but weaker than Approach 3.

---

# Dry run of Approach 3

## Example

```text
grid = [[1,1,2],
        [2,3,3],
        [1,3,4]]
```

Flattened sorted order would be:

```text
1,1,1,2,2,3,3,3,4
```

Median is the 5th smallest (1-based), i.e. `2`.

### Step 1: bounds

- minimum = `1`
- maximum = `4`

So:

```text
left = 1
right = 4
target = 9 / 2 + 1 = 5
```

### Step 2: mid = 2

Count how many elements `<= 2`

Row `[1,1,2]` → `3`
Row `[2,3,3]` → `1`
Row `[1,3,4]` → `1`

Total:

```text
5
```

Since `5 >= target`, median is `<= 2`, so:

```text
right = 2
```

### Step 3: mid = 1

Count how many elements `<= 1`

Row `[1,1,2]` → `2`
Row `[2,3,3]` → `0`
Row `[1,3,4]` → `1`

Total:

```text
3
```

Since `3 < 5`, median is larger:

```text
left = 2
```

Now:

```text
left == right == 2
```

Answer = `2`.

---

# Why “smallest x with count(x) >= target” gives the median

Let:

```text
target = m * n / 2 + 1
```

Because total number of elements is odd, the median is exactly the `target`-th smallest element.

Now define:

```text
count(x) = number of matrix elements <= x
```

Then:

- if `count(x) < target`, `x` is too small,
- if `count(x) >= target`, `x` is large enough to reach the median position.

So the median is the **smallest value** where this condition becomes true.

That is exactly what lower-bound binary search returns.

---

# Comparison of approaches

## Approach 1: Flatten and Sort

### Pros

- easiest to write
- easiest to understand

### Cons

- ignores row ordering
- too expensive for the intended follow-up

### Complexity

```text
Time:  O(m*n*log(m*n))
Space: O(m*n)
```

---

## Approach 2: Min-Heap K-Way Merge

### Pros

- elegant
- uses row sorting
- good if you specifically want the kth smallest from sorted lists

### Cons

- still depends on `k`, which is about half the matrix
- not as asymptotically strong as value-space binary search

### Complexity

```text
Time:  O((m*n/2) * log m)
Space: O(m)
```

---

## Approach 3: Value Binary Search + Row Binary Search

### Pros

- intended optimal solution
- exploits both row-sorted property and value monotonicity
- excellent complexity

### Cons

- a bit more abstract initially

### Complexity

```text
Time:  O(m * log n * log V)
Space: O(1)
```

---

## Approach 4: Value Binary Search + Linear Row Scan

### Pros

- easier stepping stone to Approach 3

### Cons

- slower than necessary

### Complexity

```text
Time:  O(m * n * log V)
Space: O(1)
```

---

# Final recommended solution

Use:

## Binary Search on Value Space + Binary Search in Each Row

because it is the cleanest truly efficient method for this problem.

---

# Final polished Java solution

```java
class Solution {
    public int matrixMedian(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;

        int low = Integer.MAX_VALUE;
        int high = Integer.MIN_VALUE;

        for (int[] row : grid) {
            low = Math.min(low, row[0]);
            high = Math.max(high, row[n - 1]);
        }

        int need = m * n / 2 + 1;

        while (low < high) {
            int mid = low + (high - low) / 2;

            int count = 0;
            for (int[] row : grid) {
                count += upperBound(row, mid);
            }

            if (count < need) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        return low;
    }

    private int upperBound(int[] row, int target) {
        int left = 0, right = row.length;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (row[mid] <= target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }
}
```

---

# Pattern takeaway

This is a classic example of:

```text
Binary Search on Answer
+ Efficient counting using sorted structure
```

Whenever you see:

- rows sorted,
- need kth smallest / median,
- cannot flatten everything efficiently,

it is a strong signal to consider:

1. binary searching a candidate value,
2. counting how many numbers are `<= candidate`.

That pattern appears in many matrix and order-statistics problems.
