/**
 * @param {number[]} digits
 * @return {number[]}
 */
var plusOne = function (digits) {
  const n = digits.length;

  let carry = 1;

  for (let i = n - 1; i >= 0 && carry; i--) {
    const sum = digits[i] + carry;
    carry = Math.floor(sum / 10);
    digits[i] = sum % 10;
  }

  if (carry) {
    return [carry].concat(digits);
  }

  return digits;
};
