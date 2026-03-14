# 1815. Maximum Number of Groups Getting Fresh Donuts — Exhaustive Java Notes

## Problem Statement

A donut shop bakes donuts in batches of size `batchSize`.

Rules:

- all donuts from the current batch must be served before any donuts from the next batch,
- groups arrive one by one,
- all people in a group must be served before the next group starts,
- a group is **happy** if the first person in that group gets a **fresh** donut, meaning there are no leftovers from the previous group.

You may reorder the groups arbitrarily.

Return the maximum number of happy groups.

---

## Example 1

```text
Input:
batchSize = 3
groups = [1,2,3,4,5,6]

Output:
4
```

One optimal order is:

```text
[6,2,4,5,1,3]
```

Then groups 1, 2, 4, and 6 are happy.

---

## Example 2

```text
Input:
batchSize = 4
groups = [1,3,2,5,2,2,1,6]

Output:
4
```

---

## Constraints

```text
1 <= batchSize <= 9
1 <= groups.length <= 30
1 <= groups[i] <= 10^9
```

The crucial clue is:

```text
batchSize <= 9
```

That means only the **remainder modulo batchSize** matters.

---

# 1. Core Insight

Suppose a group has size `g`.

Only this matters:

```text
g % batchSize
```

Why?

Because if there are `r` leftover donuts before the group starts, then after serving this group the new leftover is determined entirely by:

```text
(r + g) % batchSize
```

Equivalently, if we think of “used modulo batchSize”, only the remainder affects the next state.

So instead of working with huge group sizes, we compress them into remainder buckets:

```text
count[0], count[1], ..., count[batchSize - 1]
```

where:

```text
count[x] = number of groups whose size % batchSize == x
```

---

# 2. Immediate Greedy Win: Remainder 0

Any group with:

```text
group % batchSize == 0
```

always starts on a fresh batch if scheduled at any point when the previous state is “clean”.

But actually even more strongly, such groups themselves do not disturb the modulo state.

Each remainder-0 group is always happy if inserted whenever the current leftover is 0, and since they do not change the remainder, they are always straightforward to count immediately.

So the answer starts with:

```text
count[0]
```

Then we solve the reduced problem on remainders `1..batchSize-1`.

---

# 3. Pairing Complementary Remainders

If two remainders add up to `batchSize`, they complement each other.

For example, if `batchSize = 7`, then:

- remainder `1` pairs with `6`
- remainder `2` pairs with `5`
- remainder `3` pairs with `4`

If one group leaves remainder `r`, and the next group has remainder `batchSize - r`, then together they exactly consume a whole number of batches and reset the leftover to 0.

That often creates more happy groups.

So before full DP, it is profitable to greedily pair:

```text
r and batchSize - r
```

for all `r`.

Also, when `batchSize` is even, remainder `batchSize/2` pairs with itself.

This greedy reduction is not strictly required for correctness if you do full DP, but it helps shrink the state space a lot.

---

# 4. Modeling the State

After greedy preprocessing, we need to know:

- how many groups of each remainder are still unused,
- what the current leftover modulo state is.

A group is happy exactly when it starts with leftover `0`.

If current remainder is `mod`, and we choose remainder `r`, then:

- the group is happy iff `mod == 0`
- next remainder becomes:

```text
(mod + r) % batchSize
```

So this naturally becomes a DP / DFS over remaining counts.

---

# 5. Approach 1 — DFS + Memoization on Remainder Counts

## Main Idea

Let `cnt[r]` be how many groups remain with remainder `r`.

Define a DFS state by:

- the vector of counts of remaining remainders

The current leftover modulo can actually be inferred from how much total remainder has already been used, but the simplest implementation carries it directly or derives it from the state.

A standard elegant trick is:

- encode the remaining counts into a compact integer / long key
- recursively try taking one group of each available remainder
- if current remainder is `0`, gain `+1` for the chosen group
- memoize the best result

---

## State Compression

Because:

```text
groups.length <= 30
batchSize <= 9
```

