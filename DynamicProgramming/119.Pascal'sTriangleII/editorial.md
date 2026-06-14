# 119. Pascal's Triangle II — Exhaustive Approach Summary (Java)

## Problem restatement

Given an integer `rowIndex`, return the `rowIndex`-th row of Pascal’s Triangle using **0-based indexing**.

Examples:

- `rowIndex = 0` → `[1]`
- `rowIndex = 1` → `[1, 1]`
- `rowIndex = 3` → `[1, 3, 3, 1]`

In Pascal’s Triangle:

- the first and last element of every row are `1`
- every middle element is the sum of the two elements directly above it

So if row `r` is:

```text
[r0, r1, r2, ..., rr]
```

then:

- first = `1`
- last = `1`
- middle values satisfy:

```text
row[r][c] = row[r-1][c-1] + row[r-1][c]
```

---

## Important observation

We only need **one row**, not the full triangle.

That changes the optimization strategy significantly.

A full Pascal triangle build is valid, but not the most space-efficient approach.

---

# Approach 1 — Build the full Pascal triangle up to `rowIndex`

## Idea

Generate rows from `0` to `rowIndex`, store every row, and return the last one.

This is the most straightforward solution if you already know how to generate Pascal’s Triangle.

---

## Construction logic

For each row `i`:

- create a list of size `i + 1`
- set first and last elements to `1`
- for middle indices `j`:

```text
current[j] = previous[j - 1] + previous[j]
```

---

## Java code

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<Integer> getRow(int rowIndex) {
        List<List<Integer>> triangle = new ArrayList<>();

        for (int i = 0; i <= rowIndex; i++) {
            List<Integer> row = new ArrayList<>();

            for (int j = 0; j <= i; j++) {
                if (j == 0 || j == i) {
                    row.add(1);
                } else {
                    row.add(triangle.get(i - 1).get(j - 1) + triangle.get(i - 1).get(j));
                }
            }

            triangle.add(row);
        }

        return triangle.get(rowIndex);
    }
}
```

---

## Time complexity

To build all rows from `0` to `rowIndex`, total elements created are:

```text
1 + 2 + 3 + ... + (rowIndex + 1)
```

That is:

```text
O(rowIndex^2)
```

---

## Space complexity

Since we store the entire triangle:

```text
O(rowIndex^2)
```

---

## Strengths

- Very easy to understand
- Good if you want the whole triangle, not just one row

## Weaknesses

- Uses much more space than necessary
- Does not satisfy the follow-up optimization spirit

---

# Approach 2 — Iterative row-by-row build using only previous row

## Idea

We do not need the full triangle.

To build row `i`, we only need row `i - 1`.

So:

- keep `prev`
- build `curr`
- replace `prev = curr`

At the end, `prev` is the answer.

---

## Transition

If `prev` is the previous row, then the current row is:

- first = `1`
- last = `1`
- middle:

```text
curr[j] = prev.get(j - 1) + prev.get(j)
```

---

## Java code

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<Integer> getRow(int rowIndex) {
        List<Integer> prev = new ArrayList<>();
        prev.add(1);

        for (int i = 1; i <= rowIndex; i++) {
            List<Integer> curr = new ArrayList<>();
            curr.add(1);

            for (int j = 1; j < i; j++) {
                curr.add(prev.get(j - 1) + prev.get(j));
            }

            curr.add(1);
            prev = curr;
        }

        return prev;
    }
}
```

---

## Time complexity

We still build all rows up to `rowIndex`, so total work is:

```text
O(rowIndex^2)
```

---

## Space complexity

We only store one previous row and one current row, each of size at most `rowIndex + 1`.

So extra space is:

```text
O(rowIndex)
```

---

## Why this is better than Approach 1

Because the recurrence only depends on the immediately previous row.

Storing all older rows is wasteful if the problem only asks for one row.

---

# Approach 3 — In-place DP using one array/list, updating right to left

