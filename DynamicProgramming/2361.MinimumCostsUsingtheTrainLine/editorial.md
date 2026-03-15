# Minimum Cost to Reach Each Stop — Exhaustive Dynamic Programming Summary

## Overview

There are `N + 1` stops, and we are initially at stop `0`.

There are two routes that go through these stops:

- **regular**
- **express**

We are given two **1-indexed** integer arrays, `regular` and `express`, both of length `N`.

- `regular[i]` describes the cost to go from stop `i - 1` to stop `i` using the **regular** route.
- `express[i]` describes the cost to go from stop `i - 1` to stop `i` using the **express** route.

We are also given `expressCost`, which is the price of switching from the **regular** route to the **express** route.

Important rules:

- Switching **regular → express** costs `expressCost`
- Switching **express → regular** costs `0`
- Staying on the express route has no extra cost beyond the segment cost
- We must return a **1-indexed** array `costs` of length `N`, where `costs[i]` is the minimum cost to reach stop `i` from stop `0`

---

## Why Dynamic Programming Fits

Two structural hints strongly suggest dynamic programming:

### 1. Each decision depends on previous decisions

At each stop, we must choose whether the next segment should be taken using:

- the regular route
- the express route

But this decision depends on the route we are currently on, because:

- using express while currently on regular may require paying `expressCost`
- switching back from express to regular is free

So the cost of the current move depends on the previous state.

### 2. We are asked for a minimum cost

Whenever a problem asks for the minimum or maximum value, and each choice depends on earlier choices, dynamic programming is usually a strong candidate.

---

# Approach 1: Top-Down Dynamic Programming

## Intuition

At each stop `i`, we have two options:

- take the **regular** route
- take the **express** route

### If we take the regular route

We spend:

```text
regular[i]
```

### If we take the express route

We spend:

```text
express[i]
```

But if we are currently on the regular route, then we must also pay:

```text
expressCost
```

So the express option depends on the lane we are currently in.

After making either choice, we recursively solve the smaller problem for the previous stop.

---

## What state do we need?

To describe a subproblem completely, we need:

- the stop index `i`
- the lane we are currently in

We encode lane as:

- `1` = regular
- `0` = express

So the state is:

```text
(i, lane)
```

and:

```text
dp[i][lane]
```

stores the minimum cost associated with that state.

---

## Important observation

Because switching from **express → regular** is free, it can never be more expensive to reach a stop in the regular lane than in the express lane and then switch to regular for free.

So for the final answer at each stop, we can use the value corresponding to the regular lane.

---

## Why naive recursion is too slow

Without memoization, every stop branches into two choices:

- regular
- express

So the recursion tree can grow exponentially, roughly like:

```text
2^N
```

That is far too large for `N` up to `10^5`.

However, many subproblems repeat.
If we cache the answer for each `(i, lane)`, we avoid recomputation.

That converts the solution into linear time.

---

## Algorithm

1. Create a 2D array `dp` of size `N x 2`
2. Initialize all values to `-1`
3. Define a recursive function `solve(i, lane)`
4. Base case:
   - if `i < 0`, return `0`
5. If `dp[i][lane]` is already computed, return it
6. Compute:
   - cost of using regular lane
   - cost of using express lane
7. Store and return the minimum
8. After solving all states, use `dp[i][1]` to build the answer array

---

## Code — Top-Down DP

```java
class Solution {
    long solve(int i, int lane, long[][] dp, int[] regular, int[] express, int expressCost) {
        // If all stops are covered, return 0.
        if (i < 0) {
            return 0;
        }

        if (dp[i][lane] != -1) {
            return dp[i][lane];
        }

        // Use the regular lane; no extra cost to switch lanes if required.
        long regularLane = regular[i] + solve(i - 1, 1, dp, regular, express, expressCost);

        // Use express lane; add expressCost if the previously regular lane was used.
        long expressLane = (lane == 1 ? expressCost : 0) + express[i]
                                                    + solve(i - 1, 0, dp, regular, express, expressCost);

        return dp[i][lane] = Math.min(regularLane, expressLane);
    }

    public long[] minimumCosts(int[] regular, int[] express, int expressCost) {
        long[][] dp = new long[regular.length][2];
        for (int i = 0; i < regular.length; i++) {
            Arrays.fill(dp[i], -1);
        }

        solve(regular.length - 1, 1, dp, regular, express, expressCost);

        long[] ans = new long[regular.length];
        // Store cost for each stop.
        for (int i = 0 ; i < regular.length; i++) {
            ans[i] = dp[i][1];
        }

        return ans;
    }
}
```

---

## Complexity Analysis — Top-Down DP

Let `N` be the number of stops.

