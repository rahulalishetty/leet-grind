// JavaScript
var singleNumber = function (nums) {
  return nums.reduce((acc, cur) => acc ^ cur, 0);
};
