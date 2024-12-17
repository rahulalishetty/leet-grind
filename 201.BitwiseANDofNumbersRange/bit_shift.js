/**
 * @param {number} left
 * @param {number} right
 * @return {number}
 */
var rangeBitwiseAnd = function (left, right) {
  if (!left) return 0;

  let shift = 0;
  while (left < right) {
    left >>= 1;
    right >>= 1;
    shift++;
  }

  return left << shift;
};
