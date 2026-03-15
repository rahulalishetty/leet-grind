# 774. Minimize Max Distance to Gas Station — Approaches

## Approach 1: Dynamic Programming (Memory Limit Exceeded)

### Intuition

Let `dp[n][k]` represent the minimum possible maximum interval distance when we add `k` stations across the first `n` intervals.

Let `deltas[i] = stations[i+1] - stations[i]`.

If we add `x` stations to the `(n+1)`th interval, the largest subinterval there becomes:

```
deltas[n+1] / (x + 1)
```

The remaining `k-x` stations are distributed across the previous intervals.

Thus:

```
dp[n+1][k] = min over x in [0..k] of max( deltas[n+1]/(x+1), dp[n][k-x] )
```

### Implementation

```java
class Solution {
    public double minmaxGasDist(int[] stations, int K) {
        int N = stations.length;
        double[] deltas = new double[N-1];
        for (int i = 0; i < N-1; ++i)
            deltas[i] = stations[i+1] - stations[i];

        double[][] dp = new double[N-1][K+1];

        for (int i = 0; i <= K; ++i)
            dp[0][i] = deltas[0] / (i+1);

        for (int p = 1; p < N-1; ++p)
            for (int k = 0; k <= K; ++k) {
                double bns = 999999999;
                for (int x = 0; x <= k; ++x)
                    bns = Math.min(bns, Math.max(deltas[p] / (x+1), dp[p-1][k-x]));
                dp[p][k] = bns;
            }

        return dp[N-2][K];
    }
}
```

### Complexity

Time Complexity

```
O(N * K^2)
```

Space Complexity

```
O(N * K)
```

---

# Approach 2: Brute Force (Time Limit Exceeded)

### Intuition

We repeatedly add a station to the **largest current interval**.

Each original interval `i` has a counter:

```
count[i]
```

representing how many pieces the interval has been split into.

The maximum subinterval becomes:

```
deltas[i] / count[i]
```

### Implementation

```java
class Solution {
    public double minmaxGasDist(int[] stations, int K) {
        int N = stations.length;

        double[] deltas = new double[N-1];
        for (int i = 0; i < N-1; ++i)
            deltas[i] = stations[i+1] - stations[i];

        int[] count = new int[N-1];
        Arrays.fill(count, 1);

        for (int k = 0; k < K; ++k) {
            int best = 0;
            for (int i = 0; i < N-1; ++i)
                if (deltas[i] / count[i] > deltas[best] / count[best])
                    best = i;

            count[best]++;
        }

        double ans = 0;
        for (int i = 0; i < N-1; ++i)
            ans = Math.max(ans, deltas[i] / count[i]);

        return ans;
    }
}
```

### Complexity

Time Complexity

```
O(N * K)
```

Space Complexity

```
O(N)
```

---

# Approach 3: Heap (Time Limit Exceeded)

### Intuition

Instead of scanning every interval each time, we maintain a **max heap** storing the current largest interval.

Each heap node stores:

```
(interval length, number of parts)
```

The priority is:

```
interval_length / parts
```

### Implementation

```java
class Solution {
    public double minmaxGasDist(int[] stations, int K) {
        int N = stations.length;

        PriorityQueue<int[]> pq = new PriorityQueue<int[]>((a, b) ->
            (double)b[0]/b[1] < (double)a[0]/a[1] ? -1 : 1);

        for (int i = 0; i < N-1; ++i)
            pq.add(new int[]{stations[i+1] - stations[i], 1});

        for (int k = 0; k < K; ++k) {
            int[] node = pq.poll();
            node[1]++;
            pq.add(node);
        }

        int[] node = pq.poll();
        return (double)node[0] / node[1];
    }
}
```

### Complexity

Time Complexity

```
O(N + K log N)
```

Space Complexity

```
O(N)
```

---

# Approach 4: Preprocessing + Heap + Greedy (Accepted)

### Intuition

The previous heap approach inserts stations **one by one**, which is inefficient when `K` is large.

Instead we first estimate how many stations each interval should receive using an upper bound distance:

```
D = (stations[last] - stations[first]) / (K + 1)
```

Then we refine using a heap.

### Algorithm

1. Compute interval lengths

```
Li = stations[i+1] - stations[i]
```

2. Estimate stations per interval

```
ki = floor(Li / D)
```

3. Compute resulting distance

```
di = Li / (ki + 1)
```

4. Push `(di, Li, ki)` into heap

5. Distribute remaining stations greedily.

### Implementation

```java
import java.util.*;

class Solution {
    public double minmaxGasDist(int[] stations, int K) {

        int N = stations.length;

        double distUpper =
            (stations[N - 1] - stations[0]) / (double)(K + 1);

        PriorityQueue<double[]> pq =
            new PriorityQueue<>((a, b) -> Double.compare(b[0], a[0]));

        for (int i = 0; i < N - 1; i++) {

            double interval = stations[i + 1] - stations[i];

            int ki = (int)Math.floor(interval / distUpper);

            double actualDist = interval / (ki + 1);

            K -= ki;

            pq.add(new double[]{actualDist, interval, ki});
        }

        while (K-- > 0) {

            double[] top = pq.poll();

            double interval = top[1];

            int ki = (int)top[2] + 1;

            pq.add(new double[]{interval / (ki + 1), interval, ki});
        }

        return pq.peek()[0];
    }
}
```

### Complexity

Time Complexity

```
O(N log N)
```

Space Complexity

```
O(N)
```

---

# Approach 5: Binary Search (Accepted)

### Intuition

Let

```
possible(D)
```

check if we can make **every interval ≤ D** using at most `K` new stations.

This function is **monotonic**, allowing binary search.

### Algorithm

Binary search on distance `D`.

For interval length:

```
X = stations[i+1] - stations[i]
```

Stations required:

```
floor(X / D)
```

Total required stations:

```
sum floor(Xi / D)
```

If this ≤ K → feasible.

### Implementation

```java
class Solution {

    public double minmaxGasDist(int[] stations, int K) {

        double lo = 0;
        double hi = 1e8;

        while (hi - lo > 1e-6) {

            double mid = (lo + hi) / 2.0;

            if (possible(mid, stations, K))
                hi = mid;
            else
                lo = mid;
        }

        return lo;
    }

    public boolean possible(double D, int[] stations, int K) {

        int used = 0;

        for (int i = 0; i < stations.length - 1; ++i)
            used += (int)((stations[i+1] - stations[i]) / D);

        return used <= K;
    }
}
```

### Complexity

Time Complexity

```
O(N log W)
```

Where:

```
W = range / precision = 1e14
```

Space Complexity

```
O(1)
```
