# 2659. Make Array Empty

## Problem Statement

You are given an integer array `nums` containing **distinct numbers**, and you can perform the following operations until the array becomes empty:

1. **If the first element has the smallest value in the array, remove it.**
2. **Otherwise, move the first element to the end of the array.**

Return the **number of operations** required to make the array empty.

---

## Example 1

**Input**

```
nums = [3,4,-1]
```

**Output**

```
5
```

**Explanation**

| Operation | Array      |
| --------- | ---------- |
| 1         | [4, -1, 3] |
| 2         | [-1, 3, 4] |
| 3         | [3, 4]     |
| 4         | [4]        |
| 5         | []         |

---

## Example 2

**Input**

```
nums = [1,2,4,3]
```

**Output**

```
5
```

**Explanation**

| Operation | Array     |
| --------- | --------- |
| 1         | [2, 4, 3] |
| 2         | [4, 3]    |
| 3         | [3, 4]    |
| 4         | [4]       |
| 5         | []        |

---

## Example 3

**Input**

```
nums = [1,2,3]
```

**Output**

```
3
```

**Explanation**

| Operation | Array  |
| --------- | ------ |
| 1         | [2, 3] |
| 2         | [3]    |
| 3         | []     |

---

## Constraints

- `1 <= nums.length <= 10^5`
- `-10^9 <= nums[i] <= 10^9`
- All values in `nums` are **distinct**.
