# 1597. Build Binary Expression Tree From Infix Expression

## Problem

We are given a valid **infix arithmetic expression** `s` containing:

- single-digit operands: `0` to `9`
- binary operators: `+`, `-`, `*`, `/`
- parentheses: `(` and `)`

We need to build **any valid binary expression tree** such that:

1. Its **inorder traversal** reproduces `s` after removing parentheses.
2. The tree respects the correct **order of operations**:
   - parentheses first
   - `*` and `/` before `+` and `-`
3. Operands appear in the **same left-to-right order** as in `s`.

Each node in the expression tree is either:

- a **leaf node** containing an operand
- an **internal node** containing an operator and exactly two children

---

## Why this problem is tricky

For a normal binary tree construction problem, structure is given directly.

Here, the structure must be **inferred** from:

- precedence rules
- parentheses
- operand order

So the real challenge is: **how do we split the expression into left and right subexpressions correctly?**

---

# Key idea

The root of any valid expression tree must be the **last operator to be evaluated** in the expression.

That means:

- operators inside parentheses are evaluated earlier, so they should not become the root of the outer expression
- lower-precedence operators (`+`, `-`) become roots before higher-precedence operators (`*`, `/`) if they are outside parentheses

This observation leads naturally to recursive parsing and stack-based parsing.

---

# Approach 1: Recursive Divide-and-Conquer by Finding the Lowest-Precedence Operator

## Intuition

Suppose we want to build a tree for a substring `s[l...r]`.

The root operator should be the operator that is evaluated **last** in that substring.

So we scan the substring and find the operator that:

1. is **outside all parentheses**
2. has the **lowest precedence**
3. if ties occur, we usually pick the **rightmost** operator of that precedence for left-associative parsing

Then:

- everything to the left becomes the left subtree
- everything to the right becomes the right subtree

If the substring is wrapped by a full pair of parentheses, we strip them and continue.

If the substring contains only one digit, that becomes a leaf.

---

## How to detect parentheses depth

While scanning the substring:

- `(` increases depth
- `)` decreases depth

We only consider operators when:

```text
depth == 0
```

because only then are they in the current expression level.

---

## Why lowest precedence?

Example:

```text
2-3/(5*2)+1
```

At the outermost level, the operators are:

```text
-   +
```

Both have lower precedence than `/`, so one of them should be the root.

A valid parse is:

```text
(2 - (3 / (5*2))) + 1
```

so `+` can become the root.

Inside the left side, `-` becomes the next root, and so on.

---

## Algorithm

Define:

```text
build(l, r)
```

that constructs an expression tree from `s[l...r]`.

Steps:

1. If `l == r`, return a node with the digit.
2. If the whole substring is wrapped by one matching outer pair of parentheses, remove them and recurse.
3. Scan from `l` to `r`:
   - track parentheses depth
   - among operators at depth 0, choose the one with the lowest precedence
4. Create an operator node for that position.
5. Recursively build:
   - left subtree from left side
   - right subtree from right side
6. Return the node.

---

## Java code

