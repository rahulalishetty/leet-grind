# 1655. Distribute Repeating Integers

## Problem Description

You are given:

- An array **nums** of `n` integers.
- An array **quantity** of `m` customer order quantities.

Where:

- `quantity[i]` represents how many integers the **i-th customer** wants.

### Distribution Rules

You must determine whether it is possible to distribute the integers such that:

1. The **i-th customer receives exactly `quantity[i]` integers**.
2. All integers given to a single customer must be **equal**.
3. Every customer must be **satisfied**.

Return:

```
true  -> if distribution is possible
false -> otherwise
```

---

# Example 1

### Input

```
nums = [1,2,3,4]
quantity = [2]
```

### Output

```
false
```

### Explanation

The customer needs **2 identical integers**, but every number in `nums` is unique.

---

# Example 2

### Input

```
nums = [1,2,3,3]
quantity = [2]
```

### Output

```
true
```

### Explanation

Customer receives:

```
[3,3]
```

Remaining numbers `[1,2]` are unused.

---

# Example 3

### Input

```
nums = [1,1,2,2]
quantity = [2,2]
```

### Output

```
true
```

### Explanation

Possible distribution:

```
Customer 0 -> [1,1]
Customer 1 -> [2,2]
```

---

# Constraints

```
n == nums.length
1 <= n <= 10^5
```

```
1 <= nums[i] <= 1000
```

```
m == quantity.length
1 <= m <= 10
```

```
1 <= quantity[i] <= 10^5
```

```
There are at most 50 unique values in nums
```

---

# Key Observations

- Each customer must receive **identical numbers**.
- Customers cannot mix different values.
- Therefore we care about the **frequency of each unique number**.

Example:

```
nums = [1,1,1,2,2,3]
```

Frequency map:

```
1 -> 3
2 -> 2
3 -> 1
```

Customers can only be assigned from these frequency pools.

---

# Important Insight

Since:

```
m <= 10
```

The number of customers is **very small**, which allows solutions based on:

- **Backtracking**
- **Bitmask Dynamic Programming**
- **Subset enumeration**
