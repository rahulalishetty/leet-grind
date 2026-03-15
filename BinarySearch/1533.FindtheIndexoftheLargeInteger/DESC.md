# Create a Markdown file for the problem statement

import pypandoc

content = """

# 1533. Find the Index of the Large Integer

## Problem Description

We have an integer array `arr`, where **all integers are equal except for one integer that is larger than the rest**.

You **do not have direct access** to the array. Instead, you must interact with it using an **API called `ArrayReader`**.

---

## ArrayReader API

### compareSub

```
int compareSub(int l, int r, int x, int y)
```

Where:

```
0 <= l, r, x, y < ArrayReader.length()
l <= r
x <= y
```

This function compares the **sum of two subarrays**:

```
arr[l..r] vs arr[x..y]
```

Return values:

```
1  -> sum(arr[l..r]) > sum(arr[x..y])
0  -> sums are equal
-1 -> sum(arr[l..r]) < sum(arr[x..y])
```

---

### length

```
int length()
```

Returns the **size of the array**.

---

## Important Constraint

You are allowed to call:

```
compareSub() at most 20 times
```

Both API functions run in **O(1) time**.

---

## Goal

Return the **index of the element that is larger than the rest**.

---

## Example 1

Input:

```
arr = [7,7,7,7,10,7,7,7]
```

Output:

```
4
```

Explanation:

Example API calls:

```
reader.compareSub(0,0,1,1) -> 0
```

This compares:

```
arr[0] vs arr[1]
```

Since they are equal, neither is the largest.

```
reader.compareSub(2,2,3,3) -> 0
```

Thus `arr[2]` and `arr[3]` are also not the largest.

```
reader.compareSub(4,4,5,5) -> 1
```

This shows:

```
arr[4] > arr[5]
```

Therefore:

```
index 4 contains the largest element
```

Only **3 API calls** were used.

---

## Example 2

Input:

```
arr = [6,6,12]
```

Output:

```
2
```

---

## Constraints

```
2 <= arr.length <= 5 * 10^5
```

```
1 <= arr[i] <= 100
```

All elements are **equal except one element which is larger**.

---

## Follow-up Questions

1. What if **two numbers** in the array are larger than the rest?
2. What if there is **one number larger** and **one number smaller** than the rest?
   """

path = "/mnt/data/find_index_large_integer_1533.md"

pypandoc.convert_text(content, "md", format="md", outputfile=path, extra_args=["--standalone"])

path
