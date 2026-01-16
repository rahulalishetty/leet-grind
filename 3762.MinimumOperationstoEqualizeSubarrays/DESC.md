# 3762. Minimum Operations to Equalize Subarrays

You are given:

- An integer array `nums`
- An integer `k`
- A list of range queries `queries`, where each `queries[i] = [li, ri]`

In **one operation**, you may **increase or decrease** any `nums[j]` by **exactly `k`**.

For each query `[li, ri]`, compute the **minimum number of operations** required to make all elements in the subarray `nums[li..ri]` **equal**. If it is **impossible**, return `-1`.

Return an array `ans` such that `ans[i]` corresponds to the `i`-th query.

---

## Examples

### Example 1

**Input**

- `nums = [1, 4, 7]`
- `k = 3`
- `queries = [[0, 1], [0, 2]]`

**Output**

- `ans = [1, 2]`

**Explanation (one optimal set of operations)**

|   i | [li, ri] | nums[li..ri] | Possible? | Operations                           | Final     | ans[i] |
| --: | :------: | :----------- | :-------: | :----------------------------------- | :-------- | -----: |
|   0 |  [0, 1]  | [1, 4]       |    Yes    | `nums[0] + k = 1 + 3 = 4`            | [4, 4]    |      1 |
|   1 |  [0, 2]  | [1, 4, 7]    |    Yes    | `nums[0] + k = 4`, `nums[2] - k = 4` | [4, 4, 4] |      2 |

Thus, `ans = [1, 2]`.

---

### Example 2

**Input**

- `nums = [1, 2, 4]`
- `k = 2`
- `queries = [[0, 2], [0, 0], [1, 2]]`

**Output**

- `ans = [-1, 0, 1]`

**Explanation (one optimal set of operations)**

|   i | [li, ri] | nums[li..ri] | Possible? | Operations                | Final     | ans[i] |
| --: | :------: | :----------- | :-------: | :------------------------ | :-------- | -----: |
|   0 |  [0, 2]  | [1, 2, 4]    |    No     | —                         | [1, 2, 4] |     -1 |
|   1 |  [0, 0]  | [1]          |    Yes    | Already equal             | [1]       |      0 |
|   2 |  [1, 2]  | [2, 4]       |    Yes    | `nums[1] + k = 2 + 2 = 4` | [4, 4]    |      1 |

Thus, `ans = [-1, 0, 1]`.

---

## Constraints

- `1 <= n == nums.length <= 4 * 10^4`
- `1 <= nums[i] <= 10^9`
- `1 <= k <= 10^9`
- `1 <= queries.length <= 4 * 10^4`
- `queries[i] = [li, ri]`
- `0 <= li <= ri <= n - 1`
