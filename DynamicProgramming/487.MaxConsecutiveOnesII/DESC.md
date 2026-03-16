# 487. Max Consecutive Ones II

## Problem Statement

Given a binary array `nums`, return the **maximum number of consecutive 1's** in the array **if you can flip at most one `0`**.

---

## Example 1

**Input**

```
nums = [1,0,1,1,0]
```

**Output**

```
4
```

**Explanation**

- Flip the first `0` → `[1,1,1,1,0]` → longest streak = **4**
- Flip the second `0` → `[1,0,1,1,1]` → longest streak = **3**

Maximum = **4**.

---

## Example 2

**Input**

```
nums = [1,0,1,1,0,1]
```

**Output**

```
4
```

**Explanation**

- Flip the first `0` → `[1,1,1,1,0,1]` → longest streak = **4**
- Flip the second `0` → `[1,0,1,1,1,1]` → longest streak = **4**

Maximum = **4**.

---

## Constraints

- `1 <= nums.length <= 10^5`
- `nums[i]` is either `0` or `1`

---

## Follow-up

What if the numbers come in **one by one as an infinite stream**?

- You cannot store the entire array.
- The algorithm must work using **constant memory**.

---

## Key Idea

You are allowed to flip **at most one `0`**.

So the problem becomes:

> Find the longest subarray containing **at most one zero**.

This is a classic **sliding window** problem.

---

## Sliding Window Intuition

Maintain a window `[left, right]`.

Inside the window we allow **at most one `0`**.

If the window contains **more than one `0`**, we move the `left` pointer until the condition is restored.

The window length represents the number of elements that can be treated as `1`s after flipping at most one `0`.

---

## Algorithm

1. Initialize two pointers:
   - `left = 0`
   - `right = 0`

2. Track the number of zeros in the window.

3. Expand `right` pointer:
   - If `nums[right] == 0`, increase zero count.

4. If zero count becomes greater than `1`:
   - Move `left` pointer until zero count becomes `1` again.

5. Update maximum window length.

---

## Java Implementation

```java
class Solution {
    public int findMaxConsecutiveOnes(int[] nums) {
        int left = 0;
        int zeros = 0;
        int maxLength = 0;

        for (int right = 0; right < nums.length; right++) {

            if (nums[right] == 0) {
                zeros++;
            }

            while (zeros > 1) {
                if (nums[left] == 0) {
                    zeros--;
                }
                left++;
            }

            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(n)
```

Each element is visited at most twice (once by `right`, once by `left`).

### Space Complexity

```
O(1)
```

Only a few variables are used.

---

## Follow-up: Infinite Stream Version

If the data arrives **as a stream**, we cannot store the entire array.

But the sliding window technique still works because it only needs:

- the position of the **last zero**
- the current window size

### Idea

Track:

- `prevZeroIndex`
- `current window length`

When a second zero appears, shift the window after the previous zero.

---

## Stream-Friendly Java Solution

```java
class Solution {

    public int findMaxConsecutiveOnes(int[] nums) {
        int prevZero = -1;
        int left = 0;
        int max = 0;

        for (int right = 0; right < nums.length; right++) {

            if (nums[right] == 0) {
                left = prevZero + 1;
                prevZero = right;
            }

            max = Math.max(max, right - left + 1);
        }

        return max;
    }
}
```

---

## Why This Works

At any moment the window contains **at most one zero**.

When another `0` appears:

- the window start moves just after the previous zero
- ensuring only one zero remains in the window

This approach uses **constant memory**, making it suitable for streaming inputs.

---

## Summary

| Technique      | Idea                             | Time | Space |
| -------------- | -------------------------------- | ---- | ----- |
| Sliding Window | Allow at most one zero in window | O(n) | O(1)  |
| Stream Version | Track last zero position         | O(n) | O(1)  |

Key observation:

> The problem reduces to finding the longest subarray with **at most one zero**.

Sliding window naturally solves this in linear time.
