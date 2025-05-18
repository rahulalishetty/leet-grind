function countColorings(m, n) {
  const MOD = 1e9 + 7;
  let total = 0;

  function isValid(grid) {
    for (let i = 0; i < m; i++) {
      for (let j = 0; j < n; j++) {
        if (i > 0 && grid[i][j] === grid[i - 1][j]) return false;
        if (j > 0 && grid[i][j] === grid[i][j - 1]) return false;
      }
    }
    return true;
  }

  function dfs(i, j, grid) {
    if (i === m) {
      if (isValid(grid)) total = (total + 1) % MOD;
      return;
    }

    for (let color = 0; color < 3; color++) {
      grid[i][j] = color;
      let [ni, nj] = j === n - 1 ? [i + 1, 0] : [i, j + 1];
      dfs(ni, nj, grid);
    }
  }

  let grid = Array.from({ length: m }, () => Array(n).fill(0));
  dfs(0, 0, grid);
  return total;
}
