# 1948. Delete Duplicate Folders in System

## Approach: Serialization-Based Representation of Subtrees

## Overview

This problem is most naturally solved by turning the folder system into a **tree**.

Each folder is a node, and each edge represents a parent-child folder relationship.

Once we build that tree, the real challenge becomes:

> How do we detect whether two folders represent the same subtree structure?

The key idea is to compute a **serialized representation** of every subtree.

If two folders produce the same serialization, then they are duplicates and should be deleted together with all of their descendants.

This document explains that approach in detail.

---

# High-Level Strategy

We can solve the problem in three major steps:

1. **Build a tree** from the given folder paths
2. **Serialize every subtree** using post-order traversal and count how often each serialization occurs
3. **Traverse the tree again** and keep only the folders whose subtree serialization is unique

This is a classic example of:

- representing hierarchical structure as a tree
- computing a canonical form for each subtree
- using a frequency map to detect duplicates

---

# Step 1: Build the Tree

## Intuition

Each path in `paths` describes a chain of nested folders starting from the root.

For example:

```text
["one", "two", "three"]
```

represents:

```text
/one/two/three
```

If multiple paths share prefixes, then they share the same ancestors in the tree.

That means this is essentially a **Trie-like construction**:

- the root is `/`
- each folder name is an edge or child label
- each node stores a map from folder name to child node

---

## Why a Trie Fits Perfectly

A Trie is ideal whenever:

- input consists of many paths
- different paths may share common prefixes
- we want to represent all paths compactly

That is exactly what the folder system is.

For example, if we have:

```text
["a"]
["a", "b"]
["a", "b", "x"]
["c"]
["c", "b"]
```

then the tree naturally shares the common prefixes:

- `/a`
- `/a/b`
- `/c`

rather than storing each path independently.

---

## Node Structure

Each node needs two things:

1. a mapping from child folder names to child nodes
2. a place to store the serialized subtree representation of that node

In the provided Java solution, that is:

```java
class Trie {
    String serial;
    Map<String, Trie> children = new HashMap<>();
}
```

Where:

- `children` stores all child folders
- `serial` stores the serialized structure of the current node’s subtree

---

# Step 2: Serialize the Tree and Identify Duplicates

## Why Post-Order Traversal?

The problem says two folders are identical if they contain the same non-empty set of identical subfolders and the same underlying recursive structure.

That means the identity of a node depends entirely on the identity of its children.

So before we can describe node `x`, we first need to know the description of each child subtree.

That is exactly what **post-order traversal** gives us:

- first process all children
- then process the current node

This is the correct traversal order whenever a parent depends on child-derived information.

---

## What Does Serialization Mean Here?

We want a string representation of a subtree such that:

- identical subtree structures produce the same string
- different subtree structures produce different strings

That string becomes the subtree’s signature.

---

## Definition of `serial(x)`

Let `serial(x)` denote the serialization of node `x`.

### Case 1: `x` is a leaf

If `x` has no children, then:

```text
serial(x) = ""
```

That makes sense because a leaf contains no subfolder structure beneath it.

For example, in Example 1, the leaf folders named `b`, `b`, and `a` all serialize to the empty string.

---

### Case 2: `x` has children

Suppose `x` has children:

```text
y1, y2, ..., yk
```

Then we define:

```text
serial(x) = y1(serial(y1)) y2(serial(y2)) ... yk(serial(yk))
```

In words:

- for each child
- take the child folder name
- append `(` + child serialization + `)`
- concatenate the results

So each child contributes something like:

```text
childName(serial(child))
```

and the current node is represented by concatenating all such child descriptions.

---

## Example of Serialization

Suppose a folder has two children:

- child `x`, whose subtree serializes to `y()`
- child `z`, whose subtree serializes to `""`

Then the parent serialization becomes something like:

```text
x(y())z()
```

This string fully captures:

- which child folders exist
- what recursive subtree structure they each contain

---

## Why Naive Concatenation Is Not Enough

There is an important subtlety.

A folder’s children are stored in a map, and map iteration order is not guaranteed.

Even if two folders have the exact same set of child subtrees, their serializations could differ if the children are processed in different orders.

For example, these should clearly be identical:

```text
a -> {x, z}
b -> {z, x}
```

If we serialize based on arbitrary traversal order, one might produce:

```text
x(...)z(...)
```

and the other:

```text
z(...)x(...)
```

Those strings differ, even though the structures are the same.

---

## Fix: Sort the Child Representations

