# 698. Partition to K Equal Sum Subsets — Approaches

## Overview

We have the task of breaking the given array into `k` different parts such that all parts have the same sum.

This implies that each part will have a sum of:

```text
totalArraySum / k
```

While breaking the given array into `k` subsets, we can pick elements from any position.

An intuitive way to approach this problem is to build each subset by selecting one element at a time. Since the whole array has sum `totalArraySum`, as long as the current subset sum `currentSubsetSum` is less than `totalArraySum / k`, we can continue adding elements to the current subset.

- If `currentSubsetSum == totalArraySum / k`, we have completed one subset and can move on to the next.
- If `currentSubsetSum > totalArraySum / k`, this path cannot work, so we backtrack.

This leads naturally to a **backtracking** solution.

---

## What is Backtracking?

Backtracking is a recursive technique where we:

1. try a choice,
2. continue solving the smaller subproblem,
3. and if that choice does not lead to a solution, undo it and try another choice.

In this problem:

- we pick an element and try including it in the current subset,
- if that does not let us complete all valid subsets,
- we remove that element and try something else.

---

# Approach 1: Naive Backtracking

## Intuition

Our goal is to partition the array into `k` subsets of equal sum.

First, compute the total sum of the array. If it is not divisible by `k`, the answer is immediately `false`.

Otherwise, let:

```text
targetSum = totalArraySum / k
```

We use backtracking to build subsets one by one.

We maintain:

- `currSum` → current subset sum
- `count` → number of completed subsets
- `taken[]` → whether an element has already been used

### Important optimization

When `count == k - 1`, we can return `true`.

Why?

Because if we have already formed `k - 1` subsets with sum `targetSum`, the remaining elements must also sum to `targetSum`, since the total sum is divisible by `k`.

---

## Algorithm

1. Compute `totalArraySum`.
2. If `totalArraySum % k != 0`, return `false`.
3. Let `targetSum = totalArraySum / k`.
4. Use recursive backtracking:
   - If `count == k - 1`, return `true`.
   - If `currSum > targetSum`, return `false`.
   - If `currSum == targetSum`, start building the next subset.
   - Otherwise, try every element not yet taken.

---

## Implementation

```java
class Solution {
    private boolean backtrack(int[] arr, int count, int currSum, int k, int targetSum, boolean[] taken) {
        int n = arr.length;

        // We made k - 1 subsets with target sum and last subset will also have target sum.
        if (count == k - 1) {
            return true;
        }

        // Current subset sum exceeds target sum, no need to proceed further.
        if (currSum > targetSum) {
            return false;
        }

        // When current subset sum reaches target sum then one subset is made.
        // Increment count and reset current subset sum to 0.
        if (currSum == targetSum) {
            return backtrack(arr, count + 1, 0, k, targetSum, taken);
        }

        // Try not picked elements to make some combinations.
        for (int j = 0; j < n; ++j) {
            if (!taken[j]) {
                // Include this element in current subset.
                taken[j] = true;

                // If using current jth element in this subset leads to make all valid subsets.
                if (backtrack(arr, count, currSum + arr[j], k, targetSum, taken)) {
                    return true;
                }

                // Backtrack step.
                taken[j] = false;
            }
        }

        // We were not able to make a valid combination after picking each element from the array,
        // hence we can't make k subsets.
        return false;
    }

    public boolean canPartitionKSubsets(int[] arr, int k) {
        int totalArraySum = 0;
        int n = arr.length;

        for (int i = 0; i < n; ++i) {
             totalArraySum += arr[i];
        }

        // If total sum not divisible by k, we can't make subsets.
        if (totalArraySum % k != 0) {
            return false;
        }

        int targetSum = totalArraySum / k;
        boolean[] taken = new boolean[n];

        return backtrack(arr, 0, 0, k, targetSum, taken);
    }
}
```

---

## Complexity Analysis

Let `N` be the number of elements in the array.

### Time Complexity

```text
O(N · N!)
```

In the worst case, we try permutations of elements, and each recursive step scans the array.

### Space Complexity

```text
O(N)
```

- `taken[]` uses `O(N)`
- recursion depth is at most `O(N)`

---

# Approach 2: Optimized Backtracking

## Intuition

In the previous approach, every recursive call starts scanning the array again from index `0`, even though earlier elements may already be taken.

We can optimize by continuing the scan from the last chosen index.

This reduces redundant work.

### Additional optimization: sort in decreasing order

