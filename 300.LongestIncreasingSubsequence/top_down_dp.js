/**
 * @param {number[]} nums
 * @return {number}
 */
var lengthOfLIS = function (nums) {
  const n = nums.length,
    dp = {};

  function find(i, prev) {
    if (i == n) return 0;
    if (dp[`${(i, prev)}`]) return dp[`${(i, prev)}`];

    const noTake = find(i + 1, prev);
    let take = 0;
    if (prev === -1 || nums[i] > nums[prev]) take = find(i + 1, i) + 1;

    return (dp[`${(i, prev)}`] = Math.max(take, noTake));
  }

  return find(0, -1);
};
