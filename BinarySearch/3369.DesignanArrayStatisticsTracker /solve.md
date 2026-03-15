# 3369. Design an Array Statistics Tracker — Java Solutions and Detailed Notes

## Problem

We need to design a data structure that supports:

- `addNumber(int number)`
- `removeFirstAddedNumber()`
- `getMean()`
- `getMedian()`
- `getMode()`

The structure behaves like a dynamic array with **FIFO deletion**:

- new values are appended
- removals always delete the **earliest added** number

We must answer:

- **mean** = floor(sum / count)
- **median** = middle element in sorted order, and if there are two choices, take the **larger**
- **mode** = most frequent value, and if tied, take the **smallest value**

There are at most `10^5` operations total.

---

# What makes this problem tricky?

Each operation affects different statistics in different ways:

- **Mean** wants:
  - running `sum`
  - running `count`

- **Mode** wants:
  - frequency counts
  - fast tie-breaking by smallest number

- **Median** wants:
  - order statistics under insertion and FIFO deletion

The median part is the hardest because we need both:

- insertion of arbitrary values
- deletion of a specific old value
- ability to quickly find the middle element

A plain heap pair is not enough by itself unless we add lazy deletion carefully.
A balanced BST with counts works very well if we augment it for order statistics.
A Fenwick tree / segment tree can also work if we coordinate-compress values.

So there are several valid designs.

---

# Approach 1: Simple list + sort on every query (easy but too slow)

## Idea

Store all current numbers in a queue/list.

- `addNumber`: append
- `removeFirstAddedNumber`: remove from front
- `getMean`: compute with running sum
- `getMedian`: copy current values, sort them, take the median
- `getMode`: recompute frequencies each time

This is simple but inefficient.

---

## Java code

```java
import java.util.*;

class StatisticsTracker {
    private Deque<Integer> queue;
    private long sum;

    public StatisticsTracker() {
        queue = new ArrayDeque<>();
        sum = 0;
    }

    public void addNumber(int number) {
        queue.offerLast(number);
        sum += number;
    }

    public void removeFirstAddedNumber() {
        int x = queue.pollFirst();
        sum -= x;
    }

    public int getMean() {
        return (int) (sum / queue.size());
    }

    public int getMedian() {
        List<Integer> list = new ArrayList<>(queue);
        Collections.sort(list);
        return list.get(list.size() / 2);
    }

    public int getMode() {
        Map<Integer, Integer> freq = new HashMap<>();
        int bestFreq = 0;
        int bestVal = Integer.MAX_VALUE;

        for (int x : queue) {
            int f = freq.getOrDefault(x, 0) + 1;
            freq.put(x, f);

            if (f > bestFreq || (f == bestFreq and x < bestVal)) {
                bestFreq = f;
                bestVal = x;
            }
        }

        return bestVal;
    }
}
```

---

## Complexity

- `addNumber`: `O(1)`
- `removeFirstAddedNumber`: `O(1)`
- `getMean`: `O(1)`
- `getMedian`: `O(n log n)`
- `getMode`: `O(n)`

Too slow in worst case.

---

# Approach 2: Queue + two heaps for median + HashMap/TreeSet for mode (practical, but careful)

This is a common interview-style solution.

## Data structures

We keep:

### 1. FIFO queue

To know which value to remove next.

### 2. Running sum and size

For mean.

### 3. Two heaps for median

We maintain:

- `small`: max heap for lower half
- `large`: min heap for upper half

Because the problem wants the **larger median** when count is even, we maintain:

```text
large.size() == small.size()
or
large.size() == small.size() + 1
```

So the median is always:

```text
large.peek()
```

### 4. Lazy deletion maps

Because removing the earliest inserted value may remove an arbitrary element from one of the heaps, not necessarily the root.

So we use lazy deletion:

- mark values to be removed later in a map
- prune heap tops when needed

### 5. Frequency map + ordered set for mode

Maintain:

- `freq[value] = count`
- a `TreeSet` of pairs `(frequency, -value)` or a custom comparator so that the “best” mode is quickly available.

Since we want:

- highest frequency
- smallest value on ties

we can keep a `TreeSet<Node>` ordered by:

1. frequency descending
2. value ascending

Then `first()` gives the mode.

---

## Main difficulty with heaps

With duplicates and arbitrary deletions, we must know whether a removed value belongs logically to `small` or `large`.

A standard trick is:

- compare the value with `large.peek()`
- if value >= median, treat it as removed from `large`
- else from `small`

