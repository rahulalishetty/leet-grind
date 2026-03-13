# 2521. Distinct Prime Factors of Product of Array

You are given an array of positive integers `nums`.

Return the **number of distinct prime factors** in the **product of all elements of nums**.

---

# Definitions

### Prime Number

A number greater than **1** is called **prime** if it is divisible only by:

```
1 and itself
```

### Factor

An integer `val1` is a **factor** of another integer `val2` if:

```
val2 / val1 is an integer
```

---

# Key Idea

Instead of computing the **entire product**, which may become extremely large, we can:

1. **Factorize each number in the array**
2. Collect all **prime factors**
3. Store them in a **set**
4. Return the **size of the set**

Because:

```
Prime factors of product(nums) =
Union of prime factors of each nums[i]
```

---

# Example 1

## Input

```
nums = [2,4,3,7,10,6]
```

## Product

```
2 * 4 * 3 * 7 * 10 * 6 = 10080
```

Prime factorization:

```
10080 = 2^5 * 3^2 * 5 * 7
```

Distinct prime factors:

```
2, 3, 5, 7
```

## Output

```
4
```

---

# Example 2

## Input

```
nums = [2,4,8,16]
```

## Product

```
2 * 4 * 8 * 16 = 1024
```

Prime factorization:

```
1024 = 2^10
```

Distinct prime factors:

```
2
```

## Output

```
1
```

---

# Constraints

```
1 <= nums.length <= 10^4
2 <= nums[i] <= 1000
```

---

# Problem Goal

Determine how many **unique prime numbers** appear in the **prime factorization of the product of all numbers in the array**.
