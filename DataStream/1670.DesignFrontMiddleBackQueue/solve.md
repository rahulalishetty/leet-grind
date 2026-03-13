# 1670. Design Front Middle Back Queue — Exhaustive Java Notes

## Problem Statement

Design a queue that supports insertion and deletion at:

- the **front**
- the **middle**
- the **back**

Implement:

```java
class FrontMiddleBackQueue {

    public FrontMiddleBackQueue() {

    }

    public void pushFront(int val) {

    }

    public void pushMiddle(int val) {

    }

    public void pushBack(int val) {

    }

    public int popFront() {

    }

    public int popMiddle() {

    }

    public int popBack() {

    }
}
```

### Important rule

When the queue length is even and there are **two middle choices**, we use the **frontmost middle**.

That rule affects both:

- `pushMiddle`
- `popMiddle`

---

## Example

```text
Input:
["FrontMiddleBackQueue", "pushFront", "pushBack", "pushMiddle", "pushMiddle", "popFront", "popMiddle", "popMiddle", "popBack", "popFront"]
[[], [1], [2], [3], [4], [], [], [], [], []]

Output:
[null, null, null, null, null, 1, 3, 4, 2, -1]
```

Explanation:

```text
q.pushFront(1);   // [1]
q.pushBack(2);    // [1, 2]
q.pushMiddle(3);  // [1, 3, 2]
q.pushMiddle(4);  // [1, 4, 3, 2]
q.popFront();     // returns 1, queue becomes [4, 3, 2]
q.popMiddle();    // returns 3, queue becomes [4, 2]
q.popMiddle();    // returns 4, queue becomes [2]
q.popBack();      // returns 2, queue becomes []
q.popFront();     // returns -1
```

---

# 1. Core Observations

This is not a normal queue.

We need all of these efficiently:

- insert at front
- insert at middle
- insert at back
- remove at front
- remove at middle
- remove at back

The tricky part is the **middle**.

A good way to think about this problem is to split the queue into **two halves**:

- `left`
- `right`

and maintain an invariant about their sizes.

---

# 2. What Should the Invariant Be?

We want middle operations to become local.

The best invariant is:

```text
left.size() == right.size()
or
left.size() == right.size() + 1
```

So `left` is either:

- the same size as `right`, or
- larger by exactly 1

That means:

```text
left.size() >= right.size()
left.size() - right.size() <= 1
```

This is the decisive design.

Why?

Because with this invariant:

- the **frontmost middle** is always the **last element of left**
- `pushMiddle` can be implemented near the end of `left`
- `popMiddle` can be implemented by removing from the end of `left`

That makes the problem much simpler.

---

# 3. Why Is the Middle at the End of `left`?

Let total size be `n`.

## Case 1: `n` is odd

Example:

```text
[1, 2, 3, 4, 5]
```

Split as:

```text
left  = [1, 2, 3]
right = [4, 5]
```

The middle is `3`, which is the last element of `left`.

## Case 2: `n` is even

Example:

```text
[1, 2, 3, 4]
```

The two middle candidates are `2` and `3`.

The problem wants the **frontmost** middle, which is `2`.

Split as:

```text
left  = [1, 2]
right = [3, 4]
```

Again, the frontmost middle is the last element of `left`.

So in both odd and even lengths:

```text
middle = last(left)
```

This is exactly why the invariant is so useful.

---

# 4. Approach 1 — Simple `ArrayList`

## Intuition

Since the total number of operations is only at most `1000`, a straightforward simulation is fully acceptable.

We can store the whole queue in an `ArrayList<Integer>` and directly use index-based insertion/removal.

### Middle index rule

Let size be `n`.

- for `pushMiddle`, insert at index `n / 2`
- for `popMiddle`, remove index `(n - 1) / 2`

Why?

Because:

- when `n` is even, `n / 2` inserts before the back-middle element, which creates the correct front-middle behavior
- when removing, `(n - 1) / 2` gives the frontmost middle

---

## Java Code

```java
import java.util.*;

class FrontMiddleBackQueue {
    private List<Integer> list;

    public FrontMiddleBackQueue() {
        list = new ArrayList<>();
    }

    public void pushFront(int val) {
        list.add(0, val);
    }

    public void pushMiddle(int val) {
        list.add(list.size() / 2, val);
    }

    public void pushBack(int val) {
        list.add(val);
    }

    public int popFront() {
        if (list.isEmpty()) {
            return -1;
        }
        return list.remove(0);
    }

    public int popMiddle() {
        if (list.isEmpty()) {
            return -1;
        }
        int mid = (list.size() - 1) / 2;
        return list.remove(mid);
    }

    public int popBack() {
        if (list.isEmpty()) {
            return -1;
        }
        return list.remove(list.size() - 1);
    }
}
```

