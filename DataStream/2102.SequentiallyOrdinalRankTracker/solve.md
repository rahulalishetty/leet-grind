# 2102. Sequentially Ordinal Rank Tracker — Exhaustive Java Notes

## Problem Statement

A scenic location is represented by:

- `name`: a unique string
- `score`: an integer attractiveness score

Ranking rule:

1. Higher `score` is better.
2. If scores are equal, lexicographically smaller `name` is better.

You need to design a data structure that supports:

- `add(name, score)`: insert a new location
- `get()`: return the **ith best** location, where `i` is the number of times `get()` has been called so far

So:

- 1st `get()` returns the 1st best location
- 2nd `get()` returns the 2nd best location
- 3rd `get()` returns the 3rd best location
- and so on

---

## Example

### Input

```text
["SORTracker", "add", "add", "get", "add", "get", "add", "get", "add", "get", "add", "get", "get"]
[[], ["bradford", 2], ["branford", 3], [], ["alps", 2], [], ["orland", 2], [], ["orlando", 3], [], ["alpine", 2], [], []]
```

### Output

```text
[null, null, null, "branford", null, "alps", null, "bradford", null, "bradford", null, "bradford", "orland"]
```

---

## Constraints

- `name` consists of lowercase English letters
- `name` is unique among all locations
- `1 <= name.length <= 10`
- `1 <= score <= 10^5`
- number of `get()` calls never exceeds number of `add()` calls
- at most `4 * 10^4` total calls

---

# 1. Core Observation

The tricky part is not sorting once.

The tricky part is this rule:

> on the kth call to `get()`, return the kth best item among all items added so far.

That means `get()` is not asking for the current best every time.

It is asking for an ever-advancing rank:

- first best
- second best
- third best
- ...

So the data structure must remember how many answers have already been consumed.

---

# 2. Ranking Comparator

We will repeatedly use the same ranking rule.

For two locations `a` and `b`:

- `a` is better than `b` if `a.score > b.score`
- if scores tie, `a` is better than `b` if `a.name` is lexicographically smaller

In Java comparator form:

```java
(a, b) -> {
    if (a.score != b.score) return b.score - a.score; // higher score first
    return a.name.compareTo(b.name);                  // smaller name first
}
```

This defines the global order from best to worst.

---

# 3. Approach 1 — Store Everything and Sort on Every `get()`

## Intuition

The most direct idea is:

- keep all inserted locations in a list
- when `get()` is called:
  - sort the whole list
  - return the element at index `getCount`

This is correct, but inefficient.

It is still a good baseline because it makes the problem structure obvious.

---

## Java Code

```java
import java.util.*;

class SORTrackerBruteForce {
    static class Location {
        String name;
        int score;

        Location(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    private final List<Location> list;
    private int getCount;

    public SORTrackerBruteForce() {
        list = new ArrayList<>();
        getCount = 0;
    }

    public void add(String name, int score) {
        list.add(new Location(name, score));
    }

    public String get() {
        list.sort((a, b) -> {
            if (a.score != b.score) return Integer.compare(b.score, a.score);
            return a.name.compareTo(b.name);
        });
        return list.get(getCount++).name;
    }
}
```

---

## Complexity

Let `n` be the current number of inserted locations.

### `add`

- `O(1)`

### `get`

- sorting costs `O(n log n)`

### Total

Too slow if we do this many times.

---

## Why this is bad

If there are many `get()` calls, we keep re-sorting nearly the same collection again and again.

That repeated work is unnecessary.

---

# 4. Approach 2 — Maintain a Sorted List with Binary Insertion

## Intuition

Instead of sorting everything every time, keep the list always sorted.

Then:

- `add(name, score)`:
  - find correct insertion point
  - insert there
- `get()`:
  - return `list[getCount]`
  - increment `getCount`

This avoids repeated sorting, but list insertion in the middle is still expensive.

---

## Key Idea

Because the list is always kept in best-to-worst order:

- 1st `get()` returns `list[0]`
- 2nd `get()` returns `list[1]`
- 3rd `get()` returns `list[2]`

So `get()` becomes trivial.

