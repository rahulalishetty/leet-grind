# 2424. Longest Uploaded Prefix

## Problem Restatement

We are given a stream of videos numbered from **1 to n**.

Videos are uploaded **in arbitrary order**, and we must support two operations:

- `upload(video)` → mark that a specific video has been uploaded
- `longest()` → return the **length of the longest uploaded prefix**

A prefix `i` is valid if **all videos from `1` to `i` have been uploaded**.

Example:

```
uploaded = {1,2,3}
longest prefix = 3
```

But if:

```
uploaded = {2,3}
longest prefix = 0
```

because video `1` is missing.

---

# Key Insight

The longest prefix is simply the **largest `i` such that every video from `1..i` is uploaded**.

We never need to check values greater than the current prefix.

Instead, maintain a pointer that expands forward whenever the next video becomes available.

---

# Approach 1 — Boolean Array + Pointer (Optimal)

## Intuition

Maintain:

```
uploaded[i] → whether video i has been uploaded
pointer → current longest prefix
```

Algorithm:

1. Mark uploaded videos in a boolean array.
2. Maintain pointer `prefix`.
3. Whenever a video is uploaded, we check if it allows us to extend the prefix.

Example:

```
uploaded = [false,false,false,false]
prefix = 0
```

Upload `1`:

```
uploaded[1] = true
prefix -> 1
```

Upload `2`:

```
prefix -> 2
```

Upload `3`:

```
prefix -> 3
```

This works because prefix **only moves forward**.

---

## Java Implementation

```java
class LUPrefix {

    private boolean[] uploaded;
    private int prefix;

    public LUPrefix(int n) {
        uploaded = new boolean[n + 2];
        prefix = 0;
    }

    public void upload(int video) {
        uploaded[video] = true;

        while (uploaded[prefix + 1]) {
            prefix++;
        }
    }

    public int longest() {
        return prefix;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
upload: amortized O(1)
longest: O(1)
```

Because the pointer only moves **forward at most n times**.

### Space Complexity

```
O(n)
```

This is the **best possible solution**.

---

# Approach 2 — HashSet + Pointer

## Intuition

Instead of a boolean array, use a `HashSet` to store uploaded videos.

Then extend the prefix if the next number exists.

---

## Java Implementation

```java
import java.util.*;

class LUPrefix {

    private Set<Integer> set;
    private int prefix;

    public LUPrefix(int n) {
        set = new HashSet<>();
        prefix = 0;
    }

    public void upload(int video) {
        set.add(video);

        while (set.contains(prefix + 1)) {
            prefix++;
        }
    }

    public int longest() {
        return prefix;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
upload: amortized O(1)
longest: O(1)
```

### Space Complexity

```
O(n)
```

However, this uses hashing overhead.

---

# Approach 3 — TreeSet (Ordered Structure)

## Intuition

We can store uploaded videos in a **TreeSet** and track the prefix.

But because TreeSet operations are `O(log n)`, this is slower than the array approach.

---

## Java Implementation

```java
import java.util.*;

class LUPrefix {

    private TreeSet<Integer> set;
    private int prefix;

    public LUPrefix(int n) {
        set = new TreeSet<>();
        prefix = 0;
    }

    public void upload(int video) {
        set.add(video);

        while (set.contains(prefix + 1)) {
            prefix++;
        }
    }

    public int longest() {
        return prefix;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
upload: O(log n)
longest: O(1)
```

### Space Complexity

```
O(n)
```

This approach is correct but unnecessary.

---

# Approach 4 — Union Find (Overkill but Interesting)

## Intuition

We can treat uploaded videos as **connected segments**.

Union adjacent uploaded videos.

Then check if the component containing `1` reaches some index `i`.

However, this requires additional structures and is unnecessarily complex for this problem.

---

## Why Union-Find is Overkill

The prefix only grows forward sequentially.

We don't need dynamic connectivity.

Therefore a simple pointer approach is far simpler and faster.

---

# Example Walkthrough

Input:

```
n = 4
```

Operations:

```
upload(3)
longest() -> 0
```

State:

```
uploaded = {3}
prefix = 0
```

---

```
upload(1)
longest() -> 1
```

State:

```
uploaded = {1,3}
prefix = 1
```

---

```
upload(2)
longest() -> 3
```

State:

```
uploaded = {1,2,3}
prefix = 3
```

---

# Correctness Proof

## Claim

The algorithm always returns the correct longest prefix.

### Reason

`prefix` is defined as the largest integer such that:

```
all videos 1..prefix are uploaded
```

Whenever a new video is uploaded:

- if it is **greater than prefix + 1**, it cannot extend the prefix
- if it is **exactly prefix + 1**, the prefix expands

The loop continues expanding until the next missing video appears.

Because each index is visited once, correctness holds.

---

# Comparison of Approaches

| Approach                | Time           | Space | Notes             |
| ----------------------- | -------------- | ----- | ----------------- |
| Boolean Array + Pointer | O(1) amortized | O(n)  | **Best solution** |
| HashSet                 | O(1) amortized | O(n)  | extra hashing     |
| TreeSet                 | O(log n)       | O(n)  | slower            |
| Union-Find              | O(α(n))        | O(n)  | unnecessary       |

---

# Final Recommended Solution

Use the **boolean array + prefix pointer** method.

```java
class LUPrefix {

    private boolean[] uploaded;
    private int prefix;

    public LUPrefix(int n) {
        uploaded = new boolean[n + 2];
        prefix = 0;
    }

    public void upload(int video) {
        uploaded[video] = true;

        while (uploaded[prefix + 1]) {
            prefix++;
        }
    }

    public int longest() {
        return prefix;
    }
}
```

---

# Final Complexity

```
Time:  O(1) amortized per operation
Space: O(n)
```

This solution easily handles the constraint:

```
2 * 10^5 operations
```
