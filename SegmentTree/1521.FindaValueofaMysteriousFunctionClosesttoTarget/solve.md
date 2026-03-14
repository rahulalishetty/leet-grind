# 1521. Find a Value of a Mysterious Function Closest to Target

## Problem Restatement

For this problem, the hidden function is:

```text
func(arr, l, r) = arr[l] & arr[l+1] & ... & arr[r]
```

So we need to find a subarray whose bitwise AND is as close as possible to `target`.

Formally, return the minimum possible value of:

```text
| (arr[l] & arr[l+1] & ... & arr[r]) - target |
```

for all valid subarrays.

---

## Key Constraints

```text
1 <= arr.length <= 10^5
1 <= arr[i] <= 10^6
0 <= target <= 10^7
```

The important clue is:

- `n` is as large as `10^5`, so `O(n^2)` is impossible
- the operation is **bitwise AND**, which is monotone when extending a subarray

That monotonicity is the key to the efficient solution.

---

# Core Bitwise Insight

For a fixed ending index `i`, consider all subarrays ending at `i`:

```text
[arr[i]]
[arr[i-1], arr[i]]
[arr[i-2], ..., arr[i]]
...
```

Their AND values have a special property:

- as the subarray becomes longer, the AND can only stay the same or decrease
- once a bit becomes `0`, it can never come back to `1`

So for all subarrays ending at `i`, the number of **distinct AND values** is small.

In fact, it is bounded by about the number of bits, because each time the value changes, at least one bit is permanently turned off.

Since `arr[i] <= 10^6`, that is around 20 bits.

This gives an `O(n * log(maxValue))` style solution.

---

# Approach 1: Set of Distinct AND Values for Each Ending Position (Recommended)

## Idea

Let:

```text
prev = all distinct AND values of subarrays ending at i-1
```

Now for index `i`, every subarray ending at `i` is either:

1. the single-element subarray `[i, i]`, giving value `arr[i]`
2. some earlier subarray ending at `i-1`, extended by `arr[i]`

If a previous subarray had AND value `x`, then after appending `arr[i]`, the new AND becomes:

```text
x & arr[i]
```

So the set of AND values for subarrays ending at `i` is:

```text
cur = { arr[i] } U { x & arr[i] for x in prev }
```

We deduplicate these values, and for each one, update the answer with:

```text
abs(value - target)
```

---

## Why the set stays small

Suppose the distinct AND values for subarrays ending at `i` are listed in decreasing order.

Each time the value changes, at least one more bit has been turned from `1` to `0`.

Since there are only about 20 relevant bits, there can only be a small number of distinct values.

So even though we process all subarrays conceptually, the compressed set is tiny.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int closestToTarget(int[] arr, int target) {
        Set<Integer> prev = new HashSet<>();
        int ans = Integer.MAX_VALUE;

        for (int num : arr) {
            Set<Integer> cur = new HashSet<>();
            cur.add(num);

            for (int x : prev) {
                cur.add(x & num);
            }

            for (int val : cur) {
                ans = Math.min(ans, Math.abs(val - target));
            }

            prev = cur;
        }

        return ans;
    }
}
```

---

## Complexity

Let `B` be the number of bits needed to represent values in `arr`.

Because the number of distinct AND values per ending position is small, the effective size of `prev` is `O(B)`.

So time complexity is approximately:

```text
O(n * B)
```

For this problem, `B` is about 20.

Space complexity:

```text
O(B)
```

---

## Pros

- Short and elegant
- Standard accepted solution
- Exploits the exact structure of bitwise AND

## Cons

- The bounded-size-set insight is not obvious at first

---

# Approach 2: Distinct AND Values Using Lists Instead of Hash Sets

## Idea

The hash set solution is clean, but we can do slightly better practically.

Because the AND values generated in each step naturally collapse quickly, we can store them in a list and deduplicate manually.

For each `num = arr[i]`:

- start a new list with `num`
- for every value `x` in previous list, compute `y = x & num`
- append `y` only if it differs from the last appended value

This works because repeated AND operations often produce consecutive duplicates.

---

## Java Code

```java
import java.util.*;

