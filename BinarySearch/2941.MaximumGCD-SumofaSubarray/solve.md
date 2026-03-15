# 2941. Maximum GCD-Sum of a Subarray — Java Solutions and Detailed Notes

## Problem

Given an array `nums` and an integer `k`, define the **gcd-sum** of a subarray as:

- `s` = sum of the subarray
- `g` = gcd of all elements in the subarray

Then:

```text
gcd-sum = s * g
```

We need the **maximum gcd-sum** among all subarrays with length at least `k`.

---

## First observations

A brute-force approach would enumerate every subarray, compute its sum and gcd, and keep the best answer.

That is far too slow:

- there are `O(n^2)` subarrays,
- recomputing gcd and sum repeatedly is expensive.

We need to exploit structure.

Two key facts matter:

### 1. Prefix sums give subarray sums in O(1)

If:

```text
prefix[i] = nums[0] + nums[1] + ... + nums[i-1]
```

then sum of subarray `[l..r]` is:

```text
prefix[r + 1] - prefix[l]
```

### 2. The number of distinct gcd values for subarrays ending at a fixed index is small

When we extend subarrays ending at position `i`, the gcd values can only decrease and they merge aggressively.
So for each ending position, the number of distinct gcds is at most about `O(log(max(nums)))` in practice.

This is the main optimization.

---

# Approach 1: Brute Force with Prefix Sum + Rolling GCD

## Idea

Enumerate every start index `l`, extend to every end index `r`, keep a rolling gcd, compute sum with prefix sums, and update answer if the length is at least `k`.

This is still too slow for the given constraints, but it is the most direct baseline.

---

## Java code

```java
class Solution {
    public long maxGcdSum(int[] nums, int k) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }

        long ans = 0;

        for (int l = 0; l < n; l++) {
            int g = 0;
            for (int r = l; r < n; r++) {
                g = gcd(g, nums[r]);
                if (r - l + 1 >= k) {
                    long sum = prefix[r + 1] - prefix[l];
                    ans = Math.max(ans, sum * g);
                }
            }
        }

        return ans;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Complexity

Time complexity:

```text
O(n^2 * log V)
```

where `V = max(nums)`.

Space complexity:

```text
O(n)
```

This is not acceptable for `n = 10^5`.

---

# Approach 2: Distinct GCD states for subarrays ending at each index (Optimal)

This is the intended efficient approach.

## Idea

For each index `i`, consider all subarrays that end at `i`.

If we know the distinct gcd values of subarrays ending at `i - 1`, then for `i` we can form new gcds by taking:

```text
gcd(previous_gcd, nums[i])
```

plus the new one-element subarray `[nums[i]]`.

Many of these gcds collapse to the same value, so the state remains small.

For each gcd value, we only need to know the earliest start index that gives that gcd for some suffix ending at `i`.

Actually, because we want the maximum sum for a fixed gcd and end index, we want the **smallest start index** among subarrays ending at `i` with that gcd, since all numbers are positive and a smaller start gives a larger sum.

So each state is:

```text
(gcdValue, minimumStartIndex)
```

for subarrays ending at current position.

Then, whenever a state corresponds to a subarray length at least `k`, we compute its sum and candidate answer.

---

## Why keeping minimum start works

For a fixed end index `i` and fixed gcd `g`, if there are multiple subarrays ending at `i` with gcd `g`, then the one with the **smallest start** has the **largest sum**, because all numbers are positive.

So among all subarrays ending at `i` with gcd `g`, only the smallest start matters.

---

## State transition

Suppose `prev` stores states for subarrays ending at `i - 1`.

For current `nums[i]`, create new states:

1. Start a fresh subarray `[i..i]`:
   ```text
   (nums[i], i)
   ```

2. For each `(g, start)` in `prev`, extend by `nums[i]`:
   ```text
   newG = gcd(g, nums[i])
   ```
   Then merge by gcd value:
   - if `newG` already exists, keep the minimum start index
   - else insert it

This creates the compressed state list for subarrays ending at `i`.

Then evaluate all states whose length is at least `k`.

---

## Java code

```java
import java.util.*;

