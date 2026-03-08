# Mirror Reflection — Approach 1: Geometry Simulation (Ray + Wall Collisions)

## Idea

Model the laser ray inside a `p x p` square starting at `(0,0)` and moving with direction vector `(rx, ry) = (p, q)`.

At any moment the ray position is:

- `x(t) = x + rx * t`
- `y(t) = y + ry * t`

When the ray hits a wall (`x = 0`, `x = p`, `y = 0`, `y = p`), the corresponding component of the direction flips sign (reflection).

This approach is a **time-stepped event simulation** where each step jumps directly to the _next_ wall hit.

---

## Key Concepts

### 1) `EPS` (Epsilon tolerance)

Floating-point arithmetic cannot reliably represent many rational values exactly.
So instead of checking `x == p`, we check:

```java
Math.abs(x - p) < EPS
```

`EPS` is a tiny tolerance (e.g., `1e-6`) that treats two doubles as “equal enough”.

**Why needed:** Without it, due to roundoff, the ray might never be detected as exactly reaching a receptor or wall, causing incorrect reflections or infinite loops.

---

### 2) What `t` means

`t` is the **time** (parameter) until the ray hits the next wall. The ray moves continuously forward, so `t` must be **positive**.

If we are at `(x, y)` moving by `(rx, ry)`, then the time to reach each wall is:

- left wall `x = 0`: `t = (0 - x) / rx`
- right wall `x = p`: `t = (p - x) / rx`
- bottom `y = 0`: `t = (0 - y) / ry`
- top `y = p`: `t = (p - y) / ry`

We only keep times where `t > EPS` (strictly forward movement), then pick the minimum.

---

## Proof Sketch: Why the minimum positive `t` is correct

Let the ray be the continuous path `P(t) = (x + rx t, y + ry t)` for `t >= 0`.

Each wall intersection time computed above is a solution to a linear equation like `x + rx t = 0`.
Among all **valid future** solutions (`t > 0`), the smallest one is the first time the ray reaches _any_ boundary.

If we chose a larger time `t2 > t1`:

- At time `t1` the ray has already reached a wall.
- The segment from `t1` to `t2` would go outside the square (or pass through a wall without reflecting).
  That violates the physical reflection model.

Therefore, the next collision is at `min { t_i | t_i > 0 }`.

---

## Java Code Example (Simulation)

```java
class Solution {
    private static final double EPS = 1e-6;

    public int mirrorReflection(int p, int q) {
        double x = 0, y = 0;
        double rx = p, ry = q;

        // Loop until the ray reaches a receptor corner.
        while (!isReceptor(x, y, p)) {
            double t = Double.POSITIVE_INFINITY;

            // Candidate times to hit each wall (only forward: t > EPS)
            if ((0 - x) / rx > EPS) t = Math.min(t, (0 - x) / rx);
            if ((p - x) / rx > EPS) t = Math.min(t, (p - x) / rx);
            if ((0 - y) / ry > EPS) t = Math.min(t, (0 - y) / ry);
            if ((p - y) / ry > EPS) t = Math.min(t, (p - y) / ry);

            // Move to the next collision point
            x += rx * t;
            y += ry * t;

            // Reflect if we hit vertical / horizontal wall
            if (close(x, 0) || close(x, p)) rx *= -1;
            if (close(y, 0) || close(y, p)) ry *= -1;
        }

        // Determine which receptor we hit
        if (close(x, p) && close(y, p)) return 1; // (p, p)
        if (close(x, p) && close(y, 0)) return 0; // (p, 0)
        return 2;                                  // (0, p)
    }

    private boolean isReceptor(double x, double y, int p) {
        return (close(x, p) && (close(y, 0) || close(y, p)))
            || (close(x, 0) && close(y, p));
    }

    private boolean close(double a, double b) {
        return Math.abs(a - b) < EPS;
    }
}
```

---

## Complexity & Tradeoffs

- **Time:** proportional to number of reflections until a receptor is reached (can be many).
- **Space:** O(1).
- **Risks:** floating-point precision pitfalls; requires `EPS`; must avoid infinite loops.

This approach is intuitive (it mirrors real physics), but it’s not the most robust/optimal for large inputs.
