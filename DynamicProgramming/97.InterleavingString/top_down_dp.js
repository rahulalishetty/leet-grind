/**
 * @param {string} s1
 * @param {string} s2
 * @param {string} s3
 * @return {boolean}
 */
var isInterleave = function (A, B, C) {
  const l = A.length,
    m = B.length,
    n = C.length,
    dp = [...Array(l)].map((e) => Array(m).fill(-1));

  function findMatch(i, j, k) {
    if (i === l) return B.slice(j) === C.slice(k);
    if (j === m) return A.slice(i) === C.slice(k);
    if (dp[i][j] !== -1) return dp[i][j];

    let found = false;
    if (
      (A[i] === C[k] && findMatch(i + 1, j, k + 1)) ||
      (B[j] === C[k] && findMatch(i, j + 1, k + 1))
    ) {
      found = true;
    }

    return (dp[i][j] = found);
  }

  return findMatch(0, 0, 0);
};
