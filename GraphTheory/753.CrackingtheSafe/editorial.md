# 753. Cracking the Safe — Three Java Approaches

## Problem Restated

The password is some unknown string of `n` digits, each digit in `[0, k-1]`.
We must output **any minimum-length string** such that, no matter what the
real password is, it appears as a contiguous substring somewhere in our
output.

Since there are `k^n` possible passwords, and the best we can ever do is
make every new keystroke complete one _new_ password (sharing the previous
`n-1` digits with it), the theoretical minimum length is:

```
k^n + (n - 1)
```

The puzzle is _constructing_ such a string.

---

## The Key Idea: This Is a De Bruijn Sequence

Think of every possible **`(n-1)`-digit string** as a node in a graph
(if `n = 1`, there's a single node, the empty string).

From a node `u`, draw an edge for every digit `d ∈ [0, k-1]` to the node
`v` obtained by dropping `u`'s first character and appending `d`. That
edge is naturally **labeled** with the full `n`-digit string `u + d` — i.e.
each edge _is_ one possible password.

- Number of nodes: `k^(n-1)`
- Number of edges: `k^n` (exactly the number of passwords)
- Every node has **in-degree = out-degree = k** → the graph is balanced,
  so an **Eulerian circuit** (a closed walk using every edge exactly once)
  is guaranteed to exist.

If we walk this Eulerian circuit and write down the start node, followed by
the one new digit each edge contributes, we get a string of length
`(n-1) + k^n` that contains every password exactly once as a substring.
That is precisely the optimal answer the problem asks for.

So **"Crack the safe" = "Find an Eulerian circuit in the de Bruijn graph."**

All three approaches below are just different ways to find that circuit.

---

## Approach 1: Backtracking (DFS with Explicit Undo)

### Idea

Simulate exactly what you'd do by hand: keep typing digits, remembering
every `n`-digit window you've already produced. At each step, try the
digits `0..k-1`; if a digit creates a _brand-new_ window, take it and move
on. If every choice from here leads to a dead end, **undo** the last digit
(remove it from the visited set and from the string) and try the next
option from the parent call.

This is the literal, no-theory-needed brute force. It happens to terminate
almost immediately in practice (because of the Eulerian-balance property
above — you essentially never have to backtrack), but it doesn't _rely_ on
that fact: it explores and abandons paths exactly like classic
backtracking on a maze.

### Java

```java
public static String crackSafeBacktrack(int n, int k) {
    int total = (int) Math.pow(k, n);
    Set<String> visited = new HashSet<>();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < n; i++) sb.append('0');   // initial "n zeros" password
    visited.add(sb.toString());
    if (total == 1) return sb.toString();
    backtrack(sb, visited, total, n, k);
    return sb.toString();
}

private static boolean backtrack(StringBuilder sb, Set<String> visited,
                                  int total, int n, int k) {
    if (visited.size() == total) return true;          // every password seen
    String window = sb.substring(sb.length() - n + 1);  // last n-1 digits
    for (char c = '0'; c < '0' + k; c++) {
        String candidate = window + c;
        if (!visited.contains(candidate)) {
            visited.add(candidate);
            sb.append(c);
            if (backtrack(sb, visited, total, n, k)) return true;
            visited.remove(candidate);            // undo
            sb.deleteCharAt(sb.length() - 1);     // undo
        }
    }
    return false;
}
```

### Why it works

- We start the typed string as `"000...0"` (`n` zeros), the first password.
- At every step, the _only_ part of the current string that matters for
  the future is its last `n-1` digits (the new window we're about to
  extend). The full visited-history doesn't otherwise constrain us.
- We stop the moment `visited.size() == total` — at that instant every
  password has appeared.

### Complexity

- **Time:** Worst case looks exponential (`O(k!)`-ish if you only count
  raw branching), but the balanced-degree property of the de Bruijn graph
  means a path that reaches a dead end before visiting all `k^n` edges is
  _impossible_ — so in practice this finds the answer in
  `O(k^n)` work with **zero backtracking**. It's still useful to implement
  it this way because it's the most intuitive solution and remains
  correct even if you doubt the graph theory.
- **Space:** `O(k^n)` for the visited set and recursion stack.

---

## Approach 2: Hierholzer's Algorithm — Recursive Post-Order DFS (Optimal)

### Idea

Directly construct the Eulerian circuit. Do a DFS over the `(n-1)`-digit
**nodes**. At each node, try every outgoing **edge** (digit `0..k-1`); if
unused, mark it used, recurse into the neighbor, and — _only after the
recursive call returns_ — append that digit to the answer.

This "append after the recursive call" (post-order) is the classic
Hierholzer trick: because every node's in-degree equals its out-degree,
DFS can _never_ get stuck before using every edge out of the start node, so
no backtracking/undo is ever required — we simply never revisit a marked
edge.

One subtlety that's easy to get backwards: the digits collected by the
DFS must be placed **before** the starting node, not after it:

```
answer = (digits collected by dfs)  +  (starting (n-1)-digit node)
```

### Java

```java
public static String crackSafeHierholzerRecursive(int n, int k) {
    Set<String> seenEdges = new HashSet<>();
    StringBuilder sb = new StringBuilder();
    StringBuilder startBuilder = new StringBuilder();
    for (int i = 0; i < n - 1; i++) startBuilder.append('0');
    String start = startBuilder.toString();
    dfs(start, n, k, seenEdges, sb);
    return sb.toString() + start;
}

private static void dfs(String node, int n, int k,
                         Set<String> seenEdges, StringBuilder sb) {
    for (char d = '0'; d < '0' + k; d++) {
        String edge = node + d;                  // the full n-digit password
        if (!seenEdges.contains(edge)) {
            seenEdges.add(edge);
            dfs(edge.substring(1), n, k, seenEdges, sb);  // move to next node
            sb.append(d);                          // post-order: write AFTER recursing
        }
    }
}
```

### Why it works

- `node` always has length `n-1`. `edge = node + d` has length `n` — it's
  literally one password, and also the edge identifier in our graph.
- `edge.substring(1)` drops the oldest character and keeps `d`, giving the
  next `(n-1)`-length node — sliding the window forward by one digit.
- Because the graph is Eulerian-balanced, this DFS, including all its
  recursive descendants, ends up using **every single edge exactly once**
  before fully unwinding back to the top call.
- Writing `d` only after the recursive call returns produces the digits in
  the correct order to be the _suffix_ `sb`, with the very first node of
  the circuit (`start`) needing to be appended at the **end** of `sb` to
  complete the sequence (verified directly against the official example:
  for `n=2, k=2` this produces exactly `"01100"`, the sample answer).

### Complexity

- **Time:** `O(k^n)` — exactly one DFS call per edge, no wasted work, no
  undoing.
- **Space:** `O(k^n)` for the visited-edge set and the recursion stack
  (depth up to `k^n`).

This is the most elegant and efficient of the three approaches.

---

## Approach 3: Hierholzer's Algorithm — Iterative (Stack-Based)

### Idea

Same algorithm as Approach 2, but with the recursion replaced by an
explicit stack, which avoids relying on the call stack (useful if you're
wary of recursion depth, or want a more "mechanical" trace of the
Eulerian-circuit construction).

We keep two parallel stacks:

- `nodeStack`: the chain of `(n-1)`-digit nodes currently being visited
  (mirrors the call stack of the recursive version).
- `digitStack`: for each node on `nodeStack`, the digit of the edge that
  was used to _arrive_ at it from its parent (mirrors the `d` that the
  recursive version would append once that call returns).

When the node on top of the stack has **no unused outgoing edge left**, that's
the iterative equivalent of "the recursive call is about to return" — we
pop it and append its arrival digit, exactly mirroring the post-order
append in Approach 2.

### Java

```java
public static String crackSafeHierholzerIterative(int n, int k) {
    Set<String> seenEdges = new HashSet<>();
    StringBuilder startBuilder = new StringBuilder();
    for (int i = 0; i < n - 1; i++) startBuilder.append('0');
    String start = startBuilder.toString();

    Deque<String> nodeStack = new ArrayDeque<>();
    Deque<Character> digitStack = new ArrayDeque<>();
    nodeStack.push(start);
    digitStack.push('#');           // sentinel: no real edge leads to the start node

    StringBuilder result = new StringBuilder();

    while (!nodeStack.isEmpty()) {
        String node = nodeStack.peek();
        boolean advanced = false;
        for (char d = '0'; d < '0' + k; d++) {
            String edge = node + d;
            if (!seenEdges.contains(edge)) {
                seenEdges.add(edge);
                nodeStack.push(edge.substring(1));
                digitStack.push(d);
                advanced = true;
                break;                // descend immediately, just like recursion would
            }
        }
        if (!advanced) {             // no unused edge left => "return" from this node
            nodeStack.pop();
            char d = digitStack.pop();
            if (d != '#') result.append(d);
        }
    }
    return result.toString() + start;
}
```

### Why it works

- Pushing a node + its arrival digit is exactly "calling `dfs(next)`".
- Failing to find any unused outgoing edge from the node on top of the
  stack is exactly "the for-loop in `dfs` finished, time to return" — at
  that point we pop and append the digit, exactly when the recursive
  version would.
- Because both versions perform identical operations in identical order,
  they always produce the same digit sequence.

### Complexity

- **Time:** `O(k^n · k)` — each time we look at the top of the stack we may
  rescan up to `k` digits to find the next unused one (a simple
  unoptimized version; this can be reduced to `O(k^n)` total by caching a
  per-node "next digit to try" pointer, since once a digit is marked seen
  it never needs to be rechecked).
- **Space:** `O(k^n)` for the two stacks and the seen-edge set.

Useful when you want Hierholzer's algorithm without deep recursion.

---

## Verifying Correctness

A small helper confirms any candidate answer is valid — every length-`n`
window should be one of the `k^n` distinct passwords, and the total length
should match the theoretical minimum:

```java
public static boolean verify(String answer, int n, int k) {
    int total = (int) Math.pow(k, n);
    Set<String> found = new HashSet<>();
    for (int i = 0; i + n <= answer.length(); i++) {
        found.add(answer.substring(i, i + n));
    }
    return found.size() == total && answer.length() == total + n - 1;
}
```

All three approaches were compiled and run against the examples and
several additional cases:

| n   | k   | Backtrack result                | Recursive Hierholzer            | Iterative Hierholzer            | Valid |
| --- | --- | ------------------------------- | ------------------------------- | ------------------------------- | ----- |
| 1   | 2   | `01`                            | `10`                            | `10`                            | ✅    |
| 2   | 2   | `00110`                         | `01100` (matches sample)        | `01100`                         | ✅    |
| 3   | 2   | `0001011100`                    | `0011101000`                    | `0011101000`                    | ✅    |
| 2   | 3   | `0010211220`                    | `0221120100`                    | `0221120100`                    | ✅    |
| 4   | 1   | `0000`                          | `0000`                          | `0000`                          | ✅    |
| 3   | 3   | `00010020110120210221112122200` | `00222121112201202101102001000` | `00222121112201202101102001000` | ✅    |

(Also stress-tested up to the constraint boundary `k^n = 4096`, e.g.
`n=4,k=4` and `n=2,k=10` — all valid, with `n=2,k=2` exactly matching the
problem's own sample output `"01100"`.)

---

## Comparison

| Approach                  | Idea                                       | Time                                                 | Space    | Notes                                                                                      |
| ------------------------- | ------------------------------------------ | ---------------------------------------------------- | -------- | ------------------------------------------------------------------------------------------ |
| 1. Backtracking           | DFS + explicit undo over visited passwords | Looks exponential, behaves like `O(k^n)` in practice | `O(k^n)` | Most intuitive; relies on no theory, but never actually backtracks thanks to graph balance |
| 2. Hierholzer (recursive) | Post-order DFS over de Bruijn graph nodes  | `O(k^n)`                                             | `O(k^n)` | Cleanest, optimal, the standard accepted solution                                          |
| 3. Hierholzer (iterative) | Same as #2, with an explicit stack         | `O(k^n · k)` (easily reducible to `O(k^n)`)          | `O(k^n)` | Avoids deep recursion; good when you want full control over traversal order                |

All three return a string of the provably minimal length
`k^n + n - 1`, just potentially a _different_ valid string — the problem
accepts any correct minimum-length answer.
