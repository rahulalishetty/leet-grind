# Maximum Score by Hopping to the Last Index — Detailed Summary of Both Solutions

## Problem Restatement

We are given an array `nums`.

We start at index `0` and must eventually reach index `n - 1`.

From an index `i`, we may jump to any later index `j > i`.

If we jump from `i` to `j`, we earn:

```text
(j - i) * nums[j]
```

We must maximize the total score collected by a sequence of jumps ending at the last index.

---

# Part 1 — The Straightforward Dynamic Programming Solution

## Core DP Idea

Let:

```text
dp[j] = maximum score obtainable when we arrive at index j
```

Then to compute `dp[j]`, consider the last jump into `j`.

Suppose that last jump came from some earlier index `i < j`.

Then:

- we already earned `dp[i]` before reaching `i`
- jumping from `i` to `j` adds:
  ```text
  (j - i) * nums[j]
  ```

So:

```text
dp[j] = max over all i < j of [dp[i] + (j - i) * nums[j]]
```

That is the direct recurrence.

### Base case

```text
dp[0] = 0
```

because we start at index `0` and have not jumped yet.

---

## Why This DP Is Correct

Every valid path to `j` must end with exactly one final jump from some earlier index `i`.

There is no other possibility.

So if we try all earlier `i`, and take the best of:

```text
dp[i] + jump_score(i, j)
```

we cover all valid ways to reach `j`.

This is the classic “last step” DP pattern.

---

## Brute-Force / Quadratic DP Implementation

```java
class Solution {
    public long maxScore(int[] nums) {
        int n = nums.length;
        long[] dp = new long[n];
        dp[0] = 0;

        for (int j = 1; j < n; j++) {
            long best = Long.MIN_VALUE;
            for (int i = 0; i < j; i++) {
                best = Math.max(best, dp[i] + 1L * (j - i) * nums[j]);
            }
            dp[j] = best;
        }

        return dp[n - 1];
    }
}
```

---

## Time and Space Complexity of the Basic DP

### Time

For each `j`, we check all `i < j`.

So total work is:

```text
1 + 2 + 3 + ... + (n - 1) = O(n^2)
```

### Space

We store the `dp` array:

```text
O(n)
```

---

## Why This Solution May Be Too Slow

The recurrence is correct, but the inner loop makes it quadratic.

So if `n` is large, this can time out.

That means we need to look for structure inside:

```text
dp[i] + (j - i) * nums[j]
```

and optimize that maximum query.

---

# Part 2 — Optimized Solution Using Li Chao Tree

Now let us derive the faster solution.

## Start From the Same DP

We have:

```text
dp[j] = max over i < j of [dp[i] + (j - i) * nums[j]]
```

Expand the expression:

```text
dp[j] = max over i < j of [dp[i] + j * nums[j] - i * nums[j]]
```

Now separate the part depending only on `j`:

```text
dp[j] = j * nums[j] + max over i < j of [dp[i] - i * nums[j]]
```

This is the key algebraic rewrite.

---

## Important Structural Observation

For a fixed earlier index `i`, the expression:

```text
dp[i] - i * nums[j]
```

is linear in the variable `nums[j]`.

If we introduce:

```text
x = nums[j]
```

then for each fixed `i`, define a line:

```text
f_i(x) = dp[i] - i * x
```

or in standard line form:

```text
f_i(x) = m * x + b
```

with:

- slope `m = -i`
- intercept `b = dp[i]`

Then:

```text
dp[j] = j * nums[j] + max_i f_i(nums[j])
```

So for each `j`, we need:

1. query the maximum value among all previously inserted lines at `x = nums[j]`
2. then add `j * nums[j]`
3. then insert the new line for index `j`

This transforms the problem into a dynamic set of lines with maximum queries.

That is exactly what a **Li Chao Tree** is designed for.

---

## Why a Li Chao Tree Fits

A Li Chao Tree supports:

- inserting lines
- querying the maximum or minimum line value at a point `x`

efficiently.

Since every index `i` contributes one line:

```text
f_i(x) = dp[i] - i * x
```

and every index `j` needs a maximum query at:

```text
x = nums[j]
```

the Li Chao Tree gives the desired optimization.

---

## Optimized Algorithm Outline

### Step 1

Start with index `0`.

Since:

```text
dp[0] = 0
```

the line contributed by index `0` is:

```text
f_0(x) = 0 - 0*x = 0
```

So the first inserted line is:

```text
y = 0
```

### Step 2

For each `j` from `1` to `n - 1`:

- query the best line value at `x = nums[j]`
- compute:

```text
dp[j] = best + j * nums[j]
```

- insert the line for this `j`:

