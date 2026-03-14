# Handling Sum Queries After Update — Lazy Segment Tree Approach

## Intuition

We use a **Lazy Propagation Segment Tree** to efficiently process range updates and queries.

The segment tree stores:

```
seg[ind] = number of 1's in nums1 within range [low, high]
```

From this we can derive the number of zeros in the segment:

```
zeros = (high - low + 1) - seg[ind]
```

When we **flip bits** in a range:

```
1 -> 0
0 -> 1
```

the number of ones becomes the number of zeros and vice‑versa.

Therefore:

```
seg[ind] = segment_length - seg[ind]
```

---

# Lazy Propagation Idea

Instead of immediately updating all elements inside a range, we store a **lazy flag**.

```
lazy[ind] = 1  → this segment must be flipped
```

When a node is processed:

```
seg[ind] = (high - low + 1) - seg[ind]
```

and the lazy flag propagates to children.

We store only the **parity of flips**:

```
lazy[ind] = 1 - lazy[ind]
```

so repeated flips cancel each other.

---

# Handling Query Types

## Query Type 1

```
[1, l, r]
```

Flip bits in `nums1` between `l` and `r`.

This becomes a **range update** in the segment tree.

---

## Query Type 2

```
[2, p, 0]
```

Operation:

```
nums2[i] += nums1[i] * p
```

Let:

```
ones = number of 1s in nums1
```

Then the increase in the sum of nums2 is:

```
ones * p
```

So we maintain a variable:

```
s += ones * p
```

which tracks the accumulated effect of all type‑2 queries.

---

## Query Type 3

```
[3, 0, 0]
```

Return:

```
sum(nums2) + s
```

Where:

- `sum(nums2)` = initial sum of nums2
- `s` = accumulated additions caused by query type 2

---

# Complexity

Each segment tree operation:

```
rangeUpdate → O(log N)
query       → O(log N)
```

Thus overall complexity:

```
O(Q log N)
```

Space complexity:

```
O(N)
```

---

# Java Implementation

```java
class Solution {
    public long[] handleQuery(int[] nums1, int[] nums2, int[][] queries) {
        SegmentTreeLazy st = new SegmentTreeLazy(nums1);
        int n = nums1.length;

        long sum = 0;
        for(int i: nums2) sum += i;

        long s = 0;
        int q = queries.length;
        List<Long> ll = new ArrayList<>();

        for(int i = 0; i < q; i++){
            int[] v = queries[i];

            if(v[0] == 1){
                int l = v[1], r = v[2];
                st.rangeUpdate(l, r);
            }
            else if(v[0] == 2){
                s += (long)v[1] * st.query(0, n - 1);
            }
            else{
                ll.add(sum + s);
            }
        }

        long[] arr = new long[ll.size()];
        for(int i = 0; i < arr.length; i++){
            arr[i] = ll.get(i);
        }

        return arr;
    }
}
```

---

# Segment Tree with Lazy Propagation

```java
class SegmentTreeLazy {

    int[] seg, lazy;
    int n;
    int[] givenArr;
    int MAXN = 400000;

    SegmentTreeLazy(int[] nums){
        seg = new int[MAXN];
        lazy = new int[MAXN];
        givenArr = nums;
        n = givenArr.length;
        build(0, 0, n - 1);
    }

    void build(int index, int l, int r) {
        if (l == r) {
            seg[index] = givenArr[l];
            return;
        }

        int mid = (l + r) / 2;
        build(2*index + 1, l, mid);
        build(2*index + 2, mid + 1, r);

        seg[index] = seg[2*index + 1] + seg[2*index + 2];
    }

    void rangeUpdate(int ind, int low, int high, int l, int r){

        if(lazy[ind] != 0){
            seg[ind] = (high - low + 1 - seg[ind]);

            if(low != high){
                lazy[2*ind + 1] = 1 - lazy[2*ind + 1];
                lazy[2*ind + 2] = 1 - lazy[2*ind + 2];
            }

            lazy[ind] = 0;
        }

        if(r < low || l > high) return;

        if(low >= l && high <= r){

            seg[ind] = (high - low + 1 - seg[ind]);

            if(low != high){
                lazy[2*ind + 1] = 1 - lazy[2*ind + 1];
                lazy[2*ind + 2] = 1 - lazy[2*ind + 2];
            }

            return;
        }

        int mid = (low + high)/2;

        rangeUpdate(2*ind + 1, low, mid, l, r);
        rangeUpdate(2*ind + 2, mid + 1, high, l, r);

        seg[ind] = seg[2*ind + 1] + seg[2*ind + 2];
    }

    void rangeUpdate(int l, int r){
        rangeUpdate(0, 0, n - 1, l, r);
    }

    int query(int ind, int low, int high, int l, int r){

        if(lazy[ind] != 0){

            seg[ind] = (high - low + 1 - seg[ind]);

            if(low != high){
                lazy[2*ind + 1] = 1 - lazy[2*ind + 1];
                lazy[2*ind + 2] = 1 - lazy[2*ind + 2];
            }

            lazy[ind] = 0;
        }

        if(r < low || high < l) return 0;

        if(low >= l && high <= r){
            return seg[ind];
        }

        int mid = (low + high)/2;

        int le = query(2*ind + 1, low, mid, l, r);
        int re = query(2*ind + 2, mid + 1, high, l, r);

        return le + re;
    }

    int query(int l, int r){
        return query(0, 0, n - 1, l, r);
    }
}
```

---

# Key Takeaway

The important trick is realizing:

```
nums2 update depends only on count of 1's in nums1
```

So instead of modifying every element of `nums2`, we:

1. Maintain **count of 1s using a segment tree**
2. Accumulate updates lazily using a variable `s`

This avoids expensive `O(n)` updates and keeps operations efficient.
