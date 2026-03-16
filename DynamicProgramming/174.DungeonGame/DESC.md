# 174. Dungeon Game

## Problem Statement

The demons have captured the princess and imprisoned her in the **bottom-right corner** of a dungeon.
The dungeon consists of an **m x n grid of rooms**.

A valiant knight starts in the **top-left room** and must travel through the dungeon to rescue the princess.

---

## Rules

- The knight has an **initial health value**, which is a positive integer.
- If at any point the knight's **health drops to 0 or below**, he **dies immediately**.
- Each room in the dungeon may affect the knight's health:

| Room Value      | Meaning                                       |
| --------------- | --------------------------------------------- |
| Negative number | A demon damages the knight (health decreases) |
| 0               | Empty room                                    |
| Positive number | Magic orb increases health                    |

---

## Movement Constraints

To reach the princess as quickly as possible, the knight may **only move:**

- **Right**
- **Down**

---

## Goal

Return the **minimum initial health** the knight must start with so that he can safely reach the princess.

Important:

- The knight must **never allow health to drop to 0 or below**.
- The **starting room** and the **princess's room** may also contain health effects.

---

## Example 1

### Input

```
dungeon = [
 [-2, -3,  3],
 [-5,-10,  1],
 [10, 30, -5]
]
```

### Output

```
7
```

### Explanation

The optimal path is:

```
RIGHT → RIGHT → DOWN → DOWN
```

With an initial health of **7**, the knight survives all rooms and rescues the princess.

---

## Example 2

### Input

```
dungeon = [[0]]
```

### Output

```
1
```

### Explanation

The knight starts and ends in the same room.
He needs **at least 1 health** to survive.

---

## Constraints

- `m == dungeon.length`
- `n == dungeon[i].length`
- `1 <= m, n <= 200`
- `-1000 <= dungeon[i][j] <= 1000`
