# 536. Construct Binary Tree from String

You need to construct a **binary tree** from a string consisting of **parentheses and integers**.

The entire input string represents a binary tree.

The format is:

- An **integer** representing the root node value
- Followed by **zero, one, or two pairs of parentheses**
- Each pair of parentheses represents a **child subtree**

The structure inside the parentheses follows the **same format recursively**.

The **left child is always constructed first**, followed by the right child (if it exists).

---

## Example 1

**Input**

```
s = "4(2(3)(1))(6(5))"
```

**Output**

```
[4,2,6,3,1,5]
```

---

## Example 2

**Input**

```
s = "4(2(3)(1))(6(5)(7))"
```

**Output**

```
[4,2,6,3,1,5,7]
```

---

## Example 3

**Input**

```
s = "-4(2(3)(1))(6(5)(7))"
```

**Output**

```
[-4,2,6,3,1,5,7]
```

---

## Constraints

```
0 <= s.length <= 3 * 10^4
```

The string consists only of:

- digits `0-9`
- parentheses `(` and `)`
- minus sign `-` for negative numbers

All numbers in the tree have value at most:

```
2^30
```
