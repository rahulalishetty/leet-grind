// JavaScript Solution
var permuteUnique = function (nums) {
  let results = [];
  let counter = {};
  for (let num of nums) {
    if (!(num in counter)) counter[num] = 0;
    counter[num]++;
  }
  function backtrack(comb, N) {
    if (comb.length === N) {
      results.push([...comb]);
      return;
    }
    for (let num in counter) {
      if (counter[num] === 0) continue;
      comb.push(parseInt(num));
      counter[num]--;
      backtrack(comb, N);
      counter[num]++;
      comb.pop();
    }
  }
  backtrack([], nums.length);
  return results;
};
