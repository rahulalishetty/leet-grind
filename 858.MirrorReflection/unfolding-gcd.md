# Mirror Reflection — Approach 2: Unfolding + Number Theory (GCD / LCM)

## Idea (Unfolding)

Instead of bouncing the ray, **reflect the room** across walls and let the ray travel in a straight line.

Each time the ray would reflect off a wall, imagine the square mirrored across that wall. In this unfolded plane of mirrored squares, the ray never turns — it continues straight.

Starting direction is from `(0,0)` toward `(p, q)`.

So after scaling by an integer `k`, the ray reaches:

- `x = k * p`
- `y = k * q`

in the unfolded plane.

---

## When does the ray hit a receptor?

A receptor corresponds to hitting a **corner** of some mirrored square.
Corners occur when both coordinates are multiples of `p`:

- `x = n * p`
- `y = m * p`

We already have `x = k * p`.
So we only need `y = k * q` to also be a multiple of `p`:

> Find the smallest positive integer `k` such that `p | (kq)`.

---

## Proof: Why `k = p / gcd(p, q)`

Let `g = gcd(p, q)`. Write:

- `p = g * p'`
- `q = g * q'`
  where `gcd(p', q') = 1`.

We need `p | (kq)`:

`g p' | k (g q')`
Cancel `g`:

`p' | k q'`

Since `gcd(p', q') = 1`, the only way `p'` divides `k q'` is for `p'` to divide `k`.

Therefore the smallest positive `k` is:

> `k = p' = p / gcd(p, q)`.

---

## Determining WHICH receptor (Parity argument)

In the unfolded plane, the first hit point is `(k*p, k*q)`.

Let:

- `k = p / g`
- `m = (k*q)/p = q / g`

Only parity (odd/even) matters because each reflection flips which side maps to the original room.

- `k` odd → right wall (`x = p`)
- `k` even → left wall (`x = 0`)
- `m` odd → top wall (`y = p`)
- `m` even → bottom (`y = 0`)

Thus:

| k parity | m parity | receptor |
| -------: | -------: | -------: |
|      odd |     even | 0 (p, 0) |
|      odd |      odd | 1 (p, p) |
|     even |      odd | 2 (0, p) |

---

## Java Code Example (Math / GCD)

```java
class Solution {
    public int mirrorReflection(int p, int q) {
        int g = gcd(p, q);

        int k = p / g; // horizontal rooms crossed
        int m = q / g; // vertical rooms crossed

        if (k % 2 == 1 && m % 2 == 0) return 0;
        if (k % 2 == 1 && m % 2 == 1) return 1;
        return 2;
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

## Complexity & Tradeoffs

- **Time:** `O(log(min(p,q)))` due to gcd.
- **Space:** O(1).
- **Benefits:** exact integer arithmetic; no precision issues; fastest and most robust.
- **Interpretation:** converts geometry into divisibility + parity.

This is the preferred production/interview solution.
