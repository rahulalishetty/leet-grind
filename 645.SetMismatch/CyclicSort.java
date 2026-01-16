class CyclicSort {
  public int[] findErrorNums(int[] nums) {
    int n = nums.length;
    int i = 0;

    // Cyclic sort: place each number at index num - 1
    while (i < n) {
      int correctIndex = nums[i] - 1;
      if (nums[i] != nums[correctIndex]) {
        // swap nums[i] with nums[correctIndex]
        int temp = nums[i];
        nums[i] = nums[correctIndex];
        nums[correctIndex] = temp;
      } else {
        i++;
      }
    }

    // After sorting, the index with mismatch gives duplicate & missing
    for (i = 0; i < n; i++) {
      if (nums[i] != i + 1) {
        int duplicate = nums[i];
        int missing = i + 1;
        return new int[] { duplicate, missing };
      }
    }

    // Problem guarantees a solution, but just in case:
    return new int[] { -1, -1 };
  }
}