### Time Complexity: `O(N)`

There are only `2 * N` distinct states:

- each stop
- each of the 2 lanes

Each state is computed once, and each computation does only constant work.

So total time is:

```text
O(N)
```

### Space Complexity: `O(N)`

We use:

- `dp` table of size `2 * N`
- recursion stack of depth up to `N`

So total auxiliary space is:

```text
O(N)
```

The output array is usually not counted in auxiliary space.

---

# Approach 2: Bottom-Up Dynamic Programming

## Intuition

The top-down solution works, but recursion adds stack overhead.

We can compute the same states iteratively.

Instead of solving from the last stop backward, we build the answer from stop `0` up to stop `N`.

For each stop `i`, we track the minimum cost to reach it by:

- regular lane
- express lane

---

## DP Definition

Let:

- `dp[i][1]` = minimum cost to reach stop `i` and end on the **regular** lane
- `dp[i][0]` = minimum cost to reach stop `i` and end on the **express** lane

### Base case

At stop `0`:

- we start on regular, so:

```text
dp[0][1] = 0
```

- to be on express at stop `0`, we would need to switch from regular, so:

```text
dp[0][0] = expressCost
```

This representation is convenient for the iterative transitions.

---

## Transition

For each stop `i` from `1` to `N`:

### Reach stop `i` via regular

We can come from:

- previous regular lane
- previous express lane, then switch to regular for free

So:

```text
dp[i][1] = regular[i - 1] + min(dp[i - 1][1], dp[i - 1][0])
```

### Reach stop `i` via express

We can come from:

- previous express lane directly
- previous regular lane, but then must pay `expressCost`

So:

```text
dp[i][0] = express[i - 1] + min(expressCost + dp[i - 1][1], dp[i - 1][0])
```

### Final answer for stop `i`

Since the stop can be reached by either lane:

```text
answer[i - 1] = min(dp[i][0], dp[i][1])
```

---

## Algorithm

1. Create a 2D array `dp` of size `(N + 1) x 2`
2. Set:
   - `dp[0][1] = 0`
   - `dp[0][0] = expressCost`
3. Iterate from `i = 1` to `N`
4. Compute:
   - `dp[i][1]`
   - `dp[i][0]`
5. Store `min(dp[i][0], dp[i][1])` in the answer array
6. Return the answer array

---

## Code — Bottom-Up DP

```java
class Solution {
    public long[] minimumCosts(int[] regular, int[] express, int expressCost) {
        long[] ans = new long[regular.length];

        long[][] dp = new long[regular.length + 1][2];
        dp[0][1] = 0;
        // Need to spend expressCost, as we start from the regular lane initially.
        dp[0][0] = expressCost;

        for (int i = 1; i < regular.length + 1; i++) {
            // Use the regular lane; no extra cost to switch to the express lane.
            dp[i][1] = regular[i - 1] + Math.min(dp[i - 1][1], dp[i - 1][0]);

            // Use express lane; add extra cost if the previously regular lane was used.
            dp[i][0] = express[i - 1] + Math.min(expressCost + dp[i - 1][1], dp[i - 1][0]);

            ans[i - 1] = Math.min(dp[i][0], dp[i][1]);
        }
        return ans;
    }
}
```

---

## Complexity Analysis — Bottom-Up DP

### Time Complexity: `O(N)`

We process each stop exactly once, and each iteration does only constant work.

So total time is:

```text
O(N)
```

### Space Complexity: `O(N)`

We store a DP table of size approximately `2 * N`.

So total auxiliary space is:

```text
O(N)
```

Again, output array space is not usually counted.

---

# Approach 3: Space-Optimized Bottom-Up Dynamic Programming

## Intuition

In the bottom-up solution, notice something important:

To compute state `dp[i]`, we only need:

```text
dp[i - 1]
```

We do not need the full DP table.

That means we can compress the state into just two variables:

- previous best cost on regular lane
- previous best cost on express lane

This preserves the same logic while reducing space.

---

## State Compression

Let:

- `prevRegularLane` = minimum cost to reach previous stop on regular
- `prevExpressLane` = minimum cost to reach previous stop on express

Initially:

```text
prevRegularLane = 0
prevExpressLane = expressCost
```

For each stop `i`:

```text
regularLaneCost = regular[i - 1] + min(prevRegularLane, prevExpressLane)
expressLaneCost = express[i - 1] + min(expressCost + prevRegularLane, prevExpressLane)
```

Then:

```text
answer[i - 1] = min(regularLaneCost, expressLaneCost)
```

Finally update:

```text
prevRegularLane = regularLaneCost
prevExpressLane = expressLaneCost
```

---

## Algorithm

