# Proof Sketch: Why Union-Find Runs in O(m log\* n) Time

This document summarizes the amortized analysis of Union-Find
(Disjoint Set Union) with:

- Union by Rank
- Path Compression

The result shows that performing `m` Find/Union operations on `n` elements
takes:

O(m log\* n)

where log\* n (iterated logarithm) is an extremely slow-growing function.

---

# 1. Preliminaries

Union-Find maintains a forest of trees.

Each node stores:

- parent pointer
- rank (upper bound on tree height)

Operations:

- find(x)
- union(x, y)

Path compression flattens trees during find().

---

# 2. Structural Lemmas

## Lemma 1: Ranks Increase Along Parent Paths

Parent pointers always move toward nodes of greater or equal rank.

Reason:

- Smaller-rank tree attaches under larger-rank tree.
- If equal ranks merge, parent rank increases.
- Path compression only links nodes directly to the root.

Thus ranks strictly increase along any path to the root.

---

## Lemma 2: Rank r Implies ≥ 2^r Nodes

If a node has rank r, its subtree contains at least 2^r nodes.

Proof (induction):

- Rank 0 → 1 node = 2^0
- Rank r+1 only arises when merging two rank-r trees:
  2^r + 2^r = 2^(r+1)

High rank nodes require exponentially many elements.

---

## Lemma 3: At Most n / 2^r Nodes of Rank r

Since each rank-r node represents ≥ 2^r elements,
there can be at most n / 2^r such nodes.

High ranks are therefore very rare.

---

# 3. Bucket Method

Ranks are grouped into exponentially growing buckets.

Define:

tower(B) = 2^(2^(...2)) (B times)

Bucket B contains ranks in:

[tower(B-1), tower(B) - 1]

These ranges grow extremely fast.

---

## Observation 1: Number of Buckets ≤ log\* n

Since ranks cannot exceed O(log n),
and tower grows explosively,
only the first log\* n buckets can contain nodes.

Therefore:

Number of buckets = O(log\* n)

---

## Observation 2: Few Nodes Per Bucket

Using Lemma 3, total nodes in bucket B is bounded by:

≤ 2n / 2^{tower(B-1)}

Bucket sizes shrink doubly-exponentially.

---

# 4. Time Decomposition

Let total Find traversal cost be:

T = T1 + T2 + T3

Where:

- T1 = steps reaching root
- T2 = steps crossing bucket boundaries
- T3 = steps within same bucket

---

## T1: Root Reaches

Each find ends at one root.

T1 = O(m)

---

## T2: Bucket Crossings

Ranks strictly increase along path.
Each find crosses at most O(log\* n) buckets.

So:

T2 = O(m log\* n)

---

## T3: Same-Bucket Traversals

Fix node u.

Because path compression updates parents upward,
u's parent rank strictly increases over time.

Within bucket B, rank range is bounded.
So u can participate in only bounded traversals inside B.

Summing across all nodes in bucket B:

T3(B) ≤ 2n

Across O(log\* n) buckets:

T3 = O(n log\* n)

---

# 5. Final Bound

Total cost:

T = O(m) + O(m log* n) + O(n log* n)

For typical sequences (m ≥ n):

T = O(m log\* n)

Amortized per operation:

O(log\* n)

---

# 6. Why log\* n Is Tiny

log\* n = number of times log2 must be applied until ≤ 1.

Even for enormous n:

log\* n ≤ 5

Thus Union-Find is practically constant time.

---

# 7. Stronger Result

The tightest known bound is:

O(m α(n))

where α(n) is the inverse Ackermann function,
which grows even slower than log\* n.

---

# Core Intuition

1. Ranks increase along paths.
2. High ranks require exponentially many nodes.
3. High-rank nodes are rare.
4. Bucket ranges grow explosively.
5. Path compression prevents repeated work.

Therefore Union-Find runs in near-linear time:
O(m log\* n).