This works with careful balancing and pruning.

---

## Java code

```java
import java.util.*;

class StatisticsTracker {
    private Deque<Integer> order;
    private long sum;
    private int size;

    private PriorityQueue<Integer> small; // max heap
    private PriorityQueue<Integer> large; // min heap

    private Map<Integer, Integer> delayedSmall;
    private Map<Integer, Integer> delayedLarge;
    private int smallSize;
    private int largeSize;

    private Map<Integer, Integer> freq;
    private TreeSet<Node> modeSet;

    public StatisticsTracker() {
        order = new ArrayDeque<>();
        sum = 0L;
        size = 0;

        small = new PriorityQueue<>(Collections.reverseOrder());
        large = new PriorityQueue<>();

        delayedSmall = new HashMap<>();
        delayedLarge = new HashMap<>();
        smallSize = 0;
        largeSize = 0;

        freq = new HashMap<>();
        modeSet = new TreeSet<>((a, b) -> {
            if (a.freq != b.freq) return Integer.compare(b.freq, a.freq); // higher freq first
            return Integer.compare(a.value, b.value); // smaller value first
        });
    }

    public void addNumber(int number) {
        order.offerLast(number);
        sum += number;
        size++;

        addMode(number);
        addMedian(number);
    }

    public void removeFirstAddedNumber() {
        int x = order.pollFirst();
        sum -= x;
        size--;

        removeMode(x);
        removeMedian(x);
    }

    public int getMean() {
        return (int) (sum / size);
    }

    public int getMedian() {
        pruneLarge();
        return large.peek();
    }

    public int getMode() {
        return modeSet.first().value;
    }

    private void addMode(int x) {
        int oldFreq = freq.getOrDefault(x, 0);
        if (oldFreq > 0) {
            modeSet.remove(new Node(x, oldFreq));
        }
        int newFreq = oldFreq + 1;
        freq.put(x, newFreq);
        modeSet.add(new Node(x, newFreq));
    }

    private void removeMode(int x) {
        int oldFreq = freq.get(x);
        modeSet.remove(new Node(x, oldFreq));

        if (oldFreq == 1) {
            freq.remove(x);
        } else {
            freq.put(x, oldFreq - 1);
            modeSet.add(new Node(x, oldFreq - 1));
        }
    }

    private void addMedian(int x) {
        pruneLarge();
        if (large.isEmpty() || x >= large.peek()) {
            large.offer(x);
            largeSize++;
        } else {
            small.offer(x);
            smallSize++;
        }
        rebalance();
    }

    private void removeMedian(int x) {
        pruneLarge();
        if (!large.isEmpty() && x >= large.peek()) {
            delayedLarge.put(x, delayedLarge.getOrDefault(x, 0) + 1);
            largeSize--;
            if (!large.isEmpty() && large.peek() == x) {
                pruneLarge();
            }
        } else {
            delayedSmall.put(x, delayedSmall.getOrDefault(x, 0) + 1);
            smallSize--;
            if (!small.isEmpty() && small.peek() == x) {
                pruneSmall();
            }
        }
        rebalance();
    }

    private void rebalance() {
        pruneSmall();
        pruneLarge();

        while (largeSize > smallSize + 1) {
            int x = large.poll();
            pruneLarge();
            largeSize--;
            small.offer(x);
            smallSize++;
        }

        while (largeSize < smallSize) {
            int x = small.poll();
            pruneSmall();
            smallSize--;
            large.offer(x);
            largeSize++;
        }

        pruneSmall();
        pruneLarge();
    }

    private void pruneSmall() {
        while (!small.isEmpty()) {
            int x = small.peek();
            int cnt = delayedSmall.getOrDefault(x, 0);
            if (cnt == 0) break;
            small.poll();
            if (cnt == 1) delayedSmall.remove(x);
            else delayedSmall.put(x, cnt - 1);
        }
    }

    private void pruneLarge() {
        while (!large.isEmpty()) {
            int x = large.peek();
            int cnt = delayedLarge.getOrDefault(x, 0);
            if (cnt == 0) break;
            large.poll();
            if (cnt == 1) delayedLarge.remove(x);
            else delayedLarge.put(x, cnt - 1);
        }
    }

    static class Node {
        int value;
        int freq;

        Node(int value, int freq) {
            this.value = value;
            this.freq = freq;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Node)) return false;
            Node other = (Node) o;
            return value == other.value && freq == other.freq;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, freq);
        }
    }
}
```

