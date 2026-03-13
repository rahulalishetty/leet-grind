# 3369. Design an Array Statistics Tracker — Exhaustive Java Notes

## Problem Statement

Design a data structure that supports:

- adding a number
- removing the earliest added number
- querying:
  - **mean**
  - **median**
  - **mode**

You need to implement:

```java
class StatisticsTracker {

    public StatisticsTracker() {

    }

    public void addNumber(int number) {

    }

    public void removeFirstAddedNumber() {

    }

    public int getMean() {

    }

    public int getMedian() {

    }

    public int getMode() {

    }
}
```

---

## Definitions

### Mean

The floored average of all numbers currently in the structure:

```text
mean = floor(sum / count)
```

### Median

Sort all current values in non-decreasing order.

- If there is one middle element, return it.
- If there are two middle choices, return the **larger** one.

That means for size `n`, the median is the element at index:

```text
n / 2
```

in **0-indexed sorted order**.

Examples:

- `[2, 3, 4]` → median = `3`
- `[2, 3, 4, 5]` → median = `4`

### Mode

The value with the highest frequency.

If multiple values have the same highest frequency, return the **smallest** one.

---

## Key Difficulty

This is not just a static statistics problem.

The data structure is dynamic and must support:

- insertions
- deletions of the **oldest inserted element**
- fast mean
- fast median
- fast mode

So we need to combine ideas from:

- queue
- balanced ordering
- frequency tracking
- running sum

---

# Approach 1 — Brute Force / Direct Simulation

## Idea

Maintain all elements in insertion order.

For queries:

- `getMean()` → compute from sum or scan
- `getMedian()` → copy, sort, and read middle
- `getMode()` → count frequencies each time

This is conceptually simplest.

---

## Data Structures

- `Queue<Integer>` for insertion/removal order
- maybe `long sum`
- for median: sort a copied list
- for mode: hash map built during query

---

## Java Code

```java
import java.util.*;

class StatisticsTracker {
    private Queue<Integer> queue;
    private long sum;

    public StatisticsTracker() {
        queue = new ArrayDeque<>();
        sum = 0;
    }

    public void addNumber(int number) {
        queue.offer(number);
        sum += number;
    }

    public void removeFirstAddedNumber() {
        int x = queue.poll();
        sum -= x;
    }

    public int getMean() {
        return (int) (sum / queue.size());
    }

    public int getMedian() {
        List<Integer> arr = new ArrayList<>(queue);
        Collections.sort(arr);
        return arr.get(arr.size() / 2);
    }

    public int getMode() {
        Map<Integer, Integer> freq = new HashMap<>();
        int bestFreq = 0;
        int bestValue = Integer.MAX_VALUE;

        for (int x : queue) {
            int f = freq.getOrDefault(x, 0) + 1;
            freq.put(x, f);

            if (f > bestFreq || (f == bestFreq && x < bestValue)) {
                bestFreq = f;
                bestValue = x;
            }
        }
        return bestValue;
    }
}
```

---

## Complexity

### `addNumber`

```text
O(1)
```

### `removeFirstAddedNumber`

```text
O(1)
```

### `getMean`

```text
O(1)
```

### `getMedian`

Sorting current elements:

```text
O(n log n)
```

### `getMode`

Count all elements:

```text
O(n)
```

### Space

```text
O(n)
```

---

## Why this is too slow

The total number of operations can be up to `10^5`.

If many queries ask for median or mode, repeatedly sorting or rebuilding frequencies is too expensive.

So brute force is useful for intuition, but not for the final solution.

---

# Approach 2 — Maintain Sorted Multiset + Frequency Map

## Idea

Improve over brute force by maintaining more information online.

We maintain:

- queue for earliest removal
- running sum for mean
- sorted multiset for median
- frequency map for mode

The challenge is that Java does **not** have a built-in ordered multiset with order statistics.

A plain `TreeMap<Integer, Integer>` can maintain sorted counts, but:

