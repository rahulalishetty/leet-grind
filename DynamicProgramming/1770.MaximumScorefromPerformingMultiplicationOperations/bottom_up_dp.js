/**
 * @param {number[]} nums
 * @param {number[]} multipliers
 * @return {number}
 */
var maximumScore = function (nums, multipliers) {
  const n = nums.length,
    m = multipliers.length,
    dp = [...Array(m + 1)].map((e) => Array(m + 1).fill(0));

  for (let i = m - 1; i >= 0; i--) {
    for (let left = i; left >= 0; left--) {
      const right = n - 1 - (i - left),
        mul = multipliers[i];
      dp[i][left] = Math.max(
        mul * nums[left] + dp[i + 1][left + 1],
        mul * nums[right] + dp[i + 1][left]
      );
    }
  }
  return dp[0][0];
};
