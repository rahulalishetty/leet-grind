# 3829. Design Ride Sharing System — Exhaustive Java Notes

## Problem Statement

A ride sharing system manages:

- **riders** waiting for rides
- **drivers** becoming available over time

The system must always match in **arrival order**:

- earliest available **driver**
- with earliest waiting **rider**

We need to implement:

```java
class RideSharingSystem {

    public RideSharingSystem() {

    }

    public void addRider(int riderId) {

    }

    public void addDriver(int driverId) {

    }

    public int[] matchDriverWithRider() {

    }

    public void cancelRider(int riderId) {

    }
}
```

---

## Required Operations

### `addRider(int riderId)`

Adds a rider to the waiting queue.

### `addDriver(int driverId)`

Adds a driver to the available queue.

### `matchDriverWithRider()`

Matches the earliest available driver with the earliest waiting rider.

Returns:

```java
new int[]{driverId, riderId}
```

If either side is unavailable:

```java
new int[]{-1, -1}
```

### `cancelRider(int riderId)`

Cancels a rider if they are still waiting and not already matched.

---

## Example 1

```text
Input:
["RideSharingSystem", "addRider", "addDriver", "addRider", "matchDriverWithRider", "addDriver", "cancelRider", "matchDriverWithRider", "matchDriverWithRider"]
[[], [3], [2], [1], [], [5], [3], [], []]

Output:
[null, null, null, null, [2, 3], null, null, [5, 1], [-1, -1]]
```

### Explanation

- rider `3` arrives
- driver `2` arrives
- rider `1` arrives
- match → `[2, 3]`
- driver `5` arrives
- cancel rider `3` → no effect because already matched
- match → `[5, 1]`
- no one left → `[-1, -1]`

---

## Example 2

```text
Input:
["RideSharingSystem", "addRider", "addDriver", "addDriver", "matchDriverWithRider", "addRider", "cancelRider", "matchDriverWithRider"]
[[], [8], [8], [6], [], [2], [2], []]

Output:
[null, null, null, null, [8, 8], null, null, [-1, -1]]
```

---

## Constraints

- `1 <= riderId, driverId <= 1000`
- Each `riderId` is unique among riders and added at most once
- Each `driverId` is unique among drivers and added at most once
- At most `1000` total calls

---

# 1. Core Observation

This is fundamentally a **queue matching** problem.

We need:

- riders in order of arrival
- drivers in order of arrival
- cancellation support for riders

The difficulty is not matching itself.

Matching is easy if we have two queues:

- `riders`
- `drivers`

The real difficulty is:

> How do we cancel a rider who may be somewhere inside the rider queue?

That single requirement determines the data structure design.

---

# 2. Smallest Possible Insight

If cancellation did **not** exist, the problem would be trivial:

- store riders in a queue
- store drivers in a queue
- pop one from each when matching

But `cancelRider(riderId)` means the rider may need to disappear **before reaching the front**.

A plain queue is not enough unless we accept lazy deletion or expensive middle removal.

That leads naturally to multiple approaches.

---

# 3. Approach 1 — Plain Queues + Lazy Cancellation

## Idea

Use:

- a queue for riders
- a queue for drivers
- a set of canceled riders

When a rider is canceled:

- do not physically remove them from the queue
- just mark them as canceled

When matching:

- discard canceled riders from the front until the front rider is valid

This is a classic **lazy deletion** technique.

---

## Why it works

Suppose rider queue is:

```text
[3, 1, 7, 9]
```

and rider `1` is canceled.

Instead of removing `1` from the middle, we store:

```text
canceled = {1}
```

Later, when matching reaches the front:

- if front rider is canceled, pop and skip
- otherwise use them

Each rider enters the queue once and leaves once, so even though some cleanup happens later, total work stays efficient.

---

## Java Code

