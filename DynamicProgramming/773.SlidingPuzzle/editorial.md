# 773. Sliding Puzzle — Exhaustive Solution Notes

## Overview

We are given a fixed **2 x 3 sliding puzzle** containing the numbers:

```text
0, 1, 2, 3, 4, 5
```

where:

- `0` represents the empty square
- a move consists of swapping `0` with one of its 4-directionally adjacent neighbors

The goal is to transform the board into the solved state:

```text
[[1, 2, 3],
 [4, 5, 0]]
```

and return the **minimum number of moves** required.

If it is impossible to reach the solved state, return:

```text
-1
```

Because the board has only 6 positions, there are at most:

```text
6! = 720
```

possible configurations.

That is small enough to treat the puzzle as a **graph search** problem over states.

This write-up explains two approaches:

1. **Depth-First Search (DFS)**
2. **Breadth-First Search (BFS)**

The BFS approach is the standard correct and optimal solution for shortest path in an unweighted state graph.

---

## Problem Statement

On a `2 x 3` board, there are five numbered tiles `1` through `5` and one empty square `0`.

A move consists of choosing `0` and swapping it with a tile adjacent in one of the four cardinal directions.

Return the minimum number of moves required to reach the solved board:

```text
[[1,2,3],
 [4,5,0]]
```

If the puzzle cannot be solved, return `-1`.

---

## Example 1

**Input**

```text
board = [[1,2,3],
         [4,0,5]]
```

**Output**

```text
1
```

**Explanation**

Swap `0` and `5` once.

---

## Example 2

**Input**

```text
board = [[1,2,3],
         [5,4,0]]
```

**Output**

```text
-1
```

**Explanation**

This configuration cannot be transformed into the solved state.

---

## Example 3

**Input**

```text
board = [[4,1,2],
         [5,0,3]]
```

**Output**

```text
5
```

**Explanation**

One optimal path is:

```text
After move 0: [[4,1,2],[5,0,3]]
After move 1: [[4,1,2],[0,5,3]]
After move 2: [[0,1,2],[4,5,3]]
After move 3: [[1,0,2],[4,5,3]]
After move 4: [[1,2,0],[4,5,3]]
After move 5: [[1,2,3],[4,5,0]]
```

So the minimum number of moves is `5`.

---

## Constraints

- `board.length == 2`
- `board[i].length == 3`
- `0 <= board[i][j] <= 5`
- all values are unique

---

# Key Insight: Model the Puzzle as a Graph

Each board configuration can be treated as a **node** in a graph.

A legal move corresponds to an **edge** between two configurations.

Then the problem becomes:

> Find the shortest path from the initial board configuration to the target configuration.

This is exactly why **BFS** is the natural optimal solution.

---

# Flattening the Board to a String

To make state handling simpler, we flatten the `2 x 3` board into a string of length 6.

For example:

```text
[[1,2,3],
 [4,0,5]]
```

becomes:

```text
"123405"
```

The solved board becomes:

```text
"123450"
```

This makes it easy to:

- compare states
- hash states in a set/map
- swap characters to generate next states

---

# Index Mapping in the Flattened Board

The flattened board positions are:

```text
0 1 2
3 4 5
```

So each index corresponds to a position on the board.

The possible moves of the empty tile (`0`) are fixed for each index:

```text
0 -> [1, 3]
1 -> [0, 2, 4]
2 -> [1, 5]
3 -> [0, 4]
4 -> [1, 3, 5]
5 -> [2, 4]
```

This adjacency list is used by both DFS and BFS.

---

# Approach 1: Depth-First Search (DFS)

## Intuition

Because the total number of states is small, a brute-force exploration is feasible.

We can:

1. start from the initial state
2. recursively explore all possible next states
3. record the minimum number of moves needed to reach each state
4. check whether the target state was reached

This works, but it is not the best way to find the shortest path.

DFS explores deeply along one path before trying others. That means it may find the solved state late or reach the same state many times through longer paths.

So while DFS can work here with pruning, it is inefficient compared to BFS.

---

## DFS State Representation

We use:

- `state`: the flattened board string
- `zeroPos`: the current index of `0`
- `moves`: how many moves have been taken so far

We also maintain a map:

```text
visited[state] = minimum moves seen so far to reach this state
```

If we revisit a state with a move count that is not better, we stop exploring that branch.

---

## DFS Logic

At each DFS call:

1. If `state` has already been visited with fewer or equal moves, return immediately.
2. Otherwise, record the current move count.
3. Find all valid positions that `0` can swap with.
4. Generate each new state by swapping `0` with one of those positions.
5. Recurse with `moves + 1`.

