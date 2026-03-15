/**
 * @param {character[][]} matrix
 * @return {number}
 */
var maximalSquare = function (matrix) {
  const n = matrix.length;
  const m = matrix[0].length;
  const memo = [...Array(n)].map((e) => Array(m).fill(-1));
  let area = 0;
  const dp = (i, j) => {
    if (i < 0 || j < 0) return 0;

    if (memo[i][j] > -1) return memo[i][j];

    memo[i][j] =
      Math.min(dp(i - 1, j), dp(i, j - 1), dp(i - 1, j - 1)) +
      (Number(matrix[i][j]) === 1 ? 1 : 0);

    if (Number(matrix[i][j]) === 0) memo[i][j] = 0;
    if (area < memo[i][j]) area = memo[i][j];

    // console.log(i, j, matrix[i][j], memo[i][j])
    return memo[i][j];
  };

  dp(n - 1, m - 1);
  return area * area;
};
