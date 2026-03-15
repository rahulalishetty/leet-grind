# 3036. Number of Subarrays That Match a Pattern II

## Problem Statement

You are given:

- a 0-indexed integer array `nums` of size `n`
- a 0-indexed integer array `pattern` of size `m`, where each element is one of `-1`, `0`, and `1`

A subarray `nums[i..j]` of size `m + 1` matches the pattern if for every `k`:

- `nums[i + k + 1] > nums[i + k]` when `pattern[k] == 1`
- `nums[i + k + 1] == nums[i + k]` when `pattern[k] == 0`
- `nums[i + k + 1] < nums[i + k]` when `pattern[k] == -1`

Return the number of subarrays in `nums` that match `pattern`.

---

## Example 1

```text
Input:  nums = [1,2,3,4,5,6], pattern = [1,1]
Output: 4
```

Explanation:

The pattern `[1,1]` means we want subarrays of length `3` such that:

```text
first < second < third
```

Matching subarrays:

```text
[1,2,3]
[2,3,4]
[3,4,5]
[4,5,6]
```

So the answer is `4`.

---

## Example 2

```text
Input:  nums = [1,4,4,1,3,5,5,3], pattern = [1,0,-1]
Output: 2
```

Explanation:

The pattern means:

```text
nums[i]   < nums[i+1]
nums[i+1] == nums[i+2]
nums[i+2] > nums[i+3]
```

Matching subarrays:

```text
[1,4,4,1]
[3,5,5,3]
```

So the answer is `2`.

---

## Constraints

- `2 <= n == nums.length <= 10^6`
- `1 <= nums[i] <= 10^9`
- `1 <= m == pattern.length < n`
- `-1 <= pattern[i] <= 1`

---

# Core Insight

The pattern does **not** depend on actual values.
It depends only on the comparison between adjacent numbers.

So instead of matching directly on `nums`, we should first build a **comparison array**:

```text
comp[i] =
    1   if nums[i+1] > nums[i]
    0   if nums[i+1] == nums[i]
   -1   if nums[i+1] < nums[i]
```

Then a subarray `nums[i..i+m]` matches `pattern` **if and only if**:

```text
comp[i..i+m-1] == pattern
```

That transforms the original problem into:

> Count how many times `pattern` appears as a contiguous subarray inside `comp`.

This is now a classic pattern matching problem on arrays.

---

# Why Problem II Changes the Strategy

In Problem I, `n <= 100`, so direct checking was enough.

Here:

```text
n <= 10^6
```

That changes everything.

An `O(n * m)` solution can time out badly.

So for Problem II, we should aim for:

```text
O(n + m)
```

That naturally suggests:

- **KMP**
- **Z-algorithm**
- possibly **rolling hash** as a probabilistic alternative

---

# Approach 1: Direct Window Checking (Too Slow for Worst Case)

## Intuition

The simplest solution is:

- for each possible start index
- compare adjacent elements according to the pattern

This is correct, but too slow when both `n` and `m` are large.

---

## Java Code

```java
class Solution {
    public int countMatchingSubarrays(int[] nums, int[] pattern) {
        int n = nums.length;
        int m = pattern.length;
        int answer = 0;

        for (int start = 0; start + m < n; start++) {
            boolean ok = true;

            for (int k = 0; k < m; k++) {
                int left = nums[start + k];
                int right = nums[start + k + 1];

                if (pattern[k] == 1 && right <= left) {
                    ok = false;
                    break;
                }
                if (pattern[k] == 0 && right != left) {
                    ok = false;
                    break;
                }
                if (pattern[k] == -1 && right >= left) {
                    ok = false;
                    break;
                }
            }

            if (ok) {
                answer++;
            }
        }

        return answer;
    }
}
```

---

## Complexity Analysis

### Time Complexity

There are `n - m` candidate subarrays, and each one checks `m` comparisons:

```text
O((n - m) * m)
```

Worst case:

```text
O(n * m)
```

This is too slow for `n = 10^6`.

