# 1656. Design an Ordered Stream — Exhaustive Java Notes

## Problem Statement

There is a stream of `n` `(idKey, value)` pairs arriving in arbitrary order.

- `idKey` is an integer in the range `1..n`
- `value` is a string
- No two pairs have the same `idKey`

We need to design a stream that returns values in increasing order of their IDs by returning a **chunk** after each insertion.

The concatenation of all returned chunks must equal the full list of values sorted by `idKey`.

---

## Class to Implement

```java
class OrderedStream {

    public OrderedStream(int n) {

    }

    public List<String> insert(int idKey, String value) {

    }
}
```

---

## Example

### Input

```text
["OrderedStream", "insert", "insert", "insert", "insert", "insert"]
[[5], [3, "ccccc"], [1, "aaaaa"], [2, "bbbbb"], [5, "eeeee"], [4, "ddddd"]]
```

### Output

```text
[null, [], ["aaaaa"], ["bbbbb", "ccccc"], [], ["ddddd", "eeeee"]]
```

### Explanation

```java
OrderedStream os = new OrderedStream(5);

os.insert(3, "ccccc"); // []
os.insert(1, "aaaaa"); // ["aaaaa"]
os.insert(2, "bbbbb"); // ["bbbbb", "ccccc"]
os.insert(5, "eeeee"); // []
os.insert(4, "ddddd"); // ["ddddd", "eeeee"]
```

Sorted order by id:

```text
["aaaaa", "bbbbb", "ccccc", "ddddd", "eeeee"]
```

---

## Constraints

```text
1 <= n <= 1000
1 <= idKey <= n
value.length == 5
value consists only of lowercase letters
Each call to insert has a unique id
Exactly n calls will be made to insert
```

---

# 1. Core Insight

The stream is not asking us to return **all inserted values so far**.

It is asking us to return the **largest consecutive chunk starting from the current pointer**.

That means we only care about:

- where the next expected index is
- whether that position is already filled
- how far we can continue consecutively

So this is really a **pointer + storage** problem.

---

# 2. What Is the Pointer Doing?

Suppose `ptr` is the smallest id that has not yet been returned.

When we insert `(idKey, value)`:

- store `value` at position `idKey`
- if `idKey != ptr`, we cannot return anything yet
- if `idKey == ptr`, we may now be able to return a chunk:
  - return `stream[ptr]`
  - then `stream[ptr + 1]`
  - then `stream[ptr + 2]`
  - keep going while values exist

So the whole problem reduces to:

> Maintain an array indexed by `idKey`, and advance a pointer whenever consecutive values are available.

---

# 3. Why an Array Is the Natural Fit

Because:

- ids are from `1` to `n`
- each id appears exactly once
- we need direct access by id

This is exactly what arrays are good at.

Using an array gives:

- `O(1)` storage at `idKey`
- `O(1)` lookup at current pointer
- simple implementation

---

# 4. Approach 1 — Direct Array + Pointer

## Intuition

Store every value at its id position.

Keep a pointer `ptr` that always points to the first not-yet-returned id.

Whenever we insert a new value:

1. place it into the array
2. if it is not at `ptr`, return empty list
3. if it is at `ptr`, keep collecting consecutive values until a missing one is found

This is the cleanest and intended solution.

---

## Dry Run

Let:

```text
n = 5
```

Initially:

```text
stream = [null, null, null, null, null, null]   // ignore index 0
ptr = 1
```

### Insert `(3, "ccccc")`

```text
stream[3] = "ccccc"
ptr = 1
```

`stream[1]` is still missing, so return:

```text
[]
```

---

### Insert `(1, "aaaaa")`

```text
stream[1] = "aaaaa"
ptr = 1
```

Now `stream[1]` exists, so collect:

- `"aaaaa"`

Move pointer:

```text
ptr = 2
```

`stream[2]` is missing, stop.

Return:

```text
["aaaaa"]
```

---

### Insert `(2, "bbbbb")`

```text
stream[2] = "bbbbb"
ptr = 2
```

Now collect:

- `"bbbbb"`
- `"ccccc"`

Move pointer to `4`.

Return:

```text
["bbbbb", "ccccc"]
```

---

# 5. Java Implementation — Approach 1

```java
import java.util.*;

class OrderedStream {
    private String[] stream;
    private int ptr;

    public OrderedStream(int n) {
        stream = new String[n + 1]; // 1-based indexing
        ptr = 1;
    }

    public List<String> insert(int idKey, String value) {
        stream[idKey] = value;

        List<String> result = new ArrayList<>();
        while (ptr < stream.length && stream[ptr] != null) {
            result.add(stream[ptr]);
            ptr++;
        }
        return result;
    }
}
```

---

## Complexity

### Time Complexity

### Constructor

```text
O(n)
```

because we allocate an array of size `n + 1`.

### insert

At first glance, the `while` loop looks like it could be expensive.

But each position is processed only once, when the pointer passes it.

