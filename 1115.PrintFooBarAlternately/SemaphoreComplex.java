class SemaphoreComplex {
  private int n;
  private final Semaphore canRun = new Semaphore(1);
  private volatile int fooProgress = -1;
  private volatile int barProgress = 0;

  public SemaphoreComplex(int n) {
    this.n = n;
  }

  public synchronized void foo(Runnable printFoo) throws InterruptedException {

    for (int i = 0; i < n; i++) {

      // printFoo.run() outputs "foo". Do not change or remove this line.
      if (barProgress < i)
        wait();
      printFoo.run();
      fooProgress++;
      notifyAll();
    }
  }

  public synchronized void bar(Runnable printBar) throws InterruptedException {

    for (int i = 0; i < n; i++) {

      // printBar.run() outputs "bar". Do not change or remove this line.
      if (fooProgress < i)
        wait();
      printBar.run();
      barProgress++;
      notifyAll();
    }
  }
}