1. Initialize:
   - `prevRegularLane = 0`
   - `prevExpressLane = expressCost`
2. Create answer array
3. For each stop from `1` to `N`:
   - compute cost to reach current stop by regular
   - compute cost to reach current stop by express
   - store the minimum in answer
   - update previous lane costs
4. Return answer

---

## Code — Space-Optimized Bottom-Up DP

```java
class Solution {
    public long[] minimumCosts(int[] regular, int[] express, int expressCost) {

        long prevRegularLane = 0;
        // Need to spend expressCost, as we start from the regular lane initially.
        long prevExpressLane = expressCost;

        long[] ans = new long[regular.length];
        for (int i = 1; i < regular.length + 1; i++) {
            // Use the regular lane; no extra cost to switch to the express lane.
            long regularLaneCost = regular[i - 1] + Math.min(prevRegularLane, prevExpressLane);

            // Use express lane; add extra cost if the previously regular lane was used.
            long expressLaneCost = express[i - 1] + Math.min(expressCost + prevRegularLane, prevExpressLane);

            ans[i - 1] = Math.min(regularLaneCost, expressLaneCost);

            prevRegularLane = regularLaneCost;
            prevExpressLane = expressLaneCost;
        }

        return ans;
    }
}
```

---

## Complexity Analysis — Space-Optimized DP

### Time Complexity: `O(N)`

We iterate through all stops once.

So:

```text
O(N)
```

### Space Complexity: `O(1)`

Ignoring the answer array, we use only two variables for DP state.

So:

```text
O(1)
```

---

# Deeper Intuition Behind the Transitions

The most important part of this problem is the asymmetry of switching cost:

- **regular → express** costs money
- **express → regular** is free

That asymmetry is exactly why we must track both route states separately.

If switching both ways were free, the problem would be much simpler because at every stop we would just choose the cheaper segment.

But here, entering express is a special decision with a penalty, so we must remember whether we are currently on regular or express.

That is what the DP state captures.

---

# Why the Answer at Each Stop Is the Minimum of Both Lanes

The problem asks for the minimum cost to reach stop `i`.

It does **not** require us to end on a specific lane.

So after computing:

- cost to reach stop `i` on regular
- cost to reach stop `i` on express

the answer is simply:

```text
min(regular, express)
```

That is why every approach stores:

```text
Math.min(...)
```

for the current stop.

---

# Common Pitfalls

## 1. Forgetting the initial condition

We start on **regular**, not express.

So the initial state must reflect that.

---

## 2. Mixing up the free direction of switching

Only:

```text
express → regular
```

is free.

Not the other way around.

---

## 3. Using `int` instead of `long`

Since costs accumulate over many stops, `int` may overflow.

Use `long`.

---

## 4. Thinking greedy is enough

A greedy approach like “choose the cheaper route at each step” does not work reliably, because using express may require paying `expressCost`.

So a locally cheaper express segment may still be globally worse.

---

# Final Takeaway

All three approaches are based on the same dynamic programming idea:

- track the minimum cost to reach each stop on each lane
- use the switching rules to transition to the next stop

The differences are only in implementation style:

### Approach 1

- recursive
- memoized
- easy to think about
- uses recursion stack

### Approach 2

- iterative
- avoids recursion
- uses full DP table

### Approach 3

- iterative
- same logic as Approach 2
- compresses state to constant space

If asked which one is best in practice, the space-optimized bottom-up approach is usually the cleanest and most efficient.

---

# Summary Table

| Approach                  | Idea                       |   Time | Extra Space |
| ------------------------- | -------------------------- | -----: | ----------: |
| Top-Down DP               | recursion + memoization    | `O(N)` |      `O(N)` |
| Bottom-Up DP              | iterative DP table         | `O(N)` |      `O(N)` |
| Space-Optimized Bottom-Up | iterative with 2 variables | `O(N)` |      `O(1)` |

---

# Full Preferred Solution in Practice

```java
class Solution {
    public long[] minimumCosts(int[] regular, int[] express, int expressCost) {

        long prevRegularLane = 0;
        long prevExpressLane = expressCost;

        long[] ans = new long[regular.length];

        for (int i = 1; i <= regular.length; i++) {
            long regularLaneCost = regular[i - 1] + Math.min(prevRegularLane, prevExpressLane);
            long expressLaneCost = express[i - 1] + Math.min(expressCost + prevRegularLane, prevExpressLane);

            ans[i - 1] = Math.min(regularLaneCost, expressLaneCost);

            prevRegularLane = regularLaneCost;
            prevExpressLane = expressLaneCost;
        }

        return ans;
    }
}
```

This is the same DP idea distilled to its cleanest form.
