# 1810. Minimum Path Cost in a Hidden Grid

## Problem Statement

This is an **interactive problem**.

There is a robot located somewhere inside a **hidden grid**. Your task is to determine the **minimum total cost required to move the robot from its starting cell to a target cell**.

However, the challenge is that:

- You **do not know the grid dimensions**
- You **do not know the start position**
- You **do not know the target position**
- You **cannot directly access the grid**

Instead, you can only interact with a special object called **GridMaster**.

---

# Grid Description

The hidden grid has size:

```
m x n
```

Each cell can be either:

- **Blocked** (`0`)
- **Empty** with a **positive movement cost** (`>=1`)

When the robot moves **into a cell**, you must pay the cost of that cell.

Important:

- The **starting cell cost is not counted** initially.
- The **starting cell and target cell are guaranteed to be different**.
- Both **starting and target cells are not blocked**.

---

# GridMaster API

You can interact with the grid using the following methods:

### 1. canMove

```
boolean canMove(char direction)
```

Returns:

- `true` if the robot can move in that direction
- `false` otherwise

Directions:

```
'U' -> Up
'D' -> Down
'L' -> Left
'R' -> Right
```

---

### 2. move

```
int move(char direction)
```

Moves the robot in the given direction.

Returns:

- **cost of the destination cell** if the move succeeds
- **-1** if the move would hit a blocked cell or go outside the grid

If the move fails, the robot **remains in the same cell**.

---

### 3. isTarget

```
boolean isTarget()
```

Returns:

- `true` if the robot is currently on the **target cell**
- `false` otherwise

---

# Objective

You must compute:

```
Minimum total cost to reach the target cell
```

Return:

```
-1 if the target is unreachable
```

---

# Custom Testing (Only for Explanation)

During testing, the hidden grid is simulated using:

```
grid (m x n)
r1, c1 -> starting position
r2, c2 -> target position
```

Cell meaning:

```
grid[i][j] == 0   -> blocked cell
grid[i][j] >= 1   -> movement cost of that cell
```

Note:

Your solution **will not have direct access to this grid**.

---

# Example 1

Input

```
grid = [[2,3],[1,1]]
r1 = 0, c1 = 1
r2 = 1, c2 = 0
```

Output

```
2
```

Explanation

Initial robot position:

```
(0,1) value = 3
```

Interaction example:

```
canMove('U') -> false
canMove('D') -> true
canMove('L') -> true
canMove('R') -> false
```

Move left:

```
move('L') -> moves to (0,0), cost = 2
```

Check target:

```
isTarget() -> false
```

Next moves:

```
move('D') -> moves to (1,0), cost = 1
isTarget() -> true
```

Total cost:

```
2
```

---

# Example 2

Input

```
grid = [[0,3,1],
        [3,4,2],
        [1,2,0]]

r1 = 2, c1 = 0
r2 = 0, c2 = 2
```

Output

```
9
```

Explanation

Optimal path:

```
(2,0) -> (2,1) -> (1,1) -> (1,2) -> (0,2)
```

Total cost:

```
2 + 4 + 2 + 1 = 9
```

---

# Example 3

Input

```
grid = [[1,0],
        [0,1]]

r1 = 0, c1 = 0
r2 = 1, c2 = 1
```

Output

```
-1
```

Explanation

There is **no valid path** between the start and the target.

---

# Constraints

```
1 <= m, n <= 100
0 <= grid[i][j] <= 100
```

Additional guarantees:

- The grid is finite
- The graph formed by roads is connected except for blocked cells
- Start and target cells are valid
