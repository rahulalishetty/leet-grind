class BruteForce {
  public int minFallingPathSum(int[][] matrix) {

    int minFallingSum = Integer.MAX_VALUE;
    for (int startCol = 0; startCol < matrix.length; startCol++) {
      minFallingSum = Math.min(minFallingSum, findMinFallingPathSum(matrix, 0, startCol));
    }
    return minFallingSum;
  }

  public int findMinFallingPathSum(int[][] matrix, int row, int col) {
    // check if we are out of the left or right boundary of the matrix
    if (col < 0 || col == matrix.length) {
      return Integer.MAX_VALUE;
    }
    // check if we have reached the last row
    if (row == matrix.length - 1) {
      return matrix[row][col];
    }

    // calculate the minimum falling path sum starting from each possible next step
    int left = findMinFallingPathSum(matrix, row + 1, col);
    int middle = findMinFallingPathSum(matrix, row + 1, col + 1);
    int right = findMinFallingPathSum(matrix, row + 1, col - 1);

    return Math.min(left, Math.min(middle, right)) + matrix[row][col];
  }
}
