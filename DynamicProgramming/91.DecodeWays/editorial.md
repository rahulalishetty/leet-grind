# 91. Decode Ways — Exhaustive Approach Summary (Java)

## Problem restatement

You are given a digit string `s`. Each valid chunk maps as:

- `"1"` → `A`
- `"2"` → `B`
- ...
- `"26"` → `Z`

You must count how many **valid decodings** exist for the whole string.

A decoding is valid only when:

- A single digit is between `'1'` and `'9'`
- A two-digit number is between `"10"` and `"26"`
- `"0"` can **never** stand alone
- Leading zero in a chunk is invalid, so `"06"` is invalid

---

## Core observation

At any index `i`, there are only two meaningful choices:

1. Decode `s[i]` alone, if it is between `'1'` and `'9'`
2. Decode `s[i..i+1]` together, if it is between `"10"` and `"26"`

That naturally leads to a recurrence:

```text
ways(i) =
    0, if s[i] == '0'
    ways(i+1), if s[i] is valid alone
    + ways(i+2), if s[i..i+1] is valid together
```

Base case:

```text
ways(n) = 1
```

That means: once you successfully consume the whole string, that contributes one valid decoding.

---

# Approach 1 — Plain recursion (top-down brute force)

## Idea

At each position, try:

- taking one digit
- taking two digits if valid

Then recursively count the ways for the remaining suffix.

This is the most direct way to think about the problem.

---

## Recursive state

Let:

```java
decode(i) = number of ways to decode substring s[i...n-1]
```

Then:

- if `i == n`, return `1`
- if `s.charAt(i) == '0'`, return `0`
- otherwise:
  - take one digit → `decode(i + 1)`
  - if two-digit value is valid → add `decode(i + 2)`

---

## Java code

```java
class Solution {
    public int numDecodings(String s) {
        return decode(s, 0);
    }

    private int decode(String s, int i) {
        if (i == s.length()) {
            return 1;
        }

        if (s.charAt(i) == '0') {
            return 0;
        }

        int ways = decode(s, i + 1);

        if (i + 1 < s.length() && isValidTwoDigit(s, i)) {
            ways += decode(s, i + 2);
        }

        return ways;
    }

    private boolean isValidTwoDigit(String s, int i) {
        char first = s.charAt(i);
        char second = s.charAt(i + 1);

        return first == '1' || (first == '2' && second <= '6');
    }
}
```

---

## Time complexity

In the worst case, this is **exponential**:

```text
O(2^n)
```

Reason: many states are recomputed again and again.

Example: for `"111111..."`, each index branches into two recursive calls, causing a Fibonacci-style explosion.

---

## Space complexity

```text
O(n)
```

For recursion stack depth.

---

## Strengths

- Very intuitive
- Best for deriving the recurrence

## Weaknesses

- Too slow for larger inputs
- Repeats the same work many times

---

# Approach 2 — Top-down DP with memoization

## Idea

The brute force recursion is correct, but inefficient because the same index is solved repeatedly.

Since the answer from index `i` depends only on suffix `s[i...]`, we can memoize `decode(i)`.

This converts the exponential solution into a linear one.

---

## State definition

```java
memo[i] = number of ways to decode substring s[i...n-1]
```

Use `-1` to mean “not computed yet”.

---

## Transition

Same recurrence as recursion:

```text
memo[i] =
    0                                if s[i] == '0'
    memo[i+1]                        if single digit valid
    + memo[i+2]                      if two-digit valid
```

Base case:

```text
memo[n] = 1
```

Or equivalently handle `i == n` directly in recursion.

---

## Java code

```java
import java.util.Arrays;

class Solution {
    public int numDecodings(String s) {
        int[] memo = new int[s.length()];
        Arrays.fill(memo, -1);
        return decode(s, 0, memo);
    }

    private int decode(String s, int i, int[] memo) {
        if (i == s.length()) {
            return 1;
        }

        if (s.charAt(i) == '0') {
            return 0;
        }

        if (memo[i] != -1) {
            return memo[i];
        }

        int ways = decode(s, i + 1, memo);

        if (i + 1 < s.length() && isValidTwoDigit(s, i)) {
            ways += decode(s, i + 2, memo);
        }

        memo[i] = ways;
        return ways;
    }

    private boolean isValidTwoDigit(String s, int i) {
        char first = s.charAt(i);
        char second = s.charAt(i + 1);

        return first == '1' || (first == '2' && second <= '6');
    }
}
```

---

## Time complexity

```text
O(n)
```

Each index `i` is solved once.

---

## Space complexity

```text
O(n)
```

- `O(n)` for memo array
- `O(n)` recursion stack

Overall still `O(n)` auxiliary space.

---

## Why this is usually the best learning approach

