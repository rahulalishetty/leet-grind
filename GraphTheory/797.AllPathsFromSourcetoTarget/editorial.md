# 797. All Paths From Source to Target — Java Solutions

Given a DAG of `n` nodes (`0` to `n-1`), find **all paths** from node `0` to node `n-1`.

---

## Approach 1: DFS + Backtracking (most natural)

Since it's a DAG, there's no risk of infinite loops, so we don't even need a "visited" set —
we just walk forward, and backtrack when we hit a dead end or reach the target.

**Idea:**

- Maintain a running path list, starting with `[0]`.
- At each node, try every neighbor.
- If the current node is `n-1`, save a copy of the path.
- Otherwise, recurse into each neighbor, then remove it from the path (backtrack) after returning.

```java
import java.util.*;

class Solution {
    public List<List<Integer>> allPathsSourceTarget(int[][] graph) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> path = new ArrayList<>();
        path.add(0);
        dfs(graph, 0, path, result);
        return result;
    }

    private void dfs(int[][] graph, int node, List<Integer> path, List<List<Integer>> result) {
        int target = graph.length - 1;

        // Base case: reached the target, record a copy of the current path
        if (node == target) {
            result.add(new ArrayList<>(path));
            return;
        }

        // Explore every outgoing edge from this node
        for (int next : graph[node]) {
            path.add(next);          // choose
            dfs(graph, next, path, result);  // explore
            path.remove(path.size() - 1);    // un-choose (backtrack)
        }
    }
}
```

**Complexity:**

- Time: `O(2^n * n)` in the worst case — there can be exponentially many paths in a DAG with up to `n` nodes each, and copying each path costs `O(n)`.
- Space: `O(n)` for recursion stack / current path (excluding the output).

---

## Approach 2: BFS with Path Queue

Instead of recursion, we can do an iterative BFS where each queue element is an **entire path so far**, not just a node. When a path's last node is the target, we save it; otherwise we extend it by one edge for each neighbor and push the new paths back onto the queue.

```java
import java.util.*;

class Solution {
    public List<List<Integer>> allPathsSourceTarget(int[][] graph) {
        List<List<Integer>> result = new ArrayList<>();
        int target = graph.length - 1;

        Queue<List<Integer>> queue = new LinkedList<>();
        List<Integer> start = new ArrayList<>();
        start.add(0);
        queue.offer(start);

        while (!queue.isEmpty()) {
            List<Integer> path = queue.poll();
            int lastNode = path.get(path.size() - 1);

            if (lastNode == target) {
                result.add(path);
                continue;
            }

            for (int next : graph[lastNode]) {
                List<Integer> newPath = new ArrayList<>(path);
                newPath.add(next);
                queue.offer(newPath);
            }
        }

        return result;
    }
}
```

**Complexity:**

- Time: `O(2^n * n)` — same reasoning as above, plus each path is copied when extended.
- Space: `O(2^n * n)` for the queue in the worst case, since many partial paths can be in flight simultaneously (higher memory footprint than DFS).

---

## Approach 3: DFS with Memoization (DP on subpaths)

We can think of this recursively in terms of "**all paths from node `i` to `n-1`**" as a subproblem: `solve(i)` = list of all paths starting at `i` and ending at `n-1`. Since the graph is a DAG, a node's answer never depends on itself, so we can safely cache results.

`solve(i) = { [i] }` if `i == n-1`, otherwise it's the union over every neighbor `j` of `[i] + path` for every `path` in `solve(j)`.

```java
import java.util.*;

class Solution {
    private Map<Integer, List<List<Integer>>> memo = new HashMap<>();
    private int target;

    public List<List<Integer>> allPathsSourceTarget(int[][] graph) {
        target = graph.length - 1;
        return solve(graph, 0);
    }

    private List<List<Integer>> solve(int[][] graph, int node) {
        if (memo.containsKey(node)) {
            return memo.get(node);
        }

        List<List<Integer>> paths = new ArrayList<>();

        if (node == target) {
            List<Integer> single = new ArrayList<>();
            single.add(node);
            paths.add(single);
        } else {
            for (int next : graph[node]) {
                for (List<Integer> subPath : solve(graph, next)) {
                    List<Integer> newPath = new ArrayList<>();
                    newPath.add(node);
                    newPath.addAll(subPath);
                    paths.add(newPath);
                }
            }
        }

        memo.put(node, paths);
        return paths;
    }
}
```

**Complexity:**

- Time: `O(2^n * n)` worst case (a complete DAG can still have exponentially many paths, so memoization doesn't reduce the _output_ size), but it avoids redundant re-exploration of shared subgraphs when many paths converge through the same node, which helps on graphs with heavy path-sharing.
- Space: `O(2^n * n)` to store all cached paths.

---

## Comparison

| Approach            | Time (worst case) | Space (worst case)        | Notes                                                          |
| ------------------- | ----------------- | ------------------------- | -------------------------------------------------------------- |
| DFS + Backtracking  | `O(2^n * n)`      | `O(n)` aux (excl. output) | Simplest, most memory-efficient, preferred in interviews       |
| BFS with Path Queue | `O(2^n * n)`      | `O(2^n * n)`              | Iterative, but stores many full paths in the queue at once     |
| DFS + Memoization   | `O(2^n * n)`      | `O(2^n * n)`              | Useful when subpaths are heavily reused across recursive calls |

For this problem, **Approach 1 (DFS + Backtracking)** is generally the best choice: it's simple,
easy to reason about, and only uses `O(n)` extra space beyond the output itself, since it mutates
one shared path list instead of copying paths at every step.

---

## Walkthrough on Example 1

`graph = [[1,2],[3],[3],[]]`, target = `3`

```
Start: path = [0]
  visit 1: path = [0,1]
    visit 3 (== target): save [0,1,3]
  backtrack: path = [0]
  visit 2: path = [0,2]
    visit 3 (== target): save [0,2,3]
  backtrack: path = [0]

Result: [[0,1,3],[0,2,3]]
```

This matches the expected output.
