import java.util.function.IntBinaryOperator;
import java.util.Arrays;

/**
 * Sparse Table for static range queries with an idempotent operation:
 * - min, max, gcd, bitwise AND/OR, etc.
 *
 * Build: O(n log n)
 * Query: O(1)
 *
 * Preconditions:
 * - op is idempotent: op(x, x) == x
 * - array length >= 1
 */
public final class SparseTable {
  private final int n;
  private final int maxK; // floor(log2(n))
  private final int[][] st; // st[k][i] = op over [i, i + 2^k - 1]
  private final int[] log2; // floor(log2(x)) for x in [0..n]
  private final IntBinaryOperator op;

  /**
   * Constructs a SparseTable over the given array using the provided idempotent
   * op.
   */
  public SparseTable(int[] array, IntBinaryOperator op) {
    if (array == null || array.length == 0)
      throw new IllegalArgumentException("array must be non-empty");
    if (op == null)
      throw new IllegalArgumentException("op must be non-null");
    this.n = array.length;
    this.op = op;

    // Precompute floor logs
    log2 = new int[n + 1];
    log2[1] = 0;
    for (int i = 2; i <= n; i++)
      log2[i] = log2[i >> 1] + 1;

    maxK = log2[n];
    st = new int[maxK + 1][n];

    // k = 0 (length = 1)
    System.arraycopy(array, 0, st[0], 0, n);

    // Build for k >= 1: combine two adjacent blocks of size 2^(k-1)
    for (int k = 1; k <= maxK; k++) {
      int len = 1 << k;
      int half = len >> 1;
      int limit = n - len + 1;
      for (int i = 0; i < limit; i++) {
        st[k][i] = op.applyAsInt(st[k - 1][i], st[k - 1][i + half]);
      }
    }
  }

  /**
   * Query op over the inclusive range [l, r].
   * Runs in O(1).
   */
  public int query(int l, int r) {
    if (l < 0 || r < 0 || l >= n || r >= n || l > r) {
      throw new IndexOutOfBoundsException("Invalid range: [" + l + "," + r + "]");
    }
    int len = r - l + 1;
    int k = log2[len];
    int left = st[k][l];
    int right = st[k][r - (1 << k) + 1];
    return op.applyAsInt(left, right);
  }

  /** Convenience factory for range minimum queries (RMQ). */
  public static SparseTable forMin(int[] array) {
    return new SparseTable(array, Math::min);
  }

  /** Convenience factory for range maximum queries. */
  public static SparseTable forMax(int[] array) {
    return new SparseTable(array, Math::max);
  }

  /** Read-only debug view (optional). */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("SparseTable{n=").append(n).append(", maxK=").append(maxK).append("}\n");
    for (int k = 0; k <= maxK; k++) {
      sb.append("k=").append(k).append(" len=").append(1 << k).append(" : ");
      sb.append(Arrays.toString(Arrays.copyOf(st[k], Math.max(0, n - (1 << k) + 1)))).append('\n');
    }
    return sb.toString();
  }

  // Example usage
  public static void main(String[] args) {
    int[] arr = { 1, 3, 4, 8, 6, 1, 4, 2 };
    SparseTable rmq = SparseTable.forMin(arr);
    System.out.println(rmq.query(1, 6)); // -> 1

    SparseTable rMax = SparseTable.forMax(arr);
    System.out.println(rMax.query(2, 5)); // -> 8

    // GCD example (idempotent): gcd(x, x) == x
    IntBinaryOperator gcd = (a, b) -> {
      a = Math.abs(a);
      b = Math.abs(b);
      if (a == 0)
        return b;
      if (b == 0)
        return a;
      while (b != 0) {
        int t = a % b;
        a = b;
        b = t;
      }
      return a;
    };
    SparseTable rGcd = new SparseTable(arr, gcd);
    System.out.println(rGcd.query(0, 7)); // gcd over entire array
  }
}
