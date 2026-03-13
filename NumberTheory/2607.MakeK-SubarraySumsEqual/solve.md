# 2607. Make K-Subarray Sums Equal

## Problem Restatement

You are given a circular array `arr` and an integer `k`.

You may increment or decrement any element by `1` per operation.

Your goal is to make the sum of **every circular subarray of length `k`** equal, with the **minimum total number of operations**.

---

## Core Insight

Let:

- `S_i` = sum of the circular subarray of length `k` starting at index `i`

If all such sums are equal, then:

```text
S_i = S_{i+1}
```

Expand both:

```text
arr[i] + arr[i+1] + ... + arr[i+k-1]
arr[i+1] + arr[i+2] + ... + arr[i+k]
```

Everything cancels except:

```text
arr[i] = arr[i+k]   (mod n)
```

So the condition "all circular subarrays of length `k` have equal sum" is **equivalent** to:

```text
arr[i] = arr[(i + k) mod n]   for every i
```

That means indices connected by repeatedly adding `k` modulo `n` must all end up with the **same value**.

---

## Why Cycles Appear

Consider moving through indices like this:

```text
i, (i+k) mod n, (i+2k) mod n, ...
```

These positions form a cycle.

The number of distinct cycles is:

```text
g = gcd(n, k)
```

Each cycle contains:

```text
n / g
```

indices.

Inside one cycle, all values must become equal.

So the problem becomes:

> For each cycle, choose one target value so that changing every value in that cycle to that target costs as little as possible.

---

## Best Target for One Cycle: Median

Suppose a cycle contains values:

```text
[x1, x2, x3, ..., xm]
```

If we make them all equal to `t`, cost is:

```text
|x1 - t| + |x2 - t| + ... + |xm - t|
```

This expression is minimized when `t` is the **median**.

So the algorithm is:

1. Split indices into cycles.
2. For each cycle:
   - collect its values
   - find the median
   - sum distances to the median
3. Add costs of all cycles

---

# Approach 1 — Explicit Cycle Traversal + Sorting

## Intuition

We directly simulate the cycle structure induced by jumps of `k` in a circular array.

For each unvisited index:

- keep jumping by `k mod n`
- collect all values in that cycle
- sort them
- choose the median
- compute cost to make every value equal to the median

This is the most direct and easiest-to-understand method.

---

## Example 1

```text
arr = [1,4,1,3], k = 2
n = 4
gcd(4, 2) = 2
```

Cycles:

- cycle 1: indices `0 -> 2 -> 0`, values `[1, 1]`
- cycle 2: indices `1 -> 3 -> 1`, values `[4, 3]`

For `[1,1]`, median is `1`, cost = `0`

For `[4,3]`, median can be `3` or `4`, minimum cost = `1`

Total = `1`

---

## Algorithm

1. Let `n = arr.length`
2. Create `visited[]`
3. For each index `i`:
   - if already visited, skip
   - otherwise traverse the full cycle using `(curr + k) % n`
   - collect all values in a list
   - sort the list
   - take median = middle element
   - add absolute distance from every value to the median
4. Return total cost

---

## Java Code

```java
import java.util.*;

class Solution {
    public long makeSubKSumEqual(int[] arr, int k) {
        int n = arr.length;
        boolean[] visited = new boolean[n];
        long answer = 0;

        for (int start = 0; start < n; start++) {
            if (visited[start]) continue;

            List<Integer> cycle = new ArrayList<>();
            int cur = start;

            while (!visited[cur]) {
                visited[cur] = true;
                cycle.add(arr[cur]);
                cur = (cur + k) % n;
            }

            Collections.sort(cycle);
            int median = cycle.get(cycle.size() / 2);

            for (int value : cycle) {
                answer += Math.abs((long) value - median);
            }
        }

        return answer;
    }
}
```

---

## Complexity Analysis

Let:

- `n = arr.length`

Each index belongs to exactly one cycle and is visited once.

### Time Complexity

Collecting all cycles: `O(n)`

Sorting all cycle arrays:
the total across all cycles is:

```text
O(n log n)
```

So overall:

```text
O(n log n)
```

### Space Complexity

- visited array: `O(n)`
- cycle storage across processing: `O(n)` worst case

