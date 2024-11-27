class BitManipulation {
  public int findMaximumXOR(int[] nums) {
    int maxNum = nums[0];
    for (int num : nums)
      maxNum = Math.max(maxNum, num);
    // length of max number in a binary representation
    int L = (Integer.toBinaryString(maxNum)).length();

    int maxXor = 0, currXor;
    Set<Integer> prefixes = new HashSet<>();
    for (int i = L - 1; i > -1; --i) {
      // Go to the next bit by the left shift
      maxXor <<= 1;

      // Set 1 in the smallest bit
      currXor = maxXor | 1;
      prefixes.clear();

      // compute all possible prefixes
      // of length (L - i) in binary representation
      for (int num : nums)
        prefixes.add(num >> i);

      // Update maxXor, if two of these prefixes could result in currXor.
      // Check if p1^p2 == currXor, i.e. p1 == currXor^p2.
      for (int p : prefixes) {
        if (prefixes.contains(currXor ^ p)) {
          maxXor = currXor;
          break;
        }
      }
    }
    return maxXor;
  }
}
