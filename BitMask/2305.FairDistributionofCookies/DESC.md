# 2305. Fair Distribution of Cookies

## Problem Statement

You are given an integer array `cookies`, where:

```
cookies[i] = number of cookies in the i-th bag
```

You are also given an integer `k` representing the **number of children**.

Your task is to distribute all the cookie bags among the `k` children with the following rules:

- Each bag must be given **entirely to one child**.
- A bag **cannot be split** between multiple children.
- Every bag must be assigned.

The **unfairness** of a distribution is defined as:

```
maximum number of cookies received by any single child
```

Your goal is to **minimize this unfairness**.

Return the **minimum possible unfairness** across all possible distributions.

---

## Example 1

### Input

```
cookies = [8,15,10,20,8]
k = 2
```

### Output

```
31
```

### Explanation

One optimal distribution is:

```
Child 1 → [8,15,8]
Child 2 → [10,20]
```

Total cookies:

```
Child 1 = 8 + 15 + 8 = 31
Child 2 = 10 + 20 = 30
```

Unfairness:

```
max(31, 30) = 31
```

It can be proven that **no distribution results in unfairness smaller than 31**.

---

## Example 2

### Input

```
cookies = [6,1,3,2,2,4,1,2]
k = 3
```

### Output

```
7
```

### Explanation

One optimal distribution:

```
Child 1 → [6,1]
Child 2 → [3,2,2]
Child 3 → [4,1,2]
```

Totals:

```
Child 1 = 6 + 1 = 7
Child 2 = 3 + 2 + 2 = 7
Child 3 = 4 + 1 + 2 = 7
```

Unfairness:

```
max(7, 7, 7) = 7
```

No distribution achieves unfairness lower than `7`.

---

## Constraints

```
2 <= cookies.length <= 8
1 <= cookies[i] <= 10^5
2 <= k <= cookies.length
```
