class BottomUpDP {
  public int maxProfit(int[] prices) {
    int[] MP = new int[prices.length + 2];

    for (int i = prices.length - 1; i >= 0; i--) {
      int C1 = 0;
      // Case 1). buy and sell the stock
      for (int sell = i + 1; sell < prices.length; sell++) {
        int profit = (prices[sell] - prices[i]) + MP[sell + 2];
        C1 = Math.max(profit, C1);
      }

      // Case 2). do no transaction with the stock p[i]
      int C2 = MP[i + 1];

      // wrap up the two cases
      MP[i] = Math.max(C1, C2);
    }
    return MP[0];
  }
}
