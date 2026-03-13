# First Unique Number — Approaches

## Approach 1: Brute Force

### Intuition

The simplest solution for this problem is to create a queue and search through it to find the first unique number.

Algorithm:

1. Iterate through the queue starting from the oldest element.
2. For each number, count how many times it appears in the queue.
3. If the count equals 1, return that number.
4. If no number appears exactly once, return `-1`.

We can use built-in helpers:

- Java → `Collections.frequency()`
- Python → `.count()`

### Implementation

```java
class FirstUnique {

  private Queue<Integer> queue = new ArrayDeque<>();

  public FirstUnique(int[] nums) {
    for (int num : nums) {
      queue.add(num);
    }
  }

  public int showFirstUnique() {
    for (int num : queue) {
      int count = Collections.frequency(queue, num);
      if (count == 1) {
        return num;
      }
    }
    return -1;
  }

  public void add(int value) {
    queue.add(value);
  }
}
```

### Complexity Analysis

Let:

```
K = length of initial array
N = total numbers in queue
```

**Constructor:** `O(K)`
**add():** `O(1)`
**showFirstUnique():** `O(N²)`

Space complexity:

```
O(N)
```

---

# Approach 2: Queue + HashMap of Unique Status

## Intuition

The inefficiency in Approach 1 is repeated counting operations.

Instead, track whether a number is **unique** using a HashMap.

```
Map<number, isUnique>
```

Possible cases when `add(value)` is called:

1. Number never seen → mark as `true`, add to queue.
2. Number seen once → mark as `false`.
3. Number already non‑unique → ignore.

During `showFirstUnique()`:

Remove non‑unique numbers from the front of the queue.

### Implementation

```java
class FirstUnique {

  private Queue<Integer> queue = new ArrayDeque<>();
  private Map<Integer, Boolean> isUnique = new HashMap<>();

  public FirstUnique(int[] nums) {
    for (int num : nums) {
      this.add(num);
    }
  }

  public int showFirstUnique() {

    while (!queue.isEmpty() && !isUnique.get(queue.peek())) {
      queue.remove();
    }

    if (!queue.isEmpty()) {
      return queue.peek();
    }

    return -1;
  }

  public void add(int value) {

    if (!isUnique.containsKey(value)) {
      isUnique.put(value, true);
      queue.add(value);

    } else {
      isUnique.put(value, false);
    }
  }
}
```

### Complexity Analysis

**Constructor:** `O(K)`
**add():** `O(1)`
**showFirstUnique():** `O(1)` amortized

Space complexity:

```
O(N)
```

---

# Approach 3: LinkedHashSet + HashMap

## Intuition

To eliminate amortization, perform removals during `add()`.

Problem: removing from middle of queue is expensive.

Solution: use **LinkedHashSet**.

LinkedHashSet properties:

- HashSet lookup → `O(1)`
- Maintains insertion order
- Supports `O(1)` removal from middle

This allows maintaining a queue of unique numbers efficiently.

### Implementation

```java
class FirstUnique {

  private Set<Integer> setQueue = new LinkedHashSet<>();
  private Map<Integer, Boolean> isUnique = new HashMap<>();

  public FirstUnique(int[] nums) {
    for (int num : nums) {
      this.add(num);
    }
  }

  public int showFirstUnique() {

    if (!setQueue.isEmpty()) {
       return setQueue.iterator().next();
    }

    return -1;
  }

  public void add(int value) {

    if (!isUnique.containsKey(value)) {

      isUnique.put(value, true);
      setQueue.add(value);

    } else if (isUnique.get(value)) {

      isUnique.put(value, false);
      setQueue.remove(value);
    }
  }
}
```

### Complexity Analysis

**Constructor:** `O(K)`
**add():** `O(1)`
**showFirstUnique():** `O(1)`

Space complexity:

```
O(N)
```

---

# Key Takeaways

| Approach                | Idea                        | Query Complexity |
| ----------------------- | --------------------------- | ---------------- |
| Brute Force             | Count occurrences           | O(N²)            |
| Queue + HashMap         | Track unique status         | O(1) amortized   |
| LinkedHashSet + HashMap | Maintain ordered unique set | O(1)             |

The **LinkedHashSet approach** provides the cleanest fully constant-time design.
