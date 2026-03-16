# 818. Race Car — Exhaustive Solution Notes

## Overview

This problem asks for the shortest sequence of instructions to move a car from position `0` to a given target on an infinite number line.

The car begins at:

- position = `0`
- speed = `+1`

The allowed instructions are:

- `A` = accelerate
- `R` = reverse

The challenge is that:

- `A` doubles the speed each time
- `R` changes only the direction, not the position
- sometimes the optimal strategy is to **overshoot** the target and come back
- sometimes the optimal strategy is to **undershoot**, reverse, and adjust

This makes the problem a shortest-path / dynamic-programming problem rather than a simple greedy simulation.

This write-up explains:

1. The general **approach framework**
2. **Dijkstra’s Algorithm**
3. **Dynamic Programming**

Both accepted approaches run in:

```text
O(T log T)
```

where `T = target`.

---

## Problem Statement

Your car starts at:

```text
position = 0
speed = +1
```

The instructions are:

### Accelerate (`A`)

```text
position += speed
speed *= 2
```

### Reverse (`R`)

```text
if speed > 0: speed = -1
else: speed = 1
```

Position does not change during reverse.

Return the minimum number of instructions needed to reach exactly `target`.

---

## Example 1

**Input**

```text
target = 3
```

**Output**

```text
2
```

**Explanation**

The shortest sequence is:

```text
AA
```

Simulation:

- Start: position = 0, speed = 1
- `A` → position = 1, speed = 2
- `A` → position = 3, speed = 4

Reached target in 2 steps.

---

## Example 2

**Input**

```text
target = 6
```

**Output**

```text
5
```

**Explanation**

The shortest sequence is:

```text
AAARA
```

Simulation:

- Start: position = 0, speed = 1
- `A` → position = 1, speed = 2
- `A` → position = 3, speed = 4
- `A` → position = 7, speed = 8
- `R` → position = 7, speed = -1
- `A` → position = 6, speed = -2

Reached target in 5 steps.

---

## Constraints

- `1 <= target <= 10^4`

---

# Approach Framework

Before discussing the algorithms, it helps to understand a structural view of valid instruction sequences.

Let:

```text
A^k
```

mean applying the instruction `A` exactly `k` times in a row.

If we apply `A` repeatedly starting from speed `+1`, then after `k` accelerations:

- the car moves a total distance of:

```text
1 + 2 + 4 + ... + 2^(k-1) = 2^k - 1
```

So:

```text
A^k
```

moves the car by:

```text
2^k - 1
```

in its current direction.

That is one of the core observations of the problem.

---

## Why We Can Think in Segments

A general command sequence can be viewed as alternating runs of `A` separated by `R`.

For example:

```text
A^k1 R A^k2 R A^k3 ...
```

Each block of `A`s contributes a signed displacement:

```text
(2^k1 - 1) - (2^k2 - 1) + (2^k3 - 1) - ...
```

depending on direction changes caused by `R`.

So instead of thinking one instruction at a time, we can think in terms of choosing how many times to accelerate before each reverse.

This viewpoint explains why the problem is not just about simulating motion greedily: the optimal answer is really about composing these blocks efficiently.

---

## Important Distance Threshold

Suppose `a` is the smallest integer such that:

```text
2^a >= target
```

Then a useful fact is:

> We never need to consider driving “too far” beyond the target.

That is because every extra overshoot must eventually be canceled by future reverse-and-return steps.

So the search can be bounded to a region related to the target.

This motivates both accepted solutions.

---

# Approach 1: Dijkstra’s Algorithm

## Intuition

This problem can be interpreted as a shortest-path problem in a weighted graph.

Each node represents a “remaining target” or relative displacement state.

From a given state, we choose a run of `A`s of length `k`.

That moves us by:

```text
2^k - 1
```

and costs:

- `k` instructions if we hit the destination exactly
- `k + 1` if we also need an `R` afterward to continue

Since different choices of `k` produce different costs, the graph is **weighted**.

That makes Dijkstra’s algorithm a natural fit.

---

## State Interpretation

Instead of storing the car’s full `(position, speed)` state, this solution stores a transformed state:

```text
remaining target distance
```

Call it `targ`.

If we apply `A^k`, we move by:

```text
walk = 2^k - 1
```

So the new remaining target becomes:

```text
targ2 = walk - targ
```

This formula may look unusual at first, but it is a result of the transformed representation being centered around how far we still need to compensate after making a move.

The benefit is that it turns the problem into a graph over a bounded set of integer targets.

---

## Barrier

We define:

```text
K = smallest integer such that 2^K >= target
barrier = 2^K
```

Then all meaningful remaining-target states are within:

```text
[-barrier, barrier]
```

So the graph has only:

```text
O(T)
```

nodes.

That is what makes Dijkstra practical here.

---

## Why Dijkstra Works

