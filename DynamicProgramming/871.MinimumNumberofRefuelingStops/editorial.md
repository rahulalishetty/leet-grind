# 871. Minimum Number of Refueling Stops

## Overview

We need to reach `target` miles starting with `startFuel` liters of fuel.

Each station is given as:

```text
stations[i] = [position_i, fuel_i]
```

That means:

- `position_i` is how far the station is from the start
- `fuel_i` is how much fuel that station provides
- if we stop there, we must take **all** the fuel from that station

The goal is to return the **minimum number of refueling stops** needed to reach the target.

If it is impossible, return `-1`.

---

# Approach 1: Dynamic Programming

## Core Idea

Define:

```text
dp[i] = the farthest distance we can reach using exactly i refueling stops
```

This is a very natural state definition because the question asks for the **minimum number of stops**. So instead of directly asking:

> “Can I reach the target?”

we ask:

> “How far can I get with 0 stops, 1 stop, 2 stops, ...?”

Then the answer is simply the **smallest `i`** such that:

```text
dp[i] >= target
```

---

## Intuition

Initially, before using any station:

- with `0` refueling stops, the farthest we can go is `startFuel`

So:

```text
dp[0] = startFuel
```

Now suppose we are processing a station:

```text
stations[i] = [location, fuel]
```

If we already know that with `t` stops we can reach at least `location`, then this means:

- we are capable of arriving at this station using `t` refuels
- if we choose to refuel here, then we will have used `t + 1` refuels
- and our new farthest reachable distance becomes:

```text
dp[t] + fuel
```

So we can update:

```text
dp[t + 1] = max(dp[t + 1], dp[t] + fuel)
```

The `max` is needed because there may be multiple ways to use `t + 1` stops, and we want the one that gets us the farthest.

---

## Why Iterate `t` Backwards?

When processing one station, we must avoid using that same station multiple times in the same iteration.

So for station `i`, we iterate `t` from `i` down to `0`.

This ensures that:

- `dp[t]` still refers to states from **before** using the current station
- we do not accidentally chain updates from the same station multiple times

This is the same pattern used in many 0/1 knapsack-style DP problems.

---

## Small Example

Let:

```text
target = 100
startFuel = 10
stations = [[10,60],[20,30],[30,30],[60,40]]
```

Start:

```text
dp = [10, 0, 0, 0, 0]
```

Meaning:

- with 0 stops, farthest reach = 10

### Process station [10, 60]

Since `dp[0] = 10`, we can reach position `10`.

So:

```text
dp[1] = max(dp[1], dp[0] + 60) = 70
```

Now:

```text
dp = [10, 70, 0, 0, 0]
```

### Process station [20, 30]

Check from back to front:

- `dp[1] = 70`, so with 1 stop we can reach station at 20
  therefore:

```text
dp[2] = max(dp[2], 70 + 30) = 100
```

- `dp[0] = 10`, cannot reach 20, so no update from there

Now:

```text
dp = [10, 70, 100, 0, 0]
```

### Process station [30, 30]

- `dp[2] = 100`, can reach 30
  so:

```text
dp[3] = max(dp[3], 100 + 30) = 130
```

- `dp[1] = 70`, can also reach 30
  so:

```text
dp[2] = max(dp[2], 70 + 30) = 100
```

Now:

```text
dp = [10, 70, 100, 130, 0]
```

### Process station [60, 40]

- `dp[3] = 130`, can reach 60
  so:

```text
dp[4] = max(dp[4], 130 + 40) = 170
```

- `dp[2] = 100`, can also reach 60
  so:

```text
dp[3] = max(dp[3], 100 + 40) = 140
```

- `dp[1] = 70`, can also reach 60
  so:

```text
dp[2] = max(dp[2], 70 + 40) = 110
```

Final:

```text
dp = [10, 70, 110, 140, 170]
```

Now find the smallest index `i` where `dp[i] >= 100`:

- `dp[0] = 10`
- `dp[1] = 70`
- `dp[2] = 110`

So the answer is:

```text
2
```

---

## DP Transition

For every station `[location, fuel]`:

```text
if dp[t] >= location:
    dp[t + 1] = max(dp[t + 1], dp[t] + fuel)
```

---

## Java Code

```java
class Solution {
    public int minRefuelStops(int target, int startFuel, int[][] stations) {
        int N = stations.length;
        long[] dp = new long[N + 1];
        dp[0] = startFuel;

        for (int i = 0; i < N; ++i) {
            for (int t = i; t >= 0; --t) {
                if (dp[t] >= stations[i][0]) {
                    dp[t + 1] = Math.max(dp[t + 1], dp[t] + (long) stations[i][1]);
                }
            }
        }

        for (int i = 0; i <= N; ++i) {
            if (dp[i] >= target) return i;
        }

        return -1;
    }
}
```

---

## DP Code Walkthrough

