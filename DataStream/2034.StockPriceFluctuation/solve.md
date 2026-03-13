# 2034. Stock Price Fluctuation — Exhaustive Java Notes

## Problem Statement

You are given a stream of stock records. Each record contains:

- a `timestamp`
- a `price`

The stream has two complications:

1. Records may arrive **out of order**
2. A later record may **correct** the price at an existing timestamp

You need to design a data structure that supports:

- `update(timestamp, price)`
  update or correct the stock price at `timestamp`
- `current()`
  return the price at the **latest timestamp**
- `maximum()`
  return the **maximum current price**
- `minimum()`
  return the **minimum current price**

---

## Example

### Input

```text
["StockPrice", "update", "update", "current", "maximum", "update", "maximum", "update", "minimum"]
[[], [1, 10], [2, 5], [], [], [1, 3], [], [4, 2], []]
```

### Output

```text
[null, null, null, 5, 10, null, 5, null, 2]
```

### Explanation

```java
StockPrice stockPrice = new StockPrice();
stockPrice.update(1, 10); // timestamp 1 -> price 10
stockPrice.update(2, 5);  // timestamp 2 -> price 5
stockPrice.current();     // latest timestamp = 2, answer = 5
stockPrice.maximum();     // max price = 10
stockPrice.update(1, 3);  // correct timestamp 1 from 10 to 3
stockPrice.maximum();     // max price becomes 5
stockPrice.update(4, 2);  // timestamp 4 -> price 2
stockPrice.minimum();     // min price = 2
```

---

## Constraints

- `1 <= timestamp, price <= 10^9`
- At most `10^5` total calls
- `current`, `maximum`, and `minimum` are called only after at least one update

---

# 1. Core Observations

This is not just a simple stream problem.

We must handle **three independent requirements**:

### A. We need the latest timestamp

That suggests tracking:

- `latestTimestamp`

### B. We need corrections by timestamp

That suggests a map:

- `timestamp -> current price`

### C. We need fast min and max among the **current** prices

This is the tricky part.

Because timestamps can be corrected, an old price can become invalid.

For example:

```text
update(1, 10)
update(2, 5)
update(1, 3)
```

Now price `10` is no longer valid, even though it was inserted earlier.

So any solution that only stores values without handling stale data will be wrong.

---

# 2. What makes this problem tricky?

The difficulty is not `current()`.

That part is easy:

- keep track of the largest timestamp seen so far
- return the price stored at that timestamp

The challenge is `maximum()` and `minimum()` under **corrections**.

When a timestamp is updated:

```text
timestamp = t
old price = p1
new price = p2
```

we must ensure:

- `p1` no longer contributes if no timestamp currently has that price
- `p2` starts contributing

That is why many naive solutions fail.

---

# 3. Approach 1 — Brute Force with HashMap

## Idea

Maintain only:

- a `HashMap<Integer, Integer>` from timestamp to current price
- a variable `latestTimestamp`

Then:

- `update()` is easy
- `current()` is easy
- `maximum()` scans all map values
- `minimum()` scans all map values

This is the most direct approach.

---

## Java Code

```java
import java.util.*;

class StockPrice {
    private Map<Integer, Integer> timeToPrice;
    private int latestTimestamp;

    public StockPrice() {
        timeToPrice = new HashMap<>();
        latestTimestamp = 0;
    }

    public void update(int timestamp, int price) {
        timeToPrice.put(timestamp, price);
        latestTimestamp = Math.max(latestTimestamp, timestamp);
    }

    public int current() {
        return timeToPrice.get(latestTimestamp);
    }

    public int maximum() {
        int ans = Integer.MIN_VALUE;
        for (int price : timeToPrice.values()) {
            ans = Math.max(ans, price);
        }
        return ans;
    }

    public int minimum() {
        int ans = Integer.MAX_VALUE;
        for (int price : timeToPrice.values()) {
            ans = Math.min(ans, price);
        }
        return ans;
    }
}
```

---

## Complexity

Let `n` be the number of distinct timestamps currently stored.

- `update()` → `O(1)` average
- `current()` → `O(1)`
- `maximum()` → `O(n)`
- `minimum()` → `O(n)`

### Space

- `O(n)`

---

## Verdict

This works, but it is too slow in the worst case because we may do up to `10^5` operations.

If many operations are `maximum()` and `minimum()`, repeated full scans become expensive.

---

# 4. Approach 2 — HashMap + TreeMap of Price Frequencies

## Idea

Instead of scanning all prices every time, maintain counts of active prices.

We store:

1. `timeToPrice`
   - current price for each timestamp

2. `priceCount`
   - how many timestamps currently have a given price

