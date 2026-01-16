import java.util.concurrent.CountDownLatch;

class CountDownLatchSolution {
  private final CountDownLatch firstDone = new CountDownLatch(1);
  private final CountDownLatch secondDone = new CountDownLatch(1);

  public CountDownLatchSolution() {
  }

  public void first(Runnable printFirst) {
    printFirst.run();
    firstDone.countDown();
  }

  public void second(Runnable printSecond) throws InterruptedException {
    firstDone.await();
    printSecond.run();
    secondDone.countDown();
  }

  public void third(Runnable printThird) throws InterruptedException {
    secondDone.await();
    printThird.run();
  }
}
