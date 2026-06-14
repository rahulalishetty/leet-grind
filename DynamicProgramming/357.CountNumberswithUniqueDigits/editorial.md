# 357. Count Numbers with Unique Digits

## Problem

Given an integer `n`, return the count of all numbers with unique digits, `x`, where:

```text
0 <= x < 10^n
```

That means:

- `n = 0` → numbers in `[0, 1)` → just `0`
- `n = 1` → numbers in `[0, 10)` → `0..9`
- `n = 2` → numbers in `[0, 100)` → `0..99`
- and so on

Constraint:

```text
0 <= n <= 8
```

---

## Core Observation

This is fundamentally a **counting / combinatorics** problem.

Instead of iterating through every number and checking whether its digits are unique, we can count:

- how many valid 1-digit numbers exist,
- how many valid 2-digit numbers exist,
- how many valid 3-digit numbers exist,
- ...
- up to length `n`.

Because digits must be unique:

- the first digit cannot be `0` for multi-digit numbers,
- after choosing one digit, the number of choices for the next digit decreases.

Also, since there are only **10 digits** (`0..9`), once `n > 10`, the answer would stop changing.
In this problem `n <= 8`, so we never hit that ceiling, but it is still a useful mental model.

---

# Approach 1: Brute Force Enumeration

## Idea

Generate every number from `0` to `10^n - 1` and check whether its digits are all unique.

This is the most direct approach and is useful for understanding the problem, but it is not efficient.

---

## How it works

For each number:

1. Extract digits one by one using `% 10` and `/ 10`.
2. Track which digits have appeared.
3. If a digit repeats, reject the number.
4. Otherwise count it.

Special case:

- `0` is valid and has unique digits.

---

## Java Code

```java
class Solution {
    public int countNumbersWithUniqueDigits(int n) {
        int limit = 1;
        for (int i = 0; i < n; i++) {
            limit *= 10;
        }

        int count = 0;
        for (int num = 0; num < limit; num++) {
            if (hasUniqueDigits(num)) {
                count++;
            }
        }
        return count;
    }

    private boolean hasUniqueDigits(int num) {
        boolean[] seen = new boolean[10];

        // 0 is a valid number with unique digits
        if (num == 0) {
            return true;
        }

        while (num > 0) {
            int digit = num % 10;
            if (seen[digit]) {
                return false;
            }
            seen[digit] = true;
            num /= 10;
        }

        return true;
    }
}
```

---

## Complexity

Let `N = 10^n`.

For each number, checking digits takes `O(n)` in the worst case.

- **Time:** `O(10^n * n)`
- **Space:** `O(1)`
  (`boolean[10]` is constant size)

---

## Pros

- Very easy to understand
- Good as a correctness baseline
- Useful for testing small values

## Cons

- Exponential time
- Completely unnecessary given the small digit universe and the counting structure of the problem

---

# Approach 2: Backtracking / DFS Construction

## Idea

Instead of checking every number, build only valid numbers digit by digit.

At each step:

- choose a digit that has not been used yet,
- recurse,
- count every valid partial number.

This avoids exploring invalid numbers like `11`, `232`, `998`, etc.

---

## Important counting detail

The problem asks for the number of valid integers in:

```text
0 <= x < 10^n
```

So we need to count all valid numbers of length:

- `0` digits → the number `0`
- `1` digit
- `2` digits
- ...
- `n` digits

A neat way to model that is:

- start answer at `1` for the number `0`,
- start DFS from non-zero leading digits,
- every time we extend a valid number, increment the answer.

---

## Java Code

```java
public int countNumbersWithUniqueDigits(int n) {
    if (n == 0) return 1;

    boolean[] used = new boolean[10];
    int total = 1; // count "0"

    for (int first = 1; first <= 9; first++) {
        used[first] = true;
        total += dfs(n, 1, used);
        used[first] = false;
    }

    return total;
}

private int dfs(int n, int length, boolean[] used) {
    int count = 1; // current number

    if (length == n) return count;

    for (int digit = 0; digit <= 9; digit++) {
        if (!used[digit]) {
            used[digit] = true;
            count += dfs(n, length + 1, used);
            used[digit] = false;
        }
    }

    return count;
}
```

