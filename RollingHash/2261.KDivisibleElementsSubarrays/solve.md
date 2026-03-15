# 2261. K Divisible Elements Subarrays

## Problem Statement

Given an integer array `nums` and two integers `k` and `p`, return the number of **distinct subarrays** that contain **at most `k` elements divisible by `p`**.

Two subarrays are distinct if:

- they have different lengths, or
- there exists at least one index where their values differ.

A subarray is a **contiguous non-empty** part of the array.

---

## Example 1

```text
Input:
nums = [2,3,3,2,2], k = 2, p = 2

Output:
11
```

Valid distinct subarrays are:

```text
[2], [2,3], [2,3,3], [2,3,3,2],
[3], [3,3], [3,3,2], [3,3,2,2],
[3,2], [3,2,2], [2,2]
```

The full subarray `[2,3,3,2,2]` is invalid because it contains `3` elements divisible by `2`, which is more than `k = 2`.

---

## Example 2

```text
Input:
nums = [1,2,3,4], k = 4, p = 1

Output:
10
```

Since every element is divisible by `1`, every subarray has at most `4` divisible elements, so all subarrays are valid.

Total number of subarrays of length `4` is:

```text
4 * 5 / 2 = 10
```

---

## Constraints

- `1 <= nums.length <= 200`
- `1 <= nums[i], p <= 200`
- `1 <= k <= nums.length`

---

## Follow-up

Can you solve it in:

```text
O(n^2)
```

time?

---

# Core Idea

We need to count **distinct** subarrays satisfying:

```text
number of elements divisible by p <= k
```

There are two subproblems here:

1. **Validity check**: whether a subarray has at most `k` divisible elements
2. **Distinctness check**: avoid counting the same subarray values more than once

Because `n <= 200`, even a fairly direct solution can pass. But the follow-up asks for an `O(n^2)` direction, so it is worth understanding multiple levels of improvement.

---

# Approach 1: Brute Force + Build Subarray Lists + HashSet

## Intuition

The most straightforward solution is:

- generate every subarray
- count how many elements in it are divisible by `p`
- if valid, store the subarray in a set to ensure uniqueness

In Java, arrays do not compare by content inside a `HashSet`, so the easiest direct representation is usually a `List<Integer>`.

This approach is conceptually simple, but not the cleanest in performance.

---

## Algorithm

For each `start` index:

1. Initialize `divisibleCount = 0`
2. Extend the subarray to every `end >= start`
3. If `nums[end] % p == 0`, increment `divisibleCount`
4. If `divisibleCount > k`, stop extending
5. Otherwise, build the current subarray and add it to a set

---

## Java Code

```java
import java.util.*;

class Solution {
    public int countDistinct(int[] nums, int k, int p) {
        Set<List<Integer>> seen = new HashSet<>();
        int n = nums.length;

        for (int start = 0; start < n; start++) {
            int divisibleCount = 0;
            List<Integer> current = new ArrayList<>();

            for (int end = start; end < n; end++) {
                if (nums[end] % p == 0) {
                    divisibleCount++;
                }

                if (divisibleCount > k) {
                    break;
                }

                current.add(nums[end]);
                seen.add(new ArrayList<>(current));
            }
        }

        return seen.size();
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n^3)
```

Why?

- `O(n^2)` subarrays
- copying the current list into the set can cost `O(length)`

So in the worst case, total copying cost becomes cubic.

### Space Complexity

```text
O(n^3)
```

In the worst case, many distinct subarrays are stored, and their total content size can be cubic in aggregate.

---

## Verdict

This is acceptable for the given constraints, and it is the easiest correct solution to write in an interview if you want a baseline first.

But it does not meet the follow-up spirit cleanly.

---

# Approach 2: Brute Force + String Serialization + HashSet

## Intuition

Instead of storing `List<Integer>`, we can serialize each subarray into a string such as:

```text
"2#3#3#2"
```

and store the string in a `HashSet<String>`.

This is often shorter to write and can be easier to debug.

