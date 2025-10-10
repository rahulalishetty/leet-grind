class SegmentTree {

  private int[] tree;
  private int size;

  public NumArray(int[] nums) {
    this.size = nums.length;
    this.tree = new int[4 * this.size];

    this.build(nums, 0, 0, this.size - 1);
  }

  private void build(int[] nums, int node, int start, int end) {
    if (start == end) {
      this.tree[node] = nums[start];
    } else {
      int mid = start + (end - start) / 2;
      build(nums, 2 * node + 1, start, mid);
      build(nums, 2 * node + 2, mid + 1, end);
      this.tree[node] = this.tree[2 * node + 1] + this.tree[2 * node + 2];
    }
  }

  public void update(int index, int val) {
    this.update(0, 0, this.size - 1, index, val);
  }

  private void update(int node, int start, int end, int index, int val) {
    if (start == end) {
      this.tree[node] = val;
    } else {
      int mid = start + (end - start) / 2;

      if (index <= mid) {
        this.update(2 * node + 1, start, mid, index, val);
      } else {
        this.update(2 * node + 2, mid + 1, end, index, val);
      }
      this.tree[node] = this.tree[2 * node + 1] + this.tree[2 * node + 2];
    }
  }

  public int sumRange(int left, int right) {
    return query(0, 0, this.size - 1, left, right);
  }

  private int query(int node, int start, int end, int left, int right) {
    if (right < start || left > end) {
      return 0;
    }
    if (left <= start && end <= right) {
      return this.tree[node];
    }
    // if(start == end) {
    // return this.tree[node];
    // }

    int mid = start + (end - start) / 2;
    int leftSum = query(2 * node + 1, start, mid, left, right);
    int rightSum = query(2 * node + 2, mid + 1, end, left, right);
    return leftSum + rightSum;
  }
}

/**
 * Your NumArray object will be instantiated and called as such:
 * NumArray obj = new NumArray(nums);
 * obj.update(index,val);
 * int param_2 = obj.sumRange(left,right);
 */
