# Alien Dictionary

## Detailed Notes on Breadth-First Search and Depth-First Search Topological Sort Approaches

## Overview

This problem is about recovering a valid alphabet ordering from a sorted list of words in an unknown language.

The key idea is that the given word list is already sorted according to the alien alphabet. That means the relative order of some letters can be inferred by comparing adjacent words.

This write-up explains two major approaches:

1. **Breadth-First Search using Kahn’s Algorithm**
2. **Depth-First Search using Topological Sort**

Both approaches follow the same broad structure:

1. **Extract dependency rules** from the word list
2. **Build a graph** where letters are nodes and precedence rules are directed edges
3. **Topologically sort** the graph

---

# Important Things to Keep in Mind

Before diving into the approaches, there are several key points that are easy to miss.

## 1. Letters inside one word do not imply ordering

For example, from the word:

```text
kitten
```

we cannot conclude:

```text
k comes before i
i comes before t
...
```

That is not how dictionary order works.

A single word tells us nothing about the relative order of its own letters.

---

## 2. Prefix invalidity must be detected

If a longer word appears before its own prefix, then the input is invalid.

For example:

```text
abcd
ab
```

This can never happen in a valid dictionary ordering, because prefixes must come first.

So whenever:

- `word1.length() > word2.length()`
- and `word1.startsWith(word2)`

we must immediately return:

```text
""
```

---

## 3. Multiple valid answers may exist

There can be more than one valid alien alphabet.

That is perfectly fine.

We may return **any valid ordering**.

---

## 4. Output must contain all unique letters

Even letters that are unconstrained must still appear in the output, as long as they appear somewhere in the input.

So the graph must include:

- letters that have outgoing edges
- letters that have incoming edges
- letters that have no edges at all

---

# General Strategy Used by All Approaches

Every correct solution breaks into these three steps:

## Step 1: Extract rules from adjacent words

By comparing each pair of adjacent words, we look for the **first differing character**.

If:

```text
word1[j] != word2[j]
```

then we know:

```text
word1[j] must come before word2[j]
```

This gives one ordering rule.

Only the **first difference** matters.
Everything after that is irrelevant for lexicographic order.

---

## Step 2: Build a graph

Treat each unique character as a graph node.

If:

```text
a must come before b
```

then add a directed edge:

```text
a -> b
```

An adjacency list is the best representation for this problem.

---

## Step 3: Topologically sort the graph

A topological ordering of the graph gives a valid alien alphabet order.

If the graph contains a cycle, then no valid alphabet exists.

So we must detect cycles as well.

---

# Why Comparing Adjacent Words Is Enough

Suppose the dictionary contains two adjacent words:

```text
abacus
algorithm
```

The first differing characters are:

```text
b and l
```

So the only conclusion we can draw is:

```text
b comes before l
```

We do **not** learn anything from the rest of the suffixes.

This is true in the alien dictionary as well.

Also, we compare only **adjacent** words.
We do not add all implied transitive edges explicitly.

For example, if we have:

```text
rgh
xcd
tny
bcd
```

then comparing adjacent pairs gives only:

```text
r -> x
x -> t
t -> b
```

We do not explicitly add:

```text
r -> t
r -> b
x -> b
```

because topological sorting will infer those relationships from the graph structure.

---

# Approach 1: Breadth-First Search

## Intuition

This approach uses **Kahn’s Algorithm**, which is a BFS-style topological sort.

The idea is:

- keep track of how many prerequisites each letter has
- repeatedly pick letters with **zero incoming edges**
- append them to the output
- remove their outgoing edges
- continue until no nodes remain

If some letters never become available, then the graph must contain a cycle.

---

# Part 1: Extracting Information

Consider a sorted alien dictionary.

To infer letter order, compare each adjacent pair of words and look for the first mismatch.

If:

```text
word1[j] != word2[j]
```

then:

```text
word1[j] -> word2[j]
```

This gives one directed edge in the dependency graph.

---

## Why Only the First Difference Matters

Suppose:

```text
wxqkj
whqg
```

The first differing character is:

```text
x vs h
```

So we know:

```text
x -> h
```

Everything after that is irrelevant, because lexicographic ordering is determined at the first difference.

---

# Part 2: Representing the Relations

We use:

## 1. Adjacency List