---

## Complexity

Each insertion/removal in the middle or front of `ArrayList` may shift elements.

### Time

- `pushFront`: `O(n)`
- `pushMiddle`: `O(n)`
- `pushBack`: `O(1)` amortized
- `popFront`: `O(n)`
- `popMiddle`: `O(n)`
- `popBack`: `O(1)`

### Space

```text
O(n)
```

---

## Verdict

This is the easiest implementation.

Given the constraint of only `1000` operations, this solution is completely fine in practice.

But it is not the cleanest data-structure design answer.

---

# 5. Approach 2 — Two Deques (Optimal Design)

This is the best approach conceptually.

---

## Intuition

Maintain:

- `left`
- `right`

as two deques.

Interpret the whole queue as:

```text
queue = left followed by right
```

Maintain the invariant:

```text
left.size() == right.size()
or
left.size() == right.size() + 1
```

So:

- `left` is never smaller than `right`
- `left` is at most 1 larger than `right`

This ensures:

- front = front of `left` if queue not empty
- back = back of `right` if `right` non-empty, otherwise back of `left`
- middle = back of `left`

---

# 6. Rebalancing Logic

After every operation, we rebalance.

## If `left` is too large

If:

```text
left.size() > right.size() + 1
```

move one element from the end of `left` to the front of `right`.

## If `right` is too large

If:

```text
left.size() < right.size()
```

move one element from the front of `right` to the end of `left`.

That is all.

---

# 7. How Each Operation Works

## `pushFront(val)`

Insert at the front of the total queue.

So just do:

```text
left.addFirst(val)
```

Then rebalance.

---

## `pushBack(val)`

Insert at the back of the total queue.

So do:

```text
right.addLast(val)
```

Then rebalance.

If `right` is empty initially, this is still fine because rebalancing will fix the halves.

---

## `pushMiddle(val)`

This one is subtle.

We want insertion at the **frontmost middle position**.

### Case A: sizes equal

Example:

```text
left  = [1, 2]
right = [3, 4]
queue = [1, 2, 3, 4]
```

Middle insertion should produce:

```text
[1, 2, val, 3, 4]
```

That means `val` should become the new last element of `left`.

So when sizes are equal:

```text
left.addLast(val)
```

### Case B: `left` has one extra

Example:

```text
left  = [1, 2, 3]
right = [4, 5]
queue = [1, 2, 3, 4, 5]
```

Middle insertion should produce:

```text
[1, 2, val, 3, 4, 5]
```

So we must first move the old middle (`3`) from end of `left` to front of `right`, then place `val` at end of `left`.

Equivalent implementation:

```text
right.addFirst(left.removeLast());
left.addLast(val);
```

A cleaner way is:

- if `left` bigger than `right`, move one from `left` to `right`
- then add `val` to `left`

---

## `popFront()`

If queue empty, return `-1`.

Otherwise:

- if `left` non-empty, remove from front of `left`
- rebalance

Because by invariant, any existing queue starts in `left`.

---

## `popBack()`

If queue empty, return `-1`.

Otherwise:

- if `right` non-empty, remove from back of `right`
- else remove from back of `left`

Then rebalance.

---

## `popMiddle()`

If queue empty, return `-1`.

The middle is always:

```text
left.removeLast()
```

Then rebalance.

This is the nicest consequence of the invariant.

---

# 8. Optimal Java Code Using Two Deques

