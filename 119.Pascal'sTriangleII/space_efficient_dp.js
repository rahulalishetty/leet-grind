var getRow = function (rowIndex) {
  let row = Array(rowIndex + 1).fill(1);
  for (let i = 1; i < rowIndex; i++) {
    for (let j = i; j > 0; j--) {
      row[j] += row[j - 1];
    }
  }
  return row;
};
