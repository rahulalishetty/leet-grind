/**
 * @param {number[]} prices
 * @param {number} fee
 * @return {number}
 */
var maxProfit = function (prices, fee) {
  const n = prices.length,
    dp = [...Array(n)].map((e) => Array(2).fill(-1));

  function findMaxProfit(i, holding) {
    if (i === n) {
      if (holding) return -Infinity;
      return 0;
    }
    if (dp[i][holding] !== -1) return dp[i][holding];

    let doNothing = findMaxProfit(i + 1, holding),
      doSomething;

    if (holding) {
      doSomething = findMaxProfit(i + 1, 0) + prices[i] - fee;
    } else {
      doSomething = findMaxProfit(i + 1, 1) - prices[i];
    }

    return (dp[i][holding] = Math.max(doNothing, doSomething));
  }
  return findMaxProfit(0, 0);
};