This is the classic follow-up solution.

## Idea

Use a single list representing the current row.

Start with:

```text
[1]
```

Then repeatedly transform it into the next row.

The key challenge is:

- each value depends on the old values from the previous row
- if we update left to right, we overwrite values too early

So we must update **right to left**.

---

## Why right to left?

Suppose current row representation is:

```text
[1, 3, 3, 1]
```

To generate next row:

```text
[1, 4, 6, 4, 1]
```

For middle values:

```text
row[j] = row[j] + row[j - 1]
```

But this formula assumes:

- `row[j]` is still old previous-row value
- `row[j - 1]` is also still old previous-row value

If you update left to right, `row[j - 1]` would already be modified, which corrupts the result.

Right-to-left preserves correctness.

---

## Step-by-step example for `rowIndex = 4`

Start:

```text
[1]
```

After row 1:

- append 1 → `[1, 1]`

After row 2:

- append 1 → `[1, 1, 1]`
- update from right to left:
  - j = 1 → `row[1] = row[1] + row[0] = 1 + 1 = 2`
- result: `[1, 2, 1]`

After row 3:

- append 1 → `[1, 2, 1, 1]`
- j = 2 → `1 + 2 = 3`
- j = 1 → `2 + 1 = 3`
- result: `[1, 3, 3, 1]`

After row 4:

- append 1 → `[1, 3, 3, 1, 1]`
- j = 3 → `1 + 3 = 4`
- j = 2 → `3 + 3 = 6`
- j = 1 → `3 + 1 = 4`
- result: `[1, 4, 6, 4, 1]`

---

## Java code

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<Integer> getRow(int rowIndex) {
        List<Integer> row = new ArrayList<>();
        row.add(1);

        for (int i = 1; i <= rowIndex; i++) {
            row.add(1);

            for (int j = i - 1; j > 0; j--) {
                row.set(j, row.get(j) + row.get(j - 1));
            }
        }

        return row;
    }
}
```

---

## Time complexity

We perform updates for each row:

```text
1 + 2 + 3 + ... + rowIndex
```

So total time is:

```text
O(rowIndex^2)
```

---

## Space complexity

Only one row is stored:

```text
O(rowIndex)
```

This satisfies the follow-up.

---

## Why this is usually the best DP answer

It is:

- simple
- elegant
- space-optimal with respect to DP construction
- directly aligned with the follow-up

---

# Approach 4 — Mathematical combinatorics using binomial coefficients

This is the most interesting non-DP solution.

## Key observation

The `rowIndex`-th row of Pascal’s Triangle is exactly the sequence of binomial coefficients:

```text
C(rowIndex, 0), C(rowIndex, 1), C(rowIndex, 2), ..., C(rowIndex, rowIndex)
```

For example, row 4 is:

```text
C(4,0)=1
C(4,1)=4
C(4,2)=6
C(4,3)=4
C(4,4)=1
```

So row 4 is:

```text
[1, 4, 6, 4, 1]
```

---

## Efficient recurrence for combinations

Instead of computing factorials, use:

```text
C(n, k) = C(n, k-1) * (n - k + 1) / k
```

This lets us generate the row from left to right.

If current value is `C(n, k-1)`, next value is:

```text
next = current * (n - k + 1) / k
```

---

## Why this is safe here

Constraint:

```text
0 <= rowIndex <= 33
```

So values fit within Java `int`, but during intermediate multiplication it is safer to use `long`.

---

## Java code

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<Integer> getRow(int rowIndex) {
        List<Integer> row = new ArrayList<>();
        long value = 1;

        for (int k = 0; k <= rowIndex; k++) {
            row.add((int) value);
            value = value * (rowIndex - k) / (k + 1);
        }

        return row;
    }
}
```

---

## Time complexity

We compute exactly one value per position:

```text
O(rowIndex)
```

---

## Space complexity

The output list itself has `rowIndex + 1` elements:

