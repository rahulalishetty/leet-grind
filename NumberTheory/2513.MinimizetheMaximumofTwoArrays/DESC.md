# 2513. Minimize the Maximum of Two Arrays

We have two arrays **arr1** and **arr2** which are initially empty.

You need to add **positive integers** to them such that they satisfy all the following conditions:

1. **arr1** contains `uniqueCnt1` distinct positive integers, each **not divisible by `divisor1`**.
2. **arr2** contains `uniqueCnt2` distinct positive integers, each **not divisible by `divisor2`**.
3. **No integer appears in both arrays**.

Return the **minimum possible maximum integer** that can appear in either array.

---

# Example 1

## Input

```
divisor1 = 2
divisor2 = 7
uniqueCnt1 = 1
uniqueCnt2 = 3
```

## Output

```
4
```

## Explanation

We can distribute the first 4 natural numbers:

```
arr1 = [1]
arr2 = [2,3,4]
```

Conditions satisfied:

- `arr1` contains 1 number not divisible by 2
- `arr2` contains 3 numbers not divisible by 7
- No number appears in both arrays

Maximum value used: **4**

---

# Example 2

## Input

```
divisor1 = 3
divisor2 = 5
uniqueCnt1 = 2
uniqueCnt2 = 1
```

## Output

```
3
```

## Explanation

```
arr1 = [1,2]
arr2 = [3]
```

Conditions satisfied and the maximum value used is **3**.

---

# Example 3

## Input

```
divisor1 = 2
divisor2 = 4
uniqueCnt1 = 8
uniqueCnt2 = 2
```

## Output

```
15
```

## Explanation

One possible configuration:

```
arr1 = [1,3,5,7,9,11,13,15]
arr2 = [2,6]
```

Conditions satisfied:

- arr1 numbers are not divisible by 2
- arr2 numbers are not divisible by 4
- No duplicates across arrays

Minimum possible maximum value is **15**.

---

# Constraints

```
2 <= divisor1, divisor2 <= 10^5
1 <= uniqueCnt1, uniqueCnt2 < 10^9
2 <= uniqueCnt1 + uniqueCnt2 <= 10^9
```

---

# Problem Goal

Determine the **minimum possible maximum integer** such that:

- `uniqueCnt1` numbers exist **not divisible by divisor1**
- `uniqueCnt2` numbers exist **not divisible by divisor2**
- All numbers across both arrays are **distinct**
