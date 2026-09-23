class Solution {
  public List<List<Integer>> allPathsSourceTarget(int[][] graph) {
    int n = graph.length;
    int target = n - 1;

    List<List<Integer>> result = new ArrayList<>();
    Deque<List<Integer>> q = new ArrayDeque<>();

    q.addLast(Arrays.asList(0));

    while (!q.isEmpty()) {
      List<Integer> path = q.removeFirst();
      int last = path.get(path.size() - 1);

      if (last == target) {
        result.add(path);
        continue;
      }

      for (int next : graph[last]) {
        List<Integer> newPath = new ArrayList<>(path);
        newPath.add(next);
        q.addLast(newPath);
      }
    }

    return result;
  }
}