```text
f_j(x) = dp[j] - j * x
```

with:

- slope = `-j`
- intercept = `dp[j]`

### Step 3

Return:

```text
dp[n - 1]
```

---

## Java Code for the Li Chao Tree Solution

```java
class Solution {
    public long maxScore(int[] nums) {
        int n = nums.length;
        long[] dp = new long[n];

        long minX = nums[0], maxX = nums[0];
        for (int v : nums) {
            minX = Math.min(minX, v);
            maxX = Math.max(maxX, v);
        }

        LiChaoTree tree = new LiChaoTree(minX, maxX);

        // index 0 => dp[0] = 0
        // line: f_0(x) = dp[0] - 0*x = 0
        tree.addLine(new Line(0, 0));

        for (int j = 1; j < n; j++) {
            long best = tree.query(nums[j]);
            dp[j] = best + 1L * j * nums[j];

            // add line for this index:
            // f_j(x) = dp[j] - j*x
            tree.addLine(new Line(-j, dp[j]));
        }

        return dp[n - 1];
    }

    static class Line {
        long m, b; // y = m*x + b

        Line(long m, long b) {
            this.m = m;
            this.b = b;
        }

        long value(long x) {
            return m * x + b;
        }
    }

    static class Node {
        long l, r;
        Line line;
        Node left, right;

        Node(long l, long r) {
            this.l = l;
            this.r = r;
        }
    }

    static class LiChaoTree {
        Node root;

        LiChaoTree(long minX, long maxX) {
            root = new Node(minX, maxX);
        }

        void addLine(Line newLine) {
            insert(root, newLine);
        }

        long query(long x) {
            return query(root, x);
        }

        private void insert(Node node, Line newLine) {
            long l = node.l, r = node.r;
            long mid = l + ((r - l) >> 1);

            if (node.line == null) {
                node.line = newLine;
                return;
            }

            Line low = node.line;
            Line high = newLine;

            if (low.value(mid) < high.value(mid)) {
                Line tmp = low;
                low = high;
                high = tmp;
            }

            node.line = low;

            if (l == r) return;

            if (high.value(l) > low.value(l)) {
                if (node.left == null) node.left = new Node(l, mid);
                insert(node.left, high);
            } else if (high.value(r) > low.value(r)) {
                if (node.right == null) node.right = new Node(mid + 1, r);
                insert(node.right, high);
            }
        }

        private long query(Node node, long x) {
            if (node == null) return Long.MIN_VALUE / 4;

            long ans = node.line == null ? Long.MIN_VALUE / 4 : node.line.value(x);
            if (node.l == node.r) return ans;

            long mid = node.l + ((node.r - node.l) >> 1);
            if (x <= mid) return Math.max(ans, query(node.left, x));
            return Math.max(ans, query(node.right, x));
        }
    }
}
```

---

## Why `long` Is Used

The score added by a jump is:

```text
(j - i) * nums[j]
```

and the total score can accumulate over many jumps.

This may overflow `int`, so the DP array and all line computations should use `long`.

That is why the method should return:

```java
public long maxScore(int[] nums)
```

and not `int`.

If a platform requires `int`, only cast at the end if the constraints guarantee safety.

---

# Deep Intuition for the Li Chao Transformation

This is the most important conceptual step.

The original recurrence is:

```text
dp[j] = max_i [dp[i] + (j - i) * nums[j]]
```

At first glance, this looks like two-dimensional dependence on both `i` and `j`.

But once you expand it:

```text
dp[j] = j * nums[j] + max_i [dp[i] - i * nums[j]]
```

the dependence on `j` becomes:

- a fixed additive term: `j * nums[j]`
- plus the maximum value of one among many linear functions at `x = nums[j]`

That means every old index `i` becomes a reusable formula for future indices.

Each such formula is a line.

So instead of recomputing every earlier jump candidate from scratch, we maintain all candidate lines in a data structure and query the best one.

This is the precise reason the optimization works.

---

# Worked Example

Consider:

```text
nums = [2, 5, 1]
```

We start at index `0`.

## Basic DP

### `dp[0] = 0`

### Compute `dp[1]`

Only possible previous index is `0`:

```text
dp[1] = dp[0] + (1 - 0) * nums[1]
      = 0 + 1 * 5
      = 5
```

### Compute `dp[2]`

Try all earlier indices:

From `0`:

```text
dp[0] + (2 - 0) * nums[2] = 0 + 2 * 1 = 2
```

From `1`:

```text
dp[1] + (2 - 1) * nums[2] = 5 + 1 * 1 = 6
```

So:

```text
dp[2] = 6
```

Final answer: `6`

---

## Same Example Through Li Chao View

### Start

`dp[0] = 0`

