# 3526. Range XOR Queries with Subarray Reversals

## Problem summary

We are given:

- an array `nums`
- a list of queries of three types

### Query types

1. **Point update**

```text
[1, index, value]
```

Set:

```text
nums[index] = value
```

2. **Range XOR**

```text
[2, left, right]
```

Return:

```text
nums[left] ^ nums[left + 1] ^ ... ^ nums[right]
```

3. **Reverse subarray**

```text
[3, left, right]
```

Reverse:

```text
nums[left...right]
```

We must return all answers to type-2 queries in order.

---

# Why this problem is tricky

If there were only:

- point updates
- range XOR queries

then a Fenwick tree or segment tree would solve it easily.

If there were only:

- reversals
- range XOR queries

then we would want a structure that can reverse intervals efficiently.

The real difficulty is supporting **all three operations together**:

- point assignment
- interval reversal
- interval XOR query

That combination strongly suggests an **implicit balanced binary tree with lazy reversal**, such as:

- implicit treap
- splay tree
- rope / randomized BST

This is the cleanest fully dynamic solution.

---

# Key insight

The array can be represented as an **ordered sequence tree**.

If every node stores:

- its value
- subtree size
- subtree XOR
- lazy reverse flag

then we can support:

- split by position
- merge back
- reverse a range
- query XOR on a range
- update a single position

all in logarithmic expected time.

This is exactly what an **implicit treap** is good at.

---

# Approach 1: Implicit Treap with lazy reversal (recommended)

## Intuition

An implicit treap stores the sequence order by subtree sizes rather than explicit keys.

Each treap node represents one array element.

### Each node stores

- `val` → this array value
- `xor` → XOR of the whole subtree
- `size` → subtree size
- `rev` → lazy reversal flag
- random priority for heap balancing

### Supported primitives

#### `split(root, k)`

Split the first `k` elements from the rest.

#### `merge(a, b)`

Merge two treaps where every element in `a` comes before every element in `b`.

#### lazy reverse

If a node is marked reversed:

- swap left and right child
- propagate the flag down later

With these, every query becomes a standard sequence manipulation.

---

## How each query is handled

Assume the array is stored in treap `root`.

### 1. Update `[1, index, value]`

We isolate exactly one element:

- split first `index`
- split next 1 element
- change its value
- recompute
- merge back

### 2. Range XOR `[2, left, right]`

We isolate the range:

- split first `left`
- split next `right - left + 1`
- answer is middle subtree XOR
- merge back

### 3. Reverse `[3, left, right]`

Again isolate that range:

- split first `left`
- split next `right - left + 1`
- toggle reverse flag on middle subtree
- merge back

---

## Java implementation

