# 2509. Cycle Length Queries in a Tree

## Problem summary

We are given a complete binary tree with:

```text
2^n - 1
```

nodes.

The tree uses the usual heap-style numbering:

- root = `1`
- left child of `x` = `2 * x`
- right child of `x` = `2 * x + 1`

For each query `[a, b]`:

1. temporarily add an edge between nodes `a` and `b`
2. compute the length of the cycle created
3. remove that added edge

We must return the cycle length for each query.

---

# Key observation

A tree already has exactly one simple path between any two nodes.

If we add one extra edge between `a` and `b`, the new cycle is simply:

- the original unique path from `a` to `b`
- plus the newly added edge `(a, b)`

So the cycle length is:

```text
distance(a, b) + 1
```

Thus, the whole problem reduces to computing the distance between two nodes in a complete binary tree.

---

# Important structural property of this tree

Since parent of node `x` is:

```text
x / 2
```

(using integer division),

we can move any node upward toward the root by repeatedly dividing by `2`.

That makes Lowest Common Ancestor (LCA) computation extremely simple.

---

# Approach 1: Move both nodes upward until they meet (recommended)

## Intuition

To compute the distance between `a` and `b`, repeatedly move the larger node upward.

Why does that work?

Because in this numbering scheme:

- deeper nodes always have larger values than their ancestors
- dividing by `2` moves directly to the parent

So if `a > b`, then `a` cannot be an ancestor of `b`, and moving `a` upward is the right thing to do.
Likewise for `b`.

Each upward move contributes `1` to the path length.

Once both nodes become equal, we have reached their LCA.

If the number of upward moves is `steps`, then:

```text
distance(a, b) = steps
cycle length = steps + 1
```

---

## Algorithm

For each query `[a, b]`:

1. initialize `steps = 0`
2. while `a != b`:
   - if `a > b`, set `a = a / 2`
   - else set `b = b / 2`
   - increment `steps`
3. answer is:

```text
steps + 1
```

---

## Java code

```java
class Solution {
    public int[] cycleLengthQueries(int n, int[][] queries) {
        int[] answer = new int[queries.length];

        for (int i = 0; i < queries.length; i++) {
            int a = queries[i][0];
            int b = queries[i][1];
            int steps = 0;

            while (a != b) {
                if (a > b) {
                    a /= 2;
                } else {
                    b /= 2;
                }
                steps++;
            }

            answer[i] = steps + 1;
        }

        return answer;
    }
}
```

---

## Why this works

The unique tree path from `a` to `b` goes:

- upward from `a` to `LCA(a,b)`
- upward from `b` to `LCA(a,b)`

Every loop iteration moves one endpoint one level upward, counting one tree edge on that path.

When the two values become equal, they meet at the LCA.

So the number of moves equals the tree distance, and adding the extra edge gives the cycle length.

---

## Complexity

Let `m = queries.length`.

The height of the tree is at most `n`, and `n <= 30`.

So for each query, the loop runs at most `O(n)` times, which is at most `30`.

### Time complexity

```text
O(m * n)
```

Since `n <= 30`, this is effectively:

```text
O(m)
```

in practice.

### Space complexity

```text
O(1)
```

excluding the output array.

---

# Approach 2: Explicit LCA by depth alignment

## Intuition

Another standard way to compute distance is:

1. compute depth of `a`
2. compute depth of `b`
3. move the deeper node upward until both depths match
4. move both upward together until they meet
5. use the distance formula

This is a more textbook LCA approach.

Because the tree is represented implicitly, node depth is simply:

```text
depth(x) = number of times we can divide by 2 before reaching 1
```

---

## Distance formula

If:

- `da = depth(a)`
- `db = depth(b)`
- `dlca = depth(LCA(a,b))`

then:

```text
distance(a, b) = da + db - 2 * dlca
cycle length = distance + 1
```

---

## Algorithm

For each query `[a, b]`:

1. compute `depthA`
2. compute `depthB`
3. move the deeper node upward until depths are equal
4. while `a != b`, move both upward
5. count total moves
6. return `moves + 1`

---

## Java code

