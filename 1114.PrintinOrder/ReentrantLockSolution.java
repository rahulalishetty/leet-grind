class ReentrantLockSolution {
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition firstDone = lock.newCondition();
  private final Condition secondDone = lock.newCondition();
  private int stage = 1;

  public void first(Runnable printFirst) {
    lock.lock();
    try {
      printFirst.run();
      stage = 2;
      firstDone.signalAll();
    } finally {
      lock.unlock();
    }
  }

  public void second(Runnable printSecond) throws InterruptedException {
    lock.lock();
    try {
      while (stage < 2)
        firstDone.await();
      printSecond.run();
      stage = 3;
      secondDone.signalAll();
    } finally {
      lock.unlock();
    }
  }

  public void third(Runnable printThird) throws InterruptedException {
    lock.lock();
    try {
      while (stage < 3)
        secondDone.await();
      printThird.run();
    } finally {
      lock.unlock();
    }
  }
}
