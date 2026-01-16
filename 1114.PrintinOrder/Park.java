class Park {
  private final AtomicReference<Thread> t2 = new AtomicReference<>();
  private final AtomicReference<Thread> t3 = new AtomicReference<>();

  public void first(Runnable printFirst) {
    printFirst.run();
    Thread s = t2.get();
    if (s != null)
      LockSupport.unpark(s);
  }

  public void second(Runnable printSecond) {
    t2.set(Thread.currentThread());
    LockSupport.park(); // wait for first()
    printSecond.run();
    Thread s = t3.get();
    if (s != null)
      LockSupport.unpark(s);
  }

  public void third(Runnable printThird) {
    t3.set(Thread.currentThread());
    LockSupport.park(); // wait for second()
    printThird.run();
  }
}