```java
import java.util.*;

class RideSharingSystem {
    private Queue<Integer> riders;
    private Queue<Integer> drivers;
    private Set<Integer> canceled;

    public RideSharingSystem() {
        riders = new ArrayDeque<>();
        drivers = new ArrayDeque<>();
        canceled = new HashSet<>();
    }

    public void addRider(int riderId) {
        riders.offer(riderId);
    }

    public void addDriver(int driverId) {
        drivers.offer(driverId);
    }

    public int[] matchDriverWithRider() {
        // Remove canceled riders from the front.
        while (!riders.isEmpty() && canceled.contains(riders.peek())) {
            canceled.remove(riders.poll());
        }

        if (riders.isEmpty() || drivers.isEmpty()) {
            return new int[]{-1, -1};
        }

        int driverId = drivers.poll();
        int riderId = riders.poll();
        return new int[]{driverId, riderId};
    }

    public void cancelRider(int riderId) {
        canceled.add(riderId);
    }
}
```

---

## Complexity

### `addRider`

```text
O(1)
```

### `addDriver`

```text
O(1)
```

### `cancelRider`

```text
O(1)
```

### `matchDriverWithRider`

Worst-case for a single call may skip many canceled riders:

```text
O(n)
```

But amortized over all operations:

```text
O(1)
```

because each rider is removed from the queue at most once.

### Space

```text
O(r + d)
```

where `r` and `d` are total riders/drivers currently stored.

---

## Pros

- very simple
- practical
- excellent under these constraints
- clean amortized performance

## Cons

- `matchDriverWithRider()` is not strict worst-case `O(1)`
- cleanup is deferred rather than immediate

---

# 4. Approach 2 — Doubly Linked List + HashMap for Riders

## Idea

To support real cancellation in `O(1)`, store riders in a **doubly linked list**, and map each rider ID to its node.

Then:

- `addRider(riderId)` → append node at tail
- `cancelRider(riderId)` → remove its node directly using the map
- `matchDriverWithRider()` → pop head rider, pop head driver

Drivers do not need cancellation, so a normal queue is enough for them.

---

## Why this is stronger

In Approach 1, rider cancellation is delayed.

In this approach, rider cancellation is immediate:

- no skipped elements later
- no lazy cleanup
- exact queue contents always reflect active waiting riders

This is useful when strict real-time structure consistency matters.

---

## Data Structures

### Riders

- doubly linked list preserving arrival order
- `Map<Integer, Node>` for O(1) lookup by rider ID

### Drivers

- `ArrayDeque<Integer>` queue

---

## Java Code

```java
import java.util.*;

class RideSharingSystem {
    private static class Node {
        int riderId;
        Node prev, next;

        Node(int riderId) {
            this.riderId = riderId;
        }
    }

    private Node head;
    private Node tail;
    private Map<Integer, Node> riderMap;
    private Queue<Integer> drivers;

    public RideSharingSystem() {
        riderMap = new HashMap<>();
        drivers = new ArrayDeque<>();
    }

    public void addRider(int riderId) {
        Node node = new Node(riderId);
        riderMap.put(riderId, node);

        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    public void addDriver(int driverId) {
        drivers.offer(driverId);
    }

    public int[] matchDriverWithRider() {
        if (head == null || drivers.isEmpty()) {
            return new int[]{-1, -1};
        }

        int driverId = drivers.poll();
        int riderId = head.riderId;

        removeNode(head);
        riderMap.remove(riderId);

        return new int[]{driverId, riderId};
    }

    public void cancelRider(int riderId) {
        Node node = riderMap.get(riderId);
        if (node == null) return; // already matched or already canceled
        removeNode(node);
        riderMap.remove(riderId);
    }

    private void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;

        if (prev != null) prev.next = next;
        else head = next;

        if (next != null) next.prev = prev;
        else tail = prev;

        node.prev = null;
        node.next = null;
    }
}
```

---

## Complexity

### `addRider`

```text
O(1)
```

### `addDriver`

```text
O(1)
```

### `cancelRider`

```text
O(1)
```

### `matchDriverWithRider`

```text
O(1)
```

### Space

```text
O(r + d)
```

---

## Pros

- all operations are true `O(1)`
- immediate cancellation
- no lazy cleanup

## Cons

- more code
- more implementation detail
- easy to make pointer bugs in interviews if rushed