It is still not asymptotically better, because string building also costs proportional to subarray length.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int countDistinct(int[] nums, int k, int p) {
        Set<String> seen = new HashSet<>();
        int n = nums.length;

        for (int start = 0; start < n; start++) {
            int divisibleCount = 0;
            StringBuilder sb = new StringBuilder();

            for (int end = start; end < n; end++) {
                if (nums[end] % p == 0) {
                    divisibleCount++;
                }

                if (divisibleCount > k) {
                    break;
                }

                sb.append(nums[end]).append('#');
                seen.add(sb.toString());
            }
        }

        return seen.size();
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n^3)
```

### Space Complexity

```text
O(n^3)
```

---

## Verdict

Good for readability, but still fundamentally cubic.

---

# Approach 3: Rolling Hash for Subarrays + Early Break

## Intuition

The expensive part of Approaches 1 and 2 is that every valid subarray must be copied or serialized in full.

We can avoid that by representing each subarray with a rolling hash.

As we extend the subarray:

```text
hash = hash * base + value
```

Then insert that hash into a set.

This reduces per-extension work to constant time.

Because we also stop as soon as the divisible count exceeds `k`, the total time becomes much closer to `O(n^2)`.

However, hashing introduces the theoretical possibility of collisions.

For competitive programming, that is often acceptable, especially with a robust base and `long`.

---

## Important Design Choice

Since `nums[i] <= 200`, values are small.
So a base larger than `200` works naturally.

For example:

```text
base = 201
```

Then:

```text
hash = hash * 201 + nums[end]
```

This behaves like encoding the subarray in base 201.

Because Java `long` can overflow for longer arrays, many solutions simply rely on overflow behavior as a large implicit modulus. That is usually acceptable here.

Another option is to use an explicit modulus, but then collision risk still exists anyway.

---

## Algorithm

For each `start` index:

1. Set `divisibleCount = 0`
2. Set `hash = 0`
3. For each `end >= start`:
   - if `nums[end] % p == 0`, increment `divisibleCount`
   - if `divisibleCount > k`, break
   - update rolling hash
   - insert hash into set

---

## Java Code

```java
import java.util.*;

class Solution {
    public int countDistinct(int[] nums, int k, int p) {
        Set<Long> seen = new HashSet<>();
        int n = nums.length;
        long base = 201L;

        for (int start = 0; start < n; start++) {
            int divisibleCount = 0;
            long hash = 0;

            for (int end = start; end < n; end++) {
                if (nums[end] % p == 0) {
                    divisibleCount++;
                }

                if (divisibleCount > k) {
                    break;
                }

                hash = hash * base + nums[end];
                seen.add(hash);
            }
        }

        return seen.size();
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n^2)
```

There are `O(n^2)` subarray extensions total, and each extension does only constant work.

### Space Complexity

```text
O(n^2)
```

In the worst case, all valid subarrays are distinct.

---

## Verdict

This is a strong practical answer to the follow-up.

But it is still probabilistic due to hash collisions.

A skeptical interviewer might ask: can we avoid collisions entirely?

That leads us to a trie.

---

# Approach 4: Trie of Subarrays (Exact O(n^2) Distinctness)

## Intuition

We want:

- `O(n^2)` generation of valid subarrays
- exact distinctness checking
- no collision risk

A trie is perfect for this.

Each path from the root represents a subarray:

- root -> `2` -> `3` -> `3` represents `[2,3,3]`
- root -> `3` -> `2` represents `[3,2]`

As we build subarrays starting at each index, we walk/create nodes in the trie.

Whenever we first create a node for a path, that means this exact subarray has never appeared before, so we count it once.

Because `nums[i] <= 200`, each node can store children in a `Map<Integer, TrieNode>`.

---

## Why This Solves Distinctness Exactly

Two subarrays are the same if and only if they have the same sequence of values.

In a trie, equal sequences land on the same path. Different sequences diverge somewhere.

So uniqueness is captured structurally, not probabilistically.

---

## Algorithm

1. Create a trie root.
2. For each `start`:
   - reset `divisibleCount = 0`
   - begin at trie root
3. Extend `end` from `start` to `n-1`:
   - update `divisibleCount`
   - if it exceeds `k`, break
   - move/create child for `nums[end]`
   - if this child was newly created, increment answer

The key observation:

- each newly created trie node corresponds to one newly discovered distinct valid subarray

---

## Java Code

```java
import java.util.*;

class Solution {
    static class TrieNode {
        Map<Integer, TrieNode> children = new HashMap<>();
    }