The cost shifts into `add()`.

---

## Java Code

```java
import java.util.*;

class SORTrackerSortedList {
    static class Location {
        String name;
        int score;

        Location(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    private final List<Location> sorted;
    private int getCount;

    public SORTrackerSortedList() {
        sorted = new ArrayList<>();
        getCount = 0;
    }

    public void add(String name, int score) {
        Location cur = new Location(name, score);

        int left = 0, right = sorted.size();
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (better(cur, sorted.get(mid))) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        sorted.add(left, cur);
    }

    public String get() {
        return sorted.get(getCount++).name;
    }

    private boolean better(Location a, Location b) {
        if (a.score != b.score) return a.score > b.score;
        return a.name.compareTo(b.name) < 0;
    }
}
```

---

## Complexity

### `add`

- binary search: `O(log n)`
- array insertion shift: `O(n)`
- total: `O(n)`

### `get`

- `O(1)`

---

## Pros

- Simple
- Easy to reason about
- `get()` is extremely fast

## Cons

- Insertions are still too expensive for `4 * 10^4` operations in the worst case

---

# 5. Approach 3 — TreeSet + Iterator Position? Why It Is Awkward

A natural thought is:

- keep all locations in a `TreeSet` ordered from best to worst
- somehow remember where the kth answer is
- advance on each `get()`

This sounds attractive, but it is awkward in Java because:

- `TreeSet` does not support indexing
- moving to the kth element requires iteration
- maintaining a stable iterator while also inserting new better elements is messy

So while a balanced BST helps maintain sorted order, it does not directly solve the advancing-rank query efficiently.

This is why the standard elegant solution uses **two heaps**.

---

# 6. Approach 4 — Two Heaps (Optimal)

This is the standard and best solution.

---

## 6.1 Main Insight

Suppose we have already answered `k - 1` calls to `get()`.

Then on the next `get()`, we need the **kth best** location.

So at any moment, it is useful to partition all inserted locations into two groups:

- a group containing the current top `k` candidates
- the rest

More specifically, after `k` calls to `get()`, we want a structure where:

- one heap stores the best `k` locations
- the boundary element among them is exactly the answer to the kth `get()`

The trick is to maintain that boundary incrementally.

---

## 6.2 Heap Roles

We use two heaps:

### `left`

Stores the best locations that have already been exposed up to the current query rank.

Its top should be the **worst among the exposed group**.

That is important because when a better item arrives, it may need to enter the exposed group and push the previous worst exposed item out.

So `left` is a **min-style boundary heap** with respect to the ranking.

### `right`

Stores the remaining locations.

Its top should be the **best among the unexposed group**.

When `get()` is called, we move the best item from `right` into `left`.

Then the top of `left` becomes the current answer.

---

## 6.3 Comparator Trick

Global ranking is:

- higher score is better
- smaller name is better

For `right`, we want the **best** element on top.

So comparator for `right` is:

```java
(a, b) -> {
    if (a.score != b.score) return Integer.compare(b.score, a.score);
    return a.name.compareTo(b.name);
}
```

For `left`, we want the **worst among the best-so-far** on top.

So comparator for `left` is the reverse:

```java
(a, b) -> {
    if (a.score != b.score) return Integer.compare(a.score, b.score);
    return b.name.compareTo(a.name);
}
```

This makes `left.peek()` exactly the current kth-best answer after enough balancing.

---

# 7. How the Two-Heap Process Works

We maintain this invariant:

- `left` contains exactly as many elements as the number of `get()` calls already made
- `left` contains the best such elements
- `left.peek()` is the worst of them, i.e. the last returned rank
- `right` contains all remaining elements, with its best candidate on top

### On `add(name, score)`

A new location can belong either to the exposed side or the unexposed side.

A clean way is:

1. put it into `left`
2. move the worst from `left` into `right`

Why does that work?

Because `left` is supposed to keep only the currently exposed count worth of best items.
So after inserting one extra item, ejecting the worst restores the invariant.

### On `get()`

We need to expose one more best location.

So:

1. take the best from `right`
2. move it into `left`
3. now `left.peek()` is the answer

