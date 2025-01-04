/**
 * @param {string} s
 * @param {string} p
 * @return {boolean}
 */
var isMatch = function (s, p) {
  const n = s.length,
    m = p.length,
    memo = {};

  function find(i, j) {
    const key = i + "," + j;
    if (key in memo) return memo[key];
    if (i == n && j == m) return (memo[key] = true);
    if (i == n) return (memo[key] = p[j] == "*" && find(i, j + 1));

    if (p[j] == "*") return (memo[key] = find(i + 1, j) || find(i, j + 1));
    if (p[j] == "?") return (memo[key] = find(i + 1, j + 1));

    return (memo[key] = p[j] == s[i] && find(i + 1, j + 1));
  }

  return find(0, 0);
};
