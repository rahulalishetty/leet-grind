# 1966. Binary Searchable Numbers in an Unsorted Array — Java Solutions and Detailed Notes

## Problem

We are given an array `nums` of **unique** integers.

A search procedure works like this:

```text
func(sequence, target)
  while sequence is not empty
    randomly choose an element from sequence as the pivot
    if pivot = target, return true
    else if pivot < target, remove pivot and all elements to its left from the sequence
    else, remove pivot and all elements to its right from the sequence
  end while
  return false
```

If the array were sorted, this would behave like binary search.
But the array is not necessarily sorted.

We need to count how many values are **guaranteed to be found**, no matter how the pivots are chosen.

---

# Core insight

A value `nums[i]` is guaranteed to be found **iff**:

- every value to its **left** is **smaller** than `nums[i]`, and
- every value to its **right** is **greater** than `nums[i]`.

In other words:

```text
max(nums[0..i-1]) < nums[i] < min(nums[i+1..n-1])
```

Why?

Because if there is a larger number on the left, then that number could be chosen as pivot and delete the target.
If there is a smaller number on the right, then that number could be chosen as pivot and delete the target.

So the problem reduces to counting indices that satisfy this condition.

---

# Approach 1: Brute Force Check for Every Index

## Idea

For each index `i`:

- scan everything on the left and ensure all values are smaller,
- scan everything on the right and ensure all values are larger.

If both hold, count it.

This approach is simple and directly matches the condition, but it is quadratic.

---

## Why it works

Suppose we are checking `nums[i]`.

### Left side condition

If there exists `j < i` with:

```text
nums[j] > nums[i]
```

then if `nums[j]` is chosen as pivot while target is `nums[i]`:

- pivot > target
- pivot and everything to its right are removed

That deletion can remove `nums[i]`, so `nums[i]` is not guaranteed.

### Right side condition

If there exists `j > i` with:

```text
nums[j] < nums[i]
```

then if `nums[j]` is chosen as pivot:

- pivot < target
- pivot and everything to its left are removed

That deletion can remove `nums[i]`, so `nums[i]` is not guaranteed.

Therefore the element is guaranteed only if both conditions hold.

---

## Java code

