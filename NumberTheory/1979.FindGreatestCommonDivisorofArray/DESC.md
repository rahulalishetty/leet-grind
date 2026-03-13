# 1979. Find Greatest Common Divisor of Array

## Problem Statement

Given an integer array **nums**, return the **greatest common divisor (GCD)** of the **smallest number** and the **largest number** in the array.

The **greatest common divisor (GCD)** of two integers is the **largest positive integer that divides both numbers without leaving a remainder**.

---

## Examples

### Example 1

**Input**

```
nums = [2,5,6,9,10]
```

**Output**

```
2
```

**Explanation**

- Smallest number in `nums` = **2**
- Largest number in `nums` = **10**
- `gcd(2,10) = 2`

---

### Example 2

**Input**

```
nums = [7,5,6,8,3]
```

**Output**

```
1
```

**Explanation**

- Smallest number in `nums` = **3**
- Largest number in `nums` = **8**
- `gcd(3,8) = 1`

---

### Example 3

**Input**

```
nums = [3,3]
```

**Output**

```
3
```

**Explanation**

- Smallest number in `nums` = **3**
- Largest number in `nums` = **3**
- `gcd(3,3) = 3`

---

## Constraints

```
2 <= nums.length <= 1000
1 <= nums[i] <= 1000
```

---
