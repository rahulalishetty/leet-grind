# Approach 2: Greatest Common Divisor (Optimal)

## Key Insight

If all frequencies must be divisible by X:

```
C_i % X == 0 for all i
```

Then X must divide the **greatest common divisor** of all counts.

Let:

```
g = gcd(C_1, C_2, ..., C_k)
```

If `g >= 2`, grouping is possible.
If `g == 1`, impossible.

---

## Proof

If X divides every `C_i`, then X is a common divisor.
The largest such divisor is `g`.

So valid grouping exists iff:

```
g >= 2
```

---

## Algorithm

1. Count frequencies.
2. Compute GCD of all non-zero counts.
3. Return `g >= 2`.

---

## Java Code

```java
class Solution {
    public boolean hasGroupsSizeX(int[] deck) {
        int[] count = new int[10000];
        for (int c : deck)
            count[c]++;

        int g = 0;
        for (int i = 0; i < 10000; ++i)
            if (count[i] > 0)
                g = (g == 0) ? count[i] : gcd(g, count[i]);

        return g >= 2;
    }

    private int gcd(int x, int y) {
        return y == 0 ? x : gcd(y, x % y);
    }
}
```

---

## Complexity

Frequency counting: O(N)

Each GCD: O(log N)

Overall:

```
O(N log N)
```

Space:

```
O(N)
```

---

## Comparison

| Brute Force         | GCD                             |
| ------------------- | ------------------------------- |
| Tries many X values | Single mathematical computation |
| O(N²) worst-case    | O(N log N)                      |
| Simple logic        | Elegant and optimal             |

---

## Final Takeaway

Brute force tests possible group sizes.
The optimal solution reduces the problem to computing a GCD across frequencies.

This turns a search problem into a number-theory problem.