---

## Complexity

- `addNumber`: `O(log n)`
- `removeFirstAddedNumber`: `O(log n)`
- `getMean`: `O(1)`
- `getMedian`: amortized `O(log n)` / practically `O(1)` after heap maintenance
- `getMode`: `O(1)` for `first()` of TreeSet, though updates cost `O(log n)`

This works, but the implementation is delicate.

---

# Approach 3: Queue + frequency map + Treap / order-statistics tree (clean theoretical solution)

This is the most structurally elegant general solution.

## Idea

Use a balanced BST augmented with subtree sizes so we can:

- insert a value
- delete a value
- find the k-th smallest value

Then median becomes an order-statistics query.

We also maintain:

- queue for FIFO deletion
- sum for mean
- frequency map + TreeSet for mode

In Java, the standard library has `TreeMap`, but it does **not** support k-th order statistics directly.

So to make this approach fully correct, we need a custom order-statistics tree such as a **Treap**.

---

## Data kept

- `Deque<Integer> order`
- `long sum`
- `int size`
- `Treap` storing values with multiplicities and subtree sizes
- `HashMap<Integer, Integer> freq`
- `TreeSet<Node>` for mode

### Median index

If current count is `n`, and we want the larger median for even `n`, then median index in 0-based sorted order is:

```text
n / 2
```

So we ask the order-statistics tree for the `(n / 2 + 1)`-th smallest in 1-based indexing.

---

## Java code

```java
import java.util.*;

class StatisticsTracker {
    private Deque<Integer> order;
    private long sum;
    private int size;

    private Treap treap;

    private Map<Integer, Integer> freq;
    private TreeSet<Node> modeSet;

    public StatisticsTracker() {
        order = new ArrayDeque<>();
        sum = 0L;
        size = 0;

        treap = new Treap();

        freq = new HashMap<>();
        modeSet = new TreeSet<>((a, b) -> {
            if (a.freq != b.freq) return Integer.compare(b.freq, a.freq);
            return Integer.compare(a.value, b.value);
        });
    }

    public void addNumber(int number) {
        order.offerLast(number);
        sum += number;
        size++;

        treap.insert(number);
        addMode(number);
    }

    public void removeFirstAddedNumber() {
        int x = order.pollFirst();
        sum -= x;
        size--;

        treap.erase(x);
        removeMode(x);
    }

    public int getMean() {
        return (int) (sum / size);
    }

    public int getMedian() {
        return treap.kth(size / 2 + 1);
    }

    public int getMode() {
        return modeSet.first().value;
    }

    private void addMode(int x) {
        int oldFreq = freq.getOrDefault(x, 0);
        if (oldFreq > 0) modeSet.remove(new Node(x, oldFreq));
        int newFreq = oldFreq + 1;
        freq.put(x, newFreq);
        modeSet.add(new Node(x, newFreq));
    }

    private void removeMode(int x) {
        int oldFreq = freq.get(x);
        modeSet.remove(new Node(x, oldFreq));
        if (oldFreq == 1) {
            freq.remove(x);
        } else {
            freq.put(x, oldFreq - 1);
            modeSet.add(new Node(x, oldFreq - 1));
        }
    }

    static class Node {
        int value;
        int freq;

        Node(int value, int freq) {
            this.value = value;
            this.freq = freq;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Node)) return false;
            Node other = (Node) o;
            return value == other.value && freq == other.freq;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, freq);
        }
    }

    static class Treap {
        static class TNode {
            int key, priority, cnt, size;
            TNode left, right;

            TNode(int key, int priority) {
                this.key = key;
                this.priority = priority;
                this.cnt = 1;
                this.size = 1;
            }
        }

        private TNode root;
        private Random rand = new Random();

        private int size(TNode node) {
            return node == null ? 0 : node.size;
        }

        private void pull(TNode node) {
            if (node != null) {
                node.size = node.cnt + size(node.left) + size(node.right);
            }
        }

        private TNode rotateRight(TNode y) {
            TNode x = y.left;
            y.left = x.right;
            x.right = y;
            pull(y);
            pull(x);
            return x;
        }

        private TNode rotateLeft(TNode x) {
            TNode y = x.right;
            x.right = y.left;
            y.left = x;
            pull(x);
            pull(y);
            return y;
        }

        public void insert(int key) {
            root = insert(root, key);
        }

        private TNode insert(TNode node, int key) {
            if (node == null) return new TNode(key, rand.nextInt());

            if (key == node.key) {
                node.cnt++;
            } else if (key < node.key) {
                node.left = insert(node.left, key);
                if (node.left.priority > node.priority) {
                    node = rotateRight(node);
                }
            } else {
                node.right = insert(node.right, key);
                if (node.right.priority > node.priority) {
                    node = rotateLeft(node);
                }
            }

            pull(node);
            return node;
        }

        public void erase(int key) {
            root = erase(root, key);
        }

        private TNode erase(TNode node, int key) {
            if (node == null) return null;

            if (key == node.key) {
                if (node.cnt > 1) {
                    node.cnt--;
                } else {
                    if (node.left == null) return node.right;
                    if (node.right == null) return node.left;

                    if (node.left.priority > node.right.priority) {
                        node = rotateRight(node);
                        node.right = erase(node.right, key);
                    } else {
                        node = rotateLeft(node);
                        node.left = erase(node.left, key);
                    }
                }
            } else if (key < node.key) {
                node.left = erase(node.left, key);
            } else {
                node.right = erase(node.right, key);
            }

            pull(node);
            return node;
        }

        public int kth(int k) {
            return kth(root, k);
        }

        private int kth(TNode node, int k) {
            int leftSize = size(node.left);
            if (k <= leftSize) return kth(node.left, k);
            if (k <= leftSize + node.cnt) return node.key;
            return kth(node.right, k - leftSize - node.cnt);
        }
    }
}
```

