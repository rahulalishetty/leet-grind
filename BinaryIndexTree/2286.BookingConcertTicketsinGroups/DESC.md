# 2286. Booking Concert Tickets in Groups

## Problem Statement

A concert hall has:

- `n` rows numbered from `0` to `n - 1`
- each row contains `m` seats numbered from `0` to `m - 1`

You must design a **ticketing system** that allocates seats under the following conditions:

1. A group of `k` spectators can sit **together in a row**
2. A group of `k` spectators can be allocated seats **across rows (not necessarily together)**

However, spectators have strict preferences:

- They will only accept seats in rows **≤ maxRow**
- If multiple rows are available, the **smallest row index** is chosen
- If multiple seats are available in a row, the **smallest seat number** is chosen

---

# Class Specification

Implement the following class:

```
BookMyShow
```

---

## Constructor

```
BookMyShow(int n, int m)
```

Initializes the ticket system with:

- `n` rows
- `m` seats per row

---

## Method 1 — gather

```
int[] gather(int k, int maxRow)
```

Allocates **k consecutive seats in the same row**.

Returns:

```
[r, c]
```

Where:

- `r` = row number
- `c` = seat number of the **first allocated seat**

Conditions:

- seats `[c, c + k - 1]` must be empty
- row `r ≤ maxRow`
- smallest possible `r`
- smallest possible `c`

If allocation is not possible, return:

```
[]
```

---

## Method 2 — scatter

```
boolean scatter(int k, int maxRow)
```

Allocates **k seats (not necessarily consecutive)** among rows `0..maxRow`.

Rules:

- fill seats from **smallest row number**
- within a row, use **smallest seat numbers first**

Returns:

```
true
```

if allocation succeeds.

Otherwise:

```
false
```

---

# Example

## Input

```
["BookMyShow", "gather", "gather", "scatter", "scatter"]
[[2, 5], [4, 0], [2, 0], [5, 1], [5, 1]]
```

---

## Output

```
[null, [0,0], [], true, false]
```

---

## Explanation

```
BookMyShow bms = new BookMyShow(2, 5);
```

There are **2 rows** with **5 seats each**.

---

### Operation 1

```
bms.gather(4, 0)
```

Return:

```
[0,0]
```

Seats `[0..3]` in **row 0** are allocated.

---

### Operation 2

```
bms.gather(2, 0)
```

Return:

```
[]
```

Only **1 seat remains** in row 0, so **2 consecutive seats** cannot be allocated.

---

### Operation 3

```
bms.scatter(5, 1)
```

Return:

```
true
```

Seats allocated:

```
row 0 -> seat 4
row 1 -> seats [0..3]
```

---

### Operation 4

```
bms.scatter(5, 1)
```

Return:

```
false
```

Only **one seat remains** in the hall.

---

# Constraints

```
1 <= n <= 5 * 10^4
1 <= m, k <= 10^9
0 <= maxRow <= n - 1
At most 5 * 10^4 calls to gather and scatter
```
