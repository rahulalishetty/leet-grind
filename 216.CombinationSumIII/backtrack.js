/**
 * @param {number} k
 * @param {number} n
 * @return {number[][]}
 */
var combinationSum3 = function (k, n) {
  let unique = new Set();

  function find(target, count, idx, comb) {
    if (count === 0 && target === 0) {
      unique.add(JSON.stringify([...comb]));
    }
    if (target < 0 || count < 0) return;

    for (let i = idx; i < 10; i++) {
      find(target - i, count - 1, i + 1, [...comb, i]);
    }
  }

  find(n, k, 1, []);
  return Array.from(unique, JSON.parse);
};
