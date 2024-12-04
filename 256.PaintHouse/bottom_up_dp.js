/**
 * @param {number[][]} costs
 * @return {number}
 */
var minCost = function (cost) {
  const n = cost.length;

  for (let i = 1; i < n; i++) {
    cost[i][0] += Math.min(cost[i - 1][1], cost[i - 1][2]);
    cost[i][1] += Math.min(cost[i - 1][0], cost[i - 1][2]);
    cost[i][2] += Math.min(cost[i - 1][0], cost[i - 1][1]);
  }

  return Math.min(cost[n - 1][0], cost[n - 1][1], cost[n - 1][2]);
};
