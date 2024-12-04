/**
 * @param {number} n
 * @param {number} k
 * @return {number}
 */
var numWays = function (n, k) {
  const dp = Array(n + 1).fill(-1);
  function findWays(i) {
    if (i === 1) return k;
    if (i === 2) return k * k;
    if (dp[i] !== -1) return dp[i];

    let count = (k - 1) * (findWays(i - 1) + findWays(i - 2));

    return (dp[i] = count);
  }

  return findWays(n);
};
