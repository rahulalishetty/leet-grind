var maxCoins = function (nums) {
  const n = nums.length;
  const arr = [1, ...nums, 1];
  const dp = Array.from({ length: n + 2 }, () => Array(n + 2).fill(-1));

  function solve(left, right) {
    if (left + 1 === right) return 0; // no balloons to burst in between
    if (dp[left][right] !== -1) return dp[left][right];

    let maxCoins = 0;
    for (let k = left + 1; k < right; k++) {
      const coins = arr[left] * arr[k] * arr[right];
      maxCoins = Math.max(maxCoins, solve(left, k) + coins + solve(k, right));
    }

    return (dp[left][right] = maxCoins);
  }

  return solve(0, n + 1);
};
