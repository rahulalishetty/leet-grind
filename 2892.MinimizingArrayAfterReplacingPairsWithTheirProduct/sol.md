# Minimum Possible Array Length After Adjacent Product Merges

## Problem Restatement

You are given:

- an integer array `nums`
- an integer `k`

You may repeatedly perform the following operation:

- choose two **adjacent** elements `x` and `y`
- if `x * y <= k`, replace them with a single element equal to `x * y`

This shortens the array length by `1`.

Your goal is to find the **minimum possible final length** after performing the operation any number of times.

---

## Main Insight

The important thing to notice is that every merge replaces two adjacent values by their **product**.

So if all values are positive integers, then:

```text
x * y >= x
x * y >= y
```

That means a merged value is never smaller than the values that created it.

This gives a crucial monotonic property:

- if two adjacent blocks **can** merge now, merging them reduces the length immediately
- if two adjacent blocks **cannot** merge now because their product is already greater than `k`, then making either block larger later will never help

In other words:

> once a boundary becomes impossible to merge, it stays impossible forever

That is the foundation of the greedy solution.

---

## Why a Greedy Strategy Makes Sense

Suppose while scanning the array from left to right, you have already compressed some prefix into the smallest possible set of mergeable blocks.

Now you read the next number.

There are only two possibilities:

### Case 1: it can merge with the block immediately to its left

Then not merging would be wasteful, because:

- merging decreases the final length by `1`
- postponing the merge cannot unlock some better future option that requires keeping them separate
- if they are mergeable now, merging immediately is always safe

### Case 2: it cannot merge with the block immediately to its left

Then this boundary is permanent.
Since values only stay the same or grow after further merges, this pair will never become mergeable later.

So the optimal strategy is:

- keep each current merged block on a stack
- after adding a new value, repeatedly merge with the previous block while allowed

This is exactly the same pattern as maintaining a compressed representation of the prefix.

---

## Core Greedy Idea

Maintain a stack of block values.

Each value on the stack represents one contiguous segment of the original array that has already been fully merged as much as possible.

For every `num` in `nums`:

1. push `num` onto the stack
2. while the top two stack values can merge, merge them
3. continue until the top two can no longer merge

At the end:

- each stack element is one final block
- the number of blocks is the minimum possible array length

---

## Why the Stack Works

The stack represents the current array after greedily compressing the processed prefix.

When a new number arrives:

- only the boundary between the new block and the previous block can create a fresh merge opportunity
- if that merge happens, the newly formed product may again merge with the block before it
- so the only place where repeated work is needed is the top of the stack

That is why a stack is the natural data structure here.

---

## Step-by-Step Example

Consider:

```text
nums = [1, 2, 2, 3], k = 5
```

### Start

Stack is empty.

---

### Read `1`

Push it:

```text
[1]
```

No previous block exists, so nothing to merge.

---

### Read `2`

Push it:

```text
[1, 2]
```

Check top two:

```text
1 * 2 = 2 <= 5
```

They can merge, so replace them with `2`:

```text
[2]
```

---

### Read `2`

Push it:

```text
[2, 2]
```

Check top two:

```text
2 * 2 = 4 <= 5
```

They can merge:

```text
[4]
```

---

### Read `3`

Push it:

```text
[4, 3]
```

Check top two:

```text
4 * 3 = 12 > 5
```

Cannot merge.

Final stack:

```text
[4, 3]
```

Final answer:

```text
2
```

---

## Another Example

Consider:

```text
nums = [2, 2, 2], k = 4
```

### Read first `2`

Stack:

```text
[2]
```

### Read second `2`

Stack:

```text
[2, 2]
```

Since:

```text
2 * 2 = 4 <= 4
```

merge:

```text
[4]
```

### Read third `2`

Stack:

```text
[4, 2]
```

Now:

```text
4 * 2 = 8 > 4
```

cannot merge.

Final length is:

```text
2
```

This is optimal.

---

## Key Invariant

After processing the first `i` elements of `nums`, the stack stores the minimum-length representation obtainable from that prefix.

Additionally:

- no two adjacent stack elements are mergeable
- otherwise we would have merged them already

This invariant is what makes the greedy scan correct.

---

## Correctness Intuition

The correctness comes from these facts:

### 1. Merging decreases length immediately

Every valid merge removes one element from the array.

