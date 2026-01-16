class BlockingQueues {
  private final int n;
  private final BlockingQueue<Integer> qZero = new ArrayBlockingQueue<>(1);
  private final BlockingQueue<Integer> qOdd = new ArrayBlockingQueue<>(1);
  private final BlockingQueue<Integer> qEven = new ArrayBlockingQueue<>(1);

  public BlockingQueues(int n) throws InterruptedException {
    this.n = n;
    qZero.put(1); // start token
  }

  public void zero(IntConsumer printNumber) throws InterruptedException {
    for (int i = 1; i <= n; i++) {
      qZero.take();
      printNumber.accept(0);
      if ((i & 1) == 1)
        qOdd.put(i);
      else
        qEven.put(i);
    }
  }

  public void odd(IntConsumer printNumber) throws InterruptedException {
    for (int i = 1; i <= n; i += 2) {
      int v = qOdd.take();
      printNumber.accept(v);
      qZero.put(1);
    }
  }

  public void even(IntConsumer printNumber) throws InterruptedException {
    for (int i = 2; i <= n; i += 2) {
      int v = qEven.take();
      printNumber.accept(v);
      qZero.put(1);
    }
  }
}
