/**
 * @param {number[]} prices
 * @param {number} fee
 * @return {number}
 */
var maxProfit = function (prices, fee) {
  const n = prices.length,
    dp = [...Array(n + 1)].map((e) => Array(2).fill(0));
  // dp[0][0] = 0;
  // dp[0][1] = -prices[0];

  for (let i = n - 1; i >= 0; i--) {
    for (let holding = 0; holding < 2; holding++) {
      let doNothing = dp[i + 1][holding],
        doSomething;

      if (holding) {
        doSomething = dp[i + 1][0] + prices[i] - fee;
      } else {
        doSomething = dp[i + 1][1] - prices[i];
      }
      dp[i][holding] = Math.max(doNothing, doSomething);
    }
  }

  return dp[0][0];
};
