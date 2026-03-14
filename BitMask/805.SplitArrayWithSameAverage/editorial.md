# 805. Split Array With Same Average — Approach 1

## Meet in the Middle

### Intuition

We want to split the array into two non‑empty subsets **B** and **C** such that:

```
average(B) = average(C)
```

Let:

- `A` be the input array
- `N` = number of elements
- `S` = total sum of array

Assume subset **B** has:

```
K elements
sum = X
```

Subset **C** therefore has:

```
N - K elements
sum = S - X
```

The condition becomes:

```
X / K = (S - X) / (N - K)
```

Solving:

```
X(N - K) = (S - X)K
```

which simplifies to:

```
X / K = S / N
```

This means:

```
average(B) = average(A)
```

So the problem reduces to finding a subset **B** whose average equals the overall array average.

---

## Key Transformation

Let:

```
μ = average(A)
```

If we subtract the average from each element:

```
A[i] = A[i] - μ
```

Then the goal becomes:

```
Find a non‑empty subset whose sum = 0
```

---

## Meet in the Middle Idea

Trying all subsets directly takes:

```
O(2^N)
```

which is too slow when `N ≤ 30`.

Instead:

1. Split the array into two halves.
2. Compute all subset sums of each half.
3. Store them in sets.

Then check if:

- any subset directly sums to `0`, or
- a subset in the left half cancels with one in the right half.

Meaning:

```
x (from left) + y (from right) = 0
```

or

```
y = -x
```

---

## Important Edge Case

We must avoid selecting subsets that cause **one side of the partition to be empty**.

If:

```
sleft = sum(first half)
sright = sum(second half)
```

we cannot choose both together because that represents choosing the **entire array**.

---

# Java Implementation

```java
import java.awt.Point;

class Solution {
    public boolean splitArraySameAverage(int[] A) {
        int N = A.length;
        int S = 0;
        for (int x: A) S += x;
        if (N == 1) return false;

        int g = gcd(S, N);
        Point mu = new Point(-(S/g), N/g);

        List<Point> A2 = new ArrayList();
        for (int x: A)
            A2.add(fracAdd(new Point(x, 1), mu));

        Set<Point> left = new HashSet();
        left.add(A2.get(0));

        for (int i = 1; i < N/2; ++i) {
            Set<Point> left2 = new HashSet();
            Point z = A2.get(i);
            left2.add(z);

            for (Point p: left) {
                left2.add(p);
                left2.add(fracAdd(p, z));
            }

            left = left2;
        }

        if (left.contains(new Point(0, 1))) return true;

        Set<Point> right = new HashSet();
        right.add(A2.get(N-1));

        for (int i = N/2; i < N-1; ++i) {
            Set<Point> right2 = new HashSet();
            Point z = A2.get(i);
            right2.add(z);

            for (Point p: right) {
                right2.add(p);
                right2.add(fracAdd(p, z));
            }

            right = right2;
        }

        if (right.contains(new Point(0, 1))) return true;

        Point sleft = new Point(0, 1);
        for (int i = 0; i < N/2; ++i)
            sleft = fracAdd(sleft, A2.get(i));

        Point sright = new Point(0, 1);
        for (int i = N/2; i < N; ++i)
            sright = fracAdd(sright, A2.get(i));

        for (Point ha: left) {
            Point ha2 = new Point(-ha.x, ha.y);
            if (right.contains(ha2) && (!ha.equals(sleft) || !ha2.equals(sright)))
                return true;
        }

        return false;
    }

    public Point fracAdd(Point A, Point B) {
        int numer = A.x * B.y + B.x * A.y;
        int denom = A.y * B.y;

        int g = gcd(numer, denom);
        numer /= g;
        denom /= g;

        if (denom < 0) {
            numer *= -1;
            denom *= -1;
        }

        return new Point(numer, denom);
    }

    public int gcd(int a, int b) {
       if (b==0) return a;
       return gcd(b, a%b);
    }
}
```

---

# Complexity Analysis

Let `N` be the number of elements.

### Time Complexity

```
O(2^(N/2))
```

We compute subset sums for both halves independently.

### Space Complexity

```
O(2^(N/2))
```

We store all subset sums for each half.
