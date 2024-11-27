class FineCary {
  public int getSum(int a, int b) {
    int x = Math.abs(a), y = Math.abs(b);
    // ensure that abs(a) >= abs(b)
    if (x < y)
      return getSum(b, a);

    // abs(a) >= abs(b) -->
    // a determines the sign
    int sign = a > 0 ? 1 : -1;

    if (a * b >= 0) {
      // sum of two positive integers x + y
      // where x > y
      while (y != 0) {
        int answer = x ^ y;
        int carry = (x & y) << 1;
        x = answer;
        y = carry;
      }
    } else {
      // difference of two positive integers x - y
      // where x > y
      while (y != 0) {
        int answer = x ^ y;
        int borrow = ((~x) & y) << 1;
        x = answer;
        y = borrow;
      }
    }
    return x * sign;
  }
}