- inserting/deleting is easy
- finding the median by rank is not efficient unless we scan

So this approach is a partial improvement, but median is still problematic.

---

## Data Structures

- `Queue<Integer>` → oldest element removal
- `TreeMap<Integer, Integer>` → sorted counts
- `HashMap<Integer, Integer>` → direct frequencies
- `TreeMap<Integer, TreeSet<Integer>>` or something similar for mode
- `long sum`
- `int size`

---

## Observation

### Mean

Easy:

```text
sum / size
```

### Mode

Can be maintained if we know frequencies dynamically.

A useful trick:

- `freq[value] = count`
- also maintain a structure grouped by frequency

For example:

```text
frequency -> all values with that frequency
```

Then the mode is:

- largest frequency
- smallest value among values with that frequency

That can be answered efficiently.

### Median

Still not easy with just `TreeMap`.

If we scan counts to locate the `size / 2`-th value, that can degrade to:

```text
O(number of distinct values)
```

Still potentially too slow.

---

# Approach 3 — Two Heaps for Median + Frequency Structures for Mode

## Idea

This is a very natural advanced design:

- queue for FIFO deletion
- running sum for mean
- two heaps for median
- frequency map + ordered sets for mode

This works well for streaming medians with insertions and deletions, but deletions are not arbitrary by value index; they are by oldest inserted number.

The problem: Java `PriorityQueue` does not support efficient deletion of an arbitrary element in the middle.

We can solve that with **lazy deletion**.

This leads to a valid advanced design.

---

## Median with Two Heaps

Maintain:

- `left` = max heap containing smaller half
- `right` = min heap containing larger half

We want median to be the **larger middle** when size is even.

So we maintain invariant:

```text
right.size() == left.size()
or
right.size() == left.size() + 1
```

Then median is always:

```text
right.peek()
```

because `right` holds the upper half, and its smallest element is exactly the required median.

---

## Problem with deletion

Suppose oldest value `x` must be removed.

If `x` is deep inside one heap, `PriorityQueue.remove(x)` is `O(n)`.

To avoid that, we use:

- heap stores values as usual
- `delayed[value]` counts how many times a value should be discarded later
- when a heap top is invalid, we pop it until the top is valid

This is called **lazy deletion**.

---

## Mode maintenance

Maintain:

- `freq[value]`
- `freqToValues[count] = sorted set of values with this count`
- `maxFreq`

When a value frequency changes from `f -> f+1` or `f -> f-1`, move it between groups.

Then:

- mode frequency is `maxFreq`
- mode value is smallest value in `freqToValues[maxFreq]`

This gives efficient mode.

---

## Is this final-best?

It is a strong design, but implementing lazy deletion and exact balancing correctly is somewhat intricate.

There is an even cleaner approach for median and mode in Java:

- `TreeSet` with unique IDs for median ordering
- another `TreeSet` keyed by frequency for mode

That is often cleaner and more deterministic.

So let us move to the most practical final solution.

---

# Approach 4 — Final Recommended Solution

## Core idea

We separate the three statistics:

### Mean

Maintain:

- `long sum`
- `int size`

Then:

```text
mean = sum / size
```

### Median

We need sorted order **with duplicates** and support:

- insertion
- deletion of a specific old element
- median query

The clean trick is:

Store each inserted number as a unique node:

```java
(value, id)
```

where `id` is insertion order.

Then maintain two `TreeSet<Node>`:

- `left`: smaller half
- `right`: larger half

Sorted by:

1. value
2. id

This handles duplicates naturally.

We also keep references to the exact node objects in a queue so the earliest inserted node can be removed precisely.

### Mode

Maintain:

- `Map<Integer, Integer> freq`
- `TreeSet<int[]>`-like structure is awkward in Java because mutable array keys are dangerous

Better:
Use a custom `FreqNode` object in a `TreeSet<FreqNode>` ordered by:

1. frequency descending
2. value ascending

