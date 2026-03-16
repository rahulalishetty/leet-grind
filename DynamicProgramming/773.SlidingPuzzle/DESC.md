# 773. Sliding Puzzle

## Problem Description

You are given a **2 x 3 board** containing five numbered tiles (`1` through `5`) and one empty space represented by `0`.

A **move** consists of selecting the empty square (`0`) and swapping it with one of its **4-directionally adjacent** tiles (up, down, left, or right).

The puzzle is considered **solved** when the board becomes:

```
[[1,2,3],
 [4,5,0]]
```

Your task is to determine the **minimum number of moves** required to reach the solved state.

If it is **impossible** to reach the solved configuration, return **-1**.

---

## Board Rules

- Board size is fixed at **2 rows × 3 columns**
- Tiles contain numbers **0 through 5**
- `0` represents the **empty tile**
- Tiles can only swap with **adjacent positions**
- Every tile value appears **exactly once**

---

## Goal

Return the **minimum number of moves** needed to transform the given board into the solved configuration.

If no sequence of moves can reach the solved state, return **-1**.

---

## Example 1

### Input

```
board = [[1,2,3],
         [4,0,5]]
```

### Output

```
1
```

### Explanation

Swap `0` and `5`.

Initial:

```
1 2 3
4 0 5
```

After one move:

```
1 2 3
4 5 0
```

Solved.

---

## Example 2

### Input

```
board = [[1,2,3],
         [5,4,0]]
```

### Output

```
-1
```

### Explanation

This configuration cannot reach the solved state regardless of the number of moves.

---

## Example 3

### Input

```
board = [[4,1,2],
         [5,0,3]]
```

### Output

```
5
```

### Explanation

One optimal sequence of moves:

Move 0:

```
4 1 2
5 0 3
```

Move 1:

```
4 1 2
0 5 3
```

Move 2:

```
0 1 2
4 5 3
```

Move 3:

```
1 0 2
4 5 3
```

Move 4:

```
1 2 0
4 5 3
```

Move 5:

```
1 2 3
4 5 0
```

Puzzle solved.

---

## Constraints

```
board.length == 2
board[i].length == 3
0 <= board[i][j] <= 5
Each value appears exactly once
```

---