---

## Why this works

Suppose `n = 2`.

- Start with `1` for the number `0`
- DFS starts with leading digits `1..9`
- Each 1-digit number is counted
- Then each valid second digit is appended and counted

So we count exactly all valid numbers in `[0, 100)`.

---

## Complexity

The number of valid nodes explored is bounded by the number of unique-digit numbers of length up to `n`.

This is much smaller than brute force, but asymptotically still exponential in `n`.

A rough upper bound is:

- **Time:** `O(9 + 9*9 + 9*9*8 + 9*9*8*7 + ...)`, which is exponential
- **Space:** `O(n)` recursion depth + `O(10)` visited array
  So effectively **`O(n)`**

Because `n <= 8`, this is still practical.

---

## Pros

- Conceptually clean
- Generates only valid numbers
- Good bridge between brute force and mathematical counting

## Cons

- Still more expensive than necessary
- More implementation overhead than the combinatorics solution

---

# Approach 3: Dynamic Counting with Permutations (Most Standard)

## Idea

Count how many unique-digit numbers exist for each length directly.

Let:

- `f(1)` = number of unique-digit 1-digit numbers
- `f(2)` = number of unique-digit 2-digit numbers
- ...
- `f(k)` = number of unique-digit k-digit numbers

Then answer is:

```text
1 + f(1) + f(2) + ... + f(n)
```

The `1` is for the number `0`.

---

## Deriving the formula

### For 1-digit numbers

Digits allowed: `0..9`

All are unique automatically.

So:

```text
f(1) = 10
```

But another equally common formulation is:

- answer starts as `10` when `n >= 1`
- then build counts for length `2, 3, ...`

That is the cleanest implementation.

---

### For 2-digit numbers

- first digit: `1..9` → `9` choices
- second digit: any digit except the first → `9` choices
  (`0` is allowed here unless it was used already)

So:

```text
f(2) = 9 * 9 = 81
```

---

### For 3-digit numbers

- first digit: `9`
- second digit: `9`
- third digit: `8`

So:

```text
f(3) = 9 * 9 * 8
```

---

### In general

For length `k >= 2`:

```text
f(k) = 9 * 9 * 8 * 7 * ...
```

The first `9` is for the leading digit (`1..9`).

Then the remaining choices decrease because digits cannot repeat.

---

## Iterative DP-style View

We can maintain:

- `current` = number of valid unique-digit numbers of the current length
- `available` = how many unused digits remain for the next position

For example:

- length `1`: `current = 9`
- length `2`: `current *= 9`
- length `3`: `current *= 8`
- length `4`: `current *= 7`
- ...

Then keep adding `current` into the answer.

---

## Java Code

```java
class Solution {
    public int countNumbersWithUniqueDigits(int n) {
        if (n == 0) {
            return 1;
        }

        int answer = 10;   // all 1-digit numbers: 0..9
        int current = 9;   // ways for the first digit of multi-digit numbers
        int available = 9; // remaining choices for the next position

        for (int len = 2; len <= n; len++) {
            current *= available;
            answer += current;
            available--;
        }

        return answer;
    }
}
```

---

## Walkthrough for `n = 2`

Start:

- `answer = 10`
- `current = 9`
- `available = 9`

For `len = 2`:

- `current = 9 * 9 = 81`
- `answer = 10 + 81 = 91`
- `available = 8`

Return `91`.

---

## Walkthrough for `n = 3`

Start:

- `answer = 10`

For `len = 2`:

- add `9 * 9 = 81`
- `answer = 91`

For `len = 3`:

- add `9 * 9 * 8 = 648`
- `answer = 739`

So result is:

```text
739
```

---

## Complexity

Loop runs at most `n - 1` times.

- **Time:** `O(n)`
- **Space:** `O(1)`

This is the best practical solution.

---

## Pros

- Fastest and simplest among optimal solutions
- Directly uses the counting structure of the problem
- Very interview-friendly

## Cons

- Requires careful reasoning about counting choices
- Slightly less intuitive at first than brute force

---

# Approach 4: Top-Down DP / Memoized Digit Construction

## Idea

