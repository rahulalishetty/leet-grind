/**
 * @param {string} text1
 * @param {string} text2
 * @return {number}
 */
var longestCommonSubsequence = function (text1, text2) {
  const n = text1.length,
    m = text2.length,
    a = text1,
    b = text2,
    dp = [...Array(n + 1)].map((e) => Array(m + 1).fill(0));

  for (let i = 1; i <= n; i++) {
    for (let j = 1; j <= m; j++) {
      dp[i][j] =
        a[i - 1] === b[j - 1]
          ? 1 + dp[i - 1][j - 1]
          : Math.max(dp[i - 1][j], dp[i][j - 1]);
    }
  }

  return dp[n][m];
};
