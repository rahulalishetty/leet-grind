class MStack {
  public int[] findBuildings(int[] heights) {
    Stack<Integer> st = new Stack<>();

    for (int i = 0; i < heights.length; i++) {
      while (!st.isEmpty() && heights[st.peek()] <= heights[i]) {
        st.pop();
      }
      st.push(i);
    }

    int[] res = new int[st.size()];
    for (int i = res.length - 1; i >= 0; i--) {
      res[i] = st.pop();
    }
    return res;
  }
}
