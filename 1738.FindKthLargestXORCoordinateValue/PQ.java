class PQ {
  public int kthLargestValue(int[][] matrix, int k) {
    int m = matrix.length, n = matrix[0].length;
    int[][] px = new int[m + 1][n + 1];

    PriorityQueue<Integer> pq = new PriorityQueue<>(k); // min-heap
    for (int i = 1; i <= m; i++) {
      for (int j = 1; j <= n; j++) {
        px[i][j] = px[i - 1][j] ^ px[i][j - 1] ^ px[i - 1][j - 1] ^ matrix[i - 1][j - 1];

        if (pq.size() < k)
          pq.offer(px[i][j]);
        else if (px[i][j] > pq.peek()) {
          pq.poll();
          pq.offer(px[i][j]);
        }
      }
    }
    return pq.peek();
  }
}
