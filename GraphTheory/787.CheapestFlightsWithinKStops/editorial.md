# 787. Cheapest Flights Within K Stops — Java Solutions

## Problem Recap

There are `n` cities and a list of directed flights `[from, to, price]`.
Find the cheapest price from `src` to `dst` using **at most `k` stops**
(i.e., at most `k + 1` edges / flights). Return `-1` if no such route exists.

Below are three different approaches, each with intuition, code, and
complexity analysis.

---

## Approach 1: Bellman-Ford (Dynamic Programming) — Recommended

### Intuition

"At most `k` stops" means "at most `k + 1` edges" in the path from `src`
to `dst`. This is exactly the structure Bellman-Ford relaxation handles
naturally: if we relax all edges `k + 1` times, `dist[v]` after `i`
relaxation rounds holds the cheapest cost to reach `v` using **at most
`i` edges**.

So we run the standard Bellman-Ford relaxation for exactly `k + 1`
rounds (not until convergence, since we deliberately want to cap the
number of edges used, not find the unrestricted shortest path).

**Key trick:** In each round, relax edges using a _copy_ of the previous
round's distance array. If we update `dist` in place while iterating,
we might use an edge relaxed _in the same round_, which would allow a
path with more edges than the round count intends.

### Code

```java
import java.util.Arrays;

class Solution {
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        // dist[v] = cheapest cost to reach v using at most (current round) edges
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        // We allow at most k stops => at most k + 1 edges => k + 1 relaxation rounds
        for (int round = 0; round <= k; round++) {
            // Copy so updates within this round don't leak into the same round
            int[] next = Arrays.copyOf(dist, n);

            for (int[] flight : flights) {
                int from = flight[0], to = flight[1], price = flight[2];

                if (dist[from] == Integer.MAX_VALUE) continue; // 'from' unreachable so far

                if (dist[from] + price < next[to]) {
                    next[to] = dist[from] + price;
                }
            }

            dist = next;
        }

        return dist[dst] == Integer.MAX_VALUE ? -1 : dist[dst];
    }
}
```

### Walkthrough (Example 1)

`flights = [[0,1,100],[1,2,100],[2,0,100],[1,3,600],[2,3,200]]`, `src=0`, `dst=3`, `k=1`

- `dist = [0, INF, INF, INF]`
- Round 0 (allow 1 edge): relax all edges once using round-0 `dist`.
  - `0->1`: `dist[1] = 100`
  - `1->3`: `dist[1]` was INF at start of round, so skipped (uses the _old_ dist)
  - Result: `dist = [0, 100, INF, INF]`
- Round 1 (allow 2 edges): relax again using the array from round 0.
  - `0->1`: `100` (no improvement)
  - `1->2`: `dist[1] + 100 = 200`
  - `1->3`: `dist[1] + 600 = 700`
  - `2->3`: `dist[2]` was INF at start of this round, skipped
  - Result: `dist = [0, 100, 200, 700]`
- Loop ends (`k = 1`, so 2 rounds total: round 0 and round 1).
- Answer: `dist[3] = 700` ✅

### Complexity

- **Time:** `O(k * E)` where `E` is the number of flights (edges).
- **Space:** `O(n)` for the distance arrays.

---

## Approach 2: BFS / Level-by-Level Relaxation (Bellman-Ford variant using an adjacency list)

### Intuition

This is functionally the same idea as Approach 1, but instead of scanning
the flat `flights` array each round, we pre-build an adjacency list and
process it level by level (level = number of edges used so far), which
more directly mirrors a **BFS over "at most k+1 edges"**. This can be
slightly more cache-friendly and makes the "layer" concept explicit,
which is useful if you want to extend the problem (e.g., track the
actual path).

### Code

```java
import java.util.*;

class Solution {
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        // Build adjacency list: city -> list of [neighbor, price]
        List<List<int[]>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
        for (int[] f : flights) {
            graph.get(f[0]).add(new int[]{f[1], f[2]});
        }

        int[] cost = new int[n];
        Arrays.fill(cost, Integer.MAX_VALUE);
        cost[src] = 0;

        // BFS queue holds [city, costSoFar]; process one "stop layer" at a time
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{src, 0});

        int stops = 0;
        int best = Integer.MAX_VALUE;

        while (!queue.isEmpty() && stops <= k) {
            int size = queue.size();
            // Snapshot so relaxations in this layer don't affect this same layer
            int[] costSnapshot = Arrays.copyOf(cost, n);

            for (int i = 0; i < size; i++) {
                int[] cur = queue.poll();
                int city = cur[0], curCost = cur[1];

                if (curCost > costSnapshot[city]) continue; // stale entry, skip

                for (int[] edge : graph.get(city)) {
                    int next = edge[0], price = edge[1];
                    int newCost = curCost + price;

                    if (newCost < cost[next]) {
                        cost[next] = newCost;
                        queue.offer(new int[]{next, newCost});
                    }
                }
            }
            stops++;
        }

        return cost[dst] == Integer.MAX_VALUE ? -1 : cost[dst];
    }
}
```

