var grayCode = function (n) {
  let result = [0];
  for (let i = 1; i <= n; i++) {
    let previousSequenceLength = result.length;
    let mask = 1 << (i - 1);
    for (let j = previousSequenceLength - 1; j >= 0; j--) {
      result.push(mask + result[j]);
    }
  }
  return result;
};
