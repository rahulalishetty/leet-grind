/**
 * @param {number[]} prices
 * @return {number}
 */
var maxProfit = function (prices) {
  const n = prices.length,
    dp = [...Array(n)].map((e) => Array(2).fill(-1));
  console.log(dp);

  function findProfit(i, holding) {
    if (i >= n) return 0;
    if (dp[i][holding] !== -1) return dp[i][holding];

    let doNothing = findProfit(i + 1, holding),
      doSomething;

    if (holding) {
      doSomething = prices[i] + findProfit(i + 2, 0);
    } else {
      doSomething = findProfit(i + 1, 1) - prices[i];
    }

    return (dp[i][holding] = Math.max(doSomething, doNothing));
  }

  return findProfit(0, 0);
};
