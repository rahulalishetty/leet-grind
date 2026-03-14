# 1569. Number of Ways to Reorder Array to Get Same BST

## Problem

You are given an array **nums** representing a **permutation of integers from 1 to n**.

We construct a **Binary Search Tree (BST)** by inserting the elements of `nums` **in order** into an initially empty BST.

Your task is to determine:

> How many different ways can we reorder `nums` such that the **constructed BST remains identical** to the BST formed from the original `nums`.

Return the number of such reorderings.

Because the result can be very large, return the answer **modulo (10⁹ + 7)**.

---

# Example 1

### Input

```
nums = [2,1,3]
```

### Output

```
1
```

### Explanation

Original BST:

```
    2
   / \\
  1   3
```

Another ordering that produces the same BST:

```
[2,3,1]
```

No other reorderings produce the same BST.

---

# Example 2

### Input

```
nums = [3,4,5,1,2]
```

### Output

```
5
```

### Explanation

The following reorderings produce the **same BST**:

```
[3,1,2,4,5]
[3,1,4,2,5]
[3,1,4,5,2]
[3,4,1,2,5]
[3,4,1,5,2]
```

---

# Example 3

### Input

```
nums = [1,2,3]
```

### Output

```
0
```

### Explanation

The BST formed is completely right-skewed.

```
1
 \\
  2
   \\
    3
```

Any other ordering produces a **different BST**, so the answer is `0`.

---

# Constraints

```
1 ≤ nums.length ≤ 1000
1 ≤ nums[i] ≤ nums.length
All integers in nums are distinct
```
