# 3533. Concatenated Divisibility

## Problem Restatement

You are given:

- an array of positive integers `nums`
- a positive integer `k`

You may reorder `nums` into some permutation.

If concatenating the decimal strings of the numbers in that order produces a number divisible by `k`, then that permutation is valid.

You must return:

- the **lexicographically smallest valid permutation**
- or an empty array if no valid permutation exists

---

## Key Constraints

```text
1 <= nums.length <= 13
1 <= nums[i] <= 10^5
1 <= k <= 100
```

The two most important clues are:

- `n <= 13`
- `k <= 100`

That strongly suggests:

- bitmask DP over subsets of numbers
- remainder DP modulo `k`
- memoized search with reconstruction

Because:

```text
2^13 = 8192
```

which is tiny enough for subset-based dynamic programming.

---

# Core Insight

We do **not** need the full concatenated number.

That number can become astronomically large.

Instead, we only care about its value **modulo `k`**.

That is the central trick.

---

## Concatenation modulo formula

Suppose current concatenated value is `X`, and we append a number `y` having `len(y)` decimal digits.

Then:

```text
newValue = X * 10^{len(y)} + y
```

So modulo `k`:

```text
newRem = (rem * 10^{len(y)} + y) % k
```

where:

- `rem = X % k`

That means when building the permutation, we only need to track:

- which numbers have been used
- current remainder modulo `k`

This completely avoids large integer construction.

---

# Lexicographic Smallness

The problem asks for the **lexicographically smallest permutation** among all valid ones.

That means:

- compare permutations element by element
- the first smaller element wins

So once we know which states can still lead to a valid final answer, we should always greedily choose the **smallest possible next number** that preserves feasibility.

That suggests:

1. sort the candidates by value (and index if needed)
2. use DP/memo to test whether a state is solvable
3. reconstruct greedily

---

# Handling duplicate values

The statement says "a permutation of nums", and the result is returned as a list of integers.
So if `nums` contains equal values, they are still distinct array positions during permutation generation, but the final returned list compares by values.

The safest approach is:

- treat every element by its original index for subset DP
- sort indices by `(value, index)` during reconstruction so lexicographic ordering on values is respected deterministically

This avoids ambiguity.

---

# Preprocessing

For each number `nums[i]`, precompute:

1. `digits[i]` = number of decimal digits
2. `valMod[i] = nums[i] % k`
3. `pow10[digits[i]] % k`

Then when appending `nums[i]` to a state with remainder `rem`:

```text
nextRem = (rem * pow10[digits[i]] + valMod[i]) % k
```

---

# Approach 1: DFS + Memoization on `(mask, rem)` with Greedy Reconstruction (Recommended)

## Idea

Define:

```text
can(mask, rem)
```

= whether it is possible to complete the permutation using the unused numbers so that the final concatenated number becomes divisible by `k`, given that the current remainder is `rem`.

Base case:

- if all numbers are used, return whether `rem == 0`

Transition:

- try appending any unused number
- recurse on the resulting `(nextMask, nextRem)`

Memoize the result.

Once feasibility is known, reconstruct the answer greedily by always picking the smallest next value that still allows completion.

---

## State Definition

- `mask`: which indices are already used
- `rem`: current remainder modulo `k`

State count:

```text
2^n * k
```

At worst:

```text
8192 * 100 = 819200
```

Very manageable.

---

## Why this works

The future only depends on:

- which numbers remain
- the remainder of the prefix modulo `k`

The exact full prefix number does not matter, because modulo transition fully captures how future appends affect divisibility.

