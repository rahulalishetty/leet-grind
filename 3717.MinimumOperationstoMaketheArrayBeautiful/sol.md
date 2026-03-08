# Minimum Operations to Make the Array Beautiful

## Problem

You are given an integer array `nums`.

An array is called **beautiful** if for every index `i > 0`:

`nums[i]` is divisible by `nums[i - 1]`

That means for every adjacent pair:

`nums[i] % nums[i - 1] == 0`

You are allowed to perform the following operation any number of times:

- choose any index `i > 0`
- increment `nums[i]` by `1`

We need to return the **minimum number of operations** required to make the array beautiful.

---

# 1. What the problem is really asking

We want to transform the array so that each element becomes a multiple of the element immediately before it.

So after all operations, the final array must satisfy:

- `nums[1]` is a multiple of `nums[0]`
- `nums[2]` is a multiple of `nums[1]`
- `nums[3]` is a multiple of `nums[2]`
- and so on

The important restriction is:

- we **cannot** modify `nums[0]`
- for every `i > 0`, we can only **increase** `nums[i]`

That means once we decide the final value of `nums[i - 1]`, the only thing we can do for `nums[i]` is move it **upward** until it becomes divisible by that previous fixed value.

That immediately suggests a greedy left-to-right approach.

---

# 2. Core intuition

## 2.1 Why left-to-right is natural

Look at index `i`.

The divisibility condition involving this index is:

`nums[i] % nums[i - 1] == 0`

When we process index `i`, the value of `nums[i - 1]` is already fixed:

- either it was the original value (`i - 1 = 0`)
- or we already adjusted it earlier

Since we are not allowed to decrease anything, and we are only allowed to increment `nums[i]`, there is only one meaningful choice:

> Increase `nums[i]` to the **smallest multiple** of `nums[i - 1]` that is at least the current value.

That is the cheapest valid choice.

---

## 2.2 Why choosing a larger multiple is never good

Suppose the previous fixed value is `prev`, and the current value is `curr`.

We need the final value of `curr` to be some multiple of `prev`:

- `prev`
- `2 * prev`
- `3 * prev`
- ...

But we are only allowed to increase, so we need the **smallest multiple ≥ curr**.

If we choose a larger multiple than necessary:

- we spend more operations immediately
- we make this element larger
- that can only make the next element’s divisibility requirement harder, not easier

So there is never any benefit in overshooting.

This is the key greedy insight.

---

# 3. Greedy strategy

Process the array from left to right.

For each index `i` from `1` to `n - 1`:

1. Let `prev = final value of nums[i - 1]`
2. Let `curr = current value of nums[i]`
3. Find the smallest value `newCurr >= curr` such that:

   `newCurr % prev == 0`

4. Add `newCurr - curr` to the answer
5. Replace the effective value of `nums[i]` with `newCurr`
6. Continue to the next index

That produces the minimum total number of increments.

---

# 4. How to compute the needed increment

We want the smallest multiple of `prev` that is at least `curr`.

## Case 1: `curr` is already divisible by `prev`

If:

`curr % prev == 0`

then we do not need any operations.

Increment needed:

`0`

---

## Case 2: `curr` is not divisible by `prev`

Let:

`r = curr % prev`

Then `curr` is `r` above the previous lower multiple of `prev`.

So to reach the next multiple, we need:

`prev - r`

extra increments.

So the added amount is:

`prev - (curr % prev)`

---

## Compact formula

A common compact expression is:

`add = (prev - (curr % prev)) % prev`

Why this works:

- if `curr % prev == 0`, then `add = 0`
- otherwise it becomes exactly `prev - (curr % prev)`

So this single formula handles both cases.

---

# 5. Why the greedy algorithm is correct

We should justify this carefully.

## Claim

When processing index `i`, increasing `nums[i]` to the **smallest valid multiple** of the already-fixed `nums[i - 1]` is always optimal.

## Reason

At index `i`, the only condition we must satisfy is:

`nums[i]` must be divisible by `nums[i - 1]`

Since `nums[i - 1]` is already fixed, every valid final value of `nums[i]` must be one of the multiples of `nums[i - 1]` that are at least the current `nums[i]`.

Among all such valid choices, the smallest one:

- uses the fewest increments at index `i`
- leaves `nums[i]` as small as possible
- therefore cannot make later constraints harder than a larger choice would

So any larger choice is never better.

This means the locally optimal choice is also globally optimal.

---

# 6. Another way to see the proof

Suppose at some index `i`, an alleged optimal solution increases `nums[i]` to a value larger than the smallest valid multiple.

Call:

- `best` = smallest valid multiple
- `larger` = some larger chosen multiple

Then:

- `larger > best`
- using `best` would use fewer operations at index `i`
- and for index `i + 1`, making the previous value smaller cannot create a worse situation, because the next element only needs to be divisible by this previous value, and smaller divisors are easier to hit than larger ones

So replacing `larger` with `best` cannot hurt future feasibility and strictly improves or preserves the total cost.

Contradiction.

Hence the smallest valid multiple is optimal.

---

# 7. Step-by-step example

Consider:

`nums = [3, 5, 8]`

We process left to right.

---

## Index 1

Previous fixed value:

`prev = 3`

Current value:

`curr = 5`

We need the smallest multiple of `3` that is at least `5`.

Multiples of `3` are:

- `3`
- `6`
- `9`
- ...

The smallest valid one is:

`6`

So increment needed:

`6 - 5 = 1`

Array effectively becomes:

`[3, 6, 8]`

Operations so far:

`1`

---

## Index 2

Now previous fixed value is the adjusted value:

`prev = 6`

Current value:

`curr = 8`

We need the smallest multiple of `6` that is at least `8`.

Multiples of `6`:

- `6`
- `12`
- `18`
- ...

The smallest valid one is:

`12`

So increment needed:

`12 - 8 = 4`

Array effectively becomes:

`[3, 6, 12]`

Operations total:

`1 + 4 = 5`

Final answer:

`5`

---

# 8. Another example

Consider:

`nums = [2, 4, 7]`

## Index 1

- `prev = 2`
- `curr = 4`
- `4 % 2 == 0`

No change needed.

## Index 2

- `prev = 4`
- `curr = 7`
- next multiple of `4` after `7` is `8`

So increment needed:

`1`

Final array:

`[2, 4, 8]`

Total operations:

`1`

---

# 9. Edge cases

## Case 1: array of length 1

If the array has only one element, there are no indices `i > 0`.

So the array is already beautiful.

Answer:

`0`

---

## Case 2: element already divisible

If `nums[i] % nums[i - 1] == 0`, then no operation is needed for that index.

---

## Case 3: cascading growth

Sometimes making one element larger forces later elements to grow much more.

Example:

`[4, 5, 6]`

- index 1: `5` must become `8` → add `3`
- index 2: now `6` must be divisible by `8`, so it becomes `8` → add `2`

Total = `5`

This is why the adjusted value must be carried forward.

---

# 10. Java code

```java
class Solution {
    public int minOperations(int[] nums) {
        long ops = 0;
        long prev = nums[0];

        for (int i = 1; i < nums.length; i++) {
            long curr = nums[i];
            long add = (prev - (curr % prev)) % prev;
            ops += add;
            prev = curr + add;
        }

        return ops;
    }
}
```

---

# 11. Code explanation in detail

## Method signature

```java
public long minOperations(int[] nums)
```

This returns the minimum number of increments required.

`long` is used for the answer because the total number of operations may become large.

---

## Initialize answer and previous value

```java
long ops = 0;
long prev = nums[0];
```

- `ops` stores the running total of increments
- `prev` stores the effective final value of the previous element

Initially, the first element cannot be changed, so its fixed value is simply `nums[0]`.

---

## Traverse the array

```java
for (int i = 1; i < nums.length; i++) {
```

We process each element starting from index `1`.

---

## Read current value

```java
long curr = nums[i];
```

This is the current value before adjustment.

---

## Compute required increment

```java
long add = (prev - (curr % prev)) % prev;
```

This gives the minimum nonnegative number to add so that:

`(curr + add) % prev == 0`

### Why this formula works

- if `curr % prev == 0`, then:
  - `add = (prev - 0) % prev = 0`
- otherwise:
  - `add = prev - (curr % prev)`

So it exactly computes the gap to the next multiple.

---

## Add to answer

```java
ops += add;
```

We accumulate the number of operations.

---

## Update previous value

```java
prev = curr + add;
```

Now the adjusted value at index `i` becomes the new fixed previous value for the next step.

This is crucial because later divisibility checks use this updated number.

---

## Return result

```java
return ops;
```

After all indices are processed, `ops` is the minimum total cost.

---

# 12. Complexity analysis

Let `n` be the array length.

## Time complexity

We scan the array once.

At each index, we do only constant-time arithmetic:

- one modulo
- a few additions/subtractions
- one assignment

So:

**Time complexity: `O(n)`**

---

## Space complexity

We use only a few variables:

- `ops`
- `prev`
- `curr`
- `add`

No extra arrays or data structures are used.

So:

**Space complexity: `O(1)`**

---

# 13. Why dynamic programming is unnecessary

At first glance, this may look like a DP problem because each choice affects future elements.

But the choice at each step is actually forced:

- once `nums[i - 1]` is fixed
- the cheapest valid value for `nums[i]` is uniquely determined

There is no real branching worth exploring.

That is why a simple greedy approach is enough.

---

# 14. Why brute force is impossible

A brute-force method would try different increased values for each element and test all possible combinations.

That would explode combinatorially.

But the divisibility requirement plus the “increment only” restriction makes the optimal move at each step obvious:

- always take the nearest valid multiple

So brute force is unnecessary.

---

# 15. Final takeaway

This problem becomes simple once you notice:

- each element depends only on the final value of the previous one
- you can only increase, never decrease
- for each index, the optimal target is the **smallest multiple** of the previous adjusted value that is at least the current value

So the algorithm is:

1. fix the first element
2. scan left to right
3. raise each element to the next valid multiple of the previous adjusted value
4. sum all increments

That greedy strategy is both correct and optimal.

---
