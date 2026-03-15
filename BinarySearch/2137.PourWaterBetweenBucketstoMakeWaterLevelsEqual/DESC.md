# 2137. Pour Water Between Buckets to Make Water Levels Equal

## Problem Description

You have **n buckets**, each containing some gallons of water.
The amount of water in each bucket is given by a **0-indexed integer array**:

```
buckets[i]
```

which represents the number of gallons in the `i`th bucket.

You are also given an integer:

```
loss
```

which represents the **percentage of water spilled during pouring**.

---

## Operation Rules

You can **pour water from one bucket to another**.

- The amount poured can be **any real value** (not necessarily an integer).
- When you pour `k` gallons of water:

```
loss percent of k is spilled
```

Meaning:

```
water received = k * (1 - loss / 100)
```

---

## Goal

You want to **make the water level in all buckets equal**.

Return the **maximum possible amount of water in each bucket** after performing any number of pours.

Answers within:

```
10^-5
```

of the correct answer are accepted.

---

# Example 1

Input

```
buckets = [1,2,7]
loss = 80
```

Output

```
2.00000
```

Explanation

Pour **5 gallons** from bucket `7` to bucket `1`.

Spilled:

```
5 × 80% = 4
```

Water received:

```
5 - 4 = 1
```

Final buckets:

```
[2,2,2]
```

---

# Example 2

Input

```
buckets = [2,4,6]
loss = 50
```

Output

```
3.50000
```

Explanation

Step 1

Pour `0.5` gallons from bucket `4` to bucket `2`.

Spilled:

```
0.5 × 50% = 0.25
```

Received:

```
0.25
```

Buckets become:

```
[2.25, 3.5, 6]
```

Step 2

Pour `2.5` gallons from bucket `6` to bucket `2.25`.

Spilled:

```
2.5 × 50% = 1.25
```

Received:

```
1.25
```

Final buckets:

```
[3.5,3.5,3.5]
```

---

# Example 3

Input

```
buckets = [3,3,3,3]
loss = 40
```

Output

```
3.00000
```

Explanation

All buckets already contain the same amount of water.

---

# Constraints

```
1 <= buckets.length <= 10^5
0 <= buckets[i] <= 10^5
0 <= loss <= 99
```
