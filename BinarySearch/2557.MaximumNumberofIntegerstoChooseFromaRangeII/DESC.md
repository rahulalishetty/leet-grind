# 2557. Maximum Number of Integers to Choose From a Range II

## Problem Description

You are given:

- An integer array `banned`
- Two integers `n` and `maxSum`

You want to choose some integers under the following rules:

1. The chosen integers must be in the range:

```
[1, n]
```

2. Each integer can be chosen **at most once**.

3. The chosen integers **must not appear in the `banned` array**.

4. The **sum of the chosen integers must not exceed `maxSum`**.

Your task is to **return the maximum number of integers you can choose** that satisfy these conditions.

---

# Example 1

Input

```
banned = [1,4,6]
n = 6
maxSum = 4
```

Output

```
1
```

Explanation

You can choose:

```
3
```

- `3` is within `[1,6]`
- `3` is not in `banned`
- Sum = `3 ≤ maxSum`

Only one integer can be chosen.

---

# Example 2

Input

```
banned = [4,3,5,6]
n = 7
maxSum = 18
```

Output

```
3
```

Explanation

You can choose:

```
1, 2, 7
```

Conditions satisfied:

- All numbers are within `[1,7]`
- None are in `banned`
- Sum:

```
1 + 2 + 7 = 10 ≤ 18
```

So the maximum count is:

```
3
```

---

# Constraints

```
1 <= banned.length <= 10^5
1 <= banned[i] <= n <= 10^9
1 <= maxSum <= 10^15
```