```java
class Solution {
    public int binarySearchableNumbers(int[] nums) {
        int n = nums.length;
        int answer = 0;

        for (int i = 0; i < n; i++) {
            boolean ok = true;

            for (int j = 0; j < i; j++) {
                if (nums[j] > nums[i]) {
                    ok = false;
                    break;
                }
            }

            if (!ok) {
                continue;
            }

            for (int j = i + 1; j < n; j++) {
                if (nums[j] < nums[i]) {
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

## Complexity

Time complexity:

```text
O(n^2)
```

Space complexity:

```text
O(1)
```

---

## Verdict

Good for understanding the condition.
Too slow for `n = 10^5`.

---

# Approach 2: Prefix Maximum + Suffix Minimum

## Idea

The condition for index `i` is:

```text
max on left < nums[i] < min on right
```

So we can preprocess:

- `prefixMax[i]` = maximum element in `nums[0..i]`
- `suffixMin[i]` = minimum element in `nums[i..n-1]`

Then for each `i` we check:

```text
(i == 0 || prefixMax[i - 1] < nums[i]) &&
(i == n - 1 || nums[i] < suffixMin[i + 1])
```

This gives a linear-time solution.

---

## Example

```text
nums = [-1, 5, 2]
```

Prefix max:

```text
[-1, 5, 5]
```

Suffix min:

```text
[-1, 2, 2]
```

Check each index:

### i = 0, value = -1

- no left side
- right min = 2
- `-1 < 2` yes

Count it.

### i = 1, value = 5

- left max = -1 < 5 yes
- right min = 2
- `5 < 2` no

Not counted.

### i = 2, value = 2

- left max = 5
- `5 < 2` no

Not counted.

Answer = `1`.

---

## Java code

```java
class Solution {
    public int binarySearchableNumbers(int[] nums) {
        int n = nums.length;
        int[] prefixMax = new int[n];
        int[] suffixMin = new int[n];

        prefixMax[0] = nums[0];
        for (int i = 1; i < n; i++) {
            prefixMax[i] = Math.max(prefixMax[i - 1], nums[i]);
        }

        suffixMin[n - 1] = nums[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            suffixMin[i] = Math.min(suffixMin[i + 1], nums[i]);
        }

        int answer = 0;

        for (int i = 0; i < n; i++) {
            boolean leftOk = (i == 0) || (prefixMax[i - 1] < nums[i]);
            boolean rightOk = (i == n - 1) || (nums[i] < suffixMin[i + 1]);

            if (leftOk && rightOk) {
                answer++;
            }
        }

        return answer;
    }
}
```

---

## Complexity

Time complexity:

```text
O(n)
```

Space complexity:

```text
O(n)
```

---

## Verdict

This is the most standard and easiest optimal solution.

---

# Approach 3: One Prefix Pass + One Backward Pass (Still O(n), less explicit)

## Idea

We can avoid storing the entire suffix-min array if we process from right to left while keeping a running minimum.

We still need a prefix maximum array, because for each index we need the maximum element to its left.

So:

1. Build `prefixMax`.
2. Traverse from right to left with `rightMin`.
3. Check each element on the fly.

This reduces one auxiliary array.

---

## Java code

```java
class Solution {
    public int binarySearchableNumbers(int[] nums) {
        int n = nums.length;
        int[] prefixMax = new int[n];

        prefixMax[0] = nums[0];
        for (int i = 1; i < n; i++) {
            prefixMax[i] = Math.max(prefixMax[i - 1], nums[i]);
        }

        int answer = 0;
        int rightMin = Integer.MAX_VALUE;

        for (int i = n - 1; i >= 0; i--) {
            boolean leftOk = (i == 0) || (prefixMax[i - 1] < nums[i]);
            boolean rightOk = nums[i] < rightMin;

            if (leftOk && rightOk) {
                answer++;
            }

            rightMin = Math.min(rightMin, nums[i]);
        }

        return answer;
    }
}
```

---

## Complexity

Time complexity:

```text
O(n)
```

Space complexity:

```text
O(n)
```

because we still keep `prefixMax`.

---

# Approach 4: Monotonic Stack Interpretation

## Idea

This problem can also be understood through elimination.

An element is **not** binary searchable if:

- there is a bigger value somewhere on its left, or
- there is a smaller value somewhere on its right.

A monotonic structure can help identify invalid positions, but for this problem the prefix/suffix method is cleaner.

Still, it is useful to see the conceptual interpretation:

- left validity comes from being greater than all previous values,
- right validity comes from being smaller than all future values.

That is effectively the same logic as checking whether the element belongs to both:

- the increasing frontier from the left,
- and the increasing frontier from the right in reverse form.

In practice, the prefix/suffix solution is the better implementation.

---

# Proof of correctness for the prefix/suffix condition

We now prove that an element `nums[i]` is guaranteed to be found **iff**:

```text
max(nums[0..i-1]) < nums[i] < min(nums[i+1..n-1])
```

## Necessity

Assume `nums[i]` is guaranteed to be found.

### Left side

If there were some `j < i` with:

```text
nums[j] > nums[i]
```

then choosing `nums[j]` as pivot while searching for `nums[i]` would cause:

- pivot > target
- remove pivot and all elements to its right

Since `i > j`, `nums[i]` lies to the right and would be removed.

That contradicts “guaranteed to be found”.

So every element on the left must be smaller.

### Right side

If there were some `j > i` with:

```text
nums[j] < nums[i]
```

then choosing `nums[j]` as pivot would cause:

- pivot < target
- remove pivot and all elements to its left

Since `i < j`, `nums[i]` lies to the left and would be removed.

Contradiction again.

So every element on the right must be larger.

Thus the condition is necessary.

---

## Sufficiency

Now assume:

```text
all elements on the left are smaller than nums[i]
all elements on the right are larger than nums[i]
```

Take any pivot chosen during the search process.

### If pivot == nums[i]

We are done.

### If pivot < nums[i]

Then pivot cannot be to the right of `i`, because every element on the right is greater than `nums[i]`, hence also greater than pivot relation would fail.

So pivot must be on the left side.
Removing pivot and everything to its left does not remove `nums[i]`.

### If pivot > nums[i]`

