# 3109. Find the Index of Permutation

## Problem Statement

You are given an array **perm** of length **n** which is a permutation of:

```
[1, 2, ..., n]
```

Your task is to return the **index** of `perm` in the **lexicographically sorted list of all permutations** of `[1,2,...,n]`.

Because the number of permutations grows very quickly (`n!`), the result may be very large.

Return the answer **modulo**:

```
10^9 + 7
```

---

# Lexicographic Order

Lexicographic ordering means permutations are ordered the same way words are ordered in a dictionary.

Example for `n = 3`:

```
[1,2,3]
[1,3,2]
[2,1,3]
[2,3,1]
[3,1,2]
[3,2,1]
```

The index starts from **0**.

---

# Example 1

## Input

```
perm = [1,2]
```

## Output

```
0
```

## Explanation

All permutations of `[1,2]`:

```
[1,2]
[2,1]
```

The permutation `[1,2]` appears at **index 0**.

---

# Example 2

## Input

```
perm = [3,1,2]
```

## Output

```
4
```

## Explanation

All permutations of `[1,2,3]`:

```
[1,2,3]   -> index 0
[1,3,2]   -> index 1
[2,1,3]   -> index 2
[2,3,1]   -> index 3
[3,1,2]   -> index 4
[3,2,1]   -> index 5
```

The permutation `[3,1,2]` is located at **index 4**.

---

# Constraints

```
1 <= n == perm.length <= 10^5
perm is a permutation of [1, 2, ..., n]
```

---

# Key Observations

- The total number of permutations of `n` elements is:

```
n!
```

- The index of a permutation in lexicographic order can be computed using the **factorial number system**.

- For each position `i`, we count how many smaller unused numbers exist to the right, multiply that count by `(n-i-1)!`, and accumulate the result.

- Because `n` can be up to **100,000**, we cannot enumerate permutations explicitly.

- Efficient data structures (such as **Fenwick Tree / BIT**) are typically used to track unused numbers while computing the rank.

---

# Output Requirement

Return the index **modulo**:

```
10^9 + 7
```
