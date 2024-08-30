/**
  Instead of base-10, we are dealing with base-26 number system. Based on the same idea,
  we can just replace 10s with 26s and convert alphabets to numbers.

  For a title "LEET":

  L = 12
  E = (12 x 26) + 5 = 317
  E = (317 x 26) + 5 = 8247
  T = (8247 x 26) + 20 = 214442
*/

var titleToNumber = function (s) {
  let result = 0;
  let n = s.length;
  for (let i = 0; i < n; i++) {
    result = result * 26;
    // In JavaScript, subtracting characters is subtracting ASCII values of characters
    result += s.charCodeAt(i) - "A".charCodeAt(0) + 1;
  }
  return result;
};
