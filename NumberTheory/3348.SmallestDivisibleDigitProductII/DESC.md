# 3348. Smallest Divisible Digit Product II

You are given a string `num` which represents a **positive integer**, and an integer `t`.

A number is called **zero-free** if **none of its digits are 0**.

Your task is to return the **smallest zero-free number greater than or equal to `num`** such that:

```
product_of_digits % t == 0
```

If no such number exists, return:

```
"-1"
```

---

# Definitions

### Zero-Free Number

A number whose digits **do not contain the digit 0**.

Example:

```
1234 → zero-free
1053 → NOT zero-free
```

### Digit Product

The product of all digits of the number.

Example:

```
1488
product = 1 × 4 × 8 × 8 = 256
```

---

# Example 1

## Input

```
num = "1234"
t = 256
```

## Output

```
"1488"
```

## Explanation

The smallest zero-free number ≥ `1234` whose digit product is divisible by `256` is:

```
1488
```

Digit product:

```
1 × 4 × 8 × 8 = 256
```

```
256 % 256 = 0
```

---

# Example 2

## Input

```
num = "12355"
t = 50
```

## Output

```
"12355"
```

## Explanation

The number is already:

- zero-free
- product divisible by `50`

Digit product:

```
1 × 2 × 3 × 5 × 5 = 150
```

```
150 % 50 = 0
```

So the answer is the number itself.

---

# Example 3

## Input

```
num = "11111"
t = 26
```

## Output

```
"-1"
```

## Explanation

No number ≥ `11111` that is zero-free has a digit product divisible by `26`.

Therefore the answer is:

```
-1
```

---

# Constraints

```
2 <= num.length <= 2 * 10^5
num consists only of digits ['0'..'9']
num has no leading zeros
1 <= t <= 10^14
```

---

# Observations

1. Digits allowed in the result are:

```
1..9
```

since the number must be **zero-free**.

2. The digit product can be represented using **prime factorization** of digits:

| Digit | Prime Factors |
| ----- | ------------- |
| 1     | —             |
| 2     | 2             |
| 3     | 3             |
| 4     | 2²            |
| 5     | 5             |
| 6     | 2 × 3         |
| 7     | 7             |
| 8     | 2³            |
| 9     | 3²            |

3. Therefore, the product of digits can only contain prime factors:

```
2, 3, 5, 7
```

4. If `t` contains any prime factor other than:

```
2, 3, 5, 7
```

then the answer is impossible.

---

# Problem Goal

Find the **smallest number ≥ num** such that:

```
1. No digit is zero
2. Product of digits % t == 0
```

Return it as a **string**.

If impossible:

```
return "-1"
```
