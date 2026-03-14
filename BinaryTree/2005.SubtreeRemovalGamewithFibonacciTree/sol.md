# Fibonacci Tree Game – Detailed Explanation

## Problem Summary

A **Fibonacci Tree** is defined recursively:

- `order(0)` → empty tree
- `order(1)` → single node
- `order(n)` → a root node with:
  - left subtree = `order(n-2)`
  - right subtree = `order(n-1)`

Two players **Alice** and **Bob** play a game:

- Alice starts first.
- On each turn a player chooses **any node** and removes that node **and its entire subtree**.
- If a player is **forced to delete the root**, they **lose**.

Goal: determine whether **Alice wins** assuming both players play optimally.

---

# Key Insight

This problem is a **combinatorial game**. The standard tool for analyzing such games is the **Sprague‑Grundy theorem**.

Each position (tree) has a **Grundy value**:

- `0` → losing position
- `>0` → winning position

The current player wins **iff Grundy ≠ 0**.

---

# Structure of Fibonacci Tree

For `F(n)`:

```
      root
     /   \\
  F(n-2)  F(n-1)
```

Moves available:

- remove a node inside the left subtree
- remove a node inside the right subtree
- remove an entire subtree
- removing the **root loses immediately**

Thus the root behaves like a **poisoned node**.

---

# Grundy Recurrence

Let:

```
g(n) = Grundy value of Fibonacci tree of order n
```

Base cases:

```
g(0) = 0
g(1) = 0
```

For `n ≥ 2`:

```
g(n) = (g(n-2)+1) XOR (g(n-1)+1)
```

Why `+1`?

Because a player can remove the **entire subtree**, which behaves like one additional move beyond all internal subtree moves.

---

# Computing Initial Values

| n   | g(n) |
| --- | ---- |
| 1   | 0    |
| 2   | 1    |
| 3   | 3    |
| 4   | 6    |
| 5   | 3    |
| 6   | 3    |
| 7   | 0    |
| 8   | 5    |
| 9   | 7    |
| 10  | 14   |
| 11  | 7    |
| 12  | 7    |
| 13  | 0    |

Observe the pattern:

```
g(n) = 0 when n = 1, 7, 13, 19 ...
```

This means:

```
n ≡ 1 (mod 6)
```

These are **losing positions**.

---

# Final Rule

Alice loses when:

```
n % 6 == 1
```

Otherwise Alice wins.

---

# Optimal Strategy Interpretation

If the current position is **not** `6k+1`, Alice can always remove a subtree so that the resulting tree becomes `6k+1`.

That forces Bob into a losing state.

---

# Java Implementation

```java
class Solution {
    public boolean findGameWinner(int n) {
        return n % 6 != 1;
    }
}
```

Explanation:

```
n % 6 == 1  → losing position → Bob wins
otherwise   → winning position → Alice wins
```

---

# Complexity

Time Complexity

```
O(1)
```

Space Complexity

```
O(1)
```

No tree construction or DP is required once the mathematical pattern is discovered.

---

# Key Takeaways

1. The game reduces to a **subtree removal impartial game**.
2. Using **Sprague‑Grundy theory**, the Fibonacci structure leads to a recurrence.
3. The Grundy sequence reveals a **period‑6 pattern**.
4. The entire problem collapses to a simple rule:

```
Alice wins ⇔ n % 6 ≠ 1
```

This converts what appears to be a complex tree game into a **constant‑time arithmetic check**.
