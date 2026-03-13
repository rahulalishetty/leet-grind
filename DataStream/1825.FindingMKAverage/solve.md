# 1825. Finding MK Average — Exhaustive Java Notes

## Problem Statement

You are given two integers `m` and `k`, and a stream of integers.

Design a data structure `MKAverage` that supports:

- `addElement(int num)`: add `num` to the stream
- `calculateMKAverage()`: consider only the **last `m` elements**
  - remove the **smallest `k`**
  - remove the **largest `k`**
  - return the floor of the average of the remaining `m - 2k` elements

If the stream currently contains fewer than `m` elements, return `-1`.

---

## Example

```text
Input:
["MKAverage", "addElement", "addElement", "calculateMKAverage",
 "addElement", "calculateMKAverage",
 "addElement", "addElement", "addElement", "calculateMKAverage"]

[[3, 1], [3], [1], [], [10], [], [5], [5], [5], []]

Output:
[null, null, null, -1, null, 3, null, null, null, 5]
```

### Walkthrough

```text
MKAverage obj = new MKAverage(3, 1);

addElement(3)  -> stream = [3]
addElement(1)  -> stream = [3, 1]
calculateMKAverage() -> -1   // fewer than 3 elements

addElement(10) -> stream = [3, 1, 10]
take last 3 = [3, 1, 10]
remove smallest 1 -> [3, 10]
remove largest 1  -> [3]
average = 3
return 3

addElement(5)  -> stream = [3, 1, 10, 5]
addElement(5)  -> stream = [3, 1, 10, 5, 5]
addElement(5)  -> stream = [3, 1, 10, 5, 5, 5]
take last 3 = [5, 5, 5]
remove smallest 1 and largest 1 -> [5]
average = 5
return 5
```

---

# 1. Core Insight

For every query, we care only about the **last `m` elements**.

Inside those `m` elements, we conceptually split them into 3 groups:

- `lo`: the smallest `k` elements
- `mid`: the middle `m - 2k` elements
- `hi`: the largest `k` elements

Then:

```text
MKAverage = sum(mid) / (m - 2k)
```

rounded down, which Java integer division already does for non-negative values.

So the real challenge is:

> maintain a sliding window of size `m`, while dynamically keeping it partitioned into the smallest `k`, middle `m - 2k`, and largest `k` elements.

That is the whole problem.

---

# 2. Why a Brute Force Approach Is Too Slow

A literal implementation would do this for every `calculateMKAverage()`:

1. copy the last `m` elements
2. sort them
3. discard first `k`
4. discard last `k`
5. sum the middle

This costs:

```text
O(m log m)
```

per query.

Since there can be up to `10^5` operations, this is too expensive in the worst case.

We need incremental maintenance.

---

# 3. Approach 1 — Brute Force with Sorting

## Idea

Maintain the full stream in a list.

Whenever `calculateMKAverage()` is called:

- if size < `m`, return `-1`
- copy the last `m` elements
- sort them
- sum from index `k` to `m-k-1`

This is the easiest way to understand the problem.

## Java Code

```java
import java.util.*;

class MKAverageBruteForce {
    private final int m;
    private final int k;
    private final List<Integer> stream;

    public MKAverageBruteForce(int m, int k) {
        this.m = m;
        this.k = k;
        this.stream = new ArrayList<>();
    }

    public void addElement(int num) {
        stream.add(num);
    }

    public int calculateMKAverage() {
        if (stream.size() < m) {
            return -1;
        }

        List<Integer> last = new ArrayList<>();
        for (int i = stream.size() - m; i < stream.size(); i++) {
            last.add(stream.get(i));
        }

        Collections.sort(last);

        long sum = 0;
        for (int i = k; i < m - k; i++) {
            sum += last.get(i);
        }

        return (int) (sum / (m - 2 * k));
    }
}
```

## Complexity

- `addElement`: `O(1)`
- `calculateMKAverage`: `O(m log m)`
- space: `O(n)` for the stream, plus `O(m)` temporary copy

## Verdict

Good for understanding, not good enough for large constraints.

---

# 4. Approach 2 — Sliding Window + Re-sort Every Time

## Idea

