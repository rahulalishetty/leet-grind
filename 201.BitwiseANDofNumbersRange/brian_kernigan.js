/**
 * @param {number} left
 * @param {number} right
 * @return {number}
 */
var rangeBitwiseAnd = function (left, right) {
  while (left < right) {
    left = left & (left - 1);
  }

  return left;
};