At each state, we can choose any `k` from `0` to `K`.

That creates an edge with weight:

- `k` if it lands exactly
- `k + 1` if it must reverse afterward

Dijkstra repeatedly extracts the lowest-cost state not yet finalized.

Thus when it reaches the destination state, the cost is guaranteed to be minimal.

---

## Algorithm

1. Compute `K`, the smallest integer such that `2^K >= target`
2. Set `barrier = 2^K`
3. Create a distance array over all possible remaining-target states in `[-barrier, barrier]`
4. Start Dijkstra from the initial transformed state
5. For each state, try all possible `k` from `0` to `K`
6. Compute:
   - `walk = 2^k - 1`
   - `targ2 = walk - targ1`
   - cost = current steps + `k` or `k + 1`
7. Relax edges as in standard Dijkstra
8. Return the distance for remaining target `0`

---

## Java Implementation — Dijkstra

```java
class Solution {
    public int racecar(int target) {
        int K = 33 - Integer.numberOfLeadingZeros(target - 1);
        int barrier = 1 << K;
        int[] dist = new int[2 * barrier + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[target] = 0;

        PriorityQueue<Node> pq = new PriorityQueue<Node>(
            (a, b) -> a.steps - b.steps);
        pq.offer(new Node(0, target));

        while (!pq.isEmpty()) {
            Node node = pq.poll();
            int steps = node.steps, targ1 = node.target;
            if (dist[Math.floorMod(targ1, dist.length)] > steps) continue;

            for (int k = 0; k <= K; ++k) {
                int walk = (1 << k) - 1;
                int targ2 = walk - targ1;
                int steps2 = steps + k + (targ2 != 0 ? 1 : 0);

                if (Math.abs(targ2) <= barrier &&
                    steps2 < dist[Math.floorMod(targ2, dist.length)]) {
                    pq.offer(new Node(steps2, targ2));
                    dist[Math.floorMod(targ2, dist.length)] = steps2;
                }
            }
        }

        return dist[0];
    }
}

class Node {
    int steps, target;
    Node(int s, int t) {
        steps = s;
        target = t;
    }
}
```

---

## Complexity Analysis — Dijkstra

Let `T = target`.

### Time Complexity

There are `O(T)` meaningful states.

Each state is processed with up to `O(log T)` priority-queue work and tries up to `O(log T)` different acceleration lengths, but the editorial simplifies the overall bound to:

```text
O(T log T)
```

This is the accepted complexity.

### Space Complexity

The distance array and priority queue use:

```text
O(T)
```

space.

---

# Approach 2: Dynamic Programming

## Intuition

This is the most commonly taught solution.

We define:

```text
dp[t] = minimum instructions needed to reach position t
```

for every `t` from `0` to `target`.

Now consider some target `t`.

Let `k` be the smallest integer such that:

```text
2^(k-1) <= t < 2^k
```

That means:

```text
2^k - 1
```

is the first full-acceleration position that is at least as large as `t`.

Then there are two main strategies.

---

## Strategy 1: Reach Exactly With Only A’s

If:

```text
t = 2^k - 1
```

then the answer is simply:

```text
k
```

because:

```text
A^k
```

lands exactly on `t`.

This is obviously optimal because every instruction is an `A`, and no reverse is needed.

---

## Strategy 2: Undershoot, Reverse, Adjust

Suppose we do only `k - 1` accelerations first.

Then we reach:

```text
2^(k-1) - 1
```

This is less than `t`.

Now reverse, move backward some smaller distance, reverse again, and then solve the remaining smaller subproblem.

If the backward acceleration block uses `j` accelerations, then we move backward by:

```text
2^j - 1
```

So after:

```text
A^(k-1) R A^j R
```

the net position becomes:

```text
(2^(k-1) - 1) - (2^j - 1)
```

The remaining distance is:

```text
t - (2^(k-1) - 1) + (2^j - 1)
```

which matches the recurrence in the code:

```text
dp[t - (1 << (k-1)) + (1 << j)]
```

The cost of this strategy is:

- `k - 1` for the first block of `A`s
- `1` for reverse
- `j` for the backward block of `A`s
- `1` for reverse again

Total:

```text
k - 1 + j + 2
```

plus the remaining DP cost.

---

## Strategy 3: Overshoot, Reverse, Adjust

Another option is to go too far first.

Do:

```text
A^k
```

This reaches:

```text
2^k - 1
```

which is greater than `t`.

Then reverse and solve the remaining overshoot:

```text
(2^k - 1) - t
```

The cost is:

- `k` for the accelerations
- `1` for the reverse
- plus `dp[(2^k - 1) - t]`

So:

```text
dp[t] = min(dp[t], dp[(2^k - 1) - t] + k + 1)
```

This is only useful if the overshoot remainder is smaller than `t`.

---

## Recurrence Summary

