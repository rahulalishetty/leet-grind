## Multiple Java Approaches with Detailed Explanation

## Problem Restatement

We are given access to a hidden grid through an interactive API called `GridMaster`.

We do **not** know:

- the grid dimensions
- the starting position
- the target position
- which cells are blocked

We can only ask the robot these questions:

- `canMove(direction)` — whether moving in a direction is possible
- `move(direction)` — actually move there and get the cost of the destination cell
- `isTarget()` — whether the current cell is the target

Our goal is to compute the **minimum total cost** required to move from the unknown start cell to the unknown target cell.

If the target is unreachable, return `-1`.

---

# Core Difficulty

This problem has **two layers**:

## Layer 1: Discover the hidden graph

Because the grid is hidden, we must first explore the reachable area and reconstruct enough information about it.

## Layer 2: Compute the minimum-cost path

Once the reachable region is known, the problem becomes a standard shortest path problem on a weighted graph.

So most correct solutions have the same high-level structure:

1. use **DFS / backtracking exploration** to reveal the reachable cells
2. then run **Dijkstra's algorithm** on the discovered graph

---

# Why DFS Exploration Is Necessary

The `GridMaster` robot is stateful:

- when we call `move('U')`, the robot physically changes position
- so if we explore one branch, we must later move back to continue exploring other branches

That makes backtracking essential.

DFS is the most natural way to do this because:

- it explores a path deeply
- then returns to the parent
- then tries the next direction

This exactly matches the way we must interact with the robot.

---

# Key Observation About Costs

The cost belongs to the **destination cell** that we move into.

So if we move from cell `A` to cell `B`, the edge cost is:

```text
cost(B)
```

The start cell contributes no cost before any movement.

This means after exploration, every reachable cell can be treated as a node, and moving into a neighbor has a weight equal to that neighbor’s discovered cost.

---

# Coordinate Normalization Trick

Because the real coordinates are hidden, we usually assign artificial coordinates while exploring.

A common trick is:

- treat the unknown start cell as `(0, 0)`
- moving:
  - up -> `(-1, 0)`
  - down -> `(1, 0)`
  - left -> `(0, -1)`
  - right -> `(0, 1)`

These coordinates are only for our internal bookkeeping.

---

# Recommended Main Approach

The strongest and most standard solution is:

## Approach 1

**DFS to build the reachable graph + Dijkstra to compute the minimum cost**

This is the best practical answer and the one most interviewers would expect.

After that, we will also discuss:

## Approach 2

**DFS to build a weighted grid + priority-queue shortest path on the discovered cells**

This is very similar algorithmically, but organized slightly differently.

## Approach 3

**Why plain BFS after exploration is not enough, and when it can be adapted**

This is included mainly as a contrast, because the graph is weighted.

---

# Approach 1: DFS Exploration + Dijkstra on the Discovered Graph

## Intuition

We first use DFS to explore every reachable cell from the starting position.

During exploration:

- if `canMove(dir)` is true, we may move into that neighboring cell
- `move(dir)` returns the cost of entering that cell
- after exploring that neighbor recursively, we must move back using the opposite direction

While doing this, we build:

- a map from coordinates to cell entry cost
- the target’s normalized coordinate if found

After the hidden reachable region is discovered, we run Dijkstra from `(0, 0)` to the target coordinate.

---

## Data Structures

We will use:

### 1. `Map<Integer, Integer> costMap`

Stores the cost of entering each discovered cell.

We can encode a 2D coordinate `(x, y)` into a single integer key.

### 2. `Set<Integer> visited`

Used during DFS exploration.

### 3. target coordinate

Store where the target was discovered.

### 4. Priority queue

Used in Dijkstra to compute the minimum cost path.

---

## Direction Utilities

We will keep four synchronized arrays:

- direction letters for `GridMaster`
- reverse direction letters for backtracking
- row/column deltas

Example:

```java
char[] DIRS = {'U', 'D', 'L', 'R'};
char[] BACK = {'D', 'U', 'R', 'L'};
int[] dx = {-1, 1, 0, 0};
int[] dy = {0, 0, -1, 1};
```

---

## Java Solution

