# 2557. Maximum Number of Integers to Choose From a Range II — Java Solutions and Detailed Notes

## Problem

We are given:

- an integer array `banned`,
- an integer `n`,
- a long `maxSum`.

We want to choose as many integers as possible such that:

1. each chosen integer is in:

```text
[1, n]
```

2. each integer is chosen at most once,
3. chosen integers are not in `banned`,
4. total sum of chosen integers is at most `maxSum`.

We must return the **maximum count** of integers we can choose.

```java
class Solution {
    public int maxCount(int[] banned, int n, long maxSum) {

    }
}
```

---

# Core greedy insight

To maximize the **count** of chosen integers under a sum limit, we should always prefer the **smallest available integers**.

Why?

If you replace a smaller chosen number with a larger one, the total sum increases, which can only hurt your ability to choose more numbers.

So the optimal strategy is:

> Choose valid numbers in increasing order, skipping banned values, until the sum budget runs out.

That greedy principle is the backbone of all correct solutions.

---

# Why the naïve scan from 1 to n is not enough

A straightforward idea is:

- put banned numbers into a set,
- iterate from `1` to `n`,
- if current number is not banned and fits in the remaining budget, take it.

This works logically, but `n` can be:

```text
10^9
```

so scanning every integer is impossible.

We need to exploit the fact that:

- `banned.length <= 10^5`
- only a relatively small number of blocked points matter.

That suggests processing **intervals of allowed numbers** instead of individual numbers.

---

# Arithmetic sum formula

If we want the sum of integers from `L` to `R`:

```text
L + (L+1) + ... + R
```

the formula is:

```text
(R - L + 1) * (L + R) / 2
```

We will use this repeatedly.

---

# Approach 1: Greedy Scan with HashSet (Correct but too slow for worst case)

## Idea

This is the most direct solution.

1. Put all banned numbers into a set.
2. Iterate from `1` to `n`.
3. If a number is not banned and fits in the remaining sum, choose it.

This is useful as a conceptual baseline, but not acceptable for `n = 10^9`.

---

## Java code

```java
import java.util.*;

class Solution {
    public int maxCount(int[] banned, int n, long maxSum) {
        Set<Integer> bannedSet = new HashSet<>();
        for (int b : banned) {
            if (b >= 1 && b <= n) {
                bannedSet.add(b);
            }
        }

        int count = 0;
        long sum = 0;

        for (int x = 1; x <= n; x++) {
            if (bannedSet.contains(x)) {
                continue;
            }

            if (sum + x > maxSum) {
                break;
            }

            sum += x;
            count++;
        }

        return count;
    }
}
```

---

## Complexity

Time complexity:

```text
O(n + banned.length)
```

Space complexity:

```text
O(banned.length)
```

---

## Verdict

Correct, but too slow when `n` is huge.

---

# Approach 2: Sort banned and process allowed intervals greedily (Optimal and intended)

## Main idea

Since we want the smallest valid integers, imagine the numbers from `1` to `n`.

The banned values split this range into **allowed intervals**.

Example:

```text
n = 10
banned = [2, 5, 8]
```

Allowed intervals are:

```text
[1,1], [3,4], [6,7], [9,10]
```

Instead of checking numbers one by one, we process each allowed interval in bulk.

For each interval:

- if we can afford the whole interval, take it all,
- otherwise, take only the maximum prefix of that interval that fits into `maxSum`.

Because numbers are increasing globally, this is exactly what greedy wants.

---

## Step 1: Clean banned values

Some banned values may be:

- duplicated,
- outside `[1, n]`.

They do not matter more than once, so we:

1. sort `banned`,
2. ignore duplicates,
3. ignore values outside `[1, n]`.

---

## Step 2: Build allowed intervals implicitly

If the previous banned value is `prev`, and the current banned value is `b`, then the allowed interval is:

```text
[prev + 1, b - 1]
```

Also handle:

- the prefix before the first banned value,
- the suffix after the last banned value.

---

## Step 3: For each allowed interval, decide how many numbers to take

Suppose current allowed interval is:

```text
[L, R]
```

There are two cases.

### Case A: Can take all numbers in `[L, R]`

Compute:

```text
sum(L..R)
```

If that is within remaining budget, take all of them.

### Case B: Cannot take all

Then we need the maximum `x` such that taking:

```text
[L, L+1, ..., x]
```

still fits.

This is again a prefix of the interval, because greedy always takes the smallest available numbers first.

We can find `x` by binary search.

---

## Sum of prefix `[L..x]`

```text
count = x - L + 1
sum = count * (L + x) / 2
```

We need the largest `x` such that:

```text
sum <= remainingBudget
```

---

# Java solution (interval processing + binary search inside interval)

