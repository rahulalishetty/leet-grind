var isNumber = function (s) {
  var seenDigit = false;
  var seenExponent = false;
  var seenDot = false;
  for (var i = 0; i < s.length; i++) {
    var curr = s[i];
    if (!isNaN(curr)) {
      seenDigit = true;
    } else if (curr == "+" || curr == "-") {
      if (i > 0 && s[i - 1] != "e" && s[i - 1] != "E") {
        return false;
      }
    } else if (curr == "e" || curr == "E") {
      if (seenExponent || !seenDigit) {
        return false;
      }
      seenExponent = true;
      seenDigit = false;
    } else if (curr == ".") {
      if (seenDot || seenExponent) {
        return false;
      }
      seenDot = true;
    } else {
      return false;
    }
  }
  return seenDigit;
};
