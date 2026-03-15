var maxCoins = function (nums) {
  const n = nums.length;
  const arr = [1, ...nums, 1];
  const dp = Array.from({ length: n + 2 }, () => Array(n + 2).fill(0));

  for (let len = 2; len <= n + 1; len++) {
    for (let left = 0; left <= n + 1 - len; left++) {
      const right = left + len;
      for (let k = left + 1; k < right; k++) {
        dp[left][right] = Math.max(
          dp[left][right],
          dp[left][k] + arr[left] * arr[k] * arr[right] + dp[k][right]
        );
      }
    }
  }

  return dp[0][n + 1];
};
