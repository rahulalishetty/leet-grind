# 3260. Find the Largest Palindrome Divisible by K — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public String largestPalindrome(int n, int k) {

    }
}
```

---

# Problem Restatement

We need the **largest `n`-digit palindrome** that is divisible by `k`.

Constraints:

- `1 <= n <= 10^5`
- `1 <= k <= 9`

The answer must be returned as a **string**.

Because `n` can be extremely large, we cannot construct huge numbers using ordinary integer types.

---

# Core Insight

A palindrome is determined by its left half.

But even that is too large to brute force when `n = 10^5`.

So we need to exploit the very small range of:

```text
k <= 9
```

That is the real simplifying factor.

Since divisibility depends only on the remainder modulo `k`, and `k` is tiny, we can build the palindrome digit-by-digit using **modular arithmetic**.

The main idea is:

- choose palindrome digits from outside inward
- always try the largest digit first
- keep track of the current remainder modulo `k`
- ensure that the unfinished middle can still complete to remainder `0`

This gives a digit-DP / greedy-with-reachability solution.

---

# Palindrome Contribution Formula

Suppose the palindrome has length `n`, indexed from `0` to `n - 1`.

If we place digit `d` at positions:

```text
i and n - 1 - i
```

then its contribution modulo `k` is:

```text
d * (10^(n-1-i) + 10^i) mod k
```

If `i == n - 1 - i` (middle digit of odd-length palindrome), the contribution is just:

```text
d * 10^i mod k
```

So each mirrored digit pair contributes a fixed remainder modulo `k`.

This makes the problem naturally suited for DP.

---

# Approach 1 — Greedy Construction + DP Reachability on Remainders (Recommended)

## Idea

Let:

```text
m = (n + 1) / 2
```

Only the first `m` digits need to be chosen; the rest are forced by symmetry.

We define a DP that tells us:

> from position `pos` onward, which remainders are achievable for the suffix of the palindrome construction?

Then while building the answer left to right, at each position we try digits from `9` down to `0` (except leading digit cannot be `0`), and choose the first one that still allows completion to a total remainder `0`.

Because we always take the largest valid digit, the resulting palindrome is lexicographically largest, hence numerically largest.

---

## DP State

Let:

```text
can[pos][rem] = whether positions pos..m-1 can be filled so that their total contribution modulo k is rem
```

We compute this backward.

Transition:

At position `pos`, try digit `d`, add its mirrored contribution modulo `k`, and transition to `pos + 1`.

Because `k <= 9`, the remainder space is tiny.

---

## Java Code

```java
import java.util.*;

class Solution {
    public String largestPalindrome(int n, int k) {
        int m = (n + 1) / 2;

        int[] pow10 = new int[n];
        pow10[0] = 1 % k;
        for (int i = 1; i < n; i++) {
            pow10[i] = (pow10[i - 1] * 10) % k;
        }

        int[] contrib = new int[m];
        for (int i = 0; i < m; i++) {
            int j = n - 1 - i;
            if (i == j) {
                contrib[i] = pow10[i] % k;
            } else {
                contrib[i] = (pow10[i] + pow10[j]) % k;
            }
        }

        boolean[][] can = new boolean[m + 1][k];
        can[m][0] = true;

        for (int pos = m - 1; pos >= 0; pos--) {
            for (int rem = 0; rem < k; rem++) {
                for (int d = 0; d <= 9; d++) {
                    int add = (d * contrib[pos]) % k;
                    int next = (rem - add) % k;
                    if (next < 0) next += k;

                    if (can[pos + 1][next]) {
                        can[pos][rem] = true;
                        break;
                    }
                }
            }
        }

        StringBuilder firstHalf = new StringBuilder();
        int remNeeded = 0;

        for (int pos = 0; pos < m; pos++) {
            int startDigit = (pos == 0 ? 9 : 9);
            int endDigit = (pos == 0 ? 1 : 0);

            for (int d = startDigit; d >= endDigit; d--) {
                int add = (d * contrib[pos]) % k;
                int next = (remNeeded - add) % k;
                if (next < 0) next += k;

                if (can[pos + 1][next]) {
                    firstHalf.append((char) ('0' + d));
                    remNeeded = next;
                    break;
                }
            }
        }

        StringBuilder ans = new StringBuilder(firstHalf);
        int mirrorStart = (n % 2 == 0 ? m - 1 : m - 2);

        for (int i = mirrorStart; i >= 0; i--) {
            ans.append(firstHalf.charAt(i));
        }

        return ans.toString();
    }
}
```

---

## Complexity

Let:

```text
m = (n + 1) / 2
```

Since each position tries at most 10 digits and `k <= 9`:

```text
Time:  O(n * k * 10) = O(n)
Space: O(n * k) = O(n)
```

This is fully efficient for `n = 10^5`.

---

# Approach 2 — BFS / Parent Reconstruction on Half Positions and Remainders

## Idea

This is another way to formulate the same DP.

Instead of only storing reachability, we can store the best digit choice and parent remainder while computing states.

That lets us reconstruct the answer afterward.

This is still essentially the same state graph:

- node = `(pos, rem)`
- edges = place digit `d`

Because we want the lexicographically largest answer, we process digits from `9` downward.

---

## Java Code

```java
import java.util.*;

