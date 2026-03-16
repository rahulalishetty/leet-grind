# Number of Distinct Islands II — Canonical Hash Approach

## Overview

This problem extends the earlier distinct-islands problem.

In the simpler version, two islands were considered the same only if one could be translated to match the other.

Here, the definition is broader:

Two islands are considered the same if one can be transformed into the other using:

- translation
- rotation by 90°, 180°, or 270°
- reflection

That means the representation we choose for an island must be invariant under all of those transformations.

This document explains the **Canonical Hash** approach in detail.

---

# Core Idea

For each island:

1. collect all of its cells as local coordinates
2. generate all 8 valid transformations of the island
3. normalize each transformed version by translating it to a fixed origin
4. choose one canonical representation among those 8 normalized forms
5. insert that canonical representation into a set

At the end, the size of the set is the number of distinct island shapes.

---

# Why 8 Transformations?

For a 2D shape on a grid, considering rotation and reflection gives exactly **8 symmetries**.

These are the transforms of a point `(x, y)`:

```text
(x, y)
(-x, y)
(x, -y)
(-x, -y)
(y, x)
(-y, x)
(y, -x)
(-y, -x)
```

These correspond to all combinations needed to cover:

- original orientation
- rotations
- reflections
- reflected rotations

So if two islands are equivalent under any allowed operation, one of these 8 transformed coordinate sets will match.

---

# Why We Need a Canonical Representation

Suppose two identical shapes appear in different places in the grid.

Their raw coordinates differ because their absolute positions differ.

Suppose two identical shapes also appear under rotation or reflection.

Now the coordinates differ even more.

So we need to convert every island into a representation that ignores:

- absolute location
- orientation
- mirrored orientation

That canonical representation is what we insert into a set.

If two islands have the same canonical representation, they are the same island shape under the problem rules.

---

# Step 1: Explore Each Island

We first perform DFS to collect the cells belonging to one island.

This part is standard flood fill.

Every land cell in an island is visited once and added to the current shape.

---

# Step 2: Represent the Shape

There are two implementations shown:

- **Python** uses complex numbers
- **Java** uses integer coordinate manipulation

Both do the same thing conceptually.

---

## Python Representation with Complex Numbers

A cell `(r, c)` is represented as:

```text
complex(r, c)
```

So:

- real part = row
- imaginary part = column

This is elegant because rotating a point by 90° around the origin can be done by multiplying by `1j`.

That makes the implementation compact and mathematically clean.

---

## Java Representation with Encoded Integers

A cell `(r, c)` is stored as:

```text
r * number_of_columns + c
```

Later, the coordinate can be recovered by:

```text
x = z / number_of_columns
y = z % number_of_columns
```

This is a compact way to store coordinates in a single integer.

---

# Step 3: Generate All 8 Transformations

For each island shape, we create all 8 transformed versions.

Each transformed version corresponds to one symmetry of the square.

After generating one transformed version, we normalize it so that its position becomes comparable to other islands.

---

# Step 4: Normalize by Translation

After rotation or reflection, the coordinates may become negative or shifted arbitrarily.

So we translate the transformed shape so that its bottom-left-most coordinate becomes `(0, 0)`.

Practically, that means:

- find the minimum `x`
- find the minimum `y`
- subtract those minimum values from every point

This removes the effect of translation.

Now the shape is expressed in a location-independent way.

---

# Step 5: Choose the Canonical Form

We now have 8 normalized versions of the same island, one for each transform.

Among these 8 possibilities, we choose one fixed representative.

The provided implementations choose the **maximum** lexicographic representation among the 8 candidates.

You could also choose the minimum as long as you are consistent.

What matters is:

- equivalent islands produce the same set of 8 transformed normalized forms
- choosing the same rule every time gives the same canonical output

---

# Step 6: Insert Canonical Forms Into a Set

Once an island is converted into its canonical representation, insert it into a hash set.

Since sets remove duplicates automatically:

- identical canonical forms collapse together
- the number of entries in the set equals the number of distinct island shapes