```java
import java.util.*;

class Solution {

    static class Node {
        int val;
        int xor;
        int size;
        int priority;
        boolean rev;
        Node left, right;

        Node(int val, Random rand) {
            this.val = val;
            this.xor = val;
            this.size = 1;
            this.priority = rand.nextInt();
        }
    }

    static class Pair {
        Node left, right;
        Pair(Node left, Node right) {
            this.left = left;
            this.right = right;
        }
    }

    private final Random rand = new Random();

    private int size(Node node) {
        return node == null ? 0 : node.size;
    }

    private int xor(Node node) {
        return node == null ? 0 : node.xor;
    }

    private void push(Node node) {
        if (node == null || !node.rev) return;

        node.rev = false;
        Node tmp = node.left;
        node.left = node.right;
        node.right = tmp;

        if (node.left != null) node.left.rev ^= true;
        if (node.right != null) node.right.rev ^= true;
    }

    private void pull(Node node) {
        if (node == null) return;
        node.size = 1 + size(node.left) + size(node.right);
        node.xor = node.val ^ xor(node.left) ^ xor(node.right);
    }

    private Pair split(Node root, int k) {
        // first k elements go to left
        if (root == null) return new Pair(null, null);

        push(root);

        if (size(root.left) >= k) {
            Pair p = split(root.left, k);
            root.left = p.right;
            pull(root);
            return new Pair(p.left, root);
        } else {
            Pair p = split(root.right, k - size(root.left) - 1);
            root.right = p.left;
            pull(root);
            return new Pair(root, p.right);
        }
    }

    private Node merge(Node a, Node b) {
        if (a == null) return b;
        if (b == null) return a;

        push(a);
        push(b);

        if (a.priority > b.priority) {
            a.right = merge(a.right, b);
            pull(a);
            return a;
        } else {
            b.left = merge(a, b.left);
            pull(b);
            return b;
        }
    }

    private Node build(int[] nums) {
        Node root = null;
        for (int x : nums) {
            root = merge(root, new Node(x, rand));
        }
        return root;
    }

    public int[] getResults(int[] nums, int[][] queries) {
        Node root = build(nums);
        List<Integer> ans = new ArrayList<>();

        for (int[] q : queries) {
            int type = q[0];
            int a = q[1];
            int b = q[2];

            if (type == 1) {
                // update index a to value b
                Pair p1 = split(root, a);
                Pair p2 = split(p1.right, 1);

                if (p2.left != null) {
                    p2.left.val = b;
                    pull(p2.left);
                }

                root = merge(p1.left, merge(p2.left, p2.right));

            } else if (type == 2) {
                // range xor [a, b]
                Pair p1 = split(root, a);
                Pair p2 = split(p1.right, b - a + 1);

                ans.add(xor(p2.left));

                root = merge(p1.left, merge(p2.left, p2.right));

            } else {
                // reverse [a, b]
                Pair p1 = split(root, a);
                Pair p2 = split(p1.right, b - a + 1);

                if (p2.left != null) {
                    p2.left.rev ^= true;
                }

                root = merge(p1.left, merge(p2.left, p2.right));
            }
        }

        int[] res = new int[ans.size()];
        for (int i = 0; i < ans.size(); i++) {
            res[i] = ans.get(i);
        }
        return res;
    }
}
```

---

## Correctness sketch

The implicit treap always maintains the exact current array order.

- `split(root, k)` partitions the sequence into:
  - first `k` elements
  - remaining elements
- `merge(a, b)` restores order because all elements of `a` come before all elements of `b`
- subtree XOR is maintained by:

```text
node.xor = node.val ^ xor(left) ^ xor(right)
```

- lazy reversal works because reversing a subtree sequence corresponds to swapping left and right recursively, which the `rev` flag delays efficiently

Therefore:

- update isolates one element and changes it
- range XOR isolates the interval and reads its subtree XOR
- reverse isolates the interval and toggles its lazy reverse flag

So every query result is correct.

---

## Complexity

Let `n = nums.length`, `q = queries.length`.

### Time complexity

Each operation performs a constant number of:

- splits
- merges
- pulls / pushes

Each of those is expected:

```text
O(log n)
```

So total:

```text
O((n + q) log n)
```

Expected, because treap balancing is randomized.

### Space complexity

```text
O(n)
```

for the treap nodes.

---

# Approach 2: Brute force simulation

## Intuition

The simplest possible solution is to directly mutate the array.

### For each query

- update → assign directly
- range XOR → loop from `left` to `right`
- reverse → two-pointer reversal

This is easy to implement and correct, but too slow for worst-case input sizes.

---

## Java implementation

```java
import java.util.*;

class Solution {
    public int[] getResults(int[] nums, int[][] queries) {
        List<Integer> ans = new ArrayList<>();

        for (int[] q : queries) {
            int type = q[0];
            int a = q[1];
            int b = q[2];

            if (type == 1) {
                nums[a] = b;
            } else if (type == 2) {
                int xr = 0;
                for (int i = a; i <= b; i++) {
                    xr ^= nums[i];
                }
                ans.add(xr);
            } else {
                while (a < b) {
                    int tmp = nums[a];
                    nums[a] = nums[b];
                    nums[b] = tmp;
                    a++;
                    b--;
                }
            }
        }

        int[] res = new int[ans.size()];
        for (int i = 0; i < ans.size(); i++) {
            res[i] = ans.get(i);
        }
        return res;
    }
}
```

---

## Complexity