### Complexity

- **Time:** `O(k * E)` in the worst case (each layer can re-examine edges out of many nodes).
- **Space:** `O(n + E)` for the graph and queue.

> Note: This BFS-with-layers approach is really Bellman-Ford in disguise.
> A **plain Dijkstra by cost alone is not directly correct here**, because
> Dijkstra greedily finalizes the globally cheapest node first, but that
> node might have used too many stops — a more expensive-but-fewer-stops
> path might still be needed later. That's why Approach 3 adds "stops
> used" into the state instead of just running vanilla Dijkstra.

---

## Approach 3: Modified Dijkstra (State = (city, stopsUsed))

### Intuition

Vanilla Dijkstra tracks the best cost _per city_. Here we need the best
cost _per (city, number of stops used)_ pair, because a path that
reaches a city more expensively might still be valid if it did so with
fewer stops, leaving room to reach `dst` within the limit.

We use a min-heap ordered by cost, where each heap entry is
`(cost, city, stopsUsed)`. We pop the cheapest entry; the first time we
pop `dst`, that is the answer (standard Dijkstra correctness argument
still holds because we always expand the globally cheapest frontier
node first). We only continue expanding neighbors if `stopsUsed <= k`.

### Code

```java
import java.util.*;

class Solution {
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        List<List<int[]>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
        for (int[] f : flights) {
            graph.get(f[0]).add(new int[]{f[1], f[2]});
        }

        // Min-heap ordered by cost: {cost, city, stopsUsed}
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{0, src, 0});

        // bestStops[city] = fewest stops used to reach city with the cost we accepted it at.
        // Prevents revisiting a city with a worse (cost, stops) combo than already seen.
        int[] bestStops = new int[n];
        Arrays.fill(bestStops, Integer.MAX_VALUE);

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int cost = cur[0], city = cur[1], stops = cur[2];

            if (city == dst) return cost; // first time we pop dst, it's optimal

            if (stops > k || stops >= bestStops[city]) continue; // no budget left, or dominated
            bestStops[city] = stops;

            for (int[] edge : graph.get(city)) {
                int next = edge[0], price = edge[1];
                pq.offer(new int[]{cost + price, next, stops + 1});
            }
        }

        return -1;
    }
}
```

### Why the `bestStops` pruning is safe

We only skip expanding a state `(city, stops)` if we have already
expanded `city` with an equal or fewer number of stops (`bestStops[city] <= stops`).
Because the heap pops cheapest cost first, if we already expanded
`city` with fewer-or-equal stops, any path through this new, costlier
entry cannot lead to a better answer than what's already been queued
from the cheaper expansion — it's strictly dominated on both cost and
stops.

### Complexity

- **Time:** `O(E * k * log(E * k))` in the worst case, since a node can be
  pushed onto the heap once per stop count, and heap operations cost
  `log` of the heap size.
- **Space:** `O(E * k)` for the heap in the worst case.

---

## Comparison Summary

| Approach                     | Time Complexity      | Space Complexity | Notes                                                                                                                                                          |
| ---------------------------- | -------------------- | ---------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Bellman-Ford (array scan) | `O(k * E)`           | `O(n)`           | Simplest to write and reason about; best for this constraint size (`n ≤ 100`).                                                                                 |
| 2. BFS layer-by-layer        | `O(k * E)`           | `O(n + E)`       | Same idea as (1) with adjacency list + BFS framing; easy to extend to path reconstruction.                                                                     |
| 3. Modified Dijkstra         | `O(k * E * log(kE))` | `O(k * E)`       | Generalizes well, but more overhead; needed when edge weights could be negative-adjacent tricky cases are not a concern here, so it's mostly for illustration. |

**Recommendation:** For this problem's constraints (`n ≤ 100`, `flights.length ≤ n*(n-1)/2 ≤ 4950`),
**Approach 1 (Bellman-Ford)** is the cleanest, most efficient, and least
error-prone solution. It directly encodes "at most k+1 edges" via the
number of relaxation rounds.

---

## Test Cases

```java
public class Main {
    public static void main(String[] args) {
        Solution sol = new Solution();

        // Example 1
        int[][] flights1 = {{0,1,100},{1,2,100},{2,0,100},{1,3,600},{2,3,200}};
        System.out.println(sol.findCheapestPrice(4, flights1, 0, 3, 1)); // 700

        // Example 2
        int[][] flights2 = {{0,1,100},{1,2,100},{0,2,500}};
        System.out.println(sol.findCheapestPrice(3, flights2, 0, 2, 1)); // 200

        // Example 3
        int[][] flights3 = {{0,1,100},{1,2,100},{0,2,500}};
        System.out.println(sol.findCheapestPrice(3, flights3, 0, 2, 0)); // 500
    }
}
```
