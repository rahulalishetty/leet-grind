# Minimum Cost to Reach the Last Index — Detailed Explanation

## Problem Statement

You are given:

- A **0-indexed integer array `nums`** of length `n`
- A **0-indexed integer array `costs`** of length `n`

You start at **index `0`**.

You may **jump from index `i` to index `j` (`i < j`)** if one of the following conditions holds.

### Condition 1

```
nums[i] <= nums[j]
and
nums[k] < nums[i] for every k where i < k < j
```

### Condition 2

```
nums[i] > nums[j]
and
nums[k] >= nums[i] for every k where i < k < j
```

When you **land on index `j`**, you pay:

```
costs[j]
```

Your goal is to compute the **minimum total cost to reach index `n - 1`**.

---

# Key Insight

This is a **dynamic programming + monotonic stack** problem.

The DP part computes the **minimum cost to reach each index**.

The stack part helps us efficiently determine which previous indices can jump to the current index.

---

# Dynamic Programming Formulation

Let:

```
dp[i] = minimum cost required to reach index i
```

Base case:

```
dp[0] = 0
```

For every other index:

```
dp[i] = min(dp[j] + costs[i])  for every valid jump j -> i
```

The main difficulty is efficiently finding all valid `j` for each `i`.

---

# Understanding the Jump Conditions

## Condition 1

```
nums[j] <= nums[i]
and all nums[k] < nums[j] for j < k < i
```

This means:

- while moving from `j` to `i`, we never encounter a value ≥ `nums[j]`

This naturally corresponds to a **monotonic decreasing stack**.

---

## Condition 2

```
nums[j] > nums[i]
and all nums[k] >= nums[j] for j < k < i
```

This means:

- while moving from `j` to `i`, we never encounter a value < `nums[j]`

This corresponds to a **monotonic increasing stack**.

---

# Why Monotonic Stacks Work

The stacks maintain candidate indices that may still produce valid jumps.

When we encounter a new index `i`, some earlier indices become invalid and can be popped.

Each popped index represents a **valid jump source**.

Each index is pushed and popped **at most once**, giving **O(n)** time complexity.

---

# Algorithm

Maintain two stacks:

```
dec = monotonic decreasing stack
inc = monotonic increasing stack
```

Steps:

1. Initialize:

```
dp[0] = 0
push 0 into both stacks
```

2. For each index `i` from `1` to `n-1`:

### Process decreasing stack

While:

```
nums[dec.top()] <= nums[i]
```

Pop `j` and update:

```
dp[i] = min(dp[i], dp[j] + costs[i])
```

### Process increasing stack

While:

```
nums[inc.top()] > nums[i]
```

Pop `j` and update:

```
dp[i] = min(dp[i], dp[j] + costs[i])
```

3. Push `i` into both stacks.

4. Continue until the end.

5. Return:

```
dp[n - 1]
```

---

# Correct Java Implementation

```java
import java.util.*;

class Solution {
    public long minCost(int[] nums, int[] costs) {
        int n = nums.length;

        long[] dp = new long[n];
        Arrays.fill(dp, Long.MAX_VALUE);
        dp[0] = 0;

        Deque<Integer> dec = new ArrayDeque<>();
        Deque<Integer> inc = new ArrayDeque<>();

        dec.push(0);
        inc.push(0);

        for (int i = 1; i < n; i++) {

            while (!dec.isEmpty() && nums[dec.peek()] <= nums[i]) {
                int j = dec.pop();
                dp[i] = Math.min(dp[i], dp[j] + costs[i]);
            }

            while (!inc.isEmpty() && nums[inc.peek()] > nums[i]) {
                int j = inc.pop();
                dp[i] = Math.min(dp[i], dp[j] + costs[i]);
            }

            dec.push(i);
            inc.push(i);
        }

        return dp[n - 1];
    }
}
```

---

# Example Walkthrough

```
nums  = [3,2,4,4,1]
costs = [3,7,6,4,2]
```

Start:

```
dp = [0, ∞, ∞, ∞, ∞]
```

### i = 1

Possible jump:

```
0 -> 1
```

Cost:

```
dp[1] = 0 + 7 = 7
```

---

### i = 2

Possible jumps:

```
1 -> 2
0 -> 2
```

Minimum:

```
dp[2] = 6
```

---

### i = 3

Possible jump:

```
2 -> 3
```

```
dp[3] = 10
```

---

### i = 4

Possible jumps:

```
3 -> 4
2 -> 4
1 -> 4
```

Minimum:

```
dp[4] = 8
```

Final answer:

```
8
```

---

# Complexity Analysis

## Time Complexity

```
O(n)
```

Reason:

- each index is pushed once
- each index is popped once
- stack operations are amortized constant time

---

## Space Complexity

```
O(n)
```

Used by:

- DP array
- two stacks

---

# Key Observations

### 1. Dynamic Programming

We model the problem as a **minimum cost path to each index**.

### 2. Monotonic Stacks

The jump conditions translate directly into **monotonic stack structures**.

### 3. Linear Complexity

Without stacks the solution would be **O(n²)**.
The stack trick reduces it to **O(n)**.

---

# Summary

| Component        | Idea                                    |
| ---------------- | --------------------------------------- |
| DP               | `dp[i]` = min cost to reach index `i`   |
| Decreasing Stack | handles jumps where `nums[j] ≤ nums[i]` |
| Increasing Stack | handles jumps where `nums[j] > nums[i]` |
| Time Complexity  | `O(n)`                                  |
| Space Complexity | `O(n)`                                  |

---

# Final Takeaway

This problem combines:

- **dynamic programming**
- **monotonic stack optimization**

to efficiently compute the minimum cost path through constrained jumps.

Recognizing the hidden **monotonic patterns** in the jump rules is the key insight that enables the linear-time solution.