### 1. Create the DP array

```java
long[] dp = new long[N + 1];
dp[0] = startFuel;
```

- `dp[i]` stores the farthest reachable distance using exactly `i` stops
- we use `long` because the reachable distance can become large

### 2. Process each station

```java
for (int i = 0; i < N; ++i) {
```

We consider stations from left to right.

### 3. Update backwards

```java
for (int t = i; t >= 0; --t) {
```

We go backward to prevent reusing the same station more than once.

### 4. Check if station is reachable

```java
if (dp[t] >= stations[i][0]) {
```

If we can already reach this station with `t` stops, then it is valid to refuel here.

### 5. Transition to `t + 1` stops

```java
dp[t + 1] = Math.max(dp[t + 1], dp[t] + (long) stations[i][1]);
```

Refueling here increases the farthest reachable distance.

### 6. Find the smallest valid answer

```java
for (int i = 0; i <= N; ++i) {
    if (dp[i] >= target) return i;
}
```

Return the minimum stops needed to reach the target.

---

## Complexity Analysis for DP

### Time Complexity

```text
O(N^2)
```

Why?

- there are `N` stations
- for each station, we may scan up to `N` DP states

So total work is quadratic.

### Space Complexity

```text
O(N)
```

We only use a 1D DP array of size `N + 1`.

---

## DP Strengths and Weaknesses

### Strengths

- very systematic and easy to reason about
- directly models the question “how far can we reach with k stops?”
- clean transition

### Weaknesses

- `O(N^2)` is slower than the heap-based greedy solution
- less optimal for larger inputs, though still acceptable for `N <= 500`

---

# Approach 2: Heap (Greedy + Priority Queue)

## Core Idea

As we move forward, every station we pass becomes a **possible refueling choice**.

But we do **not** need to decide immediately whether to refuel there.

Instead, we delay the decision.

When we eventually discover that we do not have enough fuel to continue, we retroactively choose to refuel from the **largest fuel station among all stations we have already passed**.

That is why we use a **max-heap**.

---

## Intuition

Imagine driving from left to right.

At each station:

1. spend fuel to reach that station
2. add that station's fuel amount into a max-heap
3. if fuel becomes negative before reaching the current station, then:
   - we must have refueled somewhere in the past
   - to minimize the number of stops, we should choose the station with the **largest available fuel**
   - so we repeatedly pop the largest fuel value from the heap until fuel becomes non-negative

This works because if we are forced to refuel, choosing the largest past station gives the maximum benefit per stop.

That greedy choice minimizes the total number of refuels.

---

## Why the Greedy Choice Is Correct

Suppose we need extra fuel to continue.

Among all the stations we have already passed, choosing the largest fuel station is always at least as good as choosing any smaller one.

Why?

Because each refueling stop counts equally as **one stop**.

So when trying to minimize the number of stops, each stop should give us as much fuel as possible.

This is exactly what the max-heap gives us.

---

## Step-by-Step Example

Again use:

```text
target = 100
startFuel = 10
stations = [[10,60],[20,30],[30,30],[60,40]]
```

We maintain:

- `tank` = current fuel
- `prev` = previous location
- `pq` = max-heap of fuels from stations we have passed
- `ans` = number of refueling stops used

Initial state:

```text
tank = 10
prev = 0
pq = []
ans = 0
```

---

### Go to station at 10 with fuel 60

Distance traveled:

```text
10 - 0 = 10
```

Fuel after driving:

```text
tank = 10 - 10 = 0
```

Fuel is not negative, so we are fine.

Add this station’s fuel to heap:

```text
pq = [60]
prev = 10
```

---

### Go to station at 20 with fuel 30

Distance traveled:

```text
20 - 10 = 10
```

Fuel after driving:

```text
tank = 0 - 10 = -10
```

Now fuel is negative, so we must retroactively refuel.

Pop the largest fuel from heap:

- take `60`

Now:

```text
tank = -10 + 60 = 50
ans = 1
pq = []
```

Then add current station fuel:

```text
pq = [30]
prev = 20
```

---

### Go to station at 30 with fuel 30

Distance traveled:

```text
30 - 20 = 10
```

Fuel after driving:

```text
tank = 50 - 10 = 40
```

Add current station fuel:

```text
pq = [30, 30]
prev = 30
```

---

### Go to station at 60 with fuel 40

Distance traveled:

```text
60 - 30 = 30
```

Fuel after driving:

```text
tank = 40 - 30 = 10
```

Add current station fuel:

```text
pq = [40, 30, 30]
prev = 60
```

---

### Go to target 100

Distance traveled:

```text
100 - 60 = 40
```

Fuel after driving:

```text
tank = 10 - 40 = -30
```

Need to refuel retroactively.

Pop largest from heap:

- take `40`

Now:

```text
tank = -30 + 40 = 10
ans = 2
```

We can now reach the target.

Final answer:

