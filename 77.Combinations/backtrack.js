/**
 * @param {number} n
 * @param {number} k
 * @return {number[][]}
 */
var combine = function (n, k) {
  const res = [];

  const backtrack = (start, combination) => {
    if (combination.length === k) {
      res.push([...combination]);
      return;
    }

    for (let i = start; i <= n; i++) {
      combination.push(i);
      backtrack(i + 1, combination);
      combination.pop();
    }
  };

  backtrack(1, []);
  return res;
};
