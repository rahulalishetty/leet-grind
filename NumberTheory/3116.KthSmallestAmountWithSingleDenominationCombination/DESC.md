# 3116. Kth Smallest Amount With Single Denomination Combination

You are given an integer array `coins` representing coins of different denominations and an integer `k`.

You have an **infinite number of coins of each denomination**. However, you are **not allowed to combine coins of different denominations**.

Return the **kth smallest amount** that can be made using these coins.

---

# Key Rule

For each coin denomination `c`, you can only form:

```
c, 2c, 3c, 4c, ...
```

You **cannot mix different coins** in a single amount.

Example:

If coins are `[2,5]`, valid amounts are:

```
2,4,6,8,10,...
5,10,15,20,...
```

Combined sorted amounts:

```
2,4,5,6,8,10,12,14,15...
```

---

# Example 1

## Input

```
coins = [3,6,9]
k = 3
```

## Output

```
9
```

## Explanation

Coin `3` produces multiples:

```
3, 6, 9, 12, 15, ...
```

Coin `6` produces:

```
6, 12, 18, 24, ...
```

Coin `9` produces:

```
9, 18, 27, 36, ...
```

Combined sorted sequence:

```
3, 6, 9, 12, 15, ...
```

The **3rd smallest amount is `9`**.

---

# Example 2

## Input

```
coins = [5,2]
k = 7
```

## Output

```
12
```

## Explanation

Coin `5` produces:

```
5, 10, 15, 20, ...
```

Coin `2` produces:

```
2, 4, 6, 8, 10, 12, ...
```

Combined sorted sequence:

```
2, 4, 5, 6, 8, 10, 12, 14, 15, ...
```

The **7th smallest amount is `12`**.

---

# Constraints

```
1 <= coins.length <= 15
1 <= coins[i] <= 25
1 <= k <= 2 * 10^9
coins contains pairwise distinct integers
```