---

# Python Implementation

```python
class Solution(object):
    def numDistinctIslands2(self, grid):
        seen = set()
        def explore(r, c):
            if (0 <= r < len(grid) and 0 <= c < len(grid[0]) and
                    grid[r][c] and (r, c) not in seen):
                seen.add((r, c))
                shape.add(complex(r, c))
                explore(r+1, c)
                explore(r-1, c)
                explore(r, c+1)
                explore(r, c-1)

        def canonical(shape):
            def translate(shape):
                w = complex(min(z.real for z in shape),
                            min(z.imag for z in shape))
                return sorted(str(z-w) for z in shape)

            ans = None
            for k in xrange(4):
                ans = max(ans, translate([z * (1j)**k for z in shape]))
                ans = max(ans,  translate([complex(z.imag, z.real) * (1j)**k
                                           for z in shape]))
            return tuple(ans)

        shapes = set()
        for r in range(len(grid)):
            for c in range(len(grid[0])):
                shape = set()
                explore(r, c)
                if shape:
                    shapes.add(canonical(shape))

        return len(shapes)
```

---

# Python Code Walkthrough

## 1. `seen` set

```python
seen = set()
```

This keeps track of all grid cells already visited.

Without it, DFS would revisit the same land repeatedly.

---

## 2. DFS exploration

```python
def explore(r, c):
    if (0 <= r < len(grid) and 0 <= c < len(grid[0]) and
            grid[r][c] and (r, c) not in seen):
        seen.add((r, c))
        shape.add(complex(r, c))
        explore(r+1, c)
        explore(r-1, c)
        explore(r, c+1)
        explore(r, c-1)
```

This is a standard DFS.

For each valid land cell:

- mark it visited
- add it to the current island shape
- recurse in four directions

Notice that `shape` is a set of complex numbers.

---

## 3. `canonical(shape)`

This function computes the canonical representation of one island.

It does the heavy lifting.

---

## 4. Inner `translate(shape)` helper

```python
def translate(shape):
    w = complex(min(z.real for z in shape),
                min(z.imag for z in shape))
    return sorted(str(z-w) for z in shape)
```

This function normalizes a transformed shape.

It finds the smallest row and smallest column among the transformed points, creates that offset as `w`, and subtracts it from every point.

Then it sorts the normalized points so that the representation becomes deterministic.

The returned sorted list can now be compared lexicographically.

---

## 5. Generate rotated versions

```python
for k in xrange(4):
    ans = max(ans, translate([z * (1j)**k for z in shape]))
```

Here:

- `k = 0, 1, 2, 3`
- multiplying by `(1j)**k` rotates the point by `0°, 90°, 180°, 270°`

So this loop covers the 4 rotations.

---

## 6. Generate reflected versions

```python
ans = max(ans, translate([complex(z.imag, z.real) * (1j)**k
                           for z in shape]))
```

The expression:

```python
complex(z.imag, z.real)
```

swaps row and column, which acts like a reflection before rotation.

Then again multiplying by `(1j)**k` generates 4 rotated forms of that reflected version.

So together:

- 4 direct rotations
- 4 reflected rotations

gives all 8 symmetries.

---

## 7. Return canonical tuple

```python
return tuple(ans)
```

The tuple form is hashable, so it can be inserted into a Python set.

---

## 8. Main traversal

```python
shapes = set()
for r in range(len(grid)):
    for c in range(len(grid[0])):
        shape = set()
        explore(r, c)
        if shape:
            shapes.add(canonical(shape))
```

For every cell:

- start with an empty `shape`
- run DFS
- if DFS found an island, compute its canonical form
- insert it into the set of distinct shapes

---

## 9. Final answer

```python
return len(shapes)
```

The size of the set is the number of distinct islands.

---

# Java Implementation