class Solution {
    public int closestToTarget(int[] arr, int target) {
        List<Integer> prev = new ArrayList<>();
        int ans = Integer.MAX_VALUE;

        for (int num : arr) {
            List<Integer> cur = new ArrayList<>();
            cur.add(num);
            ans = Math.min(ans, Math.abs(num - target));

            for (int x : prev) {
                int y = x & num;
                if (cur.get(cur.size() - 1) != y) {
                    cur.add(y);
                    ans = Math.min(ans, Math.abs(y - target));
                }
            }

            prev = cur;
        }

        return ans;
    }
}
```

---

## Why manual deduplication works

If `prev` stores distinct AND values from subarrays ending at `i-1`, then as you iterate through them and AND with `num`, many consecutive results collapse to the same value.

Keeping only changes is enough.

This avoids hashing overhead and is often faster in practice.

---

## Complexity

Same asymptotic behavior:

```text
O(n * B)
```

Space:

```text
O(B)
```

---

## Pros

- Faster in practice than `HashSet`
- Still clean
- Often preferred by experienced contestants

## Cons

- Slightly less obvious than the set-based version

---

# Approach 3: Brute Force with Early AND Shrinking (Educational, Not Practical)

## Idea

For each starting index `l`, extend to the right:

```text
andVal &= arr[r]
```

and update the answer.

Since AND only decreases, you may hope early shrinking helps enough.

Unfortunately, worst-case complexity is still too high for `n = 10^5`.

---

## Java Code

```java
class Solution {
    public int closestToTarget(int[] arr, int target) {
        int n = arr.length;
        int ans = Integer.MAX_VALUE;

        for (int l = 0; l < n; l++) {
            int andVal = ~0;
            for (int r = l; r < n; r++) {
                andVal &= arr[r];
                ans = Math.min(ans, Math.abs(andVal - target));

                if (andVal == 0 && target >= 0) {
                    // further extension cannot increase AND
                    // but still not a reliable enough pruning for worst-case performance
                }
            }
        }

        return ans;
    }
}
```

---

## Complexity

Worst case:

```text
O(n^2)
```

which is too slow.

---

## Pros

- Very easy to understand
- Good for deriving the AND monotonicity insight

## Cons

- Not viable for the given constraints

---

# Approach 4: Segment Tree / Sparse Table + Binary Search Idea (Why it is awkward)

## Idea

Since range AND is associative, you could build a sparse table for range AND queries.

Then for each starting index `l`, as `r` increases, the AND decreases monotonically, so maybe binary search around the point closest to `target`.

This sounds appealing, but in practice it is awkward because:

- the AND value changes in irregular jumps
- you still have to inspect many breakpoints
- the distinct-values-per-ending-position method is much simpler and more direct

So while technically possible to reason about, this is not the best route.

---

## Verdict

Interesting academically, but inferior to the standard distinct-AND-values DP/set solution.

---

# Deep Intuition

## Why AND behaves so nicely here

Bitwise AND is monotone under extension:

```text
x & y <= x
```

bitwise, in the sense that bits can only turn off.

So if you fix the right endpoint and extend leftward, the sequence of AND values can only move downward through a small chain of bit-patterns.

That is what compresses the quadratic number of subarrays into a tiny set of distinct results.

---

## Why OR would feel similar but different

A related problem with bitwise OR has the opposite behavior:

- OR can only stay the same or increase as the subarray grows

And a similar “distinct values per endpoint stay small” idea also works there.

This problem is the AND-version of that pattern.

---

## Why the number of distinct AND values is small

Take any chain:

```text
v1 > v2 > v3 > ...
```

of distinct AND values for subarrays ending at a fixed position.

Every time the value changes, at least one bit is lost forever.

With only around 20 bits available, the chain length is bounded.

That is the fundamental reason the algorithm is near-linear.

---

# Correctness Sketch for Approach 1

We prove that the set-based DP considers every relevant subarray AND.

## Claim 1

At iteration `i`, `prev` contains exactly the distinct AND values of all subarrays ending at `i-1`.

This is true initially with `prev = empty`.

## Claim 2

The set of AND values of subarrays ending at `i` is exactly:

```text
{ arr[i] } U { x & arr[i] for x in prev }
```

Why?

Every subarray ending at `i` is either:

- `[i, i]`, whose AND is `arr[i]`
- or some subarray ending at `i-1`, extended by `arr[i]`

If the old subarray’s AND was `x`, then the new AND is `x & arr[i]`.

So the formula is complete.

## Claim 3

By checking every value in `cur`, we consider the AND result of every subarray ending at `i`.

Since this is done for all `i`, we consider every subarray in the array.

Therefore, taking the minimum absolute difference over all values seen yields the correct answer.

---

# Example Walkthrough

## Example 1

```text
arr = [9, 12, 3, 7, 15], target = 5
```

### i = 0, num = 9

Subarrays ending here:

```text
[9]
```

Distinct ANDs:

```text
{9}
```

Best difference:

```text
|9 - 5| = 4
```

### i = 1, num = 12

From previous `{9}`:

```text
9 & 12 = 8
```

Current distinct ANDs:

```text
{12, 8}
```

Differences:

```text
|12 - 5| = 7
|8 - 5| = 3
```

### i = 2, num = 3

From previous `{12, 8}`:

```text
12 & 3 = 0
8 & 3 = 0
```

Current distinct ANDs:

```text
{3, 0}
```

Differences:

```text
|3 - 5| = 2
|0 - 5| = 5
```

Now answer becomes `2`, which is optimal.

---

## Example 2

```text
arr = [1000000,1000000,1000000], target = 1
```

Every subarray AND is always:

```text
1000000
```

So answer is:

```text
|1000000 - 1| = 999999
```

---

## Example 3

```text
arr = [1,2,4,8,16], target = 0
```

Some subarray ANDs quickly become `0`, for example:

```text
1 & 2 = 0
```

So exact target `0` is reachable, hence answer is:

```text
0
```

---

# Final Recommended Java Solution

This is the version I would submit.

```java
import java.util.*;