This is the elegant heart of the solution.

---

# 8. Dry Run

Consider:

```text
add("bradford", 2)
add("branford", 3)
get()
```

After two adds:

- the best unseen locations are in `right`
- `left` is empty because no `get()` has happened yet

When `get()` happens:

- move best from `right` → `left`
- that is `"branford"`
- `left.peek()` = `"branford"`

Now:

```text
add("alps", 2)
get()
```

After adding `"alps"`:

- insert into `left`, then move worst from `left` to `right`
- this keeps the already exposed rank count valid

On next `get()`:

- move best from `right` to `left`
- now exposed set contains the best 2 elements
- worst among those 2 is the 2nd best overall
- answer becomes `"alps"`

That matches the example.

---

# 9. Optimal Java Solution

```java
import java.util.*;

class SORTracker {
    static class Location {
        String name;
        int score;

        Location(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    // left: worst among the exposed/best-so-far group is on top
    private final PriorityQueue<Location> left;

    // right: best among the remaining group is on top
    private final PriorityQueue<Location> right;

    public SORTracker() {
        left = new PriorityQueue<>((a, b) -> {
            if (a.score != b.score) return Integer.compare(a.score, b.score);
            return b.name.compareTo(a.name);
        });

        right = new PriorityQueue<>((a, b) -> {
            if (a.score != b.score) return Integer.compare(b.score, a.score);
            return a.name.compareTo(b.name);
        });
    }

    public void add(String name, int score) {
        Location loc = new Location(name, score);

        left.offer(loc);
        right.offer(left.poll());
    }

    public String get() {
        left.offer(right.poll());
        return left.peek().name;
    }
}
```

---

# 10. Why This Works

Let the number of `get()` calls already made be `k`.

We maintain:

- `left` contains exactly the best `k` locations
- `right` contains the rest
- `left.peek()` is the kth best location because it is the worst inside the best-`k` set

### Why `add()` is correct

When a new location arrives, it might belong to the best-`k` set or not.

We temporarily insert it into `left`, which now has `k + 1` elements.
Then we remove the worst element from `left` and push it to `right`.

So after that:

- `left` again has size `k`
- and it contains the best `k` elements

### Why `get()` is correct

Before the next `get()`, `left` contains the best `k` elements.
We want the best `k + 1` elements.

So we take the best element from `right` and move it into `left`.
Now `left` contains the best `k + 1` elements.

Its worst element is exactly the `(k + 1)`th best location, which is what this `get()` must return.

---

# 11. Correctness Proof

## Invariant

After processing all operations so far, if `g` is the number of completed `get()` calls, then:

1. `left` contains exactly the best `g` locations.
2. `right` contains all remaining locations.
3. `left.peek()` is the `g`th best location when `g > 0`.

We prove this by induction.

### Base Case

Initially:

- no locations
- no `get()` calls
- `left` and `right` are empty

The invariant holds trivially.

### After `add(name, score)`

Suppose before the add, `left` contains the best `g` locations.

We insert the new location into `left`, then remove the worst from `left` and push it into `right`.

So `left` again contains exactly `g` locations, and those are the best `g` among all locations seen so far.

Thus the invariant is preserved.

### After `get()`

Suppose before the call, `left` contains the best `g` locations, and `right` contains the rest.

The best remaining location is at `right.peek()`.
Moving it into `left` makes `left` contain the best `g + 1` locations.

The worst inside that set is the `(g + 1)`th best overall, so `left.peek()` is exactly the correct answer.

Thus the invariant is preserved.

Therefore the algorithm is correct.

∎

---

# 12. Complexity Analysis

Let `n` be the number of inserted locations so far.

### `add`

- one heap insertion into `left`
- one heap removal from `left`
- one heap insertion into `right`

Total:

```text
O(log n)
```

### `get`

- one heap removal from `right`
- one heap insertion into `left`

Total:

```text
O(log n)
```

### Space

All locations are stored in the two heaps:

```text
O(n)
```

This easily fits the constraints.

---

# 13. A Slightly Different but Equivalent Heap Formulation

Some people write the solution with the opposite interpretation:

