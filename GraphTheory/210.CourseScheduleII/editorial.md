# LeetCode 210 — Course Schedule II

## Problem Summary

Given `numCourses` courses (labeled `0` to `numCourses - 1`) and a list of prerequisite pairs
`[a, b]` meaning _"take b before a"_, return a valid ordering to complete all courses.
Return an empty array if it is impossible (i.e., a cycle exists).

This is a classic **Topological Sort** problem on a Directed Graph.

---

## Approach 1 — BFS (Kahn's Algorithm / In-degree Method)

### Intuition

Every course that has no prerequisites can be taken immediately.
After "taking" it, reduce the in-degree of courses that depended on it.
Whenever a course's in-degree drops to zero, it becomes available.
If we finish processing all courses, we have a valid order.
If we can't (a cycle exists), return `[]`.

### Algorithm Steps

1. Build an adjacency list and compute in-degree for every node.
2. Push all nodes with in-degree 0 into a queue.
3. Dequeue a node, add it to the result, and decrement in-degree of its neighbours.
4. Push neighbours whose in-degree becomes 0.
5. If `result.size() == numCourses`, return it; otherwise return `[]`.

### Complexity

|       | Value    |
| ----- | -------- |
| Time  | O(V + E) |
| Space | O(V + E) |

### Code

```java
import java.util.*;

class Solution {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        // Build adjacency list and in-degree array
        List<List<Integer>> adj = new ArrayList<>();
        int[] inDegree = new int[numCourses];

        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());

        for (int[] pre : prerequisites) {
            adj.get(pre[1]).add(pre[0]); // edge: pre[1] -> pre[0]
            inDegree[pre[0]]++;
        }

        // Start with all courses that have no prerequisites
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) queue.offer(i);
        }

        int[] order = new int[numCourses];
        int idx = 0;

        while (!queue.isEmpty()) {
            int course = queue.poll();
            order[idx++] = course;

            for (int next : adj.get(course)) {
                inDegree[next]--;
                if (inDegree[next] == 0) queue.offer(next);
            }
        }

        // If we couldn't process all courses, a cycle exists
        return idx == numCourses ? order : new int[0];
    }
}
```

### Dry Run Example

```
numCourses = 4
prerequisites = [[1,0],[2,0],[3,1],[3,2]]

Graph:  0 → 1 → 3
        0 → 2 → 3

inDegree: [0, 1, 1, 2]

Queue start: [0]
Take 0  → decrement 1,2  → inDegree: [0,0,0,2] → enqueue 1,2
Take 1  → decrement 3    → inDegree: [0,0,0,1]
Take 2  → decrement 3    → inDegree: [0,0,0,0] → enqueue 3
Take 3

Result: [0, 1, 2, 3] ✓
```

---

## Approach 2 — DFS with Cycle Detection (Three-Color Marking)

### Intuition

Perform DFS on the graph. A valid topological order is the **reverse post-order** of a DFS:
push a node to the result only after all its descendants have been visited.

Track three states for each node:

- `0` — **WHITE** (unvisited)
- `1` — **GRAY** (currently being explored — part of the current DFS path)
- `2` — **BLACK** (fully processed)

If we ever visit a GRAY node again, we've found a back-edge → **cycle detected**.

### Algorithm Steps

1. Build adjacency list.
2. DFS each unvisited node; return `false` immediately if a cycle is detected.
3. After exploring all neighbours, push the current node to a stack.
4. Pop the stack to get topological order.

### Complexity

|       | Value    |
| ----- | -------- |
| Time  | O(V + E) |
| Space | O(V + E) |

### Code

```java
import java.util.*;

class Solution {
    private int[] color;        // 0=white, 1=gray, 2=black
    private int[] adj[];        // adjacency list as arrays (fast)
    private int[] result;
    private int idx;
    private boolean hasCycle;

    public int[] findOrder(int numCourses, int[][] prerequisites) {
        color  = new int[numCourses];
        result = new int[numCourses];
        idx    = numCourses - 1; // fill from the back (reverse post-order)

        // Build adjacency list
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) graph.add(new ArrayList<>());
        for (int[] pre : prerequisites) graph.get(pre[1]).add(pre[0]);

        // Convert to arrays for speed (optional but clean)
        adj = new int[numCourses][];
        for (int i = 0; i < numCourses; i++) {
            adj[i] = graph.get(i).stream().mapToInt(Integer::intValue).toArray();
        }

        for (int i = 0; i < numCourses; i++) {
            if (color[i] == 0) dfs(i);
            if (hasCycle) return new int[0];
        }

        return result;
    }

    private void dfs(int node) {
        if (hasCycle) return;
        color[node] = 1; // GRAY — currently visiting

        for (int neighbour : adj[node]) {
            if (color[neighbour] == 1) { // back-edge → cycle!
                hasCycle = true;
                return;
            }
            if (color[neighbour] == 0) dfs(neighbour);
        }

        color[node] = 2;       // BLACK — fully processed
        result[idx--] = node;  // post-order: append after all descendants
    }
}
```