```java
import java.util.*;

class Solution {
    public int maxCount(int[] banned, int n, long maxSum) {
        Arrays.sort(banned);

        long remaining = maxSum;
        int answer = 0;

        int prev = 0;  // previous banned number inside range [1..n]

        for (int i = 0; i < banned.length; i++) {
            int b = banned[i];

            if (b < 1 || b > n) {
                continue;
            }

            if (b == prev) {
                continue; // skip duplicates
            }

            int left = prev + 1;
            int right = b - 1;

            answer += takeFromInterval(left, right, remaining);
            remaining -= usedSum(left, right, remaining);

            if (remaining == 0) {
                return answer;
            }

            prev = b;
        }

        // tail interval after the last banned number
        int left = prev + 1;
        int right = n;

        answer += takeFromInterval(left, right, remaining);

        return answer;
    }

    private int takeFromInterval(int left, int right, long budget) {
        if (left > right || budget <= 0) {
            return 0;
        }

        long total = rangeSum(left, right);
        if (total <= budget) {
            return right - left + 1;
        }

        int lo = left, hi = right;
        while (lo < hi) {
            int mid = lo + (hi - lo + 1) / 2;
            if (rangeSum(left, mid) <= budget) {
                lo = mid;
            } else {
                hi = mid - 1;
            }
        }

        if (rangeSum(left, lo) <= budget) {
            return lo - left + 1;
        }
        return 0;
    }

    private long usedSum(int left, int right, long budget) {
        if (left > right || budget <= 0) {
            return 0L;
        }

        long total = rangeSum(left, right);
        if (total <= budget) {
            return total;
        }

        int lo = left, hi = right;
        while (lo < hi) {
            int mid = lo + (hi - lo + 1) / 2;
            if (rangeSum(left, mid) <= budget) {
                lo = mid;
            } else {
                hi = mid - 1;
            }
        }

        long best = rangeSum(left, lo);
        return best <= budget ? best : 0L;
    }

    private long rangeSum(long left, long right) {
        long count = right - left + 1;
        return count * (left + right) / 2;
    }
}
```

---

## Note on the above implementation

The logic is correct, but it performs two binary searches per interval:

- one to compute count,
- one to compute used sum.

We can simplify that.

---

# Approach 3: Sort banned and process intervals with a single helper (recommended implementation)

## Idea

Same interval-based greedy approach, but cleaner.

For each allowed interval `[L, R]`:

1. if whole interval fits, take all,
2. otherwise binary search the largest prefix endpoint `X`,
3. take exactly:

```text
X - L + 1
```

and consume exactly:

```text
sum(L..X)
```

This is the cleanest optimal solution.

---

## Java code

```java
import java.util.*;

class Solution {
    public int maxCount(int[] banned, int n, long maxSum) {
        Arrays.sort(banned);

        long remaining = maxSum;
        int answer = 0;
        int prev = 0;

        for (int i = 0; i < banned.length; i++) {
            int b = banned[i];

            if (b < 1 || b > n) {
                continue;
            }
            if (b == prev) {
                continue;
            }

            int left = prev + 1;
            int right = b - 1;

            Result res = consumeInterval(left, right, remaining);
            answer += res.count;
            remaining -= res.sumUsed;

            if (remaining == 0) {
                return answer;
            }

            prev = b;
        }

        Result tail = consumeInterval(prev + 1, n, remaining);
        answer += tail.count;

        return answer;
    }

    private Result consumeInterval(int left, int right, long budget) {
        if (left > right || budget <= 0) {
            return new Result(0, 0L);
        }

        long whole = rangeSum(left, right);
        if (whole <= budget) {
            return new Result(right - left + 1, whole);
        }

        int lo = left, hi = right;
        while (lo < hi) {
            int mid = lo + (hi - lo + 1) / 2;
            if (rangeSum(left, mid) <= budget) {
                lo = mid;
            } else {
                hi = mid - 1;
            }
        }

        long bestSum = rangeSum(left, lo);
        if (bestSum <= budget) {
            return new Result(lo - left + 1, bestSum);
        }
        return new Result(0, 0L);
    }

    private long rangeSum(long left, long right) {
        long count = right - left + 1;
        return count * (left + right) / 2;
    }

    private static class Result {
        int count;
        long sumUsed;

        Result(int count, long sumUsed) {
            this.count = count;
            this.sumUsed = sumUsed;
        }
    }
}
```

---

## Complexity

Let `m = banned.length`.

Time complexity:

```text
O(m log m + m log n)
```

Why?

- sort banned: `O(m log m)`
- process at most `m + 1` intervals
- each interval may perform one binary search over numeric values inside the interval: `O(log n)`

Space complexity:

```text
O(1)
```

excluding sorting overhead.

This is fully efficient for the constraints.

---

# Approach 4: Prefix-sum formula with merged banned values and no per-number scan (same idea, slightly different presentation)

## Idea

Another way to view the optimal solution:

1. sort and deduplicate banned values in range `[1, n]`,
2. imagine allowed intervals,
3. greedily consume the smallest allowed values first.

