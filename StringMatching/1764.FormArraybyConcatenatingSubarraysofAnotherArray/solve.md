# 1764. Form Array by Concatenating Subarrays of Another Array

## Problem Statement

You are given:

- a 2D integer array `groups`
- an integer array `nums`

You need to determine whether it is possible to choose `groups.length` disjoint subarrays from `nums` such that:

- the `i`-th chosen subarray is exactly equal to `groups[i]`
- the chosen subarrays appear in the same order as `groups`
- the chosen subarrays do not overlap

Return `true` if this is possible, otherwise return `false`.

A subarray is a contiguous sequence of elements.

---

## Example 1

```text
Input:
groups = [[1,-1,-1],[3,-2,0]]
nums = [1,-1,0,1,-1,-1,3,-2,0]

Output:
true
```

Explanation:

We can match:

- `[1,-1,-1]` at indices `[3,4,5]`
- `[3,-2,0]` at indices `[6,7,8]`

These matches are in order and disjoint.

---

## Example 2

```text
Input:
groups = [[10,-2],[1,2,3,4]]
nums = [1,2,3,4,10,-2]

Output:
false
```

Explanation:

`[10,-2]` appears after `[1,2,3,4]`, but `groups[0]` must appear before `groups[1]`.

---

## Example 3

```text
Input:
groups = [[1,2,3],[3,4]]
nums = [7,7,1,2,3,4,7,7]

Output:
false
```

Explanation:

The only possible matches would overlap on value `3` at index `4`, so they are not disjoint.

---

## Constraints

- `1 <= groups.length <= 10^3`
- `1 <= groups[i].length`
- `sum(groups[i].length) <= 10^3`
- `1 <= nums.length <= 10^3`
- `-10^7 <= groups[i][j], nums[k] <= 10^7`

---

# Core Insight

The subarrays must be:

1. matched in order
2. disjoint
3. exact matches

That means once we match one group starting at some position in `nums`, the next group can only start **after** the end of the previous match.

This strongly suggests a greedy left-to-right scan:

- try to match `groups[0]`
- once found, move the pointer to the end of that match
- then try to match `groups[1]`
- continue until either all groups match or we run out of `nums`

Because the arrays are small, this simple scan is already sufficient.

---

# Approach 1: Greedy Two-Pointer Scan

## Intuition

Maintain:

- `groupIndex` = which group we are currently trying to match
- `numIndex` = current position in `nums`

At each step:

- check whether `groups[groupIndex]` matches starting at `nums[numIndex]`
- if yes:
  - advance `numIndex` by the length of that group
  - move to the next group
- if no:
  - increment `numIndex` by 1 and try again

This ensures:

- order is preserved
- matches are disjoint
- we always take the earliest possible valid placement

That greedy choice is safe because delaying an already valid earlier placement cannot help future groups.

---

## Algorithm

1. Set `groupIndex = 0`, `numIndex = 0`
2. While `groupIndex < groups.length` and `numIndex < nums.length`
3. Check whether `groups[groupIndex]` matches `nums` starting at `numIndex`
4. If it matches:
   - set `numIndex += groups[groupIndex].length`
   - increment `groupIndex`
5. Otherwise:
   - increment `numIndex`
6. At the end, return whether all groups were matched

---

## Java Code

```java
class Solution {
    public boolean canChoose(int[][] groups, int[] nums) {
        int groupIndex = 0;
        int numIndex = 0;

        while (groupIndex < groups.length && numIndex < nums.length) {
            if (matches(groups[groupIndex], nums, numIndex)) {
                numIndex += groups[groupIndex].length;
                groupIndex++;
            } else {
                numIndex++;
            }
        }

        return groupIndex == groups.length;
    }

    private boolean matches(int[] group, int[] nums, int start) {
        if (start + group.length > nums.length) {
            return false;
        }

        for (int i = 0; i < group.length; i++) {
            if (group[i] != nums[start + i]) {
                return false;
            }
        }

        return true;
    }
}
```

---

## Complexity Analysis

Let:

- `n = nums.length`
- `m = sum of lengths of all groups`

### Time Complexity

In the worst case, we may try matching a group at many positions.

Each attempted match can cost up to the group length.

A safe upper bound is:

```text
O(n * m)
```

Given the constraints, this is easily acceptable.

Since both are at most `1000`, worst-case work is around `10^6`, which is fine.

### Space Complexity

```text
O(1)
```

---

## Verdict

This is the best practical solution.

---

# Approach 2: Explicit Nested Scan Per Group

## Intuition

Another way to write the same greedy idea is:

For each group:

- scan `nums` from the current allowed starting point until a match is found
- once found, move the current pointer to the end of that matched segment
- continue to the next group

This is logically the same as Approach 1, but some people find it easier to reason about because each group is handled in its own loop.

---

## Java Code

```java
class Solution {
    public boolean canChoose(int[][] groups, int[] nums) {
        int start = 0;

        for (int[] group : groups) {
            boolean found = false;

            while (start + group.length <= nums.length) {
                if (matches(group, nums, start)) {
                    start += group.length;
                    found = true;
                    break;
                }
                start++;
            }

            if (!found) {
                return false;
            }
        }

        return true;
    }

    private boolean matches(int[] group, int[] nums, int start) {
        for (int i = 0; i < group.length; i++) {
            if (group[i] != nums[start + i]) {
                return false;
            }
        }
        return true;
    }
}
```