### Dry Run Example

```
numCourses = 3
prerequisites = [[1,0],[2,1]]   Graph: 0 → 1 → 2

DFS(0): color[0]=GRAY
  DFS(1): color[1]=GRAY
    DFS(2): color[2]=GRAY
      no neighbours
    color[2]=BLACK → result[2] = 2
  color[1]=BLACK → result[1] = 1
color[0]=BLACK → result[0] = 0

Result: [0, 1, 2] ✓
```

---

## Approach 3 — Iterative DFS (Explicit Stack, No Recursion)

### Intuition

Same logic as Approach 2 but replaces the call stack with an explicit stack.
Useful when `numCourses` is very large and recursion depth could overflow the JVM stack.

Each stack entry stores `(node, iterator_position)` so we can resume processing
a node's neighbours after returning from a "recursive" call.

### Complexity

|       | Value    |
| ----- | -------- |
| Time  | O(V + E) |
| Space | O(V + E) |

### Code

```java
import java.util.*;

class Solution {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
        for (int[] pre : prerequisites) adj.get(pre[1]).add(pre[0]);

        int[] color = new int[numCourses]; // 0=white, 1=gray, 2=black
        int[] result = new int[numCourses];
        int idx = numCourses - 1;

        // Stack stores [node, neighbourIndex]
        Deque<int[]> stack = new ArrayDeque<>();

        for (int start = 0; start < numCourses; start++) {
            if (color[start] != 0) continue;

            stack.push(new int[]{start, 0});
            color[start] = 1; // GRAY

            while (!stack.isEmpty()) {
                int[] top   = stack.peek();
                int node    = top[0];
                int nIdx    = top[1];
                List<Integer> neighbours = adj.get(node);

                if (nIdx < neighbours.size()) {
                    top[1]++; // advance iterator
                    int nb = neighbours.get(nIdx);

                    if (color[nb] == 1) return new int[0]; // cycle
                    if (color[nb] == 0) {
                        color[nb] = 1;
                        stack.push(new int[]{nb, 0});
                    }
                } else {
                    // All neighbours processed — finalize this node
                    stack.pop();
                    color[node] = 2;
                    result[idx--] = node;
                }
            }
        }

        return result;
    }
}
```

---

## Approach 4 — BFS with Parallel Scheduling (Level-by-Level)

### Intuition

A variation of Kahn's Algorithm that builds the order **level by level** (semester by semester).
All courses in the same level can be taken in parallel.
Useful when you also need to know the _minimum number of semesters_.

### Code

```java
import java.util.*;

class Solution {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        int[] inDegree = new int[numCourses];

        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
        for (int[] pre : prerequisites) {
            adj.get(pre[1]).add(pre[0]);
            inDegree[pre[0]]++;
        }

        Queue<Integer> current = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) current.offer(i);
        }

        int[] order = new int[numCourses];
        int idx = 0;

        // Process level by level (each level = one semester)
        while (!current.isEmpty()) {
            Queue<Integer> next = new LinkedList<>();

            while (!current.isEmpty()) {
                int course = current.poll();
                order[idx++] = course;

                for (int nb : adj.get(course)) {
                    if (--inDegree[nb] == 0) next.offer(nb);
                }
            }
            current = next;
        }

        return idx == numCourses ? order : new int[0];
    }
}
```

---

## Comparison of All Approaches

| Approach            | Algorithm         | Time   | Space  | Best For                         |
| ------------------- | ----------------- | ------ | ------ | -------------------------------- |
| 1 — BFS Kahn's      | In-degree queue   | O(V+E) | O(V+E) | Simplest, most intuitive         |
| 2 — DFS Recursive   | Three-color DFS   | O(V+E) | O(V+E) | Natural, concise code            |
| 3 — DFS Iterative   | Explicit stack    | O(V+E) | O(V+E) | Large inputs (no stack overflow) |
| 4 — BFS Level-order | Semester batching | O(V+E) | O(V+E) | When parallel scheduling matters |

---

## Edge Cases

```java
// No prerequisites — any order works
findOrder(3, new int[][]{})  // e.g. [0, 1, 2]

// Cycle — impossible
findOrder(2, new int[][]{{0,1},{1,0}})  // []

// Single course
findOrder(1, new int[][]{})  // [0]

// Self-loop
findOrder(2, new int[][]{{0,0}})  // []
```

---

## Key Takeaway

> Topological sort works **if and only if the directed graph is a DAG** (no cycles).
> BFS (Kahn's) is usually preferred in interviews for clarity.
> DFS is preferred when you need to detect cycles inline or build a recursive solution.