For each target `t`:

### Exact hit

If:

```text
t == 2^k - 1
```

then:

```text
dp[t] = k
```

### Otherwise

Try all undershoot strategies:

```text
dp[t] = min(dp[t], dp[t - 2^(k-1) + 2^j] + (k - 1) + j + 2)
```

for all:

```text
0 <= j < k - 1
```

Also try overshoot:

```text
dp[t] = min(dp[t], dp[(2^k - 1) - t] + k + 1)
```

when useful.

---

## Why DP Works

Every nontrivial target `t` is reduced to a smaller target.

So if we fill `dp` from small to large, all referenced subproblems are already known.

This yields a bottom-up dynamic programming solution.

---

## Java Implementation — Dynamic Programming

```java
class Solution {
    public int racecar(int target) {
        int[] dp = new int[target + 3];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        dp[1] = 1;
        dp[2] = 4;

        for (int t = 3; t <= target; ++t) {
            int k = 32 - Integer.numberOfLeadingZeros(t);

            if (t == (1 << k) - 1) {
                dp[t] = k;
                continue;
            }

            for (int j = 0; j < k - 1; ++j) {
                dp[t] = Math.min(
                    dp[t],
                    dp[t - (1 << (k - 1)) + (1 << j)] + k - 1 + j + 2
                );
            }

            if ((1 << k) - 1 - t < t) {
                dp[t] = Math.min(
                    dp[t],
                    dp[(1 << k) - 1 - t] + k + 1
                );
            }
        }

        return dp[target];
    }
}
```

---

## Complexity Analysis — Dynamic Programming

Let `T = target`.

### Time Complexity

For each `t` from `1` to `T`, we try up to `O(log t)` possibilities for `j`.

So total time complexity is:

```text
O(T log T)
```

### Space Complexity

The DP array stores `T + 1` values:

```text
O(T)
```

---

# Comparing the Two Accepted Approaches

## Dijkstra

### Strengths

- elegant shortest-path interpretation
- naturally handles weighted transitions
- conceptually appealing if you see the problem as graph search

### Weaknesses

- harder to derive from scratch
- state transformation is less intuitive
- implementation is more complex

---

## Dynamic Programming

### Strengths

- more standard editorial solution
- easier to reason about using overshoot/undershoot cases
- very clean bottom-up implementation
- usually preferred in interviews once derived

### Weaknesses

- the recurrence is not obvious initially
- requires careful understanding of the motion structure

---

# Common Mistakes

## 1. Assuming you should never overshoot

Overshooting is often optimal.

Example:

```text
target = 6
```

Optimal sequence:

```text
AAARA
```

which overshoots to `7` first, then comes back.

---

## 2. Forgetting that `A^k` moves `2^k - 1`

This is the main geometric fact behind the problem.

If you do not recognize it, the recurrence is hard to derive.

---

## 3. Miscomputing `k`

For a target `t`, `k` should be chosen so that:

```text
2^(k-1) <= t < 2^k
```

which means `2^k - 1` is the first all-accelerate position not smaller than `t`.

---

## 4. Ignoring the undershoot strategy

It is not enough to only overshoot and come back.

Sometimes the best path accelerates almost to the target, reverses briefly, then accelerates forward again.

---

# Final Summary

## Main Insight

After `k` accelerations, the car reaches:

```text
2^k - 1
```

So every target can be analyzed in relation to the nearest such positions.

---

## Two Core Strategies

For each target `t`:

1. **overshoot** to `2^k - 1`, reverse, and solve the smaller remainder
2. **undershoot** to `2^(k-1) - 1`, reverse backward a bit, reverse again, and solve the remaining distance

Dynamic programming combines these possibilities efficiently.

---

## Best Complexities

### Dijkstra

- Time: `O(T log T)`
- Space: `O(T)`

### Dynamic Programming

- Time: `O(T log T)`
- Space: `O(T)`

---

# Best Final Java Solution

The dynamic programming solution is usually the clearest accepted approach.

```java
class Solution {
    public int racecar(int target) {
        int[] dp = new int[target + 3];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;
        dp[1] = 1;
        dp[2] = 4;

        for (int t = 3; t <= target; ++t) {
            int k = 32 - Integer.numberOfLeadingZeros(t);

            if (t == (1 << k) - 1) {
                dp[t] = k;
                continue;
            }

            for (int j = 0; j < k - 1; ++j) {
                dp[t] = Math.min(
                    dp[t],
                    dp[t - (1 << (k - 1)) + (1 << j)] + k - 1 + j + 2
                );
            }

            if ((1 << k) - 1 - t < t) {
                dp[t] = Math.min(
                    dp[t],
                    dp[(1 << k) - 1 - t] + k + 1
                );
            }
        }

        return dp[target];
    }
}
```

This is the standard accepted dynamic programming solution for **Race Car**.