Instead of keeping the whole stream forever, keep only the **last `m` elements** in a queue/deque.

For `addElement(num)`:

- push `num`
- if size exceeds `m`, pop from the front

For `calculateMKAverage()`:

- copy the current deque to an array/list
- sort
- compute answer

This is slightly cleaner because the data structure directly matches the window we care about.

## Java Code

```java
import java.util.*;

class MKAverageWindowSort {
    private final int m;
    private final int k;
    private final Deque<Integer> window;

    public MKAverageWindowSort(int m, int k) {
        this.m = m;
        this.k = k;
        this.window = new ArrayDeque<>();
    }

    public void addElement(int num) {
        window.addLast(num);
        if (window.size() > m) {
            window.removeFirst();
        }
    }

    public int calculateMKAverage() {
        if (window.size() < m) {
            return -1;
        }

        List<Integer> arr = new ArrayList<>(window);
        Collections.sort(arr);

        long sum = 0;
        for (int i = k; i < m - k; i++) {
            sum += arr.get(i);
        }

        return (int) (sum / (m - 2 * k));
    }
}
```

## Complexity

- `addElement`: `O(1)`
- `calculateMKAverage`: `O(m log m)`
- space: `O(m)`

## Verdict

Still too slow when both `m` and number of operations are large.

---

# 5. Toward the Optimal Solution

We need to avoid sorting from scratch.

The natural structure is to maintain three balanced ordered multisets:

- `lo`: smallest `k`
- `mid`: middle `m - 2k`
- `hi`: largest `k`

We also maintain:

- a queue of the last `m` elements
- the sum of elements in `mid`

When a new element arrives:

1. insert it into one of the sets
2. if window exceeds size `m`, remove the oldest element from whichever set contains it
3. rebalance the three sets so that:
   - `lo.size() == k`
   - `hi.size() == k`
   - all remaining valid elements are in `mid`
4. answer is simply:

```text
midSum / (m - 2k)
```

This gives us an efficient sliding-window design.

---

# 6. Key Difficulty: We Need a Multiset, Not Just a Set

Values can repeat.

So we cannot use just a normal `TreeSet<Integer>` because duplicates would collapse.

We need a multiset-like structure.

In Java, a standard way is:

```text
TreeMap<Integer, Integer>
```

where:

- key = value
- mapped value = frequency

This lets us support:

- add one occurrence
- remove one occurrence
- get smallest key
- get largest key

all in `O(log n)`.

---

# 7. Approach 3 — Three TreeMaps + Queue (Optimal)

## High-Level Design

Maintain:

- `Deque<Integer> q`: the last `m` elements
- `MultiSet lo`: smallest `k`
- `MultiSet mid`: middle `m - 2k`
- `MultiSet hi`: largest `k`
- `long midSum`: sum of all values currently in `mid`

### Invariants

Whenever the window has exactly `m` elements:

- `lo` contains exactly `k` smallest elements
- `hi` contains exactly `k` largest elements
- `mid` contains the remaining `m - 2k` elements
- `midSum` is the sum of `mid`

Then the answer is immediate.

---

# 8. Rebalancing Logic

After insertion or deletion, sizes may be wrong or ordering may be violated.

We repair in stages:

### Size balancing

- while `lo.size() > k`: move the largest from `lo` to `mid`
- while `hi.size() > k`: move the smallest from `hi` to `mid`
- while `lo.size() < k`: move the smallest from `mid` to `lo`
- while `hi.size() < k`: move the largest from `mid` to `hi`

### Order balancing

Even if sizes are correct, some elements may be in the wrong side:

- if largest in `lo` > smallest in `mid`, swap them
- if largest in `mid` > smallest in `hi`, swap them

In practice, a disciplined insertion/removal plus size-fix is enough if we choose good placement rules, but writing a robust `rebalance()` is safer.

---

# 9. Clean Multiset Helper

We implement a helper with:

- `add(x)`
- `remove(x)`
- `firstKey()`
- `lastKey()`
- `pollFirst()`
- `pollLast()`
- `size()`
- `contains(x)`

---

# 10. Optimal Java Solution