Line from index `0`:

```text
f_0(x) = 0 - 0*x = 0
```

### For `j = 1`

`x = nums[1] = 5`

Query best line at `x = 5`:

```text
f_0(5) = 0
```

So:

```text
dp[1] = 1 * 5 + 0 = 5
```

Insert line for index `1`:

```text
f_1(x) = dp[1] - 1*x = 5 - x
```

### For `j = 2`

`x = nums[2] = 1`

Query best of:

```text
f_0(1) = 0
f_1(1) = 4
```

Best = `4`

Then:

```text
dp[2] = 2 * 1 + 4 = 6
```

Same answer.

---

# Understanding the Li Chao Tree Internals

A Li Chao Tree stores one “currently best” line per segment and pushes competing lines into left or right child segments only where they might outperform the stored line.

The crucial property used is:

- two lines intersect at most once

So when comparing two lines on an interval:

- one line is better on one side
- the other may be better on the other side

This allows recursive insertion.

---

## The `Line` Class

```java
static class Line {
    long m, b; // y = m*x + b

    Line(long m, long b) {
        this.m = m;
        this.b = b;
    }

    long value(long x) {
        return m * x + b;
    }
}
```

Each previous index `i` contributes a line:

```text
y = -i * x + dp[i]
```

---

## The `Node` Class

```java
static class Node {
    long l, r;
    Line line;
    Node left, right;

    Node(long l, long r) {
        this.l = l;
        this.r = r;
    }
}
```

Each node stores:

- the x-interval it represents
- one line currently considered best over that interval at the midpoint
- child nodes for subintervals

---

## Why We Need `minX` and `maxX`

A Li Chao Tree must know the x-range over which queries happen.

Here all queries are made at:

```text
x = nums[j]
```

So before building the tree, we compute:

- minimum value in `nums`
- maximum value in `nums`

That becomes the coordinate domain.

---

## How Insertion Works

When inserting a new line into a node interval `[l, r]`:

1. compare the existing line and new line at the midpoint
2. keep the better one at the midpoint in the current node
3. the other line might still win on the left or right side
4. recurse only into the side where it can still beat the stored line

That is why insertion is logarithmic in the coordinate range.

---

## How Query Works

To query at a point `x`:

1. evaluate the current node’s stored line at `x`
2. recurse into the child interval containing `x`
3. return the maximum of all values seen along that path

So each query also takes logarithmic time.

---

# Comparing Both Solutions

## Solution 1: Quadratic DP

### Recurrence

```text
dp[j] = max_i [dp[i] + (j-i) * nums[j]]
```

### Pros

- simplest to derive
- easiest to understand initially
- good for validating correctness

### Cons

- too slow for large `n`

### Complexity

- Time: `O(n^2)`
- Space: `O(n)`

---

## Solution 2: Li Chao Tree Optimization

### Same recurrence, optimized

Transform:

```text
dp[j] = j * nums[j] + max_i [dp[i] - i * nums[j]]
```

Treat each `i` as a line.

### Pros

- much faster
- elegant algebraic optimization
- standard dynamic programming + data structure technique

### Cons

- more advanced
- implementation is significantly more complex
- easier to make mistakes in tree logic

### Complexity

- Time: `O(n log C)`
- Space: `O(n)`

where `C = max(nums) - min(nums) + 1` is the x-coordinate domain size.

---

# Which Solution Should You Use?

## Use the basic `O(n^2)` DP if:

- constraints are small
- you want the clearest explanation
- you want a correct baseline solution first

## Use the Li Chao Tree if:

- constraints are large
- `O(n^2)` is too slow
- you are comfortable with convex-hull / line-query optimizations

In practice, the right workflow is:

1. derive the plain DP first
2. algebraically transform it
3. recognize the line-query structure
4. optimize with Li Chao Tree

That gives both correctness and performance.

---

# Final Complexity Summary

## Quadratic DP

- **Time:** `O(n^2)`
- **Space:** `O(n)`

## Li Chao Tree DP

- **Time:** `O(n log C)`
- **Space:** `O(n)`

---

# Final Conceptual Summary

This problem is fundamentally a dynamic programming problem where:

- `dp[j]` asks for the best score to reach index `j`
- every earlier index `i` is a candidate for the last jump

The direct recurrence is:

```text
dp[j] = max_i [dp[i] + (j - i) * nums[j]]
```

The advanced insight is that after expansion, every previous `i` becomes a line:

```text
f_i(x) = dp[i] - i*x
```

and each new `j` only asks for the best line value at:

```text
x = nums[j]
```

So the optimized solution is just:

- DP
- plus a dynamic maximum-line query structure

That is the full logic behind both solutions.
