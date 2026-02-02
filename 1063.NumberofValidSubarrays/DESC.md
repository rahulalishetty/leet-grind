# 1063. Number of Valid Subarrays

Given an integer array `nums`, return the number of non-empty subarrays where the leftmost element of the subarray is not larger than any other element in the subarray.

A subarray is a contiguous part of an array.

## Examples

### Example 1

**Input:** `nums = [1,4,2,5,3]`
**Output:** `11`
**Explanation:**
The 11 valid subarrays are:
`[1]`, `[4]`, `[2]`, `[5]`, `[3]`, `[1,4]`, `[2,5]`, `[1,4,2]`, `[2,5,3]`, `[1,4,2,5]`, `[1,4,2,5,3]`.

### Example 2

**Input:** `nums = [3,2,1]`
**Output:** `3`
**Explanation:**
The 3 valid subarrays are: `[3]`, `[2]`, `[1]`.

### Example 3

**Input:** `nums = [2,2,2]`
**Output:** `6`
**Explanation:**
The 6 valid subarrays are: `[2]`, `[2]`, `[2]`, `[2,2]`, `[2,2]`, `[2,2,2]`.

## Constraints

- `1 <= nums.length <= 5 * 10^4`
- `0 <= nums[i] <= 10^5`
