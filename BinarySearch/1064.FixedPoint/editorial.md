# 1064. Fixed Point — Overview and Binary Search Approach

## Overview

In an interview setting, it is important to clarify the **problem constraints**, because they often hint at the expected time complexity.

In this problem:

```
1 <= arr.length < 10^4
```

In most programming environments, an algorithm performing up to about **10^8 operations** is acceptable.

Therefore, a simple **O(N)** solution would pass the constraints.

### Linear Search Idea

We can iterate through the array from left to right and check:

```
arr[i] == i
```

The first index that satisfies this condition is the answer.

Example:

```
arr = [-10, -5, 0, 3, 7]
```

```
i = 0 → arr[0] = -10
i = 1 → arr[1] = -5
i = 2 → arr[2] = 0
i = 3 → arr[3] = 3  ← match
```

Answer:

```
3
```

### Complexity

Time complexity:

```
O(N)
```

Space complexity:

```
O(1)
```

Although this solution works, interviewers usually ask for a **better optimized solution**.

---

# Approach 1: Binary Search

## Intuition

The array is:

- **sorted**
- **contains distinct integers**

Whenever data is **sorted**, binary search should always be considered.

Binary search works by repeatedly **halving the search space**.

---

## Binary Search Framework

Typical binary search involves:

1. Define the search range `[left, right]`
2. Compute the midpoint

```
mid = (left + right) / 2
```

3. Determine whether `mid` is the answer.
4. Reduce the search space to half.

---

## Applying Binary Search to This Problem

The answer must be an **index of the array**.

Therefore:

```
left = 0
right = N - 1
```

### Three Cases

#### Case 1

```
arr[mid] == mid
```

This is a **valid fixed point**.

But we want the **smallest index**, so we continue searching on the **left side**.

```
answer = mid
right = mid - 1
```

---

#### Case 2

```
arr[mid] < mid
```

This means the array value is **too small**.

All elements on the **left side will also be too small**, because the array is sorted.

Therefore the solution **cannot exist on the left side**.

Move to the right:

```
left = mid + 1
```

---

#### Case 3

```
arr[mid] > mid
```

The value is **too large**.

Because the array is sorted, values on the **right side will be even larger**.

So the solution **cannot exist on the right side**.

Move left:

```
right = mid - 1
```

---

## Algorithm

1. Initialize:

```
left = 0
right = N - 1
answer = -1
```

2. While:

```
left <= right
```

3. Compute midpoint:

```
mid = (left + right) / 2
```

4. Compare:

```
arr[mid] == mid
```

Store answer and search left.

```
answer = mid
right = mid - 1
```

5. If:

```
arr[mid] < mid
```

Move right.

```
left = mid + 1
```

6. If:

```
arr[mid] > mid
```

Move left.

```
right = mid - 1
```

7. Return `answer`.

---

## Implementation

```java
class Solution {
    public int fixedPoint(int[] arr) {

        int left = 0, right = arr.length - 1;
        int answer = -1;

        while (left <= right) {

            int mid = (left + right) / 2;

            if (arr[mid] == mid) {
                answer = mid;
                right = mid - 1;
            }
            else if (arr[mid] < mid) {
                left = mid + 1;
            }
            else {
                right = mid - 1;
            }
        }

        return answer;
    }
}
```

---

# Complexity Analysis

Let **N** be the size of the array.

### Time Complexity

```
O(log N)
```

Each iteration halves the search space:

```
N → N/2 → N/4 → N/8 → ... → 1
```

This requires **log N iterations**.

---

### Space Complexity

```
O(1)
```

Binary search uses only a few variables and requires **constant extra space**.