```text
O(rowIndex)
```

Extra auxiliary space beyond output is:

```text
O(1)
```

---

## Why this is asymptotically the best

Unlike DP approaches, this does **not** build earlier rows.

It directly computes the target row.

So if the question only asks for one row, this is the most efficient method in time.

---

## Important subtlety

Avoid factorial-based formulas like:

```text
C(n, k) = n! / (k! * (n-k)!)
```

because:

- factorials grow very quickly
- even if the final answer fits, intermediate factorials may overflow
- it is less efficient than the iterative combination update

The recurrence formula is much better.

---

# Approach 5 — Symmetry-aware combinatorial construction

## Idea

Pascal rows are symmetric:

```text
C(n, k) = C(n, n-k)
```

So you only need to compute up to the middle, then mirror.

For example:

```text
row 6 = [1, 6, 15, 20, 15, 6, 1]
```

You can compute:

```text
1, 6, 15, 20
```

and then mirror.

This does not asymptotically improve beyond `O(rowIndex)`, but it is a useful mathematical refinement.

---

## Java code

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<Integer> getRow(int rowIndex) {
        List<Integer> row = new ArrayList<>();

        long value = 1;
        int mid = rowIndex / 2;

        for (int k = 0; k <= mid; k++) {
            row.add((int) value);
            value = value * (rowIndex - k) / (k + 1);
        }

        int start = (rowIndex % 2 == 0) ? mid - 1 : mid;
        for (int i = start; i >= 0; i--) {
            row.add(row.get(i));
        }

        return row;
    }
}
```

---

## Time complexity

Still:

```text
O(rowIndex)
```

---

## Space complexity

Still:

```text
O(rowIndex)
```

---

## Is this better in interviews?

Usually not by much.

The standard combinatorial recurrence is simpler and already optimal.

This symmetric version is more of a neat mathematical extension.

---

# Detailed dry runs

## Example 1: `rowIndex = 3`

Expected row:

```text
[1, 3, 3, 1]
```

### Using combinatorial formula

`n = 3`

- `k = 0` → `1`
- `k = 1` → `1 * 3 / 1 = 3`
- `k = 2` → `3 * 2 / 2 = 3`
- `k = 3` → `3 * 1 / 3 = 1`

Answer:

```text
[1, 3, 3, 1]
```

---

## Example 2: `rowIndex = 0`

Only one element:

```text
[1]
```

---

## Example 3: `rowIndex = 1`

- `C(1,0) = 1`
- `C(1,1) = 1`

Answer:

```text
[1, 1]
```

---

## Example 4: `rowIndex = 4`

Using 1-row DP:

Start:

```text
[1]
```

After row 1:

```text
[1, 1]
```

After row 2:

```text
[1, 2, 1]
```

After row 3:

```text
[1, 3, 3, 1]
```

After row 4:

```text
[1, 4, 6, 4, 1]
```

---

# Why right-to-left update is mandatory in Approach 3

Suppose current row is:

```text
[1, 3, 3, 1]
```

If you update left to right:

- update index 1 first:
  - becomes `3 + 1 = 4`
- now when computing index 2:
  - you use modified index 1 (`4`) instead of old value (`3`)
  - result becomes `3 + 4 = 7`, which is wrong

Correct next row should be:

```text
[1, 4, 6, 4, 1]
```

not:

```text
[1, 4, 7, ...]
```

So right-to-left is not optional. It is the core reason the in-place update works.

---

# Common mistakes

## Mistake 1 — Using left-to-right update in the one-row DP

This breaks correctness due to overwritten values.

---

## Mistake 2 — Using factorial formulas directly

This can overflow unnecessarily and is less efficient.

---

## Mistake 3 — Forgetting that indexing is 0-based

`rowIndex = 0` corresponds to:

```text
[1]
```

not `[1,1]`.

---

## Mistake 4 — Confusing output space with auxiliary space

Even the best solution must return a list of size `rowIndex + 1`.

So the output itself is `O(rowIndex)`.

When people say auxiliary space is `O(1)`, they mean **excluding the output list**.

---

# Comparing all approaches

| Approach                     | Idea                           |     Time |      Extra Space | Notes                       |
| ---------------------------- | ------------------------------ | -------: | ---------------: | --------------------------- |
| Full triangle build          | Store all rows                 | `O(n^2)` |         `O(n^2)` | Easiest but wasteful        |
| Previous-row iterative       | Keep only prev and curr        | `O(n^2)` |           `O(n)` | Good basic optimization     |
| In-place one-row DP          | Update same list right-to-left | `O(n^2)` |           `O(n)` | Best DP/follow-up solution  |
| Binomial coefficient formula | Directly compute `C(n, k)`     |   `O(n)` | `O(1)` auxiliary | Best asymptotic time        |
| Symmetry-aware combinatorics | Compute half and mirror        |   `O(n)` | `O(1)` auxiliary | Nice mathematical extension |

Here `n = rowIndex`.

---

# Which solution is best in interviews?

That depends on what the interviewer values.

## If they want the standard DP/follow-up answer

Use the **in-place one-row DP** approach.

It directly addresses:

> Could you optimize your algorithm to use only `O(rowIndex)` extra space?

That is likely the expected official follow-up solution.

## If they ask for the best asymptotic time

Use the **binomial coefficient** approach.

That is mathematically sharper because you compute the target row directly in `O(n)` time.

---

# Recommended final interview solution for the follow-up

This is likely the most expected answer:

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<Integer> getRow(int rowIndex) {
        List<Integer> row = new ArrayList<>();
        row.add(1);

        for (int i = 1; i <= rowIndex; i++) {
            row.add(1);
            for (int j = i - 1; j > 0; j--) {
                row.set(j, row.get(j) + row.get(j - 1));
            }
        }

        return row;
    }
}
```