class Solution {
    public String largestPalindrome(int n, int k) {
        int m = (n + 1) / 2;

        int[] pow10 = new int[n];
        pow10[0] = 1 % k;
        for (int i = 1; i < n; i++) {
            pow10[i] = (pow10[i - 1] * 10) % k;
        }

        int[] contrib = new int[m];
        for (int i = 0; i < m; i++) {
            int j = n - 1 - i;
            if (i == j) contrib[i] = pow10[i];
            else contrib[i] = (pow10[i] + pow10[j]) % k;
        }

        boolean[][] can = new boolean[m + 1][k];
        int[][] pick = new int[m][k];
        for (int[] row : pick) Arrays.fill(row, -1);

        can[m][0] = true;

        for (int pos = m - 1; pos >= 0; pos--) {
            for (int rem = 0; rem < k; rem++) {
                int low = (pos == 0 ? 1 : 0);
                for (int d = 9; d >= low; d--) {
                    int add = (d * contrib[pos]) % k;
                    int next = (rem - add) % k;
                    if (next < 0) next += k;

                    if (can[pos + 1][next]) {
                        can[pos][rem] = true;
                        pick[pos][rem] = d;
                        break;
                    }
                }
            }
        }

        StringBuilder firstHalf = new StringBuilder();
        int rem = 0;
        for (int pos = 0; pos < m; pos++) {
            int d = pick[pos][rem];
            firstHalf.append((char) ('0' + d));
            int add = (d * contrib[pos]) % k;
            rem = (rem - add) % k;
            if (rem < 0) rem += k;
        }

        StringBuilder ans = new StringBuilder(firstHalf);
        int mirrorStart = (n % 2 == 0 ? m - 1 : m - 2);
        for (int i = mirrorStart; i >= 0; i--) {
            ans.append(firstHalf.charAt(i));
        }

        return ans.toString();
    }
}
```

---

## Complexity

Same as Approach 1:

```text
Time:  O(n)
Space: O(n)
```

---

# Approach 3 — Brute Force Over Half Palindromes (Conceptual Only)

## Idea

A palindrome of length `n` is determined by its first half.

So one naive thought is:

1. enumerate the first half from largest to smallest
2. mirror it into a palindrome
3. test divisibility by `k`
4. return the first success

---

## Why it fails

If `n = 10^5`, the first half has length about `50000`.

That means the number of possible candidates is astronomically large.

So brute force is completely infeasible.

---

# Approach 4 — Search Downward From the Largest n-Digit Palindrome (Conceptual Only)

## Idea

Another tempting idea is:

- start from the largest `n`-digit palindrome
- move downward through palindromes
- check divisibility by `k`

---

## Why it fails

The search space is still enormous, and decrementing palindromes efficiently does not fix the fundamental exponential explosion.

So we need the remainder-based constructive approach instead.

---

# Why Greedy Alone Is Not Enough

You might think:

> always place the largest possible digit at each mirrored position

But that only works if we also know the suffix can still complete to divisibility by `k`.

So pure greedy is not enough.

What makes the solution correct is:

- greedy choice of the digit
- backed by a DP feasibility check on remaining positions

That is the important combination.

---

# Detailed Walkthrough

## Example 1

```text
n = 3, k = 5
```

A 3-digit palindrome looks like:

```text
aba
```

We want the largest such number divisible by 5.

Divisibility by 5 means the last digit must be:

```text
0 or 5
```

But leading zero is forbidden, and since the number is a palindrome, first digit = last digit.

So the outer digits must be `5`.

Now the largest middle digit is `9`.

So the answer is:

```text
595
```

---

## Example 2

```text
n = 1, k = 4
```

Single-digit palindromes are just:

```text
1,2,3,4,5,6,7,8,9
```

Among those divisible by 4:

```text
4,8
```

Largest is:

```text
8
```

---

## Example 3

```text
n = 5, k = 6
```

A number divisible by 6 must be divisible by both:

- 2
- 3

Divisible by 2 means the last digit is even.
Since it is a palindrome, the first digit must equal the last digit, so the outer digit must be an even nonzero digit.

The DP-guided greedy construction finds the lexicographically largest palindrome satisfying the modulo condition, which is:

```text
89898
```

---

# Important Correctness Argument

At position `pos`, suppose we try digits from `9` downward.

We choose the first digit `d` such that the remaining positions can still complete the palindrome to total remainder `0`.

Why is that correct?

Because:

- all earlier positions are already fixed identically across candidate solutions
- among candidates differing first at this position, the one with larger digit is numerically larger
- if a larger digit allows completion, it is always better than any smaller digit

So the DP feasibility check ensures correctness, and greedy ensures maximality.

---

# Common Pitfalls

## 1. Using integer types for the full palindrome

The palindrome may have up to `10^5` digits, so it must be handled as a string / character array.

---

## 2. Forgetting the middle position contribution for odd `n`

If `i == n - 1 - i`, that position contributes only once, not twice.

---

## 3. Allowing leading zero

The first digit must be from `1..9`.

---

## 4. Assuming brute force over palindromes is feasible

It is not. The half-space is far too large.

---

# Best Approach

## Recommended: Greedy digit construction with DP reachability on modulo states

This is the best solution because:

- `k` is tiny
- modulo states are very small
- the palindrome structure reduces the degrees of freedom to half the positions
- DP guarantees feasibility
- greedy guarantees the largest answer

---

# Final Recommended Java Solution

```java
import java.util.*;