This is often the clearest **interview explanation**:

1. Start with recursion
2. Notice repeated subproblems
3. Add memoization
4. Then convert to bottom-up if needed

That shows strong problem-solving flow.

---

# Approach 3 — Bottom-up DP (tabulation)

## Idea

Instead of solving from the front recursively, solve suffixes from the back iteratively.

If:

```java
dp[i] = number of ways to decode substring s[i...n-1]
```

then:

- `dp[n] = 1`
- fill `dp` from right to left

---

## Why right-to-left?

Because `dp[i]` depends on:

- `dp[i + 1]`
- `dp[i + 2]`

So those must already be known.

---

## Transition

For each index `i`:

- if `s.charAt(i) == '0'`, then `dp[i] = 0`
- else:
  - `dp[i] += dp[i + 1]`
  - if two-digit valid, `dp[i] += dp[i + 2]`

---

## Java code

```java
class Solution {
    public int numDecodings(String s) {
        int n = s.length();
        int[] dp = new int[n + 1];

        dp[n] = 1; // empty suffix

        for (int i = n - 1; i >= 0; i--) {
            if (s.charAt(i) == '0') {
                dp[i] = 0;
                continue;
            }

            dp[i] = dp[i + 1];

            if (i + 1 < n && isValidTwoDigit(s, i)) {
                dp[i] += dp[i + 2];
            }
        }

        return dp[0];
    }

    private boolean isValidTwoDigit(String s, int i) {
        char first = s.charAt(i);
        char second = s.charAt(i + 1);

        return first == '1' || (first == '2' && second <= '6');
    }
}
```

---

## Time complexity

```text
O(n)
```

---

## Space complexity

```text
O(n)
```

For the DP array.

---

## Strengths

- No recursion stack
- Often preferred in production because it is iterative
- Easy to reason about once recurrence is known

## Weaknesses

- Slightly less intuitive initially than memoized recursion

---

# Approach 4 — Space-optimized bottom-up DP

## Key observation

At index `i`, we only need:

- `dp[i + 1]`
- `dp[i + 2]`

So we do **not** need the full array.

We can keep just two rolling variables.

---

## Variable meaning

Let:

- `next1 = dp[i + 1]`
- `next2 = dp[i + 2]`

Then compute current answer `curr = dp[i]`.

After processing index `i`, shift:

- `next2 = next1`
- `next1 = curr`

---

## Java code

```java
class Solution {
    public int numDecodings(String s) {
        int n = s.length();

        int next1 = 1; // dp[n]
        int next2 = 0; // conceptually dp[n+1], only needed for shifting

        for (int i = n - 1; i >= 0; i--) {
            int curr;

            if (s.charAt(i) == '0') {
                curr = 0;
            } else {
                curr = next1;

                if (i + 1 < n && isValidTwoDigit(s, i)) {
                    curr += (i + 2 <= n) ? next2OrBase(i, n, next2) : 0;
                }
            }

            next2 = next1;
            next1 = curr;
        }

        return next1;
    }

    private boolean isValidTwoDigit(String s, int i) {
        char first = s.charAt(i);
        char second = s.charAt(i + 1);

        return first == '1' || (first == '2' && second <= '6');
    }

    private int next2OrBase(int i, int n, int next2) {
        // when i + 2 == n, dp[n] should be 1
        // but due to shifting logic, next2 already represents dp[i+2]
        return next2;
    }
}
```

The above version works, but the shifting logic is a bit harder to read cleanly.

A cleaner implementation is below.

---

## Cleaner space-optimized Java code

```java
class Solution {
    public int numDecodings(String s) {
        int n = s.length();

        int dp1 = 1; // dp[i+1], initially dp[n]
        int dp2 = 0; // dp[i+2] placeholder

        for (int i = n - 1; i >= 0; i--) {
            int curr = 0;

            if (s.charAt(i) != '0') {
                curr = dp1;

                if (i + 1 < n && isValidTwoDigit(s, i)) {
                    curr += (i + 2 == n) ? 1 : dp2;
                }
            }

            dp2 = dp1;
            dp1 = curr;
        }

        return dp1;
    }

    private boolean isValidTwoDigit(String s, int i) {
        char first = s.charAt(i);
        char second = s.charAt(i + 1);

        return first == '1' || (first == '2' && second <= '6');
    }
}
```

---

## Time complexity

```text
O(n)
```

## Space complexity

```text
O(1)
```

This is the optimal DP solution in terms of extra space.

---

## Important subtlety

The handling of `dp[i + 2]` when `i + 2 == n` must contribute `1`, because consuming exactly the whole string is one valid decoding path.

That is why the line:

```java
curr += (i + 2 == n) ? 1 : dp2;
```

is important.

---

