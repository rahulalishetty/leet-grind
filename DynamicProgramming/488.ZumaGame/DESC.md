# 488. Zuma Game

## Problem Description

You are playing a variation of the **Zuma** game.

In this version, there is a **single row of colored balls** on a board. Each ball can have one of the following colors:

```
R - Red
Y - Yellow
B - Blue
G - Green
W - White
```

You also have a number of colored balls **in your hand**.

---

## Game Rules

Your goal is to **clear all balls from the board**.

On each turn you may:

1. Pick **one ball from your hand**.
2. Insert it **anywhere in the board**:
   - between two balls, or
   - at either end of the row.

---

## Removal Rule

After inserting a ball:

- If there are **three or more consecutive balls of the same color**, they are **removed**.

Example:

```
RRR → removed
BBBB → removed
YYY → removed
```

---

## Chain Reactions

After a removal:

- New groups of **three or more consecutive balls** may form.
- These groups are **also removed automatically**.

This process continues until:

```
No more groups of 3+ identical balls exist
```

---

## Winning Condition

You **win the game** if:

```
The board becomes empty
```

---

## Game End

You **lose the game** if:

- The board is **not empty**
- You have **no balls left in your hand**

---

## Goal

Given:

```
board → current balls on the board
hand  → balls available in your hand
```

Return:

```
The minimum number of balls you must insert to clear the board.
```

If it is **impossible**, return:

```
-1
```

---

# Example 1

### Input

```
board = "WRRBBW"
hand  = "RB"
```

### Output

```
-1
```

### Explanation

Best attempt:

```
Insert R → WRRRBBW
WRRRBBW → WBBW
```

Then:

```
Insert B → WBBBW
WBBBW → WW
```

But:

```
Board still contains balls
Hand is empty
```

So clearing the board is **impossible**.

---

# Example 2

### Input

```
board = "WWRRBBWW"
hand  = "WRBRW"
```

### Output

```
2
```

### Explanation

Step 1:

```
Insert R
WWRRBBWW → WWRRRBBWW
WWRRRBBWW → WWBBWW
```

Step 2:

```
Insert B
WWBBWW → WWBBBWW
WWBBBWW → WWWW → empty
```

Total balls inserted:

```
2
```

---

# Example 3

### Input

```
board = "G"
hand  = "GGGGG"
```

### Output

```
2
```

### Explanation

Step 1:

```
Insert G → GG
```

Step 2:

```
Insert G → GGG
GGG → empty
```

Total balls used:

```
2
```

---

# Constraints

```
1 <= board.length <= 16
1 <= hand.length <= 5
```

Characters allowed:

```
R, Y, B, G, W
```

Important guarantee:

```
The initial board never contains a group of 3 or more identical balls.
```

---

# Key Observations

- The board is **very small (≤16)**.
- The hand is **very small (≤5)**.

Because of this, the problem is typically solved using:

```
DFS / Backtracking + Pruning
```

or

```
BFS + State compression
```

---

# Important Mechanics

Whenever you insert a ball:

1. The board changes.
2. You must repeatedly **remove any groups ≥3**.
3. Chain reactions may occur.

Example:

```
RRBBBRR
Insert B → RRBBBBRR
Remove BBBB → RRRR
Remove RRRR → empty
```

---

# State Representation

A game state can be represented as:

```
(board_string, remaining_hand)
```

Each move:

```
choose a position
choose a ball from hand
insert
collapse groups
```

Search continues until:

```
board becomes empty
```

---

# Objective

Minimize:

```
number of balls inserted
```

If no sequence of moves clears the board:

```
return -1
```

---

# Problem Summary

You must determine the **minimum insertions** required to clear the Zuma board while:

- handling chain reactions
- exploring valid insertion points
- efficiently pruning impossible states

Because of the **small constraints**, the problem is designed for:

```
DFS / BFS with memoization
```
