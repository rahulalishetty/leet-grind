class Solution {
  int N;
  int[][] pairs;

  public int minSwapsCouples(int[] row) {
    N = row.length / 2;
    pairs = new int[N][2];
    for (int i = 0; i < N; ++i) {
      pairs[i][0] = row[2 * i] / 2;
      pairs[i][1] = row[2 * i + 1] / 2;
    }

    return solve(0);
  }

  public void swap(int a, int b, int c, int d) {
    int t = pairs[a][b];
    pairs[a][b] = pairs[c][d];
    pairs[c][d] = t;
  }

  public int solve(int i) {
    if (i == N)
      return 0;
    int x = pairs[i][0], y = pairs[i][1];
    if (x == y)
      return solve(i + 1);

    int jx = 0, kx = 0, jy = 0, ky = 0; // Always gets set later
    for (int j = i + 1; j < N; ++j) {
      for (int k = 0; k <= 1; ++k) {
        if (pairs[j][k] == x) {
          jx = j;
          kx = k;
        }
        if (pairs[j][k] == y) {
          jy = j;
          ky = k;
        }
      }
    }

    swap(i, 1, jx, kx);
    int ans1 = 1 + solve(i + 1);
    swap(i, 1, jx, kx);

    swap(i, 0, jy, ky);
    int ans2 = 1 + solve(i + 1);
    swap(i, 0, jy, ky);

    return Math.min(ans1, ans2);
  }
}