```java
class Solution {
    int[][] grid;
    boolean[][] seen;
    ArrayList<Integer> shape;

    public void explore(int r, int c) {
        if (0 <= r && r < grid.length && 0 <= c && c < grid[0].length &&
                grid[r][c] == 1 && !seen[r][c]) {
            seen[r][c] = true;
            shape.add(r * grid[0].length + c);
            explore(r+1, c);
            explore(r-1, c);
            explore(r, c+1);
            explore(r, c-1);
        }
    }

    public String canonical(ArrayList<Integer> shape) {
        String ans = "";
        int lift = grid.length + grid[0].length;
        int[] out = new int[shape.size()];
        int[] xs = new int[shape.size()];
        int[] ys = new int[shape.size()];

        for (int c = 0; c < 8; ++c) {
            int t = 0;
            for (int z: shape) {
                int x = z / grid[0].length;
                int y = z % grid[0].length;
                //x y, x -y, -x y, -x -y
                //y x, y -x, -y x, -y -x
                xs[t] = c<=1 ? x : c<=3 ? -x : c<=5 ? y : -y;
                ys[t++] = c<=3 ? (c%2==0 ? y : -y) : (c%2==0 ? x : -x);
            }

            int mx = xs[0], my = ys[0];
            for (int x: xs) {
                mx = Math.min(mx, x);
            }
            for (int y: ys) {
                my = Math.min(my, y);
            }

            for (int j = 0; j < shape.size(); ++j) {
                out[j] = (xs[j] - mx) * lift + (ys[j] - my);
            }
            Arrays.sort(out);
            String candidate = Arrays.toString(out);
            if (ans.compareTo(candidate) < 0) {
                ans = candidate;
            }
        }
        return ans;
    }

    public int numDistinctIslands2(int[][] grid) {
        this.grid = grid;
        seen = new boolean[grid.length][grid[0].length];
        Set shapes = new HashSet<String>();

        for (int r = 0; r < grid.length; ++r) {
            for (int c = 0; c < grid[0].length; ++c) {
                shape = new ArrayList();
                explore(r, c);
                if (!shape.isEmpty()) {
                    shapes.add(canonical(shape));
                }
            }
        }

        return shapes.size();
    }
}
```

---

# Java Code Walkthrough

## 1. Stored fields

```java
int[][] grid;
boolean[][] seen;
ArrayList<Integer> shape;
```

- `grid` is the input matrix
- `seen` tracks visited land cells
- `shape` stores the current island as encoded integers

---

## 2. DFS exploration

```java
public void explore(int r, int c) {
    if (0 <= r && r < grid.length && 0 <= c && c < grid[0].length &&
            grid[r][c] == 1 && !seen[r][c]) {
        seen[r][c] = true;
        shape.add(r * grid[0].length + c);
        explore(r+1, c);
        explore(r-1, c);
        explore(r, c+1);
        explore(r, c-1);
    }
}
```

This is the same DFS idea as in Python.

The coordinate `(r, c)` is encoded into one integer:

```java
r * grid[0].length + c
```

This allows compact storage.

---

## 3. `canonical(shape)`

This function computes the canonical representation of the island.

It tries all 8 transforms and picks the lexicographically largest normalized encoding.

---

## 4. Why `lift` is needed

```java
int lift = grid.length + grid[0].length;
```

After normalization, coordinates become non-negative.

To encode `(x, y)` into a single integer uniquely, we use:

```java
x * lift + y
```

The value of `lift` just needs to be large enough so that different `(x, y)` pairs do not collide.

Using `grid.length + grid[0].length` is sufficient here.

---

## 5. Recover original coordinates

```java
int x = z / grid[0].length;
int y = z % grid[0].length;
```

This reverses the earlier encoding.

---

## 6. Generate all 8 transforms

```java
xs[t] = c<=1 ? x : c<=3 ? -x : c<=5 ? y : -y;
ys[t++] = c<=3 ? (c%2==0 ? y : -y) : (c%2==0 ? x : -x);
```

This block maps each coordinate `(x, y)` into one of the 8 symmetric forms.

The comment in the code describes them:

```text
x y, x -y, -x y, -x -y
y x, y -x, -y x, -y -x
```

