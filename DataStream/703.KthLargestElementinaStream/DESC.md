# 703. Kth Largest Element in a Stream

You are part of a university admissions office and need to keep track of the **kth highest test score** from applicants in real-time. This helps determine cut-off marks for interviews and admissions dynamically as new applicants submit their scores.

You must implement a class that continuously maintains the **kth largest score** as new scores arrive.

---

# Problem Description

Given:

- An integer `k`
- A stream of scores `nums`

The system should maintain the **kth largest element** in the sorted list of all scores seen so far.

---

# Class Specification

Implement the class:

```
KthLargest
```

---

## Constructor

```
KthLargest(int k, int[] nums)
```

Initializes the object with:

- `k` → the rank of the score to maintain
- `nums` → initial stream of scores

---

## Method

```
int add(int val)
```

Adds a new score `val` to the stream and returns the **kth largest score** among all scores seen so far.

---

# Example 1

## Input

```
["KthLargest", "add", "add", "add", "add", "add"]
[[3, [4,5,8,2]], [3], [5], [10], [9], [4]]
```

## Output

```
[null, 4, 5, 5, 8, 8]
```

## Explanation

```
KthLargest kthLargest = new KthLargest(3, [4,5,8,2]);
```

Sorted stream:

```
[2,4,5,8]
```

3rd largest = **4**

---

### Step 1

```
add(3)
```

Stream:

```
[2,3,4,5,8]
```

3rd largest:

```
4
```

---

### Step 2

```
add(5)
```

Stream:

```
[2,3,4,5,5,8]
```

3rd largest:

```
5
```

---

### Step 3

```
add(10)
```

Stream:

```
[2,3,4,5,5,8,10]
```

3rd largest:

```
5
```

---

### Step 4

```
add(9)
```

Stream:

```
[2,3,4,5,5,8,9,10]
```

3rd largest:

```
8
```

---

### Step 5

```
add(4)
```

Stream:

```
[2,3,4,4,5,5,8,9,10]
```

3rd largest:

```
8
```

---

# Example 2

## Input

```
["KthLargest", "add", "add", "add", "add"]
[[4, [7,7,7,7,8,3]], [2], [10], [9], [9]]
```

## Output

```
[null,7,7,7,8]
```

---

# Constraints

```
0 <= nums.length <= 10^4
1 <= k <= nums.length + 1
-10^4 <= nums[i] <= 10^4
-10^4 <= val <= 10^4
```

Maximum calls:

```
10^4 calls to add()
```
