# 2344. Minimum Deletions to Make Array Divisible

You are given two positive integer arrays **nums** and **numsDivide**. You can delete any number of elements from **nums**.

Return the **minimum number of deletions** such that the **smallest element in nums divides all the elements of numsDivide**. If this is not possible, return **-1**.

Note that an integer **x divides y** if:

```
y % x == 0
```

---

# Example 1

## Input

```
nums = [2,3,2,4,3]
numsDivide = [9,6,9,3,15]
```

## Output

```
2
```

## Explanation

The smallest element in:

```
[2,3,2,4,3]
```

is **2**, which **does not divide all elements of numsDivide**.

We delete the elements equal to **2**, which requires **2 deletions**:

```
nums = [3,4,3]
```

Now the smallest element is:

```
3
```

And:

```
3 divides 9, 6, 9, 3, 15
```

So the answer is:

```
2
```

---

# Example 2

## Input

```
nums = [4,3,6]
numsDivide = [8,2,6,10]
```

## Output

```
-1
```

## Explanation

We need the **smallest element in nums** to divide **all values in numsDivide**.

No possible deletions from:

```
[4,3,6]
```

produce a smallest value that divides every element of:

```
[8,2,6,10]
```

Therefore the answer is:

```
-1
```

---

# Constraints

```
1 ≤ nums.length, numsDivide.length ≤ 10^5
1 ≤ nums[i], numsDivide[i] ≤ 10^9
```
