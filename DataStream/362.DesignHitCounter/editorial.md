# Design Hit Counter — Approaches

## Approach 1: Using Queue

### Intuition

A key observation is that timestamps arrive in **monotonically increasing order**.

Also, when timestamps grow, we must ignore timestamps that are **300 seconds older** than the current timestamp.

This behavior naturally matches a **FIFO structure**, therefore a **queue** works well.

---

### Algorithm

1. Maintain a queue storing timestamps.
2. `hit(timestamp)` → push timestamp into the queue.
3. `getHits(timestamp)` → remove all timestamps where:

```
timestamp - oldTimestamp >= 300
```

4. The number of hits equals the **size of the queue**.

---

### Implementation

```java
class HitCounter {
    private Queue<Integer> hits;

    public HitCounter() {
        this.hits = new LinkedList<>();
    }

    public void hit(int timestamp) {
        hits.add(timestamp);
    }

    public int getHits(int timestamp) {
        while (!hits.isEmpty()) {
            int diff = timestamp - hits.peek();

            if (diff >= 300) {
                hits.remove();
            } else {
                break;
            }
        }

        return hits.size();
    }
}
```

---

### Complexity Analysis

Let **N** be the total number of timestamps processed.

#### Time Complexity

```
hit      → O(1)
getHits  → amortized O(1)
```

Worst case:

```
O(n)
```

when removing many timestamps.

However each timestamp is:

- inserted once
- removed once

Total operations across all calls:

```
O(N)
```

Therefore **amortized O(1)**.

---

#### Space Complexity

```
O(N)
```

The queue stores all timestamps within the last 300 seconds.

---

# Approach 2: Using Deque with Pairs

### Intuition

In real systems, **many hits may occur at the same timestamp**.

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

This leads to repeated removals.

Instead we store:

```
(timestamp, count)
```

Example:

```
(1,5)
```

This dramatically reduces memory and operations.

---

### Data Structures

```
Deque<Pair<timestamp,count>>
int totalHits
```

`totalHits` tracks number of hits in the last 300 seconds.

---

### Algorithm

#### hit(timestamp)

1. If last timestamp equals current timestamp:

```
increase count
```

2. Otherwise insert new pair:

```
(timestamp,1)
```

3. Increment total hits.

---

#### getHits(timestamp)

1. Remove expired timestamps:

```
timestamp - oldestTimestamp >= 300
```

2. Reduce `totalHits` using the count removed.

3. Return `totalHits`.

---

### Implementation

```java
class HitCounter {

    private int total;
    private Deque<Pair<Integer,Integer>> hits;

    public HitCounter() {
        total = 0;
        hits = new LinkedList<>();
    }

    public void hit(int timestamp) {

        if (hits.isEmpty() || hits.getLast().getKey() != timestamp) {
            hits.add(new Pair<>(timestamp,1));
        } else {

            int prev = hits.getLast().getValue();
            hits.removeLast();

            hits.add(new Pair<>(timestamp, prev + 1));
        }

        total++;
    }

    public int getHits(int timestamp) {

        while (!hits.isEmpty()) {

            int diff = timestamp - hits.getFirst().getKey();

            if (diff >= 300) {

                total -= hits.getFirst().getValue();
                hits.removeFirst();

            } else {
                break;
            }
        }

        return total;
    }
}
```

---

### Complexity Analysis

#### Time Complexity

```
hit      → O(1)
getHits  → O(n) worst case
```

However when **k hits share the same timestamp**:

- Approach 1 removes **k elements**
- Approach 2 removes **1 pair**

Thus operations become significantly cheaper.

---

#### Space Complexity

Worst case:

```
O(N)
```

But with repeated timestamps:

```
O(unique timestamps)
```

which can be much smaller.