Then the first element of the set is always the mode.

When frequency of a value changes:

- remove old `(value, oldFreq)` from the tree
- update frequency
- insert new `(value, newFreq)` if new frequency > 0

This is clean and efficient.

---

# Why this works well

This design gives:

- `addNumber`: `O(log n)`
- `removeFirstAddedNumber`: `O(log n)`
- `getMean`: `O(1)`
- `getMedian`: `O(1)`
- `getMode`: `O(1)`

That fits `10^5` operations comfortably.

---

# 1. Data Structures in Detail

## Node for median sets

```java
class Node {
    int value;
    int id;
}
```

Comparator:

- smaller value first
- if equal, smaller id first

This makes every inserted item unique.

---

## Two TreeSets for median

- `left`: lower half
- `right`: upper half

We maintain invariant:

```text
right.size() == left.size()
or
right.size() == left.size() + 1
```

So the median is always:

```java
right.first().value
```

Because:

- when total size is odd, `right` has one extra element, and its first is the middle
- when total size is even, `right` has the larger middle at its front

Exactly what the problem asks.

---

## Queue for oldest removal

We store the inserted nodes in:

```java
Deque<Node> order
```

Then `removeFirstAddedNumber()` removes `order.pollFirst()`.

That node can be removed from either `left` or `right`.

---

## Frequency map + ordered frequency set

For each value:

```java
freq[value] = current count
```

And maintain a `TreeSet<FreqNode>` sorted by:

1. frequency descending
2. value ascending

Then:

```java
mode = freqSet.first().value
```

Because highest frequency is first, and ties pick smaller value.

---

# 2. Rebalancing for Median

After every add/remove, we rebalance the two sets.

We want:

```text
right.size() == left.size()
or
right.size() == left.size() + 1
```

If `left` gets too large:

- move `left.last()` to `right`

If `right` gets too large by more than 1:

- move `right.first()` to `left`

Also ensure ordering:

every element in `left` <= every element in `right`

If violated, swap extremes.

In practice, with the insertion logic below plus rebalancing, this stays valid.

---

# 3. Final Java Solution

```java
import java.util.*;

class StatisticsTracker {
    private static class Node {
        int value;
        int id;

        Node(int value, int id) {
            this.value = value;
            this.id = id;
        }
    }

    private static class FreqNode {
        int value;
        int freq;

        FreqNode(int value, int freq) {
            this.value = value;
            this.freq = freq;
        }
    }

    private final Comparator<Node> nodeComparator = (a, b) -> {
        if (a.value != b.value) return Integer.compare(a.value, b.value);
        return Integer.compare(a.id, b.id);
    };

    private final Comparator<FreqNode> freqComparator = (a, b) -> {
        if (a.freq != b.freq) return Integer.compare(b.freq, a.freq); // higher freq first
        return Integer.compare(a.value, b.value); // smaller value first
    };

    private Deque<Node> order;
    private TreeSet<Node> left;   // lower half
    private TreeSet<Node> right;  // upper half, median comes from here

    private Map<Integer, Integer> freq;
    private TreeSet<FreqNode> freqSet;

    private long sum;
    private int nextId;

    public StatisticsTracker() {
        order = new ArrayDeque<>();
        left = new TreeSet<>(nodeComparator);
        right = new TreeSet<>(nodeComparator);

        freq = new HashMap<>();
        freqSet = new TreeSet<>(freqComparator);

        sum = 0L;
        nextId = 0;
    }

    public void addNumber(int number) {
        Node node = new Node(number, nextId++);
        order.addLast(node);
        sum += number;

        // median structure
        if (right.isEmpty() || nodeComparator.compare(node, right.first()) >= 0) {
            right.add(node);
        } else {
            left.add(node);
        }
        rebalance();

        // mode structure
        updateFrequency(number, +1);
    }

    public void removeFirstAddedNumber() {
        Node node = order.pollFirst();
        sum -= node.value;

        if (!left.remove(node)) {
            right.remove(node);
        }
        rebalance();

        updateFrequency(node.value, -1);
    }

    public int getMean() {
        return (int) (sum / order.size());
    }

    public int getMedian() {
        return right.first().value;
    }

    public int getMode() {
        return freqSet.first().value;
    }

    private void updateFrequency(int value, int delta) {
        int oldFreq = freq.getOrDefault(value, 0);
        if (oldFreq > 0) {
            freqSet.remove(new FreqNode(value, oldFreq));
        }

        int newFreq = oldFreq + delta;
        if (newFreq == 0) {
            freq.remove(value);
        } else {
            freq.put(value, newFreq);
            freqSet.add(new FreqNode(value, newFreq));
        }
    }

    private void rebalance() {
        // Ensure all left <= all right
        if (!left.isEmpty() && !right.isEmpty() &&
            nodeComparator.compare(left.last(), right.first()) > 0) {

            Node a = left.pollLast();
            Node b = right.pollFirst();
            left.add(b);
            right.add(a);
        }

        // Size invariant: right.size() == left.size() or right.size() == left.size() + 1
        while (left.size() > right.size()) {
            right.add(left.pollLast());
        }

        while (right.size() > left.size() + 1) {
            left.add(right.pollFirst());
        }

        // Ordering may need one more fix after moves
        if (!left.isEmpty() && !right.isEmpty() &&
            nodeComparator.compare(left.last(), right.first()) > 0) {

            Node a = left.pollLast();
            Node b = right.pollFirst();
            left.add(b);
            right.add(a);
        }
    }
}
```

