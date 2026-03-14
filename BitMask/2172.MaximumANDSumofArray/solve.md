# 2172. Maximum AND Sum of Array

## Problem Restatement

You are given:

- an array `nums`
- `numSlots` slots numbered from `1` to `numSlots`
- each slot can hold **at most 2 numbers**

If a number `x` is placed into slot `s`, it contributes:

```text
x & s
```

to the total score.

You must place all numbers into the slots and maximize the total AND sum.

Return that maximum value.

---

## Key Constraints

```text
1 <= numSlots <= 9
1 <= n <= 2 * numSlots
1 <= nums[i] <= 15
```

The most important clues are:

- `numSlots <= 9`
- each slot has capacity `2`
- total capacity is at most `18`

That is a very strong signal that a **state compression DP** solution is intended.

This is not a greedy problem.
A number that looks good in one slot may block a better future assignment.

---

# Core Insight

Each slot has 2 positions.

So instead of thinking in terms of `numSlots` slots, think in terms of:

```text
2 * numSlots positions
```

For example, if `numSlots = 3`, then the positions are:

- position 0 -> slot 1
- position 1 -> slot 1
- position 2 -> slot 2
- position 3 -> slot 2
- position 4 -> slot 3
- position 5 -> slot 3

Now the problem becomes:

> Assign each number in `nums` to a distinct position, maximizing total score.

This is a classic bitmask DP formulation.

---

# Approach 1: Bitmask DP Over Positions (Recommended)

## Idea

There are `2 * numSlots` available positions.

We use a bitmask of length `2 * numSlots`:

- bit = `1` means that position is already occupied
- bit = `0` means it is still free

If `mask` has `k` set bits, that means we have already placed the first `k` numbers of `nums`.

So:

```text
dp[mask] = maximum AND sum after placing first popcount(mask) numbers
           into the occupied positions in mask
```

From this state, try putting the next number into every empty position.

---

## Why does `popcount(mask)` tell us which number to place?

Suppose we place numbers in the order:

```text
nums[0], nums[1], nums[2], ...
```

If `mask` already uses `k` positions, then exactly `k` numbers have been placed.

So the next number to place must be:

```text
nums[k]
```

This removes the need to store the index separately.

---

## Mapping Position to Slot

If position index is `pos`, then its slot number is:

```java
slot = pos / 2 + 1;
```

Because positions `0,1` belong to slot `1`, positions `2,3` belong to slot `2`, and so on.

The contribution of placing `nums[k]` into `pos` is:

```java
nums[k] & slot
```

---

## State Transition

For each `mask`:

- let `used = Integer.bitCount(mask)`
- if `used == nums.length`, all numbers have been placed
- otherwise, next number is `nums[used]`

Try every position `pos` that is still empty:

- `nextMask = mask | (1 << pos)`
- `dp[nextMask] = max(dp[nextMask], dp[mask] + (nums[used] & slot))`

---

## Java Code

```java
class Solution {
    public int maximumANDSum(int[] nums, int numSlots) {
        int totalPositions = 2 * numSlots;
        int totalMasks = 1 << totalPositions;

        int[] dp = new int[totalMasks];

        for (int mask = 0; mask < totalMasks; mask++) {
            int used = Integer.bitCount(mask);

            if (used >= nums.length) continue;

            int num = nums[used];

            for (int pos = 0; pos < totalPositions; pos++) {
                if ((mask & (1 << pos)) != 0) continue;

                int slot = pos / 2 + 1;
                int nextMask = mask | (1 << pos);

                dp[nextMask] = Math.max(dp[nextMask], dp[mask] + (num & slot));
            }
        }

        int ans = 0;
        for (int mask = 0; mask < totalMasks; mask++) {
            if (Integer.bitCount(mask) == nums.length) {
                ans = Math.max(ans, dp[mask]);
            }
        }

        return ans;
    }
}
```

---

## Complexity

Let:

```text
m = 2 * numSlots <= 18
```

Then:

- number of masks = `2^m`
- for each mask, we may try all `m` positions

So time complexity is:

```text
O(m * 2^m)
```

Since `m <= 18`:

