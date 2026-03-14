# Binary Expression Tree Equivalence (Addition Only)

## Problem summary

We are given two binary expression trees.

Rules of the tree:

- leaf nodes are variables like `a`, `b`, `x`
- internal nodes are operators
- in this problem, the **only operator is `+`**

We must return whether the two trees are **equivalent**, meaning they evaluate to the same value **for every possible assignment of variables**.

Because the only operator is addition, the exact shape of the tree does **not** matter as long as the multiset of variables is the same.

Example:

```text
a + (b + c)
(b + c) + a
```

These are equivalent because addition is:

- associative
- commutative

So both expressions represent:

```text
a + b + c
```

---

# Core insight

Since only `+` exists, every expression tree can be flattened into a bag / frequency table of variables.

Two trees are equivalent **iff** their variable frequencies are identical.

That is the whole problem.

---

# Approach 1: DFS + frequency counting array

## Intuition

All variables are lowercase English letters, so there are only 26 possibilities.

We can traverse each tree and count how many times each variable appears.

For each `+` node:

- recurse left
- recurse right

For each variable node:

- increment its frequency

At the end, compare the two frequency arrays.

---

## Why this works

Because the only operation is `+`, the expression value is just the sum of all variable contributions.

So:

```text
a + b + a + c
```

is fully determined by:

- `a` appears 2 times
- `b` appears 1 time
- `c` appears 1 time

The tree structure becomes irrelevant.

---

## Algorithm

1. Create an integer array `count1[26]`
2. Create an integer array `count2[26]`
3. DFS over `root1` and fill `count1`
4. DFS over `root2` and fill `count2`
5. Compare the two arrays
6. If all frequencies match, return `true`, otherwise `false`

---

## Java code

```java
class Solution {
    public boolean checkEquivalence(Node root1, Node root2) {
        int[] count1 = new int[26];
        int[] count2 = new int[26];

        dfs(root1, count1);
        dfs(root2, count2);

        for (int i = 0; i < 26; i++) {
            if (count1[i] != count2[i]) {
                return false;
            }
        }
        return true;
    }

    private void dfs(Node node, int[] count) {
        if (node == null) {
            return;
        }

        if (node.val == '+') {
            dfs(node.left, count);
            dfs(node.right, count);
        } else {
            count[node.val - 'a']++;
        }
    }
}
```

---

## Complexity

Let `n` be the number of nodes in each tree.

### Time complexity

```text
O(n)
```

We visit each node exactly once.

### Space complexity

```text
O(h)
```

where `h` is tree height due to recursion stack.

In the worst case of a skewed tree:

```text
O(n)
```

The counting arrays are constant size: `O(1)`.

---

# Approach 2: DFS + HashMap frequency counting

## Intuition

The previous approach is best here because we only have 26 lowercase letters.

Still, a more general solution uses a `HashMap<Character, Integer>` instead of a fixed array.

This is useful if:

- variables were not limited to `a-z`
- operands were strings or larger symbols

The logic is the same:

- flatten the tree
- count variable occurrences
- compare maps

---

