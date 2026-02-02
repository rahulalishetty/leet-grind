class SpaceOpt {
  public int[] findBuildings(int[] heights) {
    List<Integer> res = new ArrayList<>();
    int max = 0;

    for (int i = heights.length - 1; i >= 0; i--) {
      if (heights[i] > max) {
        res.add(i);
        max = heights[i];
      }
    }

    Collections.reverse(res);
    return res.stream().mapToInt(i -> i).toArray();
  }
}