### Space Complexity

```text
O(1)
```

---

## Verdict

Correct, but not appropriate for the full constraints.

---

# Approach 2: Build Comparison Array + Direct Window Match

## Intuition

We can cleanly separate the problem into two steps:

1. convert `nums` into `comp`
2. compare windows of `comp` against `pattern`

This is cleaner than raw checking on `nums`, but if we still compare each window directly, worst-case time is still too large.

---

## Java Code

```java
class Solution {
    public int countMatchingSubarrays(int[] nums, int[] pattern) {
        int n = nums.length;
        int m = pattern.length;

        int[] comp = new int[n - 1];
        for (int i = 0; i < n - 1; i++) {
            if (nums[i + 1] > nums[i]) {
                comp[i] = 1;
            } else if (nums[i + 1] == nums[i]) {
                comp[i] = 0;
            } else {
                comp[i] = -1;
            }
        }

        int answer = 0;

        for (int start = 0; start + m <= comp.length; start++) {
            boolean ok = true;
            for (int i = 0; i < m; i++) {
                if (comp[start + i] != pattern[i]) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                answer++;
            }
        }

        return answer;
    }
}
```

---

## Complexity Analysis

### Time Complexity

- Build `comp`: `O(n)`
- Window matching: `O((n - m) * m)`

Total:

```text
O(n + (n - m) * m)
```

Worst case:

```text
O(n * m)
```

### Space Complexity

```text
O(n)
```

---

## Verdict

Better abstraction, but still too slow asymptotically.

---

# Approach 3: KMP on the Comparison Array

## Intuition

Once we build the comparison array, the problem becomes:

> Count occurrences of `pattern` inside `comp`.

That is a standard exact pattern matching problem.

KMP works on arrays just as well as on strings, because it only relies on equality comparison.

So we can use:

- text = `comp`
- pattern = `pattern`

and count how many matches occur.

This gives linear time.

---

## Why KMP Fits Perfectly

KMP preprocesses the pattern using the **LPS array** and then scans the text without backtracking.

Because both arrays consist of integers, equality checks are straightforward.

This is the strongest exact answer for the full constraints.

---

## Algorithm

1. Build the comparison array `comp`
2. Build the LPS array for `pattern`
3. Run KMP matching of `pattern` over `comp`
4. Count every occurrence

Each occurrence corresponds to one matching subarray of `nums`.

---

## Java Code

```java
class Solution {
    public int countMatchingSubarrays(int[] nums, int[] pattern) {
        int[] comp = buildComparisonArray(nums);
        return kmpCount(comp, pattern);
    }

    private int[] buildComparisonArray(int[] nums) {
        int[] comp = new int[nums.length - 1];

        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i + 1] > nums[i]) {
                comp[i] = 1;
            } else if (nums[i + 1] == nums[i]) {
                comp[i] = 0;
            } else {
                comp[i] = -1;
            }
        }

        return comp;
    }

    private int kmpCount(int[] text, int[] pattern) {
        int[] lps = buildLPS(pattern);
        int i = 0, j = 0, count = 0;

        while (i < text.length) {
            if (text[i] == pattern[j]) {
                i++;
                j++;

                if (j == pattern.length) {
                    count++;
                    j = lps[j - 1];
                }
            } else {
                if (j > 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return count;
    }

    private int[] buildLPS(int[] pattern) {
        int[] lps = new int[pattern.length];
        int len = 0;

        for (int i = 1; i < pattern.length; ) {
            if (pattern[i] == pattern[len]) {
                lps[i] = ++len;
                i++;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }

        return lps;
    }
}
```

---

## Complexity Analysis

Let:

- `n = nums.length`
- `m = pattern.length`

### Time Complexity

- Build `comp`: `O(n)`
- Build LPS: `O(m)`
- KMP scan: `O(n)`

Total:

```text
O(n + m)
```

### Space Complexity

```text
O(n + m)
```

for `comp` and `lps`.

If needed, you can say the dominant auxiliary structures are both linear.

---

