# Minimum Operations to Make the Array Beautiful — Correct DP Solution

## Problem

You are given an integer array `nums`.

An array is called **beautiful** if for every index `i > 0`:

`nums[i]` is divisible by `nums[i - 1]`

So for every adjacent pair, we need:

`nums[i] % nums[i - 1] == 0`

You may perform the following operation any number of times:

- choose any index `i > 0`
- increment `nums[i]` by `1`

We must return the **minimum number of operations** required to make the array beautiful.

---

# 1. The first tempting greedy idea — and why it fails

A very natural first thought is:

- process from left to right
- for each `i`, raise `nums[i]` to the **smallest multiple** of the already-fixed previous value `nums[i - 1]`

That sounds reasonable because it is the cheapest local fix.

But it is **not always globally optimal**.

## Counterexample

Consider:

`nums = [5, 13, 18]`

### Greedy choice

- keep `5`
- make `13` the smallest multiple of `5` → `15`
  cost = `2`
- now make `18` divisible by `15` → `30`
  cost = `12`

Total cost = `14`

### Better choice

- keep `5`
- make `13` become `20`
  cost = `7`
- make `18` become `20`
  cost = `2`

Total cost = `9`

So the greedy answer `14` is wrong, and the true optimum is `9`.

---

# 2. Why the greedy argument breaks

The flawed assumption is:

> making the current value as small as possible must help future positions

That is false here.

Future cost does not depend only on how small the current value is.
It depends on **which multiples** later values must reach.

For example, for the next value `18`:

- if previous value is `15`, then the next multiple at least `18` is `30`, so the cost is `12`
- if previous value is `20`, then the next multiple at least `18` is `20`, so the cost is `2`

So a **larger** current value can make the next step dramatically cheaper.

That means we cannot keep just one choice per index.
We must preserve multiple candidate final values.

---

# 3. Correct problem reformulation

We are really choosing a new final array:

`b[0], b[1], ..., b[n-1]`

such that:

1. `b[0] = nums[0]`
   because index `0` cannot be changed

2. `b[i] >= nums[i]` for all `i > 0`
   because we may only increment

3. `b[i] % b[i - 1] == 0` for all `i > 0`
   because the final array must be beautiful

And we want to minimize:

`(b[1] - nums[1]) + (b[2] - nums[2]) + ... + (b[n-1] - nums[n-1])`

So this is a **dynamic programming over possible final values** problem.

---

# 4. Main DP intuition

At position `i`, the key thing that affects the future is:

- the final value chosen for position `i`

So instead of storing only one best answer for the prefix, we store:

- for each possible final value `v` of the current position,
- the minimum total cost to reach that state

This is the right state because the next element only cares about one thing:

- what value it must be divisible by

That value is exactly the chosen final value of the previous position.

---

# 5. DP state definition

Let:

`dp[v] = minimum total cost after processing the current prefix, if the current element’s final value is exactly v`

This means:

- we have already made the prefix beautiful
- the current last value is `v`
- `dp[v]` is the minimum cost among all ways to achieve that

---

# 6. Base state

The first element cannot be changed.

So initially:

- the only possible final value is `nums[0]`
- and the cost is `0`

So:

`dp[nums[0]] = 0`

All other states are impossible at the start.

---

# 7. Transition

Suppose we are processing `nums[i] = x`.

Assume we already have a previous DP state:

- previous final value = `pre`
- previous cost = `dp[pre]`

Now the new final value `cur` must satisfy:

1. `cur >= x`
2. `cur % pre == 0`

So `cur` must be a multiple of `pre`, starting from the first multiple that is at least `x`.

That first valid multiple is:

`ceil(x / pre) * pre`

A standard integer formula for that is:

`((x + pre - 1) / pre) * pre`

Then all other valid choices are:

- `cur`
- `cur + pre`
- `cur + 2 * pre`
- ...

For each such candidate, the additional cost is:

`cur - x`

So the transition is:

`next[cur] = min(next[cur], dp[pre] + (cur - x))`

