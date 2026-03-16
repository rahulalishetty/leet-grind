# Optimal Account Balancing — Detailed Notes

This document converts the provided explanation into a detailed Markdown note.

The problem discussed here is the classic **Optimal Account Balancing** problem:

- We are given a list of transactions
- Each transaction is of the form:

```text
[from, to, amount]
```

- We want to settle all debts using the **minimum number of transactions**

---

# Core Idea: Reduce Pairwise Transactions to Net Balances

## Intuition

Instead of thinking about the original debt graph directly, we can focus only on each person’s **net balance**.

For example:

- if a person receives money overall, they end up with a **positive** balance
- if a person pays money overall, they end up with a **negative** balance

What matters for the final settlement is not the original path of money, but only the final balance of each person.

### Example Interpretation

Suppose person `1`:

- is owed `5` by person `2`
- owes `10` to person `3`
- owes `10` to person `4`

Then person `1` has net balance:

```text
+5 - 10 - 10 = -15
```

So person `1` must pay `15` overall.

---

# Institution Interpretation

A useful conceptual trick is to imagine an external “institution”.

- A person with positive balance can pay that amount to the institution
- A person with negative balance can receive that amount from the institution

If there are `n` people with non-zero balances, this settles everything in at most `n` transactions.

Even better, one of the actual people can conceptually act as the institution, which reduces the number of required transactions to:

```text
n - 1
```

This leads to a crucial insight:

> Any group of people whose total net balance sums to `0` can settle internally using `groupSize - 1` transactions.

So the problem becomes:

1. compute net balances
2. ignore everyone whose balance is zero
3. work only with the resulting list of non-zero balances

If the list is empty, the answer is `0`.

---

# Step 1: Build the Balance List

Given the transactions, build a map:

```text
person -> net balance
```

For each transaction `[from, to, amount]`:

- `from` gains `amount`
- `to` loses `amount`

or the opposite depending on the chosen sign convention, as long as it is used consistently.

Then collect all non-zero balances into a list:

```text
balanceList
```

This list is the real state space of the problem.

---

# Approach 1: Backtracking

## Intuition

Once we have the balance list, we want to settle all balances with the minimum number of transfers.

We define a recursive function:

```text
dfs(cur)
```

which means:

> the minimum number of transactions needed to settle all balances from index `cur` onward.

At each step:

- skip all already-zero balances
- take the current non-zero balance at index `cur`
- try to settle it with one of the later balances having the opposite sign
- recursively solve the rest

The recursive idea is:

- transfer all of `balanceList[cur]` into `balanceList[nxt]`
- then `cur` becomes settled
- recurse on the next index

This is a standard backtracking search over settlement combinations.

---

## Why Opposite Sign Matters

We only try pairing `cur` with `nxt` if:

```text
balanceList[cur] * balanceList[nxt] < 0
```

That means one person owes money and the other should receive money.

If both have the same sign, settling them against each other makes no sense.

This pruning is important.

---

## Recursive State

Suppose:

```text
balanceList[cur] = 3
```

Then we try every later person with negative balance.

If we transfer the full `3` into one of them:

```text
balanceList[nxt] += balanceList[cur]
```

then person `cur` is considered settled, and we recurse on:

```text
dfs(cur + 1)
```

After recursion, we undo the transfer to backtrack.

---

## Algorithm

1. Build `creditMap` / balance map
2. Extract all non-zero balances into `creditList`
3. Define `dfs(cur)`:
   - skip all zero balances
   - if `cur == n`, return `0`
   - otherwise try all `nxt > cur` where signs are opposite
   - transfer `creditList[cur]` into `creditList[nxt]`
   - recurse
   - undo transfer
   - keep the minimum result
4. Return `dfs(0)`

---

## Java Implementation