---

# 4. Walkthrough on Example 1

## Operations

```text
add 4
add 4
add 2
add 3
getMean
getMedian
getMode
removeFirstAddedNumber
getMode
```

---

## After adding 4

Data:

```text
[4]
```

- sum = 4
- mean = 4
- sorted = [4]
- median = 4
- freq(4)=1, mode=4

---

## After adding another 4

Data:

```text
[4, 4]
```

Sorted:

```text
[4, 4]
```

Two middles are positions 0 and 1. We take the **larger middle**, i.e. index `2 / 2 = 1`.

Median:

```text
4
```

Mode:

```text
4
```

---

## After adding 2

Data:

```text
[4, 4, 2]
```

Sorted:

```text
[2, 4, 4]
```

Median:

```text
4
```

Mode:

```text
4
```

---

## After adding 3

Data:

```text
[4, 4, 2, 3]
```

Sorted:

```text
[2, 3, 4, 4]
```

Median is larger middle:

```text
index = 4 / 2 = 2
value = 4
```

Mean:

```text
(4 + 4 + 2 + 3) / 4 = 13 / 4 = 3
```

Mode:

```text
4
```

Correct.

---

## Remove earliest added number

Earliest was the first `4`.

Remaining:

```text
[4, 2, 3]
```

Sorted:

```text
[2, 3, 4]
```

Frequencies:

- 2 → 1
- 3 → 1
- 4 → 1

All tied, so smallest value is the mode:

```text
2
```

Correct.

---

# 5. Why the Median Invariant Works

We always maintain:

```text
right.size() == left.size()
or
right.size() == left.size() + 1
```

and all values in `left` are <= all values in `right`.

Therefore:

- if total count is odd, `right` has one extra element, so its smallest element is the exact middle
- if total count is even, `right` holds the upper half, and its smallest element is the larger middle

That matches the problem definition perfectly.

---

# 6. Correctness Sketch

We should not just trust the code. Let us justify it carefully.

## Mean correctness

We maintain exact running sum and exact current size.

So:

```text
getMean() = floor(sum / size)
```

which is exactly the required mean.

---

## Median correctness

The multiset of all current numbers is partitioned into:

- `left`
- `right`

such that:

1. every number in `left` <= every number in `right`
2. `right.size()` is either equal to `left.size()` or larger by 1