So across all `n` insertions, the pointer moves at most `n` times total.

Therefore:

- worst-case for one call: `O(n)`
- amortized per call: `O(1)`
- total across all calls: `O(n)`

### Final

```text
Amortized O(1) per insert
Total O(n)
```

### Space Complexity

```text
O(n)
```

for the array.

---

# 6. Correctness Argument

We should not just trust the code. Let us verify the invariant.

## Invariant

Before every `insert` call:

- all ids `< ptr` have already been returned
- `ptr` is the smallest id not yet returned

When we insert `(idKey, value)`:

- we store it in `stream[idKey]`
- then we keep returning values starting from `ptr` while they exist

This ensures:

- no id smaller than `ptr` is returned again
- every available consecutive value starting at `ptr` is returned
- we stop exactly at the first missing id

So after the loop, the invariant still holds.

Hence the algorithm is correct.

---

# 7. Approach 2 — HashMap + Pointer

## Intuition

Instead of an array, we can use a `HashMap<Integer, String>`.

This is more general and works even if ids were sparse or much larger.

For this exact problem, though, it is not as natural as an array because ids are already dense in `1..n`.

Still, it is a valid alternative.

---

## Java Implementation — Approach 2

```java
import java.util.*;

class OrderedStream {
    private Map<Integer, String> map;
    private int ptr;

    public OrderedStream(int n) {
        map = new HashMap<>();
        ptr = 1;
    }

    public List<String> insert(int idKey, String value) {
        map.put(idKey, value);

        List<String> result = new ArrayList<>();
        while (map.containsKey(ptr)) {
            result.add(map.get(ptr));
            ptr++;
        }
        return result;
    }
}
```

---

## Complexity

### Time Complexity

- `put`: average `O(1)`
- `containsKey`: average `O(1)`
- pointer advances at most `n` times total

So:

```text
Amortized O(1) per insert
Total O(n)
```

### Space Complexity

```text
O(n)
```

---

## Pros and Cons

### Pros

- more flexible if ids are sparse
- easy to reason about

### Cons

- extra hashing overhead
- less efficient than array for dense integer ids
- not as clean for this problem

---

# 8. Approach 3 — TreeMap / Ordered Map

## Intuition

We can also store inserted values in a `TreeMap<Integer, String>` and try to consume consecutive ids from the current pointer.

This works, but it is overkill.

A `TreeMap` maintains keys in sorted order, which is useful when the key space is arbitrary and we need ordered traversal.

Here, we already know the exact next key we are waiting for: `ptr`.

So ordering is not really the bottleneck.

---

## Java Implementation — Approach 3

```java
import java.util.*;

class OrderedStream {
    private TreeMap<Integer, String> map;
    private int ptr;

    public OrderedStream(int n) {
        map = new TreeMap<>();
        ptr = 1;
    }

    public List<String> insert(int idKey, String value) {
        map.put(idKey, value);

        List<String> result = new ArrayList<>();
        while (map.containsKey(ptr)) {
            result.add(map.get(ptr));
            ptr++;
        }
        return result;
    }
}
```

---

## Complexity

`TreeMap` operations are `O(log n)`.

So:

- `put`: `O(log n)`
- `containsKey`: `O(log n)`
- `get`: `O(log n)`

Across all insertions, pointer still moves at most `n` times.

### Final

```text
O(log n) per insert, plus chunk extraction
Overall O(n log n)
```

### Space Complexity

```text
O(n)
```

---

## Why This Is Not Ideal

A skeptical interviewer would ask:

> Why are you paying `log n` for ordered storage when ids are fixed in `1..n`?

That is the right question.

For this problem, array beats `TreeMap`.

---

# 9. Best Approach

The best practical solution is:

## Array + Pointer

Because it is:

- simplest
- fastest
- easiest to prove correct
- perfectly matched to the constraints

---

# 10. Detailed Example Walkthrough

Let us walk through the full example more mechanically.

## Initial State

```text
n = 5
stream = [null, null, null, null, null, null]
ptr = 1
```

---

## Operation 1

```java
insert(3, "ccccc")
```

State:

```text
stream = [null, null, null, "ccccc", null, null]
ptr = 1
```

Since `stream[1] == null`, return:

```text
[]
```

---

## Operation 2

```java
insert(1, "aaaaa")
```

State:

```text
stream = [null, "aaaaa", null, "ccccc", null, null]
ptr = 1
```

Now:

- `stream[1] = "aaaaa"` → add it
- `ptr = 2`
- `stream[2] == null` → stop

Return:

```text
["aaaaa"]
```

---

## Operation 3

```java
insert(2, "bbbbb")
```

State:

```text
stream = [null, "aaaaa", "bbbbb", "ccccc", null, null]
ptr = 2
```

Now:

- `stream[2] = "bbbbb"` → add
- `ptr = 3`
- `stream[3] = "ccccc"` → add
- `ptr = 4`
- `stream[4] == null` → stop

Return:

