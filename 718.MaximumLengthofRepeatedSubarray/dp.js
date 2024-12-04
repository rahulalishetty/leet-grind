/**
 * @param {number[]} nums1
 * @param {number[]} nums2
 * @return {number}
 */
var findLength = function (nums1, nums2) {
  const n = nums1.length,
    m = nums2.length,
    dp = [...Array(n + 1)].map((e) => Array(m + 1).fill(0));
  let maxLen = 0;

  for (let i = n - 1; i >= 0; i--) {
    for (let j = n - 1; j >= 0; j--) {
      if (nums1[i] === nums2[j]) {
        dp[i][j] = Math.max(dp[i][j], dp[i + 1][j + 1] + 1);
        maxLen = Math.max(maxLen, dp[i][j]);
      }
    }
  }

  return maxLen;
};