A map:

```text
character -> list of characters that must come after it
```

## 2. Indegree Count Map

A map:

```text
character -> number of incoming edges
```

This lets us quickly identify which characters currently have no prerequisites.

---

## Why Indegree Is Better Than a Reverse Adjacency List

We could have stored reverse edges too, but for Kahn’s algorithm we only need to know:

> how many incoming edges remain?

So instead of storing all incoming neighbors explicitly, we just count them.

That is both simpler and more memory-efficient.

---

# Part 3: Assemble a Valid Ordering

The BFS process is:

1. Put all letters with indegree `0` into a queue
2. Repeatedly remove one from the queue
3. Append it to the answer
4. For each outgoing neighbor:
   - decrement its indegree
   - if its indegree becomes `0`, add it to the queue
5. At the end:
   - if we processed all unique letters, return the result
   - otherwise there is a cycle, so return `""`

---

## Java Code

```java
public String alienOrder(String[] words) {
    // Step 0: Create data structures and find all unique letters.
    Map<Character, List<Character>> adjList = new HashMap<>();
    Map<Character, Integer> counts = new HashMap<>();
    for (String word : words) {
        for (char c : word.toCharArray()) {
            counts.put(c, 0);
            adjList.put(c, new ArrayList<>());
        }
    }

    // Step 1: Find all edges.
    for (int i = 0; i < words.length - 1; i++) {
        String word1 = words[i];
        String word2 = words[i + 1];
        // Check that word2 is not a prefix of word1.
        if (word1.length() > word2.length() && word1.startsWith(word2)) {
            return "";
        }
        // Find the first non match and insert the corresponding relation.
        for (int j = 0; j < Math.min(word1.length(), word2.length()); j++) {
            if (word1.charAt(j) != word2.charAt(j)) {
                adjList.get(word1.charAt(j)).add(word2.charAt(j));
                counts.put(word2.charAt(j), counts.get(word2.charAt(j)) + 1);
                break;
            }
        }
    }

    // Step 2: Breadth-first search.
    StringBuilder sb = new StringBuilder();
    Queue<Character> queue = new LinkedList<>();
    for (Character c : counts.keySet()) {
        if (counts.get(c).equals(0)) {
            queue.add(c);
        }
    }
    while (!queue.isEmpty()) {
        Character c = queue.remove();
        sb.append(c);
        for (Character next : adjList.get(c)) {
            counts.put(next, counts.get(next) - 1);
            if (counts.get(next).equals(0)) {
                queue.add(next);
            }
        }
    }

    if (sb.length() < counts.size()) {
        return "";
    }
    return sb.toString();
}
```

---

# Detailed Walkthrough of the BFS Code

## Step 0: Initialize all unique letters

```java
for (String word : words) {
    for (char c : word.toCharArray()) {
        counts.put(c, 0);
        adjList.put(c, new ArrayList<>());
    }
}
```

This is very important.

Even letters with no edges must still appear in the final answer.

---

## Step 1: Extract edges

```java
for (int i = 0; i < words.length - 1; i++) {
    String word1 = words[i];
    String word2 = words[i + 1];
```

We compare each adjacent pair.

---

## Prefix invalidity check

```java
if (word1.length() > word2.length() && word1.startsWith(word2)) {
    return "";
}
```

This handles invalid cases like:

```text
abcd
ab
```

---

## First differing character

```java
for (int j = 0; j < Math.min(word1.length(), word2.length()); j++) {
    if (word1.charAt(j) != word2.charAt(j)) {
        adjList.get(word1.charAt(j)).add(word2.charAt(j));
        counts.put(word2.charAt(j), counts.get(word2.charAt(j)) + 1);
        break;
    }
}
```

This adds the ordering rule and increments indegree of the destination node.

The `break` is critical because only the first difference matters.

---

## Initialize BFS queue with indegree 0 nodes

```java
for (Character c : counts.keySet()) {
    if (counts.get(c).equals(0)) {
        queue.add(c);
    }
}
```

These are the letters that can appear first.

---

## Process BFS

```java
while (!queue.isEmpty()) {
    Character c = queue.remove();
    sb.append(c);
    for (Character next : adjList.get(c)) {
        counts.put(next, counts.get(next) - 1);
        if (counts.get(next).equals(0)) {
            queue.add(next);
        }
    }
}
```

