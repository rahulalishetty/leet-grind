/**
 * @param {number[]} candidates
 * @param {number} target
 * @return {number[][]}
 */
var combinationSum = function (candidates, target) {
  const unique = new Set(),
    n = candidates.length;

  function find(t, comb) {
    if (t === 0) {
      unique.add(JSON.stringify(comb.sort((a, b) => a - b)));
    }

    for (let i = 0; i < n; i++) {
      if (t - candidates[i] >= 0) {
        find(t - candidates[i], [...comb, candidates[i]]);
      }
    }
  }

  find(target, []);

  return Array.from(unique, JSON.parse);
};