---

## Complexity

All main updates and queries become:

- `addNumber`: `O(log n)`
- `removeFirstAddedNumber`: `O(log n)`
- `getMean`: `O(1)`
- `getMedian`: `O(log n)` for `kth`
- `getMode`: `O(1)` after `TreeSet` maintenance

This is robust and theoretically clean.

---

# Approach 4: Queue + coordinate compression + Fenwick tree + mode structure (excellent offline-style solution)

This is often the cleanest if we are allowed to preprocess all numbers that ever appear.

Because the operations list is bounded (`10^5` total), we can imagine compressing all values that will ever be added.

Then maintain:

- `Deque<Integer>` of inserted values
- `sum`, `size`
- `freq[value]` for mode
- `TreeSet` for best mode
- **Fenwick tree over compressed values** for median

## Why Fenwick works

Fenwick stores frequencies of values in sorted coordinate order.

Then:

- add number → `+1` at its compressed index
- remove number → `-1`
- median = find the smallest value whose prefix frequency reaches:
  ```text
  size / 2 + 1
  ```
  because we want the larger median for even length.

This gives `O(log n)` median operations and a simpler implementation than custom treap, but it requires knowing all possible values in advance.

That is very practical in contest settings when the operations are provided as input beforehand.
For a class-only API setting, it is less natural unless preprocessing is allowed externally.

---

## Java code sketch

```java
import java.util.*;

class StatisticsTracker {
    private Deque<Integer> order = new ArrayDeque<>();
    private long sum = 0;
    private int size = 0;

    private Fenwick fenwick;
    private List<Integer> values;
    private Map<Integer, Integer> compress;

    private Map<Integer, Integer> freq = new HashMap<>();
    private TreeSet<Node> modeSet = new TreeSet<>((a, b) -> {
        if (a.freq != b.freq) return Integer.compare(b.freq, a.freq);
        return Integer.compare(a.value, b.value);
    });

    // This constructor assumes we already know all values that may be added.
    public StatisticsTracker(List<Integer> allValues) {
        TreeSet<Integer> sorted = new TreeSet<>(allValues);
        values = new ArrayList<>(sorted);
        compress = new HashMap<>();
        for (int i = 0; i < values.size(); i++) {
            compress.put(values.get(i), i + 1); // 1-based Fenwick
        }
        fenwick = new Fenwick(values.size());
    }

    public void addNumber(int number) {
        order.offerLast(number);
        sum += number;
        size++;

        fenwick.add(compress.get(number), 1);
        addMode(number);
    }

    public void removeFirstAddedNumber() {
        int x = order.pollFirst();
        sum -= x;
        size--;

        fenwick.add(compress.get(x), -1);
        removeMode(x);
    }

    public int getMean() {
        return (int) (sum / size);
    }

    public int getMedian() {
        int rank = size / 2 + 1;
        int idx = fenwick.kth(rank);
        return values.get(idx - 1);
    }

    public int getMode() {
        return modeSet.first().value;
    }

    private void addMode(int x) {
        int old = freq.getOrDefault(x, 0);
        if (old > 0) modeSet.remove(new Node(x, old));
        int now = old + 1;
        freq.put(x, now);
        modeSet.add(new Node(x, now));
    }

    private void removeMode(int x) {
        int old = freq.get(x);
        modeSet.remove(new Node(x, old));
        if (old == 1) {
            freq.remove(x);
        } else {
            freq.put(x, old - 1);
            modeSet.add(new Node(x, old - 1));
        }
    }

    static class Node {
        int value, freq;
        Node(int value, int freq) {
            this.value = value;
            this.freq = freq;
        }
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Node)) return false;
            Node other = (Node) o;
            return value == other.value && freq == other.freq;
        }
        @Override
        public int hashCode() {
            return Objects.hash(value, freq);
        }
    }

    static class Fenwick {
        int n;
        int[] bit;

        Fenwick(int n) {
            this.n = n;
            this.bit = new int[n + 1];
        }

        void add(int idx, int delta) {
            while (idx <= n) {
                bit[idx] += delta;
                idx += idx & -idx;
            }
        }

        int sum(int idx) {
            int res = 0;
            while (idx > 0) {
                res += bit[idx];
                idx -= idx & -idx;
            }
            return res;
        }

        int kth(int k) {
            int idx = 0;
            int mask = Integer.highestOneBit(n);
            for (int step = mask; step != 0; step >>= 1) {
                int next = idx + step;
                if (next <= n && bit[next] < k) {
                    idx = next;
                    k -= bit[next];
                }
            }
            return idx + 1;
        }
    }
}
```

