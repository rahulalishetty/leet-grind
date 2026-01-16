# Overview

Range queries compute values over a subarray of an array. Common examples include:

- **sumq(a, b):** Sum of elements in [a, b]
- **minq(a, b):** Minimum element in [a, b]
- **maxq(a, b):** Maximum element in [a, b]

A naive loop-based approach takes O(n) per query, resulting in O(nq) time for q queries — inefficient for large inputs.

---

## Static Array Queries

For static arrays (where values don’t change), auxiliary data structures can be precomputed to answer queries efficiently.

---

## Prefix Sum Array for Sum Queries

A prefix sum array `P` is constructed such that:

- **P[k] = sumq(0, k):** The sum of all elements up to index `k`.
- **Construction time:** O(n)
- **Query time:** O(1), using the formula:

  ```note
  sumq(a, b) = P[b] − P[a−1]
  ```

  (with P[−1] defined as 0).

### Example:

Original array: `1 3 4 8 6 1 4 2`
Prefix sum array: `1 4 8 16 22 23 27 29`

To compute `sumq(3, 6)`:

```note
sumq(3, 6) = P[6] − P[2] = 27 − 8 = 19
```

---

## Extension to 2D Arrays

The prefix sum concept extends to 2D arrays, where each element represents the sum of all elements in the rectangle from the origin to that position.

The sum of any rectangular subarray can be computed in O(1) using:

```note
S(A) − S(B) − S(C) + S(D)
```

Where `S(X)` is the prefix sum up to corner `X`.

## Minimum Queries – Overview

Unlike sum queries (which are additive and easy to combine via prefix sums), minimum (and maximum) queries are non-linear and cannot be aggregated through addition or subtraction. However, using a preprocessing technique called a Sparse Table, we can answer any `minq(a, b)` in **O(1)** time after **O(n log n)** preprocessing.

This method is suitable for static arrays — it works efficiently when the array is not updated between queries.

---

### Core Idea – Precompute Power-of-Two Ranges

We precompute the minimum for all subarrays whose lengths are powers of two: `1, 2, 4, 8, …`, up to `⌊log₂n⌋`. For every starting index `a` and every `k` such that `2^k ≤ n`, we store:

```note
minq(a, a + 2^k - 1)
```

This forms a Sparse Table, where each row corresponds to a starting index, and each column corresponds to a range length (power of two).

---

### Example

#### Array:

`1 3 4 8 6 1 4 2`

#### Precomputed Ranges:

1. **Length 1 (2⁰ = 1):**

   ```note
   minq(0,0) = 1, minq(1,1) = 3, minq(2,2) = 4, minq(3,3) = 8,
   minq(4,4) = 6, minq(5,5) = 1, minq(6,6) = 4, minq(7,7) = 2
   ```

2. **Length 2 (2¹ = 2):**

   ```note
   minq(0,1) = 1, minq(1,2) = 3, minq(2,3) = 4, minq(3,4) = 6,
   minq(4,5) = 1, minq(5,6) = 1, minq(6,7) = 2
   ```

3. **Length 4 (2² = 4):**

   ```note
   minq(0,3) = 1, minq(1,4) = 3, minq(2,5) = 1, minq(3,6) = 1,
   minq(4,7) = 1
   ```

4. **Length 8 (2³ = 8):**

   ```note
   minq(0,7) = 1
   ```

---

### Recursive Computation Formula

To compute efficiently, we use the recurrence relation:

```note
minq(a, b) = min(minq(a, a + w - 1), minq(a + w, b))
```

Where:

- `b - a + 1` is a power of two,
- `w = (b - a + 1) / 2`.

Each longer interval’s minimum is computed by combining two adjacent smaller power-of-two intervals.

---

### Querying – O(1) Time

Once precomputed, any `minq(a, b)` can be answered in constant time:

1. Compute the length `L = b - a + 1`.
2. Let `k = ⌊log₂(L)⌋`, the largest power of two ≤ `L`.
3. Use the precomputed values:

```note
minq(a, b) = min(minq(a, a + 2^k - 1), minq(b - 2^k + 1, b))
```

#### Example Query: `minq(1, 6)`

Array: `1 3 4 8 6 1 4 2`
Index: `0 1 2 3 4 5 6 7`

- Length = 6
- Largest power of two ≤ 6 → 4 (`2² = 4`)
- Split the range `[1, 6]` into:
  - `[1, 4]` (first 4 elements)
  - `[3, 6]` (last 4 elements)

From the precomputed table:

- `minq(1, 4) = 3`
- `minq(3, 6) = 1`

Thus:

```note
minq(1, 6) = min(3, 1) = 1
```

---

### Complexity Summary

| Operation     | Time Complexity |
| ------------- | --------------- |
| Preprocessing | O(n log n)      |
| Query         | O(1)            |
| Space         | O(n log n)      |

---

### Conceptual Summary

- The Sparse Table method leverages overlapping intervals of size powers of two.
- It is optimal for static datasets where values don’t change.
- It is especially effective for idempotent operations like `min`, `max`, and `gcd` (since combining overlapping intervals doesn’t cause double-counting).
- However, it cannot handle updates efficiently — for that, Segment Trees or Fenwick Trees (BITs) are used.