the count vector is tiny.

One standard encoding:

- use 5 bits per remainder count
- since a count is at most 30, 5 bits are enough
- remainders range from `1` to `batchSize - 1`, at most 8 slots

So one `long` can encode everything comfortably.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int batchSize;
    private Map<Long, Integer> memo = new HashMap<>();

    public int maxHappyGroups(int batchSize, int[] groups) {
        this.batchSize = batchSize;

        int[] count = new int[batchSize];
        for (int g : groups) {
            count[g % batchSize]++;
        }

        int happy = count[0];

        // Greedy complement pairing
        for (int i = 1; i <= (batchSize - 1) / 2; i++) {
            int j = batchSize - i;
            int use = Math.min(count[i], count[j]);
            happy += use;
            count[i] -= use;
            count[j] -= use;
        }

        // Special self-pair when batchSize is even
        if (batchSize % 2 == 0) {
            int half = batchSize / 2;
            int use = count[half] / 2;
            happy += use;
            count[half] %= 2;
        }

        long state = encode(count);
        return happy + dfs(state, 0);
    }

    private int dfs(long state, int mod) {
        if (state == 0) return 0;

        long key = (state << 4) | mod;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        int best = 0;

        for (int r = 1; r < batchSize; r++) {
            int c = getCount(state, r);
            if (c == 0) continue;

            long nextState = state - (1L << ((r - 1) * 5));
            int gain = (mod == 0) ? 1 : 0;
            best = Math.max(best, gain + dfs(nextState, (mod + r) % batchSize));
        }

        memo.put(key, best);
        return best;
    }

    private long encode(int[] count) {
        long state = 0;
        for (int r = 1; r < batchSize; r++) {
            state |= ((long) count[r]) << ((r - 1) * 5);
        }
        return state;
    }

    private int getCount(long state, int r) {
        return (int) ((state >> ((r - 1) * 5)) & 31L);
    }
}
```

---

## Why This Works

The DFS tries every valid next remainder bucket.
The happiness contribution of the next chosen group depends only on whether the current leftover is zero.
Then we transition to the next remainder state.

Memoization ensures each compressed state is solved once.

---

## Complexity

The exact number of states depends on the remaining count distribution, but it is manageable because:

- `batchSize <= 9`
- total groups `<= 30`

In practice this solution is fast enough and is the standard accepted approach.

Space is proportional to the number of memoized states.

---

# 6. Important Observation: Why Complement Pairing Is Safe

If one group has remainder `r` and another has remainder `batchSize - r`, serving them consecutively causes the remainder to reset to zero after the second one.

These complementary pairs are naturally strong candidates to form happy chains.

Greedily taking them first is safe as a reduction because:

- they directly yield one guaranteed extra happy group in the sequence structure,
- they reduce the state space dramatically,
- any optimal arrangement can be transformed to realize these exact complementary cancellations without reducing the answer.

This preprocessing is widely used in accepted solutions.

---

# 7. Approach 2 — DFS + Memoization Without Greedy Preprocessing

## Main Idea

You can also skip complement pairing entirely and let DFS solve the whole thing.

This is simpler conceptually:

1. count remainders,
2. count `remainder 0` groups immediately,
3. DFS over remaining count vector.

This is fully correct, though it may explore more states than the preprocessed version.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int batchSize;
    private Map<Long, Integer> memo = new HashMap<>();

    public int maxHappyGroups(int batchSize, int[] groups) {
        this.batchSize = batchSize;

        int[] count = new int[batchSize];
        for (int g : groups) {
            count[g % batchSize]++;
        }

        int baseHappy = count[0];
        count[0] = 0;

        return baseHappy + dfs(encode(count), 0);
    }

    private int dfs(long state, int mod) {
        if (state == 0) return 0;

        long key = (state << 4) | mod;
        if (memo.containsKey(key)) return memo.get(key);

        int best = 0;
        for (int r = 1; r < batchSize; r++) {
            int c = getCount(state, r);
            if (c == 0) continue;

            long nextState = state - (1L << ((r - 1) * 5));
            int gain = (mod == 0) ? 1 : 0;
            best = Math.max(best, gain + dfs(nextState, (mod + r) % batchSize));
        }

        memo.put(key, best);
        return best;
    }

    private long encode(int[] count) {
        long state = 0;
        for (int r = 1; r < batchSize; r++) {
            state |= ((long) count[r]) << ((r - 1) * 5);
        }
        return state;
    }

    private int getCount(long state, int r) {
        return (int) ((state >> ((r - 1) * 5)) & 31L);
    }
}
```