# Approach 5 — Forward DP (prefix-based tabulation)

## Idea

So far, we used:

```text
dp[i] = ways to decode suffix starting at i
```

We can also define DP over prefixes:

```text
dp[i] = ways to decode first i characters
```

This is another very standard formulation.

---

## State definition

Let `dp[i]` mean:

> number of ways to decode substring `s[0..i-1]`

So:

- `dp[0] = 1` → empty prefix has one way
- Answer is `dp[n]`

---

## Transition

At position `i` (1-based in DP):

### Single-digit contribution

If `s[i-1]` is between `'1'` and `'9'`:

```text
dp[i] += dp[i-1]
```

### Two-digit contribution

If `s[i-2..i-1]` is between `"10"` and `"26"`:

```text
dp[i] += dp[i-2]
```

---

## Java code

```java
class Solution {
    public int numDecodings(String s) {
        int n = s.length();
        int[] dp = new int[n + 1];

        dp[0] = 1;

        for (int i = 1; i <= n; i++) {
            char one = s.charAt(i - 1);

            if (one != '0') {
                dp[i] += dp[i - 1];
            }

            if (i >= 2 && isValidTwoDigit(s.charAt(i - 2), s.charAt(i - 1))) {
                dp[i] += dp[i - 2];
            }
        }

        return dp[n];
    }

    private boolean isValidTwoDigit(char first, char second) {
        return first == '1' || (first == '2' && second <= '6');
    }
}
```

---

## Time complexity

```text
O(n)
```

## Space complexity

```text
O(n)
```

---

## Why many people like this formulation

This style mirrors many classic DP problems:

- word break
- climbing stairs
- partition/counting problems

It often feels more natural if you think in terms of “how many ways to build up to position `i`”.

---

# Approach 6 — Forward DP with O(1) space

Since `dp[i]` only depends on:

- `dp[i - 1]`
- `dp[i - 2]`

we can compress the DP.

---

## Java code

```java
class Solution {
    public int numDecodings(String s) {
        int n = s.length();

        int prev2 = 1; // dp[0]
        int prev1 = (s.charAt(0) == '0') ? 0 : 1; // dp[1]

        for (int i = 2; i <= n; i++) {
            int curr = 0;

            char one = s.charAt(i - 1);
            char first = s.charAt(i - 2);
            char second = s.charAt(i - 1);

            if (one != '0') {
                curr += prev1;
            }

            if (isValidTwoDigit(first, second)) {
                curr += prev2;
            }

            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }

    private boolean isValidTwoDigit(char first, char second) {
        return first == '1' || (first == '2' && second <= '6');
    }
}
```

---

## Time complexity

```text
O(n)
```

## Space complexity

```text
O(1)
```

---

# Detailed dry runs

## Example 1: `s = "12"`

Valid decodings:

- `1,2` → `AB`
- `12` → `L`

Answer = `2`

### Suffix DP view

```text
dp[2] = 1
dp[1] = 1    // "2"
dp[0] = dp[1] + dp[2] = 2
```

---

## Example 2: `s = "226"`

Valid decodings:

- `2,2,6` → `BBF`
- `22,6` → `VF`
- `2,26` → `BZ`

Answer = `3`

### Suffix DP

```text
dp[3] = 1
dp[2] = 1         // "6"
dp[1] = dp[2] + dp[3] = 2   // "2" and "26"
dp[0] = dp[1] + dp[2] = 3   // "2" and "22"
```

---

## Example 3: `s = "06"`

At index 0, `'0'` cannot be decoded alone.

So answer = `0`

---

## Example 4: `s = "11106"`

This is the classic tricky case.

Valid:

- `1,1,10,6`
- `11,10,6`

Invalid:

- `1,11,06` because `"06"` is invalid

### Suffix DP walkthrough

String indices:

```text
0 1 2 3 4
1 1 1 0 6
```

Now compute from right to left:

- `dp[5] = 1`
- `dp[4] = 1` because `"6"` valid
- `dp[3] = 0` because `"0"` invalid alone
- `dp[2] = dp[3] + dp[4] = 1` because `"1"` valid and `"10"` valid
- `dp[1] = dp[2] + dp[3] = 1` because `"11"` valid but `dp[3]=0`
- `dp[0] = dp[1] + dp[2] = 2`

Answer = `2`

---

# Edge cases you must understand

## 1. Leading zero

### Input

```text
"06"
```

### Result

```text
0
```

Because `"06"` is not the same as `"6"`.

---

## 2. Isolated zero

### Input

```text
"30"
```

### Result

```text
0
```

Why?

- `"3"` is valid
- but remaining `"0"` is invalid
- `"30"` is not between `10` and `26`

So no valid decoding.

---

## 3. Zero only works as part of 10 or 20

