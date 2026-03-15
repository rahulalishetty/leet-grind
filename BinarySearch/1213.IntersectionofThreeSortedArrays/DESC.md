# 1213. Intersection of Three Sorted Arrays

## Problem Description

Given three integer arrays:

- `arr1`
- `arr2`
- `arr3`

All arrays are:

- **sorted**
- **strictly increasing**

Return a **sorted array** containing only the integers that appear in **all three arrays**.

---

## Example 1

```
Input:
arr1 = [1,2,3,4,5]
arr2 = [1,2,5,7,9]
arr3 = [1,3,4,5,8]

Output:
[1,5]
```

### Explanation

Only the following numbers appear in **all three arrays**:

```
1
5
```

---

## Example 2

```
Input:
arr1 = [197,418,523,876,1356]
arr2 = [501,880,1593,1710,1870]
arr3 = [521,682,1337,1395,1764]

Output:
[]
```

### Explanation

There are **no common elements** across all three arrays.

---

## Constraints

```
1 <= arr1.length, arr2.length, arr3.length <= 1000
```

```
1 <= arr1[i], arr2[i], arr3[i] <= 2000
```
