# 493. Reverse Pairs

`Hard`

Given an integer array nums, return the number of reverse pairs in the array.

A reverse pair is a pair (i, j) where:

```note
0 <= i < j < nums.length and
nums[i] > 2 * nums[j].
```

Example 1:

```note
Input: nums = [1,3,2,3,1]
Output: 2
Explanation: The reverse pairs are:
(1, 4) --> nums[1] = 3, nums[4] = 1, 3 > 2 _ 1
(3, 4) --> nums[3] = 3, nums[4] = 1, 3 > 2 _ 1
```

Example 2:

```note
Input: nums = [2,4,3,5,1]
Output: 3
Explanation: The reverse pairs are:
(1, 4) --> nums[1] = 4, nums[4] = 1, 4 > 2 _ 1
(2, 4) --> nums[2] = 3, nums[4] = 1, 3 > 2 _ 1
(3, 4) --> nums[3] = 5, nums[4] = 1, 5 > 2 * 1
```

Constraints:

```note
1 <= nums.length <= 5 * 104
-231 <= nums[i] <= 231 - 1
```
