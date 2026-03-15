# 2604. Minimum Time to Eat All Grains

## Problem Description

There are **n hens** and **m grains** located on a number line.

You are given two integer arrays:

```
hens   -> positions of hens
grains -> positions of grains
```

Each hen and grain is positioned on the **same 1D line**.

---

## Movement Rules

- If a hen and a grain are at the **same position**, the hen eats the grain instantly.
- A hen can eat **multiple grains**.
- In **1 second**, a hen can move:

```
+1 unit (right)
-1 unit (left)
```

- All hens can move **simultaneously and independently**.

---

## Goal

Return the **minimum time required for the hens to eat all grains**, assuming the hens act optimally.

---

# Example 1

Input

```
hens = [3,6,7]
grains = [2,4,7,9]
```

Output

```
2
```

Explanation

One optimal strategy:

- Hen at `3` moves to `2` and eats the grain in **1 second**.
- Hen at `6` moves to `4` and eats the grain in **2 seconds**.
- Hen at `7` eats grain at `7` instantly and then moves to `9` in **2 seconds**.

The **maximum time required is 2 seconds**.

It can be proven that it is impossible to eat all grains in less than `2` seconds.

---

# Example 2

Input

```
hens = [4,6,109,111,213,215]
grains = [5,110,214]
```

Output

```
1
```

Explanation

One optimal strategy:

- Hen at `4` moves to `5` and eats the grain in **1 second**.
- Hen at `111` moves to `110` and eats the grain in **1 second**.
- Hen at `215` moves to `214` and eats the grain in **1 second**.

Other hens do not need to move.

The **maximum time required is 1 second**.

---

# Constraints

```
1 <= hens.length, grains.length <= 2 * 10^4
```

```
0 <= hens[i], grains[j] <= 10^9
```
