/**
 * @param {number} n
 * @param {number} k
 * @param {number} target
 * @return {number}
 */
var numRollsToTarget = function (n, k, target) {
  const dp = [...Array(n)].map((e) => Array(target).fill(-1)),
    mod = 1e9 + 7;
  function find(i, sum) {
    if (i === n) {
      if (sum === target) return 1;
      else return 0;
    }
    if (sum >= target) return 0;
    if (dp[i][sum] !== -1) return dp[i][sum];

    let count = 0;
    for (let j = 1; j <= k; j++) {
      count = (count + find(i + 1, sum + j)) % mod;
    }

    return (dp[i][sum] = count);
  }

  return find(0, 0);
};