3. `latestTimestamp`

Then:

- `update(timestamp, price)`:
  - if timestamp already exists, decrement the old price count
  - increment the new price count
- `current()`:
  - return price at latest timestamp
- `maximum()`:
  - return largest key in `TreeMap`
- `minimum()`:
  - return smallest key in `TreeMap`

This is a clean fully-valid approach.

---

## Why this works

Suppose:

```text
update(1, 10)
update(2, 5)
update(1, 3)
```

Then active prices are:

- `3` once
- `5` once

Price `10` is removed from the frequency map because its count drops to zero.

So the `TreeMap` always reflects the **current valid multiset** of prices.

---

## Java Code

```java
import java.util.*;

class StockPrice {
    private Map<Integer, Integer> timeToPrice;
    private TreeMap<Integer, Integer> priceCount;
    private int latestTimestamp;

    public StockPrice() {
        timeToPrice = new HashMap<>();
        priceCount = new TreeMap<>();
        latestTimestamp = 0;
    }

    public void update(int timestamp, int price) {
        if (timeToPrice.containsKey(timestamp)) {
            int oldPrice = timeToPrice.get(timestamp);
            int freq = priceCount.get(oldPrice);

            if (freq == 1) {
                priceCount.remove(oldPrice);
            } else {
                priceCount.put(oldPrice, freq - 1);
            }
        }

        timeToPrice.put(timestamp, price);
        priceCount.put(price, priceCount.getOrDefault(price, 0) + 1);
        latestTimestamp = Math.max(latestTimestamp, timestamp);
    }

    public int current() {
        return timeToPrice.get(latestTimestamp);
    }

    public int maximum() {
        return priceCount.lastKey();
    }

    public int minimum() {
        return priceCount.firstKey();
    }
}
```

---

## Complexity

Let `n` be the number of distinct timestamps, and `m` be the number of distinct active prices.

- `update()` → `O(log m)`
- `current()` → `O(1)`
- `maximum()` → `O(log m)` or effectively `O(1)` lookup depending on implementation details, but we treat ordered-map access as `O(log m)`
- `minimum()` → `O(log m)`

### Space

- `O(n + m)`

Since `m <= n`, this is `O(n)` space overall.

---

## Pros

- clean
- fully correct
- no lazy cleanup logic
- easy to reason about

## Cons

- ordered map operations are `O(log n)`
- not quite as elegant as the heap-based lazy-deletion solution many interviews expect

---

# 5. Approach 3 — HashMap + MaxHeap + MinHeap with Lazy Deletion

This is the most common optimal solution.

---

## Idea

We maintain:

1. `timeToPrice`
   - maps each timestamp to its **current correct price**

2. `latestTimestamp`
   - largest timestamp seen so far

3. `maxHeap`
   - stores pairs `(price, timestamp)` ordered by largest price first

4. `minHeap`
   - stores pairs `(price, timestamp)` ordered by smallest price first

Whenever we update a timestamp, we **do not remove old heap entries immediately**.

Instead, we push the new `(price, timestamp)` into both heaps.

This means heaps may contain stale entries, but we clean them **lazily** when answering `maximum()` or `minimum()`.

---

## Why lazy deletion is needed

Imagine:

```text
update(1, 10)
update(1, 3)
```

The heaps contain both:

- `(10, 1)` ← stale
- `(3, 1)` ← current

When `maximum()` checks the top, if `(10,1)` is on top, we verify:

```text
timeToPrice.get(1) == 10 ?
```

This is false, because current price at timestamp `1` is now `3`.

So we discard `(10,1)` and continue.

Eventually the top entry is guaranteed to be valid.

---

# 6. Why this approach is correct

For every timestamp, the map stores exactly one current price.

Each update inserts a new heap record for that timestamp.

So the heaps may contain multiple versions of the same timestamp, but only one version matches the current map value.

When we clean stale tops repeatedly, the first valid top is the true answer.

That gives us efficient min/max under corrections.

---

# 7. Optimal Java Solution