```java
/**
 * // This is the GridMaster's API interface.
 * // You should not implement it, or speculate about its implementation
 * interface GridMaster {
 *     boolean canMove(char direction);
 *     int move(char direction);
 *     boolean isTarget();
 * }
 */

class Solution {
    private static final int SHIFT = 201;   // enough since constraints <= 100
    private static final int OFFSET = 100;  // normalize around start
    private final char[] DIRS = {'U', 'D', 'L', 'R'};
    private final char[] BACK = {'D', 'U', 'R', 'L'};
    private final int[] dx = {-1, 1, 0, 0};
    private final int[] dy = {0, 0, -1, 1};

    private Map<Integer, Integer> costMap = new HashMap<>();
    private Set<Integer> seen = new HashSet<>();
    private int targetKey = -1;

    public int findShortestPath(GridMaster master) {
        int startKey = encode(0, 0);
        costMap.put(startKey, 0);

        dfs(master, 0, 0);

        if (targetKey == -1) {
            return -1;
        }

        return dijkstra(startKey, targetKey);
    }

    private void dfs(GridMaster master, int x, int y) {
        int key = encode(x, y);
        seen.add(key);

        if (master.isTarget()) {
            targetKey = key;
        }

        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            int nKey = encode(nx, ny);

            if (!master.canMove(DIRS[i]) || seen.contains(nKey)) {
                continue;
            }

            int moveCost = master.move(DIRS[i]);
            costMap.put(nKey, moveCost);

            dfs(master, nx, ny);

            // backtrack to the original position
            master.move(BACK[i]);
        }
    }

    private int dijkstra(int startKey, int targetKey) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        Map<Integer, Integer> dist = new HashMap<>();

        pq.offer(new int[]{startKey, 0});
        dist.put(startKey, 0);

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int key = curr[0];
            int currDist = curr[1];

            if (key == targetKey) {
                return currDist;
            }

            if (currDist > dist.getOrDefault(key, Integer.MAX_VALUE)) {
                continue;
            }

            int[] pos = decode(key);
            int x = pos[0], y = pos[1];

            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];
                int nKey = encode(nx, ny);

                if (!costMap.containsKey(nKey)) {
                    continue;
                }

                int nextDist = currDist + costMap.get(nKey);
                if (nextDist < dist.getOrDefault(nKey, Integer.MAX_VALUE)) {
                    dist.put(nKey, nextDist);
                    pq.offer(new int[]{nKey, nextDist});
                }
            }
        }

        return -1;
    }

    private int encode(int x, int y) {
        return (x + OFFSET) * SHIFT + (y + OFFSET);
    }

    private int[] decode(int key) {
        int x = key / SHIFT - OFFSET;
        int y = key % SHIFT - OFFSET;
        return new int[]{x, y};
    }
}
```

---

## Detailed Explanation of the DFS Phase

### Step 1: Start at `(0, 0)`

We pretend the unknown start position is coordinate `(0, 0)`.

```java
int startKey = encode(0, 0);
costMap.put(startKey, 0);
dfs(master, 0, 0);
```

The cost of the start cell is `0` in our shortest-path formulation because the problem says the starting cell’s cost is not charged before the first move.

---

### Step 2: Mark current cell visited

```java
int key = encode(x, y);
seen.add(key);
```

This prevents infinite revisits while exploring.

---

### Step 3: Check whether current cell is target

```java
if (master.isTarget()) {
    targetKey = key;
}
```

So once the target is discovered, we remember its normalized coordinate.

---

### Step 4: Explore all four directions

```java
for (int i = 0; i < 4; i++) {
    ...
}
```

For each direction:

- compute neighbor coordinate
- check `canMove`
- if not already seen, move into it

---

### Step 5: Record the cost of the destination cell

```java
int moveCost = master.move(DIRS[i]);
costMap.put(nKey, moveCost);
```

This is the key weighted-edge detail.
The returned `moveCost` is the cost of entering that neighboring cell.

---

### Step 6: Recursively explore neighbor

```java
dfs(master, nx, ny);
```

---

### Step 7: Backtrack

```java
master.move(BACK[i]);
```

This is crucial.

Without moving back, the robot would remain in the neighbor cell, and future exploration from the original cell would become impossible.

---

## Detailed Explanation of the Dijkstra Phase

Once all reachable cells are known, we run shortest path from the start to the target.

### Why Dijkstra?

The graph is weighted because different cells have different entry costs.

So ordinary BFS is not correct.

---

### Priority Queue State

```java
pq.offer(new int[]{startKey, 0});
```

Each queue element stores:

- encoded cell key
- minimum known cost to reach it

---

### Relaxation Rule

For each discovered neighbor:

```java
int nextDist = currDist + costMap.get(nKey);
```

Because moving into the neighbor costs `costMap.get(nKey)`.

If this gives a better path than before, update it and push into the heap.

---

## Correctness Summary for Approach 1

This works because:

1. DFS with backtracking discovers every reachable cell and its correct movement cost.
2. The discovered reachable cells form an explicit weighted graph.
3. Dijkstra correctly computes the minimum-cost path in any graph with nonnegative edge weights.

Since costs are nonnegative, Dijkstra is valid.

---

## Complexity Analysis for Approach 1

Let `K` be the number of reachable cells.