Use recursion with memoization to count how many numbers can be formed from a given state.

State can include:

- current position
- which digits are already used
- whether we have started the number yet

This is a classic **digit DP flavor**, though for this problem it is overkill because the upper bound is exactly `10^n`.

Still, it is valuable because it generalizes to harder problems such as:

- count numbers with unique digits in a custom range `[L, R]`
- count numbers satisfying digit constraints
- count numbers with no adjacent equal digits
- count numbers with bounded digit sum, etc.

---

## State definition

Let:

```text
dfs(pos, usedMask, started)
```

mean:

- we are choosing the digit for position `pos`,
- `usedMask` tells which digits are already used,
- `started` tells whether a non-leading digit has been placed yet.

If we reach `pos == n`, that corresponds to one valid number.

This naturally counts `0` as well via the path where we never start.

---

## Java Code

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    private Map<String, Integer> memo = new HashMap<>();

    public int countNumbersWithUniqueDigits(int n) {
        return dfs(0, n, 0, false);
    }

    private int dfs(int pos, int n, int usedMask, boolean started) {
        if (pos == n) {
            return 1;
        }

        String key = pos + "," + usedMask + "," + started;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        int total = 0;

        // Option 1: still skip placing a digit (leading zero zone)
        if (!started) {
            total += dfs(pos + 1, n, usedMask, false);
        }

        // Option 2: place a digit
        for (int digit = 0; digit <= 9; digit++) {
            if (!started && digit == 0) {
                continue; // handled by the "not started" branch
            }

            if ((usedMask & (1 << digit)) != 0) {
                continue;
            }

            total += dfs(pos + 1, n, usedMask | (1 << digit), true);
        }

        memo.put(key, total);
        return total;
    }
}
```

---

## Why this works

This recursion models the number as an `n`-position string:

- before the number “starts”, positions are just leading zero slots,
- once we place a non-zero digit, the number has started,
- then every next chosen digit must be unused,
- if we never start, that corresponds to the number `0`.

So it counts all valid numbers in `[0, 10^n)`.

---

## Complexity

The mask has at most `2^10 = 1024` possibilities.

State count is roughly:

```text
O(n * 2^10 * 2)
```

Transitions from each state try up to 10 digits.

So:

- **Time:** `O(n * 2^10 * 10)` → effectively `O(n)` with a bigger constant
- **Space:** `O(n * 2^10)`

For this problem, that is unnecessary but still perfectly fine.

---

## Pros

- Very general
- Helpful for learning digit DP / bitmask DP
- Extends naturally to range-based digit problems

## Cons

- Overengineered for this problem
- More code, more moving parts
- Harder to reason about than combinatorics

---

# Approach 5: Bottom-Up DP by Length

## Idea

This is basically the same mathematics as Approach 3, but expressed more explicitly as DP.

Let:

```text
dp[len] = number of unique-digit numbers with exactly len digits
```

Then:

- `dp[1] = 10`
- for `len >= 2`:
  - first digit has `9` choices
  - second digit has `9`
  - third has `8`
  - and so on

Then sum `dp[1]...dp[n]`, and include `dp[0] = 1` if we want to represent the number `0`.

This formulation is useful if you want the table itself for explanation or debugging.

---

## Java Code

```java
class Solution {
    public int countNumbersWithUniqueDigits(int n) {
        int[] dp = new int[Math.max(n + 1, 2)];
        dp[0] = 1;   // number 0
        dp[1] = 10;  // 0..9

        for (int len = 2; len <= n; len++) {
            int count = 9;
            int choices = 9;

            for (int pos = 2; pos <= len; pos++) {
                count *= choices;
                choices--;
            }

            dp[len] = count;
        }

        int answer = 0;
        for (int len = 0; len <= n; len++) {
            answer += dp[len];
        }

        // dp[1] already includes 0..9, while dp[0] also counted 0,
        // so this particular table definition double-counts 0.
        // To avoid that, return using a cleaner convention.
        // Keeping this code for demonstration only.
        return correctedAnswer(n);
    }