---

## Complexity

Asymptotically similar to Approach 1, but more states are visited.

Still acceptable because the compressed state space is small enough for the constraints.

---

# 8. Approach 3 — DP on Count-State with Derived Modulo

## Main Idea

Instead of storing `mod` separately in the memo key, you can derive it from the state.

How?

If we know the original remainder counts and current remaining counts, then we know exactly how much total remainder has already been used, so current modulo is determined.

That means the DP state can be just the count vector.

This is a nice optimization and is often used in editorial-quality solutions.

---

## Key Formula

Suppose original counts are `orig[r]` and current remaining counts are `cnt[r]`.

Then the total consumed remainder is:

```text
sum((orig[r] - cnt[r]) * r) % batchSize
```

So current modulo can be inferred.

That means a memo key only needs the compressed count vector.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int batchSize;
    private int[] original;
    private Map<Long, Integer> memo = new HashMap<>();

    public int maxHappyGroups(int batchSize, int[] groups) {
        this.batchSize = batchSize;
        this.original = new int[batchSize];

        for (int g : groups) {
            original[g % batchSize]++;
        }

        int happy = original[0];
        original[0] = 0;

        long state = encode(original);
        return happy + dfs(state);
    }

    private int dfs(long state) {
        if (state == 0) return 0;
        if (memo.containsKey(state)) return memo.get(state);

        int mod = currentMod(state);
        int best = 0;

        for (int r = 1; r < batchSize; r++) {
            int c = getCount(state, r);
            if (c == 0) continue;

            long nextState = state - (1L << ((r - 1) * 5));
            int gain = (mod == 0) ? 1 : 0;
            best = Math.max(best, gain + dfs(nextState));
        }

        memo.put(state, best);
        return best;
    }

    private int currentMod(long state) {
        int usedSum = 0;
        for (int r = 1; r < batchSize; r++) {
            int remain = getCount(state, r);
            int used = original[r] - remain;
            usedSum = (usedSum + used * r) % batchSize;
        }
        return usedSum;
    }

    private long encode(int[] count) {
        long state = 0;
        for (int r = 1; r < batchSize; r++) {
            state |= ((long) count[r]) << ((r - 1) * 5);
        }
        return state;
    }

    private int getCount(long state, int r) {
        return (int) ((state >> ((r - 1) * 5)) & 31L);
    }
}
```

---

## Why This Is Nice

This reduces the memo dimension slightly because `mod` no longer needs to be part of the key.

That can cut memory and simplify the recursion.

The tradeoff is recomputing the modulo from the state each time.

Since `batchSize <= 9`, that recomputation is tiny.

---

# 9. Why Brute Force Permutations Are Impossible

A naive approach would try all orderings of groups.

With up to 30 groups, that is:

```text
30!
```

which is absurdly large.

Even if many groups share the same remainder, permutation-based search is hopeless.

The only reason the problem is tractable is that:

- `batchSize <= 9`
- only remainders matter
- so many group orders collapse to the same compressed count state

That is exactly why state compression works.

---

# 10. Why Only the Current Remainder Matters

Suppose before serving a group there are `x` leftover donuts from the current batch.

A group with size `g` will:

- be happy iff `x == 0`
- leave new remainder:

```text
(x + g) % batchSize
```

No other detail of the past matters.

So the entire history is compressible into:

```text
current remainder modulo batchSize
```

plus the multiset of remaining remainders.

That Markov-style property is what makes DP possible.

---

# 11. Small Worked Example

Take:

```text
batchSize = 3
groups = [1,2,3,4,5,6]
```

Remainders:

- `1 % 3 = 1`
- `2 % 3 = 2`
- `3 % 3 = 0`
- `4 % 3 = 1`
- `5 % 3 = 2`
- `6 % 3 = 0`

So counts are:

```text
count[0] = 2
count[1] = 2
count[2] = 2
```

We immediately get:

```text
happy = 2
```

from the remainder-0 groups.

Then `1` pairs with `2`, twice:

```text
happy += 2
```

Total:

```text
4
```

which matches the answer.

---

# 12. Correctness Sketch

The ordering only matters through the sequence of remainders modulo `batchSize`.

At any step, if the current remainder is `mod`, choosing a group with remainder `r` contributes:

- `+1` happy group iff `mod == 0`
- new state with remainder `(mod + r) % batchSize`

Thus the future only depends on:

- the current remainder
- how many groups of each remainder remain

The DFS explores all such choices and memoizes optimal results for each compressed state.
Hence it computes the true maximum.

Greedy preprocessing on complementary remainders only removes obviously optimal reductions and does not alter the best achievable answer.

---

# 13. Comparison of Approaches

| Approach                    | State        |                     Time |                       Space | Notes                       |
| --------------------------- | ------------ | -----------------------: | --------------------------: | --------------------------- |
| DFS + memo + greedy pairing | counts + mod |    practical exponential |                 memo states | standard strongest solution |
| DFS + memo no greedy        | counts + mod | a bit larger state space |                 memo states | conceptually simplest       |
| DFS + memo derived mod      | counts only  |                  similar | slightly less memo key size | elegant state reduction     |

All are based on the same core idea: state compression over remainder counts.

---

# 14. Recommended Java Solution

This is the version I would recommend most often: greedy preprocessing plus memoized DFS.

```java
import java.util.*;

