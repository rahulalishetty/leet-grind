# 368. Largest Divisible Subset

## Problem Statement

Given a set of **distinct positive integers** `nums`, return the **largest subset** `answer` such that for every pair `(answer[i], answer[j])`:

- `answer[i] % answer[j] == 0`, or
- `answer[j] % answer[i] == 0`

If there are multiple valid answers, return **any one of them**.

---

## Examples

### Example 1

**Input:**

```
nums = [1,2,3]
```

**Output:**

```
[1,2]
```

**Explanation:**

- `[1,3]` is also a valid answer.

---

### Example 2

**Input:**

```
nums = [1,2,4,8]
```

**Output:**

```
[1,2,4,8]
```

---

## Constraints

- `1 <= nums.length <= 1000`
- `1 <= nums[i] <= 2 * 10^9`
- All integers in `nums` are **unique**