Thus, in sorted order:

- `left` contains the first half
- `right` contains the second half, including the desired median

Therefore `right.first()` is exactly the required median.

---

## Mode correctness

For each value `v`, we maintain exact frequency `freq[v]`.

In `freqSet`, values are ordered by:

1. frequency descending
2. value ascending

So the first element is always:

- highest frequency
- smallest value among ties

which is exactly the mode.

---

## FIFO deletion correctness

Each insertion creates a unique `Node(value, id)` and appends it to `order`.

So `removeFirstAddedNumber()` always removes the actual earliest surviving inserted element.

That node is removed from the median structure, and its value frequency is decremented in the mode structure.

So all structures stay consistent.

---

# 7. Complexity Analysis of Final Solution

Let `n` be the current number of elements.

## `addNumber`

- insert into one `TreeSet`
- maybe rebalance with constant many `TreeSet` moves
- update frequency map and frequency `TreeSet`

```text
O(log n)
```

## `removeFirstAddedNumber`

- pop from deque
- remove from one of the median `TreeSet`s
- rebalance
- update frequency map / frequency `TreeSet`

```text
O(log n)
```

## `getMean`

```text
O(1)
```

## `getMedian`

```text
O(1)
```

## `getMode`

```text
O(1)
```

## Space

All structures together store current elements and frequency entries.

```text
O(n)
```

This is efficient enough for `10^5` operations.

---

# 8. Alternative Final Approach — Coordinate Compression + Fenwick Trees

There is another very strong approach worth knowing.

## Idea

If we knew all values in advance, we could:

- compress values
- maintain counts in a Fenwick tree / BIT
- maintain sums if needed
- binary search the median by order statistic

Mode could still be maintained separately with frequencies.

This is powerful for dynamic order statistics.

---

## Why it is awkward here

Values can be as large as `10^9`, and the API is online.

If the problem were solved offline, or if all numbers from future operations were known up front, coordinate compression would be attractive.

But for a class design problem, the online `TreeSet` solution is much cleaner in Java.

So this is more of a theoretical alternative than the recommended implementation here.

---

# 9. Common Mistakes

## Mistake 1: Using `int` for the running sum

Values and operation counts can be large enough that sum may overflow `int`.

Use:

```java
long sum;
```

---

## Mistake 2: For median on even size, choosing the smaller middle

This problem explicitly says:

> If there are two choices for a median, take the larger one.

So for sorted array size `n`, use:

```text
index = n / 2
```

not `(n - 1) / 2`.

---

## Mistake 3: Using plain `TreeSet<Integer>` for median with duplicates

That would collapse duplicates, which is wrong.

You need unique identities, such as:

```java
(value, id)
```

---

## Mistake 4: For mode ties, picking arbitrary value

Tie-breaking matters.

If multiple values have the same max frequency, return the **smallest** one.

---

## Mistake 5: Recomputing mode by scanning all frequencies each time

That can degrade badly.

Maintain an ordered frequency structure so `getMode()` is constant time.

---

# 10. Comparison of Approaches

| Approach                    |      Add |       Remove First | Mean |      Median |             Mode | Space | Notes                      |
| --------------------------- | -------: | -----------------: | ---: | ----------: | ---------------: | ----: | -------------------------- |
| Brute force                 |     O(1) |               O(1) | O(1) |  O(n log n) |             O(n) |  O(n) | Easy but too slow          |
| TreeMap counts + scan       | O(log n) |           O(log n) | O(1) | O(distinct) | O(1) or O(log n) |  O(n) | Median not ideal           |
| Two heaps + lazy deletion   | O(log n) | O(log n) amortized | O(1) |        O(1) |             O(1) |  O(n) | Good but intricate         |
| Two TreeSets + freq TreeSet | O(log n) |           O(log n) | O(1) |        O(1) |             O(1) |  O(n) | Best practical Java design |

---

# 11. Interview Summary

