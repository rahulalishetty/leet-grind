/**
 * @param {number} num
 * @return {string}
 */
var convertToBase7 = function (num) {
  if (num === 0) return num.toString();
  let base7 = "",
    BASE = 7,
    sign = num < 0 ? "-" : "";
  num = Math.abs(num);

  while (num) {
    base7 = String(Math.floor(num % BASE)) + base7;
    num = Math.floor(num / BASE);
  }

  return `${sign}${base7}`;
};
