# 1409. Queries on a Permutation With Key

## Problem Restatement

We are given:

- an array `queries`
- an integer `m`

Initially, the permutation is:

```text
P = [1, 2, 3, ..., m]
```

For each query value `queries[i]`:

1. find its current position in `P` using **0-based indexing**
2. store that position in the answer
3. move that value to the front of `P`

Return the array of recorded positions.

---

## Core Idea

This is a **simulation** problem.

The permutation changes after every query, so the position of a value depends on all earlier operations.

The simplest way is to literally maintain the current permutation and simulate:

- search for the query value
- record its index
- remove it
- insert it at the front

Because:

```text
1 <= m <= 1000
1 <= queries.length <= m
```

even the straightforward simulation is fast enough.

Still, there are multiple useful ways to think about and solve it.

---

# Approach 1 — Direct Simulation with List / ArrayList

## Intuition

Maintain the current permutation as a dynamic list.

For each query:

- scan the list to find the query value
- record the index
- remove that value from its current position
- insert it at the beginning

This is the most natural and easiest-to-understand solution.

---

## Algorithm

1. Build a list containing:
   ```text
   [1, 2, 3, ..., m]
   ```
2. For each query:
   - linearly search for the query in the list
   - record the index
   - remove the value from that index
   - insert it at index `0`
3. Return the result array

---

## Java Code

```java
import java.util.*;

class Solution {
    public int[] processQueries(int[] queries, int m) {
        List<Integer> perm = new ArrayList<>();
        for (int i = 1; i <= m; i++) {
            perm.add(i);
        }

        int[] ans = new int[queries.length];

        for (int i = 0; i < queries.length; i++) {
            int q = queries[i];
            int pos = 0;

            while (perm.get(pos) != q) {
                pos++;
            }

            ans[i] = pos;

            perm.remove(pos);
            perm.add(0, q);
        }

        return ans;
    }
}
```

---

## Complexity Analysis

Let:

- `n = queries.length`

Each query may require:

- linear search through up to `m` elements
- shifting elements during remove/insert

So each query costs:

```text
O(m)
```

Overall:

```text
O(n * m)
```

Since `m <= 1000`, this is acceptable.

### Space Complexity

```text
O(m)
```

for the current permutation, plus the output array.

---

# Approach 2 — Simulation with Plain Array

## Intuition

Since the permutation size is small, we can store it in a plain array instead of a list.

For each query:

- find its position in the array
- record the position
- shift elements right to move it to the front

This avoids list method calls and shows the mechanics very explicitly.

---

## Algorithm

1. Initialize array:
   ```text
   perm = [1, 2, 3, ..., m]
   ```
2. For each query:
   - find its index `pos`
   - save `pos` in answer
   - store the value
   - shift all elements from `0..pos-1` one step right
   - place the queried value at index `0`
3. Return answer

---

## Java Code

```java
class Solution {
    public int[] processQueries(int[] queries, int m) {
        int[] perm = new int[m];
        for (int i = 0; i < m; i++) {
            perm[i] = i + 1;
        }

        int[] ans = new int[queries.length];

        for (int i = 0; i < queries.length; i++) {
            int q = queries[i];
            int pos = 0;

            while (perm[pos] != q) {
                pos++;
            }

            ans[i] = pos;

            int value = perm[pos];
            while (pos > 0) {
                perm[pos] = perm[pos - 1];
                pos--;
            }
            perm[0] = value;
        }

        return ans;
    }
}
```

---

## Complexity Analysis

Each query may require a linear search and a shift:

```text
O(m)
```

Overall:

```text
O(n * m)
```

### Space Complexity

```text
O(m)
```

---

# Approach 3 — Position Array + Simulation of Ranks

## Intuition

Because the values are always between `1` and `m`, we can maintain the current position of each value.

However, if one value moves to the front, positions of many others also change.

So whenever we process a query `q`:

- answer is `pos[q]`
- every value currently before `q` shifts right by 1
- `q` becomes position `0`

This avoids storing the whole permutation explicitly and instead maintains positions of values.

It is still `O(m)` per query, but it is an interesting alternative perspective.

---

## Algorithm

1. Initialize:
   ```text
   pos[x] = x - 1
   ```
   for `x = 1..m`
2. For each query `q`:
   - let `current = pos[q]`
   - record `current`
   - for every value `x`:
     - if `pos[x] < current`, increment `pos[x]`
   - set `pos[q] = 0`
3. Return answer

---

## Java Code

```java
class Solution {
    public int[] processQueries(int[] queries, int m) {
        int[] pos = new int[m + 1];
        for (int x = 1; x <= m; x++) {
            pos[x] = x - 1;
        }

        int[] ans = new int[queries.length];

        for (int i = 0; i < queries.length; i++) {
            int q = queries[i];
            int current = pos[q];
            ans[i] = current;

            for (int x = 1; x <= m; x++) {
                if (pos[x] < current) {
                    pos[x]++;
                }
            }

            pos[q] = 0;
        }

        return ans;
    }
}
```

---

## Complexity Analysis

For each query, we may scan all `m` values:

```text
O(m)
```

Overall:

```text
O(n * m)
```

### Space Complexity

```text
O(m)
```

---

# Approach 4 — Binary Indexed Tree (Fenwick Tree) + Indexed Positions

## Intuition

This is the more advanced solution.

Instead of physically moving elements in a list, we assign each value a virtual position.

### Trick

Reserve space in front for future moves.

Suppose `n = queries.length`.

Initially place values `1..m` at positions:

```text
n + 1, n + 2, ..., n + m
```