---

# 5. Approach 3 — LinkedHashSet for Riders + Queue for Drivers

## Idea

Java gives us a very convenient structure:

```java
LinkedHashSet
```

It preserves insertion order **and** supports O(1) add/remove/contains on average.

That makes it a great fit for riders:

- arrival order matters
- cancellation by ID matters

So we can use:

- `LinkedHashSet<Integer>` for riders
- `ArrayDeque<Integer>` for drivers

Then:

- earliest rider = first element of the linked hash set iterator
- cancel rider = `riders.remove(riderId)`

This is elegant and compact.

---

## Why it works

A `LinkedHashSet` is basically:

- a hash table for O(1) lookup
- plus linked-order maintenance for iteration in insertion order

So it behaves like:

- queue order
- set deletion by value

That is exactly what riders need.

---

## Java Code

```java
import java.util.*;

class RideSharingSystem {
    private LinkedHashSet<Integer> riders;
    private Queue<Integer> drivers;

    public RideSharingSystem() {
        riders = new LinkedHashSet<>();
        drivers = new ArrayDeque<>();
    }

    public void addRider(int riderId) {
        riders.add(riderId);
    }

    public void addDriver(int driverId) {
        drivers.offer(driverId);
    }

    public int[] matchDriverWithRider() {
        if (riders.isEmpty() || drivers.isEmpty()) {
            return new int[]{-1, -1};
        }

        int riderId = riders.iterator().next();
        riders.remove(riderId);

        int driverId = drivers.poll();
        return new int[]{driverId, riderId};
    }

    public void cancelRider(int riderId) {
        riders.remove(riderId);
    }
}
```

---

## Complexity

### `addRider`

```text
O(1) average
```

### `addDriver`

```text
O(1)
```

### `cancelRider`

```text
O(1) average
```

### `matchDriverWithRider`

- get first rider via iterator
- remove rider
- pop driver

Overall:

```text
O(1) average
```

### Space

```text
O(r + d)
```

---

## Pros

- shortest clean solution
- supports immediate cancellation
- avoids manual linked list code

## Cons

- depends on language-specific data structure
- some interviewers may want a more explicit structure
- "iterator().next()" is neat, but some people find it less transparent

---

# 6. Which Approach Is Best?

For this problem’s constraints, all three are acceptable.

## Best practical choice

**Approach 3 — LinkedHashSet + Queue**

Reason:

- cleanest code
- immediate cancellation
- efficient enough
- easiest to explain

## Best general algorithmic teaching choice

**Approach 2 — Doubly Linked List + HashMap**

Reason:

- teaches how to combine:
  - queue order
  - O(1) deletion by key
- common systems-design/data-structure pattern

## Best simplest choice

**Approach 1 — Queues + Lazy Cancellation**

Reason:

- minimum implementation effort
- excellent when amortized behavior is acceptable

---

# 7. Dry Run

Let us dry run Approach 3.

## Operations

```text
addRider(3)
addDriver(2)
addRider(1)
matchDriverWithRider()
addDriver(5)
cancelRider(3)
matchDriverWithRider()
matchDriverWithRider()
```

---

## After `addRider(3)`

```text
riders = [3]
drivers = []
```

## After `addDriver(2)`

```text
riders = [3]
drivers = [2]
```

## After `addRider(1)`

```text
riders = [3, 1]
drivers = [2]
```

## First `matchDriverWithRider()`

Earliest rider = `3`
Earliest driver = `2`

Return:

```text
[2, 3]
```

State becomes:

```text
riders = [1]
drivers = []
```

## After `addDriver(5)`

```text
riders = [1]
drivers = [5]
```

## `cancelRider(3)`

Rider `3` is already matched, so not present.

No change:

```text
riders = [1]
drivers = [5]
```

## Second `matchDriverWithRider()`

Earliest rider = `1`
Earliest driver = `5`

Return:

```text
[5, 1]
```

State becomes:

```text
riders = []
drivers = []
```

## Third `matchDriverWithRider()`

One side empty.

Return:

```text
[-1, -1]
```

