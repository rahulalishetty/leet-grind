/**
  Scanning AZZC from right to left while accumulating results:

  First, ask the question, what the value of 'C' is:
  'C' = 3 x 26^0 = 3 x 1 = 3
  result = 0 + 3 = 3

  Then, ask the question, what the value of 'Z*' is:
  'Z*' = 26 x 26^1 = 26 x 26 = 676
  result = 3 + 676 = 679

  Then, ask the question, what the value of 'Z**' is:
  'Z**' = 26 x 26^2 = 26 x 676 = 17576
  result = 679 + 17576 = 18255

  Finally, ask the question, what the value of 'A***' is:
  'A***' = 1 x 26^3 = 1 x 17576 = 17576
  result = 18255 + 17576 = 35831
*/

var titleToNumber = function (s) {
  let result = 0;

  const alpha_map = {};
  for (let i = 0; i < 26; i++) {
    // Decimal 65 in ASCII corresponds to char 'A'
    alpha_map[String.fromCharCode(i + 65)] = i + 1;
  }

  const n = s.length;
  for (let i = 0; i < n; i++) {
    let cur_char = s[n - 1 - i];
    result += alpha_map[cur_char] * Math.pow(26, i);
  }
  return result;
};