    private int correctedAnswer(int n) {
        if (n == 0) {
            return 1;
        }

        int answer = 10;
        int current = 9;
        int available = 9;

        for (int len = 2; len <= n; len++) {
            current *= available;
            answer += current;
            available--;
        }

        return answer;
    }
}
```

---

## Cleaner DP definition

To avoid the double-counting issue, define:

```text
dp[len] = number of unique-digit numbers with exactly len digits and no leading zero
```

Then:

- `answer = 1 + dp[1] + dp[2] + ... + dp[n]`
- `dp[1] = 9`
- `dp[2] = 9 * 9`
- `dp[3] = 9 * 9 * 8`
- ...

---

## Cleaner Java Code

```java
class Solution {
    public int countNumbersWithUniqueDigits(int n) {
        if (n == 0) {
            return 1;
        }

        int[] dp = new int[n + 1];
        dp[1] = 9;

        for (int len = 2; len <= n; len++) {
            dp[len] = dp[len - 1] * (11 - len);
        }

        int answer = 1; // number 0
        for (int len = 1; len <= n; len++) {
            answer += dp[len];
        }

        return answer;
    }
}
```

---

## Why the recurrence works

If:

```text
dp[len - 1] = 9 * 9 * 8 * ... * (11 - (len - 1))
```

then for the next digit there are exactly:

```text
11 - len
```

choices left.

Examples:

- `len = 2` → multiply by `9`
- `len = 3` → multiply by `8`
- `len = 4` → multiply by `7`

---

## Complexity

- **Time:** `O(n)`
- **Space:** `O(n)`

If you compress the DP state, it becomes the `O(1)` solution from Approach 3.

---

## Pros

- Nice if you want a table of counts by length
- Makes the recurrence explicit
- Good pedagogically

## Cons

- Slightly more verbose than Approach 3
- Extra array is unnecessary

---

# Approach 6: Pure Mathematical Closed-Form Style Summation

## Idea

Write the answer directly as:

```text
answer = 1 + 9 + 9*9 + 9*9*8 + 9*9*8*7 + ...
```

up to length `n`.

This is not really different algorithmically from Approach 3, but it helps sharpen the counting logic.

---

## Formula

For exact length `k >= 1`:

```text
count(k) = 9 * P(9, k - 1)
```

where `P(9, k - 1)` is the permutation count:

```text
P(9, k - 1) = 9 * 8 * 7 * ... * (10 - (k - 1))
```

Then:

```text
answer = 1 + Σ count(k), for k = 1 to n
```

---

## Java Code

```java
class Solution {
    public int countNumbersWithUniqueDigits(int n) {
        int answer = 1; // for 0

        for (int len = 1; len <= n; len++) {
            answer += countExactLength(len);
        }

        return answer;
    }

