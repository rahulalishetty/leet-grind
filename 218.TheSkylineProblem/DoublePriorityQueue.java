class DoublePriorityQueue {
  public List<List<Integer>> getSkyline(int[][] buildings) {
    // Iterate over all buildings, for each building i
    // add (position, i) to edges.
    List<List<Integer>> edges = new ArrayList<>();
    for (int i = 0; i < buildings.length; ++i) {
      edges.add(Arrays.asList(buildings[i][0], i));
      edges.add(Arrays.asList(buildings[i][1], i));
    }
    Collections.sort(edges, (a, b) -> {
      return a.get(0) - b.get(0);
    });

    // Initailize an empty Priority Queue 'live' to store all the newly
    // added buildings, an empty list answer to store the skyline key points.
    Queue<List<Integer>> live = new PriorityQueue<>((a, b) -> {
      return b.get(0) - a.get(0);
    });
    List<List<Integer>> answer = new ArrayList<>();

    int idx = 0;

    // Iterate over all the sorted edges.
    while (idx < edges.size()) {
      // Since we might have multiple edges at same x,
      // Let the 'currX' be the current position.
      int currX = edges.get(idx).get(0);

      // While we are handling the edges at 'currX':
      while (idx < edges.size() && edges.get(idx).get(0) == currX) {
        // The index 'b' of this building in 'buildings'
        int b = edges.get(idx).get(1);

        // If this is a left edge of building 'b', we
        // add (height, right) of building 'b' to 'live'.
        if (buildings[b][0] == currX) {
          int right = buildings[b][1];
          int height = buildings[b][2];
          live.offer(Arrays.asList(height, right));
        }
        idx += 1;
      }

      // If the tallest live building has been passed,
      // we remove it from 'live'.
      while (!live.isEmpty() && live.peek().get(1) <= currX)
        live.poll();

      // Get the maximum height from 'live'.
      int currHeight = live.isEmpty() ? 0 : live.peek().get(0);

      // If the height changes at this currX, we add this
      // skyline key point [currX, max_height] to 'answer'.
      if (answer.isEmpty() || answer.get(answer.size() - 1).get(1) != currHeight)
        answer.add(Arrays.asList(currX, currHeight));
    }

    // Return 'answer' as the skyline.
    return answer;
  }
}
