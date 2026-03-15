/**
 * @param {number[][]} obstacleGrid
 * @return {number}
 */
var uniquePathsWithObstacles = function (obstacleGrid) {
  const m = obstacleGrid.length,
    n = obstacleGrid[0].length;
  let dp = [...Array(m)].map((e) => Array(n).fill(0));
  dp[0][0] = obstacleGrid[0][0] ? 0 : 1;

  for (let i = 0; i < m; i++) {
    for (let j = 0; j < n; j++) {
      if (!obstacleGrid[i][j]) {
        if (i !== 0) {
          dp[i][j] += dp[i - 1][j];
        }
        if (j !== 0) {
          dp[i][j] += dp[i][j - 1];
        }
      }
    }
  }

  return dp[m - 1][n - 1];
};