```text
2
```

---

## Important Insight

The algorithm does **not** literally stop at a station when passing it.

Instead, it says:

- “I have passed this station, so I may choose it later if needed.”

When fuel becomes insufficient, we pretend we had chosen the best previous station(s).

This is a standard greedy trick.

---

## Java Code

```java
class Solution {
    public int minRefuelStops(int target, int tank, int[][] stations) {
        // pq is a max-heap of gas station capacities
        PriorityQueue<Integer> pq = new PriorityQueue(Collections.reverseOrder());
        int ans = 0, prev = 0;

        for (int[] station : stations) {
            int location = station[0];
            int capacity = station[1];

            tank -= location - prev;

            while (!pq.isEmpty() && tank < 0) {  // must refuel in past
                tank += pq.poll();
                ans++;
            }

            if (tank < 0) return -1;

            pq.offer(capacity);
            prev = location;
        }

        // Treat target like a final station with no fuel
        tank -= target - prev;
        while (!pq.isEmpty() && tank < 0) {
            tank += pq.poll();
            ans++;
        }

        if (tank < 0) return -1;

        return ans;
    }
}
```

---

## Heap Code Walkthrough

### 1. Max-heap setup

```java
PriorityQueue<Integer> pq = new PriorityQueue(Collections.reverseOrder());
```

Java’s default priority queue is a min-heap, so we reverse the order to simulate a max-heap.

This lets us always pull the station with the most fuel first.

### 2. Track current fuel and previous location

```java
int ans = 0, prev = 0;
```

- `ans` counts the number of refuels
- `prev` stores the last location we were at

### 3. Move to each station

```java
tank -= location - prev;
```

We spend fuel equal to the distance traveled.

### 4. Refuel only when necessary

```java
while (!pq.isEmpty() && tank < 0) {
    tank += pq.poll();
    ans++;
}
```

If we do not have enough fuel, then we must refuel from some station we already passed.

We always choose the largest available one.

### 5. If still negative, impossible

```java
if (tank < 0) return -1;
```

This means:

- we needed more fuel
- but there were no previous stations left to use

So the target cannot be reached.

### 6. Add current station for future use

```java
pq.offer(capacity);
```

We do not decide whether to use it now. We simply store it as an option.

### 7. Handle the final stretch to target

After processing all stations, we still need to travel from the last station to the target.

So we repeat the same logic one final time.

---

## Complexity Analysis for Heap

### Time Complexity

```text
O(N log N)
```

Why?

- each station is inserted into the heap once
- each station can be removed from the heap at most once
- each heap operation costs `O(log N)`

### Space Complexity

```text
O(N)
```

The heap may store up to all previously passed stations.

---

## Heap Strengths and Weaknesses

### Strengths

- more efficient than the DP solution
- elegant greedy logic
- excellent for this “minimum number of choices with best prior options” type of problem

### Weaknesses

- less immediately intuitive than DP
- the “retroactive refueling” idea may feel unusual at first

---

# Comparing the Two Approaches

## Dynamic Programming

Use DP when you want a very explicit state definition:

```text
dp[i] = farthest reachable distance using i stops
```

This is mathematically clean and great for understanding the structure of the problem.

## Heap / Greedy

Use the heap approach when you want the best performance.

The central greedy insight is:

> only refuel when forced to, and when forced, choose the biggest fuel amount among stations already passed.

This gives the optimal answer in:

```text
O(N log N)
```

which is better than:

```text
O(N^2)
```

---

# Which Approach Is Better?

For interviews and practical coding:

- the **heap approach** is usually considered the better solution
- it is faster and is the standard optimal solution

For learning and deep understanding:

- the **DP approach** is excellent because it makes the state transition very explicit

A strong way to understand the problem is:

1. first understand the DP formulation
2. then understand why the greedy heap version improves efficiency

---

# Final Takeaway

This problem has two very instructive solutions:

## DP Perspective

Think in terms of:

```text
“How far can I reach with exactly k refuels?”
```

That leads to:

```text
dp[k]
```

and a straightforward transition.

## Greedy Perspective

Think in terms of:

```text
“I will postpone refueling decisions until I truly need them.”
```

Then:

- remember every station passed
- when fuel runs short, take the largest available station first

That naturally leads to a max-heap.

---

# Summary

- `dp[i]` = farthest reachable distance using exactly `i` stops
- DP transition:
  ```text
  if dp[t] >= location:
      dp[t+1] = max(dp[t+1], dp[t] + fuel)
  ```
- DP complexity:
  - Time: `O(N^2)`
  - Space: `O(N)`

- Heap idea:
  - store passed stations in a max-heap
  - only refuel when necessary
  - always choose the largest available previous station first

- Heap complexity:
  - Time: `O(N log N)`
  - Space: `O(N)`

- Best practical solution: **Heap**
- Best for structured understanding: **DP**
