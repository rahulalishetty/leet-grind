# 2526. Find Consecutive Integers from a Data Stream

## Problem Statement

For a stream of integers, implement a data structure that checks whether the **last `k` integers** in the stream are all equal to a given `value`.

Implement the `DataStream` class:

```java
class DataStream {

    public DataStream(int value, int k) {

    }

    public boolean consec(int num) {

    }
}
```

- `DataStream(int value, int k)` initializes the object.
- `boolean consec(int num)` adds `num` to the stream and returns:
  - `true` if the last `k` integers are all equal to `value`
  - `false` otherwise

If fewer than `k` integers have been seen, the answer must be `false`.

---

## Example

### Input

```text
["DataStream", "consec", "consec", "consec", "consec"]
[[4, 3], [4], [4], [4], [3]]
```

### Output

```text
[null, false, false, true, false]
```

### Explanation

```java
DataStream dataStream = new DataStream(4, 3); // value = 4, k = 3

dataStream.consec(4); // false
dataStream.consec(4); // false
dataStream.consec(4); // true
dataStream.consec(3); // false
```

---

## Constraints

- `1 <= value, num <= 10^9`
- `1 <= k <= 10^5`
- At most `10^5` calls will be made to `consec`

---

# Core Observation

We only care about one condition:

> Are the **last `k` elements** all equal to `value`?

That means every time a new number arrives, we do **not** need the whole stream history.
We only need enough information to determine whether the current suffix of length `k` is fully composed of `value`.

This is a strong hint that a compact sliding-window style solution exists.

---

# Approach 1: Brute Force with Explicit Last-k Window

## Intuition

The most direct idea is:

1. Keep the stream elements in a queue.
2. Keep only the last `k` elements.
3. Whenever `consec(num)` is called:
   - append `num`
   - remove older elements if the queue becomes larger than `k`
   - if queue size is less than `k`, return `false`
   - otherwise scan the queue and check whether every element equals `value`

This is the most literal implementation of the problem.

---

## Java Code

```java
import java.util.*;

class DataStream {
    private final int value;
    private final int k;
    private final Deque<Integer> window;

    public DataStream(int value, int k) {
        this.value = value;
        this.k = k;
        this.window = new ArrayDeque<>();
    }

    public boolean consec(int num) {
        window.addLast(num);

        if (window.size() > k) {
            window.removeFirst();
        }

        if (window.size() < k) {
            return false;
        }

        for (int x : window) {
            if (x != value) {
                return false;
            }
        }

        return true;
    }
}
```

---

## Complexity Analysis

### Time Complexity

For each `consec(num)` call:

- insert into deque: `O(1)`
- optional removal from front: `O(1)`
- scan up to `k` elements: `O(k)`

So total:

```text
O(k) per call
```

In the worst case across `n` calls:

```text
O(n * k)
```

---

### Space Complexity

We store at most `k` elements in the deque:

```text
O(k)
```

---

## Pros

- Very easy to understand
- Closely matches the problem statement
- Good first solution in an interview if you want to build intuition

## Cons

- Re-scans the same window repeatedly
- Too wasteful when there are many queries

---

# Approach 2: Sliding Window + Count of Matching Values

## Intuition

The waste in Approach 1 comes from scanning all `k` elements every time.

Instead, inside the current last-`k` window, we can maintain:

- how many elements currently equal `value`

Then:

- if the window size is less than `k`, answer is `false`
- if the window size is exactly `k`, answer is `true` **iff**
  the number of matching elements is `k`

This removes the repeated scan.

---

## How It Works

Suppose:

- `window` stores the last at most `k` elements
- `matchCount` stores how many elements in `window` equal `value`

When `num` arrives:

1. Insert `num`
2. If `num == value`, increment `matchCount`
3. If window size exceeds `k`, remove the oldest element:
   - if removed element was `value`, decrement `matchCount`
4. If window size is less than `k`, return `false`
5. Otherwise return `matchCount == k`

This is a standard fixed-size sliding window.

---

## Java Code

```java
import java.util.*;

class DataStream {
    private final int value;
    private final int k;
    private final Deque<Integer> window;
    private int matchCount;

    public DataStream(int value, int k) {
        this.value = value;
        this.k = k;
        this.window = new ArrayDeque<>();
        this.matchCount = 0;
    }

    public boolean consec(int num) {
        window.addLast(num);
        if (num == value) {
            matchCount++;
        }

        if (window.size() > k) {
            int removed = window.removeFirst();
            if (removed == value) {
                matchCount--;
            }
        }

        if (window.size() < k) {
            return false;
        }

        return matchCount == k;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Each call does only constant-time work:

- add to deque: `O(1)`
- maybe remove from deque: `O(1)`
- update counter: `O(1)`

So:

```text
O(1) per call
```

---

### Space Complexity

Deque stores at most `k` elements:

```text
O(k)
```

---

## Why This Is Better

Compared to brute force:

- we no longer re-check all `k` elements every time
- the result is derived from one maintained statistic: `matchCount`

This is already an optimal time-per-operation solution.

---

# Approach 3: Count Consecutive Suffix Length Only

## Key Insight

The problem is even simpler than a full sliding window.

We do **not** actually need the last `k` elements explicitly.

Why?

Because the condition

> “last `k` integers are all equal to `value`”

is equivalent to

> “the current suffix of consecutive `value`s has length at least `k`”

That means we only need to track:

- `streak = number of consecutive trailing elements equal to value`

When a new number arrives:

- if `num == value`, increment `streak`
- otherwise reset `streak = 0`

Then return:

```text
streak >= k
```

This is the cleanest solution.

---

## Why This Works

Suppose the most recent elements are:

```text
..., value, value, value, value
```

If the suffix length of `value` is at least `k`, then the last `k` elements are all `value`.

If the suffix length is less than `k`, then somewhere inside the last `k` elements there must be a number different from `value`.

So tracking the suffix length is enough.

---

## Java Code

```java
class DataStream {
    private final int value;
    private final int k;
    private int streak;