When DFS finishes, if the solved state `"123450"` was visited, its recorded move count is the answer.

Otherwise return `-1`.

---

## Why DFS Is Not Ideal for Shortest Path

DFS does not search by move count level.

That means:

- it may explore long useless paths first
- it may revisit states through many non-optimal routes
- even if it finds the target, it still may need to keep exploring to prove optimality

This is why BFS is superior here.

---

## Java Implementation — DFS

```java
import java.util.*;

class Solution {

    private final int[][] directions = {
        { 1, 3 },
        { 0, 2, 4 },
        { 1, 5 },
        { 0, 4 },
        { 3, 5, 1 },
        { 4, 2 },
    };

    public int slidingPuzzle(int[][] board) {
        StringBuilder startState = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                startState.append(board[i][j]);
            }
        }

        Map<String, Integer> visited = new HashMap<>();

        dfs(startState.toString(), visited, startState.indexOf("0"), 0);

        return visited.getOrDefault("123450", -1);
    }

    private void dfs(
        String state,
        Map<String, Integer> visited,
        int zeroPos,
        int moves
    ) {
        if (visited.containsKey(state) && visited.get(state) <= moves) {
            return;
        }
        visited.put(state, moves);

        for (int nextPos : directions[zeroPos]) {
            String newState = swap(state, zeroPos, nextPos);
            dfs(newState, visited, nextPos, moves + 1);
        }
    }

    private String swap(String str, int i, int j) {
        StringBuilder sb = new StringBuilder(str);
        sb.setCharAt(i, str.charAt(j));
        sb.setCharAt(j, str.charAt(i));
        return sb.toString();
    }
}
```

---

## Complexity Analysis — DFS

Let:

- `m` = number of rows
- `n` = number of columns

Here these are fixed as `2` and `3`, but we keep the notation for clarity.

### Time Complexity

There are at most:

```text
(m × n)!
```

possible board states.

In DFS, states may be reached many times through different paths before pruning helps.

Each new state generation involves string manipulation of length `m × n`.

So the given analysis is:

```text
O((m × n)! × (m × n)^2)
```

For this specific puzzle, that is still manageable because `m × n = 6`.

---

### Space Complexity

The visited map can store up to:

```text
(m × n)!
```

states.

The recursion stack can also become deep.

So space complexity is:

```text
O((m × n)!)
```

---

# Approach 2: Breadth-First Search (BFS)

## Intuition

This is the correct and standard shortest-path approach.

Because each move has equal cost (`1`), BFS guarantees:

> the first time we reach the solved state, we have found the minimum number of moves

This is exactly what the problem asks.

---

## BFS State

We again use the flattened board string as a state.

We maintain:

- a queue of states to process
- a visited set to avoid revisiting states
- a `moves` counter for BFS levels

Each BFS level corresponds to all states reachable in exactly that many moves.

---

## BFS Algorithm

1. Convert the board to the start string.
2. If the start string is already the target, answer is `0`.
3. Put the start string into:
   - the queue
   - the visited set
4. Repeatedly:
   - process all states in the current queue level
   - if any state equals the target, return the current move count
   - otherwise, generate all neighboring states by swapping `0`
   - enqueue unvisited neighbors
5. If BFS ends without reaching the target, return `-1`

---

## Why BFS Guarantees the Minimum Moves

BFS explores:

- all states at distance 0
- then all states at distance 1
- then all states at distance 2
- and so on

So the first time the target appears, that depth is the minimum number of moves needed.

This property is what makes BFS the ideal choice here.

---

## Java Implementation — BFS

```java
import java.util.*;

class Solution {

    public int slidingPuzzle(int[][] board) {
        int[][] directions = new int[][] {
            { 1, 3 },
            { 0, 2, 4 },
            { 1, 5 },
            { 0, 4 },
            { 1, 3, 5 },
            { 2, 4 },
        };

        String target = "123450";
        StringBuilder startState = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                startState.append(board[i][j]);
            }
        }

        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(startState.toString());
        visited.add(startState.toString());

        int moves = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            while (size-- > 0) {
                String currentState = queue.poll();

                if (currentState.equals(target)) {
                    return moves;
                }

                int zeroPos = currentState.indexOf('0');
                for (int newPos : directions[zeroPos]) {
                    String nextState = swap(currentState, zeroPos, newPos);

                    if (visited.contains(nextState)) continue;

                    visited.add(nextState);
                    queue.add(nextState);
                }
            }
            moves++;
        }

        return -1;
    }

    private String swap(String str, int i, int j) {
        StringBuilder sb = new StringBuilder(str);
        sb.setCharAt(i, str.charAt(j));
        sb.setCharAt(j, str.charAt(i));
        return sb.toString();
    }
}
```