Sorting the array in decreasing order helps fail faster:

- large numbers are placed first,
- invalid branches are pruned earlier.

---

## Algorithm

1. Compute `totalArraySum`.
2. If not divisible by `k`, return `false`.
3. Sort the array in descending order.
4. Backtrack with:
   - `index` → where to continue searching from
   - `count` → completed subsets
   - `currSum` → current subset sum

---

## Implementation

```java
class Solution {
    private boolean backtrack(int[] arr, int index, int count, int currSum, int k,
                              int targetSum, boolean[] taken) {

        int n = arr.length;

        // We made k - 1 subsets with target sum and last subset will also have target sum.
        if (count == k - 1) {
            return true;
        }

        // No need to proceed further.
        if (currSum > targetSum) {
            return false;
        }

        // When curr sum reaches target then one subset is made.
        // Increment count and reset current sum.
        if (currSum == targetSum) {
            return backtrack(arr, 0, count + 1, 0, k, targetSum, taken);
        }

        // Try not picked elements to make some combinations.
        for (int j = index; j < n; ++j) {
            if (!taken[j]) {
                // Include this element in current subset.
                taken[j] = true;

                // If using current jth element in this subset leads to make all valid subsets.
                if (backtrack(arr, j + 1, count, currSum + arr[j], k, targetSum, taken)) {
                    return true;
                }

                // Backtrack step.
                taken[j] = false;
            }
        }

        // We were not able to make a valid combination after picking each element from the array,
        // hence we can't make k subsets.
        return false;
    }

    void reverse(int[] arr) {
        for (int i = 0, j = arr.length - 1; i < j; i++, j--) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public boolean canPartitionKSubsets(int[] arr, int k) {
        int totalArraySum = 0;
        int n = arr.length;

        for (int i = 0; i < n; ++i) {
             totalArraySum += arr[i];
        }

        // If total sum not divisible by k, we can't make subsets.
        if (totalArraySum % k != 0) {
            return false;
        }

        // Sort in decreasing order.
        Arrays.sort(arr);
        reverse(arr);

        int targetSum = totalArraySum / k;
        boolean[] taken = new boolean[n];

        return backtrack(arr, 0, 0, 0, k, targetSum, taken);
    }
}
```

---

## Complexity Analysis

Let `N` be the number of elements in the array and `k` the number of subsets.

### Time Complexity

```text
O(k · 2^N)
```

For each subset, we are effectively exploring include/exclude choices over the array.

### Space Complexity

```text
O(N)
```

- `taken[]` uses `O(N)`
- recursion stack is at most `O(N)`

---

# Approach 3: Backtracking + Memoization

## Intuition

Some recursive states repeat.

For example, if the same set of elements has already been chosen before and we know it cannot lead to a valid partition, then we do not need to recompute it.

So we memoize the result using the state of chosen elements.

In this version, we represent the chosen elements using a string of `'0'` and `'1'`.

- `'1'` → chosen
- `'0'` → not chosen

Then we store:

```text
Map<String, Boolean>
```

---

## Algorithm

1. Compute total sum and check divisibility by `k`.
2. Sort descending.
3. Use recursive backtracking.
4. Before solving a state, check if it is already in the memo map.
5. If yes, return cached result.

---

## Implementation

```java
class Solution {
    private boolean backtrack(int[] arr, int index, int count, int currSum, int k,
                              int targetSum, char[] taken, HashMap<String, Boolean> memo) {

        int n = arr.length;

        // We made k - 1 subsets with target sum and last subset will also have target sum.
        if (count == k - 1) {
            return true;
        }

        // No need to proceed further.
        if (currSum > targetSum) {
            return false;
        }

        String takenStr = new String(taken);

        // If we have already computed the current combination.
        if (memo.containsKey(takenStr)) {
            return memo.get(takenStr);
        }

        // When curr sum reaches target then one subset is made.
        // Increment count and reset current sum.
        if (currSum == targetSum) {
            boolean ans = backtrack(arr, 0, count + 1, 0, k, targetSum, taken, memo);
            memo.put(takenStr, ans);
            return ans;
        }

        // Try not picked elements to make some combinations.
        for (int j = index; j < n; ++j) {
            if (taken[j] == '0') {
                // Include this element in current subset.
                taken[j] = '1';

                // If using current jth element in this subset leads to make all valid subsets.
                if (backtrack(arr, j + 1, count, currSum + arr[j], k, targetSum, taken, memo)) {
                    return true;
                }

                // Backtrack step.
                taken[j] = '0';
            }
        }

        // We were not able to make a valid combination after picking each element from array,
        // hence we can't make k subsets.
        memo.put(takenStr, false);
        return false;
    }

    void reverse(int[] arr) {
        for (int i = 0, j = arr.length - 1; i < j; i++, j--) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public boolean canPartitionKSubsets(int[] arr, int k) {
        int totalArraySum = 0;
        int n = arr.length;

        for (int i = 0; i < n; ++i) {
             totalArraySum += arr[i];
        }

        // If total sum not divisible by k, we can't make subsets.
        if (totalArraySum % k != 0) {
            return false;
        }

        // Sort in decreasing order.
        Arrays.sort(arr);
        reverse(arr);

        int targetSum = totalArraySum / k;

        char[] taken = new char[n];
        for(int i = 0; i < n; ++i) {
            taken[i] = '0';
        }

        // Memoize the ans using taken element's string as key.
        HashMap<String, Boolean> memo = new HashMap<>();

        return backtrack(arr, 0, 0, 0, k, targetSum, taken, memo);
    }
}
```

