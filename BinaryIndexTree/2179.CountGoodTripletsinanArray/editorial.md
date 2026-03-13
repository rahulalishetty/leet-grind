# 2179. Count Good Triplets in an Array — Binary Indexed Tree Approach

## Intuition

If indices `i, j, k` satisfy:

```
0 ≤ i < j < k < n
and
0 ≤ pos2[nums1[i]] < pos2[nums1[j]] < pos2[nums1[k]] < n
```

then `(nums1[i], nums1[j], nums1[k])` forms a **good triplet**.

Since both `nums1` and `nums2` are **permutations of `[0, 1, ..., n-1]`**, we can transform the problem into counting valid index triplets.

---

## Index Mapping

We build an array:

```
indexMapping[i] = pos2[nums1[i]]
```

Where:

- `pos2[v]` = index of value `v` in `nums2`

This converts the problem into:

> Count the number of triplets `i < j < k` such that

```
indexMapping[i] < indexMapping[j] < indexMapping[k]
```

This is essentially counting **increasing subsequence triplets**.

---

## Fix the Middle Element

For each `j`:

- `left` = number of elements **to the left of j** that are smaller than `indexMapping[j]`
- `right` = number of elements **to the right of j** that are greater than `indexMapping[j]`

The number of valid triplets with `j` as the middle element is:

```
left × right
```

We sum this for all `j`.

---

## Why Use a Binary Indexed Tree (Fenwick Tree)

A **Binary Indexed Tree** supports:

- **Prefix sum query**
- **Point update**

Both in:

```
O(log n)
```

This allows us to efficiently compute how many smaller elements exist before a given position.

---

## Implementation

```java
class FenwickTree {

    private int[] tree;

    public FenwickTree(int size) {
        tree = new int[size + 1];
    }

    public void update(int index, int delta) {
        index++;
        while (index < tree.length) {
            tree[index] += delta;
            index += index & -index;
        }
    }

    public int query(int index) {
        index++;
        int res = 0;
        while (index > 0) {
            res += tree[index];
            index -= index & -index;
        }
        return res;
    }
}

class Solution {

    public long goodTriplets(int[] nums1, int[] nums2) {
        int n = nums1.length;

        int[] pos2 = new int[n];
        int[] reversedIndexMapping = new int[n];

        for (int i = 0; i < n; i++) {
            pos2[nums2[i]] = i;
        }

        for (int i = 0; i < n; i++) {
            reversedIndexMapping[pos2[nums1[i]]] = i;
        }

        FenwickTree tree = new FenwickTree(n);
        long res = 0;

        for (int value = 0; value < n; value++) {
            int pos = reversedIndexMapping[value];

            int left = tree.query(pos);

            tree.update(pos, 1);

            int right = (n - 1 - pos) - (value - left);

            res += (long) left * right;
        }

        return res;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Each element performs:

- `query()` → `O(log n)`
- `update()` → `O(log n)`

Total:

```
O(n log n)
```

---

### Space Complexity

Binary Indexed Tree storage:

```
O(n)
```