---

## Complexity

- `addNumber`: `O(log n)`
- `removeFirstAddedNumber`: `O(log n)`
- `getMean`: `O(1)`
- `getMedian`: `O(log n)`
- `getMode`: `O(1)`

Very strong solution if coordinate compression is available.

---

# How to think about mode

Mode has two rules:

1. highest frequency
2. smallest value on ties

A clean way to maintain this dynamically is:

- keep `freq[value]`
- keep an ordered set of `(frequency, value)`

Whenever a frequency changes:

- remove old node
- insert new node

Order by:

- frequency descending
- value ascending

Then the best mode is always the first node.

This works in all serious solutions above.

---

# How to think about median

Median definition here is slightly unusual for even size:

> If there are two choices, take the **larger** one.

If the current size is `n`, then the median rank in sorted order is:

```text
n / 2 + 1   (1-based)
```

Examples:

- `n = 1` → rank 1
- `n = 2` → rank 2 (the larger median)
- `n = 3` → rank 2
- `n = 4` → rank 3

That rank formula is what the treap / Fenwick / heap balancing must target.

---

# Comparison of approaches

## Approach 1: Simple list + recomputation

### Pros

- very easy to implement
- good for understanding

### Cons

- too slow

### Complexity

- median and mode queries are expensive

---

## Approach 2: Queue + heaps + lazy deletion + mode structure

### Pros

- practical online solution
- no preprocessing needed

### Cons

- median deletion logic is delicate
- more bug-prone

### Complexity

- all operations around `O(log n)` except mean `O(1)`

---

## Approach 3: Queue + Treap + mode structure

### Pros

- very clean theory
- straightforward support for insertion, deletion, kth statistic

### Cons

- requires writing a custom balanced BST

### Complexity

- all update/query operations optimal

---

## Approach 4: Queue + Fenwick + compression + mode structure

### Pros

- excellent if all values are known in advance
- simpler than Treap in some contexts

### Cons

- needs offline compression setup
- less natural for a pure online class design

### Complexity

- all update/query operations around `O(log n)`

---

# Final recommended solution

For a true **online class design**, the most robust general answer is:

## Queue + Order Statistic Tree + Frequency Structure

That means:

- queue for FIFO deletion
- running sum and count for mean
- custom Treap for median
- frequency map + ordered set for mode

This gives strong worst-case behavior and clean semantics.

---

# Final polished Java solution (recommended)

