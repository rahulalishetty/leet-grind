import java.util.Set;

public class BruteForce {

  public int rectangleArea(int[][] rectagles) {
    int N = rectagles.length;
    Set<Integer> x = new HashSet<>();
    Set<Integer> y = new HashSet<>();
    for (int[] rec : rectagles) {
      x.add(rec[0]);
      x.add(rec[2]);
      y.add(rec[1]);
      y.add(rec[3]);
    }

    Integer[] xAxis = x.toArray(new Integer[0]);
    Integer[] yAxis = y.toArray(new Integer[0]);
    Arrays.sort(xAxis);
    Arrays.sort(yAxis);

    Map<Integer, Integer> xIndex = new HashMap<>();
    Map<Integer, Integer> yIndex = new HashMap<>();
    for (int i = 0; i < xAxis.length; i++) {
      xIndex.put(xAxis[i], i);
    }
    for (int i = 0; i < yAxis.length; i++) {
      yIndex.put(yAxis[i], i);
    }

    boolean[][] grid = new boolean[xAxis.length][yAxis.length];

    for (int[] rec : rectagles) {
      int x1 = xIndex.get(rec[0]);
      int x2 = xIndex.get(rec[2]);
      int y1 = yIndex.get(rec[1]);
      int y2 = yIndex.get(rec[3]);
      for (int i = x1; i < x2; i++) {
        for (int j = y1; j < y2; j++) {
          grid[i][j] = true;
        }
      }
    }

    long area = 0;
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[i].length; j++) {
        if (grid[i][j]) {
          area += (long) (xAxis[i + 1] - xAxis[i]) * (yAxis[j + 1] - yAxis[j]);
        }
      }
    }

    return (int) (area % 1_000_000_007);
  }
}