In the worst case, a query may process `O(n)` elements.

So total:

```text
O(qn)
```

which is too slow for `10^5`.

Space:

```text
O(1)
```

besides output storage.

---

# Approach 3: Segment tree + explicit range reversal by swaps

## Intuition

If we ignore reversals, a segment tree easily supports:

- point update
- range XOR query

But reversal is difficult because reversing changes positions of many elements.

A naive workaround is:

- keep a segment tree for XOR
- when reversing `[l, r]`, explicitly swap:

```text
nums[l] <-> nums[r]
nums[l+1] <-> nums[r-1]
...
```

and update both positions in the segment tree

This is much better than plain brute force for XOR queries, but reversal still costs linear time in the range length.

So it does not meet the intended efficiency.

---

## Java implementation

```java
import java.util.*;

class Solution {
    static class SegTree {
        int n;
        int[] tree;

        SegTree(int[] nums) {
            n = nums.length;
            tree = new int[4 * n];
            build(1, 0, n - 1, nums);
        }

        void build(int node, int l, int r, int[] nums) {
            if (l == r) {
                tree[node] = nums[l];
                return;
            }
            int mid = (l + r) >>> 1;
            build(node << 1, l, mid, nums);
            build(node << 1 | 1, mid + 1, r, nums);
            tree[node] = tree[node << 1] ^ tree[node << 1 | 1];
        }

        void update(int idx, int val) {
            update(1, 0, n - 1, idx, val);
        }

        void update(int node, int l, int r, int idx, int val) {
            if (l == r) {
                tree[node] = val;
                return;
            }
            int mid = (l + r) >>> 1;
            if (idx <= mid) update(node << 1, l, mid, idx, val);
            else update(node << 1 | 1, mid + 1, r, idx, val);
            tree[node] = tree[node << 1] ^ tree[node << 1 | 1];
        }

        int query(int ql, int qr) {
            return query(1, 0, n - 1, ql, qr);
        }

        int query(int node, int l, int r, int ql, int qr) {
            if (ql <= l && r <= qr) return tree[node];
            int mid = (l + r) >>> 1;
            int res = 0;
            if (ql <= mid) res ^= query(node << 1, l, mid, ql, qr);
            if (qr > mid) res ^= query(node << 1 | 1, mid + 1, r, ql, qr);
            return res;
        }
    }

    public int[] getResults(int[] nums, int[][] queries) {
        SegTree st = new SegTree(nums);
        List<Integer> ans = new ArrayList<>();

        for (int[] q : queries) {
            int type = q[0], a = q[1], b = q[2];

            if (type == 1) {
                nums[a] = b;
                st.update(a, b);
            } else if (type == 2) {
                ans.add(st.query(a, b));
            } else {
                while (a < b) {
                    int tmp = nums[a];
                    nums[a] = nums[b];
                    nums[b] = tmp;
                    st.update(a, nums[a]);
                    st.update(b, nums[b]);
                    a++;
                    b--;
                }
            }
        }

        int[] res = new int[ans.size()];
        for (int i = 0; i < ans.size(); i++) {
            res[i] = ans.get(i);
        }
        return res;
    }
}
```

---

## Complexity

- update: `O(log n)`
- XOR query: `O(log n)`
- reverse: up to `O((r-l+1) log n)`

Worst-case total can still be:

```text
O(qn log n)
```

So not good enough.

---

# Approach 4: Sqrt decomposition / block decomposition (practical but complex)

## Intuition

A more advanced alternative is to use block decomposition.

Idea:

- split array into blocks
- each block stores XOR of its elements
- reversal of a range can be done by:
  - reversing partial boundary blocks explicitly
  - flipping order / lazy orientation of full blocks in the middle

This can lead to around:

```text
O((n + q) * sqrt(n))
```

or similar depending on implementation.

This is a practical technique for sequence operations, but it is much more complex than the implicit treap and still not as elegant.

Since the problem mixes:

- range query
- point update
- range reverse

sequence trees are a more natural fit.

So this approach is mostly worth mentioning as an alternative design direction, not as the best interview solution.