```text
18 * 2^18 ≈ 4.7 million operations
```

which is completely fine.

Space complexity:

```text
O(2^m)
```

---

## Why this is the best practical solution

This approach is:

- compact
- fast
- easy to reason about
- standard for this problem

It directly models the real constraint: **each slot has two spaces**.

---

# Approach 2: DFS + Memoization with Ternary/Base-3 State Per Slot

## Idea

Another natural way is to track, for each slot, how many numbers are already placed in it:

- `0` = empty
- `1` = one number used
- `2` = full

Since each slot has 3 possible states, we can encode the slot occupancy as a **base-3 number**.

For example, with `numSlots = 3`, a state like:

```text
[2, 1, 0]
```

means:

- slot 1 is full
- slot 2 has one number
- slot 3 is empty

This can be encoded into one integer.

Then we do DFS:

- the number of already placed elements tells us which `nums[idx]` to place next
- try placing it in any slot that is not full
- recurse
- memoize the result

---

## Why base-3 works well here

Each slot has exactly 3 possible occupancy counts:

```text
0, 1, 2
```

So base-3 encoding is a perfect compact representation.

Total number of states:

```text
3^numSlots
```

Since `numSlots <= 9`:

```text
3^9 = 19683
```

Very small.

---

## State Definition

Let:

```text
dfs(state, idx) = maximum AND sum we can get
                  starting from number nums[idx]
                  with slot occupancy encoded in state
```

Or even better:

- `idx` can be derived from the total number of used positions in `state`
- but passing it explicitly keeps code simpler

Transition:

- try every slot with occupancy `< 2`
- put current number there
- contribution is `nums[idx] & (slotNumber)`

---

## Java Code

```java
import java.util.Arrays;

class Solution {
    private int[] nums;
    private int numSlots;
    private int[] memo;
    private int[] pow3;

    public int maximumANDSum(int[] nums, int numSlots) {
        this.nums = nums;
        this.numSlots = numSlots;

        pow3 = new int[numSlots];
        pow3[0] = 1;
        for (int i = 1; i < numSlots; i++) {
            pow3[i] = pow3[i - 1] * 3;
        }

        int totalStates = 1;
        for (int i = 0; i < numSlots; i++) totalStates *= 3;

        memo = new int[totalStates];
        Arrays.fill(memo, -1);

        return dfs(0, 0);
    }

    private int dfs(int state, int idx) {
        if (idx == nums.length) return 0;
        if (memo[state] != -1) return memo[state];

        int best = 0;

        for (int slot = 0; slot < numSlots; slot++) {
            int count = (state / pow3[slot]) % 3;
            if (count == 2) continue;

            int nextState = state + pow3[slot];
            int gain = nums[idx] & (slot + 1);

            best = Math.max(best, gain + dfs(nextState, idx + 1));
        }

        return memo[state] = best;
    }
}
```

---

## Complexity

Number of states:

```text
3^numSlots
```

For each state, we try up to `numSlots` slots.

So time complexity:

```text
O(numSlots * 3^numSlots)
```

With `numSlots <= 9`, this is tiny.

Space complexity:

```text
O(3^numSlots)
```

---

## Pros

- Elegant
- State size is extremely small
- Often even cleaner conceptually than position-bitmask DP

## Cons

- Base-3 encoding is less obvious if you have not seen it before
- Slightly more specialized

---

# Approach 3: DFS + Memoization with Explicit Slot Capacities Array

## Idea

We can also do straightforward recursion:

- keep an array `count[slot]` showing how many numbers are in each slot
- try placing the current number into each slot with remaining capacity
- memoize based on the counts

This is conceptually simple, but we still need a compact memo key.

A common way is to serialize the counts array into an integer key using base-3 encoding anyway.

So this approach is essentially the same search as Approach 2, but explained more operationally.

---