```java
import java.util.*;

class MKAverage {
    private static class MultiSet {
        private final TreeMap<Integer, Integer> map = new TreeMap<>();
        private int size = 0;

        void add(int x) {
            map.put(x, map.getOrDefault(x, 0) + 1);
            size++;
        }

        boolean remove(int x) {
            Integer cnt = map.get(x);
            if (cnt == null) return false;
            if (cnt == 1) map.remove(x);
            else map.put(x, cnt - 1);
            size--;
            return true;
        }

        int firstKey() {
            return map.firstKey();
        }

        int lastKey() {
            return map.lastKey();
        }

        int pollFirst() {
            int x = firstKey();
            remove(x);
            return x;
        }

        int pollLast() {
            int x = lastKey();
            remove(x);
            return x;
        }

        boolean contains(int x) {
            return map.containsKey(x);
        }

        int size() {
            return size;
        }

        boolean isEmpty() {
            return size == 0;
        }
    }

    private final int m;
    private final int k;
    private final Deque<Integer> window;

    private final MultiSet lo;
    private final MultiSet mid;
    private final MultiSet hi;

    private long midSum;

    public MKAverage(int m, int k) {
        this.m = m;
        this.k = k;
        this.window = new ArrayDeque<>();
        this.lo = new MultiSet();
        this.mid = new MultiSet();
        this.hi = new MultiSet();
        this.midSum = 0L;
    }

    public void addElement(int num) {
        window.addLast(num);

        if (window.size() <= m) {
            addNumber(num);
            if (window.size() == m) {
                rebalance();
            }
        } else {
            addNumber(num);
            int old = window.removeFirst();
            removeNumber(old);
            rebalance();
        }
    }

    public int calculateMKAverage() {
        if (window.size() < m) {
            return -1;
        }
        return (int) (midSum / (m - 2 * k));
    }

    private void addNumber(int x) {
        if (!lo.isEmpty() && x <= lo.lastKey()) {
            lo.add(x);
        } else if (!hi.isEmpty() && x >= hi.firstKey()) {
            hi.add(x);
        } else {
            mid.add(x);
            midSum += x;
        }
        rebalance();
    }

    private void removeNumber(int x) {
        if (lo.remove(x)) {
            // removed from lo
        } else if (hi.remove(x)) {
            // removed from hi
        } else if (mid.remove(x)) {
            midSum -= x;
        }
        rebalance();
    }

    private void rebalance() {
        // If total size is less than m, we can keep everything in mid initially,
        // then shape the sets progressively. The size rules below still work.

        while (lo.size() > k) {
            int x = lo.pollLast();
            mid.add(x);
            midSum += x;
        }

        while (hi.size() > k) {
            int x = hi.pollFirst();
            mid.add(x);
            midSum += x;
        }

        while (lo.size() < k && !mid.isEmpty()) {
            int x = mid.pollFirst();
            midSum -= x;
            lo.add(x);
        }

        while (hi.size() < k && !mid.isEmpty()) {
            int x = mid.pollLast();
            midSum -= x;
            hi.add(x);
        }

        while (!lo.isEmpty() && !mid.isEmpty() && lo.lastKey() > mid.firstKey()) {
            int a = lo.pollLast();
            int b = mid.pollFirst();
            midSum -= b;
            lo.add(b);
            mid.add(a);
            midSum += a;
        }

        while (!mid.isEmpty() && !hi.isEmpty() && mid.lastKey() > hi.firstKey()) {
            int a = mid.pollLast();
            int b = hi.pollFirst();
            midSum -= a;
            hi.add(a);
            mid.add(b);
            midSum += b;
        }

        while (lo.size() > k) {
            int x = lo.pollLast();
            mid.add(x);
            midSum += x;
        }

        while (hi.size() > k) {
            int x = hi.pollFirst();
            mid.add(x);
            midSum += x;
        }

        while (lo.size() < k && !mid.isEmpty()) {
            int x = mid.pollFirst();
            midSum -= x;
            lo.add(x);
        }

        while (hi.size() < k && !mid.isEmpty()) {
            int x = mid.pollLast();
            midSum -= x;
            hi.add(x);
        }
    }
}
```

---

# 11. Why This Works