That is the DP compression principle.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int[] nums;
    private int n, k, fullMask;
    private int[] digitPow;
    private int[] valMod;
    private Boolean[][] memo;
    private Integer[] order;

    public int[] concatenatedDivisibility(int[] nums, int k) {
        this.nums = nums;
        this.n = nums.length;
        this.k = k;
        this.fullMask = (1 << n) - 1;

        digitPow = new int[n];
        valMod = new int[n];

        int[] pow10 = new int[7];
        pow10[0] = 1 % k;
        for (int i = 1; i < pow10.length; i++) {
            pow10[i] = (pow10[i - 1] * 10) % k;
        }

        for (int i = 0; i < n; i++) {
            int digits = String.valueOf(nums[i]).length();
            digitPow[i] = pow10[digits];
            valMod[i] = nums[i] % k;
        }

        memo = new Boolean[1 << n][k];

        order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> {
            if (nums[a] != nums[b]) return Integer.compare(nums[a], nums[b]);
            return Integer.compare(a, b);
        });

        if (!dfs(0, 0)) return new int[0];

        int[] ans = new int[n];
        int mask = 0, rem = 0, idx = 0;

        while (mask != fullMask) {
            for (int id : order) {
                if ((mask & (1 << id)) != 0) continue;

                int nextRem = (rem * digitPow[id] + valMod[id]) % k;
                if (dfs(mask | (1 << id), nextRem)) {
                    ans[idx++] = nums[id];
                    mask |= 1 << id;
                    rem = nextRem;
                    break;
                }
            }
        }

        return ans;
    }

    private boolean dfs(int mask, int rem) {
        if (mask == fullMask) return rem == 0;
        if (memo[mask][rem] != null) return memo[mask][rem];

        for (int id : order) {
            if ((mask & (1 << id)) != 0) continue;

            int nextRem = (rem * digitPow[id] + valMod[id]) % k;
            if (dfs(mask | (1 << id), nextRem)) {
                return memo[mask][rem] = true;
            }
        }

        return memo[mask][rem] = false;
    }
}
```

---

## Complexity

Number of states:

```text
O(2^n * k)
```

Each state tries at most `n` transitions.

So total time complexity:

```text
O(2^n * n * k)
```

Space:

```text
O(2^n * k)
```

This is excellent for `n <= 13` and `k <= 100`.

---

## Pros

- Clean
- Naturally supports lexicographic reconstruction
- Strongest practical solution

## Cons

- Requires careful modulo concatenation formula
- Need to think in terms of feasibility, not direct construction first

---

# Approach 2: Bottom-Up DP for Feasibility + Greedy Reconstruction

## Idea

Instead of top-down recursion, we can build a table:

```text
dp[mask][rem] = true/false
```

meaning:

- using exactly the indices in `mask`, it is possible to get remainder `rem`

Initialize:

```text
dp[0][0] = true
```

Then for every reachable state, append any unused number.

At the end, if:

```text
dp[fullMask][0] == false
```

then answer does not exist.

To reconstruct lexicographically smallest result, greedily choose the smallest next element that keeps the state reachable to the end.

For this, a forward-only feasibility table is not always enough, so it is helpful to also have a reverse reachability concept or simply use memoized suffix feasibility as in Approach 1. Still, bottom-up remains a valid conceptual approach.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int[] concatenatedDivisibility(int[] nums, int k) {
        int n = nums.length;
        int fullMask = (1 << n) - 1;

        int[] valMod = new int[n];
        int[] digitPow = new int[n];

        int[] pow10 = new int[7];
        pow10[0] = 1 % k;
        for (int i = 1; i < pow10.length; i++) {
            pow10[i] = (pow10[i - 1] * 10) % k;
        }

        for (int i = 0; i < n; i++) {
            valMod[i] = nums[i] % k;
            int digits = String.valueOf(nums[i]).length();
            digitPow[i] = pow10[digits];
        }

        boolean[][] dp = new boolean[1 << n][k];
        dp[0][0] = true;

        for (int mask = 0; mask <= fullMask; mask++) {
            for (int rem = 0; rem < k; rem++) {
                if (!dp[mask][rem]) continue;

                for (int i = 0; i < n; i++) {
                    if ((mask & (1 << i)) != 0) continue;
                    int nextMask = mask | (1 << i);
                    int nextRem = (rem * digitPow[i] + valMod[i]) % k;
                    dp[nextMask][nextRem] = true;
                }
            }
        }

        if (!dp[fullMask][0]) return new int[0];

        // For lexicographically smallest exact reconstruction, top-down suffix feasibility
        // is simpler, so in practice Approach 1 is preferred.
        return new int[0]; // placeholder if using pure bottom-up only
    }
}
```

---

## Complexity

Same state-space complexity:

```text
O(2^n * n * k)
```

Space:

```text
O(2^n * k)
```

---

## Pros

- Iterative
- Very explicit state transitions

## Cons

- Reconstruction is less elegant
- Pure bottom-up is not as pleasant for lexicographic path retrieval

---

# Approach 3: DP Storing Parent for Lexicographically Smallest Reachable State

## Idea

We can enhance bottom-up DP to also store a parent pointer for each reachable state:

```text
parentMask[mask][rem]
parentIndex[mask][rem]
parentRem[mask][rem]
```

But because a state may be reachable in multiple ways, and we need the lexicographically smallest full permutation, tie-breaking becomes subtle.

A clean way is:

- process transitions in lexicographic order
- only set a parent the first time a state is reached
- but this does **not always** guarantee the lexicographically smallest full permutation unless the state ordering and reachability logic are carefully aligned

So while possible, it is more delicate than the top-down greedy reconstruction.

---

## Practical takeaway

This approach is valid, but for correctness simplicity, Approach 1 is preferable.

---

# Approach 4: Brute Force Over All Permutations

## Idea

Generate all permutations of `nums`, concatenate them modulo `k`, and return the lexicographically smallest valid one.

Because `n <= 13`, this is not feasible.

At worst:

```text
13! = 6,227,020,800
```

which is far too large.

---

## Java Code

```java
import java.util.*;

class Solution {
    private int[] best = null;
    private int k;

    public int[] concatenatedDivisibility(int[] nums, int k) {
        this.k = k;
        Arrays.sort(nums);

        boolean[] used = new boolean[nums.length];
        List<Integer> cur = new ArrayList<>();
        backtrack(nums, used, cur);

        if (best == null) return new int[0];
        return best;
    }

    private void backtrack(int[] nums, boolean[] used, List<Integer> cur) {
        if (best != null) return; // because sorted traversal gives first lexicographic answer

        if (cur.size() == nums.length) {
            if (isDivisible(cur)) {
                best = cur.stream().mapToInt(Integer::intValue).toArray();
            }
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            used[i] = true;
            cur.add(nums[i]);
            backtrack(nums, used, cur);
            cur.remove(cur.size() - 1);
            used[i] = false;
        }
    }

    private boolean isDivisible(List<Integer> arr) {
        int rem = 0;
        for (int x : arr) {
            int p = 1;
            int y = x;
            while (y > 0) {
                p = (p * 10) % k;
                y /= 10;
            }
            rem = (rem * p + x % k) % k;
        }
        return rem == 0;
    }
}
```

---

## Complexity

Time:

```text
O(n! * n)
```

or worse depending on implementation.

Not feasible.

---

## Pros

- Straightforward
- Useful only for tiny instances

## Cons

- Completely impractical for upper constraints

---

# Deep Intuition

## Why remainder is the only thing that matters

Suppose we already built a prefix `P`.

To decide whether a future continuation can make the final number divisible by `k`, we do not need the exact full integer `P`.

We only need:

```text
P % k
```

because every future concatenation step transforms the remainder deterministically.

That is the mathematical reason DP works.

---

## Why sorting + greedy reconstruction gives lexicographically smallest answer

Once feasibility is memoized, we reconstruct as follows:

- at each step, try unused numbers in ascending order
- choose the first one that still allows completion to a valid final remainder `0`

This is correct because lexicographic order depends on the earliest position where two permutations differ.

So making the earliest possible element as small as possible is always optimal, provided completion is still possible.

---

## Why index-based subset handling is safer than value-based subset handling

Even if values repeat, array positions are distinct elements in a permutation.

If we used values alone as state identity, duplicates would collapse incorrectly.

By using bitmasks over indices, we preserve exact usage count and avoid ambiguity.

The returned result still uses the values themselves.

---

# Correctness Sketch for Approach 1

We prove the DFS + memoization solution is correct.

## State definition

`dfs(mask, rem)` is true iff it is possible to append all currently unused elements to the current prefix so that the final concatenated number is divisible by `k`, given that the current prefix remainder is `rem`.

## Base case

If all numbers are used, then the concatenated number is complete.

It is valid iff:

```text
rem == 0
```

So the base case is correct.

## Transition

If some elements remain, choose any unused index `i`.

Appending `nums[i]` changes the remainder to:

```text
nextRem = (rem * 10^{digits(nums[i])} + nums[i]) % k
```

Therefore, the current state is solvable iff at least one unused `i` leads to a solvable next state.

That matches the recurrence exactly.

## Memoization validity

The result of a state depends only on:

- which elements are used
- current remainder modulo `k`

So caching by `(mask, rem)` is valid.

## Lexicographic reconstruction

During reconstruction, we test unused candidates in ascending value order and choose the first one that still leads to a solvable suffix.

If there were a lexicographically smaller valid permutation, it would differ at the first position where we skipped a smaller feasible candidate, which is impossible by construction.

Hence the reconstructed permutation is the lexicographically smallest valid one.

---

# Example Walkthrough

## Example 1

```text
nums = [3, 12, 45], k = 5
```

Sorted order is:

```text
[3, 12, 45]
```

Try prefix `3`:

```text
rem = 3 % 5 = 3
```

Append `12`:

```text
312 % 5 = 2
```

Append `45`:

