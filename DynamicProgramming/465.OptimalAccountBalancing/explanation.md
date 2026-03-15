# Optimal Account Balancing — Two DFS Approaches (Intuition + Time Complexity)

This note summarizes **two common solutions** for LeetCode “Optimal Account Balancing”:

1. **Backtracking DFS on balances** (pairing debts and credits, minimizing transactions directly)
2. **Bitmask DP / subset DFS** (maximize # of zero-sum groups, then compute `n - groups`)

Both start with the same preprocessing step: compress transactions into net balances.

---

## 0) Shared preprocessing: convert transactions → net balances

For each transaction `[from, to, amount]`:

- `from` pays → net balance increases (they owe more / have surplus outgoing)
- `to` receives → net balance decreases

(Exact sign convention doesn’t matter as long as it’s consistent; in your code `from += amount`, `to -= amount`.)

Then collect only **non-zero** balances into a list `creditList`.

```java
Map<Integer, Integer> creditMap = new HashMap<>();
for (int[] t : transactions) {
    creditMap.put(t[0], creditMap.getOrDefault(t[0], 0) + t[2]);
    creditMap.put(t[1], creditMap.getOrDefault(t[1], 0) - t[2]);
}

creditList = new ArrayList<>();
for (int amount : creditMap.values()) {
    if (amount != 0) creditList.add(amount);
}
```

Let `n = creditList.size()` be the number of non-zero balances.

This reduction is crucial: it often makes `n` small enough (≤ 12–15 in typical constraints) to allow exponential algorithms.

---

## 1) Approach A: Backtracking DFS (directly minimize transfers)

### Key intuition

Once balances are computed, the problem is:

> Find the minimum number of pairwise transfers to make all balances zero.

The classic backtracking idea:

- Move left to right (`cur`)
- Skip already-settled entries (`balance == 0`)
- For current unsettled person `cur`, try to settle them by pairing with a later person `nxt` of opposite sign
- Perform a “transfer” implicitly by adding `cur` into `nxt` (meaning: after a transaction between cur and nxt, cur becomes settled)
- Recurse to settle the next index

Code shape (from your version):

```java
private int dfs(int cur, int n) {
    while (cur < n && creditList.get(cur) == 0) cur++;
    if (cur == n) return 0;

    int cost = Integer.MAX_VALUE;
    for (int nxt = cur + 1; nxt < n; nxt++) {
        if (creditList.get(nxt) * creditList.get(cur) < 0) { // opposite signs
            creditList.set(nxt, creditList.get(nxt) + creditList.get(cur));
            cost = Math.min(cost, 1 + dfs(cur + 1, n));
            creditList.set(nxt, creditList.get(nxt) - creditList.get(cur)); // backtrack
        }
    }
    return cost;
}
```

### Why this is correct

- Each recursive step “eliminates” one person (`cur`) by settling them against one other person.
- Trying all possible `nxt` candidates ensures we explore all possible settlement matchings.
- Taking the minimum across choices yields the minimal number of transactions.

### Time complexity (big picture)

- Worst-case is **exponential** due to exploring combinations of pairings.
- A common bound is roughly **O(n!)** in the worst case (similar to exploring matchings).
- In practice, pruning helps a lot:
  - skipping zeros
  - only pairing opposite signs
  - many duplicates collapse behavior

### Space complexity

- **O(n)** recursion depth (plus the list).

### Practical notes

This approach is usually fastest in practice for typical constraints because it prunes aggressively and avoids scanning all `2^n` masks.

---

## 2) Approach B: Bitmask DP / subset DFS (maximize zero-sum groups)

### Key intuition

For any subset of **k** people whose balances sum to **0**, the debts inside that subset can be settled with **k − 1** transfers.

So if we can partition `n` people into `m` disjoint zero-sum groups:

- Total transfers = Σ(kᵢ − 1) = (Σkᵢ) − m = **n − m**

Therefore:

> Minimizing transfers is equivalent to **maximizing** the number of zero-sum groups `m`.

So the algorithm computes:

- `maxGroups = dfs(fullMask)`
- answer = `n - maxGroups`

### DP state and meaning

Use a bitmask `mask` of length `n`:

- bit i = 1 → person i included in current subset.

Define:

> `dfs(mask)` = maximum number of zero-sum subgroups we can form using exactly the people in `mask`.

Base:

- `dfs(0) = 0`

Transition:

- remove one person at a time and recurse on the smaller mask:

```java
answer = max over i in mask of dfs(mask without i)
```

Then, if the subset sum is zero, the whole subset contributes one valid group:

```java
dfs(mask) = answer + 1   if sum(mask) == 0
dfs(mask) = answer       otherwise
```

That matches your code:

```java
private int dfs(int totalMask, int[] memo) {
    if (memo[totalMask] != -1) return memo[totalMask];

    int balanceSum = 0, answer = 0;

    for (int i = 0; i < creditList.size(); i++) {
        int bit = 1 << i;
        if ((totalMask & bit) != 0) {
            balanceSum += creditList.get(i);
            answer = Math.max(answer, dfs(totalMask ^ bit, memo));
        }
    }

    memo[totalMask] = answer + (balanceSum == 0 ? 1 : 0);
    return memo[totalMask];
}
```

### Why “+1 if sum(mask) == 0” makes sense

If a subset `mask` sums to zero, then those people **can form one complete settlement group**.
The recursion already computes the best grouping count from smaller subsets; adding 1 accounts for “this subset itself is a group”.

---

## 3) Complexity comparison (important)

### Bitmask DP approach

- There are `2^n` masks.
- For each mask, the code iterates over up to `n` bits to:
  - compute sum(mask)
  - try removing each bit

So:

- **Time:** `O(n · 2^n)`
- **Space:** `O(2^n)` for memo (plus recursion `O(n)`)

This is predictable and often acceptable for `n ≤ 20` (and very good for `n ≤ 15`).

### Backtracking approach

- **Time:** exponential; worst-case roughly factorial-like due to branching (`O(n!)` is a common way to think about it).
- **Space:** `O(n)` recursion depth.

In practice, backtracking is often faster than mask DP for typical problem constraints because it prunes early, but mask DP provides a clean complexity upper bound `O(n·2^n)`.

---

## 4) When to choose which

### Choose backtracking DFS when:

- You want best practical performance on typical small `n`
- You can leverage pruning (skipping zeros, choosing opposite signs, etc.)

### Choose bitmask DP when:

- You prefer predictable `O(n·2^n)` complexity
- You want a solution framed as “maximize zero-sum groups”
- You want memoized reuse across all subsets

---
