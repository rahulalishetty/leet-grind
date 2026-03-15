public class DFS {
  public int maxVacationDays(int[][] flights, int[][] days) {
    return dfs(flights, days, 0, 0);
  }

  public int dfs(int[][] flights, int[][] days, int cur_city, int weekno) {
    if (weekno == days[0].length)
      return 0;
    int maxvac = 0;
    for (int i = 0; i < flights.length; i++) {
      if (flights[cur_city][i] == 1 || i == cur_city) {
        int vac = days[i][weekno] + dfs(flights, days, i, weekno + 1);
        maxvac = Math.max(maxvac, vac);
      }
    }
    return maxvac;
  }
}
