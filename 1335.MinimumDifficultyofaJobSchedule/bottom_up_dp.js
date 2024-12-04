/**
 * @param {number[]} jobDifficulty
 * @param {number} d
 * @return {number}
 */
var minDifficulty = function (jobDifficulty, d) {
  const n = jobDifficulty.length,
    dp = [...Array(d + 1)].map((e) => Array(n + 1).fill(Infinity));
  for (let i = 0; i <= d; i++) dp[i][n] = 0;

  for (let days = 1; days <= d; days++) {
    for (let i = 0; i < n - days + 1; i++) {
      let curMax = 0;
      for (let j = i + 1; j < n - days + 2; j++) {
        curMax = Math.max(curMax, jobDifficulty[j - 1]);
        dp[days][i] = Math.min(dp[days][i], curMax + dp[days - 1][j]);
      }
    }
  }

  return dp[d][0] === Infinity ? -1 : dp[d][0];
};
