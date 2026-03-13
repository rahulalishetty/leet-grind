# 2221. Find Triangular Sum of an Array — Java Summary

## Method Signature

All Java solutions below use this signature:

```java
class Solution {
    public int triangularSum(int[] nums) {

    }
}
```

---

# Problem Restatement

We are given an array `nums` of digits (`0` to `9`).

We repeatedly replace the array by a new array where:

```text
newNums[i] = (nums[i] + nums[i + 1]) % 10
```

This reduces the length by 1 each round.

When only one element remains, that value is the **triangular sum**.

We need to return that final value.

---

# Core Insight

The most direct solution is to simulate the process exactly as described.

Because:

```text
nums.length <= 1000
```

an `O(n^2)` simulation is already fast enough.

But there is also a deeper combinational pattern:

the final value is a weighted sum of the original digits using **binomial coefficients modulo 10**.

That gives us a second, more mathematical approach.

---

# Approach 1 — Direct Simulation (Recommended)

## Idea

Modify the array level by level.

If the current array length is `m`, then after one round only the first `m - 1` positions matter:

```text
nums[i] = (nums[i] + nums[i + 1]) % 10
```

After each round, reduce the effective length by 1.

At the end, `nums[0]` is the answer.

---

## Why this works

This is exactly the process described in the problem statement.

We do not need any extra array if we update in place from left to right, because each new value depends only on the old current position and the next old position, which has not been overwritten yet.

---

## Java Code

```java
class Solution {
    public int triangularSum(int[] nums) {
        int n = nums.length;

        for (int len = n; len > 1; len--) {
            for (int i = 0; i < len - 1; i++) {
                nums[i] = (nums[i] + nums[i + 1]) % 10;
            }
        }

        return nums[0];
    }
}
```

---

## Complexity

The array shrinks from `n` to `1`.

So the total number of updates is:

```text
(n - 1) + (n - 2) + ... + 1 = O(n^2)
```

Thus:

```text
Time:  O(n^2)
Space: O(1)
```

With `n <= 1000`, this is fully acceptable.

---

# Approach 2 — Simulation with an Auxiliary Array

## Idea

Instead of modifying in place, create a temporary array at each round.

This can be easier to understand conceptually.

At every step:

- build `next` of size `currentLength - 1`
- fill it using adjacent sums mod 10
- replace the current array reference

---

## Java Code

```java
class Solution {
    public int triangularSum(int[] nums) {
        int[] cur = nums;

        while (cur.length > 1) {
            int[] next = new int[cur.length - 1];
            for (int i = 0; i < cur.length - 1; i++) {
                next[i] = (cur[i] + cur[i + 1]) % 10;
            }
            cur = next;
        }

        return cur[0];
    }
}
```

---

## Complexity

```text
Time:  O(n^2)
Space: O(n)
```

This is correct, though the in-place version is slightly better.

---

# Approach 3 — Combinatorial Formula Using Binomial Coefficients

## Idea

The repeated adjacent-sum process follows Pascal’s Triangle.

For an array of length `n`, the final answer is:

```text
sum( C(n - 1, i) * nums[i] ) mod 10
```

for:

```text
i = 0 to n - 1
```

So instead of simulating every layer, we can compute the final answer directly from binomial coefficients.

---

## Why this formula is true

Take:

```text
nums = [a, b, c, d]
```

After one round:

```text
[a+b, b+c, c+d]
```

After the next round:

```text
[a+2b+c, b+2c+d]
```

After the final round:

```text
a + 3b + 3c + d
```

The coefficients are:

```text
1, 3, 3, 1
```

which are exactly a row of Pascal’s triangle.

In general, the final value is:

```text
C(n-1,0)*nums[0] + C(n-1,1)*nums[1] + ... + C(n-1,n-1)*nums[n-1]
```

taken modulo 10.

---

## Practical Note

Since the modulo is `10`, and `n <= 1000`, we can compute the binomial coefficients iteratively using integer arithmetic and reduce modulo 10 at each step carefully.

A simple safe way for this problem size is to use Pascal triangle DP.

---

## Java Code