This is the same essential strategy as Approach 3, but it is worth presenting as the clean “mathematical interval” method because that is the real insight.

---

## Example

```text
banned = [4,3,5,6], n = 7, maxSum = 18
```

After sorting:

```text
[3,4,5,6]
```

Allowed intervals:

```text
[1,2], [7,7]
```

Take `[1,2]`:

```text
sum = 3
remaining = 15
count = 2
```

Take `[7,7]`:

```text
sum = 7
remaining = 8
count = 3
```

No more allowed values.

Answer = `3`.

---

# Why greedy by smallest values is optimal

Suppose you have two valid available integers:

```text
a < b
```

If you choose `b` instead of `a`, then:

- your count increases by `1` either way,
- but using `b` consumes more budget.

So choosing `a` is never worse and is often strictly better.

Applying this repeatedly proves that the optimal set is exactly the smallest allowed values whose sum fits within `maxSum`.

That is why interval-greedy is correct.

---

# Fully polished recommended Java solution

```java
import java.util.*;

class Solution {
    public int maxCount(int[] banned, int n, long maxSum) {
        Arrays.sort(banned);

        long remaining = maxSum;
        int count = 0;
        int prev = 0;

        for (int i = 0; i < banned.length; i++) {
            int b = banned[i];

            if (b < 1 || b > n) {
                continue;
            }
            if (b == prev) {
                continue;
            }

            Result res = takeSmallest(prev + 1, b - 1, remaining);
            count += res.count;
            remaining -= res.sumUsed;

            if (remaining == 0) {
                return count;
            }

            prev = b;
        }

        Result res = takeSmallest(prev + 1, n, remaining);
        count += res.count;

        return count;
    }

    private Result takeSmallest(int left, int right, long budget) {
        if (left > right || budget <= 0) {
            return new Result(0, 0L);
        }

        long sumAll = sumRange(left, right);
        if (sumAll <= budget) {
            return new Result(right - left + 1, sumAll);
        }

        int lo = left, hi = right;
        while (lo < hi) {
            int mid = lo + (hi - lo + 1) / 2;
            if (sumRange(left, mid) <= budget) {
                lo = mid;
            } else {
                hi = mid - 1;
            }
        }

        long used = sumRange(left, lo);
        if (used <= budget) {
            return new Result(lo - left + 1, used);
        }

        return new Result(0, 0L);
    }

    private long sumRange(long l, long r) {
        long count = r - l + 1;
        return count * (l + r) / 2;
    }

    private static class Result {
        int count;
        long sumUsed;

        Result(int count, long sumUsed) {
            this.count = count;
            this.sumUsed = sumUsed;
        }
    }
}
```

---

# Proof sketch of correctness

We prove the algorithm is correct in two steps.

## Lemma 1

Among all allowed numbers, choosing smaller numbers first maximizes how many can be chosen under a fixed sum budget.

### Reason

If a solution contains a larger number `b` but excludes a smaller allowed number `a < b`, replacing `b` with `a` keeps the count unchanged and does not increase the sum. Repeating this exchange yields a solution consisting of the smallest allowed numbers.

So the optimal solution is always a prefix of the globally sorted allowed numbers.

---

## Lemma 2

Processing allowed intervals from left to right and taking the maximum affordable prefix of each interval exactly simulates taking the smallest allowed numbers globally.

### Reason

All numbers in earlier intervals are smaller than all numbers in later intervals, because intervals come from the natural number line in increasing order.

Thus, consuming intervals from left to right is identical to consuming the sorted allowed numbers from smallest upward.

---

## Therefore

The algorithm returns the maximum possible count.

---

# Comparison of approaches

## Approach 1: HashSet + scan from 1 to n

### Pros

- simplest to understand

### Cons

- impossible for large `n`

### Complexity

```text
Time:  O(n + m)
Space: O(m)
```

---

## Approach 2: Sorted banned + interval processing

### Pros

- efficient
- uses the real structure of the problem
- avoids scanning all numbers up to `n`

### Cons

- requires careful interval reasoning

### Complexity

```text
Time:  O(m log m + m log n)
Space: O(1) extra
```

---

## Approach 3: Recommended polished interval solution

### Pros

- cleanest optimal implementation
- easy to justify mathematically
- handles huge `n` and `maxSum`

### Cons

- slightly more advanced than direct greedy scan

### Complexity

```text
Time:  O(m log m + m log n)
Space: O(1) extra
```

---

# Takeaway pattern

This problem is a strong example of:

```text
Greedy on smallest values
+ compressing huge numeric ranges into intervals
+ arithmetic progression sums
```

Whenever:

- values come from a huge range,
- only relatively few blocked points matter,
- and you want the maximum count under a sum budget,

you should think about:

1. sorting the blocked points,
2. turning the range into allowed intervals,
3. consuming intervals greedily using arithmetic formulas.

That is the main pattern here.
