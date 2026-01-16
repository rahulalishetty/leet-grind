import java.util.concurrent.Semaphore;

class SemaphoreSolution {
  private final Semaphore canRunSecond = new Semaphore(0);
  private final Semaphore canRunThird = new Semaphore(0);

  public SemaphoreSolution() {
  }

  public void first(Runnable printFirst) {
    printFirst.run();
    canRunSecond.release();
  }

  public void second(Runnable printSecond) throws InterruptedException {
    canRunSecond.acquire();
    printSecond.run();
    canRunThird.release();
  }

  public void third(Runnable printThird) throws InterruptedException {
    canRunThird.acquire();
    printThird.run();
  }
}
