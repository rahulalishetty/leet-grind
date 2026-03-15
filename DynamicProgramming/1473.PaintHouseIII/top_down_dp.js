/**
 * @param {number[]} houses
 * @param {number[][]} cost
 * @param {number} m
 * @param {number} n
 * @param {number} target
 * @return {number}
 */
var minCost = function (houses, cost, m, n, target) {
  const dp = [...Array(m)].map((e) =>
    [...Array(n + 1)].map((f) => Array(target + 1).fill(-1))
  );
  function findMinCost(i, prev, remain) {
    if (i === m) {
      if (remain === 0) return 0;
      return Infinity;
    }
    if (remain < 0) return Infinity;
    if (dp[i][prev][remain] !== -1) return dp[i][prev][remain];

    let minCost = Infinity;

    if (houses[i]) {
      minCost = Math.min(
        minCost,
        findMinCost(i + 1, houses[i], prev !== houses[i] ? remain - 1 : remain)
      );
    } else {
      for (let j = 1; j <= n; j++) {
        const c = cost[i][j - 1];
        minCost = Math.min(
          minCost,
          c + findMinCost(i + 1, j, prev !== j ? remain - 1 : remain)
        );
      }
    }

    return (dp[i][prev][remain] = minCost);
  }
  const ans = findMinCost(0, 0, target);
  return ans === Infinity ? -1 : ans;
};
