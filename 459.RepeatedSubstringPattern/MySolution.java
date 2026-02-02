class Solution {
  public boolean repeatedSubstringPattern(String s) {
    int n = s.length();
    if (n == 1)
      return false;

    for (int i = 0; i < n / 2; i++) {
      String pattern = s.substring(0, i + 1);
      int plen = pattern.length();

      if (n % plen != 0)
        continue;
      int cuts = n / plen, iter;

      for (iter = 1; iter < cuts; iter++) {
        String peice = s.substring(iter * plen, (iter + 1) * plen);
        System.out.println(peice);
        if (!pattern.equals(peice)) {
          break;
        }
      }

      if (iter == cuts)
        return true;
    }

    return false;
  }
}