This problem is really three subproblems combined with FIFO deletion:

- **mean** → maintain running `sum` and `size`
- **median** → maintain two balanced ordered halves
- **mode** → maintain exact frequencies plus a structure ordered by `(frequency desc, value asc)`

The cleanest Java solution is:

- `Deque<Node>` for oldest removal
- two `TreeSet<Node>` for the median
- `HashMap<Integer, Integer>` + `TreeSet<FreqNode>` for the mode
- `long sum` for the mean

That gives:

```text
addNumber              O(log n)
removeFirstAddedNumber O(log n)
getMean                O(1)
getMedian              O(1)
getMode                O(1)
Space                  O(n)
```

This is the best balanced solution for both correctness and implementation clarity.

---

# 12. Final Recommended Code Again

```java
import java.util.*;

class StatisticsTracker {
    private static class Node {
        int value;
        int id;

        Node(int value, int id) {
            this.value = value;
            this.id = id;
        }
    }

    private static class FreqNode {
        int value;
        int freq;

        FreqNode(int value, int freq) {
            this.value = value;
            this.freq = freq;
        }
    }

    private final Comparator<Node> nodeComparator = (a, b) -> {
        if (a.value != b.value) return Integer.compare(a.value, b.value);
        return Integer.compare(a.id, b.id);
    };

    private final Comparator<FreqNode> freqComparator = (a, b) -> {
        if (a.freq != b.freq) return Integer.compare(b.freq, a.freq);
        return Integer.compare(a.value, b.value);
    };

    private Deque<Node> order;
    private TreeSet<Node> left;
    private TreeSet<Node> right;

    private Map<Integer, Integer> freq;
    private TreeSet<FreqNode> freqSet;

    private long sum;
    private int nextId;

    public StatisticsTracker() {
        order = new ArrayDeque<>();
        left = new TreeSet<>(nodeComparator);
        right = new TreeSet<>(nodeComparator);

        freq = new HashMap<>();
        freqSet = new TreeSet<>(freqComparator);

        sum = 0L;
        nextId = 0;
    }

    public void addNumber(int number) {
        Node node = new Node(number, nextId++);
        order.addLast(node);
        sum += number;

        if (right.isEmpty() || nodeComparator.compare(node, right.first()) >= 0) {
            right.add(node);
        } else {
            left.add(node);
        }
        rebalance();

        updateFrequency(number, +1);
    }

    public void removeFirstAddedNumber() {
        Node node = order.pollFirst();
        sum -= node.value;

        if (!left.remove(node)) {
            right.remove(node);
        }
        rebalance();

        updateFrequency(node.value, -1);
    }

    public int getMean() {
        return (int) (sum / order.size());
    }

    public int getMedian() {
        return right.first().value;
    }

    public int getMode() {
        return freqSet.first().value;
    }

    private void updateFrequency(int value, int delta) {
        int oldFreq = freq.getOrDefault(value, 0);
        if (oldFreq > 0) {
            freqSet.remove(new FreqNode(value, oldFreq));
        }

        int newFreq = oldFreq + delta;
        if (newFreq == 0) {
            freq.remove(value);
        } else {
            freq.put(value, newFreq);
            freqSet.add(new FreqNode(value, newFreq));
        }
    }

    private void rebalance() {
        if (!left.isEmpty() && !right.isEmpty() &&
            nodeComparator.compare(left.last(), right.first()) > 0) {
            Node a = left.pollLast();
            Node b = right.pollFirst();
            left.add(b);
            right.add(a);
        }

        while (left.size() > right.size()) {
            right.add(left.pollLast());
        }

        while (right.size() > left.size() + 1) {
            left.add(right.pollFirst());
        }

        if (!left.isEmpty() && !right.isEmpty() &&
            nodeComparator.compare(left.last(), right.first()) > 0) {
            Node a = left.pollLast();
            Node b = right.pollFirst();
            left.add(b);
            right.add(a);
        }
    }
}
```