class Solution {
    public String largestPalindrome(int n, int k) {
        int m = (n + 1) / 2;

        int[] pow10 = new int[n];
        pow10[0] = 1 % k;
        for (int i = 1; i < n; i++) {
            pow10[i] = (pow10[i - 1] * 10) % k;
        }

        int[] contrib = new int[m];
        for (int i = 0; i < m; i++) {
            int j = n - 1 - i;
            if (i == j) {
                contrib[i] = pow10[i] % k;
            } else {
                contrib[i] = (pow10[i] + pow10[j]) % k;
            }
        }

        boolean[][] can = new boolean[m + 1][k];
        can[m][0] = true;

        for (int pos = m - 1; pos >= 0; pos--) {
            for (int rem = 0; rem < k; rem++) {
                int low = (pos == 0 ? 1 : 0);
                for (int d = low; d <= 9; d++) {
                    int add = (d * contrib[pos]) % k;
                    int next = (rem - add) % k;
                    if (next < 0) next += k;

                    if (can[pos + 1][next]) {
                        can[pos][rem] = true;
                        break;
                    }
                }
            }
        }

        StringBuilder firstHalf = new StringBuilder();
        int remNeeded = 0;

        for (int pos = 0; pos < m; pos++) {
            int low = (pos == 0 ? 1 : 0);

            for (int d = 9; d >= low; d--) {
                int add = (d * contrib[pos]) % k;
                int next = (remNeeded - add) % k;
                if (next < 0) next += k;

                if (can[pos + 1][next]) {
                    firstHalf.append((char) ('0' + d));
                    remNeeded = next;
                    break;
                }
            }
        }

        StringBuilder ans = new StringBuilder(firstHalf);
        int mirrorStart = (n % 2 == 0 ? m - 1 : m - 2);

        for (int i = mirrorStart; i >= 0; i--) {
            ans.append(firstHalf.charAt(i));
        }

        return ans.toString();
    }
}
```

---

# Complexity Summary

Because `k <= 9`, the DP state space is tiny.

```text
Time:  O(n)
Space: O(n)
```

This is efficient even for:

```text
n = 10^5
```

---

# Final Takeaway

The crucial reduction is:

- a palindrome is determined by its first half
- divisibility depends only on remainder modulo `k`
- `k` is tiny

So we can construct the answer digit-by-digit, using DP to ensure the suffix can finish the divisibility requirement, and greedy to make the palindrome as large as possible.