So:

```text
O(n)
```

---

# Approach 2 — Use gcd(n, k) Directly

## Intuition

The cycle structure does not need a visited array if we use number theory.

If:

```text
g = gcd(n, k)
```

then there are exactly `g` disjoint cycles.

They are:

- indices congruent to `0` under repeated jumps by `k`
- indices congruent to `1`
- ...
- indices congruent to `g - 1`

So for each `start` from `0` to `g - 1`, we can traverse one cycle directly.

This is cleaner and avoids the `visited[]` array.

---

## Why This Works

Repeated jumps by `k` modulo `n` partition the array into `gcd(n,k)` disjoint orbits.

So instead of discovering cycles dynamically, we can enumerate them deterministically.

---

## Algorithm

1. Compute `g = gcd(n, k)`
2. For each `start` from `0` to `g - 1`:
   - traverse:
     ```text
     start, (start + k) % n, (start + 2k) % n, ...
     ```
     until it returns to `start`
   - collect values in that cycle
   - sort
   - choose median
   - add absolute differences
3. Return total cost

---

## Java Code

```java
import java.util.*;

class Solution {
    public long makeSubKSumEqual(int[] arr, int k) {
        int n = arr.length;
        int g = gcd(n, k);
        long answer = 0;

        for (int start = 0; start < g; start++) {
            List<Integer> cycle = new ArrayList<>();
            int cur = start;

            do {
                cycle.add(arr[cur]);
                cur = (cur + k) % n;
            } while (cur != start);

            Collections.sort(cycle);
            int median = cycle.get(cycle.size() / 2);

            for (int value : cycle) {
                answer += Math.abs((long) value - median);
            }
        }

        return answer;
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

## Complexity Analysis

### Time Complexity

- Building cycles: `O(n)`
- Sorting all cycles together: `O(n log n)`

Overall:

```text
O(n log n)
```

### Space Complexity

```text
O(n)
```

for cycle storage in the worst case.

---

# Approach 3 — Optimized Median Selection with Quickselect

## Intuition

Sorting each cycle is simple, but sorting fully is stronger than what we actually need.

To minimize absolute deviation, we only need the **median**, not complete ordering.

So we can:

- build each cycle
- use **Quickselect** to find the median in average linear time
- then compute cost

This reduces the expected total runtime from `O(n log n)` to **expected `O(n)`** over all cycles.

This approach is more advanced and more interview-heavy.

---

## Important Note

Worst-case Quickselect can degrade to `O(n^2)`, though randomized pivot selection gives **expected linear time**.

So:

- if you want the cleanest production/interview answer, use sorting
- if you want the more optimized theoretical approach, use Quickselect

---

## Java Code

```java
import java.util.*;

class Solution {
    private final Random random = new Random();

    public long makeSubKSumEqual(int[] arr, int k) {
        int n = arr.length;
        int g = gcd(n, k);
        long answer = 0;

        for (int start = 0; start < g; start++) {
            ArrayList<Integer> cycleList = new ArrayList<>();
            int cur = start;

            do {
                cycleList.add(arr[cur]);
                cur = (cur + k) % n;
            } while (cur != start);

            int size = cycleList.size();
            int[] cycle = new int[size];
            for (int i = 0; i < size; i++) {
                cycle[i] = cycleList.get(i);
            }

            int median = quickSelect(cycle, 0, size - 1, size / 2);

            for (int value : cycle) {
                answer += Math.abs((long) value - median);
            }
        }

        return answer;
    }

    private int quickSelect(int[] nums, int left, int right, int k) {
        while (left <= right) {
            int pivotIndex = left + random.nextInt(right - left + 1);
            int finalIndex = partition(nums, left, right, pivotIndex);

            if (finalIndex == k) return nums[finalIndex];
            if (finalIndex < k) left = finalIndex + 1;
            else right = finalIndex - 1;
        }
        return -1;
    }