    public int countDistinct(int[] nums, int k, int p) {
        TrieNode root = new TrieNode();
        int n = nums.length;
        int answer = 0;

        for (int start = 0; start < n; start++) {
            TrieNode current = root;
            int divisibleCount = 0;

            for (int end = start; end < n; end++) {
                if (nums[end] % p == 0) {
                    divisibleCount++;
                }

                if (divisibleCount > k) {
                    break;
                }

                if (!current.children.containsKey(nums[end])) {
                    current.children.put(nums[end], new TrieNode());
                    answer++;
                }

                current = current.children.get(nums[end]);
            }
        }

        return answer;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n^2)
```

Why?

- there are `O(n^2)` subarray extensions
- each extension performs expected `O(1)` map access

### Space Complexity

```text
O(n^2)
```

In the worst case, every valid subarray is distinct, so the trie can contain `O(n^2)` nodes.

---

## Verdict

This is the cleanest exact answer to the follow-up:

- no collision risk
- `O(n^2)` time
- exact uniqueness checking

For this problem, this is arguably the best conceptual solution.

---

# Approach 5: Trie with Fixed-Size Children Array

## Intuition

Because:

```text
1 <= nums[i] <= 200
```

we can replace the `HashMap<Integer, TrieNode>` with a fixed-size array of length `201`.

That avoids hash map overhead and makes each transition strictly constant time.

This is often faster in practice.

---

## Trade-off

This improves constant factors, but each node now allocates a full `TrieNode[201]`.

That may waste memory if the trie is sparse.

Given `n <= 200`, this is still manageable in many settings, but it is less elegant memory-wise.

---

## Java Code

```java
class Solution {
    static class TrieNode {
        TrieNode[] children = new TrieNode[201];
    }

