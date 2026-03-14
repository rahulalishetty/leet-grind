# 1628. Design an Expression Tree With Evaluate Function

## Problem summary

We are given a **postfix expression** (also called Reverse Polish Notation) and must build a **binary expression tree**.

Each tree node is either:

- a **number node** (leaf)
- an **operator node** with exactly two children

Supported operators:

- `+`
- `-`
- `*`
- `/`

After constructing the tree, the judge will call:

```java
evaluate()
```

on the root node, and that must return the value of the expression.

---

## Key observation

Postfix notation has a very useful property:

- when we see a number, it is a ready-made operand
- when we see an operator, it applies to the **two most recent complete expressions**

That means postfix expressions are extremely natural to parse using a **stack**.

Example:

```text
["3","4","+","2","*","7","/"]
```

Process left to right:

- push `3`
- push `4`
- see `+` → pop `4`, pop `3`, make `(3+4)`
- push `2`
- see `*` → pop `2`, pop `(3+4)`, make `((3+4)*2)`
- push `7`
- see `/` → pop `7`, pop `((3+4)*2)`, make `(((3+4)*2)/7)`

At the end, the stack contains exactly one tree: the answer.

---

# Approach 1: Classic stack construction with one generic operator node

## Intuition

This is the most direct solution.

We use a stack of `Node`.

### If token is a number

Create a leaf node and push it.

### If token is an operator

Pop:

- right subtree
- left subtree

Create an operator node with those children and push it back.

At the end, one node remains on the stack, the root of the expression tree.

---

## Tree design

We can implement:

- `NumNode` for integers
- `OpNode` for operators

`OpNode.evaluate()` can switch on the operator character and evaluate recursively.

This is simple and works well.

---

## Java code

```java
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This is the interface for the expression tree Node.
 * You should not remove it, and you can define some classes to implement it.
 */
abstract class Node {
    public abstract int evaluate();
}

/** Leaf node */
class NumNode extends Node {
    int value;

    NumNode(int value) {
        this.value = value;
    }

    @Override
    public int evaluate() {
        return value;
    }
}

/** Operator node */
class OpNode extends Node {
    char op;
    Node left;
    Node right;

    OpNode(char op, Node left, Node right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public int evaluate() {
        int a = left.evaluate();
        int b = right.evaluate();

        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            default: // '/'
                return a / b;
        }
    }
}

/**
 * This is the TreeBuilder class.
 */
class TreeBuilder {
    Node buildTree(String[] postfix) {
        Deque<Node> stack = new ArrayDeque<>();

        for (String token : postfix) {
            if (isOperator(token)) {
                Node right = stack.pop();
                Node left = stack.pop();
                stack.push(new OpNode(token.charAt(0), left, right));
            } else {
                stack.push(new NumNode(Integer.parseInt(token)));
            }
        }

        return stack.pop();
    }

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-")
            || token.equals("*") || token.equals("/");
    }
}
```

---

## Why right is popped before left

This is critical.

For postfix:

```text
a b -
```

means:

```text
a - b
```

When we see `-`, the stack top is `b`, below it is `a`.

So:

```java
Node right = stack.pop();
Node left = stack.pop();
```

must happen in exactly that order.

If reversed, subtraction and division would be wrong.

---

## Complexity

Let `n = postfix.length`.

### Time complexity

```text
O(n)
```

Each token is processed once.

### Space complexity

```text
O(n)
```

The stack can hold up to `O(n)` nodes in the worst case.

---

# Approach 2: More modular object-oriented design with one class per operator

## Intuition

The follow-up asks whether the design can be made **modular**, so that adding operators does not require changing existing evaluation logic.

That suggests a better object-oriented design:

- `NumberNode`
- `AddNode`
- `SubtractNode`
- `MultiplyNode`
- `DivideNode`

Each operator class knows how to evaluate itself.

This avoids a `switch` inside `evaluate()`.

That means if later we add operators like:

- `%`
- `^`
- max
- min

we can simply add new subclasses instead of editing existing logic.

This is often the best answer to the follow-up.

---

## Design idea

All operator nodes share:

- `left`
- `right`

So we can define an abstract binary operator base class:

```java
abstract class BinaryNode extends Node
```

Then subclasses implement:

```java
evaluate()
```

individually.

---

## Java code

```java
import java.util.ArrayDeque;
import java.util.Deque;

abstract class Node {
    public abstract int evaluate();
}

class NumberNode extends Node {
    private final int value;

    public NumberNode(int value) {
        this.value = value;
    }

    @Override
    public int evaluate() {
        return value;
    }
}

abstract class BinaryNode extends Node {
    protected Node left;
    protected Node right;

    public BinaryNode(Node left, Node right) {
        this.left = left;
        this.right = right;
    }
}

class AddNode extends BinaryNode {
    public AddNode(Node left, Node right) {
        super(left, right);
    }

    @Override
    public int evaluate() {
        return left.evaluate() + right.evaluate();
    }
}

class SubtractNode extends BinaryNode {
    public SubtractNode(Node left, Node right) {
        super(left, right);
    }

    @Override
    public int evaluate() {
        return left.evaluate() - right.evaluate();
    }
}

class MultiplyNode extends BinaryNode {
    public MultiplyNode(Node left, Node right) {
        super(left, right);
    }

    @Override
    public int evaluate() {
        return left.evaluate() * right.evaluate();
    }
}

class DivideNode extends BinaryNode {
    public DivideNode(Node left, Node right) {
        super(left, right);
    }

    @Override
    public int evaluate() {
        return left.evaluate() / right.evaluate();
    }
}

class TreeBuilder {
    Node buildTree(String[] postfix) {
        Deque<Node> stack = new ArrayDeque<>();

        for (String token : postfix) {
            if (isOperator(token)) {
                Node right = stack.pop();
                Node left = stack.pop();
                stack.push(createOperatorNode(token, left, right));
            } else {
                stack.push(new NumberNode(Integer.parseInt(token)));
            }
        }

        return stack.pop();
    }

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-")
            || token.equals("*") || token.equals("/");
    }

    private Node createOperatorNode(String op, Node left, Node right) {
        switch (op) {
            case "+":
                return new AddNode(left, right);
            case "-":
                return new SubtractNode(left, right);
            case "*":
                return new MultiplyNode(left, right);
            default: // "/"
                return new DivideNode(left, right);
        }
    }
}
```

