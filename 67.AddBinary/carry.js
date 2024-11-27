/**
 * @param {string} a
 * @param {string} b
 * @return {string}
 */
var addBinary = function (a, b) {
  const n = a.length,
    m = b.length;

  let a_idx = n - 1,
    b_idx = m - 1,
    ans = "",
    carry = 0;

  function getSumAndCarry(a, b, carry) {
    const sum = Number(a) + Number(b) + carry;
    return [sum % 2, Math.floor(sum / 2)];
  }

  for (; a_idx >= 0 && b_idx >= 0; a_idx--, b_idx--) {
    const [val, c] = getSumAndCarry(a[a_idx], b[b_idx], carry);
    carry = c;
    ans = val + ans;
  }

  for (; a_idx >= 0; a_idx--) {
    const [val, c] = getSumAndCarry(a[a_idx], 0, carry);
    carry = c;
    ans = val + ans;
  }

  for (; b_idx >= 0; b_idx--) {
    const [val, c] = getSumAndCarry(b[b_idx], 0, carry);
    carry = c;
    ans = val + ans;
  }

  return carry ? carry + ans : ans;
};
