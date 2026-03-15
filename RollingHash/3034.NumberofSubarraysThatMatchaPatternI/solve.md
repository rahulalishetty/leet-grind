# 3034. Number of Subarrays That Match a Pattern I

## Problem Statement

You are given:

- a 0-indexed integer array `nums` of length `n`
- a 0-indexed integer array `pattern` of length `m`, where each element is one of `-1`, `0`, or `1`

A subarray `nums[i..j]` of size `m + 1` matches the pattern if for every `k`:

- `nums[i + k + 1] > nums[i + k]` when `pattern[k] == 1`
- `nums[i + k + 1] == nums[i + k]` when `pattern[k] == 0`
- `nums[i + k + 1] < nums[i + k]` when `pattern[k] == -1`

Return the number of subarrays of `nums` that match `pattern`.

---

## Example 1

```text
Input:  nums = [1,2,3,4,5,6], pattern = [1,1]
Output: 4
```

Explanation:

The pattern `[1,1]` means:

```text
first < second < third
```

So the matching subarrays are:

```text
[1,2,3], [2,3,4], [3,4,5], [4,5,6]
```

Answer = `4`.

---

## Example 2

```text
Input:  nums = [1,4,4,1,3,5,5,3], pattern = [1,0,-1]
Output: 2
```

Explanation:

The pattern means:

```text
nums[i] < nums[i+1]
nums[i+1] == nums[i+2]
nums[i+2] > nums[i+3]
```

Matching subarrays are:

```text
[1,4,4,1] and [3,5,5,3]
```

Answer = `2`.

---

## Constraints

- `2 <= n == nums.length <= 100`
- `1 <= nums[i] <= 10^9`
- `1 <= m == pattern.length < n`
- `-1 <= pattern[i] <= 1`

---

# Core Insight

The pattern does **not** compare absolute values.
It only compares the relationship between adjacent numbers:

- increasing → `1`
- equal → `0`
- decreasing → `-1`

So instead of working directly on `nums`, we can transform `nums` into a **comparison array**:

```text
comp[i] =
    1   if nums[i+1] > nums[i]
    0   if nums[i+1] == nums[i]
   -1   if nums[i+1] < nums[i]
```

Then a subarray of `nums` matches `pattern` exactly when a corresponding contiguous segment of `comp` equals `pattern`.

This transformation makes the problem much cleaner.

---

# Approach 1: Direct Window Checking on `nums`

## Intuition

The most straightforward solution is:

- try every starting index `i`
- check whether the subarray `nums[i..i+m]` satisfies all `m` comparisons required by `pattern`

This is simple and fully acceptable because `n <= 100`.

---

## Algorithm

For each starting index `start` from `0` to `n - m - 1`:

1. For each `k` from `0` to `m - 1`:
   - compare `nums[start + k]` and `nums[start + k + 1]`
   - verify the result matches `pattern[k]`