```java
class Solution {
    public int minTransfers(int[][] transactions) {
        Map<Integer, Integer> creditMap = new HashMap<>();
        for (int[] t : transactions) {
            creditMap.put(t[0], creditMap.getOrDefault(t[0], 0) + t[2]);
            creditMap.put(t[1], creditMap.getOrDefault(t[1], 0) - t[2]);
        }

        creditList = new ArrayList<>();
        for (int amount : creditMap.values()) {
            if (amount != 0) {
                creditList.add(amount);
            }
        }

        int n = creditList.size();
        return dfs(0, n);
    }

    List<Integer> creditList;

    private int dfs(int cur, int n) {
        while (cur < n && creditList.get(cur) == 0) {
            cur++;
        }

        if (cur == n) {
            return 0;
        }

        int cost = Integer.MAX_VALUE;
        for (int nxt = cur + 1; nxt < n; nxt++) {
            if (creditList.get(nxt) * creditList.get(cur) < 0) {
                creditList.set(nxt, creditList.get(nxt) + creditList.get(cur));
                cost = Math.min(cost, 1 + dfs(cur + 1, n));
                creditList.set(nxt, creditList.get(nxt) - creditList.get(cur));
            }
        }

        return cost;
    }
}
```

---

## Complexity Analysis

Let `n` be the number of non-zero balances.

### Time Complexity

In the worst case:

- from `dfs(0)` there can be up to `n - 1` choices
- from `dfs(1)` there can be up to `n - 2` choices
- and so on

This gives:

```text
O((n - 1)!)
```

This is factorial-time in the worst case.

### Space Complexity

We use:

- `creditMap` and `creditList`: `O(n)`
- recursion stack: up to `O(n)`

So total:

```text
O(n)
```

---

# Why the Backtracking Works

Every recursive call is deciding:

> who should absorb the balance of the current person?

By forcing `cur` to become settled before moving on, we systematically enumerate all possible settlement structures.

Because we try all valid opposite-sign pairings and take the minimum, the result is optimal.

---

# Approach 2: Dynamic Programming with Bitmasking

## Intuition

The backtracking solution works directly with the balances and explicitly tries settlement pairings.

But there is another way to think about the problem.

Recall the earlier key fact:

> A group of `k` people whose total balance is `0` can settle internally in `k - 1` transactions.

So if we can partition the balance list into as many zero-sum groups as possible, then we minimize the number of transactions.

### Why?

If there are `n` non-zero balances total and we partition them into `m` zero-sum groups, then the total number of transactions is:

```text
(n - 1) + (n - 1) + ...
```

group by group, which sums to:

```text
n - m
```

because each zero-sum group of size `g` needs `g - 1` transactions.

So the problem becomes:

> Partition the balance list into the maximum number of disjoint zero-sum subgroups.

That is a perfect setup for **bitmask DP**.

---

# Bitmask Representation

Suppose the balance list has size `n`.

We represent any subset of people using a bitmask of length `n`.

For example, if:

```text
n = 4
```

then mask:

```text
1111
```

means all four people are included.

Mask:

```text
1011
```

means persons `0`, `1`, and `3` are included.

This lets us represent subsets compactly.

---

# DP State Definition

Define:

```text
dfs(mask)
```

as:

> the maximum number of zero-sum subgroups that can be formed from the people included in `mask`.

Then the final answer is:

```text
n - dfs((1 << n) - 1)
```

because if we can form `m` zero-sum groups from all `n` people, then the total transactions needed are:

```text
n - m
```

---

# Recursive Transition

To solve `dfs(totalMask)`:

1. Try removing one person at a time from the group
2. Recursively solve the smaller subset
3. Take the maximum result from those subproblems
4. If the total balance sum of the current `totalMask` is `0`, then the current subset itself can form one additional zero-sum group

So:

```text
dfs(mask) = max(dfs(mask without one person))
```

and if the sum of balances in `mask` is zero:

```text
dfs(mask) = maxSubproblem + 1
```

This works because if the current subset has total sum zero, then after recursively solving a smaller subproblem, the “remaining” portion can be grouped into one more zero-sum group.

---

# Memoization

There are many repeated subset computations.

So we memoize:

```text
memo[mask]
```

where `memo[mask]` stores the maximum number of zero-sum subgroups obtainable from that subset.

This reduces the state space to all subsets of the `n` balances.

---

## Algorithm