    private int countExactLength(int len) {
        if (len == 1) {
            return 9;
        }

        int count = 9;      // first digit
        int available = 9;  // remaining choices

        for (int i = 2; i <= len; i++) {
            count *= available;
            available--;
        }

        return count;
    }
}
```

---

## Complexity

- **Time:** `O(n^2)` in this exact implementation because each `countExactLength(len)` recomputes from scratch
- **Space:** `O(1)`

This can be optimized to `O(n)` by carrying the previous product forward, which is exactly Approach 3.

---

## Pros

- Very explicit mathematically
- Easy to reason about exact-length counts

## Cons

- Recomputes repeated work unless optimized
- Inferior to Approach 3 in implementation quality

---

# Best Approach

## Recommended: Approach 3

For this problem, the best solution is the **iterative combinatorics / DP counting** approach.

Why?

- It is optimal: `O(n)` time, `O(1)` space
- It directly matches the structure of the problem
- It is short and interview-ready
- It avoids the unnecessary overhead of recursion or full digit DP

---

## Final Recommended Java Solution

```java
class Solution {
    public int countNumbersWithUniqueDigits(int n) {
        if (n == 0) {
            return 1;
        }

        int answer = 10;   // valid numbers in [0, 10)
        int current = 9;   // ways to choose the first digit for multi-digit numbers
        int available = 9; // available digits for the next position

        for (int len = 2; len <= n; len++) {
            current *= available;
            answer += current;
            available--;
        }

        return answer;
    }
}
```

---

# Dry Run

## Example: `n = 2`

We want count of unique-digit numbers from `0` to `99`.

### 1-digit numbers

All valid:

```text
0..9 => 10 numbers
```

### 2-digit numbers

- first digit: `1..9` → `9`
- second digit: any digit except the first → `9`

```text
9 * 9 = 81
```

### Total

```text
10 + 81 = 91
```

---

## Example: `n = 0`

Range is:

```text
0 <= x < 1
```

Only `0` exists.

Answer:

```text
1
```

---

## Example: `n = 3`

- 1-digit: `10`
- 2-digit: `9 * 9 = 81`
- 3-digit: `9 * 9 * 8 = 648`

Total:

```text
10 + 81 + 648 = 739
```

---

# Common Mistakes

## 1. Forgetting that `0` is included

Many people start with `9` for 1-digit numbers and forget the standalone number `0`.

Correct handling:

- either start with `answer = 10` when `n >= 1`,
- or start with `answer = 1` and then add exact-length positive numbers.

---

## 2. Letting the first digit be zero in multi-digit numbers

For a 2-digit number, `"01"` is not a valid 2-digit number in normal integer representation.

So the first digit must be chosen from `1..9`.

---

## 3. Reusing digits incorrectly in counting

After choosing:

- first digit → 9 choices,
- second digit → 9 choices,
- third digit → 8 choices,

not always `9`.

The available choices shrink.

---

## 4. Confusing “exactly n digits” with “less than 10^n”

The problem asks for all numbers less than `10^n`, so you must count all lengths up to `n`, not only exact length `n`.

---

## 5. Overengineering with digit DP

Digit DP is powerful, but here the direct permutation counting is much simpler and cleaner.

---

# Interview Discussion

If asked in an interview, a strong answer is:

1. Start by clarifying the problem counts all numbers in `[0, 10^n)`.
2. Mention brute force first.
3. Then pivot to counting exact-length numbers combinatorially.
4. Explain:
   - first digit has 9 choices,
   - next has 9,
   - next has 8, etc.
5. Produce the `O(n)` / `O(1)` solution.

That shows both:

- practical problem-solving,
- and awareness of tradeoffs.

---

# Comparison Table

| Approach                | Idea                                          |                Time |         Space | Notes                                      |
| ----------------------- | --------------------------------------------- | ------------------: | ------------: | ------------------------------------------ |
| Brute Force Enumeration | Check every number in `[0, 10^n)`             |       `O(10^n * n)` |        `O(1)` | Simplest, but wasteful                     |
| Backtracking / DFS      | Build only valid numbers                      |         Exponential |        `O(n)` | Better than brute force, still unnecessary |
| Iterative Combinatorics | Count exact-length numbers directly           |              `O(n)` |        `O(1)` | Best solution                              |
| Top-Down DP / Digit DP  | Memoized state over positions and used digits |  `O(n * 2^10 * 10)` | `O(n * 2^10)` | Overkill but very general                  |
| Bottom-Up DP by Length  | Store counts per length                       |              `O(n)` |        `O(n)` | Clear recurrence, slightly more verbose    |
| Closed-Form Summation   | Sum permutation counts                        | `O(n^2)` as written |        `O(1)` | Good mathematically, but repeated work     |

---

# Final Takeaway

This problem looks like DP at first glance because it asks for a count over a numeric range, but its real structure is simpler:

- there are only 10 digits,
- uniqueness means choices shrink step by step,
- so the cleanest solution is just **permutation counting by digit length**.

That leads to the optimal solution:

- **Time:** `O(n)`
- **Space:** `O(1)`

and the key recurrence is:

```text
count(len) = count(len - 1) * availableChoices
```

with available choices decreasing from `9` to `8` to `7`, and so on.

---

# Practice Extension

A few useful follow-up questions to strengthen understanding:

1. How would you count numbers with unique digits in a range `[L, R]`?
2. How would you count numbers with exactly `n` digits and unique digits?
3. What changes if repeated digits are allowed but adjacent equal digits are forbidden?
4. How would you solve it for arbitrary upper bounds like `x <= 54321`?

Those variants naturally lead into **digit DP**, where Approach 4 becomes much more relevant.
