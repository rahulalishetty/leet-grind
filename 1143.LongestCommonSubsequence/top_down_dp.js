/**
 * @param {string} text1
 * @param {string} text2
 * @return {number}
 */
var longestCommonSubsequence = function (text1, text2) {
  const n = text1.length;
  const m = text2.length;
  const memo = [...Array(n)].map((e) => Array(m).fill(0));

  const findMaxSequence = (i, j) => {
    if (i === n || j === m) return 0;

    if (memo[i][j] === 0) {
      const val =
        text1.charAt(i) === text2.charAt(j)
          ? 1 + findMaxSequence(i + 1, j + 1)
          : 0;
      memo[i][j] = Math.max(
        val,
        findMaxSequence(i, j + 1),
        findMaxSequence(i + 1, j)
      );
    }

    return memo[i][j];
  };

  return findMaxSequence(0, 0);
};
