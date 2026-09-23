# 444. Sequence Reconstruction

## Problem Recap

Given a permutation `nums` of `[1, n]` and a list `sequences` where each `sequences[i]` is a
subsequence of `nums`, determine whether `nums` is the **unique shortest common supersequence**
of all the arrays in `sequences`.

In other words: among all sequences that contain every `sequences[i]` as a subsequence, is
`nums` the only one with the minimum possible length (`n`)?

---

## The Core Insight

This problem is really a question about **partial order reconstruction**.

Each `sequences[i]` tells us, for consecutive pairs of elements, that one number must come
_before_ another in any valid supersequence. If we collect every such "comes immediately
before" relationship across all sequences, we get a directed graph on the numbers `1..n`.

- If that graph, combined transitively, forces a **single, total order** on all `n` numbers,
  then `nums` is the only shortest supersequence (it's exactly that forced order).
- If at any point the order isn't forced (two numbers could legally appear in either order
  relative to each other), then we can build at least one alternative supersequence of the
  same length — so `nums` is **not unique**.

This gives us two equivalent ways to solve the problem:

1. **Topological Sort (Kahn's Algorithm)** — build the graph explicitly and check that the
   topological order is unique and matches `nums`.
2. **Adjacent-Pair Verification** — a sharper observation that lets us skip building the graph
   entirely and just check coverage of `nums`'s own adjacent pairs.

Both run in **O(n + ΣlenOf(sequences[i]))** time, but Approach 2 is simpler and uses less memory.

---

## Approach 1: Topological Sort (Kahn's BFS Algorithm)

### Idea

1. Build a directed graph: for every sequence, each consecutive pair `(u, v)` becomes an edge
   `u -> v`, meaning `u` must precede `v`.
2. Run Kahn's algorithm (BFS topological sort) using in-degrees.
3. **Uniqueness check**: at every step, the BFS queue must contain _exactly one_ node. If it
   ever contains more than one, there are two numbers with no determined relative order yet,
   meaning multiple valid orderings exist → not unique.
4. **Correctness check**: the order produced must match `nums` exactly, position by position.
5. **Completeness check**: every one of the `n` numbers must get processed (guards against
   missing numbers or cycles, though cycles can't occur here since `sequences[i]` is guaranteed
   to be consistent with `nums`).

### Java Code

```java
import java.util.*;

class Solution {
    public boolean sequenceReconstruction(int[] nums, List<List<Integer>> sequences) {
        int n = nums.length;

        // adjacency sets (a Set, not a List, to avoid double-counting duplicate edges
        // that can appear across different sequences)
        List<Set<Integer>> graph = new ArrayList<>(n + 1);
        int[] indegree = new int[n + 1];
        for (int i = 0; i <= n; i++) graph.add(new HashSet<>());

        // Build the graph from every consecutive pair in every sequence
        for (List<Integer> seq : sequences) {
            for (int i = 0; i + 1 < seq.size(); i++) {
                int u = seq.get(i), v = seq.get(i + 1);
                if (graph.get(u).add(v)) {   // only count a fresh edge once
                    indegree[v]++;
                }
            }
        }

        // Standard Kahn's algorithm setup
        Queue<Integer> queue = new LinkedList<>();
        for (int num = 1; num <= n; num++) {
            if (indegree[num] == 0) queue.offer(num);
        }

        int idx = 0;
        while (!queue.isEmpty()) {
            // More than one "free" node means the next element isn't uniquely determined
            if (queue.size() > 1) return false;

            int node = queue.poll();

            // The forced order must match nums exactly
            if (idx >= n || nums[idx] != node) return false;
            idx++;

            for (int next : graph.get(node)) {
                if (--indegree[next] == 0) {
                    queue.offer(next);
                }
            }
        }

        // Every number from 1..n must have been placed
        return idx == n;
    }
}
```

### Walkthrough — Example 3

`nums = [1,2,3]`, `sequences = [[1,2],[1,3],[2,3]]`

- Edges built: `1->2`, `1->3`, `2->3`
- In-degrees: `1:0`, `2:1`, `3:2`
- Queue starts with `{1}` (size 1, OK) → pop `1`, matches `nums[0]`. Decrement `2`'s in-degree
  to 0 (enqueue), decrement `3`'s in-degree to 1.
- Queue is `{2}` (size 1, OK) → pop `2`, matches `nums[1]`. Decrement `3`'s in-degree to 0
  (enqueue).
- Queue is `{3}` (size 1, OK) → pop `3`, matches `nums[2]`.
- All 3 nodes processed → **return true**.

### Complexity

- **Time:** `O(n + L)` where `L = Σ len(sequences[i])`, since each pair is processed once and
  every node/edge is visited once in the BFS.
- **Space:** `O(n + L)` for the graph (adjacency sets) and in-degree array.

---

## Approach 2: Adjacent-Pair Verification (No Graph Needed)

### Idea

Look at `nums` itself. It has `n - 1` _adjacent pairs_: `(nums[0], nums[1])`,
`(nums[1], nums[2])`, …, `(nums[n-2], nums[n-1])`.

**Claim:** `nums` is the unique shortest supersequence **if and only if every one of these
`n - 1` adjacent pairs appears as two _consecutive_ elements in at least one sequence.**

Why this works:

- _If every adjacent pair is "confirmed"_ by some sequence, then for every `i`, some sequence
  directly forces `nums[i]` to come immediately before `nums[i+1]`. Chaining these `n - 1`
  forced relationships pins down the entire order — it has to be exactly `nums`. No other
  arrangement of length `n` can satisfy all sequences, so it's unique.

- _If some adjacent pair `(nums[i], nums[i+1])` is never confirmed_, then no sequence contains
  _both_ of those numbers together (because if a sequence contained both, since nothing lies
  between them in `nums`, they'd necessarily appear consecutively in that sequence too — a
  subsequence can't "skip in" extra room that doesn't exist). Since no sequence depends on the
  relative order of `nums[i]` and `nums[i+1]`, we can freely swap them to build a different,
  equally short, valid supersequence — so `nums` is **not** unique.

This means we never need to build a graph or run a BFS — we just need to mark which adjacent
_positions_ in `nums` get "confirmed" while scanning the sequences once.

### Java Code

```java
import java.util.*;

class Solution {
    public boolean sequenceReconstruction(int[] nums, List<List<Integer>> sequences) {
        int n = nums.length;

        // pos[value] = index of that value within nums
        int[] pos = new int[n + 1];
        for (int i = 0; i < n; i++) {
            pos[nums[i]] = i;
        }

        // confirmed[i] == true means the pair (nums[i], nums[i+1]) was seen
        // as consecutive elements in some sequence
        boolean[] confirmed = new boolean[Math.max(n - 1, 0)];
        int confirmedCount = 0;

        for (List<Integer> seq : sequences) {
            for (int i = 0; i + 1 < seq.size(); i++) {
                int u = seq.get(i);
                int v = seq.get(i + 1);
                int pu = pos[u];
                int pv = pos[v];

                // Only pairs that are *immediately* adjacent in nums count as "confirming"
                if (pv - pu == 1 && !confirmed[pu]) {
                    confirmed[pu] = true;
                    confirmedCount++;

                    // Early exit: once all gaps are confirmed, we already know the answer
                    if (confirmedCount == n - 1) return true;
                }
            }
        }

        return confirmedCount == n - 1;
    }
}
```

### Walkthrough — Example 1 (should be `false`)

`nums = [1,2,3]`, `sequences = [[1,2],[1,3]]`

- `pos = {1:0, 2:1, 3:2}`, need to confirm gaps at index `0` (pair `1,2`) and index `1`
  (pair `2,3`).
- Sequence `[1,2]`: pair `(1,2)` → `pos` diff `= 1` → confirms gap `0`. `confirmedCount = 1`.
- Sequence `[1,3]`: pair `(1,3)` → `pos` diff `= 2` → **not adjacent**, ignored.
- Final `confirmedCount = 1`, but we needed `2` → **return false.** ✔️ matches expected output.

### Walkthrough — Example 3 (should be `true`)

`nums = [1,2,3]`, `sequences = [[1,2],[1,3],[2,3]]`

- `[1,2]` confirms gap `0`. `[1,3]` is skipped (not adjacent in `nums`). `[2,3]` confirms
  gap `1`.
- `confirmedCount = 2 = n - 1` → **return true.** ✔️

### Complexity

- **Time:** `O(n + L)` — one pass to build `pos`, one pass over all sequence elements.
- **Space:** `O(n)` — just the `pos` array and the `confirmed` boolean array. No graph, no
  queue — noticeably leaner than Approach 1.

---

## Why Not Brute Force?

A naive idea would be: generate all permutations consistent with the sequences, check how many
have minimum length, and see if `nums` is the only one. This is conceptually simple but
requires exploring up to `n!` orderings — completely infeasible once `n` exceeds about 10,
let alone the constraint's `n ≤ 10⁴`. It's mentioned here only to motivate why we instead reduce
the problem to checking _forced adjacency_, which both approaches above do in linear time.

---

## Comparison

| Approach                         | Time     | Space    | Notes                                                                     |
| -------------------------------- | -------- | -------- | ------------------------------------------------------------------------- |
| 1. Topological Sort (Kahn's BFS) | O(n + L) | O(n + L) | More general technique; intuitive if you already think in graphs.         |
| 2. Adjacent-Pair Verification    | O(n + L) | O(n)     | Same asymptotic time, lower constant/memory; relies on a sharper insight. |

`L = Σ len(sequences[i])`, bounded by `10^5` per the constraints.

Both approaches are fully correct and pass within the given constraints (`n ≤ 10^4`,
`Σ len(sequences[i]) ≤ 10^5`). Approach 2 is the better choice in an interview once you can
justify the "adjacent pair forcing" argument, since it avoids building any graph structure at
all.
