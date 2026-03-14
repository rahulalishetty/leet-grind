# 1755. Closest Subsequence Sum — Exhaustive Java Notes

## Problem Statement

You are given:

- an integer array `nums`
- an integer `goal`

You may choose **any subsequence** of `nums` — including the empty subsequence.

If the chosen subsequence has sum `sum`, your objective is to minimize:

```text
abs(sum - goal)
```

Return the minimum possible value of that expression.

A subsequence is formed by deleting zero or more elements without changing the order of the remaining elements.

---

## Example 1

```text
Input:
nums = [5,-7,3,5]
goal = 6

Output:
0
```

Explanation:

Choose the whole array:

```text
5 + (-7) + 3 + 5 = 6
```

So:

```text
abs(6 - 6) = 0
```

---

## Example 2

```text
Input:
nums = [7,-9,15,-2]
goal = -5

Output:
1
```

Explanation:

Choose subsequence:

```text
[7, -9, -2]
sum = -4
```

Then:

```text
abs(-4 - (-5)) = 1
```

---

## Example 3

```text
Input:
nums = [1,2,3]
goal = -7

Output:
7
```

Explanation:

The empty subsequence has sum `0`, so:

```text
abs(0 - (-7)) = 7
```

which is optimal.

---

## Constraints

```text
1 <= nums.length <= 40
-10^7 <= nums[i] <= 10^7
-10^9 <= goal <= 10^9
```

The crucial constraint is:

```text
nums.length <= 40
```

This is too large for brute force over all subsequences:

```text
2^40 ≈ 1 trillion
```

But small enough for **meet-in-the-middle**.

---

# 1. Core Insight

If we split the array into two halves:

- left half of size about `n/2`
- right half of size about `n/2`

then instead of enumerating all `2^n` subsequences, we enumerate:

```text
2^(n/2) + 2^(n/2)
```

subset sums.

Since:

```text
2^20 = 1,048,576
```

this is feasible.

This is the standard **meet-in-the-middle** pattern.

---

# 2. Why Meet-in-the-Middle Works

Suppose we split `nums` into:

```text
left
right
```

Every subsequence sum of the full array can be written as:

```text
sumLeft + sumRight
```

where:

- `sumLeft` is a subset sum from the left half
- `sumRight` is a subset sum from the right half

So the problem becomes:

> Find `sumLeft` and `sumRight` such that
> `abs(sumLeft + sumRight - goal)` is minimized.

This is now a two-list combination problem.

---

# 3. Approach 1 — Meet-in-the-Middle + Sort + Binary Search

## Main Idea

1. Split the array into two halves.
2. Generate all subset sums of each half.
3. Sort one of the two subset-sum lists.
4. For every sum in the other list, use binary search to find the closest complement.

---

## Step-by-Step

Let:

```text
leftSums = all subset sums of left half
rightSums = all subset sums of right half
```

For each `x` in `leftSums`, we want a `y` in `rightSums` such that:

```text
x + y ≈ goal
```

That means:

```text
y ≈ goal - x
```

So for each `x`, binary search `rightSums` for the insertion point of `goal - x`, and check:

- the element at that position
- the previous element