### DFS exploration

Each reachable cell is visited once, and each direction is checked a constant number of times.

So exploration is:

```text
O(K)
```

### Dijkstra

Each cell has at most 4 neighbors, so the number of edges is `O(K)`.

Using a heap, Dijkstra is:

```text
O(K log K)
```

### Total

```text
O(K log K)
```

### Space

- cost map: `O(K)`
- visited set: `O(K)`
- priority queue / dist map: `O(K)`

So space is:

```text
O(K)
```

---

# Approach 2: DFS Exploration + Explicit Weighted Adjacency Graph + Dijkstra

## Intuition

This approach is conceptually almost the same as Approach 1, but instead of storing just cell costs and regenerating neighbors by coordinate arithmetic during Dijkstra, we explicitly build the weighted adjacency graph while exploring.

That can make the second phase feel cleaner, because Dijkstra runs over a true graph structure.

---

## Main Idea

During DFS exploration:

- whenever we successfully move from cell `u` to cell `v`
- we add an edge from `u` to `v` with weight `cost(v)`
- and because movement is reversible between open cells, we also add the reverse edge from `v` to `u` with weight `cost(u)`

Then Dijkstra runs directly over this adjacency graph.

---

## Java Solution

```java
/**
 * // This is the GridMaster's API interface.
 * // You should not implement it, or speculate about its implementation
 * interface GridMaster {
 *     boolean canMove(char direction);
 *     int move(char direction);
 *     boolean isTarget();
 * }
 */

class Solution2 {
    private static final int SHIFT = 201;
    private static final int OFFSET = 100;

    private final char[] DIRS = {'U', 'D', 'L', 'R'};
    private final char[] BACK = {'D', 'U', 'R', 'L'};
    private final int[] dx = {-1, 1, 0, 0};
    private final int[] dy = {0, 0, -1, 1};

    private Set<Integer> seen = new HashSet<>();
    private Map<Integer, Integer> nodeCost = new HashMap<>();
    private Map<Integer, List<int[]>> graph = new HashMap<>();
    private int target = -1;

    public int findShortestPath(GridMaster master) {
        int start = encode(0, 0);
        nodeCost.put(start, 0);
        graph.put(start, new ArrayList<>());

        dfs(master, 0, 0);

        if (target == -1) {
            return -1;
        }

        return shortestPath(start, target);
    }

    private void dfs(GridMaster master, int x, int y) {
        int key = encode(x, y);
        seen.add(key);

        if (master.isTarget()) {
            target = key;
        }

        graph.putIfAbsent(key, new ArrayList<>());

        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            int nextKey = encode(nx, ny);

            if (!master.canMove(DIRS[i])) {
                continue;
            }

            if (!seen.contains(nextKey)) {
                int enterCost = master.move(DIRS[i]);
                nodeCost.put(nextKey, enterCost);
                graph.putIfAbsent(nextKey, new ArrayList<>());

                // forward edge: entering next costs enterCost
                graph.get(key).add(new int[]{nextKey, enterCost});
                // reverse edge: returning to current costs nodeCost(current)
                graph.get(nextKey).add(new int[]{key, nodeCost.get(key)});

                dfs(master, nx, ny);
                master.move(BACK[i]);
            } else {
                // if already discovered, add graph edge if not already necessary conceptually
                graph.get(key).add(new int[]{nextKey, nodeCost.get(nextKey)});
            }
        }
    }

    private int shortestPath(int start, int target) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        Map<Integer, Integer> dist = new HashMap<>();

        pq.offer(new int[]{start, 0});
        dist.put(start, 0);

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int node = curr[0];
            int d = curr[1];

            if (node == target) {
                return d;
            }

            if (d > dist.getOrDefault(node, Integer.MAX_VALUE)) {
                continue;
            }

            for (int[] edge : graph.getOrDefault(node, Collections.emptyList())) {
                int next = edge[0];
                int w = edge[1];
                int nd = d + w;

                if (nd < dist.getOrDefault(next, Integer.MAX_VALUE)) {
                    dist.put(next, nd);
                    pq.offer(new int[]{next, nd});
                }
            }
        }

        return -1;
    }

    private int encode(int x, int y) {
        return (x + OFFSET) * SHIFT + (y + OFFSET);
    }
}
```

---

## Differences from Approach 1

Compared with Approach 1:

- Approach 1 stores cell costs and infers edges from coordinate neighbors during Dijkstra.
- Approach 2 stores the actual weighted adjacency graph explicitly.

Both are valid.

---

## A Subtle Detail About Reverse Edge Weights

If moving from `u` to `v` costs the destination cell’s cost, then the reverse move from `v` to `u` costs the cost of entering `u`.

That is why:

