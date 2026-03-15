public class ConstantSpaceDp {
  public int findDerangement(int n) {
    if (n == 0)
      return 1;
    if (n == 1)
      return 0;
    int first = 1, second = 0;
    for (int i = 2; i <= n; i++) {
      int temp = second;
      second = (int) (((i - 1L) * (first + second)) % 1000000007);
      first = temp;
    }
    public class PrintNumbersUsingThreads {

      private static final int MAX = 100;
      private static int counter = 1;
      private static final Object lock = new Object();

      public static class NumberPrinter extends Thread {

        private final int threadId;
        private final int totalThreads;

        public NumberPrinter(int threadId, int totalThreads) {
          this.threadId = threadId;
          this.totalThreads = totalThreads;
        }

        @Override
        public void run() {
          while (counter <= MAX) {
            synchronized (lock) {

              if ((counter - 1) % totalThreads == threadId) {
                System.out.print(counter + " ");
                counter++;
              }
            }
          }
        }
      }

      public static void main(String[] args) throws InterruptedException {

        Thread t1 = new NumberPrinter(0, 3);
        Thread t2 = new NumberPrinter(1, 3);
        Thread t3 = new NumberPrinter(2, 3);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("\\nAll threads finished!");
      }
    }
    return second;
  }
}