```java
import java.util.*;

class StockPrice {
    private Map<Integer, Integer> timeToPrice;
    private PriorityQueue<int[]> maxHeap;
    private PriorityQueue<int[]> minHeap;
    private int latestTimestamp;

    public StockPrice() {
        timeToPrice = new HashMap<>();

        maxHeap = new PriorityQueue<>((a, b) -> {
            if (a[0] != b[0]) return b[0] - a[0]; // larger price first
            return b[1] - a[1];
        });

        minHeap = new PriorityQueue<>((a, b) -> {
            if (a[0] != b[0]) return a[0] - b[0]; // smaller price first
            return a[1] - b[1];
        });

        latestTimestamp = 0;
    }

    public void update(int timestamp, int price) {
        timeToPrice.put(timestamp, price);
        maxHeap.offer(new int[]{price, timestamp});
        minHeap.offer(new int[]{price, timestamp});
        latestTimestamp = Math.max(latestTimestamp, timestamp);
    }

    public int current() {
        return timeToPrice.get(latestTimestamp);
    }

    public int maximum() {
        while (true) {
            int[] top = maxHeap.peek();
            int price = top[0];
            int timestamp = top[1];

            if (timeToPrice.get(timestamp) == price) {
                return price;
            }

            maxHeap.poll(); // stale entry
        }
    }

    public int minimum() {
        while (true) {
            int[] top = minHeap.peek();
            int price = top[0];
            int timestamp = top[1];

            if (timeToPrice.get(timestamp) == price) {
                return price;
            }

            minHeap.poll(); // stale entry
        }
    }
}
```

---

# 8. Step-by-Step Dry Run

Consider:

```text
update(1, 10)
update(2, 5)
update(1, 3)
```

---

## After `update(1, 10)`

Map:

```text
1 -> 10
```

Max heap contains:

```text
(10,1)
```

Min heap contains:

```text
(10,1)
```

Latest timestamp:

```text
1
```

---

## After `update(2, 5)`

Map:

```text
1 -> 10
2 -> 5
```

Max heap:

```text
(10,1), (5,2)
```

Min heap:

```text
(5,2), (10,1)
```

Latest timestamp:

```text
2
```

---

## After `update(1, 3)`

Map:

```text
1 -> 3
2 -> 5
```

Max heap may contain:

```text
(10,1), (5,2), (3,1)
```

Min heap may contain:

```text
(3,1), (10,1), (5,2)
```

Notice:

- `(10,1)` is now stale

---

## `current()`

Latest timestamp is `2`

```text
return 5
```

---

## `maximum()`

Top of max heap might be `(10,1)`

Check:

```text
timeToPrice.get(1) == 10 ?
```

No, current map has `1 -> 3`

So discard `(10,1)`

Next top is `(5,2)`

Check:

```text
timeToPrice.get(2) == 5 ?
```

Yes

```text
return 5
```

Correct.

---

## `minimum()`

Top of min heap is `(3,1)`

Check:

```text
timeToPrice.get(1) == 3 ?
```

Yes

```text
return 3
```

Correct.

---

# 9. Complexity of Heap + Lazy Deletion

Let `q` be the number of operations.

### `update(timestamp, price)`

We insert into two heaps:

- `O(log q)`

### `current()`

- `O(1)`

### `maximum()` / `minimum()`

At first glance, stale cleanup looks expensive.

But each stale entry is inserted once and removed at most once.

So across all operations, total cleanup is linear in the number of inserted heap entries.

Therefore:

- amortized `O(log q)` per query

### Space

Each update inserts one entry into both heaps:

- `O(q)`

Map also uses:

- `O(number of timestamps) <= O(q)`

So total:

- `O(q)`

---

# 10. Comparison of Approaches

| Approach                          |     Update | Current |              Max/Min |  Space | Notes                        |
| --------------------------------- | ---------: | ------: | -------------------: | -----: | ---------------------------- |
| HashMap + scan                    |     `O(1)` |  `O(1)` |               `O(n)` | `O(n)` | easiest, too slow            |
| HashMap + TreeMap counts          | `O(log n)` |  `O(1)` |           `O(log n)` | `O(n)` | very clean                   |
| HashMap + 2 heaps + lazy deletion | `O(log n)` |  `O(1)` | amortized `O(log n)` | `O(n)` | most standard optimal answer |

---

# 11. Which approach should you choose?

## In an interview

The strongest answer is usually:

- explain brute force briefly
- explain why corrections break naive heap logic
- present **HashMap + maxHeap + minHeap + lazy deletion**

That demonstrates both correctness and efficiency.

## In production-style code

`TreeMap + frequency counting` is often easier to reason about and maintain.

It avoids stale heap entries entirely.

So from a software-engineering perspective, the TreeMap solution is arguably cleaner.

---

# 12. Common Mistakes

## Mistake 1: Using only a heap without a timestamp map

If you only push prices into heaps, corrections cannot invalidate old values properly.

You need a way to verify whether a heap entry is still current.

That is exactly why we need:

```java
Map<Integer, Integer> timeToPrice
```

---

## Mistake 2: Forgetting that updates can overwrite old timestamps

This is not append-only.

For example:

```text
update(5, 100)
update(5, 1)
```

Now price `100` must no longer count.

---

## Mistake 3: Thinking `current()` means most recently updated record

It actually means:

