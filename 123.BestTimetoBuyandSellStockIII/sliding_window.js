var maxProfit = function (prices) {
  if (prices.length <= 1) return 0;
  let left_min = prices[0];
  let right_max = prices[prices.length - 1];
  let length = prices.length;
  let left_profits = new Array(length).fill(0);
  let right_profits = new Array(length + 1).fill(0);
  for (let l = 1; l < length; ++l) {
    left_profits[l] = Math.max(left_profits[l - 1], prices[l] - left_min);
    left_min = Math.min(left_min, prices[l]);
    let r = length - 1 - l;
    right_profits[r] = Math.max(right_profits[r + 1], right_max - prices[r]);
    right_max = Math.max(right_max, prices[r]);
  }
  let max_profit = 0;
  for (let i = 0; i < length; ++i) {
    max_profit = Math.max(max_profit, left_profits[i] + right_profits[i + 1]);
  }
  return max_profit;
};
