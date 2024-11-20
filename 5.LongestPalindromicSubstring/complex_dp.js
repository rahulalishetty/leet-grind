/**
 * @param {string} s
 * @return {string}
 */
var longestPalindrome = function (st) {
  const n = st.length,
    dp = {};

  function findPalindrome(i, j, start, len) {
    if (dp.hasOwnProperty(`${i}:${j}:${start}`))
      return dp[`${i}:${j}:${start}`];
    if (i == j) {
      return [start, len + 1];
    }
    if (i > j) {
      return [start, len];
    }

    let match;
    if (st[i] == st[j]) {
      match = findPalindrome(i + 1, j - 1, start, len + 2);
    }

    const left = findPalindrome(i, j - 1, i, 0);
    const right = findPalindrome(i + 1, j, i + 1, 0);

    let final = left[1] >= right[1] ? left : right;

    if (match && match[1] >= final[1]) {
      final = match;
    }

    return (dp[`${i}:${j}:${start}`] = final);
  }

  const [start, len] = findPalindrome(0, n - 1, 0, 0);

  return st.substring(start, start + len);
};
