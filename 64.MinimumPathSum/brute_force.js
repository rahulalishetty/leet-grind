var calculate = function (grid, i, j) {
  if (i == grid.length || j == grid[0].length) return Number.MAX_SAFE_INTEGER;
  if (i == grid.length - 1 && j == grid[0].length - 1) return grid[i][j];
  return (
    grid[i][j] + Math.min(calculate(grid, i + 1, j), calculate(grid, i, j + 1))
  );
};
var minPathSum = function (grid) {
  return calculate(grid, 0, 0);
};
