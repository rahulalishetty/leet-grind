from pathlib import Path
import pypandoc

md = """

# 546. Remove Boxes

## Problem Description

You are given an array `boxes` where each element represents the **color of a box** using a positive integer.

You may remove boxes in multiple rounds until **no boxes remain**.

### Removal Rule

In each round:

- You may choose **a group of continuous boxes with the same color**.
- Suppose the group contains **k boxes**.
- Removing them gives **k × k points**.

Your objective is to **maximize the total points** earned after removing all boxes.

---

# Example 1

### Input

```
boxes = [1,3,2,2,2,3,4,3,1]
```

### Output

```
23
```

### Explanation

Step-by-step optimal removals:

```
[1, 3, 2, 2, 2, 3, 4, 3, 1]
→ remove [2,2,2]      → score = 3×3 = 9
[1, 3, 3, 4, 3, 1]

→ remove [4]          → score = 1×1 = 1
[1, 3, 3, 3, 1]

→ remove [3,3,3]      → score = 3×3 = 9
[1, 1]

→ remove [1,1]        → score = 2×2 = 4

Total = 9 + 1 + 9 + 4 = **23**
```

---

# Example 2

### Input

```
boxes = [1,1,1]
```

### Output

```
9
```

### Explanation

Remove all boxes together:

```
[1,1,1] → score = 3×3 = 9
```

---

# Example 3

### Input

```
boxes = [1]
```

### Output

```
1
```

### Explanation

Only one box:

```
score = 1×1 = 1
```

---

# Constraints

```
1 ≤ boxes.length ≤ 100
1 ≤ boxes[i] ≤ 100
```

---