## Verdict

This is the best exact solution.

---

# Approach 4: Z-Algorithm on the Comparison Array

## Intuition

Another exact linear-time option is the Z-algorithm.

Once we build `comp`, we can combine:

```text
pattern + [sentinel] + comp
```

where `sentinel` is a value not in `{-1,0,1}`, for example `2`.

Then compute the Z-array.

Whenever a Z-value at a position in the `comp` portion is at least `m`, we found one occurrence of `pattern`.

This is equivalent in strength to KMP.

---

## Why This Works

Z-array gives:

```text
z[i] = length of the longest segment starting at i
       that matches the prefix
```

If the combined prefix is exactly the pattern, then every place in the text part where `z[i] >= m` corresponds to a full pattern match.

---

## Java Code

```java
class Solution {
    public int countMatchingSubarrays(int[] nums, int[] pattern) {
        int[] comp = buildComparisonArray(nums);
        int m = pattern.length;

        int[] combined = new int[m + 1 + comp.length];
        int idx = 0;

        for (int x : pattern) {
            combined[idx++] = x;
        }
        combined[idx++] = 2; // sentinel outside {-1,0,1}
        for (int x : comp) {
            combined[idx++] = x;
        }

        int[] z = buildZ(combined);
        int count = 0;

        for (int i = m + 1; i < combined.length; i++) {
            if (z[i] >= m) {
                count++;
            }
        }

        return count;
    }

    private int[] buildComparisonArray(int[] nums) {
        int[] comp = new int[nums.length - 1];
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i + 1] > nums[i]) {
                comp[i] = 1;
            } else if (nums[i + 1] == nums[i]) {
                comp[i] = 0;
            } else {
                comp[i] = -1;
            }
        }
        return comp;
    }

    private int[] buildZ(int[] arr) {
        int n = arr.length;
        int[] z = new int[n];
        int left = 0, right = 0;

        for (int i = 1; i < n; i++) {
            if (i <= right) {
                z[i] = Math.min(right - i + 1, z[i - left]);
            }

            while (i + z[i] < n && arr[z[i]] == arr[i + z[i]]) {
                z[i]++;
            }

            if (i + z[i] - 1 > right) {
                left = i;
                right = i + z[i] - 1;
            }
        }

        return z;
    }
}
```

---

## Complexity Analysis

### Time Complexity

- Build `comp`: `O(n)`
- Build combined array: `O(n + m)`
- Build Z-array: `O(n + m)`

Total:

```text
O(n + m)
```

### Space Complexity

```text
O(n + m)
```

---

## Verdict

Excellent exact alternative to KMP.

---

# Approach 5: Rolling Hash on the Comparison Array

## Intuition

A more probabilistic alternative is rolling hash.

After building `comp`, treat it like a sequence and hash windows of length `m`.

Then compare rolling hashes to the hash of `pattern`.

This can be fast, but it is not exact unless extra care is taken with multiple hashes.

Because KMP and Z already give exact linear-time solutions, rolling hash is not the best recommendation here.

---

## Sketch

1. Build `comp`
2. Compute hash of `pattern`
3. Slide a length-`m` window across `comp`
4. Compare hashes and count matches

---

## Why This Is Not the Best Final Answer

Rolling hash can collide.

That is often acceptable in practice, but for a clean exact answer:

- KMP is exact
- Z is exact
- both are already linear

So hash-based matching is more of an alternative technique than the preferred one here.

---

# Why the Comparison Array Transformation Is Correct

A subarray of `nums` of size `m + 1` is determined by `m` adjacent comparisons:

```text
(nums[i], nums[i+1]),
(nums[i+1], nums[i+2]),
...
(nums[i+m-1], nums[i+m])
```

Each of those comparisons maps to exactly one value in `{-1,0,1}`.

So matching the original problem condition is equivalent to checking whether the sequence of these `m` comparison values equals `pattern`.

That gives a one-to-one correspondence between:

- matching subarrays of `nums`
- occurrences of `pattern` inside `comp`

