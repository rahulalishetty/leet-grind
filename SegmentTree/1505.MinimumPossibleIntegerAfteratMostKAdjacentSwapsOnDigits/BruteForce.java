class BruteForce {
  public String minInteger(String num, int k) {
    char[] arr = num.toCharArray();
    int n = arr.length;
    int i = 0;

    while (k > 0 && i < n) {
      int pos = i;
      for (int j = i + 1; j < n && j - i <= k; j++) {
        if (arr[j] < arr[pos]) {
          pos = j;
        }
      }

      int swaps = pos - i;
      if (swaps > k)
        break;

      for (int j = pos; j > i; j--) {
        char temp = arr[j];
        arr[j] = arr[j - 1];
        arr[j - 1] = temp;
      }

      k -= swaps;
      i++;
    }

    return new String(arr);
  }
}
