class Solution {
    public long minInversionCount(int[] nums, int k) {
        int n = nums.length;
        if (k <= 1) return 0;
        long ans = Long.MAX_VALUE;

        for (int l = 0; l + k <= n; l++) {
            long inv = 0;
            for (int i = l; i < l + k; i++) {
                for (int j = i + 1; j < l + k; j++) {
                    if (nums[i] > nums[j]) inv++;
                }
            }
            ans = Math.min(ans, inv);
        }
        return ans == Long.MAX_VALUE ? 0 : ans;
    }
}