Each processed node removes its outgoing edges conceptually by decrementing neighbors’ indegrees.

---

## Cycle detection

```java
if (sb.length() < counts.size()) {
    return "";
}
```

If some letters were never processed, they must be trapped in a cycle.

---

# Complexity Analysis for BFS Approach

Let:

- `N` = number of words
- `C` = total number of characters across all words
- `U` = number of unique letters

## Time Complexity

```text
O(C)
```

Why?

### Step 1 and 2

Comparing adjacent words can inspect each character in the worst case.
That costs `O(C)` overall.

### Step 3

Topological sorting costs `O(V + E)`.

Here:

- `V = U`
- `E <= min(U^2, N - 1)`

So the graph processing cost is:

```text
O(U + min(U^2, N))
```

Since both `U` and `N` are bounded above by `C`, the overall cost simplifies to:

```text
O(C)
```

---

## Space Complexity

```text
O(1)
```

for the original LeetCode constraints where `U <= 26`.

More generally:

```text
O(U + min(U^2, N))
```

because the adjacency list dominates memory usage.

---

# Approach 2: Depth-First Search

## Intuition

Another way to topologically sort a directed graph is with DFS.

The idea is:

- recursively explore all outgoing dependencies first
- once a node has no more unexplored outgoing edges, add it to the output

This naturally produces nodes in **reverse topological order**.

So:

- either reverse the final output
- or use a **reverse adjacency list** so that nodes are appended in the final correct order directly

The solution here uses a reverse adjacency list.

---

# Why DFS Topological Sort Works

In DFS, a node is added to the output after all nodes that depend on it have already been processed.

So the finishing order is the reverse of a valid topological ordering.

That is why DFS is a standard topological sort technique.

---

# Reverse Adjacency List Trick

Instead of storing:

```text
a -> b
```

store the reverse edge:

```text
b -> a
```

Then the DFS output order becomes directly usable without reversing at the end.

This is a neat implementation trick.

---

# Cycle Detection with Graph Coloring

In directed graphs, cycles are commonly detected using three states:

- **white**: unvisited
- **grey**: currently being explored
- **black**: fully processed

If during DFS we re-enter a **grey** node, then we found a cycle.

In the provided implementation:

- `seen.get(c) == false` means grey
- `seen.get(c) == true` means black

---

## Java Code

```java
class Solution {

    private Map<Character, List<Character>> reverseAdjList = new HashMap<>();
    private Map<Character, Boolean> seen = new HashMap<>();
    private StringBuilder output = new StringBuilder();

    public String alienOrder(String[] words) {
        // Step 0: Put all unique letters into reverseAdjList as keys.
        for (String word : words) {
            for (char c : word.toCharArray()) {
                reverseAdjList.putIfAbsent(c, new ArrayList<>());
            }
        }

        // Step 1: Find all edges and add reverse edges to reverseAdjList.
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            // Check that word2 is not a prefix of word1.
            if (word1.length() > word2.length() && word1.startsWith(word2)) {
                return "";
            }
            // Find the first non match and insert the corresponding relation.
            for (int j = 0; j < Math.min(word1.length(), word2.length()); j++) {
                if (word1.charAt(j) != word2.charAt(j)) {
                    reverseAdjList.get(word2.charAt(j)).add(word1.charAt(j));
                    break;
                }
            }
        }

        // Step 2: DFS to build up the output list.
        for (Character c : reverseAdjList.keySet()) {
            boolean result = dfs(c);
            if (!result) return "";
        }

        return output.toString();
    }

    // Return true iff no cycles detected.
    private boolean dfs(Character c) {
        if (seen.containsKey(c)) {
            return seen.get(c); // If this node was grey (false), a cycle was detected.
        }
        seen.put(c, false);
        for (Character next : reverseAdjList.get(c)) {
            boolean result = dfs(next);
            if (!result) return false;
        }
        seen.put(c, true);
        output.append(c);
        return true;
    }
}
```

---

# Detailed Walkthrough of the DFS Code

## Step 0: Initialize all unique letters

```java
for (String word : words) {
    for (char c : word.toCharArray()) {
        reverseAdjList.putIfAbsent(c, new ArrayList<>());
    }
}
```

