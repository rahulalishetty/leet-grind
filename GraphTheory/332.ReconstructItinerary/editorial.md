# 332. Reconstruct Itinerary — Solutions

## Problem Recap

Given a list of `[from, to]` tickets, reconstruct the itinerary starting at `"JFK"` that uses **every ticket exactly once** and is the **lexicographically smallest** when read as a sequence of airport codes.

This is really a graph problem in disguise: each ticket is a directed edge, and we need to find an **Eulerian path** (a path that uses every edge exactly once) that starts at `JFK` and is lexicographically smallest among all valid Eulerian paths. The problem statement guarantees such a path exists.

Two approaches are covered below, both verified against the official examples:

1. **Greedy DFS with Backtracking** — intuitive, but can be slow in adversarial cases.
2. **Hierholzer's Algorithm** — the textbook way to find an Eulerian path, runs in `O(E log E)` with no backtracking needed.

---

## Approach 1: Greedy DFS with Backtracking

### Idea

Build a graph where each airport maps to a sorted multiset of its destinations (sorted so we always try the lexicographically smallest option first). Then do a DFS from `JFK`:

- At each airport, try its destinations in sorted order.
- "Use" a ticket by decrementing its count, move to the next airport, and recurse.
- If the recursive call eventually uses **all** tickets, we're done — return immediately.
- If it gets stuck (reaches a dead end before all tickets are used), **undo** that choice (restore the ticket count) and try the next smallest destination instead.

The reason backtracking is needed at all: greedily always picking the smallest neighbor _without_ checking ahead can walk you into a dead end. For example, with edges `JFK→A`, `JFK→B`, `A→C`, `B→A`, `C→B` — picking `JFK→A` first looks smallest, but only `JFK→B→A→C→B` actually uses all the tickets. A pure greedy walk would need to detect the dead end and backtrack to try `JFK→B` instead.

### Java Code

```java
import java.util.*;

class Solution {

    public List<String> findItinerary(List<List<String>> tickets) {
        // graph: airport -> (destination -> remaining ticket count), destinations sorted
        Map<String, TreeMap<String, Integer>> graph = new HashMap<>();
        for (List<String> t : tickets) {
            graph.computeIfAbsent(t.get(0), k -> new TreeMap<>())
                 .merge(t.get(1), 1, Integer::sum);
        }

        LinkedList<String> route = new LinkedList<>();
        route.add("JFK");
        dfs(graph, "JFK", tickets.size(), route);
        return route;
    }

    private boolean dfs(Map<String, TreeMap<String, Integer>> graph, String current,
                         int ticketsLeft, LinkedList<String> route) {
        if (ticketsLeft == 0) return true; // used every ticket — done

        TreeMap<String, Integer> next = graph.get(current);
        if (next == null) return false; // dead end, tickets remain unused

        for (String dest : next.keySet()) { // TreeMap iterates in sorted order
            int count = next.get(dest);
            if (count == 0) continue; // ticket already used up

            next.put(dest, count - 1);   // use the ticket
            route.addLast(dest);

            if (dfs(graph, dest, ticketsLeft - 1, route)) return true;

            route.removeLast();          // backtrack
            next.put(dest, count);       // restore the ticket
        }
        return false;
    }
}
```

### Complexity

- **Time:** `O(E^2)` in the worst case — at each of the `E` edges we may scan/restore neighboring edges during backtracking. In practice it runs fast because true backtracking is rare for typical inputs, but adversarial graphs can force a lot of undo work.
- **Space:** `O(E)` for the graph and recursion stack.

---

## Approach 2: Hierholzer's Algorithm (Recommended)

### Idea

This is the standard algorithm for finding an Eulerian path, and it never needs to backtrack. The key insight: if you greedily walk edges (always picking the lexicographically smallest unused edge) and only stop a branch when you truly run out of edges from the current node, then record nodes in **post-order** (i.e., add a node to the front of the result only _after_ you've exhausted all its outgoing edges), the resulting sequence is automatically the correct Eulerian path.

Intuitively: any time you get "stuck" partway through, you're stuck at a node that closes a cycle, not a true dead end — Hierholzer's algorithm handles this by letting the recursion unwind and prepend nodes in the order their edges get exhausted, which naturally splices completed cycles into the right place in the final route.

### Java Code

```java
import java.util.*;

class Solution {

    private Map<String, PriorityQueue<String>> graph = new HashMap<>();
    private LinkedList<String> route = new LinkedList<>();

    public List<String> findItinerary(List<List<String>> tickets) {
        for (List<String> t : tickets) {
            graph.computeIfAbsent(t.get(0), k -> new PriorityQueue<>())
                 .add(t.get(1));
        }
        visit("JFK");
        return route;
    }

    private void visit(String airport) {
        PriorityQueue<String> destinations = graph.get(airport);
        while (destinations != null && !destinations.isEmpty()) {
            String next = destinations.poll(); // always take smallest unused edge
            visit(next);
        }
        route.addFirst(airport); // post-order: add only once all edges from here are used
    }
}
```

### Complexity

- **Time:** `O(E log E)` — each of the `E` edges is polled from a priority queue exactly once, and each poll/insert is `O(log E)`.
- **Space:** `O(E)` for the graph, the route, and the recursion stack.

This is strictly better than Approach 1 in the worst case, and is the standard solution expected for this problem.

---

## Verification

Both implementations were compiled and run against the two examples from the problem:

```text
Input:  [["MUC","LHR"],["JFK","MUC"],["SFO","SJC"],["LHR","SFO"]]
Output: [JFK, MUC, LHR, SFO, SJC]   ✓ matches expected

Input:  [["JFK","SFO"],["JFK","ATL"],["SFO","ATL"],["ATL","JFK"],["ATL","SFO"]]
Output: [JFK, ATL, JFK, SFO, ATL, SFO]   ✓ matches expected
```

---

## Summary

| Approach                  | Time                | Space  | Backtracking? | Notes                                   |
| ------------------------- | ------------------- | ------ | ------------- | --------------------------------------- |
| 1. Greedy DFS + Backtrack | `O(E^2)` worst case | `O(E)` | Yes           | Easier to reason about, but can be slow |
| 2. Hierholzer's Algorithm | `O(E log E)`        | `O(E)` | No            | Optimal, the standard approach          |

For an interview or production setting, **Approach 2 (Hierholzer's Algorithm)** is the one to know — it's the textbook technique for Eulerian path problems and avoids the backtracking overhead entirely.
