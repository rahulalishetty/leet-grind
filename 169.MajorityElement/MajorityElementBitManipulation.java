class MajorityElementBitManipulation {
  static int majorityElement(int[] nums) {
    int n = nums.length, majority_element = 0;

    for (int i = 0; i < 32; i++) {
      int bit = 1 << i, bit_count = 0;

      for (int num : nums) {
        if ((num & bit) != 0) {
          bit_count++;
        }
      }

      if (bit_count > n / 2) {
        majority_element |= bit;
      }
    }

    return majority_element;
  }

  public static void main(String[] args) {
    int[] test1 = { 3, 2, 3 };
    System.out.println(majorityElement(test1));
    int[] test2 = { 2, 2, 1, 1, 1, 2, 2 };
    System.out.println(majorityElement(test2));
  }
}
