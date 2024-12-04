/**
 * @param {number[]} jobDifficulty
 * @param {number} d
 * @return {number}
 */
var minDifficulty = function (jobDifficulty, d) {
  const n = jobDifficulty.length,
    dp = [...Array(n)].map((e) => Array(d).fill(-1));
  if (n < d) return -1;
  function findMax(i, day) {
    if (day === d) {
      if (i === n) return 0;
      return Infinity;
    } else if (i === n) return Infinity;
    if (dp[i][day] !== -1) return dp[i][day];

    let minDiff = Infinity,
      curMax = 0;
    for (let j = i; j < n; j++) {
      curMax = Math.max(curMax, jobDifficulty[j]);
      minDiff = Math.min(minDiff, findMax(j + 1, day + 1) + curMax);
    }

    return (dp[i][day] = minDiff);
  }

  return findMax(0, 0);
};
