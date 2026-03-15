var uniquePaths = function (m, n) {
  let d = new Array(m).fill(0).map(() => new Array(n).fill(1));
  for (let col = 1; col < m; ++col) {
    for (let row = 1; row < n; ++row) {
      d[col][row] = d[col - 1][row] + d[col][row - 1];
    }
  }
  return d[m - 1][n - 1];
};