```java
graph.get(nextKey).add(new int[]{key, nodeCost.get(key)});
```

This is an important modeling detail.

---

## Complexity Analysis for Approach 2

Same asymptotic complexity as Approach 1.

Let `K` = number of reachable cells.

### Exploration

```text
O(K)
```

### Dijkstra

```text
O(K log K)
```

### Space

```text
O(K)
```

---

# Approach 3: Why Plain BFS Is Not Correct After Exploration

## Intuition

After reconstructing the hidden reachable graph, it may be tempting to run BFS to find the shortest path.

That would be incorrect in general.

Why?

Because BFS minimizes:

- number of edges

But this problem asks us to minimize:

- total movement cost

Those are not the same when edge weights differ.

---

## Counterexample

Suppose from the start we have two possible ways to reach target:

### Path A

- 2 moves
- costs: `100 + 100`
- total = `200`

### Path B

- 4 moves
- costs: `1 + 1 + 1 + 1`
- total = `4`

BFS would choose Path A because it has fewer steps.

But the correct answer is Path B because it has lower total cost.

So plain BFS is wrong.

---

## When BFS Would Be Correct

BFS would only be correct if all movement costs were equal, for example:

- every empty cell had cost `1`

Then minimizing number of moves would match minimizing total cost.

But here costs vary from cell to cell, so we need Dijkstra.

---

# What Changes If Movement Costs Were Only 0 or 1?

If the edge weights were only:

- `0`
- `1`

then we could use **0-1 BFS** instead of Dijkstra.

But in this problem cell costs can be up to 100, so ordinary Dijkstra remains the correct standard tool.

---

# Why Backtracking DFS Is Better Than Backtracking BFS for Exploration

Since the robot physically moves during exploration, we must always keep track of where it currently is.

DFS is better suited for this because:

- move into a neighbor
- recursively explore
- move back

That is a natural recursion pattern.

A BFS-style exploration with a physical robot would be much more awkward because the robot would constantly need to be repositioned across many frontier states.

So even though shortest-path uses Dijkstra later, the hidden-grid discovery phase is most naturally done with DFS.

---

# Follow-up: What If Each Node Can Be Visited Only Once?

This changes the problem significantly.

In the original problem, the shortest path is allowed to revisit nodes if needed.

If revisits are forbidden, then we are now looking for:

> a minimum-cost simple path from the start to the target

This is much harder.

On a general graph, shortest simple path with such constraints becomes combinatorially difficult.

Possible changes:

- state would need to include which nodes have been visited
- that suggests **bitmask DP** if the graph is small
- or exponential backtracking / branch-and-bound

However, in this problem `n` can be up to 100 (reachable cells also potentially large), so a generic simple-path DP would be infeasible.

So the original problem is tractable precisely because revisits are allowed and Dijkstra applies.

---

# Practical Interview Summary

If asked this in an interview, the best structure is:

## Step 1

Use DFS + backtracking with the `GridMaster` to discover the hidden reachable graph.

## Step 2

Once the graph is reconstructed, run Dijkstra from the normalized start to the discovered target.

This is the cleanest and most standard solution.

---

# Common Pitfalls

## 1. Forgetting to backtrack

After moving into a neighbor during DFS, you must move back.

Otherwise the robot state becomes inconsistent.

---

## 2. Using BFS for the weighted shortest path

The graph is weighted by destination cell costs, so BFS is not enough.

---

## 3. Charging the start cell cost

The problem explicitly says the starting cell cost is not charged before movement begins.

So the shortest path starts with cost `0`.

---

## 4. Modeling reverse edges incorrectly

If entering `v` costs `cost(v)`, then returning from `v` to `u` costs `cost(u)`, not `cost(v)`.

---

## 5. Not handling unreachable target

If DFS never encounters the target, return `-1` immediately.

---

# Final Takeaway

This problem is really a combination of:

1. **hidden graph exploration**
2. **shortest path on a weighted graph**

The main challenge is that the graph is not given explicitly, so we must reconstruct it by physically moving the robot and backtracking.

Once the graph is known, the rest is a textbook Dijkstra problem.

---

# Summary

## Best Approach

**DFS exploration + Dijkstra shortest path**

## Main Ideas

- normalize unknown start as `(0, 0)`
- use DFS with backtracking to discover all reachable cells
- record destination cell costs
- detect target location during DFS
- run Dijkstra on the reconstructed weighted graph

## Time Complexity

If `K` is the number of reachable cells:

```text
O(K log K)
```

## Space Complexity

```text
O(K)
```

## Java Approaches Included

1. DFS + cost-map + Dijkstra by coordinate neighbors
2. DFS + explicit weighted graph + Dijkstra
3. conceptual discussion of why BFS is insufficient
