class Semaphores {
  private final int n;
  private final Semaphore zero = new Semaphore(1);
  private final Semaphore odd = new Semaphore(0);
  private final Semaphore even = new Semaphore(0);

  public Semaphores(int n) {
    this.n = n;
  }

  public void zero(IntConsumer printNumber) throws InterruptedException {
    for (int i = 1; i <= n; i++) {
      zero.acquire();
      printNumber.accept(0);
      if ((i & 1) == 1)
        odd.release();
      else
        even.release();
    }
  }

  public void odd(IntConsumer printNumber) throws InterruptedException {
    for (int i = 1; i <= n; i += 2) {
      odd.acquire();
      printNumber.accept(i);
      zero.release();
    }
  }

  public void even(IntConsumer printNumber) throws InterruptedException {
    for (int i = 2; i <= n; i += 2) {
      even.acquire();
      printNumber.accept(i);
      zero.release();
    }
  }
}