---

# Why XOR is convenient here

XOR has useful algebraic properties:

- associative
- commutative
- identity is `0`

So subtree XOR for a sequence tree is easy to maintain:

```text
xor(subtree) = xor(left) ^ val ^ xor(right)
```

Also, reversing a sequence does **not** change the XOR of that sequence.

That means lazy reversal only changes structure/order, not the XOR aggregate itself.

This is one reason the implicit treap solution is especially clean here.

---

# Best approach

The best solution is:

## **Approach 1: Implicit Treap with lazy reversal**

Why:

- supports all three operations efficiently
- naturally models a mutable sequence
- XOR aggregate is easy to maintain
- lazy reverse integrates perfectly

---

# Final recommended solution

```java
import java.util.*;

class Solution {

    static class Node {
        int val;
        int xor;
        int size;
        int priority;
        boolean rev;
        Node left, right;

        Node(int val, Random rand) {
            this.val = val;
            this.xor = val;
            this.size = 1;
            this.priority = rand.nextInt();
        }
    }

    static class Pair {
        Node left, right;
        Pair(Node left, Node right) {
            this.left = left;
            this.right = right;
        }
    }

    private final Random rand = new Random();

    private int size(Node node) {
        return node == null ? 0 : node.size;
    }

    private int xor(Node node) {
        return node == null ? 0 : node.xor;
    }

    private void push(Node node) {
        if (node == null || !node.rev) return;

        node.rev = false;
        Node tmp = node.left;
        node.left = node.right;
        node.right = tmp;

        if (node.left != null) node.left.rev ^= true;
        if (node.right != null) node.right.rev ^= true;
    }

    private void pull(Node node) {
        if (node == null) return;
        node.size = 1 + size(node.left) + size(node.right);
        node.xor = node.val ^ xor(node.left) ^ xor(node.right);
    }

    private Pair split(Node root, int k) {
        if (root == null) return new Pair(null, null);

        push(root);

        if (size(root.left) >= k) {
            Pair p = split(root.left, k);
            root.left = p.right;
            pull(root);
            return new Pair(p.left, root);
        } else {
            Pair p = split(root.right, k - size(root.left) - 1);
            root.right = p.left;
            pull(root);
            return new Pair(root, p.right);
        }
    }

    private Node merge(Node a, Node b) {
        if (a == null) return b;
        if (b == null) return a;

        push(a);
        push(b);

        if (a.priority > b.priority) {
            a.right = merge(a.right, b);
            pull(a);
            return a;
        } else {
            b.left = merge(a, b.left);
            pull(b);
            return b;
        }
    }

    private Node build(int[] nums) {
        Node root = null;
        for (int x : nums) {
            root = merge(root, new Node(x, rand));
        }
        return root;
    }

    public int[] getResults(int[] nums, int[][] queries) {
        Node root = build(nums);
        List<Integer> ans = new ArrayList<>();

        for (int[] q : queries) {
            int type = q[0];
            int a = q[1];
            int b = q[2];

            if (type == 1) {
                Pair p1 = split(root, a);
                Pair p2 = split(p1.right, 1);

                if (p2.left != null) {
                    p2.left.val = b;
                    pull(p2.left);
                }

                root = merge(p1.left, merge(p2.left, p2.right));

            } else if (type == 2) {
                Pair p1 = split(root, a);
                Pair p2 = split(p1.right, b - a + 1);

                ans.add(xor(p2.left));

                root = merge(p1.left, merge(p2.left, p2.right));

            } else {
                Pair p1 = split(root, a);
                Pair p2 = split(p1.right, b - a + 1);

                if (p2.left != null) {
                    p2.left.rev ^= true;
                }

                root = merge(p1.left, merge(p2.left, p2.right));
            }
        }

        int[] res = new int[ans.size()];
        for (int i = 0; i < ans.size(); i++) {
            res[i] = ans.get(i);
        }
        return res;
    }
}
```

## Complexity

- **Time:** expected `O((n + q) log n)`
- **Space:** `O(n)`

This is the cleanest fully efficient solution for the problem.