- `left` as max-heap of exposed items
- `right` as min-heap of unexposed items

That also works, but the balancing logic becomes a bit more confusing.

The version in this note is usually the cleanest:

- `left.peek()` is always the current answer after `get()`
- `right.peek()` is the next candidate to expose

That makes the reasoning easier.

---

# 14. Common Mistakes

## Mistake 1: Returning the best every time

That would solve a different problem.

This problem asks for:

- 1st best
- then 2nd best
- then 3rd best
- ...

not the current best repeatedly.

---

## Mistake 2: Using only one heap

A single heap can give you the current best efficiently,
but it cannot conveniently support this advancing-rank behavior while keeping future candidates ready.

---

## Mistake 3: Using the wrong comparator tie-break

When scores are equal:

- lexicographically **smaller** name is better

That means:

```text
"alps" is better than "bradford"
```

because `"alps".compareTo("bradford") < 0`.

---

## Mistake 4: Mixing up heap roles

In the optimal solution:

- `left` must expose the boundary answer
- `right` must expose the next best unseen candidate

If those roles are reversed, the balancing logic breaks.

---

# 15. Comparison of Approaches

| Approach                     | Idea                              |      `add` |        `get` |  Space | Notes                                 |
| ---------------------------- | --------------------------------- | ---------: | -----------: | -----: | ------------------------------------- |
| Brute force sort every `get` | Keep unsorted list, sort on query |     `O(1)` | `O(n log n)` | `O(n)` | Correct but slow                      |
| Sorted list                  | Keep array always sorted          |     `O(n)` |       `O(1)` | `O(n)` | Simpler, but insertion expensive      |
| TreeSet-ish thinking         | Ordered set with moving rank      |    awkward |      awkward | `O(n)` | Hard to implement efficiently in Java |
| Two heaps                    | Maintain exposed set + unseen set | `O(log n)` |   `O(log n)` | `O(n)` | Best solution                         |

---

# 16. Interview-Style Intuition Summary

The key mental shift is this:

`get()` is not asking for the maximum.
It is asking for the next rank in sorted order.

So we should maintain a boundary between:

- locations already exposed by previous `get()` calls
- locations not yet exposed

The best not-yet-exposed location should be easy to fetch.
The worst already-exposed location should also be easy to fetch, because that is the current answer.

That naturally suggests two heaps:

- `right`: best unseen location on top
- `left`: worst seen location on top

Then:

- `add()` inserts and restores the boundary
- `get()` moves one more item across the boundary

That gives an elegant `O(log n)` solution.

---

# 17. Final Recommended Java Solution

```java
import java.util.*;

class SORTracker {
    static class Location {
        String name;
        int score;

        Location(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    private final PriorityQueue<Location> left;
    private final PriorityQueue<Location> right;

    public SORTracker() {
        // Worst among exposed items on top.
        left = new PriorityQueue<>((a, b) -> {
            if (a.score != b.score) return Integer.compare(a.score, b.score);
            return b.name.compareTo(a.name);
        });

        // Best among unexposed items on top.
        right = new PriorityQueue<>((a, b) -> {
            if (a.score != b.score) return Integer.compare(b.score, a.score);
            return a.name.compareTo(b.name);
        });
    }

    public void add(String name, int score) {
        Location loc = new Location(name, score);
        left.offer(loc);
        right.offer(left.poll());
    }

    public String get() {
        left.offer(right.poll());
        return left.peek().name;
    }
}
```

---

# 18. Small Sanity Check

Suppose we add:

```text
("b", 2), ("a", 2), ("c", 3)
```

Sorted order is:

```text
("c", 3), ("a", 2), ("b", 2)
```

So `get()` answers should be:

1. `c`
2. `a`
3. `b`

The two-heap solution produces exactly this order.

---

# 19. Final Takeaway

This is a ranking data structure problem, not just a sorting problem.

The decisive idea is to maintain a split between:

- already-returnable best items
- future candidates

Once you see that, the two-heap solution becomes natural and gives:

```text
Time:  O(log n) per operation
Space: O(n)
```

That is the right solution for this problem.
