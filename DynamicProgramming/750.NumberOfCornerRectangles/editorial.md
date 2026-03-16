# 750. Number of Corner Rectangles — Detailed Notes

This document converts the provided explanation into a detailed Markdown note.

---

# Problem Recap

You are given an `m x n` binary matrix `grid`.

A **corner rectangle** is formed when there exist:

- two distinct rows `r1` and `r2`
- two distinct columns `c1` and `c2`

such that all four corner cells are `1`:

```text
grid[r1][c1] = 1
grid[r1][c2] = 1
grid[r2][c1] = 1
grid[r2][c2] = 1
```

Only the corners matter. The cells inside the rectangle can be anything.

The goal is to count how many such rectangles exist.

---

# Core Insight

A rectangle is fully determined by:

- a pair of rows
- a pair of columns

So one very useful way to count rectangles is:

> Count how many times the same pair of columns appears with `1`s in multiple rows.

If a pair of columns `(c1, c2)` appears together in:

```text
k
```

different rows, then those `k` rows contribute:

```text
C(k, 2) = k * (k - 1) / 2
```

corner rectangles, because we can choose any two of those rows to serve as the top and bottom sides.

The two approaches below are different ways to exploit this idea efficiently.

---

# Approach 1: Count Corners

## Intuition

Suppose we process the matrix row by row.

When we encounter a new row, we ask:

> How many new rectangles are created using this row as the bottom side?

For every pair of columns `(c1, c2)` where the current row has:

```text
row[c1] = 1 and row[c2] = 1
```

we can form a new rectangle with every previous row that also had `1`s in exactly those two columns.

So we maintain a count:

```text
count[c1, c2]
```

which stores:

> How many previous rows had `1`s at both columns `c1` and `c2`?

Then, when processing a new row:

- for each pair `(c1, c2)` of `1`s in the row
- add `count[c1, c2]` to the answer
- then increment `count[c1, c2]`

This works because every previous matching row forms exactly one new rectangle with the current row.

---

## Why It Works

Suppose the current row has `1`s at columns:

```text
c1 and c2
```

If there were already `k` earlier rows that also had `1`s at those same columns, then the current row can pair with each of those `k` rows to form a unique rectangle.

So:

```text
new rectangles contributed by this pair = count[c1, c2]
```

After accounting for them, we update the count because the current row may help future rows form rectangles too.

---

## Data Structure Choice

Since `c1` and `c2` are just column indices, we can encode the pair into a single integer key.

For example, if the number of columns is at most `200`, then:

```text
key = c1 * 200 + c2
```

uniquely identifies the ordered pair `(c1, c2)` with `c1 < c2`.

This lets us store the pair count in a hash map.

---

## Java Implementation

```java
class Solution {
    public int countCornerRectangles(int[][] grid) {
        Map<Integer, Integer> count = new HashMap();
        int ans = 0;

        for (int[] row : grid) {
            for (int c1 = 0; c1 < row.length; ++c1) if (row[c1] == 1) {
                for (int c2 = c1 + 1; c2 < row.length; ++c2) if (row[c2] == 1) {
                    int pos = c1 * 200 + c2;
                    int c = count.getOrDefault(pos, 0);
                    ans += c;
                    count.put(pos, c + 1);
                }
            }
        }

        return ans;
    }
}
```

---

## Complexity Analysis

Let:

- `R` = number of rows
- `C` = number of columns

### Time Complexity

For each row, in the worst case we inspect all pairs of columns:

```text
O(C^2)
```

Across all rows:

```text
O(R * C^2)
```

### Space Complexity

We may store counts for every pair of columns:

```text
O(C^2)
```

---

## Strength of This Approach

This method is simple and elegant.

It works especially well when rows are sparse, because then the number of pairs of `1`s in each row is much smaller than `C^2`.

---

# Approach 2: Heavy and Light Rows

## Motivation

In Approach 1, a row with `X` ones requires:

```text
O(X^2)
```

work, because we enumerate every pair of `1`s in that row.

This is fine if `X` is small.

But if a row is dense, containing many `1`s, then enumerating all pairs becomes expensive.

So the idea is:

> Treat sparse rows and dense rows differently.

This is a classic square-root decomposition style optimization.

---

## Heavy vs Light Rows

Let:

```text
N = total number of 1s in the whole grid
```

Define a row to be:

- **light** if it has fewer than `sqrt(N)` ones
- **heavy** if it has at least `sqrt(N)` ones

Why does this help?

Because:

- light rows are cheap to enumerate pairwise
- heavy rows are few in number, since each heavy row contains many ones

In fact, there can be at most about:

```text
sqrt(N)
```

heavy rows

because each heavy row contributes at least `sqrt(N)` ones and the total number of ones is only `N`.

---

## Main Idea

### For light rows

Continue using the same pair-counting method as Approach 1.

That is:

- enumerate all pairs of `1`s
- use the `count` map
- add previous occurrences

This costs:

```text
O(X^2)
```

for a row with `X` ones, which is fine when `X < sqrt(N)`.

---

### For heavy rows

Do not enumerate all pairs of columns.

Instead:

1. Store the columns containing `1` in a set
2. Compare this heavy row against every other row
3. Count how many columns they share with `1`
4. If the number of shared `1`s is `f`, then the number of rectangles contributed by that row pair is:

```text
f * (f - 1) / 2
```

This avoids explicit pair generation for dense rows.

