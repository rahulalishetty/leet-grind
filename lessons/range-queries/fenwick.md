# Binary Indexed Tree (Fenwick Tree)

## Motivation: Why BIT Exists

The prefix sum array allows fast range sum queries in **O(1)** time but cannot handle updates efficiently:

- **Prefix Sum Array**:
  - Query sum of [a, b]: **O(1)**
  - Update element at index k: **O(n)** (requires rebuilding suffix sums)

This is fine for static arrays but impractical for dynamic scenarios (e.g., real-time data or dynamic scoreboards).

**Binary Indexed Tree (BIT)** addresses this trade-off:

- Query prefix sum in **O(log n)**
- Update an element in **O(log n)**

It sacrifices constant-time queries for logarithmic efficiency while gaining dynamic flexibility.

---

## Representation: Tree in an Array

Despite its name, a Binary Indexed Tree is stored as a **1-indexed array** with implicit parent-child relationships determined by binary arithmetic.

For any index `k`:

- `p(k)` = largest power of 2 dividing `k`
- `tree[k]` = sum of elements in range `[k - p(k) + 1, k]`

Thus:

- Each `tree[k]` stores a **partial sum** of the array — not the entire prefix, just the last `p(k)` elements.

### Example

**Array (1-indexed):**

| Index | 1   | 2   | 3   | 4   | 5   | 6   | 7   | 8   |
| ----- | --- | --- | --- | --- | --- | --- | --- | --- |
| Value | 1   | 3   | 4   | 8   | 6   | 1   | 4   | 2   |

**Binary Indexed Tree:**

| Index | 1   | 2   | 3   | 4   | 5   | 6   | 7   | 8   |
| ----- | --- | --- | --- | --- | --- | --- | --- | --- |
| tree  | 1   | 4   | 4   | 16  | 6   | 7   | 4   | 29  |

---

## How Each Tree Cell Represents a Range

For the first few indices:

| k   | Binary | `p(k)` = `k & -k` | Range Length | Range Covered by `tree[k]` |
| --- | ------ | ----------------- | ------------ | -------------------------- |
| 1   | 0001   | 1                 | 1            | [1, 1]                     |
| 2   | 0010   | 2                 | 2            | [1, 2]                     |
| 3   | 0011   | 1                 | 1            | [3, 3]                     |
| 4   | 0100   | 4                 | 4            | [1, 4]                     |
| 5   | 0101   | 1                 | 1            | [5, 5]                     |
| 6   | 0110   | 2                 | 2            | [5, 6]                     |
| 7   | 0111   | 1                 | 1            | [7, 7]                     |
| 8   | 1000   | 8                 | 8            | [1, 8]                     |

### Example:

- `tree[4]` = sum(1..4)
- `tree[6]` = sum(5..6)
- `tree[8]` = sum(1..8)

Resulting array:
`tree = [1, 4, 4, 16, 6, 7, 4, 29]`

---

## Prefix Sum Queries: `sum(1, k)`

To compute any prefix sum efficiently:

```cpp
int sum(int k) {
    int s = 0;
    while (k >= 1) {
        s += tree[k];
        k -= k & -k;  // move to parent
    }
    return s;
}
```

### Explanation:

1. Start at index `k`.
2. Add `tree[k]` (represents `[k - p(k) + 1, k]`).
3. Move to the parent node: `k -= p(k) = k - (k & -k)`.
4. Repeat until `k = 0`.

Each step removes the lowest set bit of `k`, climbing up the implicit tree.

### Example: `sum(1, 7)`

- `k = 7` → `tree[7] = sum(7, 7)`
- `k = 6` → `tree[6] = sum(5, 6)`
- `k = 4` → `tree[4] = sum(1, 4)`

Result:
`sum(1, 7) = 4 + 7 + 16 = 27`

Only 3 lookups → **O(log n)**.

---

## Range Sum Queries: `sum(a, b)`

To compute the sum of `[a, b]`:
`sum(a, b) = sum(1, b) - sum(1, a-1)`

Both prefix sums take **O(log n)**, so total time: **O(log n)**.

---

## Updating a Value: `add(k, x)`

To increase an element at position `k` by `x`, propagate updates upward:

```cpp
void add(int k, int x) {
    while (k <= n) {
        tree[k] += x;
        k += k & -k;  // move to next affected node
    }
}
```

### Explanation:

- `k += k & -k` moves to the next node that covers `k`.
- Add `x` to each affected node’s sum.
- Each index appears in **O(log n)** nodes → **O(log n)** update time.

### Example: Update index 3 (value +5)

Affected tree nodes:

- `tree[3]` (covers `[3, 3]`)
- `tree[4]` (covers `[1, 4]`)
- `tree[8]` (covers `[1, 8]`)

Add `+5` to indices 3, 4, and 8.

---

## Bit Operation Insight: `k & -k`

This isolates the lowest set bit of `k`, corresponding to the range size (`p(k)`).

For `k = 6` (110₂):

- `-k = (~k + 1) = 010₂`
- `k & -k = 2`

Thus:

- `k -= (k & -k)` moves upward (to the parent).
- `k += (k & -k)` moves downward (to the next node covering `k`).

---

## Complexity

| Operation   | Time Complexity | Explanation                    |
| ----------- | --------------- | ------------------------------ |
| `sum(1, k)` | **O(log n)**    | Traverse ancestors in the tree |
| `sum(a, b)` | **O(log n)**    | Two prefix sums                |
| `add(k, x)` | **O(log n)**    | Update all nodes covering `k`  |
| Memory      | **O(n)**        | Same as array size             |

---

## Conceptual Summary

| Feature         | Description                                                         |
| --------------- | ------------------------------------------------------------------- |
| **Goal**        | Maintain dynamic prefix sums                                        |
| **Structure**   | Array of partial sums (length = lowest power of two dividing index) |
| **Query**       | Decompose prefix `[1, k]` into disjoint power-of-two intervals      |
| **Update**      | Propagate changes to all overlapping intervals that include `k`     |
| **Key Formula** | `p(k) = k & -k`                                                     |
| **Time per Op** | **O(log n)**                                                        |
| **Space**       | **O(n)**                                                            |
| **Invented by** | Peter Fenwick, 1994                                                 |

---

## Mental Model: Visual Intuition

Think of the binary representation of index `k` as the path to a node in an implicit binary tree:

```
          8(1-8)
      /           \
    4(1-4)        12(9-12)
   /   \
 2(1-2) 6(5-6)
```

- Each node represents a block of size `2^i`.
- Moving upward removes one bit (climbing to parent).
- Moving downward adds one bit (reaching next affected node).

---

## Key Comparison: Prefix Sum vs BIT

| Feature               | Prefix Sum   | Binary Indexed Tree |
| --------------------- | ------------ | ------------------- |
| **Query Time**        | **O(1)**     | **O(log n)**        |
| **Update Time**       | **O(n)**     | **O(log n)**        |
| **Structure**         | Simple array | Tree-like array     |
| **Supports Updates?** | ❌ No        | ✅ Yes              |
| **Memory**            | **O(n)**     | **O(n)**            |
| **Use Case**          | Static data  | Dynamic updates     |