## Java Code

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    private int[] nums;
    private int numSlots;
    private Map<Integer, Integer> memo;
    private int[] pow3;

    public int maximumANDSum(int[] nums, int numSlots) {
        this.nums = nums;
        this.numSlots = numSlots;
        this.memo = new HashMap<>();

        pow3 = new int[numSlots];
        pow3[0] = 1;
        for (int i = 1; i < numSlots; i++) {
            pow3[i] = pow3[i - 1] * 3;
        }

        int[] counts = new int[numSlots];
        return dfs(0, counts);
    }

    private int dfs(int idx, int[] counts) {
        if (idx == nums.length) return 0;

        int key = encode(counts);
        if (memo.containsKey((idx << 20) ^ key)) {
            return memo.get((idx << 20) ^ key);
        }

        int best = 0;

        for (int slot = 0; slot < numSlots; slot++) {
            if (counts[slot] == 2) continue;

            counts[slot]++;
            int gain = nums[idx] & (slot + 1);
            best = Math.max(best, gain + dfs(idx + 1, counts));
            counts[slot]--;
        }

        memo.put((idx << 20) ^ key, best);
        return best;
    }

    private int encode(int[] counts) {
        int state = 0;
        for (int i = 0; i < numSlots; i++) {
            state += counts[i] * pow3[i];
        }
        return state;
    }
}
```

---

## Complexity

Same idea as Approach 2.

Time:

```text
O(numSlots * 3^numSlots)
```

Space:

```text
O(3^numSlots)
```

---

## Pros

- Very intuitive from a recursion standpoint
- Easy to build incrementally

## Cons

- Slightly more overhead
- Less clean than direct DP or direct base-3 memoization

---

# Approach 4: Brute Force Backtracking Without Memoization

## Idea

Try every possible assignment of each number to a slot that still has capacity.

This is the raw recursive form before memoization.

---

## Java Code

```java
class Solution {
    private int ans = 0;

    public int maximumANDSum(int[] nums, int numSlots) {
        int[] count = new int[numSlots];
        dfs(nums, 0, numSlots, count, 0);
        return ans;
    }