```text
31245 % 5 = 0
```

So `[3,12,45]` is valid.

Since reconstruction tries numbers in ascending order and this one works, it is the lexicographically smallest valid permutation.

---

## Example 2

```text
nums = [10, 5], k = 10
```

Sorted order is:

```text
[5, 10]
```

Concatenation:

```text
510 % 10 = 0
```

So `[5,10]` is valid.

Trying `[10,5]` gives:

```text
105 % 10 = 5
```

not valid.

Answer is `[5,10]`.

---

## Example 3

```text
nums = [1,2,3], k = 5
```

Any number divisible by 5 must end with digit `0` or `5`.

None of the numbers ends with `0` or `5`, so no permutation can form a final divisible concatenation.

The DP will correctly find no valid completion and return `[]`.

---

# Final Recommended Java Solution

This is the version I would submit.

```java
import java.util.*;

class Solution {
    private int[] nums;
    private int n, k, fullMask;
    private int[] digitPow;
    private int[] valMod;
    private Boolean[][] memo;
    private Integer[] order;

    public int[] concatenatedDivisibility(int[] nums, int k) {
        this.nums = nums;
        this.n = nums.length;
        this.k = k;
        this.fullMask = (1 << n) - 1;

        digitPow = new int[n];
        valMod = new int[n];

        int[] pow10 = new int[7];
        pow10[0] = 1 % k;
        for (int i = 1; i < pow10.length; i++) {
            pow10[i] = (pow10[i - 1] * 10) % k;
        }

        for (int i = 0; i < n; i++) {
            int digits = String.valueOf(nums[i]).length();
            digitPow[i] = pow10[digits];
            valMod[i] = nums[i] % k;
        }

        memo = new Boolean[1 << n][k];

        order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> {
            if (nums[a] != nums[b]) return Integer.compare(nums[a], nums[b]);
            return Integer.compare(a, b);
        });

        if (!dfs(0, 0)) return new int[0];

        int[] ans = new int[n];
        int mask = 0, rem = 0, idx = 0;

        while (mask != fullMask) {
            for (int id : order) {
                if ((mask & (1 << id)) != 0) continue;

                int nextRem = (rem * digitPow[id] + valMod[id]) % k;
                if (dfs(mask | (1 << id), nextRem)) {
                    ans[idx++] = nums[id];
                    mask |= 1 << id;
                    rem = nextRem;
                    break;
                }
            }
        }

        return ans;
    }

    private boolean dfs(int mask, int rem) {
        if (mask == fullMask) return rem == 0;
        if (memo[mask][rem] != null) return memo[mask][rem];

        for (int id : order) {
            if ((mask & (1 << id)) != 0) continue;

            int nextRem = (rem * digitPow[id] + valMod[id]) % k;
            if (dfs(mask | (1 << id), nextRem)) {
                return memo[mask][rem] = true;
            }
        }

        return memo[mask][rem] = false;
    }
}
```

---

# Comparison of Approaches

| Approach   | Main Idea                                                    |  Time Complexity | Space Complexity | Recommended |
| ---------- | ------------------------------------------------------------ | ---------------: | ---------------: | ----------- |
| Approach 1 | DFS + memo on `(mask, remainder)` with greedy reconstruction | `O(2^n * n * k)` |     `O(2^n * k)` | Yes         |
| Approach 2 | Bottom-up DP on subset and remainder                         | `O(2^n * n * k)` |     `O(2^n * k)` | Good        |
| Approach 3 | Parent-tracked DP for exact path retrieval                   | `O(2^n * n * k)` |     `O(2^n * k)` | Possible    |
| Approach 4 | Brute-force permutations                                     |      `O(n! * n)` |           `O(n)` | No          |

---

# Pattern Recognition Takeaway

This problem has a very recognizable pattern:

- order matters
- divisibility of a huge constructed number is asked
- `n` is small
- `k` is small

That strongly points to:

- modulo DP
- subset bitmasking
- lexicographic reconstruction from feasibility

Whenever a huge concatenated number appears, you should almost immediately think:

> I only need its remainder, not the number itself.

That is the essential mathematical simplification.

---

# Final Takeaway

The cleanest solution is:

1. precompute each number’s digit length and modulo `k`
2. use the transition:
   ```text
   nextRem = (rem * 10^digits + value) % k
   ```
3. memoize feasibility by `(usedMask, currentRemainder)`
4. reconstruct greedily in ascending value order
5. return the first lexicographically smallest valid full permutation

That gives an efficient and correct solution for the given constraints.
