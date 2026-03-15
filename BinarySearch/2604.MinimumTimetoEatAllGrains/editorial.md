# 2604. Minimum Time to Eat All Grains — Binary Search Intuition

## Intuition

The idea is to solve the problem using **Binary Search**.

Why binary search?

We want to find the **minimum time `t`** such that all grains can be eaten by hens acting optimally.

Think of the system like this:

- **Hens = Workers**
- **Grains = Jobs**
- All workers can work **concurrently**.

So if we give each worker a **maximum allowed time `t`**, we can check whether it is possible to finish all jobs within that time.

Thus the problem becomes:

> Given a time `t`, can all hens eat all grains?

If yes → try smaller `t`
If no → increase `t`

This monotonic behavior allows **binary search on time**.

---

# Worker Strategy

For a fixed time `t`, each hen tries to eat as many grains as possible.

We define two types of work for each hen:

### MUST Tasks

If there are grains on the **left side** of the hen, the hen **must** be able to reach them.

If a hen cannot reach the leftmost grain within time `t`, then the schedule is impossible.

### BONUS Tasks

After finishing mandatory left-side grains, if the hen still has remaining time, it can help by eating grains on the **right side**.

---

# Two Possible Movement Strategies

Each hen can optimize its movement in two ways.

## Option A: Go Left First, Then Right

1. Move left to clear all left grains.
2. Return to the hen’s starting position.
3. If time remains, move right to eat additional grains.

Time cost:

```
(h - L) * 2 + (R - h)
```

Where

```
h = hen position
L = leftmost grain
R = rightmost grain eaten
```

The `*2` appears because the hen must go **left and return** before moving right.

---

## Option B: Go Right First, Then Left

Instead of immediately going left, the hen can go right first while still ensuring it can later reach all left grains.

Cost:

```
(R - h) * 2 + (h - L)
```

This means:

1. Move right first
2. Come back toward the left side
3. Still cover all required grains

---

# Example

```
Hens   = [8]
Grains = [0,0,7,9]
```

### Option A (Left → Right)

```
(8 - 0) * 2 + (9 - 8)
= 16 + 1
= 17
```

### Option B (Right → Left)

```
(9 - 8) * 2 + (8 - 0)
= 2 + 8
= 10
```

Option B is better here.

---

# Binary Search Structure

Binary search tries different values of time `t`.

Search range:

```
0 → max possible time
```

The upper bound can be estimated as:

```
maxT =
    if grains[0] <= hens[0]:
        (2 * (hens[0] - grains[0])) + grains[last] - hens[0]
    else:
        grains[last] - hens[0]
```

This represents the worst-case scenario where the **first hen does all the work**.

---

# Complexity

Let

```
hn = hens.length
gn = grains.length
```

### Time Complexity

```
hn log hn
+ gn log gn
+ log(maxT)
```

Explanation:

- Sorting hens and grains
- Binary search on time
- Linear feasibility check

### Space Complexity

```
O(1)
```

(ignoring sorting overhead)

---

# Java Implementation

```java
class Solution {

    /*
    Note: Why apply time t to each Hen separately?
    - Because hens act like concurrent workers.
    - Each hen should try to consume as many grains as possible
      to reduce work for the next hen.
    */
    private boolean binarySearch(int[] hens, int[] grains, int t, int hn, int gn) {

        int h = 0;
        int g = 0;

        while (h < hn && g < gn) {

            if (grains[g] <= hens[h]) { // grain is left of hen

                int y = hens[h] - grains[g];

                if (y > t) return false;

                // Option B: move left then right
                int moveLeftThenRight = g;

                while (moveLeftThenRight < gn && grains[moveLeftThenRight] <= hens[h])
                    moveLeftThenRight++;

                int currT = t - (y * 2);

                while (moveLeftThenRight < gn &&
                       currT >= (grains[moveLeftThenRight] - hens[h]))
                    moveLeftThenRight++;

                // Option A: move right then left
                int moveRightThenLeft = g;

                while (moveRightThenLeft < gn &&
                       grains[moveRightThenLeft] - grains[g] <=
                       t - (grains[moveRightThenLeft] - hens[h]))
                    moveRightThenLeft++;

                g = Math.max(moveLeftThenRight, moveRightThenLeft);

            } else {

                while (g < gn && t >= (grains[g] - hens[h]))
                    g++;

            }

            h++;
        }

        return g == gn;
    }


    public int minimumTime(int[] hens, int[] grains) {

        int hn = hens.length;
        int gn = grains.length;

        Arrays.sort(hens);
        Arrays.sort(grains);

        int minT = 0;

        int maxT =
            (grains[0] <= hens[0])
            ? (2 * (hens[0] - grains[0])) + grains[gn - 1] - hens[0]
            : grains[gn - 1] - hens[0];

        int res = maxT;

        while (minT <= maxT) {

            int currT = minT + (maxT - minT) / 2;

            if (binarySearch(hens, grains, currT, hn, gn)) {

                res = currT;
                maxT = currT - 1;

            } else {

                minT = currT + 1;

            }
        }

        return res;
    }
}
```

---

# Key Takeaways

This problem combines several classic algorithmic patterns:

### 1. Binary Search on Answer

Used when a **feasibility function is monotonic**.

### 2. Greedy Worker Assignment

Each hen eats the **maximum possible grains**.

### 3. Interval Movement Optimization

Each hen optimizes movement using:

```
Left → Right
or
Right → Left
```

### 4. Two-pointer Greedy Simulation

The grains pointer only moves forward.

---

This combination makes the algorithm efficient even for the constraint:

```
2 * 10^4
```