---

## Complexity Analysis — BFS

Let:

- `m` = number of rows
- `n` = number of columns

Again these are fixed here, but we keep the general expression.

### Time Complexity

There are at most:

```text
(m × n)!
```

possible states.

BFS processes each state at most once.

Generating a neighbor requires swapping in a string of length `m × n`, which costs `O(m × n)`.

So the total time complexity is:

```text
O((m × n)! × (m × n))
```

For the actual board size:

```text
6! × 6 = 720 × 6
```

which is very small.

---

### Space Complexity

The visited set and queue together may store up to all reachable states:

```text
O((m × n)!)
```

So the space complexity is:

```text
O((m × n)!)
```

---

# Why Flattening to a String Is Convenient

Using a string instead of a 2D array for each state is very practical because:

- it is hashable
- easy to store in a `Set` or `Map`
- easy to compare to the target state
- easy to generate neighbors with a simple swap

The board is tiny, so converting and swapping strings is perfectly acceptable.

---

# Common Mistakes

## 1. Using DFS and assuming the first solution is minimal

DFS does not guarantee minimum moves.

Only BFS gives shortest path in an unweighted graph.

---

## 2. Forgetting to track visited states

Without a visited set, the search will cycle infinitely because moves can undo each other.

---

## 3. Using the wrong adjacency mapping for the zero tile

The valid swaps depend on the flattened position of `0`.

For example:

- index `0` can swap with `1` and `3`
- index `1` can swap with `0`, `2`, and `4`

This mapping must be correct.

---

## 4. Not checking for unsolvable boards

If BFS exhausts all reachable states without finding `"123450"`, return `-1`.

---

# Comparing DFS and BFS

## DFS

### Strengths

- simple brute-force exploration
- works because the state space is tiny

### Weaknesses

- does not guarantee shortest path efficiently
- revisits many states through longer routes
- worse practical performance

---

## BFS

### Strengths

- naturally solves shortest-path problems
- explores by move count level
- first time target is found is optimal
- standard solution for this puzzle

### Weaknesses

- still needs storage for visited states and queue, though tiny here

---

# Final Summary

## State Representation

Flatten the `2 x 3` board into a string of length 6.

Example:

```text
[[1,2,3],[4,0,5]] -> "123405"
```

Goal:

```text
"123450"
```

---

## Key Graph Idea

- each board configuration = a node
- each legal swap of `0` = an edge
- find the shortest path from start to target

---

## Best Approach

Breadth-First Search (BFS)

Why?

Because the graph is unweighted and BFS guarantees the minimum number of moves.

---

## Complexities

### DFS

- Time: `O((m × n)! × (m × n)^2)`
- Space: `O((m × n)!)`

### BFS

- Time: `O((m × n)! × (m × n))`
- Space: `O((m × n)!)`

For this problem, since `m × n = 6`, the actual number of states is at most `720`.

---

# Best Final Java Solution

```java
import java.util.*;

class Solution {

    public int slidingPuzzle(int[][] board) {
        int[][] directions = new int[][] {
            { 1, 3 },
            { 0, 2, 4 },
            { 1, 5 },
            { 0, 4 },
            { 1, 3, 5 },
            { 2, 4 },
        };

        String target = "123450";
        StringBuilder startState = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                startState.append(board[i][j]);
            }
        }

        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        String start = startState.toString();

        queue.add(start);
        visited.add(start);

        int moves = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();

            while (size-- > 0) {
                String currentState = queue.poll();

                if (currentState.equals(target)) {
                    return moves;
                }

                int zeroPos = currentState.indexOf('0');
                for (int newPos : directions[zeroPos]) {
                    String nextState = swap(currentState, zeroPos, newPos);

                    if (visited.contains(nextState)) {
                        continue;
                    }

                    visited.add(nextState);
                    queue.add(nextState);
                }
            }

            moves++;
        }

        return -1;
    }

    private String swap(String str, int i, int j) {
        StringBuilder sb = new StringBuilder(str);
        sb.setCharAt(i, str.charAt(j));
        sb.setCharAt(j, str.charAt(i));
        return sb.toString();
    }
}
```

This is the standard optimal BFS solution for the 2 x 3 Sliding Puzzle problem.
