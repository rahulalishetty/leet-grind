# 1066. Campus Bikes II

## Problem Description

On a campus represented as a **2D grid**, there are:

- `n` workers
- `m` bikes

where:

```
n <= m
```

Each worker and bike is represented by a coordinate:

```
[x, y]
```

Your goal is to **assign exactly one bike to each worker** such that:

- each bike can be assigned to **only one worker**
- the **total Manhattan distance** between workers and assigned bikes is **minimized**

Return the **minimum possible sum** of Manhattan distances.

---

## Manhattan Distance

The Manhattan distance between two points:

```
p1 = (x1, y1)
p2 = (x2, y2)
```

is defined as:

```
|x1 - x2| + |y1 - y2|
```

This represents the distance when movement is allowed **only horizontally and vertically**.

---

# Example 1

Input

```
workers = [[0,0],[2,1]]
bikes = [[1,2],[3,3]]
```

Output

```
6
```

Explanation

Assignment:

```
Worker 0 -> Bike 0
Worker 1 -> Bike 1
```

Distances:

```
|0-1| + |0-2| = 3
|2-3| + |1-3| = 3
```

Total:

```
3 + 3 = 6
```

---

# Example 2

Input

```
workers = [[0,0],[1,1],[2,0]]
bikes = [[1,0],[2,2],[2,1]]
```

Output

```
4
```

Explanation

One optimal assignment:

```
Worker 0 -> Bike 0
Worker 1 -> Bike 2
Worker 2 -> Bike 1
```

Distances:

```
|0-1| + |0-0| = 1
|1-2| + |1-1| = 1
|2-2| + |0-2| = 2
```

Total:

```
1 + 1 + 2 = 4
```

Another assignment also gives total distance **4**, which is also optimal.

---

# Example 3

Input

```
workers = [[0,0],[1,0],[2,0],[3,0],[4,0]]
bikes = [[0,999],[1,999],[2,999],[3,999],[4,999]]
```

Output

```
4995
```

Explanation

Each worker is assigned the bike with the same x-coordinate.

Distance for each worker:

```
|x-x| + |0-999| = 999
```

Total distance:

```
999 * 5 = 4995
```

---

# Constraints

```
n == workers.length
m == bikes.length
1 <= n <= m <= 10
workers[i].length == 2
bikes[i].length == 2
0 <= workers[i][0], workers[i][1], bikes[i][0], bikes[i][1] < 1000
```

Additional notes:

- All worker coordinates are **unique**
- All bike coordinates are **unique**
- Each worker must get **exactly one bike**
- Each bike can be used **at most once**

---
