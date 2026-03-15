# 1746. Maximum Subarray Sum After One Operation

## Problem Description

You are given an integer array `nums`.

You must perform **exactly one operation** where you can replace one element:

```
nums[i] -> nums[i] * nums[i]
```

After performing the operation, return the **maximum possible subarray sum**.

### Rules

- The subarray must be **non-empty**.
- The squaring operation must be used **exactly once**.

---

# Examples

## Example 1

**Input**

```
nums = [2,-1,-4,-3]
```

**Output**

```
17
```

**Explanation**

Perform the operation on index `2`:

```
-4 → 16
```

New array:

```
[2,-1,16,-3]
```

Maximum subarray:

```
2 + (-1) + 16 = 17
```

---

## Example 2

**Input**

```
nums = [1,-1,1,1,-1,-1,1]
```

**Output**

```
4
```

**Explanation**

Perform the operation on index `1`:

```
-1 → 1
```

New array:

```
[1,1,1,1,-1,-1,1]
```

Maximum subarray:

```
1 + 1 + 1 + 1 = 4
```

---

# Constraints

```
1 <= nums.length <= 10^5
-10^4 <= nums[i] <= 10^4
```
