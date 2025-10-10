class Quickselect {
  public int kthLargestValue(int[][] matrix, int k) {
    int m = matrix.length, n = matrix[0].length;
    int[][] px = new int[m + 1][n + 1];

    int[] vals = new int[m * n];
    int idx = 0;

    for (int i = 1; i <= m; i++) {
      for (int j = 1; j <= n; j++) {
        px[i][j] = px[i - 1][j] ^ px[i][j - 1] ^ px[i - 1][j - 1] ^ matrix[i - 1][j - 1];
        vals[idx++] = px[i][j];
      }
    }

    // We need k-th largest => index k-1 if array sorted descending.
    return quickselectKthLargest(vals, 0, vals.length - 1, k - 1);
  }

  // Quickselect for k-th index in descending order
  private int quickselectKthLargest(int[] a, int l, int r, int kIdxDesc) {
    Random rand = new Random(42); // stable randomness
    while (l <= r) {
      int pivotIdx = l + rand.nextInt(r - l + 1);
      int newPivot = partitionDesc(a, l, r, pivotIdx);
      if (newPivot == kIdxDesc)
        return a[newPivot];
      else if (newPivot < kIdxDesc)
        l = newPivot + 1;
      else
        r = newPivot - 1;
    }
    // Should not reach here if inputs are valid
    throw new IllegalArgumentException("k out of range");
  }

  // Lomuto-style partition, arranging values in DESCENDING order
  private int partitionDesc(int[] a, int l, int r, int pivotIdx) {
    int pivotVal = a[pivotIdx];
    swap(a, pivotIdx, r);
    int store = l;
    for (int i = l; i < r; i++) {
      if (a[i] > pivotVal) { // '>' for descending
        swap(a, store++, i);
      }
    }
    swap(a, store, r);
    return store;
  }

  private void swap(int[] a, int i, int j) {
    int t = a[i];
    a[i] = a[j];
    a[j] = t;
  }
}