    private int partition(int[] nums, int left, int right, int pivotIndex) {
        int pivotValue = nums[pivotIndex];
        swap(nums, pivotIndex, right);

        int store = left;
        for (int i = left; i < right; i++) {
            if (nums[i] < pivotValue) {
                swap(nums, store, i);
                store++;
            }
        }

        swap(nums, store, right);
        return store;
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
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

## Complexity Analysis

### Expected Time Complexity

- cycle construction: `O(n)`
- median finding via Quickselect: expected `O(size of cycle)` per cycle

Total expected:

```text
O(n)
```

### Worst-Case Time Complexity

```text
O(n^2)
```

in adversarial pivot behavior.

### Space Complexity

```text
O(n)
```

---

# Formal Proof of the Transformation

## Claim 1

All circular subarrays of length `k` having equal sum implies:

```text
arr[i] = arr[(i+k) mod n]
```

### Proof

Let:

```text
S_i = arr[i] + arr[i+1] + ... + arr[i+k-1]
S_{i+1} = arr[i+1] + arr[i+2] + ... + arr[i+k]
```

If all sums are equal, then `S_i = S_{i+1}`.

Subtract the common overlapping part:

```text
arr[i] = arr[i+k]
```

Since the array is circular, indexing is modulo `n`.

So for every `i`:

```text
arr[i] = arr[(i+k) mod n]
```

Proved.

---

## Claim 2

Inside each cycle, making all values equal to the median gives minimum cost.

### Proof Sketch

For a set of values `x1, x2, ..., xm`, define:

```text
f(t) = Σ |xi - t|
```

This function is minimized at any median.

That is a standard property of absolute deviation.

So the optimal target for a cycle is its median.

Proved.

---

# Full Worked Example

## Example 2

```text
arr = [2,5,5,7], k = 3
n = 4
gcd(4, 3) = 1
```

So there is only one cycle:

```text
0 -> 3 -> 2 -> 1 -> 0
```

Values collected:

```text
[2, 7, 5, 5]
```

Sorted:

```text
[2, 5, 5, 7]
```

Median can be `5`

Cost:

```text
|2-5| + |7-5| + |5-5| + |5-5|
= 3 + 2 + 0 + 0
= 5
```

Answer = `5`

---

# Edge Cases

## 1. `k = n`

Every circular subarray of length `n` is the whole array, so all sums are already equal.

Our logic also agrees:

```text
gcd(n, n) = n
```

Each cycle has one element, so cost is `0`.

---

## 2. `k = 1`

Every subarray of length `1` is just one element, so all elements must become equal.

Our logic agrees:

```text
gcd(n, 1) = 1
```

One big cycle containing all elements.

Minimum cost = make all values equal to the median.

---

## 3. All values already valid

If every cycle already has identical values, answer is `0`.

---

## 4. Large values up to `10^9`

No issue, because we only use subtraction and absolute differences.

Use `long` for the answer, since total operations can exceed `int`.

---

# Which Approach Should You Use?

## Best interview answer

**Approach 2**

Why:

- mathematically clean
- directly uses `gcd(n, k)`
- easy to explain
- efficient enough

## Best easiest-to-understand answer

**Approach 1**

Why:

- explicit cycle discovery
- no number theory dependence in traversal logic

## Best optimized approach

**Approach 3**

Why:

- expected linear-time median selection
- more advanced, but less standard

---

# Final Recommended Java Solution

This is the version most worth remembering.

```java
import java.util.*;

class Solution {
    public long makeSubKSumEqual(int[] arr, int k) {
        int n = arr.length;
        int g = gcd(n, k);
        long answer = 0;

        for (int start = 0; start < g; start++) {
            List<Integer> cycle = new ArrayList<>();
            int cur = start;

            do {
                cycle.add(arr[cur]);
                cur = (cur + k) % n;
            } while (cur != start);

            Collections.sort(cycle);
            int median = cycle.get(cycle.size() / 2);

            for (int value : cycle) {
                answer += Math.abs((long) value - median);
            }
        }

        return answer;
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

# Summary

## Key Reduction

Equal sums of all circular subarrays of length `k`
iff

```text
arr[i] = arr[(i+k) mod n]
```

So indices form cycles.

## Number of cycles

```text
gcd(n, k)
```

## Best target per cycle

Median

## Final complexity

With sorting:

```text
Time:  O(n log n)
Space: O(n)
```

With Quickselect:

```text
Expected Time: O(n)
Space: O(n)
```

---