### Time complexity

```text
O(rowIndex^2)
```

### Space complexity

```text
O(rowIndex)
```

---

# Recommended mathematically optimal solution

If you want the fastest direct construction:

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<Integer> getRow(int rowIndex) {
        List<Integer> row = new ArrayList<>();
        long value = 1;

        for (int k = 0; k <= rowIndex; k++) {
            row.add((int) value);
            value = value * (rowIndex - k) / (k + 1);
        }

        return row;
    }
}
```

### Time complexity

```text
O(rowIndex)
```

### Extra auxiliary space

```text
O(1)
```

### Output space

```text
O(rowIndex)
```

---

# Compact interview explanation

A concise explanation for the follow-up solution:

> We can build Pascal’s Triangle row by row using a single list.
> To generate the next row in place, each middle element becomes the sum of the old current element and the old left neighbor.
> Since updating left to right would overwrite needed values, we update from right to left.
> This gives `O(rowIndex^2)` time and `O(rowIndex)` extra space.

A concise explanation for the mathematical solution:

> The `rowIndex`-th Pascal row is exactly the sequence of binomial coefficients `C(rowIndex, k)`.
> Using the recurrence `C(n, k) = C(n, k-1) * (n-k+1) / k`, we can compute each entry directly in linear time.

---

# Final takeaway

This problem has two distinct solution families:

1. **DP / triangle construction view**
   - natural
   - matches Pascal definition directly
   - follow-up answer: one-row in-place DP

2. **Combinatorics view**
   - sharper mathematically
   - directly computes the row
   - best asymptotic time

For interviews, it is ideal to know both:

- the in-place DP solution because it directly answers the follow-up
- the combinatorial solution because it shows deeper insight

---

# Practice extension

A useful next step is to compare this with:

- **118. Pascal’s Triangle** — generate the entire triangle
- binomial coefficient problems
- combinatorics + DP crossover problems

This helps build the instinct for when a DP construction can be replaced by a direct mathematical formula.