```java
class Solution {
    public int triangularSum(int[] nums) {
        int n = nums.length;

        int[] row = new int[n];
        row[0] = 1;

        for (int i = 1; i < n; i++) {
            for (int j = i; j >= 1; j--) {
                row[j] = (row[j] + row[j - 1]) % 10;
            }
        }

        int ans = 0;
        for (int i = 0; i < n; i++) {
            ans = (ans + row[i] * nums[i]) % 10;
        }

        return ans;
    }
}
```

---

## Complexity

Building the Pascal row takes:

```text
O(n^2)
```

Then one more pass for the weighted sum.

So:

```text
Time:  O(n^2)
Space: O(n)
```

This is not asymptotically faster than simulation, but it reveals the mathematical structure.

---

# Approach 4 — Recursive Definition (Not Recommended)

## Idea

One can define the process recursively:

- base case: if length is 1, return it
- otherwise build the next reduced array and recurse

This matches the problem statement closely, but recursion adds overhead and is unnecessary.

---

## Java Code

```java
class Solution {
    public int triangularSum(int[] nums) {
        return solve(nums);
    }

    private int solve(int[] nums) {
        if (nums.length == 1) {
            return nums[0];
        }

        int[] next = new int[nums.length - 1];
        for (int i = 0; i < nums.length - 1; i++) {
            next[i] = (nums[i] + nums[i + 1]) % 10;
        }

        return solve(next);
    }
}
```

---

## Complexity

```text
Time:  O(n^2)
Space: O(n^2)   // due to repeated array creation and recursion stack
```

So this is correct but inferior to the iterative versions.

---

# Detailed Walkthrough

## Example 1

```text
nums = [1,2,3,4,5]
```

Round 1:

```text
[3,5,7,9]
```

because:

```text
1+2 = 3
2+3 = 5
3+4 = 7
4+5 = 9
```

Round 2:

```text
[8,2,6]
```

because:

```text
3+5 = 8
5+7 = 12 % 10 = 2
7+9 = 16 % 10 = 6
```

Round 3:

```text
[0,8]
```

Round 4:

```text
[8]
```

So the triangular sum is:

```text
8
```

---

## Example 2

```text
nums = [5]
```

There is already only one element.

So the triangular sum is simply:

```text
5
```

---

# Important Correctness Argument

Every round combines adjacent elements with addition mod 10.

This operation is deterministic, and both simulation approaches apply exactly that transformation at each layer.

So the in-place and auxiliary-array simulations are directly correct by construction.

For the combinational approach, the repeated neighbor addition produces Pascal-triangle coefficients, so the final single number equals the binomial weighted sum modulo 10.

Thus all listed approaches are correct.

---

# Common Pitfalls

## 1. Forgetting the modulo 10 at each step

Each adjacent sum must be reduced by `% 10`.

---

## 2. Overwriting values incorrectly during in-place simulation

Update from left to right with the current effective length. This works because `nums[i + 1]` has not been overwritten yet when computing `nums[i]`.

---

## 3. Assuming you need a complex optimization

You do not. The constraints are small enough that the direct `O(n^2)` simulation is already ideal.

---

## 4. Mixing up Pascal-row index

For an array of length `n`, the final coefficients come from row:

```text
n - 1
```

not `n`.

---

# Best Approach

## Recommended: In-place simulation

This is the best solution here because:

- it is the simplest
- it matches the problem statement exactly
- it uses constant extra space
- it is fast enough for the constraints

---

# Final Recommended Java Solution

```java
class Solution {
    public int triangularSum(int[] nums) {
        int n = nums.length;

        for (int len = n; len > 1; len--) {
            for (int i = 0; i < len - 1; i++) {
                nums[i] = (nums[i] + nums[i + 1]) % 10;
            }
        }

        return nums[0];
    }
}
```

---

# Complexity Summary

```text
Time:  O(n^2)
Space: O(1)
```

for:

```text
1 <= nums.length <= 1000
```

---

# Final Takeaway

This problem looks like a triangle-building process, and the direct in-place simulation is already enough.

Under the hood, the final value is also a Pascal-triangle weighted sum of the original digits, which explains why the process is called a triangular sum.