---

## Why This Helps

If a row is heavy, it has many `1`s, so `X^2` could be large.

But the number of heavy rows is small.

So instead of paying `O(X^2)` for a heavy row, we pay about:

```text
O(R * C)
```

or more precisely linear-in-other-row-sizes work per heavy row.

This creates a better total bound when the number of ones is bounded.

---

## Preprocessing

Before applying the heavy/light strategy, collect the column positions of `1`s for each row.

For each row `r`, store:

```text
rows.get(r)
```

as a list of columns where `grid[r][c] == 1`.

Also compute:

```text
N = total number of ones
sqrtN = floor(sqrt(N))
```

These values guide the classification of rows.

---

## Java Implementation

```java
class Solution {
    public int countCornerRectangles(int[][] grid) {
        List<List<Integer>> rows = new ArrayList();
        int N = 0;

        for (int r = 0; r < grid.length; ++r) {
            rows.add(new ArrayList());
            for (int c = 0; c < grid[r].length; ++c)
                if (grid[r][c] == 1) {
                    rows.get(r).add(c);
                    N++;
                }
        }

        int sqrtN = (int) Math.sqrt(N);
        int ans = 0;
        Map<Integer, Integer> count = new HashMap();

        for (int r = 0; r < grid.length; ++r) {
            if (rows.get(r).size() >= sqrtN) {
                Set<Integer> target = new HashSet(rows.get(r));

                for (int r2 = 0; r2 < grid.length; ++r2) {
                    if (r2 <= r && rows.get(r2).size() >= sqrtN) {
                        continue;
                    }

                    int found = 0;
                    for (int c2 : rows.get(r2)) {
                        if (target.contains(c2)) {
                            found++;
                        }
                    }

                    ans += found * (found - 1) / 2;
                }
            } else {
                for (int i1 = 0; i1 < rows.get(r).size(); ++i1) {
                    int c1 = rows.get(r).get(i1);
                    for (int i2 = i1 + 1; i2 < rows.get(r).size(); ++i2) {
                        int c2 = rows.get(r).get(i2);
                        int ct = count.getOrDefault(200 * c1 + c2, 0);
                        ans += ct;
                        count.put(200 * c1 + c2, ct + 1);
                    }
                }
            }
        }

        return ans;
    }
}
```

---

## Important Note About the Code

In the provided code snippet, the condition:

```java
rows.get(r2).size() >= sqrt
```

appears to use `sqrt`, but the variable defined earlier is:

```java
sqrtN
```

So that line should conceptually refer to `sqrtN`.

The intended logic is:

- skip double-counting heavy-heavy row pairs
- only count each such pair once

---

## Complexity Analysis

Let:

- `R` = number of rows
- `C` = number of columns
- `N` = total number of `1`s in the grid

### Time Complexity

The provided explanation states the total complexity as:

```text
O(N * sqrt(N) + R * C)
```

The intuition is:

- light rows are handled by pair enumeration, but since each light row has at most `sqrt(N)` ones, this part is bounded efficiently
- heavy rows are few (at most about `sqrt(N)`), and each heavy row can be compared against all rows in near-linear time

So the total becomes:

```text
O(N * sqrt(N) + R * C)
```

### Space Complexity

We store:

- the `rows` structure: `O(N)`
- a temporary set for a heavy row
- the `count` map over column pairs: up to `O(C^2)`

So the given bound is:

```text
O(N + R + C^2)
```

---

# Comparing the Two Approaches

| Approach             | Main Idea                                                             |          Time Complexity | Space Complexity |
| -------------------- | --------------------------------------------------------------------- | -----------------------: | ---------------: |
| Count Corners        | Count repeated column pairs across rows                               |             `O(R * C^2)` |         `O(C^2)` |
| Heavy and Light Rows | Use pair counting for sparse rows and row intersection for dense rows | `O(N * sqrt(N) + R * C)` | `O(N + R + C^2)` |

---

# When to Prefer Each Approach

## Count Corners

Prefer this when:

- the matrix is not too large
- you want the simplest correct solution
- columns are limited
- row density is moderate

This approach is easy to implement and explain.

---

## Heavy and Light Rows

Prefer this when:

- the number of ones is relatively small compared to `R * C`
- some rows may be very dense
- you want a more optimized solution

This approach is more advanced, but it avoids the worst-case pair enumeration cost for dense rows.

---

# Key Takeaways

## 1. Rectangles come from shared column pairs

The essential combinatorial idea is:

> Two rows form corner rectangles according to how many columns they both have as `1`.

If two rows share `k` such columns, they contribute:

```text
C(k, 2)
```

rectangles.

---

## 2. Approach 1 counts by column-pair frequency

Every time a new row contains a pair `(c1, c2)`, all previous rows with that pair create new rectangles.

---

## 3. Approach 2 separates sparse and dense rows

- sparse rows → enumerate pairs
- dense rows → intersect row supports

This gives a better asymptotic bound when the total number of ones is limited.

---

## 4. The problem is fundamentally combinatorial

Even though it looks geometric, the solution is really about counting repeated patterns of `1`s.

---

# Final Insight

A corner rectangle does not require scanning the whole interior.

It only depends on whether two rows share two columns with `1`s.

That is why the problem reduces to either:

- counting column pairs across rows, or
- counting shared ones across row pairs

Once that observation is made, the rest is just choosing the most efficient way to count those combinations.