```java
class Solution {
    public int[] cycleLengthQueries(int n, int[][] queries) {
        int[] answer = new int[queries.length];

        for (int i = 0; i < queries.length; i++) {
            int a = queries[i][0];
            int b = queries[i][1];

            int depthA = depth(a);
            int depthB = depth(b);
            int dist = 0;

            while (depthA > depthB) {
                a /= 2;
                depthA--;
                dist++;
            }

            while (depthB > depthA) {
                b /= 2;
                depthB--;
                dist++;
            }

            while (a != b) {
                a /= 2;
                b /= 2;
                dist += 2;
            }

            answer[i] = dist + 1;
        }

        return answer;
    }

    private int depth(int x) {
        int d = 0;
        while (x > 1) {
            x /= 2;
            d++;
        }
        return d;
    }
}
```

---

## Complexity

For each query:

- computing depth is `O(n)`
- upward moves are also `O(n)`

So total per query is:

```text
O(n)
```

Overall:

```text
O(m * n)
```

Again, since `n <= 30`, this is very efficient.

Space:

```text
O(1)
```

---

# Approach 3: Build ancestor sets / paths to root

## Intuition

Another way is to explicitly record the ancestor chain of one node, then climb the other node until you hit a common ancestor.

For example:

- build all ancestors of `a`
- then move `b` upward until it appears in that ancestor set
- that meeting point is the LCA

After finding the LCA, compute distance.

This works, but it is more memory-heavy and not as elegant as Approach 1.

---

## Algorithm

For each query `[a, b]`:

1. store path from `a` to root
2. move `b` upward until it hits a stored ancestor
3. count distances from both sides
4. return total distance + 1

---

## Java code

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    public int[] cycleLengthQueries(int n, int[][] queries) {
        int[] answer = new int[queries.length];

        for (int i = 0; i < queries.length; i++) {
            int a = queries[i][0];
            int b = queries[i][1];

            Map<Integer, Integer> distFromA = new HashMap<>();
            int dist = 0;
            int x = a;

            while (x > 0) {
                distFromA.put(x, dist);
                x /= 2;
                dist++;
            }

            int y = b;
            int distFromB = 0;

            while (!distFromA.containsKey(y)) {
                y /= 2;
                distFromB++;
            }

            int treeDistance = distFromA.get(y) + distFromB;
            answer[i] = treeDistance + 1;
        }

        return answer;
    }
}
```

---

## Complexity

For each query, ancestor chain length is at most tree height:

```text
O(n)
```

Overall:

```text
O(m * n)
```

Space per query:

```text
O(n)
```

This is still fine because `n <= 30`, but less clean than Approach 1.

---

# Approach 4: Binary lifting (theoretical overkill)

## Intuition

If this were a general tree with many LCA queries, a natural approach would be **binary lifting**.

We would precompute jump pointers:

```text
up[node][j] = 2^j-th ancestor of node
```

and then answer each query in `O(log n)`.

However, in this specific problem, binary lifting is unnecessary because:

1. the tree is implicit
2. parent is just `x / 2`
3. the height is tiny (`n <= 30`)

So repeated division is already simpler and just as fast.

Still, for completeness, binary lifting is a possible general framework.

---

## Why it is unnecessary here

The maximum number of upward moves per query is only about `30`.

That is already constant for practical purposes.

So any heavy preprocessing would only add complexity without benefit.

---

# Best approach

The best solution is:

## **Approach 1: Move the larger node upward until both meet**

Why:

- shortest code
- no extra data structures
- directly exploits the heap-style numbering
- extremely fast under the constraints

---

# Common mistake

A common misunderstanding is to think we must actually modify the tree for every query.

That is not needed.

Adding edge `(a, b)` creates a cycle whose length depends only on the existing path between `a` and `b`.

So we only need:

```text
distance(a, b) + 1
```

No explicit graph construction is necessary.

---

# Final recommended solution

```java
class Solution {
    public int[] cycleLengthQueries(int n, int[][] queries) {
        int[] answer = new int[queries.length];

        for (int i = 0; i < queries.length; i++) {
            int a = queries[i][0];
            int b = queries[i][1];
            int steps = 0;

            while (a != b) {
                if (a > b) {
                    a /= 2;
                } else {
                    b /= 2;
                }
                steps++;
            }

            answer[i] = steps + 1;
        }

        return answer;
    }
}
```

## Complexity

- **Time:** `O(m * n)`, with `n <= 30`, so effectively `O(m)`
- **Space:** `O(1)`

This is the cleanest and most efficient solution for the problem.
