# 362. Design Hit Counter

Design a **hit counter** that counts the number of hits received in the **past 5 minutes (300 seconds)**.

The system accepts timestamps in **seconds granularity**, and calls are made in **chronological order** (timestamps are monotonically increasing).

Multiple hits may occur at the **same timestamp**.

---

## Class Design

Implement the following class:

### `HitCounter()`

Initializes the hit counter.

### `void hit(int timestamp)`

Records a hit that happened at the given timestamp.

Multiple hits may occur at the same timestamp.

### `int getHits(int timestamp)`

Returns the number of hits received in the **past 5 minutes (300 seconds)** from the given timestamp.

---

# Example

## Input

```
["HitCounter", "hit", "hit", "hit", "getHits", "hit", "getHits", "getHits"]
[[], [1], [2], [3], [4], [300], [300], [301]]
```

## Output

```
[null, null, null, null, 3, null, 4, 3]
```

## Explanation

```
HitCounter hitCounter = new HitCounter();

hitCounter.hit(1);       // hit at timestamp 1
hitCounter.hit(2);       // hit at timestamp 2
hitCounter.hit(3);       // hit at timestamp 3

hitCounter.getHits(4);   // returns 3

hitCounter.hit(300);     // hit at timestamp 300

hitCounter.getHits(300); // returns 4

hitCounter.getHits(301); // returns 3
```

---

# Constraints

- `1 <= timestamp <= 2 * 10^9`
- All calls are made in **chronological order**
- At most **300 calls** will be made to `hit` and `getHits`

---

# Follow-up

What if the **number of hits per second is extremely large**?

Would the same design still scale efficiently?
