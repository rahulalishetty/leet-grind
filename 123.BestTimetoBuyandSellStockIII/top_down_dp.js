/**
 * @param {number[]} prices
 * @return {number}
 */
var maxProfit = function (prices) {
  const n = prices.length,
    memo = Array(n)
      .fill()
      .map(() =>
        Array(3)
          .fill()
          .map(() => Array(2).fill(-1))
      );

  function dp(i, holding, t) {
    if (t === 0) return 0;
    if (i === n) return 0;
    if (memo[i][t][Number(holding)] !== -1) return memo[i][t][Number(holding)];

    let maxProfit = dp(i + 1, holding, t); // skip
    if (holding) {
      maxProfit = Math.max(maxProfit, dp(i + 1, false, t - 1) + prices[i]); // sell
    } else {
      maxProfit = Math.max(maxProfit, dp(i + 1, true, t) - prices[i]); // buy
    }

    return (memo[i][t][Number(holding)] = maxProfit);
  }

  return dp(0, false, 2);
};