    private void dfs(int[] nums, int idx, int numSlots, int[] count, int current) {
        if (idx == nums.length) {
            ans = Math.max(ans, current);
            return;
        }

        for (int slot = 0; slot < numSlots; slot++) {
            if (count[slot] == 2) continue;

            count[slot]++;
            dfs(nums, idx + 1, numSlots, count, current + (nums[idx] & (slot + 1)));
            count[slot]--;
        }
    }
}
```

---

## Complexity

This can be very large.

In the worst case, each number may try many slots, so the branching factor is high.

Without memoization, many equivalent states are recomputed again and again.

This is not the preferred solution.

---

## Pros

- Simplest conceptual starting point
- Good for deriving the memoized solution

## Cons

- Too much repeated work
- Inferior to DP / memoized approaches

---

# Deep Intuition

## Why greedy does not work

A tempting greedy idea is:

> For each number, put it into the slot where `num & slot` is largest.

That fails because slot capacity is limited to 2.

You may use a valuable slot too early and block a later number that benefits even more from that slot.

So local best placement is not reliable.

---

## Why sorting also does not solve it

You might also think:

- large numbers first
- or numbers with many bits first
- or slots with high index first

But the AND operation is very bit-specific.

A smaller number can sometimes match a slot much better than a larger number.

So you really need global optimization.

---

## Why state compression is natural

The important information is not the full arrangement history.
What matters is only:

- which positions are already occupied, or
- how many numbers are already in each slot

That is exactly what compressed state DP captures.

Once you know current occupancies, the past order no longer matters.

---

# Correctness Sketch for Approach 1

We prove that the bitmask DP finds the optimal answer.

## State meaning

`dp[mask]` stores the maximum score obtainable after placing exactly `popcount(mask)` numbers into the occupied positions represented by `mask`.

## Base case

```text
dp[0] = 0
```

No positions used, no numbers placed, total score = 0.

## Transition

Suppose `mask` has `k` used positions.
Then the next number to place is `nums[k]`.

For every empty position `pos`, placing `nums[k]` there yields:

```text
dp[mask | (1 << pos)] = max(
    dp[mask | (1 << pos)],
    dp[mask] + (nums[k] & slot(pos))
)
```

This considers every legal next move.

## Optimal substructure

If an optimal full assignment ends in some position for the last placed number, then before that move, the earlier assignments must themselves form an optimal solution for the previous mask. Otherwise, we could improve the final solution.

So DP is valid.

Therefore, by enumerating all masks and transitions, we compute the optimal answer.

---

# Example Walkthrough

## Example 1

```text
nums = [1,2,3,4,5,6]
numSlots = 3
```

Total positions = `6`.

Positions map to slots:

- 0,1 -> slot 1
- 2,3 -> slot 2
- 4,5 -> slot 3

When `mask = 0`, we place `nums[0] = 1`.

Possible gains:

- into slot 1: `1 & 1 = 1`
- into slot 2: `1 & 2 = 0`
- into slot 3: `1 & 3 = 1`

DP explores all such possibilities.

Eventually it reaches the best arrangement:

- slot 1 -> `[1, 4]`
- slot 2 -> `[2, 6]`
- slot 3 -> `[3, 5]`

Score:

```text
(1&1) + (4&1) + (2&2) + (6&2) + (3&3) + (5&3)
= 1 + 0 + 2 + 2 + 3 + 1
= 9
```

---

# Final Recommended Java Solution

This is the version I would submit.

```java
class Solution {
    public int maximumANDSum(int[] nums, int numSlots) {
        int totalPositions = 2 * numSlots;
        int totalMasks = 1 << totalPositions;

        int[] dp = new int[totalMasks];

        for (int mask = 0; mask < totalMasks; mask++) {
            int used = Integer.bitCount(mask);

            if (used >= nums.length) continue;

            for (int pos = 0; pos < totalPositions; pos++) {
                if ((mask & (1 << pos)) != 0) continue;

                int slot = pos / 2 + 1;
                int nextMask = mask | (1 << pos);

                dp[nextMask] = Math.max(
                    dp[nextMask],
                    dp[mask] + (nums[used] & slot)
                );
            }
        }

        int ans = 0;
        for (int mask = 0; mask < totalMasks; mask++) {
            if (Integer.bitCount(mask) == nums.length) {
                ans = Math.max(ans, dp[mask]);
            }
        }

        return ans;
    }
}
```

---

# Comparison of Approaches

| Approach   | Main Idea                                   |            Time Complexity | Space Complexity | Recommended |
| ---------- | ------------------------------------------- | -------------------------: | ---------------: | ----------- |
| Approach 1 | Bitmask DP over `2 * numSlots` positions    |         `O((2S) * 2^(2S))` |      `O(2^(2S))` | Yes         |
| Approach 2 | DFS + memo with base-3 slot occupancy state |               `O(S * 3^S)` |         `O(3^S)` | Yes         |
| Approach 3 | DFS + memo with counts array encoding       |               `O(S * 3^S)` |         `O(3^S)` | Good        |
| Approach 4 | Plain backtracking                          | Exponential, repeated work |  Recursive stack | No          |

Here `S = numSlots`.

---

# Which approach is better?

Both Approach 1 and Approach 2 are strong.

## Choose Approach 1 when:

- you are comfortable with bitmask DP
- you like the “two positions per slot” modeling
- you want a standard iterative solution

## Choose Approach 2 when:

- you notice each slot has states `0/1/2`
- you like memoized DFS
- you want fewer states

Interestingly, the base-3 solution often has fewer states than the position-bitmask one:

- bitmask states: `2^(2S)`
- ternary states: `3^S`

For `S = 9`:

```text
2^18 = 262144
3^9  = 19683
```

So the ternary-state solution is extremely efficient too.

Still, the bitmask-position solution is often considered the most direct to derive.

---

# Pattern Recognition Takeaway

This problem is a strong example of:

- small capacity constraints
- small slot count
- assignment optimization
- state compression DP

Whenever you see:

- at most 9 containers
- each with tiny fixed capacity
- need maximum score over assignments

you should suspect:

- bitmask DP
- ternary/base-k occupancy encoding
- DFS + memoization over compact state

---

# Final Takeaway

The cleanest way to think about this problem is:

1. each slot has two positions
2. track which positions are used
3. place numbers one by one
4. use DP over the occupancy state

That gives an efficient and robust solution well within the constraints.