class Solution {
    public long maxGcdSum(int[] nums, int k) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }

        long ans = 0;

        List<int[]> prev = new ArrayList<>();
        // each pair: [gcd, minStart]

        for (int i = 0; i < n; i++) {
            List<int[]> curr = new ArrayList<>();
            curr.add(new int[]{nums[i], i});

            for (int[] p : prev) {
                int newG = gcd(p[0], nums[i]);
                int start = p[1];

                if (curr.get(curr.size() - 1)[0] == newG) {
                    curr.get(curr.size() - 1)[1] = Math.min(curr.get(curr.size() - 1)[1], start);
                } else {
                    curr.add(new int[]{newG, start});
                }
            }

            // compress further if consecutive entries share gcd
            List<int[]> compressed = new ArrayList<>();
            for (int[] p : curr) {
                if (!compressed.isEmpty() && compressed.get(compressed.size() - 1)[0] == p[0]) {
                    compressed.get(compressed.size() - 1)[1] =
                        Math.min(compressed.get(compressed.size() - 1)[1], p[1]);
                } else {
                    compressed.add(new int[]{p[0], p[1]});
                }
            }

            for (int[] p : compressed) {
                int g = p[0];
                int start = p[1];
                if (i - start + 1 >= k) {
                    long sum = prefix[i + 1] - prefix[start];
                    ans = Math.max(ans, sum * g);
                }
            }

            prev = compressed;
        }

        return ans;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Complexity

Let `D` be the number of distinct gcd states per index. In practice this is small, around `O(log V)`.

Time complexity:

```text
O(n * D * log V)
```

Usually written as roughly:

```text
O(n log^2 V)
```

or simply `O(n * number_of_distinct_gcd_states)`.

Space complexity:

```text
O(n)` for prefix sums + O(D) state list
```

This is efficient enough for `n = 10^5`.

---

# Approach 3: HashMap-based version of the same optimal idea

## Idea

Instead of a list of `(gcd, minStart)` pairs, use a map from gcd value to minimum start index.

This is often easier to reason about, though a list-based compressed approach is usually faster in Java due to lower overhead.

---

## Java code

```java
import java.util.*;

class Solution {
    public long maxGcdSum(int[] nums, int k) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }

        long ans = 0;
        Map<Integer, Integer> prev = new HashMap<>();

        for (int i = 0; i < n; i++) {
            Map<Integer, Integer> curr = new HashMap<>();

            curr.put(nums[i], i);

            for (Map.Entry<Integer, Integer> entry : prev.entrySet()) {
                int g = entry.getKey();
                int start = entry.getValue();

                int newG = gcd(g, nums[i]);
                curr.merge(newG, start, Math::min);
            }

            for (Map.Entry<Integer, Integer> entry : curr.entrySet()) {
                int g = entry.getKey();
                int start = entry.getValue();

                if (i - start + 1 >= k) {
                    long sum = prefix[i + 1] - prefix[start];
                    ans = Math.max(ans, sum * g);
                }
            }

            prev = curr;
        }

        return ans;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Complexity

Time complexity is similar to Approach 2 in principle, but HashMap overhead makes constants larger.

Space complexity:

```text
O(n + D)
```

---

# Why the compressed gcd-state method works

For each end index `i`, every subarray ending at `i` either:

- starts at `i` itself, giving gcd `nums[i]`, or
- extends a subarray ending at `i - 1`, so its gcd becomes:
  ```text
  gcd(oldGcd, nums[i])
  ```

So the set of gcds for subarrays ending at `i` can be generated entirely from the previous state.

Because gcd only decreases and many values collapse together, the number of distinct gcds stays small.

For each gcd, only the smallest start index matters, because it gives the largest sum among subarrays ending at `i` with that gcd.

Thus checking those compressed states is enough to find the optimal answer.

---

# Worked example

## Example 1

```text
nums = [2,1,4,4,4,2], k = 2
```

Prefix sums:

```text
[0,2,3,7,11,15,17]
```

We process ending positions one by one.

### i = 0, nums[i] = 2
Subarrays ending here:

- `[2]` → gcd 2, start 0

Length is 1 < k, so ignore.

### i = 1, nums[i] = 1
New states:

