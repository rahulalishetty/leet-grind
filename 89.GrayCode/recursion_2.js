var grayCode = function (n) {
  let nextNum = 0;
  let result = [];

  let grayCodeHelper = function (n) {
    if (n == 0) {
      result.push(nextNum);
      return;
    }
    grayCodeHelper(n - 1);
    // Flip the bit at (n - 1)th position from right
    nextNum = nextNum ^ (1 << (n - 1));
    grayCodeHelper(n - 1);
  };

  grayCodeHelper(n);
  return result;
};