## Java code

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    public boolean checkEquivalence(Node root1, Node root2) {
        Map<Character, Integer> map1 = new HashMap<>();
        Map<Character, Integer> map2 = new HashMap<>();

        dfs(root1, map1);
        dfs(root2, map2);

        return map1.equals(map2);
    }

    private void dfs(Node node, Map<Character, Integer> map) {
        if (node == null) {
            return;
        }

        if (node.val == '+') {
            dfs(node.left, map);
            dfs(node.right, map);
        } else {
            map.put(node.val, map.getOrDefault(node.val, 0) + 1);
        }
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
O(k + h)
```

Where:

- `k` = number of distinct variables
- `h` = recursion stack height

Since at most 26 lowercase letters exist, this is effectively still bounded and small.

---

# Approach 3: DFS that returns a canonical signature

## Intuition

Another valid way is to convert each tree into a canonical representation and compare the results.

Because only `+` exists, a canonical signature can simply be the sorted sequence of variables.

Example:

```text
a + (c + b + a)
```

becomes:

```text
aabc
```

If both trees reduce to the same sorted signature, they are equivalent.

This is not as efficient as direct counting, but it is conceptually clear.

---

## Algorithm

1. Traverse the tree and collect all variables into a list
2. Sort the list
3. Convert the sorted list to a string
4. Do this for both trees
5. Compare the strings

---

## Java code

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Solution {
    public boolean checkEquivalence(Node root1, Node root2) {
        List<Character> list1 = new ArrayList<>();
        List<Character> list2 = new ArrayList<>();

        dfs(root1, list1);
        dfs(root2, list2);

        Collections.sort(list1);
        Collections.sort(list2);

        return list1.equals(list2);
    }

    private void dfs(Node node, List<Character> list) {
        if (node == null) {
            return;
        }

        if (node.val == '+') {
            dfs(node.left, list);
            dfs(node.right, list);
        } else {
            list.add(node.val);
        }
    }
}
```

---

## Complexity

### Time complexity

```text
O(n log n)
```

because of sorting.

### Space complexity

```text
O(n)
```

for the collected variables plus recursion stack.

---

# Approach 4: Iterative DFS / stack traversal

## Intuition

If you want to avoid recursion, you can do the same counting iteratively with an explicit stack.

This is useful when:

- recursion depth may be large
- you prefer iterative tree traversal

Logic remains unchanged:

- if node is `+`, expand both children
- otherwise count the variable

---

## Java code

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public boolean checkEquivalence(Node root1, Node root2) {
        int[] count1 = countVariables(root1);
        int[] count2 = countVariables(root2);

        for (int i = 0; i < 26; i++) {
            if (count1[i] != count2[i]) {
                return false;
            }
        }
        return true;
    }

    private int[] countVariables(Node root) {
        int[] count = new int[26];
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            Node node = stack.pop();
            if (node == null) {
                continue;
            }

            if (node.val == '+') {
                stack.push(node.left);
                stack.push(node.right);
            } else {
                count[node.val - 'a']++;
            }
        }

        return count;
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
O(h)
```

worst-case `O(n)` for the stack.

---

# Best approach

For this exact problem, **Approach 1** is the best.

Why?

- simplest
- linear time
- constant extra counting storage
- directly uses the problem constraint of lowercase letters

---

# Correctness argument

Suppose a tree contains only `+` operators.

Then the value represented by the tree is just the sum of all leaf variables, with repetitions.

Because addition is associative and commutative:

- re-grouping operands does not matter
- swapping operands does not matter

So two trees are equivalent if and only if each variable appears the same number of times in both trees.

That is exactly what the counting-array solution checks.

---

# Follow-up: what changes if `-` is also allowed?

This changes the problem **substantially**.

Subtraction is **not commutative** and **not associative**:

```text
a - b != b - a
(a - b) - c != a - (b - c)
```

So we can no longer treat the tree as just an unordered bag of variables.

## What breaks?

In the `+`-only version, both of these were equivalent:

```text
a + (b + c)
(b + c) + a
```

But with subtraction:

```text
a - (b - c)
(a - b) - c
```

These are not generally equal.

So frequency counting alone is no longer enough.

---

## One clean idea for handling `+` and `-`

Represent every expression as a signed frequency vector.

Examples:

```text
a + b      -> +a +b
a - b      -> +a -b
a - (b-c)  -> +a -b +c
```

This means while traversing the tree, carry a sign:

- for `+`: left and right keep the same sign
- for `-`: left keeps the same sign, right gets the negated sign

Then aggregate coefficients for each variable.

If the final signed coefficient map matches for both trees, the expressions are equivalent.

### Example

```text
a - (b - c)
```

Traversal gives:

- `a` with `+1`
- `b` with `-1`
- `c` with `+1`

So canonical form is:

```text
a - b + c
```

Another tree producing the same signed coefficients would be equivalent.

---

## Sketch for follow-up solution

```java
private void dfs(Node node, int sign, int[] coeff) {
    if (node == null) return;

    if (node.val == '+') {
        dfs(node.left, sign, coeff);
        dfs(node.right, sign, coeff);
    } else if (node.val == '-') {
        dfs(node.left, sign, coeff);
        dfs(node.right, -sign, coeff);
    } else {
        coeff[node.val - 'a'] += sign;
    }
}
```

Then compare the coefficient arrays.

This works because addition and subtraction over variables form a linear combination.

---

# Final recommended solution

```java
class Solution {
    public boolean checkEquivalence(Node root1, Node root2) {
        int[] count1 = new int[26];
        int[] count2 = new int[26];

        dfs(root1, count1);
        dfs(root2, count2);

        for (int i = 0; i < 26; i++) {
            if (count1[i] != count2[i]) {
                return false;
            }
        }
        return true;
    }

    private void dfs(Node node, int[] count) {
        if (node == null) {
            return;
        }

        if (node.val == '+') {
            dfs(node.left, count);
            dfs(node.right, count);
        } else {
            count[node.val - 'a']++;
        }
    }
}
```

This is the simplest and most efficient solution for the original `+`-only problem.