## Invariant 1

`lo` always holds the smallest values among the current window, up to size `k`.

## Invariant 2

`hi` always holds the largest values among the current window, up to size `k`.

## Invariant 3

`mid` holds exactly everything else.

## Invariant 4

`midSum` is always equal to the sum of elements in `mid`.

Because `calculateMKAverage()` only depends on the middle block, once these invariants hold, the answer is correct.

---

# 12. Dry Run

Take:

```text
m = 3, k = 1
stream = [3, 1, 10]
```

Sorted last `m`:

```text
[1, 3, 10]
```

So:

- `lo = [1]`
- `mid = [3]`
- `hi = [10]`
- `midSum = 3`

Answer:

```text
3 / (3 - 2) = 3
```

Now add `5`:

Window becomes:

```text
[1, 10, 5]
```

Sorted:

```text
[1, 5, 10]
```

So:

- `lo = [1]`
- `mid = [5]`
- `hi = [10]`
- `midSum = 5`

Answer:

```text
5
```

Now add `5`, then `5`:

Last 3 elements become:

```text
[5, 5, 5]
```

So:

- `lo = [5]`
- `mid = [5]`
- `hi = [5]`
- `midSum = 5`

Answer:

```text
5
```

Correct.

---

# 13. Complexity of the Optimal Approach

Each insertion/removal into a `TreeMap`-backed multiset costs:

```text
O(log m)
```

Each `addElement` performs only constant many such operations plus rebalancing moves, and each move is also `O(log m)`.

So overall:

- `addElement`: `O(log m)`
- `calculateMKAverage`: `O(1)`
- space: `O(m)`

This fits the constraints well.

---

# 14. Alternative Optimal Flavor — Fenwick Tree / Segment Tree

Because the values satisfy:

```text
1 <= num <= 10^5
```

we can also exploit the bounded value range.

Instead of storing actual multisets as ordered maps, maintain two Fenwick trees:

- one for frequencies
- one for prefix sums

Then for the current sliding window of size `m`, we can:

1. find the cutoff value where the first `k` smallest elements end
2. find the cutoff value where the last `k` largest elements begin
3. compute the middle sum using prefix sums

This is elegant because the value domain is small enough.

## Idea

For the current window:

- maintain `freq[v] = how many times value v appears`
- maintain `sum[v] = v * freq[v]`

Then:

- total sum of window is easy
- sum of smallest `k` can be found by binary lifting on the frequency BIT
- sum of largest `k` similarly

Then:

```text
middleSum = totalSum - smallestKSum - largestKSum
```

and:

```text
answer = middleSum / (m - 2k)
```

This avoids three multisets entirely.

## Complexity

- `addElement`: `O(log V)`
- `calculateMKAverage`: `O(log V)`
- where `V = 100000`

This is also excellent.

---

# 15. Fenwick Tree Approach — Java Code

