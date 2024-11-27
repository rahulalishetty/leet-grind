/**
 * @param {string} a
 * @param {string} b
 * @return {string}
 */
var addBinary = function (a, b) {
  const n = a.length,
    m = b.length;

  if (n < m) return addBinary(b, a);

  let res = "",
    j = m - 1,
    carry = 0;

  for (let i = n - 1; i >= 0; i--) {
    if (a[i] == 1) carry++;
    if (j >= 0 && b[j--] == 1) carry++;

    res = (carry % 2) + res;
    carry = Math.floor(carry / 2);
  }

  return carry ? carry + res : res;
};
