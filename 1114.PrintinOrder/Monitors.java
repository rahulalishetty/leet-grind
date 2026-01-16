class Monitors {
  private int stage = 1; // 1 => first, 2 => second, 3 => third

  public Monitors() {
  }

  public synchronized void first(Runnable printFirst) {
    printFirst.run();
    stage = 2;
    notifyAll();
  }

  public synchronized void second(Runnable printSecond) throws InterruptedException {
    while (stage < 2)
      wait();
    printSecond.run();
    stage = 3;
    notifyAll();
  }

  public synchronized void third(Runnable printThird) throws InterruptedException {
    while (stage < 3)
      wait();
    printThird.run();
  }
}
