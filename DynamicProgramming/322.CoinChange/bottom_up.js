/**
 * @param {number[]} coins
 * @param {number} amount
 * @return {number}
 */
var coinChange = function (coins, amount) {
  const n = coins.length;
  const memo = Array(amount + 1).fill(Number.MAX_VALUE);
  memo[0] = 0;

  for (let i = 1; i <= amount; i++) {
    for (let j = 0; j < n; j++) {
      if (i - coins[j] >= 0)
        memo[i] = Math.min(memo[i], memo[i - coins[j]] + 1);
    }
  }

  return memo[amount] === Number.MAX_VALUE ? -1 : memo[amount];
};
