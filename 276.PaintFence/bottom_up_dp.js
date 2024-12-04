/**
 * @param {number} n
 * @param {number} k
 * @return {number}
 */
var numWays = function (n, k) {
  if (n === 1) return k;
  let first = k * k,
    second = k;

  for (let i = 3; i <= n; i++) {
    const count = (k - 1) * (first + second);
    second = first;
    first = count;
  }

  return first;
};