This is the decisive simplification.

---

# Dry Run

## Example 2

```text
nums = [1,4,4,1,3,5,5,3]
pattern = [1,0,-1]
```

Build `comp`:

- `4 > 1` → `1`
- `4 == 4` → `0`
- `1 < 4` → `-1`
- `3 > 1` → `1`
- `5 > 3` → `1`
- `5 == 5` → `0`
- `3 < 5` → `-1`

So:

```text
comp = [1,0,-1,1,1,0,-1]
```

Now count occurrences of:

```text
pattern = [1,0,-1]
```

They appear starting at indices:

- `0`
- `4`

So the answer is `2`.

These correspond to subarrays:

- `[1,4,4,1]`
- `[3,5,5,3]`

---

# Comparing the Best Approaches

## KMP

### Strengths

- exact
- linear
- standard interview solution
- no sentinel handling needed

### Weakness

- requires LPS understanding

---

## Z-Algorithm

### Strengths

- also exact and linear
- elegant “pattern + separator + text” formulation

### Weakness

- requires choosing a safe sentinel value

---

## Direct window checking

### Strengths

- easiest to write

### Weakness

- not viable for the largest inputs

---

# Final Recommended Solution

Use:

## Comparison array + KMP

This is the strongest exact solution:

- transform the numeric problem into sequence matching
- apply a standard linear pattern matcher
- count all occurrences

---

## Clean Final Java Solution

```java
class Solution {
    public int countMatchingSubarrays(int[] nums, int[] pattern) {
        int n = nums.length;
        int[] comp = new int[n - 1];

        for (int i = 0; i < n - 1; i++) {
            if (nums[i + 1] > nums[i]) {
                comp[i] = 1;
            } else if (nums[i + 1] == nums[i]) {
                comp[i] = 0;
            } else {
                comp[i] = -1;
            }
        }

        int[] lps = buildLPS(pattern);
        int i = 0, j = 0, count = 0;

        while (i < comp.length) {
            if (comp[i] == pattern[j]) {
                i++;
                j++;

                if (j == pattern.length) {
                    count++;
                    j = lps[j - 1];
                }
            } else {
                if (j > 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return count;
    }

    private int[] buildLPS(int[] pattern) {
        int[] lps = new int[pattern.length];
        int len = 0;

        for (int i = 1; i < pattern.length; ) {
            if (pattern[i] == pattern[len]) {
                lps[i] = ++len;
                i++;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }

        return lps;
    }
}
```

---

# Common Mistakes

## 1. Matching values instead of relations

The pattern refers to adjacent comparisons, not the numbers themselves.

---

## 2. Using subarray length `m` instead of `m + 1`

A pattern of length `m` describes `m` adjacent relations, which requires `m + 1` numbers.

---

## 3. Forgetting equality case

`pattern[k] == 0` means the adjacent numbers must be exactly equal.

---

## 4. Using brute force for Problem II

That may pass Problem I, but not the `10^6`-scale version.

---

## 5. Assuming KMP only applies to strings

KMP works on arrays too, as long as equality comparison is defined.

---

# Complexity Summary

## Direct checking on `nums`

- Time: `O(n * m)`
- Space: `O(1)`

## Comparison array + direct window compare

- Time: `O(n * m)`
- Space: `O(n)`

## Comparison array + KMP

- Time: `O(n + m)`
- Space: `O(n + m)`

## Comparison array + Z-algorithm

- Time: `O(n + m)`
- Space: `O(n + m)`

## Comparison array + rolling hash

- Time: near `O(n + m)`
- Space: `O(n)`
- Caveat: probabilistic

---

# Interview Summary

The decisive step is to transform `nums` into its adjacent-comparison signature:

```text
1  for increase
0  for equality
-1 for decrease
```

Then the problem becomes:

> Count how many times `pattern` appears in that comparison array.

That is a classic sequence-matching problem.

For Problem II, the best exact solution is:

- build the comparison array
- run KMP
- count the matches

This yields an exact linear-time solution.
