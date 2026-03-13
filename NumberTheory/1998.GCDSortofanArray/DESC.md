# 1998. GCD Sort of an Array

You are given an integer array `nums`, and you can perform the following operation any number of times on `nums`:

Swap the positions of two elements `nums[i]` and `nums[j]` **if**:

```
gcd(nums[i], nums[j]) > 1
```

where `gcd(nums[i], nums[j])` is the **greatest common divisor** of the two numbers.

Your task is to determine whether it is possible to **sort the array in non-decreasing order** using the above swap operation any number of times.

Return:

- `true` if the array **can be sorted**
- `false` otherwise

---

# Example 1

### Input

```
nums = [7,21,3]
```

### Output

```
true
```

### Explanation

We can sort `[7,21,3]` by performing the following operations:

1. Swap **7 and 21** because `gcd(7,21) = 7`

```
[21,7,3]
```

2. Swap **21 and 3** because `gcd(21,3) = 3`

```
[3,7,21]
```

The array is now sorted.

---

# Example 2

### Input

```
nums = [5,2,6,2]
```

### Output

```
false
```

### Explanation

`5` cannot be swapped with any other number because:

```
gcd(5,2) = 1
gcd(5,6) = 1
```

Since no valid swaps exist involving `5`, the array cannot be sorted.

---

# Example 3

### Input

```
nums = [10,5,9,3,15]
```

### Output

```
true
```

### Explanation

We can sort the array as follows:

1. Swap **10 and 15** because `gcd(10,15) = 5`

```
[15,5,9,3,10]
```

2. Swap **15 and 3** because `gcd(15,3) = 3`

```
[3,5,9,15,10]
```

3. Swap **10 and 15** because `gcd(10,15) = 5`

```
[3,5,9,10,15]
```

The array becomes sorted.

---

# Constraints

```
1 <= nums.length <= 3 * 10^4
2 <= nums[i] <= 10^5
```