One of them is the closest possible.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minAbsDifference(int[] nums, int goal) {
        int n = nums.length;
        int mid = n / 2;

        int[] left = Arrays.copyOfRange(nums, 0, mid);
        int[] right = Arrays.copyOfRange(nums, mid, n);

        List<Integer> leftSums = generateSums(left);
        List<Integer> rightSums = generateSums(right);

        Collections.sort(rightSums);

        int ans = Integer.MAX_VALUE;

        for (int x : leftSums) {
            int target = goal - x;
            int idx = lowerBound(rightSums, target);

            if (idx < rightSums.size()) {
                ans = Math.min(ans, Math.abs(x + rightSums.get(idx) - goal));
            }
            if (idx > 0) {
                ans = Math.min(ans, Math.abs(x + rightSums.get(idx - 1) - goal));
            }

            if (ans == 0) return 0;
        }

        return ans;
    }

    private List<Integer> generateSums(int[] arr) {
        int m = arr.length;
        List<Integer> sums = new ArrayList<>(1 << m);

        for (int mask = 0; mask < (1 << m); mask++) {
            int sum = 0;
            for (int i = 0; i < m; i++) {
                if (((mask >> i) & 1) == 1) {
                    sum += arr[i];
                }
            }
            sums.add(sum);
        }

        return sums;
    }

    private int lowerBound(List<Integer> list, int target) {
        int l = 0, r = list.size();
        while (l < r) {
            int mid = l + (r - l) / 2;
            if (list.get(mid) < target) {
                l = mid + 1;
            } else {
                r = mid;
            }
        }
        return l;
    }
}
```

---

## Complexity Analysis

Let `n = nums.length`.

Each half has size at most `20`.

Generating subset sums:

```text
O(2^(n/2) * n)
```

or more precisely `O(2^(n/2) * n/2)`.

Sorting one list:

```text
O(2^(n/2) log 2^(n/2))
```

For each left sum, binary search on right sums:

```text
O(2^(n/2) log 2^(n/2))
```

Overall:

```text
O(2^(n/2) * n + 2^(n/2) log 2^(n/2))
```

which is usually summarized as:

```text
O(n * 2^(n/2))
```

Space:

```text
O(2^(n/2))
```

---

# 4. Why Checking Only the Lower Bound and Previous Element Is Enough

Suppose `rightSums` is sorted.

For a fixed `x`, we want:

```text
x + y
```

to be as close as possible to `goal`.

Equivalently, `y` should be as close as possible to:

```text
goal - x
```

In a sorted array, the closest value to a target is always among:

- the first element `>= target`
- the previous element `< target`

So binary search plus checking at most two candidates is enough.

---

# 5. Approach 2 — Meet-in-the-Middle + Two Pointers

## Main Idea

Instead of binary-searching for every left sum, we can:

1. generate all subset sums of both halves
2. sort both lists
3. use two pointers:
   - one starts at the smallest left sum
   - one starts at the largest right sum

Then move pointers depending on whether the current total is too small or too large.

This is similar to the classic two-sum closest pattern.

---

## Why It Works

Let:

```text
leftSums sorted ascending
rightSums sorted ascending
```

Use:

- `i = 0`
- `j = rightSums.size() - 1`

Current total:

```text
leftSums[i] + rightSums[j]
```

If this sum is too large, decrease `j`.
If it is too small, increase `i`.

This explores candidate sums in a monotonic way.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minAbsDifference(int[] nums, int goal) {
        int n = nums.length;
        int mid = n / 2;

        int[] left = Arrays.copyOfRange(nums, 0, mid);
        int[] right = Arrays.copyOfRange(nums, mid, n);

        List<Integer> leftSums = generateSums(left);
        List<Integer> rightSums = generateSums(right);

        Collections.sort(leftSums);
        Collections.sort(rightSums);

        int i = 0;
        int j = rightSums.size() - 1;
        int ans = Integer.MAX_VALUE;

        while (i < leftSums.size() && j >= 0) {
            long sum = (long) leftSums.get(i) + rightSums.get(j);
            ans = Math.min(ans, (int) Math.abs(sum - goal));

            if (ans == 0) return 0;

            if (sum > goal) {
                j--;
            } else {
                i++;
            }
        }

        return ans;
    }

    private List<Integer> generateSums(int[] arr) {
        int m = arr.length;
        List<Integer> sums = new ArrayList<>(1 << m);

        for (int mask = 0; mask < (1 << m); mask++) {
            int sum = 0;
            for (int i = 0; i < m; i++) {
                if (((mask >> i) & 1) == 1) {
                    sum += arr[i];
                }
            }
            sums.add(sum);
        }

        return sums;
    }
}
```

---

## Complexity Analysis

Generating subset sums:

```text
O(n * 2^(n/2))
```

Sorting both lists:

