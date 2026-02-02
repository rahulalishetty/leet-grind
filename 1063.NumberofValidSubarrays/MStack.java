class MStack {
  public long countValidSubarrays(int[] nums) {
    int n = nums.length;
    long ans = 0;

    Deque<Integer> st = new ArrayDeque<>(); // indices, values strictly increasing from top outward

    for (int i = n - 1; i >= 0; i--) {
      // pop elements that are >= nums[i] so top becomes strictly smaller
      while (!st.isEmpty() && nums[st.peek()] >= nums[i]) {
        st.pop();
      }
      int nextStrictSmaller = st.isEmpty() ? n : st.peek();
      ans += (long) (nextStrictSmaller - i);
      st.push(i);
    }
    return ans;
  }
}
