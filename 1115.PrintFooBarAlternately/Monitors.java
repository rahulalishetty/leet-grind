class Monitors {
  private int n;
  private boolean fooTurn = true;

  public Monitors(int n) {
    this.n = n;
  }

  public synchronized void foo(Runnable printFoo) throws InterruptedException {
    for (int i = 0; i < n; i++) {
      while (!fooTurn) {
        wait();
      }
      printFoo.run(); // prints "foo"
      fooTurn = false;
      notifyAll();
    }
  }

  public synchronized void bar(Runnable printBar) throws InterruptedException {
    for (int i = 0; i < n; i++) {
      while (fooTurn) {
        wait();
      }
      printBar.run(); // prints "bar"
      fooTurn = true;
      notifyAll();
    }
  }
}
