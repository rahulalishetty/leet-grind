# 1231. Divide Chocolate

## Problem Description

You have **one chocolate bar** that consists of multiple chunks.
Each chunk has its own sweetness value given by the array:

```
sweetness[i]
```

You want to share the chocolate with **k friends**.

To do this:

- You make **k cuts**
- This divides the chocolate bar into **k + 1 pieces**
- Each piece must consist of **consecutive chunks**

Since you are generous, you will:

- Eat the piece with the **minimum total sweetness**
- Give the other pieces to your friends

Your goal is to **maximize the sweetness of the piece you eat**.

In other words:

> Find the **maximum possible minimum sweetness** among all pieces after making `k` cuts.

---

## Example 1

```
Input:
sweetness = [1,2,3,4,5,6,7,8,9]
k = 5

Output:
6
```

Explanation:

One optimal division is:

```
[1,2,3] | [4,5] | [6] | [7] | [8] | [9]
```

The minimum sweetness among these pieces is:

```
min(6, 9, 6, 7, 8, 9) = 6
```

So the maximum sweetness you can guarantee for yourself is **6**.

---

## Example 2

```
Input:
sweetness = [5,6,7,8,9,1,2,3,4]
k = 8

Output:
1
```

Explanation:

There is only **one possible way** to divide the chocolate into **9 pieces** (each chunk individually).

The smallest sweetness piece is:

```
1
```

---

## Example 3

```
Input:
sweetness = [1,2,2,1,2,2,1,2,2]
k = 2

Output:
5
```

Explanation:

One optimal division:

```
[1,2,2] | [1,2,2] | [1,2,2]
```

Each piece has sweetness:

```
5
```

So the maximum sweetness you can get is **5**.

---

## Constraints

```
0 <= k < sweetness.length <= 10^4
```

```
1 <= sweetness[i] <= 10^5
```