---

## Complexity Analysis

### Time Complexity

Again:

```text
O(n * m)
```

in the worst case.

### Space Complexity

```text
O(1)
```

---

## Verdict

Also excellent. This is arguably the clearest implementation.

---

# Approach 3: KMP for Each Group

## Intuition

Since each group needs to be matched as a contiguous subarray inside `nums`, we can view each group as a pattern and `nums` as the text.

That suggests using KMP-style pattern matching for integer arrays.

For each group:

- find the earliest occurrence of that group in `nums` starting from the current pointer
- once found, jump past it
- continue with the next group

This is algorithmically elegant, especially if `nums` and groups were much larger.

However, under the current constraints, it is more complicated than necessary.

Still, it is a valid alternative.

---

## KMP on Integer Arrays

KMP does not require strings specifically. It only needs element equality.

So we can compute an LPS array for each integer group and use it to search in `nums`.

---

## Java Code

```java
class Solution {
    public boolean canChoose(int[][] groups, int[] nums) {
        int start = 0;

        for (int[] group : groups) {
            int pos = kmpSearch(nums, start, group);
            if (pos == -1) {
                return false;
            }
            start = pos + group.length;
        }

        return true;
    }

    private int kmpSearch(int[] nums, int start, int[] pattern) {
        int[] lps = buildLPS(pattern);

        int i = start;
        int j = 0;

        while (i < nums.length) {
            if (nums[i] == pattern[j]) {
                i++;
                j++;

                if (j == pattern.length) {
                    return i - j;
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }

        return -1;
    }

    private int[] buildLPS(int[] pattern) {
        int[] lps = new int[pattern.length];
        int len = 0;
        int i = 1;

        while (i < pattern.length) {
            if (pattern[i] == pattern[len]) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
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
- `m = sum of lengths of groups`

### Time Complexity

Each KMP search is linear in the remaining part of `nums` plus the pattern length.

Across all groups, a reasonable bound is:

```text
O(n * numberOfGroups + m)
```

or simply linear-ish under these constraints.

### Space Complexity

```text
O(max group length)
```

for the LPS array.

---

## Verdict

Correct and efficient, but overkill for this problem size.

---

# Approach 4: Recursive Backtracking (Unnecessary)

## Intuition

One could think of recursively trying all possible placements for each group in order.

But because the groups must be matched in order and the earliest valid placement is always safe, full backtracking is unnecessary.

The greedy scan already captures the only useful search direction.

So recursion adds complexity without benefit.

---

## Why Greedy Is Safe

Suppose a group can be matched at positions `p1` and `p2`, where `p1 < p2`.

Choosing the earlier valid match `p1` cannot hurt future groups, because it leaves **more** remaining array available than choosing `p2`.

So there is never a reason to skip the earliest valid placement.

That is why the greedy algorithm is correct.

---

# Proof Sketch for Greedy Correctness

We process groups from left to right.

For the current group, let the earliest valid match start at position `p`.

Any solution that places this group later than `p` leaves strictly fewer elements available for subsequent groups.

So if there exists any valid full solution, there also exists one using the earliest valid placement for the current group.

By repeating this argument group by group, the greedy strategy is safe.

---

# Common Mistakes

## 1. Allowing overlaps

Once a group is matched from `start` to `start + len - 1`, the next group must begin at or after:

```text
start + len
```

not before.

---

## 2. Matching groups out of order

The problem requires the groups to appear in the same order as given.

---

## 3. Thinking repeated values create ambiguity requiring backtracking

They do not.

Because the earliest valid placement is always optimal, no branching is needed.

---

## 4. Forgetting bounds before matching

Before checking whether a group matches at `start`, ensure:

```text
start + group.length <= nums.length
```

---

# Final Recommended Solution

Use the greedy scanning solution.

It is:

- simple
- correct
- efficient enough for all constraints

---

## Clean Final Java Solution

```java
class Solution {
    public boolean canChoose(int[][] groups, int[] nums) {
        int g = 0;
        int i = 0;

        while (g < groups.length && i < nums.length) {
            if (match(groups[g], nums, i)) {
                i += groups[g].length;
                g++;
            } else {
                i++;
            }
        }

        return g == groups.length;
    }

    private boolean match(int[] group, int[] nums, int start) {
        if (start + group.length > nums.length) {
            return false;
        }

        for (int j = 0; j < group.length; j++) {
            if (group[j] != nums[start + j]) {
                return false;
            }
        }

        return true;
    }
}
```

---

# Complexity Summary

## Greedy direct scan

- Time: `O(n * m)` in the worst case
- Space: `O(1)`

## Greedy nested form

- Time: `O(n * m)`
- Space: `O(1)`

## KMP per group

- Time: near linear / `O(n + m)` style per pattern-search structure
- Space: `O(max group length)`

---

# Interview Summary

The problem looks like it may need backtracking, but it does not.

The key insight is:

- the groups must be matched in order
- the subarrays must be disjoint
- taking the earliest valid match for a group is always optimal

So a simple greedy scan through `nums`, matching each group in order, is both correct and sufficient.
