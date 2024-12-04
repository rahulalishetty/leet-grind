/**
 * @param {number[]} coins
 * @param {number} amount
 * @return {number}
 */
var coinChange = function (coins, amount) {
  const n = coins.length;
  const memo = Array(amount + 1).fill(-1);
  const dp = (sum) => {
    if (sum === 0) return 0;
    if (memo[sum] > -1) return memo[sum];

    let nCoins = Number.MAX_VALUE;
    for (let i = 0; i < n; i++) {
      if (sum - coins[i] >= 0)
        nCoins = Math.min(dp(sum - coins[i]) + 1, nCoins);
    }

    return (memo[sum] = nCoins);
  };
  const ans = dp(amount);
  return ans === Number.MAX_VALUE ? -1 : ans;
};