```java
import java.util.*;

class MKAverageFenwick {
    private static final int MAXV = 100000;

    private static class Fenwick {
        long[] bit;

        Fenwick(int n) {
            bit = new long[n + 2];
        }

        void add(int idx, long delta) {
            for (int i = idx; i < bit.length; i += i & -i) {
                bit[i] += delta;
            }
        }

        long sum(int idx) {
            long res = 0;
            for (int i = idx; i > 0; i -= i & -i) {
                res += bit[i];
            }
            return res;
        }
    }

    private final int m;
    private final int k;
    private final Deque<Integer> q;
    private final Fenwick freq;
    private final Fenwick sum;
    private long totalSum;

    public MKAverageFenwick(int m, int k) {
        this.m = m;
        this.k = k;
        this.q = new ArrayDeque<>();
        this.freq = new Fenwick(MAXV);
        this.sum = new Fenwick(MAXV);
        this.totalSum = 0L;
    }

    public void addElement(int num) {
        q.addLast(num);
        freq.add(num, 1);
        sum.add(num, num);
        totalSum += num;

        if (q.size() > m) {
            int old = q.removeFirst();
            freq.add(old, -1);
            sum.add(old, -old);
            totalSum -= old;
        }
    }

    public int calculateMKAverage() {
        if (q.size() < m) {
            return -1;
        }

        long leftSum = sumSmallestK(k);
        long rightSum = sumLargestK(k);
        long middle = totalSum - leftSum - rightSum;
        return (int) (middle / (m - 2 * k));
    }

    private long sumSmallestK(int k) {
        if (k == 0) return 0;
        int val = kthSmallest(k);
        long cntBefore = freq.sum(val - 1);
        long sumBefore = sum.sum(val - 1);
        long need = k - cntBefore;
        return sumBefore + need * val;
    }

    private long sumLargestK(int k) {
        if (k == 0) return 0;
        long totalCnt = freq.sum(MAXV);
        int val = kthSmallest((int) (totalCnt - k + 1));
        long cntBefore = freq.sum(val - 1);
        long sumBefore = sum.sum(val - 1);
        long cntAtAndAfter = totalCnt - cntBefore;
        long sumAtAndAfter = totalSum - sumBefore;
        long extra = cntAtAndAfter - k;
        return sumAtAndAfter - extra * val;
    }

    private int kthSmallest(int k) {
        int idx = 0;
        int bitMask = 1;
        while ((bitMask << 1) <= MAXV) {
            bitMask <<= 1;
        }

        long target = k;
        for (int step = bitMask; step > 0; step >>= 1) {
            int next = idx + step;
            if (next <= MAXV && freq.bit[next] < target) {
                idx = next;
                target -= freq.bit[next];
            }
        }
        return idx + 1;
    }
}
```

---

# 16. Comparing the Two Good Approaches

## Three TreeMaps

### Pros

- directly models the three partitions
- conceptually close to the statement
- `calculateMKAverage()` is `O(1)`

### Cons

- implementation is trickier
- lots of balancing details
- duplicates require careful multiset handling

## Fenwick Tree

### Pros

- excellent because values are bounded
- mathematically neat
- no partition juggling

### Cons

- less intuitive at first
- order-statistics logic is harder to derive
- depends on bounded value range

---

# 17. Which One Should You Prefer?

For interviews and general-purpose explanation:

- the **three-multiset approach** is easier to justify from the problem statement

For competitive programming with this exact constraint set:

- the **Fenwick approach** is extremely strong because value range is only `10^5`

If asked for the most practical answer in Java, I would usually present:

1. brute force
2. optimal three-multiset solution
3. mention Fenwick as a value-range optimization

That shows both structural reasoning and awareness of constraint-specific optimizations.

---

# 18. Common Mistakes

## Mistake 1: Using the whole stream instead of the last `m`

The problem only cares about the last `m` elements.

## Mistake 2: Forgetting duplicates

You need a multiset, not a plain set.

## Mistake 3: Using `int` for sums

The sum can exceed `int`, so use:

```java
long
```

for maintained sums.

## Mistake 4: Not removing the outgoing element correctly

When the window exceeds size `m`, the oldest element must be deleted from the exact partition where it currently lives.

## Mistake 5: Wrong partition sizes

The target sizes are:

- `lo = k`
- `mid = m - 2k`
- `hi = k`

once the window reaches size `m`.

---

# 19. Final Interview Summary

The problem is a **sliding window + order statistics** problem.

For the current last `m` elements, we need:

- discard the smallest `k`
- discard the largest `k`
- average the middle block

The brute force solution sorts the last `m` elements every time, costing:

```text
O(m log m)
```

The optimal idea is to maintain the window incrementally.

A strong solution is to maintain three ordered multisets:

- `lo`: smallest `k`
- `mid`: middle `m - 2k`
- `hi`: largest `k`

and a running `midSum`.

Then:

- `addElement(num)` inserts the new value, removes the outgoing value if needed, and rebalances the three sets
- `calculateMKAverage()` returns:

```text
midSum / (m - 2k)
```

This gives:

```text
addElement      O(log m)
calculateMKAverage  O(1)
space           O(m)
```

A second optimal solution uses Fenwick trees because values are bounded by `10^5`, giving approximately:

```text
addElement          O(log V)
calculateMKAverage  O(log V)
```

where `V = 100000`.

Both are efficient enough, but the three-multiset approach maps most directly to the problem's definition.
