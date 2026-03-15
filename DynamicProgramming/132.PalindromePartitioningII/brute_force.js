/**
 * @param {string} s
 * @return {number}
 */
var minCut = function (s) {
  const n = s.length,
    dp = Array(n)
      .fill()
      .map(() => Array(n).fill(false));
  let ans = Infinity;

  function dfs(start, cuts) {
    if (start === n) {
      ans = Math.min(ans, cuts - 1);
    }

    for (let end = start; end < n; end++) {
      if (s[start] === s[end] && (end - start <= 2 || dp[start + 1][end - 1])) {
        dp[start][end] = true;
        dfs(end + 1, cuts + 1);
      }
    }
  }

  dfs(0, 0);
  return ans;
};
