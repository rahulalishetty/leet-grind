# 2117. Abbreviating the Product of a Range

You are given two positive integers `left` and `right` with `left <= right`. Calculate the product of all integers in the inclusive range `[left, right]`.

Since the product may be very large, you will abbreviate it following these steps:

1. **Count all trailing zeros in the product and remove them.**
   Let this count be **C**.
   - Example: `1000` has **3 trailing zeros**
   - Example: `546` has **0 trailing zeros**

2. Let the remaining number of digits in the product be **d**.
   - If `d > 10`, express the product as:

   ```
   <pre>...<suf>
   ```

   where:
   - `<pre>` = first **5 digits**
   - `<suf>` = last **5 digits** after removing trailing zeros

   Example:

   ```
   1234567654321 -> 12345...54321
   ```

3. If `d <= 10`, keep the number unchanged.

   Example:

   ```
   1234567 -> 1234567
   ```

4. Finally represent the result as:

```
<pre>...<suf>eC
```

Example:

```
12345678987600000 -> "12345...89876e5"
```

---

# Example 1

### Input

```
left = 1
right = 4
```

### Output

```
"24e0"
```

### Explanation

```
1 × 2 × 3 × 4 = 24
```

- Trailing zeros = `0`
- Digits = `2` (≤ 10)

Final representation:

```
"24e0"
```

---

# Example 2

### Input

```
left = 2
right = 11
```

### Output

```
"399168e2"
```

### Explanation

```
Product = 39916800
```

- Trailing zeros = `2`
- After removing zeros → `399168`
- Digits = `6` (≤ 10)

Final representation:

```
"399168e2"
```

---

# Example 3

### Input

```
left = 371
right = 375
```

### Output

```
"7219856259e3"
```

### Explanation

```
Product = 7219856259000
```

- Trailing zeros = `3`
- Remaining product = `7219856259`

Final representation:

```
"7219856259e3"
```

---

# Constraints

```
1 <= left <= right <= 10^4
```