class Solution {
    private int batchSize;
    private Map<Long, Integer> memo = new HashMap<>();

    public int maxHappyGroups(int batchSize, int[] groups) {
        this.batchSize = batchSize;

        int[] count = new int[batchSize];
        for (int g : groups) {
            count[g % batchSize]++;
        }

        int happy = count[0];

        for (int i = 1; i <= (batchSize - 1) / 2; i++) {
            int j = batchSize - i;
            int use = Math.min(count[i], count[j]);
            happy += use;
            count[i] -= use;
            count[j] -= use;
        }

        if (batchSize % 2 == 0) {
            int half = batchSize / 2;
            int use = count[half] / 2;
            happy += use;
            count[half] %= 2;
        }

        long state = encode(count);
        return happy + dfs(state, 0);
    }

    private int dfs(long state, int mod) {
        if (state == 0) return 0;

        long key = (state << 4) | mod;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        int best = 0;

        for (int r = 1; r < batchSize; r++) {
            int c = getCount(state, r);
            if (c == 0) continue;

            long nextState = state - (1L << ((r - 1) * 5));
            int gain = (mod == 0) ? 1 : 0;
            best = Math.max(best, gain + dfs(nextState, (mod + r) % batchSize));
        }

        memo.put(key, best);
        return best;
    }

    private long encode(int[] count) {
        long state = 0;
        for (int r = 1; r < batchSize; r++) {
            state |= ((long) count[r]) << ((r - 1) * 5);
        }
        return state;
    }

    private int getCount(long state, int r) {
        return (int) ((state >> ((r - 1) * 5)) & 31L);
    }
}
```

---

# 15. Final Takeaway

This problem looks like a permutation problem, but it is really a **compressed remainder scheduling** problem.

The winning observations are:

1. only `group % batchSize` matters,
2. `batchSize <= 9`, so remainder count states are tiny,
3. the entire process is determined by:
   - current remainder,
   - remaining counts of each remainder.

That turns a hopeless search over `30!` orderings into a manageable memoized DFS over compressed states.

This is exactly the type of problem where recognizing the right state compression is everything.