```java
import java.util.*;

class FrontMiddleBackQueue {
    private Deque<Integer> left;
    private Deque<Integer> right;

    public FrontMiddleBackQueue() {
        left = new ArrayDeque<>();
        right = new ArrayDeque<>();
    }

    private void rebalance() {
        if (left.size() > right.size() + 1) {
            right.addFirst(left.removeLast());
        } else if (left.size() < right.size()) {
            left.addLast(right.removeFirst());
        }
    }

    public void pushFront(int val) {
        left.addFirst(val);
        rebalance();
    }

    public void pushMiddle(int val) {
        if (left.size() > right.size()) {
            right.addFirst(left.removeLast());
        }
        left.addLast(val);
    }

    public void pushBack(int val) {
        right.addLast(val);
        rebalance();
    }

    public int popFront() {
        if (left.isEmpty() && right.isEmpty()) {
            return -1;
        }

        int ans;
        if (!left.isEmpty()) {
            ans = left.removeFirst();
        } else {
            ans = right.removeFirst();
        }

        rebalance();
        return ans;
    }

    public int popMiddle() {
        if (left.isEmpty() && right.isEmpty()) {
            return -1;
        }

        int ans = left.removeLast();
        rebalance();
        return ans;
    }

    public int popBack() {
        if (left.isEmpty() && right.isEmpty()) {
            return -1;
        }

        int ans;
        if (!right.isEmpty()) {
            ans = right.removeLast();
        } else {
            ans = left.removeLast();
        }

        rebalance();
        return ans;
    }
}
```

---

# 9. Why This Works

We maintain:

```text
left.size() == right.size()
or
left.size() == right.size() + 1
```

So `left` always contains the middle candidate.

Therefore:

- front is accessible
- back is accessible
- middle is accessible

All operations only touch deque ends, which are `O(1)`.

That is why this design is optimal.

---

# 10. Dry Run on Example 1

Start:

```text
left = []
right = []
```

## `pushFront(1)`

```text
left = [1]
right = []
queue = [1]
```

## `pushBack(2)`

Add to `right`:

```text
left = [1]
right = [2]
queue = [1, 2]
```

Balanced already.

## `pushMiddle(3)`

Sizes equal, so add to end of `left`:

```text
left = [1, 3]
right = [2]
queue = [1, 3, 2]
```

## `pushMiddle(4)`

Now `left` has one extra.

Move one from end of `left` to front of `right`:

```text
left = [1]
right = [3, 2]
```

Then add `4` to end of `left`:

```text
left = [1, 4]
right = [3, 2]
queue = [1, 4, 3, 2]
```

Correct.

## `popFront()`

Remove front of `left`:

```text
returns 1
left = [4]
right = [3, 2]
```

Rebalance because `left.size() < right.size()`:

Move front of `right` to end of `left`:

```text
left = [4, 3]
right = [2]
queue = [4, 3, 2]
```

## `popMiddle()`

Remove end of `left`:

```text
returns 3
left = [4]
right = [2]
queue = [4, 2]
```

## `popMiddle()`

Remove end of `left`:

```text
returns 4
left = []
right = [2]
```

Rebalance:

```text
left = [2]
right = []
queue = [2]
```

## `popBack()`

`right` empty, so remove end of `left`:

```text
returns 2
left = []
right = []
```

## `popFront()`

Empty:

```text
returns -1
```

Everything matches.

---

# 11. Correctness Proof

We now prove the two-deque solution is correct.

## Invariant

After every operation:

```text
left.size() == right.size()
or
left.size() == right.size() + 1
```

and the queue order is exactly:

```text
all elements of left, followed by all elements of right
```

### Initialization

Initially both deques are empty.

So the invariant holds.

---

## Lemma 1

At any time, the frontmost middle element of the queue is the last element of `left`.

### Proof

If total size is odd, `left` contains one more element than `right`, so the exact middle is the last element of `left`.

If total size is even, `left` and `right` have equal size. The two middle positions are the last element of `left` and the first element of `right`. The frontmost one is the last element of `left`.

So in all cases, the frontmost middle is the last element of `left`. ∎

---

## Lemma 2

`pushFront`, `pushBack`, and `pushMiddle` place the new value in the correct position.

### Proof

- `pushFront` adds to the front of `left`, which is the front of the full queue.
- `pushBack` adds to the back of `right`, which is the back of the full queue.
- `pushMiddle` either:
  - directly appends to `left` when sizes are equal, or
  - first shifts one old middle candidate from `left` to `right`, then appends to `left`.

In both cases, the new value becomes the frontmost valid middle insertion. ∎

---

## Lemma 3

`popFront`, `popBack`, and `popMiddle` remove the correct element.

### Proof

- `popFront` removes the first element of the full queue.
- `popBack` removes the last element of `right` if present, else the last element of `left`, which is the back of the full queue.
- By Lemma 1, `popMiddle` removes the last element of `left`, which is exactly the frontmost middle.

Thus each pop operation is correct. ∎

---

