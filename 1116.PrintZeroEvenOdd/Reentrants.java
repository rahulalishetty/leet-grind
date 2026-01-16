class Reentrants {
  private final int n;
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition cz = lock.newCondition();
  private final Condition co = lock.newCondition();
  private final Condition ce = lock.newCondition();

  private int cur = 1;
  private int state = 0; // 0=zero turn, 1=odd turn, 2=even turn

  public Reentrants(int n) {
    this.n = n;
  }

  public void zero(IntConsumer printNumber) throws InterruptedException {
    for (int i = 0; i < n; i++) {
      lock.lock();
      try {
        while (state != 0)
          cz.await();
        printNumber.accept(0);
        state = ((cur & 1) == 1) ? 1 : 2;
        if (state == 1)
          co.signal();
        else
          ce.signal();
      } finally {
        lock.unlock();
      }
    }
  }

  public void odd(IntConsumer printNumber) throws InterruptedException {
    while (true) {
      lock.lock();
      try {
        while (state != 1) {
          if (cur > n)
            return;
          co.await();
        }
        if (cur > n)
          return;
        printNumber.accept(cur++);
        state = 0;
        cz.signal();
      } finally {
        lock.unlock();
      }
    }
  }

  public void even(IntConsumer printNumber) throws InterruptedException {
    while (true) {
      lock.lock();
      try {
        while (state != 2) {
          if (cur > n)
            return;
          ce.await();
        }
        if (cur > n)
          return;
        printNumber.accept(cur++);
        state = 0;
        cz.signal();
      } finally {
        lock.unlock();
      }
    }
  }
}