```java
/**
 * Definition for a binary tree node.
 * class Node {
 *     char val;
 *     Node left;
 *     Node right;
 *     Node() { this.val = ' '; }
 *     Node(char val) { this.val = val; }
 *     Node(char val, Node left, Node right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */
class Solution {
    private String s;

    public Node expTree(String s) {
        this.s = s;
        return build(0, s.length() - 1);
    }

    private Node build(int left, int right) {
        // Single operand
        if (left == right) {
            return new Node(s.charAt(left));
        }

        // Remove outer parentheses if they wrap the whole substring
        if (isWrapped(left, right)) {
            return build(left + 1, right - 1);
        }

        int index = -1;
        int minPrecedence = Integer.MAX_VALUE;
        int depth = 0;

        for (int i = left; i <= right; i++) {
            char c = s.charAt(i);

            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            } else if (depth == 0 && isOperator(c)) {
                int prec = precedence(c);

                // pick the rightmost operator among the lowest precedence
                if (prec <= minPrecedence) {
                    minPrecedence = prec;
                    index = i;
                }
            }
        }

        Node root = new Node(s.charAt(index));
        root.left = build(left, index - 1);
        root.right = build(index + 1, right);
        return root;
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private int precedence(char c) {
        if (c == '+' || c == '-') return 1;
        return 2; // * or /
    }

    private boolean isWrapped(int left, int right) {
        if (s.charAt(left) != '(' || s.charAt(right) != ')') {
            return false;
        }

        int depth = 0;
        for (int i = left; i <= right; i++) {
            char c = s.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') depth--;

            // If we close the first '(' before reaching right,
            // then outer parentheses do not wrap the whole substring.
            if (depth == 0 && i < right) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Complexity

Let `n = s.length()`.

### Time complexity

Worst case:

```text
O(n^2)
```

Why:

- for each recursive level, we may scan a substring linearly
- in skewed parses like `1+2+3+4+5+...`, the recursion depth can be `O(n)`

### Space complexity

```text
O(n)
```

due to recursion depth in the worst case.

---

## Pros and cons

### Pros

- conceptually simple
- mirrors how expressions are defined
- easy to reason about correctness

### Cons

- repeated rescanning of substrings
- worst-case `O(n^2)`

---

# Approach 2: Shunting-Yard Style Parsing with Two Stacks

## Intuition

A more efficient and more standard parsing solution uses **two stacks**:

1. **node stack** for operands / subtrees
2. **operator stack** for operators and parentheses

This is closely related to Dijkstra’s **shunting-yard algorithm**.

The key rule:

When we see a new operator, we should first process all operators already on the stack that have **greater or equal precedence**, unless blocked by `(`.

That guarantees correct operator precedence and left associativity.

---

## How subtree construction works

Whenever we decide to apply an operator:

- pop the operator
- pop the right subtree
- pop the left subtree
- create a new operator node
- push it back to the node stack

Example:

```text
3*4-2*5
```

As parsing proceeds:

- `3`, `4` become nodes
- `*` combines them into subtree `(3*4)`
- later `2`, `5` combine into `(2*5)`
- then `-` combines the two products

---

## Parentheses handling

- `(` goes on operator stack
- when `)` appears, keep applying operators until matching `(` is found
- then discard the `(`

This ensures parenthesized subexpressions are solved first.

---

## Algorithm

Initialize:

- `nodes = empty stack`
- `ops = empty stack`

Traverse the string character by character:

### If digit

Push a leaf node onto `nodes`.

### If '('

Push onto `ops`.

### If ')'

Keep applying operators until top is `'('`.
Pop `'('`.

### If operator

While:

- top of `ops` is an operator
- and its precedence is **>=** current operator precedence

apply that operator.

Then push current operator.

At the end, apply all remaining operators.

The final node on the node stack is the answer.

---

## Java code

```java
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Definition for a binary tree node.
 * class Node {
 *     char val;
 *     Node left;
 *     Node right;
 *     Node() { this.val = ' '; }
 *     Node(char val) { this.val = val; }
 *     Node(char val, Node left, Node right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */
class Solution {
    public Node expTree(String s) {
        Deque<Node> nodes = new ArrayDeque<>();
        Deque<Character> ops = new ArrayDeque<>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isDigit(c)) {
                nodes.push(new Node(c));
            } else if (c == '(') {
                ops.push(c);
            } else if (c == ')') {
                while (!ops.isEmpty() && ops.peek() != '(') {
                    buildTop(nodes, ops);
                }
                ops.pop(); // remove '('
            } else { // operator
                while (!ops.isEmpty() && ops.peek() != '(' &&
                       precedence(ops.peek()) >= precedence(c)) {
                    buildTop(nodes, ops);
                }
                ops.push(c);
            }
        }

        while (!ops.isEmpty()) {
            buildTop(nodes, ops);
        }

        return nodes.pop();
    }

    private void buildTop(Deque<Node> nodes, Deque<Character> ops) {
        char op = ops.pop();
        Node right = nodes.pop();
        Node left = nodes.pop();
        Node root = new Node(op);
        root.left = left;
        root.right = right;
        nodes.push(root);
    }

    private int precedence(char c) {
        if (c == '+' || c == '-') return 1;
        return 2; // * or /
    }
}
```

---

## Complexity

### Time complexity

```text
O(n)
```

Each character is processed once, and each operator/node is pushed and popped at most once.

### Space complexity

```text
O(n)
```

for the two stacks.

---

## Pros and cons

### Pros

- optimal linear time
- standard parsing technique
- avoids repeated rescans

### Cons

- more mechanical than recursive divide-and-conquer
- slightly less intuitive at first glance

---

# Approach 3: Recursive Descent Parsing by Grammar Levels

## Intuition

Another elegant way is to parse according to grammar precedence levels.

We split the grammar into levels:

```text
expression := term (('+' | '-') term)*
term       := factor (('*' | '/') factor)*
factor     := digit | '(' expression ')'
```

This naturally enforces precedence:

- `factor` handles digits and parenthesized expressions
- `term` handles `*` and `/`
- `expression` handles `+` and `-`

Each parser function returns a subtree.

This is often the cleanest parsing method if you are comfortable with formal grammar.

---

## Why it works

Because the grammar itself encodes precedence:

- multiplication/division are grouped inside `term`
- addition/subtraction are grouped at the outer `expression` level

So the parse tree automatically respects operator precedence.

---

## Algorithm

Maintain a global index `i`.

### parseExpression()

- parse a `term`
- while next token is `+` or `-`:
  - consume operator
  - parse another `term`
  - build a new operator node

### parseTerm()

- parse a `factor`
- while next token is `*` or `/`:
  - consume operator
  - parse another `factor`
  - build a new operator node

### parseFactor()

- if current token is digit:
  - return leaf
- if current token is `(`:
  - consume it
  - parse an expression
  - consume `)`
  - return subtree

---

## Java code

```java
/**
 * Definition for a binary tree node.
 * class Node {
 *     char val;
 *     Node left;
 *     Node right;
 *     Node() { this.val = ' '; }
 *     Node(char val) { this.val = val; }
 *     Node(char val, Node left, Node right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */
class Solution {
    private String s;
    private int index;

    public Node expTree(String s) {
        this.s = s;
        this.index = 0;
        return parseExpression();
    }

    private Node parseExpression() {
        Node left = parseTerm();

        while (index < s.length() &&
               (s.charAt(index) == '+' || s.charAt(index) == '-')) {
            char op = s.charAt(index++);
            Node right = parseTerm();
            left = new Node(op, left, right);
        }

        return left;
    }

    private Node parseTerm() {
        Node left = parseFactor();

        while (index < s.length() &&
               (s.charAt(index) == '*' || s.charAt(index) == '/')) {
            char op = s.charAt(index++);
            Node right = parseFactor();
            left = new Node(op, left, right);
        }

        return left;
    }

    private Node parseFactor() {
        char c = s.charAt(index);

        if (Character.isDigit(c)) {
            index++;
            return new Node(c);
        }

        // Must be '('
        index++; // skip '('
        Node node = parseExpression();
        index++; // skip ')'
        return node;
    }
}
```

---

## Complexity

### Time complexity

```text
O(n)
```

Every character is consumed a constant number of times.

### Space complexity

```text
O(n)
```

due to recursion depth in the worst case.

---

## Pros and cons

### Pros

- elegant
- directly reflects grammar and precedence
- linear time

### Cons

- requires comfort with parsing / grammar thinking
- slightly more abstract for some interview settings

---

# Comparing the approaches

## Approach 1: Recursive divide-and-conquer scan

- Time: `O(n^2)` worst case
- Space: `O(n)`
- Best when you want the most direct “find root operator and split” explanation

## Approach 2: Two stacks

- Time: `O(n)`
- Space: `O(n)`
- Best practical iterative solution

## Approach 3: Recursive descent

- Time: `O(n)`
- Space: `O(n)`
- Best conceptual parser if you like grammar-based solutions

---

# Which one should you use in an interview?

If asked for multiple approaches:

- start with **Approach 1** because it is the easiest to derive
- then improve to **Approach 2** or **Approach 3** for linear time

If you want the strongest production-quality solution:

- **Approach 2 (two stacks)** is excellent
- **Approach 3 (recursive descent)** is arguably the cleanest conceptually

---

# Final recommended solution

For most interview settings, this is the best balance of clarity and efficiency:

## Two-stacks solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public Node expTree(String s) {
        Deque<Node> nodes = new ArrayDeque<>();
        Deque<Character> ops = new ArrayDeque<>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isDigit(c)) {
                nodes.push(new Node(c));
            } else if (c == '(') {
                ops.push(c);
            } else if (c == ')') {
                while (ops.peek() != '(') {
                    buildTop(nodes, ops);
                }
                ops.pop();
            } else {
                while (!ops.isEmpty() && ops.peek() != '(' &&
                       precedence(ops.peek()) >= precedence(c)) {
                    buildTop(nodes, ops);
                }
                ops.push(c);
            }
        }

        while (!ops.isEmpty()) {
            buildTop(nodes, ops);
        }

        return nodes.pop();
    }

    private void buildTop(Deque<Node> nodes, Deque<Character> ops) {
        char op = ops.pop();
        Node right = nodes.pop();
        Node left = nodes.pop();
        nodes.push(new Node(op, left, right));
    }

    private int precedence(char c) {
        if (c == '+' || c == '-') return 1;
        return 2;
    }
}
```

It is linear, robust, and respects all requirements.
