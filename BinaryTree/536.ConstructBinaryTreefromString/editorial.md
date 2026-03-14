# Construct Binary Tree from String — Detailed Explanation

## Overview

This problem belongs to the broader class of **Serialize / Deserialize problems**.
These problems are important when we want to:

- Store complex data structures (like trees or graphs)
- Save them in files or databases
- Transmit them across networks
- Reconstruct them later

In this problem we are given a **string representation of a binary tree** (serialized tree), and we must **deserialize it into an actual tree structure**.

The output should be the **root `TreeNode`**, from which the entire tree can be accessed.

---

## Recursive Nature of Trees

Trees naturally have a **recursive structure**:

- A **tree** consists of:
  - A root node
  - A left subtree
  - A right subtree

Similarly, the **string representation** of the tree also follows a recursive pattern.

Example representation:

```
4(2(3)(1))(6(5))
```

Structure:

```
      4
     / \\
    2   6
   / \\  /
  3   1 5
```

Each subtree itself follows the same structure.

---

# Approach 1: Recursion

## Intuition

An **opening bracket `(`** indicates the **start of a new subtree**.

Whenever we encounter `(`:

- We recursively build that subtree.

A **closing bracket `)`** indicates the **end of the current subtree**.

Thus recursion naturally matches the nested structure of parentheses.

---

## Helper Function: `getNumber`

This function extracts the integer value for a node.

It:

- Reads digits
- Handles negative numbers
- Returns:
  - Parsed number
  - Next index in the string

Pseudo logic:

```
read digits
handle optional '-' sign
convert characters to integer
return (value, next_index)
```

---

## Recursive Builder Function

Function:

```
str2treeInternal(string, index)
```

Returns:

- constructed subtree root
- next index to process

### Steps

1. **Termination condition**

If index reaches end of string:

```
return (null, index)
```

2. **Parse node value**

Use `getNumber()`.

3. **Create TreeNode**

```
node = new TreeNode(value)
```

4. **Check for left subtree**

If next char is `(`:

```
node.left = recursive_call()
```

5. **Check for right subtree**

If another `(` appears:

```
node.right = recursive_call()
```

6. **Return node and updated index**

---

## Implementation

```java
class Solution {

    public TreeNode str2tree(String s) {
        return str2treeInternal(s, 0).getKey();
    }

    public Pair<Integer,Integer> getNumber(String s, int index) {

        boolean negative = false;

        if (s.charAt(index) == '-') {
            negative = true;
            index++;
        }

        int number = 0;

        while (index < s.length() && Character.isDigit(s.charAt(index))) {
            number = number * 10 + (s.charAt(index) - '0');
            index++;
        }

        return new Pair<>(negative ? -number : number, index);
    }

    public Pair<TreeNode,Integer> str2treeInternal(String s, int index) {

        if (index == s.length()) {
            return new Pair<>(null, index);
        }

        Pair<Integer,Integer> numberData = getNumber(s, index);

        int value = numberData.getKey();
        index = numberData.getValue();

        TreeNode node = new TreeNode(value);

        Pair<TreeNode,Integer> data;

        if (index < s.length() && s.charAt(index) == '(') {
            data = str2treeInternal(s, index + 1);
            node.left = data.getKey();
            index = data.getValue();
        }

        if (node.left != null && index < s.length() && s.charAt(index) == '(') {
            data = str2treeInternal(s, index + 1);
            node.right = data.getKey();
            index = data.getValue();
        }

        return new Pair<>(
            node,
            index < s.length() && s.charAt(index) == ')' ? index + 1 : index
        );
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(N)
```

Where **N = length of string**.

Each character is processed **exactly once**.

---

### Space Complexity

```
O(H)
```

Where **H = height of tree**.

Worst case (skewed tree):

```
O(N)
```

due to recursion stack.

---

# Approach 2: Stack (Iterative)

## Intuition

Recursive solutions rely on the **system call stack**.

If the tree becomes extremely deep, recursion can cause **stack overflow**.

Instead we simulate recursion using **our own stack**.

---

## Node Processing States

Each node can be in three conceptual states:

| State       | Meaning                  |
| ----------- | ------------------------ |
| NOT_STARTED | Node value not processed |
| LEFT_DONE   | Left subtree processed   |
| RIGHT_DONE  | Both children processed  |

We don't explicitly store these states in code but the algorithm logically follows them.

---

## Algorithm

1. Create an empty **stack**
2. Push the root node
3. Iterate through the string
4. At each step:

### If digit or '-'

- Parse node value
- Assign to node
- If `(` appears → create **left child**

### If `(` appears after left child

- Create **right child**

### If `)`

- Subtree finished
- Pop node

---

## Implementation

```java
class Solution {

    public TreeNode str2tree(String s) {

        if (s.isEmpty()) {
            return null;
        }

        TreeNode root = new TreeNode();

        Stack<TreeNode> stack = new Stack<>();
        stack.add(root);

        for (int index = 0; index < s.length();) {

            TreeNode node = stack.pop();

            if (Character.isDigit(s.charAt(index)) || s.charAt(index) == '-') {

                Pair<Integer,Integer> numberData = getNumber(s, index);

                int value = numberData.getKey();
                index = numberData.getValue();

                node.val = value;

                if (index < s.length() && s.charAt(index) == '(') {

                    stack.add(node);

                    node.left = new TreeNode();
                    stack.add(node.left);
                }

            } else if (s.charAt(index) == '(' && node.left != null) {

                stack.add(node);

                node.right = new TreeNode();
                stack.add(node.right);
            }

            ++index;
        }

        return stack.empty() ? root : stack.pop();
    }

    public Pair<Integer,Integer> getNumber(String s, int index) {

        boolean negative = false;

        if (s.charAt(index) == '-') {
            negative = true;
            index++;
        }

        int number = 0;

        while (index < s.length() && Character.isDigit(s.charAt(index))) {
            number = number * 10 + (s.charAt(index) - '0');
            index++;
        }

        return new Pair<>(negative ? -number : number, index);
    }
}
```

---

## Complexity Analysis

### Time Complexity

```
O(N)
```

Each character in the string is processed once.

---

### Space Complexity

```
O(H)
```

Where **H = tree height**.

Worst case skewed tree:

```
O(N)
```

due to stack usage.

---

# Key Insight

| Approach  | Idea                                     | Time | Space |
| --------- | ---------------------------------------- | ---- | ----- |
| Recursion | Natural mapping to parentheses structure | O(N) | O(H)  |
| Stack     | Simulates recursion safely               | O(N) | O(H)  |

Both approaches parse the string **once** and reconstruct the tree structure.