---

## Complexity Analysis

Let `N` be the number of elements in the array.

### Time Complexity

```text
O(N · 2^N)
```

There are at most `2^N` unique `taken` states, and each state may scan the array.

### Space Complexity

```text
O(N · 2^N)
```

- up to `2^N` strings of length `N` in memo
- plus recursion stack

---

# Approach 4: Backtracking + Memoization with Bitmasking

## Intuition

Instead of storing the chosen-state as a string, we can store it as an integer bitmask.

Since `nums.length <= 16`, this is perfect.

- bit `i = 1` → element at index `i` is chosen
- bit `i = 0` → element at index `i` is not chosen

This is more efficient than strings.

---

## Useful Bitmask Operations

### Check if bit `i` is set

```java
(mask >> i) & 1
```

### Set bit `i`

```java
mask | (1 << i)
```

### Unset bit `i`

```java
mask ^ (1 << i)
```

---

## Algorithm

Same as Approach 3, except:

- replace string state with integer `mask`
- memo becomes:

```text
HashMap<Integer, Boolean>
```

---

## Implementation

```java
class Solution {
    private boolean backtrack(int[] arr, int index, int count, int currSum, int k,
                              int targetSum, Integer mask, HashMap<Integer, Boolean> memo) {

        int n = arr.length;

        // We made k - 1 subsets with target sum and last subset will also have target sum.
        if (count == k - 1) {
            return true;
        }

        // No need to proceed further.
        if (currSum > targetSum) {
            return false;
        }

        // If we have already computed the current combination.
        if (memo.containsKey(mask)) {
            return memo.get(mask);
        }

        // When curr sum reaches target then one subset is made.
        // Increment count and reset current sum.
        if (currSum == targetSum) {
            boolean ans = backtrack(arr, 0, count + 1, 0, k, targetSum, mask, memo);
            memo.put(mask, ans);
            return ans;
        }

        // Try not picked elements to make some combinations.
        for (int j = index; j < n; ++j) {
            if (((mask >> j) & 1) == 0) {
                // Include this element in current subset.
                mask = (mask | (1 << j));

                // If using current jth element in this subset leads to make all valid subsets.
                if (backtrack(arr, j + 1, count, currSum + arr[j], k, targetSum, mask, memo)) {
                    return true;
                }

                // Backtrack step.
                mask = (mask ^ (1 << j));
            }
        }

        // We were not able to make a valid combination after picking each element from the array,
        // hence we can't make k subsets.
        memo.put(mask, false);
        return false;
    }

    void reverse(int[] arr) {
        for (int i = 0, j = arr.length - 1; i < j; i++, j--) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public boolean canPartitionKSubsets(int[] arr, int k) {
        int totalArraySum = 0;
        int n = arr.length;

        for (int i = 0; i < n; ++i) {
             totalArraySum += arr[i];
        }

        // If total sum not divisible by k, we can't make subsets.
        if (totalArraySum % k != 0) {
            return false;
        }

        // Sort in decreasing order.
        Arrays.sort(arr);
        reverse(arr);

        int targetSum = totalArraySum / k;
        Integer mask = 0;

        // Memoize the ans using taken element's string as key.
        HashMap<Integer, Boolean> memo = new HashMap<>();

        return backtrack(arr, 0, 0, 0, k, targetSum, mask, memo);
    }
}
```

