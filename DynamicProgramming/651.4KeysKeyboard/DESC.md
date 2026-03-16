# 651. 4 Keys Keyboard

Imagine you have a special keyboard with the following keys:

- **A**: Print one `'A'` on the screen.
- **Ctrl-A**: Select the whole screen.
- **Ctrl-C**: Copy selection to buffer.
- **Ctrl-V**: Print buffer on screen appending it after what has already been printed.

Given an integer `n`, return the **maximum number of 'A'** you can print on the screen with **at most `n` key presses**.

---

# Examples

## Example 1

**Input**

```
n = 3
```

**Output**

```
3
```

**Explanation**

The best sequence of operations is:

```
A, A, A
```

Each press prints one `A`, so the total becomes **3**.

---

## Example 2

**Input**

```
n = 7
```

**Output**

```
9
```

**Explanation**

Optimal sequence:

```
A, A, A, Ctrl-A, Ctrl-C, Ctrl-V, Ctrl-V
```

Step-by-step:

| Step | Operation | Screen         |
| ---- | --------- | -------------- |
| 1    | A         | A              |
| 2    | A         | AA             |
| 3    | A         | AAA            |
| 4    | Ctrl-A    | AAA (selected) |
| 5    | Ctrl-C    | buffer = AAA   |
| 6    | Ctrl-V    | AAAAAA         |
| 7    | Ctrl-V    | AAAAAAAAA      |

Total `A`s = **9**.

---

# Constraints

```
1 <= n <= 50
```

---

# Key Observations

There are **two types of strategies**:

### 1. Typing 'A' directly

Each key press prints one `A`.

Example:

```
A A A A A
```

After `n` presses → `n` A's.

---

### 2. Copy-Paste Strategy

The powerful operations are:

```
Ctrl-A
Ctrl-C
Ctrl-V
```

The idea is:

1. Build some A's.
2. Select them all.
3. Copy them.
4. Paste repeatedly.

Example:

```
AAA → Ctrl-A → Ctrl-C → Ctrl-V → Ctrl-V
```

This multiplies the current number of A's.

---

# Problem Goal

We want to determine:

> When should we **stop typing `A` and start copy-pasting** to maximize the total output?

Because:

- Copy operations cost **2 presses** (`Ctrl-A`, `Ctrl-C`).
- Every additional **Ctrl-V** duplicates the buffer.

---

# Why Dynamic Programming Works

The optimal solution depends on **previous states**.

If `dp[i]` represents:

```
maximum number of A's obtainable with i key presses
```

Then we can build the answer progressively.

Two possibilities exist for step `i`:

### Option 1 — Press 'A'

```
dp[i] = dp[i-1] + 1
```

### Option 2 — Use Copy-Paste

Suppose we stop typing at step `j`.

Then we perform:

```
Ctrl-A
Ctrl-C
Ctrl-V ...
```

Total operations used:

```
(j presses) + 2 + k
```

Result:

```
dp[j] * (k + 1)
```

We choose the maximum among all valid splits.

---

# Java Implementation (Dynamic Programming)

```java
class Solution {
    public int maxA(int n) {
        int[] dp = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            dp[i] = dp[i - 1] + 1;

            for (int j = 3; j < i; j++) {
                dp[i] = Math.max(dp[i], dp[i - j] * (j - 1));
            }
        }

        return dp[n];
    }
}
```

---

# Complexity Analysis

## Time Complexity

```
O(n^2)
```

For each `i`, we check all possible breakpoints.

---

## Space Complexity

```
O(n)
```

We store results for `n` states.

---

# Key Insight

The optimal sequence always follows this structure:

```
A A A ... A
Ctrl-A
Ctrl-C
Ctrl-V
Ctrl-V
Ctrl-V ...
```

Meaning:

1. Build a base number of `A`s.
2. Copy them.
3. Paste repeatedly to multiply them.

The challenge is choosing **when to switch from typing to copying**.

Dynamic Programming efficiently evaluates all possibilities.

---

# Final Takeaway

The problem demonstrates a common pattern:

**Optimization with operation sequences**.

The optimal strategy is:

```
build → copy → paste multiple times
```

rather than typing all characters individually.