We repeat this for every previous state and every valid new multiple.

---

# 8. Why a bound of 100 is enough

A crucial optimization is that we do **not** need to consider arbitrarily large final values.

For this problem, the constraints are small:

- `nums[i] <= 50`

A standard accepted bound is to consider only candidate final values from `1` to `100`.

## Why this works

Assume the previous chosen value is `pre <= 100`.

Now we want the smallest multiple of `pre` that is at least `x`, where `x <= 50`.

There are two cases:

### Case 1: `pre > 50`

Then `pre` itself is already at least `x`.
So the smallest valid multiple is just:

`pre`

which is still `<= 100`.

### Case 2: `pre <= 50`

Then the first valid multiple at least `x` is at most:

`2 * pre`

because `x <= 50`.

And since `pre <= 50`, we get:

`2 * pre <= 100`

So the smallest relevant valid multiple is always at most `100`.

Now by induction:

- the first state is `nums[0] <= 50`
- every later relevant optimal state stays within `1..100`

So it is safe to restrict DP values to `<= 100`.

That makes the state space small and manageable.

---

# 9. Complete algorithm

1. Create a map or array for DP states
2. Initialize:
   - `dp[nums[0]] = 0`
3. For each next value `x = nums[i]`:
   - create a fresh `next`
   - for every reachable previous value `pre`:
     - compute the first valid multiple of `pre` that is at least `x`
     - enumerate all multiples up to `100`
     - update the minimum cost for each candidate
   - replace `dp` with `next`
4. After processing the full array, return the minimum value stored in `dp`

---

# 10. Step-by-step walkthrough on the counterexample

Consider:

`nums = [5, 13, 18]`

We know the correct answer should be `9`.

---

## Step 1: initialize

Only one possible state after the first element:

`dp = {5 -> 0}`

That means:

- current final value is `5`
- total cost so far is `0`

---

## Step 2: process `13`

Previous state:

- `pre = 5`
- cost = `0`

We need multiples of `5` that are at least `13`.

The first such multiple is:

`15`

Then the valid choices are:

- `15` with extra cost `2`
- `20` with extra cost `7`
- `25` with extra cost `12`
- `30` with extra cost `17`
- ...

So after processing index `1`, we get states like:

- `15 -> 2`
- `20 -> 7`
- `25 -> 12`
- `30 -> 17`
- ...

---

## Step 3: process `18`

Now process each previous state.

### From state `15 -> 2`

Need multiples of `15` at least `18`:

- `30` with extra cost `12`
  total = `14`
- `45` with extra cost `27`
- ...

So from `15`, the best path starts with cost `14`.

### From state `20 -> 7`

Need multiples of `20` at least `18`:

- `20` with extra cost `2`
  total = `9`
- `40` with extra cost `22`
- ...

So from `20`, we get total cost `9`.

That is better than `14`.

Therefore the DP correctly finds:

**Answer = 9**

with final array:

`[5, 20, 20]`

---

# 11. Why this DP is correct

We should justify correctness carefully.

## Claim

After processing index `i`, `dp[v]` stores the minimum total number of operations needed to make the prefix `nums[0..i]` beautiful, with final value at index `i` equal to `v`.

## Proof idea

### Base case

At `i = 0`, the only possible final value is `nums[0]`, and the cost is `0`.
So the DP initialization is correct.

### Transition

Suppose we have correctly computed all DP states for index `i - 1`.

To build a valid state at index `i` with final value `cur`, the previous final value `pre` must satisfy:

`cur % pre == 0`

Also we must have:

`cur >= nums[i]`

Then the total cost is:

- the best cost to reach `pre`
- plus the cost to increase `nums[i]` to `cur`, which is `cur - nums[i]`

So for each candidate `cur`, taking the minimum over all valid previous `pre` gives the true optimal cost.

Thus the transition preserves correctness.

### Conclusion

By induction, after the final index, the minimum among all reachable DP states is the optimal answer.

---