```text
O(2^(n/2) log 2^(n/2))
```

Two-pointer sweep:

```text
O(2^(n/2))
```

Overall still:

```text
O(n * 2^(n/2))
```

Space:

```text
O(2^(n/2))
```

---

# 6. Approach 3 — DFS to Generate Subset Sums + Binary Search

## Main Idea

Instead of generating subset sums by bitmask iteration, generate them by DFS / recursion.

This is especially natural for meet-in-the-middle:

- recursively enumerate sums of the left half
- recursively enumerate sums of the right half

Then apply the same binary-search combination logic.

This does not change asymptotic complexity, but some people find it more intuitive.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int minAbsDifference(int[] nums, int goal) {
        int n = nums.length;
        int mid = n / 2;

        List<Integer> leftSums = new ArrayList<>();
        List<Integer> rightSums = new ArrayList<>();

        generate(nums, 0, mid, 0, leftSums);
        generate(nums, mid, n, 0, rightSums);

        Collections.sort(rightSums);

        int ans = Integer.MAX_VALUE;

        for (int x : leftSums) {
            int target = goal - x;
            int idx = lowerBound(rightSums, target);

            if (idx < rightSums.size()) {
                ans = Math.min(ans, Math.abs(x + rightSums.get(idx) - goal));
            }
            if (idx > 0) {
                ans = Math.min(ans, Math.abs(x + rightSums.get(idx - 1) - goal));
            }

            if (ans == 0) return 0;
        }

        return ans;
    }

    private void generate(int[] nums, int start, int end, int sum, List<Integer> out) {
        if (start == end) {
            out.add(sum);
            return;
        }

        generate(nums, start + 1, end, sum, out);
        generate(nums, start + 1, end, sum + nums[start], out);
    }

    private int lowerBound(List<Integer> list, int target) {
        int l = 0, r = list.size();
        while (l < r) {
            int mid = l + (r - l) / 2;
            if (list.get(mid) < target) {
                l = mid + 1;
            } else {
                r = mid;
            }
        }
        return l;
    }
}
```

---

## Complexity

Same as the earlier meet-in-the-middle approaches:

```text
O(n * 2^(n/2))
```

Space:

```text
O(2^(n/2))
```

plus recursion stack `O(n)`.

---

# 7. Why Brute Force Over All Subsequences Is Impossible

A direct brute-force solution would enumerate all subsets of `nums`.

That is:

```text
2^n
```

With `n = 40`:

```text
2^40 ≈ 1,099,511,627,776
```

That is far too large.

Meet-in-the-middle reduces this to approximately:

```text
2^20 + 2^20
```

which is around one million per half — completely manageable.

This is the defining trick of the problem.

---

# 8. Why Dynamic Programming by Sum Is Not Suitable

Sometimes subset-sum problems invite DP over possible sums.

That does **not** work well here because:

- `nums[i]` can be as large as `10^7`
- values can be negative
- `goal` can be up to `10^9`

So a DP indexed by sum would have an enormous range.

That is why the intended approach is not pseudo-polynomial DP, but meet-in-the-middle.

---

# 9. Small Worked Example

Take:

```text
nums = [5, -7, 3, 5]
goal = 6
```

Split into halves:

```text
left = [5, -7]
right = [3, 5]
```

Subset sums of left:

- `[] = 0`
- `[5] = 5`
- `[-7] = -7`
- `[5,-7] = -2`

So:

```text
leftSums = [0, 5, -7, -2]
```

Subset sums of right:

- `[] = 0`
- `[3] = 3`
- `[5] = 5`
- `[3,5] = 8`

So:

```text
rightSums = [0, 3, 5, 8]
```

Now for `x = -2`, we want `y ≈ 8`.
There is exactly `8` in `rightSums`.

So:

```text
x + y = -2 + 8 = 6
abs(6 - 6) = 0
```

Optimal answer found.

---

# 10. Correctness Sketch

Every subsequence of the full array can be uniquely decomposed into:

- a subsequence of the left half
- a subsequence of the right half

Therefore every achievable total sum is of the form:

```text
leftSum + rightSum
```

Conversely, every pair `(leftSum, rightSum)` corresponds to a valid subsequence of the full array.

So the search for the closest subsequence sum is exactly the search for the pair of subset sums whose total is closest to `goal`.

The meet-in-the-middle algorithms enumerate all such half-sums and then search for the best pairing.
Thus they are correct.

---

# 11. Comparison of Approaches

| Approach             | Main Idea                          |             Time |        Space | Notes                                 |
| -------------------- | ---------------------------------- | ---------------: | -----------: | ------------------------------------- |
| MITM + binary search | sort one list, search complements  | `O(n * 2^(n/2))` | `O(2^(n/2))` | standard editorial solution           |
| MITM + two pointers  | sort both lists, sweep toward goal | `O(n * 2^(n/2))` | `O(2^(n/2))` | elegant alternative                   |
| DFS-generated MITM   | recursive subset-sum generation    | `O(n * 2^(n/2))` | `O(2^(n/2))` | same idea, different generation style |

---

# 12. Which Approach Should You Prefer?

In most interviews and coding rounds, the best choice is:

## Meet-in-the-middle + sort + binary search

Why?

- it is the classic recognized solution,
- easy to prove,
- efficient,
- simple enough to implement cleanly in Java.

The two-pointer version is also excellent once both sum lists are sorted.

---

# 13. Practical Java Notes

## Integer overflow?

Subset sums can reach around:

```text
20 * 10^7 = 2 * 10^8
```

per half, which still fits in `int`.

The combined sum also stays within about `4 * 10^8`.

So `int` is safe here.

Still, when computing:

```java
Math.abs(sum - goal)
```

it is often cleaner to use `long` temporarily in the arithmetic if you want extra safety, especially in generic templates.

---

# 14. Recommended Java Solution

```java
import java.util.*;

