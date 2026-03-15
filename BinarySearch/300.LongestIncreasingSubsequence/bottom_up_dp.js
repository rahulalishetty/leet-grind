/**
 * @param {number[]} nums
 * @return {number}
 */
var lengthOfLIS = function (nums) {
  const n = nums.length,
    dp = Array(n).fill(1);
  let maxLen = 1;

  for (let i = 1; i < n; i++) {
    for (let j = 0; j < i; j++) {
      if (nums[j] < nums[i]) {
        dp[i] = Math.max(dp[i], dp[j] + 1);
        maxLen = Math.max(maxLen, dp[i]);
      }
    }
  }

  return maxLen;
};