## Lemma 4

Rebalancing restores the invariant without changing queue order.

### Proof

If `left` is too large by more than 1, moving `left.removeLast()` to `right.addFirst()` preserves the concatenated order.

If `right` is larger, moving `right.removeFirst()` to `left.addLast()` also preserves the concatenated order.

Therefore the logical queue order remains unchanged while the size invariant is restored. ∎

---

## Theorem

The two-deque algorithm always returns correct results for every operation.

### Proof

By initialization the invariant holds. By Lemma 2 all push operations are correct. By Lemma 3 all pop operations are correct. By Lemma 4 rebalancing preserves queue order and restores the invariant after every operation. Therefore every operation behaves exactly as required. ∎

---

# 12. Complexity Analysis

## Approach 1: `ArrayList`

### Time

- `pushFront`: `O(n)`
- `pushMiddle`: `O(n)`
- `pushBack`: `O(1)` amortized
- `popFront`: `O(n)`
- `popMiddle`: `O(n)`
- `popBack`: `O(1)`

### Space

```text
O(n)
```

---

## Approach 2: Two Deques

Each operation touches only deque ends and at most one rebalance move.

### Time

All operations are:

```text
O(1)
```

### Space

```text
O(n)
```

---

# 13. Common Mistakes

## Mistake 1: Using the wrong middle index

For even length, the problem wants the **frontmost middle**.

For popping from a list of size `n`, use:

```java
(n - 1) / 2
```

not `n / 2`.

---

## Mistake 2: Using the wrong invariant

If you let `right` be larger than `left`, middle operations become awkward.

The best invariant is:

```text
left.size() >= right.size()
and
left.size() <= right.size() + 1
```

---

## Mistake 3: Forgetting to rebalance after front/back operations

Without rebalancing, middle operations stop working correctly.

---

## Mistake 4: Mishandling empty cases

Every pop method must return `-1` when the queue is empty.

---

# 14. Interview Discussion

If an interviewer asks for the simplest solution, start with:

- `ArrayList`
- direct insert/remove by index

Then point out that it is `O(n)` for middle operations.

After that, move to the optimal design:

- split queue into two deques
- keep `left` equal to `right` or larger by one
- the middle is always the back of `left`

That progression shows both:

- practical reasoning
- data-structure design ability

---

# 15. Final Recommended Java Solution

This is the version you should prefer in interviews and serious solutions.

```java
import java.util.*;

class FrontMiddleBackQueue {
    private Deque<Integer> left;
    private Deque<Integer> right;

    public FrontMiddleBackQueue() {
        left = new ArrayDeque<>();
        right = new ArrayDeque<>();
    }

    private void rebalance() {
        if (left.size() > right.size() + 1) {
            right.addFirst(left.removeLast());
        } else if (left.size() < right.size()) {
            left.addLast(right.removeFirst());
        }
    }

    public void pushFront(int val) {
        left.addFirst(val);
        rebalance();
    }

    public void pushMiddle(int val) {
        if (left.size() > right.size()) {
            right.addFirst(left.removeLast());
        }
        left.addLast(val);
    }

    public void pushBack(int val) {
        right.addLast(val);
        rebalance();
    }

    public int popFront() {
        if (left.isEmpty() && right.isEmpty()) {
            return -1;
        }

        int ans;
        if (!left.isEmpty()) {
            ans = left.removeFirst();
        } else {
            ans = right.removeFirst();
        }

        rebalance();
        return ans;
    }

    public int popMiddle() {
        if (left.isEmpty() && right.isEmpty()) {
            return -1;
        }

        int ans = left.removeLast();
        rebalance();
        return ans;
    }

    public int popBack() {
        if (left.isEmpty() && right.isEmpty()) {
            return -1;
        }

        int ans;
        if (!right.isEmpty()) {
            ans = right.removeLast();
        } else {
            ans = left.removeLast();
        }

        rebalance();
        return ans;
    }
}
```

---

# 16. Final Summary

The key to this problem is not “queue” thinking, but **balanced two-half structure** thinking.

The cleanest model is:

- split the queue into `left` and `right`
- maintain:

```text
left.size() == right.size()
or
left.size() == right.size() + 1
```

Then:

- front = front of `left`
- middle = back of `left`
- back = back of `right` if present, else back of `left`

This gives:

- `O(1)` for all operations
- simple reasoning
- clean implementation

That is the real idea behind the problem.
