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

  function find(i, j) {
    if (memo[i][j] != null) return memo[i][j];

    if (i === 0) return j;
    if (j === 0) return i;

    if (word1[i - 1] === word2[j - 1]) return (memo[i][j] = find(i - 1, j - 1));
    else {
      return (memo[i][j] =
        1 + Math.min(find(i - 1, j), find(i, j - 1), find(i - 1, j - 1)));
    }
  }

  return find(n, m);
};
