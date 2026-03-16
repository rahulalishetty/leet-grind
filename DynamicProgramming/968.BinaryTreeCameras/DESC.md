import pypandoc, textwrap

md = """

# 968. Binary Tree Cameras

## Problem Description

You are given the **root of a binary tree**.

You can install **cameras** on tree nodes. Each camera can monitor:

- Its **parent**
- **Itself**
- Its **immediate children**

Your task is to determine the **minimum number of cameras** required so that **every node in the tree is monitored**.

---

## Camera Coverage

If a camera is placed on a node `X`, it covers:

```
        Parent
           |
        [ Camera ]
         /     \\
     Left     Right
```

So a camera monitors:

- The node itself
- Its parent
- Its left child
- Its right child

---

## Goal

Return the **minimum number of cameras** needed so that **all nodes are monitored**.

---

## Example 1

### Input

```
root = [0,0,null,0,0]
```

### Output

```
1
```

### Explanation

A single camera placed at the correct node can monitor:

- Its parent
- Itself
- Its two children

Thus **one camera** is sufficient.

---

## Example 2

### Input

```
root = [0,0,null,0,null,0,null,null,0]
```

### Output

```
2
```

### Explanation

At least **two cameras** are required to ensure every node in the tree is monitored.

Multiple valid camera placements may exist.

---

## Constraints

```
1 <= number of nodes <= 1000
Node.val == 0
```

---

## Key Observations

1. A camera covers **three levels locally**:
   - parent
   - node itself
   - children

2. Placing cameras greedily at leaf nodes is **not optimal**.

3. Cameras are usually best placed **on the parent of leaf nodes**, because this covers:

```
leaf -> parent -> sibling
```

4. This naturally leads to a **post‑order DFS strategy** where decisions are made **bottom‑up**.

---

## Tree Representation

Binary trees are typically represented as:

```
[0,0,null,0,0]
```

Which corresponds to:

```
      0
     /
    0
   / \\
  0   0
```

---

## Difficulty

```
Hard
```

"""

path = "/mnt/data/binary_tree_cameras_problem.md"
pypandoc.convert_text(textwrap.dedent(md), "md", format="md", outputfile=path, extra_args=["--standalone"])
path
