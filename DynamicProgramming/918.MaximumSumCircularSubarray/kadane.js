/**
 * @param {number[]} nums
 * @return {number}
 */
var maxSubarraySumCircular = function (nums) {
  let curMax = 0,
    max = nums[0],
    n = nums.length,
    curMin = 0,
    min = nums[0],
    totalSum = 0;

  for (let i = 0; i < n; i++) {
    curMax = Math.max(curMax, 0) + nums[i];
    max = Math.max(curMax, max);

    curMin = Math.min(curMin, 0) + nums[i];
    min = Math.min(min, curMin);

    totalSum += nums[i];
  }

  if (totalSum === min) return max;

  return Math.max(max, totalSum - min);
};