    public DataStream(int value, int k) {
        this.value = value;
        this.k = k;
        this.streak = 0;
    }

    public boolean consec(int num) {
        if (num == value) {
            streak++;
        } else {
            streak = 0;
        }

        return streak >= k;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Each `consec(num)` call does only a few constant-time operations:

```text
O(1)
```

---

### Space Complexity

We store only three integers:

```text
O(1)
```

---

## This Is the Best Approach

This is the most elegant and most efficient solution:

- no queue
- no sliding window structure
- no repeated scan
- no extra memory proportional to `k`

It uses the strongest possible simplification of the problem.

---

# Dry Run

Let:

```text
value = 4
k = 3
```

Now process the stream:

---

### Call 1: `consec(4)`

- `num == value`
- `streak = 1`

Check:

```text
1 >= 3 ? false
```

Return `false`

---

### Call 2: `consec(4)`

- `num == value`
- `streak = 2`

Check:

```text
2 >= 3 ? false
```

Return `false`

---

### Call 3: `consec(4)`

- `num == value`
- `streak = 3`

Check:

```text
3 >= 3 ? true
```

Return `true`

---

### Call 4: `consec(3)`

- `num != value`
- `streak = 0`

Check:

```text
0 >= 3 ? false
```

Return `false`

---

# Correctness Explanation

We now justify the optimal streak-based solution carefully.

---

## Claim

After each call to `consec(num)`, `streak` equals the number of consecutive trailing elements in the stream that are equal to `value`.

---

## Proof

We use induction over the stream updates.

### Base Case

Before any elements are processed:

```text
streak = 0
```

This is correct, because the stream is empty and has no trailing `value`s.

---

### Inductive Step

Assume before processing the next number, `streak` correctly stores the number of consecutive trailing `value`s.

Now process new `num`.

#### Case 1: `num == value`

Then the trailing suffix of `value`s extends by exactly 1.
So the new correct value should be:

```text
old streak + 1
```

And the algorithm does exactly that.

#### Case 2: `num != value`

Then the trailing suffix of `value`s is broken immediately, so the number of consecutive trailing `value`s becomes:

```text
0
```

And the algorithm sets `streak = 0`.

So in both cases, the invariant remains true.

Thus the claim holds for all updates.

---

## Final Result

The last `k` elements are all equal to `value` **iff** the number of consecutive trailing `value`s is at least `k`.

Since `streak` is exactly that quantity, returning:

```java
streak >= k
```

is correct.

---

# Comparison of Approaches

| Approach | Idea                                   | Time per `consec` |  Space | Notes                        |
| -------- | -------------------------------------- | ----------------: | -----: | ---------------------------- |
| 1        | Keep last `k` elements and scan them   |            `O(k)` | `O(k)` | Simple but inefficient       |
| 2        | Keep last `k` elements + count matches |            `O(1)` | `O(k)` | Good sliding-window solution |
| 3        | Keep only trailing streak length       |            `O(1)` | `O(1)` | Best solution                |

---

# Interview Discussion

A strong interview progression would be:

### Step 1

State the brute-force idea clearly.

That shows correctness thinking.

### Step 2

Optimize by observing repeated work in rescanning the window.

That leads to the sliding-window counter approach.

### Step 3

Question whether the entire last-`k` window is even necessary.

That leads to the strongest observation:

> We only care about how many consecutive `value`s are currently at the end.

This is usually the intended solution.

---

# Common Mistakes

## Mistake 1: Returning true once total occurrences of `value` reach `k`

That is wrong.

Example:

```text
value = 4, k = 3
stream = [4, 1, 4, 4]
```

There are three 4s overall, but the last 3 elements are:

```text
[1, 4, 4]
```

So the answer is `false`.

We need **last `k` elements**, not total count.

---

## Mistake 2: Forgetting to reset after a mismatch

If `num != value`, the streak must become zero immediately.

Example:

```text
value = 5, k = 2
stream = [5, 5, 3, 5]
```

The last two are not both 5, so after the 3 arrives the streak must reset.

---

## Mistake 3: Using a queue when not needed

A queue works, but it is overkill for this problem once you realize only the suffix matters.

---

# Final Recommended Java Solution

```java
class DataStream {
    private final int value;
    private final int k;
    private int streak;

    public DataStream(int value, int k) {
        this.value = value;
        this.k = k;
        this.streak = 0;
    }

    public boolean consec(int num) {
        if (num == value) {
            streak++;
        } else {
            streak = 0;
        }
        return streak >= k;
    }
}
```

---

# Final Summary

The problem looks like a sliding-window problem, but it simplifies even further.

We do **not** need to remember the whole last `k` elements.

We only need to know:

- how many consecutive trailing elements equal `value`

Maintain a single integer `streak`:

- increment it if `num == value`
- reset it to `0` otherwise

Then:

```java
return streak >= k;
```

This gives:

- **Time:** `O(1)` per operation
- **Space:** `O(1)`

This is the cleanest and optimal solution.