# 12. Java code

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    public int minOperations(int[] nums) {
        Map<Integer, Integer> dp = new HashMap<>();
        dp.put(nums[0], 0);

        for (int i = 1; i < nums.length; i++) {
            int x = nums[i];
            Map<Integer, Integer> next = new HashMap<>();

            for (Map.Entry<Integer, Integer> entry : dp.entrySet()) {
                int pre = entry.getKey();
                int cost = entry.getValue();

                // Smallest multiple of pre that is >= x
                int cur = ((x + pre - 1) / pre) * pre;

                while (cur <= 100) {
                    int newCost = cost + (cur - x);
                    next.merge(cur, newCost, Math::min);
                    cur += pre;
                }
            }

            dp = next;
        }

        int ans = Integer.MAX_VALUE;
        for (int cost : dp.values()) {
            ans = Math.min(ans, cost);
        }
        return ans;
    }
}
```

---

# 13. Code explanation in detail

## 13.1 DP storage

```java
Map<Integer, Integer> dp = new HashMap<>();
dp.put(nums[0], 0);
```

We use a map:

- key = possible final value of the current position
- value = minimum cost to achieve it

Initially only `nums[0]` is possible.

---

## 13.2 Process each next element

```java
for (int i = 1; i < nums.length; i++) {
    int x = nums[i];
    Map<Integer, Integer> next = new HashMap<>();
```

For each new array element `x`, we compute a fresh set of DP states.

---

## 13.3 Iterate previous states

```java
for (Map.Entry<Integer, Integer> entry : dp.entrySet()) {
    int pre = entry.getKey();
    int cost = entry.getValue();
```

Each previous state says:

- the previous final value is `pre`
- the minimum cost so far is `cost`

---

## 13.4 Compute first valid multiple

```java
int cur = ((x + pre - 1) / pre) * pre;
```

This is the smallest multiple of `pre` that is at least `x`.

That is the first legal final value for the current position.

---

## 13.5 Enumerate all valid multiples

```java
while (cur <= 100) {
    int newCost = cost + (cur - x);
    next.merge(cur, newCost, Math::min);
    cur += pre;
}
```

Every multiple of `pre` at least `x` is a valid candidate final value.

For each one:

- extra operations = `cur - x`
- total cost = previous cost + extra cost

We keep only the minimum total cost for each final value `cur`.

---

## 13.6 Move to next layer

```java
dp = next;
```

After processing index `i`, the current layer becomes the new DP.

---

## 13.7 Extract answer

```java
int ans = Integer.MAX_VALUE;
for (int cost : dp.values()) {
    ans = Math.min(ans, cost);
}
return ans;
```

At the end, the final value of the last element can be many different numbers.

We want the minimum cost among all of them.

---

# 14. Complexity analysis

Let:

- `n` = length of the array
- value range considered = `1..100`

## Time complexity

At each index:

- there are at most `100` possible previous states
- for each state, we enumerate multiples up to `100`

In the worst case, that gives an upper bound of:

**`O(n * 100 * 100)`**

Since `100` is a constant, this is effectively linear in practice.

A more practical view is that the runtime is tiny because the DP state space is very small.

## Space complexity

We store only:

- current DP map
- next DP map

Each contains at most `100` states.

So:

**Space complexity: `O(100)`**

which is effectively:

**`O(1)`**

with respect to the input size.

---

# 15. Why this approach is better than greedy

The greedy approach keeps only one candidate value per index.
That loses important future possibilities.

The DP approach keeps **all relevant candidate final values** for the current position, each with its best achievable cost.

So it does not prematurely discard options like:

- choosing `20` instead of `15` in `[5, 13, 18]`

That preserved flexibility is exactly why the DP succeeds where greedy fails.

---

# 16. Final takeaway

This problem is not locally greedy because:

- choosing the smallest valid value at one step can create very expensive later steps

The correct solution is to use dynamic programming where:

- state = chosen final value of the current position
- value = minimum cost to reach that state

For each next element, we transition to all valid multiples of the previous chosen value.

Because the value range is small, this DP is efficient and clean.

---
