/**
 * @param {number[]} prices
 * @return {number}
 */
var maxProfit = function (prices) {
  const n = prices.length,
    memo = Array(n)
      .fill()
      .map(() => Array(2).fill(-1));

  function dp(i, holding) {
    if (i === n) return 0;
    if (memo[i][Number(holding)] != -1) return memo[i][Number(holding)];

    let maxProfit = dp(i + 1, holding); // skip
    if (holding) {
      maxProfit = Math.max(maxProfit, dp(i + 1, false) + prices[i]); // sell
    } else {
      maxProfit = Math.max(maxProfit, dp(i + 1, true) - prices[i]); // buy
    }

    return (memo[i][Number(holding)] = maxProfit);
  }

  return dp(0, false);
};
