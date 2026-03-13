# 2607. Make K-Subarray Sums Equal

You are given a **0-indexed integer array `arr`** and an integer `k`.

The array is **circular**, which means:

- the element after the last element is the first element
- the element before the first element is the last element

You may perform the following operation any number of times:

- pick any element of `arr`
- increase it by `1`, or
- decrease it by `1`

Return the **minimum number of operations** required so that the sum of **every subarray of length `k`** is equal.

A **subarray** is a contiguous part of the array.

---

## Example 1

**Input**

```text
arr = [1,4,1,3], k = 2
```

**Output**

```text
1
```

**Explanation**

We can decrease `arr[1]` from `4` to `3`.

The array becomes:

```text
[1,3,1,3]
```

Now every circular subarray of length `2` has sum `4`:

- starting at `0`: `[1,3]` → `4`
- starting at `1`: `[3,1]` → `4`
- starting at `2`: `[1,3]` → `4`
- starting at `3`: `[3,1]` → `4`

So the minimum number of operations is `1`.

---

## Example 2

**Input**

```text
arr = [2,5,5,7], k = 3
```

**Output**

```text
5
```

**Explanation**

We can:

- increase `arr[0]` from `2` to `5` using `3` operations
- decrease `arr[3]` from `7` to `5` using `2` operations

The array becomes:

```text
[5,5,5,5]
```

Now every circular subarray of length `3` has sum `15`.

So the minimum number of operations is `5`.

---

## Constraints

```text
1 <= k <= arr.length <= 10^5
1 <= arr[i] <= 10^9
```
