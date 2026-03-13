# 1819. Number of Different Subsequences GCDs — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int countDifferentSubsequenceGCDs(int[] nums) {

    }
}
```

---

# Problem Restatement

We are given an array `nums` of positive integers.

A **subsequence** is any sequence formed by deleting some elements without changing the relative order of the remaining elements.

For every non-empty subsequence, compute its gcd.

We need to return:

```text
the number of distinct gcd values among all non-empty subsequences
```

---

# Core Challenge

The number of subsequences is exponential.

If `nums.length = 10^5`, then enumerating all subsequences is impossible.

So the main task is to avoid generating subsequences directly.

---

# Key Insight

Instead of asking:

> What gcds do all subsequences produce?

we ask the reverse:

> For a value `x`, can `x` be the gcd of some subsequence?

This reversal is the heart of the solution.

---

# Fundamental Observation

Suppose we want to know whether some value `x` can appear as the gcd of a subsequence.

Then all numbers chosen in that subsequence must be multiples of `x`.

So we only need to consider numbers in `nums` that are divisible by `x`.

Now take the gcd of **all present multiples of `x`** in the array.

If that gcd becomes exactly `x`, then `x` is achievable as the gcd of some subsequence.

If it is larger than `x`, then no subsequence of those multiples can reduce it to `x`.

So for each candidate `x`, we do:

1. iterate over multiples of `x`
2. collect those that exist in `nums`
3. compute gcd of them
4. if gcd becomes `x`, then `x` is one valid distinct subsequence gcd

---

# Why This Works

Let:

```text
Sx = all values in nums that are divisible by x
```

If some subsequence has gcd exactly `x`, then all of its numbers belong to `Sx`.

Now if we take gcd over some subset of `Sx`, it can only be a divisor of the gcd of all chosen elements.

The important fact is:

- if the gcd of the present multiples of `x` can be reduced to `x`, then `x` is achievable
- otherwise it is not

This gives a clean sieve-like approach.

---

# Approach 1 — Sieve Over All Possible GCD Values (Recommended)

## Idea

Let `maxVal = max(nums)`.

For every `x` from `1` to `maxVal`:

- look at multiples: `x, 2x, 3x, ...`
- among those, keep only the values present in `nums`
- compute running gcd
- if the running gcd becomes `x`, then increment answer

We use a boolean array `exists[]` to mark values present in `nums`.

This gives a very efficient solution.

---

## Java Code

```java
class Solution {
    public int countDifferentSubsequenceGCDs(int[] nums) {
        int maxVal = 0;
        for (int num : nums) {
            maxVal = Math.max(maxVal, num);
        }

        boolean[] exists = new boolean[maxVal + 1];
        for (int num : nums) {
            exists[num] = true;
        }

        int answer = 0;

        for (int x = 1; x <= maxVal; x++) {
            int g = 0;

            for (int multiple = x; multiple <= maxVal; multiple += x) {
                if (!exists[multiple]) continue;

                g = gcd(g, multiple);

                if (g == x) {
                    answer++;
                    break;
                }
            }
        }

        return answer;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Complexity Analysis

Let `M = max(nums)`.

For each `x`, we iterate through multiples of `x`.

Total work is:

```text
M/1 + M/2 + M/3 + ... + M/M
```

which is:

```text
O(M log M)
```

Each step performs a gcd, which is very fast in practice.

So total complexity is approximately:

```text
Time:  O(M log M)
Space: O(M)
```

This is the standard optimal solution.

---

# Approach 2 — Same Sieve Idea Using HashSet Presence

## Idea

Instead of a boolean presence array, we can store numbers in a `HashSet<Integer>`.

Then for each candidate gcd `x`, we still iterate through multiples of `x` and check whether they are present.

This is conceptually similar, but slower than the boolean array because hash lookups are more expensive than array indexing.

Still, it is valid and may be easier to write if we do not want a large presence array.

---

## Java Code

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    public int countDifferentSubsequenceGCDs(int[] nums) {
        int maxVal = 0;
        Set<Integer> seen = new HashSet<>();

        for (int num : nums) {
            seen.add(num);
            maxVal = Math.max(maxVal, num);
        }

        int answer = 0;

        for (int x = 1; x <= maxVal; x++) {
            int g = 0;

            for (int multiple = x; multiple <= maxVal; multiple += x) {
                if (!seen.contains(multiple)) continue;

                g = gcd(g, multiple);
                if (g == x) {
                    answer++;
                    break;
                }
            }
        }

        return answer;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Complexity

Asymptotically similar:

```text
Time:  O(M log M)
Space: O(M)
```

But constants are worse than Approach 1 due to `HashSet`.

So the boolean-array version is preferred.

---

# Approach 3 — Build GCD Set of Subsequences Incrementally (Conceptual / Often Too Slow)

## Idea

A more direct DP-like thought is:

As we scan numbers one by one, maintain the set of gcds of all subsequences ending at the current position.

If current number is `num`, then new gcd values come from:

- subsequence containing only `num` -> gcd = `num`
- extending previous subsequences -> gcd(oldGcd, num)

Add all these gcds into a global answer set.

This works because gcd values collapse quickly, so in many practical cases the set stays small.

However, worst-case performance can still be too large compared with the sieve solution.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int countDifferentSubsequenceGCDs(int[] nums) {
        Set<Integer> all = new HashSet<>();
        Set<Integer> prev = new HashSet<>();

        for (int num : nums) {
            Set<Integer> curr = new HashSet<>();
            curr.add(num);

            for (int g : prev) {
                curr.add(gcd(g, num));
            }

            all.addAll(curr);
            prev = curr;
        }

        return all.size();
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Why This Is Not the Best Here

This method is elegant and sometimes useful for subsequence-gcd problems, but here:

- `nums.length` can be `10^5`
- values go up to `2 * 10^5`

The sieve-based divisor approach is much more robust and predictable.

So this approach is educational, but not the best final choice.

---

# Approach 4 — Brute Force Over Subsequences (Only for Understanding)

## Idea

Generate all subsequences, compute gcd of each, store distinct gcds in a set.

This is the most direct interpretation of the problem.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int countDifferentSubsequenceGCDs(int[] nums) {
        Set<Integer> set = new HashSet<>();
        backtrack(nums, 0, 0, false, set);
        return set.size();
    }

    private void backtrack(int[] nums, int idx, int currentGcd, boolean taken, Set<Integer> set) {
        if (idx == nums.length) {
            if (taken) {
                set.add(currentGcd);
            }
            return;
        }

        // skip nums[idx]
        backtrack(nums, idx + 1, currentGcd, taken, set);

        // take nums[idx]
        if (!taken) {
            backtrack(nums, idx + 1, nums[idx], true, set);
        } else {
            backtrack(nums, idx + 1, gcd(currentGcd, nums[idx]), true, set);
        }
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

## Why This Is Impossible

The number of subsequences is:

```text
2^n - 1
```

For `n = 10^5`, this is astronomically large.

So this approach is only useful for tiny examples or conceptual understanding.

---

# Detailed Walkthrough of the Recommended Approach

## Example 1

```text
nums = [6, 10, 3]
```

Distinct values present:

```text
3, 6, 10
```

Let us test possible gcds:

### x = 1

Multiples of 1 that are present:

```text
3, 6, 10
```

Running gcd:

```text
gcd(0,3) = 3
gcd(3,6) = 3
gcd(3,10) = 1
```

So 1 is achievable.

---

### x = 2

Multiples of 2 present:

```text
6, 10
```

Running gcd:

```text
gcd(0,6) = 6
gcd(6,10) = 2
```

So 2 is achievable.

---

### x = 3

Multiples of 3 present:

```text
3, 6
```

Running gcd:

```text
gcd(0,3) = 3
```

So 3 is achievable.

---

### x = 6

Present multiples:

```text
6
```

Running gcd = 6 -> achievable.

---

### x = 10

Present multiples:

```text
10
```

Running gcd = 10 -> achievable.

So distinct gcd values are:

```text
1, 2, 3, 6, 10
```

Count = 5.

---

# Why Checking All Present Multiples Is Enough

Suppose we are testing whether `x` can be a subsequence gcd.

All numbers of such a subsequence must be divisible by `x`.

So they must come from the present multiples of `x`.

Now consider the gcd of those present multiples.

- if it reaches `x`, then some combination of them yields gcd `x`
- if it stays greater than `x`, then every subsequence formed from them will also have gcd greater than `x`

So the test is both necessary and sufficient.

This is the central correctness idea.

---

# Common Pitfalls

## 1. Thinking subsequences must preserve contiguity

They do not.

This is subsequence, not subarray.

So order is irrelevant for gcd existence.

---

## 2. Trying to enumerate subsequences

This immediately becomes impossible.

---

## 3. Missing the reverse viewpoint

The trick is not to generate gcds from subsequences.

The trick is to test each possible gcd candidate.

---

## 4. Using only distinct nums values and ignoring multiples structure

The divisibility-by-candidate perspective is essential.

---

# Best Approach

## Recommended: Sieve Over Possible GCD Values

Why this is best:

- leverages the value constraint `max(nums) <= 2 * 10^5`
- avoids subsequence enumeration entirely
- uses harmonic-series complexity
- simple and elegant once the insight is understood

---

# Final Recommended Java Solution

```java
class Solution {
    public int countDifferentSubsequenceGCDs(int[] nums) {
        int maxVal = 0;
        for (int num : nums) {
            maxVal = Math.max(maxVal, num);
        }

        boolean[] exists = new boolean[maxVal + 1];
        for (int num : nums) {
            exists[num] = true;
        }

        int answer = 0;

        for (int x = 1; x <= maxVal; x++) {
            int g = 0;

            for (int multiple = x; multiple <= maxVal; multiple += x) {
                if (!exists[multiple]) continue;

                g = gcd(g, multiple);

                if (g == x) {
                    answer++;
                    break;
                }
            }
        }

        return answer;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}
```

---

# Complexity Summary

Let:

```text
M = max(nums)
```

Then:

- build presence array: `O(n)`
- loop candidates 1..M
- for each candidate iterate over multiples

Total:

```text
Time:  O(M log M)
Space: O(M)
```

This is optimal for the constraints.

---

# Final Takeaway

This is a classic example of reversing the perspective:

Instead of asking:

> what gcds do subsequences create?

ask:

> for each value `x`, can `x` be the gcd of some subsequence?

That turns a subsequence explosion problem into a clean divisor-sieve problem.

The best solution is:

1. mark which values exist
2. test every candidate gcd
3. gcd together present multiples of that candidate
4. count those candidates that succeed