So if a merge is available and does not harm future possibilities, we should take it.

### 2. Products are monotonic

A merged block has value equal to the product of all elements inside it.
As more merges happen, block values never decrease.

So if two adjacent blocks have product greater than `k` now, they will never become mergeable later.

### 3. Only local reconsideration is needed

When a new element is appended to the processed prefix, the only new possible merges involve the right end.
That is why repeated merging at the top of the stack is sufficient.

Together, these imply the greedy stack process is optimal.

---

## Full Java Code

```java
import java.util.*;

class Solution {
    public int minArrayLength(int[] nums, int k) {
        Deque<Long> stack = new ArrayDeque<>();

        for (int num : nums) {
            stack.addLast((long) num);

            while (stack.size() >= 2) {
                long y = stack.removeLast();
                long x = stack.removeLast();

                if (x * y <= k) {
                    stack.addLast(x * y);
                } else {
                    stack.addLast(x);
                    stack.addLast(y);
                    break;
                }
            }
        }

        return stack.size();
    }
}
```

---

## Detailed Code Walkthrough

## 1. Stack declaration

```java
Deque<Long> stack = new ArrayDeque<>();
```

We use a stack to store the current merged blocks.

Why `Long` and not `Integer`?

Because when checking:

```java
x * y
```

the multiplication may overflow `int`, even if the final comparison is only against `k`.

Using `long` avoids accidental overflow.

---

## 2. Iterate through the array

```java
for (int num : nums) {
    stack.addLast((long) num);
```

Each number initially starts as its own block.

We push it to the stack as the newest block.

---

## 3. Repeatedly merge while possible

```java
while (stack.size() >= 2) {
    long y = stack.removeLast();
    long x = stack.removeLast();

    if (x * y <= k) {
        stack.addLast(x * y);
    } else {
        stack.addLast(x);
        stack.addLast(y);
        break;
    }
}
```

This loop checks the top two blocks.

### If `x * y <= k`

They can merge, so we replace them by their product.

This may create another merge opportunity with the new top of the stack, so we continue the loop.

### If `x * y > k`

They cannot merge.
We restore them and stop.

Why stop immediately?

Because only the top boundary was under reconsideration.
If the top two cannot merge now, there is no reason to look deeper:

- all earlier boundaries were already settled
- the stack invariant says they were already non-mergeable

---

## 4. Final answer

```java
return stack.size();
```

Every stack entry is one final surviving block.

So the minimum possible length is simply the number of blocks left.

---

## Complexity Analysis

Let `n = nums.length`.

### Time Complexity: `O(n)`

At first, the repeated inner `while` loop may look expensive.

But each iteration of that loop does one of two things:

- it performs a successful merge, which reduces the number of blocks
- or it fails once and breaks

Each original element is:

- pushed once
- popped/merged a limited number of times

A successful merge reduces the stack size by `1`, and there can be at most `n - 1` successful merges overall.

So the total number of stack operations across the entire algorithm is linear.

Therefore:

```text
Time Complexity = O(n)
```

---

### Space Complexity: `O(n)`

In the worst case, no merges are possible, so all elements remain on the stack.

Thus:

```text
Space Complexity = O(n)
```

---

## Why This Is Better Than Brute Force

A brute-force approach might try all possible valid sequences of adjacent merges.

That is not practical because:

- at each step there may be several choices
- different merge orders can branch combinatorially
- the number of possible sequences grows very quickly

The greedy stack solution avoids exploring merge orders explicitly.

Instead, it uses the monotonic product property to prove that only one compression behavior matters:

- merge immediately whenever possible at the current right boundary

That collapses the problem to a linear scan.

---

## Subtle but Important Detail

The algorithm is not just saying:

> merge any adjacent pair you see

It is saying something more precise:

> process left to right, and maintain the already processed prefix in its optimally compressed form

That distinction matters because the stack is preserving a strong invariant about the prefix.

This is what makes the solution principled rather than ad hoc.

---

## Final Takeaway

The heart of the problem is the monotonic behavior of products.

Because merged values never decrease:

- an impossible merge stays impossible forever
- a possible merge should be taken immediately

That leads naturally to a greedy stack algorithm:

- push each number
- keep merging the top two blocks while allowed
- answer is the number of blocks left

This yields:

- **Time:** `O(n)`
- **Space:** `O(n)`

and is the optimal practical solution.