1. Build the net balance map
2. Store all non-zero balances in `creditList`
3. Let `n = creditList.size()`
4. Create `memo` array of size `2^n`, initialize to `-1`
5. Set `memo[0] = 0`
6. Define recursive `dfs(mask)`:
   - if already memoized, return it
   - compute the total balance sum of the people in `mask`
   - try removing each set bit from `mask`
   - keep the maximum value from subproblems
   - if `balanceSum == 0`, add `1`
   - store and return
7. Final answer:

```text
n - dfs((1 << n) - 1)
```

---

## Java Implementation

```java
class Solution {
    public int minTransfers(int[][] transactions) {
        Map<Integer, Integer> creditMap = new HashMap<>();
        for (int[] t : transactions) {
            creditMap.put(t[0], creditMap.getOrDefault(t[0], 0) + t[2]);
            creditMap.put(t[1], creditMap.getOrDefault(t[1], 0) - t[2]);
        }

        creditList = new ArrayList<>();
        for (int amount : creditMap.values()) {
            if (amount != 0) {
                creditList.add(amount);
            }
        }

        int n = creditList.size();
        int[] memo = new int[1 << n];
        Arrays.fill(memo, -1);
        memo[0] = 0;

        return n - dfs((1 << n) - 1, memo);
    }

    List<Integer> creditList;

    private int dfs(int totalMask, int[] memo) {
        if (memo[totalMask] != -1) {
            return memo[totalMask];
        }

        int balanceSum = 0;
        int answer = 0;

        for (int i = 0; i < creditList.size(); i++) {
            int curBit = 1 << i;
            if ((totalMask & curBit) != 0) {
                balanceSum += creditList.get(i);
                answer = Math.max(answer, dfs(totalMask ^ curBit, memo));
            }
        }

        memo[totalMask] = answer + (balanceSum == 0 ? 1 : 0);
        return memo[totalMask];
    }
}
```

---

## Complexity Analysis

Let `n` be the number of non-zero balances.

### Time Complexity

There are:

```text
2^n
```

possible subsets.

For each subset, we iterate through up to `n` positions to:

- compute the sum
- try removing each set bit

So the total complexity is:

```text
O(n * 2^n)
```

### Space Complexity

We use:

- `memo` array of size `2^n`
- recursion stack up to `O(n)`

So the dominant term is:

```text
O(2^n)
```

---

# Why the Bitmask DP Works

The central idea is that minimizing transactions is equivalent to maximizing the number of zero-sum groups.

That transforms the problem from:

- “How do I directly simulate transfers?”

into:

- “How many zero-sum groups can I form?”

The bitmask DP explores every subset and counts how many valid zero-sum groups can be extracted.

Because all subsets are memoized, each state is solved only once.

---

# Comparing the Two Approaches

| Approach     | Core Idea                                            | Time Complexity | Space Complexity |
| ------------ | ---------------------------------------------------- | --------------: | ---------------: |
| Backtracking | Settle current balance with an opposite-sign balance |   `O((n - 1)!)` |           `O(n)` |
| Bitmask DP   | Maximize number of zero-sum subgroups                |    `O(n * 2^n)` |         `O(2^n)` |

---

# Which Approach Is Better?

## Backtracking

Pros:

- very intuitive once net balances are understood
- directly models debt settlement
- easy to explain conceptually

Cons:

- worst-case factorial complexity
- may struggle as the number of non-zero balances grows

## Bitmask DP

Pros:

- much better asymptotic complexity
- elegant transformation into zero-sum subgroup partitioning
- strong use of subset DP

Cons:

- less intuitive at first
- requires bitmask techniques and subset reasoning

---

# Key Takeaways

1. The first major simplification is converting raw transactions into net balances.
2. Once zero balances are removed, the problem becomes much smaller.
3. The backtracking approach works by greedily settling one person at a time and exploring all valid opposite-sign pairings.
4. The DP approach works by recognizing that every zero-sum subgroup saves one transaction.
5. The optimal solution can therefore be phrased as:
   - maximize the number of zero-sum groups
   - then answer is `n - numberOfGroups`

---

# Final Insight

The most important conceptual leap is this:

> We do not need to preserve the original transaction graph.
> We only need the final net balances.

From there:

- one solution explores direct settlement choices via backtracking
- the other solution reformulates the problem as a subset-partition DP

That is what makes this problem such a strong example of both **state compression** and **problem transformation**.