Again, this ensures unconstrained letters are still included.

---

## Step 1: Build reverse edges

```java
if (word1.charAt(j) != word2.charAt(j)) {
    reverseAdjList.get(word2.charAt(j)).add(word1.charAt(j));
    break;
}
```

If normal rule is:

```text
word1[j] -> word2[j]
```

then reverse adjacency stores:

```text
word2[j] -> word1[j]
```

---

## Step 2: Run DFS from all nodes

```java
for (Character c : reverseAdjList.keySet()) {
    boolean result = dfs(c);
    if (!result) return "";
}
```

We must start DFS from every node because the graph may have multiple disconnected components.

---

## DFS state logic

```java
if (seen.containsKey(c)) {
    return seen.get(c);
}
```

If the node was:

- grey → returns `false`, meaning cycle
- black → returns `true`, already safely processed

---

## Mark as grey

```java
seen.put(c, false);
```

This means the node is currently on the DFS path.

---

## Explore outgoing neighbors

```java
for (Character next : reverseAdjList.get(c)) {
    boolean result = dfs(next);
    if (!result) return false;
}
```

If any neighbor exploration finds a cycle, propagate failure upward.

---

## Mark as black and append

```java
seen.put(c, true);
output.append(c);
```

Now all prerequisites have been processed, so this node can safely be added to the output.

---

# Complexity Analysis for DFS Approach

## Time Complexity

```text
O(C)
```

The graph building phase is the same as in the BFS solution and costs `O(C)`.

The DFS visits each node and edge once overall, so the graph traversal cost is also linear in graph size.

Thus the full solution remains:

```text
O(C)
```

---

## Space Complexity

```text
O(1)
```

under the original 26-letter constraint.

More generally:

```text
O(U + min(U^2, N))
```

because of the adjacency list and DFS bookkeeping.

---

# Comparing the Two Approaches

## Breadth-First Search (Kahn’s Algorithm)

### Strengths

- very explicit handling of prerequisites
- natural use of indegrees
- easy cycle detection by incomplete processing
- often easier to explain step by step

### Weaknesses

- requires indegree bookkeeping
- queue-based logic may feel less intuitive if you think recursively

---

## Depth-First Search Topological Sort

### Strengths

- elegant recursive structure
- cycle detection with graph coloring is powerful
- no indegree map needed

### Weaknesses

- graph coloring can be subtle
- reverse adjacency list trick is slightly less obvious
- recursion depth can matter in larger settings

---

# Which Approach Should You Prefer?

## For interview clarity

Use **Approach 1: BFS / Kahn’s Algorithm**

It is usually the easiest to explain and reason about:

- build graph
- count indegrees
- repeatedly remove zero-indegree nodes

## For elegance and familiarity with DFS

Use **Approach 2: DFS Topological Sort**

It is a great choice if you are comfortable with:

- graph coloring
- reverse postorder logic
- cycle detection in directed graphs

---

# Final Takeaway

The problem is fundamentally a **topological sorting** problem.

The key steps are always:

1. compare adjacent words
2. extract first-difference precedence rules
3. build a directed graph
4. topologically sort the graph
5. detect invalid prefix cases and cycles

If there is a cycle, no valid alien alphabet exists.
If there is no cycle, any topological ordering is a valid answer.

---

# Summary

## Core Idea

Treat letters as graph nodes and precedence rules as directed edges.

## Important Edge Cases

- invalid prefix case like `abcd`, `ab`
- letters with no edges must still appear
- only the first difference between adjacent words matters

## Approach 1: BFS / Kahn’s Algorithm

- build adjacency list
- track indegree counts
- process zero-indegree nodes in a queue
- if not all nodes are processed, there is a cycle

### Complexity

- **Time:** `O(C)`
- **Space:** `O(1)` for fixed alphabet, otherwise `O(U + min(U^2, N))`

## Approach 2: DFS Topological Sort

- build reverse adjacency list
- DFS with graph coloring
- append nodes after exploring dependencies
- detect cycles via grey-node revisits

### Complexity

- **Time:** `O(C)`
- **Space:** `O(1)` for fixed alphabet, otherwise `O(U + min(U^2, N))`

## Recommended

Use **BFS / Kahn’s Algorithm** for the most interview-friendly solution.
