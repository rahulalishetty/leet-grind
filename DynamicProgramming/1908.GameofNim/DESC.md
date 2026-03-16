# 1908. Game of Nim

## Problem Description

Alice and Bob take turns playing a game with **Alice starting first**.

In this game:

- There are **n piles of stones**.
- On each player's turn, the player can remove **any positive number of stones** from **one non‑empty pile**.
- The **first player who cannot make a move loses**, and the other player wins.

Given an integer array `piles`, where:

```
piles[i] = number of stones in the ith pile
```

Return:

- `true` if **Alice wins**
- `false` if **Bob wins**

Assume **both players play optimally**.

---

# Examples

## Example 1

**Input**

```
piles = [1]
```

**Output**

```
true
```

**Explanation**

There is only one possible scenario:

1. Alice removes one stone from the first pile.

```
piles = [0]
```

2. Bob now has **no stones to remove**.

Alice wins.

---

## Example 2

**Input**

```
piles = [1,1]
```

**Output**

```
false
```

**Explanation**

Bob will always win.

One possible sequence:

1. Alice removes one stone from the first pile.

```
piles = [0,1]
```

2. Bob removes one stone from the second pile.

```
piles = [0,0]
```

3. Alice now has **no stones to remove**.

Bob wins.

---

## Example 3

**Input**

```
piles = [1,2,3]
```

**Output**

```
false
```

**Explanation**

Bob will always win.

One possible sequence:

1. Alice removes **three stones** from the third pile.

```
piles = [1,2,0]
```

2. Bob removes **one stone** from the second pile.

```
piles = [1,1,0]
```

3. Alice removes **one stone** from the first pile.

```
piles = [0,1,0]
```

4. Bob removes **one stone** from the second pile.

```
piles = [0,0,0]
```

5. Alice cannot move.

Bob wins.

---

# Constraints

```
n == piles.length
1 <= n <= 7
1 <= piles[i] <= 7
```

---

# Follow‑up

Could you find a **linear time solution**?

Although the linear time solution may be beyond the scope of an interview, it is interesting to know that this problem reduces to the **classical Nim Game**, where the optimal strategy depends on the **XOR (nim‑sum) of all piles**.
