var getRow = function (rowIndex) {
  let prev = [1];
  for (let i = 1; i <= rowIndex; i++) {
    let curr = Array(i + 1).fill(1);
    for (let j = 1; j < i; j++) {
      curr[j] = prev[j - 1] + prev[j];
    }
    prev = curr;
  }
  return prev;
};