---

## Why this is more modular

In Approach 1, the logic for all operators is centralized in this block:

```java
switch (op) {
    ...
}
```

So adding a new operator requires editing existing code.

In Approach 2:

- each operator has its own class
- evaluation behavior is encapsulated
- the system is closer to the Open/Closed Principle

You still need to teach the builder how to instantiate the new node class, but the **evaluation logic itself** remains untouched.

That is the main follow-up win.

---

## Complexity

Same as Approach 1.

### Time complexity

```text
O(n)
```

### Space complexity

```text
O(n)
```

---

# Approach 3: Recursive construction from postfix index (less common, but educational)

## Intuition

Since postfix is naturally processed left to right with a stack, that is the standard solution.

But there is another perspective:

If we read postfix from **right to left**, then:

- the current token is the root of the current subtree
- if it is a number, return a leaf
- if it is an operator, recursively build:
  - right subtree first
  - left subtree second

Why right first?

Because in postfix, the right operand is closer to the operator when scanning backward.

Example:

```text
3 4 + 2 * 7 /
```

Reading backward:

- `/` is root
- build right subtree from `7`
- build left subtree from `3 4 + 2 *`

This works, though it is less standard than the stack solution.

---

## Java code

```java
abstract class Node {
    public abstract int evaluate();
}

class NumNode extends Node {
    int value;

    NumNode(int value) {
        this.value = value;
    }

    @Override
    public int evaluate() {
        return value;
    }
}

class OpNode extends Node {
    char op;
    Node left;
    Node right;

    OpNode(char op, Node left, Node right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public int evaluate() {
        int a = left.evaluate();
        int b = right.evaluate();

        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            default: return a / b;
        }
    }
}

class TreeBuilder {
    private String[] postfix;
    private int index;

    Node buildTree(String[] postfix) {
        this.postfix = postfix;
        this.index = postfix.length - 1;
        return build();
    }

    private Node build() {
        String token = postfix[index--];

        if (isOperator(token)) {
            Node right = build();
            Node left = build();
            return new OpNode(token.charAt(0), left, right);
        }

        return new NumNode(Integer.parseInt(token));
    }

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-")
            || token.equals("*") || token.equals("/");
    }
}
```

---

## Complexity

### Time complexity

```text
O(n)
```

### Space complexity

```text
O(n)
```

because of recursion depth in the worst case.

---

# Comparison of approaches

## Approach 1: Stack + generic operator node

- simplest standard solution
- easiest to write in an interview
- evaluation uses a switch

## Approach 2: Stack + one class per operator

- best modular design
- strongest answer for the follow-up
- slightly more verbose

## Approach 3: Recursive construction from reverse postfix

- elegant
- less common
- good for conceptual understanding

---

# Recommended interview answer

If asked normally, start with **Approach 1**:

- easy
- efficient
- standard postfix parsing

If asked about the follow-up, then present **Approach 2**:

- move operator behavior into subclasses
- no need to change existing `evaluate()` logic when adding new operator classes

---

# Follow-up discussion: how to support more operators without changing evaluate logic?

The best answer is:

- keep `Node.evaluate()` polymorphic
- define one class per operator
- let each operator class implement its own behavior

For example, to add `%`:

```java
class ModNode extends BinaryNode {
    public ModNode(Node left, Node right) {
        super(left, right);
    }

    @Override
    public int evaluate() {
        return left.evaluate() % right.evaluate();
    }
}
```

Then the existing node classes do not need to change.

Only the builder or factory needs to know how to instantiate the new node class.

That is far more modular than a giant switch inside evaluate.

---

# Final recommended solution

For the original problem, this is a clean and strong answer:

```java
import java.util.ArrayDeque;
import java.util.Deque;

abstract class Node {
    public abstract int evaluate();
}

class NumNode extends Node {
    int value;

    NumNode(int value) {
        this.value = value;
    }

    @Override
    public int evaluate() {
        return value;
    }
}

class OpNode extends Node {
    char op;
    Node left;
    Node right;

    OpNode(char op, Node left, Node right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public int evaluate() {
        int a = left.evaluate();
        int b = right.evaluate();

        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            default:
                return a / b;
        }
    }
}

class TreeBuilder {
    Node buildTree(String[] postfix) {
        Deque<Node> stack = new ArrayDeque<>();

        for (String token : postfix) {
            if (isOperator(token)) {
                Node right = stack.pop();
                Node left = stack.pop();
                stack.push(new OpNode(token.charAt(0), left, right));
            } else {
                stack.push(new NumNode(Integer.parseInt(token)));
            }
        }

        return stack.pop();
    }

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-")
            || token.equals("*") || token.equals("/");
    }
}
```

This is the most practical solution, and the modular class-per-operator design is the best follow-up answer.
