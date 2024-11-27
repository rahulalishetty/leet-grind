/**
 * @param {number[]} nums
 * @return {number[]}
 */
var singleNumber = function (nums) {
  const xor = nums.reduce((acc, a) => acc ^ a, 0);
  const diff = xor & -xor;
  let x = nums.reduce((acc, a) => (a & diff ? acc ^ a : acc), 0);

  return [x, xor ^ x];
};
