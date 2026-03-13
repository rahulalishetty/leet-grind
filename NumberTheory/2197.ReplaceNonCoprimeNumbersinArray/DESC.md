# 2197. Replace Non-Coprime Numbers in Array

You are given an array of integers `nums`. Perform the following steps:

1. Find any two **adjacent numbers** in `nums` that are **non-coprime**.
2. If no such numbers are found, stop the process.
3. Otherwise, delete the two numbers and replace them with their **LCM (Least Common Multiple)**.
4. Repeat this process as long as you keep finding two adjacent non-coprime numbers.

Return the final modified array.

It can be shown that replacing adjacent non-coprime numbers in **any arbitrary order** will lead to the same result.

The test cases are generated such that the values in the final array are **≤ 10^8**.

Two values `x` and `y` are **non-coprime** if:

```
gcd(x, y) > 1
```

where `gcd(x, y)` is the **Greatest Common Divisor** of `x` and `y`.

---

# Example 1

## Input

```
nums = [6,4,3,2,7,6,2]
```

## Output

```
[12,7,6]
```

## Explanation

```
(6,4) are non-coprime → LCM(6,4) = 12
nums = [12,3,2,7,6,2]

(12,3) are non-coprime → LCM(12,3) = 12
nums = [12,2,7,6,2]

(12,2) are non-coprime → LCM(12,2) = 12
nums = [12,7,6,2]

(6,2) are non-coprime → LCM(6,2) = 6
nums = [12,7,6]
```

No more adjacent non-coprime numbers exist.

Final array:

```
[12,7,6]
```

---

# Example 2

## Input

```
nums = [2,2,1,1,3,3,3]
```

## Output

```
[2,1,1,3]
```

## Explanation

```
(3,3) → LCM = 3
nums = [2,2,1,1,3,3]

(3,3) → LCM = 3
nums = [2,2,1,1,3]

(2,2) → LCM = 2
nums = [2,1,1,3]
```

No more adjacent non-coprime numbers remain.

Final array:

```
[2,1,1,3]
```

---

# Constraints

```
1 ≤ nums.length ≤ 10^5
1 ≤ nums[i] ≤ 10^5
```