To make serialization canonical, we sort the serialized child strings before concatenating them.

That guarantees that equivalent subtree structures always produce the same serialization string.

This is the critical normalization step.

---

## Frequency Map

After serializing each non-leaf node, we store its serialization in a hash map:

- key = serialized subtree string
- value = how many times that serialization appears

If a serialization appears more than once, then all nodes with that serialization are duplicates.

---

# Step 3: Traverse Again and Keep Only Unique Folders

Now that we know which serializations are duplicated, we do a second DFS from the root.

At each node:

- if its serialization appears more than once, skip it entirely
- otherwise, keep it
- record its path in the answer
- continue into its children

This works because deleting a folder also deletes all of its descendants.

So once we decide a node is duplicate, we do not traverse deeper into that subtree.

---

# Important Subtlety: Deletion Happens Only Once

The problem explicitly states:

> the file system only runs the deletion once

That means:

- we identify duplicates in the original tree
- mark those folders
- delete them
- stop

We do **not** recompute duplicates after deletion.

This matters in cases where deleting one set of folders causes some remaining folders to become identical afterward.

Those newly identical folders must **not** be deleted.

The two-pass approach respects that requirement naturally, because the frequency map is computed once from the original tree.

---

# Java Implementation

```java
class Solution {

    class Trie {

        String serial; // current node structure's serialized representation
        Map<String, Trie> children = new HashMap<>(); // current node's child nodes
    }

    public List<List<String>> deleteDuplicateFolder(List<List<String>> paths) {
        Trie root = new Trie(); // root node

        // build a trie tree
        for (List<String> path : paths) {
            Trie cur = root;
            for (String node : path) {
                cur.children.putIfAbsent(node, new Trie());
                cur = cur.children.get(node);
            }
        }

        Map<String, Integer> freq = new HashMap<>(); // hash table records the occurrence times of each serialized representation
        // post-order traversal based on depth-first search, calculate the serialized representation of each node structure
        construct(root, freq);
        List<List<String>> ans = new ArrayList<>();
        List<String> path = new ArrayList<>();
        // operate the trie, delete duplicate folders
        operate(root, freq, path, ans);
        return ans;
    }

    private void construct(Trie node, Map<String, Integer> freq) {
        if (node.children.isEmpty()) return; // if it is a leaf node, no operation is needed.

        List<String> v = new ArrayList<>();
        for (Map.Entry<String, Trie> entry : node.children.entrySet()) {
            construct(entry.getValue(), freq);
            v.add(entry.getKey() + "(" + entry.getValue().serial + ")");
        }

        Collections.sort(v);
        StringBuilder sb = new StringBuilder();
        for (String s : v) {
            sb.append(s);
        }
        node.serial = sb.toString();
        freq.put(node.serial, freq.getOrDefault(node.serial, 0) + 1);
    }

    private void operate(
        Trie node,
        Map<String, Integer> freq,
        List<String> path,
        List<List<String>> ans
    ) {
        if (freq.getOrDefault(node.serial, 0) > 1) return; // if the serialization representation appears more than once, it needs to be deleted

        if (!path.isEmpty()) {
            ans.add(new ArrayList<>(path));
        }

        for (Map.Entry<String, Trie> entry : node.children.entrySet()) {
            path.add(entry.getKey());
            operate(entry.getValue(), freq, path, ans);
            path.remove(path.size() - 1);
        }
    }
}
```

---

# Detailed Walkthrough of the Code

## 1. Trie Node Definition

```java
class Trie {
    String serial;
    Map<String, Trie> children = new HashMap<>();
}
```

This node stores:

- `serial`: the serialized representation of the subtree rooted at this node
- `children`: a map from child folder names to child nodes

This is enough for both construction and duplicate detection.

---

## 2. Building the Tree

```java
Trie root = new Trie();

for (List<String> path : paths) {
    Trie cur = root;
    for (String node : path) {
        cur.children.putIfAbsent(node, new Trie());
        cur = cur.children.get(node);
    }
}
```

For every input path:

- start at the root
- insert folders one by one
- if a child folder does not exist yet, create it
- move downward

This is standard Trie insertion.

---

## 3. Serialization Construction

```java
Map<String, Integer> freq = new HashMap<>();
construct(root, freq);
```

This phase computes the serialization of every subtree and records its frequency.

---

## 4. Post-Order DFS in `construct`

```java
private void construct(Trie node, Map<String, Integer> freq) {
    if (node.children.isEmpty()) return;
```

