# Number of Recent Calls — Sliding Window Approach

## Overview

This problem tests basic knowledge of **data structures and algorithms**.

We are given a sequence of ping calls:

```
[t1, t2, t3, ... , tn]
```

These timestamps are:

- **Strictly increasing**
- Processed **one at a time**

For each call `ti`, we must count how many calls fall within the time range:

```
[ti - 3000, ti]
```

---

# Key Observation

The stream of timestamps:

- Always **increases**
- Grows continuously

However, older calls eventually become **irrelevant** once they fall outside the `3000 ms` window.

Therefore:

We only need to store calls that belong to the **recent window**.

This leads naturally to a **Sliding Window** approach.

---

# Approach 1: Sliding Window with LinkedList

## Intuition

We maintain a container storing **only recent pings**.

For each new ping:

1. Add the new timestamp
2. Remove timestamps older than `t - 3000`

The remaining timestamps form the valid window.

---

## Data Structure Choice

We need efficient operations:

| Operation | Requirement                 |
| --------- | --------------------------- |
| Append    | Add new ping at end         |
| Remove    | Remove old pings from start |

Suitable structures:

- Java → `LinkedList`
- Python → `deque`

These support:

```
O(1) insertion
O(1) deletion
```

---

# Algorithm

Maintain a container called:

```
slidingWindow
```

### ping(t)

Step 1 — Add the new ping

```
append t to slidingWindow
```

Step 2 — Remove outdated pings

Remove elements from the front while:

```
timestamp < t - 3000
```

Step 3 — Return window size

```
size(slidingWindow)
```

---

# Implementation

```java
class RecentCounter {

    LinkedList<Integer> slideWindow;

    public RecentCounter() {
        slideWindow = new LinkedList<>();
    }

    public int ping(int t) {

        // Step 1: add new ping
        slideWindow.addLast(t);

        // Step 2: remove outdated pings
        while (slideWindow.getFirst() < t - 3000) {
            slideWindow.removeFirst();
        }

        // Step 3: return number of recent calls
        return slideWindow.size();
    }
}
```

---

# Complexity Analysis

## Maximum Sliding Window Size

Because timestamps are strictly increasing:

```
max window size = 3000
```

This represents the maximum time range.

---

## Time Complexity

Worst-case removal loop:

```
O(3000)
```

Since `3000` is constant:

```
O(1)
```

Amortized behavior:

Each timestamp is:

- Added once
- Removed once

Therefore each operation remains constant time.

---

## Space Complexity

Maximum stored timestamps:

```
3000
```

Thus:

```
O(1)
```

Constant space.

---

# Discussion

You might wonder whether **binary search** could be used to find outdated calls faster.

Binary search requires:

```
random access
```

This works well with **arrays**, but not with **linked lists**, because locating a middle element takes linear time.

If we switched to an array:

- Removing old elements becomes expensive
- Memory usage grows unnecessarily

Therefore the **LinkedList + sliding window** approach is both:

- Simpler
- More efficient

---

# Related Problem

A similar system-design style problem:

```
Logger Rate Limiter
```

Both problems involve maintaining **recent events within a time window**.
