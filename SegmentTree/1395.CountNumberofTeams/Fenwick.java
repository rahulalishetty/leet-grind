class Fenwick {

  public int numTeams(int[] rating) {
    // Find the maximum rating
    int maxRating = 0;
    for (int r : rating) {
      maxRating = Math.max(maxRating, r);
    }

    // Initialize Binary Indexed Trees for left and right sides
    int[] leftBIT = new int[maxRating + 1];
    int[] rightBIT = new int[maxRating + 1];

    // Populate the right BIT with all ratings initially
    for (int r : rating) {
      updateBIT(rightBIT, r, 1);
    }

    int teams = 0;
    for (int currentRating : rating) {
      // Remove current rating from right BIT
      updateBIT(rightBIT, currentRating, -1);

      // Count soldiers with smaller and larger ratings on both sides
      int smallerRatingsLeft = getPrefixSum(leftBIT, currentRating - 1);
      int smallerRatingsRight = getPrefixSum(rightBIT, currentRating - 1);
      int largerRatingsLeft = getPrefixSum(leftBIT, maxRating) -
          getPrefixSum(leftBIT, currentRating);
      int largerRatingsRight = getPrefixSum(rightBIT, maxRating) -
          getPrefixSum(rightBIT, currentRating);

      // Count increasing and decreasing sequences
      teams += (smallerRatingsLeft * largerRatingsRight);
      teams += (largerRatingsLeft * smallerRatingsRight);

      // Add current rating to left BIT
      updateBIT(leftBIT, currentRating, 1);
    }

    return teams;
  }

  // Update the Binary Indexed Tree
  private void updateBIT(int[] BIT, int index, int value) {
    while (index < BIT.length) {
      BIT[index] += value;
      index += index & (-index); // Move to the next relevant index in BIT
    }
  }

  // Get the sum of all elements up to the given index in the BIT
  private int getPrefixSum(int[] BIT, int index) {
    int sum = 0;
    while (index > 0) {
      sum += BIT[index];
      index -= index & (-index); // Move to the parent node in BIT
    }
    return sum;
  }
}
