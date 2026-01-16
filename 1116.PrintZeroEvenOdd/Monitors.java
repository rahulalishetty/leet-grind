import java.util.function.IntConsumer;

class Monitors {
  private final int n;
  private int cur = 1;
  private boolean zeroTurn = true; // clearer than zeroDone

  public Monitors(int n) {
    this.n = n;
  }

  public synchronized void zero(IntConsumer printNumber) throws InterruptedException {
    for (int i = 0; i < n; i++) {
      while (!zeroTurn)
        wait();
      printNumber.accept(0);
      zeroTurn = false;
      notifyAll();
    }
  }

  public synchronized void odd(IntConsumer printNumber) throws InterruptedException {
    while (cur <= n) {
      while (zeroTurn || (cur & 1) == 0) {
        if (cur > n)
          return;
        wait();
      }
      printNumber.accept(cur++);
      zeroTurn = true;
      notifyAll();
    }
  }

  public synchronized void even(IntConsumer printNumber) throws InterruptedException {
    while (cur <= n) {
      while (zeroTurn || (cur & 1) == 1) {
        if (cur > n)
          return;
        wait();
      }
      printNumber.accept(cur++);
      zeroTurn = true;
      notifyAll();
    }
  }
}