> price at the largest timestamp

Not the last operation performed.

Example:

```text
update(10, 7)
update(3, 100)
```

`current()` must still return the price at timestamp `10`, not `3`.

---

## Mistake 4: Removing arbitrary heap entries directly

Heaps do not support efficient arbitrary deletion unless you add extra indexing structures.

Lazy deletion is the clean workaround.

---

# 13. Correctness Argument for the Heap Solution

We should be precise here.

---

## Invariant 1

`timeToPrice` always stores the correct current price for every timestamp.

This is true because each `update(timestamp, price)` overwrites the map entry.

---

## Invariant 2

For every current `(timestamp, price)` pair in the map, both heaps contain at least one matching entry `(price, timestamp)`.

This is true because every update inserts the new pair into both heaps.

---

## Invariant 3

A heap entry `(price, timestamp)` is valid iff:

```java
timeToPrice.get(timestamp) == price
```

If not, it corresponds to an outdated version of that timestamp.

---

## Why `maximum()` is correct

The max heap orders entries by decreasing price.

If the top is stale, it is discarded.

Eventually the top becomes valid.

Because every valid current entry exists in the heap, and the heap orders by price, the first valid top must be the maximum current price.

Same logic applies to `minimum()`.

So the algorithm is correct.

---

# 14. Interview-Style Explanation

A concise way to explain the optimal solution:

> I keep a hash map from timestamp to current price, so I can overwrite corrections and answer `current()` using the latest timestamp.
> For `maximum()` and `minimum()`, I use a max heap and min heap of `(price, timestamp)` pairs. Since corrections create stale heap entries, I use lazy deletion: whenever I look at the heap top, I compare it with the current map value for that timestamp. If it does not match, I pop it. The first matching top is the correct max or min.

That is the essence.

---

# 15. Final Recommended Java Solution

This is the version I would submit.

```java
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

class StockPrice {
    private final Map<Integer, Integer> timeToPrice;
    private final PriorityQueue<int[]> maxHeap;
    private final PriorityQueue<int[]> minHeap;
    private int latestTimestamp;

    public StockPrice() {
        timeToPrice = new HashMap<>();

        maxHeap = new PriorityQueue<>((a, b) -> {
            if (a[0] != b[0]) {
                return Integer.compare(b[0], a[0]);
            }
            return Integer.compare(b[1], a[1]);
        });

        minHeap = new PriorityQueue<>((a, b) -> {
            if (a[0] != b[0]) {
                return Integer.compare(a[0], b[0]);
            }
            return Integer.compare(a[1], b[1]);
        });

        latestTimestamp = 0;
    }

    public void update(int timestamp, int price) {
        timeToPrice.put(timestamp, price);
        maxHeap.offer(new int[]{price, timestamp});
        minHeap.offer(new int[]{price, timestamp});
        latestTimestamp = Math.max(latestTimestamp, timestamp);
    }

    public int current() {
        return timeToPrice.get(latestTimestamp);
    }

    public int maximum() {
        while (true) {
            int[] top = maxHeap.peek();
            int price = top[0];
            int timestamp = top[1];

            if (timeToPrice.get(timestamp) == price) {
                return price;
            }

            maxHeap.poll();
        }
    }

    public int minimum() {
        while (true) {
            int[] top = minHeap.peek();
            int price = top[0];
            int timestamp = top[1];

            if (timeToPrice.get(timestamp) == price) {
                return price;
            }

            minHeap.poll();
        }
    }
}
```

---

# 16. Final Summary

The problem has three separate needs:

- correction by timestamp
- latest timestamp lookup
- min/max among current prices

A plain map solves the first two, but not min/max efficiently.

There are two strong ways to solve min/max:

1. **TreeMap of price frequencies**
   - simpler and very clean
2. **MinHeap + MaxHeap with lazy deletion**
   - standard optimal interview solution

The heap solution works because stale entries can be detected using the timestamp-to-current-price map.

So the final efficient design is:

- `HashMap<Integer, Integer> timeToPrice`
- `int latestTimestamp`
- `PriorityQueue<int[]> maxHeap`
- `PriorityQueue<int[]> minHeap`

This gives:

- `update()` → `O(log n)`
- `current()` → `O(1)`
- `maximum()` / `minimum()` → amortized `O(log n)`
- space → `O(n)`

---

## Note about the class in your prompt

Your prompt describes **Stock Price Fluctuation**, but the code skeleton included `DetectSquares`.
For this problem, the correct class should be:

```java
class StockPrice {
    public StockPrice() { }
    public void update(int timestamp, int price) { }
    public int current() { }
    public int maximum() { }
    public int minimum() { }
}
```
