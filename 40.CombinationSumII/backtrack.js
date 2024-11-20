/**
 * @param {number[]} candidates
 * @param {number} target
 * @return {number[][]}
 */
var combinationSum2 = function (candidates, target) {
  let unique = new Set(),
    n = candidates.length;
  candidates.sort((a, b) => a - b);

  function find(t, comb, i, used) {
    if (t === 0) {
      unique.add(JSON.stringify(comb.sort((a, b) => a - b)));
      return;
    }
    if (t < 0 || i >= n) return;

    if (i > 0 && candidates[i - 1] === candidates[i] && !used) {
      find(t - candidates[i], [...comb, candidates[i]], i + 1, true);
    }
    find(t, [...comb], i + 1, false);
  }

  find(target, [], 0, false);
  return Array.from(unique, JSON.parse);
};
