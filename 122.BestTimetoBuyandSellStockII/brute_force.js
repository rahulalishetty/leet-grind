var maxProfit = function (prices) {
  return calculate(prices, 0);
};
var calculate = function (prices, s) {
  if (s >= prices.length) return 0;
  let max = 0;
  for (let start = s; start < prices.length; start++) {
    let maxprofit = 0;
    for (let i = start + 1; i < prices.length; i++) {
      if (prices[start] < prices[i]) {
        let profit = calculate(prices, i + 1) + prices[i] - prices[start];
        if (profit > maxprofit) maxprofit = profit;
      }
    }
    if (maxprofit > max) max = maxprofit;
  }
  return max;
};
