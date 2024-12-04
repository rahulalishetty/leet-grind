/**
 * @param {number} k
 * @param {number[]} prices
 * @return {number}
 */
var maxProfit = function (k, prices) {
  const n = prices.length,
    dp = [...Array(n + 1)].map((e) =>
      [...Array(k + 1)].map((_) => Array(2).fill(0))
    );

  for (let i = n - 1; i >= 0; i--) {
    for (let j = 1; j <= k; j++) {
      for (let h = 0; h < 2; h++) {
        let doNothing = dp[i + 1][j][h],
          doSomething;
        if (h) {
          doSomething = prices[i] + dp[i + 1][j - 1][0];
        } else {
          doSomething = dp[i + 1][j][1] - prices[i];
        }
        dp[i][j][h] = Math.max(doNothing, doSomething);
      }
    }
  }
  return dp[0][k][0];
};
