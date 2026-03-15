/**
 * @param {number} amount
 * @param {number[]} coins
 * @return {number}
 */
var change = function (amount, coins) {
  const dp = [...Array(amount + 1)].map((e) => Array(coins.length).fill(-1));

  function findWays(value, i) {
    if (value === 0) return 1;
    if (i === coins.length) return 0;
    if (dp[value][i] !== -1) return dp[value][i];
    if (coins[i] > value) {
      return (dp[value][i] = findWays(value, i + 1));
    }

    let count = findWays(value - coins[i], i) + findWays(value, i + 1);

    return (dp[value][i] = count);
  }

  return findWays(amount, 0);
};
