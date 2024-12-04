/**
 * @param {character[][]} matrix
 * @return {number}
 */
var maximalSquare = function (matrix) {
  const n = matrix.length,
    m = matrix[0].length;
  let maxSquare = 0;

  matrix = matrix.map((row, i) =>
    row.map((col, j) => {
      // console.log(i, j)
      if ((i === 0 || j === 0) && matrix[i][j] === "1") maxSquare = 1;
      return Number(col);
    })
  );

  for (let i = 1; i < n; i++) {
    for (let j = 1; j < m; j++) {
      if (matrix[i][j]) {
        matrix[i][j] += Math.min(
          matrix[i - 1][j],
          matrix[i][j - 1],
          matrix[i - 1][j - 1]
        );
        maxSquare = Math.max(maxSquare, matrix[i][j]);
      }
    }
  }
  console.log(matrix);
  return maxSquare * maxSquare;
};