- `[1]` → gcd 1, start 1
- extending previous gcd 2:
  `gcd(2,1)=1`, start 0

Compressed:
- gcd 1, minimum start 0

Now subarray `[2,1]` has:
- sum = 3
- gcd = 1
- gcd-sum = 3

### i = 2, nums[i] = 4
States:
- `[4]` → gcd 4, start 2
- extend gcd 1 from previous → gcd(1,4)=1, start 0

Compressed:
- gcd 4, start 2
- gcd 1, start 0

For gcd 1 with start 0:
- length 3
- sum 7
- gcd-sum 7

### i = 3, nums[i] = 4
States:
- `[4]` → gcd 4, start 3
- extend gcd 4,start2 → gcd 4,start2
- extend gcd 1,start0 → gcd 1,start0

Compressed:
- gcd 4, start 2
- gcd 1, start 0

For gcd 4,start2:
- subarray `[4,4]`
- sum = 8
- gcd-sum = 32

### i = 4, nums[i] = 4
Compressed states:
- gcd 4, start 2
- gcd 1, start 0

For gcd 4,start2:
- subarray `[4,4,4]`
- sum = 12
- gcd-sum = 48

That becomes the maximum answer.

---

# Comparison of approaches

## Approach 1: Brute Force
### Pros
- simplest to derive

### Cons
- far too slow

### Complexity

```text
Time:  O(n^2 log V)
Space: O(n)
```

---

## Approach 2: Compressed gcd states with list (Recommended)
### Pros
- optimal practical solution
- low overhead
- works within constraints

### Cons
- slightly trickier to derive

### Complexity

```text
Time:  ~O(n * number_of_distinct_gcd_states)
Space: O(n + D)
```

---

## Approach 3: HashMap gcd states
### Pros
- conceptually clean
- easy to code

### Cons
- larger constant factors than list compression

### Complexity

Similar asymptotically to Approach 2, but usually slower in Java.

---

# Final recommended Java solution

```java
import java.util.*;

class Solution {
    public long maxGcdSum(int[] nums, int k) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }

        long ans = 0;
        List<int[]> prev = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            List<int[]> curr = new ArrayList<>();
            curr.add(new int[]{nums[i], i});

            for (int[] state : prev) {
                int g = gcd(state[0], nums[i]);
                int start = state[1];

                if (curr.get(curr.size() - 1)[0] == g) {
                    curr.get(curr.size() - 1)[1] = Math.min(curr.get(curr.size() - 1)[1], start);
                } else {
                    curr.add(new int[]{g, start});
                }
            }

            List<int[]> compressed = new ArrayList<>();
            for (int[] state : curr) {
                if (!compressed.isEmpty() &&
                    compressed.get(compressed.size() - 1)[0] == state[0]) {
                    compressed.get(compressed.size() - 1)[1] =
                        Math.min(compressed.get(compressed.size() - 1)[1], state[1]);
                } else {
                    compressed.add(new int[]{state[0], state[1]});
                }
            }

            for (int[] state : compressed) {
                int g = state[0];
                int start = state[1];

                if (i - start + 1 >= k) {
                    long sum = prefix[i + 1] - prefix[start];
                    ans = Math.max(ans, sum * g);
                }
            }

            prev = compressed;
        }

        return ans;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

# Edge cases

## 1. `k = 1`
Then every single element subarray is allowed, so answer is at least:

```text
max(nums[i] * nums[i])
```

The algorithm naturally handles this.

## 2. All numbers equal
Then every subarray has the same gcd, and larger sums dominate.
The optimal answer is the whole array’s sum times that gcd, provided length >= k.

## 3. Highly varying numbers
The compressed gcd state count still remains manageable because gcd values collapse aggressively.

---

# Pattern takeaway

This problem is a classic example of:

```text
For each ending index, maintain all distinct gcds of subarrays ending there
```

This pattern appears in several advanced array + gcd problems.

The key reason it works is:

- extending subarrays transforms gcds predictably via `gcd(old, newValue)`
- many subarrays share the same gcd
- so we can compress states instead of enumerating all subarrays

That turns an impossible `O(n^2)` problem into an efficient near-linear one.