If the node is a leaf, we stop immediately.

Why?

Because a leaf’s serialization is simply `""`, and in this implementation we do not explicitly need to build child descriptions beneath it.

Its `serial` field remains the default empty value, which is enough.

---

## 5. Serialize Each Child First

```java
for (Map.Entry<String, Trie> entry : node.children.entrySet()) {
    construct(entry.getValue(), freq);
    v.add(entry.getKey() + "(" + entry.getValue().serial + ")");
}
```

For each child:

1. recursively compute the child’s serialization
2. create a string of the form:

```text
childName(childSerialization)
```

So if a child named `b` is a leaf, its contribution is:

```text
b()
```

If a child named `x` has serialization `y()`, its contribution is:

```text
x(y())
```

---

## 6. Sort Child Descriptions

```java
Collections.sort(v);
```

This ensures order-independent serialization.

Without sorting, equivalent folder sets could produce different serialized strings just because child iteration order differed.

Sorting makes the serialization canonical.

---

## 7. Concatenate to Build Current Serialization

```java
StringBuilder sb = new StringBuilder();
for (String s : v) {
    sb.append(s);
}
node.serial = sb.toString();
```

This concatenates all normalized child contributions into the current node’s serialization.

That serialization uniquely represents the subtree rooted at `node`.

---

## 8. Record Frequency

```java
freq.put(node.serial, freq.getOrDefault(node.serial, 0) + 1);
```

Now we count how many times this exact subtree structure occurs.

If the frequency becomes greater than 1, then this structure is duplicated somewhere else in the tree.

---

## 9. Second DFS to Keep Only Unique Folders

```java
List<List<String>> ans = new ArrayList<>();
List<String> path = new ArrayList<>();
operate(root, freq, path, ans);
```

This phase builds the final result by skipping duplicate subtrees.

---

## 10. Deletion Logic in `operate`

```java
if (freq.getOrDefault(node.serial, 0) > 1) return;
```

If the current node’s serialization appears more than once, then it is duplicate and must be deleted.

Since deleting a folder deletes all of its descendants too, we stop immediately and do not recurse deeper.

---

## 11. Add Current Path to Answer

```java
if (!path.isEmpty()) {
    ans.add(new ArrayList<>(path));
}
```

We do not add the artificial root `/`, so only non-empty paths are recorded.

The current path is copied into the answer list.

---

## 12. Recurse Into Children

```java
for (Map.Entry<String, Trie> entry : node.children.entrySet()) {
    path.add(entry.getKey());
    operate(entry.getValue(), freq, path, ans);
    path.remove(path.size() - 1);
}
```

This is standard DFS path tracking:

- append child name
- recurse
- backtrack by removing it

This reconstructs all surviving folder paths from the root.

---

# Small Conceptual Example

Suppose the input is:

```text
["a"]
["c"]
["d"]
["a", "b"]
["c", "b"]
["d", "a"]
```

The tree becomes:

```text
/
├── a
│   └── b
├── c
│   └── b
└── d
    └── a
```

Now serialize bottom-up:

- leaf `b` under `/a` → `""`
- leaf `b` under `/c` → `""`
- leaf `a` under `/d` → `""`

Then:

- `/a` serializes as `b()`
- `/c` serializes as `b()`
- `/d` serializes as `a()`

So:

- `b()` appears twice
- therefore `/a` and `/c` are duplicates

During the second traversal:

- skip `/a`
- skip `/c`
- keep `/d`
- keep `/d/a`

Final result:

```text
[["d"], ["d", "a"]]
```

---

# Why the Approach Is Correct

The method is correct because:

1. A folder is determined entirely by the set of child folder names and the structure of each child subtree.
2. Post-order traversal ensures that every child subtree is serialized before its parent.
3. Sorting child contributions ensures that equivalent child sets produce identical serializations regardless of insertion order.
4. Therefore, two folders get the same serialization if and only if they are structurally identical in the sense required by the problem.
5. Using a frequency map lets us identify every duplicated subtree in the original tree.
6. The second traversal deletes exactly those duplicated subtrees and keeps all others.

That matches the problem statement exactly.

---

# Complexity Analysis

## What Actually Dominates the Cost?

The expensive part is not basic DFS itself.

The important cost is:

- building all serialized strings
- storing them in the hash map
- sorting child serial representations when forming each subtree

Everything else is asymptotically smaller.

---

## Key Tree-Theoretic Result

