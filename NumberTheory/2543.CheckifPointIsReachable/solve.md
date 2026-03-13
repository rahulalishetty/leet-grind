# 2543. Check if Point Is Reachable — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public boolean isReachable(int targetX, int targetY) {

    }
}
```

---

# Problem Restatement

We start at point:

```text
(1, 1)
```

We want to reach:

```text
(targetX, targetY)
```

Allowed moves from `(x, y)` are:

```text
(x, y - x)
(x - y, y)
(2x, y)
(x, 2y)
```

We need to decide whether the target point is reachable in a finite number of steps.

---

# Core Insight

This problem looks like graph search, but the coordinates can be as large as:

```text
10^9
```

So BFS or brute force over states is impossible.

The key is to find an **invariant**.

---

# Fundamental Observation: The GCD Behavior

Look at how the gcd of the coordinates changes.

Suppose current point is:

```text
(x, y)
```

Consider the four operations.

## 1. Subtraction moves

```text
(x, y - x)
(x - y, y)
```

These preserve gcd:

```text
gcd(x, y - x) = gcd(x, y)
gcd(x - y, y) = gcd(x, y)
```

So subtraction does **not** change the gcd.

---

## 2. Doubling moves

```text
(2x, y)
(x, 2y)
```

These may multiply the gcd by a factor of 2, but they do not introduce any odd prime factor into the gcd that was not already present.

So the gcd can only change by powers of 2.

---

# Starting From (1,1)

At the starting point:

```text
gcd(1, 1) = 1
```

From there:

- subtraction keeps gcd the same
- doubling may multiply gcd by powers of 2

Therefore every reachable point must satisfy:

```text
gcd(targetX, targetY) = power of 2
```

That gives a necessary condition.

It turns out this condition is also sufficient.

So the answer is:

> `(targetX, targetY)` is reachable **iff**
> `gcd(targetX, targetY)` is a power of 2.

---

# Why the Condition Is Also Sufficient

Suppose:

```text
g = gcd(targetX, targetY)
```

and `g` is a power of 2.

Then divide both coordinates by `g`:

```text
a = targetX / g
b = targetY / g
```

Now:

```text
gcd(a, b) = 1
```

Using subtraction moves (the Euclidean algorithm idea), any coprime pair can be reduced back to `(1,1)`.

Then the power-of-2 part can be built back using doubling operations.

So if the gcd is a power of 2, the point is reachable.

---

# Approach 1 — GCD Is a Power of 2 (Recommended)

## Idea

1. Compute:

```text
g = gcd(targetX, targetY)
```

2. Check whether `g` is a power of 2.

A number is a power of 2 iff:

```text
(g & (g - 1)) == 0
```

for positive `g`.

---

## Java Code

```java
class Solution {
    public boolean isReachable(int targetX, int targetY) {
        int g = gcd(targetX, targetY);
        return (g & (g - 1)) == 0;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Complexity

Computing gcd takes:

```text
O(log(min(targetX, targetY)))
```

So:

```text
Time:  O(log(min(targetX, targetY)))
Space: O(1)
```

This is the best solution.

---

# Approach 2 — Remove All Factors of 2 From the GCD

## Idea

Another equivalent way to check whether gcd is a power of 2 is:

1. compute gcd
2. divide out all factors of 2
3. if the remaining number is 1, then gcd was a power of 2

This is mathematically the same as Approach 1.

---

## Java Code

```java
class Solution {
    public boolean isReachable(int targetX, int targetY) {
        int g = gcd(targetX, targetY);

        while ((g & 1) == 0) {
            g >>= 1;
        }

        return g == 1;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Complexity

Still:

```text
Time:  O(log(min(targetX, targetY)))
Space: O(1)
```

---

# Approach 3 — Reverse Euclidean Reduction (Conceptual)

## Idea

Instead of thinking from `(1,1)` forward, think backward from `(targetX, targetY)`.

Backward versions of the moves correspond to:

- adding one coordinate to the other
- halving even coordinates

This suggests a Euclidean-style reduction process.

The critical insight is that subtraction-like reasoning reduces the pair while preserving gcd, and only factors of 2 matter for the scaling part.

So again we end up with the same criterion:

```text
gcd(targetX, targetY) must be a power of 2
```

This approach is more conceptual than implementation-focused.

---

## Why it helps

It explains _why_ the answer depends on gcd:

- subtraction steps are basically Euclid’s algorithm
- halving/doubling controls only powers of 2

So reachability is governed exactly by the odd part of the gcd.

If the odd part is bigger than 1, the point is not reachable.

---

# Approach 4 — BFS / Graph Search (Too Slow)

## Idea

Treat each point as a node in an infinite graph and perform BFS from `(1,1)`.

---

## Why it fails

Coordinates can be as large as:

```text
10^9
```

The reachable state space is enormous.

So explicit graph search is infeasible.

This approach is only useful as an initial intuition before finding the gcd invariant.

---

# Detailed Proof Sketch

Let:

```text
g = gcd(targetX, targetY)
```

## Necessary part

Start from `(1,1)`.

Initial gcd:

```text
1
```

Allowed moves:

- subtraction preserves gcd
- doubling may multiply gcd by 2

So after any number of moves, gcd can only be:

```text
2^k
```

for some `k >= 0`.

Therefore, if the target is reachable, its gcd must be a power of 2.

---

## Sufficient part

Now suppose:

```text
g = gcd(targetX, targetY) = 2^k
```

Then define:

```text
a = targetX / g
b = targetY / g
```

Now:

```text
gcd(a, b) = 1
```

For coprime pairs, Euclidean-style subtraction can reduce them to `(1,1)`.

So `(a,b)` is reducible to `(1,1)`.

Then using doubling operations, we can restore the removed factor `2^k`.

Hence `(targetX, targetY)` is reachable.

So the condition is both necessary and sufficient.

---

# Example Walkthrough

## Example 1

```text
targetX = 6
targetY = 9
```

Compute gcd:

```text
gcd(6, 9) = 3
```

Is 3 a power of 2?

```text
No
```

So the point is **not reachable**.

Answer:

```text
false
```

---

## Example 2

```text
targetX = 4
targetY = 7
```

Compute gcd:

```text
gcd(4, 7) = 1
```

Is 1 a power of 2?

```text
Yes
```

So the point is reachable.

Answer:

```text
true
```

---

# Common Pitfalls

## 1. Trying to simulate moves directly

That quickly becomes intractable because coordinates can be huge.

---

## 2. Missing that subtraction preserves gcd

This is the main structural fact behind the solution.

---

## 3. Forgetting that doubling only changes gcd by factors of 2

It cannot create odd gcd factors from nowhere.

---

## 4. Thinking every coprime pair is enough without considering scaling

The full condition is not merely:

```text
gcd(targetX, targetY) == 1
```

It is:

```text
gcd(targetX, targetY) is a power of 2
```

because powers of 2 can be introduced through doubling.

---

# Best Approach

## Recommended: Compute gcd and test whether it is a power of 2

This is the cleanest and fastest method.

It captures the exact invariant of the operations.

---

# Final Recommended Java Solution

```java
class Solution {
    public boolean isReachable(int targetX, int targetY) {
        int g = gcd(targetX, targetY);
        return (g & (g - 1)) == 0;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

# Complexity Summary

```text
Time:  O(log(min(targetX, targetY)))
Space: O(1)
```

---

# Final Takeaway

The problem is really about a gcd invariant.

Starting from `(1,1)`:

- subtraction keeps gcd unchanged
- doubling only adds factors of 2

Therefore the target is reachable exactly when:

```text
gcd(targetX, targetY)
```

is a power of 2.