2. If all comparisons match, increment answer

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

                if (pattern[k] == 1 && !(right > left)) {
                    ok = false;
                    break;
                }
                if (pattern[k] == 0 && !(right == left)) {
                    ok = false;
                    break;
                }
                if (pattern[k] == -1 && !(right < left)) {
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

Let:

- `n = nums.length`
- `m = pattern.length`

### Time Complexity

There are `n - m` candidate subarrays, and each one checks `m` comparisons:

```text
O((n - m) * m)
```

Worst case:

```text
O(n * m)
```

Since `n <= 100`, this is tiny.

### Space Complexity

```text
O(1)
```

---

## Verdict

This is the simplest correct solution and already enough for the given constraints.

---

# Approach 2: Build Comparison Array, Then Compare Windows

## Intuition

Instead of repeatedly comparing raw numbers for every window, first convert `nums` into its comparison signature.

For example:

```text
nums = [1,4,4,1,3]
```

becomes:

```text
comp = [1,0,-1,1]
```

Now the problem becomes:

> Count how many subarrays of `comp` of length `m` equal `pattern`.

This is easier to reason about.

---

## Algorithm

1. Build `comp` of length `n - 1`
2. For each starting index `start` in `comp`:
   - compare `comp[start..start+m-1]` with `pattern`
3. Count matches

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
- Compare windows: `O((n - m) * m)`

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

for the comparison array.

---

## Verdict

This is still simple, but the transformed view is much cleaner and prepares naturally for KMP.

---

# Approach 3: KMP on the Comparison Array

## Intuition

Once we convert `nums` into the comparison array `comp`, we are looking for occurrences of `pattern` inside `comp`.

That is a standard pattern-matching problem on arrays.

So we can apply **KMP**:

- text = `comp`
- pattern = `pattern`

This gives linear-time matching.

For Problem I, this is not necessary because the constraints are small, but it is a strong and scalable solution pattern.

---

## Why KMP Fits

KMP is not limited to strings.
It works on any sequence where equality comparison is defined.

Here both `comp` and `pattern` are integer arrays containing only `-1`, `0`, and `1`, so KMP applies directly.

---

## Algorithm

1. Build the comparison array `comp`
2. Build the LPS array for `pattern`
3. Run KMP to count how many times `pattern` appears in `comp`

Each match corresponds to one subarray of `nums` of length `m + 1`.

---

## Java Code

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

        return kmpCount(comp, pattern);
    }

    private int kmpCount(int[] text, int[] pattern) {
        int[] lps = buildLPS(pattern);
        int i = 0, j = 0;
        int count = 0;

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

---

## Verdict

This is the best algorithmic solution if you want scalability and a reusable pattern.

---

# Approach 4: Z-Algorithm on the Comparison Array

## Intuition

Instead of KMP, we can also use the Z-algorithm after combining:

```text
pattern + sentinel + comp
```

Because `pattern` and `comp` contain `-1`, `0`, `1`, we can choose a sentinel value outside that range, such as `2`.

Then each position in the combined array whose Z-value is at least `m` corresponds to a match.

This is another exact linear-time solution.

---

## Algorithm

1. Build `comp`
2. Create combined array:

```text
combined = pattern + [2] + comp
```

3. Build Z-array on `combined`
4. For each position that starts inside `comp`, if `z[i] >= pattern.length`, count a match

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

        int[] combined = new int[m + 1 + comp.length];
        int idx = 0;

        for (int x : pattern) {
            combined[idx++] = x;
        }
        combined[idx++] = 2; // sentinel not in {-1,0,1}
        for (int x : comp) {
            combined[idx++] = x;
        }

        int[] z = buildZ(combined);
        int answer = 0;

        for (int i = m + 1; i < combined.length; i++) {
            if (z[i] >= m) {
                answer++;
            }
        }

        return answer;
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

Elegant and exact.
Equivalent in strength to KMP here.

---

# Approach 5: Rolling Hash on the Comparison Array

## Intuition

After building the comparison array, we could hash windows of length `m` and compare them to the hash of `pattern`.

This can make matching fast.

But there is a problem:

- rolling hash can collide
- KMP and Z already give exact linear-time solutions

So while rolling hash is a valid alternative in practice, it is not the best principled answer.

---

## Sketch

1. Build `comp`
2. Compute rolling hash for `pattern`
3. Slide a window of length `m` over `comp`
4. Count windows whose hash matches

This is fast, but probabilistic unless double hashing is used.

---

# Comparing the Best Approaches

## Direct window checking

### Strengths

- simplest
- enough for current constraints

### Weakness

- not scalable

---

## Comparison array + direct window match

### Strengths

- cleaner abstraction
- easier to reason about than raw comparisons

### Weakness

- still quadratic-like in the worst case

---

## KMP

### Strengths

- exact
- linear
- reusable
- natural sequence-matching solution

### Weakness

- more setup than needed for `n <= 100`

---

## Z-algorithm

### Strengths

- also exact and linear
- elegant matching formulation

### Weakness

- slightly less common than KMP in interviews

---

# Final Recommended Solution

For this exact problem, because constraints are tiny, the direct solution is fully acceptable.

But if you want the strongest algorithmic approach, the best formulation is:

1. build the comparison array
2. match `pattern` in that array using KMP

That gives a clean linear solution.

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

## 1. Comparing the wrong subarray length

If `pattern.length = m`, the matching subarray in `nums` must have length:

```text
m + 1
```

because each pattern value compares two adjacent elements.

---

## 2. Matching raw numbers instead of adjacent relations

The pattern is about:

- greater than
- equal
- less than

not actual values.

---

## 3. Forgetting to transform comparisons consistently

The comparison encoding must be:

- `1` for increasing
- `0` for equal
- `-1` for decreasing

Any mismatch in encoding breaks the logic.

---

## 4. Assuming KMP only works for strings

KMP works on any sequence with equality comparison, including integer arrays.

---

# Complexity Summary

## Direct checking on `nums`

- Time: `O(n * m)`
- Space: `O(1)`

## Comparison array + direct compare

- Time: `O(n * m)`
- Space: `O(n)`

## Comparison array + KMP

- Time: `O(n + m)`
- Space: `O(n + m)`

## Comparison array + Z-algorithm

- Time: `O(n + m)`
- Space: `O(n + m)`

---

# Interview Summary

The decisive simplification is to convert `nums` into the array of adjacent comparisons.

Then the problem becomes:

> Count how many times `pattern` appears in that comparison array.

Once phrased that way:

- brute force is straightforward
- KMP or Z gives a clean linear solution

For Problem I, brute force is enough.
For stronger technique and reuse, KMP is the best answer.
