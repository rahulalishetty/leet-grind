# 362. Design Hit Counter — Approaches

## Approach 1: Using Queue

### Intuition

A key observation is that timestamps arrive in **monotonically increasing order**.
Also, we only care about hits that happened within the **last 300 seconds (5 minutes)**.

Therefore, timestamps older than `300` seconds compared to the current timestamp must be discarded.

This behavior naturally follows **FIFO (First In First Out)** order, which makes a **Queue** an appropriate data structure.

---

## Algorithm

1. Store each timestamp in a queue when `hit()` is called.
2. During `getHits(timestamp)`:
   - Remove all timestamps whose difference with the current timestamp is **≥ 300**.
3. The number of hits is simply the **size of the queue**.

---

## Implementation

```java
class HitCounter {

    private Queue<Integer> hits;

    public HitCounter() {
        this.hits = new LinkedList<Integer>();
    }

    public void hit(int timestamp) {
        this.hits.add(timestamp);
    }

    public int getHits(int timestamp) {

        while (!this.hits.isEmpty()) {
            int diff = timestamp - this.hits.peek();

            if (diff >= 300)
                this.hits.remove();
            else
                break;
        }

        return this.hits.size();
    }
}
```

---

## Complexity Analysis

### Time Complexity

**hit()**

```
O(1)
```

Queue insertion takes constant time.

**getHits()**

Worst case a single call may remove many elements.

```
O(n)
```

However each timestamp is processed only twice:

- once when inserted
- once when removed

Therefore across `N` operations:

```
Total time = O(N)
```

Amortized per call:

```
O(1)
```

---

### Space Complexity

```
O(N)
```

Where `N` is the total number of timestamps stored.

---

# Approach 2: Using Deque with Pairs

## Intuition

Consider the follow‑up scenario:

Many hits may occur **at the same timestamp**.

Example:

```
hit(1)
hit(1)
hit(1)
hit(1)
hit(1)
```

Approach 1 stores:

```
[1,1,1,1,1]
```

This causes **repeated removals later**.

Instead we store:

```
(timestamp, count)
```

Example:

```
(1,5)
```

This compresses repeated timestamps.

To support efficient insertions and deletions from both ends we use a **Deque**.

---

## Algorithm

Maintain:

```
Deque<Pair<timestamp, count>>
totalHits
```

### hit(timestamp)

1. If the last entry has the same timestamp:
   - increment its count
2. Otherwise:
   - append a new `(timestamp, 1)` pair

Increase `totalHits`.

---

### getHits(timestamp)

1. Remove entries older than **300 seconds**.
2. Decrease `totalHits` by the count removed.
3. Return `totalHits`.

---

## Implementation

```java
class HitCounter {

    private int total;
    private Deque<Pair<Integer, Integer>> hits;

    public HitCounter() {
        this.total = 0;
        this.hits = new LinkedList<Pair<Integer, Integer>>();
    }

    public void hit(int timestamp) {

        if (this.hits.isEmpty() || this.hits.getLast().getKey() != timestamp) {

            this.hits.add(new Pair<Integer, Integer>(timestamp, 1));

        } else {

            int prevCount = this.hits.getLast().getValue();

            this.hits.removeLast();

            this.hits.add(new Pair<Integer, Integer>(timestamp, prevCount + 1));
        }

        this.total++;
    }

    public int getHits(int timestamp) {

        while (!this.hits.isEmpty()) {

            int diff = timestamp - this.hits.getFirst().getKey();

            if (diff >= 300) {

                this.total -= this.hits.getFirst().getValue();

                this.hits.removeFirst();

            } else {
                break;
            }
        }

        return this.total;
    }
}
```

---

# Complexity Analysis

## Time Complexity

### hit()

```
O(1)
```

### getHits()

Worst case:

```
O(n)
```

Where `n` is number of timestamp pairs.

However repeated timestamps are **compressed into one pair**, so if a timestamp repeats `k` times:

Removal cost becomes:

```
O(1)
```

instead of `O(k)`.

---

## Space Complexity

```
O(N)
```

Where `N` is total hits processed.

With repeated timestamps:

```
space reduces significantly
```

because multiple hits share the same `(timestamp,count)` pair.
