/**
 * @param {number[]} prices
 * @return {number}
 */
var maxProfit = function (prices) {
  const n = prices.length;

  function dp(i, minPrice, maxProfit) {
    if (i === n) return maxProfit;

    const profit = prices[i] - minPrice;
    return dp(
      i + 1,
      Math.min(minPrice, prices[i]),
      Math.max(profit, maxProfit)
    );
  }

  return dp(0, Infinity, 0);
};