Then each time we move a queried value to the front, assign it a new smaller position:

```text
n, n-1, n-2, ...
```

Now we need to answer:

> how many active elements are before the current position of the queried value?

That is a prefix sum problem.

A **Fenwick Tree** supports:

- prefix sum queries
- point updates

in:

```text
O(log(m + n))
```

So the total becomes much faster.

This approach is especially valuable when the constraints are larger.

---

## Data Structure Setup

- `pos[value]` = current virtual position of that value
- Fenwick Tree stores whether a position is occupied
- Initially:
  - value `1` at position `n + 1`
  - value `2` at position `n + 2`
  - ...
  - value `m` at position `n + m`
- `front = n`

For a query `q`:

1. current position = `pos[q]`
2. number of elements before it =
   ```text
   query(pos[q] - 1)
   ```
3. remove it from old position
4. place it at `front`
5. decrement `front`

---

## Java Code

```java
class Solution {
    public int[] processQueries(int[] queries, int m) {
        int n = queries.length;
        int size = n + m + 2;

        Fenwick bit = new Fenwick(size);
        int[] pos = new int[m + 1];

        for (int value = 1; value <= m; value++) {
            int p = n + value;
            pos[value] = p;
            bit.add(p, 1);
        }

        int front = n;
        int[] ans = new int[n];

        for (int i = 0; i < n; i++) {
            int q = queries[i];
            int currentPos = pos[q];

            ans[i] = bit.sum(currentPos - 1);

            bit.add(currentPos, -1);
            pos[q] = front;
            bit.add(front, 1);
            front--;
        }

        return ans;
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

        int sum(int index) {
            int res = 0;
            while (index > 0) {
                res += tree[index];
                index -= index & -index;
            }
            return res;
        }
    }
}
```

---

## Complexity Analysis

Let:

- `n = queries.length`

Each query does:

- one prefix sum
- two point updates

Each in:

```text
O(log(m + n))
```

So total:

```text
O((n + m) log(m + n))
```

### Space Complexity

```text
O(m + n)
```

for the Fenwick tree and position array.

---

# Worked Example

## Example 1

```text
queries = [3,1,2,1], m = 5
```

Initial permutation:

```text
[1,2,3,4,5]
```

### Query 3

Position of `3` is `2`

Answer so far:

```text
[2]
```

Move `3` to front:

```text
[3,1,2,4,5]
```

### Query 1

Position of `1` is `1`

Answer so far:

```text
[2,1]
```

Move `1` to front:

```text
[1,3,2,4,5]
```

### Query 2

Position of `2` is `2`

Answer so far:

```text
[2,1,2]
```

Move `2` to front:

```text
[2,1,3,4,5]
```

### Query 1

Position of `1` is `1`

Final answer:

```text
[2,1,2,1]
```

---

# Correctness Reasoning

## Claim 1

Each simulation-based approach always maintains the exact current permutation after every query.

### Why?

For each query value:

- we find its current index
- record that index
- remove that exact value from its current position
- place it at the front

This matches the problem statement exactly.

So after each query, the maintained permutation is correct.

---

## Claim 2

The recorded index for each query is correct.

### Why?

The value is searched before any modification for that query.

So the found index is exactly its position in the current permutation at that time.

That is the required output for that query.

---

## Claim 3

In the Fenwick Tree approach, the prefix sum before the current virtual position equals the value’s current index in the permutation.

### Why?

The Fenwick Tree stores active occupied positions in sorted order.

The number of active positions before `pos[q]` is exactly how many values appear before `q` in the permutation.

Hence:

```text
bit.sum(pos[q] - 1)
```

is the correct 0-based index.

---

# Comparison of Approaches

## Approach 1 — ArrayList simulation

Pros:

- easiest to write
- very intuitive

Cons:

- `remove` and `add(0, x)` cause shifts

Best for:

- simplest accepted solution

---

## Approach 2 — Plain array simulation

Pros:

- explicit and low-level
- easy to understand how movement works

Cons:

- manual shifting logic

---

## Approach 3 — Position array update

Pros:

- interesting alternative viewpoint
- avoids storing the actual permutation

Cons:

- still `O(m)` per query

---

## Approach 4 — Fenwick Tree

Pros:

- asymptotically best
- scalable
- elegant data-structure solution

Cons:

- more complex than necessary for current constraints

---

# Final Recommended Solution

For these constraints, **Approach 1** is the most practical and readable.

```java
import java.util.*;

class Solution {
    public int[] processQueries(int[] queries, int m) {
        List<Integer> perm = new ArrayList<>();
        for (int i = 1; i <= m; i++) {
            perm.add(i);
        }

        int[] ans = new int[queries.length];

        for (int i = 0; i < queries.length; i++) {
            int q = queries[i];
            int pos = 0;

            while (perm.get(pos) != q) {
                pos++;
            }

            ans[i] = pos;
            perm.remove(pos);
            perm.add(0, q);
        }

        return ans;
    }
}
```

---

# Complexity Summary

## Approach 1

```text
Time:  O(n * m)
Space: O(m)
```

## Approach 2

```text
Time:  O(n * m)
Space: O(m)
```

## Approach 3

```text
Time:  O(n * m)
Space: O(m)
```

## Approach 4

```text
Time:  O((n + m) log(n + m))
Space: O(n + m)
```

---

# Final Takeaway

This problem is fundamentally about **maintaining a changing permutation**.

For the given constraints, direct simulation is already enough.

The clean mental model is:

1. keep the current permutation
2. locate the queried value
3. record its position
4. move it to the front

That is exactly what the problem asks us to simulate.
