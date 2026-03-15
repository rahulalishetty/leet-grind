class Solution {

  public int numberOfPatterns(int m, int n) {
    int[][] jump = new int[10][10];

    // Initialize the jump over numbers for all valid jumps
    jump[1][3] = jump[3][1] = 2;
    jump[4][6] = jump[6][4] = 5;
    jump[7][9] = jump[9][7] = 8;
    jump[1][7] = jump[7][1] = 4;
    jump[2][8] = jump[8][2] = 5;
    jump[3][9] = jump[9][3] = 6;
    jump[1][9] = jump[9][1] = jump[3][7] = jump[7][3] = 5;

    boolean[] visitedNumbers = new boolean[10];
    int totalPatterns = 0;

    // Count patterns starting from corner numbers (1, 3, 7, 9) and multiply by 4
    // due to symmetry
    totalPatterns += countPatternsFromNumber(1, 1, m, n, jump, visitedNumbers) * 4;

    // Count patterns starting from edge numbers (2, 4, 6, 8) and multiply by 4 due
    // to symmetry
    totalPatterns += countPatternsFromNumber(2, 1, m, n, jump, visitedNumbers) * 4;

    // Count patterns starting from the center number (5)
    totalPatterns += countPatternsFromNumber(5, 1, m, n, jump, visitedNumbers);

    return totalPatterns;
  }

  private int countPatternsFromNumber(
      int currentNumber,
      int currentLength,
      int minLength,
      int maxLength,
      int[][] jump,
      boolean[] visitedNumbers) {
    // Base case: if current pattern length exceeds maxLength, stop exploring
    if (currentLength > maxLength)
      return 0;

    int validPatterns = 0;
    // If current pattern length is within the valid range, count it
    if (currentLength >= minLength) {
      validPatterns++;
    }

    visitedNumbers[currentNumber] = true;

    // Explore all possible next numbers
    for (int nextNumber = 1; nextNumber <= 9; nextNumber++) {
      int jumpOverNumber = jump[currentNumber][nextNumber];
      // Check if the next number is unvisited and either:
      // 1. There's no number to jump over, or
      // 2. The number to jump over has been visited
      if (!visitedNumbers[nextNumber] &&
          (jumpOverNumber == 0 || visitedNumbers[jumpOverNumber])) {
        validPatterns += countPatternsFromNumber(
            nextNumber,
            currentLength + 1,
            minLength,
            maxLength,
            jump,
            visitedNumbers);
      }
    }

    // Backtrack: unmark the current number before returning
    visitedNumbers[currentNumber] = false;

    return validPatterns;
  }
}
