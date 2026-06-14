# 213. House Robber II

## Problem

You are a professional robber planning to rob houses arranged in a **circle**.
Each house has some money, but adjacent houses cannot be robbed together.

Since the first and last houses are adjacent, you **cannot rob both**.

Return the maximum money you can rob.

---

## Key Insight

Convert circular problem → two linear problems:

1. Exclude last house → solve `[0 ... n-2]`
2. Exclude first house → solve `[1 ... n-1]`

Final answer:

```
max(rob(0 → n-2), rob(1 → n-1))
```

---

## Edge Case

```
if (n == 1) return nums[0];
```

---

## Java Solution (Optimized)

```java
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;

        if (n == 1) return nums[0];

        return Math.max(
            robLinear(nums, 0, n - 2),
            robLinear(nums, 1, n - 1)
        );
    }

    private int robLinear(int[] nums, int start, int end) {
        int prev2 = 0;
        int prev1 = 0;

        for (int i = start; i <= end; i++) {
            int curr = Math.max(prev1, nums[i] + prev2);
            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }
}
```

---

## Complexity

- Time: `O(n)`
- Space: `O(1)`

---

## Example

```
nums = [2,3,2]

Case 1: [2,3] → 3
Case 2: [3,2] → 3

Answer = 3
```

---

## Takeaway

Reduce circular constraint → two linear DP problems.
