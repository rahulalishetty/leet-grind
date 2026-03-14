var generateTrees = function (n) {
  let dp = Array.from(Array(n + 1), () => Array.from(Array(n + 1), () => []));
  for (let i = 0; i <= n; i++) {
    dp[i][i].push(new TreeNode(i));
  }
  for (let numberOfNodes = 2; numberOfNodes <= n; numberOfNodes++) {
    for (let start = 1; start <= n - numberOfNodes + 1; start++) {
      let end = start + numberOfNodes - 1;
      for (let i = start; i <= end; i++) {
        let leftSubtrees = i != start ? dp[start][i - 1] : [null];
        let rightSubtrees = i != end ? dp[i + 1][end] : [null];
        for (let left of leftSubtrees) {
          for (let right of rightSubtrees) {
            dp[start][end].push(new TreeNode(i, left, right));
          }
        }
      }
    }
  }
  return dp[1][n];
};