```java
import java.util.*;

class StatisticsTracker {
    private Deque<Integer> order;
    private long sum;
    private int size;
    private Treap treap;
    private Map<Integer, Integer> freq;
    private TreeSet<Node> modeSet;

    public StatisticsTracker() {
        order = new ArrayDeque<>();
        sum = 0L;
        size = 0;
        treap = new Treap();

        freq = new HashMap<>();
        modeSet = new TreeSet<>((a, b) -> {
            if (a.freq != b.freq) return Integer.compare(b.freq, a.freq);
            return Integer.compare(a.value, b.value);
        });
    }

    public void addNumber(int number) {
        order.offerLast(number);
        sum += number;
        size++;

        treap.insert(number);
        updateModeAdd(number);
    }

    public void removeFirstAddedNumber() {
        int x = order.pollFirst();
        sum -= x;
        size--;

        treap.erase(x);
        updateModeRemove(x);
    }

    public int getMean() {
        return (int) (sum / size);
    }

    public int getMedian() {
        return treap.kth(size / 2 + 1);
    }

    public int getMode() {
        return modeSet.first().value;
    }

    private void updateModeAdd(int x) {
        int old = freq.getOrDefault(x, 0);
        if (old > 0) {
            modeSet.remove(new Node(x, old));
        }
        int now = old + 1;
        freq.put(x, now);
        modeSet.add(new Node(x, now));
    }

    private void updateModeRemove(int x) {
        int old = freq.get(x);
        modeSet.remove(new Node(x, old));
        if (old == 1) {
            freq.remove(x);
        } else {
            freq.put(x, old - 1);
            modeSet.add(new Node(x, old - 1));
        }
    }

    static class Node {
        int value;
        int freq;

        Node(int value, int freq) {
            this.value = value;
            this.freq = freq;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Node)) return false;
            Node other = (Node) o;
            return this.value == other.value && this.freq == other.freq;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, freq);
        }
    }

    static class Treap {
        static class TNode {
            int key;
            int priority;
            int cnt;
            int size;
            TNode left, right;

            TNode(int key, int priority) {
                this.key = key;
                this.priority = priority;
                this.cnt = 1;
                this.size = 1;
            }
        }

        private TNode root;
        private Random rand = new Random();

        private int size(TNode node) {
            return node == null ? 0 : node.size;
        }

        private void pull(TNode node) {
            if (node != null) {
                node.size = node.cnt + size(node.left) + size(node.right);
            }
        }

        private TNode rotateRight(TNode y) {
            TNode x = y.left;
            y.left = x.right;
            x.right = y;
            pull(y);
            pull(x);
            return x;
        }

        private TNode rotateLeft(TNode x) {
            TNode y = x.right;
            x.right = y.left;
            y.left = x;
            pull(x);
            pull(y);
            return y;
        }

        public void insert(int key) {
            root = insert(root, key);
        }

        private TNode insert(TNode node, int key) {
            if (node == null) {
                return new TNode(key, rand.nextInt());
            }

            if (key == node.key) {
                node.cnt++;
            } else if (key < node.key) {
                node.left = insert(node.left, key);
                if (node.left.priority > node.priority) {
                    node = rotateRight(node);
                }
            } else {
                node.right = insert(node.right, key);
                if (node.right.priority > node.priority) {
                    node = rotateLeft(node);
                }
            }

            pull(node);
            return node;
        }

        public void erase(int key) {
            root = erase(root, key);
        }

        private TNode erase(TNode node, int key) {
            if (node == null) return null;

            if (key == node.key) {
                if (node.cnt > 1) {
                    node.cnt--;
                } else {
                    if (node.left == null) return node.right;
                    if (node.right == null) return node.left;

                    if (node.left.priority > node.right.priority) {
                        node = rotateRight(node);
                        node.right = erase(node.right, key);
                    } else {
                        node = rotateLeft(node);
                        node.left = erase(node.left, key);
                    }
                }
            } else if (key < node.key) {
                node.left = erase(node.left, key);
            } else {
                node.right = erase(node.right, key);
            }

            pull(node);
            return node;
        }

        public int kth(int k) {
            return kth(root, k);
        }

        private int kth(TNode node, int k) {
            int leftSize = size(node.left);
            if (k <= leftSize) {
                return kth(node.left, k);
            }
            if (k <= leftSize + node.cnt) {
                return node.key;
            }
            return kth(node.right, k - leftSize - node.cnt);
        }
    }
}
```

---

# Key takeaways

## Mean

Easy:

- running `sum`
- running `count`

## Mode

Use:

- `HashMap<Integer, Integer>` for frequencies
- ordered set of `(freq, value)` with custom ordering

## Median

Need a structure supporting:

- insert
- delete specific value
- kth smallest

Good choices:

- custom Treap / order-statistics tree
- Fenwick tree with coordinate compression
- two heaps with lazy deletion

For a true online design, the Treap solution is the cleanest general-purpose answer.
