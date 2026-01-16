class Xor {
  public int[] findErrorNums(int[] nums) {
    int n = nums.length;

    int xorAll = 0;
    for (int i = 1; i <= n; i++) {
      xorAll ^= i;
    }

    int xorNums = 0;
    for (int num : nums) {
      xorNums ^= num;
    }

    // xorBoth = missing ^ duplicate
    int xorBoth = xorAll ^ xorNums;

    // Get rightmost set bit in xorBoth
    int rightmostSetBit = xorBoth & -xorBoth;

    int bucket1 = 0;
    int bucket2 = 0;

    // Partition 1..n by rightmostSetBit
    for (int i = 1; i <= n; i++) {
      if ((i & rightmostSetBit) != 0) {
        bucket1 ^= i;
      } else {
        bucket2 ^= i;
      }
    }

    // Partition nums by rightmostSetBit
    for (int num : nums) {
      if ((num & rightmostSetBit) != 0) {
        bucket1 ^= num;
      } else {
        bucket2 ^= num;
      }
    }

    // Now bucket1 and bucket2 are {missing, duplicate} in some order.
    // Figure out which one is the duplicate by checking nums.
    int candidate1 = bucket1;
    int candidate2 = bucket2;

    int duplicate = -1;
    int missing = -1;

    for (int num : nums) {
      if (num == candidate1) {
        duplicate = candidate1;
        missing = candidate2;
        break;
      } else if (num == candidate2) {
        duplicate = candidate2;
        missing = candidate1;
        break;
      }
    }

    return new int[] { duplicate, missing };
  }
}
