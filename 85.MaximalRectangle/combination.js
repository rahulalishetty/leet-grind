// uses logic from  84 - Largest Rectangle in Histogram.
var maximalRectangle = function (matrix) {
  if (matrix.length === 0) return 0;
  let maxarea = 0;
  let dp = Array(matrix.length)
    .fill(0)
    .map(() => Array(matrix[0].length).fill(0));
  for (let i = 0; i < matrix.length; i++) {
    for (let j = 0; j < matrix[0].length; j++) {
      if (matrix[i][j] === "1") {
        // compute the maximum width and update dp with it
        dp[i][j] = j === 0 ? 1 : dp[i][j - 1] + 1;
        let width = dp[i][j];
        // compute the maximum area rectangle with a lower right corner at [i, j]
        for (let k = i; k >= 0; k--) {
          width = Math.min(width, dp[k][j]);
          maxarea = Math.max(maxarea, width * (i - k + 1));
        }
      }
    }
  }
  return maxarea;
};