---

## Complexity Analysis

Let `N` be the number of elements in the array.

### Time Complexity

```text
O(N · 2^N)
```

### Space Complexity

```text
O(2^N)
```

- memo stores up to `2^N` masks
- recursion stack is `O(N)` extra

---

# Approach 5: Tabulation + Bitmasking

## Intuition

This is the bottom-up DP version of the bitmask memoization idea.

We define:

```text
subsetSum[mask]
```

as the current subset sum modulo `targetSum` for this mask.

If:

```text
subsetSum[mask] == -1
```

then this state is unreachable.

### Key idea

When we add a new element:

- if it does not exceed `targetSum`, we transition to the new mask
- and update:

```text
subsetSum[newMask] = (subsetSum[mask] + arr[i]) % targetSum
```

Why modulo?

- if the current subset reaches exactly `targetSum`, modulo resets it to `0`
- which means we start filling the next subset

At the end, if:

```text
subsetSum[(1 << n) - 1] == 0
```

then all elements have been partitioned into valid subsets.

---

## Algorithm

1. Compute total sum and verify divisibility by `k`.
2. Let `targetSum = totalArraySum / k`.
3. Create DP array:

```java
subsetSum[1 << n]
```

Initialize all states to `-1`, except:

```java
subsetSum[0] = 0
```

4. Iterate over all masks:
   - if state is reachable,
   - try adding every unpicked element that does not exceed `targetSum`.

5. Check final mask.

---

## Implementation

```java
class Solution {
    public boolean canPartitionKSubsets(int[] arr, int k) {
        int totalArraySum = 0;
        int n = arr.length;

        for (int i = 0; i < n; ++i) {
            totalArraySum += arr[i];
        }

        // If total sum not divisible by k, we can't make subsets.
        if (totalArraySum % k != 0) {
            return false;
        }

        int targetSum = totalArraySum / k;

        int[] subsetSum = new int[(1 << n)];
        for (int i = 0; i < (1 << n); ++i) {
            subsetSum[i] = -1;
        }
        // Initially only one state is valid, i.e don't pick anything.
        subsetSum[0] = 0;

        for (int mask = 0; mask < (1 << n); mask++) {
            // If the current state has not been reached earlier.
            if (subsetSum[mask] == -1) {
                continue;
            }

            for (int i = 0; i < n; i++) {
                // If the number arr[i] was not picked earlier, and arr[i] + subsetSum[mask]
                // is not greater than the targetSum then add arr[i] to the subset
                // sum at subsetSum[mask] and store the result at subsetSum[mask | (1 << i)].
                if ((mask & (1 << i)) == 0 && subsetSum[mask] + arr[i] <= targetSum) {
                    subsetSum[mask | (1 << i)] = (subsetSum[mask] + arr[i]) % targetSum;
                }
            }

            if (subsetSum[(1 << n) - 1] == 0) {
                return true;
            }
        }

        return subsetSum[(1 << n) - 1] == 0;
    }
}
```

---

## Complexity Analysis

Assume `N` is the number of elements in the array.

### Time Complexity

```text
O(N · 2^N)
```

There are `2^N` mask states, and each state scans all `N` elements.

### Space Complexity

```text
O(2^N)
```

The DP array stores one value for each mask.

---

# Summary

| Approach                   | Core Idea                                       |         Time |        Space |
| -------------------------- | ----------------------------------------------- | -----------: | -----------: |
| Naive Backtracking         | Try all subset constructions                    |  `O(N · N!)` |       `O(N)` |
| Optimized Backtracking     | Continue from last index + sort descending      | `O(k · 2^N)` |       `O(N)` |
| Backtracking + Memoization | Cache repeated chosen-element states as strings | `O(N · 2^N)` | `O(N · 2^N)` |
| Memoization + Bitmask      | Same as above, but state is an integer mask     | `O(N · 2^N)` |     `O(2^N)` |
| Tabulation + Bitmask       | Bottom-up DP over masks                         | `O(N · 2^N)` |     `O(2^N)` |

---

# Final Insight

The most important ideas in this problem are:

1. **The total sum must be divisible by `k`.**
2. **Backtracking is the natural starting point.**
3. **Sorting in descending order helps prune invalid paths early.**
4. **The chosen/un-chosen state of elements is what really defines the subproblem.**
5. **Bitmasking is the cleanest and most efficient way to represent that state.**