```text
["bbbbb", "ccccc"]
```

---

## Operation 4

```java
insert(5, "eeeee")
```

State:

```text
stream = [null, "aaaaa", "bbbbb", "ccccc", null, "eeeee"]
ptr = 4
```

`stream[4] == null`, so return:

```text
[]
```

---

## Operation 5

```java
insert(4, "ddddd")
```

State:

```text
stream = [null, "aaaaa", "bbbbb", "ccccc", "ddddd", "eeeee"]
ptr = 4
```

Now collect:

- `"ddddd"`
- `"eeeee"`

Return:

```text
["ddddd", "eeeee"]
```

Done.

---

# 11. Common Mistakes

## Mistake 1: Using 0-based indexing carelessly

The ids start from `1`, not `0`.

If you use a 0-based array, you must consistently translate:

```text
index = idKey - 1
```

A lot of bugs come from mixing these two systems.

Using an array of size `n + 1` and ignoring index `0` is often cleaner.

---

## Mistake 2: Returning only the inserted value

Suppose we insert at the pointer.

We must return **the full consecutive chunk**, not just that one value.

For example:

```java
insert(2, "bbbbb")
```

may need to return:

```text
["bbbbb", "ccccc"]
```

not merely `["bbbbb"]`.

---

## Mistake 3: Moving the pointer only once

The pointer must advance until the next missing id.

Not just one step.

Wrong idea:

```java
if (stream[ptr] != null) {
    result.add(stream[ptr]);
    ptr++;
}
```

Correct idea:

```java
while (ptr < stream.length && stream[ptr] != null) {
    result.add(stream[ptr]);
    ptr++;
}
```

---

## Mistake 4: Sorting after every insertion

Some might try:

- store all inserted pairs
- sort by id on each insert
- return the next chunk

This is unnecessarily expensive.

The problem has strong structure: ids are unique and bounded.

Use that structure.

---

# 12. Interview Explanation Version

If you need to explain this quickly in an interview:

> Since ids are from 1 to n and each id appears once, I can store values directly in an array indexed by id.
> I keep a pointer `ptr` to the smallest id not yet returned.
> On each insert, I place the value into the array. Then while `stream[ptr]` is filled, I append it to the answer and increment `ptr`.
> This works because each element is consumed exactly once, so total work across all operations is linear.

That is usually the ideal explanation.

---

# 13. Optimized Final Java Solution

```java
import java.util.*;

class OrderedStream {
    private final String[] stream;
    private int ptr;

    public OrderedStream(int n) {
        this.stream = new String[n + 1];
        this.ptr = 1;
    }

    public List<String> insert(int idKey, String value) {
        stream[idKey] = value;

        List<String> ans = new ArrayList<>();
        while (ptr < stream.length && stream[ptr] != null) {
            ans.add(stream[ptr]);
            ptr++;
        }
        return ans;
    }
}
```

---

# 14. Complexity Summary

## Best Solution: Array + Pointer

### Time

- Constructor: `O(n)`
- `insert`: amortized `O(1)`
- Total across all inserts: `O(n)`

### Space

```text
O(n)
```

---

# 15. Comparison of Approaches

| Approach          | Data Structure             |      Insert Cost |   Total Cost |  Space | Notes                             |
| ----------------- | -------------------------- | ---------------: | -----------: | -----: | --------------------------------- |
| Array + Pointer   | `String[]`                 | Amortized `O(1)` |       `O(n)` | `O(n)` | Best choice                       |
| HashMap + Pointer | `HashMap<Integer, String>` | Amortized `O(1)` |       `O(n)` | `O(n)` | More general, less efficient here |
| TreeMap + Pointer | `TreeMap<Integer, String>` |       `O(log n)` | `O(n log n)` | `O(n)` | Overkill                          |

---

# 16. Final Takeaway

This is a classic example of using the constraints properly.

Because:

- ids are unique
- ids are dense
- ids lie in `1..n`

the smartest solution is not a fancy data structure.

It is simply:

- direct array storage
- one moving pointer

That gives the cleanest code and the strongest performance.

---

# 17. One More Variant: 0-Based Array Version

Some people prefer 0-based indexing.

That version looks like this:

```java
import java.util.*;

class OrderedStream {
    private final String[] stream;
    private int ptr;

    public OrderedStream(int n) {
        stream = new String[n];
        ptr = 0;
    }

    public List<String> insert(int idKey, String value) {
        stream[idKey - 1] = value;

        List<String> ans = new ArrayList<>();
        while (ptr < stream.length && stream[ptr] != null) {
            ans.add(stream[ptr]);
            ptr++;
        }
        return ans;
    }
}
```

This is equally correct.

The only difference is:

```text
array index = idKey - 1
```

Use whichever indexing style you can keep bug-free.

---

# 18. Final Answer

For this problem, the recommended Java solution is the **array + pointer** approach.

It is:

- correct
- minimal
- optimal for the given constraints
- easy to implement in an interview
