# Mario Two-Lane Freeway — Detailed Solution Summary

## Problem Restatement

Mario drives along a freeway with exactly two lanes:

- `lane1[i]` = coins gained or lost if he travels mile `i` in lane 1
- `lane2[i]` = coins gained or lost if he travels mile `i` in lane 2

Rules:

- He may **enter at any mile**.
- He may **exit at any later point**, but must travel **at least one mile**.
- He always **enters on lane 1**.
- He may switch lanes **at most 2 times**.
- A switch may happen:
  - **immediately upon entering**, or
  - **just before exiting**

We need the **maximum total coins** Mario can earn.

---

## Core Insight

This is not just a plain maximum subarray problem.

In a normal maximum subarray problem, for each position, we ask:

> What is the best subarray ending here?

Here, that is not enough, because Mario’s score also depends on:

1. **Which lane he is currently in**
2. **How many lane switches he has already used**

So the problem becomes a constrained version of Kadane’s algorithm.

---

## Structural Intuition

Because Mario starts on lane 1 and can switch at most 2 times, the lane pattern of any valid trip must look like one of these:

### 1. No switch

`lane1`

### 2. One switch

- `lane1 → lane2`
- or, if he switches immediately on entry, effectively `lane2`

### 3. Two switches

- `lane1 → lane2 → lane1`
- or if he switches immediately on entry, then later once more, effectively `lane2 → lane1`

So every valid trip is a contiguous interval of miles, plus a lane choice pattern with at most 2 changes.

That strongly suggests dynamic programming over:

- current mile
- current lane
- number of switches used

---

## DP State Design

Let:

- `dp1[s]` = maximum coins for a valid trip that **ends at the current mile in lane 1**, having used exactly `s` switches
- `dp2[s]` = maximum coins for a valid trip that **ends at the current mile in lane 2**, having used exactly `s` switches

Where:

- `s ∈ {0, 1, 2}`

So we maintain only 6 states total:

- `dp1[0], dp1[1], dp1[2]`
- `dp2[0], dp2[1], dp2[2]`

Each state represents:

> Among all valid ways to choose a starting point and drive continuously up to the current mile, what is the best score if I end here in this lane with this many switches used?

---

## Why These States Are Sufficient

To decide the best value for the next mile, we do not need the full history.

We only need to know:

- current lane
- how many switches were used so far
- total coins accumulated

That is enough because the next move only depends on whether Mario:

- stays in the same lane, or
- switches from the other lane

So this DP has the correct “memory”.

---

## Transition Intuition

Assume we are processing mile `i`.

If Mario ends this mile in **lane 1**, then exactly one of these must be true:

1. He **starts** a new trip at mile `i` in lane 1
2. He was already in lane 1 at mile `i-1` and **continues**
3. He was in lane 2 at mile `i-1` and **switches** into lane 1 before mile `i`

Likewise for lane 2.

That “start / continue / switch” decomposition is the heart of the recurrence.

---

## Transitions for Lane 1

Let the value collected at mile `i` in lane 1 be `lane1[i]`.

### `new_dp1[0]`

To end in lane 1 with 0 switches used, Mario has only two possibilities:

- start a new trip at mile `i` in lane 1
- continue a previous 0-switch trip in lane 1

So:

```text
new_dp1[0] = max(
    lane1[i],
    old_dp1[0] + lane1[i]
)
```

### `new_dp1[1]`

To end in lane 1 with exactly 1 switch used:

- continue an existing 1-switch trip in lane 1
- switch from lane 2 with 0 switches used

So:

```text
new_dp1[1] = max(
    old_dp1[1] + lane1[i],
    old_dp2[0] + lane1[i]
)
```

### `new_dp1[2]`

To end in lane 1 with exactly 2 switches used:

- continue an existing 2-switch trip in lane 1
- switch from lane 2 with 1 switch used

So:

```text
new_dp1[2] = max(
    old_dp1[2] + lane1[i],
    old_dp2[1] + lane1[i]
)
```

---

## Transitions for Lane 2

Let the value collected at mile `i` in lane 2 be `lane2[i]`.

### Important subtlety: starting directly in lane 2

Mario always enters on lane 1.

But the problem explicitly says he may switch **immediately upon entering**.

So he is allowed to start the trip at mile `i` and instantly move to lane 2 before driving that mile.

That consumes **1 switch**.

So lane 2 can be the first traveled lane, but it costs one switch.

### `new_dp2[0]`

Impossible.

There is no way to end in lane 2 with 0 switches, because Mario enters on lane 1.

### `new_dp2[1]`

To end in lane 2 with exactly 1 switch used:

- start at mile `i` and immediately switch into lane 2
- continue an existing 1-switch trip in lane 2
- switch from lane 1 with 0 switches used

So:

```text
new_dp2[1] = max(
    lane2[i],
    old_dp2[1] + lane2[i],
    old_dp1[0] + lane2[i]
)
```

### `new_dp2[2]`

To end in lane 2 with exactly 2 switches used:

- continue an existing 2-switch trip in lane 2
- switch from lane 1 with 1 switch used

So:

```text
new_dp2[2] = max(
    old_dp2[2] + lane2[i],
    old_dp1[1] + lane2[i]
)
```

---

## Why Starting a New Trip Must Be Included

Mario can enter the freeway **anywhere**.

That means the best answer may begin at any mile, not necessarily mile 0.

This is exactly the same reason Kadane’s algorithm includes the choice:

- either extend the previous subarray
- or start fresh here

Without the “start here” option, the DP would incorrectly force Mario to begin too early.

---

## Why “Just Before Exiting” Does Not Need Special Handling

The statement says Mario may switch lanes **just before exiting**.

This sounds special, but it is already naturally handled by the DP.

Why?

Because if Mario wants to switch just before exiting, that simply means:

- the last mile is traveled in the new lane,
- and the trip ends immediately after that mile.

Since our DP tracks the best trip ending at every mile in every lane with every switch count, this case is already covered.

So no extra exit-state logic is needed.

---

## Full Java Code

```java
class Solution {
    public long maxCoins(int[] lane1, int[] lane2) {
        int n = lane1.length;
        long NEG = Long.MIN_VALUE / 4;

        long[] dp1 = {NEG, NEG, NEG};
        long[] dp2 = {NEG, NEG, NEG};

        long ans = NEG;

        for (int i = 0; i < n; i++) {
            long[] ndp1 = {NEG, NEG, NEG};
            long[] ndp2 = {NEG, NEG, NEG};

            // End at mile i in lane 1
            ndp1[0] = Math.max((long) lane1[i], dp1[0] + lane1[i]);

            if (dp1[1] != NEG) ndp1[1] = Math.max(ndp1[1], dp1[1] + lane1[i]);
            if (dp2[0] != NEG) ndp1[1] = Math.max(ndp1[1], dp2[0] + lane1[i]);

            if (dp1[2] != NEG) ndp1[2] = Math.max(ndp1[2], dp1[2] + lane1[i]);
            if (dp2[1] != NEG) ndp1[2] = Math.max(ndp1[2], dp2[1] + lane1[i]);

            // End at mile i in lane 2
            // Start directly in lane 2 by switching immediately on entry => 1 switch
            ndp2[1] = Math.max(ndp2[1], (long) lane2[i]);

            if (dp2[1] != NEG) ndp2[1] = Math.max(ndp2[1], dp2[1] + lane2[i]);
            if (dp1[0] != NEG) ndp2[1] = Math.max(ndp2[1], dp1[0] + lane2[i]);

            if (dp2[2] != NEG) ndp2[2] = Math.max(ndp2[2], dp2[2] + lane2[i]);
            if (dp1[1] != NEG) ndp2[2] = Math.max(ndp2[2], dp1[1] + lane2[i]);

            dp1 = ndp1;
            dp2 = ndp2;

            for (int s = 0; s <= 2; s++) {
                ans = Math.max(ans, dp1[s]);
                ans = Math.max(ans, dp2[s]);
            }
        }

        return ans;
    }
}
```

---

## Code Walkthrough

### 1. Sentinel negative value

```java
long NEG = Long.MIN_VALUE / 4;
```

We use a very small negative number to represent impossible states.

We do **not** use `Long.MIN_VALUE` directly because later we add lane values to DP values, and adding to `Long.MIN_VALUE` risks overflow. Dividing by 4 keeps it safely very negative.

---

### 2. DP initialization

```java
long[] dp1 = {NEG, NEG, NEG};
long[] dp2 = {NEG, NEG, NEG};
```

Before processing any mile, no trip exists yet.

So all states are initially impossible.

---

### 3. For each mile, build new DP arrays

```java
long[] ndp1 = {NEG, NEG, NEG};
long[] ndp2 = {NEG, NEG, NEG};
```

We compute the states for mile `i` using the previous mile’s states.

This avoids accidentally mixing updated values from the same iteration.

---

### 4. Lane 1 updates

```java
ndp1[0] = Math.max((long) lane1[i], dp1[0] + lane1[i]);
```

This is the Kadane-like step for lane 1 with 0 switches:

- start new at `i`
- or continue previous

Then:

```java
if (dp1[1] != NEG) ndp1[1] = Math.max(ndp1[1], dp1[1] + lane1[i]);
if (dp2[0] != NEG) ndp1[1] = Math.max(ndp1[1], dp2[0] + lane1[i]);
```

To end in lane 1 with 1 switch:

- continue same lane with 1 switch already used
- or come from lane 2 with 0 switches and switch now

Similarly for 2 switches:

```java
if (dp1[2] != NEG) ndp1[2] = Math.max(ndp1[2], dp1[2] + lane1[i]);
if (dp2[1] != NEG) ndp1[2] = Math.max(ndp1[2], dp2[1] + lane1[i]);
```

---

### 5. Lane 2 updates

```java
ndp2[1] = Math.max(ndp2[1], (long) lane2[i]);
```

This corresponds to:

- enter on lane 1
- switch immediately
- travel mile `i` in lane 2

That uses exactly 1 switch.

Then:

```java
if (dp2[1] != NEG) ndp2[1] = Math.max(ndp2[1], dp2[1] + lane2[i]);
if (dp1[0] != NEG) ndp2[1] = Math.max(ndp2[1], dp1[0] + lane2[i]);
```

To end in lane 2 with 1 switch:

- continue lane 2 after already using 1 switch
- or switch from lane 1 after using 0 switches

And for 2 switches:

```java
if (dp2[2] != NEG) ndp2[2] = Math.max(ndp2[2], dp2[2] + lane2[i]);
if (dp1[1] != NEG) ndp2[2] = Math.max(ndp2[2], dp1[1] + lane2[i]);
```

---

### 6. Move to next mile

```java
dp1 = ndp1;
dp2 = ndp2;
```

Now the newly computed states become the current states.

---

### 7. Update the global answer

```java
for (int s = 0; s <= 2; s++) {
    ans = Math.max(ans, dp1[s]);
    ans = Math.max(ans, dp2[s]);
}
```

At every mile, any valid state ending there could be the best final answer, because Mario can exit anytime after at least one mile.

---

## Formal Correctness Intuition

We can justify correctness with the following reasoning.

### Invariant

After processing mile `i`:

- `dp1[s]` stores the maximum coins among all valid trips that end at mile `i` in lane 1 using exactly `s` switches
- `dp2[s]` stores the same for lane 2

### Base case

At the first processed mile, the only possible ways to form a trip are:

- start in lane 1 with 0 switches
- start in lane 2 by immediate switch with 1 switch

These are exactly what the transitions allow.

### Inductive step

Assume the invariant holds for mile `i - 1`.

Then any optimal trip ending at mile `i` in some lane must be one of:

- starting at `i`
- continuing from the same lane at `i - 1`
- switching from the other lane at `i - 1`

No other possibility exists.

Since the transition takes the maximum over exactly those possibilities, the invariant remains true for mile `i`.

Thus, by induction, all DP states are correct, and taking the maximum over all ending states gives the answer.

---

## Worked Conceptual Example

Suppose:

```text
lane1 = [4, -10, 3, 1]
lane2 = [2, 5, -1, 6]
```

Intuitively:

- Staying in lane 1 throughout is bad because of `-10`
- A smart strategy might be:
  - take lane 1 at mile 0 = `4`
  - switch to lane 2 for mile 1 = `5`
  - maybe continue in lane 2 for mile 3 = `6`

The DP automatically compares:

- staying
- switching once
- switching twice
- starting fresh later

This is exactly why greedy thinking is risky here: a locally bad mile in one lane may still be worthwhile if it enables a stronger segment structure later.

---

## Why a Greedy Approach Is Unsafe

A tempting idea is:

- at each mile, pick the lane with larger coins
- switch whenever beneficial

That fails because the switch budget is limited to 2.

A switch used too early may block a much better switch later.

So the problem has **resource-constrained decisions**, which is a classic sign that greedy local choices may be wrong and DP is more reliable.

---

## Time Complexity

For each mile, we update a constant number of states:

- 3 switch counts for lane 1
- 3 switch counts for lane 2

Each update takes constant time.

So:

```text
Time Complexity = O(n)
```

where `n` is the number of miles.

---

## Space Complexity

We only store:

- current `dp1`, `dp2`
- next `ndp1`, `ndp2`

Each has length 3.

So total auxiliary space is constant:

```text
Space Complexity = O(1)
```

---

## Why This Is Better Than Brute Force

A brute-force approach would try:

- all starting positions
- all ending positions
- all possible lane switch placements

That becomes much too expensive.

Even with only 2 switches, the number of combinations grows roughly with interval choices and switch positions, leading to at least quadratic or cubic exploration depending on implementation.

The DP compresses all those possibilities into just 6 running states.

That is the main optimization.

---