### Inputs

```text
"10" -> 1
"20" -> 1
"100" -> 0
"101" -> 1
```

Reason:

- `"10"` → `J`
- `"20"` → `T`
- `"100"` cannot become `"10", "0"`
- `"101"` only works as `"10", "1"`

---

## 4. Two-digit validity is not just “<= 26”

This is a common bug.

For example:

```text
"06" <= 26 numerically
```

but it is still invalid.

So you must enforce:

- first digit must not be `'0'`
- valid pairs are exactly `"10"` through `"26"`

A robust character check is usually better than parsing integers repeatedly.

---

# Common mistakes

## Mistake 1 — Treating `'0'` as a valid single digit

Wrong.

`'0'` has no mapping by itself.

---

## Mistake 2 — Allowing `"06"`

Wrong because of leading zero.

---

## Mistake 3 — Forgetting base case `ways(n) = 1`

This base case is essential.

When you successfully consume the whole string, that is one valid path.

Without this, counts collapse incorrectly.

---

## Mistake 4 — Parsing substrings repeatedly

You may see code like:

```java
int value = Integer.parseInt(s.substring(i, i + 2));
```

This works, but it is less efficient and less clean than direct char comparison.

Prefer:

```java
first == '1' || (first == '2' && second <= '6')
```

---

## Mistake 5 — Wrong initialization in forward DP

For prefix DP:

- `dp[0]` must be `1`
- `dp[1]` depends on whether first character is `'0'`

These initializations matter a lot.

---

# Comparing all approaches

| Approach               | Idea                                        |     Time |  Space | Notes                        |
| ---------------------- | ------------------------------------------- | -------: | -----: | ---------------------------- |
| Plain recursion        | Try 1-digit and 2-digit choices recursively | `O(2^n)` | `O(n)` | Best for deriving recurrence |
| Memoized recursion     | Cache answer for each index                 |   `O(n)` | `O(n)` | Best learning path           |
| Bottom-up DP (suffix)  | Fill `dp[i]` from right to left             |   `O(n)` | `O(n)` | Iterative and clean          |
| Bottom-up DP optimized | Keep only next two states                   |   `O(n)` | `O(1)` | Optimal extra space          |
| Forward DP (prefix)    | Build answer from left to right             |   `O(n)` | `O(n)` | Very standard DP style       |
| Forward DP optimized   | Keep only previous two values               |   `O(n)` | `O(1)` | Compact and efficient        |

---

# Which approach should you use in interviews?

A strong sequence is:

1. Explain plain recursion
2. Show the repeated-subproblem issue
3. Convert to memoization
4. Mention bottom-up optimization

That demonstrates:

- pattern recognition
- recurrence design
- DP conversion skill
- optimization awareness

If asked for final production-grade code, the **O(1) space iterative DP** is usually the best answer.

---

# Recommended final interview solution (cleanest practical version)

This version is compact, efficient, and readable.

```java
class Solution {
    public int numDecodings(String s) {
        int n = s.length();

        int prev2 = 1; // dp[i-2]
        int prev1 = s.charAt(0) == '0' ? 0 : 1; // dp[i-1]

        for (int i = 2; i <= n; i++) {
            int curr = 0;

            char one = s.charAt(i - 1);
            if (one != '0') {
                curr += prev1;
            }

            char first = s.charAt(i - 2);
            char second = s.charAt(i - 1);
            if (first == '1' || (first == '2' && second <= '6')) {
                curr += prev2;
            }

            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }
}
```

---

# Why this final solution works

For each position `i`:

- If the current digit can stand alone, append it to all decodings of prefix `i-1`
- If the last two digits form a valid letter, append that to all decodings of prefix `i-2`

So each valid construction route is counted exactly once.

---

# Interview explanation in compact form

You can explain it like this:

> Let `dp[i]` be the number of ways to decode the first `i` characters.
> If `s[i-1] != '0'`, then the last digit can be decoded alone, so add `dp[i-1]`.
> If the last two digits form a number from `10` to `26`, then they can be decoded together, so add `dp[i-2]`.
> Initialize `dp[0] = 1`, and build the answer left to right.

That is usually enough for a strong explanation.

---

# Final takeaway

This problem is fundamentally a **counting DP on a string**.

The real difficulty is not the recurrence itself, but handling invalid zero cases correctly:

- `'0'` alone is invalid
- only `"10"` and `"20"` allow zero
- `"06"` is invalid

Once that is handled, the DP becomes straightforward.

---

# Practice extensions

After mastering this problem, the next useful variants are:

- **Decode Ways II** (with `*` wildcard)
- word-break style string DP problems
- climbing-stairs style counting transitions
- partition/counting DP on strings

These reinforce the same recurrence-building instinct.
