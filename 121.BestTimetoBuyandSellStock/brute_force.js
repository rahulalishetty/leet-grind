var maxProfit = function (prices) {
  let maxprofit = 0;
  for (let i = 0; i < prices.length - 1; i++) {
    for (let j = i + 1; j < prices.length; j++) {
      let profit = prices[j] - prices[i];
      if (profit > maxprofit) maxprofit = profit;
    }
  }
  return maxprofit;
};