Then pivot cannot be to the left of `i`, because all left-side values are smaller than `nums[i]`.
So pivot must be on the right side.
Removing pivot and everything to its right does not remove `nums[i]`.

Thus no pivot choice can eliminate `nums[i]` before it is eventually selected.

So the element is guaranteed to be found.

Hence the condition is sufficient.

---

# Which approach should you use?

## Best practical solution

Use **Approach 2: Prefix Maximum + Suffix Minimum**

It is:

- linear,
- simple,
- easy to explain,
- fully optimal for the given constraints.

---

# Final recommended Java solution

```java
class Solution {
    public int binarySearchableNumbers(int[] nums) {
        int n = nums.length;

        int[] prefixMax = new int[n];
        int[] suffixMin = new int[n];

        prefixMax[0] = nums[0];
        for (int i = 1; i < n; i++) {
            prefixMax[i] = Math.max(prefixMax[i - 1], nums[i]);
        }

        suffixMin[n - 1] = nums[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            suffixMin[i] = Math.min(suffixMin[i + 1], nums[i]);
        }

        int answer = 0;

        for (int i = 0; i < n; i++) {
            boolean leftOk = (i == 0) || (prefixMax[i - 1] < nums[i]);
            boolean rightOk = (i == n - 1) || (nums[i] < suffixMin[i + 1]);

            if (leftOk && rightOk) {
                answer++;
            }
        }

        return answer;
    }
}
```

---

# Exhaustive walkthrough on a larger example

Consider:

```text
nums = [3, 1, 4, 2, 5]
```

We compute:

## Prefix max

```text
index:      0  1  2  3  4
nums:       3  1  4  2  5
prefixMax:  3  3  4  4  5
```

## Suffix min

```text
suffixMin:  1  1  2  2  5
```

Now check each position:

### i = 0, nums[i] = 3

- left side: none
- right side min = 1
- `3 < 1` false

Not binary searchable.

### i = 1, nums[i] = 1

- left max = 3
- `3 < 1` false

Not binary searchable.

### i = 2, nums[i] = 4

- left max = 3 → OK
- right min = 2
- `4 < 2` false

Not binary searchable.

### i = 3, nums[i] = 2

- left max = 4
- `4 < 2` false

Not binary searchable.

### i = 4, nums[i] = 5

- left max = 4 → OK
- no right side

Binary searchable.

Answer = `1`.

---

# Follow-up: what if duplicates are allowed?

The original problem says all values are unique.

If duplicates are allowed, the logic changes slightly.

For a target value to be guaranteed to be found, you need to be careful about equal values appearing on either side, because choosing an equal pivot would immediately return true **if the pivot value equals the target value**, even if it is not the same occurrence.

So the problem would need to be redefined:

- Are we searching for a **value** or a specific **index**?
- If searching by value, duplicates may actually help.
- If searching for a specific occurrence, duplicates complicate the guarantee condition.

If the follow-up still asks for values guaranteed to be found, then a natural modification is:

- left side must contain no value **greater** than target,
- right side must contain no value **smaller** than target.

So the strict inequalities become non-strict relative to equal values, depending on the exact interpretation.

For the original version with unique values, the strict prefix/suffix condition is the correct one.

---

# Pattern takeaway

This problem is really about identifying elements that are already in a “globally correct” sorted position relative to everything before and after them.

That naturally suggests:

- **prefix maxima**
- **suffix minima**

This is a common pattern whenever you need to know if an element is simultaneously:

- larger than all before it,
- smaller than all after it.
