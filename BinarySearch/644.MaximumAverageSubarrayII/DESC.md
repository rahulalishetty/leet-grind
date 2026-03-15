# 644. Maximum Average Subarray II

You are given an integer array `nums` consisting of `n` elements, and an integer `k`.

Find a **contiguous subarray** whose length is **greater than or equal to `k`** that has the **maximum average value**, and return this value.

Any answer with a **calculation error less than 10^-5** will be accepted.

---

## Example 1

**Input**

```
nums = [1,12,-5,-6,50,3]
k = 4
```

**Output**

```
12.75000
```

**Explanation**

- When the length is `4`, averages are:

  ```
  [0.5, 12.75, 10.5]
  ```

  The maximum average is **12.75**.

- When the length is `5`, averages are:

  ```
  [10.4, 10.8]
  ```

  The maximum average is **10.8**.

- When the length is `6`, averages are:

  ```
  [9.16667]
  ```

  The maximum average is **9.16667**.

The **maximum average occurs with the subarray**:

```
[12, -5, -6, 50]
```

Average:

```
12.75
```

Subarrays with length **less than `k` are not considered**.

---

## Example 2

**Input**

```
nums = [5]
k = 1
```

**Output**

```
5.00000
```

---

## Constraints

- `n == nums.length`
- `1 <= k <= n <= 10^4`
- `-10^4 <= nums[i] <= 10^4`