Together, these cover all rotations and reflections.

---

## 7. Find translation offset

```java
int mx = xs[0], my = ys[0];
for (int x: xs) {
    mx = Math.min(mx, x);
}
for (int y: ys) {
    my = Math.min(my, y);
}
```

This finds the smallest x and y in the transformed shape.

Subtracting them shifts the whole transformed island so its minimum coordinate becomes `(0, 0)`.

---

## 8. Normalize transformed coordinates

```java
for (int j = 0; j < shape.size(); ++j) {
    out[j] = (xs[j] - mx) * lift + (ys[j] - my);
}
Arrays.sort(out);
```

Now each transformed point is normalized and converted to a stable sortable integer form.

Sorting is necessary so that the same shape always produces the same sequence regardless of traversal order inside this transformation step.

---

## 9. Convert to candidate string

```java
String candidate = Arrays.toString(out);
if (ans.compareTo(candidate) < 0) {
    ans = candidate;
}
```

The normalized sorted representation is converted into a string.

Among the 8 candidates, we keep the lexicographically largest one.

That becomes the canonical encoding of the island.

---

## 10. Main method

```java
public int numDistinctIslands2(int[][] grid) {
    this.grid = grid;
    seen = new boolean[grid.length][grid[0].length];
    Set shapes = new HashSet<String>();

    for (int r = 0; r < grid.length; ++r) {
        for (int c = 0; c < grid[0].length; ++c) {
            shape = new ArrayList();
            explore(r, c);
            if (!shape.isEmpty()) {
                shapes.add(canonical(shape));
            }
        }
    }

    return shapes.size();
}
```

This loops over the grid, extracts islands, converts each to canonical form, and inserts the result into a set.

The final set size is the answer.

---

# Why This Approach Is Correct

Two islands should be considered identical if one can be transformed into the other by any allowed symmetry.

This algorithm explicitly checks all such symmetries.

For each island:

- generate all 8 valid transformed forms
- normalize each one by translation
- choose one canonical representative

Therefore:

- if two islands are equivalent under rotation/reflection/translation, they will produce the same canonical representation
- if they are not equivalent, they will not produce the same canonical representation

That is why set-based deduplication works.

---

# Complexity Analysis

Let:

- `R` = number of rows
- `C` = number of columns

## Time Complexity

```text
O(R * C * log(R * C))
```

### Why?

We visit every grid square once during DFS.

Each land square belongs to at most one island.

For each island, we compute 8 transformed versions and sort the normalized coordinates.

Across the whole grid, the total amount of sorting work contributes the logarithmic factor.

So the total complexity is:

```text
O(R * C * log(R * C))
```

---

## Space Complexity

```text
O(R * C)
```

Why?

- `seen` requires `O(R * C)`
- the collected island cells and canonical representations together also use linear space in the total number of cells

So auxiliary space is linear in the size of the grid.

---

# Practical Interpretation

This is a strong canonicalization pattern:

1. collect an object
2. enumerate all equivalent transformed forms
3. normalize each form
4. pick one canonical representative
5. hash that representative

This pattern is broader than this one problem. It appears in:

- graph isomorphism style normalization
- geometry problems
- shape matching
- pattern deduplication
- symbolic state reduction

---

# Key Takeaways

- Translation alone is not enough in this problem.
- We must also account for rotation and reflection.
- Every island is transformed in all 8 valid ways.
- Each transformed version is normalized to remove position dependence.
- The canonical representation is chosen consistently from among those 8 normalized versions.
- A set of canonical forms gives the number of distinct islands.

---

# Summary

- Use DFS to collect each island.
- Represent the island as coordinates.
- Generate all 8 rotations/reflections.
- Normalize each transformed version by shifting it to the origin.
- Sort the normalized coordinates to make the representation deterministic.
- Choose one canonical representation among the 8.
- Insert into a set.
- Return the size of the set.

## Final Complexity

- **Time:** `O(R * C * log(R * C))`
- **Space:** `O(R * C)`
