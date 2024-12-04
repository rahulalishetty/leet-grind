/**
 * @param {number[][]} matrix
 * @return {number}
 */
var minFallingPathSum = function (grid) {
  const n = grid.length,
    m = grid[0].length;
  let min = Infinity;

  if (n === 1) {
    return grid[0].reduce((a, acc) => Math.min(acc, a), Infinity);
  }

  for (let i = 1; i < n; i++) {
    for (let j = 0; j < m; j++) {
      grid[i][j] += Math.min(
        j - 1 >= 0 ? grid[i - 1][j - 1] : Infinity,
        grid[i - 1][j],
        j + 1 < m ? grid[i - 1][j + 1] : Infinity
      );

      if (i === n - 1) {
        min = Math.min(grid[i][j], min);
      }
    }
  }
  return min;
};
