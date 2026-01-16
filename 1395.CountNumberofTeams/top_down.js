/**
 * @param {number[]} rating
 * @return {number}
 */
var numTeams = function (rating) {
  function countIncreasing(idx, size) {
    if (idx === n) return 0;
    if (size === 3) return 1;

    if (incMemo[idx][size] !== -1) {
      return incMemo[idx][size];
    }

    let teams = 0;

    for (let i = idx + 1; i < n; i++) {
      if (rating[i] > rating[idx]) {
        teams += countIncreasing(i, size + 1);
      }
    }

    return (incMemo[idx][size] = teams);
  }

  function countDecreasing(idx, size) {
    if (idx === n) return 0;
    if (size === 3) return 1;

    if (decMemo[idx][size] !== -1) {
      return decMemo[idx][size];
    }

    let teams = 0;

    for (let i = idx + 1; i < n; i++) {
      if (rating[i] < rating[idx]) {
        teams += countDecreasing(i, size + 1);
      }
    }

    return (decMemo[idx][size] = teams);
  }

  const n = rating.length,
    incMemo = Array.from({ length: n }, () => Array(4).fill(-1)),
    decMemo = Array.from({ length: n }, () => Array(4).fill(-1));
  let teams = 0;

  for (let i = 0; i < n; i++) {
    teams += countIncreasing(i, 1) + countDecreasing(i, 1);
  }

  return teams;
};
