# Segment Trees

Let’s break it down step by step, focusing on structure, intuition, algorithms, and implementation — and relate it to what you already know (prefix sums, BIT, and sparse tables).

---

## 1. Motivation — Why Segment Trees Exist

We often need to:

- Query some aggregate (sum, min, max, gcd, etc.) over a range `[a, b]`
- Update individual array elements dynamically

### Different Data Structures and Trade-offs

| Structure           | Query Time | Update Time | Supports Dynamic Updates? | Supports Non-Sum Ops?         |
| ------------------- | ---------- | ----------- | ------------------------- | ----------------------------- |
| Prefix Sum Array    | O(1)       | O(n)        | ❌                        | Only sum                      |
| Binary Indexed Tree | O(log n)   | O(log n)    | ✅                        | Mostly sum                    |
| Sparse Table        | O(1)       | O(n)        | ❌                        | Any idempotent op             |
| **Segment Tree**    | O(log n)   | O(log n)    | ✅                        | ✅ (sum, min, max, gcd, etc.) |

The Segment Tree is the most general of the lot — it trades a bit of space and implementation complexity for generality and flexibility.

---

## 2. Concept — A Tree over Array Ranges

A Segment Tree is a binary tree where:

- Each leaf node represents one element of the array.
- Each internal node represents a range that is the union of its children’s ranges.
- Each node stores an aggregate value (like the sum or minimum) of that range.

---

## 3. Example Array

**Index:** 0 1 2 3 4 5 6 7
**Array:** 5 8 6 3 2 7 2 6

For simplicity, let `n = 8` (a power of two).

---

## 4. Structure of the Segment Tree

```plaintext
         [0,7] = 39
      ┌────────────┴────────────┐
     [0,3]=22                     [4,7]=17
     ┌──────┴──────┐             ┌──────┴──────┐
  [0,1]=13      [2,3]=9       [4,5]=9        [6,7]=8
  ┌──┴──┐ ┌──┴──┐ ┌──┴──┐ ┌──┴──┐
  [0]=5 [1]=8 [2]=6 [3]=3 [4]=2 [5]=7 [6]=2 [7]=6
```

### Observations:

- Leaves (bottom row) represent the array itself.
- Every internal node is the sum of its two children.
- The root represents the sum of the entire array = 39.

---

## 5. Range Query Intuition

Let’s find the sum of range `[2,7]`:

**Array indices:** 0 1 [2 3 4 5 6 7]
**Array values:** 5 8 [6 3 2 7 2 6]

Instead of summing each individually, we use existing nodes from the tree that exactly cover this range.

In the tree:

- `[2,3] = 9` (sum of elements 2–3)
- `[4,7] = 17` (sum of elements 4–7)
  → `[2,7] = 9 + 17 = 26`

Each level contributes at most two nodes to a query → total = **O(log n)**.

---

## 6. Updating an Element

Suppose we change `array[5]` (value `7`) → `10`.

We update the leaf node `[5]`, then recompute all parent nodes up to the root.

**Path of update:**

`[5] → [4,5] → [4,7] → [0,7]`

Each level has one node on this path → **O(log n)** updates.

---

## 7. Array Representation (Bottom-Up Implementation)

Segment trees are usually implemented in arrays for efficiency:

- Total array size = `2n`
- Leaves start from index `n`
- Internal nodes are above them

For the same example:

**Tree array (1-indexed for clarity):**
`Index:  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15`
`Value: 39 22 17 13  9  9  8  5  8  6  3  2  7  2  6`

### Relations:

- `Parent(i) = i / 2`
- `Left(i) = 2 * i`
- `Right(i) = 2 * i + 1`

---

## 8. Range Sum Query Implementation

```cpp
int sum(int a, int b) {
  a += n; // shift to leaf index
  b += n;
  int s = 0;
  while (a <= b) {
  if ((a % 2) == 1) s += tree[a++]; // a is right child
  if ((b % 2) == 0) s += tree[b--]; // b is left child
  a /= 2; // move to parent
  b /= 2;
  }
  return s;
}
```

### How it works:

- Initially `a` and `b` are at leaves `[a+n]` and `[b+n]`.
- Each step climbs up one level.
- Before climbing:
  - If `a` is a right child, its range isn’t included above → add it and move right.
  - If `b` is a left child, its range isn’t included above → add it and move left.
- When `a > b`, the range is fully covered.

**Complexity:** Each level processes at most 2 nodes → **O(log n)**.

---

## 9. Update Operation

```cpp
void add(int k, int x) {
  k += n; // move to leaf
  tree[k] += x; // update leaf
  for (k /= 2; k >= 1; k /= 2)
  tree[k] = tree[2*k] + tree[2*k + 1]; // recompute parents
}
```

### How it works:

- Start at the updated leaf.
- Go upward, recalculating each parent as the sum of its children.
- **O(log n)** updates.

---

## 10. Generalization Beyond Sums

Segment trees can handle any associative operation that can be merged from two parts, e.g.:

| Operation   | Combine Function | Node Value Stores       |
| ----------- | ---------------- | ----------------------- | --------------- |
| Sum         | `a + b`          | Total sum               |
| Minimum     | `min(a, b)`      | Smallest value          |
| Maximum     | `max(a, b)`      | Largest value           |
| GCD         | `gcd(a, b)`      | Greatest common divisor |
| Bitwise ops | `a & b`, `a      | b`                      | Bitwise results |

**Rule:** If you can combine two child results to get the parent’s result → it’s a valid segment tree operation.

---

## 11. Example: Minimum Query Tree

For array: `5 8 6 3 1 7 2 6`

```plaintext
       1
     ┌────────┴────────┐
    3                   1
   ┌───┴───┐           ┌──┴──┐
  5       3           1      2
  ┌─┴─┐   ┌─┴─┐       ┌─┴─┐  ┌─┴─┐
   5   8   6   3       1   7  2   6
```

- Each internal node stores the minimum of its children.
- The root stores the minimum of the entire array (`1`).

Querying and updating are identical in structure — only the operation changes from `+` to `min`.

---

## 12. Complexity and Characteristics

| Operation | Time     | Space | Notes                  |
| --------- | -------- | ----- | ---------------------- |
| Build     | O(n)     | O(2n) | Bottom-up              |
| Query     | O(log n) | –     | Uses O(log n) nodes    |
| Update    | O(log n) | –     | Recomputes parent path |

Supports: Sum, Min, Max, GCD, Bitwise ops, etc. Very flexible.

---

A Segment Tree is a versatile range-query and update data structure built over a binary hierarchy of array segments:

- **Build:** O(n)
- **Query:** O(log n)
- **Update:** O(log n)
- **Space:** O(2n)
- **Supports:** sum, min, max, gcd, bitwise, etc.
- **Conceptually:** combines the update power of BIT with the generality of the Sparse Table.