class Solution {
    public int minAbsDifference(int[] nums, int goal) {
        int n = nums.length;
        int mid = n / 2;

        int[] left = Arrays.copyOfRange(nums, 0, mid);
        int[] right = Arrays.copyOfRange(nums, mid, n);

        List<Integer> leftSums = generateSums(left);
        List<Integer> rightSums = generateSums(right);

        Collections.sort(rightSums);

        int ans = Integer.MAX_VALUE;

        for (int x : leftSums) {
            int target = goal - x;
            int idx = lowerBound(rightSums, target);

            if (idx < rightSums.size()) {
                ans = Math.min(ans, Math.abs(x + rightSums.get(idx) - goal));
            }
            if (idx > 0) {
                ans = Math.min(ans, Math.abs(x + rightSums.get(idx - 1) - goal));
            }

            if (ans == 0) {
                return 0;
            }
        }

        return ans;
    }

    private List<Integer> generateSums(int[] arr) {
        int m = arr.length;
        List<Integer> sums = new ArrayList<>(1 << m);

        for (int mask = 0; mask < (1 << m); mask++) {
            int sum = 0;
            for (int i = 0; i < m; i++) {
                if (((mask >> i) & 1) == 1) {
                    sum += arr[i];
                }
            }
            sums.add(sum);
        }

        return sums;
    }

    private int lowerBound(List<Integer> list, int target) {
        int l = 0, r = list.size();
        while (l < r) {
            int mid = l + (r - l) / 2;
            if (list.get(mid) < target) {
                l = mid + 1;
            } else {
                r = mid;
            }
        }
        return l;
    }
}
```

---

# 15. Final Takeaway

This problem is a textbook example of when to use **meet-in-the-middle**.

The brute-force search space:

```text
2^40
```

is too large.

But splitting into halves reduces it to roughly:

```text
2^20 + 2^20
```

which is manageable.

Once you recognize that, the rest becomes:

1. generate half subset sums,
2. combine them efficiently,
3. minimize the absolute difference to the goal.

That is the essential pattern behind the entire solution.
