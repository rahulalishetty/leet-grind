/**
 * @param {string} word1
 * @param {string} word2
 * @return {number}
 */
var minDistance = function (word1, word2) {
  const n = word1.length,
    m = word2.length,
    memo = Array(n + 1)
      .fill()
      .map(() => Array(m + 1).fill(null));

  for (let i = 0; i <= n; i++) {
    memo[i][0] = i;
  }

  for (let i = 0; i <= m; i++) {
    memo[0][i] = i;
  }

  for (let i = 1; i <= n; i++) {
    for (let j = 1; j <= m; j++) {
      if (word1[i - 1] === word2[j - 1]) memo[i][j] = memo[i - 1][j - 1];
      else {
        memo[i][j] =
          Math.min(memo[i - 1][j], memo[i][j - 1], memo[i - 1][j - 1]) + 1;
      }
    }
  }

  return memo[n][m];
};
