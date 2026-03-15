# 1891. Cutting Ribbons

## Problem Description

You are given an integer array `ribbons`, where:

```
ribbons[i] = length of the i-th ribbon
```

You are also given an integer `k`.

You may cut any ribbon into **any number of segments with positive integer lengths**, or **choose not to cut it at all**.

---

## Example Cuts

If a ribbon has length **4**, you can:

```
[4]
[3,1]
[2,2]
[2,1,1]
[1,1,1,1]
```

You may **discard leftover pieces** after cutting.

---

## Goal

Determine the **maximum ribbon length `x`** such that you can obtain:

```
at least k ribbons of length x
```

If it is **impossible** to produce `k` ribbons of the same length:

```
return 0
```

---

## Example 1

Input

```
ribbons = [9,7,5]
k = 3
```

Output

```
5
```

Explanation

```
9 → [5,4]
7 → [5,2]
5 → [5]
```

Now we have **3 ribbons of length 5**.

---

## Example 2

Input

```
ribbons = [7,5,9]
k = 4
```

Output

```
4
```

Explanation

```
7 → [4,3]
5 → [4,1]
9 → [4,4,1]
```

Now we have **4 ribbons of length 4**.

---

## Example 3

Input

```
ribbons = [5,7,9]
k = 22
```

Output

```
0
```

Explanation

It is **impossible** to create 22 ribbons of equal positive length.

---

## Constraints

```
1 <= ribbons.length <= 10^5
```

```
1 <= ribbons[i] <= 10^5
```

```
1 <= k <= 10^9
```