Correct.

---

# 8. Correctness Argument

We should be precise about why the design works.

## Invariant 1

The rider structure stores exactly the riders who are currently waiting and not canceled or matched.

## Invariant 2

The rider structure preserves rider arrival order.

## Invariant 3

The driver queue stores exactly the drivers who are currently available and unmatched, in arrival order.

From these invariants, the matching rule becomes immediate:

- earliest waiting rider = front/first rider
- earliest available driver = front driver

So `matchDriverWithRider()` always returns the correct pair whenever both exist.

When a rider is canceled:

- that rider is removed from waiting storage
- therefore they cannot be matched later

That matches the problem requirement exactly.

---

# 9. Edge Cases

## Case 1: No riders

```java
matchDriverWithRider()
```

should return:

```java
new int[]{-1, -1}
```

## Case 2: No drivers

Same result:

```java
new int[]{-1, -1}
```

## Case 3: Cancel already matched rider

No effect.

## Case 4: Cancel non-waiting rider

No effect.

## Case 5: Rider and driver IDs may numerically match

That does not matter.

For example:

- rider ID = 8
- driver ID = 8

These are different domains logically. The match is still valid:

```text
[8, 8]
```

---

# 10. Interview Discussion

A strong interview explanation could be:

> I need FIFO matching on both sides, so queues naturally model drivers and riders. The extra challenge is rider cancellation before matching. A plain queue does not support efficient middle deletion. So I either use lazy deletion with a cancellation set, or I store riders in a structure that preserves insertion order and supports O(1) deletion by ID, such as a doubly linked list plus hash map, or a LinkedHashSet in Java.

That explanation shows:

- you identified the real difficulty
- you chose data structures because of operation requirements
- not because of pattern memorization

---

# 11. Recommended Final Java Solution

For Java, this is the nicest version to submit.

```java
import java.util.*;

class RideSharingSystem {
    private LinkedHashSet<Integer> riders;
    private Queue<Integer> drivers;

    public RideSharingSystem() {
        riders = new LinkedHashSet<>();
        drivers = new ArrayDeque<>();
    }

    public void addRider(int riderId) {
        riders.add(riderId);
    }

    public void addDriver(int driverId) {
        drivers.offer(driverId);
    }

    public int[] matchDriverWithRider() {
        if (riders.isEmpty() || drivers.isEmpty()) {
            return new int[]{-1, -1};
        }

        int riderId = riders.iterator().next();
        riders.remove(riderId);

        int driverId = drivers.poll();
        return new int[]{driverId, riderId};
    }

    public void cancelRider(int riderId) {
        riders.remove(riderId);
    }
}
```

---

# 12. Why This Final Version Is Good

It directly matches the problem needs:

- **Riders**
  - maintain arrival order
  - allow cancellation by ID
  - allow taking earliest rider

- **Drivers**
  - only need FIFO queue

That is why the asymmetric design is natural:

- `LinkedHashSet<Integer>` for riders
- `Queue<Integer>` for drivers

---

# 13. Comparison Table

| Approach | Rider Structure              | Driver Structure | Cancel Rider   | Match          | Notes                           |
| -------- | ---------------------------- | ---------------- | -------------- | -------------- | ------------------------------- |
| 1        | Queue + canceled set         | Queue            | O(1) mark only | amortized O(1) | simplest lazy deletion          |
| 2        | Doubly linked list + HashMap | Queue            | O(1)           | O(1)           | most explicit robust design     |
| 3        | LinkedHashSet                | Queue            | O(1) avg       | O(1) avg       | cleanest Java-specific solution |

---

# 14. Final Takeaway

The heart of the problem is not queue matching.

The heart is this combination:

- preserve arrival order
- delete a waiting rider by ID

Once you identify that, the right structures become obvious.

If you want the cleanest Java answer, use:

- `LinkedHashSet<Integer>` for riders
- `ArrayDeque<Integer>` for drivers

If you want the most data-structure-heavy and interview-classic answer, use:

- doubly linked list + hash map for riders
- queue for drivers

Both are fully valid.