    public int countDistinct(int[] nums, int k, int p) {
        TrieNode root = new TrieNode();
        int answer = 0;
        int n = nums.length;

        for (int start = 0; start < n; start++) {
            TrieNode current = root;
            int divisibleCount = 0;

            for (int end = start; end < n; end++) {
                if (nums[end] % p == 0) {
                    divisibleCount++;
                }

                if (divisibleCount > k) {
                    break;
                }

                int value = nums[end];
                if (current.children[value] == null) {
                    current.children[value] = new TrieNode();
                    answer++;
                }

                current = current.children[value];
            }
        }

        return answer;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```text
O(n^2)
```

### Space Complexity

```text
O(n^2 * 201)
```

If analyzed by raw allocated array slots, the constant factor is much larger.
In asymptotic interview discussion, we usually still summarize the node count as `O(n^2)`, but memory constants are significantly heavier.

---

# Prefix Divisible Count Optimization

## Intuition

In the previous approaches, we updated `divisibleCount` incrementally as we extended `end`.

That is already efficient.

But another possible viewpoint is to precompute a prefix count array:

```text
divPrefix[i] = number of elements divisible by p in nums[0..i-1]
```

Then for any subarray `[l..r]`, the number of divisible elements is:

```text
divPrefix[r + 1] - divPrefix[l]
```

This can be useful if you want random-access subarray validity queries.

However, for this problem, since we already extend subarrays in order and can break early once the count exceeds `k`, the incremental counter is usually simpler and better.

So prefix counts are conceptually useful, but not necessary for the strongest solution.

---

# Why Early Break Matters

Suppose while extending a subarray from `start`, the divisible count exceeds `k` at position `end`.

Then every longer subarray starting at `start` will also exceed `k`, because we only add more elements and never remove any.

So we can safely stop.

This pruning is what keeps all approaches efficient enough.

---

# Recommended Solution

## Best exact solution

Use the **trie** approach.

Why?

- it meets the `O(n^2)` follow-up
- it avoids hash collisions
- it handles distinctness naturally

## Best compact practical solution

Use the **rolling hash set** approach.

Why?

- shorter code
- still `O(n^2)` in practice
- usually accepted

---

# Final Recommended Java Solution (Trie)

```java
import java.util.*;

class Solution {
    static class TrieNode {
        Map<Integer, TrieNode> children = new HashMap<>();
    }

    public int countDistinct(int[] nums, int k, int p) {
        TrieNode root = new TrieNode();
        int distinctCount = 0;

        for (int start = 0; start < nums.length; start++) {
            TrieNode current = root;
            int divisibleCount = 0;

            for (int end = start; end < nums.length; end++) {
                if (nums[end] % p == 0) {
                    divisibleCount++;
                }

                if (divisibleCount > k) {
                    break;
                }

                current.children.putIfAbsent(nums[end], new TrieNode());

                if (current.children.get(nums[end]).children.isEmpty()
                    && current.children.size() > 0) {
                    // This check is not reliable for first-creation counting.
                    // Keep the more explicit version below in practice.
                }

                current = current.children.get(nums[end]);
            }
        }

        return countDistinctUsingExplicitCreation(nums, k, p);
    }

    private int countDistinctUsingExplicitCreation(int[] nums, int k, int p) {
        TrieNode root = new TrieNode();
        int answer = 0;

        for (int start = 0; start < nums.length; start++) {
            TrieNode current = root;
            int divisibleCount = 0;

            for (int end = start; end < nums.length; end++) {
                if (nums[end] % p == 0) {
                    divisibleCount++;
                }

                if (divisibleCount > k) {
                    break;
                }

                if (!current.children.containsKey(nums[end])) {
                    current.children.put(nums[end], new TrieNode());
                    answer++;
                }

                current = current.children.get(nums[end]);
            }
        }

        return answer;
    }
}
```

---

# Cleaner Final Java Solution

```java
import java.util.*;

class Solution {
    static class TrieNode {
        Map<Integer, TrieNode> children = new HashMap<>();
    }

    public int countDistinct(int[] nums, int k, int p) {
        TrieNode root = new TrieNode();
        int answer = 0;

        for (int start = 0; start < nums.length; start++) {
            TrieNode current = root;
            int divisibleCount = 0;

            for (int end = start; end < nums.length; end++) {
                if (nums[end] % p == 0) {
                    divisibleCount++;
                }

                if (divisibleCount > k) {
                    break;
                }

                if (!current.children.containsKey(nums[end])) {
                    current.children.put(nums[end], new TrieNode());
                    answer++;
                }

                current = current.children.get(nums[end]);
            }
        }

        return answer;
    }
}
```

---

# Common Mistakes

## 1. Counting all valid subarrays instead of distinct ones

This is the biggest trap.

The problem does not ask for the number of valid positions.
It asks for the number of distinct value sequences.

---

## 2. Forgetting to break after divisible count exceeds `k`

Once invalid, longer subarrays from the same start remain invalid.

Not breaking wastes time.

---

## 3. Using arrays directly in a `HashSet<int[]>`

This does not compare by content in Java.

Two arrays with identical values are still different objects.

So this is wrong for distinctness.

---

## 4. Assuming rolling hash is collision-free

It is usually fine in practice, but not mathematically exact.

That is why the trie solution is stronger conceptually.

---

# Interview Summary

## Baseline

Generate every subarray, check validity, store it in a set.

## Improvement

Avoid repeated copying with rolling hash.

## Exact follow-up answer

Use a trie of subarrays and stop extension as soon as divisible count exceeds `k`.

---

# Final Complexity Summary

## Brute force with explicit subarray storage

- Time: `O(n^3)`
- Space: `O(n^3)`

## Rolling hash set

- Time: `O(n^2)`
- Space: `O(n^2)`

## Trie

- Time: `O(n^2)`
- Space: `O(n^2)`

---

# Final Answer

The best exact `O(n^2)` approach is the trie-based solution:

```java
import java.util.*;

class Solution {
    static class TrieNode {
        Map<Integer, TrieNode> children = new HashMap<>();
    }

    public int countDistinct(int[] nums, int k, int p) {
        TrieNode root = new TrieNode();
        int answer = 0;

        for (int start = 0; start < nums.length; start++) {
            TrieNode current = root;
            int divisibleCount = 0;

            for (int end = start; end < nums.length; end++) {
                if (nums[end] % p == 0) {
                    divisibleCount++;
                }

                if (divisibleCount > k) {
                    break;
                }

                if (!current.children.containsKey(nums[end])) {
                    current.children.put(nums[end], new TrieNode());
                    answer++;
                }

                current = current.children.get(nums[end]);
            }
        }

        return answer;
    }
}
```
