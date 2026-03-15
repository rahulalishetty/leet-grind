# 3247. Number of Subsequences with Odd Sum

Given an array `nums`, return the number of subsequences with an odd sum of elements.

Since the answer may be very large, return it modulo 10<sup>9</sup> + 7.

Example 1:

Input: `nums = [1,1,1]`

Output: `4`

Explanation:
The odd-sum subsequences are: `[1, 1, 1]`, `[1, 1, 1]`, `[1, 1, 1]`, `[1, 1, 1]`.

Example 2:

Input: `nums = [1,2,2]`

Output: `4`

Explanation:
The odd-sum subsequences are: `[1, 2, 2]`, `[1, 2, 2]`, `[1, 2, 2]`, `[1, 2, 2]`.

Constraints:

- `1 <= nums.length <= 10<sup>5</sup>`
- `1 <= nums[i] <= 10<sup>9</sup>`
