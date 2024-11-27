/**
 * @param {number} a
 * @param {number} b
 * @return {number}
 */
var getSum = function (a, b) {
  let carry = 0,
    sum;

  while (b) {
    sum = a ^ b;
    carry = (a & b) << 1;
    b = carry;
    a = sum;
  }

  return a;
};
