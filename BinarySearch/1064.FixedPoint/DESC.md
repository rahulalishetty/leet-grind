# 1064. Fixed Point

## Problem Description

Given an array of **distinct integers** `arr`, where `arr` is **sorted in ascending order**, return the **smallest index `i`** that satisfies:

```
arr[i] == i
```

If no such index exists, return:

```
-1
```

---

## Example 1

```
Input: arr = [-10,-5,0,3,7]
Output: 3
```

Explanation:

```
arr[0] = -10
arr[1] = -5
arr[2] = 0
arr[3] = 3
arr[4] = 7
```

Here:

```
arr[3] == 3
```

So the answer is:

```
3
```

---

## Example 2

```
Input: arr = [0,2,5,8,17]
Output: 0
```

Explanation:

```
arr[0] == 0
```

Therefore the smallest index satisfying the condition is:

```
0
```

---

## Example 3

```
Input: arr = [-10,-5,3,4,7,9]
Output: -1
```

Explanation:

No index `i` satisfies:

```
arr[i] == i
```

Therefore the result is:

```
-1
```

---

## Constraints

```
1 <= arr.length < 10^4
-10^9 <= arr[i] <= 10^9
```

- The array elements are **distinct**
- The array is **sorted in ascending order**

---

## Follow Up

The **O(n)** solution is straightforward.

Can you design a solution with **better time complexity**?
