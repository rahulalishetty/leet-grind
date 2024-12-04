/**
 * @param {number} k
 * @param {number[]} prices
 * @return {number}
 */
var maxProfit = function (k, prices) {
  const n = prices.length,
    dp = [...Array(n)].map((e) => [...Array(k)].map((_) => Array(2).fill(-1)));

  function findMaxProfit(i, remainingTransactions, holding) {
    if (remainingTransactions === k || i === n) return 0;
    if (dp[i][remainingTransactions][holding] !== -1)
      return dp[i][remainingTransactions][holding];

    let doNothing = findMaxProfit(i + 1, remainingTransactions, holding),
      doSomething;

    if (holding) {
      doSomething =
        prices[i] + findMaxProfit(i + 1, remainingTransactions + 1, 0);
    } else {
      doSomething = findMaxProfit(i + 1, remainingTransactions, 1) - prices[i];
    }

    return (dp[i][remainingTransactions][holding] = Math.max(
      doNothing,
      doSomething
    ));
  }

  return findMaxProfit(0, 0, 0);
};
