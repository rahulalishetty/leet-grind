# 685. Redundant Connection II

A rooted tree is a directed graph with exactly one node (the root) such that all other nodes are descendants of this node. In a rooted tree:

- Every node has exactly one parent, except for the root node, which has no parents.

The input is a directed graph that started as a rooted tree with `n` nodes (distinct values from `1` to `n`), with one additional directed edge added. This added edge connects two different vertices chosen from `1` to `n` and was not an existing edge.

The graph is represented as a 2D array of edges, where each element is a pair `[uᵢ, vᵢ]` representing a directed edge from node `uᵢ` to node `vᵢ`.

Your task is to return an edge that can be removed to make the graph a rooted tree with `n` nodes. If there are multiple valid answers, return the one that appears last in the given 2D array.

## Examples

### Example 1

**Input:**
`edges = [[1,2],[1,3],[2,3]]`
**Output:**
`[2,3]`

### Example 2

**Input:**
`edges = [[1,2],[2,3],[3,4],[4,1],[1,5]]`
**Output:**
`[4,1]`

## Constraints

- `n == edges.length`
- `3 <= n <= 1000`
- `edges[i].length == 2`
- `1 <= uᵢ, vᵢ <= n`
- `uᵢ != vᵢ`