class Solution {
    public int closestToTarget(int[] arr, int target) {
        List<Integer> prev = new ArrayList<>();
        int ans = Integer.MAX_VALUE;

        for (int num : arr) {
            List<Integer> cur = new ArrayList<>();
            cur.add(num);
            ans = Math.min(ans, Math.abs(num - target));

            for (int x : prev) {
                int y = x & num;
                if (cur.get(cur.size() - 1) != y) {
                    cur.add(y);
                    ans = Math.min(ans, Math.abs(y - target));
                }
            }

            prev = cur;
        }

        return ans;
    }
}
```

---

# Comparison of Approaches

| Approach   | Main Idea                                             | Time Complexity | Space Complexity | Recommended         |
| ---------- | ----------------------------------------------------- | --------------: | ---------------: | ------------------- |
| Approach 1 | HashSet of distinct AND values per ending index       |      `O(n * B)` |           `O(B)` | Yes                 |
| Approach 2 | List of distinct AND values with manual deduplication |      `O(n * B)` |           `O(B)` | Yes, best practical |
| Approach 3 | Brute force all subarrays                             |        `O(n^2)` |           `O(1)` | No                  |
| Approach 4 | Range-AND structure + breakpoint search               |     Complicated |  Extra structure | No                  |

Here `B` is roughly the number of relevant bits, around `20`.

---

# Pattern Recognition Takeaway

This problem is a classic example of the pattern:

- subarray operation is bitwise AND / OR
- extending a subarray changes the value monotonically
- distinct results per endpoint stay small

Whenever you see this combination, think:

> keep the compressed set of distinct results for subarrays ending at the current index

That often turns an `O(n^2)` problem into near-linear time.

---

# Final Takeaway

The most effective way to solve this problem is:

1. iterate through the array
2. maintain all distinct subarray AND values ending at the current position
3. update them by AND-ing with the current number
4. deduplicate aggressively
5. track the minimum absolute difference to target

That gives an efficient and elegant `O(n * B)` solution.