Let `T` be an unordered rooted tree.

For each node `x`, define:

- `dist[x]`: number of nodes on the path from the root to `x`
- `size[x]`: size of the subtree rooted at `x`

Then:

```text
sum(dist[x]) over all x in T = sum(size[x]) over all x in T
```

### Why this is true

Take any node `x'`.

That node contributes to the subtree size of every ancestor of `x'`, including itself.

So the number of times `x'` is counted across all subtree sizes equals the number of nodes on the path from the root to `x'`.

Summing this over all nodes gives the identity.

This fact is useful because it lets us bound the total size of all serialized subtree descriptions.

---

## Bounding the Total Tree Size from the Input

The input gives all folder paths.

The total number of characters across all path strings is bounded by:

```text
2 × 10^5
```

That means the total depth-sum across the tree is also bounded by that scale.

So:

```text
sum(dist[x]) <= 2 × 10^5
```

and therefore:

```text
sum(size[x]) <= 2 × 10^5
```

This is the structural quantity that controls the total serialization cost.

---

## Length of One Node’s Serialization

For any node `x`, its serialization contains two main parts:

### 1. Folder names of its descendant child links

Each folder name has length at most 10.

So across the subtree, this contributes at most:

```text
10 * size[x]
```

### 2. Parentheses

Every child serialization is wrapped in parentheses.

That adds at most:

```text
2 * size[x]
```

So the total length of the serialization of node `x` is at most:

```text
12 * size[x]
```

---

## Total Length of All Serialization Strings

Summing over all nodes:

```text
total serialization length <= 12 * sum(size[x])
```

Since:

```text
sum(size[x]) <= 2 × 10^5
```

we get:

```text
total serialization length <= 12 * 2 × 10^5 = 2.4 × 10^6
```

So the total amount of serialized data stored is safely manageable.

---

## Space Complexity

The dominant storage is:

- the trie nodes
- the serialization strings
- the frequency map

The most precise argument in the provided explanation bounds the total serialization data by roughly:

```text
O(10^6)
```

in the worst case.

At the usual asymptotic level, this is simply:

```text
O(total input size)
```

or equivalently linear in the size of the constructed tree.

---

## Time Complexity

The major work is:

- constructing serializations bottom-up
- sorting child serialization components
- hashing and storing them

Even with sorting, the total work remains comfortably within the constraints.

The detailed argument in the original explanation estimates the total operations to be around:

```text
10^7
```

in pessimistic cases.

So this approach is efficient enough.

At a high level, it behaves roughly like:

```text
O(total serialization work + sorting work)
```

which stays practical under the given limits.

---

# Practical Interpretation of the Complexity

Even though the explanation contains a more careful upper-bound argument than usual interview solutions, the practical takeaway is straightforward:

- every folder is processed a constant number of times
- every subtree gets one canonical serialization
- duplicate detection becomes a hash-map lookup problem
- sorting child encodings is the only extra cost, and it remains manageable

So this is a very efficient and scalable solution for the problem constraints.

---

# Why This Approach Is a Good Fit

This problem looks complicated if approached directly from the folder-path view.

But once converted into a tree, it becomes much cleaner.

The core pattern is:

1. **build a hierarchical structure**
2. **compute a canonical bottom-up representation**
3. **detect duplicates using hashing**
4. **do a second pass to remove or keep nodes**

This pattern shows up in many tree and graph problems.

---

# Key Takeaways

- The file system should be modeled as a **Trie / rooted multi-way tree**
- Duplicate folders are detected by comparing **serialized subtree structures**
- The correct traversal for building subtree identity is **post-order DFS**
- Child serializations must be **sorted** to avoid order sensitivity
- A frequency map tells us which serialized subtrees are duplicates
- A second DFS collects only those paths whose subtree serialization is unique
- Deletion is performed **once**, so duplicates are identified from the original tree only

---

# Summary

## Main Idea

Use a Trie-like tree and compute a canonical serialized string for each subtree.

## Workflow

1. Build the folder tree
2. Post-order serialize every subtree
3. Count frequency of each serialization
4. Traverse again and skip duplicate subtrees
5. Collect remaining folder paths

## Core Serialization Rule

For a node with children:

```text
childName1(serial(child1)) childName2(serial(child2)) ...
```

after sorting child contributions.

## Why It Works

Equivalent subtrees get the same canonical serialization.

## Final Result

All duplicate folders and their descendants are skipped, and all remaining valid paths are returned.
